package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class ModifyTDCustInfoIntSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Logger log = Logger.getLogger(ModifyTDCustInfoIntSVC.class);
	
	/**
     * 1、无线固话客户资料查询接口
     * @author yanwu
     * @param param: IData
     * @service SS.ModifyTDCustInfoIntSVC.getCustInfo
     * @return IData
     * @throws Exception
     */
    public IData getCustInfo(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	IDataUtil.chkParam(param, "CHECK_MODE");
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	String strCheckMode = param.getString("CHECK_MODE");
    	param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        //如果操作员是通过免密码的方式则 模糊化客户资料
        //如果操作员是通过身份校验 进入页面的 则不模糊化
        if( !"F".equals(strCheckMode) )//F是认证组件中的免模糊化
        {
        	param.put("X_DATA_NOT_FUZZY","true");//强制不模糊化
        }
        
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        if( uca == null ){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:找不到用户资料!");
        }

        param.put("CUST_EPARCHY_CODE", uca.getCustomer().getEparchyCode());
        param.put("USER_ID", uca.getUserId());
        param.put("CUST_ID", uca.getCustId());
        param.put("TRADE_TYPE_CODE", "3811");
        IData custInfos = CSAppCall.call( "SS.ModifyCustInfoSVC.getCustInfo", param).getData(0);


        IData retnData = new DataMap(); 
        retnData.putAll(custInfos.getData("CUST_INFO"));
        retnData.putAll(custInfos.getData("COMM_INFO"));
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", getVisit().getDepartId());
        String staffid=getVisit().getStaffId();
        retnData.put("DEPART_KIND_CODE", departKindCode);
        retnData.put("LOGIN_STAFF_ID", staffid);
        
        return retnData;
    }
    
    /**
     * 2、无线固话客户资料修改接口
     * @author yanwu
     * @param param: IData
     * @service SS.ModifyTDCustInfoIntSVC.setCustInfo
     * @return IData
     * @throws Exception
     */
    public IData setCustInfo(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	IDataUtil.chkParam(param, "CHECK_MODE");
    	IDataUtil.chkParam(param, "CUST_NAME");
    	//IDataUtil.chkParam(param, "SEX");
    	IDataUtil.chkParam(param, "IS_REAL_NAME");
    	IDataUtil.chkParam(param, "PSPT_TYPE_CODE");
    	IDataUtil.chkParam(param, "PSPT_ID");
    	IDataUtil.chkParam(param, "PSPT_END_DATE");
    	IDataUtil.chkParam(param, "PSPT_ADDR");
    	IDataUtil.chkParam(param, "PHONE");
    	IDataUtil.chkParam(param, "REMARK");
    	
    	String strPtc = param.getString("PSPT_TYPE_CODE", "");
    	if( "D".equals(strPtc) || "E".equals(strPtc) || "G".equals(strPtc) || "L".equals(strPtc) || "M".equals(strPtc) ){
    		IDataUtil.chkParam(param, "AGENT_CUST_NAME");
        	IDataUtil.chkParam(param, "AGENT_PSPT_TYPE_CODE");
        	IDataUtil.chkParam(param, "AGENT_PSPT_ID");
        	IDataUtil.chkParam(param, "AGENT_PSPT_ADDR");
    	}
    	param.put("TRADE_TYPE_CODE", "3811");
    	IData dataset = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", param).getData(0);
    	return dataset;
    }
}
