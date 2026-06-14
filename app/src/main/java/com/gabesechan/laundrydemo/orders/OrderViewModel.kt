package com.gabesechan.laundrydemo.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.models.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val ordersServer: OrdersServer
): ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    var sortedOrders: List<Order> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val orders = ordersServer.getAll().process()
            sortedOrders = orders.sortedWith(
                compareBy<Order> { it.state == "COMPLETED" }
                    .thenByDescending { it.submitted }
            )
            _isLoaded.value = true
        }
    }


}