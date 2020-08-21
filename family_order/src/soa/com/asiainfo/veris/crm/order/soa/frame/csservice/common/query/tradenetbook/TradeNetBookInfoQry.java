
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tradenetbook;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeNetBookInfoQry
{

    /**
     * 获取电子工单内容
     * 
     * @param pd
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getEFormInfoByTradeId(String trade_id) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", trade_id);
        IDataset b_trade = Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS", iParam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return b_trade;
    }

    /**
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBookInfoForInf(IData param) throws Exception
    {

        IDataset list = Dao.qryByCode("TF_B_TRADEBOOK", "SQL_BY_BOOKID_2", param, Route.CONN_CRM_CEN);

        return list;
    }
    
    
    /**
     * 获取台账表的数据
     * 
     * @param pd
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IData getInfoByTradeId(String trade_id) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", trade_id);
        IDataset b_trade = Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADEID", iParam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (b_trade.size()>0)
        	return b_trade.getData(0);
        return null;
    }
    

    public static IDataset queryCnotePrintTag(IData params) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_CNOTE_PRINT_TAG", "SEL_CNOTE_PRINT_TAG", params,Route.CONN_CRM_CG);
    }
    
    public static IDataset queryCnotePrintInfo(IData params) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_CNOTE_PRINT_TAG", "SEL_CNOTE_PRINT_INFO", params,Route.CONN_CRM_CG);
    }
    
    /**
	 * 搬迁工单表
	 *
	 * @param tradeInfo
	 * @throws Exception
	 */
	public static void moveBhTrade(IData tradeInfo) throws Exception {
		if (IDataUtil.isNotEmpty(tradeInfo)) {
			tradeInfo.put("PRINT_TAG", "1");
			Dao.insert("TF_BH_CNOTE_PRINT_TAG", tradeInfo, Route.CONN_CRM_CG);
			String[] keys = new String[] {"TRADE_ID"};
			Dao.delete("TF_B_CNOTE_PRINT_TAG", tradeInfo, keys, Route.CONN_CRM_CG);
		}
	}
}
