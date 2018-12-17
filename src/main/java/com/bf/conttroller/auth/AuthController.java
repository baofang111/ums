package com.bf.conttroller.auth;

import com.bf.common.constant.Constant;
import com.bf.core.model.AuthRequest;
import com.bf.core.model.AuthResponse;
import com.bf.core.util.jwt.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bf on 2017/9/9.
 */
@Controller
@RequestMapping
public class AuthController {

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private StringRedisTemplate template;

    @RequestMapping(value = "aaa")
    public void authTest(){
        System.out.println("经过测试........");
    }

    public static void main(String[] args) {
		// test code
        // test git commit test modify file

    }

    /**
     *  生成 token
     * @param request
     * @return
     */
    @RequestMapping(value = "auth")
    public ResponseEntity<?> token(@RequestBody AuthRequest request){
        try {
            if (StringUtils.isEmpty(request.getClientId()) || StringUtils.isEmpty(request.getUserName()) || StringUtils.isEmpty(request.getPassword())) {
                Long clientId = template.opsForValue().increment(Constant.DEFAULT_JWT_CLIENT_ID, 1);
                // 生成 token id
                request.setClientId(clientId.toString());
            }
            String accessToken = tokenUtil.generateToken(request.getUserName());
            // token 持续时间
            return ResponseEntity.ok(new AuthResponse(accessToken, request.getClientId()));
        } catch (Exception e) {
            //TODO 带操作

            return null;
        }

    }

}
