package com.gabesechan.laundrydemo.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R

@Composable
fun OrderScreen(navController: NavController, viewModel: OrderViewModel = hiltViewModel()) {
    val isLoaded by viewModel.isLoaded.collectAsState()
    OrderScreenInternal(isLoaded, viewModel.sortedOrders)
}

@Composable
fun OrderScreenInternal(isLoaded: Boolean, orders: List<GetOrder>) {
    if(isLoaded) {
        Column(
            Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            orders.forEach {
                OrderDisplay(it)
            }
        }
    }
}

@Composable
fun OrderDisplay(order: GetOrder) {
    Column() {
        Text(stringResource(R.string.order_num, order.id))
        Text(stringResource(R.string.status, order.state))
        order.lines.forEach { line->
            if(line.quantity != null && line.totalCost != null) {
                Text(stringResource(R.string.order_line_with_quantity, line.quantity, line.name, line.pricePerUnit, line.totalCost))
            }
            else {
                Text(stringResource(R.string.order_line_without_quantity,  line.name, line.pricePerUnit))
            }

        }
        Spacer(modifier = Modifier.height(12.dp))
    }

}

