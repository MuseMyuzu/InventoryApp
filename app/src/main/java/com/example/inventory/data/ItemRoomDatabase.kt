package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase: RoomDatabase() {
    //DAOの情報を与える
    abstract fun itemDao(): ItemDao

    companion object{
        //Volatile: キャッシュではなくメモリに保存し、
        //INSTANCEの値は常に最新になる
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context): ItemRoomDatabase{
            //INSTANCEを返すか、
            //nullならsynchronizedで初期化
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )
                    //Migrationのときは、古いデータベースを削除
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }


    }
}