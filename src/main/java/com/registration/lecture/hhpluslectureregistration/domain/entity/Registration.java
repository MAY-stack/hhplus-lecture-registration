package com.registration.lecture.hhpluslectureregistration.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor // 모든 필드 초기화 생성자
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"studentId", "lectureId"})
        }
)
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationSeq;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long lectureId;

    @CreationTimestamp
    private LocalDateTime registrationDtm;

    public Registration(Long studentId, Long lectureId) {
        this.studentId = studentId;
        this.lectureId = lectureId;
    }

}
