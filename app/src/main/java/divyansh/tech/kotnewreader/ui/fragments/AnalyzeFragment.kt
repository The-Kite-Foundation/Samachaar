package divyansh.tech.kotnewreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import divyansh.tech.kotnewreader.R
import divyansh.tech.kotnewreader.utils.Alert
import divyansh.tech.kotnewreader.utils.Resource
import kotlinx.android.synthetic.main.fragment_analyze.*

class AnalyzeFragment : BaseFragment() {

    val args: AnalyzeFragmentArgs by navArgs()

    override fun provideView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_analyze, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSentimentText()
        setupEmotionText()
    }

    private fun setupSentimentText() {
        viewModel.getSentiments(args.query)
        viewModel.sentimentText.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    positiveSentiment.text = it.data?.pos.toString() + " - " + it.data?.pos_percent
                    neutralSentiment.text = it.data?.mid.toString() + " - " + it.data?.mid_percent
                    negativeSentiment.text = it.data?.neg.toString() + " - " + it.data?.neg_percent
                }

                is Resource.Error -> {
                    Alert.createAlertDialog(context!!).show()
                    it.message?.let {
                        Toast.makeText(activity, "Failed ${it}", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    Alert.createAlertDialog(context!!).show()
                }
            }
        })
    }

    private fun setupEmotionText() {
        viewModel.getCommunicationAnalysis(args.query)
        viewModel.getEmotionalAnalysis(args.query)
        viewModel.communicationText.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    communicationAnalysis.text = it.data?.get(0)?.predictions?.get(0)?.prediction + " - " + it.data?.get(0)?.predictions?.get(0)?.probability.toString()
                }

                is Resource.Error -> {
                    Alert.createAlertDialog(context!!).show()
                    it.message?.let {
                        Toast.makeText(activity, "Failed ${it}", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    Alert.createAlertDialog(context!!).show()
                }
            }
        })

        viewModel.emotionText.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    emotionalAnalysis.text = it.data?.get(0)?.predictions?.get(0)?.prediction + " " + it.data?.get(0)?.predictions?.get(0)?.probability.toString()
                }

                is Resource.Error -> {
                    Alert.createAlertDialog(context!!).show()
                    it.message?.let {
                        Toast.makeText(activity, "Failed ${it}", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    Alert.createAlertDialog(context!!).show()
                }
            }
        })
    }
}