package com.eunhye.storeassistant


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eunhye.storeassistant.data.InventoryDatabase
import com.eunhye.storeassistant.data.Product
import com.eunhye.storeassistant.data.ProductRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(application: Application): AndroidViewModel(application){

    private val repo: ProductRepo

    val allProducts: StateFlow<List<Product>>

    init {
        val productDao = InventoryDatabase.getDatabase(application).productDao()
        repo = ProductRepo(productDao)

        allProducts = repo.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insertProduct(product: Product){
        viewModelScope.launch {
            repo.insertProduct(product)
        }
    }

    fun updateProduct(product: Product){
        viewModelScope.launch {
            repo.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product){
        viewModelScope.launch {
            product.imgPath?.let { path -> ImageUtils.deleteImage(path) }
            repo.deleteProduct(product)
        }
    }

    suspend fun getProductById(id:Int): Product?{
        return repo.getProductById(id)
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            // Delete all images first
            allProducts.value.forEach { product ->
                product.imgPath?.let { path ->
                    ImageUtils.deleteImage(path)
                }
            }
            // Then clear database
            allProducts.value.forEach { product ->
                repo.deleteProduct(product)
            }
        }
    }
}