package com.registration.lecture.hhpluslectureregistration.infrastructure.JpaRepository;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRegistrationRepository extends JpaRepository<Registration, Long> {

    // 학생 아이디와 강좌 아이디로 수강신청 정보 존재 여부 조회
    boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId);

    // 학생 아이디로 수강 신청 내역 조회
    List<Registration> findAllByStudentIdOrderByRegistrationDtm(Long studentId);

    @Query("SELECT r, l FROM Registration r JOIN Lecture l ON r.lectureId = l.lectureId WHERE r.studentId = :studentId ORDER BY r.registrationDtm")
    List<Object[]> findRegistrationsWithLectureByStudentId(@Param("studentId") Long studentId);

    // 강좌 아이디로 수강신청 정보 조회
    Integer countByLectureId(Long lectureId);

}
