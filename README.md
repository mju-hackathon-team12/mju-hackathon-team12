# Dear, Seoul — 장소 우편함

명지대학교 해커톤 Team 12 프로젝트. 작품 취향을 바탕으로 서울의 익명 레터를 추천하고, 새 레터를 남깁니다.

Java 17 · Spring Boot · Spring AI · Spring Data JPA · H2

## 실행

```powershell
$env:GEMINI_API_KEY = '발급받은 키'
.\gradlew.bat bootRun
```

접속: `http://localhost:8080`. AI 추천·검토에는 API 키가 필요하며, `.env`는 자동으로 읽지 않습니다.

개발 프로필의 H2 콘솔: `http://localhost:8080/h2-console/`.
JDBC URL은 `jdbc:h2:file:./data/dearseoul`, 사용자명은 `sa`, 비밀번호는 `DB_PASSWORD` 설정값입니다(기본 빈 값).

## 디렉터리 구조

```text
src/
├── main/
│   ├── java/com/mju/dearseoul/
│   │   ├── common/
│   │   │   ├── controller/
│   │   │   ├── exception/       # 공통 예외 처리
│   │   │   ├── response/        # 공통 응답 DTO
│   │   │   ├── security/
│   │   │   └── validation/
│   │   └── domain/
│   │       ├── content/
│   │       ├── letter/
│   │       ├── place/
│   │       ├── preference/
│   │       └── recommendation/
│   └── resources/
│       ├── static/              # 화면·스타일·JS
│       ├── db/seed/             # 콘텐츠·장소·레터 초기 SQL
│       └── application*.yml
└── test/
    └── java/                   # 서버 테스트
```

각 도메인은 필요한 `controller`, `service`, `repository`, `entity`, `dto`로 구성합니다. Controller는 HTTP 입출력, Service는 비즈니스 로직·트랜잭션, Repository는 DB 접근을 담당하며, 응답에는 엔티티 대신 DTO를 사용합니다.

프런트는 `app.js`(상태·화면·이벤트), `api.js`(서버 통신), `map-data.js`(지도 좌표)로 구성합니다.

## 공통 예외 응답

`ErrorCode`에서 HTTP 상태·오류 코드·기본 메시지를 정의하고, `GlobalExceptionHandler`가 아래 형식으로 응답합니다.

```json
{
  "code": "NOT_FOUND",
  "message": "요청한 대상을 찾을 수 없습니다."
}
```

```java
throw new BusinessException(ErrorCode.NOT_FOUND);
throw new BusinessException(ErrorCode.NOT_FOUND, "장소를 찾을 수 없습니다.");
```

입력 오류와 중복 요청도 같은 형식을 사용합니다. 처리하지 못한 예외는 `500 INTERNAL_SERVER_ERROR`로 응답하며 내부 오류 메시지는 노출하지 않습니다.

## 테스트

```powershell
.\gradlew.bat test
```
