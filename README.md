# Dark App

Native Android prototype for smart screen dimming.

## Prototype Features

- Nonlinear dimming scale for finer control in low light.
- Full-screen non-touchable Android overlay.
- Foreground notification with a stop action.
- Reading, Night, and Ultra presets.
- Deep preset with stronger dimming up to a 96% overlay alpha.
- Blackout preset with a dedicated 99% overlay alpha.
- Optional brightness boost through Android system brightness.
- Neutral, Warm, and Red tone filters.
- Jetpack Compose Material 3 control screen.

Brightness boost requires Android's "modify system settings" permission. The app can set brightness up to the device maximum, but it cannot exceed the physical display limit.

## Build

```bash
ANDROID_HOME="$HOME/Library/Android/sdk" \
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
./gradlew testDebugUnitTest assembleDebug
```

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.
