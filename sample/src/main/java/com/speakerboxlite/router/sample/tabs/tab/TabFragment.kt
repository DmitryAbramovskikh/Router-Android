package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentTabBinding

class TabFragment: BaseViewModelFragment<TabViewModel, FragmentTabBinding>(R.layout.fragment_tab)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}