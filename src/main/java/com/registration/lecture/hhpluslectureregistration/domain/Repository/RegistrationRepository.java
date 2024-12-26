package com.registration.lecture.hhpluslectureregistration.domain.Repository;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository {

    // 수강 신청 내역 존재 여부
    boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId);

    // 강좌 수강 신청 학생 수 조회
    Integer countByLectureId(Long lectureId);

    // 수강 신청 내역 조회
    List<Registration> findAllByStudentIdOrderByRegistrationDtm(Long studentId);

    List<Object[]> findRegistrationsWithLectureByStudentId(Long studentId);

    // 수강 신청
    Registration save(Registration registration);
}
