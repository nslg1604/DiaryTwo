package com.niaz.diary.ui.edit.activities

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.R
import com.niaz.diary.mvi.edit.EditEvent
import com.niaz.diary.mvi.edit.EditViewModel
import com.niaz.diary.utils.MyConst
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun EditScreen(viewModel: EditViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current as Activity

    LaunchedEffect(state.offsetTitle, state.offsetCalendar) {
        viewModel.readNote().collectLatest { note ->
            viewModel.onEvent(EditEvent.NoteChanged(note))
        }
    }

    BackHandler {
        viewModel.onEvent(EditEvent.BackClicked)
        context.finish()
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

        ShowInfo(
            infoType = MyConst.INFO_TITLES,
            offset = state.offsetTitle,
            onMoveLeft = { viewModel.onEvent(EditEvent.TitleOffsetChanged(state.offsetTitle - 1)) },
            onMoveRight = { viewModel.onEvent(EditEvent.TitleOffsetChanged(state.offsetTitle + 1)) },
            getInfo = viewModel::getInfo
        )

        ShowInfo(
            infoType = MyConst.INFO_CALENDAR,
            offset = state.offsetCalendar,
            onMoveLeft = { viewModel.onEvent(EditEvent.CalendarOffsetChanged(state.offsetCalendar - 1)) },
            onMoveRight = { viewModel.onEvent(EditEvent.CalendarOffsetChanged(state.offsetCalendar + 1)) },
            getInfo = viewModel::getInfo
        )

        ShowNote(
            note = state.note,
            onNoteChange = { viewModel.onEvent(EditEvent.NoteChanged(it)) },
            onDone = { viewModel.onEvent(EditEvent.BackClicked); context.finish() }
        )
    }

}

@Composable
fun ShowNote(
    note: String,
    onNoteChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
//    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )


//        OutlinedTextField(
//            value = note,
//            onValueChange = onNoteChange,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number,
//                imeAction = ImeAction.Done,
//                autoCorrect = false
//            ),
//            keyboardActions = KeyboardActions(
//                onDone = { onDone() }
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .focusRequester(focusRequester)
//        )
    }

    LaunchedEffect(focusRequester) {
        delay(100)
        try {
            focusRequester.requestFocus()
  //          keyboardController?.show()
        } catch (e: IllegalStateException) {
            Timber.e(e, "FocusRequester error")
        }
    }
}

@Composable
fun ShowInfo(
    infoType: String,
    offset: Int,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    getInfo: (String, Int) -> String
) {
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
                .clickable { onMoveLeft() },
            contentAlignment = Alignment.Center
        ) { Text(text = "<", fontSize = 26.sp, textAlign = TextAlign.Center) }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = getInfo(infoType, offset),
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
                .clickable { onMoveRight() },
            contentAlignment = Alignment.Center
        ) { Text(text = ">", fontSize = 26.sp, textAlign = TextAlign.Center) }
    }
}
