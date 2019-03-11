package com.zxycloud.common.utils.netWork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zxycloud.common.R;
import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.MediaFileJudgeUtils;
import com.zxycloud.common.widget.BswProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtils {

    public static final int SSO = 0x098;
    public static final int BUSINESS = 0x096;
    public static final int FILE = 0x095;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SSO, BUSINESS, FILE})
    public @interface NetApiType {
    }

    public static final int GET = 0x089;
    public static final int POST = 0x088;
    public static final int FILE_UPLOAD = 0x086;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GET, POST, FILE_UPLOAD})
    public @interface NetRequestType {
    }

    /**
     * 上下文
     */
    private final Context mContext;
    /**
     * 网络请求结果回调
     */
    private NetRequestCallBack netRequestCallBack;
    /**
     * header
     */
    private Map<String, String> headerParams;
    /**
     * 加载提示框
     */
    private BswProgressDialog bswProgressDialog;

    /**
     * header cookie
     */
    private InDiskCookieStore cookieStore;

    public NetUtils(Context mContext) {
        this.mContext = mContext;
        initHeader();
        cookieStore = new InDiskCookieStore(mContext);
        bswProgressDialog = new BswProgressDialog(mContext, R.style.progress_dialog_loading, R.string.is_loading);
    }

    /**
     * 初始化请求头，具体情况根据需求设置
     */
    private void initHeader() {
        if (headerParams == null) {
            headerParams = new HashMap<>();
//            headerParams.put("DEVICE_ID", App.getInstance().getAndroidId());
            headerParams.put("Content-Type", "application/json");
            headerParams.put("APP_TYPE", "7");
        }
    }

    /**
     * 初始化数据
     *
     * @param action    当前请求的尾址
     * @param inputType 网络请求类型
     */
    private Retrofit initBaseData(final String action, int inputType) {

        // https信任管理
        TrustManager[] trustManager = new TrustManager[] {
                new X509TrustManager() {

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (inputType != FILE) {
            // 请求超时
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
        } else {
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
        }
        // 请求参数获取
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(@android.support.annotation.NonNull Chain chain) throws IOException {
                Request request = chain.request();
                okhttp3.Response proceed = chain.proceed(request);
                CommonUtils.log().i("request", "request header : ".concat(request.headers().toString())
                        .concat("\nrequest content : ".concat(request.toString()))
                        .concat("\nproceed header : ".concat(proceed.headers().toString())));
                return proceed;
            }
        });
        CookieManager cookieManager = new CookieManager(new InDiskCookieStore(mContext), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        builder.cookieJar(new JavaNetCookieJar(cookieManager));

        try {
            // https信任
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // 全部信任
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        // 构建Builder，请求结果RxJava接收，使用GSON转化为Bean，
        Retrofit.Builder builder1 = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

//        switch (inputType) {
//            case SSO:
//                builder1.baseUrl(String.format("%s%s", CommonUtils.getSPUtils(mContext).getString(SPUtils.SSO, BuildConfig.loginUrl), action));
//                break;
//
//            case BUSINESS:
//                builder1.baseUrl(String.format("%s%s", CommonUtils.getSPUtils(mContext).getString(SPUtils.SSO, BuildConfig.loginUrl), action));
//                break;
//
//            case FILE:
//                builder1.baseUrl(String.format("%s%s", CommonUtils.getSPUtils(mContext).getString(SPUtils.SSO, BuildConfig.loginUrl), action));
//                break;
//        }

        return builder1.build();
    }

    public void request(NetRequestCallBack netRequestCallBack, final boolean showDialog, final ApiRequest... apiRequests) {
        if (CommonUtils.judgeListNull(apiRequests) == 0) {
            throw new IllegalArgumentException("apiRequests can't be null");
        }
        this.netRequestCallBack = netRequestCallBack;
        if (showDialog) {
            showLoadDialog();
        }
        CommonUtils.threadPoolExecute(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                for (ApiRequest apiRequest : apiRequests) {
                    switch (apiRequest.getApiType()) {
                        case GET:
                            get(apiRequest.getAction(), apiRequest.getRequestParams(), apiRequest.getResultClazz(), apiRequest.getTag(), showDialog, apiRequest.getRequestType());
                            break;

                        case POST:
                            post(apiRequest.getAction(), apiRequest.getRequestBody(), apiRequest.getRequestParams(), apiRequest.getResultClazz(), apiRequest.getTag(), showDialog, apiRequest.getRequestType());
                            break;

                        case FILE_UPLOAD:
                            postFile(apiRequest.getAction(), apiRequest.getRequestBody(), apiRequest.getResultClazz(), apiRequest.getTag());
                            break;
                    }
                }
            }
        });
    }

    /**
     * Get请求
     *
     * @param action     网络请求尾址
     * @param params     请求参数
     * @param clazz      返回数据类
     * @param tag        请求复用时的判断标签
     * @param showDialog 是否展示请求加载框
     */
    private <T extends BaseBean> void get(final String action, Map<String, Object> params, Class<T> clazz, Object tag, boolean showDialog, int inputType) {
        RetrofitService getService = initBaseData(action.substring(0, action.lastIndexOf("/") + 1), inputType).create(RetrofitService.class);
        if (params == null) {
            params = new HashMap<>();
        }

        CommonUtils.log().i("zzz", "request====" + new JSONObject(params));

        getService.getResult(action.substring(action.lastIndexOf("/") + 1), headerParams, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<>(action, clazz, tag, showDialog));
    }

    /**
     * Post请求
     *
     * @param action     网络请求尾址
     * @param o          请求参数类
     * @param clazz      返回数据类
     * @param tag        请求复用时的判断标签
     * @param showDialog 是否展示请求加载框
     */
    private <T extends BaseBean> void post(final String action, Object o, Map<String, Object> fieldMap, Class<T> clazz, Object tag, boolean showDialog, int inputType) {

        boolean isFieldMapUseful = CommonUtils.judgeListNull(fieldMap) > 0;
        String requestString;
        String useAction = action.substring(action.lastIndexOf("/") + 1);
        if (CommonUtils.notEmpty(o)) {
            requestString = new Gson().toJson(o);
            if (isFieldMapUseful) {
                for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                    useAction = useAction.concat(useAction.contains("?") ? "&" : "?").concat(entry.getKey()).concat("=").concat(entry.getValue().toString());
                }
            }
        } else {
            requestString = String.valueOf(new JSONObject(isFieldMapUseful ? new HashMap<String, Object>() : fieldMap));
        }

        CommonUtils.log().i("request", "request====" + requestString);


        if (TextUtils.isEmpty(requestString) && CommonUtils.notEmpty(netRequestCallBack)) {
            //noinspection unchecked
            netRequestCallBack.error(action, new Exception(CommonUtils.text().getString(mContext, R.string.data_abnormal)), tag);
        }

        RetrofitService jsonService = initBaseData(action, inputType).create(RetrofitService.class);

        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        requestString);

        jsonService.postResult(useAction, headerParams, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<>(action, clazz, tag, showDialog));
    }

    /**
     * 上传文件
     *
     * @param action 尾址
     * @param clazz  返回结果bean 类
     * @param tag    标签
     * @param <T>    上传数据类泛型
     */
    private <T extends BaseBean> void postFile(String action, Object files, final Class<T> clazz, Object tag) {
        try {
            //noinspection unchecked
            List<File> fileList = (List<File>) files;
            if (CommonUtils.judgeListNull(fileList) == 0) {
                throw new IllegalArgumentException("You haven’t chosen file");
            }
            initHeader();
            //防止重置私有云后，不重新创建，导致异常
            RetrofitService fileService = initBaseData(action.substring(0, action.lastIndexOf("/") + 1), FILE).create(RetrofitService.class);

            Map<String, String> fileHeader = headerParams;
            if (fileHeader.containsKey("Content-Type")) {
                fileHeader.remove("Content-Type");
            }
            MediaFileJudgeUtils.MediaFileType mediaFileType = MediaFileJudgeUtils.getFileType(fileList.get(0).getAbsolutePath());
            if (CommonUtils.isEmpty(mediaFileType)) {
                throw new IllegalArgumentException("File is wrong type");
            }
            List<MultipartBody.Part> partList = filesToMultipartBodyParts(fileList, mediaFileType.mimeType);

            fileService.fileResult(action.substring(action.lastIndexOf("/") + 1), fileHeader, partList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<>(action, clazz, tag, true));
        } catch (ClassCastException e) {
            CommonUtils.log().e(getClass().getSimpleName(), e);
        }
    }

    private List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files, String fileType) {
        CommonUtils.log().i(getClass().getSimpleName(), fileType);
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(fileType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    public void removeCookies() {
        cookieStore.removeAll();
    }

    private class MyObserver<T extends BaseBean> implements Observer<ResponseBody> {

        private Class<T> clazz;
        private String action;
        boolean showDialog;
        /**
         * 返回结果状态：0、正常Bean；1、Bitmap
         */
        private int resultStatus = 0;
        private Object tag;

        MyObserver(String action, Class<T> clazz, Object tag, boolean showDialog) {
            this.clazz = clazz;
            this.action = action;
            this.tag = tag;
            this.showDialog = showDialog;
        }

        MyObserver(String action, Object tag, int resultStatus, boolean showDialog) {
            this.action = action;
            this.tag = tag;
            this.resultStatus = resultStatus;
            this.showDialog = showDialog;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {

        }

        @SuppressWarnings("unchecked")
        @Override
        public void onNext(@NonNull ResponseBody responseBody) {
            if (showDialog) {
                hideLoadDialog();
            }
            try {
                switch (resultStatus) {
                    case 0:
                        String responseString = responseBody.string();
                        CommonUtils.log().i("responseString", action + "********** responseString get  " + responseString);
                        if (CommonUtils.notEmpty(netRequestCallBack)) {
                            netRequestCallBack.success(action, (T) new Gson().fromJson(responseString, clazz), tag);
                        }
                        break;

                    case 1:
                        if (CommonUtils.notEmpty(netRequestCallBack)) {
                            netRequestCallBack.success(action, BitmapFactory.decodeStream(responseBody.byteStream()), tag);
                        }
                        CommonUtils.log().i("responseString", action + "********** 图片获取成功 ");
                        break;

                    default:
                        break;
                }
            } catch (IOException e) {
                CommonUtils.log().e(getClass().getSimpleName(), e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onError(@NonNull Throwable e) {
            if (showDialog) {
                hideLoadDialog();
            }
            try {
                if (e instanceof HttpException) {
                    ResponseBody errorbody = ((HttpException) e).response().errorBody();
                    if (CommonUtils.notEmpty(errorbody)) {
                        CommonUtils.log().i("responseString", String.format("%s********** responseString get error %s content %s", action, e.toString(), TextUtils.isEmpty(errorbody.string()) ? "" : errorbody));
                    }
                } else {
                    CommonUtils.log().i("responseString", String.format("%s********** responseString get error %s", action, e.toString()));
                }
            } catch (IOException | NullPointerException e1) {
                e1.printStackTrace();
            }
            if (CommonUtils.notEmpty(netRequestCallBack)) {
                netRequestCallBack.error(action, e, tag);
            }
        }

        @Override
        public void onComplete() {

        }
    }

    /**
     * 隐藏加载提示框
     */
    public void hideLoadDialog() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bswProgressDialog != null && bswProgressDialog.isShowing()) {
                    bswProgressDialog.dismiss();
                }
            }
        });
    }

    /**
     * 显示加载提示框
     */
    public void showLoadDialog() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bswProgressDialog != null && ! bswProgressDialog.isShowing()) {
                        bswProgressDialog.show();
                    }
                } catch (Exception ex) {
                    StringWriter stackTrace = new StringWriter();
                    ex.printStackTrace(new PrintWriter(stackTrace));
                    CommonUtils.log().i("Exception", stackTrace.toString());
                }
            }
        });
    }

    public boolean hasToken(String baseUrl) {
        return cookieStore.hasCookies(baseUrl);
    }

    /**
     * 网络请求文本结果回调接口
     */
    public abstract static class NetRequestCallBack<TT extends BaseBean> {

        public void success(String action, Bitmap bitmap, Object tag) {

        }

        public void success(String action, File file, Object tag) {

        }

        public abstract void success(String action, TT baseBean, Object tag);

        /**
         * 访问失败回调抽象方法
         *
         * @param action 网络访问尾址
         * @param e      所返回的异常
         * @param tag    当接口复用时，用于区分请求的表识
         */
        public abstract void error(String action, Throwable e, Object tag);
    }
}
