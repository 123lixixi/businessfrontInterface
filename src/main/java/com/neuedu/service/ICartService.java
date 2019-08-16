package com.neuedu.service;

import com.neuedu.common.ServerResponse;

import javax.servlet.http.HttpSession;

public interface ICartService {
    public ServerResponse add(Integer userId,Integer productId, Integer count);
}
