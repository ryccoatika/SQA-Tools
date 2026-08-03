package com.ryccoatika.sqatoolkit.devinfo.ui.info

import com.ryccoatika.sqatoolkit.devinfo.ui.info.camera.CameraType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.connectivity.ConnectivityType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.device.DeviceType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.hardware.HardwareType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.network.NetworkType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.sensor.SensorType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.software.SoftwareType
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

internal interface DevInfoTypes {
  @Provides
  @IntoSet
  fun provideBasicDeviceInfo(bind: DeviceType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideHardwareInfo(bind: HardwareType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideNetworkInfo(bind: NetworkType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideSoftwareInfo(bind: SoftwareType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideCameraInfo(bind: CameraType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideConnectivityInfo(bind: ConnectivityType): DevInfoType = bind

  @Provides
  @IntoSet
  fun provideSensorInfo(bind: SensorType): DevInfoType = bind
}
