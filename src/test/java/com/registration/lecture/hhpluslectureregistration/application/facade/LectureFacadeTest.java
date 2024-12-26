package com.registration.lecture.hhpluslectureregistration.application.facade;

import com.registration.lecture.hhpluslectureregistration.application.dto.LectureDTO;
import com.registration.lecture.hhpluslectureregistration.application.mapper.LectureMapper;
import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LectureFacadeTest {

    @Mock
    private LectureService lectureService;

    @Mock
    private LectureMapper lectureMapper;

    @InjectMocks
    private LectureFacade lectureFacade;

    @Test
    void 날짜별_수강_가능_강좌를_조회하면_결과를_반환한다() {
        // Given
        LocalDate date = LocalDate.now();

        Lecture lecture1 = new Lecture(1L, "Lecture 1", "Instructor A", date, "10:00", 30, null);
        Lecture lecture2 = new Lecture(2L, "Lecture 2", "Instructor B", date, "14:00", 25, null);

        LectureDTO lectureDTO1 = LectureDTO.builder()
                .lectureId(1L)
                .lectureTitle("Lecture 1")
                .instructorName("Instructor A")
                .lectureDate(date)
                .lectureTime("10:00")
                .availableSeats(10)
                .capacity(30)
                .build();

        LectureDTO lectureDTO2 = LectureDTO.builder()
                .lectureId(2L)
                .lectureTitle("Lecture 2")
                .instructorName("Instructor B")
                .lectureDate(date)
                .lectureTime("14:00")
                .availableSeats(5)
                .capacity(25)
                .build();

        Mockito.when(lectureService.findAllByLectureDateOrderByLectureTimeAsc(date))
                .thenReturn(List.of(lecture1, lecture2));
        Mockito.when(lectureService.getAvailableSeats(lecture1)).thenReturn(10);
        Mockito.when(lectureService.getAvailableSeats(lecture2)).thenReturn(5);
        Mockito.when(lectureMapper.entityToDto(lecture1, 10)).thenReturn(lectureDTO1);
        Mockito.when(lectureMapper.entityToDto(lecture2, 5)).thenReturn(lectureDTO2);

        // When
        List<LectureDTO> result = lectureFacade.findAvailableLecturesByDate(date);

        // Then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(lectureDTO1, result.get(0));
        Assertions.assertEquals(lectureDTO2, result.get(1));

        Mockito.verify(lectureService).findAllByLectureDateOrderByLectureTimeAsc(date);
        Mockito.verify(lectureService).getAvailableSeats(lecture1);
        Mockito.verify(lectureService).getAvailableSeats(lecture2);
        Mockito.verify(lectureMapper).entityToDto(lecture1, 10);
        Mockito.verify(lectureMapper).entityToDto(lecture2, 5);
    }

    @Test
    void 잔여석이_없는_강좌는_제외된다() {
        // Given
        LocalDate date = LocalDate.now();

        Lecture lecture1 = new Lecture(1L, "Lecture 1", "Instructor A", date, "10:00", 30, null);
        Lecture lecture2 = new Lecture(2L, "Lecture 2", "Instructor B", date, "14:00", 25, null);

        LectureDTO lectureDTO1 = LectureDTO.builder()
                .lectureId(1L)
                .lectureTitle("Lecture 1")
                .instructorName("Instructor A")
                .lectureDate(date)
                .lectureTime("10:00")
                .availableSeats(10)
                .capacity(30)
                .build();

        Mockito.when(lectureService.findAllByLectureDateOrderByLectureTimeAsc(date))
                .thenReturn(List.of(lecture1, lecture2));
        Mockito.when(lectureService.getAvailableSeats(lecture1)).thenReturn(10);
        Mockito.when(lectureService.getAvailableSeats(lecture2)).thenReturn(0);
        Mockito.when(lectureMapper.entityToDto(lecture1, 10)).thenReturn(lectureDTO1);

        // When
        List<LectureDTO> result = lectureFacade.findAvailableLecturesByDate(date);

        // Then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(lectureDTO1, result.get(0));

        Mockito.verify(lectureService).findAllByLectureDateOrderByLectureTimeAsc(date);
        Mockito.verify(lectureService).getAvailableSeats(lecture1);
        Mockito.verify(lectureService).getAvailableSeats(lecture2);
        Mockito.verify(lectureMapper).entityToDto(lecture1, 10);
    }
}