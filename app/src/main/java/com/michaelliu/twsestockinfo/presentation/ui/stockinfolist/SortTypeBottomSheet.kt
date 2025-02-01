package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelliu.twsestockinfo.databinding.BottomSheetSortTypeBinding
import com.michaelliu.twsestockinfo.domain.model.SortType

class SortTypeBottomSheet(
    private val selectedSortType: SortType,
    private val onSortTypeSelected: (SortType) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSortTypeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSortTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (selectedSortType) {
            SortType.BY_CODE_DESC -> binding.rbCodeDesc.isChecked = true
            SortType.BY_CODE_ASC -> binding.rbCodeAsc.isChecked = true
        }

        binding.rgSortOptions.setOnCheckedChangeListener { _, checkedId ->
            val newType = when (checkedId) {
                binding.rbCodeDesc.id -> SortType.BY_CODE_DESC
                binding.rbCodeAsc.id -> SortType.BY_CODE_ASC
                else -> SortType.BY_CODE_DESC
            }
            onSortTypeSelected(newType)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}