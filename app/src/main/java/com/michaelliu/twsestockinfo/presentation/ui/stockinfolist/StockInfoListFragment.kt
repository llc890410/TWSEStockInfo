package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelliu.twsestockinfo.databinding.FragmentStockInfoListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockInfoListFragment : Fragment() {

    private var _binding: FragmentStockInfoListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockInfoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}