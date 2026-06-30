# MyDream

MyDream은 사용자가 기록한 꿈 내용을 AI로 해몽하고, 해몽 결과를 날짜별로 저장해 다시 확인할 수 있는 Kotlin Multiplatform 앱입니다. Android와 iOS에서 공통 비즈니스 로직과 Compose UI를 공유하며, Room을 우선 저장소로 사용하고 Firestore를 백업 저장소로 함께 사용합니다.

## 주요 기능

- AI 꿈 해몽
  - 꿈 내용을 입력하면 Cloud Run API를 통해 해몽 결과를 생성합니다.
  - 해몽 결과에는 제목, 운세 점수, 분석 내용, 긍정 에너지, 기대 포인트, 주의 포인트, 행운 아이템, 행운 색상이 포함됩니다.
  - 앱 언어 설정에 따라 API 요청 언어를 함께 전달합니다.

- 꿈 기록 저장 및 달력 조회
  - 해몽 결과를 Room DB에 먼저 저장하고 Firestore에 백업합니다.
  - 꿈 달력 화면에서 날짜별 꿈 기록을 확인할 수 있습니다.
  - 저장된 꿈 기록을 삭제하면 로컬 DB와 Firestore 백업 데이터가 함께 삭제됩니다.

- 일일 해몽 이용 횟수 관리
  - 하루 무료 해몽 횟수를 관리합니다.
  - 무료 횟수를 모두 사용한 뒤 보상형 광고 시청으로 추가 해몽 기회를 받을 수 있습니다.
  - 사용량 정보는 로컬 DB에 저장하고 Firestore에 동기화합니다.

- 오늘의 운세
  - 날짜 기준으로 오늘의 운세 API를 호출합니다.
  - 행운의 숫자, 행운의 물건, 행운의 색상을 제공합니다.
  - 같은 날짜의 운세는 Room에 캐싱해 재호출 없이 표시합니다.

- 로그인 및 계정 관리
  - FirebaseAuth 기반 익명 로그인과 Google 로그인을 지원합니다.
  - iOS 구현에는 Apple 로그인 흐름이 포함되어 있습니다.
  - 계정 정보 확인, 로그아웃, 회원 탈퇴 기능을 제공합니다.
  - 소셜 로그인 후 Firestore의 기존 꿈 데이터를 로컬 DB로 동기화합니다.

- 앱 설정
  - 한국어/영어 언어 전환을 지원합니다.
  - 라이트/다크 화면 모드를 지원합니다.
  - 피드백 폼, 이용약관, 개인정보처리방침 링크를 제공합니다.

- 광고
  - Google Mobile Ads 기반 광고를 연동합니다.
  - 설정 화면 배너 광고와 꿈 해몽 추가 이용을 위한 보상형 광고 흐름이 포함되어 있습니다.

## 기술 스택

### 공통

- Kotlin Multiplatform
- Compose Multiplatform
- Compose Material3
- MVVM + Repository Pattern
- Kotlin Coroutines / Flow
- Koin
- Kotlinx Serialization
- Kotlinx Datetime
- Ktor Client

### 데이터

- Room
- SQLite Bundled Driver
- Firestore
- FirebaseAuth

### Android

- Android Gradle Plugin
- Firebase Android SDK
- Google Sign-In
- Google Mobile Ads
- User Messaging Platform

### iOS

- Kotlin/Native
- CocoaPods
- FirebaseAuth
- FirebaseFirestore
- GoogleSignIn
- Google Mobile Ads SDK
- SwiftUI 진입점 + ComposeApp framework

## 프로젝트 구조

```text
.
├── composeApp
│   ├── src/commonMain
│   │   ├── kotlin/com/garam/mydream
│   │   │   ├── app                 # 앱 진입, Navigation, 공통 테마
│   │   │   ├── core
│   │   │   │   ├── auth            # 인증 Repository 인터페이스
│   │   │   │   ├── data            # Firebase DataSource, MainRepository
│   │   │   │   ├── database        # Room Entity, DAO, Database
│   │   │   │   ├── designsystem    # 색상, 폰트
│   │   │   │   ├── di              # Koin 모듈
│   │   │   │   ├── localization    # 앱 언어 처리
│   │   │   │   ├── network         # Ktor API 서비스
│   │   │   │   └── settings        # 설정 저장소 인터페이스
│   │   │   └── feature             # 화면별 Compose UI와 ViewModel
│   │   ├── composeResources        # 문자열, 이미지, 폰트 리소스
│   │   ├── androidMain             # Android actual 구현
│   │   └── iosMain                 # iOS actual 구현
│   └── build.gradle.kts
├── iosApp                          # iOS 앱 진입점과 Xcode 프로젝트
├── gradle/libs.versions.toml       # 의존성 버전 카탈로그
└── settings.gradle.kts
```

## 아키텍처

이 프로젝트는 `composeApp` 모듈을 중심으로 공통 비즈니스 로직과 UI를 구성합니다.

```text
Compose Screen
    ↓
ViewModel
    ↓
Repository
    ↓
Room DAO / Ktor API / Firebase DataSource
```

- UI는 Compose 기반으로 작성하고 상태는 ViewModel에서 관리합니다.
- ViewModel은 Repository를 통해 데이터 소스에 접근합니다.
- 꿈 해몽 기록은 Room에 먼저 저장하고 Firestore에 백업합니다.
- Android/iOS 플랫폼 기능은 `expect/actual` 또는 플랫폼별 source set으로 분리합니다.

## 플레이스토어

## 앱스토어

https://apps.apple.com/kr/app/mydream-ai-%EA%BF%88%ED%95%B4%EB%AA%BD/id6762837084
