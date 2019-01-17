package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Service(interfaceClass = UserAPI.class)
public class UserServiceImpl implements UserAPI{

    @Autowired
    MoocUserTMapper moocUserTMapper;

    @Override
    public int login(String username, String password) {
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);

        MoocUserT result = moocUserTMapper.selectOne(moocUserT);
        if(result != null && result.getUuid() > 0){
            System.out.println(MD5Util.encrypt(password));
            if(result.getUserPwd().equals(MD5Util.encrypt(password))){
                return result.getUuid();
            }
        }
        return 0;
    }

    @Override
    public boolean register(UserModel userModel) {
        //注册实体转数据实体
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
        moocUserT.setUserPwd(MD5Util.encrypt(userModel.getPassword()));
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setAddress(userModel.getAddress());
        moocUserT.setUserPhone(moocUserT.getUserPhone());

        Integer row = moocUserTMapper.insert(moocUserT);
        if(row > 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean checkUsername(String username) {
        Integer count = moocUserTMapper.selectCount(new EntityWrapper<MoocUserT>().eq("user_name", username));
        if(count != null && count > 0){
            return false;
        }else {
            return true;
        }
    }

    private UserInfoModel do2UserInfo(MoocUserT moocUserT){
        UserInfoModel userInfoModel = new UserInfoModel();

        userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(moocUserT.getLifeState() + "");
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setAddress(moocUserT.getAddress());
        userInfoModel.setCreateTime(moocUserT.getBeginTime().getTime());
        userInfoModel.setBiography(moocUserT.getBiography());

        return userInfoModel;
    }
    @Override
    public UserInfoModel getUserInfo(int uuid) {
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        return do2UserInfo(moocUserT);
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoModel.getUuid());
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));
        moocUserT.setBiography(userInfoModel.getBiography());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        //moocUserT.setBeginTime(new Date(userInfoModel.getCreateTime()));
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setAddress(userInfoModel.getAddress());
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setUserPhone(userInfoModel.getPhone());
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUpdateTime(new Date(System.currentTimeMillis()));

        Integer result = moocUserTMapper.updateById(moocUserT);
        if(result != null && result > 0){
            UserInfoModel userInfo = getUserInfo(moocUserT.getUuid());
            return userInfo;
        }else {
            return userInfoModel;
        }
    }
}
