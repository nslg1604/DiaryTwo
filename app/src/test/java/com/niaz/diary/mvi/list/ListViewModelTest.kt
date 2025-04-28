package com.niaz.diary.mvi.list
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.niaz.diary.MyApp
import com.niaz.diary.data.title.TitleEntity
import com.niaz.diary.mvi.edit.MainCoroutineRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class ListViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = MainCoroutineRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @MockK
    private lateinit var titleRepo: TitleRepo

    @RelaxedMockK
    private lateinit var context: Context

    @RelaxedMockK
    private lateinit var mockUri: Uri

    @RelaxedMockK
    private lateinit var mockCursor: Cursor

    private lateinit var viewModel: ListViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock the repository behavior
        coEvery { titleRepo.getTitles() } returns emptyList()

        // Create the view model with the mocked repository
        viewModel = ListViewModel(titleRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `when LoadTitles intent is processed, titles are loaded from repository`() = testScope.runTest {
        // Given
        val titles = listOf(
            TitleEntity("Title 1"),
            TitleEntity("Title 2")
        )
        coEvery { titleRepo.getTitles() } returns titles

        // When
        viewModel.processIntent(ListIntent.LoadTitles)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.state.first()
        assertEquals(titles, currentState.titles)
        coVerify { titleRepo.getTitles() }
    }

    @Test
    fun `when AddTitle intent is processed, title is added to repository`() = testScope.runTest {
        // Given
        val newTitle = TitleEntity("New Title")
        val updatedTitles = listOf(newTitle)
        coEvery { titleRepo.insertTitle(newTitle) } just Runs
        coEvery { titleRepo.getTitles() } returns updatedTitles

        // When
        viewModel.processIntent(ListIntent.AddTitle(newTitle))
        advanceUntilIdle()

        // Then
        val currentState = viewModel.state.first()
        assertEquals(updatedTitles, currentState.titles)
        coVerify { titleRepo.insertTitle(newTitle) }
        coVerify { titleRepo.getTitles() }
    }

    @Test
    fun `when UpdateTitle intent is processed, title is updated in repository`() = testScope.runTest {
        // Given
        val updatedTitle = TitleEntity("Updated Title").apply { id = 1 }
        val updatedTitles = listOf(updatedTitle)
        coEvery { titleRepo.updateTitle(updatedTitle) } just Runs
        coEvery { titleRepo.getTitles() } returns updatedTitles

        // When
        viewModel.processIntent(ListIntent.UpdateTitle(updatedTitle))
        advanceUntilIdle()

        // Then
        val currentState = viewModel.state.first()
        assertEquals(updatedTitles, currentState.titles)
        coVerify { titleRepo.updateTitle(updatedTitle) }
        coVerify { titleRepo.getTitles() }
    }

    @Test
    fun `getFileNameFromUri returns correct filename`() {
        // Given
        every { context.contentResolver.query(mockUri, null, null, null, null) } returns mockCursor
        every { mockCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) } returns 0
        every { mockCursor.moveToFirst() } returns true
        every { mockCursor.getString(0) } returns "test.db"

        // When
        val result = viewModel.getFileNameFromUri(context, mockUri)

        // Then
        assertEquals("test.db", result)
        verify { mockCursor.close() }
    }

    @Test
    fun `ShowAddTitleDialog intent updates state correctly`() = testScope.runTest {
        // When
        viewModel.processIntent(ListIntent.ShowAddTitleDialog)

        // Then
        val currentState = viewModel.state.first()
        assertTrue(currentState.showAddTitleDialog)
    }

    @Test
    fun `HideAddTitleDialog intent updates state correctly`() = testScope.runTest {
        // Given
        viewModel.processIntent(ListIntent.ShowAddTitleDialog)
        assertTrue(viewModel.state.first().showAddTitleDialog)

        // When
        viewModel.processIntent(ListIntent.HideAddTitleDialog)

        // Then
        val currentState = viewModel.state.first()
        assertFalse(currentState.showAddTitleDialog)
    }

    @Test
    fun `ShowTitleMenuDialog intent updates state correctly`() = testScope.runTest {
        // Given
        val title = TitleEntity("Test Title")

        // When
        viewModel.processIntent(ListIntent.ShowTitleMenuDialog(title))

        // Then
        val currentState = viewModel.state.first()
        assertTrue(currentState.showTitleMenuDialog)
        assertEquals(title, currentState.selectedTitle)
    }

    @Test
    fun `ToggleMenu intent toggles menu visibility`() = testScope.runTest {
        // Given - Initially the menu is hidden
        assertFalse(viewModel.state.first().showMenu)

        // When - Toggle it on
        viewModel.processIntent(ListIntent.ToggleMenu)

        // Then
        assertTrue(viewModel.state.first().showMenu)

        // When - Toggle it off again
        viewModel.processIntent(ListIntent.ToggleMenu)

        // Then
        assertFalse(viewModel.state.first().showMenu)
    }

    @Test
    fun `initTitleEntities inserts default titles when database is empty`() = testScope.runTest {
        // Given
        val app = mockk<MyApp>()
        mockkObject(MyApp.Companion)
        every { MyApp.getInstance() } returns app
        every { app.getString(any()) } returns "Default Title"
        coEvery { titleRepo.insertTitle(any()) } just Runs

        // When
        viewModel.initTitleEntities()

        // Then
        coVerify(exactly = 4) { titleRepo.insertTitle(any()) }
    }
}


