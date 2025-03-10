package com.niaz.diary.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.data.MyNote
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import com.niaz.diary.viewmodel.ShowViewModel
import com.niaz.test2.R

class ShowActivity : ComponentActivity() {
    private val viewModel: ShowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initMyNotes()
        setContent {
            ShowScreen(
                viewModel
            )
        }
    }

    @Composable
    fun ShowScreen(viewModel: ShowViewModel) {
        var offsetTitle by remember { mutableStateOf(0) }
        var myNotes:List<MyNote> by remember { mutableStateOf(ArrayList()) }

        LaunchedEffect(offsetTitle) {
            viewModel.readMyNotes(offsetTitle).collect { newMyNotes ->
                myNotes = newMyNotes
                MyLogger.d("ShowActivity - LaunchedEffect offsetTitle=" + offsetTitle + "size=" + myNotes.size)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.history),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            ShowInfo(
                MyConst.INFO_TITLES,
                viewModel,
                offsetTitle,
                onOffsetChange = { newOffset ->
                    offsetTitle = newOffset
                })

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until myNotes.size) {
                    var myNote = myNotes.get(i)
                    ShowMyNote(myNote)
                }
            }

        }
    }

    @Composable
    fun ShowMyNote(myNote: MyNote) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = myNote.date,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = myNote.note,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )


        }
    }


    @Composable
    fun ShowInfo(
        infoType: String,
        viewModel: ShowViewModel,
        offset: Int,
        onOffsetChange: (Int) -> Unit
    ) {
        MyLogger.d(".....ShowData " + infoType)
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
                        if (infoType == MyConst.INFO_TITLES && offset > 0) {
                            newOffset -= 1
                            onOffsetChange(newOffset)
                        }
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
                        if (infoType == MyConst.INFO_TITLES && offset < MyData.titleEntities.size - 1) {
                            MyLogger.d("ShowActivity - right")
                            newOffset += 1
                            onOffsetChange(newOffset)
                        }
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
        MyLogger.d("ShowActivity - ShowData infoType=$infoType offset=$offset")
    }

}