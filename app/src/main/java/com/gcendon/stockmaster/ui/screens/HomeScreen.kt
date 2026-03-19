package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.gcendon.stockmaster.ui.components.ProductCard
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    viewModel: ProductViewModel = viewModel()
) {
    val productList by viewModel.products.collectAsState()

    LazyColumn(contentPadding = innerPadding) {
        items(productList) { product ->
            ProductCard(product = product)
        }
    }
}