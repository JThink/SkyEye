package com.jthink.skyeye.web.message;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 状态码
 * @date 2016-10-09 09:15:34
 */
public enum StatusCode {

    // success
    SUCCESS("00000", "提交成功"),
    NO_RESPONSE("00001", "没有查询到结果"),
    // fail
//    SIGN_ERROR("10101", "签名错误"),
//    BAD_REQUEST("10102", "API地址不存在"),
//    ARGS_ERROR("10103", "缺少必选请求参数或格式错误"),
//    ACCOUNT_INVALID_NOT_EXISTS("10104", "账号不存在"),
//
//    TASK_NOT_EXISTS("10201", "任务ID不存在"),
//    TASK_NO_ACCESS("10202", "没有此任务访问权限"),
//    TASK_NOT_ALLOW_UPDATE("10203", "任务无法更新"),
//    TASK_NOT_ALLOW_OPT("10204", "任务无法操作"),
//    TASK_NOT_ALLOW_OPT_FAILED("10205", "任务操作失败"),
//    TASK_NOT_ALLOW_DELETE("10206", "任务无法删除"),
//    TASK_TYPE_INVALID("10207", "任务类型不正确"),
//    TASK_QUERY_NUM_INVALID("10208", "任务查询个数超限"),
//
//    TAG_UID_NOT_EXISTS("10301", "标签值标识不存在"),
//    TAG_VALUE_NOT_EXISTS("10302", "标签值格式错误"),
//    CONDITION_INVALID("10303", "标签规则不符合要求"),
//    TAG_NO_ACCESS("10304", "没有此标签的访问权限"),
//
//    UPLOAD_SUCCESS("10400", "文件上传任务生成成功"),
//    FILE_SIZE_LIMIT("10401", "上传文件过大"),
//    UPLOAD_FILE_FAIL("10402", "文件上传失败"),
//    FILE_TYPE_ERROR("10403", "非法文件类型"),
//    UPLOAD_MATCH_FAIL("10404", "注册的标签和人群上传的文件表头不一致"),
//
//    ACCOUNT_INTERFACE_NOACCESS("20201", "没有此接口访问权限"),
    SQL_ERROR("10101", "sql错误"),
    CONDITION_ERROR("10102", "查询条件错误"),
    SYSTEM_ERROR("20101", "系统异常");


    private String code;
    private String msg;

    private StatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
