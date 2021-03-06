package divyansh.tech.kotnewreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Operation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import divyansh.tech.kotnewreader.R
import divyansh.tech.kotnewreader.adapters.NewsAdapter
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_favourites.*
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesFragment : BaseFragment() {

    @Inject
    lateinit var newsAdapter: NewsAdapter

    override fun provideView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.titleText.text = getString(R.string.FavoritesTitle)
        Log.i("INJECTED FROM FAV ", viewModel.newRepository.db.hashCode().toString() + " api ->" + viewModel.newRepository.api.hashCode().toString())
        setupObservers(view)
        setupItemTouchHelperCallback(view)
        setupRecyclerView()
    }

    private fun setupItemTouchHelperCallback(view: View) {
        val ItemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val article = newsAdapter.differ.currentList[viewHolder.adapterPosition]
                viewModel.deleteArticle(article)
                Snackbar.make(view, getString(R.string.articleDeleted), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)) {
                        viewModel.upsertArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(ItemTouchHelperCallback).attachToRecyclerView(rvSavedNews)
    }

    private fun setupRecyclerView() {
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable(getString(R.string.articleArgument), it)
            }
            findNavController().navigate(
                R.id.action_favouritesFragment_to_articleFragment,
                bundle
            )
        }
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupObservers(view: View) {
        viewModel.getAllArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

        viewModel.syncNews(context!!).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Operation.State.IN_PROGRESS -> Snackbar.make(view, getString(R.string.syncStarted), Snackbar.LENGTH_SHORT).show()

                is Operation.State.FAILURE -> Snackbar.make(view, getString(R.string.syncFailed), Snackbar.LENGTH_SHORT).show()

                is Operation.State.SUCCESS -> Snackbar.make(view, getString(R.string.syncCompleted), Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}