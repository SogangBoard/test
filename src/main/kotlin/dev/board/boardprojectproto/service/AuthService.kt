package dev.board.boardprojectproto.service

import dev.board.boardprojectproto.dto.UserInfoDto
import dev.board.boardprojectproto.dto.UserLoginRequestDto
import dev.board.boardprojectproto.dto.UserSignUpDto
import java.util.UUID

interface AuthService {
    // TODO: 회원가입
    fun saveUser(userSingUpDto: UserSignUpDto): UUID

    // TODO: 로그인
    fun loginUser(userLoginRequestDto: UserLoginRequestDto): UserInfoDto

    // TODO: 유저토큰 재생산

    // TODO: 이메일을 가진 유저가 존재하는지 확인하기

    // TODO: 비밀번호 변경
}
