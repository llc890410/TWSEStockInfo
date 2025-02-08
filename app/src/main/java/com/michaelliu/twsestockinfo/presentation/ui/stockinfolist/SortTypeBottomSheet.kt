package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelliu.twsestockinfo.databinding.BottomSheetSortTypeBinding
import com.michaelliu.twsestockinfo.domain.model.SortType

class SortTypeBottomSheet : BottomSheetDialogFragment() {
    interface SortTypeBottomSheetListener {
        fun onSortTypeSelected(sortType: SortType)
    }

    companion object {
        private const val ARG_SORT_TYPE = "ARG_SORT_TYPE"

        fun newInstance(selectedSortType: SortType): SortTypeBottomSheet {
            val fragment = SortTypeBottomSheet()
            val args = Bundle().apply {
                putString(ARG_SORT_TYPE, selectedSortType.name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: BottomSheetSortTypeBinding? = null
    private val binding get() = _binding!!
    private var listener: SortTypeBottomSheetListener? = null

    private val selectedSortType: SortType by lazy {
        val typeName = arguments?.getString(ARG_SORT_TYPE, SortType.BY_CODE_DESC.name)
        SortType.valueOf(typeName ?: SortType.BY_CODE_DESC.name)
    }

    fun setSortTypeBottomSheetListener(listener: SortTypeBottomSheetListener) {
        this.listener = listener
    }

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

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.post {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // 強制展開全部
            }
        }

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
            listener?.onSortTypeSelected(newType)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}