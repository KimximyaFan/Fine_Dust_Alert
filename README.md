# 서울 미세먼지 알림 어플

곧 겨울이 다가옵니다. 그러면 미세먼지도 같이 다가옵니다.

# 앱 설명

외출시 창문을 열어놓고 나갈지, 마스크를 쓰고 나갈지 
미세먼지 수치를 보고 판단해야 합니다.

그런데 네이버에 검색해서 오늘의 미세먼지 수치를 확인하는 건
여간 귀찮은 일이 아닙니다.

아침에 일어나서 하루를 시작할 때, 
그래도 휴대폰은 쉽게 켜서 봅니다.

따라서 이 앱을 이용하면 그 귀찮음을 해소 할 수 있습니다.

어플을 한번 실행만 시켜두면 어플을 종료해도 
백그라운드에서 자동으로 미세먼지 알림을 출력해줍니다.

## 기능

![image](https://github.com/user-attachments/assets/81f1b9b9-06ef-493d-933e-81c013d27df7)

서울시에서 정보를 가져와서 표시해줍니다.

정보는 현재 서울 평균 대기 상태를 나타나냅니다.

전반적인 대기상태, 미세먼지 수치, 초미세먼지 수치를 보여줍니다.

기본적인 알림주기는 1시간, 
기본적인 임계값은 75입니다.

설정한 알림 주기마다 API를 호출해서 서울시 대기를 체크합니다.

![image](https://github.com/user-attachments/assets/cdda9565-9918-4290-b44d-08bf94ed21a1)

알림 주기와 미세먼지 임계값을

그림처럼 사용자가 직접 정할 수 있습니다

![image](https://github.com/user-attachments/assets/49eb4a46-0536-4d1b-bcc7-3b8e502b9187)

대기상태가 임계값을 넘어가면 

위 그림과 같이 알림이 뜹니다.

해당 그림은 임계값을 33으로 설정했을 때 입니다.

어플을 종료해도 백그라운드에서 워커가 돌아가면서 지속적으로 주기마다 서울시 api를 호출해서 대기 정보를 체크합니다.


## 구현

### API

![image](https://github.com/user-attachments/assets/1e008c06-feb3-4e57-9104-01f70c95dad9)

사용한 API는 서울시 공공데이터 입니다.



![image](https://github.com/user-attachments/assets/241f2462-fe18-4999-963e-cb7200f81a0a)

그중에서 서울시 대기환경 평균 현황을 이용했습니다.




![image](https://github.com/user-attachments/assets/b9d342a5-775d-450f-983e-b9ec473a0b9f)
![image](https://github.com/user-attachments/assets/bc4fc4b0-64e4-43a5-96da-4042df209844)
![image](https://github.com/user-attachments/assets/318d1d08-5d44-46ac-b202-7a91bf35acdd)

retrofit을 이용해서 api를 호출 했습니다.



![image](https://github.com/user-attachments/assets/28eff98b-5750-4e1f-976b-bea0e6efbe94)

그 외에도 
호출한 XML 데이터를 바탕으로 
AirQualityResponse, Result, Row 을 거쳐서 정보를 뽑아냅니다.



### 백그라운드 알림

![image](https://github.com/user-attachments/assets/1946d721-b294-411c-8d8b-f2ed81fd3fb7)
![image](https://github.com/user-attachments/assets/62468dfc-4d21-4d70-9aad-b899b5cee111)

Worker 를 이용해서 백그라운드 알림을 구현했습니다.




