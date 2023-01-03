
## 깃헙 organization 출석부 만들기

- [x] 토큰 받아 연결
- [x] 깃헙 issue받아 아이디만 분리 -> getLogin으로
- [ ] 마크다운 형식으로 README 파일 만들기 (PrintWriter 설정)

1. open된 issue에 출석했다는 comment를 단 사람들의 login id를 긁어와서 출석체크 현황을 readme.md에 만든다.
 - [ ] opened되어 있는 issue가 하나 밖에 없는지 예외체크
 - [ ] comment 중복으로 달았을 경우 한번만 체크
 - [ ] comment가 있으면 
 - [ ] 주별 출석률 / 월별 출석률 확인

2. 00시가 되면 전날 issue는 closed 된다.

3. 00시가 되면 날짜에 맞는 issue가 생성된다.
- [ ] LocalDate.now() 통해서 issue title 설정
- [ ] 하나의 issue만 opened 되어 있어야 한다.

4. readme.md에는 자동으로 날짜가 추가된다.


* 추가하고 싶은 기능 : 요일 옆에 날짜 표시기능 예) 월요일(1/2)

정해야할 것
> 1. 언제 issue를 closed 되야 하는지
> 2. issue에 있는 comment를 한번에 긁어 와야함,
> 3. 월 ~ 일에 있는 comment를 한번에 긁어와 list에 boolean으로 넣어 for문 돌면서 markdown 표 형식 만들기. 
> 4. 다음 월요일이 되면 reset되어 다시 월요일 부터 시작해야함... shit
> 

지금 할 수 있는것
> - issue를 현재 날짜에 맞게끔 title과 body 내용을 설정해 자동 생성기능
> - comment를 issue마다 가지고 올 수 있는데, issue를 가지고 오는 기준이 closed, opened, all 기준으로 가지고 올 수 있음.
> - I/O를 통해서 PrintWriter 통해서 markdown 생성해야하는데, 그 시점이 매일 스터디를 끝나는 시점을 기준으로 진행해야 한다.
> - issue를 일별로 만들어야 하는것은 확실하다. 
> - 일요일 되면 모든 issue를 closed 시키자. 그리고 opened된 모든 issue를 가져와서 요일 별로 만들자.
> - 요일별 issue를 각 id마다의 list에 boolean값으로 참석 여부를 추가하자.
> - 참석한 사람은 true로 하고, 각 날이 지날때 마다 comment가 없으면 default 값은 false로 한다.


closed 된 것을 가지고 올때
 - 다른 사람이 issue를 달 수도 있으니, 내가 닫은 것만 가지고 오기. -> 내가 닫은 기능이 정상적으로 작동 안됨.

Issue 최근 7개 돌면서 comment 마다 사람들 id를 키로, List<boolean> 참석 여부를 value로 가지는 map에 저장하기.
 - organization에서 member를 가지고 오는 것이 아니라, comment를 기준으로 가지고 오기.
 > organization을 기준으로 가지고 온다면, organization 등록되어 있지만 실제 참여 안하는 인원이 생각보다 많음.
 - comment 돌면서 map에 값이 없는 사람이 있으면 put해주는데, value인 list에 다른 사람과 동일하게 크기를 맞춰줘야함
 - 발견된 이전 까지는 list에 false값으로 세팅하기.

comment를 돌아 만든 map을 바탕으로 markdown 생성하기
 - 그냥 하면 됨.
 - 신경써야할것 : issue 생성시 body로 comment가 내 계정으로 자동 생성되는데, 나는 그럼 출석률 계산을 못하는 것인가?

string 형으로 생성한 markdown을 PrintWriter를 통해서 markdown 파일 생성하기.

### github 내부 설정
 - issue 작성 권한 나로 설정. issue closed 방지를 위한 권한 나로 설정.

### Trouble shooting 
![getClosedByMethod](img/getClosedByMethod.png)
 - `Note that GitHub doesn't always seem to report this information even for an issue that's already closed`
 - 위에서 볼 수 있듯 내가 가진 issue를 분리 시키고 싶었는데, `getClosedBy`메서드를 통해서 `GHUser`를 정상적으로 불러오지 못했다.




