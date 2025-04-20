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
import com.niaz.diary.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.platform.LocalContext

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.niaz.diary.utils.MyPermissions

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
        var addTitle by remember { mutableStateOf(false) }
        val message by viewModel.message.collectAsState()
        var exportImportDialog by remember { mutableStateOf(false) }
        val titleEntities by viewModel.titleEntities.collectAsState()

        LaunchedEffect(message) {
            if (!message.isNullOrEmpty()) {
                MyLogger.d("ListActivity - new message=" + message)
                exportImportDialog = true
            }
        }

        LaunchedEffect(Unit) {
            viewModel.readTitleEntitiesFromDatabaseAsync()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                titleEntities.forEachIndexed { index, titleEntity ->
                    MyLogger.d("ListActivity index=" + index + " id=" + titleEntity.id + " title=" + titleEntity.title)
                    ShowTitleRow(
                        iTitle = index,
                        titleEntity = titleEntity,
                        onTitleChange = { updatedEntity ->
                            MyLogger.d("ListActivity - before update id=" + titleEntity.id + " title=" + titleEntity.title)
                            viewModel.updateTitleInDatabaseAsync(updatedEntity)
                        },
                        onDelete = {
                            viewModel.deleteTitleInDatabaseAsync(titleEntity)
                        }
                    )
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
                AddTitleDialog(
                    onDismiss = { addTitle = false },
                    onConfirm = { title ->
                        val titleEntity = TitleEntity(title)
                        MyLogger.d("ListActivity - before add id=" + titleEntity.id + " title=" + titleEntity.title)
                        viewModel.addTitleToDatabaseAsync(titleEntity)
                        addTitle = false
                    }
                )
            }

            if (exportImportDialog) {
                AlertDialog(
                    onDismissRequest = {
                        exportImportDialog = false
                    },
                    text = {
                        Text(
                            text = message,
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                exportImportDialog = false
                                viewModel.resetMessage()
                            }
                        ) {
                            Text(stringResource(R.string.close))
                        }
                    }
                )
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
        var titleClicked by remember { mutableStateOf(false) }
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
                        titleClicked = true
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

        if (titleClicked) {
            ShowTitleMenuDialog(
                title = titleEntity,
                onDismissRequest = {
                    titleClicked = false
                },
                onEditClick = {
                    titleClicked = true
                },
                onDeleteClick = {
                    onDelete()
                    titleClicked = false
                },
                onTitleChange = { newTitle ->
                    onTitleChange(titleEntity.copy(title = newTitle))
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
        var showEditDialog by remember { mutableStateOf(false) }

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
                        onClick = {
                            onEditClick()
                            showEditDialog = true
                        }
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

        if (showEditDialog) {
            EditTitleDialog(
                oldTitle = title.title,
                onDismiss = {
                    showEditDialog = false
                    onDismissRequest()
                },
                onConfirm = { newTitle ->
                    onTitleChange(newTitle)
                    showEditDialog = false
                    onDismissRequest()
                }
            )
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
////////////////////////////////////////////
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

@Composable
fun MyMenu(viewModel: ListViewModel){
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as Activity
    MyLogger.d("ListActivity - myMenu")

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.importDatabaseFromUri(context, uri)
            }
        }
    }


    Box {
        IconButton(onClick = {
            showMenu = !showMenu
            MyLogger.d("ListActivity - myMenu - onClick show=" + showMenu)
        }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.wrapContentSize()
        ) {

            DropdownMenuItem(
                onClick = {
                    showMenu = false
                    val myPermissions = MyPermissions()
                    val granted = myPermissions
                        .checkAndRequestWritePermission(activity, 1)
                    if (granted) {
                        viewModel.onExportDatabase()
                    }
                },
                text = {
                    Text(stringResource(R.string.export_db))
                }
            )

            DropdownMenuItem(
                onClick = {
                    showMenu = false
//                    viewModel.onImportDatabase()
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
                    showMenu = false
                    showAboutDialog = true
                },
                text = {
                    Text(stringResource(R.string.about))
                }
            )
        }
    }
    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}


