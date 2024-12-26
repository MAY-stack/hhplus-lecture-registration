package com.registration.lecture.hhpluslectureregistration.application.mapper;

import com.registration.lecture.hhpluslectureregistration.application.dto.RegistrationDTO;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {
    public RegistrationDTO toRegistrationDTO(Student student, Lecture lecture, Registration registration) {
        return RegistrationDTO.builder()
                .studentId(registration.getStudentId())
                .studentName(student.getStudentName())
                .lectureId(registration.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .instructorName(lecture.getInstructorName())
                .lectureDate(lecture.getLectureDate())
                .lectureTime(lecture.getLectureTime())
                .registrationDtm(registration.getRegistrationDtm())
                .build();
    }
}
