package com.registration.lecture.hhpluslectureregistration.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @Column(nullable = false, length = 100)
    private String lectureTitle;

    @Column(nullable = false, length = 50)
    private String instructorName;

    @Column(nullable = false)
    private LocalDate lectureDate;

    @Column(nullable = false)
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "시간 형식은 HH:mm이어야 합니다.")
    private String lectureTime;

    @Column(nullable = false)
    private Integer capacity;

    @CreationTimestamp
    private LocalDateTime lectureCreatedDtm;
}
