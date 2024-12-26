package com.registration.lecture.hhpluslectureregistration.interfaces.controller;

import com.registration.lecture.hhpluslectureregistration.application.dto.RegistrationDTO;
import com.registration.lecture.hhpluslectureregistration.application.facade.RegistrationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationFacade registrationFacade;

    // 특강 신청
    @PostMapping("/{studentId}/{lectureId}")
    public ResponseEntity<RegistrationDTO> registerLecture(@PathVariable Long studentId,
                                                           @PathVariable Long lectureId) throws Exception {
        RegistrationDTO registrationDto = registrationFacade.registerLecture(studentId, lectureId);
        return ResponseEntity.ok(registrationDto);
    }

    // 신청 목록 조회
    @GetMapping("/{studentId}")
    public ResponseEntity<List<RegistrationDTO>> registerLecture(@PathVariable Long studentId) {
        List<RegistrationDTO> registrationList = registrationFacade.findRegistrationByStudentId(studentId);
        return ResponseEntity.ok(registrationList);
    }
}