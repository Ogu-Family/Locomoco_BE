package org.prgms.locomocoserver.user.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
}
