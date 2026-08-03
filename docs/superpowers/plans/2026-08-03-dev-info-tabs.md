# Dev-Info Complete All Tabs — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fill the six stub tabs in `features/dev-info` (Hardware, Network, Software, Camera, Connectivity, Sensor) with detailed device information, following the existing Device tab pattern.

**Architecture:** Each tab = `Get{X}Info` usecase (gathers `List<Item>` off-main) + a tiny `{X}ViewModel` over a shared `InfoViewModel` base + `{X}` composable rendering `ItemComposer`. Data gathering lives in focused per-domain `*InfoUtils`. Permission-gated fields render a placeholder + Grant button that requests the permission on tap and refreshes.

**Tech Stack:** Kotlin, Jetpack Compose (Material3), kotlin-inject (tatarka) DI, coroutines, Android device APIs (Camera2, SensorManager, TelephonyManager, WifiManager, EGL14), JNI (`NativeHelper`).

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 37. Guard any API > 24 with `Build.VERSION.SDK_INT` checks.
- No unit-test infra exists; **verification = `./gradlew :features:dev-info:compileDebugKotlin` then `:assembleDebug`, plus manual on-device smoke.** Do NOT add test dependencies.
- Every getter is defensive: wrap device-API reads in `runCatching`/try, return `"-"` / `"Unknown"` / null on failure. One unsupported API must never blank a tab.
- Usecases gather on `withContext(Dispatchers.IO)`.
- Reuse existing helpers: `NativeHelper.getProp(key)`, `NativeHelper.execute(cmd)`, `String?.or(default)` (`com.ryccoatika.sqatoolkit.common.utils.or` — returns default if null/blank).
- New field labels use `RawTextItem(label = context.getString(R.string.x), value)` resolved in the usecase (inject `Context`). Existing enum labels (Device/System/DRM, already shipped) stay unchanged.
- kotlin-inject: `@Inject` on constructors; register each `DevInfoType` `@IntoSet` in `DevInfoScreens.DevInfoTypes` (already present for all 7). Utils/usecases are constructor-injected; `Context` is already provided in the graph (see `DeviceInfoUtils`).
- Indentation: 2 spaces (match existing files).

---

### Task 1: Model additions

**Files:**
- Modify: `features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/core/model/Item.kt`

**Interfaces:**
- Produces: `PermissionItem(label: String, permission: String, value: String)`, `RawTextItem(label: String, value: String)`, `ExpandableGroupItem(title: String, summary: String, items: List<Item>)`, and `GroupItem` gains `rawTitle: String? = null`.

- [ ] **Step 1: Add the new model types**

Append to `Item.kt` (after `ElapsedTimeItem`, and edit `GroupItem`):

```kotlin
internal data class RawTextItem(
  val label: String,
  val value: String,
) : Item

internal data class PermissionItem(
  val label: String,
  val permission: String,
  val value: String,
) : Item

internal data class ExpandableGroupItem(
  val title: String,
  val summary: String,
  val items: List<Item>,
) : Item

internal data class GroupItem(
  val label: Label? = null,
  val rawTitle: String? = null,
  val items: List<Item>,
) : Item
```

Note: `GroupItem`'s existing call sites pass `label` positionally (`GroupItem(null, basicItems)` / `GroupItem(Label.DRM, listOf(...))`). Adding `rawTitle` between `label` and `items` breaks those. Fix them in this step: change existing call sites in `GetDeviceInfo.kt` to named `items =` OR keep param order `label, rawTitle, items` and update the positional calls. Use named args at call sites: `GroupItem(items = basicItems)`, `GroupItem(label = Label.DRM, items = listOf(...))`.

- [ ] **Step 2: Update existing GroupItem call sites**

In `GetDeviceInfo.kt`, change `GroupItem(null, basicItems)` → `GroupItem(items = basicItems)` (repeat for manufacturerItems, saleItems, systemItems), and `GroupItem(Label.DRM, listOf(...))` → `GroupItem(label = Label.DRM, items = listOf(...))`. In `ItemGroupComposer.kt` preview, `GroupItem(label = Label.Device, items = listOf(...))`.

- [ ] **Step 3: Compile**

Run: `./gradlew :features:dev-info:compileDebugKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/core/model/Item.kt features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/core/usecase/GetDeviceInfo.kt features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/common/ItemGroupComposer.kt
git commit -m "feat(dev-info): add RawText, Permission, ExpandableGroup item models"
```

---

### Task 2: Shared strings + common values

**Files:**
- Modify: `features/dev-info/src/main/res/values/strings.xml`

**Interfaces:**
- Produces: string resources referenced by all later tasks. Full list below.

- [ ] **Step 1: Add strings**

Add inside `<resources>`:

```xml
  <!-- common -->
  <string name="di_text_permission_required">Permission required</string>
  <string name="di_text_grant">Grant</string>
  <string name="di_text_not_available">Not available</string>
  <string name="di_text_yes">Yes</string>
  <string name="di_text_no">No</string>

  <!-- software -->
  <string name="di_group_android">Android</string>
  <string name="di_group_system">System</string>
  <string name="di_label_android_version">Android Version</string>
  <string name="di_label_api_level">API Level</string>
  <string name="di_label_codename">Codename</string>
  <string name="di_label_build_number">Build Number</string>
  <string name="di_label_build_id">Build ID</string>
  <string name="di_label_build_type">Build Type</string>
  <string name="di_label_build_tags">Build Tags</string>
  <string name="di_label_fingerprint">Fingerprint</string>
  <string name="di_label_runtime">Runtime</string>
  <string name="di_label_root">Root Access</string>
  <string name="di_label_ab_update">Seamless (A/B) Updates</string>
  <string name="di_label_treble">Project Treble</string>
  <string name="di_label_play_services">Play Services</string>
  <string name="di_text_detected">Detected</string>
  <string name="di_text_not_detected">Not Detected</string>
  <string name="di_text_not_installed">Not Installed</string>

  <!-- hardware -->
  <string name="di_group_processor">Processor</string>
  <string name="di_group_gpu">GPU</string>
  <string name="di_group_memory">Memory</string>
  <string name="di_group_storage">Storage</string>
  <string name="di_group_display">Display</string>
  <string name="di_group_battery">Battery</string>
  <string name="di_label_chipset">Chipset</string>
  <string name="di_label_architecture">Architecture</string>
  <string name="di_label_abis">Supported ABIs</string>
  <string name="di_label_cores">CPU Cores</string>
  <string name="di_label_governor">Governor</string>
  <string name="di_label_cpu_freq">Clock Range</string>
  <string name="di_label_gpu_vendor">Vendor</string>
  <string name="di_label_gpu_renderer">Renderer</string>
  <string name="di_label_gpu_version">Version</string>
  <string name="di_label_ram_total">Total RAM</string>
  <string name="di_label_ram_available">Available RAM</string>
  <string name="di_label_ram_threshold">Low Memory Threshold</string>
  <string name="di_label_memory_class">Memory Class</string>
  <string name="di_label_large_memory_class">Large Memory Class</string>
  <string name="di_label_storage_internal">Internal (Data)</string>
  <string name="di_label_storage_system">System</string>
  <string name="di_label_resolution">Resolution</string>
  <string name="di_label_density">Density</string>
  <string name="di_label_refresh_rate">Refresh Rate</string>
  <string name="di_label_screen_size">Screen Size</string>
  <string name="di_label_hdr">HDR</string>
  <string name="di_label_battery_technology">Technology</string>
  <string name="di_label_battery_health">Health</string>
  <string name="di_label_battery_status">Status</string>
  <string name="di_label_battery_capacity">Capacity</string>
  <string name="di_label_battery_voltage">Voltage</string>
  <string name="di_label_battery_temperature">Temperature</string>

  <!-- camera -->
  <string name="di_camera_back">Back Camera</string>
  <string name="di_camera_front">Front Camera</string>
  <string name="di_camera_external">External Camera</string>
  <string name="di_label_facing">Facing</string>
  <string name="di_label_hardware_level">Hardware Level</string>
  <string name="di_label_megapixels">Megapixels</string>
  <string name="di_label_sensor_size">Sensor Size</string>
  <string name="di_label_focal_lengths">Focal Lengths</string>
  <string name="di_label_apertures">Apertures</string>
  <string name="di_label_flash">Flash</string>
  <string name="di_label_max_zoom">Max Digital Zoom</string>
  <string name="di_label_iso_range">ISO Range</string>
  <string name="di_label_max_photo">Max Photo</string>
  <string name="di_label_max_video">Max Video</string>
  <string name="di_label_fps_ranges">FPS Ranges</string>
  <string name="di_label_ois">Optical Stabilization</string>
  <string name="di_label_raw">RAW Support</string>
  <string name="di_label_af_modes">AF Modes</string>

  <!-- network -->
  <string name="di_group_telephony">SIM &amp; Network</string>
  <string name="di_label_network_operator">Network Operator</string>
  <string name="di_label_sim_operator">SIM Operator</string>
  <string name="di_label_network_mccmnc">Network MCC/MNC</string>
  <string name="di_label_sim_mccmnc">SIM MCC/MNC</string>
  <string name="di_label_sim_country">SIM Country</string>
  <string name="di_label_network_country">Network Country</string>
  <string name="di_label_phone_type">Phone Type</string>
  <string name="di_label_sim_state">SIM State</string>
  <string name="di_label_sim_count">SIM Count</string>
  <string name="di_label_roaming">Roaming</string>
  <string name="di_label_data_network_type">Data Network Type</string>
  <string name="di_label_imei">IMEI / MEID</string>
  <string name="di_label_phone_number">Phone Number</string>

  <!-- connectivity -->
  <string name="di_group_wifi">Wi-Fi</string>
  <string name="di_group_bluetooth">Bluetooth</string>
  <string name="di_group_other">Other</string>
  <string name="di_label_wifi_enabled">Enabled</string>
  <string name="di_label_wifi_5ghz">5 GHz</string>
  <string name="di_label_wifi_6ghz">6 GHz</string>
  <string name="di_label_wifi_aware">Wi-Fi Aware</string>
  <string name="di_label_wifi_direct">Wi-Fi Direct</string>
  <string name="di_label_wifi_mac">MAC Address</string>
  <string name="di_label_wifi_ssid">SSID</string>
  <string name="di_label_wifi_bssid">BSSID</string>
  <string name="di_label_wifi_speed">Link Speed</string>
  <string name="di_label_wifi_frequency">Frequency</string>
  <string name="di_label_wifi_rssi">Signal (RSSI)</string>
  <string name="di_label_wifi_ip">IP Address</string>
  <string name="di_label_bt_supported">Supported</string>
  <string name="di_label_bt_le">Bluetooth LE</string>
  <string name="di_label_bt_advertiser">LE Advertiser</string>
  <string name="di_label_bt_name">Adapter Name</string>
  <string name="di_label_nfc">NFC</string>
  <string name="di_label_usb_host">USB Host</string>
  <string name="di_label_ethernet">Ethernet</string>

  <!-- sensor -->
  <string name="di_label_sensor_vendor">Vendor</string>
  <string name="di_label_sensor_type">Type</string>
  <string name="di_label_sensor_version">Version</string>
  <string name="di_label_sensor_power">Power</string>
  <string name="di_label_sensor_resolution">Resolution</string>
  <string name="di_label_sensor_range">Max Range</string>
  <string name="di_label_sensor_min_delay">Min Delay</string>
  <string name="di_label_sensor_max_delay">Max Delay</string>
  <string name="di_label_sensor_reporting">Reporting Mode</string>
  <string name="di_label_sensor_wakeup">Wake-up Sensor</string>
```

- [ ] **Step 2: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add features/dev-info/src/main/res/values/strings.xml
git commit -m "feat(dev-info): add strings for all tab fields"
```

---

### Task 3: Permission requester + new composers + dispatch

**Files:**
- Create: `features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/common/utils/DevInfoCompositionLocal.kt` (add to existing file)
- Create: `features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/common/ItemPermissionComposer.kt`
- Create: `features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/common/ItemExpandableGroupComposer.kt`
- Modify: `.../ui/common/ItemComposer.kt`
- Modify: `.../ui/common/ItemGroupComposer.kt`

**Interfaces:**
- Consumes: `RawTextItem`, `PermissionItem`, `ExpandableGroupItem`, `GroupItem.rawTitle`, existing `ListItemText`.
- Produces: `LocalPermissionRequester: ProvidableCompositionLocal<(String) -> Unit>`, composers `ItemPermissionComposer`, `ItemExpandableGroupComposer`; `ItemComposer` dispatches the three new item types; `ItemGroupComposer` honors `rawTitle`.

- [ ] **Step 1: Add LocalPermissionRequester**

Append to `DevInfoCompositionLocal.kt`:

```kotlin
internal val LocalPermissionRequester = staticCompositionLocalOf<(String) -> Unit> {
  {}
}
```

- [ ] **Step 2: RawTextItem dispatch + Permission composer**

Create `ItemPermissionComposer.kt`:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalPermissionRequester
import androidx.compose.ui.res.stringResource

@Composable
internal fun ItemPermissionComposer(
  item: PermissionItem,
  modifier: Modifier = Modifier,
) {
  val requester = LocalPermissionRequester.current
  ListItem(
    modifier = modifier,
    headlineContent = { Text(item.label) },
    supportingContent = { Text(item.value) },
    trailingContent = {
      TextButton(onClick = { requester(item.permission) }) {
        Text(stringResource(R.string.di_text_grant))
      }
    },
  )
}
```

- [ ] **Step 3: Expandable group composer**

Create `ItemExpandableGroupComposer.kt`:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem

@Composable
internal fun ItemExpandableGroupComposer(
  item: ExpandableGroupItem,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  Column(modifier = modifier) {
    ListItem(
      modifier = Modifier.clickable { expanded = !expanded },
      headlineContent = { Text(item.title) },
      supportingContent = { Text(item.summary) },
      trailingContent = {
        Icon(
          imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
          contentDescription = null,
        )
      },
    )
    AnimatedVisibility(visible = expanded) {
      ItemComposer(items = item.items)
    }
  }
}
```

- [ ] **Step 4: Wire dispatch in ItemComposer**

In `ItemComposer.kt`, add imports for `RawTextItem`, `PermissionItem`, `ExpandableGroupItem`, and add branches to the `when (item)`:

```kotlin
    is RawTextItem -> ListItemText(modifier, label = item.label, value = item.value)
    is PermissionItem -> ItemPermissionComposer(item, modifier)
    is ExpandableGroupItem -> ItemExpandableGroupComposer(item, modifier)
```

(Match the existing branch arg style — check whether `ItemComposer`'s per-item render passes `modifier`; if branches call e.g. `ItemTextComposer(item)`, mirror that and drop the extra `modifier` arg. Verify `ListItemText`'s parameter order: `label, value, modifier` — pass named.)

- [ ] **Step 5: GroupItem rawTitle in ItemGroupComposer**

In `ItemGroupComposer.kt`, replace the header block:

```kotlin
      val title = item.rawTitle ?: item.label?.let { textCreator.itemLabel(it) }
      title?.let { Text(it) }
```

- [ ] **Step 6: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL. Fix `ListItemText`/branch signatures if the compiler complains.

```bash
git add features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/common/
git commit -m "feat(dev-info): permission requester local + permission/expandable composers"
```

---

### Task 4: Shared InfoViewModel + InfoViewState; migrate Device

**Files:**
- Create: `.../ui/info/common/InfoViewState.kt`
- Create: `.../ui/info/common/InfoViewModel.kt`
- Modify: `.../ui/info/device/DeviceViewModel.kt`
- Delete: `.../ui/info/device/DeviceViewState.kt` (replaced by shared)
- Modify: `.../ui/info/device/Device.kt` (use `InfoViewState`)

**Interfaces:**
- Produces: `InfoViewState(items, isLoading)` with `Empty`; `abstract InfoViewModel : ViewModel` exposing `state: StateFlow<InfoViewState>`, `fun refresh()`, and `protected abstract suspend fun load(): List<Item>`.
- Consumes: `ObservableLoadingCounter` (`com.ryccoatika.sqatoolkit.common.utils`), `Item`.

- [ ] **Step 1: InfoViewState**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.info.common

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item

@Immutable
internal data class InfoViewState(
  val items: List<Item>,
  val isLoading: Boolean,
) {
  companion object {
    val Empty = InfoViewState(items = emptyList(), isLoading = true)
  }
}
```

- [ ] **Step 2: InfoViewModel base**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.info.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal abstract class InfoViewModel : ViewModel() {
  private val items = MutableStateFlow<List<Item>>(emptyList())
  private val loadingCounter = ObservableLoadingCounter()

  val state: StateFlow<InfoViewState> = combine(
    items,
    loadingCounter.observable,
    ::InfoViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = InfoViewState.Empty,
  )

  init {
    refresh()
  }

  fun refresh() {
    viewModelScope.launch {
      loadingCounter.addLoader()
      items.value = load()
      loadingCounter.removeLoader()
    }
  }

  protected abstract suspend fun load(): List<Item>
}
```

Note: `init { refresh() }` in the base runs before the subclass constructor finishes assigning its `useCase` field. To avoid a null-during-init race, subclasses must pass the usecase to the base or the base must not call `load()` until subclass is ready. Simplest safe pattern: remove `init { refresh() }` from the base and have each subclass call `refresh()` in its own `init`. Use that — delete the base `init` block; document that subclasses call `refresh()` in `init`.

- [ ] **Step 3: Migrate DeviceViewModel**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.info.device

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetDeviceInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeviceViewModel(
  private val getDeviceInfo: GetDeviceInfo,
) : InfoViewModel() {
  init { refresh() }
  override suspend fun load(): List<Item> = getDeviceInfo.executeSync(Unit)
}
```

- [ ] **Step 4: Update Device.kt + delete DeviceViewState.kt**

In `Device.kt`, change the `state: DeviceViewState` composable param type to `InfoViewState` (import `com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewState`). Delete `DeviceViewState.kt`.

- [ ] **Step 5: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ui/info/
git commit -m "refactor(dev-info): shared InfoViewModel/InfoViewState base, migrate Device"
```

---

### Task 5: Software tab (absorbs System + DRM) + trim Device

**Files:**
- Create: `.../core/utils/SoftwareInfoUtils.kt`
- Create: `.../core/usecase/GetSoftwareInfo.kt`
- Create: `.../ui/info/software/SoftwareViewModel.kt`
- Modify: `.../ui/info/software/Software.kt`
- Modify: `.../core/utils/DeviceInfoUtils.kt` (remove moved getters)
- Modify: `.../core/usecase/GetDeviceInfo.kt` (remove System + DRM groups)

**Interfaces:**
- Consumes: `NativeHelper`, `Context`, `DrmInfo`, `RawTextItem`, `GroupItem`.
- Produces: `SoftwareInfoUtils` getters; `GetSoftwareInfo : ResultInteractor<Unit, List<Item>>`.

- [ ] **Step 1: Move getters into SoftwareInfoUtils**

Create `SoftwareInfoUtils.kt`; MOVE from `DeviceInfoUtils` (cut, don't copy): `getAndroidName(apiLevel)` stays in `DeviceInfoUtils` (Device card uses it) — instead expose a thin wrapper; move `getDeviceReleaseAndroidVersion`, `getAndroidUI`, `getSecurityPatch`, `getJavaVMVersion`, `getKernelVersion`, `getOpenGLESVersion`, `getVulkanVersion`, `getSELinux`, `getSystemUptime`, `getDrmInfo` + `DrmInfo` + `WIDEVINE_UUID`. `getDeviceReleaseAndroidVersion` calls `getAndroidName(apiLevel)` — inject `DeviceInfoUtils` into `SoftwareInfoUtils`, or duplicate the small name map. Inject `DeviceInfoUtils`:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.media.MediaDrm
import android.os.Build
import android.os.SystemClock
import com.ryccoatika.sqatoolkit.common.utils.or
import java.time.Instant
import java.util.UUID
import me.tatarka.inject.annotations.Inject

@Inject
internal class SoftwareInfoUtils(
  private val context: Context,
  private val deviceInfoUtils: DeviceInfoUtils,
) {
  fun getDeviceReleaseAndroidVersion(): String {
    val apiLevel = NativeHelper.getProp("ro.product.first_api_level").or("0").toIntOrNull() ?: 0
    return deviceInfoUtils.getAndroidName(apiLevel)
  }

  fun getAndroidUI(): String { /* moved verbatim */ return "" }
  fun getSecurityPatch(): Instant? { /* moved verbatim */ return null }
  fun getJavaVMVersion(): String = System.getProperty("java.vm.version").or("-")
  fun getKernelVersion(): String = System.getProperty("os.version").or("-")
  fun getOpenGLESVersion(): String { /* moved verbatim */ return "-" }
  fun getVulkanVersion(): String { /* moved verbatim */ return "-" }
  fun getSELinux(): String = NativeHelper.execute("getenforce").or("-")
  fun getSystemUptime(): Instant? = runCatching { Instant.now().minusMillis(SystemClock.uptimeMillis()) }.getOrNull()

  fun getRuntime(): String {
    val name = System.getProperty("java.vm.name").or("ART")
    val ver = System.getProperty("java.vm.version").or("-")
    return "$name $ver"
  }

  fun isRooted(): Boolean {
    val paths = listOf(
      "/system/bin/su", "/system/xbin/su", "/sbin/su",
      "/system/app/Superuser.apk", "/su/bin/su", "/vendor/bin/su",
    )
    return paths.any { runCatching { java.io.File(it).exists() }.getOrDefault(false) } ||
      NativeHelper.getProp("ro.debuggable") == "1" && NativeHelper.getProp("ro.secure") == "0"
  }

  fun isAbUpdate(): Boolean = NativeHelper.getProp("ro.build.ab_update") == "true"

  fun isTreble(): Boolean = NativeHelper.getProp("ro.treble.enabled") == "true"

  fun getPlayServicesVersion(): String? = runCatching {
    context.packageManager.getPackageInfo("com.google.android.gms", 0).versionName
  }.getOrNull()

  fun getDrmInfo(): DrmInfo? { /* moved verbatim, incl. WIDEVINE_UUID + close/release */ return null }
}
```

Copy the real bodies of `getAndroidUI`, `getSecurityPatch`, `getOpenGLESVersion`, `getVulkanVersion`, `getDrmInfo`, `DrmInfo`, and `WIDEVINE_UUID` verbatim from the current `DeviceInfoUtils` (see git `575255c`). Delete those methods + `DrmInfo` + `WIDEVINE_UUID` from `DeviceInfoUtils`, and remove now-unused imports there (`MediaDrm`, `PackageManager`, `SystemClock`, `UUID`) — keep imports still used by remaining `DeviceInfoUtils` code.

- [ ] **Step 2: GetSoftwareInfo usecase**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import android.content.Context
import android.os.Build
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
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
    }

    val systemItems = buildList {
      add(RawTextItem(s(R.string.di_label_root),
        if (softwareInfoUtils.isRooted()) s(R.string.di_text_detected) else s(R.string.di_text_not_detected)))
      add(RawTextItem(s(R.string.di_label_ab_update), yn(softwareInfoUtils.isAbUpdate())))
      add(RawTextItem(s(R.string.di_label_treble), yn(softwareInfoUtils.isTreble())))
      add(RawTextItem(s(R.string.di_label_play_services),
        softwareInfoUtils.getPlayServicesVersion() ?: s(R.string.di_text_not_installed)))
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
```

Note: reuses existing `di_label_java_vm`, `di_label_kernel`, `di_label_baseband`, `di_label_bootloader`, `di_label_opengl_es`, `di_label_vulkan`, `di_label_selinux`, `di_label_drm*` strings from commit `575255c`.

- [ ] **Step 3: SoftwareViewModel + Software screen**

`SoftwareViewModel.kt`:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSoftwareInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class SoftwareViewModel(
  private val getSoftwareInfo: GetSoftwareInfo,
) : InfoViewModel() {
  init { refresh() }
  override suspend fun load(): List<Item> = getSoftwareInfo.executeSync(Unit)
}
```

Rewrite `Software.kt` following `Device.kt` exactly (three overloads: injected factory → viewModel → state), keeping the existing `SoftwareType` class unchanged:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.common.ItemComposer
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewState
import me.tatarka.inject.annotations.Inject

internal typealias Software = @Composable () -> Unit

@Inject
@Composable
internal fun Software(
  viewModelFactory: () -> SoftwareViewModel,
) {
  Software(viewModel = viewModel(factory = viewModelFactory))
}

@Composable
internal fun Software(viewModel: SoftwareViewModel) {
  val viewState by viewModel.state.collectAsState()
  Software(state = viewState)
}

@Composable
internal fun Software(state: InfoViewState) {
  ItemComposer(
    items = state.items,
    modifier = Modifier
      .padding(vertical = 8.dp, horizontal = 16.dp)
      .verticalScroll(rememberScrollState())
      .fillMaxSize(),
  )
}

@Inject
internal class SoftwareType : DevInfoType {
  override val id: String get() = DevInfoType.SOFTWARE_ID
  override val order: Int get() = 4
  override val featureTitle: Int get() = R.string.di_text_software
}
```

- [ ] **Step 4: Trim GetDeviceInfo**

In `GetDeviceInfo.kt` remove the entire `systemItems` block, the `drmGroup` block, and their entries in the final list — leaving `cardItem`, Basic, Manufacturer, Sale. Remove now-unused imports (`ElapsedTimeItem`, and if unused after trim, `DateItem` stays — Manufacturer uses it). Keep `listOf(...)`.

- [ ] **Step 5: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): Software tab (Android/System/DRM), trim Device"
```

---

### Task 6: Hardware tab — CPU/RAM/Storage/Display/Battery

**Files:**
- Create: `.../core/utils/HardwareInfoUtils.kt`
- Create: `.../core/usecase/GetHardwareInfo.kt`
- Create: `.../ui/info/hardware/HardwareViewModel.kt`
- Modify: `.../ui/info/hardware/Hardware.kt`

**Interfaces:**
- Produces: `HardwareInfoUtils` (CPU/RAM/storage/display/battery getters; GPU added in Task 7), `GetHardwareInfo`.
- Consumes: `Context`, `RawTextItem`, `GroupItem`.

- [ ] **Step 1: HardwareInfoUtils (no GPU yet)**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ryccoatika.sqatoolkit.common.utils.or
import java.io.File
import java.util.Locale
import me.tatarka.inject.annotations.Inject
import kotlin.math.sqrt

@Inject
internal class HardwareInfoUtils(
  private val context: Context,
) {
  fun getChipset(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val mfr = Build.SOC_MANUFACTURER
      val model = Build.SOC_MODEL
      if (mfr.isNotBlank() || model.isNotBlank()) return "$mfr $model".trim()
    }
    return NativeHelper.getProp("ro.board.platform").or(Build.HARDWARE).or("-")
  }

  fun getArchitecture(): String = Build.SUPPORTED_ABIS.firstOrNull().or("-")

  fun getSupportedAbis(): String = Build.SUPPORTED_ABIS.joinToString(", ").or("-")

  fun getCoreCount(): Int = Runtime.getRuntime().availableProcessors()

  fun getCpuGovernor(): String =
    readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").or("-")

  fun getCpuFreqRange(): String {
    val min = readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq").toLongOrNull()
    val max = readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq").toLongOrNull()
    if (min == null || max == null) return "-"
    return "%.1f - %.1f GHz".format(Locale.US, min / 1_000_000.0, max / 1_000_000.0)
  }

  fun getRamTotal(): String = formatBytes(memInfo().totalMem)
  fun getRamAvailable(): String = formatBytes(memInfo().availMem)
  fun getRamThreshold(): String = formatBytes(memInfo().threshold)
  fun getMemoryClass(): String = "${activityManager().memoryClass} MB"
  fun getLargeMemoryClass(): String = "${activityManager().largeMemoryClass} MB"

  fun getInternalStorage(): String = statFsString(Environment.getDataDirectory().path)
  fun getSystemStorage(): String = statFsString(Environment.getRootDirectory().path)

  fun getResolution(): String {
    val m = displayMetrics()
    return "${m.widthPixels} x ${m.heightPixels}"
  }

  fun getDensity(): String {
    val m = displayMetrics()
    return "${m.densityDpi} dpi (${densityBucket(m.densityDpi)})"
  }

  fun getRefreshRate(): String {
    val rate = runCatching {
      (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate
    }.getOrNull() ?: return "-"
    return "%.0f Hz".format(Locale.US, rate)
  }

  fun getScreenSize(): String {
    val m = displayMetrics()
    val x = m.widthPixels / m.xdpi
    val y = m.heightPixels / m.ydpi
    val inches = sqrt((x * x + y * y).toDouble())
    return "%.1f\"".format(Locale.US, inches)
  }

  fun isHdr(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    return runCatching {
      (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.hdrCapabilities?.supportedHdrTypes?.isNotEmpty() == true
    }.getOrDefault(false)
  }

  // battery
  private fun batteryIntent(): Intent? =
    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

  fun getBatteryTechnology(): String =
    batteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).or("-")

  fun getBatteryHealth(): String = when (batteryIntent()?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
    BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
    else -> "-"
  }

  fun getBatteryStatus(): String = when (batteryIntent()?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
    BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
    BatteryManager.BATTERY_STATUS_FULL -> "Full"
    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
    else -> "-"
  }

  fun getBatteryCapacity(): String {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    return if (pct in 0..100) "$pct%" else "-"
  }

  fun getBatteryVoltage(): String {
    val mv = batteryIntent()?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
    return if (mv > 0) "%.3f V".format(Locale.US, mv / 1000.0) else "-"
  }

  fun getBatteryTemperature(): String {
    val t = batteryIntent()?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
    return if (t > 0) "%.1f °C".format(Locale.US, t / 10.0) else "-"
  }

  private fun memInfo(): ActivityManager.MemoryInfo {
    val mi = ActivityManager.MemoryInfo()
    activityManager().getMemoryInfo(mi)
    return mi
  }

  private fun activityManager() = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  @Suppress("DEPRECATION")
  private fun displayMetrics(): DisplayMetrics {
    val m = DisplayMetrics()
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(m)
    return m
  }

  private fun statFsString(path: String): String = runCatching {
    val sf = StatFs(path)
    val total = sf.blockCountLong * sf.blockSizeLong
    val free = sf.availableBlocksLong * sf.blockSizeLong
    "${formatBytes(free)} free / ${formatBytes(total)}"
  }.getOrDefault("-")

  private fun readFile(path: String): String =
    runCatching { File(path).readText().trim() }.getOrDefault("")

  private fun densityBucket(dpi: Int): String = when {
    dpi <= 120 -> "ldpi"
    dpi <= 160 -> "mdpi"
    dpi <= 240 -> "hdpi"
    dpi <= 320 -> "xhdpi"
    dpi <= 480 -> "xxhdpi"
    else -> "xxxhdpi"
  }

  private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "-"
    val gb = bytes / 1_073_741_824.0
    if (gb >= 1) return "%.2f GB".format(Locale.US, gb)
    val mb = bytes / 1_048_576.0
    return "%.0f MB".format(Locale.US, mb)
  }
}
```

- [ ] **Step 2: GetHardwareInfo usecase**

```kotlin
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
      GroupItem(rawTitle = s(R.string.di_group_processor), items = listOf(
        RawTextItem(s(R.string.di_label_chipset), hw.getChipset()),
        RawTextItem(s(R.string.di_label_architecture), hw.getArchitecture()),
        RawTextItem(s(R.string.di_label_abis), hw.getSupportedAbis()),
        RawTextItem(s(R.string.di_label_cores), hw.getCoreCount().toString()),
        RawTextItem(s(R.string.di_label_governor), hw.getCpuGovernor()),
        RawTextItem(s(R.string.di_label_cpu_freq), hw.getCpuFreqRange()),
      )),
      GroupItem(rawTitle = s(R.string.di_group_gpu), items = hw.getGpuItems(context)),
      GroupItem(rawTitle = s(R.string.di_group_memory), items = listOf(
        RawTextItem(s(R.string.di_label_ram_total), hw.getRamTotal()),
        RawTextItem(s(R.string.di_label_ram_available), hw.getRamAvailable()),
        RawTextItem(s(R.string.di_label_ram_threshold), hw.getRamThreshold()),
        RawTextItem(s(R.string.di_label_memory_class), hw.getMemoryClass()),
        RawTextItem(s(R.string.di_label_large_memory_class), hw.getLargeMemoryClass()),
      )),
      GroupItem(rawTitle = s(R.string.di_group_storage), items = listOf(
        RawTextItem(s(R.string.di_label_storage_internal), hw.getInternalStorage()),
        RawTextItem(s(R.string.di_label_storage_system), hw.getSystemStorage()),
      )),
      GroupItem(rawTitle = s(R.string.di_group_display), items = listOf(
        RawTextItem(s(R.string.di_label_resolution), hw.getResolution()),
        RawTextItem(s(R.string.di_label_density), hw.getDensity()),
        RawTextItem(s(R.string.di_label_refresh_rate), hw.getRefreshRate()),
        RawTextItem(s(R.string.di_label_screen_size), hw.getScreenSize()),
        RawTextItem(s(R.string.di_label_hdr), if (hw.isHdr()) s(R.string.di_text_yes) else s(R.string.di_text_no)),
      )),
      GroupItem(rawTitle = s(R.string.di_group_battery), items = listOf(
        RawTextItem(s(R.string.di_label_battery_technology), hw.getBatteryTechnology()),
        RawTextItem(s(R.string.di_label_battery_health), hw.getBatteryHealth()),
        RawTextItem(s(R.string.di_label_battery_status), hw.getBatteryStatus()),
        RawTextItem(s(R.string.di_label_battery_capacity), hw.getBatteryCapacity()),
        RawTextItem(s(R.string.di_label_battery_voltage), hw.getBatteryVoltage()),
        RawTextItem(s(R.string.di_label_battery_temperature), hw.getBatteryTemperature()),
      )),
    )
  }
}
```

Note: `getGpuItems` is added in Task 7. For this task, temporarily replace the GPU group line with `emptyList()` so it compiles, then restore in Task 7. (Or implement Task 7 before compiling Task 6 — either order; commit at Task 7.)

- [ ] **Step 3: HardwareViewModel + Hardware screen**

`HardwareViewModel.kt` (mirror SoftwareViewModel, usecase `GetHardwareInfo`). `Hardware.kt`: mirror `Software.kt` exactly, keeping `HardwareType` (order 2, `R.string.di_text_hardware`), typealias `Hardware`.

- [ ] **Step 4: Defer compile+commit to Task 7** (GPU dependency). Proceed to Task 7.

---

### Task 7: Hardware GPU via offscreen EGL

**Files:**
- Modify: `.../core/utils/HardwareInfoUtils.kt`

**Interfaces:**
- Produces: `HardwareInfoUtils.getGpuItems(context: Context): List<Item>` returning three `RawTextItem`s (vendor, renderer, version).

- [ ] **Step 1: Add EGL GPU query**

Add to `HardwareInfoUtils`:

```kotlin
  fun getGpuItems(context: Context): List<Item> {
    val info = queryGpu()
    return listOf(
      RawTextItem(context.getString(R.string.di_label_gpu_vendor), info?.vendor.or("-")),
      RawTextItem(context.getString(R.string.di_label_gpu_renderer), info?.renderer.or("-")),
      RawTextItem(context.getString(R.string.di_label_gpu_version), info?.version.or("-")),
    )
  }

  private data class GpuStrings(val vendor: String, val renderer: String, val version: String)

  private fun queryGpu(): GpuStrings? = runCatching {
    val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    val ver = IntArray(2)
    EGL14.eglInitialize(display, ver, 0, ver, 1)
    val cfgAttrs = intArrayOf(
      EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
      EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
      EGL14.EGL_NONE,
    )
    val cfgs = arrayOfNulls<EGLConfig>(1)
    val numCfg = IntArray(1)
    EGL14.eglChooseConfig(display, cfgAttrs, 0, cfgs, 0, 1, numCfg, 0)
    val ctxAttrs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
    val ctx = EGL14.eglCreateContext(display, cfgs[0], EGL14.EGL_NO_CONTEXT, ctxAttrs, 0)
    val pbAttrs = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
    val surface = EGL14.eglCreatePbufferSurface(display, cfgs[0], pbAttrs, 0)
    EGL14.eglMakeCurrent(display, surface, surface, ctx)
    val result = GpuStrings(
      vendor = GLES20.glGetString(GLES20.GL_VENDOR).or("-"),
      renderer = GLES20.glGetString(GLES20.GL_RENDERER).or("-"),
      version = GLES20.glGetString(GLES20.GL_VERSION).or("-"),
    )
    EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
    EGL14.eglDestroySurface(display, surface)
    EGL14.eglDestroyContext(display, ctx)
    EGL14.eglTerminate(display)
    result
  }.getOrNull()
```

Add imports: `android.opengl.EGL14`, `android.opengl.EGLConfig`, `android.opengl.GLES20`, `com.ryccoatika.sqatoolkit.devinfo.core.model.Item`, `com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem`, `com.ryccoatika.sqatoolkit.devinfo.R`. Restore the real `getGpuItems(context)` call in `GetHardwareInfo`.

- [ ] **Step 2: Compile & commit (Tasks 6+7)**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): Hardware tab (CPU/GPU/RAM/storage/display/battery)"
```

---

### Task 8: Camera tab (Camera2 characteristics, expandable)

**Files:**
- Create: `.../core/utils/CameraInfoUtils.kt`
- Create: `.../core/usecase/GetCameraInfo.kt`
- Create: `.../ui/info/camera/CameraViewModel.kt`
- Modify: `.../ui/info/camera/Camera.kt`

**Interfaces:**
- Produces: `CameraInfoUtils.getCameras(): List<ExpandableGroupItem>`; `GetCameraInfo`.
- Consumes: `Context`, `ExpandableGroupItem`, `RawTextItem`.

- [ ] **Step 1: CameraInfoUtils**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.StreamConfigurationMap
import android.graphics.ImageFormat
import android.media.MediaRecorder
import android.util.Size
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import java.util.Locale
import me.tatarka.inject.annotations.Inject
import kotlin.math.roundToInt

@Inject
internal class CameraInfoUtils(
  private val context: Context,
) {
  private fun s(id: Int) = context.getString(id)

  fun getCameras(): List<ExpandableGroupItem> = runCatching {
    val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    cm.cameraIdList.mapNotNull { id -> buildCamera(cm, id) }
  }.getOrDefault(emptyList())

  private fun buildCamera(cm: CameraManager, id: String): ExpandableGroupItem? = runCatching {
    val c = cm.getCameraCharacteristics(id)
    val facing = when (c.get(CameraCharacteristics.LENS_FACING)) {
      CameraCharacteristics.LENS_FACING_FRONT -> s(R.string.di_camera_front)
      CameraCharacteristics.LENS_FACING_BACK -> s(R.string.di_camera_back)
      else -> s(R.string.di_camera_external)
    }
    val mp = c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)?.let {
      "%.1f MP".format(Locale.US, (it.width.toLong() * it.height) / 1_000_000.0)
    }.or("-")
    val level = hardwareLevel(c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL))
    val map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

    val items = listOf(
      RawTextItem(s(R.string.di_label_facing), facing),
      RawTextItem(s(R.string.di_label_hardware_level), level),
      RawTextItem(s(R.string.di_label_megapixels), mp),
      RawTextItem(s(R.string.di_label_sensor_size), sensorSize(c)),
      RawTextItem(s(R.string.di_label_focal_lengths), floatList(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS), "mm")),
      RawTextItem(s(R.string.di_label_apertures), floatList(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES), "f/")),
      RawTextItem(s(R.string.di_label_flash), yn(c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true)),
      RawTextItem(s(R.string.di_label_max_zoom), c.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?.let { "%.1fx".format(Locale.US, it) }.or("-")),
      RawTextItem(s(R.string.di_label_iso_range), rangeString(c.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE))),
      RawTextItem(s(R.string.di_label_max_photo), maxSize(map, ImageFormat.JPEG)),
      RawTextItem(s(R.string.di_label_max_video), maxVideoSize(map)),
      RawTextItem(s(R.string.di_label_ois), yn(hasOis(c))),
      RawTextItem(s(R.string.di_label_raw), yn(hasRaw(c))),
    )
    ExpandableGroupItem(
      title = "$facing ($id)",
      summary = "$mp · $level",
      items = items,
    )
  }.getOrNull()

  private fun hardwareLevel(v: Int?): String = when (v) {
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "LEGACY"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "LIMITED"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "FULL"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "LEVEL_3"
    else -> "-"
  }

  private fun sensorSize(c: CameraCharacteristics): String {
    val sz = c.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE) ?: return "-"
    return "%.1f x %.1f mm".format(Locale.US, sz.width, sz.height)
  }

  private fun floatList(arr: FloatArray?, suffix: String): String {
    if (arr == null || arr.isEmpty()) return "-"
    return arr.joinToString(", ") {
      if (suffix == "f/") "f/%.1f".format(Locale.US, it) else "%.1f%s".format(Locale.US, it, suffix)
    }
  }

  private fun rangeString(r: android.util.Range<Int>?): String = r?.let { "${it.lower} - ${it.upper}" }.or("-")

  private fun maxSize(map: StreamConfigurationMap?, format: Int): String {
    val sizes = runCatching { map?.getOutputSizes(format) }.getOrNull() ?: return "-"
    val max = sizes.maxByOrNull { it.width.toLong() * it.height } ?: return "-"
    return "${max.width} x ${max.height}"
  }

  private fun maxVideoSize(map: StreamConfigurationMap?): String {
    val sizes = runCatching { map?.getOutputSizes(MediaRecorder::class.java) }.getOrNull() ?: return "-"
    val max = sizes.maxByOrNull { it.width.toLong() * it.height } ?: return "-"
    return "${max.width} x ${max.height}"
  }

  private fun hasOis(c: CameraCharacteristics): Boolean =
    c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.any { it != 0 } == true

  private fun hasRaw(c: CameraCharacteristics): Boolean =
    c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
      ?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) == true

  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)
}
```

- [ ] **Step 2: GetCameraInfo**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.utils.CameraInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetCameraInfo(
  private val cameraInfoUtils: CameraInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    cameraInfoUtils.getCameras()
  }
}
```

- [ ] **Step 3: CameraViewModel + Camera screen**

`CameraViewModel.kt` mirror pattern (usecase `GetCameraInfo`). `Camera.kt` mirror `Software.kt`, keep `CameraType` (order 5, `R.string.di_text_camera`).

- [ ] **Step 4: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): Camera tab (Camera2 characteristics, expandable)"
```

---

### Task 9: Sensor tab (expandable) + order fix

**Files:**
- Create: `.../core/utils/SensorInfoUtils.kt`
- Create: `.../core/usecase/GetSensorInfo.kt`
- Create: `.../ui/info/sensor/SensorViewModel.kt`
- Modify: `.../ui/info/sensor/Sensor.kt` (content + `order = 7`)

**Interfaces:**
- Produces: `SensorInfoUtils.getSensors(): List<ExpandableGroupItem>`; `GetSensorInfo`.

- [ ] **Step 1: SensorInfoUtils**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import java.util.Locale
import me.tatarka.inject.annotations.Inject

@Inject
internal class SensorInfoUtils(
  private val context: Context,
) {
  private fun s(id: Int) = context.getString(id)

  fun getSensors(): List<ExpandableGroupItem> = runCatching {
    val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sm.getSensorList(Sensor.TYPE_ALL).map { sensor ->
      ExpandableGroupItem(
        title = sensor.name.or("-"),
        summary = sensor.vendor.or("-"),
        items = listOf(
          RawTextItem(s(R.string.di_label_sensor_vendor), sensor.vendor.or("-")),
          RawTextItem(s(R.string.di_label_sensor_type), sensorType(sensor)),
          RawTextItem(s(R.string.di_label_sensor_version), sensor.version.toString()),
          RawTextItem(s(R.string.di_label_sensor_power), "%.2f mA".format(Locale.US, sensor.power)),
          RawTextItem(s(R.string.di_label_sensor_resolution), sensor.resolution.toString()),
          RawTextItem(s(R.string.di_label_sensor_range), sensor.maximumRange.toString()),
          RawTextItem(s(R.string.di_label_sensor_min_delay), "${sensor.minDelay} µs"),
          RawTextItem(s(R.string.di_label_sensor_max_delay), "${sensor.maxDelay} µs"),
          RawTextItem(s(R.string.di_label_sensor_reporting), reportingMode(sensor)),
          RawTextItem(s(R.string.di_label_sensor_wakeup), yn(sensor.isWakeUpSensor)),
        ),
      )
    }
  }.getOrDefault(emptyList())

  private fun sensorType(sensor: Sensor): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) sensor.stringType.or("-")
    else sensor.type.toString()

  private fun reportingMode(sensor: Sensor): String = when (sensor.reportingMode) {
    Sensor.REPORTING_MODE_CONTINUOUS -> "Continuous"
    Sensor.REPORTING_MODE_ON_CHANGE -> "On Change"
    Sensor.REPORTING_MODE_ONE_SHOT -> "One Shot"
    Sensor.REPORTING_MODE_SPECIAL_TRIGGER -> "Special Trigger"
    else -> "-"
  }

  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)
}
```

- [ ] **Step 2: GetSensorInfo** — mirror `GetCameraInfo` (usecase over `SensorInfoUtils.getSensors()`).

- [ ] **Step 3: SensorViewModel + Sensor screen** — mirror pattern. In `SensorType` set `override val order: Int get() = 7`.

- [ ] **Step 4: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): Sensor tab (expandable), fix Sensor order"
```

---

### Task 10: Network tab (telephony, permission-gated)

**Files:**
- Create: `.../core/utils/NetworkInfoUtils.kt`
- Create: `.../core/usecase/GetNetworkInfo.kt`
- Create: `.../ui/info/network/NetworkViewModel.kt`
- Modify: `.../ui/info/network/Network.kt`
- Modify: `features/dev-info/src/main/AndroidManifest.xml`

**Interfaces:**
- Produces: `NetworkInfoUtils` (permission-free getters + `hasTelephony()` + `hasPhoneStatePermission()`), `GetNetworkInfo` (emits `PermissionItem` for gated fields).

- [ ] **Step 1: Manifest permissions**

Add above `<application>` in the dev-info manifest:

```xml
  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
  <uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

- [ ] **Step 2: NetworkInfoUtils**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.ryccoatika.sqatoolkit.common.utils.or
import me.tatarka.inject.annotations.Inject

@Inject
internal class NetworkInfoUtils(
  private val context: Context,
) {
  private fun tm() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

  fun hasTelephony(): Boolean =
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

  fun hasPhoneStatePermission(): Boolean =
    ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) ==
      PackageManager.PERMISSION_GRANTED

  fun getNetworkOperator(): String = runCatching { tm().networkOperatorName }.getOrNull().or("-")
  fun getSimOperator(): String = runCatching { tm().simOperatorName }.getOrNull().or("-")
  fun getNetworkMccMnc(): String = mccMnc(runCatching { tm().networkOperator }.getOrNull())
  fun getSimMccMnc(): String = mccMnc(runCatching { tm().simOperator }.getOrNull())
  fun getSimCountry(): String = runCatching { tm().simCountryIso }.getOrNull()?.uppercase().or("-")
  fun getNetworkCountry(): String = runCatching { tm().networkCountryIso }.getOrNull()?.uppercase().or("-")

  fun getPhoneType(): String = when (runCatching { tm().phoneType }.getOrNull()) {
    TelephonyManager.PHONE_TYPE_GSM -> "GSM"
    TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
    TelephonyManager.PHONE_TYPE_SIP -> "SIP"
    else -> "None"
  }

  fun getSimState(): String = when (runCatching { tm().simState }.getOrNull()) {
    TelephonyManager.SIM_STATE_READY -> "Ready"
    TelephonyManager.SIM_STATE_ABSENT -> "Absent"
    TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN Required"
    TelephonyManager.SIM_STATE_PUK_REQUIRED -> "PUK Required"
    TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Network Locked"
    TelephonyManager.SIM_STATE_NOT_READY -> "Not Ready"
    else -> "Unknown"
  }

  fun getSimCount(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) tm().activeModemCount.toString()
    else tm().phoneCount.toString()
  }.getOrNull().or("-")

  fun isRoaming(): Boolean = runCatching { tm().isNetworkRoaming }.getOrDefault(false)

  // gated (READ_PHONE_STATE)
  fun getDataNetworkType(): String = runCatching {
    when (tm().dataNetworkType) {
      TelephonyManager.NETWORK_TYPE_LTE -> "LTE (4G)"
      TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
      TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
      TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS (3G)"
      TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2G)"
      TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS (2G)"
      TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
      else -> "Other"
    }
  }.getOrNull().or("-")

  @Suppress("HardwareIds", "MissingPermission")
  fun getImei(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tm().imei else @Suppress("DEPRECATION") tm().deviceId
  }.getOrNull().or("Unknown")

  @Suppress("HardwareIds", "MissingPermission")
  fun getPhoneNumber(): String = runCatching { tm().line1Number }.getOrNull().or("Unknown")

  private fun mccMnc(op: String?): String {
    if (op.isNullOrBlank() || op.length < 4) return "-"
    return "${op.substring(0, 3)} / ${op.substring(3)}"
  }
}
```

- [ ] **Step 3: GetNetworkInfo (gated fields → PermissionItem)**

```kotlin
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
    val granted = net.hasPhoneStatePermission()
    fun gated(label: Int, value: () -> String, permission: String): Item =
      if (granted) RawTextItem(s(label), value())
      else PermissionItem(s(label), permission, s(R.string.di_text_permission_required))

    listOf(
      GroupItem(rawTitle = s(R.string.di_group_telephony), items = listOf(
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
        gated(R.string.di_label_data_network_type, net::getDataNetworkType, Manifest.permission.READ_PHONE_STATE),
        gated(R.string.di_label_imei, net::getImei, Manifest.permission.READ_PHONE_STATE),
        gated(R.string.di_label_phone_number, net::getPhoneNumber, Manifest.permission.READ_PHONE_NUMBERS),
      )),
    )
  }
}
```

- [ ] **Step 4: NetworkViewModel + Network screen with launcher**

`NetworkViewModel.kt` mirror pattern (usecase `GetNetworkInfo`). `Network.kt` — like `Software.kt` but the state-composable wraps content in the permission launcher + `LocalPermissionRequester`:

```kotlin
@Composable
internal fun Network(viewModel: NetworkViewModel) {
  val viewState by viewModel.state.collectAsState()
  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { granted -> if (granted) viewModel.refresh() }

  CompositionLocalProvider(
    LocalPermissionRequester provides { launcher.launch(it) },
  ) {
    Network(state = viewState)
  }
}

@Composable
internal fun Network(state: InfoViewState) {
  ItemComposer(
    items = state.items,
    modifier = Modifier
      .padding(vertical = 8.dp, horizontal = 16.dp)
      .verticalScroll(rememberScrollState())
      .fillMaxSize(),
  )
}
```

Imports: `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.runtime.CompositionLocalProvider`, `...ui.common.utils.LocalPermissionRequester`. Keep `NetworkType` (order 3).

- [ ] **Step 5: Compile & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/ features/dev-info/src/main/AndroidManifest.xml
git commit -m "feat(dev-info): Network tab (telephony) with permission Grant flow"
```

---

### Task 11: Connectivity tab (WiFi/Bluetooth/other, gated)

**Files:**
- Create: `.../core/utils/ConnectivityInfoUtils.kt`
- Create: `.../core/usecase/GetConnectivityInfo.kt`
- Create: `.../ui/info/connectivity/ConnectivityViewModel.kt`
- Modify: `.../ui/info/connectivity/Connectivity.kt`

**Interfaces:**
- Produces: `ConnectivityInfoUtils` (WiFi/BT/other getters + permission checks), `GetConnectivityInfo` (emits `PermissionItem` for SSID/BSSID/link/RSSI/IP under location; adapter name under `BLUETOOTH_CONNECT`).

- [ ] **Step 1: ConnectivityInfoUtils**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import android.os.Build
import androidx.core.content.ContextCompat
import com.ryccoatika.sqatoolkit.common.utils.or
import me.tatarka.inject.annotations.Inject

@Inject
internal class ConnectivityInfoUtils(
  private val context: Context,
) {
  private fun wifi() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
  private fun pm() = context.packageManager

  fun hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED

  fun hasBluetoothConnectPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
      ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) ==
        PackageManager.PERMISSION_GRANTED

  fun isWifiEnabled(): Boolean = runCatching { wifi().isWifiEnabled }.getOrDefault(false)
  fun is5GhzSupported(): Boolean = runCatching { wifi().is5GHzBandSupported }.getOrDefault(false)
  fun is6GhzSupported(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && runCatching { wifi().is6GHzBandSupported }.getOrDefault(false)
  fun isWifiAwareSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
  fun isWifiDirectSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)

  @Suppress("DEPRECATION")
  fun getSsid(): String = runCatching { wifi().connectionInfo.ssid }.getOrNull().or("-")
  @Suppress("DEPRECATION")
  fun getBssid(): String = runCatching { wifi().connectionInfo.bssid }.getOrNull().or("-")
  @Suppress("DEPRECATION")
  fun getLinkSpeed(): String = runCatching { "${wifi().connectionInfo.linkSpeed} Mbps" }.getOrNull().or("-")
  @Suppress("DEPRECATION")
  fun getFrequency(): String = runCatching { "${wifi().connectionInfo.frequency} MHz" }.getOrNull().or("-")
  @Suppress("DEPRECATION")
  fun getRssi(): String = runCatching { "${wifi().connectionInfo.rssi} dBm" }.getOrNull().or("-")
  @Suppress("DEPRECATION")
  fun getIpAddress(): String = runCatching {
    val ip = wifi().connectionInfo.ipAddress
    "%d.%d.%d.%d".format(ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
  }.getOrNull().or("-")

  fun isBluetoothSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  fun isBleSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
  fun isBleAdvertiserSupported(): Boolean = runCatching {
    (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
      .adapter?.isMultipleAdvertisementSupported == true
  }.getOrDefault(false)

  @Suppress("MissingPermission")
  fun getBluetoothName(): String = runCatching {
    (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter?.name
  }.getOrNull().or("-")

  fun isNfcSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_NFC)
  fun isNfcEnabled(): Boolean = runCatching { NfcAdapter.getDefaultAdapter(context)?.isEnabled == true }.getOrDefault(false)
  fun isUsbHostSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_USB_HOST)
  fun isEthernetSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_ETHERNET)
}
```

- [ ] **Step 2: GetConnectivityInfo**

```kotlin
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

    val wifi = GroupItem(rawTitle = s(R.string.di_group_wifi), items = listOf(
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
    ))

    val btName: Item =
      if (conn.hasBluetoothConnectPermission()) RawTextItem(s(R.string.di_label_bt_name), conn.getBluetoothName())
      else PermissionItem(s(R.string.di_label_bt_name), Manifest.permission.BLUETOOTH_CONNECT, s(R.string.di_text_permission_required))

    val bt = GroupItem(rawTitle = s(R.string.di_group_bluetooth), items = listOf(
      RawTextItem(s(R.string.di_label_bt_supported), yn(conn.isBluetoothSupported())),
      RawTextItem(s(R.string.di_label_bt_le), yn(conn.isBleSupported())),
      RawTextItem(s(R.string.di_label_bt_advertiser), yn(conn.isBleAdvertiserSupported())),
      btName,
    ))

    val other = GroupItem(rawTitle = s(R.string.di_group_other), items = listOf(
      RawTextItem(s(R.string.di_label_nfc), yn(conn.isNfcSupported())),
      RawTextItem(s(R.string.di_label_usb_host), yn(conn.isUsbHostSupported())),
      RawTextItem(s(R.string.di_label_ethernet), yn(conn.isEthernetSupported())),
    ))

    listOf(wifi, bt, other)
  }
}
```

- [ ] **Step 3: ConnectivityViewModel + Connectivity screen with launcher** — same launcher wiring as Network (Task 10 Step 4), usecase `GetConnectivityInfo`, keep `ConnectivityType` (order 6).

- [ ] **Step 4: Compile, assemble & commit**

Run: `./gradlew :features:dev-info:compileDebugKotlin` then `./gradlew :features:dev-info:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): Connectivity tab (WiFi/Bluetooth/other) with Grant flow"
```

---

### Task 12: Full manual verification

**Files:** none (verification only).

- [ ] **Step 1: Assemble the app**

Run: `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

- [ ] **Step 2: Manual smoke on device/emulator**

Install, open Dev Info. For each tab (Device, Hardware, Network, Software, Camera, Connectivity, Sensor): confirm it renders data (no crash, no all-blank group). Tab order reads 1..7 with no duplicate. Expand a sensor and a camera. On Network/Connectivity, tap **Grant** on a gated field, allow the permission, confirm the value populates (tab refreshes). Confirm System + DRM now live under Software, not Device.

- [ ] **Step 3: Commit any fixes** found during smoke, then done.

## Self-Review Notes

- **Spec coverage:** Device trim (T5), Software incl. System+DRM (T5), Hardware incl. EGL GPU (T6/T7), Camera expandable (T8), Sensor expandable + order fix (T9), Network gated (T10), Connectivity gated (T11), permission Grant flow (T3+T10+T11), dynamic labels via RawTextItem/rawTitle (T1), shared base VM (T4), manifest perms (T10). All covered.
- **Deviation from spec:** new field labels use `RawTextItem` + string resources resolved in usecases (not `Label` enum) to avoid ~80 enum entries; existing shipped enum labels unchanged. Flagged to user.
- **Deprecation:** `WifiManager.connectionInfo` and `Display.getRealMetrics`/`defaultDisplay` are deprecated but functional at minSdk 24; `@Suppress("DEPRECATION")` applied. A future pass could adopt `WindowMetrics` (API 30+) / `ConnectivityManager` callbacks.
