package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.os.Build
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.core.model.DateItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ElapsedTimeItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.StatusItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DeviceInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetDeviceInfo(
  private val deviceInfoUtils: DeviceInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    // Device Info Card
    val cardItem = DeviceCardItem(
      androidName = deviceInfoUtils.getAndroidName(),
      internalCodename = deviceInfoUtils.getCodename(),
      sdkVersion = Build.VERSION.SDK_INT,
      releaseDate = deviceInfoUtils.getAndroidReleaseDate(),
    )

    // Basic Info
    val basicItems = listOf(
      TextItem(
        label = Label.Model,
        value = Build.MODEL,
      ),
      TextItem(
        label = Label.Brand,
        value = Build.BRAND,
      ),
      TextItem(
        label = Label.Board,
        value = Build.BOARD,
      ),
      TextItem(
        label = Label.Device,
        value = Build.DEVICE,
      ),
      TextItem(
        label = Label.ProductCode,
        value = Build.PRODUCT,
      ),
      TextItem(
        label = Label.Hardware,
        value = Build.HARDWARE,
      ),
      TextItem(
        label = Label.AndroidID,
        value = deviceInfoUtils.getAndroidId(),
      ),
      TextItem(
        label = Label.Fingerprint,
        value = Build.FINGERPRINT,
      ),
      StatusItem(
        label = Label.ESim,
        value = deviceInfoUtils.checkESimSupport(),
      ),
    )

    // Manufacturer Info
    val manufacturerItems = listOf(
      TextItem(
        label = Label.Manufacturer,
        value = Build.MANUFACTURER,
      ),
      DateItem(
        label = Label.ManufacturedDate,
        value = deviceInfoUtils.getManufacturedDate(),
      ),
      DateItem(
        label = Label.DeviceAge,
        value = deviceInfoUtils.getManufacturedDate(),
        isPeriod = true,
      ),
    )

    // Sales Info
    val saleItems = listOf(
      TextItem(
        label = Label.SalesCode,
        value = deviceInfoUtils.getSalesCode(),
      ),
      TextItem(
        label = Label.SalesCountry,
        value = deviceInfoUtils.getSalesCountry(),
      ),
    )

    val systemItems = buildList {
      add(TextItem(Label.ReleasedWith, deviceInfoUtils.getDeviceReleaseAndroidVersion()))
      val androidUI = deviceInfoUtils.getAndroidUI()
      if (androidUI.isNotBlank()) {
        add(TextItem(Label.UserInterface, androidUI))
      }
      add(DateItem(Label.SecurityPatch, deviceInfoUtils.getSecurityPatch()))
      add(TextItem(Label.Bootloader, Build.BOOTLOADER))
      add(TextItem(Label.Build, Build.DISPLAY))
      add(TextItem(Label.Baseband, Build.getRadioVersion()))
      add(TextItem(Label.JavaVM, deviceInfoUtils.getJavaVMVersion()))
      add(TextItem(Label.Kernel, deviceInfoUtils.getKernelVersion()))
      add(TextItem(Label.OpenGLES, deviceInfoUtils.getOpenGLESVersion()))
      add(TextItem(Label.Vulkan, deviceInfoUtils.getVulkanVersion()))
      add(TextItem(Label.SELinux, deviceInfoUtils.getSELinux()))
      add(ElapsedTimeItem(Label.SystemUptime, deviceInfoUtils.getSystemUptime()))
    }

    // DRM Info
    val drmGroup = deviceInfoUtils.getDrmInfo()?.let { drm ->
      GroupItem(
        label = Label.DRM,
        items = listOf(
          TextItem(Label.DRMVendor, drm.vendor),
          TextItem(Label.DRMVersion, drm.version),
          TextItem(Label.DRMDescription, drm.description),
          TextItem(Label.DRMAlgorithm, drm.algorithms),
          TextItem(Label.DRMSecurityLevel, drm.securityLevel),
          TextItem(Label.DRMMaxHDCPLevel, drm.maxHdcpLevel),
        ),
      )
    }

    listOfNotNull(
      cardItem,
      GroupItem(items = basicItems),
      GroupItem(items = manufacturerItems),
      GroupItem(items = saleItems),
      GroupItem(items = systemItems),
      drmGroup,
    )
  }
}
