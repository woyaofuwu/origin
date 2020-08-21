package com.asiainfo.veris.crm.iorder.web.igroup.esop.feestatistics;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class FeeStatistics extends CSBasePage {


    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;

    public void qryInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData param = new DataMap();
        String minFee = data.getString("cond_MIN_FEE");
        String maxFee = data.getString("cond_MAX_FEE");
        param.put("MIN_FEE", minFee);
        param.put("MAX_FEE", maxFee);
        IDataOutput outData = CSViewCall.callPage(this, "CM.ConstractGroupSVC.qryLineInfobyFee", param, getPagination("navbar1"));
        IDataset list = outData.getData();
        long infoCount = outData.getDataCount();
        setInfos(list);
        setInfoCount(infoCount);

    }
}
