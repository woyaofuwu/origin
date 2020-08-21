package com.asiainfo.veris.crm.iorder.web.person.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * 无线固话停开机类（新）
 *
 * @author chencn
 */
public abstract class TDStopAndOpenMenu extends PersonBasePage {

    /**
     * 页面初始化加载参数
     *
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IDataset tradeTypeCodeList = new DatasetList();

        IData tradeTypeCodeItem1 = new DataMap();
        tradeTypeCodeItem1.put("TRADE_TYPE_CODE", "3801");  // 默认业务类型是无线固话报停
        tradeTypeCodeItem1.put("RIGHT_CODE", "crm9B41");
        tradeTypeCodeItem1.put("TRADE_TYPE_NAME", "无线固话报停");
        tradeTypeCodeList.add(tradeTypeCodeItem1);

        IData tradeTypeCodeItem2 = new DataMap();
        tradeTypeCodeItem2.put("TRADE_TYPE_CODE", "3802");
        tradeTypeCodeItem2.put("RIGHT_CODE", "crm9B42");
        tradeTypeCodeItem2.put("TRADE_TYPE_NAME", "无线固话报开");
        tradeTypeCodeList.add(tradeTypeCodeItem2);

        IData tradeTypeCodeItem3 = new DataMap();
        tradeTypeCodeItem3.put("TRADE_TYPE_CODE", "3808");
        tradeTypeCodeItem3.put("RIGHT_CODE", "crm9B43");
        tradeTypeCodeItem3.put("TRADE_TYPE_NAME", "无线固话局方停机");
        tradeTypeCodeList.add(tradeTypeCodeItem3);

        IData tradeTypeCodeItem4 = new DataMap();
        tradeTypeCodeItem4.put("TRADE_TYPE_CODE", "3809");
        tradeTypeCodeItem4.put("RIGHT_CODE", "crm9B44");
        tradeTypeCodeItem4.put("TRADE_TYPE_NAME", "无线固话局方开机");
        tradeTypeCodeList.add(tradeTypeCodeItem4);


        // 过滤客户权限
        for (int i = (tradeTypeCodeList.size() - 1); i >= 0; i--) {
            // 获取每个菜单的权限代码
            String rightCode = tradeTypeCodeList.getData(i).getString("RIGHT_CODE");
            // 获取客户工号
            String staffId = getVisit().getStaffId();
            // 判断用户是否有该菜单权限
            boolean isStaffPriv = StaffPrivUtil.isPriv(staffId, rightCode, StaffPrivUtil.PRIV_TYPE_FUNCTION);
            if (!isStaffPriv) {
                tradeTypeCodeList.remove(i);
            }
        }
        if (IDataUtil.isNotEmpty(tradeTypeCodeList)) {
            // 获取第一个栏目的业务类型code
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
