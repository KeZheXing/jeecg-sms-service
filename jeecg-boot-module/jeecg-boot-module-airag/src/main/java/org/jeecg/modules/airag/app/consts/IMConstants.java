package org.jeecg.modules.airag.app.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: KKKKK
 * @Date: 2023/4/8 18:38
 **/
public class IMConstants {

    public static final String SCRIPT_SUCCESS = "SUCCESS";
    public static final String SCRIPT_FAILED = "FAILED";
    public static final String SCRIPT_FINISH = "-----------FINISH----------";
    public static final String SCRIPT_NOT_FOUNT = "NOT_FOUNT";
    public static final String SECRET = "openIM123";
    public static final String ADMIN_USER_ID = "openIMAdmin";
    public static List<String> groupIdList;
    public static List<String> adminList;
    public static List<String> calBackEvent;
    public static String SMS_vonage="SMS_vonage";

    public static String AUTO_RESP="AUTO_RESP_";

    public static String AUTO_RESP_TEXT="AUTO_RESP_TEXT";

    public static final String SMS_RECEIVED ="sms:received";
    public static final String SMS_sent ="sms:sent";
    public static final String SMS_DELIVERED ="sms:delivered";
    public static final String SMS_FAILED ="sms:failed";


    static {
        adminList = new ArrayList<>();
        adminList.add("5756919621");
        adminList.add("6074952936");
        calBackEvent = new ArrayList<>();
        calBackEvent.add("sms:received");
        calBackEvent.add("sms:sent");
        calBackEvent.add("sms:delivered");
        calBackEvent.add("sms:failed");
    }

    public static String HOST="http://18.142.243.193";

    public static String TOKEN;

    public static Map<String,String> TOKEN_HEADER = new HashMap<>();

    public static String URL_AUTH_USER_TOKEN = HOST + ":10002/auth/user_token";

    public static String URL_MSG_SEND = HOST + ":6060/im/imSendTo";

    public static String URL_MSG_SEND_FIRST = HOST + ":6060/im/imSendToFirst";

    public static String URL_MSG_SEND_FIRST_BATCH = HOST + ":6060/im/imSendToFirstBatch";

    public static String URL_RESET_PWD = HOST + ":6060/im/imResetPwd";

    public static String URL_AUTH_REG_USER = HOST + ":6060/im/imReg";

    public static String URL_AUTH_ADMIN_REG_USER = HOST + ":6060/im/imReg";

    public static String URL_AUTH_ADMIN_REBIND_USER = HOST + ":6060/im/imRebind";
}
