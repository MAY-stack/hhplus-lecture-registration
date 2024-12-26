package com.registration.lecture.hhpluslectureregistration.domain.Service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final RegistrationRepository registrationRepository;

    // 강좌 생성
    public Lecture createLecture(String lectureTitle,
                                 String instructorName,
                                 LocalDate lectureDate,
                                 String lectureTime,
                                 Integer capacity) {
        Lecture lecture = Lecture.builder()
                .lectureTitle(lectureTitle)
                .instructorName(instructorName)
                .lectureDate(lectureDate)
                .lectureTime(lectureTime)
                .capacity(capacity)
                .build();
        return lectureRepository.save(lecture);
    }

    // 강좌 존재 여부 검증
    public Lecture validateLectureExists(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌가 존재하지 않습니다."));
    }

    // 강좌 조회 (락)
    public Lecture findLectureWithLock(Long lectureId) {
        return lectureRepository.findByIdWithLock(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌가 존재하지 않습니다."));
    }

    // 강좌의 잔여석 계산
    public int getAvailableSeats(Lecture lecture) {
        int registeredCount = registrationRepository.countByLectureId(lecture.getLectureId());
        return lecture.getCapacity() - registeredCount;
    }

    // 날짜별 강좌 조회
    public List<Lecture> findAllByLectureDateOrderByLectureTimeAsc(LocalDate date) {
        return lectureRepository.findAllByLectureDateOrderByLectureTimeAsc(date);
    }

}
