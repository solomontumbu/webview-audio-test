# Build your WebView Audio Test APK online (no Android Studio)

This archive includes a GitHub Actions workflow that compiles the Android project in the cloud. The result is an Android **debug APK** that can be installed on a test device. No Android Studio, locally installed Android SDK, or locally installed Gradle is required.

## Steps from a browser

1. Go to https://github.com and sign in (or create an account).
2. Click **New repository**. Name it `webview-audio-test`. Select **Private** if you prefer. Create the repository without initializing it with a README.
3. Extract this ZIP on your computer.
4. In the new GitHub repository select **uploading an existing file** (or **Add file > Upload files**).
5. Drag **the contents of the extracted folder** into GitHub. That includes `.github`, `app`, `gradle`, `build.gradle`, `settings.gradle`, and `gradle.properties`. Do not upload just the ZIP: GitHub Actions needs the extracted project files.
6. Commit the uploaded files. Confirm the repository has `.github/workflows/build-apk.yml` and `app/build.gradle` at the repository root (not inside an extra parent directory).
7. Open the **Actions** tab and select **Build WebView Audio Test APK**. An upload to the `main` or `master` branch should start a build automatically. If not, choose **Run workflow** (you may need to enable Actions for the repository).
8. Open the successful workflow run and scroll to **Artifacts**. Download **WebView-Audio-Test-APK**.
9. Extract the downloaded artifact ZIP, transfer `app-debug.apk` to your Android phone, and install. Android may ask to allow app installs from the chosen file manager/browser.

## Test

1. Open the app and try **Control Audio** first.
2. Play the looping tone. Tap **Send to Background** and check whether the audio continues. Return and log the result.
3. Repeat with the screen locked.
4. Try **YouTube Music** and compare results. Google sign-in may be blocked in embedded Android WebView. YouTube Music's playback behavior is not guaranteed.

## Notes

- The included `.github/workflows/build-apk.yml` deliberately calls `gradle` directly instead of `./gradlew` because the original source archive does not include a Gradle Wrapper JAR. The workflow installs Gradle 8.9 on the cloud runner.
- If the workflow fails, read the red build step's logs for the exact error. No APK is precompiled in this ZIP.
- This is a development **debug APK**, not a Play Store release build. For public distribution, configure a release build, app signing, and review applicable YouTube and Google policies.
- The hosted CI runner downloads tools and dependencies from external services; GitHub Actions access or usage limits may apply to your account.
