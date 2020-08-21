
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ChangeSvcRecondsQry
{	
	/**
	 * 
	 * TODO
	 * @author chenfeng9
	 * @date 2017年12月19日
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 * @return IDataset
	 */
    public static IDataset getRecondsInfo(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_CHANGE_SVC_RECONDS_BY_TYPE", params, pagination);
    }
    /**
     * 
     * TODO
     * @author chenfeng9
     * @date 2017年12月19日
     * @param params
     * @param pagination
     * @return
     * @throws Exception
     * @return IDataset
     */
    public static IDataset getAllRecondsInfo(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_OTHER", "SEL_CHANGE_SVC_RECONDS_NO_TYPE", params, pagination);
    }
}
