package com.registration.lecture.hhpluslectureregistration.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class RegistrationDTO {
    private final Long studentId;           // 학생 아이디
    private final String studentName;       // 학생 이름
    private final Long lectureId;           // 강조 아이디
    private final String lectureTitle;      // 강좌명
    private final String instructorName;    // 강사명
    private final LocalDate lectureDate;    // 강좌일
    private final String lectureTime;       // 강좌 시간
    private final LocalDateTime registrationDtm;    // 수강 신청 날짜 시간
}
