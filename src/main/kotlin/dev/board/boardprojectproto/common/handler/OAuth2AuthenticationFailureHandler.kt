package dev.board.boardprojectproto.common.handler

import dev.board.boardprojectproto.repository.common.OAuth2AuthorizationRequestBasedOnCookieRepository
import dev.board.boardprojectproto.repository.common.REDIRECT_URI_PARAM_COOKIE_NAME
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils.getCookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationFailureHandler(
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
) : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        var targetUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value ?: "/"

        exception.printStackTrace()

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
