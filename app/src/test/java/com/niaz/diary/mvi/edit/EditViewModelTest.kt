package com.niaz.diary.mvi.edit
import com.niaz.diary.data.DbTools
import com.niaz.diary.data.title.TitleEntity
import com.niaz.diary.utils.MyCalendar
import com.niaz.diary.utils.MyData
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: EditViewModel

    private val myCalendar: MyCalendar = mockk(relaxed = true)
    private val dbTools: DbTools = mockk(relaxed = true)

    @Before
    fun setUp() {
        MyData.titleEntities = listOf(
            TitleEntity(id = 1, title = "Title1"),
            TitleEntity(id = 2, title = "Title2")
        )

        viewModel = EditViewModel().apply {
            this.myCalendar = this@EditViewModelTest.myCalendar
            this.dbTools = this@EditViewModelTest.dbTools
        }
    }

    @Test
    fun `when TitleOffsetChanged then offsetTitle should update`() = runTest {
        // given
        val offset = 1

        // when
        viewModel.onEvent(EditEvent.TitleOffsetChanged(offset))

        // then
        assertEquals(offset, viewModel.state.value.offsetTitle)
    }
}
