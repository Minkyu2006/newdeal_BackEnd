package com.broadwave.backend.performance;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Minkyu
 * Date : 2021-07-07
 * Time :
 * Remark : 뉴딜 성능개선사업평가 관련 Dto
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PerformanceDto {
    private String piFacilityType; // 시설유형(NOTNULL)
    private String piFacilityName; // 시설유형(NULL)
    private String piKind; // 종별구분(NOTNULL)
    private Double piCompletionYear; // 준공연도(NOTNULL)
    private Double piPublicYear; // 공용연수(NOTNULL)
    private String piType; // 형식구분(NULL)
    private Long piErectionCost; // 취득원가(NOTNULL)
    private String piSafetyLevel; // 안전등급(NOTNULL)
    private String piUsabilityLevel; // 사용성등급(NOTNULL)
    private String piGoalLevel; // 목표등급(NOTNULL)
    private Double piMaintenanceDelay;  // 유지보수기간(NOTNULL)
    private String piManagement; // 관리주체(NULL)
    private String piAgency; // 관리감독기관(NULL)
    private Double piAADT; // 연평균일교통량(NOTNULL)
    private String piBusiness; // 사업구분(NOTNULL)
    private String piBusinessType; // 사업유형(NOTNULL)
    private String piTargetAbsence; // 대상부재(NULL)
    private String piBusinessClassification; // 사업분류(NOTNULL)
    private Long piBusinessExpenses; // 사업비용(NOTNULL)
    private String piBeforeSafetyRating; // 사업전 부재 안전등급(NOTNULL)
    private String piAfterSafetyRating; // 사업후 부재 안전등급(NOTNULL)
    private Double piBusinessObligatory; // 법에 따른 의무사업(NOTNULL)
    private Double piBusinessMandatory; // 법정계획에 따른 의무사업(NOTNULL)
    private Double piBusinessPlanned; // 자체계획/의결에 따른 사업(NOTNULL)
    private Double piWhether; // 최근 1년간 민원 및 사고발생 건수(NOTNULL)
    private Double piRaterBaseYear; // 평가 기준년도(NOTNULL)
    private String piRater; // 평가자이름(NULL)
    private String piRaterBelong; // 평자가 소속(NULL)
    private String piRaterPhone; // 평가자 연락처(NULL)
    private Integer piInputCount; //대안카운트(NULL)
}
