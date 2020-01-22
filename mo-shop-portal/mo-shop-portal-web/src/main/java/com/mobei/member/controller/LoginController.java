package com.mobei.member.controller;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.member.controller.req.vo.LoginVo;
import com.mobei.member.feign.MemberLoginServiceFeign;
import com.mobei.member.input.dto.UserLoginInputDTO;
import com.mobei.web.base.BaseWebController;
import com.mobei.web.bean.MyBeanUtil;
import com.mobei.web.constants.WebConstants;
import com.mobei.web.utils.CookieUtils;
import com.mobei.web.utils.RandomValidateCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录请求
 */
@Controller
public class LoginController extends BaseWebController {

    //跳转到登录界面
    public static final String MB_LOGIN_FTL = "member/login";

    @Autowired
    private MemberLoginServiceFeign memberLoginServiceFeign;

    public static final String REDIRECT_INDEX = "redirect:/";

    /**
     * 跳转页面
     * @return
     */
    @GetMapping("/login")
    public String getLogin() {
        return MB_LOGIN_FTL;
    }

    /**
     * 接收请求参数
     * @return
     */
    @PostMapping("/login")
    public String postLogin(@ModelAttribute("loginVo") LoginVo loginVo, Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        // 1.图形验证码判断
        String graphicCode = loginVo.getGraphicCode();
        if (!RandomValidateCodeUtil.checkVerify(graphicCode, session)) {
            setErrorMsg(model, "图形验证码不正确!");
            return MB_LOGIN_FTL;
        }

        // 2.将vo转换为dto
        UserLoginInputDTO voToDto = MyBeanUtil.voToDto(loginVo, UserLoginInputDTO.class);
        voToDto.setLoginType(Constants.MEMBER_LOGIN_TYPE_PC);
        String info = webBrowserInfo(request);
        voToDto.setDeviceInfor(info);
        BaseResponse<JSONObject> login = memberLoginServiceFeign.login(voToDto);
        if (!isSuccess(login)) {
            setErrorMsg(model, login.getMsg());
            return MB_LOGIN_FTL;
        }

        // 3.将token存入到cookie中
        JSONObject data = login.getData();
        String token = data.getString("token");
        CookieUtils.setCookie(request, response, WebConstants.LOGIN_TOKEN_COOKIENAME, token);

        // 登陆成功后，跳转到首页
        return REDIRECT_INDEX;
    }

}
