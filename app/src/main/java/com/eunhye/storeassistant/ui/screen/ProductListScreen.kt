package com.eunhye.storeassistant.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eunhye.storeassistant.ui.components.ProductCard
import com.eunhye.storeassistant.ProductViewModel
import com.eunhye.storeassistant.data.Product
import com.eunhye.storeassistant.ui.components.EmptyState
import com.eunhye.storeassistant.ui.components.DeleteConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onAddClick: () -> Unit,
    onProductEditClick: (Int) -> Unit,
    viewModel: ProductViewModel = viewModel()
){
    val products by viewModel.allProducts.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Your Lil' Store Assistant",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ){
        paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)))
                .padding(paddingValues)
        ){
            if (products.isEmpty()){
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onEditClick = {
                                onProductEditClick(product.id)
                            },
                            onDeleteClick = {
                                productToDelete = product
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if(showDeleteDialog && productToDelete != null){
        DeleteConfirmationDialog(
            itemName = productToDelete!!.item,
            onConfirm = {
                viewModel.deleteProduct(productToDelete!!)
                showDeleteDialog = false
                productToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                productToDelete = null
            }
        )
    }
}