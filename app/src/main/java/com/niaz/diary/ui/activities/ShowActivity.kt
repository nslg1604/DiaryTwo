package com.niaz.diary.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.db.NoteEntity
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import com.niaz.diary.viewmodel.ShowViewModel
import com.niaz.diary.R

class ShowActivity : ComponentActivity() {
    private val viewModel: ShowViewModel by viewModels()
//    private var mode:String = MyConst.MODE_TABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyLogger.d("ShowActivity - iTitle=" + MyData.iTitle + "/" + MyData.titleEntities.get(MyData.iTitle).id)
        setContent {
            ShowScreen(
                viewModel
            )
        }
    }

    @Composable
    fun ShowScreen(viewModel: ShowViewModel) {
        var offsetTitle by remember { mutableStateOf(MyData.iTitle) }
        var myNotes: MutableList<NoteEntity> by remember { mutableStateOf(ArrayList()) }
        var mode by remember { mutableStateOf(MyConst.MODE_TABLE) }

        LaunchedEffect(offsetTitle) {
            viewModel.readMyNotes(MyData.titleEntities[offsetTitle].id).collect { newMyNotes ->
                myNotes = newMyNotes!!
                MyLogger.d("ShowActivity - LaunchedEffect offsetTitle=" + offsetTitle + " size=" + myNotes.size)
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
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(Color.White)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    IconButton(
                        onClick = {
                            mode = MyConst.MODE_TABLE
                            MyLogger.d("ShowActivity mode=$mode")
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_table_view_24),
                            contentDescription = "table icon",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            mode = MyConst.MODE_GRAPH
                            MyLogger.d("ShowActivity mode=$mode")
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color.White)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_graph_right),
                            contentDescription = "graph emulation",
                            modifier = Modifier
//                                .size(48.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                if (mode.equals(MyConst.MODE_TABLE)) {

                    //Notes
                    for (i in 0 until myNotes.size) {
                        var myNote = myNotes.get(i)
                        ShowMyNote(myNote)
                    }
                } else {
                    // todo graph calculation needed
                    Image(
                        painter = painterResource(id = R.drawable.ic_graph_emulation),
                        contentDescription = "my graph",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

        }
    }

    @Composable
    fun ShowMyNote(myNote: NoteEntity) {
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
                text = myNote.date!!,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = myNote.note!!,
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
                .height(80.dp)
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