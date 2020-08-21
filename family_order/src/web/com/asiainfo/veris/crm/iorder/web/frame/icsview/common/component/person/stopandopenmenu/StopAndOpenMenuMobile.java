package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.stopandopenmenu;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class StopAndOpenMenuMobile extends CSBizTempComponent {

    public abstract String getTypeCode();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        IDataset tradeTypeCodeList = new DatasetList();
        // typeCode 标识是从无线固话停开机类页面调用，还是其他页面调用。
         String typeCode = getTypeCode();
        // 如果是无线固话停开机类页面，那么只显示无线固话类的业务
        if("TD".equals(typeCode)){
            IData tradeTypeCodeItem6 = new DataMap();
            tradeTypeCodeItem6.put("TRADE_TYPE_CODE", "3801");
            tradeTypeCodeItem6.put("RIGHT_CODE", "crm9B41");
            tradeTypeCodeItem6.put("TRADE_TYPE_NAME", "无线固话报停（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem6);

            IData tradeTypeCodeItem7 = new DataMap();
            tradeTypeCodeItem7.put("TRADE_TYPE_CODE", "3802");
            tradeTypeCodeItem7.put("RIGHT_CODE", "crm9B42");
            tradeTypeCodeItem7.put("TRADE_TYPE_NAME", "无线固话报开（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem7);

            IData tradeTypeCodeItem11 = new DataMap();
            tradeTypeCodeItem11.put("TRADE_TYPE_CODE", "3808");
            tradeTypeCodeItem11.put("RIGHT_CODE", "crm9B43");
            tradeTypeCodeItem11.put("TRADE_TYPE_NAME", "局方无线固话停机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem11);

            IData tradeTypeCodeItem12 = new DataMap();
            tradeTypeCodeItem12.put("TRADE_TYPE_CODE", "3809");
            tradeTypeCodeItem12.put("RIGHT_CODE", "crm9B44");
            tradeTypeCodeItem12.put("TRADE_TYPE_NAME", "局方无线固话开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem12);
        }else{// 如果是停开机业务受理页面，那么显示全部停开机类的业务
            IData tradeTypeCodeItem2 = new DataMap();
            tradeTypeCodeItem2.put("TRADE_TYPE_CODE","131");
            tradeTypeCodeItem2.put("RIGHT_CODE", "crmN131");
            tradeTypeCodeItem2.put("TRADE_TYPE_NAME", "报停（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem2);

            IData tradeTypeCodeItem4 = new DataMap();
            tradeTypeCodeItem4.put("TRADE_TYPE_CODE", "133");
            tradeTypeCodeItem4.put("RIGHT_CODE", "crmN133");
            tradeTypeCodeItem4.put("TRADE_TYPE_NAME", "报开（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem4);

            IData tradeTypeCodeItem3 = new DataMap();
            tradeTypeCodeItem3.put("TRADE_TYPE_CODE", "132");
            tradeTypeCodeItem3.put("RIGHT_CODE", "crmN132");
            tradeTypeCodeItem3.put("TRADE_TYPE_NAME", "挂失（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem3);

            IData tradeTypeCodeItem5 = new DataMap();
            tradeTypeCodeItem5.put("TRADE_TYPE_CODE", "136");
            tradeTypeCodeItem5.put("RIGHT_CODE", "crmN136");
            tradeTypeCodeItem5.put("TRADE_TYPE_NAME", "局方停机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem5);

            IData tradeTypeCodeItem1 = new DataMap();
            tradeTypeCodeItem1.put("TRADE_TYPE_CODE", "126");
            tradeTypeCodeItem1.put("RIGHT_CODE", "crmN126");
            tradeTypeCodeItem1.put("TRADE_TYPE_NAME", "局方开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem1);

            IData tradeTypeCodeItem6 = new DataMap();
            tradeTypeCodeItem6.put("TRADE_TYPE_CODE", "3801");
            tradeTypeCodeItem6.put("RIGHT_CODE", "crmN801");
            tradeTypeCodeItem6.put("TRADE_TYPE_NAME", "无线固话报停（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem6);

            IData tradeTypeCodeItem7 = new DataMap();
            tradeTypeCodeItem7.put("TRADE_TYPE_CODE", "3802");
            tradeTypeCodeItem7.put("RIGHT_CODE", "crmN802");
            tradeTypeCodeItem7.put("TRADE_TYPE_NAME", "无线固话报开（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem7);

            IData tradeTypeCodeItem11 = new DataMap();
            tradeTypeCodeItem11.put("TRADE_TYPE_CODE", "3808");
            tradeTypeCodeItem11.put("RIGHT_CODE", "crmN801");
            tradeTypeCodeItem11.put("TRADE_TYPE_NAME", "局方无线固话停机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem11);

            IData tradeTypeCodeItem12 = new DataMap();
            tradeTypeCodeItem12.put("TRADE_TYPE_CODE", "3809");
            tradeTypeCodeItem12.put("RIGHT_CODE", "crmN802");
            tradeTypeCodeItem12.put("TRADE_TYPE_NAME", "局方无线固话开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem12);

            IData tradeTypeCodeItem8 = new DataMap();
            tradeTypeCodeItem8.put("TRADE_TYPE_CODE", "492");
            tradeTypeCodeItem8.put("RIGHT_CODE", "crmN492");
            tradeTypeCodeItem8.put("TRADE_TYPE_NAME", "大客户担保开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem8);

            IData tradeTypeCodeItem9 = new DataMap();
            tradeTypeCodeItem9.put("TRADE_TYPE_CODE", "496");
            tradeTypeCodeItem9.put("RIGHT_CODE", "crmN496");
            tradeTypeCodeItem9.put("TRADE_TYPE_NAME", "客户担保开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem9);

            IData tradeTypeCodeItem10 = new DataMap();
            tradeTypeCodeItem10.put("TRADE_TYPE_CODE", "497");
            tradeTypeCodeItem10.put("RIGHT_CODE", "crmN497");
            tradeTypeCodeItem10.put("TRADE_TYPE_NAME", "紧急开机（新）");
            tradeTypeCodeList.add(tradeTypeCodeItem10);
        }

        // 过滤客户权限
        for (int i = (tradeTypeCodeList.size() - 1); i >= 0; i--) {
            String rightCode = tradeTypeCodeList.getData(i).getString("RIGHT_CODE");
            String staffId = getVisit().getStaffId();// 获取客户工号
            boolean isStaffPriv = StaffPrivUtil.isPriv(staffId, rightCode, StaffPrivUtil.PRIV_TYPE_FUNCTION);
            if (!isStaffPriv) {
                tradeTypeCodeList.remove(i);
            }
        }

        IData inData = getRequestData();
        String tradeTypeCodeCurrent = inData.getString("TRADE_TYPE_CODE");
        setTradeTypeCodeCurrent(tradeTypeCodeCurrent);
        setTradeTypeCodeList(tradeTypeCodeList);
        setType_code(typeCode);

        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/stopandopenmenu/StopAndOpenMenuMobile.js");
    }

    public abstract String getTradeTypeCodeCurrent();

    public abstract void setTradeTypeCodeCurrent(String tradeTypeCodeCurrent);

    public abstract IDataset getTradeTypeCodeList();

    public abstract void setTradeTypeCodeList(IDataset tradeTypeCodeList);

    public abstract String getType_code();

    public abstract void setType_code(String type_code);

}
