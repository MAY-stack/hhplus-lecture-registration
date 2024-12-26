package com.registration.lecture.hhpluslectureregistration.application.facade;

import com.registration.lecture.hhpluslectureregistration.application.dto.RegistrationDTO;
import com.registration.lecture.hhpluslectureregistration.application.mapper.RegistrationMapper;
import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.RegistrationService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.StudentService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RegistrationFacadeTest {

    @Mock
    private LectureService lectureService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private StudentService studentService;

    @Mock
    private RegistrationMapper registrationMapper;

    @InjectMocks
    private RegistrationFacade registrationFacade;

    @Test
    void 잘못된_학생_아이디로_수강신청시_예외가_발생한다() {
        // Given
        Long studentId = 999L;
        Long lectureId = 1L;

        Mockito.when(studentService.validateStudentExists(studentId))
                .thenThrow(new IllegalArgumentException("해당 학생이 존재하지 않습니다."));

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            registrationFacade.registerLecture(studentId, lectureId);
        });

        Assertions.assertEquals("해당 학생이 존재하지 않습니다.", exception.getMessage());
        Mockito.verify(studentService).validateStudentExists(studentId);
        Mockito.verifyNoInteractions(lectureService, registrationService, registrationMapper);
    }

    @Test
    void 잘못된_강좌_아이디로_수강신청시_예외가_발생한다() {
        // Given
        Long studentId = 1L;
        Long lectureId = 999L;

        Student student = new Student(studentId, "테스트 학생", LocalDateTime.now());

        Mockito.when(studentService.validateStudentExists(studentId)).thenReturn(student);
        Mockito.when(lectureService.findLectureWithLock(lectureId))
                .thenThrow(new IllegalArgumentException("해당 강좌가 존재하지 않습니다."));

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            registrationFacade.registerLecture(studentId, lectureId);
        });

        Assertions.assertEquals("해당 강좌가 존재하지 않습니다.", exception.getMessage());
        Mockito.verify(studentService).validateStudentExists(studentId);
        Mockito.verify(lectureService).findLectureWithLock(lectureId);
        Mockito.verifyNoInteractions(registrationService, registrationMapper);
    }

    @Test
    void 잔여석이_없으면_수강신청_예외가_발생한다() {
        // Given
        Long studentId = 1L;
        Long lectureId = 1L;

        Student student = new Student(studentId, "테스트 학생", LocalDateTime.now());
        Lecture lecture = new Lecture(lectureId, "테스트 강좌", "테스트 강사", LocalDate.now(), "10:00", 30, LocalDateTime.now());

        Mockito.when(studentService.validateStudentExists(studentId)).thenReturn(student);
        Mockito.when(lectureService.findLectureWithLock(lectureId)).thenReturn(lecture);
        Mockito.when(registrationService.getRegisteredStudentsCount(lectureId)).thenReturn(30);

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            registrationFacade.registerLecture(studentId, lectureId);
        });

        Assertions.assertEquals("수강 신청 가능 인원을 초과했습니다.", exception.getMessage());
        Mockito.verify(studentService).validateStudentExists(studentId);
        Mockito.verify(lectureService).findLectureWithLock(lectureId);
        Mockito.verify(registrationService).getRegisteredStudentsCount(lectureId);
    }

    @Test
    void 정상적으로_수강신청이_등록된다() throws Exception {
        // Given
        Long studentId = 1L;
        Long lectureId = 1L;

        Student student = new Student(studentId, "테스트 학생", LocalDateTime.now());
        Lecture lecture = new Lecture(lectureId, "테스트 강좌", "테스트 강사", LocalDate.now(), "10:00", 30, LocalDateTime.now());
        Registration registration = new Registration(1L, studentId, lectureId, LocalDateTime.now());

        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .studentId(studentId)
                .studentName("테스트 학생")
                .lectureId(lectureId)
                .lectureTitle("테스트 강좌")
                .instructorName("테스트 강사")
                .lectureDate(LocalDate.now())
                .lectureTime("10:00")
                .registrationDtm(registration.getRegistrationDtm())
                .build();

        Mockito.when(studentService.validateStudentExists(studentId)).thenReturn(student);
        Mockito.when(lectureService.findLectureWithLock(lectureId)).thenReturn(lecture);
        Mockito.when(registrationService.getRegisteredStudentsCount(lectureId)).thenReturn(10);
        Mockito.when(registrationService.createRegistration(studentId, lectureId)).thenReturn(registration);
        Mockito.when(registrationMapper.toRegistrationDTO(student, lecture, registration)).thenReturn(registrationDTO);

        // When
        RegistrationDTO result = registrationFacade.registerLecture(studentId, lectureId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(studentId, result.getStudentId());
        Assertions.assertEquals(lectureId, result.getLectureId());
        Mockito.verify(studentService).validateStudentExists(studentId);
        Mockito.verify(lectureService).findLectureWithLock(lectureId);
        Mockito.verify(registrationService).getRegisteredStudentsCount(lectureId);
        Mockito.verify(registrationService).createRegistration(studentId, lectureId);
        Mockito.verify(registrationMapper).toRegistrationDTO(student, lecture, registration);
    }

    @Test
    void 학생의_수강신청_내역을_조회하면_결과가_반환된다() {
        // Given
        Long studentId = 1L;

        Student student = new Student(studentId, "테스트 학생", LocalDateTime.now());
        Registration registration = new Registration(1L, studentId, 1L, LocalDateTime.now());
        Lecture lecture = new Lecture(1L, "테스트 강좌", "테스트 강사", LocalDate.now(), "10:00", 30, LocalDateTime.now());

        Object[] mockResult = new Object[]{registration, lecture};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(mockResult);

        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .studentId(studentId)
                .studentName("테스트 학생")
                .lectureId(1L)
                .lectureTitle("테스트 강좌")
                .instructorName("테스트 강사")
                .lectureDate(LocalDate.now())
                .lectureTime("10:00")
                .registrationDtm(registration.getRegistrationDtm())
                .build();

        Mockito.when(studentService.validateStudentExists(studentId)).thenReturn(student);
        Mockito.when(registrationService.findRegistrationsWithLectureByStudentId(studentId)).thenReturn(mockResults);
        Mockito.when(registrationMapper.toRegistrationDTO(student, lecture, registration)).thenReturn(registrationDTO);

        // When
        List<RegistrationDTO> result = registrationFacade.findRegistrationByStudentId(studentId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(registrationDTO, result.get(0));

        Mockito.verify(studentService).validateStudentExists(studentId);
        Mockito.verify(registrationService).findRegistrationsWithLectureByStudentId(studentId);
        Mockito.verify(registrationMapper).toRegistrationDTO(student, lecture, registration);
    }
}