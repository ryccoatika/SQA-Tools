package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.Manifest
import android.content.Context
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.ConnectivityInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetConnectivityInfo(
  private val context: Context,
  private val conn: ConnectivityInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  private fun s(id: Int) = context.getString(id)
  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)

  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    val loc = conn.hasLocationPermission()
    fun gatedLoc(label: Int, value: () -> String): Item =
      if (loc) RawTextItem(s(label), value())
      else PermissionItem(s(label), Manifest.permission.ACCESS_FINE_LOCATION, s(R.string.di_text_permission_required))

    val wifi = GroupItem(
      rawTitle = s(R.string.di_group_wifi),
      items = listOf(
        RawTextItem(s(R.string.di_label_wifi_enabled), yn(conn.isWifiEnabled())),
        RawTextItem(s(R.string.di_label_wifi_5ghz), yn(conn.is5GhzSupported())),
        RawTextItem(s(R.string.di_label_wifi_6ghz), yn(conn.is6GhzSupported())),
        RawTextItem(s(R.string.di_label_wifi_aware), yn(conn.isWifiAwareSupported())),
        RawTextItem(s(R.string.di_label_wifi_direct), yn(conn.isWifiDirectSupported())),
        gatedLoc(R.string.di_label_wifi_ssid, conn::getSsid),
        gatedLoc(R.string.di_label_wifi_bssid, conn::getBssid),
        gatedLoc(R.string.di_label_wifi_speed, conn::getLinkSpeed),
        gatedLoc(R.string.di_label_wifi_frequency, conn::getFrequency),
        gatedLoc(R.string.di_label_wifi_rssi, conn::getRssi),
        gatedLoc(R.string.di_label_wifi_ip, conn::getIpAddress),
      ),
    )

    val btName: Item =
      if (conn.hasBluetoothConnectPermission()) {
        RawTextItem(s(R.string.di_label_bt_name), conn.getBluetoothName())
      } else {
        PermissionItem(s(R.string.di_label_bt_name), Manifest.permission.BLUETOOTH_CONNECT, s(R.string.di_text_permission_required))
      }

    val bt = GroupItem(
      rawTitle = s(R.string.di_group_bluetooth),
      items = listOf(
        RawTextItem(s(R.string.di_label_bt_supported), yn(conn.isBluetoothSupported())),
        RawTextItem(s(R.string.di_label_bt_le), yn(conn.isBleSupported())),
        RawTextItem(s(R.string.di_label_bt_advertiser), yn(conn.isBleAdvertiserSupported())),
        btName,
      ),
    )

    val other = GroupItem(
      rawTitle = s(R.string.di_group_other),
      items = listOf(
        RawTextItem(s(R.string.di_label_nfc), yn(conn.isNfcSupported())),
        RawTextItem(s(R.string.di_label_usb_host), yn(conn.isUsbHostSupported())),
        RawTextItem(s(R.string.di_label_ethernet), yn(conn.isEthernetSupported())),
      ),
    )

    listOf(wifi, bt, other)
  }
}
