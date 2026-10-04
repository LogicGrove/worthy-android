<p><img src="../assets/worthy-banner.png" alt="Worthy · Android app" width="960"></p>

# Worthy · Advanced guide

[Español](README.advanced.md) | **English** · [Quick start](../README.en.md)

Worthy is an Android savings goals app. Create a goal with a product and its price, record contributions, and check your progress. It supports several goals in a card interface and stores data locally.

## Version and checks

The first distribution is **1.0**, `versionCode = 1`, package `app.worthy.android`. The published APK is the author's supplied release file, without rebuilding or modifying it. It is byte identical to the APK included in the supplied project.

Its APK v2 signature was cryptographically verified using the official Android `apksig` library, with no errors or warnings. Its certificate does not use the usual debug name. Compare the file's integrity with `SHA256SUMS.txt` in the release.

This preparation includes static source review and inspection of APK metadata. **The app has not been run on a device or emulator, and the project's Gradle tests have not been run here.** A valid signature alone does not prove that every flow works.

## Technology and configuration

Values read from the published project:

| Item | Value |
|---|---|
| Platform / language | Android / Kotlin |
| Interface | Jetpack Compose and Material 3 |
| Data / preferences | Room 3 / DataStore |
| Navigation | Navigation 3 |
| Application ID / module | `app.worthy.android` / `app` |
| `minSdk` | 24, Android 7.0 |
| `compileSdk` / `targetSdk` | 37 / 37 |
| Android Gradle Plugin | 9.3.3 |
| Gradle / Kotlin | 9.5.0 / 2.2.10 |
| Daemon JDK / bytecode | 25 / Java 11 |
| Compose BOM / Room 3 | 2026.02.01 / 3.0.2 |

Keep the versions in `gradle/libs.versions.toml`, the wrapper, and `gradle/gradle-daemon-jvm.properties`. Release optimization is disabled in the supplied project. No dependencies or SDK levels were changed to publish this version.

## Features

- Goals with a product name, optional link, target price, and currency.
- Card home screen, goal details, and contribution recording.
- Light, dark, or system theme; dynamic colors and color styles.
- Currency, haptic feedback, and high refresh rate preferences, subject to device support.
- JSON backups and restoration of goals and contributions.

Product links are stored with each goal. This version does not implement automatic price extraction or bank access. The interface has English text resources; the READMEs are in Spanish and English.

## Structure

| Path | Contents |
|---|---|
| `app/src/main/` | Application, manifest, and resources |
| `core/` within the package | Models, money, validation, and haptics |
| `data/` | Room, repositories, DataStore, and backups |
| `feature/` | Home, add goal, goal details, settings, and theme |
| `navigation/`, `ui/`, `di/` | Navigation, components, and dependency composition |
| `app/src/test/`, `app/src/androidTest/` | Unit and instrumented tests |
| `app/schemas/` | Exported Room schema, version 1 |
| `gradle/`, `gradlew`, `gradlew.bat` | Catalog, wrapper, and JVM configuration |
| `docs/`, `assets/` | Bilingual documentation and branding |

The repository retains the original Gradle structure. Build directories, caches, local SDK configuration, and private signing material are excluded. The APK belongs in Releases.

## Open and run

1. Download or clone the repository and open its root in Android Studio.
2. Install SDK 37 and use JDK 25 as specified in the daemon configuration.
3. Sync Gradle and select the `app` module.
4. Run the debug variant on a device or emulator with API 24 or later.

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

macOS / Linux:

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

`installDebug` needs a connected device. These are developer commands, not checks performed during publication.

## Data and backups

Money uses integer minor units (`Long`) and ISO 4217 currency codes. Room stores goals and contributions in `WorthyDatabase`, version 1. DataStore stores preferences.

Exports use UTF-8 JSON through Android's file picker. Format 1 includes the format version, export time, goals, and contributions. Import validates the document and, after confirmation, replaces existing data in a Room transaction. See the [full format](backup-format.en.md).

The JSON file is not encrypted by this exporter and contains goal and contribution data. Store it privately. The manifest also enables Android system backups, whose behavior depends on the device and its settings. This is not an app cloud synchronization feature.

## Project tests

The source includes tests for money, progress, validation, backups, settings, repositories, ViewModels, card selection, and themes. It also includes instrumented tests for Room and DataStore.

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

The last task needs a device or emulator. Before an update, check goal creation, contributions, card navigation, persistence after restarting, themes, and backup/restore. Test the release variant as well.

## Signing and updates

The [signing guide](FIRMA_ANDROID.en.md) covers Android Studio and certificate verification. Keep the application ID and signing key to update existing installations; increase `versionCode` for new versions.

Debug builds usually use a different certificate. Before uninstalling to switch to release, export and keep a backup: uninstalling may erase local data. Never publish private keys, `.jks` stores, or passwords.

## Authorship and license

A project by **Manu / LogicGrove**. The supplied project has no `LICENSE` file; a code distribution license remains to be declared. Jade's license does not apply automatically. Dependencies retain their own notices and licenses.

## Official references

- [Build from the command line](https://developer.android.com/build/building-cmdline)
- [Version an app](https://developer.android.com/studio/publish/versioning)
- [Android signing](https://developer.android.com/studio/publish/app-signing)
- [Verify an APK](https://developer.android.com/tools/apksigner)
