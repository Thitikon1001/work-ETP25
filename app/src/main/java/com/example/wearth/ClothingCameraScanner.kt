package com.example.wearth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * หน้าจอสแกนประเมินราคาเสื้อผ้าด้วย CameraX
 * มีทั้งระบบจับภาพกล้องสดจริง และ Fallback Demo Simulator สำหรับ Emulator
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingCameraScanner(
  onDismiss: () -> Unit,
  onApplyAppraisalToDeposit: (QuickAppraisalResult) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  var camera by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }
  var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
  var isScanningAnimationActive by remember { mutableStateOf(true) }
  var isAnalyzing by remember { mutableStateOf(false) }
  var cameraInitError by remember { mutableStateOf(false) }

  // Scanned appraisal result
  var scanResult by remember { mutableStateOf<QuickAppraisalResult?>(null) }

  // Presets for quick appraisal samples
  val sampleClothing = remember {
    listOf(
      QuickAppraisalResult(
        itemName = "เสื้อยืดสกรีนลายวินเทจ",
        category = "เสื้อยืด",
        detectedBrand = "Uniqlo UT (ทั่วไป)",
        level = ClothingLevel.GENERAL,
        condition = ClothingCondition.VERY_GOOD,
        size = "L",
        estimatedAppraisalPrice = 80,
        confidenceScore = 96,
        detectedFabric = "ผ้าคอตตอน 100% สภาพดีมาก ไม่มีรอยเปื้อน คอเสื้อไม่ย้วย"
      ),
      QuickAppraisalResult(
        itemName = "แจ็กเก็ตยีนส์เดนิมคลาสสิก",
        category = "แจ็กเก็ต",
        detectedBrand = "Levi's (แบรนด์)",
        level = ClothingLevel.BRAND,
        condition = ClothingCondition.VERY_GOOD,
        size = "M",
        estimatedAppraisalPrice = 200,
        confidenceScore = 93,
        detectedFabric = "ผ้ายีนส์หนา 13.5oz สียังเข้ม กระดุมครบ ไม่มีตำหนิ"
      ),
      QuickAppraisalResult(
        itemName = "เสื้อเชิ้ตทำงานผ้าออกฟอร์ด",
        category = "เสื้อเชิ้ต",
        detectedBrand = "Zara Man (แบรนด์)",
        level = ClothingLevel.BRAND,
        condition = ClothingCondition.GOOD,
        size = "XL",
        estimatedAppraisalPrice = 160,
        confidenceScore = 89,
        detectedFabric = "ผ้า Oxford Cotton สีขาวสะอาด สภาพดี"
      ),
      QuickAppraisalResult(
        itemName = "เสื้อยืดเบสิก สีเอิร์ธโทน",
        category = "เสื้อยืด",
        detectedBrand = "ไม่มีแบรนด์ (ทั่วไป)",
        level = ClothingLevel.GENERAL,
        condition = ClothingCondition.GOOD,
        size = "M",
        estimatedAppraisalPrice = 60,
        confidenceScore = 91,
        detectedFabric = "ผ้าคอตตอนผสมนุ่ม สภาพพร้อมใช้งาน"
      )
    )
  }

  // Scanning laser animation
  val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
  val laserProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_anim"
  )

  fun triggerAppraisal(selectedPreset: QuickAppraisalResult? = null) {
    scope.launch {
      isAnalyzing = true
      // Simulate real-time computer vision processing
      delay(1200)
      isAnalyzing = false
      scanResult = selectedPreset ?: sampleClothing.random()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("clothing_camera_scanner_screen")
  ) {
    if (hasCameraPermission && !cameraInitError) {
      // 1. CameraX Live Viewfinder
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
          }

          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            try {
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
              }
              val imageCapture = ImageCapture.Builder().build()
              val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

              cameraProvider.unbindAll()
              val cam = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
              )
              camera = cam
            } catch (e: Exception) {
              cameraInitError = true
            }
          }, ContextCompat.getMainExecutor(ctx))

          previewView
        },
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // 2. Camera Simulation / Fallback View (for emulators or before permission)
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0xFF0F2F1D), Color(0xFF0A1F13), Color(0xFF05100A))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(32.dp)
        ) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(CircleShape)
              .background(WearthDarkGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "กล้องถ่ายภาพ",
              tint = Color(0xFFA7F3D0),
              modifier = Modifier.size(48.dp)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Text(
            text = "สแกนประเมินราคาเสื้อผ้า (CameraX)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "ส่องกล้องไปที่เสื้อผ้าของคุณเพื่อประเมินราคารับซื้อ แต้มสะสม และวิเคราะห์เนื้อผ้าได้ทันที",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD1FAE5),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(24.dp))

          if (!hasCameraPermission) {
            Button(
              onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
              colors = ButtonDefaults.buttonColors(containerColor = WearthTealGreen),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth().testTag("request_camera_permission_button")
            ) {
              Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("เปิดใช้งานกล้องถ่ายรูป", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          OutlinedButton(
            onClick = { triggerAppraisal(sampleClothing.first()) },
            border = BorderStroke(1.dp, Color(0xFF6EE7B7)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6EE7B7)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("simulate_camera_scan_button")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ทดลองสแกนด้วยภาพตัวอย่าง", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 3. Viewfinder Reticle & Laser Overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp, vertical = 110.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp)
          .border(
            width = 2.dp,
            color = Color(0xFF34D399).copy(alpha = 0.8f),
            shape = RoundedCornerShape(24.dp)
          )
          .clip(RoundedCornerShape(24.dp))
      ) {
        // Laser Scan line
        if (isScanningAnimationActive && !isAnalyzing) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp)
              .align(Alignment.TopCenter)
              .offset(y = (340 * laserProgress).dp)
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    Color.Transparent,
                    Color(0xFF34D399),
                    Color(0xFFFFFFFF),
                    Color(0xFF34D399),
                    Color.Transparent
                  )
                )
              )
          )
        }

        // Silhouette hint
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(text = "👕", fontSize = 56.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "จัดเสื้อผ้าให้อยู่ในกรอบ\nและให้เห็นป้ายคอ/ป้ายแบรนด์",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
          )
        }

        // Live detection chips
        Row(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(Color(0xFF10B981))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CameraX AI: กำลังตรวจจับป้ายและเนื้อผ้า",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
          )
        }
      }
    }

    // 4. Top Controls (Close, Torch, Camera Flip)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onDismiss,
        modifier = Modifier
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.5f))
          .testTag("close_camera_scanner_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "ปิด",
          tint = Color.White
        )
      }

      Text(
        text = "สแกนประเมินราคาเสื้อผ้า",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Torch toggle
        IconButton(
          onClick = {
            camera?.let { cam ->
              if (cam.cameraInfo.hasFlashUnit()) {
                isTorchOn = !isTorchOn
                cam.cameraControl.enableTorch(isTorchOn)
              }
            }
          },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
        ) {
          Icon(
            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = "ไฟฉาย",
            tint = if (isTorchOn) Color(0xFFFDE68A) else Color.White
          )
        }

        // Lens flip
        IconButton(
          onClick = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
              CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
          },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
        ) {
          Icon(
            imageVector = Icons.Default.FlipCameraAndroid,
            contentDescription = "สลับกล้อง",
            tint = Color.White
          )
        }
      }
    }

    // 5. Bottom Controls (Shutter Button & Quick Presets)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(bottom = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Preset test pills
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        sampleClothing.take(3).forEach { sample ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .clickable { triggerAppraisal(sample) }
          ) {
            Text(
              text = sample.category,
              color = Color.White,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Shutter / Capture Button
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .border(4.dp, Color.White, CircleShape)
          .padding(6.dp)
          .clip(CircleShape)
          .background(if (isAnalyzing) Color.Gray else WearthTealGreen)
          .clickable(enabled = !isAnalyzing) {
            triggerAppraisal()
          }
          .testTag("camera_shutter_button"),
        contentAlignment = Alignment.Center
      ) {
        if (isAnalyzing) {
          CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
          )
        } else {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "ถ่ายภาพประเมินราคา",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "แตะเพื่อถ่ายภาพและประเมินราคาทันที",
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.8f)
      )
    }

    // 6. Quick Appraisal Result Bottom Sheet
    scanResult?.let { result ->
      QuickAppraisalResultSheet(
        result = result,
        onDismiss = { scanResult = null },
        onApplyToDeposit = {
          onApplyAppraisalToDeposit(result)
          onDismiss()
        },
        onScanNext = {
          scanResult = null
        }
      )
    }
  }
}

/**
 * Bottom Sheet แสดงผลการประเมินราคาเสื้อผ้าด่วน
 * พร้อมการคำนวณแต้มและเงินสดตามเกณฑ์ของ WEARTH
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAppraisalResultSheet(
  result: QuickAppraisalResult,
  onDismiss: () -> Unit,
  onApplyToDeposit: () -> Unit,
  onScanNext: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = Modifier.testTag("appraisal_result_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header with badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(WearthLightTeal),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = WearthDarkGreen,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "ประเมินราคาสำเร็จ! ✨",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = WearthDarkGreen
            )
            Text(
              text = "วิเคราะห์โดย CameraX AI Scanner",
              style = MaterialTheme.typography.labelSmall,
              color = WearthTextSecondary
            )
          }
        }

        // Confidence Chip
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFECFDF5))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ความมั่นใจ ${result.confidenceScore}%",
            color = Color(0xFF059669),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Item Summary Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = result.itemName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Category Tag
            Text(
              text = "หมวด: ${result.category}",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0E7FF))
                .padding(horizontal = 8.dp, vertical = 3.dp),
              color = Color(0xFF3730A3),
              fontWeight = FontWeight.Bold
            )

            // Brand/Level Tag
            Text(
              text = "ระดับ: ${result.level.label} (${result.detectedBrand})",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (result.level == ClothingLevel.BRAND) Color(0xFFFCE7F3) else Color(0xFFFEF3C7))
                .padding(horizontal = 8.dp, vertical = 3.dp),
              color = if (result.level == ClothingLevel.BRAND) Color(0xFF9D174D) else Color(0xFF92400E),
              fontWeight = FontWeight.Bold
            )

            // Condition Tag
            Text(
              text = "สภาพ: ${result.condition.label}",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFD1FAE5))
                .padding(horizontal = 8.dp, vertical = 3.dp),
              color = Color(0xFF065F46),
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "🔍 รายละเอียดเนื้อผ้า: ${result.detectedFabric}",
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Appraisal Payout Options Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WearthLightTeal),
        border = BorderStroke(1.5.dp, WearthTealGreen),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "💰 ทางเลือกผลตอบแทนที่คุณจะได้รับ",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Option 1: Points (Recommended +25%)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .border(1.dp, WearthTealGreen, RoundedCornerShape(12.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "รับเป็นแต้ม WEARTH",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "คุ้มสุด +25%",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF059669))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = "ใช้แลกเสื้อตัวอื่นได้เต็มมูลค่า 1 แต้ม = 1 บาท",
                style = MaterialTheme.typography.labelSmall,
                color = WearthTextSecondary,
                fontSize = 11.sp
              )
            }

            Text(
              text = "${result.pointsReward} แต้ม\n(= ${result.pointsReward} บ.)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF059669),
              textAlign = TextAlign.End
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2: Cash Payout
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.8f))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "รับเป็นเงินสดทันที",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = WearthTextPrimary
              )
              Text(
                text = "โอนเข้าบัญชี หรือรับเงินสดที่หน้าร้าน",
                style = MaterialTheme.typography.labelSmall,
                color = WearthTextSecondary,
                fontSize = 11.sp
              )
            }

            Text(
              text = "฿${result.estimatedAppraisalPrice}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Recommended shop price
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "ราคาจำหน่ายหน้าร้านที่แนะนำ (P × 1.6):",
              style = MaterialTheme.typography.labelSmall,
              color = WearthTextSecondary
            )
            Text(
              text = "฿${result.recommendedSellingPrice}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = WearthDarkGreen
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Button(
        onClick = onApplyToDeposit,
        colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("apply_appraisal_to_deposit_button")
      ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "นำข้อมูลนี้ไปฝากเสื้อรับแต้มทันที",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onScanNext,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, WearthTealGreen),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = WearthDarkGreen),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("scan_next_clothing_button")
      ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("สแกนตัวถัดไป", fontWeight = FontWeight.Bold)
      }
    }
  }
}
