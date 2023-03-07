package dev.board.boardprojectproto.service

import dev.board.boardprojectproto.auth.OAuth2UserInfo
import dev.board.boardprojectproto.auth.OAuth2UserInfoFactory
import dev.board.boardprojectproto.auth.ProviderType
import dev.board.boardprojectproto.auth.UserPrincipal
import dev.board.boardprojectproto.common.exception.OAuthProviderMissMatchException
import dev.board.boardprojectproto.model.User
import dev.board.boardprojectproto.repository.UserRepository
import io.sentry.Sentry
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    // private val githubClient: GithubClient,
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val user = super.loadUser(userRequest)

        return runCatching {
            process(userRequest!!, user)
        }.onFailure {
            Sentry.captureException(it)
            if (it is OAuthProviderMissMatchException) {
                throw it
            }
            // TODO: 에러 다시 고치기
            throw IllegalArgumentException("auth 실패")
            // throw InternalServiceException(ErrorCode.SERVER_ERROR, it.message.toString())
        }.getOrThrow()
    }

    private fun process(userRequest: OAuth2UserRequest, user: OAuth2User): OAuth2User {
        val providerType =
            ProviderType.valueOf(userRequest.clientRegistration.registrationId.uppercase(Locale.getDefault()))

        val accessToken = userRequest.accessToken.tokenValue

        val attributes = user.attributes.toMutableMap()

        return UserPrincipal.create(getOrCreateUser(providerType, attributes), attributes)
    }

    private fun getOrCreateUser(
        providerType: ProviderType,
        attributes: MutableMap<String, Any>,
    ): User {
        val userInfo = OAuth2UserInfoFactory.getOauth2UserInfo(providerType, attributes)

        val savedUser = userRepository.findByEmail(userInfo.getEmail())
            ?: userRepository.findUserByProviderId(userInfo.getId())

        return savedUser ?: createUser(userInfo, providerType)
    }

    private fun createUser(userInfo: OAuth2UserInfo, providerType: ProviderType): User {
        val user = User(
            email = userInfo.getEmail(),
            username = userInfo.getName(),
            providerType = providerType,
            // profileImageUrl = userInfo.getImageUrl(),
            providerId = userInfo.getId(),
        )
        println("user : $user")
        println("user info: " + userInfo.getId())
        println("user name: " + userInfo.getName())
        println("provider type: $providerType")
        println("user email: " + userInfo.getEmail())

        return userRepository.saveAndFlush(user)
    }
}