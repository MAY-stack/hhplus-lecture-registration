package com.registration.lecture.hhpluslectureregistration.interfaces.controller;

import com.registration.lecture.hhpluslectureregistration.application.dto.LectureDTO;
import com.registration.lecture.hhpluslectureregistration.application.facade.LectureFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureFacade lectureFacade;  // Facade 서비스

    // 특강 신청 가능 목록 조회
    @GetMapping("/{date}")
    public ResponseEntity<List<LectureDTO>> getAvailableLecturesByDate(@PathVariable String date) throws DateTimeParseException {
        LocalDate parsedDate = LocalDate.parse(date);
        List<LectureDTO> lectures = lectureFacade.findAvailableLecturesByDate(parsedDate);
        return ResponseEntity.ok(lectures);
    }
}
