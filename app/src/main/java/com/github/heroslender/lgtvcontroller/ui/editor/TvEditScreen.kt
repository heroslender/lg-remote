package com.github.heroslender.lgtvcontroller.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.heroslender.lgtvcontroller.ControllerTopAppBar
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ControlPreview() {
    LGTVControllerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ControllerTopAppBar(
                    title = stringResource(string.edit_tv_title),
                    navigateUp = {},
                )
            }
        ) { innerPadding ->
            TvEditBody(
                uiState = TvUiState(
                    TvDetails("gads-sdgfds-g-fdsgfdgdf-sdfsdf", "Living Room TV"),
                    false
                ),
                onValueChange = {},
                onSave = {},
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding()
                    )
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvEditScreen(
    tvEditViewModel: TvEditViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ControllerTopAppBar(
                title = stringResource(string.edit_tv_title),
                navigateUp = navigateBack,
            )
        }
    ) { innerPadding ->
        TvEditBody(
            uiState = tvEditViewModel.tvUiState,
            onValueChange = tvEditViewModel::updateUiState,
            onSave = {
                scope.launch {
                    tvEditViewModel.save()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun TvEditBody(
    uiState: TvUiState,
    onValueChange: (TvDetails) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(15.dp, 10.dp)) {
        TvEditForm(
            tvDetails = uiState.tvDetails,
            onValueChange = onValueChange
        )

        Button(
            onClick = onSave,
            enabled = uiState.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(stringResource(string.save_action))
        }
    }
}

@Composable
fun TvEditForm(
    tvDetails: TvDetails,
    onValueChange: (TvDetails) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = tvDetails.tvId,
            onValueChange = {},
            label = { Text(stringResource(string.tv_id)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
        )

        OutlinedTextField(
            value = tvDetails.tvName,
            onValueChange = {},
            label = { Text(stringResource(string.tv_name)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
        )

        OutlinedTextField(
            value = tvDetails.tvDisplayName,
            onValueChange = { onValueChange(tvDetails.copy(tvDisplayName = it)) },
            label = { Text(stringResource(string.tv_display_name)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
        )
    }
}