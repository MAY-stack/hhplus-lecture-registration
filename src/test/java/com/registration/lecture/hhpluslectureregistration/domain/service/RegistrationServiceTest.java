package com.registration.lecture.hhpluslectureregistration.domain.service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Service.RegistrationService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void 강좌의_수강신청_학생수를_조회하면_정확한_숫자가_반환된다() {
        // Given
        Long lectureId = 1L;
        Mockito.when(registrationRepository.countByLectureId(lectureId)).thenReturn(5);

        // When
        Integer registeredCount = registrationService.getRegisteredStudentsCount(lectureId);

        // Then
        Assertions.assertEquals(5, registeredCount);
        Mockito.verify(registrationRepository, Mockito.times(1)).countByLectureId(lectureId);
    }

    @Test
    void 강좌의_잔여석이_없으면_예외를_던진다() {
        // Given
        Lecture lecture = Lecture.builder()
                .lectureId(1L)
                .capacity(10)
                .build();
        Mockito.when(registrationRepository.countByLectureId(lecture.getLectureId())).thenReturn(10);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> registrationService.checkAvailableSeats(lecture));

        Assertions.assertEquals("수강 신청 가능 인원을 초과했습니다.", exception.getMessage());
        Mockito.verify(registrationRepository, Mockito.times(1)).countByLectureId(lecture.getLectureId());
    }

    @Test
    void 학생의_수강신청_내역을_시간순으로_조회할_수_있다() {
        // Given
        Long studentId = 1L;
        Registration reg1 = new Registration(1L, studentId, 1L, LocalDateTime.now().minusDays(1));
        Registration reg2 = new Registration(2L, studentId, 2L, LocalDateTime.now());
        Mockito.when(registrationRepository.findAllByStudentIdOrderByRegistrationDtm(studentId))
                .thenReturn(List.of(reg1, reg2));

        // When
        List<Registration> registrations = registrationService.findAllByStudentIdOrderByRegistrationDtm(studentId);

        // Then
        Assertions.assertEquals(2, registrations.size());
        Assertions.assertEquals(1L, registrations.get(0).getLectureId());
        Assertions.assertEquals(2L, registrations.get(1).getLectureId());
        Mockito.verify(registrationRepository, Mockito.times(1))
                .findAllByStudentIdOrderByRegistrationDtm(studentId);
    }

    @Test
    void 학생의_수강신청과_강좌정보를_조회할_수_있다() {
        // Given
        Long studentId = 1L;
        Registration registration = new Registration(1L, studentId, 1L, LocalDateTime.now());
        Lecture lecture = new Lecture(1L, "테스트 강좌", "테스트 강사", LocalDate.now(), "10:00", 30, null);

        Object[] mockResult = new Object[]{registration, lecture};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(mockResult);

        Mockito.when(registrationRepository.findRegistrationsWithLectureByStudentId(studentId))
                .thenReturn(mockResults);

        // When
        List<Object[]> results = registrationService.findRegistrationsWithLectureByStudentId(studentId);

        // Then
        Assertions.assertEquals(1, results.size());
        Object[] result = results.get(0);
        Registration foundRegistration = (Registration) result[0];
        Lecture foundLecture = (Lecture) result[1];

        Assertions.assertEquals(1L, foundRegistration.getLectureId());
        Assertions.assertEquals("테스트 강좌", foundLecture.getLectureTitle());
        Mockito.verify(registrationRepository, Mockito.times(1)).findRegistrationsWithLectureByStudentId(studentId);
    }

    @Test
    void 수강신청을_생성하면_저장된_수강신청이_반환된다() throws Exception {
        // Given
        Long studentId = 1L;
        Long lectureId = 1L;
        Registration mockRegistration = new Registration(1L, studentId, lectureId, LocalDateTime.now());

        Mockito.when(registrationRepository.save(Mockito.any(Registration.class))).thenReturn(mockRegistration);

        // When
        Registration savedRegistration = registrationService.createRegistration(studentId, lectureId);

        // Then
        Assertions.assertNotNull(savedRegistration);
        Assertions.assertEquals(studentId, savedRegistration.getStudentId());
        Assertions.assertEquals(lectureId, savedRegistration.getLectureId());
        Mockito.verify(registrationRepository, Mockito.times(1)).save(Mockito.any(Registration.class));
    }

    @Test
    void 학생아이디와_강좌아이디로_수강신청내역이_있는경우_예외를_반환한다() {
        // Given
        Long studentId = 1L;
        Long lectureId = 1L;

        Mockito.when(registrationRepository.existsByStudentIdAndLectureId(studentId, lectureId)).thenReturn(true);

        // When
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> registrationService.isRegistrationExist(studentId, lectureId));

        // Then
        assertEquals("이미 수강신청한 강좌입니다.", exception.getMessage());
        Mockito.verify(registrationRepository, Mockito.times(1)).existsByStudentIdAndLectureId(studentId, lectureId);
    }
}
