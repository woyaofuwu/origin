package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.changepsswd;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class SchoolWidenetPswChgNew extends PersonBasePage {

    public abstract void setQueryTypeSet(IDataset queryTypeSet);

    /**
     * 页面初始化
     *
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IDataset QueryTypeSet = new DatasetList();
        IData QueryTypeItem1 = new DataMap();
        QueryTypeItem1.put("val", "1");
        QueryTypeItem1.put("text", "修改密码");
        QueryTypeSet.add(QueryTypeItem1);
        IData QueryTypeItem2 = new DataMap();
        QueryTypeItem2.put("val", "3");
        QueryTypeItem2.put("text", "随机密码");
        QueryTypeSet.add(QueryTypeItem2);
        setQueryTypeSet(QueryTypeSet);
    }

    /**
     * 查询宽带资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        IData wideInfo = dataset.first();
        if (wideInfo == null) {
            CSViewException.apperr(WidenetException.CRM_WIDENET_1, data.getString("USER_ID"));
        } else {
            wideInfo.put("WIDE_TYPE", wideInfo.getString("RSRV_STR2"));
            setWideInfo(wideInfo);
        }
    }

    /**
     * 业务提交
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.WidenetPswChgRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setInfo(IData info);

    public abstract void setWideInfo(IData wideInfo);

}
