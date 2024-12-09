package com.ureca.picky_be.base.business.user;

import com.ureca.picky_be.base.business.user.dto.GetNicknameValidationResp;
import com.ureca.picky_be.base.business.user.dto.GetUserResp;
import com.ureca.picky_be.base.business.user.dto.RegisterUserReq;
import com.ureca.picky_be.global.success.SuccessCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserUseCase {
    SuccessCode registerUserInfo(RegisterUserReq req);
    SuccessCode registerProfile(MultipartFile profile) throws IOException;
    GetUserResp getUserInfo();
    GetNicknameValidationResp getNicknameValidation(String nickname);
}
