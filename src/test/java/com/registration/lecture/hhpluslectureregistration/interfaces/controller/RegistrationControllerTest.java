package com.registration.lecture.hhpluslectureregistration.interfaces.controller;

import com.registration.lecture.hhpluslectureregistration.domain.Service.LectureService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.RegistrationService;
import com.registration.lecture.hhpluslectureregistration.domain.Service.StudentService;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LectureService lectureService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RegistrationService registrationService;

    private Lecture testLecture;

    @BeforeEach
    void setUp() {
        // 테스트 강좌 생성 (정원 30명)
        testLecture = lectureService.createLecture("테스트 강좌", "테스트 강사",
                LocalDate.now(), "10:00", 30);
    }

    @Test
    void 동시에_40명이_동일한_강좌에_수강신청시_30명만_성공한다() throws Exception {
        // 테스트 학생 40명 생성
        List<Student> testStudents = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            Student student = studentService.createStudent("학생 " + i);
            testStudents.add(student);
        }

        // 동기화를 위한 CountDownLatch 설정
        CountDownLatch readyLatch = new CountDownLatch(40);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(40);

        ExecutorService executorService = Executors.newFixedThreadPool(40);
        List<String> failedRequests = Collections.synchronizedList(new ArrayList<>());

        for (Student student : testStudents) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown(); // 준비 완료 표시
                    startLatch.await();    // 시작 신호 대기

                    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/registrations/{studentId}/{lectureId}",
                                            student.getStudentId(), testLecture.getLectureId())
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andReturn();

                    int status = result.getResponse().getStatus();
                    String responseBody = result.getResponse().getContentAsString();

                    if (status != 200) { // 실패한 요청 기록
                        failedRequests.add("학생 ID: " + student.getStudentId() + " " + status + " " + responseBody);
                    }
                } catch (Exception e) {
                    failedRequests.add("학생 ID: " + student.getStudentId() + " " + e.getMessage());
                } finally {
                    doneLatch.countDown(); // 작업 완료 표시
                }
            });
        }

        // 모든 스레드가 준비될 때까지 대기
        readyLatch.await();
        startLatch.countDown(); // 모든 스레드 시작
        doneLatch.await();      // 모든 작업 완료 대기

        executorService.shutdown();

        // 결과 검증
        int registeredCount = registrationService.getRegisteredStudentsCount(testLecture.getLectureId());
        assertEquals(30, registeredCount, "등록된 학생 수는 30명이어야 합니다.");
        assertEquals(10, failedRequests.size(), "실패한 요청 수는 10이어야 합니다.");

        // 실패한 요청 로그 출력
        failedRequests.forEach(logger::info);
    }

    @Test
    void 한사람이_동시에_같은_강좌에_5번_수강신청시_1번만_성공한다() throws Exception {
        // 테스트 학생 생성
        Student testStudent = studentService.createStudent("테스트 학생");

        // 동기화를 위한 CountDownLatch 설정
        CountDownLatch readyLatch = new CountDownLatch(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(5);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<String> failedRequests = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown(); // 준비 완료 표시
                    startLatch.await();    // 시작 신호 대기

                    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/registrations/{studentId}/{lectureId}",
                                            testStudent.getStudentId(), testLecture.getLectureId())
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andReturn();

                    int status = result.getResponse().getStatus();
                    String responseBody = result.getResponse().getContentAsString();

                    if (status != 200) { // 실패한 요청 기록
                        failedRequests.add("응답 상태: " + status + " - 본문: " + responseBody);
                    }
                } catch (Exception e) {
                    failedRequests.add("에러: " + e.getMessage());
                } finally {
                    doneLatch.countDown(); // 작업 완료 표시
                }
            });
        }

        // 모든 스레드가 준비될 때까지 대기
        readyLatch.await();
        startLatch.countDown(); // 모든 스레드 시작
        doneLatch.await();      // 모든 작업 완료 대기

        executorService.shutdown();

        // 결과 검증
        int registeredCount = registrationService.getRegisteredStudentsCount(testLecture.getLectureId());
        assertEquals(1, registeredCount, "등록된 학생 수는 1명이어야 합니다.");
        assertEquals(4, failedRequests.size(), "실패한 요청 수는 4건이어야 합니다.");

        // 실패한 요청 로그 출력
        failedRequests.forEach(logger::info);
    }
}
