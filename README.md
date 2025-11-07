# TravelBook

## Project setup
1. Create new Android project: Name `spike-compose`, Empty Activity.
2. Add dependencies in `app/build.gradle` (see dependency block in README).
3. Add libraries versions to `libs.versions.toml` (navigationCompose, lifecycleViewmodelCompose).

## Run
1. Add `google-services.json` from Firebase.
2. Configure Places API key and AI API keys as environment variables / properties.
3. Build & run.

## Features
- Home: list saved locations
- Create: add new location (Places autocomplete suggested)
- Edit: change, delete or mark locations visited
- Firebase Firestore for persistence
- AI endpoint for suggestions (activities/POIs)

## Next steps
- Add Auth (Firebase)
- Add Maps preview & Places Autocomplete
- Add Hilt for DI and organize network modules
- Add tests & CI
