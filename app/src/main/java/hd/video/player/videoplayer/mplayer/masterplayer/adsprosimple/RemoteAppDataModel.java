package hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemoteAppDataModel {
    @SerializedName("am_nativeids")
    @Expose
    private String am_nativeids;

    @SerializedName("am_bannerid")
    @Expose
    private String am_bannerid;

    @SerializedName("show_banner")
    @Expose
    private Integer show_banner;
    @SerializedName("nativeAdShow")
    @Expose
    private Integer nativeAdShow;

    @SerializedName("show_native")
    @Expose
    private Integer show_native;

    @SerializedName("am_interstitialid")
    @Expose
    private String am_interstitialid;

    @SerializedName("show_interstitial")
    @Expose
    private Integer show_interstitial;

    @SerializedName("am_openadid")
    @Expose
    private String am_openadid;

    @SerializedName("show_openad")
    @Expose
    private Integer show_openad;

    @SerializedName("am_rewardadid")
    @Expose
    private String am_rewardadid;

    @SerializedName("show_rewardad")
    @Expose
    private Integer show_rewardad;

    @SerializedName("inter_click_count")
    @Expose
    private Integer inter_click_count;

    @SerializedName("ads_per_session")
    @Expose
    private Integer ads_per_session;

    @SerializedName("inter_time")
    @Expose
    private Integer inter_time;

    @SerializedName("fb_bannerid")
    @Expose
    private String fb_bannerid;

    @SerializedName("fb_nativeids")
    @Expose
    private String fb_nativeids;

    @SerializedName("fb_interstitialid")
    @Expose
    private String fb_interstitialid;

    @SerializedName("qurekalinkarray")
    @Expose
    private String[] qurekalinkarray;

    @SerializedName("adstype")
    @Expose
    private String adstype;

    @SerializedName("privacyurl")
    @Expose
    private String privacyurl;

    @SerializedName("termsurl")
    @Expose
    private String termsurl;

    @SerializedName("gameiconlist")
    @Expose
    private String[] gameiconlist;

    @SerializedName("gameurllist")
    @Expose
    private String[] gameurllist;


    @SerializedName("adxbannerid")
    @Expose
    private String adxbannerid;


    @SerializedName("adxnativeids")
    @Expose
    private String adxnativeids;
    @SerializedName("adxinterstitialid")
    @Expose
    private String adxinterstitialid;
    @SerializedName("adxrewardadid")
    @Expose
    private String adxrewardadid;
    @SerializedName("adxopenadid")
    @Expose
    private String adxopenadid;

    public String getAdxopenadid() {
        return adxopenadid;
    }


    public void setAdxopenadid(String adxopenadid) {
        this.adxopenadid = adxopenadid;
    }


    public String getAdxbannerid() {
        return adxbannerid;
    }

    public void setAdxbannerid(String adxbannerid) {
        this.adxbannerid = adxbannerid;
    }

    public String getAdxnativeids() {
        return adxnativeids;
    }

    public void setAdxnativeids(String adxnativeids) {
        this.adxnativeids = adxnativeids;
    }

    public String getAdxinterstitialid() {
        return adxinterstitialid;
    }

    public void setAdxinterstitialid(String adxinterstitialid) {
        this.adxinterstitialid = adxinterstitialid;
    }

    public String getAdxrewardadid() {
        return adxrewardadid;
    }

    public void setAdxrewardadid(String adxrewardadid) {
        this.adxrewardadid = adxrewardadid;
    }


    public String[] getgameurllist() {
        return gameurllist;
    }

    public void setgameurllist(String[] gameurllist1) {
        this.gameurllist = gameurllist1;
    }

    public String[] getgameiconlist() {
        return gameiconlist;
    }

    public void setgameiconlist(String[] gameiconlist1) {
        this.gameiconlist = gameiconlist1;
    }


    public String gettermsurl() {
        return termsurl;
    }

    public void settermsurl(String termsurl1) {
        this.termsurl = termsurl1;
    }

    public String getprivacyurl() {
        return privacyurl;
    }

    public void setprivacyurl(String privacyurl1) {
        this.privacyurl = privacyurl1;
    }

    public String getadstype() {
        return adstype;
    }

    public void setadstype(String adstype1) {
        this.adstype = adstype1;
    }


    public String[] getQurekalinkArray() {
        return qurekalinkarray;
    }

    public void setQurekalinkArray(String[] qurekalinkarray1) {
        this.qurekalinkarray = qurekalinkarray1;
    }

    public String getfbInterstitialid() {
        return fb_interstitialid;
    }

    public void setfbInterstitialid(String interstitialid) {
        this.fb_interstitialid = interstitialid;
    }

    public String getfbNativeid() {
        return fb_nativeids;
    }

    public void setfbNativeid(String nativeid) {
        this.fb_nativeids = nativeid;
    }

    public String getfbBannerid() {
        return fb_bannerid;
    }

    public void setfbBannerid(String bannerid) {
        this.fb_bannerid = bannerid;
    }


    //other
    public static boolean isShowingFullScreenAd = false;
    public static int adspersession = 0;

    public Integer getAds_per_session() {
        return ads_per_session;
    }

    public void setAds_per_session(Integer ads_per_session) {
        this.ads_per_session = ads_per_session;
    }

    public Integer getinterclickcount() {
        return inter_click_count;
    }

    public void setinterclickcount(Integer ads_per_click) {
        this.inter_click_count = ads_per_click;
    }

    public String getamBannerid() {
        return am_bannerid;
    }

    public void setamBannerid(String bannerid) {
        this.am_bannerid = bannerid;
    }

    public Integer getshowBanner() {
        return show_banner;
    }

    public void setshowBanner(Integer banner) {
        this.show_banner = banner;
    }

    public Integer getshowNative() {
        return show_native;
    }

    public void setshowNative(Integer _native) {
        this.show_native = _native;
    }

    public String getamInterstitialid() {
        return am_interstitialid;
    }

    public void setamInterstitialid(String interstitialid) {
        this.am_interstitialid = interstitialid;
    }

    public Integer getshowInterstitial() {
        return show_interstitial;
    }

    public void setshowInterstitial(Integer interstitial) {
        this.show_interstitial = interstitial;
    }

    public String getamOpenadid() {
        return am_openadid;
    }

    public void setamOpenadid(String openadid) {
        this.am_openadid = openadid;
    }

    public Integer getshowOpenad() {
        return show_openad;
    }

    public void setshowOpenad(Integer openad) {
        this.show_openad = openad;
    }

    public String getamRewardadid() {
        return am_rewardadid;
    }

    public void setamRewardadid(String rewardadid) {
        this.am_rewardadid = rewardadid;
    }

    public Integer getshowRewardad() {
        return show_rewardad;
    }

    public void setshowRewardad(Integer rewardad) {
        this.show_rewardad = rewardad;
    }

    public Integer getintertime() {
        return inter_time;
    }

    public void setintertime(Integer interstitialtime) {
        this.inter_time = interstitialtime;
    }

    public String getamNativeid() {
        return am_nativeids;
    }

    public void setamNativeid(String nativeids) {
        this.am_nativeids = nativeids;
    }

}
