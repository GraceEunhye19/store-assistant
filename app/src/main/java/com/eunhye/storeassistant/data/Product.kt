package com.eunhye.storeassistant.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "item")
    val item: String,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "img_path")
    val imgPath: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)