/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.interboss.openchkacctrecqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @CREATED
 */
public class openChkAcctRecQryBean extends CSBizBean
{
 
    /**
	 * 
     * zhengdx 20180724 
     * 能力开放平台日对账查询
	 * */
	public static IDataset qryOpenChkAcctRecList(IData inparam,Pagination pagen) throws Exception
    {  
		IData param = new DataMap();

		param.put("RECON_DATE", inparam.getString("RECON_DATE"));
		param.put("RESULT_TYPE", inparam.getString("RESULT_TYPE"));
        return Dao.qryByCodeParser("TF_B_OPEN_BALANCE_RESULT", "SEL_BY_RECON_DATE", param, pagen);   
        
    }
	
	public static IDataset qryOpenChkAcctRecDayAll(IData inparam,Pagination pagen) throws Exception
    {  
		IData param = new DataMap();

		param.put("RECON_DATE", inparam.getString("RECON_DATE"));
		param.put("RESULT_TYPE", inparam.getString("RESULT_TYPE"));
        return Dao.qryByCodeParser("TF_B_OPEN_BALANCE_RESULT", "SEL_ALL_BY_RECON_DATE", param);   
        
    }
	
	
}
