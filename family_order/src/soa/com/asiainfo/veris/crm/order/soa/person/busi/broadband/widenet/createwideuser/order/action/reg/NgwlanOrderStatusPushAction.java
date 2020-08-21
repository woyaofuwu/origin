package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;

public class NgwlanOrderStatusPushAction implements ITradeAction
{
	public static Logger logger = Logger.getLogger(NgwlanOrderStatusPushAction.class);
		
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String serialNumber = btd.getMainTradeData().getSerialNumber();
		String tradeId = btd.getTradeId();
		String tradeTypeCode  = btd.getTradeTypeCode();
		
		String businessType = ""; //业务类型   00:宽带 01:互联网电视 02:智能组网 03:IMS  注：若为多融合组合，则取值为类型组合，例如宽带+互联网电视对应的类型为：0001
		String orderType = "";//工单类型 1:装机单  2:移机单  3:拆机单
		String orderState = "1";//工单状态  1: 业务受理/投诉受理
		
		if("600".equals(tradeTypeCode)){//宽带开户
			businessType = "00";
			orderType = "1";
			
			MergeWideUserCreateRequestData reqData = (MergeWideUserCreateRequestData) btd.getRD();
			// 开通魔百和
	        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId())) {
	        	businessType += "01";
	        }

	        // 开通IMS固话
	        if (StringUtils.isNotBlank(reqData.getFixNumber()) && StringUtils.isNotBlank(reqData.getImsProductId())) {
	        	businessType += "03";
	        }
			
			if(serialNumber.startsWith("KD_")){
	    		serialNumber = serialNumber.substring(3);
        	}
		}else if("4800".equals(tradeTypeCode)){//魔百和开户
			
			
			
			InternetTvOpenRequestData reqData = (InternetTvOpenRequestData) btd.getRD();
			String serviceId = reqData.getTopSetBoxBasePkgs();
			if("40227762".equals(serviceId) || "80025539".equals(serviceId) ){//办理魔百和服务
            	businessType = "01";
    			orderType = "1";
            }
			
			
			if(serialNumber.startsWith("KD_")){
	    		serialNumber = serialNumber.substring(3);
        	}
		}else if("6800".equals(tradeTypeCode)){//IMS固话开户
			businessType = "03";
			orderType = "1";
			
			if(serialNumber.startsWith("KD_")){
	    		serialNumber = serialNumber.substring(3);
        	}
		}else if("870".equals(tradeTypeCode)){//智能组网增值产品开通
			businessType = "02";
			orderType = "1";
			
			if(serialNumber.startsWith("KD_")){
	    		serialNumber = serialNumber.substring(3);
        	}
		}else if("680".equals(tradeTypeCode)){//无宽带开户
			businessType = "00";
			orderType = "1";
			
		}
		
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String  dateStr = df.format(new Date());
		String XTransId = dateStr+"0000000000000001";
		
		
		
		//调用能开接口
        String Abilityurl = "";
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", "crm.ABILITY.CIP115");
        StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
        IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
        if (Abilityurls != null && Abilityurls.size() > 0) {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", "crm.ABILITY.CIP115接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        
        //调用能力开放平台接口
        IData retData = new DataMap();
        
        IData abilityData = new DataMap();
        abilityData.put("sheetNo", tradeId); // 订单编号
        abilityData.put("businessType", businessType); //业务类型
        abilityData.put("orderType", orderType); //工单类型
        abilityData.put("phoneNumber", serialNumber); //手机号码
        abilityData.put("orderState", orderState); //工单状态
        abilityData.put("provinceId", "00030029"); //省编码
        abilityData.put("regionId", ""); //地市编码
        abilityData.put("channelId", "ngwlan"); //渠道标识
        abilityData.put("orderResult", "0"); //装维处理结果  0：成功  1：失败/撤单
        abilityData.put("XTransId", XTransId);
        
        
        try {
        	if(StringUtils.isNotEmpty(businessType) && StringUtils.isNotEmpty(orderType) ){
        		 retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
                 logger.debug("NgwlanOrderStatusPushAction retData = " + retData);
        	}
        } catch (Exception e) {
        	logger.error("NgwlanOrderStatusPushAction  exception:" + e.getMessage());
        }

        if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String rtnCode = out.getString("rtnCode");
            String retMsg = out.getString("retMsg");
            if ("00000".equals(resCode)) {
                if (!"0".equals(rtnCode)) {
                	logger.error("调用能开失败 ，原因："+retMsg);
                	logger.error("调用能开参数：" + abilityData.toString());
                    logger.error("调用能开返回结果：" + retData.toString());
                } else {
                	 // 调用成功 
                	logger.debug("调用能开成功");
                	logger.debug("调用能开参数：" + abilityData.toString());
                    logger.debug("调用能开返回结果：" + retData.toString());
                }
            } else {
            	logger.error("调用能开失败 ，原因："+resMsg);
                logger.error("调用能开参数：" + abilityData.toString());
                logger.error("调用能开返回结果：" + retData.toString());
            }
        } 
	}
}
