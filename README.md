# 대학생 멘토링 매칭 서비스, Menteetor

```
멘토링이 필요하신 대학생인가요?
Menteetor에서 원하는 분야에 대한 멘토링을 찾아보세요.
멘토링이 성사되면 앱 내부에서 채팅을 할 수 있어요.
간단한 내용은 질문 게시판을 이용해보세요. 
```

## ⭐️ Key Function

- JWT Refresh / Access Token 이용하여 로그인 구현
- 멘토링 대상 단과 / 검색어 필터링 (Flow combine으로 즉각적인 필터링 수집)
- 멘토링 매칭 기능
    - 멘토링 신청 시 멘토에게 푸시 알림 전송 및 수락 / 거절 여부 멘티에게 푸시 알림 전송
    - 멘토링 수락 시 채팅방 생성
- 채팅 기능
    - 실시간 채팅 구현 (Stomp 프로토콜 이용)
       - 텍스트, 이미지, 파일 전송 가능
    - 채팅 수신시 앱 상태가 백그라운드 / 포그라운드 여부 상관 없이 푸시 알림 전송
- 알림 기능
    - 앱 내부에서 기능 별로 (채팅 / 멘토링) 알림 제어 가능

## Development

- Koltin 1.8.0
- Java 17
  
### Libraries
- DataBinding
- Coroutine
- FCM
- Retrofit, Okhttp
- Hilt
- Stomp
