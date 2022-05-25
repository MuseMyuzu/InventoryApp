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

    //idからLiveDataのItemを取得
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    //Itemを更新
    private fun updateItem(item: Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    //SELLボタンを押したときの処理
    fun sellItem(item: Item) {
        //在庫が0より大きいか確認
        if (item.quantityInStock > 0) {
            //在庫数を1減らす（copy関数で、一部の値だけ異なるitemを作成）
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            //新しいitemで古いitemを上書き
            updateItem(newItem)
        }
    }

    //在庫が0より大きいか確認
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    //itemを削除
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    //渡された情報からItemインスタンスを作成
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
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