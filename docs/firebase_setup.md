# Firebase setup for Sprint 03

## What is already prepared in the project

- `LoginScreen` now compiles and can be wired to a real auth flow.
- The project includes an `AuthRepository` and `AuthViewModel`.
- `MainActivity` now checks auth state after the splash screen.
- If Firebase is still not configured, the app shows the login screen with a configuration message instead of crashing.

## What you must do in Firebase Console

1. Open <https://console.firebase.google.com/>.
2. Create a project or reuse the one for this app.
3. Add an Android app with package name `com.example.ruler`.
4. Download the generated `google-services.json`.
5. Enable `Authentication > Sign-in method > Email/Password`.

## What you must do in the repo

1. Copy the downloaded file to `app/google-services.json`.
2. Tell me when that file is in place.

`app/google-services.json` is intentionally ignored by Git in this project.
Keep it only in your local workspace and do not upload it to a public repository.

At that point I should do the final Firebase wiring step in Gradle:

- apply the `com.google.gms.google-services` plugin in the `app` module
- verify that Firebase resources are generated correctly
- run the build and validate login flow

## Why I did not activate the Google Services plugin yet

If I apply that plugin before `app/google-services.json` exists, the Android build is likely to fail. The project is left in a safe intermediate state so you can add the Firebase file first.
