package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.content.Context
import android.os.Build
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.DateItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ElapsedTimeItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DeviceInfoUtils
import com.ryccoatika.sqatoolkit.devinfo.core.utils.SoftwareInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetSoftwareInfo(
  private val context: Context,
  private val softwareInfoUtils: SoftwareInfoUtils,
  private val deviceInfoUtils: DeviceInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  private fun s(id: Int) = context.getString(id)

  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    val androidItems = buildList {
      add(RawTextItem(s(R.string.di_label_android_version), Build.VERSION.RELEASE.or("-")))
      add(RawTextItem(s(R.string.di_label_api_level), Build.VERSION.SDK_INT.toString()))
      add(RawTextItem(s(R.string.di_label_codename), deviceInfoUtils.getCodename()))
      add(TextItem(Label.ReleasedWith, softwareInfoUtils.getDeviceReleaseAndroidVersion()))
      val androidUI = softwareInfoUtils.getAndroidUI()
      if (androidUI.isNotBlank()) {
        add(TextItem(Label.UserInterface, androidUI))
      }
      add(DateItem(Label.SecurityPatch, softwareInfoUtils.getSecurityPatch()))
      add(RawTextItem(s(R.string.di_label_build_number), Build.DISPLAY.or("-")))
      add(RawTextItem(s(R.string.di_label_build_id), Build.ID.or("-")))
      add(RawTextItem(s(R.string.di_label_build_type), Build.TYPE.or("-")))
      add(RawTextItem(s(R.string.di_label_build_tags), Build.TAGS.or("-")))
      add(RawTextItem(s(R.string.di_label_fingerprint), Build.FINGERPRINT.or("-")))
      add(RawTextItem(s(R.string.di_label_runtime), softwareInfoUtils.getRuntime()))
      add(RawTextItem(s(R.string.di_label_java_vm), softwareInfoUtils.getJavaVMVersion()))
      add(RawTextItem(s(R.string.di_label_kernel), softwareInfoUtils.getKernelVersion()))
      add(RawTextItem(s(R.string.di_label_baseband), Build.getRadioVersion().or("-")))
      add(RawTextItem(s(R.string.di_label_bootloader), Build.BOOTLOADER.or("-")))
      add(RawTextItem(s(R.string.di_label_opengl_es), softwareInfoUtils.getOpenGLESVersion()))
      add(RawTextItem(s(R.string.di_label_vulkan), softwareInfoUtils.getVulkanVersion()))
      add(RawTextItem(s(R.string.di_label_selinux), softwareInfoUtils.getSELinux()))
      add(ElapsedTimeItem(Label.SystemUptime, softwareInfoUtils.getSystemUptime()))
    }

    val systemItems = buildList {
      add(
        RawTextItem(
          s(R.string.di_label_root),
          if (softwareInfoUtils.isRooted()) s(R.string.di_text_detected) else s(R.string.di_text_not_detected),
        ),
      )
      add(RawTextItem(s(R.string.di_label_ab_update), yn(softwareInfoUtils.isAbUpdate())))
      add(RawTextItem(s(R.string.di_label_treble), yn(softwareInfoUtils.isTreble())))
      add(
        RawTextItem(
          s(R.string.di_label_play_services),
          softwareInfoUtils.getPlayServicesVersion() ?: s(R.string.di_text_not_installed),
        ),
      )
    }

    val drmGroup = softwareInfoUtils.getDrmInfo()?.let { drm ->
      GroupItem(
        rawTitle = s(R.string.di_label_drm),
        items = listOf(
          RawTextItem(s(R.string.di_label_drm_vendor), drm.vendor),
          RawTextItem(s(R.string.di_label_drm_version), drm.version),
          RawTextItem(s(R.string.di_label_drm_description), drm.description),
          RawTextItem(s(R.string.di_label_drm_algorithm), drm.algorithms),
          RawTextItem(s(R.string.di_label_drm_security_level), drm.securityLevel),
          RawTextItem(s(R.string.di_label_drm_max_hdcp_level), drm.maxHdcpLevel),
        ),
      )
    }

    listOfNotNull(
      GroupItem(rawTitle = s(R.string.di_group_android), items = androidItems),
      GroupItem(rawTitle = s(R.string.di_group_system), items = systemItems),
      drmGroup,
    )
  }

  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)
}
