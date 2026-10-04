# Firmar Worthy para distribución

**Español** | [English](FIRMA_ANDROID.en.md) · [Guía avanzada](README.advanced.md)

Se firma el **APK**, o el **AAB** destinado a Google Play. Los README, las imágenes y el código fuente no necesitan la firma de Android. Un APK instalado durante el desarrollo suele estar firmado con una clave debug; eso no lo convierte en una versión de distribución.

## 1. Preparar el proyecto completo

Abre Worthy en Android Studio, sincroniza Gradle y comprueba que funciona. Define la versión de publicación en Gradle: `versionName` para el nombre visible y `versionCode` para la versión numérica. Conserva `app.worthy.android` si es el identificador real de la app que deseas actualizar.

Este paquete de presentación no contiene el proyecto Android ni permite compilar un APK por sí solo.

## 2. Generar un APK firmado en Android Studio

1. Abre **Build → Generate Signed Bundle / APK**.
2. Elige **APK** para una instalación directa; selecciona el módulo `app`.
3. Si ya tienes una clave de distribución de Worthy, selecciona su almacén. Si es la primera, pulsa **Create new**.
4. Para una clave nueva, guarda el archivo `.jks` fuera del repositorio. Puedes usar el nombre `worthy-release.jks`, alias `worthy-release` y una validez de **30 años**. Introduce las contraseñas en Android Studio y conserva una copia segura. Completa los datos del certificado sabiendo que su parte pública se incluye en el APK.
5. Selecciona la variante **release** y genera el archivo. Usa la opción **Locate** de Android Studio para localizar el resultado, en lugar de suponer una ruta fija.

Conserva el almacén y sus contraseñas para las siguientes versiones. No los pongas en este paquete, el código, GitHub, un README o una captura. No cambies la clave en cada compilación.

## 3. Verificar el resultado

Usa `apksigner` de Android SDK Build Tools. En Windows es `apksigner.bat`. Sustituye las dos rutas del ejemplo por las reales; el texto `VERSION_INSTALADA` es un marcador:

```powershell
& "C:\ruta\al\Android\Sdk\build-tools\VERSION_INSTALADA\apksigner.bat" verify --verbose --print-certs "C:\ruta\a\worthy-release.apk"
```

La orden debe terminar correctamente, verificar la firma y mostrar el certificado esperado. Guarda su huella pública SHA-256. Si ya existe una versión de distribución, compara el certificado con ella. Una firma válida, por sí sola, no confirma que sea la clave de distribución correcta.

Prueba también el APK **release** en un dispositivo y comprueba objetivos, aportaciones, persistencia y copias. No modifiques el APK después de firmarlo; si lo reconstruyes, verifica el resultado nuevo.

Opcionalmente calcula el hash del archivo final para identificar exactamente qué APK se distribuirá:

```powershell
Get-FileHash "C:\ruta\a\worthy-release.apk" -Algorithm SHA256
```

El hash del archivo y la huella del certificado son datos distintos. Ninguno sustituye una prueba de funcionamiento.

## 4. Si ya tienes una versión debug instalada

Android no permite reemplazar directamente una app con otra firmada por un certificado incompatible. Antes de desinstalar, usa la copia de seguridad de Worthy y comprueba que puede restaurarse. La desinstalación puede borrar los datos locales. Después instala la release y restaura la copia, si la app y el formato lo permiten.

Para futuras actualizaciones directas, mantén el identificador de aplicación, la clave de distribución compatible y una versión válida para la actualización. Incrementa `versionCode` en las nuevas publicaciones.

## 5. Si más adelante quieres Google Play

Genera un **Android App Bundle (AAB)** y configura Play App Signing. La clave de subida y la clave con la que se firman las instalaciones pueden ser diferentes. Si quieres que las versiones de GitHub y Google Play se actualicen entre sí, planifica el certificado de distribución común antes de publicar: una clave de subida no equivale automáticamente a la clave de firma de la app.

**No se ha generado ninguna clave ni firmado o subido ningún APK en esta preparación.**

## Documentación oficial

- [Firma y almacenes de claves](https://developer.android.com/studio/publish/app-signing)
- [Compilación para distribución](https://developer.android.com/build/build-for-release)
- [Verificar con apksigner](https://developer.android.com/tools/apksigner)
**La versión 1.0 se distribuye con el APK ya firmado por el autor. Conserva su clave de distribución para futuras actualizaciones.**
