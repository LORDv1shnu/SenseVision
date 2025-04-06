# SenseVision

**SenseVision** is an Android application designed to assist visually impaired individuals by capturing images, sending them to an AI server for captioning, and speaking the captions aloud using Text-to-Speech (TTS). It utilizes CameraX and Kotlin on the Android side and connects to a Flask-based Python server on the backend.

---

## Features

- **Automatic Image Capture:** Captures images at regular intervals (every 3 seconds) with Start/Stop auto-capture functionality.
- **Manual Image Capture:** Allows manual capture and sending of images.
- **AI-Powered Captioning:** Sends images to a Flask server where an AI model (placeholder or future GITBase integration) generates captions.
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
- **AI Captioning:** Placeholder functionality ready for integration with a model (e.g., GITBase or another image captioning model)

---

## Setup Instructions

### Backend Server (Flask)
1. **Install Dependencies:**
   ```bash
   pip install flask flask-cors pillow
