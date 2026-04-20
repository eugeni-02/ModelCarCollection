## How To Run

To get this project running on your local machine, follow these steps:

### 1. Prerequisites
*   Android Studio (Iguana or newer recommended).
*   A physical Android device or Emulator (API 26+).
*   A **Gemini API Key** (Get one for free at [Google AI Studio](https://aistudio.google.com/)).

### 2. Security Setup (!)
This project uses a secure method to handle API keys so they are not exposed on GitHub.

1.  Open the project in Android Studio.
2.  In the root directory, locate or create a file named `local.properties`.
3.  Add your API key to the bottom of that file like this:

GEMINI_API_KEY=your_actual_key_here

# ModelCarCollection

**ModelCarCollection** is a modern Android application designed for die-cast and model car collectors. It leverages Google's **Gemini AI** to automatically identify cars from photos, providing details like brand, scale, and estimated market value, all while keeping your collection organized locally.

---

## Features

*   **AI-Powered Identification:** Snap a photo of any model car, and the app uses the Gemini 2.5 Flash API to identify the model, manufacturer, year, and current market value.
*   **Smart Collection Dashboard:** View real-time statistics of your entire collection, including total car count, estimated total value, and brand distribution.
*   **Offline Storage:** Uses Room Database for high-performance, local persistence of your collection and images.
*   **Manual Overrides:** The AI provides a starting point, but you can manually edit or refine any detail before adding it to your collection.
*   **Camera Integration:** Built using Jetpack CameraX for a smooth, high-quality photo capture experience.

---

## Tech Stack

*   **Language:** Java
*   **Architecture:** Model-View-Controller (MVC) with Room ORM.
*   **AI Integration:** Google Gemini 2.5 Flash API.
*   **UI Components:** Material Design, RecyclerView, ConstraintLayout.
*   **Jetpack Libraries:** CameraX, Room, Lifecycle (ViewModel/LiveData).
*   **Networking:** OkHttp for RESTful API communication.
