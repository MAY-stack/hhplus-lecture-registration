package com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.RegistrationRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Registration;
import com.registration.lecture.hhpluslectureregistration.infrastructure.JpaRepository.JpaRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RegistrationRepositoryImpl implements RegistrationRepository {

    private final JpaRegistrationRepository jpaRegistrationRepository;

    @Override
    public boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId) {
        return jpaRegistrationRepository.existsByStudentIdAndLectureId(studentId, lectureId);
    }

    @Override
    public Integer countByLectureId(Long lectureId) {
        return jpaRegistrationRepository.countByLectureId(lectureId);
    }

    @Override
    public List<Registration> findAllByStudentIdOrderByRegistrationDtm(Long studentId) {
        return jpaRegistrationRepository.findAllByStudentIdOrderByRegistrationDtm(studentId);
    }

    @Override
    public List<Object[]> findRegistrationsWithLectureByStudentId(Long studentId) {
        return jpaRegistrationRepository.findRegistrationsWithLectureByStudentId(studentId);
    }

    @Override
    public Registration save(Registration registration) {
        return jpaRegistrationRepository.save(registration);
    }
}
