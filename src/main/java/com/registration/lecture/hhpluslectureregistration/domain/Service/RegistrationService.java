package com.registration.lecture.hhpluslectureregistration.domain.Service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;

    // 강좌 수강 신청 학생 수 조회
    public Integer getRegisteredStudentsCount(Long lectureId) {
        return registrationRepository.countByLectureId(lectureId);
    }

    // 잔여석 존재 여부 확인
    public void checkAvailableSeats(Lecture lecture) throws BadRequestException {
        int registeredCount = registrationRepository.countByLectureId(lecture.getLectureId());
        if (lecture.getCapacity() <= registeredCount) {
            throw new BadRequestException("수강 신청 가능 인원을 초과했습니다.");
        }
    }

    // 수강 신청 내역 조회
    public List<Registration> findAllByStudentIdOrderByRegistrationDtm(Long studentId) {
        return registrationRepository.findAllByStudentIdOrderByRegistrationDtm(studentId);
    }

    public List<Object[]> findRegistrationsWithLectureByStudentId(Long studentId) {
        return registrationRepository.findRegistrationsWithLectureByStudentId(studentId);
    }

    // 수강 신청 생성
    public Registration createRegistration(Long studentId, Long lectureId) {
        return registrationRepository.save(new Registration(studentId, lectureId));
    }

    // 수강 신청 내역 존재 여부
    public void isRegistrationExist(Long studentId, Long lectureId) throws BadRequestException {
        if (registrationRepository.existsByStudentIdAndLectureId(studentId, lectureId)) {
            throw new BadRequestException("이미 수강신청한 강좌입니다.");
        }
    }

}
