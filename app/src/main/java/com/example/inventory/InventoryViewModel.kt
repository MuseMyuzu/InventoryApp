package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

//非同期処理をしないといけないので、coroutineとViewModelを使う
class InventoryViewModel(private val itemDao: ItemDao): ViewModel() {
    //Flowが返ってくるので、asLiveData()でLiveDataに変換
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    //itemをデータベースに追加（addNewItem用）
    private fun insertItem(item: Item){
        //非同期で処理を実行
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    //三つの文字列からItemインスタンスを作成（addNewItem用）
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    //アイテムの詳細情報からデータベースに追加する（パブリック）
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    //入力されたテキストが有効か（空白でないか）確認する
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }
}

//InventoryViewModelをインスタンス化
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    //任意のクラスを引数に取る
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        //引数がInventoryViewModelと同じか確認
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}