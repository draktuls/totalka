package cz.viriom.totaler.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.viriom.totaler.history.entities.Analysis
import cz.viriom.totaler.history.entities.History

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analysis")
    fun getAll(): List<Analysis>

    @Query("SELECT * FROM analysis WHERE id=:id")
    fun getById(id: Long): Analysis

    @Insert
    fun insertAll(vararg analyses: Analysis): List<Long>

    @Delete
    fun delete(analysis: Analysis)

    @Query("DELETE FROM analysis")
    fun nuke()
}