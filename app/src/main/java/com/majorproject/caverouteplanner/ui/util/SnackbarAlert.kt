package com.majorproject.caverouteplanner.ui.util

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun displaySnackbarWithMessage(scope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String){
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}