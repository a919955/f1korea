# Backend (F1 Spring Boot)

이 디렉터리에는 기존 백엔드 저장소(`kimm9487/f1`)의 스냅샷(zip)이 포함되어 있다.

## 구조
- `f1-backend.zip`: Spring Boot 3 + Gradle 프로젝트 전체

## 다음 단계(권장)
1. 로컬에서 압축 해제 후 실제 소스 파일로 커밋하려면:
   ```bash
   git checkout -b merge/f1-backend
   unzip backend/f1-backend.zip -d backend/
   # zip 내부 최상위 폴더명을 'backend/'로 맞추고, 불필요한 산출물 제거
   rm -rf backend/**/build backend/**/.gradle
   git add backend
   git commit -m "Expand backend zip into tracked source files"
   git push -u origin merge/f1-backend
   ```
2. 원본 저장소 히스토리를 보존하고 병합하려면(원격 접근 가능 시):
   ```bash
   git remote add backend https://github.com/kimm9487/f1.git
   git fetch backend
   git subtree add --prefix=backend backend main --squash
   git push -u origin HEAD
   ```

> 임시 단계로 zip을 우선 저장소에 포함시켰다. 승인 후 원하면 전체 파일 전개 커밋(PR)도 대신 준비 가능.
