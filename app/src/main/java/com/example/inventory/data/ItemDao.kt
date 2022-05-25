package com.example.inventory.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    //onConflict: 同じ主キーのアイテムをインサートしようとしたら無視
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    //渡されたエンティティを更新
    @Update
    suspend fun update(item: Item)

    //エンティティを消す
    @Delete
    suspend fun delete(item: Item)

    //:id で関数内の引数を取得
    //Flowをつけると、データが更新されるたびに通知を受け取る
    //（つまり、最初に取得するだけでよい）
    //よって、coroutine内で呼ばなくてよい
    //→ suspendはいらない
    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    //itemのすべての列を昇順で取得
    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>
}