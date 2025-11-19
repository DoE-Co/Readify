

#  **Readify — Japanese Pop-Up Dictionary App (Prototype)**

**Readify** is a mobile-app prototype designed to turn a user’s phone into a seamless Japanese language-learning environment.
The app overlays instant dictionary pop-ups for Japanese text across apps like **YouTube**, **Twitter**, and **YouTube Music**, enabling rapid word lookup *without* interrupting the flow of reading or listening.

This project was developed for the Mobile Programming course at UW-Madison.
---

## **Features**

* **Tap-to-Translate Overlay**
  Tap any Japanese word on screen to instantly view its dictionary definition.

* **App-Wide Support**
  Works across:

  * YouTube subtitles
  * Twitter posts
  * YouTube Music lyrics
  * Browser...

* **Minimal Disruption**
  Enables users to stay inside the app they are already using instead of switching to a dictionary app.

* **Beginner-Friendly**
  Designed to remove friction from vocabulary learning and help learners stay immersed.

---

## **Motivation**

as a Japanese learners I often had to interrupt my reading or listening when I came across an uknown word by:

* copying text
* switching apps
* pasting into a dictionary
* switching back

Readify seeks eliminates this friction.
The goal is simple:
**Make vocabulary learning “in the wild” as smooth and natural as possible.**

---

## **Tech Stack**

* **Kotlin (Android)**
* **Camera + OCR support**
* **Custom UI overlays**
* **Dictionary API / local dictionary integration**
* Jetpack Compose 

---

## **How It Works (Overview)**

1. User opens any supported app (e.g., YouTube).
2. Readify listens for text rendered on-screen (subtitles, tweets, lyrics).
3. User taps a word → the app identifies Japanese tokens.
4. Readify fetches dictionary definitions and displays a non-intrusive pop-up.
5. User closes the pop-up and continues reading/listening.

---

## **Screenshots **
<img width="456" height="1081" alt="image" src="https://github.com/user-attachments/assets/c9152946-9898-475d-af72-cdc27044d06d" />
<img width="443" height="1058" alt="image" src="https://github.com/user-attachments/assets/5921069c-3438-4d0f-a4e1-24fdc72c313b" />
<img width="457" height="1094" alt="image" src="https://github.com/user-attachments/assets/869c19ad-49ad-4292-aaaa-6ee80c318d4d" />
<img width="446" height="1073" alt="image" src="https://github.com/user-attachments/assets/219152cd-b7a3-4026-8301-3ba8975b927d" />
<img width="453" height="1052" alt="image" src="https://github.com/user-attachments/assets/321f3a71-b70a-402d-b84d-c2cc1dc17904" />


---

## **Current Status**

This is a **functioning prototype** 
Future possible improvements include:

* More robust OCR for images or non-standard fonts
* Word-frequency lists and personalized review tools
* Pitch accent display
* Example sentences from corpora
* Offline dictionary support
* Integration with the Choruser prosody tool (future idea)


