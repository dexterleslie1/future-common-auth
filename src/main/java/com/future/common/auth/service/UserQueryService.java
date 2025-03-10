package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.common.auth.dto.UserDTO;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class UserQueryService extends ServiceImpl<UserMapper, User> {

    /**
     * 根据id查询个人资料
     *
     * @param id
     * @return
     * @throws BusinessException
     */
    public UserDTO get(Long id) throws BusinessException {
        Assert.isTrue(id != null && id > 0, "请指定用户id");
        User user = this.getById(id);
        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("查询的用户id {} 不存在", id);
            }

            throw new BusinessException("用户不存在！");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    /**
     * 在注册时，用于设置帐号是否已经存在 UI 提示标记
     *
     * @param loginName 帐号
     * @return 帐号存在返回 true，否则返回 false
     */
    public boolean checkIfLoginNameExists(String loginName) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getLoginName, loginName);
        User user = this.getOne(queryWrapper, true);
        return user != null;
    }
}
