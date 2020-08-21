package com.asiainfo.veris.crm.iorder.web.igroup.esop.datalineInformation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class BatchDatalineFile extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setGroupInfo(IData groupInfo);

    public void queryData(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        // IDataOutput output = CSViewCall.callPage(this, "SS.GroupPartnerInfoManagerSVC.queryData", data, getPagination("queryNav"));RECORD_NUM
        //
        // this.setInfos(output.getData());
        this.setCondition(data);
    }

    /**
     * 归档
     * 
     * @throws Exception
     */
    public void checkinWorkSheet(IRequestCycle cycle) throws Exception {
        IData info = this.getData();
        String ibsysid = info.getString("IBSYSID");
        String recordNum = info.getString("RECORD_NUM");// 页面入参获取
        String busiFromId = info.getString("BUSIFORM_ID");// 页面入参获取
        String groupId = info.getString("GROUP_ID");
        String productId = info.getString("PRODUCT_ID");
        String dealTypeAlls = info.getString("DEAL_TYPE");
        String[] dealTypeAll = dealTypeAlls.split(",");

        for (String dealType : dealTypeAll) {
            IData inData = new DataMap();
            inData.put("RECORD_NUM", recordNum);
            inData.put("IBSYSID", ibsysid);
            inData.put("BUSIFORM_ID", busiFromId);
            inData.put("GROUP_ID", groupId);
            inData.put("PRODUCT_ID", productId);
            inData.put("DEAL_TYPE", dealType);
            CSViewCall.call(this, "SS.WorkformEomscheckinSVC.checkin", inData);
        }
    }

}
