package com.ekdorn.silentium.mvs

import android.app.Application
import androidx.lifecycle.*
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus


class DialogsViewModel(application: Application) : AndroidViewModel(application) {
    private val daoScope = viewModelScope.plus(Dispatchers.IO)

    private val dao = DatabaseManager[application].dialogDAO()
    val dialogs: LiveData<List<Dialog>> = dao.getAll()

    fun removeDialog(index: Int) = daoScope.launch {
        dao.delete(dialogs.value!![index].contact.id)
    }
}
