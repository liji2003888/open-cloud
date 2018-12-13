package com.github.lyd.common.http;

import com.github.lyd.common.autoconfigure.GatewayProperties;
import com.github.lyd.common.model.ResultBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 平台Oauth2请求工具类
 *
 * @author: liuyadu
 * @date: 2018/12/11 15:51
 * @description:
 */
@Slf4j
public class OpenRestTemplate extends RestTemplate {

    private GatewayProperties gatewayProperties;

    public OpenRestTemplate(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 构建网关Oauth2 client_credentials方式请求
     *
     * @return
     */
    public OAuth2RestTemplate buildOauth2ClientRequest() {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(gatewayProperties.getClientId());
        resource.setClientSecret(gatewayProperties.getClientSecret());
        resource.setAccessTokenUri(gatewayProperties.getAuthServerAddr() + "/oauth/token");
        resource.setAuthenticationScheme(AuthenticationScheme.form);
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        return restTemplate;
    }

    /**
     * 构建网关Oauth2 password方式请求
     *
     * @return
     */
    public OAuth2RestTemplate buildOauth2PasswordRequest(String username, String password) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setClientId(gatewayProperties.getClientId());
        resource.setClientSecret(gatewayProperties.getClientSecret());
        resource.setAccessTokenUri(gatewayProperties.getAuthServerAddr().concat("/oauth/token"));
        resource.setAuthenticationScheme(AuthenticationScheme.form);
        resource.setGrantType("password");
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        return restTemplate;
    }

    /**
     * 刷新网关
     */
    public void refreshGateway() {
        try {
            Assert.notNull(this.gatewayProperties, "网关信息错误");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> formEntity = new HttpEntity<String>("", headers);
            ResultBody resultBody = buildOauth2ClientRequest().postForObject(gatewayProperties.getServerAddr().concat("/actuator/refresh-gateway"), formEntity, ResultBody.class);
            log.info("refreshGateway:{}", resultBody);
        } catch (Exception e) {
            log.error("refreshGateway error:{}", e.getMessage());
        }
    }

    public GatewayProperties getGatewayProperties() {
        return gatewayProperties;
    }

    public void setGatewayProperties(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }
}