package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 常量
 * @date 2016-09-08 21:10:31
 */
public class Constants {

    // 标点符号
    public static final String LIKE = "like";
    public static final String PERCENT = "%";
    public static final String EQUAL = "=";
    public static final String GREATER = ">";
    public static final String GEQUAL = ">=";
    public static final String LESS = "<";
    public static final String LEQUAL = "<=";
    public static final String SLASH = "/";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String POINT = ".";
    public static final String MIDDLE_LINE = "-";
    public static final String UNDER_LINE = "_";
    public static final String LINE_FEED = "\n";
    public static final String SPACE = " ";
    public static final String SINGLE_PHE = "'";
    public static final String LEFT_MIDDLE_BRAC = "[";
    public static final String RIGHT_MIDDLE_BRAC = "]";
    public static final String COLON = ":";
    public static final String VERTICAL_LINE = "|";
    public static final String VERTICAL_LINE_SPLIT = "\\|";
    public static final String XING_HAO = "*";
    public static final String JING_HAO = "#";

    // 构造sql相关
    public static final String LEFT_S_BRACKETS = "(";
    public static final String RIGHT_S_BRACKETS = ")";
    public static final String APOSTROPHE = "'";
    public static final String QUESTION_MARK = "?";
    public static final String QUESTION_MARK_SQL = "?sql=";
    public static final String ASTERISK = "*";
    public static final String L_PERCENT = "'%";
    public static final String R_PERCENT = "%'";
    public static final String NONE = "无";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String BETWEEN = "[]";
    public static final String IN = "in";
    public static final String NOT_IN = "not in";
    public static final String LIKE_SPACE = " like ";
    public static final String NOT_LIKE = " not like ";
    public static final String IS_NULL = " is null";
    public static final String OR = "OR";
    public static final String OR_SPACE = " OR ";
    public static final String AND = "AND";
    public static final String AND_SPACE = " AND ";
    public static final String APOSTROPHE_COMMA = "','";
    public static final String COUNT_ASTERISK = "count(*) as cnt";
    public static final String WHERE = "where";
    public static final String NANO_TIME_ORDER_BY_ASC = " order by nanoTime asc";

    // annotation type相关
    public static final String CS_KEY = "cs";
    public static final String CS_VALUE = "client send";
    public static final String SR_KEY = "sr";
    public static final String SR_VALUE = "server receive";
    public static final String SS_KEY = "ss";
    public static final String SS_VALUE = "server send";
    public static final String CR_KEY = "cr";
    public static final String CR_VALUE = "client receive";

    // node property
    public static final String CLIENT_KEY = "c";
    public static final String CLIENT_VALUE = "client";
    public static final String SERVER_KEY = "s";
    public static final String SERVER_VALUE = "server";

    // rpc trace跟踪hbase表相关
    public static final String TABLE_TRACE = "trace";
    public static final String TABLE_TRACE_COLUMN_FAMILY = "span";
    public static final String TABLE_ANNOTATION = "annotation";
    public static final String TABLE_ANNOTATION_COLUMN_FAMILY = "trace";
    public static final String TABLE_TIME_CONSUME = "time_consume";
    public static final String TABLE_TIME_CONSUME_COLUMN_FAMILY = TABLE_ANNOTATION_COLUMN_FAMILY;

    // RpcInvocation attachment和捕捉annotation中的exception相关
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_ID = "parentId";
    public static final String SAMPLE = "isSample";
    public static final String EXCEPTION = "exception";
    public static final String DUBBO_EXCEPTION = "dubbo.exception";
    public static final String DUBBO_TIMEOUTEXCEPTION = "dubbo.timeoutexception";

    // hdfs相关
    public final static String PROTOCOL = "hdfs";
    public final static String FLAG = "//";
    public final static String TERMINAL = ":";

    public static final String CNT = "cnt";

    // log4j参数获取
    public static final String APP_NAME = "APP_NAME";
    public static final String HOSTNAME = "HOSTNAME";

    //  name info类型相关
    public static final String API = "api";
    public static final String ACCOUNT = "account";
    public static final String MIDDLEWARE = "middleware";
    public static final String THIRD = "third";

    // zk节点
    public static final String ROOT_PATH_EPHEMERAL = "/skyeye/monitor/scroll";
    public static final String ROOT_PATH_PERSISTENT = "/skyeye/monitor/query";
    public static final String APPENDER_INIT_DATA = "appender_init_data";

    public static final String EMPTY_STR = "";

    // mail
    public static final String MONITOR_APP_ALERT = "【app】SkyEye监控中心";
    public static final String MONITOR_MAIL_SUBJECT = "[SkyEye]-alarm app alert";
    public static final String MONITOR_MAIL_INFO_EXEC = "execute";
    public static final String MONITOR_MAIL_INFO_CALL = "call";
    public static final String MONITOR_MAIL_INFO_REQUEST = "request";

    // 微信报警
    public static final String WECHAT_ALERT_APP = "项目: ";
    public static final String WECHAT_ALERT_HOST = "主机: ";
    public static final String WECHAT_ALERT_DEPOLY = "位置: ";
    public static final String WECHAT_ALERT_TIME = "时间: ";
    public static final String WECHAT_ALERT_MSG = "详情: ";
    public static final String APP_START = "start";
    public static final String APP_STOP = "stop";
    public static final String APP_APPENDER_RESTART_KEY = "restart";
    public static final String APP_APPENDER_RESTART = "kafka appender restart";
    public static final String APP_APPENDER_STOP_KEY = "stop";
    public static final String APP_APPENDER_STOP = "kafka appender stop";
    public static final String WECHAT_ALERT_RESPONSE_EXCEED = "响应时间超过阈值";
    public static final String TIME_CONSUME_ALARM_TEMPLATE = "{uniqueName}响应时间超过阈值,{window}分钟内响应时间超过{cost}ms占比大于{threshold}%,当前占比:{total}%,总请求次数:{cnt}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_UNIQUENAME = "{uniqueName}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_WINDOW = "{window}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_COST = "{cost}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_THRESHOLD = "{threshold}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_TOTAL = "{total}";
    public static final String TIME_CONSUME_ALARM_TEMPLATE_CNT = "{cnt}";

    // 日志类型
    public static final String EVENT_TYPE_NORMAL = "normal";
    public static final String EVENT_TYPE_INVOKE_INTERFACE = "invoke_interface";
    public static final String EVENT_TYPE_MIDDLEWARE_OPT = "middleware_opt";
    public static final String EVENT_TYPE_JOB_EXECUTE = "job_execute";
    public static final String EVENT_TYPE_CUSTOM_LOG = "custom_log";
    public static final String EVENT_TYPE_RPC_TRACE = "rpc_trace";
    public static final String EVENT_TYPE_THIRDPARTY_CALL = "thirdparty_call";

    // 中间件
    public static final String MIDDLEWARE_HBASE = "hbase";
    public static final String MIDDLEWARE_MONGO = "mongo";

    // 日志采集组件运行状态
    public static final String LOG_COLLECTION_RUNNING = "running";
    public static final String LOG_COLLECTION_STOPPED = "stopped";
    public static final String LOG_COLLECTION_HISTORY = "history";

    // zk节点类型
    public static final int ZK_NODE_TYPE_EPHEMERAL = 0;
    public static final int ZK_NODE_TYPE_PERSISTENT = 1;

    // zeus前端展示相关
    public static final String PLEASE_CHOOSE = "请选择";
    public static final String ALL = "all";

    // 返回结果
    public static final String RES_CODE = "resCode";
    public static final String RES_MSG = "resMsg";
    public static final String STAT_CODE = "statCode";
    public static final String STAT_MSG = "statMsg";
    public static final String DATA = "data";
    public static final String ENCRUPTY_MD5 = "md5";

    // es字段
    public static final String DAY = "day";
    public static final String TIME = "time";
    public static final String NANOTIME = "nanoTime";
    public static final String CREATED = "created";
    public static final String APP = "app";
    public static final String HOST = "host";
    public static final String THREAD = "thread";
    public static final String LEVEL = "level";
    public static final String EVENT_TYPE = "eventType";
    public static final String PACK = "pack";
    public static final String CLAZZ = "clazz";
    public static final String LINE = "line";
    public static final String MESSAGE_SMART = "messageSmart";
    public static final String MESSAGE_MAX = "messageMax";
    public static final String WEEK = "week";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String UNIQUE_NAME = "uniqueName";
    public static final String COST = "cost";
    public static final String STATUS = "status";

    // spark任务相关
    public static final String KAFKA_GROUP_ID_CONFIG = "group.id";
    public static final String KAFKA_BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
    public static final String KAFKA_AUTO_OFFSET_RESET_CONFIG = "auto.offset.reset";

    // 日志级别
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_WARNING = "WARNING";

    // rpc 服务注册中心相关
    public static final String ZK_REGISTRY_SERVICE_ROOT_PATH = "/skyeye/registry/service";
    public static final String ZK_REGISTRY_ID_ROOT_PATH = "/skyeye/registry/id";
    public static final String ZK_REGISTRY_SEQ = "/skyeye/seq";

    public static final String RPC_TYPE_NONE = "none";
    public static final String RPC_TYPE_DUBBO = "dubbo";
    public static final String RPC_TYPE_THRIFT = "thrift";
    public static final String RPC_TYPE_SC = "sc";

    // rpc trace 统计指标相关
    public static final String TRACE_SUCCESS = "success";
    public static final String TRACE_FAIL = "fail";
    public static final String TRACE_MAX = "max";
    public static final String TRACE_MIN = "min";
    public static final String TRACE_AVERAGE = "average";
    public static final String TRACE_TOTAL = "total";
    public static final String TRACE_TYPE = "type";
    public static final String TRACE_TABLE_NAME = "rpctrace";

    // 心跳检测相关
    public static final String HEARTBEAT_KEY = "heart beat key";
    public static final String HEARTBEAT_VALUE = "heart beat value";

    // docker容器相关
    public static final String SKYEYE_HOST_TO_REGISTRY = "SKYEYE_HOST_TO_REGISTRY";
    public static final String COMPUTERNAME = "COMPUTERNAME";
    public static final String SKYEYE_HOST_FILE = System.getProperty("user.home") + "/.skyeye/host";
    public static final String UNKNOWN_HOST = "UnknownHost";

}
