package com.niaz.diary.viewmodel

//@HiltViewModel
class ListViewModelOld(){}
//class ListViewModelOld @Inject constructor(
//    @ApplicationContext private val context: Context
//) : ViewModel() {
//
//    private val _titleEntities = MutableStateFlow<List<TitleEntity>>(emptyList())
//    val titleEntities: StateFlow<List<TitleEntity>> = _titleEntities
//
//    private var _message = MutableStateFlow("")
//    val message: StateFlow<String> = _message.asStateFlow()
//
//    fun readTitleEntitiesFromDatabaseAsync() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val entities = readTitleEntitiesFromDatabase()
//                if (entities.isNullOrEmpty()){
//                    initTitleEntities()
//                }
//                else {
//                    _titleEntities.value = entities!!.toList()
//                }
//            }
//        }
//    }
//
//    suspend fun readTitleEntitiesFromDatabase():List<TitleEntity>? {
//        Timber.d("ListViewModel - readTitlesFromDatabase")
//        val db = MyApp.getInstance().getDatabase()
//        if (db == null) {
//            Timber.e("ListViewModel - readTitlesFromDatabase db=null")
//            return null
//        }
//        val titleDao = db.titleDao()
//        val titlesRepo = TitlesRepo(titleDao = titleDao)
//        val titleEntities = titlesRepo.getTitles()
//        Timber.d("ListViewModel - readTitlesFromDatabase title.size=" + titleEntities?.size)
//
//        titleEntities.forEach{ titleEntity ->
//            Timber.d("ListViewModel - titleEntity id=${titleEntity.id} title=${titleEntity.title} ")
//        }
//        MyData.titleEntities = titleEntities
//        return titleEntities
//    }
//
//    suspend fun initTitleEntities(){
//        addTitleToDatabase(TitleEntity(context.getString(R.string.title_1)))
//        addTitleToDatabase(TitleEntity(context.getString(R.string.title_2)))
//        addTitleToDatabase(TitleEntity(context.getString(R.string.title_3)))
//        addTitleToDatabase(TitleEntity(context.getString(R.string.title_4)))
//    }
//
//    fun addTitleToDatabaseAsync(titleEntity:TitleEntity) {
//        Timber.d("ListViewModel - addTitleToDatabaseAsync")
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    addTitleToDatabase(titleEntity)
//                } catch (e: Exception) {
//                    Timber.e("ListViewModel - addTitleToDatabaseAsync error=" + e)
//                }
//            }
//        }
//    }
//
//    suspend fun addTitleToDatabase(titleEntity: TitleEntity) {
//        Timber.d("ListViewModel - addTitleToDatabase")
//        val db = MyApp.getInstance().getDatabase()
//        if (db == null) {
//            Timber.e("ListViewModel - addTitleToDatabase db=null")
//            return
//        }
//        Timber.d("ListViewModel - addTitlesToDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
//        val titleDao = db.titleDao()
//        val titlesRepo = TitlesRepo(titleDao = titleDao)
//        val idNew = titlesRepo.insertTitle(titleEntity) // add one title
//        val titleEntityNew = titleEntity.copy(id = idNew!!.toInt())
//        _titleEntities.update { currentList -> currentList + titleEntityNew }
//        MyData.titleEntities.add(titleEntityNew)
//    }
//
//    fun updateTitleInDatabaseAsync(titleEntity:TitleEntity) {
//        Timber.d("ListViewModel - updateTitleInDatabaseAsync")
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    updateTitleInDatabase(titleEntity)
//                    _titleEntities.update { oldList ->
//                        oldList.map { if (it.id == titleEntity.id) titleEntity else it }
//                    }
//                } catch (e: Exception) {
//                    Timber.e("ListViewModel - updateTitleInDatabaseAsync error=" + e)
//                }
//            }
//        }
//    }
//
//    suspend fun updateTitleInDatabase(titleEntity: TitleEntity) {
//        Timber.d("ListViewModel - updateTitleInDatabase")
//        val db = MyApp.getInstance().getDatabase()
//        if (db == null) {
//            Timber.e("ListViewModel - updateTitleInDatabase db=null")
//            return
//        }
//        Timber.d("ListViewModel - updateTitleInDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
//        val titleDao = db.titleDao()
//        val titlesRepo = TitlesRepo(titleDao = titleDao)
//        titlesRepo.updateTitle(titleEntity)
//    }
//
//    suspend fun deleteTitleInDatabase(titleEntity: TitleEntity) {
//        Timber.d("ListViewModel - deleteTitleInDatabase")
//        val db = MyApp.getInstance().getDatabase()
//        if (db == null) {
//            Timber.e("ListViewModel - deleteTitleInDatabase db=null")
//            return
//        }
//        Timber.d("ListViewModel - deleteTitleInDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
//        val titleDao = db.titleDao()
//        val titlesRepo = TitlesRepo(titleDao = titleDao)
//        titlesRepo.deleteTitleEntity(titleEntity)
//    }
//
//
//    fun deleteTitleInDatabaseAsync(titleEntity:TitleEntity) {
//        Timber.d("ListViewModel - deleteTitleInDatabaseAsync")
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    deleteTitleInDatabase(titleEntity)
//                    _titleEntities.update { oldList ->
//                        oldList.filterNot { it.id == titleEntity.id }
//                    }
//                } catch (e: Exception) {
//                    Timber.e("ListViewModel - deleteTitleInDatabaseAsync error=" + e)
//                }
//            }
//        }
//    }
//
//    fun resetMessage() {
//        _message.value = ""
//    }
//
//    fun setMessage(newMessage:String){
//        _message.value = newMessage
//    }
//
//    fun onExportDatabase() {
//        Timber.d("ListViewModel - onExportDatabase")
//        val databasePath = context.getDatabasePath(MyConst.DB_NAME)
//
//        if (!databasePath.exists()) {
//            Timber.e("ListViewModel - onExportDatabase - not found")
//            _message.value = context.resources.getString(R.string.db_not_found, MyConst.DB_NAME)
//            return
//        }
//        val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val destPath = "/" + Environment.DIRECTORY_DOWNLOADS + "/" + MyConst.DB_NAME
////        if (!destDir.exists()) {
////            destDir.mkdirs()
////        }
//        val destFile = File(destDir, MyConst.DB_NAME)
//        if (destFile.exists()){
//            Timber.d("ListViewModel - onExportDatabase - destFile.exists=true" )
//            destFile.delete()
//        }
//        Timber.d("ListViewModel - onExportDatabase - destFile.exists=" + destFile.exists())
//        try {
//            databasePath.copyTo(destFile, overwrite = true)
//            Timber.d("ListViewModel - onExportDatabase - exported")
//            _message.value = context.resources.getString(R.string.db_exported)
//        } catch (e: Exception) {
//            Timber.e("ListViewModel - onExportDatabase - error=" + e)
//            _message.value = context.resources.getString(R.string.db_exported)
//        }
//    }
//
//    fun exportDatabaseFromUri(context:Context, uri: Uri) {
//        val destPath = getFileNameFromUri(context, uri)
//        try {
//            val dbPath = File(context.filesDir.parent, "databases/${MyConst.DB_NAME}")
//            val outputStream = context.contentResolver.openOutputStream(uri)
//
//            FileInputStream(dbPath).use { input ->
//                outputStream?.use { output ->
//                    input.copyTo(output)
//                }
//            }
//            Timber.d("ExportDatabaseTool - db exported")
//            _message.value = context.resources.getString(R.string.db_exported, destPath)
//        } catch (e: Exception) {
//            Timber.e("ExportDatabaseTool - export error=$e")
//            _message.value = context.resources.getString(R.string.db_export_error, destPath)
//        }
//    }
//
//    fun importDatabaseFromUri(context:Context, uri: Uri) {
//        val sourcePath = getFileNameFromUri(context, uri)
//        Timber.d("ListViewModel - importDatabaseFromUri $sourcePath")
//        try {
//            MyApp.getInstance().closeDatabase()
//
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val databaseFile = context.getDatabasePath(MyConst.DB_NAME)
//
//            inputStream?.use { input ->
//                databaseFile.outputStream().use { output ->
//                    input.copyTo(output)
//                }
//            }
//
//            Timber.d("ListViewModel - onImportDatabase - imported")
//            _message.value = context.resources.getString(R.string.db_imported, sourcePath)
//
//            readTitleEntitiesFromDatabaseAsync()
//
//        } catch (e: Exception) {
//            Timber.e("ListViewModel - onImportDatabase - error=" + e)
//            _message.value = context.resources.getString(R.string.db_import_error, sourcePath)
//        }
//    }
//
//    fun getFileNameFromUri(context: Context, uri: Uri): String? {
//        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
//        returnCursor?.use { cursor ->
//            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            if (cursor.moveToFirst() && nameIndex != -1) {
//                return cursor.getString(nameIndex)
//            }
//        }
//        return null
//    }
//
//}