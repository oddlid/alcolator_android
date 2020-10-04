package net.oddware.alcolator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "drinks")
data class Drink(
    @Expose
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @Expose
    @SerializedName("tag")
    @ColumnInfo(name = "tag")
    var tag: String = "",
    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String = "",
    @Expose
    @SerializedName("volume")
    @ColumnInfo(name = "volume")
    var volumeML: Int = 0,
    @Expose
    @SerializedName("percentage")
    @ColumnInfo(name = "percentage")
    var alcPct: Double = 0.0,
    @Expose
    @SerializedName("price")
    @ColumnInfo(name = "price")
    var price: Double = 0.0
) : Serializable, Comparable<Drink> {
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
