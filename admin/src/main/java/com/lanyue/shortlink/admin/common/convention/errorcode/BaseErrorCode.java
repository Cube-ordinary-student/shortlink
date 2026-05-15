package com.lanyue.shortlink.admin.common.convention.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    CLIENT_ERROR("A000001", "客户端请求错误"),

    USER_TOKEN_VERIFICATION_FAILED("A000200", "用户Token验证失败"),

    USERNAME_VERIFICATION_FAILED("A000201", "用户名验证失败"),

    USER_PASSWORD_VERIFICATION_FAILED("A000202", "密码验证失败"),

    USER_NOT_EXIST("A000203", "用户不存在"),

    USER_NAME_EXIST("A000204", "用户名已存在"),

    USER_NOT_LOGIN("A000205", "用户未登录"),

    USER_ALREADY_LOGIN("A000206", "用户已登录"),

    USER_GROUP_COUNT_MAX("A000207", "分组数量已达上限"),

    USER_GROUP_NAME_EXIST("A000208", "分组名称已存在"),

    SHORT_LINK_NOT_EXIST("A000300", "短链接不存在"),

    SHORT_LINK_EXIST("A000301", "短链接已存在"),

    SHORT_LINK_URL_ERROR("A000302", "短链接跳转URL不合法"),

    FLOW_LIMIT_ERROR("A000400", "当前访问人数过多，请稍后再试"),

    SERVICE_ERROR("B000001", "服务器执行异常"),

    REMOTE_ERROR("C000001", "远程调用第三方出错"),

    REMOTE_TIMEOUT_ERROR("C000100", "第三方系统超时"),

    REMOTE_RESPONSE_NULL_ERROR("C000101", "第三方系统无响应");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
