
package com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
 * chenxy3
 * 2015-12-8 
 * */
public class FTTHBusiModemApplySVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(FTTHBusiModemApplySVC.class);

    private static final long serialVersionUID = 1L;

   
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 判断集团产品编码（即办理业务的号码）的product_id 是否 （7341 集团商务宽带产品） ，如果不是不能办理
     * chenxy3 20151029
     * */
    public IDataset checkFTTHBusi(IData inParam) throws Exception{ 
		FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
		IDataset userInfo=bean.checkFTTHBusi(inParam);
    	return userInfo;
    }
    
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 校验录入的宽带号码
     * 1、是否存在TRADE表未完工记录
     * 2、是否已经申领过光猫
     * */
    public IData checkKDNumber(IData inParam) throws Exception{ 
    	IData rtnData=new DataMap();
    	FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
    	IData param=new DataMap();

    	param.put("CUSTID_GROUP", inParam.getString("CUSTID_GROUP"));
    	param.put("KD_NUMBER", inParam.getString("KD_NUMBER"));
    	IDataset userTrade=bean.checkKDNumInTrade(param);
    	if(userTrade!=null && userTrade.size()>0){
    		rtnData.put("RTNCODE","1");//1=存在TRADE未完工记录，允许申领光猫
    		rtnData.put("RTNMSG", "");
    		rtnData.put("CUST_NAME", userTrade.getData(0).getString("CUST_NAME",""));
    		rtnData.put("UPDATE_TIME", userTrade.getData(0).getString("APPLY_TIME",""));
    		rtnData.put("KD_USERID", userTrade.getData(0).getString("USER_ID",""));
    		rtnData.put("KD_TRADE_ID", userTrade.getData(0).getString("TRADE_ID",""));
    	}else{
    		IDataset userOther=bean.checkKDNumInOther(param);
    		if(userOther!=null && userOther.size()>0){
    			rtnData.put("RTNCODE","8");//8=已经在OTHER表，说明已经申领过光猫
        		rtnData.put("RTNMSG", "该用户已经申领过光猫，无须再次办理。");
    		}else{
    			rtnData.put("RTNCODE","9");//9=既没有TRADE表记录，又未申领过光猫，说明还未开通FTTH宽带。
        		rtnData.put("RTNMSG", "该用户未开通FTTH宽带业务，不允许办理。");
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
    	FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
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
     * 更新申领光猫串号
     * */
    public IData updFtthBusiResNO(IData inparam)throws Exception{
    	IData returnInfo=new DataMap();
    	FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
    	IData checkData=this.checkApplyModem(inparam);
    	String FTTH_TAG=checkData.getString("FTTH_TAG","");
    	if("1".equals(FTTH_TAG)){
    		int updResult=bean.updFtthBusiResNO(inparam);
    		returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "办理成功!"); 
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
    	FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
    	bean.returnResByWilenFailUser(inparam);
    }
    
    /**
     * REQ201604080019 关于优化商务宽带成员用户查询界面的需求
     * chenxy3 20160421
     * */
    public IDataset queryFTTHBusiMem(IData inParam) throws Exception{ 
		FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
		IDataset userInfo=FTTHBusiModemApplyBean.queryFTTHBusiMem(inParam,getPagination());
		for(int k=0;k<userInfo.size();k++){
			userInfo.getData(k).put("SERIAL_NUMBER_B", userInfo.getData(k).getString("SERIAL_NUMBER_B1"));
			String userId=userInfo.getData(k).getString("USER_ID");
			String departId=userInfo.getData(k).getString("TRADE_DEPART_ID");
			String prodId=userInfo.getData(k).getString("PRODUCT_ID");
			String prodName= UProductInfoQry.getProductNameByProductId(prodId);
			String departName=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
			userInfo.getData(k).put("PRODUCT_NAME", prodName);
			userInfo.getData(k).put("TRADE_DEPART_NAME", departName);
			IData inparam=new DataMap();
			inparam.put("TRADE_ID", userInfo.getData(k).getString("TRADE_ID"));
			IDataset usertradeOthers=FTTHBusiModemApplyBean.checkApplyModemByTradeId(inparam);
	    	if(usertradeOthers!=null && usertradeOthers.size()>0){
	    		userInfo.getData(k).put("MODEM_TAG", "是");
	    	}else{
	    		IData otherParam = new DataMap();
	    		otherParam.put("USER_ID", userId);
	    		IDataset userOthers=FTTHBusiModemApplyBean.checkApplyModem(otherParam);
	    		if(DataSetUtils.isNotBlank(userOthers)){
	    			userInfo.getData(k).put("MODEM_TAG", "是");
	    		}else{
	    			userInfo.getData(k).put("MODEM_TAG", "否");
	    		}
	    	}
		}
    	return userInfo;
    }

}
