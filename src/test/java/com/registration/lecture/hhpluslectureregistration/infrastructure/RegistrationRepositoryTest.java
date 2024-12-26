package com.registration.lecture.hhpluslectureregistration.infrastructure;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl.LectureRepositoryImpl;
import com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl.RegistrationRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Import({RegistrationRepositoryImpl.class, LectureRepositoryImpl.class})
class RegistrationRepositoryTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Test
    void 학생과_강좌로_수강신청이_존재하는지_확인할_수_있다() {
        // Given
        Registration registration = new Registration(null, 1L, 1L, LocalDateTime.now());
        registrationRepository.save(registration);

        // When
        boolean exists = registrationRepository.existsByStudentIdAndLectureId(1L, 1L);

        // Then
        Assertions.assertTrue(exists);
    }

    @Test
    void 강좌의_수강신청_학생수를_조회할_수_있다() {
        // Given
        Registration registration1 = new Registration(null, 1L, 1L, LocalDateTime.now());
        Registration registration2 = new Registration(null, 2L, 1L, LocalDateTime.now());
        registrationRepository.save(registration1);
        registrationRepository.save(registration2);

        // When
        Integer count = registrationRepository.countByLectureId(1L);

        // Then
        Assertions.assertEquals(2, count);
    }

    @Test
    void 학생의_수강신청_내역을_시간순으로_조회할_수_있다() {
        // Given
        Registration registration1 = new Registration(null, 1L, 1L, LocalDateTime.now().minusDays(1));
        Registration registration2 = new Registration(null, 1L, 2L, LocalDateTime.now());
        registrationRepository.save(registration1);
        registrationRepository.save(registration2);

        // When
        List<Registration> registrations = registrationRepository.findAllByStudentIdOrderByRegistrationDtm(1L);

        // Then
        Assertions.assertEquals(2, registrations.size());
        Assertions.assertEquals(1L, registrations.get(0).getLectureId());
        Assertions.assertEquals(2L, registrations.get(1).getLectureId());
    }

    @Test
    void 학생의_수강신청과_강좌정보를_조회할_수_있다() {
        // Given
        Registration registration = new Registration(null, 1L, 1L, LocalDateTime.now());
        Lecture lecture = new Lecture(null, "테스트 강좌", "테스트 강사", LocalDate.now(), "10:00", 30, null);
        registrationRepository.save(registration);
        lectureRepository.save(lecture);

        // When
        List<Object[]> results = registrationRepository.findRegistrationsWithLectureByStudentId(1L);

        // Then
        Assertions.assertFalse(results.isEmpty());
        Object[] result = results.get(0);
        Registration foundRegistration = (Registration) result[0];
        Lecture foundLecture = (Lecture) result[1];

        Assertions.assertEquals(1L, foundRegistration.getLectureId());
        Assertions.assertEquals("테스트 강좌", foundLecture.getLectureTitle());
    }

    @Test
    void 수강신청을_저장할_수_있다() {
        // Given
        Registration registration = new Registration(null, 1L, 1L, LocalDateTime.now());

        // When
        Registration savedRegistration = registrationRepository.save(registration);

        // Then
        Assertions.assertNotNull(savedRegistration.getRegistrationSeq());
        Assertions.assertEquals(1L, savedRegistration.getStudentId());
        Assertions.assertEquals(1L, savedRegistration.getLectureId());
    }
}

