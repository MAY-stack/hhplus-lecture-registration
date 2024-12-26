package com.registration.lecture.hhpluslectureregistration.domain.Repository;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository {
    // 강좌 조회
    Optional<Lecture> findById(Long lectureId);

    // 강좌 조회 (락)
    Optional<Lecture> findByIdWithLock(Long lectureId);

    // 날짜별 강좌 조회
    List<Lecture> findAllByLectureDateOrderByLectureTimeAsc(LocalDate date);

    // 강좌 저장
    Lecture save(Lecture lecture);

    // 강좌 일괄 저장
    List<Lecture> saveAll(List<Lecture> lectureList);

    // 모든 강좌 조회
    List<Lecture> findAll();

}
