package com.personal.healthy1.services.llm

import android.util.Log
import com.personal.healthy1.remote.OkHttpHttpClientBuilderFactory
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.IngestionResult
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.services.config.OpenAiConfig
import org.example.project.services.config.OpenAiCompatChatModel
import java.io.File


object LlmUtils {

    private val TAG = this.javaClass.simpleName

    private val chatModel = OpenAiCompatChatModel.chatModel()
    private val embeddingStore  = InMemoryEmbeddingStore<TextSegment>()
    private val embeddingModel: EmbeddingModel = OpenAiEmbeddingModel.builder()
        .apiKey(OpenAiConfig.API_KEY) // todo
        .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
        .httpClientBuilder(OkHttpHttpClientBuilderFactory().create())
        .build()
    private var ingestionResult: IngestionResult? = null

    private var assistant: Assistant? = null
    private var chatMemory: MessageWindowChatMemory? = null


    init {
        CoroutineScope(Dispatchers.IO).launch {
            //testCalls()
        }
    }

    private suspend fun testCalls() {

        val aiMessage = chatModel.chat("Hello Mr. AI how are you doing!")
        Log.d(TAG, "aiMessage: $aiMessage")

        val sampleText = """
Charles James Kirk (October 14, 1993 – September 10, 2025) was an American right-wing political activist, entrepreneur, and media personality. He co-founded the conservative organization Turning Point USA (TPUSA) in 2012 and was its executive director. He published a range of books and hosted The Charlie Kirk Show, a talk radio program. Kirk was one of the most prominent voices of the MAGA movement within the Republican Party, and he is considered to be an icon and martyr of contemporary conservatism.
Kirk was born and raised in the Chicago suburbs of Arlington Heights and Prospect Heights, briefly attending Harper College before dropping out after one semester to pursue political activism full-time. He worked with various donors to fund TPUSA, rising to prominence via informal college campus debates held at his signature "Prove Me Wrong" table. He extended TPUSA's influence through initiatives such as the Professor Watchlist and mass rallies aimed at young voters, and has since been credited with generating interest in political conservatism among American youth. Under Kirk's leadership, TPUSA developed several affiliate groups, including Turning Point Action and Turning Point Faith, with the latter aimed at mobilizing religious communities around conservative issues. Partnering with Pentecostal pastor Rob McCoy in creating Turning Point Faith, Kirk became aligned with the Christian right and began advocating for Christian nationalism.
A key ally of President Donald Trump, Kirk espoused a variety of conservative and Trumpist stances, including opposition to abortion, gun control, DEI programs, and LGBT rights. His more controversial views included his criticism of the Civil Rights Act of 1964 and Martin Luther King Jr., as well as his promotion of COVID-19 misinformation, false claims of electoral fraud in 2020, and the Great Replacement conspiracy theory.
On September 10, 2025, Kirk was assassinated while speaking at a TPUSA public debate event on the Utah Valley University campus. His death sparked international attention and the subsequent condemnation of political violence by prominent domestic and international figures. Trump announced that Kirk would posthumously receive the Presidential Medal of Freedom. 
""".trimIndent()

        val kirkDocument = Document.from(sampleText)

        val embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()
        val ingestionResult = embeddingStoreIngestor.ingest(kirkDocument)
        Log.d(TAG, "ingestionresult total tokens: ${ingestionResult.tokenUsage().totalTokenCount()}")

        val embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()

        val assistant = AiServices.builder(Assistant::class.java)
            .chatModel(chatModel)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .contentRetriever(embeddingStoreContentRetriever)
            .build()
        val answer = assistant.chat("Was Chsrlie Kirk assassinated in 2025?")
        Log.d(TAG, "aiMessage: $answer")
    }

    fun initializeChat() {
        Log.d(TAG, "initChat: clearing history")
        ingestionResult = null
        assistant = null
        chatMemory = null
        embeddingStore.removeAll()
    }

    suspend fun ingestDocumentText(userText: String): IngestionResult {
        val currDocument = Document.from(userText)

        val embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()
        ingestionResult = embeddingStoreIngestor.ingest(currDocument)
        Log.d(TAG, "ingestionresult total tokens: ${ingestionResult?.tokenUsage()?.totalTokenCount()}")
        return ingestionResult!!
    }

    suspend fun ingestDocumentPdf(userFile: File): IngestionResult? {
        Log.d(TAG, "ingestDocumentPdf: ${userFile.path}")
        val currDocument = FileSystemDocumentLoader.loadDocument(
            userFile.path
        )

        Log.d(TAG, "ingestDocumentPdf2")
        val textSegments = splitIntoChunks(listOf(currDocument),
            1000,
            200)
        val numTextSegments = textSegments.size
        Log.d(TAG, "ingestDocumentPdf3 textSegments size : $numTextSegments")

        val embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()

        textSegments.take(10).forEachIndexed { index, it ->
            val embedding = embeddingModel.embed(it.text()).content()
            embeddingStore.add(embedding)
            val doc1 = Document.from(it.text())
            Log.d(TAG, "ingestDocumentPdf4-1 doc text: $it.text()")
            ingestionResult = embeddingStoreIngestor.ingest(doc1)
            Log.d(TAG, "ingestionresult pdf4-1 total tokens: ${ingestionResult?.tokenUsage()?.totalTokenCount()}")

            Log.d(TAG, "ingestDocumentPdf4: $index of $numTextSegments")
        }

        Log.d(TAG, "ingestDocumentPdf5")
        return null


        /*val embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            //.documentSplitter(DocumentSplitters.recursive(1000, 200, OpenAiTokenCountEstimator(OpenAiConfig.MODEL_NAME))) // todo
            .build()
        ingestionResult = embeddingStoreIngestor.ingest(currDocument)
        Log.d(TAG, "ingestionresult total tokens: ${ingestionResult?.tokenUsage()?.totalTokenCount()}")
        return ingestionResult!!*/
    }

    private fun splitIntoChunks(
        documents: List<Document>,
        maxTokensPerChunk: Int,
        overlapTokens: Int
    ): List<TextSegment> {
        val tokenizer = OpenAiTokenCountEstimator(OpenAiConfig.MODEL_NAME)

        val splitter = DocumentSplitters.recursive(
            maxTokensPerChunk,
            overlapTokens,
            tokenizer
        )

        val allSegments: MutableList<TextSegment> = ArrayList()
        for (document in documents) {
            val segments = splitter.split(document)
            allSegments.addAll(segments)
        }

        return allSegments
    }

    //suspend fun chatWithIngestedDocument(ingestionResult: IngestionResult, userText: String): String? {
    suspend fun chatWithIngestedDocument(userText: String): String? {
        val embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()

        chatMemory = MessageWindowChatMemory.withMaxMessages(25)
        assistant = AiServices.builder(Assistant::class.java)
            .chatModel(chatModel)
            .chatMemory(chatMemory)
            .contentRetriever(embeddingStoreContentRetriever)
            .build()
        val answer = assistant?.chat(userText)
        Log.d(TAG, "aiMessage: $answer")
        return answer
    }

}

interface Assistant  {
    fun chat(userMessage: String?): String?
}