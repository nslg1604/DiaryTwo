package com.niaz.diary.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.R
import com.niaz.diary.db.NoteEntity
import com.niaz.diary.utils.*
import com.niaz.diary.viewmodel.ShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowActivity : ComponentActivity() {
    private val viewModel: ShowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyLogger.d("ShowActivity - iTitle=${MyData.iTitle}/${MyData.titleEntities[MyData.iTitle].id}")
        setContent {
            ShowScreen(viewModel)
        }
    }

    @Composable
    fun ShowScreen(viewModel: ShowViewModel) {
        var offsetTitle by remember { mutableStateOf(MyData.iTitle) }
        var myNotes by remember { mutableStateOf<MutableList<NoteEntity>>(mutableListOf()) }
        var mode by remember { mutableStateOf(MyConst.MODE_TABLE) }

        val graphBitmap by viewModel.graphBitmap.collectAsState()

        LaunchedEffect(offsetTitle) {
            viewModel.readMyNotes(MyData.titleEntities[offsetTitle].id).collect { newMyNotes ->
                newMyNotes?.let {
                    myNotes = it
                    MyLogger.d("ShowActivity - Loaded notes: ${it.size}")
                }
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

            ShowInfo(MyConst.INFO_TITLES, viewModel, offsetTitle) { newOffset ->
                offsetTitle = newOffset
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { mode = MyConst.MODE_TABLE },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(
                                width = if (mode == MyConst.MODE_TABLE) 3.dp else 1.dp,
                                color = if (mode == MyConst.MODE_TABLE) Color.DarkGray else Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_table_view_24),
                            contentDescription = "table icon",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { mode = MyConst.MODE_GRAPH },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(
                                width = if (mode == MyConst.MODE_GRAPH) 3.dp else 1.dp,
                                color = if (mode == MyConst.MODE_GRAPH) Color.DarkGray else Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_graph_right),
                            contentDescription = "graph icon",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

////////////////////////
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp)
//                        .background(Color.White)
//                        .padding(10.dp),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    IconButton(
//                        onClick = { mode = MyConst.MODE_TABLE },
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))
//                            .background(Color.LightGray)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_table_view_24),
//                            contentDescription = "table icon",
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(10.dp))
//
//                    IconButton(
//                        onClick = { mode = MyConst.MODE_GRAPH },
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))
//                            .background(Color.LightGray)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_graph_right),
//                            contentDescription = "graph icon",
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }

                if (mode == MyConst.MODE_TABLE) {
                    myNotes.forEach { ShowMyNote(it) }
                } else {
                    graphBitmap?.let {
                        GraphImage(graphBitmap = it)
                    }
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
                text = myNote.date ?: "",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = myNote.note ?: "",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
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
                        if (infoType == MyConst.INFO_TITLES && offset > 0) {
                            onOffsetChange(offset - 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("<", fontSize = 26.sp, textAlign = TextAlign.Center)
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
                        if (infoType == MyConst.INFO_TITLES && offset < MyData.titleEntities.size - 1) {
                            onOffsetChange(offset + 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(">", fontSize = 26.sp, textAlign = TextAlign.Center)
            }
        }
    }

    @Composable
    fun GraphImage(graphBitmap: Bitmap) {
        Image(
            bitmap = graphBitmap.asImageBitmap(),
            contentDescription = "my graph",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = 0.dp,
                    max = (LocalContext.current.resources.displayMetrics.heightPixels / 2).dp
                ),
            contentScale = ContentScale.FillWidth
        )
    }
}
