package net.oddware.alcolator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "drinks")
@Serializable
data class Drink(
    @SerialName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerialName("tag")
    @ColumnInfo(name = "tag")
    var tag: String = "",
    @SerialName("name")
    @ColumnInfo(name = "name")
    var name: String = "",
    @SerialName("volume")
    @ColumnInfo(name = "volume")
    var volumeML: Int = 0,
    @SerialName("percentage")
    @ColumnInfo(name = "percentage")
    var alcPct: Double = 0.0,
    @SerialName("price")
    @ColumnInfo(name = "price")
    var price: Double = 0.0
) : Comparable<Drink> {
    fun volumeAsDL(): Double = volumeML / 100.0

    fun volumeAsL(): Double = volumeML / 1000.0

    fun alcML(): Double = ((volumeML / 100.0) * alcPct)

    fun waterML(): Double = volumeML - alcML()

    fun pricePerAlcML(): Double = price / alcML()

    fun pricePerDrinkML(): Double = price / volumeML

    override fun compareTo(other: Drink): Int {
        return id.compareTo(other.id)
    }
}
