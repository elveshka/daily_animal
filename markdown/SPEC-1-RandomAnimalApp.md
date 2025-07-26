# SPEC-1-RandomAnimalApp

## Background

В рамках MVP создано простое Android-приложение на Kotlin, предназначенное для показа случайного изображения животного (на текущий момент — кошка или собака) с подписью-фразой. Это приложение может быть использовано как развлекательное или расслабляющее средство, потенциально масштабируемое с добавлением новых типов контента (другие животные, видео, цитаты, озвучка и т.д.).

Основная цель архитектурного дизайна — подготовить масштабируемую, тестируемую и поддерживаемую основу, позволяющую легко добавлять источники данных (например, удалённые API), кэширование, расширение списка животных и локализацию.

---

## Requirements

**Must Have:**
- Показ случайного животного (кошка или собака)
- Показ случайной фразы с автором под изображением
- Обновление содержимого по нажатию

**Should Have:**
- Возможность расширения списка животных
- Возможность добавления/изменения фраз
- Локализация UI
- Устойчивость к повороту экрана

**Could Have:**
- Работа с удалённым API (TheCatAPI, Dog API)
- Fallback на избранное при отсутствии интернета
- Кеширование избранных карточек

**Won’t Have:**
- Регистрация пользователя
- Пуш-уведомления
- Админ-панель или backend

---



## Method

Архитектура: MVVM + Clean Architecture с Remote-First логикой и fallback на Room.

### Компоненты:

```plantuml
@startuml
actor User
rectangle UI {
  "DailyContentScreen"
}
rectangle ViewModel {
  "DailyContentViewModel"
}
rectangle Domain {
  "GetRandomDailyContent"
  "DailyContentRepository"
}
rectangle Data {
  interface "DataSource"
  "RemoteDataSource"
  "LocalDataSource"
  "FavoriteContentStore"
  "NetworkChecker"
}

User --> "DailyContentScreen" : Interacts
"DailyContentScreen" --> "DailyContentViewModel" : Observe state
"DailyContentViewModel" --> "GetRandomDailyContent" : Call use-case
"GetRandomDailyContent" --> "DailyContentRepository" : getRandomContent()

"DailyContentRepository" --> "NetworkChecker" : isOnline()
"DailyContentRepository" --> "RemoteDataSource" : fetch() if online
"DailyContentRepository" --> "FavoriteContentStore" : getRandomFavorite() if offline
@enduml