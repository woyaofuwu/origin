
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem;

import org.apache.log4j.Logger; 
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NoPhoneModemManageBean extends CSBizBean
{
    protected static Logger log = Logger.getLogger(NoPhoneModemManageBean.class);

    private static final long serialVersionUID = 1L;

    /**
	 * 判断是否无手机宽带 
	 * TF_B_TRADE TF_BH_TRADE
	 * 
	 * */
	public static IDataset checkIfNoPhoneUser(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("ACCOUNT_ID", inParam.getString("ACCOUNT_ID"));
        IDataset userModerms = Dao.qryByCode("TD_B_WIDENET_ACCOUNT", "SEL_WIDENET_ACCOUNT_BY_SN", param, Route.CONN_CRM_CEN);
        return userModerms;
    }
    
    /**
	 * 获取用户是否存在FTTH未完工宽带信息 
	 * TF_B_TRADE TF_BH_TRADE
	 * 
	 * */
	public static IDataset getUserTradeWWG(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        IDataset userModerms = Dao.qryByCode("TF_B_TRADE", "SEL_NOPHONE_TRADE_FTTH_BY_SN", param,Route.getJourDb());
        return userModerms;
    }
	
	/**
	 * 根据User_ID、Inst_id查询用户光猫信息TF_F_USER_OTHER
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryModermInfoByInstId(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("INST_ID", inParam.getString("INST_ID"));
        param.put("RSRV_STR1", inParam.getString("MODERM_ID"));
        param.put("RSRV_VALUE_CODE",inParam.getString("RSRV_VALUE_CODE"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_MODERM_BY_INSTID", param);
        return userModerms;
    }
}
