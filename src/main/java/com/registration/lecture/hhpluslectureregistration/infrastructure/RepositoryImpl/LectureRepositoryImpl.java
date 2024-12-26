package com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl;


import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.infrastructure.JpaRepository.JpaLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

    private final JpaLectureRepository jpaLectureRepository;

    @Override
    public Optional<Lecture> findById(Long lectureId) {
        return jpaLectureRepository.findById(lectureId);
    }

    // 강좌 조회
    @Override
    public Optional<Lecture> findByIdWithLock(Long lectureId) {
        return jpaLectureRepository.findByIdWithLock(lectureId);
    }

    // 날짜별 강좌 조회
    @Override
    public List<Lecture> findAllByLectureDateOrderByLectureTimeAsc(LocalDate date) {
        return jpaLectureRepository.findAllByLectureDateOrderByLectureTimeAsc(date);
    }

    // 강좌 저장
    @Override
    public Lecture save(Lecture lecture) {
        return jpaLectureRepository.save(lecture);
    }

    // 강좌 일괄 저장
    @Override
    public List<Lecture> saveAll(List<Lecture> lectureList) {
        return jpaLectureRepository.saveAll(lectureList);
    }

    // 모든 강좌 조회
    @Override
    public List<Lecture> findAll() {
        return jpaLectureRepository.findAll();
    }

}
