
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.FTTHBusiModemApplyBean;

/**
 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
 * chenxy3
 * 2015-12-8 
 * */
public class FTTHBusiModemManageSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(FTTHBusiModemManageSVC.class);

    private static final long serialVersionUID = 1L;

   
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 判断集团产品编码（即办理业务的号码）的product_id 是否 （7341 集团商务宽带产品） ，如果不是不能办理
     * chenxy3 20151029
     * */
    public IDataset checkFTTHBusi(IData inParam) throws Exception{ 
		FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	return bean.checkFTTHBusi(inParam);
    }
    
    public IDataset queryBusiModemInfo(IData inParam) throws Exception{ 
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER",""));
		param.put("MODERM_ID", inParam.getString("MODERM_ID",""));
		param.put("KD_NUMBER", inParam.getString("KD_NUMBER",""));
    	return bean.queryBusiModemInfo(inParam);
    }
    
    public IDataset queryBusiModemSupplementInfo(IData inParam) throws Exception{ 
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER",""));
		param.put("KD_NUMBER", inParam.getString("KD_NUMBER",""));
    	return FTTHBusiModemManageBean.queryBusiModemSupplementInfo(inParam);
    }
    
    /**
     * 
     * 校验录入的宽带号码
     * 1、是否存在TRADE表未完工记录
     * 2、是否已经申领过光猫
     * 3、是否用户有有效的FTTH宽带以及没有有效的光猫终端记录
     * */
    public IData checkKDNumber(IData inParam) throws Exception{ 
    	IData rtnData=new DataMap();
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	IData param=new DataMap();
    	param.put("CUSTID_GROUP", inParam.getString("CUSTID_GROUP"));
    	param.put("KD_NUMBER", inParam.getString("KD_NUMBER"));
    	param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
    	IDataset userOther = bean.checkKDNumInOtherTrade(param);
    	if(userOther!=null && userOther.size()>0){ //该宽带号码other表有光猫信息
    		rtnData.put("RTNCODE","8");//8=已经在OTHER表，说明已经申领过光猫
    		rtnData.put("RTNMSG", "该用户已经申领过光猫，无须再次办理。");
    	}else{
    		IDataset userTrade=bean.checkKDNumInTrade(param);
    		if(userTrade!=null && userTrade.size()>0){
        		rtnData.put("RTNCODE","1");//1=存在TRADE未完工记录，允许申领光猫
        		rtnData.put("RTNMSG", "");
        		rtnData.put("CUST_NAME", userTrade.getData(0).getString("CUST_NAME",""));
        		rtnData.put("UPDATE_TIME", userTrade.getData(0).getString("APPLY_TIME",""));
        		rtnData.put("KD_USERID", userTrade.getData(0).getString("USER_ID",""));
        		rtnData.put("KD_TRADE_ID", userTrade.getData(0).getString("TRADE_ID",""));
        	}else{
            	IData userInfo = UcaInfoQry.qryUserInfoBySn(inParam.getString("KD_NUMBER"));
        		if(IDataUtil.isEmpty(userInfo)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户宽带已拆机或未办理宽带开户，不能办理光猫申领业务！");
        		}
        		String userId = userInfo.getString("USER_ID");
        		IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
        		if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务，业务不能继续
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"您已办理拆机业务，不能办理光猫申领业务!");
        		}
        		IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
        		if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务，业务不能继续
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"您已办理特殊拆机业务，不能办理光猫申领业务!");
        		}
        		//判断是否有有效的FTTH宽带产品和没有有效的光猫终端记录
        		IDataset wideOtherInfo = bean.getWidenetAndNotFTTHModem(param);
        		if(DataSetUtils.isNotBlank(wideOtherInfo)){
        			rtnData.put("RTNCODE","2");//1=存在有效的FTTH宽带产品以及无效的光猫记录，可申领
        			rtnData.put("RTNMSG", "");
        			IDataset custInfo = CustomerInfoQry.queryCustInfoBySN(inParam.getString("SERIAL_NUMBER"));
        			if(DataSetUtils.isNotBlank(custInfo)){
        				rtnData.put("CUST_NAME", custInfo.getData(0).getString("CUST_NAME",""));
        			}
        			rtnData.put("KD_USERID", wideOtherInfo.getData(0).getString("USER_ID",""));
            		rtnData.put("UPDATE_TIME", wideOtherInfo.getData(0).getString("START_DATE",""));
            		IDataset otherInfo = bean.queryBusiModemInfo(param);
            		if(DataSetUtils.isNotBlank(otherInfo)){
            			rtnData.put("KD_TRADE_ID", otherInfo.getData(0).getString("RSRV_STR5",""));
            		}
        		}else{
        			rtnData.put("RTNCODE","9");//9=既没有TRADE表记录，又未申领过光猫，说明还未开通FTTH宽带。
            		rtnData.put("RTNMSG", "该用户未开通FTTH宽带业务，不允许办理。");
        		}
        	}
    	}
		return rtnData;
    }
    
    /**
     * 判断是否已经申领过光猫
     * 入参：USER_ID
     * 4、出参：
     *     1)FTTH_TAG ( 返回值：
				0--还未办理光猫申领  
				1--已经办理光猫申领，还未存在光猫串号 
				2--已经办理光猫申领，且已经存在光猫串号
			2)RES_NO  光猫串号，可能为空）
     * */
    public IData checkApplyModem(IData inparam)throws Exception{
    	IData rtnData=new DataMap();
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	IDataset userOthers=bean.checkApplyModem(inparam);
    	if(userOthers!=null && userOthers.size()>0){
    		String resNo=userOthers.getData(0).getString("RSRV_STR1","");
    		if(resNo!=null && !"".equals(resNo)){
    			rtnData.put("FTTH_TAG", "2");//2--已经办理光猫申领，且已经存在光猫串号
        		rtnData.put("RES_NO", resNo); 
    		}else{
    			rtnData.put("FTTH_TAG", "1");//1--已经办理光猫申领，还未存在光猫串号
        		rtnData.put("RES_NO", "");
    		}
    	}else{
    		rtnData.put("FTTH_TAG", "0");//0--还未办理光猫申领 
    		rtnData.put("RES_NO", "");
    	}
    	return rtnData;
    } 
    
    /**
     * 判断是否开户时是否已经申领过光猫
     * 入参：TRADE_ID
     * 4、出参：
     *     1)FTTH_TAG ( 返回值：
				0--还未办理光猫申领  
				1--已经办理光猫申领，还未存在光猫串号 
				2--已经办理光猫申领，且已经存在光猫串号
			2)RES_NO  光猫串号，可能为空）
     * */
    public IData checkApplyModemByTradeId(IData inparam)throws Exception{
    	IData rtnData=new DataMap();
    	String rsrv_value_code = "FTTH_GROUP";
    	String tradeId = inparam.getString("TRADE_ID");
    	IDataset tradeInfos = TradeInfoQry.queryTradeSet(tradeId, null);
    	if(DataSetUtils.isNotBlank(tradeInfos)){
    		String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
    		IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "3910", tradeTypeCode, "0898");
    		if(DataSetUtils.isNotBlank(paras)){
    			rtnData.put("FTTH_TAG", "0");//0--还未办理光猫申领 
        		rtnData.put("RES_NO", "");
    		}else{
    			IDataset userOtherInfos= TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
    	    	if(DataSetUtils.isNotBlank(userOtherInfos)){
    	        	IDataset userOthers = new DatasetList();
    	    		int tradeSize = userOtherInfos.size();
    	        	for(int i = 0 ; i < tradeSize ; i++){
    	        		if("0".equals(userOtherInfos.getData(i).getString("MODIFY_TAG"))){
    	        			userOthers.add(userOtherInfos.getData(i));
    	        		}
    	        	}
    	        	if(userOthers!=null && userOthers.size()>0){
    	        		String resNo=userOthers.getData(0).getString("RSRV_STR1","");
    	        		if(resNo!=null && !"".equals(resNo)){
    	        			rtnData.put("FTTH_TAG", "2");//2--已经办理光猫申领，且已经存在光猫串号
    	            		rtnData.put("RES_NO", resNo); 
    	        		}else{
    	        			rtnData.put("FTTH_TAG", "1");//1--已经办理光猫申领，还未存在光猫串号
    	            		rtnData.put("RES_NO", "");
    	        		}
    	        	}else{
    	        		rtnData.put("FTTH_TAG", "0");//0--还未办理光猫申领 
    	        		rtnData.put("RES_NO", "");
    	        	}
    	    	}else{
    	    		IDataset userOthers = FTTHBusiModemApplyBean.checkApplyModem(inparam);
    	    		if(userOthers!=null && userOthers.size()>0){
    	        		String resNo=userOthers.getData(0).getString("RSRV_STR1","");
    	        		if(resNo!=null && !"".equals(resNo)){
    	        			rtnData.put("FTTH_TAG", "2");//2--已经办理光猫申领，且已经存在光猫串号
    	            		rtnData.put("RES_NO", resNo); 
    	        		}else{
    	        			rtnData.put("FTTH_TAG", "1");//1--旧开户用户光猫申领流程已经办理光猫申领，还未存在光猫串号
    	        			rtnData.put("FTTH_TAG_OLD", "1");//旧开户用户光猫申领流程标记
    	            		rtnData.put("RES_NO", "");
    	        		}
    	        	}else{
    	        		rtnData.put("FTTH_TAG", "0");//0--还未办理光猫申领 
    	        		rtnData.put("RES_NO", "");
    	        	}
    	    	}
    		}
    	}else{
    		rtnData.put("FTTH_TAG", "0");//0--还未办理光猫申领 
    		rtnData.put("RES_NO", "");
    	}
    	return rtnData;
    } 
    
    /**
     * 更新申领光猫串号
     * */
    public IData updFtthBusiResNO(IData inparam)throws Exception{
    	IData returnInfo=new DataMap();
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	IData checkData=this.checkApplyModemByTradeId(inparam);
    	String FTTH_TAG=checkData.getString("FTTH_TAG","");
    	if("1".equals(FTTH_TAG)){
    		String FTTH_TAG_OLD = checkData.getString("FTTH_TAG_OLD","");
    		if("1".equals(FTTH_TAG_OLD)){//旧开户用户光猫申领流程
    			int updResult=FTTHBusiModemManageBean.updOldFtthBusiResNO(inparam);
        		returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("X_RESULTINFO", "办理成功!");
    		}else{
    			int updResult=bean.updFtthBusiResNO(inparam);
        		returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("X_RESULTINFO", "办理成功!"); 
    		}
    		
    	}else if("0".equals(FTTH_TAG)){
    		returnInfo.put("X_RESULTCODE", "6130");
            returnInfo.put("X_RESULTINFO", "该用户USER_ID=【"+inparam.getString("USER_ID")+"】还没有办理光猫申领业务!"); 
    	}else{
    		returnInfo.put("X_RESULTCODE", "6131");
            returnInfo.put("X_RESULTINFO", "该用户USER_ID=【"+inparam.getString("USER_ID")+"】已存在光猫信息！");
    	}
    	 
        return returnInfo; 
    }
    /**
     * 装机失败用户返还光猫
     * */
    public void returnResByWilenFailUser(IData inparam)throws Exception{
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	bean.returnResByWilenFailUser(inparam);
    }
}
