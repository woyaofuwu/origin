package com.asiainfo.veris.crm.iorder.web.igroup.esop.querylinepayralation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class QueryLinePayalation extends CSBasePage {

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfoCount(long infoCount);

    public void qryInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String acctId = data.getString("cond_ACCT_ID");
        String snA = data.getString("cond_SERIAL_NUMBER_A");
        String groupId = data.getString("cond_GROUP_ID");
        String sn = data.getString("cond_SERIAL_NUMBER");
        String productNo = data.getString("cond_PRODUCT_NO");
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("SERIAL_NUMBER_A", snA);
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", sn);
        param.put("PRODUCT_NO", productNo);
        IDataOutput output = CSViewCall.callPage(this, "SS.GrpLineInfoQrySVC.queryLinePayrelation", param, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setInfos(output.getData());
    }

}
