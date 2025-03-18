package com.niaz.diary.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.niaz.diary.ui.theme.DiaryTheme


class MainActivity : ComponentActivity() {
    val MY_TAG = "myapp"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = 0
//        val counter = Counter()
        val counter = mutableStateOf(0)
        enableEdgeToEdge()
        setContent {
            DiaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.background(color = Color.LightGray),
                    ){
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        MyEdit(
                            counter = counter,
                            myClick = {
                                Log.d(MY_TAG, "Click count=" + count)
                                counter.value++
                                count++
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyEdit(
    counter: State<Int>,
    myClick: () -> Unit
) {
    Log.d("myapp", "HomeScreen - count=" + counter.value)
    Text(
        text = "Clicks: ${counter.value}",
        modifier = Modifier.clickable(onClick = myClick)
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiaryTheme {
        Greeting("Android")
    }
}

class Counter {
    var value = 0
}