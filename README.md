# Volt Runner: getting it onto Google Play

This folder holds everything you need:

| Folder / file | What it is |
|---|---|
| `android/` | The Android Studio project (open this one) |
| `www/` | The game itself (HTML/JS) — the source the app is built from |
| `store/` | Store icon, banner, 6 screenshots, listing text, privacy policy |
| `assets/` | Source art for the app icon and splash screen |

Package name: **`com.voltrunner.game`**. Change it now if you want something else (it can't be changed after your first upload). It appears in `capacitor.config.json`, `android/app/build.gradle` (`namespace` and `applicationId`) and the Java folder `android/app/src/main/java/com/voltrunner/game`.

---

## Step 1: Install Android Studio (one time)
1. Download **Android Studio** from developer.android.com/studio and install it with the default options.
2. Start it once and let it download the Android SDK.

## Step 2: Open and test the project
1. Unzip this folder somewhere simple, like `C:\Projects\volt-android`.
2. In Android Studio: **File > Open** and pick the **`android`** folder (not the top folder).
3. Wait for "Gradle sync" to finish (the first time takes several minutes).
4. Plug in your Android phone with **USB debugging** on (Settings > About phone > tap *Build number* 7 times, then Developer options > USB debugging), or create an emulator under **Device Manager**.
5. Press the green ▶ **Run** button. The game should launch on the phone.

> Leaderboards won't work yet. That's normal until Step 5.

## Step 3: Create your Play Console account and app
1. Go to play.google.com/console, sign up (one-time **$25** fee) and complete identity verification.
2. **Create app** → name "Volt Runner", **Game**, **Free**, then accept the declarations.

## Step 4: Build the release bundle (.aab)
1. In Android Studio: **Build > Generate Signed App Bundle or APK > Android App Bundle**.
2. **Create new…** key store. Save the `.jks` file and passwords somewhere safe, and back them up. This is your *upload key*.
3. Choose **release**, then **Create**. The file appears in `android/app/release/app-release.aab`.
4. Every new upload needs a higher number: in `android/app/build.gradle` bump `versionCode 1` → `2` → `3`… (and `versionName` if you like).

## Step 5: Set up Google Play Games leaderboards
In Play Console, open your app:

1. **Grow users > Play Games Services > Setup and management > Configuration**. Choose **"No, my game doesn't use Google APIs"**, enter "Volt Runner", then **Create**.
2. **Edit Properties**: fill in the display name (the other fields can wait until publishing).
3. **Credentials > Add credential > Android**. If asked, configure the OAuth consent screen first (follow the link and publish it).
   - Click **Create OAuth client**: type **Android**, package `com.voltrunner.game`, and the **SHA-1 fingerprint**.
   - Make **two** credentials: one with the SHA-1 from **Test and release > Setup > App signing** (Play's app signing key, used by store installs), and one with your **debug** SHA-1 so testing from Android Studio works. To get the debug one: in Android Studio's Terminal run
     `keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android`
4. Copy the **Project ID** (the long number under the game name on the Configuration page) into
   `android/app/src/main/res/values/strings.xml` → replace `0000000000`.
5. **Leaderboards > Create leaderboard**: make these 21:

   | Name | Format | Ordering |
   |---|---|---|
   | Levels cleared | Numeric | Larger is better |
   | Sector 1 (levels 1–10) total time | Time | Smaller is better |
   | Sector 2 (levels 11–20) total time | Time | Smaller is better |
   | … one per 10 levels … | | |
   | Sector 20 (levels 191–200) total time | Time | Smaller is better |

6. Copy each leaderboard's **ID** into
   `android/app/src/main/assets/public/leaderboards.js` (and the same file in `www/` if you keep editing the game).
7. **Testers** tab: add your own Google account and your testers' emails, or the closed-testing release track. Until the game is published, only listed testers can sign in.
8. Rebuild the `.aab` (Step 4, with a higher `versionCode`).

## Step 6: Closed test (required for new personal accounts)
1. **Test and release > Testing > Closed testing > Create track**, upload the `.aab`, and add an email list with **at least 12 testers** (aim for 15–20, since people drop out).
2. Testers must opt in through the link Play gives you and **stay opted in for 14 days in a row**. Ask them to actually play and leave feedback.
3. Meanwhile, finish the dashboard tasks:
   - **Store listing**: paste the text from `store/listing.md` and upload the images in `store/`.
   - **Privacy policy**: put `store/privacy-policy.html` online (fill in your name, email and date first). Free options include GitHub Pages and Google Sites. Then paste the link.
   - **Content rating** questionnaire: mild cartoon peril, no violence against people, no user chat, no purchases.
   - **Target audience**: choose your age groups. Picking under-13 adds Google's Families policy requirements.
   - **Data safety**: the game itself collects nothing. Play Games Services is Google's SDK, so check Google's data-safety guidance for Play Games Services in the Play SDK Index and declare what it lists.
   - **Ads**: No ads.

## Step 7: Go live
1. After 14 days: **Dashboard > Apply for production** and answer the questions about your test.
2. Once approved: in **Play Games Services**, click **Publish game** *first*, then publish your production release. That order matters, or leaderboards break for regular players.

---

### Updating the game later
Edit files in `www/`, then copy them into the app:
- Easy way: copy `www/*` over `android/app/src/main/assets/public/`.
- Or with Node.js installed: run `npm install` then `npx cap sync android` in this folder.
Then bump `versionCode` and build a new `.aab`.

### How the leaderboards work
- Every level's best time is saved on the phone (shown in the level menu).
- When all 10 levels of a sector are cleared, their total best time is posted to that sector's board, and it improves as you beat your bests.
- "Levels cleared" posts how many levels you've beaten.
- 🏆 in the game opens the boards. Google Play allows at most 70 leaderboards per game, which is why times are grouped by sector instead of one board per level. If you add levels later, add one sector board per 10 levels.
