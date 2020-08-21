package com.asiainfo.veris.crm.iorder.web.igroup.esop.timerTask;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class TimerTask extends EopBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void timerFullReviewTask(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataset workformInfos = CSViewCall.call(this, "SS.WorkFormSVC.queryTimerTaskWorkformInfos", data);
        String ibsysid = "";
        String ibsysidAll = "";

        if (DataUtils.isEmpty(workformInfos)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询业务流水号对应的工单无数据！");
        }
        for (Object object : workformInfos) {
            IData workformInfo = (IData) object;
            IData pattrInfos = CSViewCall.callone(this, "SS.WorkFormSVC.getTimerTaskWorkformNewAttr", workformInfo);

            IData paramData = ScrDataTrans.buildWorkformSvcParam(pattrInfos);

            IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", paramData);
            ibsysid = result.getData(0).getString("IBSYSID");
            ibsysidAll += ibsysid + ",";
        }

        IData resultMap = new DataMap();
        if (workformInfos.size() == 1) {
            resultMap.put("IBSYSID", ibsysid);
        } else {
            resultMap.put("IBSYSID", ibsysidAll);
        }
        setAjax(resultMap);
    }

}
