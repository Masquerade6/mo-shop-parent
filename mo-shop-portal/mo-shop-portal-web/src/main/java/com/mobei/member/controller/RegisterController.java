package com.mobei.member.controller;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseResponse;
import com.mobei.member.controller.req.vo.RegisterVo;
import com.mobei.member.feign.MemberRegisterServiceFeign;
import com.mobei.member.input.dto.UserInputDTO;
import com.mobei.web.base.BaseWebController;
import com.mobei.web.bean.MyBeanUtil;
import com.mobei.web.utils.RandomValidateCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * 注册请求
 */
@Controller
public class RegisterController extends BaseWebController {

    private static final String MB_REGISTER_FTL = "member/register";

    @Autowired
    private MemberRegisterServiceFeign memberRegisterServiceFeign;

    //跳转到登录页面
    public static final String MB_LOGIN_FTL = "member/login";

    /**
     * 跳转到注册页面
     *
     * @return
     */
    @GetMapping("/register.html")
    public String getRegister() {
        return MB_REGISTER_FTL;
    }

    /**
     * 跳转到注册页面
     *
     * @param registerVo
     * @param bindingResult 一定要紧跟registerVo,中间不能有其它参数也不能放到它前面
     * @return
     * @ModelAttribute("registerVo"):将registerVo传递回页面做回显
     */
    @PostMapping("/register.html")
    public String postRegister(@ModelAttribute("registerVo") @Validated RegisterVo registerVo, BindingResult bindingResult,
                               Model model, HttpSession httpSession) {
        //1.接收表单参数(验证码) 创建对象接收参数 VO DO DTO
        if (bindingResult.hasErrors()) {//如果有错误
            //获取第一个错误
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            setErrorMsg(model, errorMsg);
            return MB_REGISTER_FTL;
        }

        //2.判断图形验证码是否正确
        String graphicCode = registerVo.getGraphicCode();
        Boolean checkVerify = RandomValidateCodeUtil.checkVerify(graphicCode, httpSession);
        if (!checkVerify) {
            setErrorMsg(model, "图形验证码不正确!");
            return MB_REGISTER_FTL;
        }

        //3.调用会员服务接口实现注册 将前端提交的VO转换成DTO
        UserInputDTO userInputDTO = MyBeanUtil.voToDto(registerVo, UserInputDTO.class);

        BaseResponse<JSONObject> register = memberRegisterServiceFeign.register(userInputDTO, registerVo.getRegistCode());

        if (!isSuccess(register)) {
            setErrorMsg(model, register.getMsg());
            return MB_REGISTER_FTL;
        }

        //4.跳转到登陆页面
        return MB_LOGIN_FTL;
    }

}
