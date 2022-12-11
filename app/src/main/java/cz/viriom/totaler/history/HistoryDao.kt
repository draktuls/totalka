package cz.viriom.totaler.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.viriom.totaler.history.entities.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertAll(vararg urls: History)

    @Delete
    fun delete(url: History)

    @Query("DELETE FROM history")
    fun nuke()
}