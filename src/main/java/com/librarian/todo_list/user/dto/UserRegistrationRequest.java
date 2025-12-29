package com.librarian.todo_list.user.dto;

import com.librarian.todo_list.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 3, max = 50, message = "닉네임은 3자 이상 50자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "닉네임은 영문, 숫자, 언더스코어만 사용 가능합니다")
    private String nickname;

    @NotBlank(message = "유저명은 필수입니다")
    @Size(min = 3, max = 50, message = "유저명은 3자 이상 50자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "유저명은 영문, 한글만 사용 가능합니다")
    private String name;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    @Pattern(regexp =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$",
             message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;
    
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3,4})\\d{4}$|^$", message = "올바른 전화번호 형식이 아닙니다 (예: 01012345678)")
    private String phoneNumber;

    private User.UserRole role;
}