package com.example.tree.paydemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity
        extends AppCompatActivity
{

    private static final String TAG = "MainActivity";
    private String       mUrl;
    private String       mAliUrl;
    private String       tranNum;
    private OkHttpClient mClient;
    private AlipayInfo   mAlipayInfo;
    private String       mWeixurl;
    //    private IWXAPI       mApi;

    //4.处理支付宝支付结果
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PayResult payResult = new PayResult((String) msg.obj);
            //            showLog("payresult="+payResult);
            /**
             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
             * docType=1) 建议商户依赖异步通知
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息

            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT)
                     .show();
                //                showLog("支付成功");
            } else {
                // 判断resultStatus 为非"9000"则代表可能支付失败
                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    Toast.makeText(MainActivity.this, "支付结果确认中", Toast.LENGTH_SHORT)
                         .show();

                } else if (TextUtils.equals(resultStatus, "6001")) {
                    Toast.makeText(MainActivity.this, "取消失败", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT)
                         .show();
                    ;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //银联提供测试接口，到公司必须使用自己的支付接口，当前接口在服务器写死价格、商品等参数，所有不需要提交参数
        mUrl = "http://101.231.204.84:8091/sim/getacptn";
        //支付宝测试接口
        mAliUrl = "http://192.168.33.40:8080/HeiMaPay/Pay?goodId=111&count=1&price=0.01";//支付协议
        //微信支附测试接口,微信提供的
        mWeixurl = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";


        mClient = new OkHttpClient();

    }

    /**
     * 银联支付
     * @param v
     */
    public void unionPay(View v) {


        Call call = getCall(mUrl);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT)
                     .show();

            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException
            {

                //获取交易流水号
                tranNum = response.body()
                                  .string();
                UPPayAssistEx.startPayByJAR(MainActivity.this,
                                            PayActivity.class,
                                            null,
                                            null,
                                            tranNum,
                                            "01");

            }
        });

    }


    /**
     * 支付宝支付
     * @param v
     */
    public void aliPay(View v) {
        Toast.makeText(this, "alipay没有测试接口,无法完成测试", Toast.LENGTH_SHORT)
             .show();
        //支付四部曲
        //1.提交信息到服务器
        Call call = getCall(mAliUrl);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT)
                     .show();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException
            {

                //2.解析服务器返回的"支付串码"
                mAlipayInfo = JSON.parseObject(response.body()
                                                       .string(), AlipayInfo.class);
                //3.调用第三方支付SDK的支付方法，传入“支付串码”
                PayTask payTask = new PayTask(MainActivity.this);
                //同步返回支付结果
                String payResult = payTask.pay(mAlipayInfo.getPayInfo(), true);

                Message msg = mHandler.obtainMessage();
                msg.obj = payResult;
                mHandler.sendMessage(msg);
            }

        });


        //2.解析服务器返回的"支付串码"

        //3.调用第三方支付sdk,传入"支付串码"
        //4.处理支付结果

    }


    /**
     * 处理银联支付返回的结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String msg = null;
        /** 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消*/
        String str = data.getExtras()
                         .getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        Toast.makeText(this, "支付结果：" + msg, Toast.LENGTH_SHORT)
             .show();
        //是不是要把支付结果发送给服务器呢？
        /**
         * 同步返回支付结果：客户端好直接开户获取到支付结果
         * 异步通知服务器：服务器不能直接获取支付结果，需要银联服务器通知我们的服务器，为什么不能用客户端通知，因为客户端通知不可靠
         */
    }

    private Call getCall(String url) {
        Request request = new Request.Builder().url(url)
                                               .build();
        return mClient.newCall(request);
    }
}
