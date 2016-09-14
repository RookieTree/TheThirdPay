package net.sourceforge.simcpux;

/**
 * 微信支付模型对象
 *
 */
public class WechatPayInfo {


    /**
     * appid : wxb4ba3c02aa476ea1
     * partnerid : 1305176001
     * packageValue : Sign=WXPay
     * noncestr : 5f520775aeefd1d7882193c82e04a684
     * timestamp : 1472803733
     * prepayid : wx20160902160853021ca5c3bf0006070358
     * sign : 12472AE02B427BBC8E8467267AB7BD84
     */

    private String appid;
    private String partnerid;
    private String packageValue;
    private String noncestr;
    private int timestamp;
    private String prepayid;
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "WechatPayInfo{" +
                "appid='" + appid + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", packageValue='" + packageValue + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp=" + timestamp +
                ", prepayid='" + prepayid + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
