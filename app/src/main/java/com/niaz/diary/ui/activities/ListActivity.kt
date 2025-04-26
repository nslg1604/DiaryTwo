package com.niaz.diary.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.niaz.diary.R
import com.niaz.diary.data.title.TitleEntity
import com.niaz.diary.mvi.list.ListIntent
import com.niaz.diary.mvi.list.ListViewModel
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListActivity : ComponentActivity() {
    private val viewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryScreen()
        }
    }

    @Composable
    fun DiaryScreen() {
        val context = LocalContext.current
        val state by viewModel.state.collectAsState()

        // Process intents when we first load
        LaunchedEffect(Unit) {
            viewModel.processIntent(ListIntent.LoadTitles)
        }

        // UI Components
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with title and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.my_title),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                MyMenu(viewModel)
            }

            // List of titles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.titles.forEachIndexed { index, titleEntity ->
                    ShowTitleRow(
                        iTitle = index,
                        titleEntity = titleEntity,
                        onTitleChange = { updatedEntity ->
                            viewModel.processIntent(ListIntent.UpdateTitle(updatedEntity))
                        },
                        onDelete = {
                            viewModel.processIntent(ListIntent.DeleteTitle(titleEntity))
                        }
                    )
                }
            }

            // Add button
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
                            Timber.d("+ click")
                            viewModel.processIntent(ListIntent.ShowAddTitleDialog)
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

            // Show add title dialog if needed
            if (state.showAddTitleDialog) {
                AddTitleDialog(
                    onDismiss = { viewModel.processIntent(ListIntent.HideAddTitleDialog) },
                    onConfirm = { title ->
                        viewModel.processIntent(ListIntent.HideAddTitleDialog)
                        val titleEntity = TitleEntity(title)
                        viewModel.processIntent(ListIntent.AddTitle(titleEntity))
                    }
                )
            }

            // Show message dialog if needed
            if (state.showMessageDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.processIntent(ListIntent.HideMessageDialog)
                    },
                    text = {
                        Text(
                            text = state.message ?: "",
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.processIntent(ListIntent.HideMessageDialog)
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                )
            }

            // Show About Dialog if needed
            if (state.showAboutDialog) {
                AboutDialog(onDismiss = { viewModel.processIntent(ListIntent.HideAboutDialog) })
            }
        }
    }

    @Composable
    fun ShowTitleRow(
        iTitle: Int,
        titleEntity: TitleEntity,
        onTitleChange: (TitleEntity) -> Unit,
        onDelete: () -> Unit
    ) {
        val context = LocalContext.current
        val state by viewModel.state.collectAsState()

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

            Text(
                text = titleEntity.title,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.processIntent(ListIntent.ShowTitleMenuDialog(titleEntity))
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    MyData.iTitle = iTitle
                    val intent = Intent(context, ShowActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_table_view_24),
                    contentDescription = "table icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Show title menu dialog if needed
        if (state.showTitleMenuDialog && state.selectedTitle == titleEntity) {
            ShowTitleMenuDialog(
                title = titleEntity,
                onDismissRequest = {
                    viewModel.processIntent(ListIntent.HideTitleMenuDialog)
                },
                onEditClick = {
                    viewModel.processIntent(ListIntent.ShowEditTitleDialog)
                },
                onDeleteClick = {
                    onDelete()
                    viewModel.processIntent(ListIntent.HideTitleMenuDialog)
                },
                onTitleChange = { newTitle ->
                    onTitleChange(titleEntity.copy(title = newTitle))
                }
            )
        }

        // Show edit title dialog if needed
        if (state.showEditTitleDialog && state.selectedTitle == titleEntity) {
            EditTitleDialog(
                oldTitle = titleEntity.title,
                onDismiss = {
                    viewModel.processIntent(ListIntent.HideEditTitleDialog)
                    viewModel.processIntent(ListIntent.HideTitleMenuDialog)
                },
                onConfirm = { newTitle ->
                    onTitleChange(titleEntity.copy(title = newTitle))
                    viewModel.processIntent(ListIntent.HideEditTitleDialog)
                    viewModel.processIntent(ListIntent.HideTitleMenuDialog)
                }
            )
        }
    }

    @Composable
    fun ShowTitleMenuDialog(
        title: TitleEntity,
        onDismissRequest: () -> Unit,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit,
        onTitleChange: (String) -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Выберите действие", fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    DialogButton(
                        icon = Icons.Filled.Edit,
                        text = "Переименовать",
                        onClick = onEditClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DialogButton(
                        icon = Icons.Filled.Delete,
                        text = "Удалить",
                        onClick = onDeleteClick
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Отмена", fontSize = 16.sp)
                }
            }
        )
    }

    @Composable
    fun MyMenu(viewModel: ListViewModel) {
        val context = LocalContext.current
        val state by viewModel.state.collectAsState()
        Timber.d("ListActivity - myMenu")

        val importLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.processIntent(ListIntent.ImportDatabase(uri, context))
                }
            }
        }

        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.processIntent(ListIntent.ExportDatabase(uri, context))
                }
            }
        }

        Box {
            IconButton(onClick = {
                viewModel.processIntent(ListIntent.ToggleMenu)
                Timber.d("ListActivity - myMenu - onClick show=" + state.showMenu)
            }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = state.showMenu,
                onDismissRequest = { viewModel.processIntent(ListIntent.HideMenu) },
                modifier = Modifier.wrapContentSize()
            ) {
                DropdownMenuItem(
                    onClick = {
                        viewModel.processIntent(ListIntent.HideMenu)
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream"
                            putExtra(Intent.EXTRA_TITLE, MyConst.DB_NAME)
                        }
                        exportLauncher.launch(intent)
                    },
                    text = {
                        Text(stringResource(R.string.export_db))
                    }
                )

                DropdownMenuItem(
                    onClick = {
                        viewModel.processIntent(ListIntent.HideMenu)
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                        }
                        importLauncher.launch(intent)
                    },
                    text = {
                        Text(stringResource(R.string.import_db))
                    }
                )

                DropdownMenuItem(
                    onClick = {
                        viewModel.processIntent(ListIntent.HideMenu)
                        viewModel.processIntent(ListIntent.ShowAboutDialog)
                    },
                    text = {
                        Text(stringResource(R.string.about))
                    }
                )
            }
        }
    }
}

@Composable
fun DialogButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = text,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun EditTitleDialog(
    oldTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(oldTitle)) }
    Timber.d("EditTitleDialog")

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
                    Timber.d("TitleDialog confirm: ${text.text}")
                    onConfirm(text.text)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(onClick = {
                Timber.d("TitleDialog dismissed")
                onDismiss()
            }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        "Unknown"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.about)) },
        text = {
            Column {
                Text(appName)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.version) + versionName)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun AddTitleDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
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