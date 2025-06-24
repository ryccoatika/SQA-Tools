package com.ryccoatika.sqatools.devinfo.ui.info

import com.ryccoatika.sqatools.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatools.devinfo.ui.info.advancedevice.AdvanceDevice
import com.ryccoatika.sqatools.devinfo.ui.info.advancedevice.AdvanceDeviceType
import com.ryccoatika.sqatools.devinfo.ui.info.basicdevice.BasicDevice
import com.ryccoatika.sqatools.devinfo.ui.info.basicdevice.BasicDeviceType
import com.ryccoatika.sqatools.devinfo.ui.info.camera.Camera
import com.ryccoatika.sqatools.devinfo.ui.info.camera.CameraType
import com.ryccoatika.sqatools.devinfo.ui.info.connectivity.Connectivity
import com.ryccoatika.sqatools.devinfo.ui.info.connectivity.ConnectivityType
import com.ryccoatika.sqatools.devinfo.ui.info.hardware.Hardware
import com.ryccoatika.sqatools.devinfo.ui.info.hardware.HardwareType
import com.ryccoatika.sqatools.devinfo.ui.info.network.Network
import com.ryccoatika.sqatools.devinfo.ui.info.network.NetworkType
import com.ryccoatika.sqatools.devinfo.ui.info.sensor.Sensor
import com.ryccoatika.sqatools.devinfo.ui.info.sensor.SensorType
import com.ryccoatika.sqatools.devinfo.ui.info.software.Software
import com.ryccoatika.sqatools.devinfo.ui.info.software.SoftwareType
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

@DevInfoScope
@Inject
internal class DevInfoScreens(
  val basicDevice: BasicDevice,
  val hardware: Hardware,
  val network: Network,
  val software: Software,
  val camera: Camera,
  val connectivity: Connectivity,
  val sensor: Sensor,
  val advanceDevice: AdvanceDevice,
)

internal interface DevInfoTypes {
  @Provides
  @IntoSet
  fun provideBasicDeviceInfo(bind: BasicDeviceType): DevInfoType = bind

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

  @Provides
  @IntoSet
  fun provideAdvanceDeviceInfo(bind: AdvanceDeviceType): DevInfoType = bind
}
