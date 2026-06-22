# AGENTS.md

## Project Overview
- 사용자가 꿈 내용을 입력하면 AI가 해몽을 제공하는 앱
- Kotlin Multiplatform 기반 Android / iOS 앱
- composeApp module에서 핵심 비즈니스 로직 관리 및 UI 작성

---

## Tech Stack
- Kotlin Multiplatform (KMP)
- Compose Multiplatform
- Architecture: MVVM + Repository (Google 권장 구조 기반)

### Data & Backend
- Local DB: Room (Android)
- Remote DB: Firestore
- Auth: FirebaseAuth
- Network: Ktor


---

## Architecture


### Rules
- 모든 비즈니스 로직은 composeApp module에 작성한다
- Repository 패턴을 반드시 사용한다

---

## Code Style
- Kotlin 공식 스타일 가이드 준수
- 단방향 데이터 흐름 (Unidirectional Data Flow) 유지
- Compose UI는 Stateless 우선으로 작성
- 상태는 ViewModel에서만 관리한다
- Flow를 사용하고 LiveData는 사용하지 않는다
- 불필요한 recomposition을 방지한다

---

## KMP Rules
- composeApp module에서 플랫폼 API 직접 사용 금지
- 플랫폼 기능은 expect/actual로 분리한다
- Android / iOS 의존성은 composeApp 모듈에 추가하지 않는다

---

## Data Rules
- 데이터는 Offline-first로 Room에서 불러오고 먼저 저장한다
- Firestore는 백업 데이터 소스로 사용한다

---

## Do
- 기존 구조와 패턴을 반드시 유지한다
- 새로운 화면에는 ViewModel을 추가한다

---

## Don't
- 새로운 라이브러리를 임의로 추가하지 않는다
- 기존 아키텍처를 변경하지 않는다
- 비즈니스 로직을 UI 레이어에 작성하지 않는다
- composeApp module에 플랫폼 종속 코드를 추가하지 않는다

---

## Commit Rules
- feat: 기능 추가
- fix: 버그 수정
- refactor: 리팩토링
- chore: 설정 변경