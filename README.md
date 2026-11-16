# MediaPlatform (com.gugugaga.mediaplatform)

## 1. Firebase setup (ek baar)
1. Console -> Authentication -> Email/Password ON. **Admin user manually banao** (email = ADMIN_EMAIL, password = tumhara lamba random code, 16+ chars).
2. Us user ka **UID copy** karo. `database.rules.json` me `ASLI_ADMIN_UID` (sab jagah) ko replace karo, phir Realtime Database -> Rules me paste + Publish.
3. Rules test: non-admin se `websites` me write fail hona chahiye; read chalna chahiye.
4. App Check enable karo (Play Integrity) - abuse kam karta hai. (Code me App Check SDK abhi nahi joda, console par enforcement tab ON mat karna jab tak SDK na jude.)
5. API key ko package + SHA-1 se restrict karo.
6. Sign-up UI app me kahin nahi hai; extra accounts rules ki wajah se bekaar rahenge.

## 2. GitHub secrets
- `GOOGLE_SERVICES_JSON_BASE64` = `base64 -w0 google-services.json`
- `ADMIN_EMAIL` = admin ka email (BuildConfig me jaata hai; code/password kahin nahi)
- (recommended) `DEBUG_KEYSTORE_BASE64`, `DEBUG_KEYSTORE_PASSWORD`, `DEBUG_KEY_ALIAS`, `DEBUG_KEY_PASSWORD`
  keystore: `keytool -genkeypair -v -keystore debug.keystore -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000`

## 3. Admin panel
Settings -> About -> "Version" par 7 baar tap (1.5 sec ke andar) -> code daalo.
- 3 galat code = 30s lock, phir 2 min, 10 min, 1 hr (sirf UX; asli security = lamba code + rules).
- 15 min inactivity par auto-logout; panel se bahar aate hi logout.
- Har write `config/version` bump karta hai, isliye users ko turant naya data milta hai.
- Naye site par `clickCount: 0` apne aap set hota hai (public +1 rule ke liye zaroori).

## 4. Ad-block remote control (RTDB `adblock/`)
`killSwitch`, `listUrl` (https hosts/ABP file, GitHub Pages par), `extraBlockedDomains`, `allowlist`, `cosmeticRules/{host_with_underscores}`, `siteModes/{host_with_underscores}` = off|normal|strict.
Badi list RTDB me mat rakho.

## 5. Build
Actions -> "Build Debug APK" -> artifact `app-debug-apk`. Local: `google-services.json` ko `app/` me rakho.

## 6. Naye features (v0.4.0) - setup notes
- **Room v2**: migration 1->2 included (cart.offlinePath, sites.searchUrlPattern, learned_pages). Fallback destructive migration abhi bhi ON hai (release se pehle hata do).
- **Update checker**: GitHub Releases se. Tag push karo (`git tag v0.4.1 && git push --tags`) -> `release.yml` APK attach karta hai. Private repo me API token ke bina check fail hoga.
- **FCM**: app sirf topics (`all`, `news`) subscribe karta hai. Bhejne ke liye Firebase Console ya server.
- **Widget / Shortcuts / Hindi**: strings-hi + QuickSearchWidget.

Repo: https://github.com/raholraj/mediaplatform
