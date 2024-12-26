package com.registration.lecture.hhpluslectureregistration.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class LectureDTO {
    private final Long lectureId;           // 강좌 아이디
    private final String lectureTitle;      // 강좌명
    private final String instructorName;    // 강사명
    private final LocalDate lectureDate;    // 강좌일
    private final String lectureTime;       // 강좌 시간
    private final Integer availableSeats;   // 잔여석
    private final Integer capacity;         // 정원
}
