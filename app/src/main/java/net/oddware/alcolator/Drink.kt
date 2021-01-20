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

    fun alcML(): Double {
        if (0 == volumeML) {
            return 0.0
        }
        return ((volumeML / 100.0) * alcPct)
    }

    fun waterML(): Double = volumeML - alcML()

    fun pricePerAlcML(): Double {
        if (0.0 == price || 0.0 == alcML()) {
            return 0.0
        }
        return price / alcML()
    }

    // This needs a check for 0
    fun pricePerDrinkML(): Double {
        if (0 == volumeML || 0.0 == price) {
            return 0.0
        }
        return price / volumeML
    }

    // matchAlcML will return how many milliliters of other drink is needed to match milliliters of
    // alcohol in this drink
    fun matchAlcML(other: Drink): Double {
        if (0.0 == alcML() || 0.0 == other.alcML()) {
            return 0.0
        }
        // We first divide this.alcML() with other.alcML(), and then multiply the result
        // with other.volumeML
        //val div = alcML() / other.alcML()
        //val res = div * other.volumeML
        //return res
        return (alcML() / other.alcML()) * other.volumeML
    }

    // Find how many other drinks it takes to get as much alcohol as in this drink
    fun matchHowMany(other: Drink): Double {
        val otherML = matchAlcML(other)
        if (0.0 == otherML) {
            return 0.0
        }
        return otherML / other.volumeML
    }

    override fun compareTo(other: Drink): Int {
        return id.compareTo(other.id)
    }
}
