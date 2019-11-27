package com.fgq.demo.wx.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package: com.fgq.demo.wx.dto
 * @ClassName: AccessTokenDTO
 * @Author: fgq
 * @Description: 微信accessTokenDTO  {"access_token":"ACCESS_TOKEN","expires_in":7200}  预留暂时未使用
 * @Date: 2019/11/27 15:55
 */
@Data
public class AccessTokenDTO implements Serializable {
    private static final long serialVersionUID = 6610575413880464062L;

    /**
     * access_token
     */
    private String accessToken;

    /**
     * 有效期
     */
    private Integer expriseIn;

}
