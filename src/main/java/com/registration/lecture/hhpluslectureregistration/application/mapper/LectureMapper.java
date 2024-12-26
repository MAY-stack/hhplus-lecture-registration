package com.registration.lecture.hhpluslectureregistration.application.mapper;

import com.registration.lecture.hhpluslectureregistration.application.dto.LectureDTO;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import org.springframework.stereotype.Component;

@Component
public class LectureMapper {

    public LectureDTO entityToDto(Lecture lecture, Integer availableSeats) {
        return LectureDTO.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .instructorName(lecture.getInstructorName())
                .lectureDate(lecture.getLectureDate())
                .lectureTime(lecture.getLectureTime())
                .availableSeats(availableSeats)
                .capacity(lecture.getCapacity())
                .build();
    }
}
