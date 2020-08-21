package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.RemoteMobileException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class StopOpenMobileBean extends CSBizBean{

    private final static transient Logger logger = Logger.getLogger(StopOpenMobileBean.class);

    public IData applyStopOpenMobile(IData idata) throws Exception{
        IData result = new DataMap();
        addOprNumb(idata);
        result = IBossCall.remoteStopOpenMobileIBOSS(idata);
        if(logger.isDebugEnabled()) {
			logger.debug("stopopenmobile.data=========="+result);
        }
        if (IDataUtil.isEmpty(result)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调IBOSS返回为空！");
        } else if (StringUtils.equals("0000", result.getString("BIZ_ORDER_RESULT", ""))) {
        	IData electricOrderParam = new DataMap();
        	electricOrderParam.put("SERIAL_NUMBER", idata.getString("MOBILENUM"));
        	electricOrderParam.put("CUST_NAME", idata.getString("CUST_NAME1"));
        	electricOrderParam.put("PSPT_ID", idata.getString("IDCARDNUM"));
        	electricOrderParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            IDataset tradeRes = CSAppCall.call("SS.StopOpenMobileRegSVC.tradeReg", electricOrderParam);
            result.putAll(tradeRes.first());
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调IBOSS返回状态异常！RESULT_DESC：" + result.getString("RESULT_DESC", ""));
        }
        return result;
    }
    
    /**
	 * 异地停开机发起接口
	 * @author liukai5
	 * @param cycle
	 * @throws Exception
	 */
	public IData stopOpenMobile(IData input) throws Exception{
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		IDataUtil.chkParam(input, "IDENT_CODE");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		IDataUtil.chkParam(input, "ID_TYPE");
		IDataUtil.chkParam(input, "OPR_CODE");
		IDataUtil.chkParam(input, "OPR_NUMB");
		IDataUtil.chkParam(input, "CHANNEL");
		String oprCode=input.getString("OPR_CODE");
		String identCode=input.getString("IDENT_CODE");
		String idValue=input.getString("ID_VALUE");
		IData stopOpenresultData=new DataMap();
		IData resultData=new DataMap();
		input.put("SERIAL_NUMBER", serialNumber);
		// 身份凭证鉴权
		IDataset userAuth = UserIdentInfoQry.queryIdentInfoByCode(identCode, idValue);
        boolean valid = false;
        if(IDataUtil.isNotEmpty(userAuth)){
        	if("EXPIRE".equals(userAuth.getData(0).getString("TAG"))){
        		valid = true;
        	}
        }
        if(valid){
        	resultData.put("X_RESULTCODE", "3018");
        	resultData.put("X_RESULTINFO", "用户身份凭证已过期/失效");
        	resultData.put("X_RSPTYPE", "2");
        	resultData.put("X_RSPCODE", "2998");
        	resultData.put("X_RSPDESC", "用户身份凭证已过期/失效");
        	resultData.put("BIZ_ORDER_RESULT", "3018");
        	resultData.put("RESULT_DESC", "用户身份凭证已过期/失效");
        	return resultData;
        }
        try{
        	input.put("KQ_FLAG", "1");//跨区停复机标记
        	if("17".equals(oprCode)){
				input.put("TRADE_TYPE_CODE", "131");
				stopOpenresultData = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", input).getData(0);
			}else if("18".equals(oprCode)){
				input.put("TRADE_TYPE_CODE", "133");
				stopOpenresultData = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", input).getData(0);
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "操作码输入失败");
			}
        }catch(Exception e){
        	resultData.put("X_RESULTCODE", "2998");
        	resultData.put("X_RESULTINFO", e.getMessage());
        	resultData.put("X_RSPTYPE", "2");
        	resultData.put("X_RSPCODE", "2998");
        	resultData.put("X_RSPDESC", e.getMessage());
        	resultData.put("BIZ_ORDER_RESULT", "2998");
        	resultData.put("RESULT_DESC", e.getMessage());
        }
			
		
		if(!IDataUtil.isEmpty(stopOpenresultData)){
			if(StringUtils.isNotBlank(stopOpenresultData.getString("TRADE_ID"," "))){
				resultData.put("BIZ_ORDER_RESULT", "0000");
				resultData.put("X_RESULTCODE", "0000");
		 		resultData.put("X_RESULTINFO", "ok");
		 		resultData.put("X_RSPTYPE", "0");
		 		resultData.put("X_RSPCODE", "0000");
		 		resultData.put("X_RSPDESC", "ok");
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地停开机失败"+stopOpenresultData.getString("X_RESULTINFO",""));
			}
			
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地停开机异常");
		}
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("ID_VALUE", idValue);
		resultData.put("ID_TYPE", input.getString("ID_TYPE"));
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		return resultData;
	}
	
	  public IData addOprNumb(IData inparam) throws Exception
		 {
		     //补充操作流水号
			 String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			 String seqRealId = SeqMgr.getRealId();
			 inparam.put("OPR_NUMB", "COP"+"898"+ date + seqRealId);
		     return inparam;
		 }

}
