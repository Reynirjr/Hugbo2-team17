# WJOOPS Customer (Android)

Skeleton Kotlin + Jetpack Compose app for restaurant ordering.

## Open & run

1. Open the folder `Whoops.mobile/` in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator/device.

The project includes a Gradle Wrapper (`gradlew` / `gradlew.bat`).

If you build from the command line, ensure the Android SDK is configured via either:

- `ANDROID_HOME` / `ANDROID_SDK_ROOT`, or
- `Whoops.mobile/local.properties` with `sdk.dir=...`

## Where to plug the real backend

- Base URL is currently set in `app/build.gradle.kts` as `BuildConfig.BASE_URL` (debug).
- Networking layer is stubbed via Retrofit interface `com.wjoops.customer.data.network.WjoopsApiService`.
- Repositories currently return mock data; swap implementations to call the API service.

## Notes

- Phone login is mocked: any 4–6 digit OTP succeeds.
- Location distance is shown if permission is granted and a last-known location exists.
- DataStore persists auth + last basket snapshot.
