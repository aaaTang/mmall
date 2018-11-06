package com.mmall.service;

import com.mmall.common.ServerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IUnionPayService {

    void pay(HttpServletRequest request, HttpServletResponse response,Long orderNo, Integer userId) throws IOException;

    void payTest(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void successRedict(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void frontRcvResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    void backRcvResponse(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
