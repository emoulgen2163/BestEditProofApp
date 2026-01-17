# Firebase Authentication Setup Guide

This guide will help you complete the Firebase Authentication setup for your Best Edit & Proof Android app.

## Prerequisites

1. **Firebase Console Account**: Sign up at [Firebase Console](https://console.firebase.google.com/)
2. **Android Studio**: Ensure you have the latest version
3. **Google Account**: For Google Sign-In configuration

---

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or select an existing project
3. Enter project name: **"Best Edit & Proof"** (or your preferred name)
4. Follow the setup wizard (disable Google Analytics if not needed)
5. Click **"Create project"**

---

## Step 2: Register Android App

1. In Firebase Console, click **"Add app"** > **Android**
2. Enter package name: `com.emoulgen.vibecodingapp` (check your `build.gradle` if different)
3. Enter app nickname: **"Best Edit Proof Android"**
4. Register app
5. **Download `google-services.json`**
6. Place `google-services.json` in your `app/` folder (not in `src/main/`)

---

## Step 3: Enable Authentication Methods

1. In Firebase Console, go to **Authentication** > **Sign-in method**
2. Enable the following providers:

### Email/Password
- Click **Email/Password**
- Toggle **Enable**
- Click **Save**

### Google Sign-In
- Click **Google**
- Toggle **Enable**
- Enter a project support email
- Click **Save**
- **Important**: Note down your **Web Client ID** (you'll need this later)

### Facebook Login
- Click **Facebook**
- Toggle **Enable**
- You'll need:
  - Facebook App ID
  - Facebook App Secret
- Follow the steps below for Facebook setup

---

## Step 4: Facebook Developer Console Setup

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Click **"My Apps"** > **"Create App"**
3. Choose **"Consumer"** or **"Business"** type
4. Fill in app details:
   - App Name: **"Best Edit & Proof"**
   - App Contact Email: Your email
5. Click **"Create App"**
6. In the dashboard:
   - Click **"Add Product"** > **"Facebook Login"** > **"Set Up"**
   - Choose **"Android"**
7. Configure Facebook Login:
   - **Package Name**: `com.emoulgen.vibecodingapp`
   - **Class Name**: `com.emoulgen.vibecodingapp.ui.screens.MainActivity`
   - **Key Hashes**: 
     - For debug: Run this command in terminal:
       ```bash
       keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
       ```
     - Enter the output as your Key Hash
   - Click **"Save"**
8. Go to **Settings** > **Basic**:
   - Note down your **App ID**
   - Note down your **App Secret** (click "Show")

---

## Step 5: Configure Firebase with Facebook

1. Go back to Firebase Console > **Authentication** > **Sign-in method** > **Facebook**
2. Paste:
   - **App ID**: From Facebook Developer Console
   - **App Secret**: From Facebook Developer Console
3. Copy the **OAuth redirect URI** shown in Firebase
4. Go back to Facebook Developer Console:
   - **Settings** > **Basic** > **Add Platform** > **Website**
   - In **Site URL**, paste the OAuth redirect URI from Firebase
5. Go to **Products** > **Facebook Login** > **Settings**:
   - Add the OAuth redirect URI to **Valid OAuth Redirect URIs**
6. Save all changes

---

## Step 6: Update Your Android App Configuration

### 6.1 Update `strings.xml`

Open `app/src/main/res/values/strings.xml` and replace placeholders:

```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```

**Where to find:**
- `default_web_client_id`: Firebase Console > Project Settings > Your Apps > Web App > Web Client ID
- `facebook_app_id`: Facebook Developer Console > Settings > Basic > App ID
- `facebook_client_token`: Facebook Developer Console > Settings > Advanced > Client Token

### 6.2 Get SHA-1 and SHA-256 Certificates

1. For **Debug** keystore, run in terminal:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
2. Copy **SHA-1** and **SHA-256** values
3. Go to Firebase Console > Project Settings > Your Android App
4. Scroll to **"SHA certificate fingerprints"**
5. Click **"Add fingerprint"** and paste both SHA-1 and SHA-256

---

## Step 7: Verify Dependencies

Ensure your `app/build.gradle.kts` has these dependencies (should already be added):

```kotlin
implementation(platform(libs.firebase.bom))
implementation(libs.firebase.auth)
implementation(libs.play.services.auth)
implementation(libs.facebook.android.sdk)
```

And the plugin:
```kotlin
id("com.google.gms.google.services")
```

---

## Step 8: Test Authentication

1. **Build and run** your app
2. Test each authentication method:

### Email/Password
- Click **Sign Up** and create an account
- Sign out, then **Sign In** with the same credentials

### Google Sign-In
- Click **Login with Google**
- Select a Google account
- Verify you're redirected to Dashboard

### Facebook Login
- Click **Login with Facebook**
- Authorize the app
- Verify you're redirected to Dashboard

---

## Troubleshooting

### Google Sign-In Issues
- **Error: 10**: Make sure SHA-1/SHA-256 are added in Firebase Console
- **Error: 12500**: Verify `default_web_client_id` in `strings.xml` matches Firebase Web Client ID

### Facebook Login Issues
- **Error: Invalid key hash**: Add your debug/release key hash in Facebook Developer Console
- **Error: App not setup**: Verify OAuth redirect URI is added in Facebook settings

### General Issues
- **"google-services.json not found"**: Ensure file is in `app/` folder (not `app/src/main/`)
- **Build errors**: Clean and rebuild project (`Build > Clean Project`, then `Build > Rebuild Project`)

---

## Security Notes

⚠️ **Important**: 
- Never commit `google-services.json` if it contains production credentials
- Add `google-services.json` to `.gitignore` if working with multiple environments
- Keep Facebook App Secret secure (only use in Firebase Console, never in code)

---

## Next Steps

Once authentication is working:
1. Test all three sign-in methods
2. Verify user data is correctly stored in Firebase Authentication console
3. Test sign-out functionality
4. Implement password reset (if needed)
5. Add email verification (optional)

---

## Support

If you encounter issues:
1. Check Firebase Console > Authentication > Users (to see if users are being created)
2. Check Logcat in Android Studio for error messages
3. Verify all configurations match the guide above
4. Test with a fresh Firebase project if issues persist

---

**Good luck with your implementation! 🚀**


