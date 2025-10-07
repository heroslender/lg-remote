package com.github.heroslender.lgtvcontroller.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.heroslender.lgtvcontroller.ControllerTopAppBar
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.ui.ConnectedDeviceScaffold
import com.github.heroslender.lgtvcontroller.ui.preferences.SwitchPrefence
import com.github.heroslender.lgtvcontroller.ui.preferences.TextPrefence
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme

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
                    TvDetails(
                        "gads-sdgfds-g-fdsgfdgdf-sdfsdf",
                        "LG WebOs Someting",
                        "Living Room TV",
                        true
                    ),
                    { null },
                    false
                ),
                onValueChange = {},
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
    val textInputState by tvEditViewModel.tvTextInputState.collectAsState()

    ConnectedDeviceScaffold(
        errorFlow = tvEditViewModel.errors,
        textInputState = textInputState,
        topBar = {
            ControllerTopAppBar(
                title = stringResource(string.edit_tv_title),
                navigateUp = navigateBack,
            )
        },
    ) {
        TvEditBody(
            uiState = tvEditViewModel.tvUiState,
            onValueChange = tvEditViewModel::updateUiState,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun TvEditBody(
    uiState: TvUiState,
    onValueChange: (TvDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tvDetails = uiState.tvDetails
    Column(modifier = modifier.padding(15.dp, 10.dp)) {
        TextPrefence(
            title = "ID",
            value = tvDetails.tvId,
        )

        TextPrefence(
            title = "Name",
            value = tvDetails.tvName,
        )

        TextPrefence(
            title = "Display name",
            value = tvDetails.tvDisplayName,
            inputValidation = uiState.displayNameValidator,
            onValueChange = { newValue ->
                onValueChange(tvDetails.copy(tvDisplayName = newValue.trim()))
            }
        )

        SwitchPrefence(
            title = "Auto Connect",
            subtitle = "Automatically connect to this device when the app starts",
            value = tvDetails.autoConnect,
            onValueChange = { newValue ->
                println("Switch changed to $newValue from ${tvDetails.autoConnect}")
                onValueChange(tvDetails.copy(autoConnect = newValue))
            }
        )
    }
}
