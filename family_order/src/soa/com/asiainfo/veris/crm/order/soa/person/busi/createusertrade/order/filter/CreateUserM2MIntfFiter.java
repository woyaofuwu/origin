package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.filter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class CreateUserM2MIntfFiter implements IFilterIn 
{

	protected static final Logger log = Logger.getLogger(CreateUserM2MIntfFiter.class);
	
	/**
	 * 批量开户接口入参检查
	 * @author yanwu
	 * @param param
	 * @throws Exception
	 */
    public void checkInparam(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "SIM_CARD_NO");
        
        IDataUtil.chkParam(param, "SELECTED_ELEMENTS");
        
        IDataUtil.chkParam(param, "CUST_NAME");
        //IDataUtil.chkParam(param, "PHONE");
        //IDataUtil.chkParam(param, "USER_TYPE_CODE");
        IDataUtil.chkParam(param, "PSPT_TYPE_CODE");
        IDataUtil.chkParam(param, "PSPT_ID");
        //IDataUtil.chkParam(param, "BIRTHDAY");
        IDataUtil.chkParam(param, "PSPT_ADDR");
        IDataUtil.chkParam(param, "PSPT_END_DATE");
        
        IDataUtil.chkParam(param, "AGENT_CUST_NAME");
        IDataUtil.chkParam(param, "AGENT_PSPT_TYPE_CODE");
        IDataUtil.chkParam(param, "AGENT_PSPT_ID");
        IDataUtil.chkParam(param, "AGENT_PSPT_ADDR");
        
        IDataUtil.chkParam(param, "PAY_NAME");
        IDataUtil.chkParam(param, "ACCT_DAY");
        IDataUtil.chkParam(param, "PAY_MODE_CODE");
    }
	
    public void transferDataInput(IData input) throws Exception
    {
    	if( log.isDebugEnabled() ){
        	log.debug("<<<<<<<<<svc>>>>>>>>>> com.ailk.personservice.busi.createusertrade.order.filter.CreateUserM2MIntfFiter <<<<<<<<<svc>>>>>>>>>>");
        }
    	// 进行规制校验
        checkInparam(input);
        
        //实名制开户证件开户数校验
        String strPsptID = input.getString("PSPT_ID");
        String strCustName = input.getString("CUST_NAME");
        IData param = new DataMap();
        param.put("PSPT_ID", strPsptID);
        param.put("CUST_NAME", strCustName);
        param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", "0898"));
        
        /**
         *  优化单位证件开户阀值权限设置需求
         *  mengqx
         */
        String strPsptTypeCode = input.getString("PSPT_TYPE_CODE");
        if ( !"".equals(strPsptTypeCode) && ("D".equals(strPsptTypeCode)||"E".equals(strPsptTypeCode)||"G".equals(strPsptTypeCode)
    	    	||"L".equals(strPsptTypeCode)||"M".equals(strPsptTypeCode)) ){
    		param.put("USER_TYPE", "1");//物联网卡（含IMS、行业应用卡）为1
    	}
        
        IDataset rs = CSAppCall.call("SS.CreatePersonUserSVC.checkRealNameLimitByPspt", param);
        if( IDataUtil.isNotEmpty(rs) ){
        	IData r = rs.getData(0);
        	String strCode = r.getString("CODE");
        	if( !"0".equals(strCode) ){
				String strMsg = r.getString("MSG"); 
				CSAppException.apperr(BizException.CRM_BIZ_5, strMsg);
			}
        	if( log.isDebugEnabled() ){
            	log.debug("<<<<<<<<<svc>>>>>>>>>> com.ailk.personservice.busi.createusertrade.order.filter.CreateUserM2MIntfFiter <<<<<<<<<CODE>>>>>>>>>>" + strCode);
            }
        }
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String userPwd = input.getString("USER_PASSWD", "");
        
        IDataset simCardInfo = new DatasetList();
        String checkDepartId = input.getString("CHECK_DEPART_ID", "");		// 根据操作员部门校验
        
        if( log.isDebugEnabled() ){
        	log.debug("<<<<<<<<<svc>>>>>>>>>> com.ailk.personservice.busi.createusertrade.order.filter.CreateUserM2MIntfFiter <<<<<<<<<checkDepartId>>>>>>>>>>" + checkDepartId);
        }
        
        ResCall.checkResourceForMphone("0", serialNumber, "0", checkDepartId, "");
        simCardInfo = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "", checkDepartId);
        /*if ( "1".equals(input.getString("M2M_FLAG", "0")) )
        {
        	@SuppressWarnings("unused")
        	
				//ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", checkDepartId);
            	//ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", checkDepartId, "", "0");
            
        }*/
        
        IData checkSimData = simCardInfo.getData(0);
        
        input.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
        input.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
        input.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
        input.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
        input.put("IMSI", checkSimData.getString("IMSI", ""));
        input.put("KI", checkSimData.getString("KI", ""));
        input.put("CAPACITY_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE", "1"));
        input.put("CARD_KIND_CODE", checkSimData.getString("CARD_KIND_CODE", ""));
        input.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", ""));
        input.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
        input.put("CARD_PASSWD", checkSimData.getString("PASSWORD", ""));// 密码密文 ceshi
        input.put("PASSCODE", checkSimData.getString("KIND", ""));// 密码加密因子 ceshi
       
        String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "0"); 
        input.put("RES_TYPE_CODE", strResTypeCode);
        String uSimOpc = checkSimData.getString("OPC", "");
        input.put("OPC_VALUE", uSimOpc);
        if (!StringUtils.isBlank(uSimOpc))
        {
        	input.put("OPC_CODE", "OPC_VALUE");
        	input.put("OPC_VALUE", uSimOpc);
        }
        
        IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);
        input.put("FLAG_4G", "");
        if ("01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")) && null != uSimOpc && !"".equals(uSimOpc))
        {
        	input.put("FLAG_4G", "1");
        }
        
        //物联网调资源接口返回的sim卡2、3、4G标识
        if ( "1".equals(input.getString("M2M_FLAG", "0")) )
        {
        	input.put("RES_FLAG_4G", simCardInfo.getData(0).getString("RSRV_STR5", ""));
        }
        input.put("IS_REAL_NAME","1");
        input.put("REAL_NAME", "1");
        input.put("ACCT_TAG", input.getString("ACCT_TAG", "2"));
        input.put("OPEN_MODE", "0");
        String strCardPasswd = simCardInfo.getData(0).getString("PASSWORD", "");
        String strKind = simCardInfo.getData(0).getString("KIND", "");
        input.put("CARD_PASSWD", strCardPasswd);	// 密码密文 
        input.put("PASSCODE", strKind);				// 密码加密因子 
        /*if ( StringUtils.isNotBlank(simCardInfo.getData(0).getString("PASSWORD", "")) 
           &&StringUtils.isNotBlank(simCardInfo.getData(0).getString("KIND", "")) )
            input.put("DEFAULT_PWD_FLAG", "1");*/

        input.put("DEFAULT_PWD_FLAG", "0");
        if ( StringUtils.isBlank(userPwd) ) {
        	input.put("USER_PASSWD", strCardPasswd);
        }
        String userTypeCode = input.getString("USER_TYPE_CODE", "0");
        input.getString("USER_TYPE_CODE", userTypeCode);
        
    }
}
