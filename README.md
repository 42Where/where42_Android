# Where42 Android

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="120" />
</p>

<p align="center">
  42서울 카뎃들의 클러스터 실시간 위치를 확인하는 Android 앱
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/MVVM-Architecture-blue"/>
</p>

---

## 소개

Where42는 42서울 클러스터 내 카뎃들의 실시간 위치를 확인하고 친구 관리를 할 수 있는 Android 앱입니다.  
클러스터 좌석 맵을 통해 친구가 어느 자리에 있는지 한눈에 확인할 수 있습니다.

- **개발 기간**: 2024.02 ~ 2025.03
- **개발 인원**: Android 1인 개발
- **스토어**: [Google Play Store 출시](https://play.google.com/store/apps/details?id=com.seoul.where42android)
- **실사용자**: 최대 18명 설치 및 운영

---

## 주요 기능

### 로그인
42서울 OAuth 인증을 통해 로그인합니다.

| 시작 화면 | OAuth 로그인 |
|:---------:|:-----------:|
| <img src="docs/screenshots/app_startpage.png" width="220"/> | <img src="docs/screenshots/oauth_page.png" width="220"/> |

---

### 메인 페이지
내 프로필(사진, intra 이름, 코멘트, 현재 위치)과 그룹별 친구들의 클러스터 입실 / 퇴근 상태를 한눈에 확인할 수 있습니다.

<p align="center">
  <img src="docs/screenshots/main_page.png" width="220"/>
</p>

---

### 그룹 관리
새 그룹을 생성하고 친구를 그룹별로 관리할 수 있습니다. 그룹 이름 중복 체크, 20자 제한, 그룹 편집 및 삭제를 지원합니다.

| 그룹 목록 | 그룹 생성 | 멤버 추가 |
|:---------:|:---------:|:---------:|
| <img src="docs/screenshots/group1.png" width="220"/> | <img src="docs/screenshots/group2.png" width="220"/> | <img src="docs/screenshots/group_edit_menu_friend_menu_add1.png" width="220"/> |

| 멤버 삭제 | 그룹 이름 변경 |
|:---------:|:--------------:|
| <img src="docs/screenshots/group_edit_menu_friend_menu_delete1.png" width="220"/> | <img src="docs/screenshots/group_edit_menu_namechange.png" width="220"/> |

---

### 친구 검색 & 추가
intra 이름으로 검색 후 체크박스로 복수 선택하여 일괄 친구 추가가 가능합니다.  
(2글자 이상, 영문/숫자/`_`/`-` 허용)

| 검색 화면 | 검색 결과 | 친구 추가 |
|:---------:|:---------:|:---------:|
| <img src="docs/screenshots/search1.png" width="220"/> | <img src="docs/screenshots/search2.png" width="220"/> | <img src="docs/screenshots/search3.png" width="220"/> |

---

### 나침반 (클러스터 맵)
> 💬 실사용자 피드백을 반영하여 개발된 기능

C1, C2, C5, C6, CX1, CX2 클러스터의 좌석 맵을 실시간으로 렌더링합니다.  
친구는 빨간 테두리로 강조되며, 좌석 클릭 시 프로필 확인 및 친구 추가가 가능합니다.

| C1 | C2 | C5 |
|:--:|:--:|:--:|
| <img src="docs/screenshots/compass_C1.png" width="220"/> | <img src="docs/screenshots/compass_C2.png" width="220"/> | <img src="docs/screenshots/compass_C5.png" width="220"/> |

| C6 | CX1 | CX2 |
|:--:|:---:|:---:|
| <img src="docs/screenshots/compass_C6.png" width="220"/> | <img src="docs/screenshots/compass_CX1.png" width="220"/> | <img src="docs/screenshots/compass_CX2.png" width="220"/> |

| 친구 상세 | 일반 멤버 상세 |
|:---------:|:--------------:|
| <img src="docs/screenshots/compass_myfriend_page.png" width="220"/> | <img src="docs/screenshots/compass_nofriend_page.png" width="220"/> |

---

### 공지사항
42서울 공식 공지를 목록으로 조회하고, 바텀시트로 상세 내용을 확인할 수 있습니다.

| 공지 목록 | 공지 상세 |
|:---------:|:---------:|
| <img src="docs/screenshots/announcement1.png" width="220"/> | <img src="docs/screenshots/announcement2.png" width="220"/> |

---

### 설정
한줄 코멘트 수정, 층별/구역별 수동 위치 설정(1~5층, 옥상, 지하), 로그아웃 기능을 제공합니다.

| 설정 메인 | 자리 설정 (입실 시) | 자리 선택 |
|:---------:|:------------------:|:---------:|
| <img src="docs/screenshots/setting_main.png" width="220"/> | <img src="docs/screenshots/setting_changeseat_true1.png" width="220"/> | <img src="docs/screenshots/setting_changeseat_true2.png" width="220"/> |

---

### 클러스터 실시간 현황
> 💬 실사용자 피드백을 반영하여 개발된 기능

클러스터별 이용률을 파이차트로 시각화하고, 42Seoul 인기 자리 순위를 확인할 수 있습니다.

| 이용률 현황 | 인기 자리 |
|:-----------:|:---------:|
| <img src="docs/screenshots/statistics1.png" width="220"/> | <img src="docs/screenshots/statistics2.png" width="220"/> |

---

## 기술 스택

| 분류 | 사용 기술 |
|------|----------|
| Language | Kotlin |
| Architecture | MVVM |
| Network | Retrofit2, OkHttp3 |
| Async | Coroutines, LiveData |
| UI | DataBinding, ViewBinding, RecyclerView, Glide |
| Storage | DataStore |
| Auth | 42서울 OAuth (WebView) |

---

## 프로젝트 구조

```
app/src/main/java/com/seoul/where42android/
├── main/               # Activity (로그인, 메인, 검색, 설정, 나침반, 공지)
├── fragment/           # Fragment (메인 목록, 검색 결과, 클러스터 맵)
├── ViewModel/          # SharedViewModel (프로필, 그룹/친구, 검색)
├── adapter/            # RecyclerView Adapter
├── dialog/             # 다이얼로그, 바텀시트
├── Base_url_api_Retrofit/  # Retrofit API, DataClass
├── utils/              # TokenManager, ApiUtils
└── WebView/            # CustomWebViewClient
```

---

## 커밋 메시지 가이드라인

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 |
| `style` | 코드 스타일/포맷 |
| `docs` | 문서 수정 |
| `build` | 빌드 관련 수정 |
| `chore` | 기타 자잘한 수정 |
| `ci` | CI 설정 수정 |
| `test` | 테스트 코드 |

```
feat: 클러스터 맵 친구 강조 표시 추가
fix: 토큰 재발급 실패 시 로그인 화면 이동
```
