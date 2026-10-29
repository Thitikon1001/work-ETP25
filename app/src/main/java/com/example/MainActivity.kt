package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.wearth.ClothingCameraScanner
import com.example.wearth.ClothingLevel
import com.example.wearth.Customer
import com.example.wearth.CustomerBottomNavBar
import com.example.wearth.CustomerSwitcherDialog
import com.example.wearth.CustomerTab
import com.example.wearth.DepositScreen
import com.example.wearth.HomeScreen
import com.example.wearth.ItemDetailSheet
import com.example.wearth.LocationPickerDialog
import com.example.wearth.ProfileScreen
import com.example.wearth.PrototypeTopBanner
import com.example.wearth.QrDialog
import com.example.wearth.RatesDialog
import com.example.wearth.RedeemSuccessDialog
import com.example.wearth.ShopScreen
import com.example.wearth.StaffAppraisalScreen
import com.example.wearth.StaffBottomNavBar
import com.example.wearth.StaffConfirmRedeemScreen
import com.example.wearth.StaffDashboardScreen
import com.example.wearth.StaffInventoryScreen
import com.example.wearth.StaffPickupDeliveryScreen
import com.example.wearth.StaffTab
import com.example.wearth.TierDialog
import com.example.wearth.UserMode
import com.example.wearth.WalletScreen
import com.example.wearth.WearthViewModel
import com.example.wearth.WelcomeScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        WearthApp()
      }
    }
  }
}

@Composable
fun WearthApp(viewModel: WearthViewModel = viewModel()) {
  var showWelcomeScreen by remember { mutableStateOf(false) }

  val userMode by viewModel.userMode.collectAsState()
  val customerTab by viewModel.customerTab.collectAsState()
  val staffTab by viewModel.staffTab.collectAsState()

  val customers by viewModel.repository.customers.collectAsState()
  val currentCustomer by viewModel.currentCustomer.collectAsState()
  val allItems by viewModel.repository.clothingItems.collectAsState()
  val filteredItems by viewModel.filteredItems.collectAsState()
  val orders by viewModel.repository.redeemOrders.collectAsState()
  val depositRequests by viewModel.repository.depositRequests.collectAsState()
  val transactionsMap by viewModel.repository.historyTransactions.collectAsState()

  val pinnedAddress by viewModel.pinnedAddress.collectAsState()
  val showLocationPicker by viewModel.showLocationPicker.collectAsState()
  val showCameraScanner by viewModel.showCameraScanner.collectAsState()
  val activeAppraisalResult by viewModel.activeAppraisalResult.collectAsState()

  val selectedItem by viewModel.selectedItem.collectAsState()
  val lastRedeemOrder by viewModel.lastRedeemOrder.collectAsState()
  val showRatesModal by viewModel.showRatesModal.collectAsState()
  val showTierModal by viewModel.showTierModal.collectAsState()
  val showQrModal by viewModel.showQrModal.collectAsState()
  val showCustomerSwitcher by viewModel.showCustomerSwitcher.collectAsState()

  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedLevelFilter by viewModel.selectedLevelFilter.collectAsState()

  val evaluatedToday by viewModel.repository.evaluatedCountToday.collectAsState()
  val pointsDistributedToday by viewModel.repository.pointsDistributedToday.collectAsState()
  val cashReceivedToday by viewModel.repository.cashReceivedToday.collectAsState()

  val customerTransactions = transactionsMap[currentCustomer.id] ?: emptyList()

  // Back handling
  BackHandler(enabled = showCameraScanner || userMode == UserMode.STAFF || customerTab != CustomerTab.HOME || selectedItem != null || showWelcomeScreen) {
    when {
      showCameraScanner -> viewModel.openCameraScanner(false)
      showWelcomeScreen -> showWelcomeScreen = false
      selectedItem != null -> viewModel.selectItem(null)
      customerTab != CustomerTab.HOME -> viewModel.setCustomerTab(CustomerTab.HOME)
      userMode == UserMode.STAFF -> viewModel.setUserMode(UserMode.CUSTOMER)
    }
  }

  if (showWelcomeScreen) {
    WelcomeScreen(
      onStartClicked = { showWelcomeScreen = false }
    )
    return
  }

  Scaffold(
    topBar = {
      Column(modifier = Modifier.statusBarsPadding()) {
        PrototypeTopBanner(
          userMode = userMode,
          currentCustomer = currentCustomer,
          onSwitchMode = { newMode -> viewModel.setUserMode(newMode) },
          onOpenCustomerSwitcher = { viewModel.openCustomerSwitcher(true) }
        )
      }
    },
    bottomBar = {
      if (userMode == UserMode.CUSTOMER) {
        CustomerBottomNavBar(
          activeTab = customerTab,
          onTabSelected = { tab -> viewModel.setCustomerTab(tab) }
        )
      } else {
        StaffBottomNavBar(
          activeTab = staffTab,
          onTabSelected = { tab -> viewModel.setStaffTab(tab) }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (userMode == UserMode.CUSTOMER) {
        when (customerTab) {
          CustomerTab.HOME -> {
            HomeScreen(
              customer = currentCustomer,
              bestSellerItems = allItems.filter { it.isRecommended }.take(5),
              onNavigateToTab = { tab -> viewModel.setCustomerTab(tab) },
              onSelectItem = { item -> viewModel.selectItem(item) },
              onOpenCameraScanner = { viewModel.openCameraScanner(true) },
              onOpenRates = { viewModel.openRatesModal(true) },
              onOpenTier = { viewModel.openTierModal(true) }
            )
          }

          CustomerTab.SHOP -> {
            ShopScreen(
              items = filteredItems,
              searchQuery = searchQuery,
              selectedCategory = selectedCategory,
              selectedLevelFilter = selectedLevelFilter,
              onSearchChange = { viewModel.setSearchQuery(it) },
              onCategorySelect = { viewModel.setSelectedCategory(it) },
              onLevelFilterSelect = { viewModel.setSelectedLevelFilter(it) },
              onSelectItem = { item -> viewModel.selectItem(item) }
            )
          }

          CustomerTab.DEPOSIT -> {
            DepositScreen(
              customer = currentCustomer,
              pinnedAddress = pinnedAddress,
              existingRequests = depositRequests,
              activeAppraisal = activeAppraisalResult,
              onOpenCameraScanner = { viewModel.openCameraScanner(true) },
              onClearActiveAppraisal = { viewModel.clearActiveAppraisal() },
              onOpenRates = { viewModel.openRatesModal(true) },
              onOpenLocationPicker = { viewModel.openLocationPicker(true) },
              onSubmitDeposit = { req -> viewModel.repository.submitDepositRequest(req) }
            )
          }

          CustomerTab.WALLET -> {
            WalletScreen(
              customer = currentCustomer,
              transactions = customerTransactions,
              onOpenTierModal = { viewModel.openTierModal(true) },
              onOpenQrModal = { viewModel.openQrModal(true) }
            )
          }

          CustomerTab.PROFILE -> {
            ProfileScreen(
              customer = currentCustomer,
              onOpenCustomerSwitcher = { viewModel.openCustomerSwitcher(true) },
              onOpenLocationPicker = { viewModel.openLocationPicker(true) },
              onOpenTierModal = { viewModel.openTierModal(true) },
              onNavigateToTab = { tab -> viewModel.setCustomerTab(tab) },
              onSwitchToStaff = { viewModel.setUserMode(UserMode.STAFF) }
            )
          }
        }
      } else {
        // Staff Mode Screens
        when (staffTab) {
          StaffTab.APPRAISAL -> {
            StaffAppraisalScreen(
              customers = customers,
              onCompleteAppraisal = { customer, name, cat, brand, lvl, cond, size, p, sellPrice, isPoints ->
                viewModel.repository.completeAppraisal(
                  customer = customer,
                  clothingName = name,
                  category = cat,
                  brandName = brand,
                  level = lvl,
                  condition = cond,
                  size = size,
                  appraisalPrice = p,
                  sellingPrice = sellPrice,
                  chosenPayoutIsPoints = isPoints
                )
              }
            )
          }

          StaffTab.CONFIRM_REDEEM -> {
            StaffConfirmRedeemScreen(
              orders = orders,
              onConfirmPickup = { code -> viewModel.repository.confirmPickup(code) }
            )
          }

          StaffTab.PICKUP_DELIVERY -> {
            StaffPickupDeliveryScreen(
              depositRequests = depositRequests,
              orders = orders,
              onUpdatePickupStatus = { id, status ->
                viewModel.repository.updatePickupRequestStatus(id, status)
              },
              onUpdateDeliveryStatus = { id, status ->
                viewModel.repository.updateDeliveryOrderStatus(id, status)
              },
              onNavigateToAppraisalWithCustomer = { phone ->
                viewModel.setStaffTab(StaffTab.APPRAISAL)
              }
            )
          }

          StaffTab.INVENTORY -> {
            StaffInventoryScreen(items = allItems)
          }

          StaffTab.DASHBOARD -> {
            StaffDashboardScreen(
              evaluatedToday = evaluatedToday,
              pointsDistributed = pointsDistributedToday,
              cashReceived = cashReceivedToday,
              totalStock = allItems.size
            )
          }
        }
      }

      // Modals & Sheets
      selectedItem?.let { item ->
        ItemDetailSheet(
          item = item,
          customer = currentCustomer,
          pinnedAddress = pinnedAddress,
          onOpenLocationPicker = { viewModel.openLocationPicker(true) },
          onDismiss = { viewModel.selectItem(null) },
          onConfirmRedeem = { calc, deliveryType, address, shippingFee ->
            viewModel.redeemSelectedItem(calc, deliveryType, address, shippingFee)
          }
        )
      }

      lastRedeemOrder?.let { order ->
        RedeemSuccessDialog(
          order = order,
          onDismiss = { viewModel.dismissRedeemDialog() },
          onSwitchToStaffToConfirm = {
            viewModel.dismissRedeemDialog()
            viewModel.setUserMode(UserMode.STAFF)
            viewModel.setStaffTab(
              if (order.deliveryType == com.example.wearth.DeliveryType.HOME_DELIVERY)
                StaffTab.PICKUP_DELIVERY
              else
                StaffTab.CONFIRM_REDEEM
            )
          }
        )
      }

      if (showLocationPicker) {
        LocationPickerDialog(
          initialAddress = pinnedAddress,
          onAddressConfirmed = { addr -> viewModel.setPinnedAddress(addr) },
          onDismiss = { viewModel.openLocationPicker(false) }
        )
      }

      if (showRatesModal) {
        RatesDialog(onDismiss = { viewModel.openRatesModal(false) })
      }

      if (showTierModal) {
        TierDialog(onDismiss = { viewModel.openTierModal(false) })
      }

      if (showQrModal) {
        QrDialog(
          customer = currentCustomer,
          onDismiss = { viewModel.openQrModal(false) }
        )
      }

      if (showCustomerSwitcher) {
        CustomerSwitcherDialog(
          customers = customers,
          currentCustomerId = currentCustomer.id,
          onSelect = { id -> viewModel.switchCustomer(id) },
          onDismiss = { viewModel.openCustomerSwitcher(false) }
        )
      }

      if (showCameraScanner) {
        ClothingCameraScanner(
          onDismiss = { viewModel.openCameraScanner(false) },
          onApplyAppraisalToDeposit = { result ->
            viewModel.applyAppraisalToDeposit(result)
          }
        )
      }
    }
  }
}
