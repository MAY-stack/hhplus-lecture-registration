package com.registration.lecture.hhpluslectureregistration.domain.Repository;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository {
    // 학생 조회
    Optional<Student> findById(Long studentId);

    // 학생 저장
    Student save(Student student);

}
