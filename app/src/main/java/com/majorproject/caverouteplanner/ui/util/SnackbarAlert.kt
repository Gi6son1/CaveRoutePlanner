package com.majorproject.caverouteplanner.ui.util

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Function to display a snackbar with a message
 * @param scope The coroutine scope
 * @param snackbarHostState The snackbar host state
 * @param message The message to display
 */
fun displaySnackbarWithMessage(scope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String){
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}