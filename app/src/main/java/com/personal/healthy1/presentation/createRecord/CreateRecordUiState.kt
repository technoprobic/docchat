package com.personal.healthy1.presentation.createRecord

data class CreateRecordUiState(
    val sourceType: Int = 0,
    val pastedText: String = "",
    val filePath: String? = null,
    val extractedText: String? = null
) {
    val isSaveEnabled: Boolean
        get() = when (sourceType) {
            0 -> pastedText.isNotBlank()
            1 -> filePath != null
            2 -> extractedText?.isNotBlank() == true
            else -> false
        }
}
