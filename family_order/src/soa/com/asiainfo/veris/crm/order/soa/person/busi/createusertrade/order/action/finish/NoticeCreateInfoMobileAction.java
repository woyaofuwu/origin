package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NoticeCreateInfoMobileAction implements ITradeFinishAction{
    
    private static transient Logger logger = Logger.getLogger(NoticeCreateInfoMobileAction.class);

    private static StringBuilder getInterFaceSQL;
    
    private static String PRETYPE = "BestUseMobile";

    static{

        getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
        }
    
	@Override
	public void executeAction(IData mainTrade) throws Exception {
	    String remark =  mainTrade.getString("REMARK");
        String iccid = "";
        if(StringUtils.isNotBlank(mainTrade.getString("RSRV_STR1"))){
            iccid = mainTrade.getString("RSRV_STR1");
        }
	    if(remark == null || remark.trim().equals("")){
	        return;
	    }
	    if(remark.endsWith("mobileuseful")){
	    	//remark = "@"+orderid+"@"+busiType+"@"+channelid+"@"+ospOrderId+"@mobileuseful";
	    	//remark = "@"+orderid+"@"+busiType+"@"+channelid+"@mobileuseful";
	        String[] remarkArray = remark.split("@");
	        String orderid = remarkArray[1];
	        String busiType = remarkArray[2];
	        String channelid = remarkArray[3];
	        String msisdn = mainTrade.getString("SERIAL_NUMBER");
	        String oprtime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	        
	        IData input = new DataMap();
	        input.put("ORDER_ID", orderid);
	        input.put("CHANNEL_ID", channelid);
	        input.put("MSISDN", msisdn);
	        input.put("RESULT_CODE", "0000");
	        input.put("RESULT_DESC", "开户成功");
	        input.put("OPR_TIME", oprtime);
            input.put("MARKETING_ACTION", busiType);
            input.put("OSP_ORDER_ID", remarkArray[4]);
	        if("002".equals(busiType)){
                input.put("ICCID", iccid);
	        }
	        //调用结果反馈接口
	        IData callData = callAbilityPlatCommon(input,"crm.ABILITY.BUMB");
	        
	        //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(msisdn,PRETYPE,"9","开户成功,能开返回信息: "+callData,"1","0");
	    }
        if(remark.endsWith("OneCardOneDevice")){ //关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
            IDataset resTrades = com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry.queryAllTradeResByTradeId(mainTrade.getString("TRADE_ID"));
            if(IDataUtil.isEmpty(resTrades)){
                return;
            }

            String newEid = "";
            String newIccid = "";
            String primarymsisdn = "";
            String newImei = "";
            for(int i=0,j=resTrades.size();i<j;i++){
                IData resTrade = resTrades.getData(i);
                if("E".equals(resTrade.getString("RES_TYPE_CODE")) && "0".equals(resTrade.getString("MODIFY_TAG"))){
                    primarymsisdn = resTrade.getString("RSRV_STR3");
                    String eidImeis = resTrade.getString("RSRV_STR2","");
                    String [] eidImei = eidImeis.split("@");
                    if(eidImei.length>1){
                        newEid = eidImei[0];
                    }
                    newImei = resTrade.getString("RSRV_STR4");
                    newIccid = resTrade.getString("RES_CODE");
                }
            }

            if(StringUtils.isEmpty(newEid)){
                return;
            }

            IData param = new DataMap();
            param.put("msisdn", mainTrade.getString("SERIAL_NUMBER"));
            param.put("deviceType","2");
            if(StringUtils.isNotEmpty(primarymsisdn)){//apple设备
                param.put("primarymsisdn", primarymsisdn);
                param.put("deviceType","1" );
            }
            param.put("eid", newEid);//补换卡
            param.put("imei", newImei);
            param.put("iccid1", newIccid);
            param.put("bizType", "001");//相同代表是eSIM设备新入网
            param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

            //调用平台接口
            String abilityCode = "crm.ABILITY.CIP127"; //6.11.	准备profile请求接口
            IData queryOrderInfo = callAbilityPlatCommon(param,abilityCode);
            if(logger.isDebugEnabled()){
                logger.debug(">>>>>>>>>>NoticeCreateInfoMobileAction>>>>>>>>>>>>>>>>"+queryOrderInfo);
            }
        }
	}
	
	//call 能力平台
    public final static IData callAbilityPlatCommon(IData data,String abilityCode) throws Exception{
        IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", abilityCode);
        IDataset urls = Dao.qryBySql(getInterFaceSQL, paramurl, "cen");
        String url = "";
        if (urls != null && urls.size() > 0)
        {
            url = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", abilityCode+"接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = url+"?";
        
        //类型
        String formatType="json";
        //time
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
        String timestamp = fmt.format(new Date()); // 时间戳，自动生成
        IData ret=new DataMap();
        String content= data.toString();
        try{
        // 唯一流水32位以内
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");// 请求流水号，自动生成
        //应用ID  应用的静态tocken 
        String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
        String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
        String staticToken=StaticUtil.getStaticValue("ABILITY_STATIC_TOKEN", "1");// 应用的静态tocken
        String access_token=AbilityEncrypting.getTockenStr(appId,staticToken);
     // 以下5项为非必填项,如果不填请给""或者null
        String userAuthorizationCode = ""; // 用户授权码，申请用户授权时，或者需要用户授权时填写
        String userPhoneNumber = ""; // 用户手机号
        String bIPCode = ""; // 业务流程编码
        String version = ""; // 业务流程版本号，若填写bIPCode，该字段必填
        String nodeId = ""; // 本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
        String sign = AbilityEncrypting.getSign(appId, timestamp, messageId, access_token, sessionId, content,
                userAuthorizationCode, userPhoneNumber, bIPCode, version, nodeId);
        //组合参数
//        String publicParam="&appId="+appId+"&access_token="+access_token+"&sign="+sign+"&timestamp="+timestamp+
//                           "&messageId="+messageId+"&sessionId="+sessionId;
//        String requestUrl = apiAddress+publicParam;
        IData input=new DataMap();
        input.put("appId", appId);
        input.put("access_token", access_token);
        input.put("sign", sign);
        input.put("timestamp", timestamp);
        input.put("messageId", messageId);
        input.put("sessionId", sessionId);
        input.put("content", data);  
        logger.debug("-----input:----- "+input);
        String inputStr =input.toString();
        logger.debug("inputStr: "+inputStr);
        String result = httpRequest(apiAddress, inputStr);
        logger.debug("-----result:----- "+result);
        ret=new DataMap(result); 
        }catch(Exception ex){
             throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
    }
    
    /**
     * 方法体说明：向远程接口发起请求，返回字符串类型结果
     * @param url 接口地址
     * @param requestMethod 请求类型
     * @param params 传递参数
     * @return String 返回结果
     */
    public static String httpRequest(String url, String requestBody) {
        // 接口返回结果
        String methodResult = null;
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Content-Type","application/json;charset=utf-8");
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
