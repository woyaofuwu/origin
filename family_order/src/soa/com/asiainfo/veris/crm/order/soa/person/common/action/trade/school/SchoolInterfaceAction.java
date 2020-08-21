
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.school;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInterfaceInfoQry;

public class SchoolInterfaceAction implements ITradeAction
{
    // 校园宽带业务需入接口表
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String tradeTypeCode = btd.getTradeTypeCode();
        IData param = new DataMap();
        // 欠停后当月开机
        if ("7309".equals(tradeTypeCode) || "633".equals(tradeTypeCode) || "641".equals(tradeTypeCode))
        {
            if ("7309".equals(tradeTypeCode) || "641".equals(tradeTypeCode))
            {
                tradeTypeCode = "7224";
            }
            else if ("633".equals(tradeTypeCode))
            {
                tradeTypeCode = "632";
            }

            String userId = btd.getMainTradeData().getUserId();
            String cancelTag = btd.getMainTradeData().getCancelTag();
            IDataset dataset = TradeInterfaceInfoQry.getBookTradeInterfaceInfoByUserId(tradeTypeCode, userId, cancelTag);
            if (IDataUtil.isNotEmpty(dataset))
            {
                String tradeId = dataset.getData(0).getString("TRADE_ID");
                param.clear();
                param.put("TRADE_ID", tradeId);
                param.put("CANCEL_TAG", cancelTag);
                Dao.executeUpdateByCodeCode("TI_BH_TRADE_INTERFACE", "INSERT_FROM_TRADE", param);
                Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "DEL_BY_TRADEID", param);
                return;
            }
        }
        MainTradeData tradeData = btd.getMainTradeData();
        param = tradeData.toData();
        param.put("TRADE_ID", btd.getRD().getTradeId());
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getRD().getTradeId()));
        param.put("ACCEPT_DATE", btd.getRD().getAcceptTime());
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("UPDATE_TIME", btd.getRD().getAcceptTime());
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        if ("632".equals(btd.getTradeTypeCode()) || "631".equals(btd.getTradeTypeCode()) || "7224".equals(btd.getTradeTypeCode()))
        {
            param.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth());
        }
        
        boolean needINS=true;
        IData pageData=btd.getRD().getPageRequestData();
        if(IDataUtil.isNotEmpty(pageData)&&"1".equals(pageData.getString("TEMP_OPEN"))){
        	needINS=false;
        }
        if(needINS){
        	Dao.executeUpdateByCodeCode("TI_B_TRADE_INTERFACE", "INS_INTERFACE", param);
        }
    }

}
