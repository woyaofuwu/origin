 
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 



public class SaleActiveEndActiveBean extends CSBizBean
{
	/**
     * 查询TL_B_USER_END_ACTIVE需要终止营销活动的信息
     */
    public static IDataset queryNeedEndActiveUser(IData inparams) throws Exception
    { 
    	IDataset infos=Dao.qryByCode("TL_B_USER_END_ACTIVE", "SEL_USER_NEED_END_ACTIVE", inparams); 
    	return infos;
    }
    
    /**
     * 更新表TL_B_USER_END_ACTIVE，将已经终止的记录打上标记。
     * */
	public static int updEndActiveUserTag(IData inParam) throws Exception
    { 
        int updResult=Dao.executeUpdateByCodeCode("TL_B_USER_END_ACTIVE", "UPD_USER_NEED_END_ACTIVE", inParam);
        return updResult;
    } 
}