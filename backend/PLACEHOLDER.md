# backend subtree 병합 준비

이 브랜치는 B안(원본 히스토리 보존) 진행을 위한 자리 표시자(placeholder) 커밋이다.

## 로컬에서 실행
```bash
# 1) 클론 및 브랜치
git clone https://github.com/a919955/f1korea.git
cd f1korea
git checkout -b merge/backend-subtree origin/merge/backend-subtree

# 2) 원본 백엔드 원격 추가
git remote add backend https://github.com/kimm9487/f1.git

# 3) fetch
git fetch backend

# 4) subtree 병합(히스토리 요약: --squash)
git subtree add --prefix=backend backend main --squash

# 5) push
git push -u origin HEAD
```

> --squash를 제거하면 더 상세한 히스토리가 보존되지만 충돌 해결이 필요할 수 있다.
