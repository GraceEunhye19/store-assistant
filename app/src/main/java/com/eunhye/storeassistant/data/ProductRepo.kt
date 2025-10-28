package com.eunhye.storeassistant.data

import kotlinx.coroutines.flow.Flow

class ProductRepo(private val productDao: ProductDao){
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product): Long{
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product){
        return productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product){
        return productDao.deleteProduct(product)
    }
}