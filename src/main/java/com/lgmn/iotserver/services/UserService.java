package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.lgmn.common.result.Result;
import com.lgmn.common.result.ResultEnum;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.iotserver.dto.LoginDto;
import com.lgmn.umaservices.basic.dto.ViewLabelRecordDto;
import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import com.lgmn.umaservices.basic.service.ViewLabelRecordService;
import com.lgmn.userservices.basic.dto.LgmnUserDto;
import com.lgmn.userservices.basic.entity.LgmnUserEntity;
import com.lgmn.userservices.basic.service.LgmnUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class UserService extends LgmnAbstractApiService<LgmnUserEntity, LgmnUserDto, String, LgmnUserService> {

    @Reference(version = "${demo.service.version}")
    LgmnUserService lgmnUserService;

    @Override
    public void initService() {
        setService(lgmnUserService);
    }

    @Autowired
    UserService userService;

    @Value("${lgmn.token-url}")
    String tokenUrl;

    @Value("${lgmn.exitLogin-url}")
    String exitLoginUrl;

    public Result login (LoginDto loginDto) throws Exception {
        JSONObject responseResult = restTemplateLogin(loginDto);
        if (responseResult.containsKey("error_description")) {
            return Result.error(ResultEnum.PASS_ERROR);
        }
        LgmnUserDto findUserDto = new LgmnUserDto();
        findUserDto.setAccount(loginDto.getPhone());
        LgmnUserEntity lgmnUserEntity=lgmnUserService.getListByDto(findUserDto).get(0);
        return Result.success(lgmnUserEntity);
    }

    public JSONObject restTemplateLogin (LoginDto loginDto) {
        // 不能用Map,
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("grant_type", "password");
        postParameters.add("username", loginDto.getPhone());
        postParameters.add("password", loginDto.getPassword());

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("android", "android", StandardCharsets.UTF_8);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        return restTemplate.exchange(tokenUrl, HttpMethod.POST, httpEntity, JSONObject.class).getBody();
    }

    public RestTemplate getRestTemplate () {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401 && response.getRawStatusCode() != 402 && response.getRawStatusCode() != 403 && response.getRawStatusCode() != 405 && response.getRawStatusCode() != 500) {
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }
}