# Dynamic Features Testing with bundletool

This document describes how to test on-demand dynamic feature modules (`fillstorage` and `fillmemory`) locally using the App Bundle and bundletool.

## Overview

On-demand feature installation requires an App Bundle (`.aab` file) and cannot be tested with a plain APK. The bundletool `--local-testing` flag enables testing the on-demand install flow on a local device without uploading to Google Play.

## Local Testing Steps

### 1. Build the App Bundle

```bash
./gradlew :app:bundleDebug
```

Output: `app/build/outputs/bundle/debug/app-debug.aab`

### 2. Generate APKs with bundletool

Install bundletool if not already installed:

```bash
# Download bundletool (or use `brew install bundletool` on macOS)
curl -L https://github.com/google/bundletool/releases/latest/download/bundletool-all.jar -o bundletool.jar
```

Build APK set for local testing:

```bash
java -jar bundletool.jar build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=app.apks \
  --local-testing \
  --ks=<keystore> \
  --ks-pass=pass:<password> \
  --ks-key-alias=<key_alias> \
  --key-pass=pass:<password>
```

Replace the keystore parameters with your debug keystore path and credentials (or use `--mode=universal` to generate a universal APK without signing).

### 3. Install APKs on Device/Emulator

```bash
java -jar bundletool.jar install-apks --apks=app.apks
```

### 4. Test On-Demand Feature Download

1. Launch the app
2. Navigate to the home screen where the feature cards are displayed
3. Tap **"Download"** on the **Fill Storage** or **Fill Memory** card
4. Confirm the module begins downloading (progress indicator appears)
5. Once complete, tap **"Open"** to launch the feature
6. Verify the feature loads and functions correctly

### 5. Test Feature Removal

1. While viewing an installed dynamic feature, open the card's overflow menu and tap **"Remove"**
2. No confirmation dialog is shown — tapping **"Remove"** immediately requests a deferred uninstall, and the card reverts to the **"Download"** state right away
3. The feature module is marked for deferred uninstall; the system reclaims space later (typically triggered by Play, not immediately)

## Important Notes

- **On-Demand Install Requirements**: On-demand installation only works when the app is installed from an App Bundle using bundletool with `--local-testing` or from Google Play internal testing. It **does NOT work** with a plain `:app:installDebug` APK.
- **Split Module Names**: The dynamic feature modules are installed as splits with the names:
  - `fillstorage` (Fill Storage module)
  - `fillmemory` (Fill Memory module)
- **Debug Signing**: Ensure your debug keystore and credentials are correct when building APKs. If you omit signing parameters, bundletool can generate a universal APK for testing purposes (less efficient but simpler for development).
- **Device State**: Ensure sufficient storage space on the device before testing download and installation.
