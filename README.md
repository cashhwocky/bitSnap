# bitSnap

A themed Snapchat app for **bitDroid** — Bloom Home / Material You, lilac everything.

bitSnap is a **native Android wrapper around Snapchat's official web client** (`web.snapchat.com`).
It is **not** a reverse-engineered client and does not touch Snapchat's private API, so there is no
custom protocol for Snap to flag as a "third-party app." Underneath, it's Snap's own web app — the
bit\* treatment is all in the native shell around it.

Made by Alex Desrosier.

---

## What it does

- **Bloom Home / Material You** chrome end to end: purple-to-teal backdrop, lavender accents,
  Unbounded / Inter / JetBrains Mono type, a frosted floating tab bar with a sliding lilac lens.
- **Lilac bitSnap icon** (original bolt-in-a-bubble mark, part of the bit\* family — not the
  Snapchat ghost), with AMOLED-lilac, Bloom and Material You (themed-icon) variants.
- **Stays logged in** — cookies and web storage persist across launches.
- **Send photos** — the web file picker opens the Android gallery/camera chooser.
- **Camera / mic bridge** — when the web client asks for them (e.g. a call on supported builds),
  bitSnap requests the Android runtime permission and hands it through.
- **Login popups work** — "Log in with Google / Apple" opens in a themed popup window.
- **External links** (help pages, shared URLs to other sites) open in your real browser.
- **Offline / retry screen**, themed splash with a load bar, in-app back navigation.
- **Theme switcher** in the ⋯ sheet: Bloom Home · Lilac AMOLED · Material You.

## Honest limits

- `web.snapchat.com` is built for **desktop browsers**, so bitSnap presents a desktop user agent to
  make it load. Chat and viewing work best; **camera capture and sending snaps are limited** on the
  web client regardless of the wrapper. This is a Snapchat-web constraint, not something the shell
  can fix.
- Snap can change their web app at any time and break parts of this.
- No Bitmoji rendering is required or added.

---

## Build

### In CI (recommended — produces the APK for you)
This repo ships a GitHub Actions workflow (`.github/workflows/build.yml`). On every push to `main`
it builds **debug + release** APKs and uploads them as a build **artifact** (Actions → the run →
Artifacts → `bitSnap-apks`).

To cut a published release APK, push a tag:
```bash
git tag v0.1.0
git push origin v0.1.0
```
The workflow attaches `bitSnap-0.1.0-release.apk` to the GitHub Release.

### Locally
Needs the Android SDK (Android Studio, or command-line tools) with platform 35:
```bash
./gradlew assembleDebug      # app/build/outputs/apk/debug/bitSnap-0.1.0-debug.apk
./gradlew assembleRelease    # debug-signed unless you add the signing secrets below
```

## Install
Copy the APK to your Pixel 10 and install it (allow "install unknown apps" for your file manager).
The debug build installs alongside anything else as `com.bitdroid.snap.debug`.

## Release signing (optional)
Without a key, release builds are debug-signed so CI always yields an installable APK. To sign with
your own key, add these repo secrets (Settings → Secrets and variables → Actions):

- `BITSNAP_KEYSTORE_B64` — base64 of your `.jks`
- `BITSNAP_KEYSTORE_PASSWORD`
- `BITSNAP_KEY_ALIAS`
- `BITSNAP_KEY_PASSWORD`

## Stack
Kotlin · Jetpack Compose · Material 3 (dynamic color) · `WebView` · Haze · Gradle 8.14 / AGP 8.10 ·
minSdk 31 · targetSdk 35. Package `com.bitdroid.snap`.
