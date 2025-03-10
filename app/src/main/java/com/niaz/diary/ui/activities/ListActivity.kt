package com.niaz.diary.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niaz.diary.db.TitleEntity
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import com.niaz.diary.viewmodel.ListViewModel
import com.niaz.test2.R

class ListActivity : ComponentActivity() {
    private val viewModel: ListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiaryScreen(
                onTitleAdded = { newTitle ->
                    MyLogger.d("Title saved: $newTitle")
                }
            )
        }
    }


    @Composable
    fun DiaryScreen(onTitleAdded: (String) -> Unit) {
        var addTitle by remember { mutableStateOf(false) }
        var titles by remember { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(Unit) {
            viewModel.readTitleEntitiesFromDatabaseAsync()
        }

        val titleEntities by viewModel.titleEntities.collectAsState()

        LaunchedEffect(titleEntities) {
            if (titleEntities != null) {
                titles = titleEntities!!.map { it.title ?: "" }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.my_title),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until titles.size){
                    val title = titles.get(i)
                    EditTitle(i, title) { oldTitle, newTitle ->
                        titles =
                            titles.map {
                                if (it == oldTitle) newTitle else it }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable(onClick = {
                            MyLogger.d("+ click")
                            addTitle = true
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        fontSize = 30.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }


            if (addTitle) {
                MyLogger.d("if showDialog")
                TitleDialog(
                    onDismiss = { addTitle = false },
                    onConfirm = { title ->
                        titles = (titles + title).toMutableList()
                        onTitleAdded(title)
                        addTitle = false
                        viewModel.addTitleToDatabaseAsync(TitleEntity(title))
                    }
                )
            }
        }
    }

    @Composable
    fun TitleDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
        var text by remember { mutableStateOf(TextFieldValue("")) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.new_title)) },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.name)) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(text.text)
                    }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismiss()
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }


    @Composable
    fun EditTitle(iTitle:Int, title: String,
                  onTitleChange: (String, String) -> Unit) {
        var textClicked by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    MyData.iTitle = iTitle
                    MyLogger.d("ListActivity edit iTitle=${MyData.iTitle} title= $title")
                    val intent = Intent(context, EditActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Title")
            }
            Spacer(modifier = Modifier.width(10.dp))

            Text(text = title,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                MyLogger.d("text - long click")
                                textClicked = true
                            }
                        )
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    MyLogger.d("Edit clicked for: $title")
                    MyData.iTitle = iTitle
                    val intent = Intent(context, ShowActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_table_rows_24),
                    contentDescription = "table icon",
                    modifier = Modifier.size(24.dp)
                )
            }

        }

        if (textClicked) {
            EditTitleDialog(
                oldTitle = title,
                onDismiss = { textClicked = false },
                onConfirm = { newTitle ->
                    onTitleChange(title, newTitle)
                    textClicked = false
                }
            )
        }
    }

    @Composable
    fun EditTitleDialog(
        oldTitle: String,
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var text by remember { mutableStateOf(TextFieldValue(oldTitle)) }
        MyLogger.d("EditTitleDialog")

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.change_title)) },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.name)) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        MyLogger.d("TitleDialog confirm: ${text.text}")
                        onConfirm(text.text)
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                Button(onClick = {
                    MyLogger.d("TitleDialog dismissed")
                    onDismiss()
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}