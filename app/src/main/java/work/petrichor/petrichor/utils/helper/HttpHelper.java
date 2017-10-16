package work.petrichor.petrichor.utils.helper;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.URLEncoder;
import java.util.Map;

import github.mitscherlich.jpack.utils.Base64Util;
import github.mitscherlich.jpack.utils.HttpUtil;

/**
 * @author mitscherlich
 * @date 2017/10/14
 **/

public class HttpHelper {

    public static final int RESPONSE_OK = 200;

    private static final String TAG = "HttpHelper";

    private String requestUrl;
    private Map<String, String> params;

    public HttpHelper(String requestUrl, Map<String, String> params) {
        this.requestUrl =requestUrl;
        this.params = params;
    }

    public void post(Handler handler, int action) {
        StringBuilder builder = new StringBuilder();

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey() + "=");
                builder.append(URLEncoder.encode(Base64Util.encode(entry.getValue().getBytes()), "UTF-8")).append("&");
            }
        } catch (Exception e) {
            Log.e(TAG, "URL encode error with message: " + e);
        }

        final String params = builder.toString();
        final Message message = handler.obtainMessage();
        final int what = action;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    message.what = what;
                    message.obj = HttpUtil.post(requestUrl, params.substring(0, params.length() - 1));
                    message.sendToTarget();
                } catch (Exception e) {
                    Log.e(TAG, "Post fail with error message: " + e);
                }
            }
        }).start();
    }

    public static class NetworkResult {

        public static final int ERR_SERVER_FAIL = 0;
        public static final int ERR_USER_EXIST = 20;
        public static final int ERR_USER_NOT_EXIST = 400;
        public static final int ERR_PSK_IS_NOT_SAME = 401;

        private boolean result;
        private int code;
        private String message;

        // 获取消息结果
        public boolean getResult() {
            return result;
        }

        // 获取响应代码
        public int getCode() {
            return code;
        }

        // 获取响应消息
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("result: " + result + "\n");
            builder.append("code: " + code + "\n");
            builder.append("message: " + message);
            return builder.toString();
        }
    }
}
