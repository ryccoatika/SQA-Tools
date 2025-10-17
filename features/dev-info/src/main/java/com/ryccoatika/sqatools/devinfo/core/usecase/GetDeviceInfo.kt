package com.ryccoatika.sqatools.devinfo.core.usecase

import android.os.Build
import com.ryccoatika.sqatools.common.ResultInteractor
import com.ryccoatika.sqatools.devinfo.core.model.DateItem
import com.ryccoatika.sqatools.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatools.devinfo.core.model.GroupItem
import com.ryccoatika.sqatools.devinfo.core.model.Item
import com.ryccoatika.sqatools.devinfo.core.model.Label
import com.ryccoatika.sqatools.devinfo.core.model.StatusItem
import com.ryccoatika.sqatools.devinfo.core.model.TextItem
import com.ryccoatika.sqatools.devinfo.core.utils.DeviceInfoUtils
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
      androidVersion = Build.VERSION.RELEASE,
      internalCodename = deviceInfoUtils.getCodename(),
      sdkVersion = Build.VERSION.SDK_INT,
      releaseDate = deviceInfoUtils.getAndroidReleaseDate(),
    )

    // Basic Info
    val basicItems = listOf(
      TextItem(
        label = Label.AndroidID,
        value = deviceInfoUtils.getAndroidId(),
      ),
      TextItem(
        label = Label.Device,
        value = Build.DEVICE,
      ),
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
        label = Label.ProductCode,
        value = Build.PRODUCT,
      ),
      TextItem(
        label = Label.Hardware,
        value = Build.HARDWARE,
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

    listOf(
      cardItem,
      GroupItem(null, basicItems),
      GroupItem(null, manufacturerItems),
      GroupItem(null, saleItems),
    )
  }
}
