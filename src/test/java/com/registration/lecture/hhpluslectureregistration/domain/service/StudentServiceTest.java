package com.registration.lecture.hhpluslectureregistration.domain.service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.StudentRepository;
import com.registration.lecture.hhpluslectureregistration.domain.Service.StudentService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void 학생을_생성하면_저장된_학생이_반환된다() {
        // Given
        String studentName = "테스트 학생";
        Student mockStudent = Student.builder()
                .studentId(1L)
                .studentName(studentName)
                .build();

        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(mockStudent);

        // When
        Student savedStudent = studentService.createStudent(studentName);

        // Then
        Assertions.assertNotNull(savedStudent);
        Assertions.assertEquals(studentName, savedStudent.getStudentName());
        Mockito.verify(studentRepository, Mockito.times(1)).save(Mockito.any(Student.class));
    }

    @Test
    void 학생이_존재하면_학생이_반환된다() {
        // Given
        Long studentId = 1L;
        Student mockStudent = Student.builder()
                .studentId(studentId)
                .studentName("테스트 학생")
                .build();

        Mockito.when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));

        // When
        Student foundStudent = studentService.validateStudentExists(studentId);

        // Then
        Assertions.assertNotNull(foundStudent);
        Assertions.assertEquals(studentId, foundStudent.getStudentId());
        Mockito.verify(studentRepository, Mockito.times(1)).findById(studentId);
    }

    @Test
    void 학생이_존재하지_않으면_예외를_던진다() {
        // Given
        Long studentId = 1L;

        Mockito.when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.validateStudentExists(studentId));

        Assertions.assertEquals("해당 학생이 존재하지 않습니다.", exception.getMessage());
        Mockito.verify(studentRepository, Mockito.times(1)).findById(studentId);
    }
}
