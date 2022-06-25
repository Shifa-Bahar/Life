package com.lifepharmacy.application.ui.cart.fragmets

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker.OnDateChangedListener
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.checkout.android_sdk.CheckoutAPIClient
import com.checkout.android_sdk.CheckoutAPIClient.OnTokenGenerated
import com.checkout.android_sdk.Request.CardTokenisationRequest
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Utils.CardUtils
import com.checkout.android_sdk.Utils.Environment
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetCardDeatilsBinding
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.CreditCardFormatTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Field


/**
 * Created by Shifa
 */
@AndroidEntryPoint
class CardDetailsBottomSheet : BaseBottomUpSheet<BottomSheetCardDeatilsBinding>(),
  ClickPreviewPaymentFragment, ClickAnimationComboActionDialog, ClickTool {
  private val viewModel: CartViewModel by activityViewModels()
  private val addressViewModel: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  private var mCheckoutAPIClient // include the module
      : CheckoutAPIClient? = null

  //  private lateinit var cardAdapter: CardsAdapter
  private val tv: CreditCardFormatTextWatcher? = null

  private var isTopUp = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()
  }

  companion object {
    var inView = false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    inView = true
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    inView = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    inView = false
  }


  private fun initLayout() {
//    viewModel.selectedPaymentType.value = PaymentType.NEW
    binding.click = this
    binding.viewModel = viewModel
    binding.lyNewCardPayment.isCard = true
    binding.lyNewCardPayment.isCvv = true
    binding.lyNewCardPayment.isExpiry = true
//    binding.addressViewModel = addressViewModel
    binding.lifecycleOwner = this
    bindCardNumber()
    bindCardyear()
    getcardType()
    bindCvv()
    bindName()
    bindPayment()
//    formValidationOutcome()
//    datedropdown()
//    binding.lyNewCardPayment.edExpiry.setOnClickListener(View.OnClickListener {
//      binding.lyNewCardPayment.clmain.visibility = View.VISIBLE
//      binding.lyNewCardPayment.llSavecard.visibility = View.GONE
//      binding.lyNewCardPayment.llIcons.visibility = View.GONE
//    }
//    )
//  datepicker()

//    initMonthYearPicker()
  }

  private fun sanitizeEntry(entry: String): String {
    return entry.replace("\\D".toRegex(), "")
  }

  fun bindName() {
    binding.lyNewCardPayment.edName.addTextChangedListener(object : TextWatcher {
      var len = 0
      override fun afterTextChanged(s: Editable) {
        val str: String = binding.lyNewCardPayment.edName.text.toString()
        if (len < str.length) { //len check for backspace
          viewModel.cardName = str
        }
      }

      override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        val str: String = binding.lyNewCardPayment.edName.text.toString()
        len = str.length

      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

      }
    })
  }


  fun getcardType() {
    var listOfPattern: ArrayList<String?> = ArrayList()

    var ptVisa: String = "^4[0-9]{6,}$";
    listOfPattern.add(ptVisa);
    var ptMasterCard: String = "^5[1-5][0-9]{5,}$";
    listOfPattern.add(ptMasterCard);
    var ptAmeExp: String = "^3[47][0-9]{5,}$";
    listOfPattern.add(ptAmeExp);
    var ptDinClb: String = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
    listOfPattern.add(ptDinClb);
    var ptDiscover: String = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
    listOfPattern.add(ptDiscover);
    var ptJcb: String = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
    listOfPattern.add(ptJcb);
  }

  fun initMonthYearPicker() {
//    dp_mes = findViewById(R.id.dp_mes) as DatePicker
    val year: Int = binding.lyNewCardPayment.datePicker.getYear()
    val month: Int = binding.lyNewCardPayment.datePicker.getMonth()
    val day: Int = binding.lyNewCardPayment.datePicker.getDayOfMonth()
    binding.lyNewCardPayment.datePicker.init(year, month, day,
      OnDateChangedListener { view, yeari, monthOfYear, dayOfMonth ->
        var month_i = monthOfYear + 1
        var year_i = yeari
        var date = "$year_i-$month_i"
        updateLabel(date.toString())
        Log.e("selected month:", Integer.toString(month_i))
        Log.e("selected year:", Integer.toString(year_i))
//        binding.lyNewCardPayment.edExpiry.append(month_i.toString())
//        viewModel.cardMonth = month_i.toString()
//        binding.lyNewCardPayment.edExpiry.append("/")
//
//        if (year_i.toString().length == 4) {
//          val substring = year_i.toString().subSequence(2, 4)
//          Log.e(TAG, "initMonthYearPicker: $substring")
//          binding.lyNewCardPayment.edExpiry.append(substring)
//        }

      })

    binding.lyNewCardPayment.datePicker.minDate = System.currentTimeMillis() - 100000

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android")
      if (daySpinnerId != 0) {
        val daySpinner: View = binding.lyNewCardPayment.datePicker.findViewById(daySpinnerId)
        if (daySpinner != null) {
          daySpinner.visibility = View.GONE
        }
      }
      val monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android")
      if (monthSpinnerId != 0) {
        val monthSpinner: View = binding.lyNewCardPayment.datePicker.findViewById(monthSpinnerId)
        if (monthSpinner != null) {
          monthSpinner.visibility = View.VISIBLE
        }
      }
      val yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android")
      if (yearSpinnerId != 0) {
        val yearSpinner: View = binding.lyNewCardPayment.datePicker.findViewById(yearSpinnerId)
        if (yearSpinner != null) {
          yearSpinner.visibility = View.VISIBLE
        }
      }
    } else { //Older SDK versions
      val f: Array<Field> = binding.lyNewCardPayment.datePicker.javaClass.declaredFields
      for (field in f) {
        if (field.name.equals("mDayPicker") || field.name.equals("mDaySpinner")) {
          field.isAccessible = true
          var dayPicker: Any? = null
          try {
            dayPicker = field.get(binding.lyNewCardPayment.datePicker)
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }
          ((dayPicker as View?) ?: return).visibility = View.GONE
        }
        if (field.name.equals("mMonthPicker") || field.name.equals("mMonthSpinner")) {
          field.isAccessible = true
          var monthPicker: Any? = null
          try {
            monthPicker = field.get(binding.lyNewCardPayment.datePicker)
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }
          ((monthPicker as View?) ?: return).visibility = View.VISIBLE
        }
        if (field.name.equals("mYearPicker") || field.name.equals("mYearSpinner")) {
          field.isAccessible = true
          var yearPicker: Any? = null
          try {
            yearPicker = field.get(binding.lyNewCardPayment.datePicker)
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }
          ((yearPicker as View?) ?: return).visibility = View.VISIBLE
        }
      }
    }
  }

  fun checkoutPayment() {
    val mTokenListener: OnTokenGenerated = object : OnTokenGenerated {
      override fun onTokenGenerated(token: CardTokenisationResponse) {
//        Toast.makeText(requireContext(), "token generated ${token.token}", Toast.LENGTH_SHORT)
//          .show()
        viewModel.token = token.token
        Log.e(TAG, "onTokenGenerated: ${token.token}")
        try {
          findNavController().navigate(R.id.orderSummaryBottomSheet)
//      findNavController().navigate(R.id.paymentFragment)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }

      override fun onError(error: CardTokenisationFail) {
        Toast.makeText(requireContext(), "token generated failed", Toast.LENGTH_SHORT)
          .show()
      }

      override fun onNetworkError(error: VolleyError?) {
        Toast.makeText(requireContext(), "token network error", Toast.LENGTH_SHORT)
          .show()
      }

    }
//    "pk_test_87af231e-03bb-48ff-8af8-e80ed2be723f",/// "pk_test_87af231e-03bb-48ff-8af8-e80ed2be723f" your public key
//    Environment.SANDBOX // the environment
    Log.e(TAG, "checkoutPayment: ${appManager.persistenceManager.ckopk}")
//    appManager.persistenceManager.ckopk,/// "pk_test_87af231e-03bb-48ff-8af8-e80ed2be723f" your public key
//    Environment.LIVE
    mCheckoutAPIClient = CheckoutAPIClient(
      requireContext(),  // context
      appManager.persistenceManager.ckopk,/// "pk_test_87af231e-03bb-48ff-8af8-e80ed2be723f" your public key.
      Environment.LIVE
    )
    mCheckoutAPIClient!!.setTokenListener(mTokenListener) // pass the callback.

    // Pass the payload and generate the token
    // Pass the payload and generate the token
    var cardnum = viewModel.cardNumber.replace(" ", "")
    mCheckoutAPIClient!!.generateToken(
      CardTokenisationRequest(
        cardnum,
        viewModel.cardName,
        viewModel.cardMonth,
        viewModel.cardYear,
        viewModel.cardCvv
      )
    )
  }


//  fun datedropdown() {
//    val today = Calendar.getInstance()
//    binding.lyNewCardPayment.datePicker.init(
//      today.get(Calendar.YEAR), today.get(Calendar.MONTH),
//      today.get(Calendar.DAY_OF_MONTH)
//    ) { view, year, month, day ->
//      val month = month + 1
//      val msg = "You Selected: $day/$month/$year"
//      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
//        .show()
//      Log.e(TAG, "datedropdown: $msg")
//    }
////binding.lyNewCardPayment.datePicker.tv_day.visibility
////    val textview = binding.lyNewCardPayment.datePicker
////    Resources.getSystem().getIdentifier(
////      "day",
////      "id", "android"
////    )
////    textview.visibility = View.GONE
////
////
////    var day =  binding.lyNewCardPayment.datePicker.resources.getIdentifier("day", "id", "android")
//
//  }

//  fun datepicker() {
//    val myCalendar = Calendar.getInstance()
//    val date =
//      OnDateSetListener { view, year, month, day ->
//        myCalendar.set(Calendar.YEAR, year)
//        myCalendar.set(Calendar.MONTH, month)
//        myCalendar.set(Calendar.DAY_OF_MONTH, day)
//        updateLabel()
//      }
//    binding.lyNewCardPayment.edExpiry.setOnClickListener(View.OnClickListener {
//      DatePickerDialog(
//        requireContext(), date, myCalendar.get(
//          Calendar.YEAR
//        ), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
//      ).show()
//    })
//
//  }


//  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//    val myCalendar = Calendar.getInstance()
//    val dialog = DatePickerDialog(
//      requireContext()
//    )
//    val datePicker: DatePicker = dialog.datePicker
//    // Limit range.
//    val c: Calendar = Calendar.getInstance()
//    c[Calendar.YEAR] = Calendar.YEAR
//    if(System.currentTimeMillis() > myCalendar.getTimeInMillis()){
//      datePicker.setMinDate(c.timeInMillis)
//      datePicker.setMaxDate(c.timeInMillis)
////      dialog.getDatePicker().setCalendarViewShown(false);
////      dialog.show();
//    }else{
//      Log.e(TAG, "onCreateDialog: ")
//      //Error message depending on the requirements.
//
//    }
////    datePicker.setMinDate(c.timeInMillis)
//
////    datePicker.setMaxDate(c.timeInMillis)
//    // Remove day.
////    val daySpinnerId: Int = Resources.getSystem().getIdentifier("day", "id", "android")
////    val daySpinner: View = dialog.getDatePicker().findViewById(daySpinnerId)
////    daySpinner.visibility = View.GONE
//    return dialog
//  }

  fun updateLabel(yearmonth: String) {
    Log.e(TAG, "updateLabel:month $yearmonth")
    var formatyearrmonth = DateTimeUtil.getFormatMonthYearString(yearmonth).toString()
    Log.e(TAG, "updateLabel: date$formatyearrmonth")
//    binding.lyNewCardPayment.edExpiry.text = formatyearrmonth
  }

  //  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//    val dialog = DatePickerDialog(
//      activity!!,
//      R.style.CustomDatePickerDialogTheme, this, mParent.mYear, mParent.mMonth, 1
//    )
////    val datePicker: DatePicker = dialog.datePicker
////    // Limit range.
//    val c: Calendar = mParent.getCalendar()
////    datePicker.setMinDate(c.timeInMillis)
////    c[Calendar.YEAR] = mParent.mYear + CC_EXP_YEARS_COUNT - 1
////    datePicker.setMaxDate(c.timeInMillis)
////    // Remove day.
////    datePicker.findViewById(resources.getIdentifier("day", "id", "android"))
////      .setVisibility(View.GONE)
//    return dialog
//  }
  private fun bindPayment() {
    binding.lyNewCardPayment.profileViewModel = profileViewModel
    binding.lyNewCardPayment.mViewModel = viewModel
    binding.lyNewCardPayment.topViewModel = topViewModel
    binding.lyNewCardPayment.lifecycleOwner = this
    binding.clicktop = this
    binding.tvToolbarTitle.text = "ADD NEW CARD"
    binding.lyNewCardPayment.edNote.text =
      "we will surely store this card for faster payment expirence. Your cvv number will not be stored."
//if radio btn new is selected no need to call the default methods again as screen is navigating in new card handling selected payment type
    if (!viewModel.selectedtypenew) {
      viewModel.bindDefaultPaymentMethod()
      viewModel.setLastPaymentType()
    }
    binding.lyNewCardPayment.swSaveCard.setOnCheckedChangeListener { _, b ->
      Log.e(TAG, "bindPayment: $b")
      viewModel.isSavecard = b
    }


//    binding.lyNewCardPayment.note = viewModel.note
    viewModel.isSavecard = binding.lyNewCardPayment.swSaveCard.isChecked //need to check
  }

  private fun bindCardNumber() {
//    var listOfPattern: ArrayList<String?> = ArrayList()
//
//    var ptVisa = "^4[0-9]{6,}$"
//    listOfPattern.add(ptVisa);
//    var ptMasterCard: String = "^5[1-5][0-9]{5,}$";
//    listOfPattern.add(ptMasterCard);
//    var ptAmeExp: String = "^3[47][0-9]{5,}$";
//    listOfPattern.add(ptAmeExp);
//    var ptDinClb: String = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
//    listOfPattern.add(ptDinClb);
//    var ptDiscover: String = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
//    listOfPattern.add(ptDiscover);
//    var ptJcb: String = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
//    listOfPattern.add(ptJcb);

    binding.lyNewCardPayment.tvCard.addTextChangedListener(object : TextWatcher {
      var len = 0
      override fun afterTextChanged(s: Editable) {
        val str: String = binding.lyNewCardPayment.tvCard.getText().toString()
        if (str.length == 4 && len < str.length) { //len check for backspace
          binding.lyNewCardPayment.tvCard.append(" ")
        }
        if (str.length == 9 && len < str.length) { //len check for backspace
          binding.lyNewCardPayment.tvCard.append(" ")
        }
        if (str.length == 14 && len < str.length) { //len check for backspace
          binding.lyNewCardPayment.tvCard.append(" ")
        }
        Log.e(AnimationDialog.TAG, "afterTextChanged: $str")
        viewModel.cardNumber = str

        val sanitized = sanitizeEntry(s.toString())
        val cardType = CardUtils.getType(sanitized)

        when (cardType.name.toString().toLowerCase()) {
          "visa" -> activity?.let {
            Glide.with(it).load(R.drawable.ic_visa).into(binding.lyNewCardPayment.imgCard)
          }
          "mastercard" -> activity?.let {
            Glide.with(it).load(R.drawable.ic_master_card).into(binding.lyNewCardPayment.imgCard)
          }
          "americanexpress" -> activity?.let {
            Glide.with(it).load(R.drawable.ic_card).into(binding.lyNewCardPayment.imgCard)
          }
          else -> activity?.let {
            Glide.with(it).load(R.drawable.ic_card).into(binding.lyNewCardPayment.imgCard)
          }
        }

//    var cardType = CardUtils.getType(viewModel.cardNumber)
        Log.e(TAG, "initLayout: ${cardType.name}")
//        val ccNum = s.toString()
//        for (p in listOfPattern) {
//          if (p?.toRegex()?.let { ccNum.matches(it) } == true) {
//            Log.d("DEBUG", "afterTextChanged : discover")
//            break
//          }
//        }
//        binding.lyNewCardPayment.tvCard = viewModel.note
//        Log.e(TAG, "afterTextChangedss: ${viewModel.note}")
      }

      override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        val str: String = binding.lyNewCardPayment.tvCard.getText().toString()
        len = str.length
        Log.e(AnimationDialog.TAG, "beforeTextChanged: $str")

      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//        binding.lyNewCardPayment.note = viewModel.note
//        Log.e(TAG, "afterTextChanged: ${viewModel.note}")
      }
    })
  }


  fun formValidationOutcome(): Boolean {
    var outcome = true
//    binding.lyNewCardPayment.isCard = true
    Log.e(TAG, "formValidationOutcome:num ${viewModel.cardNumber}")
    Log.e(TAG, "formValidationOutcome: month${viewModel.cardMonth}")
    Log.e(TAG, "formValidationOutcome: comyear${viewModel.completeYear}")
    Log.e(TAG, "formValidationOutcome: cvv${viewModel.cardCvv}")
    if (!CardUtils.isValidCard(viewModel.cardNumber.toString())) {
//      binding.lyNewCardPayment.tvCard.background.setColorFilter(
//        Color.RED,
//        PorterDuff.Mode.SRC_ATOP
//      )
      Log.e(TAG, "formValidationOutcome: card-false")
      outcome = false
      binding.lyNewCardPayment.isCard = false
    } else {
      Log.e(TAG, "formValidationOutcome: card-true")
//      binding.lyNewCardPayment.tvCard.background.setColorFilter(
//        Color.GREEN,
//        PorterDuff.Mode.SRC_ATOP
//      )
      binding.lyNewCardPayment.isCard = true
    }
    if (!CardUtils.isValidDate(viewModel.cardMonth, viewModel.completeYear)) {
//      binding.lyNewCardPayment.edExpiry.background.setColorFilter(
//        Color.RED,
//        PorterDuff.Mode.SRC_ATOP
//      )
      Log.e(TAG, "formValidationOutcome: expiry-false")
      binding.lyNewCardPayment.isExpiry = false
      outcome = false
    } else {
//      binding.lyNewCardPayment.edExpiry.background.setColorFilter(
//        Color.GREEN,
//        PorterDuff.Mode.SRC_ATOP
//      )
      Log.e(TAG, "formValidationOutcome: expiry-true")
      binding.lyNewCardPayment.isExpiry = true
    }
    if (!CardUtils.isValidCvv(
        viewModel.cardCvv,
        CardUtils.getType(binding.lyNewCardPayment.tvCard.text.toString())
      )
    ) {
      Log.e(TAG, "formValidationOutcome: cvv-false")
//      binding.lyNewCardPayment.edCvv.background.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
      outcome = false
      binding.lyNewCardPayment.isCvv = false
    } else {
//      binding.lyNewCardPayment.edCvv.background.setColorFilter(
//        Color.GREEN,
//        PorterDuff.Mode.SRC_ATOP
//      )
      Log.e(TAG, "formValidationOutcome: cvv-true")
      binding.lyNewCardPayment.isCvv = true
    }
    Log.e(TAG, "formValidationOutcome: $outcome")
    return outcome
  }
//
//  private fun bindCardyear() {
//
//    binding.lyNewCardPayment.edExpiry.addTextChangedListener(object : TextWatcher {
//      var len = 0
//      override fun afterTextChanged(s: Editable) {
////        val str: String = binding.lyNewCardPayment.datePicker.month.toString()
//        val str: String = binding.lyNewCardPayment.edExpiry.text.toString()
//        if (str.length >= 2 && len < str.length) { //len check for backspace
//          viewModel.cardMonth = str
//          Log.e(TAG, "afterTextChanged:str $str")
//          binding.lyNewCardPayment.edExpiry.append("/")
//        }
//        if (str.length == 5) {
//          val substring = str.subSequence(3, 5)
//          viewModel.cardYear = substring.toString()
//          Log.e(TAG, "afterTextChanged: $substring")
//        }
//      }
//
//      override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
//        val str: String = binding.lyNewCardPayment.edExpiry.getText().toString()
//        len = str.length
//        Log.e(AnimationDialog.TAG, "beforeTextChanged:edExpiry $str")
//      }
//
//      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//      }
//    })
//  }

  private fun bindCardyear() {

    binding.lyNewCardPayment.edExpiry.addTextChangedListener(object : TextWatcher {
      var len = 0
      override fun afterTextChanged(s: Editable) {
        val str: String = binding.lyNewCardPayment.edExpiry.text.toString()
        if (str.length == 2 && len < str.length) { //len check for backspace
          viewModel.cardMonth = str
          binding.lyNewCardPayment.edExpiry.append("/")
        }


        if (str.length == 5) {
          val substring = str.subSequence(3, 5)
          viewModel.cardYear = substring.toString()
          viewModel.completeYear = "20${viewModel.cardYear}"
          Log.e(TAG, "afterTextChanged: $substring")
        }
      }


      override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        val str: String = binding.lyNewCardPayment.edExpiry.getText().toString()
        len = str.length
        Log.e(AnimationDialog.TAG, "beforeTextChanged:edExpiry $str")
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

      }
    })
  }


  private fun bindCvv() {

    binding.lyNewCardPayment.edCvv.addTextChangedListener(object : TextWatcher {
      var len = 0
      override fun afterTextChanged(s: Editable) {
        val str: String = binding.lyNewCardPayment.edCvv.getText().toString()
        Log.e(AnimationDialog.TAG, "afterTextChanged:edCvv $str")
        viewModel.cardCvv = str
      }

      override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        val str: String = binding.lyNewCardPayment.edCvv.getText().toString()
        len = str.length
        Log.e(AnimationDialog.TAG, "beforeTextChanged:edCvv $str")
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

      }
    })
  }

  private fun observers() {

  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_card_deatils
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_READ_SMS) {
    }
  }

  override fun onClickProceed() {
    checkoutPayment()
    inView = false
    findNavController().navigateUp()
  }


  override fun onClickSelectContinue() {
    viewModel.paymentview = true
    Log.e(TAG, "onClickSelectContinue: ${formValidationOutcome()}")
    if (formValidationOutcome()) {
      checkoutPayment()
    }
    inView = false

//    findNavController().navigateUp()
//    try {
//      findNavController().navigate(R.id.orderSummaryBottomSheet)
////      findNavController().navigate(R.id.paymentFragment)
//    } catch (e: Exception) {
//      e.printStackTrace()
//    }
  }

  override fun onClickBack() {
    viewModel.paymentview = true
    findNavController().navigateUp()
  }

  override fun onClickLater() {
    Log.e(TAG, "onClickLater: ")
    binding.lyNewCardPayment.clmain.visibility = View.GONE
    binding.lyNewCardPayment.llSavecard.visibility = View.VISIBLE
    binding.lyNewCardPayment.llIcons.visibility = View.VISIBLE
  }

  override fun onClickClaim() {

  }

}


