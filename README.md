# SenseVision

**SenseVision** is an Android application designed to assist visually impaired individuals by capturing images, sending them to an AI server for captioning, and speaking the captions aloud using Text-to-Speech (TTS). It utilizes CameraX and Kotlin on the Android side and connects to a Flask-based Python server on the backend.

---

## Features

- **Automatic Image Capture:** Captures images at regular intervals (every 3 seconds) with Start/Stop auto-capture functionality.
- **Manual Image Capture:** Allows manual capture and sending of images.
- **AI-Powered Captioning:** Sends images to a Flask server where an AI model (placeholder or future integration) generates captions.
- **Text-to-Speech:** Reads the generated caption aloud for the user.
- **Live Status Updates:** Displays real-time status messages and the latest image caption on the UI.

---

## Technologies Used

### Android App
- **Language:** Kotlin
- **Camera API:** CameraX
- **Networking:** HTTP communication using `HttpURLConnection`
- **Text-to-Speech:** Android TTS API
- **UI:** XML layouts

### Backend Server
- **Framework:** Python Flask
- **CORS:** Enabled via Flask-CORS for cross-origin requests
- **AI Captioning:** Placeholder functionality ready for integration with a model (e.g., future image captioning model)

---

## Setup Instructions

### Backend Server (Flask)
1. **Install Dependencies:**
   ```bash
   pip install flask flask-cors pillow

    Run the Server:
    bash

    python app.py
    Server Listening:
    The server will listen on:
    http://<your-ip>:5000/upload

Android App

    Clone the Repository:
    bash

git clone https://github.com/LORDv1shnu/SenseVision.git
Open in Android Studio:
Open the project and ensure that the server URL in MainActivity.kt is updated to reflect your server’s IP:
kotlin

    val url = URL("http://<your-ip>:5000/upload")
    Run the App:
    Build and run the app on your Android device. Make sure you grant the necessary camera and network permissions.

Team Members

    Vishnu
    [Member 2 Name]
    [Member 3 Name]
    [Member 4 Name]

To Do

    Integrate a real image captioning model.
    Improve UI for better accessibility.
    Add support for additional languages.
    Implement offline mode and error handling improvements.

Demo Screenshots

Coming Soon...
Inspiration

This project was created with the goal of enhancing the independence and accessibility of visually impaired individuals by leveraging affordable mobile technology and open-source AI.
License

This project is open source and free to use for educational purposes.