
package com.asiainfo.veris.crm.order.soa.group.sendgift; 

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GiftMarkeQry
{
	
	/**
	 * 根据SERIAL_NUMBER查客户信息及集团信息
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 * @author chenhh6
	 */
    public static IDataset getCustAndGroupInfoBySn(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_CUST_GROUP_BY_SN", params, pagination);
    }
    
    /**
     * 保存礼品赠送记录
     * @param input
     * @throws Exception
     * @author chenhh6
     */
    public static IData saveSendGiftInfo(IData input) throws Exception
    {
    	IData map = new DataMap();
    	SQLParser sql = new SQLParser(null);
    	sql.addSQL("select SEQ_TF_F_SEND_GIFT.nextval from dual");
    	IDataset daset = Dao.qryByParse(sql);
    	for (int i = 0; i < daset.size(); i++) {
    		String xlh =daset.getData(0).getString("NEXTVAL");
    		input.put("INTER_ID", xlh);
		}
    	boolean result = Dao.insert("TF_F_SEND_GIFT", input);
        map.put("result", result);
        return map;
    }
    
    /**
     * 集团礼品营销活动的查询
     * @param inParam
     * @param pagen
     * @return
     * @throws Exception
     * @author chenhh6
     */
	public static IDataset querySendGiftInfo(IData inParam,Pagination pagen) throws Exception
	{
		return Dao.qryByCode("TF_F_SEND_GIFT", "SEL_SEND_GIFT_BY_DT", inParam, pagen);
	}
	
	/**
	 * 查询集团礼品信息
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 * @author chenhh6
	 */
    public static IDataset getGiftInfo(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_GIFTINFO_BY_CODE", params, pagination);
    }
}
