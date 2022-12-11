package cz.viriom.totaler.history.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Analysis (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "analysis_json") var analysis_json: String?
)