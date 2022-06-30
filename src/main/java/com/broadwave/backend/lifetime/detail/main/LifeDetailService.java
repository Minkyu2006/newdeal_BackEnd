package com.broadwave.backend.lifetime.detail.main;

import com.broadwave.backend.common.AjaxResponse;
import com.broadwave.backend.common.NormMath;
import com.broadwave.backend.lifetime.detail.cabonationThreePlate.CabonationThreePlateInfoDto;
import com.broadwave.backend.lifetime.detail.cabonationThreePlate.CabonationThreePlateRepository;
import com.broadwave.backend.lifetime.detail.carbonation.CabonationInfoDto;
import com.broadwave.backend.lifetime.detail.carbonation.CabonationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Minkyu
 * Date : 2022-05-23
 * Time :
 * Remark : NewDeal LifeDetail Service
*/
@Slf4j
@Service
public class LifeDetailService {

    @Value("${newdeal.aws.postgresql.url}")
    private String awsPostgresqlUrl;

    @Value("${newdeal.aws.postgresql.username}")
    private String awsPostgresqlUsername;

    @Value("${newdeal.aws.postgresql.password}")
    private String awsPostgresqlPassword;

    @Value("${newdeal.aws.python.api.url}")
    private String awsPythonApiUrl;

    private final LiftDetailRepository liftDetailRepository;

    private final CabonationRepository cabonationRepository;
    private final CabonationThreePlateRepository cabonationThreePlateRepository;

    @Autowired
    public LifeDetailService(LiftDetailRepository liftDetailRepository,
                             CabonationRepository cabonationRepository, CabonationThreePlateRepository cabonationThreePlateRepository) {
        this.liftDetailRepository = liftDetailRepository;
        this.cabonationRepository = cabonationRepository;
        this.cabonationThreePlateRepository = cabonationThreePlateRepository;
    }

    public ResponseEntity<Map<String, Object>> output(String autoNum, HttpServletRequest request) throws ClassNotFoundException {

        log.info("output 호출성공");

        AjaxResponse res = new AjaxResponse();
        HashMap<String, Object> data = new HashMap<>();

        String JWT_AccessToken = request.getHeader("JWT_AccessToken");
        log.info("JWT_AccessToken : "+JWT_AccessToken);

        log.info("autoNum : "+autoNum);

        Optional<LifeDetail> optionalLifeDetail = liftDetailRepository.findByLtDetailAutoNum(autoNum);
        if(optionalLifeDetail.isPresent()){
            log.info("optionalLifeDetail.get().getLtDetailAutonum() : "+optionalLifeDetail.get().getLtDetailAutoNum());

            if(optionalLifeDetail.get().getLtDetailType().equals("1")){
                log.info("센서리스트 테스트 and 파이썬테스트");
                // 반발경도
//                log.info("반발경도 아웃풋시작");

//                // Postgresql 접근 테스트
//                Class.forName("org.postgresql.Driver");
//
//                List<HashMap<String,Object>> deviceData = new ArrayList<>();
//                HashMap<String,Object> deviceDataInfo;
//
//                try (Connection connection = DriverManager.getConnection(awsPostgresqlUrl, awsPostgresqlUsername, awsPostgresqlPassword);) {
//                    Statement stmt = connection.createStatement();
//
//                    // 권태호 박사 쿼리 테스트
//                    String quary = "SELECT t, avg(\"value\") as val  ";
//                    quary += "FROM (SELECT time_bucket('1 days', time) as t, \"value\" FROM public.tb_static_data ";
//                    quary += "WHERE time >= '2021-10-01 00:00:00'   AND time <= '2022-05-01 00:00:00'  AND channel_number = '2'";
//                    quary += "AND device_id = 'Ssmartcs:2:DNAGW2111') AS ccc GROUP BY t ORDER BY t";
//                    ResultSet rs = stmt.executeQuery(quary);
//
////                    ResultSet rs = stmt.executeQuery("SELECT * FROM tb_device");
//                    while (rs.next()) {
////                        String deviceId = rs.getString("device_id");
////                        String deviceName = rs.getString("device_name");
////                        System.out.println("센서ID : "+deviceId +" 센서이름 : "+deviceName);
////
////                        deviceDataInfo = new HashMap<>();
////                        deviceDataInfo.put("deviceId", deviceId);
////                        deviceDataInfo.put("deviceName", deviceName);
////                        deviceData.add(deviceDataInfo);
//
//                        // 권태호 박사 쿼리 테스트
//                        String t = rs.getString("t");
//                        String val = rs.getString("val");
//                        System.out.println("시간 : "+t +" 평균 : "+val);
//
//                        deviceDataInfo = new HashMap<>();
//                        deviceDataInfo.put("deviceId", t);
//                        deviceDataInfo.put("deviceName", val);
//                        deviceData.add(deviceDataInfo);
//
//                    }
//                    data.put("chartName","test"); // 타입
//                    data.put("deviceData",deviceData);
//
//                    rs.close();
//                    stmt.close();
//                }
//                catch (SQLException e) {
//                    e.printStackTrace();
//                }


                // 파이썬 테스트
                log.info("AWS URL : "+awsPythonApiUrl);
                try {
//                    https://vqdeoa9z35.execute-api.ap-northeast-2.amazonaws.com/echo/1?filter=test
                    // 센서리스트 데이터 불러오기
                    URL url = new URL("http://python.bmaps.kr:5000/conn");
                    // 센서 데이터 불러오기
//                    URL url = new URL("http://python.bmaps.kr:5000/sensorData?sensor=Ssmartcs:2:DNAGW2111&time1=2021-10-01%2000:00:00&time2=2022-05-01%2000:00:00");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    log.info("responseCode : "+responseCode);

                    // 한글깨짐 이유 알수없음.
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                    StringBuilder stringBuffer = new StringBuilder();
                    String inputLine;

                    while ((inputLine = bufferedReader.readLine()) != null)  {
                        stringBuffer.append(inputLine);
                    }

                    bufferedReader.close();
                    String response = stringBuffer.toString();
                    log.info("response : "+response);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else if(optionalLifeDetail.get().getLtDetailType().equals("2")){
                // 탄산화깊이
                log.info("탄산화깊이 아웃풋시작");

                // 변수 선언자리
                List<Double> ltRecoveryList = new ArrayList<>();
                List<Double> ltCostList = new ArrayList<>();

                List<Integer> pf_0List = new ArrayList<>(); // 무조치시 PF<0 리스트
                List<Double> pf_List = new ArrayList<>(); // 무조치시 PF 리스트
                List<Double> b_List = new ArrayList<>(); // 무조치시 B 리스트

                double pf_max; // 손상확률 최대값(PF 최댓값)
                double pf_min; //손상확률 최소값
                double b_max; // 신뢰성 지수 최대값

                List<Double> b1_List = new ArrayList<>(); // 유지보수 개입 시 B1 리스트
                List<Double> b2_List = new ArrayList<>(); // 유지보수 개입 시 B2 리스트

                List<Integer> step1_list = new ArrayList<>(); // 우측의 step1_list
                List<Double> step2_list = new ArrayList<>(); // 우측의 step2_list
                int step1_value = 0; // 우측의 step1 값
                int maxYear = 0; // 유지보수 무조치 가능한 최대년수

                List<Double> referenceTable_List = new ArrayList<>(); // 우측 레퍼런스테이블

                List<Integer> adjustYear_list = new ArrayList<>(); // 하단의 공용연수 리스트

                int repairNum = 0; // 보수보강 개입 횟수
                double repairCost = 0; // 보수보강 총 비용(만원)

                int simulation; // 시뮬레이션 횟수

                CabonationInfoDto cabonationInfoDto = cabonationRepository.findByLtAutoNum(autoNum);

                ltRecoveryList.add(cabonationInfoDto.getLtRecoveryOne());
                ltRecoveryList.add(cabonationInfoDto.getLtRecoveryTwo());
                ltRecoveryList.add(cabonationInfoDto.getLtRecoveryThree());
                ltRecoveryList.add(cabonationInfoDto.getLtRecoveryFour());
                ltRecoveryList.add(cabonationInfoDto.getLtRecoveryFive());
                ltRecoveryList.add(cabonationInfoDto.getLtRecoverySix());
                ltCostList.add(cabonationInfoDto.getLtCostOne());
                ltCostList.add(cabonationInfoDto.getLtCostTwo());
                ltCostList.add(cabonationInfoDto.getLtCostThree());
                ltCostList.add(cabonationInfoDto.getLtCostFour());
                ltCostList.add(cabonationInfoDto.getLtCostFive());
                ltCostList.add(cabonationInfoDto.getLtCostSix());

                // 사용자 입력 난수 -  평균값
                double tdAverage = cabonationInfoDto.getLtTdAverage(); // 실측피복두께 평균값
                double yAverage = cabonationInfoDto.getLtYAverage(); // 공용연수 평균값
                double aAverage = cabonationInfoDto.getLtAAverage(); // 탄산화속도계수 평균값
                double cAverage = cabonationInfoDto.getLtCAverage(); // 탄산화깊이 평균값 - 어디서쓰는지 모름 확인필요

                // 사용자 입력 난수 -  표준편차
                double tdStandard = cabonationInfoDto.getLtTdStandard(); // 실측피복두께 표준편차
                double yVariance = cabonationInfoDto.getLtYVariance()/100; // 공용연수 변동계수
                double aStandard = cabonationInfoDto.getLtAStandard(); // 탄산화속도계수 표준편차
                double cStandard = cabonationInfoDto.getLtCStandard(); // 탄산화깊이 표준편차 - 어디서쓰는지 모름 확인필요

                simulation = cabonationInfoDto.getLtSimulation()+1; // 시뮬레이션 횟수

                // 공용연수
                int publicYear = (int) yAverage;

                // 성능평가
                double rand;
                for(int area=publicYear; area<publicYear+21; area++){
                    int pf_Count = 0; // PF<0 카운트
                    double result;
                    for(int i=1; i<simulation; i++) {
                        rand = Math.random();
                        double tdNum = NormMath.inv(rand, tdAverage, tdStandard); // td 난수

                        rand = Math.random();
                        double yNum = NormMath.inv(rand, 1, yVariance); // Y 난수

                        rand = Math.random();
                        double aNum = NormMath.inv(rand, aAverage, aStandard); // A 난수

                        // =$C21- $E21*SQRT(X$17*$D21)  -> 엑셀 식
                        result = tdNum-aNum*Math.sqrt(area*yNum);

                        if(result<0){
                            pf_Count++;
                        }
                    }

//                    log.info(area+"번 pf_Count : "+pf_Count);
                    double pf = (double)pf_Count/simulation;
                    // Pf 리스트
                    pf_List.add(pf);
                    pf_0List.add(pf_Count);
                }

                // B(베타)리스트, b1_list 계산
                for(int i=0; i<pf_List.size(); i++){
                    if(i == pf_List.size()-1){
                        if(pf_List.get(i) == 0){
                            b_List.add(-NormMath.sinv(0.00000001));
                        }else{
                            b_List.add(-NormMath.sinv(pf_List.get(i)));
                        }
                    }else{
                        if(pf_List.get(i) == 0){
                            b_List.add(pf_List.get(i+1)*1.01);
                        }else{
                            b_List.add(-NormMath.sinv(pf_List.get(i)));
                        }
                    }
                    if(b_List.get(i)<cabonationInfoDto.getLtTargetValue()){
                        b1_List.add(0.0);
                        if(maxYear == 0){
                            maxYear = publicYear-1;
                        }
                    }else{
                        b1_List.add(b_List.get(i));
                    }
                    publicYear ++;
                }

                // 공용연수 초기화
                publicYear = (int) yAverage;

                // 우측의 step2_list, step1_value 계산
                int a = 0;
                for(int i=publicYear; i<publicYear+21; i++){
                    double value;
                    if(i<maxYear) {
                        value = b_List.get(a)*cabonationInfoDto.getLtRecoveryPercent();
                    }else{
                        value = 0.0;
                    }

                    if(cabonationInfoDto.getLtTargetValue()<value){
                        step1_value ++;
                    }
                    step2_list.add(value);
                    a++;
                }

                // 우측의 step1_list 계산
                int x = publicYear;
                for(int i=publicYear+1; i<publicYear+22; i++) {
                    step1_list.add(x+1);
                    if (i % step1_value == 0) {
                        x = publicYear;
                    }else{
                        x++;
                    }
                }

                b_List.sort(Collections.reverseOrder()); // b_List 내림차순으로 정렬
                int y = publicYear;
                for(int i=0; i<step1_list.size(); i++) {
                    int step = step1_list.get(i)-y;
                    double b_List_value = b_List.get(step-1);
                    referenceTable_List.add(b_List_value*cabonationInfoDto.getLtRecoveryPercent());
                }

                // b2_list 계산
                int z = 0;
                int c = 0;
                for(int i=publicYear; i<publicYear+21; i++) {
                    if(i-maxYear<0 || i-maxYear==0){
                        b2_List.add(b1_List.get(z));
                        z++;
                    }else{
                        b2_List.add(referenceTable_List.get(c));
                        c++;
                    }
                }

                // adjustYear_list 계산
                int b1_num = 1; // B1,B2 리스트의 두번째를 제어
                int b2_num = 0; // B2 첫번째값 제어
                int publicYearNum = publicYear;
                for(int i=publicYear; i<publicYear+21; i++) {
                    // 처음엔 기본 공용연수 값 넣기
                    if(i==publicYear){
                        adjustYear_list.add(publicYear);
                    }else{
                        double b1_value1 = b1_List.get(b1_num);
                        double b2_value1 = b2_List.get(b1_num);
                        double b2_value2 = b2_List.get(b2_num);

                        if(b1_value1 < b2_value1 || b1_value1 > b2_value1){
                            if(b2_value2 < b2_value1){
                                adjustYear_list.add(publicYearNum);
                                repairNum++;
                            }else{
                                adjustYear_list.add(publicYearNum+1);
                            }
                        }else{
                            adjustYear_list.add(publicYearNum+1);
                        }
                        publicYearNum++;
                        b1_num++;
                        b2_num++;
                    }
                }

                for(int i=0; i<ltRecoveryList.size(); i++){
                    if(cabonationInfoDto.getLtRecoveryPercent().equals(ltRecoveryList.get(i))){
                        repairCost = Integer.parseInt(String.valueOf(Math.round(cabonationInfoDto.getLtRepairLength() * repairNum * ltCostList.get(i))));
                        break;
                    }
                }

                System.out.println();
                log.info("무조치시 PF<0 pf_0List : " + pf_0List);
                log.info("무조치시 PF pf_List : " + pf_List);
                log.info("무조치시 B b_List : " + b_List);
//                log.info("무조치시 PF<0 pf_0List.size() : " + pf_0List.size());
//                log.info("무조치시 PF pf_List.size() : " + pf_List.size());
//                log.info("무조치시 B b_List.size() : " + b_List.size());

                pf_max = Collections.max(pf_List);
                pf_min = -NormMath.sinv(pf_max);
                b_max =  Collections.max(b_List);
                log.info("손상확률 최대값(PF 최댓값) : " + pf_max);
                log.info("손상확률 최소값: " + pf_min);
                log.info("신뢰성 지수 최대값 : " + b_max);
                System.out.println();

                log.info("유지보수시 b1_List : " + b1_List);
//                log.info("유지보수시 b1_List.size() : " + b1_List.size());
                log.info("유지보수시 b2_List : " + b2_List);
//                log.info("유지보수시 b2_List.size() : " + b2_List.size());

                log.info("유지보수 무조치 가능한 최대년수 maxYear : " + maxYear);
                log.info("우측의 step2_list : " + step2_list);
//                log.info("우측의 step2_list.size() : " + step2_list.size());
                log.info("우측의 step1_value 값 : " + step1_value);
                log.info("우측의 step1_list : " + step1_list);
//                log.info("우측의 step1_list.size() : " + step1_list.size());
                log.info("우측의 referenceTable_List : " + referenceTable_List);
//                log.info("우측의 referenceTable_List.size() : " + referenceTable_List.size());
                log.info("하단의 공용연수 adjustYear_list : " + adjustYear_list);
                log.info("하단의 공용연수 adjustYear_list.size() : " + adjustYear_list.size());
                log.info("보수보강 개입 횟수 repairNum : " + repairNum);
                log.info("보수보강 총 비용(만원) repairCost : " + repairCost);

                // 차트의 JSON정보를 담을 Array 선언
                List<HashMap<String,Object>> noactionChartDataList = new ArrayList<>(); // 무조치 시 차트데이터
                List<HashMap<String,Object>> actionChartDataList = new ArrayList<>(); // 유지보수 개입시 차트데이터
                HashMap<String,Object> noactionchartData;
                HashMap<String,Object> actionchartData;

                int chartNum = 0;
                // 차트데이터 값 for문 알고리즘 20번돌아야됨
                for(int chartData=publicYear; chartData< publicYear+21; chartData++){

                    // 그래프로 보낼 데이터 뽑기 여기서 시작
                    noactionchartData  = new HashMap<>();
                    actionchartData  = new HashMap<>();

                    noactionchartData.put("publicYear", chartData);
                    noactionchartData.put("redline", cabonationInfoDto.getLtTargetValue());
                    noactionchartData.put("noaction", Math.floor(b_List.get(chartNum)*1000)/1000.0);
                    noactionChartDataList.add(noactionchartData);

                    actionchartData.put("publicYear", adjustYear_list.get(chartNum));
                    actionchartData.put("redline", cabonationInfoDto.getLtTargetValue());
                    actionchartData.put("action", Math.floor(b2_List.get(chartNum)*1000)/1000.0);
                    actionChartDataList.add(actionchartData);

                    chartNum++;
                }

                System.out.println();
                log.info("무조치 차트 테스트 : "+noactionChartDataList);
                log.info("무조치 차트 데이터 길이 : "+noactionChartDataList.size());
                log.info("유지보수 차트 테스트 : "+actionChartDataList);
                log.info("유지보수 차트 데이터 길이 : "+actionChartDataList.size());



                data.put("chartName","carbonation"); // 타입

                data.put("publicYear",publicYear); // 공용연수
                data.put("ltRecoveryList",ltRecoveryList); // 회복율
                data.put("ltCostList",ltCostList); // 비용

                data.put("ltRepairLength",cabonationInfoDto.getLtRepairLength()); // 보수보강 총 길이
                data.put("repairNum",repairNum); // 보수보강 개입 횟수
                data.put("repairCost",repairCost); // 보수보강 총 비용(만원)r®

                data.put("pfList",pf_List); // 무조치 시 PF
                data.put("bList",b_List); // 무조치 시 B

                data.put("bOneList",b1_List); // 유지보수 개입 시 B1
                data.put("bTwoList",b2_List); // 유지보수 개입 시 B2

                data.put("pf_max",Math.floor(pf_max*1000)/1000.0); // 손상확률 최대값(PF 최댓값)
                data.put("pf_min",Math.floor(pf_min*1000)/1000.0); // 손상확률 최소값(PF 최소값)
                data.put("bmax",Math.floor(b_max*1000)/1000.0); // 유지보수 개입 시 신뢰성지수최대값

                data.put("ltRecoveryPercent",cabonationInfoDto.getLtRecoveryPercent()*100); // 유지보수 개입 시 보수보강 회복율
                data.put("maxYear",maxYear); // 유지보수 무조치 가능한 최대년수

                data.put("ltTargetValue",cabonationInfoDto.getLtTargetValue()); // 생애주기 목표값(성능유지 기준값)
                data.put("noactionChartDataList",noactionChartDataList);
                data.put("actionChartDataList",actionChartDataList);




            }

            else if(optionalLifeDetail.get().getLtDetailType().equals("3")){
                // 균열깊이
                log.info("균열깊이 아웃풋시작");

            }

            else if(optionalLifeDetail.get().getLtDetailType().equals("4")) {
                // 열화침투량
                log.info("균열깊이 아웃풋시작");
            }

            else if(optionalLifeDetail.get().getLtDetailType().equals("5")) {
                // 탄산화깊이 바닥판3개
                log.info("탄산화깊이 바닥판3개 아웃풋시작");

                // 변수 선언자리
                List<Double> ltRecoveryList = new ArrayList<>();
                List<Double> ltCostList = new ArrayList<>();

                List<Double> pf_List_before_plate1 = new ArrayList<>(); // 바닥판1의 PF(Before) 리스트

                List<Integer> pf_List_zero_plate1 = new ArrayList<>(); // 바닥판1의 PF<0 리스트
                List<Double> pf_List_plate1 = new ArrayList<>(); // 바닥판1의 PF 리스트
                List<Double> b_List_plate1 = new ArrayList<>(); // 바닥판1의 B 리스트

                List<Integer> pf_List_zero_plate2 = new ArrayList<>(); // 바닥판2의 PF<0 리스트
                List<Double> pf_List_plate2 = new ArrayList<>(); // 바닥판2의 PF 리스트
                List<Double> b_List_plate2 = new ArrayList<>(); // 바닥판2의 B 리스트

                List<Integer> pf_List_zero_plate3 = new ArrayList<>(); // 바닥판3의 PF<0 리스트
                List<Double> pf_List_plate3 = new ArrayList<>(); // 바닥판3의 PF 리스트
                List<Double> b_List_plate3 = new ArrayList<>(); // 바닥판3의 B 리스트

//                List<Integer> pf_0List = new ArrayList<>(); // 무조치시 PF<0 리스트
//                List<Double> pf_List = new ArrayList<>(); // 무조치시 PF 리스트
//                List<Double> b_List = new ArrayList<>(); // 무조치시 B 리스트
//
//                double pf_max; // 손상확률 최대값(PF 최댓값)
//                double pf_min; //손상확률 최소값
//                double b_max; // 신뢰성 지수 최대값
//
//                List<Double> b1_List = new ArrayList<>(); // 유지보수 개입 시 B1 리스트
//                List<Double> b2_List = new ArrayList<>(); // 유지보수 개입 시 B2 리스트
//
//                List<Integer> step1_list = new ArrayList<>(); // 우측의 step1_list
//                List<Double> step2_list = new ArrayList<>(); // 우측의 step2_list
//                int step1_value = 0; // 우측의 step1 값
//                int maxYear = 0; // 유지보수 무조치 가능한 최대년수
//
//                List<Double> referenceTable_List = new ArrayList<>(); // 우측 레퍼런스테이블
//
//                List<Integer> adjustYear_list = new ArrayList<>(); // 하단의 공용연수 리스트
//
//                int repairNum = 0; // 보수보강 개입 횟수
//                double repairCost = 0; // 보수보강 총 비용(만원)

                int simulation; // 시뮬레이션 횟수

                CabonationThreePlateInfoDto cabonationThreePlateInfoDto = cabonationThreePlateRepository.findByLtAutoNum(autoNum);

                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoveryOne());
                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoveryTwo());
                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoveryThree());
                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoveryFour());
                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoveryFive());
                ltRecoveryList.add(cabonationThreePlateInfoDto.getLtRecoverySix());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostOne());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostTwo());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostThree());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostFour());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostFive());
                ltCostList.add(cabonationThreePlateInfoDto.getLtCostSix());

                // 바닥판 1 : 사용자 입력 난수 -  평균값
                double tdAveragePlate1 = cabonationThreePlateInfoDto.getLtTdAveragePlate1(); // 바닥판1의 실측피복두께 평균값
                double aAveragePlate1 = cabonationThreePlateInfoDto.getLtAAveragePlate1(); // 바닥판1의 탄산화속도계수 평균값
//                double cAveragePlate1 = cabonationThreePlateInfoDto.getLtCAveragePlate1(); // 바닥판1의 탄산화깊이 평균값

                // 바닥판 1 : 사용자 입력 난수 -  표준편차
                double tdStandardPlate1 = cabonationThreePlateInfoDto.getLtTdStandardPlate1(); // 바닥판1의 실측피복두께 표준편차
                double aStandardPlate1 = cabonationThreePlateInfoDto.getLtAStandardPlate1(); // 바닥판1의 탄산화속도계수 표준편차
//                double cStandardPlate1 = cabonationThreePlateInfoDto.getLtCStandardPlate1(); // 바닥판1의 탄산화깊이 표준편차

                // 바닥판 2 : 사용자 입력 난수 -  평균값
                double tdAveragePlate2 = cabonationThreePlateInfoDto.getLtTdAveragePlate2(); // 바닥판2의 실측피복두께 평균값
                double aAveragePlate2 = cabonationThreePlateInfoDto.getLtAAveragePlate2(); // 바닥판2의 탄산화속도계수 평균값
//                double cAveragePlate2 = cabonationThreePlateInfoDto.getLtCAveragePlate2(); // 바닥판2의 탄산화깊이 평균값

                // 바닥판 2 : 사용자 입력 난수 -  표준편차
                double tdStandardPlate2 = cabonationThreePlateInfoDto.getLtTdStandardPlate2(); // 바닥판2의 실측피복두께 표준편차
                double aStandardPlate2 = cabonationThreePlateInfoDto.getLtAStandardPlate2(); // 바닥판2의 탄산화속도계수 표준편차
//                double cStandardPlate2 = cabonationThreePlateInfoDto.getLtCStandardPlate2(); // 바닥판2의 탄산화깊이 표준편차

                // 바닥판 3 : 사용자 입력 난수 -  평균값
                double tdAveragePlate3 = cabonationThreePlateInfoDto.getLtTdAveragePlate3(); // 바닥판3의 실측피복두께 평균값
                double aAveragePlate3 = cabonationThreePlateInfoDto.getLtAAveragePlate3(); // 바닥판3의 탄산화속도계수 평균값
//                double cAveragePlate3 = cabonationThreePlateInfoDto.getLtCAveragePlate3(); // 바닥판3의 탄산화깊이 평균값

                // 바닥판 3 : 사용자 입력 난수 -  표준편차
                double tdStandardPlate3 = cabonationThreePlateInfoDto.getLtTdStandardPlate3(); // 바닥판3의 실측피복두께 표준편차
                double aStandardPlate3 = cabonationThreePlateInfoDto.getLtAStandardPlate3(); // 바닥판3의 탄산화속도계수 표준편차
//                double cStandardPlate3 = cabonationThreePlateInfoDto.getLtCStandardPlate3(); // 바닥판3의 탄산화깊이 표준편차

                // 공용연수 조정계수 : 사용자 입력 난수 -  표준편차, 평균값
                double ltYStandard = cabonationThreePlateInfoDto.getLtYStandard(); // 공용연수 조정계수 표준편차
                double ltYAverage = cabonationThreePlateInfoDto.getLtYAverage(); // 공용연수 조정계수 평균값

                simulation = cabonationThreePlateInfoDto.getLtSimulation()+1; // 시뮬레이션 횟수

                // 공용연수
                int publicYear = cabonationThreePlateInfoDto.getLtPublicYear();

                // 테스트 완료 Apache Commons Math » 3.6.1 활용
                NormalDistribution normalDistribution = new NormalDistribution();
                double normTest = 1-normalDistribution.cumulativeProbability(2.52);
                log.info("원하는 결과 : 0.00586, 테스트 결과 : "+Math.floor(normTest*100000)/100000);

                log.info("평균값 : "+tdAveragePlate1);
                log.info("표준편차 : "+tdStandardPlate1);

                // 성능평가
                double rand;
                for(int area=publicYear; area<publicYear+21; area++){

                    int pf_Count_Plate1 = 0; // 바닥판1의 PF<0 카운트
                    int pf_Count_Plate2 = 0; // 바닥판2의 PF<0 카운트
                    int pf_Count_Plate3 = 0; // 바닥판3의 PF<0 카운트
                    double result_Plate1;
                    double result_Plate2;
                    double result_Plate3;

                    for(int i=1; i<simulation; i++) {
                        rand = Math.random();
                        double yNum = NormMath.inv(rand, ltYStandard, ltYAverage); // Y 난수

                        // =$D29- $E29*SQRT(Q$25*$C29)  -> 엑셀 식
                        // 바닥판1
                        rand = Math.random();
                        double tdNumPlate1 = NormMath.inv(rand, tdStandardPlate1, tdAveragePlate1); // 바닥판1의 td 난수
                        rand = Math.random();
                        double aNumPlate1 = NormMath.inv(rand, aStandardPlate1, aAveragePlate1); // 바닥판1의 A 난수
                        result_Plate1 = tdNumPlate1-aNumPlate1*Math.sqrt(area*yNum);

                        // 바닥판2
                        rand = Math.random();
                        double tdNumPlate2 = NormMath.inv(rand, tdStandardPlate2, tdAveragePlate2); // 바닥판2의 td 난수
                        rand = Math.random();
                        double aNumPlate2 = NormMath.inv(rand, aStandardPlate2, aAveragePlate2); // 바닥판2의 A 난수
                        result_Plate2 = tdNumPlate2-aNumPlate2*Math.sqrt(area*yNum);

                        // 바닥판3
                        rand = Math.random();
                        double tdNumPlate3 = NormMath.inv(rand, tdStandardPlate3, tdAveragePlate3); // 바닥판3의 td 난수
                        rand = Math.random();
                        double aNumPlate3 = NormMath.inv(rand, aStandardPlate3, aAveragePlate3); // 바닥판3의 A 난수
                        result_Plate3 = tdNumPlate3-aNumPlate3*Math.sqrt(area*yNum);

                        if(result_Plate1<0){
                            pf_Count_Plate1++;
                        }
                        if(result_Plate2<0){
                            pf_Count_Plate2++;
                        }
                        if(result_Plate3<0){
                            pf_Count_Plate3++;
                        }

                    }

                    double pf_plate1 = (double)pf_Count_Plate1/simulation;
                    double pf_plate2 = (double)pf_Count_Plate2/simulation;
                    double pf_plate3 = (double)pf_Count_Plate3/simulation;

                    // Pf 리스트
                    pf_List_plate1.add(pf_plate1);
                    pf_List_plate2.add(pf_plate2);
                    pf_List_plate3.add(pf_plate3);

                    // Pf<0 리스트
                    pf_List_zero_plate1.add(pf_Count_Plate1);
                    pf_List_zero_plate2.add(pf_Count_Plate2);
                    pf_List_zero_plate3.add(pf_Count_Plate3);

                }

                log.info("");
                log.info("pf_List_plate1 : "+pf_List_plate1);
                log.info("pf_List_plate2 : "+pf_List_plate2);
                log.info("pf_List_plate3 : "+pf_List_plate3);
                log.info("");
                log.info("pf_List_zero_plate1 : "+pf_List_zero_plate1);
                log.info("pf_List_zero_plate2 : "+pf_List_zero_plate2);
                log.info("pf_List_zero_plate3 : "+pf_List_zero_plate3);

//                // B(베타)리스트, b1_list 계산
//                for(int i=0; i<pf_List.size(); i++){
//                    if(i == pf_List.size()-1){
//                        if(pf_List.get(i) == 0){
//                            b_List.add(-NormMath.sinv(0.00000001));
//                        }else{
//                            b_List.add(-NormMath.sinv(pf_List.get(i)));
//                        }
//                    }else{
//                        if(pf_List.get(i) == 0){
//                            b_List.add(pf_List.get(i+1)*1.01);
//                        }else{
//                            b_List.add(-NormMath.sinv(pf_List.get(i)));
//                        }
//                    }
//                    if(b_List.get(i)<cabonationInfoDto.getLtTargetValue()){
//                        b1_List.add(0.0);
//                        if(maxYear == 0){
//                            maxYear = publicYear-1;
//                        }
//                    }else{
//                        b1_List.add(b_List.get(i));
//                    }
//                    publicYear ++;
//                }

//                // 공용연수 초기화
//                publicYear = (int) yAverage;
//
//                // 우측의 step2_list, step1_value 계산
//                int a = 0;
//                for(int i=publicYear; i<publicYear+21; i++){
//                    double value;
//                    if(i<maxYear) {
//                        value = b_List.get(a)*cabonationInfoDto.getLtRecoveryPercent();
//                    }else{
//                        value = 0.0;
//                    }
//
//                    if(cabonationInfoDto.getLtTargetValue()<value){
//                        step1_value ++;
//                    }
//                    step2_list.add(value);
//                    a++;
//                }
//
//                // 우측의 step1_list 계산
//                int x = publicYear;
//                for(int i=publicYear+1; i<publicYear+22; i++) {
//                    step1_list.add(x+1);
//                    if (i % step1_value == 0) {
//                        x = publicYear;
//                    }else{
//                        x++;
//                    }
//                }
//
//                b_List.sort(Collections.reverseOrder()); // b_List 내림차순으로 정렬
//                int y = publicYear;
//                for(int i=0; i<step1_list.size(); i++) {
//                    int step = step1_list.get(i)-y;
//                    double b_List_value = b_List.get(step-1);
//                    referenceTable_List.add(b_List_value*cabonationInfoDto.getLtRecoveryPercent());
//                }
//
//                // b2_list 계산
//                int z = 0;
//                int c = 0;
//                for(int i=publicYear; i<publicYear+21; i++) {
//                    if(i-maxYear<0 || i-maxYear==0){
//                        b2_List.add(b1_List.get(z));
//                        z++;
//                    }else{
//                        b2_List.add(referenceTable_List.get(c));
//                        c++;
//                    }
//                }
//
//                // adjustYear_list 계산
//                int b1_num = 1; // B1,B2 리스트의 두번째를 제어
//                int b2_num = 0; // B2 첫번째값 제어
//                int publicYearNum = publicYear;
//                for(int i=publicYear; i<publicYear+21; i++) {
//                    // 처음엔 기본 공용연수 값 넣기
//                    if(i==publicYear){
//                        adjustYear_list.add(publicYear);
//                    }else{
//                        double b1_value1 = b1_List.get(b1_num);
//                        double b2_value1 = b2_List.get(b1_num);
//                        double b2_value2 = b2_List.get(b2_num);
//
//                        if(b1_value1 < b2_value1 || b1_value1 > b2_value1){
//                            if(b2_value2 < b2_value1){
//                                adjustYear_list.add(publicYearNum);
//                                repairNum++;
//                            }else{
//                                adjustYear_list.add(publicYearNum+1);
//                            }
//                        }else{
//                            adjustYear_list.add(publicYearNum+1);
//                        }
//                        publicYearNum++;
//                        b1_num++;
//                        b2_num++;
//                    }
//                }
//
//                for(int i=0; i<ltRecoveryList.size(); i++){
//                    if(cabonationInfoDto.getLtRecoveryPercent().equals(ltRecoveryList.get(i))){
//                        repairCost = Integer.parseInt(String.valueOf(Math.round(cabonationInfoDto.getLtRepairLength() * repairNum * ltCostList.get(i))));
//                        break;
//                    }
//                }
//
//                System.out.println();
//                log.info("무조치시 PF<0 pf_0List : " + pf_0List);
//                log.info("무조치시 PF pf_List : " + pf_List);
//                log.info("무조치시 B b_List : " + b_List);
////                log.info("무조치시 PF<0 pf_0List.size() : " + pf_0List.size());
////                log.info("무조치시 PF pf_List.size() : " + pf_List.size());
////                log.info("무조치시 B b_List.size() : " + b_List.size());
//
//                pf_max = Collections.max(pf_List);
//                pf_min = -NormMath.sinv(pf_max);
//                b_max =  Collections.max(b_List);
//                log.info("손상확률 최대값(PF 최댓값) : " + pf_max);
//                log.info("손상확률 최소값: " + pf_min);
//                log.info("신뢰성 지수 최대값 : " + b_max);
//                System.out.println();
//
//                log.info("유지보수시 b1_List : " + b1_List);
////                log.info("유지보수시 b1_List.size() : " + b1_List.size());
//                log.info("유지보수시 b2_List : " + b2_List);
////                log.info("유지보수시 b2_List.size() : " + b2_List.size());
//
//                log.info("유지보수 무조치 가능한 최대년수 maxYear : " + maxYear);
//                log.info("우측의 step2_list : " + step2_list);
////                log.info("우측의 step2_list.size() : " + step2_list.size());
//                log.info("우측의 step1_value 값 : " + step1_value);
//                log.info("우측의 step1_list : " + step1_list);
////                log.info("우측의 step1_list.size() : " + step1_list.size());
//                log.info("우측의 referenceTable_List : " + referenceTable_List);
////                log.info("우측의 referenceTable_List.size() : " + referenceTable_List.size());
//                log.info("하단의 공용연수 adjustYear_list : " + adjustYear_list);
//                log.info("하단의 공용연수 adjustYear_list.size() : " + adjustYear_list.size());
//                log.info("보수보강 개입 횟수 repairNum : " + repairNum);
//                log.info("보수보강 총 비용(만원) repairCost : " + repairCost);
//
//                // 차트의 JSON정보를 담을 Array 선언
//                List<HashMap<String,Object>> noactionChartDataList = new ArrayList<>(); // 무조치 시 차트데이터
//                List<HashMap<String,Object>> actionChartDataList = new ArrayList<>(); // 유지보수 개입시 차트데이터
//                HashMap<String,Object> noactionchartData;
//                HashMap<String,Object> actionchartData;
//
//                int chartNum = 0;
//                // 차트데이터 값 for문 알고리즘 20번돌아야됨
//                for(int chartData=publicYear; chartData< publicYear+21; chartData++){
//
//                    // 그래프로 보낼 데이터 뽑기 여기서 시작
//                    noactionchartData  = new HashMap<>();
//                    actionchartData  = new HashMap<>();
//
//                    noactionchartData.put("publicYear", chartData);
//                    noactionchartData.put("redline", cabonationInfoDto.getLtTargetValue());
//                    noactionchartData.put("noaction", Math.floor(b_List.get(chartNum)*1000)/1000.0);
//                    noactionChartDataList.add(noactionchartData);
//
//                    actionchartData.put("publicYear", adjustYear_list.get(chartNum));
//                    actionchartData.put("redline", cabonationInfoDto.getLtTargetValue());
//                    actionchartData.put("action", Math.floor(b2_List.get(chartNum)*1000)/1000.0);
//                    actionChartDataList.add(actionchartData);
//
//                    chartNum++;
//                }
//
//                System.out.println();
//                log.info("무조치 차트 테스트 : "+noactionChartDataList);
//                log.info("무조치 차트 데이터 길이 : "+noactionChartDataList.size());
//                log.info("유지보수 차트 테스트 : "+actionChartDataList);
//                log.info("유지보수 차트 데이터 길이 : "+actionChartDataList.size());
//
//
//
//                data.put("chartName","carbonation"); // 타입
//
//                data.put("publicYear",publicYear); // 공용연수
//                data.put("ltRecoveryList",ltRecoveryList); // 회복율
//                data.put("ltCostList",ltCostList); // 비용
//
//                data.put("ltRepairLength",cabonationInfoDto.getLtRepairLength()); // 보수보강 총 길이
//                data.put("repairNum",repairNum); // 보수보강 개입 횟수
//                data.put("repairCost",repairCost); // 보수보강 총 비용(만원)r®
//
//                data.put("pfList",pf_List); // 무조치 시 PF
//                data.put("bList",b_List); // 무조치 시 B
//
//                data.put("bOneList",b1_List); // 유지보수 개입 시 B1
//                data.put("bTwoList",b2_List); // 유지보수 개입 시 B2
//
//                data.put("pf_max",Math.floor(pf_max*1000)/1000.0); // 손상확률 최대값(PF 최댓값)
//                data.put("pf_min",Math.floor(pf_min*1000)/1000.0); // 손상확률 최소값(PF 최소값)
//                data.put("bmax",Math.floor(b_max*1000)/1000.0); // 유지보수 개입 시 신뢰성지수최대값
//
//                data.put("ltRecoveryPercent",cabonationInfoDto.getLtRecoveryPercent()*100); // 유지보수 개입 시 보수보강 회복율
//                data.put("maxYear",maxYear); // 유지보수 무조치 가능한 최대년수
//
//                data.put("ltTargetValue",cabonationInfoDto.getLtTargetValue()); // 생애주기 목표값(성능유지 기준값)
//                data.put("noactionChartDataList",noactionChartDataList);
//                data.put("actionChartDataList",actionChartDataList);
//
//





            }

            else if(optionalLifeDetail.get().getLtDetailType().equals("6")) {
                // 탄산화깊이(바닥판4개)
                log.info("탄산화깊이(바닥판4개) 아웃풋시작");
            }

            else{
                return ResponseEntity.ok(res.fail("문자", "존재하지 않은 타입번호 입니다. ", "문자", "타입번호 : "+optionalLifeDetail.get().getLtDetailType()));
            }

        }else{
            return ResponseEntity.ok(res.fail("문자", "존재하지 않은 고유코드 입니다. ", "문자", "고유코드 : "+autoNum));
        }

        return ResponseEntity.ok(res.dataSendSuccess(data));
    }

}
