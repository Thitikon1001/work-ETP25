package com.example.wearth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WearthRepository {

  private val _customers = MutableStateFlow<List<Customer>>(createInitialCustomers())
  val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

  private val _currentCustomerId = MutableStateFlow("c1")
  val currentCustomerId: StateFlow<String> = _currentCustomerId.asStateFlow()

  private val _clothingItems = MutableStateFlow<List<ClothingItem>>(createInitialItems())
  val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

  private val _redeemOrders = MutableStateFlow<List<RedeemOrder>>(createInitialOrders())
  val redeemOrders: StateFlow<List<RedeemOrder>> = _redeemOrders.asStateFlow()

  private val _historyTransactions = MutableStateFlow<Map<String, List<HistoryTransaction>>>(createInitialTransactions())
  val historyTransactions: StateFlow<Map<String, List<HistoryTransaction>>> = _historyTransactions.asStateFlow()

  private val _depositRequests = MutableStateFlow<List<DepositRequest>>(createInitialDepositRequests())
  val depositRequests: StateFlow<List<DepositRequest>> = _depositRequests.asStateFlow()

  // Staff Daily Stats
  private val _evaluatedCountToday = MutableStateFlow(5)
  val evaluatedCountToday: StateFlow<Int> = _evaluatedCountToday.asStateFlow()

  private val _pointsDistributedToday = MutableStateFlow(625)
  val pointsDistributedToday: StateFlow<Int> = _pointsDistributedToday.asStateFlow()

  private val _cashReceivedToday = MutableStateFlow(1420)
  val cashReceivedToday: StateFlow<Int> = _cashReceivedToday.asStateFlow()

  fun selectCustomer(customerId: String) {
    _currentCustomerId.value = customerId
  }

  fun getCurrentCustomer(): Customer {
    return _customers.value.find { it.id == _currentCustomerId.value }
      ?: _customers.value.first()
  }

  fun getCustomerByPhone(phone: String): Customer? {
    val clean = phone.replace("-", "").trim()
    return _customers.value.find { it.phone.replace("-", "").trim() == clean }
  }

  /**
   * ลูกค้าทำการแลกเสื้อ:
   * หักแต้มลูกค้า, บันทึกคำสั่งแลก, อัปเดตสถานะเสื้อเป็น RESERVED (หรือ SOLD เมื่อรับของ)
   * รองรับทั้งรับที่ร้าน และบริการจัดส่งถึงบ้าน (Eco Delivery)
   */
  fun redeemItem(
    customer: Customer,
    item: ClothingItem,
    calc: RedemptionCalculation,
    deliveryType: DeliveryType = DeliveryType.STORE_PICKUP,
    shippingAddress: DeliveryAddress? = null,
    shippingFee: Int = 0
  ): RedeemOrder {
    val orderCode = "#WE-${(1000..9999).random()}"
    val totalCashToPay = calc.cashRequired + shippingFee
    val orderStatus = if (deliveryType == DeliveryType.HOME_DELIVERY) {
      "กำลังจัดเตรียมพัสดุ"
    } else {
      "รอรับของหน้าร้าน"
    }

    val order = RedeemOrder(
      id = "ord_${System.currentTimeMillis()}",
      orderCode = orderCode,
      customerId = customer.id,
      customerName = customer.name,
      customerPhone = customer.phone,
      itemId = item.id,
      itemName = item.name,
      itemPrice = item.price,
      pointsUsed = calc.pointsUsed,
      cashPaid = totalCashToPay,
      dateTime = "วันนี้",
      status = orderStatus,
      deliveryType = deliveryType,
      shippingAddress = shippingAddress,
      shippingFee = shippingFee,
      trackingCode = if (deliveryType == DeliveryType.HOME_DELIVERY) "TH-WE-${(10000..99999).random()}" else null
    )

    // Deduct points from customer (FIFO simulation)
    _customers.update { list ->
      list.map { c ->
        if (c.id == customer.id) {
          c.copy(
            points = calc.remainingCustomerPoints,
            totalRedeemedTimes = c.totalRedeemedTimes + 1
          )
        } else c
      }
    }

    // Add transaction history
    val deliveryNote = if (deliveryType == DeliveryType.HOME_DELIVERY) {
      "ส่งถึงที่ (${shippingAddress?.label ?: "บ้าน"}) • ค่าส่ง ฿$shippingFee"
    } else {
      "รับเองที่หน้าร้าน"
    }

    val newTx = HistoryTransaction(
      id = "tx_${System.currentTimeMillis()}",
      title = "ใช้แต้มแลก${item.name}",
      date = "วันนี้",
      pointsDelta = -calc.pointsUsed,
      cashPaid = totalCashToPay,
      note = "$orderCode • $deliveryNote"
    )
    _historyTransactions.update { map ->
      val currentList = map[customer.id] ?: emptyList()
      map + (customer.id to (listOf(newTx) + currentList))
    }

    // Update item status
    _clothingItems.update { items ->
      items.map {
        if (it.id == item.id) it.copy(status = ItemStatus.RESERVED) else it
      }
    }

    // Save order
    _redeemOrders.update { listOf(order) + it }

    return order
  }

  /**
   * พนักงานอัปเดตสถานะการจัดส่งพัสดุถึงบ้าน
   */
  fun updateDeliveryOrderStatus(orderId: String, newStatus: String) {
    _redeemOrders.update { list ->
      list.map {
        if (it.id == orderId) it.copy(status = newStatus) else it
      }
    }
    if (newStatus == "จัดส่งสำเร็จ") {
      val order = _redeemOrders.value.find { it.id == orderId }
      order?.let { ord ->
        _clothingItems.update { items ->
          items.map { if (it.id == ord.itemId) it.copy(status = ItemStatus.SOLD) else it }
        }
        _cashReceivedToday.update { it + ord.cashPaid }
      }
    }
  }

  /**
   * พนักงานอัปเดตสถานะงานเรียกรถไปรับเสื้อถึงบ้าน
   */
  fun updatePickupRequestStatus(requestId: String, newStatus: String) {
    _depositRequests.update { list ->
      list.map {
        if (it.id == requestId) it.copy(status = newStatus) else it
      }
    }
  }

  /**
   * พนักงานยืนยันการรับของจากรหัส orderCode
   */
  fun confirmPickup(orderCode: String): Boolean {
    val order = _redeemOrders.value.find { it.orderCode.trim().equals(orderCode.trim(), ignoreCase = true) }
      ?: return false

    if (order.status == "รับของสำเร็จ" || order.status == "จัดส่งสำเร็จ") return true

    val updatedStatus = if (order.deliveryType == DeliveryType.HOME_DELIVERY) "จัดส่งสำเร็จ" else "รับของสำเร็จ"

    _redeemOrders.update { list ->
      list.map {
        if (it.id == order.id) it.copy(status = updatedStatus) else it
      }
    }

    _clothingItems.update { items ->
      items.map {
        if (it.id == order.itemId) it.copy(status = ItemStatus.SOLD) else it
      }
    }

    // Add cash received to staff daily stats
    _cashReceivedToday.update { it + order.cashPaid }

    return true
  }

  /**
   * พนักงานประเมินเสื้อ และบันทึกผล
   * - ผู้ฝากเลือกรับแต้ม หรือ รับเงินสด
   * - เสื้อตัวนี้จะถูกเพิ่มเข้าสต็อกร้านด้วยราคาขายที่กำหนด
   */
  fun completeAppraisal(
    customer: Customer,
    clothingName: String,
    category: String,
    brandName: String?,
    level: ClothingLevel,
    condition: ClothingCondition,
    size: String,
    appraisalPrice: Int,
    sellingPrice: Int,
    chosenPayoutIsPoints: Boolean
  ): ClothingItem {
    val pointsAwarded = PointsCalculator.calculatePointsOffer(appraisalPrice)

    val newItem = ClothingItem(
      id = "item_${System.currentTimeMillis()}",
      name = clothingName,
      category = category,
      brandName = brandName,
      level = level,
      condition = condition,
      size = size,
      price = sellingPrice,
      appraisalPrice = appraisalPrice,
      status = ItemStatus.AVAILABLE,
      imageDrawableName = if (level == ClothingLevel.BRAND) "prod_denim_jacket" else "prod_vintage_tee",
      isRecommended = true,
      description = "สภาพ$condition ผ่านการคัดและฆ่าเชื้อพร้อมใส่"
    )

    // Add to clothing stock
    _clothingItems.update { listOf(newItem) + it }

    if (chosenPayoutIsPoints) {
      // Award points to customer
      _customers.update { list ->
        list.map { c ->
          if (c.id == customer.id) {
            val newBatch = PointsBatch(
              id = "batch_${System.currentTimeMillis()}",
              amount = pointsAwarded,
              dateEarned = "วันนี้",
              expiryDate = "อีก 6 เดือน",
              isExpiringSoon = false,
              note = "จากการนำ $clothingName เข้าแลก"
            )
            c.copy(
              points = c.points + pointsAwarded,
              batches = listOf(newBatch) + c.batches,
              totalClothesDeposited = c.totalClothesDeposited + 1
            )
          } else c
        }
      }

      val newTx = HistoryTransaction(
        id = "tx_${System.currentTimeMillis()}",
        title = "รับแต้มจากการนำเสื้อเข้า ($clothingName)",
        date = "วันนี้",
        pointsDelta = pointsAwarded,
        note = "ราคาประเมิน $appraisalPrice บาท"
      )
      _historyTransactions.update { map ->
        val currentList = map[customer.id] ?: emptyList()
        map + (customer.id to (listOf(newTx) + currentList))
      }

      _pointsDistributedToday.update { it + pointsAwarded }
    } else {
      // Payout in Cash: customer gets cash, record deposit count
      _customers.update { list ->
        list.map { c ->
          if (c.id == customer.id) {
            c.copy(totalClothesDeposited = c.totalClothesDeposited + 1)
          } else c
        }
      }
    }

    _evaluatedCountToday.update { it + 1 }

    return newItem
  }

  fun submitDepositRequest(request: DepositRequest) {
    _depositRequests.update { listOf(request) + it }
  }

  companion object {
    private fun createInitialCustomers(): List<Customer> {
      return listOf(
        Customer(
          id = "c1",
          name = "น้องเมย์",
          phone = "081-234-5678",
          points = 207,
          batches = listOf(
            PointsBatch("b1", 120, "2 เม.ย. 2568", "2 ต.ค. 2568", false, "รับแต้มจากการนำเสื้อเข้า"),
            PointsBatch("b2", 87, "28 มี.ค. 2568", "28 ก.ย. 2568", false, "รับแต้มจากการนำเสื้อเข้า")
          ),
          totalClothesDeposited = 3,
          totalRedeemedTimes = 2,
          tierName = "ทั่วไป",
          savedAddresses = listOf(
            DeliveryAddress(
              id = "addr_may_home",
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
            ),
            DeliveryAddress(
              id = "addr_may_condo",
              label = "คอนโด",
              recipientName = "น้องเมย์",
              phone = "081-234-5678",
              addressText = "คอนโด แอชตัน อโศก ชั้น 18 ห้อง 1804 ถนนสุขุมวิท 21",
              district = "เขตวัฒนา",
              province = "กรุงเทพฯ",
              postalCode = "10110",
              noteToRider = "ฝากไว้ที่เคาน์เตอร์นิติบุคคลชั้น G ได้เลย",
              latitude = 13.738012,
              longitude = 100.560012
            )
          )
        ),
        Customer(
          id = "c2",
          name = "คุณกานต์",
          phone = "089-876-5432",
          points = 75,
          batches = listOf(
            PointsBatch("b3", 75, "15 พ.ย. 2567", "15 พ.ค. 2568", true, "แต้มใกล้หมดอายุใน 5 วัน!")
          ),
          totalClothesDeposited = 1,
          totalRedeemedTimes = 0,
          tierName = "ทั่วไป",
          savedAddresses = listOf(
            DeliveryAddress(
              id = "addr_kan_work",
              label = "ที่ทำงาน",
              recipientName = "คุณกานต์",
              phone = "089-876-5432",
              addressText = "อาคารสยามสแควร์วัน ชั้น 6 ถนนพระราม 1",
              district = "เขตปทุมวัน",
              province = "กรุงเทพฯ",
              postalCode = "10330",
              noteToRider = "โทรหาเมื่อมาถึงล็อบบี้ตึก",
              latitude = 13.745012,
              longitude = 100.534012
            )
          )
        ),
        Customer(
          id = "c3",
          name = "คุณแพรวา",
          phone = "086-555-1234",
          points = 250,
          batches = listOf(
            PointsBatch("b4", 250, "10 ม.ค. 2568", "10 ก.ค. 2568", false, "รับแต้มจากการนำเสื้อเข้า")
          ),
          totalClothesDeposited = 4,
          totalRedeemedTimes = 3,
          tierName = "พรีเมียม",
          savedAddresses = listOf(
            DeliveryAddress(
              id = "addr_praew_home",
              label = "บ้าน",
              recipientName = "คุณแพรวา",
              phone = "086-555-1234",
              addressText = "หมู่บ้านเพอร์เฟค พาร์ค ถนนรามคำแหง",
              district = "เขตมีนบุรี",
              province = "กรุงเทพฯ",
              postalCode = "10510",
              noteToRider = "ฝากไว้ที่ป้อม รปภ. ได้เลยค่ะ",
              latitude = 13.792012,
              longitude = 100.710012
            )
          )
        )
      )
    }

    private fun createInitialTransactions(): Map<String, List<HistoryTransaction>> {
      return mapOf(
        "c1" to listOf(
          HistoryTransaction("tx1", "รับแต้มจากการนำเสื้อเข้า", "2 เม.ย. 2568", +120),
          HistoryTransaction("tx2", "ใช้แต้มแลกเสื้อ", "1 เม.ย. 2568", -140, cashPaid = 0),
          HistoryTransaction("tx3", "รับแต้มจากการนำเสื้อเข้า", "28 มี.ค. 2568", +87),
          HistoryTransaction("tx4", "ใช้แต้มแลกเสื้อ", "25 มี.ค. 2568", -67, cashPaid = 20)
        ),
        "c2" to listOf(
          HistoryTransaction("tx5", "รับแต้มจากการนำเสื้อเข้า (เสื้อยืด)", "15 พ.ย. 2567", +75, note = "ใกล้หมดอายุ!")
        ),
        "c3" to listOf(
          HistoryTransaction("tx6", "รับแต้มจากการนำเสื้อเข้า (เสื้อแบรนด์)", "10 ม.ค. 2568", +250)
        )
      )
    }

    private fun createInitialOrders(): List<RedeemOrder> {
      val defaultAddr = DeliveryAddress(
        id = "addr_sample_may",
        label = "คอนโด",
        recipientName = "น้องเมย์",
        phone = "081-234-5678",
        addressText = "คอนโด แอชตัน อโศก ชั้น 18 ห้อง 1804 ถนนสุขุมวิท 21",
        district = "เขตวัฒนา",
        province = "กรุงเทพฯ",
        postalCode = "10110",
        noteToRider = "ฝากไว้ที่เคาน์เตอร์นิติบุคคลชั้น G"
      )

      return listOf(
        RedeemOrder(
          id = "ord_0",
          orderCode = "#WE-9642",
          customerId = "c1",
          customerName = "น้องเมย์",
          customerPhone = "081-234-5678",
          itemId = "item_1",
          itemName = "เสื้อยืดวินเทจ 90s",
          itemPrice = 290,
          pointsUsed = 140,
          cashPaid = 150,
          dateTime = "1 เม.ย. 2568",
          status = "รับของสำเร็จ",
          deliveryType = DeliveryType.STORE_PICKUP
        ),
        RedeemOrder(
          id = "ord_sample_delivery",
          orderCode = "#WE-8831",
          customerId = "c1",
          customerName = "น้องเมย์",
          customerPhone = "081-234-5678",
          itemId = "item_4",
          itemName = "เสื้อเชิ้ตลายทาง MUJI",
          itemPrice = 390,
          pointsUsed = 200,
          cashPaid = 225, // 190 + 35 shipping
          dateTime = "วันนี้",
          status = "ไรเดอร์กำลังนำส่ง",
          deliveryType = DeliveryType.HOME_DELIVERY,
          shippingAddress = defaultAddr,
          shippingFee = 35,
          trackingCode = "TH-WE-77412"
        ),
        RedeemOrder(
          id = "ord_sample_pending",
          orderCode = "#WE-7842",
          customerId = "c1",
          customerName = "น้องเมย์",
          customerPhone = "081-234-5678",
          itemId = "item_2",
          itemName = "แจ็กเก็ตยีนส์พรีเมียม",
          itemPrice = 690,
          pointsUsed = 207,
          cashPaid = 483,
          dateTime = "วันนี้",
          status = "รอรับของหน้าร้าน",
          deliveryType = DeliveryType.STORE_PICKUP
        )
      )
    }

    private fun createInitialDepositRequests(): List<DepositRequest> {
      return listOf(
        DepositRequest(
          id = "dep_sample_pickup_1",
          trackingCode = "#PK-4921",
          customerId = "c1",
          customerName = "น้องเมย์",
          phone = "081-234-5678",
          category = "เสื้อยืดและแจ็กเก็ต (3 ตัว)",
          brandName = "Uniqlo, Zara",
          size = "M, L",
          condition = ClothingCondition.VERY_GOOD,
          appointmentDate = "วันนี้ (14:00 - 16:00 น.)",
          appointmentTime = "14:00 - 16:00 น.",
          status = "ไรเดอร์กำลังเดินทางเข้ารับ",
          intakeType = DepositIntakeType.HOME_PICKUP,
          pickupAddress = DeliveryAddress(
            id = "addr_pickup_may",
            label = "บ้าน",
            recipientName = "น้องเมย์",
            phone = "081-234-5678",
            addressText = "บ้านเลขที่ 88/12 ซอยสุขุมวิท 39",
            district = "เขตวัฒนา",
            province = "กรุงเทพฯ",
            postalCode = "10110",
            noteToRider = "กดกริ่งหน้าบ้าน หรือโทรหาก่อนถึง 5 นาที"
          ),
          pickupFee = 0, // ฟรีสำหรับ 3 ตัวขึ้นไป
          numberOfClothes = 3
        ),
        DepositRequest(
          id = "dep_sample_pickup_2",
          trackingCode = "#PK-3318",
          customerId = "c2",
          customerName = "คุณกานต์",
          phone = "089-876-5432",
          category = "เสื้อเชิ้ต",
          brandName = "MUJI",
          size = "L",
          condition = ClothingCondition.GOOD,
          appointmentDate = "พรุ่งนี้ (10:00 - 12:00 น.)",
          appointmentTime = "10:00 - 12:00 น.",
          status = "รอไรเดอร์เข้ารับ",
          intakeType = DepositIntakeType.HOME_PICKUP,
          pickupAddress = DeliveryAddress(
            id = "addr_pickup_kan",
            label = "ที่ทำงาน",
            recipientName = "คุณกานต์",
            phone = "089-876-5432",
            addressText = "อาคารสยามสแควร์วัน ชั้น 6 ถนนพระราม 1",
            district = "เขตปทุมวัน",
            province = "กรุงเทพฯ",
            postalCode = "10330",
            noteToRider = "โทรหาเมื่อมาถึงล็อบบี้ตึก"
          ),
          pickupFee = 40,
          numberOfClothes = 1
        )
      )
    }

    private fun createInitialItems(): List<ClothingItem> {
      return listOf(
        // Item matching Test Case 1: General item 96 baht (Appraisal 60)
        ClothingItem(
          id = "item_test_1",
          name = "เสื้อยืดสตรีทลายกราฟิก",
          category = "เสื้อยืด",
          brandName = "Street Basic",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.VERY_GOOD,
          size = "L",
          price = 96,
          appraisalPrice = 60,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_vintage_tee",
          isRecommended = true,
          description = "เสื้อยืดคอกลมผ้าคอตตอน 100% สภาพดีมาก ไม่มีตำหนิ"
        ),
        // Item matching Test Case 2: Brand item 320 baht (Appraisal 200)
        ClothingItem(
          id = "item_test_2",
          name = "แจ็กเก็ตยีนส์พรีเมียม Levi's",
          category = "แจ็กเก็ต",
          brandName = "Levi's",
          level = ClothingLevel.BRAND,
          condition = ClothingCondition.VERY_GOOD,
          size = "M",
          price = 320,
          appraisalPrice = 200,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_denim_jacket",
          isRecommended = true,
          description = "แจ็กเก็ตยีนส์แท้ Levi's ทรงวินเทจ สภาพสมบูรณ์ สวยมาก"
        ),
        // Additional items matching mockup photos and prices (290, 690, 390, 490, 72, 108, 160)
        ClothingItem(
          id = "item_1",
          name = "เสื้อยืดวินเทจ",
          category = "เสื้อยืด",
          brandName = "Vintage Collection",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.VERY_GOOD,
          size = "L",
          price = 290,
          appraisalPrice = 180,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_vintage_tee",
          isRecommended = true,
          description = "เสื้อยืดเฟดธรรมชาติ สไตล์ 90s แท้ ทรงโอเวอร์ไซซ์"
        ),
        ClothingItem(
          id = "item_2",
          name = "แจ็กเก็ตยีนส์",
          category = "แจ็กเก็ต",
          brandName = "Uniqlo Vintage",
          level = ClothingLevel.BRAND,
          condition = ClothingCondition.VERY_GOOD,
          size = "M",
          price = 690,
          appraisalPrice = 430,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_denim_jacket",
          isRecommended = true,
          description = "แจ็กเก็ตผ้ายีนส์หนานุ่ม สีมิดไนท์บลู คัตติ้งเนี้ยบ"
        ),
        ClothingItem(
          id = "item_3",
          name = "เสื้อเชิ้ตลูกฟูก",
          category = "เสื้อเชิ้ต",
          brandName = "Earth Tone",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.GOOD,
          size = "L",
          price = 390,
          appraisalPrice = 240,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_corduroy_shirt",
          isRecommended = true,
          description = "เสื้อเชิ้ตผ้าลูกฟูกสีน้ำตาลอบอุ่น ใส่คลุมเก๋ๆ สไตล์มินิมอล"
        ),
        ClothingItem(
          id = "item_4",
          name = "เสื้อเชิ้ตลายทาง",
          category = "เสื้อเชิ้ต",
          brandName = "MUJI Linen",
          level = ClothingLevel.BRAND,
          condition = ClothingCondition.VERY_GOOD,
          size = "M",
          price = 390,
          appraisalPrice = 240,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_striped_shirt",
          isRecommended = true,
          description = "เสื้อเชิ้ตลินินผสมคอตตอน ระบายอากาศดีเยี่ยม สีฟ้าลายทางขาว"
        ),
        ClothingItem(
          id = "item_5",
          name = "กางเกงคาร์โก้",
          category = "กางเกง",
          brandName = "Utility Workwear",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.GOOD,
          size = "XL",
          price = 490,
          appraisalPrice = 300,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_cargo_pants",
          isRecommended = true,
          description = "กางเกงคาร์โก้ขากระบอก กระเป๋าจุใจ สีน้ำตาลกากี"
        ),
        ClothingItem(
          id = "item_6",
          name = "เสื้อยืดมินิมอลสีพื้น",
          category = "เสื้อยืด",
          brandName = "Everyday",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.VERY_GOOD,
          size = "S",
          price = 72,
          appraisalPrice = 45,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_vintage_tee",
          isRecommended = false,
          description = "เสื้อยืดคอตตอนแท้ สีเรียบง่าย ใส่สบายทุกวัน"
        ),
        ClothingItem(
          id = "item_7",
          name = "เสื้อกักไหมพรมเกาหลี",
          category = "เสื้อเชิ้ต",
          brandName = "Seoul Knit",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.VERY_GOOD,
          size = "Free Size",
          price = 108,
          appraisalPrice = 68,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_corduroy_shirt",
          isRecommended = false,
          description = "เสื้อกักไหมพรมลายน่ารัก แมตช์กับเสื้อเชิ้ตขาวหรือเสื้อยืด"
        ),
        ClothingItem(
          id = "item_8",
          name = "เสื้อเชิ้ตลำลองแขนสั้น",
          category = "เสื้อเชิ้ต",
          brandName = "Casual Living",
          level = ClothingLevel.GENERAL,
          condition = ClothingCondition.GOOD,
          size = "L",
          price = 160,
          appraisalPrice = 100,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_striped_shirt",
          isRecommended = false,
          description = "เสื้อเชิ้ตทรงหลวม ผ้าเบาสบาย เหมาะกับวันพักผ่อน"
        ),
        ClothingItem(
          id = "item_9",
          name = "เสื้อโปโลคลาสสิก",
          category = "เสื้อยืด",
          brandName = "Lacoste",
          level = ClothingLevel.BRAND,
          condition = ClothingCondition.VERY_GOOD,
          size = "M",
          price = 320,
          appraisalPrice = 200,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_vintage_tee",
          isRecommended = false,
          description = "เสื้อโปโลปกผ้าปิเก้ คลาสสิกตลอดกาลของ Lacoste"
        ),
        ClothingItem(
          id = "item_10",
          name = "กางเกงชิโน่เข้ารูป",
          category = "กางเกง",
          brandName = "Dockers",
          level = ClothingLevel.BRAND,
          condition = ClothingCondition.VERY_GOOD,
          size = "L",
          price = 490,
          appraisalPrice = 300,
          status = ItemStatus.AVAILABLE,
          imageDrawableName = "prod_cargo_pants",
          isRecommended = false,
          description = "กางเกงชิโน่คุณภาพเยี่ยม ใส่ทำงานหรือลำลองได้สวยงาม"
        )
      )
    }
  }
}
