package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.content.Context
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.HardwareInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetHardwareInfo(
  private val context: Context,
  private val hw: HardwareInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  private fun s(id: Int) = context.getString(id)

  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    listOf(
      GroupItem(
        rawTitle = s(R.string.di_group_processor),
        items = listOf(
          RawTextItem(s(R.string.di_label_chipset), hw.getChipset()),
          RawTextItem(s(R.string.di_label_architecture), hw.getArchitecture()),
          RawTextItem(s(R.string.di_label_abis), hw.getSupportedAbis()),
          RawTextItem(s(R.string.di_label_cores), hw.getCoreCount().toString()),
          RawTextItem(s(R.string.di_label_governor), hw.getCpuGovernor()),
          RawTextItem(s(R.string.di_label_cpu_freq), hw.getCpuFreqRange()),
        ),
      ),
      GroupItem(rawTitle = s(R.string.di_group_gpu), items = hw.getGpuItems(context)),
      GroupItem(
        rawTitle = s(R.string.di_group_memory),
        items = listOf(
          RawTextItem(s(R.string.di_label_ram_total), hw.getRamTotal()),
          RawTextItem(s(R.string.di_label_ram_available), hw.getRamAvailable()),
          RawTextItem(s(R.string.di_label_ram_threshold), hw.getRamThreshold()),
          RawTextItem(s(R.string.di_label_memory_class), hw.getMemoryClass()),
          RawTextItem(s(R.string.di_label_large_memory_class), hw.getLargeMemoryClass()),
        ),
      ),
      GroupItem(
        rawTitle = s(R.string.di_group_storage),
        items = listOf(
          RawTextItem(s(R.string.di_label_storage_internal), hw.getInternalStorage()),
          RawTextItem(s(R.string.di_label_storage_system), hw.getSystemStorage()),
        ),
      ),
      GroupItem(
        rawTitle = s(R.string.di_group_display),
        items = listOf(
          RawTextItem(s(R.string.di_label_resolution), hw.getResolution()),
          RawTextItem(s(R.string.di_label_density), hw.getDensity()),
          RawTextItem(s(R.string.di_label_refresh_rate), hw.getRefreshRate()),
          RawTextItem(s(R.string.di_label_screen_size), hw.getScreenSize()),
          RawTextItem(s(R.string.di_label_hdr), if (hw.isHdr()) s(R.string.di_text_yes) else s(R.string.di_text_no)),
        ),
      ),
      GroupItem(
        rawTitle = s(R.string.di_group_battery),
        items = listOf(
          RawTextItem(s(R.string.di_label_battery_technology), hw.getBatteryTechnology()),
          RawTextItem(s(R.string.di_label_battery_health), hw.getBatteryHealth()),
          RawTextItem(s(R.string.di_label_battery_status), hw.getBatteryStatus()),
          RawTextItem(s(R.string.di_label_battery_capacity), hw.getBatteryCapacity()),
          RawTextItem(s(R.string.di_label_battery_voltage), hw.getBatteryVoltage()),
          RawTextItem(s(R.string.di_label_battery_temperature), hw.getBatteryTemperature()),
        ),
      ),
    )
  }
}
