package com.example.moviedb.ui.screens.home.tabs

import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.repository.MoviesRepository
import com.example.moviedb.utils.ErrorEntity
import com.example.moviedb.utils.defineErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class PopularMoviesListViewModel @Inject constructor(var repo: MoviesRepository) :
    MovieViewModel() {

    init {
        Timber.i("popular view model created")
        getData()
    }

    override fun getData() {
        if (!_isLoading.value!! && canLoadMore()) {
            viewModelScope.launch {
                _isLoading.postValue(true)
                repo.getPopularMovies(currentPage)
                    .collect { response ->
                        if (response.isSuccess()) {
                            response.data?.movies?.let { allLoadedMovies.addAll(it) }
                            _movieList.postValue(allLoadedMovies)
                            totalPages = response.data?.total_pages ?: 1
                            _isLoading.postValue(false)
                            currentPage++
                            Timber.i("Network request in popular")
                        } else {
                            _error.value =
                                defineErrorType(response.error ?: ErrorEntity.Unknown)
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        Timber.i("viewModel destroyed")
    }
}



