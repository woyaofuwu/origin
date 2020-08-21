package com.asiainfo.veris.crm.iorder.web.igroup.esop.linestopandbackquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class LineStopAndBackQuery extends EopBasePage {

    public abstract void setPattrs(IDataset pattrs) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;

    public abstract void setFinishTitle(IData finishTitle) throws Exception;

    public void qryLineInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData param = new DataMap();
        String changeMode = data.getString("CHANGEMODE");
        String groupId = data.getString("cond_GROUP_ID");
        String serialNumber = data.getString("cond_SERIAL_NUMBER");
        String productNo = data.getString("cond_PRODUCTNO");
        if(StringUtils.isBlank(groupId) && StringUtils.isBlank(serialNumber) && StringUtils.isBlank(productNo)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "未输入查询条件！");
        }
        param.put("CHANGEMODE", changeMode);
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_NO", productNo);
        IDataOutput output = CSViewCall.callPage(this, "SS.GrpLineInfoQrySVC.qryLineInfoStopOrBack", param, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());
        IData finishTitle = new DataMap();
        if("stop".equals(changeMode)) {
            finishTitle.put("FINISH_TITLE", "停机时间");
        } else if("back".equals(changeMode)) {
            finishTitle.put("FINISH_TITLE", "开机时间");
        }
        setFinishTitle(finishTitle);

    }
}
