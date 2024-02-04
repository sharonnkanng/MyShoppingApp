# MyShoppingApp

This is a basic Shopping list application where users can see the items that they want to buy in a shop, and they can mark which item has been bought.

There are several features in this app:

1. It starts with an opening screen that displays a custom logo or image and jumps to the Shopping List after 3 seconds.
2. An item has the following attributes: category, icon, name, description, estimated price and status (whether it has been bought or not)
3. The shopping list displays the items in a LazyColumn with an icon for the item(an image based on the category), a check box weather it has been bought or not (user can change it during shopping), name of the item, additional attributes can also be displayed.
4. The shopping list has a “New Item” menu in the TopAppBar that opens a New Item Dialog, where the user can pick up new items that appear on the Shopping List.
5. Proper error messages are shown in case of empty fields, etc.
6. The shopping list supports removing items in two ways: one-by-one and all items atthe same time (“Delete all” button).
7. The shopping list Activity supports editing Items.
8.  Room is used for storing the items as a database/persistence data storage.
