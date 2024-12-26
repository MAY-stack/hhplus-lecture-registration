package com.registration.lecture.hhpluslectureregistration.application.facade;

import com.registration.lecture.hhpluslectureregistration.application.dto.RegistrationDTO;
import com.registration.lecture.hhpluslectureregistration.application.mapper.RegistrationMapper;
import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.RegistrationService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.StudentService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationFacade {

    private final LectureService lectureService;
    private final RegistrationService registrationService;
    private final StudentService studentService;
    private final RegistrationMapper registrationMapper;

    // 수강 신청
    @Transactional
    public RegistrationDTO registerLecture(Long studentId, Long lectureId) throws Exception {
        // 학생 존재 여부 확인
        Student student = studentService.validateStudentExists(studentId);
        // PESSIMISTIC_WRITE 잠금을 사용 하여 강좌 조회
        Lecture lecture = lectureService.findLectureWithLock(lectureId);
        // 중복 신청 여부 확인
        registrationService.isRegistrationExist(studentId, lectureId);
        // 잔여석 존재 여부 확인
        registrationService.checkAvailableSeats(lecture);
        // 수강 신청 등록
        Registration registration = registrationService.createRegistration(studentId, lectureId);
        return registrationMapper.toRegistrationDTO(student, lecture, registration);
    }

    // 수강 신청 내역 조회
    public List<RegistrationDTO> findRegistrationByStudentId(Long studentId) {
        // 학생 존재 여부 확인
        Student student = studentService.validateStudentExists(studentId);
        // 수강 신청 내역 조회 (Registration과 Lecture 함께 가져오기)
        List<Object[]> results = registrationService.findRegistrationsWithLectureByStudentId(studentId);
        return results.stream()
                .map(result -> {
                    Registration registration = (Registration) result[0];
                    Lecture lecture = (Lecture) result[1];
                    return registrationMapper.toRegistrationDTO(student, lecture, registration);
                })
                .collect(Collectors.toList());
    }
}