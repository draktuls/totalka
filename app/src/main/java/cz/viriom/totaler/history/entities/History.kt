package cz.viriom.totaler.history.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "analysis_id") var analysis_id: Long,
    @ColumnInfo(name = "url") var url: String?,
    @ColumnInfo(name = "date") var date: Int
)