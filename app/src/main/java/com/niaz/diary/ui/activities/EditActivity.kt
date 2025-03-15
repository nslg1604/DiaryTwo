package com.niaz.diary.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import com.niaz.diary.viewmodel.EditViewModel
import com.niaz.diary.R


class EditActivity : ComponentActivity() {
    private val viewModel: EditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditScreen(
                viewModel
            )
        }
    }


    @Composable
    fun EditScreen(viewModel: EditViewModel) {
        var offsetTitle by remember { mutableStateOf(MyData.iTitle) }
        var offsetCalendar by remember { mutableStateOf(0) }
        var note by remember { mutableStateOf("") }

        LaunchedEffect(offsetTitle, offsetCalendar) {
            viewModel.readNote().collect { newNote ->
                note = newNote
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.enter_data),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            ShowInfo(MyConst.INFO_TITLES,
                viewModel,
                offsetTitle,
                note,
                onOffsetChange = { newOffset ->
                viewModel.updateNoteAsync(note) // save current value
                offsetTitle = newOffset
            })

            ShowInfo(MyConst.INFO_CALENDAR, viewModel, offsetCalendar, note, onOffsetChange = { newOffset ->
                viewModel.updateNoteAsync(note)
                offsetCalendar = newOffset
            })

            ShowNote(note, onNoteChange = { newNote ->
                note = newNote // allow editing
            })
        }
    }

    @Composable
    fun ShowNote(note: String, onNoteChange: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = { newValue ->
                    onNoteChange(newValue)
                },
                label = {
                    Text(
                        text = stringResource(R.string.enter_value)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.updateNoteAsync(note)
                    }
                ),
            )
        }
    }


    @Composable
    fun ShowInfo(
        infoType: String,
        viewModel: EditViewModel,
        offset: Int,
        note: String,
        onOffsetChange: (Int) -> Unit
    ) {
//        MyLogger.d("-------EditActivity - ShowInfo " + infoType)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .height(70.dp)
                .padding(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable {
                        var newOffset = offset
                        if (infoType == MyConst.INFO_CALENDAR) {
                            newOffset -= 1
                        }
                        if (infoType == MyConst.INFO_TITLES && offset > 0) {
                            newOffset -= 1
                            MyData.iTitle = offset
                        }
                        onOffsetChange(newOffset)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "<",
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = viewModel.getInfo(infoType, offset),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(3f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable {
                        var newOffset = offset
                        if (infoType == MyConst.INFO_CALENDAR) {
                            newOffset += 1
                        }
                        if (infoType == MyConst.INFO_TITLES && offset < MyData.titleEntities.size - 1) {
                            newOffset += 1
                            MyData.iTitle = offset
                        }
                        onOffsetChange(newOffset)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ">",
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }


}
