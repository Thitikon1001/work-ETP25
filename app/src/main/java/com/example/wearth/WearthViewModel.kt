package com.example.wearth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class WearthViewModel(
  val repository: WearthRepository = WearthRepository()
) : ViewModel() {

  // Current Mode: Customer vs Staff
  private val _userMode = MutableStateFlow(UserMode.CUSTOMER)
  val userMode: StateFlow<UserMode> = _userMode.asStateFlow()

  // Navigation tabs
  private val _customerTab = MutableStateFlow(CustomerTab.HOME)
  val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

  private val _staffTab = MutableStateFlow(StaffTab.APPRAISAL)
  val staffTab: StateFlow<StaffTab> = _staffTab.asStateFlow()

  // Selected Item Detail
  private val _selectedItem = MutableStateFlow<ClothingItem?>(null)
  val selectedItem: StateFlow<ClothingItem?> = _selectedItem.asStateFlow()

  // Success Redemption Order Dialog
  private val _lastRedeemOrder = MutableStateFlow<RedeemOrder?>(null)
  val lastRedeemOrder: StateFlow<RedeemOrder?> = _lastRedeemOrder.asStateFlow()

  // Modals
  private val _showRatesModal = MutableStateFlow(false)
  val showRatesModal: StateFlow<Boolean> = _showRatesModal.asStateFlow()

  private val _showTierModal = MutableStateFlow(false)
  val showTierModal: StateFlow<Boolean> = _showTierModal.asStateFlow()

  private val _showQrModal = MutableStateFlow(false)
  val showQrModal: StateFlow<Boolean> = _showQrModal.asStateFlow()

  private val _showCustomerSwitcher = MutableStateFlow(false)
  val showCustomerSwitcher: StateFlow<Boolean> = _showCustomerSwitcher.asStateFlow()

  // CameraX Clothing Scanner
  private val _showCameraScanner = MutableStateFlow(false)
  val showCameraScanner: StateFlow<Boolean> = _showCameraScanner.asStateFlow()

  private val _activeAppraisalResult = MutableStateFlow<QuickAppraisalResult?>(null)
  val activeAppraisalResult: StateFlow<QuickAppraisalResult?> = _activeAppraisalResult.asStateFlow()

  // Location Pinning & Address Picker
  private val _showLocationPicker = MutableStateFlow(false)
  val showLocationPicker: StateFlow<Boolean> = _showLocationPicker.asStateFlow()

  private val _pinnedAddress = MutableStateFlow(
    DeliveryAddress(
      id = "addr_default_may",
      label = "บ้าน",
      recipientName = "น้องเมย์",
      phone = "081-234-5678",
      addressText = "บ้านเลขที่ 88/12 ซอยสุขุมวิท 39",
      district = "เขตวัฒนา",
      province = "กรุงเทพฯ",
      postalCode = "10110",
      noteToRider = "กดกริ่งหน้าบ้าน หรือโทรหาก่อนถึง 5 นาที",
      latitude = 13.736717,
      longitude = 100.561234
    )
  )
  val pinnedAddress: StateFlow<DeliveryAddress> = _pinnedAddress.asStateFlow()

  // Tracking dialogs
  private val _trackingOrder = MutableStateFlow<RedeemOrder?>(null)
  val trackingOrder: StateFlow<RedeemOrder?> = _trackingOrder.asStateFlow()

  private val _trackingDeposit = MutableStateFlow<DepositRequest?>(null)
  val trackingDeposit: StateFlow<DepositRequest?> = _trackingDeposit.asStateFlow()

  // Shop filter state
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("ทั้งหมด")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _selectedLevelFilter = MutableStateFlow<ClothingLevel?>(null)
  val selectedLevelFilter: StateFlow<ClothingLevel?> = _selectedLevelFilter.asStateFlow()

  // Current customer flow
  val currentCustomer: StateFlow<Customer> = combine(
    repository.customers,
    repository.currentCustomerId
  ) { customers, id ->
    val found = customers.find { it.id == id } ?: customers.first()
    // Sync pinned address with customer's default address if available
    found.savedAddresses.firstOrNull()?.let { addr ->
      if (_pinnedAddress.value.phone != found.phone) {
        _pinnedAddress.value = addr
      }
    }
    found
  }.stateIn(viewModelScope, SharingStarted.Eagerly, repository.getCurrentCustomer())

  // Filtered clothing items
  val filteredItems: StateFlow<List<ClothingItem>> = combine(
    repository.clothingItems,
    _searchQuery,
    _selectedCategory,
    _selectedLevelFilter
  ) { items, query, category, levelFilter ->
    items.filter { item ->
      val matchesQuery = query.isBlank() ||
        item.name.contains(query, ignoreCase = true) ||
        item.category.contains(query, ignoreCase = true) ||
        (item.brandName?.contains(query, ignoreCase = true) == true)

      val matchesCategory = category == "ทั้งหมด" || item.category == category
      val matchesLevel = levelFilter == null || item.level == levelFilter
      matchesQuery && matchesCategory && matchesLevel
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  fun setUserMode(mode: UserMode) {
    _userMode.value = mode
  }

  fun setCustomerTab(tab: CustomerTab) {
    _customerTab.value = tab
    _selectedItem.value = null
  }

  fun setStaffTab(tab: StaffTab) {
    _staffTab.value = tab
  }

  fun selectItem(item: ClothingItem?) {
    _selectedItem.value = item
  }

  fun openRatesModal(open: Boolean) {
    _showRatesModal.value = open
  }

  fun openTierModal(open: Boolean) {
    _showTierModal.value = open
  }

  fun openQrModal(open: Boolean) {
    _showQrModal.value = open
  }

  fun openCameraScanner(open: Boolean) {
    _showCameraScanner.value = open
  }

  fun applyAppraisalToDeposit(result: QuickAppraisalResult) {
    _activeAppraisalResult.value = result
    _customerTab.value = CustomerTab.DEPOSIT
    _showCameraScanner.value = false
  }

  fun clearActiveAppraisal() {
    _activeAppraisalResult.value = null
  }

  fun openCustomerSwitcher(open: Boolean) {
    _showCustomerSwitcher.value = open
  }

  fun openLocationPicker(open: Boolean) {
    _showLocationPicker.value = open
  }

  fun setPinnedAddress(address: DeliveryAddress) {
    _pinnedAddress.value = address
    _showLocationPicker.value = false
  }

  fun viewOrderTracking(order: RedeemOrder?) {
    _trackingOrder.value = order
  }

  fun viewDepositTracking(deposit: DepositRequest?) {
    _trackingDeposit.value = deposit
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setSelectedLevelFilter(level: ClothingLevel?) {
    _selectedLevelFilter.value = level
  }

  fun switchCustomer(customerId: String) {
    repository.selectCustomer(customerId)
    val customer = repository.customers.value.find { it.id == customerId }
    customer?.savedAddresses?.firstOrNull()?.let { addr ->
      _pinnedAddress.value = addr
    }
    _showCustomerSwitcher.value = false
  }

  fun redeemSelectedItem(
    calc: RedemptionCalculation,
    deliveryType: DeliveryType = DeliveryType.STORE_PICKUP,
    shippingAddress: DeliveryAddress? = null,
    shippingFee: Int = 0
  ) {
    val item = _selectedItem.value ?: return
    val customer = currentCustomer.value
    val order = repository.redeemItem(
      customer = customer,
      item = item,
      calc = calc,
      deliveryType = deliveryType,
      shippingAddress = shippingAddress,
      shippingFee = shippingFee
    )
    _lastRedeemOrder.value = order
    _selectedItem.value = null
  }

  fun dismissRedeemDialog() {
    _lastRedeemOrder.value = null
  }
}
