package net.sourceforge.simcpux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity
        extends AppCompatActivity
        implements Response.ErrorListener, Response.Listener<String>
{

    private static final String TAG = "MainActivity";
    private WechatPayInfo mWecharInfo;
    private IWXAPI        mApi;
    private String        mWeixurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //微信支附测试接口,微信提供的
        mWeixurl = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";

        //初始化微信支付api对象
        mApi = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");
        //        mApi.registerApp("wxb4ba3c02aa476ea1");


    }

    public void weixPay(View v) {
        //1.发送请求
        StringRequest request = new StringRequest(mWeixurl, this, this);
        Volley.newRequestQueue(this).add(request);

    }

    private void sendPayRequest() {
        PayReq req = new PayReq();
        req.appId = mWecharInfo.getAppid();
        req.partnerId = mWecharInfo.getPartnerid();
        req.prepayId = mWecharInfo.getPrepayid();//核心参数：预支付订单号
        req.nonceStr = mWecharInfo.getNoncestr();
        req.timeStamp = mWecharInfo.getTimestamp()+"";
        req.packageValue = mWecharInfo.getPackageValue();
        req.sign = mWecharInfo.getSign();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        //3.调用微信支付sdk支付方法
        mApi.sendReq(req);
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT)
             .show();
    }

    @Override
    public void onResponse(String s) {
        //返回的结果有关键字,需要替换
        String result = s.replaceAll("package", "packageValue");
        Log.d(TAG, "onResponse: " + result);
        //2.解析服务器返回的支付串码
        mWecharInfo = JSON.parseObject(result, WechatPayInfo.class);
        //3.调用第三方支付
        sendPayRequest();
    }
}
