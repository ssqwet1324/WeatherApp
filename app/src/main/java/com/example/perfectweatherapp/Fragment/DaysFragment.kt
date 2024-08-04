package com.example.perfectweatherapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectweatherapp.Adapters.WeatherAdapterNewActivity
import com.example.perfectweatherapp.MainViewModel
import com.example.perfectweatherapp.databinding.FragmentDaysBinding

class DaysFragment : Fragment() {
    private lateinit var binding: FragmentDaysBinding
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: WeatherAdapterNewActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeData()
    }

    private fun initRecyclerView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = WeatherAdapterNewActivity()
        rcView.adapter = adapter
    }

    private fun observeData() {
        model.liveDataList.observe(viewLifecycleOwner) { data ->
            (binding.rcView.adapter as WeatherAdapterNewActivity).submitList(data.subList(2, data.size))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}
