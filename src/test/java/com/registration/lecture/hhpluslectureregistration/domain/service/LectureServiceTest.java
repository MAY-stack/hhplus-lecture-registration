package com.registration.lecture.hhpluslectureregistration.domain.service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.LectureRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void 강좌를_생성하면_저장된_강좌가_반환된다() {
        // Given
        String lectureTitle = "테스트 강좌";
        String instructorName = "테스트 강사";
        LocalDate lectureDate = LocalDate.now();
        String lectureTime = "10:00";
        Integer capacity = 30;

        Lecture mockLecture = Lecture.builder()
                .lectureTitle(lectureTitle)
                .instructorName(instructorName)
                .lectureDate(lectureDate)
                .lectureTime(lectureTime)
                .capacity(capacity)
                .build();

        Mockito.when(lectureRepository.save(Mockito.any(Lecture.class))).thenReturn(mockLecture);

        // When
        Lecture savedLecture = lectureService.createLecture(lectureTitle, instructorName, lectureDate, lectureTime, capacity);

        // Then
        Assertions.assertNotNull(savedLecture);
        Assertions.assertEquals(lectureTitle, savedLecture.getLectureTitle());
        Mockito.verify(lectureRepository, Mockito.times(1)).save(Mockito.any(Lecture.class));
    }

    @Test
    void 강좌가_존재하지_않으면_예외를_던진다() {
        // Given
        Long lectureId = 1L;

        Mockito.when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> lectureService.validateLectureExists(lectureId));

        Assertions.assertEquals("해당 강좌가 존재하지 않습니다.", exception.getMessage());
        Mockito.verify(lectureRepository, Mockito.times(1)).findById(lectureId);
    }

    @Test
    void 강좌를_락으로_조회하면_존재하는_강좌를_반환한다() {
        // Given
        Long lectureId = 1L;
        Lecture mockLecture = Lecture.builder()
                .lectureId(lectureId)
                .lectureTitle("테스트 강좌")
                .build();

        Mockito.when(lectureRepository.findByIdWithLock(lectureId)).thenReturn(Optional.of(mockLecture));

        // When
        Lecture foundLecture = lectureService.findLectureWithLock(lectureId);

        // Then
        Assertions.assertNotNull(foundLecture);
        Assertions.assertEquals(lectureId, foundLecture.getLectureId());
        Mockito.verify(lectureRepository, Mockito.times(1)).findByIdWithLock(lectureId);
    }

    @Test
    void 강좌를_락으로_조회할_때_존재하지_않으면_예외를_던진다() {
        // Given
        Long lectureId = 1L;

        Mockito.when(lectureRepository.findByIdWithLock(lectureId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> lectureService.findLectureWithLock(lectureId));

        Assertions.assertEquals("해당 강좌가 존재하지 않습니다.", exception.getMessage());
        Mockito.verify(lectureRepository, Mockito.times(1)).findByIdWithLock(lectureId);
    }

    @Test
    void 강좌의_잔여석을_계산하면_정확한_숫자가_반환된다() {
        // Given
        Long lectureId = 1L;
        Lecture mockLecture = Lecture.builder()
                .lectureId(lectureId)
                .capacity(30)
                .build();

        Mockito.when(registrationRepository.countByLectureId(lectureId)).thenReturn(10);

        // When
        int availableSeats = lectureService.getAvailableSeats(mockLecture);

        // Then
        Assertions.assertEquals(20, availableSeats);
        Mockito.verify(registrationRepository, Mockito.times(1)).countByLectureId(lectureId);
    }

    @Test
    void 날짜별_강좌를_시간순으로_조회하면_결과가_반환된다() {
        // Given
        LocalDate date = LocalDate.now();
        Lecture mockLecture1 = Lecture.builder()
                .lectureTitle("오전 강좌")
                .lectureTime("09:00")
                .build();
        Lecture mockLecture2 = Lecture.builder()
                .lectureTitle("오후 강좌")
                .lectureTime("14:00")
                .build();

        Mockito.when(lectureRepository.findAllByLectureDateOrderByLectureTimeAsc(date))
                .thenReturn(List.of(mockLecture1, mockLecture2));

        // When
        List<Lecture> lectures = lectureService.findAllByLectureDateOrderByLectureTimeAsc(date);

        // Then
        Assertions.assertEquals(2, lectures.size());
        Assertions.assertEquals("오전 강좌", lectures.get(0).getLectureTitle());
        Assertions.assertEquals("오후 강좌", lectures.get(1).getLectureTitle());
        Mockito.verify(lectureRepository, Mockito.times(1)).findAllByLectureDateOrderByLectureTimeAsc(date);
    }
}
