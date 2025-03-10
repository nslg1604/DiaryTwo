package com.niaz.diary.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import com.niaz.diary.data.MyData
import com.niaz.diary.utils.MyLogger

class AddActivity : ComponentActivity() {
    val text = mutableStateOf("some text")
    val myDatas by mutableStateOf(mutableListOf<MyData>())

    //    var myDatas: MutableList<MyData> = ArrayList()
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyLogger.d("start")
        myDatas.add(MyData("aaaa"))
        myDatas.add(MyData("bbbb"))

        setContent {
            val notesList = remember {
                mutableStateListOf<String>()
            }
            notesList.add("aaa")
            notesList.add("bbb")


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.background(color = Color.White),
            ) { // column
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(color = Color.White)
                        .border(width = 2.dp, color = Color.DarkGray, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .background(Color.LightGray)
                        .padding(20.dp)

                ) {
                    Text(
                        text = "Home screen",
                        fontSize = 32.sp,
                        color = Color.Black
                    )
                }


                // Message
                val customModifier = Modifier
                    .border(width = 2.dp, color = Color.DarkGray, shape = CircleShape)
                    .clip(shape = CircleShape)
                    .background(Color.LightGray)
                    .padding(20.dp)
                Message("Add a message", customModifier,
                    myClick = {
                        MyLogger.d("AddActivity - click")
                    }
                )

                MyText("text")

                MyEdit(
                    text = text,
                    onValueChange = { newText ->
                        text.value = newText
                        myDatas.add(MyData(count++.toString()))
                        notesList.add(count++.toString())
                        MyLogger.d("MyEdit size=" + myDatas.size)
                    }
                )

                // RecycleView
//                LazyColumn(Modifier.fillMaxSize()) {
//                    itemsIndexed(notesList){index, item ->
//
//                    }
//                }

                LazyColumn(Modifier.fillMaxSize()) {
                    MyLogger.d("notes size=" + notesList.size)
                    itemsIndexed(notesList) { index, str ->
                        ListNote(str)
                        if (index != notesList.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
//                LazyColumn(Modifier.fillMaxSize()) {
//                    itemsIndexed(myDatas) { index, d ->
//                        ListItem(d)
//                        if (index != myDatas.size - 1) {
//                            HorizontalDivider()
//                        }
//                    }
//                }

                CreateList()

            }
        }
    }
}

fun showList() {
}

@Composable
fun CreateList() {
    val data = remember { mutableStateListOf<MyData>() }

    //myList.swapList(getDailyItemList()) // Returns a List<DailyItem> with latest values and uses mutable list internally

    // Function to refresh the list
    val onUpdateClick = {
        MyLogger.d("CreateList - click")
        // Do something that updates the list
        //...
        // Get the updated list to trigger a recompose
//        myList.swapList(getDailyItemList())
    }
    // Create the lazy column
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(data) { index, d ->
            ListItem(d)
            if (index != data.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MyRecycle(
    myData: State<Int>,
    myClick: () -> Unit
) {
//    Log.d("myapp", "HomeScreen - count=" + counter.value)
//    Text(
//        text = "Clicks: ${counter.value}",
//        modifier = Modifier.clickable(onClick = myClick)
//    )
}


@Composable
fun Message(
    text: String, modifier: Modifier = Modifier,
    myClick: () -> Unit
) {
    val defaultModifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clickable(onClick = myClick)
    Text(
        text, defaultModifier.then(modifier),
        fontSize = 28.sp, textAlign = TextAlign.Center
    )
}

@Composable
fun MyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text
    )
}

@Composable
fun MyEdit(
    text: State<String>,
    onValueChange: (String) -> Unit
) {
    val textValue = text.value
    OutlinedTextField(value = textValue, onValueChange = onValueChange)
}

@Composable
fun ListItem(data: MyData, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text(text = data.name)
        // … other composables required for displaying `data`
    }
}

@Composable
fun ListNote(data: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text(text = data)
        // … other composables required for displaying `data`
    }
}