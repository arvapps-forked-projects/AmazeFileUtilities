/*
 * Copyright (C) 2021-2022 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page.ui.files

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amaze.fileutilities.home_page.ui.media_tile.MediaTypeView
import com.amaze.fileutilities.utilis.FileUtils
import com.amaze.fileutilities.utilis.PreferencesConstants
import com.amaze.fileutilities.utilis.Utils
import com.amaze.fileutilities.utilis.getAppCommonSharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider

abstract class AbstractMediaInfoListFragment : Fragment(), MediaFileAdapter.OptionsMenuSelected {

    private var recyclerViewPreloader: RecyclerViewPreloader<String>? = null
    private var mediaFileAdapter: MediaFileAdapter? = null
    private var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
    private var gridLayoutManager: GridLayoutManager? = GridLayoutManager(context, 3)
    private val MAX_PRELOAD = 100

    override fun sortBy(sortingPreference: MediaFileListSorter.SortingPreference) {
        mediaFileAdapter?.invalidateData(sortingPreference)
    }

    override fun groupBy(sortingPreference: MediaFileListSorter.SortingPreference) {
        mediaFileAdapter?.invalidateData(sortingPreference)
    }

    override fun switchView(isList: Boolean) {
        resetAdapter()
    }

    override fun select(headerPosition: Int) {
        getRecyclerView().scrollToPosition(headerPosition + 5)
    }

    abstract fun getFileStorageSummaryAndMediaFileInfoPair():
        Pair<FilesViewModel.StorageSummary, List<MediaFileInfo>?>?

    abstract fun getMediaAdapterPreloader(): MediaAdapterPreloader

    abstract fun getRecyclerView(): RecyclerView

    // make sure to set getFileStorageSummaryAndMediaFileInfoPair before calling
    fun resetAdapter() {
        getFileStorageSummaryAndMediaFileInfoPair()?.let {
            val storageSummary = it.first
            val mediaFileInfoList = it.second
            mediaFileInfoList?.let {
                val usedSpace =
                    FileUtils.formatStorageLength(
                        requireContext(), storageSummary.usedSpace!!
                    )
                val totalSpace = FileUtils.formatStorageLength(
                    requireContext(), storageSummary.totalSpace!!
                )
                // set list adapter
                val sizeProvider = ViewPreloadSizeProvider<String>()
                recyclerViewPreloader = RecyclerViewPreloader(
                    Glide.with(requireActivity()),
                    getMediaAdapterPreloader(),
                    sizeProvider,
                    MAX_PRELOAD
                )
                val isList = requireContext()
                    .getAppCommonSharedPreferences().getBoolean(
                        PreferencesConstants.KEY_MEDIA_LIST_TYPE,
                        PreferencesConstants.DEFAULT_MEDIA_LIST_TYPE
                    )
                mediaFileAdapter = MediaFileAdapter(
                    requireContext(),
                    getMediaAdapterPreloader(),
                    this@AbstractMediaInfoListFragment, !isList,
                    MediaFileListSorter.SortingPreference.newInstance(
                        requireContext()
                            .getAppCommonSharedPreferences()
                    ),
                    ArrayList(mediaFileInfoList), MediaFileInfo.MEDIA_TYPE_AUDIO
                ) {
                    it.setProgress(
                        MediaTypeView.MediaTypeContent(
                            storageSummary.items, usedSpace,
                            storageSummary.progress, totalSpace
                        )
                    )
                }
                getRecyclerView().addOnScrollListener(recyclerViewPreloader!!)
                Utils.setGridLayoutManagerSpan(gridLayoutManager!!, mediaFileAdapter!!)
                getRecyclerView().layoutManager =
                    if (isList) linearLayoutManager else gridLayoutManager
                getRecyclerView().adapter = mediaFileAdapter
            }
        }
    }
}