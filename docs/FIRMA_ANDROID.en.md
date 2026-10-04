# Sign Worthy for distribution

[Español](FIRMA_ANDROID.md) | **English** · [Advanced guide](README.advanced.en.md)

Sign the **APK**, or the **AAB** intended for Google Play. READMEs, images and source code do not need an Android signature. An APK installed during development is usually signed with a debug key; that does not make it a distribution build.

## 1. Prepare the full project

Open Worthy in Android Studio, sync Gradle and check that it works. Set the release version in Gradle: `versionName` for the display name and `versionCode` for the numeric version. Keep `app.worthy.android` if it is the actual ID of the app you want to update.

This presentation package does not include the Android project and cannot build an APK by itself.

## 2. Generate a signed APK in Android Studio

1. Open **Build → Generate Signed Bundle / APK**.
2. Choose **APK** for direct installation; select the `app` module.
3. If you already have a Worthy distribution key, select its keystore. For the first key, click **Create new**.
4. For a new key, save the `.jks` outside the repository. You can use `worthy-release.jks`, the alias `worthy-release` and a validity of **30 years**. Enter passwords in Android Studio and keep a secure backup. Complete the certificate details, knowing its public part is included in the APK.
5. Select the **release** variant and generate the file. Use Android Studio's **Locate** action to find the output rather than assuming a fixed path.

Keep the keystore and passwords for future versions. Do not put them in this package, source, GitHub, a README or screenshot. Do not change the key on every build.

## 3. Verify the result

Use `apksigner` from Android SDK Build Tools. On Windows it is `apksigner.bat`. Replace both example paths with the actual paths; `INSTALLED_VERSION` is a placeholder:

```powershell
& "C:\path\to\Android\Sdk\build-tools\INSTALLED_VERSION\apksigner.bat" verify --verbose --print-certs "C:\path\to\worthy-release.apk"
```

The command should exit successfully, verify the signature and show the expected certificate. Keep its public SHA-256 fingerprint. If a distribution version already exists, compare certificates. A valid signature alone does not prove it is the correct distribution key.

Also test the **release** APK on a device, checking goals, contributions, persistence and backups. Do not modify the APK after signing; if you rebuild it, verify the new output.

Optionally calculate the final file hash to identify exactly which APK will be distributed:

```powershell
Get-FileHash "C:\path\to\worthy-release.apk" -Algorithm SHA256
```

The file hash and certificate fingerprint are different values. Neither replaces functional testing.

## 4. If a debug version is already installed

Android does not allow replacing an app directly with one signed by an incompatible certificate. Before uninstalling, use Worthy's backup option and check that it can be restored. Uninstalling may remove local data. Then install the release and restore the backup, if the app and format support it.

For future direct updates, keep the application ID, compatible distribution key and an update-compatible version. Increase `versionCode` for new releases.

## 5. If you later want Google Play

Generate an **Android App Bundle (AAB)** and configure Play App Signing. The upload key and the key that signs installed apps may differ. If you want GitHub and Google Play builds to update each other, plan a common distribution certificate before publishing: an upload key is not automatically the app signing key.

**No key was generated and no APK was signed or uploaded during this preparation.**

## Official documentation

- [Signing and keystores](https://developer.android.com/studio/publish/app-signing)
- [Build for distribution](https://developer.android.com/build/build-for-release)
- [Verify with apksigner](https://developer.android.com/tools/apksigner)
**Version 1.0 uses the APK already signed by the author. Keep the distribution key for future updates.**
