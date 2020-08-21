package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class WidenetStopAndOpenMenu extends PersonBasePage {
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IDataset tradeTypeCodeList = new DatasetList();
        IData tradeTypeCodeItem1 = new DataMap();
        tradeTypeCodeItem1.put("TRADE_TYPE_CODE", "603");
        tradeTypeCodeItem1.put("RIGHT_CODE", "crmN603");
        tradeTypeCodeItem1.put("TRADE_TYPE_NAME", "宽带报停（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem1);
        IData tradeTypeCodeItem2 = new DataMap();
        tradeTypeCodeItem2.put("TRADE_TYPE_CODE", "604");
        tradeTypeCodeItem2.put("RIGHT_CODE", "crmN604");
        tradeTypeCodeItem2.put("TRADE_TYPE_NAME", "宽带报开（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem2);
        IData tradeTypeCodeItem3 = new DataMap();
        tradeTypeCodeItem3.put("TRADE_TYPE_CODE", "632");
        tradeTypeCodeItem3.put("RIGHT_CODE", "crmN632");
        tradeTypeCodeItem3.put("TRADE_TYPE_NAME", "校园宽带报停（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem3);
        IData tradeTypeCodeItem4 = new DataMap();
        tradeTypeCodeItem4.put("TRADE_TYPE_CODE", "633");
        tradeTypeCodeItem4.put("RIGHT_CODE", "crmN633");
        tradeTypeCodeItem4.put("TRADE_TYPE_NAME", "校园宽带报开（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem4);
        IData tradeTypeCodeItem5 = new DataMap();
        tradeTypeCodeItem5.put("TRADE_TYPE_CODE", "671");
        tradeTypeCodeItem5.put("RIGHT_CODE", "crmN671");
        tradeTypeCodeItem5.put("TRADE_TYPE_NAME", "宽带局方停机（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem5);
        IData tradeTypeCodeItem6 = new DataMap();
        tradeTypeCodeItem6.put("TRADE_TYPE_CODE", "672");
        tradeTypeCodeItem6.put("RIGHT_CODE", "crmN672");
        tradeTypeCodeItem6.put("TRADE_TYPE_NAME", "宽带局方开机（新）");
        tradeTypeCodeList.add(tradeTypeCodeItem6);
        // 过滤客户权限
        for (int i = (tradeTypeCodeList.size() - 1); i >= 0; i--) {
            String rightCode = tradeTypeCodeList.getData(i).getString("RIGHT_CODE");
            String staffId = getVisit().getStaffId();// 获取客户工号
            boolean isStaffPriv = StaffPrivUtil.isPriv(staffId, rightCode, StaffPrivUtil.PRIV_TYPE_FUNCTION);
            if (!isStaffPriv) {
                tradeTypeCodeList.remove(i);
            }
        }

        if (IDataUtil.isNotEmpty(tradeTypeCodeList)) {
            String tradeTypeCodeFirst = tradeTypeCodeList.first().getString("TRADE_TYPE_CODE");
            setTradeTypeCodeEmpty("full");
            setTradeTypeCodeFirst(tradeTypeCodeFirst);
        } else {
            setTradeTypeCodeEmpty("empty");
        }
    }

    public abstract String getTradeTypeCodeFirst();

    public abstract void setTradeTypeCodeFirst(String TradeTypeCodeFirst);

    public abstract String getTradeTypeCodeEmpty();

    public abstract void setTradeTypeCodeEmpty(String TradeTypeCodeEmpty);

}
