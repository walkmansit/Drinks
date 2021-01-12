package com.office14.coffeedose.data.database


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SizeDao {

    @Query("select * from drink_size where drink_id = :id")
    fun getSizesByDrinkId(id: Int): Flow<List<SizeDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSizes(vararg sizes: SizeDbo)

    @Query("delete from drink_size")
    fun clear()

    @Query("delete from drink_size where drink_id = :id")
    fun deleteByDrinkId(id: Int)

    @Query("delete from drink_size where id not in (:idsList)")
    fun clearByNotInList(idsList: List<Int>)


    @Transaction
    fun refreshSizes(addins: List<SizeDbo>) {
        insertAllSizes(*addins.toTypedArray())
        clearByNotInList(addins.map { it.id })
    }
}