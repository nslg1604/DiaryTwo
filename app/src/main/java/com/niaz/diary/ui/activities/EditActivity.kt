package com.niaz.diary.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.niaz.diary.mvi.edit.EditViewModel
import com.niaz.diary.mvi.list.ListViewModel
import com.niaz.diary.ui.edit.activities.EditScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditActivity : ComponentActivity() {
    val viewModel: EditViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditScreen(viewModel)
        }
    }
}
