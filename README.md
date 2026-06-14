# 📖 Android EPUB Reader

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org) 
[![Jetpack Compose](https://img.shields.io/badge/Compose-UI-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg)]()

A self-taught, simple Android EPUB reader designed with modern Android development practices using MVVM pattern. It features local library management, local caching for books's covers and images, an UI built entirely with Jetpack Compose and custom EPUB parsing logic (main branch) or integrated EPUB parsing/rendering third-party engine (readium branch).
This is the first ever App that I wrote with only the help of stackoverflow, google, documents. There're many bugs that I yet to understand and resolve.

## ✨ Key Features

* **EPUB Support:** Integrated with the **Readium** library alongside custom parsing support for parsing and rendering Epub content to users.
* **Local Library:** Uses **Room Database** to securely store and index book metadata, genres, and track user reading progress.
* **Responsive UI:** Heavy file I/O operations and metadata extraction are shifted off the main thread to ensure the app never freezes.
* **Optimized Image Loading:** Asynchronous cover image processing drastically reduces repeated processing and improves the overall user experience.

## 🛠 Tech Stack & Architecture

This project is built with basic understanding of scalability and testability in mind, adhering to the **MVVM** design pattern.

* **Languages:** [Kotlin](https://kotlinlang.org/), Java
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative, modern UI.
* **Architecture:** MVVM (Model-View-ViewModel)
* **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/) for decoupled and testable components.
* **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for background threading and I/O management.
* **Local Storage:** [Room Database](https://developer.android.com/training/data-storage/room)
* **Core Reading Engine:** Custom parsing logic written by me, or Readium.

## 🏗 Project Structure

The codebase is strictly modularized by layers to enforce MVVM and separation of concerns:

```text
app/
├─ data/                  # Data layer implementation
│  ├─ dao/                # Room Data Access Objects
│  ├─ data_source/        # Local/Remote data sources
│  ├─ entity/             # Database entities
│  ├─ repository/         # Repository implementations
│  └─ util/               # Data layer utilities
├─ domain/                # Core business logic and use cases
│  ├─ epub_parser/        # Custom EPUB parsing logic
│  │  ├─ epub_model/      # Models specific to EPUB structure
│  │  └─ util/            # Parsing utilities
│  └─ model/              # Domain business models
└─ ui/                    # Presentation layer (Jetpack Compose)
   ├─ component/          # Reusable Compose UI components
   ├─ screen/             # App screens and ViewModels
   │  ├─ library/         # Library management screen
   │  └─ reader/          # EPUB reading interface
   └─ theme/              # Material Design theme, typography, and colors


