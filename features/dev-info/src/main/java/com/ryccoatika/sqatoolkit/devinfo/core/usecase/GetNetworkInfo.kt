package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.Manifest
import android.content.Context
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.NetworkInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetNetworkInfo(
  private val context: Context,
  private val net: NetworkInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  private fun s(id: Int) = context.getString(id)

  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    if (!net.hasTelephony()) {
      return@withContext listOf(RawTextItem(s(R.string.di_group_telephony), s(R.string.di_text_not_available)))
    }
    fun gated(label: Int, permission: String, value: () -> String): Item =
      if (net.hasPermission(permission)) RawTextItem(s(label), value())
      else PermissionItem(s(label), permission, s(R.string.di_text_permission_required))

    listOf(
      GroupItem(
        rawTitle = s(R.string.di_group_telephony),
        items = listOf(
          RawTextItem(s(R.string.di_label_network_operator), net.getNetworkOperator()),
          RawTextItem(s(R.string.di_label_sim_operator), net.getSimOperator()),
          RawTextItem(s(R.string.di_label_network_mccmnc), net.getNetworkMccMnc()),
          RawTextItem(s(R.string.di_label_sim_mccmnc), net.getSimMccMnc()),
          RawTextItem(s(R.string.di_label_network_country), net.getNetworkCountry()),
          RawTextItem(s(R.string.di_label_sim_country), net.getSimCountry()),
          RawTextItem(s(R.string.di_label_phone_type), net.getPhoneType()),
          RawTextItem(s(R.string.di_label_sim_state), net.getSimState()),
          RawTextItem(s(R.string.di_label_sim_count), net.getSimCount()),
          RawTextItem(s(R.string.di_label_roaming), if (net.isRoaming()) s(R.string.di_text_yes) else s(R.string.di_text_no)),
          gated(R.string.di_label_data_network_type, Manifest.permission.READ_PHONE_STATE, net::getDataNetworkType),
          gated(R.string.di_label_imei, Manifest.permission.READ_PHONE_STATE, net::getImei),
          gated(R.string.di_label_phone_number, Manifest.permission.READ_PHONE_NUMBERS, net::getPhoneNumber),
        ),
      ),
    )
  }
}
