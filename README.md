# 🎧 Thamanya

Modern Android content aggregation app for podcasts, episodes, audiobooks, and audio articles.

## 📱 Features

- **Dynamic Home Screen** with multiple content sections
- **Smart Search** with 200ms debouncing
- **Multiple Layout Types** (Grid, Horizontal Lists, Cards)
- **Content Types**: Podcasts, Episodes, Audiobooks, Audio Articles
- **Arabic & English** language support
- **Pull-to-refresh** functionality

## 🏗️ Architecture

**Clean Architecture + MVVM** with strict separation of concerns:

```
├── data/          # API services, DTOs, Repository implementations
├── domain/        # Domain models, Repository interfaces, Use cases  
├── presentation/  # ViewModels, UI screens, Components
└── di/           # Dependency Injection (Hilt)
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVVM + MVI
- **DI**: Hilt
- **Network**: Retrofit + OkHttp + Kotlinx Serialization
- **Async**: Coroutines + Flow
- **Images**: Coil
- **Testing**: JUnit 4 + MockK + Turbine

## 🔗 API Endpoints

- **Home**: `https://api-v2-b2sit6oh3a-uc.a.run.app/home_sections`
- **Search**: `https://mock.apidog.com/m1/735111-711675-default/search`

## 📋 Project Details

- **Package**: `com.thmanyah.task`
- **Min SDK**: 24
- **Target SDK**: 36
- **Java Version**: 11

## 🧪 Testing

Comprehensive test suite covering:
- Unit tests for Use Cases, Repositories, ViewModels
- Integration tests for data flow
- UI tests for critical user interactions

---