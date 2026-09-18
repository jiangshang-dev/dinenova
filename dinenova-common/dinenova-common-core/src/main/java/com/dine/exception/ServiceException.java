package com.dine.exception;


import cn.hutool.core.util.StrUtil;
import com.dine.base.IBaseEnum;
import lombok.Getter;
import lombok.ToString;

/**
 * 业务异常异常类
 *
 * @author yuanjianzhong
 * @version 1.0
 */
@ToString
@Getter
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = -997101946070796354L;

    /**
     * 异常编码
     */
    private final int code;
    /**
     * 异常消息
     */
    private final String message;

    /**
     * 数据
     */
    private Object data;

    public ServiceException(Throwable cause) {
        super(cause);
        this.code = 500;
        this.message = cause.getMessage();
    }

    public ServiceException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(int code, Object data, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ServiceException(IBaseEnum<Integer> base, Object... params) {
        super(StrUtil.format(base.getDesc(), params));
        this.code = base.getCode() == null ? 500 : base.getCode();
        this.message = StrUtil.format(base.getDesc(), params);
    }

    public ServiceException(String message, Object... params) {
        super(StrUtil.format(message, params));
        this.code = 500;
        this.message = StrUtil.format(message, params);
    }
}