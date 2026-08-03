# Dev-Info: Complete All Tabs — Design

Date: 2026-08-03
Feature module: `features/dev-info`

## Goal

`features/dev-info` shows device information across 7 tabs (horizontal pager +
filter chips). Only **Device** is implemented; the other six are placeholder
stubs rendering a single `Text`. Complete all six with detailed, comprehensive
information, following the existing Device tab pattern.

Tabs: Device (done), Hardware, Network, Software, Camera, Connectivity, Sensor.

## Existing pattern (the template)

Each tab is composed of:

- `Get{X}Info : ResultInteractor<Unit, List<Item>>` — usecase that gathers data
  off the main thread (`withContext(Dispatchers.IO)`), returns a `List<Item>`.
- `{X}ViewModel : ViewModel` — runs the usecase in `init`, exposes
  `StateFlow<...ViewState>` with `items` + `isLoading` (via `ObservableLoadingCounter`).
- `{X}ViewState(items, isLoading)` — immutable state.
- `{X}` composable — collects state, renders `ItemComposer(items = ...)`.
- `{X}Type : DevInfoType` — `id`, `order`, `featureTitle` string res. Registered
  `@IntoSet` in `DevInfoScreens.DevInfoTypes`.

Shared render model (`core/model/Item.kt`): `TextItem`, `StatusItem`, `DateItem`,
`ElapsedTimeItem`, `DeviceCardItem`, `GroupItem`. Rendered by `ItemComposer` →
per-type composers. Labels are a `Label` enum mapped to string resources by
`DevInfoTextCreator.itemLabel`.

## Architecture decisions

### 1. Per-domain util split

`DeviceInfoUtils` is already large. Add focused, single-purpose utils, each
injected only where needed:

- `DeviceInfoUtils` — keeps device basics (existing).
- `SoftwareInfoUtils` — build/OS/root/treble/DRM getters (some moved from
  `DeviceInfoUtils`: `getDeviceReleaseAndroidVersion`, `getAndroidUI`,
  `getSecurityPatch`, `getJavaVMVersion`, `getKernelVersion`, `getOpenGLESVersion`,
  `getVulkanVersion`, `getSELinux`, `getSystemUptime`, `getDrmInfo`). Moving these
  keeps `DeviceInfoUtils` focused on the Device tab.
- `HardwareInfoUtils` — CPU/GPU/RAM/storage/display/battery.
- `CameraInfoUtils` — Camera2 characteristics.
- `NetworkInfoUtils` — telephony.
- `ConnectivityInfoUtils` — WiFi/Bluetooth/NFC/USB.
- `SensorInfoUtils` — `SensorManager` enumeration.

### 2. Uniform ViewModel/ViewState

Extract shared base to remove 6× boilerplate:

- `InfoViewState(items: List<Item>, isLoading: Boolean)` in
  `ui/info/common/`, with `Empty`.
- `abstract InfoViewModel : ViewModel` holding `MutableStateFlow<List<Item>>`,
  `ObservableLoadingCounter`, `state: StateFlow<InfoViewState>`, an `init` load
  and a public `refresh()` that re-runs `abstract suspend fun load(): List<Item>`.

Each concrete VM: injects its usecase, implements `load() = useCase.executeSync(Unit)`.
Migrate the existing `DeviceViewModel`/`DeviceViewState` to the shared base.

### 3. Permission button flow (placeholder + Grant, no upfront prompt)

Some fields need dangerous permissions. Chosen behavior: show a placeholder value
and a **Grant** button; only on tap do we request the permission, then refresh.

- New model: `PermissionItem(label: Label, permission: String, value: String)` —
  value defaults to a "Permission required" string.
- New composer `ItemPermissionComposer` — renders label + value + a `TextButton`
  ("Grant"). On click calls `LocalPermissionRequester.current(item.permission)`.
- New CompositionLocal `LocalPermissionRequester: (permission: String) -> Unit`
  (default no-op).
- Network & Connectivity screens host
  `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) viewModel.refresh() }`
  and provide `LocalPermissionRequester { launcher.launch(it) }` around their content.
- Usecases check `ContextCompat.checkSelfPermission` at run time: granted → real
  `TextItem`; denied → `PermissionItem`. `refresh()` re-runs; granted values appear.
- Manifest (`features/dev-info/src/main/AndroidManifest.xml`) adds:
  `ACCESS_FINE_LOCATION`, `READ_PHONE_STATE`, `READ_PHONE_NUMBERS`,
  `BLUETOOTH_CONNECT`.
- Note: some values (e.g. IMEI on API 29+) can remain null even with permission;
  render as the real value or "Unknown". The button still honors the request.

### 4. Dynamic labels + compact expandable rows

Sensor names and camera titles aren't enumerable, and those tabs can render many
entries (30+ sensors, several cameras). Chosen layout: compact one-line rows that
expand to full detail.

- `GroupItem` gains optional `rawTitle: String?` — header uses `rawTitle` when set,
  else resolves `label`.
- New model: `RawTextItem(label: String, value: String)` — a `TextItem` whose label
  is a raw string (dynamic field names), rendered via the existing `ListItemText`.
- New model: `ExpandableGroupItem(title: String, summary: String, items: List<Item>)`.
- New composer `ItemExpandableGroupComposer` — a clickable header row showing
  `title` + `summary` + a rotating chevron; toggles local `remember { mutableStateOf(false) }`
  to reveal/hide `items` (animated). Used by Sensor (per sensor) and Camera (per camera).

### 5. Ordering fix

`SensorType.order` is currently `3` (collides with Network). Set Sensor `order = 7`.
Final order: Device 1, Hardware 2, Network 3, Software 4, Camera 5, Connectivity 6,
Sensor 7.

## Per-tab content

### Device (trim)

Remove the System group and DRM group added earlier (they move to Software).
Device tab keeps: DeviceCard, Basic, Manufacturer, Sale.

### Software

- **Android**: Android version (`Build.VERSION.RELEASE`), Android name, API level,
  codename, security patch, released-with (first API level), OneUI, build number
  (`Build.DISPLAY`), build ID (`Build.ID`), build type (`Build.TYPE`), build tags
  (`Build.TAGS`), fingerprint, baseband, bootloader, kernel, Java VM, ART runtime
  name+version, OpenGL ES, Vulkan, SELinux, system uptime.
- **System / Root**: root detected (scan common `su` binary paths), A/B updates
  (`ro.build.ab_update`), Treble enabled (`ro.treble.enabled`), Google Play services
  version (`PackageManager.getPackageInfo("com.google.android.gms")`, else "Not installed").
- **DRM** group (moved from Device): vendor, version, description, algorithms,
  security level, max HDCP level; omitted entirely when Widevine unsupported.

### Hardware

- **Processor**: chipset (`ro.board.platform` / `Build.SOC_MANUFACTURER`+`SOC_MODEL`
  on API 31+, fallback `Build.HARDWARE`), architecture (`Build.SUPPORTED_ABIS[0]`,
  64/32-bit), supported ABIs, cores (`Runtime.availableProcessors()`), governor +
  min/max frequency (`/sys/devices/system/cpu/cpu0/cpufreq/...`).
- **GPU**: vendor, renderer, version via an **offscreen EGL pbuffer context**
  (`EGL14`: init display, choose config, create pbuffer surface + context, make
  current, read `GL_VENDOR`/`GL_RENDERER`/`GL_VERSION`, tear down). Runs on the
  usecase's IO dispatcher.
- **Memory**: total / available (`ActivityManager.MemoryInfo`), low-memory threshold,
  memory class, large-heap class.
- **Storage**: internal total/free and system total/free via `StatFs`.
- **Display**: resolution, density dpi + bucket, refresh rate, physical size (inches),
  HDR supported.
- **Battery**: technology, health, status, capacity %, voltage, temperature — read
  from the sticky `ACTION_BATTERY_CHANGED` intent + `BatteryManager` capacity property.

### Camera

Per id in `CameraManager.cameraIdList`, one `ExpandableGroupItem` (title = derived
name e.g. "Back Camera (0)", summary = megapixels + hardware level). Expanded fields:
facing, hardware level (LEGACY/LIMITED/FULL/LEVEL_3), megapixels
(`SENSOR_INFO_PIXEL_ARRAY_SIZE`), sensor physical size, focal lengths, apertures,
flash available, max digital zoom, ISO range (`SENSOR_INFO_SENSITIVITY_RANGE`),
exposure time range, max photo resolution, max video resolution, FPS ranges, OIS
supported, RAW supported (capabilities), AF modes. Permission-free (characteristics
only; no camera opened).

### Network (telephony)

- **SIM / Network** (permission-free): network operator name, SIM operator name,
  MCC/MNC (network + SIM), SIM country ISO, network country ISO, phone type
  (GSM/CDMA/None), SIM state, active SIM count (`activeModemCount` API 30+), roaming.
- **Gated (`READ_PHONE_STATE`)** → `PermissionItem` until granted: data network type,
  IMEI/MEID, phone number (`READ_PHONE_NUMBERS`).
- If device lacks telephony (`FEATURE_TELEPHONY` false): single "Not available" row.

### Connectivity

- **WiFi**: enabled state, 5GHz / 6GHz / WiFi Aware / WiFi Direct support flags, MAC
  (note randomized/`02:00:...` on API 30+). **Gated (`ACCESS_FINE_LOCATION`)**:
  connected SSID, BSSID, link speed, frequency, RSSI, IP address.
- **Bluetooth**: supported, LE supported, LE advertiser supported. **Gated
  (`BLUETOOTH_CONNECT`, API 31+)**: adapter name.
- **Other**: NFC supported/enabled, USB host (`FEATURE_USB_HOST`), Ethernet feature,
  location providers available.

### Sensor

`SensorManager.getSensorList(Sensor.TYPE_ALL)`, one `ExpandableGroupItem` per sensor
(title = sensor name, summary = vendor). Expanded fields: vendor, type (string),
version, power (mA), resolution, max range, min delay (µs), max delay, reporting mode,
wake-up sensor. Permission-free.

## Files (approx.)

- New usecases (6): `Get{Software,Hardware,Camera,Network,Connectivity,Sensor}Info.kt`.
- New utils (6): `{Software,Hardware,Camera,Network,Connectivity,Sensor}InfoUtils.kt`.
- New shared: `ui/info/common/InfoViewModel.kt`, `InfoViewState.kt`.
- New/edited models: `PermissionItem`, `RawTextItem`, `ExpandableGroupItem`,
  `GroupItem.rawTitle` in `Item.kt`.
- New composers: `ItemPermissionComposer`, `ItemExpandableGroupComposer`, edit
  `ItemComposer` (dispatch), `ItemGroupComposer` (rawTitle), `LocalPermissionRequester`.
- Edited: 6 tab screen files (real content + register), `DeviceViewModel`/`DeviceViewState`
  (migrate to base), `GetDeviceInfo` (trim System/DRM), `DeviceInfoUtils` (move getters),
  `Label.kt` + `DevInfoTextCreator` + `strings.xml` (many new labels/strings), manifest,
  `SensorType.order`.

## Error handling

Every getter is defensive: `runCatching`/try-guarded, returns "-"/"Unknown"/null on
failure so a single unsupported API never blanks a tab. Usecases run on IO. Camera,
sensor, EGL, battery-intent reads all null-safe.

## Testing / verification

Compile per batch (`:features:dev-info:compileDebugKotlin`, `externalNativeBuildDebug`
if native touched, `assembleDebug`). Manual: run on device/emulator, page each tab,
verify data renders, expand a sensor/camera, tap Grant on a gated field and confirm
the value populates after granting.

## Build sequencing

1. Shared infra: models + composers + permission local + base VM/state + util split +
   Device trim/migrate + Software (absorbs System/DRM).
2. Permission-free tabs: Hardware (incl. EGL GPU), Camera, Sensor.
3. Gated tabs: Network, Connectivity (+ manifest perms + launcher wiring).

Compile after each batch; assemble at the end.
