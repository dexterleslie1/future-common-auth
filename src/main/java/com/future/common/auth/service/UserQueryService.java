package com.future.common.auth.service;

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
}
