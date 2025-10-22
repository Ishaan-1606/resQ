# ResQ - Android Frontend

This project is a polished Android frontend for the ResQ IoT+ML system, built with Jetpack Compose and modern Android development practices.

> Your Safety, Reimagined.

## Project Overview

This application serves as the user-facing interface for the ResQ system. It connects to a backend via REST and Socket.IO to display real-time sensor data, AI-driven disaster predictions, system alerts, and allows for manual control of system actuators.

**Note:** This is a frontend-only project. All backend logic is handled by a separate server.

## Getting Started

Follow these steps to get the project running on your local machine for development and testing purposes.

### Prerequisites

*   Android Studio (latest stable version recommended)
*   An Android Emulator or a physical Android device (API 26+)

### Setup Instructions

1.  **Open the Project:** Open the `ResQ` project directory in Android Studio.

2.  **Add Required Assets:** The app's source code requires a few media assets that are not included in source control. You must add them manually:
    *   **Logo:** Place your `logo.png` file inside the `app/src/main/res/drawable/` directory.
    *   **Lottie Animations:** Create a `raw` resource directory at `app/src/main/res/raw/`. Then, place the following JSON animation files inside it:
        *   `login.json`
        *   `signup.json`
        *   `loading.json`

3.  **Sync Gradle:** Once the project is open, Android Studio may prompt you to sync the Gradle files. If not, click the "Sync Project with Gradle Files" button in the toolbar. This will download all the required dependencies.

4.  **Run the App:** Select a run configuration (usually `app`) and a target device (emulator or physical), then click the "Run" button.

## Configuration

For the app to connect to the backend, you will need to configure the server's base URL. This can be done in the **Settings** screen within the app.

*   **Default for Local Backend:** The default URL is `http://10.0.2.2:5000`, which is specifically configured to allow an Android emulator to connect to a server running on your local machine (localhost).
*   **Physical Device:** If using a physical device, you must be on the same Wi-Fi network as your backend server and use your computer's local network IP address (e.g., `http://192.168.1.10:5000`).
