package com.registration.lecture.hhpluslectureregistration.application.facade;

import com.registration.lecture.hhpluslectureregistration.application.dto.LectureDTO;
import com.registration.lecture.hhpluslectureregistration.application.mapper.LectureMapper;
import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureFacade {
    private final LectureService lectureService;
    private final LectureMapper lectureMapper;

    // 날짜별 수강 신청 가능 강좌 조회
    public List<LectureDTO> findAvailableLecturesByDate(LocalDate date) {
        // 강좌를 잔여석 기준으로 필터링
        List<Lecture> allLectures = lectureService.findAllByLectureDateOrderByLectureTimeAsc(date);

        return allLectures.stream()
                .map(lecture -> {
                    int availableSeats = lectureService.getAvailableSeats(lecture); // 잔여석 계산
                    if (availableSeats > 0) {
                        return lectureMapper.entityToDto(lecture, availableSeats);
                    }
                    return null;
                })
                .filter(Objects::nonNull) // 잔여석 없는 경우 제거
                .collect(Collectors.toList());
    }
}