## How To Run

To get this project running on your local machine, follow these steps:

### 1. Prerequisites
*   Android Studio (Iguana or newer recommended).
*   A physical Android device or Emulator (API 26+).
*   A **Gemini API Key** (Get one for free at [Google AI Studio](https://aistudio.google.com/)).

### 2. API Setup (!)
This project uses a secure method to handle API keys so that they are not exposed on GitHub.

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

---

## Application

Here are some screenshots of the application in operation.
First you have the homepage. The homepage shows the user a header with the most relevant information:

* **Total number of cars in collection**
* **Estimated value of the whole collection**
* **The number of different brands in a collection**


After that the user is greeted with a list of car tabs where each tab represents a user's car from his collection. Each car tab consists of:
* **The user's picture used to identify the car**
* **The name of the car**
* **The year of the real car**
* **The name of the brand that produced the model**
* **The color of the car**
* **The year of release of the model car**
* **The scale of the model car**
* **A stylized box with the name of the brand that produced the model**
* **The estimated value of the model**

<img width="640" height="1400" alt="Main" src="https://github.com/user-attachments/assets/488cfa7d-15e5-45d0-9c6d-9a4498c0a34c" />

---

The user has the option to click on any car tab so he can edit the data points of each car, excluding the picture. In this window the user also has the option to remove that car from his collection.

<img width="640" height="1400" alt="Edit" src="https://github.com/user-attachments/assets/73bff4f4-d7b3-4092-9f32-923d1d022be9" />
<br>


---

When the user wants to add another car to his collection he needs to press the "Scan a model car" on the homescreen. This button opens up the back camera of the phone allowing the user to take the picture of the car. After that the AI model analyzes the car and returns the aforementioned information about the model car.


<img width="640" height="1400" alt="Scan" src="https://github.com/user-attachments/assets/9bd8ea7e-4d8f-4356-94d6-3bc7bbd7a73c" />
