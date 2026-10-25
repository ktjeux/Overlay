SKETCH OVERLAY — PROTOTYPE ANDROID

IMPORTANT
This is a basic prototype. It has NOT been compiled or tested on a Samsung Galaxy Tab in this environment. The cloud-build workflow is included, but a successful build has not yet been confirmed.

CURRENT PROTOTYPE FEATURES
- Select one image using Android's image picker.
- Request Android permission to display over other apps.
- Display a floating image window.
- Adjust the floating window width and image opacity.
- Toggle a non-touchable mode so input can pass to the app underneath (device behavior may vary).

NOT YET IMPLEMENTED
- A thumbnail library for all images in a chosen folder.
- Pinch-to-zoom and pan gestures on the reference image.
- A persistent hide/show handle.
- Saving the last image, position, zoom and opacity.
- Full device testing and polished UI.

BUILD WITHOUT ANDROID STUDIO ON YOUR PC (CLOUD)
1. Create or sign in to a GitHub account at https://github.com/.
2. Create a NEW repository. It can be private.
3. Upload the contents of this project folder to the repository (the .github folder must be included).
4. Open the repository's Actions tab.
5. Select "Build Sketch Overlay APK" and click "Run workflow". Alternatively, push the files to the main branch to trigger a build.
6. When the workflow finishes successfully, open that workflow run and download the artifact named "Sketch-Overlay-APK".
7. Unzip the artifact to get app-debug.apk, transfer it to your Samsung tablet, and open it to install. Android may ask you to allow installation from that source.
8. Open Sketch Overlay and grant the Android "Display over other apps" permission when requested.

The GitHub Actions workflow builds the debug APK using Java 17, Android SDK and Gradle in a cloud runner. No Android Studio installation is required on your PC. GitHub account and internet access are required. The APK is not produced until you run the workflow successfully.

If the workflow fails, open the failed run, copy the first relevant error from the build log, and use it to troubleshoot. Do not assume the build succeeded until GitHub marks the workflow as successful.
