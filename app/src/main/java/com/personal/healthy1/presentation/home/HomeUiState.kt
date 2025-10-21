package com.personal.healthy1.presentation.home

import com.personal.healthy1.data.local.UserRecord

data class HomeUiState(
    val records: List<UserRecord> = emptyList(),
    val selectedIds: List<String> = emptyList()
)
