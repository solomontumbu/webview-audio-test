# WebView Background Audio Test

A minimal Android WebView app to check whether `https://music.youtube.com/` continues playing when you press Home, switch apps or lock your phone.

## What this does

- Displays the Android WebView provider and version actually used by the APK.
- Loads the **real YouTube Music website** in Android WebView, without impersonating Chrome/Brave.
- Includes a separate offline looping sound to test WebView background audio independently of YouTube.
- Does **not** call `WebView.onPause()` / `pauseTimers()` when the app is backgrounded.
- Lets you record pass/fail observations inside the app; tap **Results** to review them.

## Build the APK (Windows / Android Studio)

This zip contains editable Android Studio source files, **not a precompiled APK**. An Android SDK and Gradle are not installed in the environment in which the project was generated, so it could not be compiled here.

1. Install [Android Studio](https://developer.android.com/studio) on a Windows PC.
2. Unzip this folder and open `WebViewAudioTest` in Android Studio.
3. Let Android Studio install Android SDK Platform 35 and sync Gradle. The project is configured for Android Gradle Plugin 8.7.3 and Gradle 8.9.
4. **If Android Studio says the Gradle wrapper is missing**, open Android Studio's Terminal and execute `gradle wrapper --gradle-version 8.9` if Gradle is installed. Alternatively, create a blank **Empty Views Activity** Java project in Android Studio and copy the provided `app/src/main/` folder, `app/build.gradle`, and app manifest into it. You can keep the new project's Gradle versions.
5. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. The debug APK should be found at `app/build/outputs/apk/debug/app-debug.apk`.
7. Transfer it to your Android phone and install it, allowing installation from the relevant source if Android asks.

## Test procedure

1. Open **WebView Audio Test**, note the provider at the top.
2. Choose **Control Audio**, tap Play, then **Send to background**.
3. Listen while another application is foregrounded, return to the test app, tap **Log Result** and select continued or stopped.
4. Do the same test while the screen is locked.
5. Choose **YouTube Music**, wait for the site to load, play a song, and repeat the app-switch and screen-lock tests.
6. If the site fails or Google sign-in is blocked, record **Page or Google sign-in failed**.
7. Open **Results** to compare all observations.

## Interpret the results

| Control audio | YouTube Music | Likely meaning |
|---|---|---|
| Keeps playing | Keeps playing | Background playback works in this test on this device; not a reliability guarantee. |
| Keeps playing | Stops / sign-in fails | WebView can play audio in background, but YouTube Music has its own limitations. |
| Stops | Stops | The WebView or Android lifecycle/power policy may suspend media. |
| Stops | Keeps playing | Site/player behavior differs; repeat each test to rule out inconsistent state. |

Important: Google sign-in through embedded Android WebViews is generally blocked by Google's OAuth security policy. This test intentionally **does not spoof a user agent or bypass any site restrictions**. YouTube Music may also have Premium, playback, or service restrictions. You do not need to log in to perform the offline control-audio test.

## Safety and privacy

The test is intended for local testing only. It does not collect analytics, extract user account data, or send data to a custom server. YouTube Music itself uses Google services according to its own privacy rules. The app's test results are stored locally in Android SharedPreferences.

## Technical details

- Java; no third-party Android libraries.
- Android min SDK 26 / target SDK 35.
- INTERNET permission only.
- Background audio is not forced through a foreground service. That is deliberate: the test examines normal WebView behavior.
- Existing controls: YouTube Music, Control Audio, Reload, Send to Background, Log Result, Results.
