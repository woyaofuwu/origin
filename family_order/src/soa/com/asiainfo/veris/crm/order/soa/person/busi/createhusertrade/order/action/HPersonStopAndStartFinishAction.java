package com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade.order.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

/**
 *  REQ202003300006_关于和教育异网用户信控管理需求  by huangmx 20200422
 *  和教育异网用户，高额停机、欠费停机时crm调用接口暂停平台侧服务，缴费开机时crm调用接口恢复平台侧服务
 *  
 * @author hmx
 *
 */
public class HPersonStopAndStartFinishAction implements ITradeFinishAction{
	static final Logger logger = Logger.getLogger(HPersonStopAndStartFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
        String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE","");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String hPersonFlag = serialNumber.substring(0, 1);//和教育异网号码
        logger.debug("========高额停机"+tradeTypeCode+"手机号"+serialNumber);
        if("H".equals(hPersonFlag)){
        	logger.debug("========高额停机"+tradeTypeCode);
            //高额停机
            if ("7110".equals(tradeTypeCode)) {
            	//组装发起数据
            	IData abilityData = new DataMap();
            	IData uData = new DataMap();
            	IData bizPointList = new DataMap();
            	IData bizPoint = new DataMap();
            	IData uParamList = new DataMap();
            	IData uParamInfo = new DataMap();
            	abilityData.put("PkgSeq", mainTrade.getString("TRADE_ID"));//交易包流水号，发起方保证唯一，以便落地方应答
            	
            	IData param = new DataMap();
                param.put("USER_ID", mainTrade.getString("USER_ID"));
                IDataset discInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param);
                if(IDataUtil.isNotEmpty(discInfos)){
                	//先取一个优惠传过去，还没确定要不要传多个
                    IData discInfo = discInfos.getData(0);
                    bizPoint.put("PointCode", discInfo.getString("DISCNT_CODE"));//业务功能点代码，优惠编码
                    bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }else{
                	bizPoint.put("PointCode", "");//业务功能点代码，优惠编码
                	bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }
            	bizPointList.put("BizPoint", bizPoint);
            	uData.put("BizPointList", bizPointList);
            	uData.put("BizServCode", "106575001234");//业务接入号，与“学校信息同步”中的“业务接入号”相同
            	uData.put("ECID", "898");//EC企业代码，学校集团898编码
            	uData.put("FeeMobNum", mainTrade.getString("SERIAL_NUMBER"));//用户计费手机号码，计费号码
            	uData.put("MobNum", mainTrade.getString("SERIAL_NUMBER"));//用户手机号码，和校园家长号码
            	uData.put("OprCode", "03");
            	//流水号为了保证唯一，精确到毫秒
            	Date date = new Date();
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            	uData.put("OprSeq", sdf.format(date));//本次操作的流水，防止同一秒内有多笔订单，精确到毫秒
            	uData.put("UParamList", uParamList);
            	uParamList.put("UParamInfo", uParamInfo);
            	uParamInfo.put("ParamCode", "");//参数代码系统解析使用，如stu_name1、stu_name2、stu_name3
            	uParamInfo.put("ParamValue", mainTrade.getString("CUST_NAME"));//参数值，参数值，为学生姓名
            	abilityData.put("UData", uData);
        		logger.debug("===================接口入参："+abilityData);
        		IData retData = new DataMap();
        		try {
        			//调用能开接口
            		retData = pubMethod(abilityData);
    			} catch (Exception e) {
    				logger.error("HPersonStopAndStartFinishAction exception:" + e.getMessage());
    			}
        		if (IDataUtil.isEmpty(retData)) {
        			logger.debug("能开返回参数retData为空 "+retData);
        		}else{
        			logger.debug("能开返回参数retData="+retData);
        		}
            }
            //欠费停机
            if("7220".equals(tradeTypeCode)){
            	//组装发起数据
            	IData abilityData = new DataMap();
            	IData uData = new DataMap();
            	IData bizPointList = new DataMap();
            	IData bizPoint = new DataMap();
            	IData uParamList = new DataMap();
            	IData uParamInfo = new DataMap();
            	abilityData.put("PkgSeq", mainTrade.getString("TRADE_ID"));//交易包流水号，发起方保证唯一，以便落地方应答
            	
            	IData param = new DataMap();
                param.put("USER_ID", mainTrade.getString("USER_ID"));
                IDataset discInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param);
                if(IDataUtil.isNotEmpty(discInfos)){
                	//先取一个优惠传过去，还没确定要不要传多个
                    IData discInfo = discInfos.getData(0);
                    bizPoint.put("PointCode", discInfo.getString("DISCNT_CODE"));//业务功能点代码，优惠编码
                    bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }else{
                	bizPoint.put("PointCode", "");//业务功能点代码，优惠编码
                	bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }
            	bizPointList.put("BizPoint", bizPoint);
            	uData.put("BizPointList", bizPointList);
            	uData.put("BizServCode", "106575001234");//业务接入号，与“学校信息同步”中的“业务接入号”相同
            	uData.put("ECID", "898");//EC企业代码，学校集团898编码
            	uData.put("FeeMobNum", mainTrade.getString("SERIAL_NUMBER"));//用户计费手机号码，计费号码
            	uData.put("MobNum", mainTrade.getString("SERIAL_NUMBER"));//用户手机号码，和校园家长号码
            	uData.put("OprCode", "03");
            	//流水号为了保证唯一，精确到毫秒
            	Date date = new Date();
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            	uData.put("OprSeq", sdf.format(date));//本次操作的流水，防止同一秒内有多笔订单，精确到毫秒
            	uData.put("UParamList", uParamList);
            	uParamList.put("UParamInfo", uParamInfo);
            	uParamInfo.put("ParamCode", "");//参数代码系统解析使用，如stu_name1、stu_name2、stu_name3
            	uParamInfo.put("ParamValue", mainTrade.getString("CUST_NAME"));//参数值，参数值，为学生姓名
            	abilityData.put("UData", uData);
        		logger.debug("===================接口入参："+abilityData);
        		IData retData = new DataMap();
        		try {
        			//调用能开接口
            		retData = pubMethod(abilityData);
    			} catch (Exception e) {
    				logger.error("HPersonStopAndStartFinishAction exception:" + e.getMessage());
    			}
        		if (IDataUtil.isEmpty(retData)) {
        			logger.debug("能开返回参数retData为空 "+retData);
        		}else{
        			logger.debug("能开返回参数retData="+retData);
        		}
        		
            }
            //缴费开机
            if("7301".equals(tradeTypeCode)){
            	//组装发起数据
            	IData abilityData = new DataMap();
            	IData uData = new DataMap();
            	IData bizPointList = new DataMap();
            	IData bizPoint = new DataMap();
            	IData uParamList = new DataMap();
            	IData uParamInfo = new DataMap();
            	abilityData.put("PkgSeq", mainTrade.getString("TRADE_ID"));//交易包流水号，发起方保证唯一，以便落地方应答
            	
            	IData param = new DataMap();
                param.put("USER_ID", mainTrade.getString("USER_ID"));
                IDataset discInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param);
                if(IDataUtil.isNotEmpty(discInfos)){
                	//先取一个优惠传过去，还没确定要不要传多个
                    IData discInfo = discInfos.getData(0);
                    bizPoint.put("PointCode", discInfo.getString("DISCNT_CODE"));//业务功能点代码，优惠编码
                    bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }else{
                	bizPoint.put("PointCode", "");//业务功能点代码，优惠编码
                	bizPoint.put("PointName", "");//业务功能点名称，优惠名称
                }
            	bizPointList.put("BizPoint", bizPoint);
            	uData.put("BizPointList", bizPointList);
            	uData.put("BizServCode", "106575001234");//业务接入号，与“学校信息同步”中的“业务接入号”相同
            	uData.put("ECID", "898");//EC企业代码，学校集团898编码
            	uData.put("FeeMobNum", mainTrade.getString("SERIAL_NUMBER"));//用户计费手机号码，计费号码
            	uData.put("MobNum", mainTrade.getString("SERIAL_NUMBER"));//用户手机号码，和校园家长号码
            	uData.put("OprCode", "04");
            	//流水号为了保证唯一，精确到毫秒
            	Date date = new Date();
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            	uData.put("OprSeq", sdf.format(date));//本次操作的流水，防止同一秒内有多笔订单，精确到毫秒
            	uData.put("UParamList", uParamList);
            	uParamList.put("UParamInfo", uParamInfo);
            	uParamInfo.put("ParamCode", "");//参数代码系统解析使用，如stu_name1、stu_name2、stu_name3
            	uParamInfo.put("ParamValue", mainTrade.getString("CUST_NAME"));//参数值，参数值，为学生姓名
            	abilityData.put("UData", uData);
        		logger.debug("===================接口入参："+abilityData);
        		IData retData = new DataMap();
        		try {
        			//调用能开接口
            		retData = pubMethod(abilityData);
    			} catch (Exception e) {
    				logger.error("HPersonStopAndStartFinishAction exception:" + e.getMessage());
    			}
        		if (IDataUtil.isEmpty(retData)) {
        			logger.debug("能开返回参数retData为空 "+retData);
        		}else{
        			logger.debug("能开返回参数retData="+retData);
        		}
            }
        	
        }
        
    }
    //调用能开接口
    public IData pubMethod(IData abilityData) throws Exception{
		//调用能开接口url
		String Abilityurl = "";
		IData param1 = new DataMap();
		param1.put("PARAM_NAME", "crm.ABILITY.CIP117");
		StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
		IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		if (Abilityurls != null && Abilityurls.size() > 0) {
			Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
		} else {
			CSAppException.appError("-1", "crm.ABILITY.CIP117接口地址未在TD_S_BIZENV表中配置");
		}
		String apiAddress = Abilityurl;
		//调用能力开放平台接口返回数据
		IData retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
		
    	return retData;
    }
}
