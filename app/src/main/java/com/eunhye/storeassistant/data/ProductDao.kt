package com.eunhye.storeassistant.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao{
    @Query ("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteALlProducts()
}