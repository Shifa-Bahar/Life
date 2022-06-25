package com.lifepharmacy.application.ui.pages.fragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentPageBinding
import com.lifepharmacy.application.databinding.FragmentVouchersBinding
import com.lifepharmacy.application.model.PageModel
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.pages.viewmodel.PageViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.adapters.ClickItemVoucher
import com.lifepharmacy.application.ui.vouchers.adapters.VouchersAdapter
import com.lifepharmacy.application.ui.vouchers.viewmodels.VouchersViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PageFragment : BaseFragment<FragmentPageBinding>(), ClickTool {
  companion object {
    fun getPageFragmentBundle(
      slug: String,
    ): Bundle {
      val bundle = Bundle()
      bundle.putString("slug", slug)
      return bundle
    }
  }

  private val viewModel: PageViewModel by viewModels()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      viewModel.slug.value = it.getString("slug")
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.tollBar.click = this

  }


  private fun observers() {
    viewModel.slug.observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it) {
          "contact-us" -> {
            binding.tollBar.tvToolbarTitle.text = getString(R.string.contact_us)
          }
          "about-us" -> {
            binding.tollBar.tvToolbarTitle.text = getString(R.string.about_us)
          }
          "terms-and-conditions" -> {
            binding.tollBar.tvToolbarTitle.text = getString(R.string.terms_and_condition)
          }
          "privacy-policy" -> {
            binding.tollBar.tvToolbarTitle.text = getString(R.string.privacy_policy)
          }
        }
      }
    })
    viewModel.getPage().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()

            it.data?.data?.let { it1 -> setContent(it1) }
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
            showLoading()

          }
        }
      }
    })
  }

  private fun setContent(content: PageModel) {
    binding.tollBar.tvToolbarTitle.text = content.title
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      binding.tvContent.text =
        Html.fromHtml(content.content, Html.FROM_HTML_MODE_COMPACT)
    } else {
      binding.tvContent.text = (Html.fromHtml(content.content))
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_page
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

}
