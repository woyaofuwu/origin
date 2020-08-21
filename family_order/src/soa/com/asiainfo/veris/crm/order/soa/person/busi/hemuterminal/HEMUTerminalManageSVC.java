
package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class HEMUTerminalManageSVC extends OrderService
{
	protected static final Logger logger = Logger.getLogger(HEMUTerminalManageSVC.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* (non-Javadoc)
	 * @see com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService#getOrderTypeCode()
	 */
	@Override
	public String getOrderTypeCode() throws Exception {
		return "588";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		return "588";
	}
	/**
	 * 
	 * @Description：根据手机号码查询用户的和目终端信息
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-29上午09:45:49
	 */
	public IData getTerminalBySN(IData input) throws Exception {
		IData result=null;
		HEMUTerminalManageBean bean = (HEMUTerminalManageBean) BeanManager.createBean(HEMUTerminalManageBean.class);
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
        {
			CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
		String userId = userInfo.getString("USER_ID");
		IData para = new DataMap();
		para.put("USER_ID", userId);
		result = bean.getTerminalBySN(para);
		if(result!=null){
			result.put("SERIAL_NUMBER", serialNumber);
		}
		return result;
		
	}
	/**
	 * @Description：校验用户是否办理了和商务套餐
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-16上午11:16:46
	 */
	public IData checkHSWUserBySN(IData input) throws Exception {
		IData result=null;
		HEMUTerminalManageBean bean = (HEMUTerminalManageBean) BeanManager.createBean(HEMUTerminalManageBean.class);
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
        {
			CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
		String userId = userInfo.getString("USER_ID");
		result = bean.checkHSWUserByUserId(userId);
		if(result!=null){
			result.put("SERIAL_NUMBER", serialNumber);
		}
		return result;
		
	}
	
	public IData checkHEMUTerminal(IData input) throws Exception{
		
		HEMUTerminalManageBean bean = (HEMUTerminalManageBean) BeanManager.createBean(HEMUTerminalManageBean.class);
	 	IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        IData resNoInfo = bean.getResNoByOhter(resNo);
        if(resNoInfo!=null){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该串号终端["+resNo+"]已经在正常使用");	
        }
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        logger.info("和目终端查询返回结果"+retDataset.toString());
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
            IData res = retDataset.first();
            String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            retData.put("RES_ID", resNo); // 终端串号
            retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
            retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
            retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
            retData.put("SUPPLEMENT_TYPE", "0");
        }
        else
        {
        	 IData res = retDataset.first();
             retData.put("X_RESULTCODE", "1");
             retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
        }
        return retData;
		
	}
	/**
	 * 
	 * @Description：TODO 和目终端换机
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-1上午11:52:09
	 */
	public IData submit(IData input) throws Exception {
		IData result = new DataMap();
		HEMUTerminalManageBean bean = (HEMUTerminalManageBean) BeanManager.createBean(HEMUTerminalManageBean.class);
		String serialNumber = input.getString("SERIAL_NUMBER");
		String res_id = input.getString("RES_ID"); 
		IData assureUserData = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (assureUserData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber);
		}
		String cust_id = assureUserData.getString("CUST_ID");
		IData assureCustData = UcaInfoQry.qryCustInfoByCustId(cust_id);
		if (assureCustData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber);
		}
		IDataset dataset = CSAppCall.call("SS.HEMUTerminalManageSVC.tradeReg", input);
		result = dataset.first();
		return result;
	}
	/**
	 * 
	 * @Description：和目终端用户满三年退还押金
	 * @param:@param user
	 * @param:@throws Exception
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-27下午04:05:29
	 */
	public void returnDeposit(IData user) throws Exception
   {
		HEMUTerminalManageBean bean = (HEMUTerminalManageBean) BeanManager.createBean(HEMUTerminalManageBean.class);
		bean.returnDeposit(user);
   }
	public IData checkSerialNumber(IData input) throws Exception {
    	IData result = new DataMap();
    	result.put("RESULT_CODE", "0000");
    	result.put("RESULT_INFO", "正常用户");
    	String serialNumber = input.getString("SERIAL_NUMBER");
		IDataset userInfo = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if (IDataUtil.isEmpty(userInfo))
        {
        	 result.put("RESULT_CODE", "9998");
        	 result.put("RESULT_INFO", "查询不到正常的手机号码");
        	 IDataset userInfo2 = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);
        	 if (IDataUtil.isNotEmpty(userInfo2))
             {
            	 result.put("RESULT_CODE", "9998");
            	 result.put("RESULT_INFO", "该号码已销户");
            	 
             }
        }
		return result;
	}
}
