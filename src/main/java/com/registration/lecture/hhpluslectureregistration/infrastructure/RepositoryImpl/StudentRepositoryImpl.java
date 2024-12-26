package com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.StudentRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import com.registration.lecture.hhpluslectureregistration.infrastructure.JpaRepository.JpaStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final JpaStudentRepository jpaStudentRepository;

    // 학생 조회
    @Override
    public Optional<Student> findById(Long studentId) {
        return jpaStudentRepository.findById(studentId);
    }

    // 학생 저장
    @Override
    public Student save(Student student) {
        return jpaStudentRepository.save(student);
    }

}
