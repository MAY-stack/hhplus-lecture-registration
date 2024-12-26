package com.registration.lecture.hhpluslectureregistration.infrastructure;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl.LectureRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Import(LectureRepositoryImpl.class)
class LectureRepositoryTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Test
    void 강좌를_저장하고_아이디로_조회할_수_있다() {
        // Given
        Lecture lecture = new Lecture(null, "테스트 강좌", "테스트 강사",
                LocalDate.now(), "10:00", 30, null);

        // When
        Lecture savedLecture = lectureRepository.save(lecture);
        Optional<Lecture> foundLecture = lectureRepository.findById(savedLecture.getLectureId());

        // Then
        Assertions.assertTrue(foundLecture.isPresent());
        Assertions.assertEquals("테스트 강좌", foundLecture.get().getLectureTitle());
    }

    @Test
    void 날짜별로_강좌를_시간순으로_조회할_수_있다() {
        // Given
        LocalDate date = LocalDate.now();
        Lecture lecture1 = new Lecture(null, "오전 강좌", "강사 A", date, "09:00", 30, null);
        Lecture lecture2 = new Lecture(null, "오후 강좌", "강사 B", date, "14:00", 20, null);
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);

        // When
        List<Lecture> lectures = lectureRepository.findAllByLectureDateOrderByLectureTimeAsc(date);

        // Then
        Assertions.assertEquals(2, lectures.size());
        Assertions.assertEquals("오전 강좌", lectures.get(0).getLectureTitle());
        Assertions.assertEquals("오후 강좌", lectures.get(1).getLectureTitle());
    }

    @Test
    void 락을_걸고_강좌를_조회할_수_있다() {
        // Given
        Lecture lecture = new Lecture(null, "락이 걸린 강좌", "강사 C",
                LocalDate.now(), "10:00", 50, null);
        Lecture savedLecture = lectureRepository.save(lecture);

        // When
        Optional<Lecture> lockedLecture = lectureRepository.findByIdWithLock(savedLecture.getLectureId());

        // Then
        Assertions.assertTrue(lockedLecture.isPresent());
        Assertions.assertEquals("락이 걸린 강좌", lockedLecture.get().getLectureTitle());
    }

    @Test
    void 여러_강좌를_저장하고_모두_조회할_수_있다() {
        // Given
        Lecture lecture1 = new Lecture(null, "강좌 1", "강사 1", LocalDate.now(), "10:00", 30, null);
        Lecture lecture2 = new Lecture(null, "강좌 2", "강사 2", LocalDate.now(), "14:00", 25, null);
        List<Lecture> lectures = List.of(lecture1, lecture2);

        // When
        List<Lecture> savedLectures = lectureRepository.saveAll(lectures);

        // Then
        Assertions.assertEquals(2, savedLectures.size());
        Assertions.assertNotNull(savedLectures.get(0).getLectureId());
        Assertions.assertNotNull(savedLectures.get(1).getLectureId());
    }
}
