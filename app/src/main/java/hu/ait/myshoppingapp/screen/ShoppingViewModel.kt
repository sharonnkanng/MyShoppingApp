package hu.ait.myshoppingapp.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.myshoppingapp.data.ShoppingDAO
import hu.ait.myshoppingapp.data.ShoppingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
        private val shoppingDAO: ShoppingDAO
    ): ViewModel() {

    fun addItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingDAO.insert(shoppingItem)
        }
    }

    fun getPurchasedItemsSum(): Flow<Float> {
        return shoppingDAO.getAllItems().map { list ->
            var sum: Float = 0.0f
            list.forEach {
                if (it.isPurchased) {
                    sum = it.price.toFloat() + sum
                }
            }
            sum
        }
    }

    fun getAllItems():Flow<List<ShoppingItem>> {
        return shoppingDAO.getAllItems()
    }

    fun changeItemState(shoppingItem: ShoppingItem, value: Boolean) {
        shoppingItem.isPurchased = value

        viewModelScope.launch {
            shoppingDAO.update(shoppingItem)
        }
    }

    fun deleteItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            shoppingDAO.delete(shoppingItem)
        }
    }

    fun editItem(todoItem: ShoppingItem) {
        viewModelScope.launch {
            shoppingDAO.update(todoItem)
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            shoppingDAO.deleteAllTodos()
        }
    }
}
