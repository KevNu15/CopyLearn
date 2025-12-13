# CopyLearn – Android Application

CopyLearn is a **native Android application** developed in **Android Studio using Kotlin and XML layouts**. The application is **fully implemented** and provides functionality for **capturing, managing, and storing documents with OCR (Optical Character Recognition)**, persisting information through a REST API.

This README describes the **final state of the project** and focuses exclusively on the application, its screens, its functionalities, and the API it consumes, in accordance with university project requirements.

---

## Project Overview

CopyLearn is designed as a lightweight document management solution that allows users to:

- Capture or select document images
- Extract text automatically using OCR
- Store documents with basic metadata
- View, search, and delete stored documents

The project emphasizes correct Android architecture, API consumption, and clean separation of responsibilities.

---

## Application Screens

The application consists of **three (3) main screens**:

### 1. Home Screen

The Home screen is the entry point of the application. It provides direct navigation to:

- Create a new document
- View the list of stored documents

The design is intentionally simple to highlight the core functionalities of the system.

---

### 2. New Document Screen

This screen allows the user to create and store a new document. Available functionalities include:

- Entering a document title
- Selecting a capture date
- Selecting an image from the device
- Extracting text from the image using OCR
- Saving the document through the API

The recognized text is displayed prior to saving, allowing verification of the OCR process.

---

### 3. Document List Screen

This screen displays all documents retrieved from the backend API. From this view, the user can:

- View stored documents
- See basic information such as title and capture date
- Search documents by title or recognized text
- Delete documents

The list is implemented using RecyclerView following standard Android development practices.

---

## Core Functionalities

- Image selection from device storage
- Optical Character Recognition (OCR)
- Document creation and deletion
- Document listing and search
- REST API integration

All data persistence is handled by the backend API.

---

## Technical Stack

- Programming Language: Kotlin
- User Interface: XML layouts
- Architecture: MVC-style structure
- Networking: Retrofit with Coroutines
- Lists: RecyclerView
- OCR: Image-based text recognition

---

## Backend API Integration

The application consumes the **CopyLearn REST API**, which provides all persistence and retrieval operations for documents and master data.

### API Endpoints Used

**Health Check**
```
GET /
```
Returns basic API information and available endpoints.

**Documents**
```
GET    /documents                // Retrieve all documents
GET    /documents/:id            // Retrieve document by ID
GET    /documents/search/:query  // Search documents by title or OCR text
POST   /documents                // Create new document
PUT    /documents                // Update existing document
DELETE /documents                // Delete document
```

**Languages**
```
GET /languages                   // Retrieve available languages
```

---

## Document Data Structure

```json
{
  "documentId": "uuid",
  "title": "Document title",
  "captureDate": "YYYY-MM-DD",
  "imageUri": "image-url",
  "recognizedText": "OCR extracted text",
  "language": {
    "code": "en",
    "name": "English"
  },
  "ocrConfidence": 0.95
}
```

---

## Academic Notes

- This project is fully functional and finalized
- Authentication was not required as part of the academic scope
- The project focuses on Android development and REST API consumption

---

## Author

Developed by **Kevin Núñez** as a university academic project using Android Studio.

---

## License

MIT License

