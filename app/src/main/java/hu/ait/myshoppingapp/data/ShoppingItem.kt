package hu.ait.myshoppingapp.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.ait.myshoppingapp.R
import java.io.Serializable


@Entity(tableName = "shoppingtable")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "category") var category: ItemCategory,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "isPurchased") var isPurchased: Boolean
) : Serializable

enum class ItemCategory {
    Food, Clothing, Electronics, Other;

    fun getIcon(): Int {
        return if (this == Food) R.drawable.food else if (this == Clothing) R.drawable.clothing else if (this == Electronics) R.drawable.electronics else R.drawable.etc
    }

}