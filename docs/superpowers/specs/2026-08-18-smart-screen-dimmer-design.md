# Smart Screen Dimmer Design

**Goal:** Build a native Android prototype that dims the display below the system brightness floor with a comfortable, easy-to-control scale.

## Product Shape

The app opens directly into the control surface. The main interaction is a large dimming slider with immediate feedback, plus three presets: Reading, Night, and Ultra. The UI includes an enable switch, a warm-filter selector, and a permission panel for Android's display-over-other-apps approval.

## Dimming Model

The user-facing value is 0-100 percent. The overlay alpha is nonlinear: low values change gently for dark-room comfort, while high values still reach a strong dim. Presets are stored as simple domain values so UI, notification actions, and future quick settings can share one model.

## Android Behavior

The prototype uses `SYSTEM_ALERT_WINDOW` and a foreground service that owns a full-screen `TYPE_APPLICATION_OVERLAY` view. The overlay is not touchable, so regular app interaction continues underneath. A foreground notification provides a visible running state and a fast stop action.

## UI

Jetpack Compose Material 3 is used for the app screen. The screen favors a utilitarian control panel: current percentage, slider, preset buttons, filter choices, status cards, and permission/action buttons. Copy is short and practical.

## Testing

Unit tests cover the dimming curve, preset model, and overlay color generation. A debug build verifies that the prototype compiles as an Android app.
