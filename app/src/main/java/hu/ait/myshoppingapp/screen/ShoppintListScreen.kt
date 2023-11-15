package hu.ait.myshoppingapp.screen

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.myshoppingapp.R
import hu.ait.myshoppingapp.data.ItemCategory
import hu.ait.myshoppingapp.data.ShoppingItem
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    shoppingViewModel: ShoppingViewModel = hiltViewModel()
) {
    val resources = LocalContext.current.resources
    val title = resources.getString(R.string.top_bar)
    val noItemString = resources.getString(R.string.empty_list)
    val all_deleted = resources.getString(R.string.all_delete)
    val delete = resources.getString(R.string.delete)

    var showAddShoppingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showNotPurchasedItemDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var itemToEdit: ShoppingItem? by rememberSaveable {
        mutableStateOf(null)
    }
    val itemList by shoppingViewModel.getAllItems().collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        shoppingViewModel.deleteAllItems()
                        scope.launch {
                            snackbarHostState.showSnackbar(all_deleted)
                        }
                    }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                    IconButton(onClick = {
                        itemToEdit = null
                        showAddShoppingDialog = true
                    }) {
                        Icon(Icons.Filled.AddCircle, null)
                    }
                    IconButton(onClick = {
                        showNotPurchasedItemDialog = true
                    }) {
                        Icon(Icons.Filled.ShoppingCart, null)
                    }
                })
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            if (showNotPurchasedItemDialog)
                NotPurchasedItemsList(shoppingViewModel = shoppingViewModel) {
                    showNotPurchasedItemDialog = false
                }
            if (showAddShoppingDialog)
                AddNewTodoForm(
                    shoppingViewModel,
                    { showAddShoppingDialog = false },
                    itemToEdit,
                    {message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )

            if (itemList.isEmpty())
                Text(text = noItemString, modifier = Modifier.padding(10.dp), fontSize = 20.sp)

            else {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(itemList) {
                        ItemCard(it,
                            onRemoveItem = {
                                shoppingViewModel.deleteItem(it)
                                scope.launch {
                                    snackbarHostState.showSnackbar(delete)
                                }}
                            ,

                            onItemCheckChange = { checkState ->
                                shoppingViewModel.changeItemState(it, checkState)
                            },
                            onEditItem = { editedTodoItem ->
                                itemToEdit = editedTodoItem
                                shoppingViewModel.editItem(editedTodoItem)
                                showAddShoppingDialog = true
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddNewTodoForm(
    shoppingViewModel: ShoppingViewModel,
    onDialogDismiss: () -> Unit = {},
    itemToEdit: ShoppingItem? = null,
    onFormReady: (String) -> Unit = {}
) {

    val resources = LocalContext.current.resources
    val item_name = resources.getString(R.string.enter_item_name)
    val item_price = resources.getString(R.string.enter_item_price)
    val item_des = resources.getString(R.string.enter_item_des)
    val purchased = resources.getString(R.string.purchased)
    val save_btn = resources.getString(R.string.save_btn)
    val edit_btn = resources.getString(R.string.edit_btn)
    val error_msg = resources.getString(R.string.error_msg)
    val empty_string = resources.getString(R.string.empty_string)
    val update = resources.getString(R.string.update)
    val create = resources.getString(R.string.create)

    Dialog(
        onDismissRequest = {
            onDialogDismiss()
        }) {
        var itemTitle by rememberSaveable {
            mutableStateOf(itemToEdit?.name ?: "")
        }
        var itemPrice by rememberSaveable {
            mutableStateOf(itemToEdit?.price ?: "")
        }
        var itemDes by rememberSaveable {
            mutableStateOf(itemToEdit?.description ?: "")
        }
        var isPurchased by rememberSaveable {
            mutableStateOf(itemToEdit?.isPurchased ?: false)
        }
        var selectedCategory by rememberSaveable {
            mutableStateOf(
                itemToEdit?.category ?: ItemCategory.Food
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            {
                item {
                    Column(
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = itemTitle,
                            onValueChange = {
                                if (it.isEmpty()) {
                                    itemTitle = empty_string
                                } else {
                                    itemTitle = it
                                }
                            },
                            label = { Text(text = item_name) }
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = itemPrice,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            onValueChange = {
                                if (it.isEmpty()) {
                                    itemPrice = empty_string
                                } else {
                                    itemPrice = it.toFloatOrNull()?.toString() ?: empty_string
                                }
                            },
                            label = { Text(text = item_price) }
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = itemDes,
                            onValueChange = {
                                itemDes = it
                            },
                            label = { Text(text = item_des) }
                        )

                        DropDownMenu(
                            list = ItemCategory.values().map { it.name },
                            preselected = selectedCategory.name,
                            onSelectionChanged = { category ->
                                selectedCategory = ItemCategory.valueOf(category)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isPurchased, onCheckedChange = {
                                isPurchased = it
                            }
                            )
                            Text(text = purchased)
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = {
                                if (itemTitle.isNotEmpty() && itemPrice.isNotEmpty()) {
                                    if (itemToEdit == null) {
                                        shoppingViewModel.addItem(
                                            ShoppingItem(
                                                0,
                                                itemTitle,
                                                itemDes,
                                                selectedCategory,
                                                itemPrice,
                                                isPurchased
                                            )
                                        )
                                        onFormReady(create)
                                    }
                                    else {
                                        var itemEdited = itemToEdit.copy(
                                            name = itemTitle,
                                            description = itemDes,
                                            category = selectedCategory,
                                            price = itemPrice,
                                            isPurchased = isPurchased
                                        )
                                        shoppingViewModel.editItem(itemEdited)
                                        onFormReady(update)
                                    }
                                    onDialogDismiss()
                                }
                            })

                            {
                                if (itemToEdit == null) {
                                    Text(text = save_btn)
                                } else {
                                    Text(text = edit_btn)
                                }
                            }
                        }
                        Row {
                            if (itemTitle.isEmpty() || itemPrice.isEmpty()) {

                                ErrorMessage(message = error_msg)

                            } else {
                                ErrorMessage(message = empty_string)
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun DropDownMenu(
    list: List<String>,
    preselected: String,
    onSelectionChanged: (myData: String) -> Unit, modifier: Modifier = Modifier
){
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value
    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        } ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top,
        ){ Text(
            text = selected,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
            Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(fraction = 0.7f)
            ){
                list.forEach { listEntry ->
                    DropdownMenuItem( onClick = {
                        selected = listEntry
                        expanded = false
                        onSelectionChanged(selected)
                    },
                        text = {
                            Text(
                                text = listEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            ) },
                    )
                } }
        }
    }
    }

@Composable
fun ItemCard(
    shopItem: ShoppingItem,
    onItemCheckChange: (Boolean) -> Unit = {},
    onRemoveItem: () -> Unit = {},
    onEditItem: (ShoppingItem) -> Unit = {}
) {
    val resources = LocalContext.current.resources
    val detail_title = resources.getString(R.string.detail_title)
    val dollar_sign = resources.getString(R.string.dollar_sign)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        var expanded by rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .padding(15.dp)
                .animateContentSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = shopItem.category.getIcon()),
                    contentDescription = "Category",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        shopItem.name,
                        style = TextStyle(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(dollar_sign + shopItem.price, style = TextStyle(fontSize = 14.sp))
                }
                Spacer(modifier = Modifier.fillMaxSize(0.35f))
                Checkbox(
                    checked = shopItem.isPurchased,
                    onCheckedChange = { onItemCheckChange(it) }
                )

                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.clickable {
                        onRemoveItem()
                    },
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Filled.Build,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable {
                        onEditItem(shopItem)
                    },
                    tint = Color.Blue
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) {
                            "Less"
                        } else {
                            "More"
                        }
                    )
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = detail_title + shopItem.description,
                    style = TextStyle(
                        fontSize = 15.sp,
                    )
                )
            }
        }
    }
}

@Composable
fun NotPurchasedItemsList(
    shoppingViewModel: ShoppingViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    val sumOfPurchasedItems: Float by shoppingViewModel.getPurchasedItemsSum().collectAsState(initial = 0.0f)
    val resources = LocalContext.current.resources
    val total_purchased = resources.getString(R.string.total_purchased)
    val close = resources.getString(R.string.close)

    Dialog(onDismissRequest = {
        onDialogDismiss()
    }) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = total_purchased,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = sumOfPurchasedItems.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(8.dp)
                )

                Button(
                    onClick = { onDialogDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(close, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
