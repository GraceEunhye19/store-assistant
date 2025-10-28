package com.eunhye.storeassistant

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eunhye.storeassistant.ui.screen.AddEditProductScreen
import com.eunhye.storeassistant.ui.screen.ProductListScreen

sealed class Screen(val route: String){
    object ProductList: Screen("product_list")
    object AddProduct: Screen("add_product")
    object EditProduct: Screen("edit_product/{productId}"){
        fun createRoute(productId: Int) = "edit_product/$productId"
    }
}

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = "product_list"
    ){
        composable(Screen.ProductList.route){
            ProductListScreen(
                onAddClick = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onProductEditClick = {
                    productId -> navController.navigate(Screen.EditProduct.createRoute(productId))
                }
            )
        }

        composable(Screen.AddProduct.route){
            AddEditProductScreen(
                productId = null,
                onNavigateBack = {navController.popBackStack()}
            )
        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId"){type = NavType.IntType})
            ){
            backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            AddEditProductScreen(
                productId = productId,
                onNavigateBack = {navController.popBackStack()}
            )
        }
    }
}
