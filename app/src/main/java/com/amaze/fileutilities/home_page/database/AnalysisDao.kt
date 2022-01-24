/*
 * Copyright (C) 2021-2022 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page.database

import androidx.room.*

@Dao
interface AnalysisDao {

    @Query("SELECT * FROM analysis")
    fun getAll(): List<Analysis>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(analysis: Analysis)

    @Delete
    fun deleteAll(vararg analysis: Analysis)

    @Delete
    fun delete(user: Analysis)
}