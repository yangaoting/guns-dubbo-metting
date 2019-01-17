package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;

    @PostMapping("/register")
    public ResponseVO register(UserModel userModel){
        if(StringUtils.isEmpty(userModel.getUsername())){
            return ResponseVO.serviceFail("用户名不能为空");
        }
        if(StringUtils.isEmpty(userModel.getPassword())){
            return ResponseVO.serviceFail("密码不能为空");
        }

        boolean isSuccess = userAPI.register(userModel);
        if(isSuccess){
            return ResponseVO.success("注册成功");
        }else{
            return ResponseVO.serviceFail("注册失败");
        }
    }

    @PostMapping("/check")
    public ResponseVO check(String username){
        if(!StringUtils.isEmpty(username)){
            boolean notExists = userAPI.checkUsername(username);
            if(notExists){
                return ResponseVO.success("用户名可用");
            }else{
                return ResponseVO.serviceFail("用户名已存在");
            }
        }else{
            return ResponseVO.serviceFail("用户名不能为空");
        }
    }

    @GetMapping("/logout")
    public ResponseVO logout(){
        return ResponseVO.success("用户退出成功");
    }

    @GetMapping("/getUserInfo")
    public ResponseVO getUserInfo(){
        String uuid = CurrentUser.getCurrentUser();
        if(StringUtils.isNotEmpty(uuid)){
            UserInfoModel userInfo = userAPI.getUserInfo(Integer.parseInt(uuid));
            if(userInfo != null){
                return ResponseVO.success(userInfo);
            }else{
                return ResponseVO.serviceFail("用户信息查询失败");
            }
        }else{
            return ResponseVO.success("用户未登录");
        }
    }

    @PostMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(@ModelAttribute UserInfoModel userInfoModel){
        String uuid = CurrentUser.getCurrentUser();
        if(StringUtils.isNotEmpty(uuid)){
            if(userInfoModel.getUuid() != Integer.parseInt(uuid)){
                return ResponseVO.serviceFail("当前登陆人与修改信息不一致");
            }
            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            if(userInfo != null){
                return ResponseVO.success(userInfo);
            }else{
                return ResponseVO.serviceFail("用户信息修改失败");
            }
        }else{
            return ResponseVO.success("用户未登录");
        }
    }
}
