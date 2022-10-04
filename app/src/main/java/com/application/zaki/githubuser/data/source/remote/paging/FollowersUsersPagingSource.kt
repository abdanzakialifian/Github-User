package com.application.zaki.githubuser.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.zaki.githubuser.data.source.remote.ApiService
import com.application.zaki.githubuser.data.source.remote.response.ListUsersResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowersUsersPagingSource @Inject constructor(private val apiService: ApiService) :
    PagingSource<Int, ListUsersResponse>() {
    private var username: String? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListUsersResponse> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getFollowersUser(username ?: "", position, params.loadSize)
            val data = response.body()
            val listFollowers = ArrayList<ListUsersResponse>()
            if (response.isSuccessful) {
                data?.let {
                    listFollowers.addAll(it)
                }
            }

            LoadResult.Page(
                data = listFollowers,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (listFollowers.isEmpty()) null else position + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListUsersResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    fun setUsername(username: String) {
        this.username = username
    }
}