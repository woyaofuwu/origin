package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.filter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class CreateUserPwlwIntfFiter implements IFilterIn 
{

	protected static final Logger log = Logger.getLogger(CreateUserPwlwIntfFiter.class);
	
	/**
	 * 物联网开户接口入参检查
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
        IDataUtil.chkParam(param, "PHONE");
        //IDataUtil.chkParam(param, "USER_TYPE_CODE");
        IDataUtil.chkParam(param, "PSPT_TYPE_CODE");
        IDataUtil.chkParam(param, "PSPT_ID");
        IDataUtil.chkParam(param, "BIRTHDAY");
        IDataUtil.chkParam(param, "PSPT_ADDR");
        IDataUtil.chkParam(param, "PSPT_END_DATE");
        
        IDataUtil.chkParam(param, "PAY_NAME");
        IDataUtil.chkParam(param, "ACCT_DAY");
        IDataUtil.chkParam(param, "PAY_MODE_CODE");
    }
	
    public void transferDataInput(IData input) throws Exception
    {
    	if( log.isDebugEnabled() )
    	{
        	log.debug("<<<<<<<<<svc>>>>>>>>>> com.ailk.personservice.busi.createusertrade.order.filter.CreateUserPwlwIntfFiter <<<<<<<<<svc>>>>>>>>>>");
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
         * BUG20171129150222_物联网批量开户经常报错用户取消当前操作，请在判断条件去掉PWLW
         * <br/>
         * 物联网批量开户不需要，调本地一证五号逻辑
         * @author zhuoyingzhi
         * @date 20180109
         */
        log.debug("--------CreateUserPwlwIntfFiterLog-----:物联网批量开户参数效验,不调本地一证五号-----");
/*        IDataset rs = CSAppCall.call("SS.CreatePersonUserSVC.checkRealNameLimitByPspt", param);
        if( IDataUtil.isNotEmpty(rs) )
        {
        	IData r = rs.getData(0);
        	String strCode = r.getString("CODE");
        	if( !"0".equals(strCode) )
        	{
				String strMsg = r.getString("MSG"); 
				CSAppException.apperr(BizException.CRM_BIZ_5, strMsg);
			}
        	if( log.isDebugEnabled() )
        	{
            	log.debug("<<<<<<<<<svc>>>>>>>>>> com.ailk.personservice.busi.createusertrade.order.filter.CreateUserPwlwIntfFiter <<<<<<<<<svc>>>>>>>>>>" + strCode);
            }
        }*/
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String userPwd = input.getString("USER_PASSWD", "");
        
        IDataset simCardInfo = new DatasetList();
        String checkDepartId = input.getString("CHECK_DEPART_ID", "");		// 根据操作员部门校验
        if ( "1".equals(input.getString("M2M_FLAG", "0")) )
        {
        	@SuppressWarnings("unused")
			IDataset checkMphoneDatas = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", checkDepartId);
            simCardInfo = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", checkDepartId, "", "0");
        }
        
        String imsi = simCardInfo.getData(0).getString("IMSI", "-1");
        String ki = simCardInfo.getData(0).getString("KI", "");
        String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE", "");
        String res_kind_name = simCardInfo.getData(0).getString("RES_KIND_NAME", "");
        String sim_type_code = simCardInfo.getData(0).getString("SIM_TYPE_CODE", "");
        String strResTypeCode = simCardInfo.getData(0).getString("RES_TYPE_CODE", "0"); 
        String opc_value = simCardInfo.getData(0).getString("OPC", "");
        String feeTag = simCardInfo.getData(0).getString("FEE_TAG", "");
        input.put("IMSI", imsi);
        input.put("KI", ki);
        input.put("RES_KIND_CODE", res_kind_code);
        input.put("RES_KIND_NAME", res_kind_name);
        input.put("SIM_TYPE_CODE", sim_type_code);
        input.put("RES_TYPE_CODE", strResTypeCode);
        input.put("OPC_VALUE", opc_value);
        input.put("SIM_FEE_TAG", feeTag);
        //input.put("CUST_NAME", input.getString("CUST_NAME", "无档户"));
        //input.put("PAY_NAME", input.getString("CUST_NAME", "无档户"));
        //input.put("PAY_MODE_CODE", input.getString("PAY_MODE_CODE", "0"));
        //input.put("PSPT_TYPE_CODE", input.getString("PSPT_TYPE_CODE", "Z"));
        //input.put("PSPT_ID", input.getString("PSPT_ID", "11111111111111111111"));
        //input.put("PHONE", input.getString("PHONE", "10086"));
        //input.put("POST_CODE", input.getString("POST_CODE", "000000"));
        //input.put("CONTACT", input.getString("CUST_NAME", "无档户"));
        //input.put("CONTACT_PHONE", input.getString("CONTACT_PHONE", "10086"));
        //input.put("SUPER_BANK_CODE", input.getString("SUPER_BANK_CODE", ""));
        //input.put("BANK_CODE", input.getString("BANK_CODE", ""));
        //input.put("BANK_ACCT_NO", input.getString("BANK_ACCT_NO", ""));
        
        //物联网调资源接口返回的sim卡2、3、4G标识
        if ( "1".equals(input.getString("M2M_FLAG", "0")) )
        {
        	input.put("RES_FLAG_4G", simCardInfo.getData(0).getString("RSRV_STR5", ""));
        }
        input.put("IS_REAL_NAME","1");
        input.put("REAL_NAME", "1");
        
        //物联网沉默期用户标识
        if ( "1".equals(input.getString("CHEN_MO_QI", "")) )
        {
        	input.put("ACCT_TAG", "2");
        	input.put("USER_TYPE_CODE", "F");
        }
        else
        {
        	input.put("ACCT_TAG", "0");
		}
        
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
        
        //吉祥号开户校验
        IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
        if (IDataUtil.isNotEmpty(dataSet))
        {
        	IData mphonecodeInfo = dataSet.first();
        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
        	if (StringUtils.equals("1", beautifulTag))
        	{
        		String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
        		if(StringUtils.isBlank(productId))
        		{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
        		}
        		String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
        		if(StringUtils.isBlank(packageId))
        		{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销包编码为空，请检查资源侧配置！");
        		}
        		String bindDiscnt = mphonecodeInfo.getString("BIND_ELEMENT_ID", "");
                String bindMonth = mphonecodeInfo.getString("DEPOSIT_MONTH", "");
                String bindDiscntString = bindDiscnt + "|" + bindMonth;
                String saleString = productId + "|" + packageId;
        	     //IData saleactiveData = new DataMap();
                 //saleactiveData.put("SERIAL_NUMBER",serialNumber);
        		input.put("SALE_PRODUCT_ID", productId);
        		input.put("SALE_PACKAGE_ID", packageId);
        		input.put("X_BIND_DEFAULT_DISCNT", bindDiscntString);
        		input.put("A_X_CODING_STR", saleString);
                input.put("BIND_SALE_TAG", "1");
                input.put("BEAUTIFUAL_TAG", "1");
                 //CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        	}
        }
        
    }
}
