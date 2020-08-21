package com.asiainfo.veris.crm.iorder.web.igroup.esop.acceptanceListReportSurvey;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AcceptanceListReportSurvey extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    /**
     * 专线勘察单清单报表查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySurveyList(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.querySurveyList", data, this.getPagination("olcomnav"));
        IDataset eomsInfos = output.getData();
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData clearList = (IData) object;
                String cityCode1 = StaticUtil.getStaticValue("EOP_CUST_CITY_CODE", clearList.getString("CITY_CODE"));
                clearList.put("CUST_CITY_CODE", cityCode1);
            }
        }
        setInfosCount(output.getDataCount());
        this.setInfos(eomsInfos);
        this.setCondition(data);
        this.setInfo(data);
    }

}
