package com.example.servicebase.exceptionhandler;

import com.example.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
        e.printStackTrace();
        return R.error().message(StringUtils.isBlank(e.getMessage()) ? "系统出错了" : e.getMessage());
    }

    @ExceptionHandler(EduException.class)
    @ResponseBody
    public R error(EduException e){
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMsg());
    }
}
