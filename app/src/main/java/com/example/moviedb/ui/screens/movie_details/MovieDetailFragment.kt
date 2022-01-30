package com.example.moviedb.ui.screens.movie_details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentMovieDetailBinding
import com.example.moviedb.data.api.responses.MovieDetailsResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()
    private val safeArgs: MovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        movieDetailsViewModel.getMovieDetails(safeArgs.movieId)
    }

    private fun observeData() {
        movieDetailsViewModel.apply {
            movieDetails.observe(viewLifecycleOwner, {
                updateViews(it)
            })

            error.observe(viewLifecycleOwner, { errorMessage ->
                binding.root.visibility = View.GONE
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.similarMovieListFragment -> findNavController().navigate(
                MovieDetailFragmentDirections.actionMovieDetailFragmentToSimilarMovieListFragment(
                    safeArgs.movieId
                )
            )
        }
        return false
    }

    private fun updateViews(movieDetails: MovieDetailsResponse) {
        with(movieDetails) {

            binding.apply {
                movieTitle.text = title
                movieOverview.text = overview
                movieOverview.movementMethod = ScrollingMovementMethod.getInstance()
                movieReleaseDate.text = release_date
                movieRating.text = popularity.toString()
                movieVotes.text = vote_count.toString()

                context?.let {
                    Glide.with(it)
                        .load(URL + poster_path)
                        .into(moviePoster)
                }
            }
        }
    }

    companion object {
        const val URL = "https://image.tmdb.org/t/p/w500"
    }
}