package dev.board.boardprojectproto.common.exception

import dev.board.boardprojectproto.common.enums.ErrorCode

class UnAuthorizedException(
    errorCode: ErrorCode,
    log: String,
) : BizException(errorCode, log)

