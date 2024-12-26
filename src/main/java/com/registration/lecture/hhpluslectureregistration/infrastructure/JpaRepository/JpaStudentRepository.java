package com.registration.lecture.hhpluslectureregistration.infrastructure.JpaRepository;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaStudentRepository extends JpaRepository<Student, Long> {
}
