package com.naltynbekkz.nulife.ui.clubs.front

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.naltynbekkz.nulife.R
import com.naltynbekkz.nulife.di.ViewModelProviderFactory
import com.naltynbekkz.nulife.ui.MainActivity
import com.naltynbekkz.nulife.ui.clubs.adapter.EventDaysAdapter
import com.naltynbekkz.nulife.ui.clubs.viewmodel.EventsViewModel
import com.naltynbekkz.nulife.util.Constants
import com.naltynbekkz.nulife.util.Convert
import kotlinx.android.synthetic.main.fragment_events.*
import javax.inject.Inject

class EventsFragment : Fragment() {

    @Inject
    lateinit var viewModelProvider: ViewModelProviderFactory
    private val viewModel: EventsViewModel by viewModels {
        viewModelProvider.create(this)
    }

    lateinit var adapter: EventDaysAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).clubsComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = EventDaysAdapter(
            click = fun(id: String) {
                findNavController().navigate(
                    AllEventsFragmentDirections.actionAllEventsFragmentToEventFragment(id)
                )
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.events.observe(viewLifecycleOwner, Observer {
            adapter.submitList(
                Convert.eventsToDays(
                    Convert.sortSavedEvents(
                        it.first,
                        if (arguments!!.getBoolean(Constants.ALL))
                            it.third
                        else
                            Convert.sortMyEvents(it.second, it.third)
                    )
                )
            )
        })

        recycler_view.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

}