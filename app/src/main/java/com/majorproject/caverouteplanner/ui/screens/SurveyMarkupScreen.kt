package com.majorproject.caverouteplanner.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.MarkupImageAndGraphOverlay
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.customcomposables.ActionCheckDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton
import com.majorproject.caverouteplanner.ui.components.markuplayouts.AltitudesLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.EntrancesAndJunctionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.PathConnectionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.WaterAndHardTraverseLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyMarkupScreen(
    returnToMenu: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext
    var markupStage by rememberSaveable { mutableIntStateOf(0) }
    val titleList = remember {
        listOf(
            "Entrances/Junctions",
            "Connect Paths",
            "Water/Hard Traverse",
            "Altitude"
        )
    }

    var openHomeButtonDialog by rememberSaveable {
        mutableStateOf(false)
    }

    BackGroundScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Stage ${markupStage + 1}: ${titleList[markupStage]}") },
            )
        }
    ) { innerPadding ->
        val surveyImage =
            BitmapFactory.decodeStream(context.assets.open("llygadlchwr.jpg")).asImageBitmap()
        var nodesList by rememberSaveable { mutableStateOf(mutableListOf<SurveyNode>()) }
        var pathsList by rememberSaveable { mutableStateOf(mutableListOf<SurveyPath>()) }

        MarkupImageAndGraphOverlay(
            surveyImage = surveyImage,
            nodes = nodesList,
            paths = pathsList,
            modifier = Modifier.padding(innerPadding)
        )

        ConstraintLayout(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            val (stageButtons, undoButton, homeButton, customLayout) = createRefs()

            CustomIconButton(
                onClick = { openHomeButtonDialog = true },
                modifier = Modifier.constrainAs(homeButton) {
                    top.linkTo(parent.top, margin = 20.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                },
                iconVector = Icons.Outlined.Home,
                contentDescription = "Home"
            )

            Row(modifier = Modifier.constrainAs(stageButtons) {
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 10.dp)
                end.linkTo(parent.end, 10.dp)
                width = Dimension.fillToConstraints
            }) {
                if (markupStage > 0) {
                    CustomTextButton(
                        onClick = {
                            markupStage--
                        },
                        text = titleList[markupStage - 1],
                        flipped = true,
                        modifier = Modifier.weight(1f),
                        iconVector = Icons.AutoMirrored.Outlined.ArrowBack
                    )
                }
                if (markupStage < titleList.size - 1) {
                    CustomTextButton(
                        onClick = {
                            markupStage++
                        },
                        text = titleList[markupStage + 1],
                        modifier = Modifier.weight(1f),
                        iconVector = Icons.AutoMirrored.Outlined.ArrowForward
                    )
                }
            }

            when (markupStage) {
                0 -> EntrancesAndJunctionsLayout(modifier = Modifier.constrainAs(customLayout) {
                    bottom.linkTo(stageButtons.top, margin = 20.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(homeButton.bottom, 20.dp)
                    height = Dimension.fillToConstraints
                }.fillMaxHeight().fillMaxWidth(0.5f)
                )

                1 -> PathConnectionsLayout (modifier = Modifier.constrainAs(customLayout) {
                    bottom.linkTo(stageButtons.top, margin = 20.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(homeButton.bottom, 20.dp)
                    height = Dimension.fillToConstraints
                }.fillMaxHeight().fillMaxWidth(0.5f)
                )
                2 -> WaterAndHardTraverseLayout (modifier = Modifier.constrainAs(customLayout) {
                    bottom.linkTo(stageButtons.top, margin = 20.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(homeButton.bottom, 20.dp)
                    height = Dimension.fillToConstraints
                }.fillMaxHeight().fillMaxWidth(0.5f)
                )
                3 -> AltitudesLayout (modifier = Modifier.constrainAs(customLayout) {
                    bottom.linkTo(stageButtons.top, margin = 20.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(homeButton.bottom, 20.dp)
                    height = Dimension.fillToConstraints
                }.fillMaxHeight().fillMaxWidth(0.5f)
                )
            }

            CustomTextButton(
                onClick = {},
                text = "Undo",
                modifier = Modifier.constrainAs(undoButton) {
                    bottom.linkTo(customLayout.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                },
                iconVector = Icons.AutoMirrored.Outlined.Undo
            )


        }

        ActionCheckDialog(
            dialogIsOpen = openHomeButtonDialog,
            dialogOpen = { openHomeButtonDialog = it },
            confirmAction = { returnToMenu() },
            message = "Are you sure you'd like to go back to the main menu? You will lose your current route setup if you do."
        )

    }
}
