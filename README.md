# Dear, Seoul — 장소 우편함

명지대학교 해커톤 Team 12 프로젝트입니다.

Java 17 / Spring Boot / Spring AI 2.0.2 / Spring Data JPA / H2 파일 DB. 정적 화면과 API를 한 서버에서 제공합니다.

## 실행

현재 `static/`은 전달받은 Dear, Seoul 화면이며, 작품 3개 이상 평가 → DB 레터 3통 추천 → 지도·읽기·따라 하기 → 검토·저장 흐름으로 연결됩니다. 작품 목록은 첨부 JSON에서 가져온 음악·영화·드라마 각 10개이며, 장소와 함께 SQL로 DB에 주입합니다. 실제 AI 추천과 검토는 `GEMINI_API_KEY`가 필요합니다.

Java 17과 최초 라이브러리 다운로드를 위한 인터넷 연결이 필요합니다.

```powershell
# 저장소 루트에서 실행
$env:GEMINI_API_KEY = '발급받은 키'
.\run-local.ps1
```

브라우저에서 `http://localhost:8080`에 접속합니다. 포트 변경은 `.\run-local.ps1 -Port 8081`.
키 없이도 화면·콘텐츠·장소·공개 레터를 조회할 수 있습니다. 추천과 작성 검토는 `503 AI_NOT_CONFIGURED`를 반환합니다.
`.env`는 자동으로 읽지 않습니다. 실행할 때 `GEMINI_API_KEY`를 직접 환경변수로 주입하세요. `.env`는 Git에서 제외됩니다.

기본 모델은 `gemini-3.1-flash-lite`입니다. [Google AI Studio](https://aistudio.google.com/apikey)에서 키를 발급하고 프로젝트의 무료 티어·사용 가능 모델을 확인하세요. [공식 가격](https://ai.google.dev/gemini-api/docs/pricing), [계정별 한도](https://ai.google.dev/gemini-api/docs/rate-limits). `GEMINI_MODEL`로 모델을 변경할 수 있고 유료 모델로 자동 전환하지 않습니다. 무료 티어에는 Google의 데이터 사용 정책이 적용됩니다.

## 환경 프로필·외부 API 로그

- `application.yml`: DB·AI 등 공통 설정. 프로필 미지정 시 `dev`를 사용합니다.
- `application-dev.yml`: 기본 INFO, 애플리케이션·외부 API DEBUG. Gemini 호출 시작·응답 수신·완료·실패를 콘솔에서 확인합니다.
- `application-prod.yml`: ERROR만 출력. 시작 배너·일반 시작 로그·Tomcat 접근 로그를 끄고 SQL·바인딩 로그도 끕니다. 외부 API 실패는 오류 코드와 상태 등 요약만 남깁니다. HTTPS 쿠키가 기본입니다.
- Docker 이미지는 `SPRING_PROFILES_ACTIVE=prod`로 실행됩니다. API 키는 이전과 같이 실행 시 주입합니다. `.env`는 자동으로 읽지 않습니다.

IntelliJ에서는 실행 구성의 Active profiles에 `dev` 또는 `prod`를 지정하거나 환경변수 `SPRING_PROFILES_ACTIVE`를 설정하세요.

```powershell
# 개발
$env:SPRING_PROFILES_ACTIVE = 'dev'
.\gradlew.bat bootRun

# 운영 JAR 실행
$env:SPRING_PROFILES_ACTIVE = 'prod'
java -jar build/libs/dearseoul.jar
```

개발 로그 예시:

```text
external_api.start requestId=... provider=gemini operation=letter-recommendation
external_api.success requestId=... provider=gemini operation=letter-recommendation durationMs=842
external_api.failure requestId=... provider=gemini operation=letter-recommendation durationMs=115 code=AI_QUOTA_EXCEEDED status=429 upstreamStatus=429 causeType=ClientException
```

`status`는 서비스가 반환하는 HTTP 상태, `upstreamStatus`는 확인 가능한 외부 HTTP 상태입니다(없으면 0). 키·인증 헤더·요청 URL·프롬프트·사용자 입력·응답 본문·SDK 예외 메시지와 스택은 기록하지 않습니다. SDK 전체 DEBUG 대신 전용 로거 `com.mju.dearseoul.external.api`로 필요한 메타데이터만 기록합니다. WARN인 호출 제한·미설정 안내는 운영에서 출력되지 않습니다.

## 구조 규칙

```text
com.mju.dearseoul
├── common
│   ├── controller
│   ├── exception
│   ├── security
│   └── validation
└── domain
    ├── place          # service / repository / entity / dto
    ├── letter         # controller / service / repository / entity / dto
    ├── preference     # controller / service / repository / entity / dto
    ├── content        # controller / service / repository / entity / dto
    └── recommendation # controller / service / repository / entity / config / infrastructure
```

Controller는 HTTP 입출력, Service는 비즈니스 로직·트랜잭션, Repository는 DB 접근을 맡습니다. 엔티티를 API 응답으로 노출하지 않습니다. 클래스·메서드·필드 어노테이션은 선언 위의 별도 줄에 작성합니다. 장소와 콘텐츠는 각각 catalog_place·catalog_content 테이블에서 조회합니다.

AI 의존성은 Spring AI BOM 2.0.2로 관리하며 `spring-ai-google-genai`와 `spring-ai-client-chat`를 사용합니다. `GeminiChatConfiguration`에서 Google GenAI 모델과 `ChatClient`를 구성하고, `GeminiGateway`는 `AiJsonClient`만 구현하며 `ChatClient` 호출·JSON 파싱·호출 제한·예외 변환을 담당합니다. 기존 `GEMINI_API_KEY`, `GEMINI_MODEL`, `GEMINI_BASE_URL`, `AI_TIMEOUT_SECONDS` 환경변수는 그대로 사용합니다.
키가 비어 있으면 모델 Bean을 생성하지 않아 일반 기능은 계속 실행됩니다. 무료 한도 초과·서버 오류·시간 초과 시 자동 재호출하지 않도록 SDK와 Spring AI 양쪽의 재시도를 비활성화했습니다.

## 공통 예외 응답

`common.exception.ErrorCode`가 오류 코드·HTTP 상태·기본 메시지를 관리합니다. 비즈니스 오류는 `BusinessException`으로 발생시키고 `GlobalExceptionHandler`가 `common.response.ErrorResponse`로 반환합니다. 기존 프론트엔드의 `{code, message}` 형식을 유지합니다.

```java
throw new BusinessException(ErrorCode.AI_RATE_LIMIT);
throw new BusinessException(ErrorCode.NOT_FOUND, "장소를 찾을 수 없습니다.");
```

입력 형식 오류·중복 요청도 같은 형식으로 반환합니다. 처리하지 못한 예외는 `500 INTERNAL_SERVER_ERROR`로 변환하며 내부 예외 메시지는 응답에 포함하지 않습니다. 필터에서 거절하는 Origin 요청도 같은 `ErrorCode`와 `ErrorResponse`를 사용합니다. 커스텀 메시지는 사용자에게 보여줄 문장만 전달하며 SDK 예외 메시지를 그대로 넣지 않습니다.

## 데이터·익명성

- 신규 공개 레터는 `experience_letter`, 취향은 `preference_session`, 작성 검토는 `letter_review`, 추천 이력은 `recommendation_history`에 저장합니다. 기존 사진 기록·팬레터 API와 엔티티는 제거했습니다. 기존 DB 파일의 테이블과 업로드 파일을 삭제하는 마이그레이션은 실행하지 않습니다.
- `src/main/resources/db/seed/music.sql`, `drama.sql`, `film.sql`: 콘텐츠 각 10개. DB 조회 구현은 `DatabaseContentCatalog`입니다.
- `db/seed/seodaemun.sql`: TourAPI 장소 161곳과 기존 레터 호환용 필름포럼 1곳. 문자열 장소 ID와 원본 source_id를 함께 보관합니다.
- `fixtures/letters.json`: 처음 실행할 때 한 번만 넣는 예시 레터 10통. 재시작 시 중복 삽입하지 않으며 화면에서 예시임을 표시합니다.
- 새 레터 저장은 검토 결과에 묶인 원문·장소만 사용하고 중복 저장을 방지합니다. 저장 후 다른 사용자의 실제 추천 후보가 됩니다. AI 대기 중에는 DB 트랜잭션을 유지하지 않습니다.

- 기본 저장: `data/dearseoul.mv.db`. 같은 `APP_DATA_DIR`이면 재시작 후 유지됩니다.
- `mailbox_session`: 256비트 무작위 HttpOnly / SameSite=Strict 쿠키, 30일 유지. 서버에는 해시를 저장합니다. 재시작으로 쿠키가 바뀌지 않습니다.
- 쿠키를 지우면 이전 취향·검토 결과와 내 레터 작성자 권한을 복구할 수 없습니다. 공개 응답에 작성자·발신자·수신자 식별자를 포함하지 않습니다.
- 레터에는 연락처 패턴·일부 금칙 표현 검사를 적용합니다. 종합적인 유해성 심사는 아닙니다.
- AI에는 취향·후보 공개 레터·장소 또는 검토할 레터 본문·콘텐츠를 전송합니다. 작성자·세션 식별자는 보내지 않습니다.
- AI 호출은 서버 전체 동시 2건·분당 10건으로 제한합니다. 제공자 한도는 더 낮을 수 있습니다.

## 검증·화면 인수

```powershell
.\gradlew.bat test bootJar
java -jar build/libs/dearseoul.jar
```

테스트는 별도 메모리 DB와 `build/test-data`를 사용합니다. Gemini 테스트는 로컬 모의 서버이며 실제 API 호출은 키 설정 후 별도로 확인해야 합니다.
화면은 `src/main/resources/static/`에 있습니다. `api.js`는 서버 호출, `app.js`는 화면 흐름, `core.js`는 서버가 채울 콘텐츠 구조, `data.js`는 지도 경계·시각 설정입니다. IDE에서 `DearseoulApplication` 실행 또는 `./gradlew.bat bootRun` 후 `/`에서 확인합니다. 브라우저 내부 Claude·규칙 기반 추천은 제거했고 DB와 Spring AI를 사용합니다.
API 계약: `docs/API.md`, 시연 절차: `docs/DEMO.md`.
SQL 파일·원본 필드·ID 규칙·재생성 방법: [초기 데이터 안내](docs/CATALOG_SEEDS.md).

## 배포

배포 계정은 아직 지정되지 않았습니다. 준비된 단일 인스턴스 환경에서 사용합니다.

```powershell
.\gradlew.bat test bootJar
docker build -t dearseoul .
# 로컬 Docker 확인용
docker run --rm -p 8080:8080 --env GEMINI_API_KEY --env COOKIE_SECURE=false -v dearseoul-data:/data dearseoul
```

공개 HTTPS에서는 `COOKIE_SECURE=true`, `GEMINI_API_KEY`, `APP_DATA_DIR=/data`를 설정하고 `/data`에 영속 볼륨을 연결합니다. H2이므로 인스턴스는 하나만 실행하고 DB를 백업하세요.
신뢰할 수 있는 프록시 뒤에서만 `FORWARD_HEADERS_STRATEGY=framework`를 사용합니다. 프록시는 외부 전달 헤더를 제거하고 올바른 호스트·프로토콜을 설정해야 합니다.
헬스체크 `/api/health`의 `aiConfigured`는 키 존재 여부이며 실제 연결 성공을 보장하지 않습니다. 영속 볼륨 없는 배포는 이번 시연의 배포 완료로 간주하지 않습니다.

## 저장소 구조

Spring Boot 프로젝트를 저장소 루트에 배치합니다. 별도 frontend·backend 모듈은 없습니다.

```text
.
├── src/main/java/           # 도메인별 서버 코드
├── src/main/resources/
│   ├── static/              # 프론트엔드 정적 파일
│   ├── db/seed/             # 기동 시 주입할 SQL
│   └── fixtures/            # 예시 레터
├── src/test/
├── gradle/
├── docs/
├── scripts/
├── datasets/
├── build.gradle
├── settings.gradle
└── Dockerfile
```

IDE에서는 루트의 build.gradle을 Gradle 프로젝트로 열고 작업 디렉터리를 저장소 루트로 설정하세요. .env와 data/는 로컬 전용이며 Git에서 제외합니다.
