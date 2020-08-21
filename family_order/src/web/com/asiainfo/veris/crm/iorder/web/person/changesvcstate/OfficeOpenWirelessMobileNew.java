package com.asiainfo.veris.crm.iorder.web.person.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author chenchunni
 * @Package com.asiainfo.veris.crm.iorder.web.person.changesvcstate
 * @Description: TODO
 * @date 2019/8/15 10:08
 */
public abstract class OfficeOpenWirelessMobileNew extends PersonBasePage {
    public abstract void setInfo(IData info);

    /**
     * 页面初始化加载参数
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "3801"); // 默认打停机菜单
        String authType = data.getString("authType", "18");
        // 无线固话停开机类页面调用时会传TYPE_CODE参数，标识是从无线固话停开机页面调用
        String typeCode = getRequest().getParameter("TYPE_CODE");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        if (null == typeCode){
            typeCode = "";
        }
        info.put("typeCode",typeCode);
        setInfo(info);
    }

    /**
     * 提交后处理函数
     * @param requestCycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", ""))) {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}