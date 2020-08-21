
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class BankBean extends CSBizBean
{
   
	private static Logger logger = Logger.getLogger(BankBean.class);

	/**
	 * 查询绑定信息
	 * @author wukw3
	 * @param pd
	 * @param inparam
	 * @return resultInfo
	 * @throws Exception
	 */
	public IDataset getBankBindDatas(IData inparam) throws Exception {
		
		IDataset resultInfos = qryBankBindInfoList(inparam.getString("USER_ID",""));
		
		if(IDataUtil.isEmpty(resultInfos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "20141203001:该用户下没有绑定的银联信息！");
		}
		
		if(logger.isDebugEnabled()){
			logger.debug(resultInfos.toString());
		}
		
		return resultInfos;
	}
	
	/**
	 * 查询绑定信息
	 * @author wukw3
	 * @param pd
	 * @param inparam
	 * @return resultInfo
	 * @throws Exception
	 */
	public IData getNetBankBindDatas(IData inparam) throws Exception {
		
		IDataset resultInfos = qryBankBindInfoList(inparam.getString("USER_ID",""));
		
		IData BackInfo = new DataMap();
		
		if(IDataUtil.isEmpty(resultInfos)){
			BackInfo.put("X_RESULTCODE", "0001");
			BackInfo.put("RSPCODE", "0001");
	        BackInfo.put("X_RESULTINFO", "该手机号码未绑定 ");
		}
		else
		{
			IData Temp = resultInfos.getData(0);
			BackInfo.put("CARDNO", Temp.getString("RSRV_STR1"));
			BackInfo.put("BANKNAME", Temp.getString("RSRV_STR2"));
			BackInfo.put("PASSWD", Temp.getString("RSRV_STR3"));
			BackInfo.put("START_DATE", Temp.getString("START_DATE"));
			BackInfo.put("END_DATE", Temp.getString("END_DATE"));
			BackInfo.put("UPDATE_TIME", Temp.getString("UPDATE_TIME"));
			BackInfo.put("UPDATE_STAFF_ID", Temp.getString("UPDATE_STAFF_ID"));
			BackInfo.put("UPDATE_DEPART_ID", Temp.getString("UPDATE_DEPART_ID"));
			
			BackInfo.put("X_RESULTCODE", "0000");
			BackInfo.put("RSPCODE", "0000");
	        BackInfo.put("X_RESULTINFO", "OK");
			
		}
		
		if(logger.isDebugEnabled()){
			logger.debug(resultInfos.toString());
		}
		
		return BackInfo;
	}
	
	public static IDataset qryBankBindInfoList(String user_id) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", user_id);
        param.put("RSRV_VALUE_CODE", "BANKBIND");

        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_USERID", param);
    }
	
	public static IDataset qryBankBindInfo(IData inparam) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", inparam.getString("USER_ID",""));
        param.put("RSRV_VALUE_CODE", "BANKBIND");
        param.put("RSRV_STR1", inparam.getString("RSRV_STR1",""));

        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_OTHER_BANK", param);
    }
	
	public static IDataset qryBankBindInfo2(IData inparam) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", inparam.getString("USER_ID",""));
        param.put("RSRV_VALUE_CODE", "BANKBIND");
        
        return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_OTHER_BANK", param);
    }
}
