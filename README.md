# TravelBook

**TravelBook** is a mobile application designed to help travelers plan, document, and enhance their travel experiences. The app combines intuitive location tracking, notes management, and smart suggestions to make trip planning and journaling seamless.  

## Key Features

- **Add & Edit Locations**: Save places you’ve visited or plan to visit with details such as country, dates, notes, and status (Planned / Visited).  
- **AI-powered Travel Suggestions**: Integrated AI provides smart recommendations and insights for each location, helping you plan better trips or enhance your travel journal.  
- **Google Maps Integration**: View and visualize locations on a map for easy navigation and reference.  
- **Notes Management**: Add, edit, or delete notes for each location with support for multiple entries per location.  
- **Firebase Integration**: Store and sync your travel data securely across devices using Firebase Firestore.  
- **Interactive UI Components**: Includes date pickers, confirmation dialogs, status toggles, and loading overlays for a smooth user experience.  

## Tech Highlights

- **AI Integration**: Suggests travel ideas and insights for your locations.  
- **Firebase Firestore**: Cloud storage for locations, notes, and user data.  
- **Google Maps**: Displays location maps with markers and zoom capabilities.  
- **Jetpack Compose**: Modern, declarative UI for responsive and interactive design.

## Setup

Follow these steps to set up the project locally:

1. **Clone the repository**:  
   ```bash
   git clone https://github.com/Marcoasf10/TravelBook.git
   cd TravelBook

2. **Open in Android Studio**:  
- Make sure you have Android Studio Arctic Fox or later installed.
- Select Open an existing project and choose the cloned repository folder.
- Install dependencies: Android Studio will automatically sync Gradle and download necessary dependencies.

3. **Configure Firebase**:
- Create a Firebase project and enable Firestore and Firabase AI Logic.
- Download the google-services.json file and place it in the app/ directory.

4. **Configure Google Maps**:
- Create an account in `Google for Developers` if you don't have one already. Generate an API key with `Maps JavaScript API`.
- Create a secrets.properties file under root with the entry: "MAPS_API_KEY={YOUR_API_KEY}"

5. **Run the app**:
- Connect an Android device or start an emulator.
- Press Run in Android Studio to build and launch the app.
