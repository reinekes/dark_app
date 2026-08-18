# Smart Screen Dimmer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a native Android prototype for smart screen dimming with a nonlinear slider, warm filter presets, overlay service, and first git commit.

**Architecture:** A small Compose Android app owns the control UI. Pure Kotlin dimming/domain functions are tested with JVM unit tests. A foreground Android service renders the dimming overlay using `WindowManager`.

**Tech Stack:** Kotlin, Android Gradle Plugin, Jetpack Compose Material 3, Android foreground service, JUnit 4.

**Spec:** `docs/superpowers/specs/2026-08-18-smart-screen-dimmer-design.md`

## Global Constraints

- Application id: `com.reinekes.darkapp`.
- Minimum SDK: 24.
- Target SDK: 34.
- Overlay permission: `android.permission.SYSTEM_ALERT_WINDOW`.
- Foreground service type: `specialUse` for the active dimming overlay.
- User-facing dim percent is 0-100; overlay alpha is derived by `DimMath.alphaForPercent`.
- The overlay must be `FLAG_NOT_TOUCHABLE` so it does not block normal phone use.

---

### Task 1: Project Shell And Dimming Math

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `app/build.gradle.kts`
- Create: `app/src/test/java/com/reinekes/darkapp/dimming/DimMathTest.kt`
- Create: `app/src/main/java/com/reinekes/darkapp/dimming/DimMath.kt`

**Interfaces:**
- Produces: `object DimMath { fun alphaForPercent(percent: Int): Float }`

- [x] **Step 1: Write the failing test**

```kotlin
@Test
fun alphaCurveIsGentleAtLowValuesAndStrongAtHighValues() {
    assertEquals(0f, DimMath.alphaForPercent(0), 0.0001f)
    assertEquals(0.055f, DimMath.alphaForPercent(10), 0.012f)
    assertEquals(0.78f, DimMath.alphaForPercent(100), 0.0001f)
}
```

- [x] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest --tests '*DimMathTest'`
Expected: FAIL because `DimMath` is not defined.

- [x] **Step 3: Write minimal implementation**

```kotlin
object DimMath {
    fun alphaForPercent(percent: Int): Float {
        val normalized = percent.coerceIn(0, 100) / 100f
        return 0.78f * normalized.toDouble().pow(1.15).toFloat()
    }
}
```

- [x] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest --tests '*DimMathTest'`
Expected: PASS.

### Task 2: Domain Model And Overlay Color

**Files:**
- Create: `app/src/main/java/com/reinekes/darkapp/dimming/DimPreset.kt`
- Create: `app/src/main/java/com/reinekes/darkapp/dimming/DimFilter.kt`
- Create: `app/src/main/java/com/reinekes/darkapp/dimming/OverlayColor.kt`
- Create: `app/src/test/java/com/reinekes/darkapp/dimming/DimPresetTest.kt`
- Create: `app/src/test/java/com/reinekes/darkapp/dimming/OverlayColorTest.kt`

**Interfaces:**
- Produces: `enum class DimFilter`
- Produces: `enum class DimPreset`
- Produces: `object OverlayColor { fun argb(percent: Int, filter: DimFilter): Int }`

- [x] **Step 1: Write failing tests for presets and colors**
- [x] **Step 2: Run tests to verify they fail**
- [x] **Step 3: Add the model and color mapping**
- [x] **Step 4: Run tests to verify they pass**

### Task 3: Android Overlay Service

**Files:**
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/reinekes/darkapp/overlay/DimOverlayService.kt`

**Interfaces:**
- Consumes: `OverlayColor.argb(percent, filter)`
- Produces: service actions `ACTION_START`, `ACTION_STOP`, extras `EXTRA_PERCENT`, `EXTRA_FILTER`.

- [x] **Step 1: Add manifest permissions and service declaration**
- [x] **Step 2: Implement foreground service notification**
- [x] **Step 3: Implement non-touchable overlay window**
- [x] **Step 4: Verify with `./gradlew assembleDebug`**

### Task 4: Compose Prototype UI

**Files:**
- Create: `app/src/main/java/com/reinekes/darkapp/MainActivity.kt`
- Create: `app/src/main/java/com/reinekes/darkapp/DimmerApp.kt`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/values-night/themes.xml`

**Interfaces:**
- Consumes: service actions from `DimOverlayService`.
- Produces: the app's first-screen prototype.

- [x] **Step 1: Build the control UI**
- [x] **Step 2: Wire permission checks and settings intent**
- [x] **Step 3: Wire enable, stop, slider, presets, and filters to service intents**
- [x] **Step 4: Verify with `./gradlew testDebugUnitTest assembleDebug`**

### Task 5: First Commit

**Files:**
- Modify: git repository metadata.

**Interfaces:**
- Produces: initial commit on the local repository with remote `origin` set to `https://github.com/reinekes/dark_app.git`.

- [ ] **Step 1: Initialize git**
- [ ] **Step 2: Add remote origin**
- [ ] **Step 3: Stage project files**
- [ ] **Step 4: Commit with `feat: create smart screen dimmer prototype`**
