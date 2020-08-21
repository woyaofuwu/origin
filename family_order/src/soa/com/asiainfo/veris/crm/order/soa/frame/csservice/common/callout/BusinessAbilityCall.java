
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;


import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class BusinessAbilityCall {
    public static Logger logger = Logger.getLogger(BusinessAbilityCall.class);

    /**
     * call 业务能力运营中心（公用）
     * add by liwei29
     * CRM权益中心相关需求
     *
     * @return
     */
    public final static IData callBusinessCenterCommon(String paramName, IData data) throws Exception {

        IData ret = new DataMap();
        try {
            //拼url参数
            String url = "";
            String formatType = "json";
            String flowId = SeqMgr.getFlowId();
            String sign = "&";
            String apiAddress = BizEnv.getEnvString(paramName);
            url = apiAddress+"format="+formatType+sign+"flowId="+flowId;
            logger.debug("-----url:----- " + url);
            String inputStr = data.toString();
            logger.debug("inputStr: " + inputStr);
            String result = httpRequest(url, inputStr);
            logger.debug("-----result:----- " + result);
            ret = new DataMap(result);
        } catch (Exception ex) {
            throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
    }

   /* public static void main(String[] args) throws Exception {

        String url = "http://10.212.2.80:8005/oppf?appId=502226& format=json&method=HAIN_UNHQ_QYorderList&flowId=2020060311203063759&appKey=5e08e2dd3d0b26ae260f76ef94460f38";
        url.replaceAll(" ","");
        IData input=new DataMap();
        logger.debug("-----url:----- " + url);
        input.put("orderId","1111");
        logger.debug("-----input:----- "+input);
        String inputStr =input.toString();
        logger.debug("inputStr: "+inputStr);
        String result = httpRequest(url, inputStr);
        logger.debug("-----result:----- "+result);
        IData ret=new DataMap(result);

    }*/

    /**
     * 方法体说明：向远程接口发起请求，返回字符串类型结果
     *
     * @param url 接口地址
     * @return String 返回结果
     * @param: requestMethod 请求类型
     * @param: params 传递参数
     */
    public static String httpRequest(String url, String requestBody) {
        // 接口返回结果
        String methodResult = null;
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Content-Type", "application/json;charset=utf-8");
            post.setRequestBody(requestBody);
            client.executeMethod(post);
            methodResult = post.getResponseBodyAsString();
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的编码格式", e);
        } catch (IOException e) {
            logger.error("IO异常", e);
        }
        return methodResult;
    }

}

