# Docker로 실행하기

## 🚀 빠른 시작

### 1. Docker Compose로 전체 실행
```bash
# 백그라운드에서 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 실행 중인 컨테이너 확인
docker-compose ps
```

### 2. 개별 실행

#### PostgreSQL만 실행
```bash
docker-compose up -d postgres
```

#### 애플리케이션만 빌드
```bash
docker build -t todo-app .
```

## 🛠️ 설정

### 환경변수
- `JWT_SECRET`: JWT 토큰 서명용 비밀키 (최소 256비트)
- `SPRING_PROFILES_ACTIVE`: Spring 프로필 (기본값: docker)

### 포트
- **애플리케이션**: `8080`
- **PostgreSQL**: `5432`

## 📋 주요 명령어

```bash
# 전체 서비스 시작
docker-compose up -d

# 서비스 중지
docker-compose down

# 볼륨까지 삭제 (데이터베이스 초기화)
docker-compose down -v

# 로그 실시간 확인
docker-compose logs -f

# 컨테이너 재시작
docker-compose restart app

# 이미지 다시 빌드
docker-compose up --build
```

## 🏥 헬스체크

### 애플리케이션 상태 확인
```bash
curl http://localhost:8080/api/users/health
```

### 데이터베이스 상태 확인
```bash
docker-compose exec postgres pg_isready -U postgres
```

## 🐛 문제 해결

### 포트 충돌
이미 8080 또는 5432 포트를 사용 중이라면:
```yaml
# docker-compose.yml에서 포트 변경
ports:
  - "8081:8080"  # 애플리케이션
  - "5433:5432"  # PostgreSQL
```

### 데이터 초기화
```bash
# 모든 데이터 삭제 후 재시작
docker-compose down -v
docker-compose up -d
```

### 로그 레벨 조정
application-docker.yml에서 로깅 레벨 변경:
```yaml
logging:
  level:
    com.librarian.todo_list: DEBUG  # INFO -> DEBUG
```