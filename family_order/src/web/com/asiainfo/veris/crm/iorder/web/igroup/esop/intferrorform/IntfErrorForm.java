package com.asiainfo.veris.crm.iorder.web.igroup.esop.intferrorform;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizview.base.CSBasePage;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class IntfErrorForm extends CSBasePage {

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;
    
    public abstract void setCondition(IData condition) throws Exception;
    
    public abstract void setOrderInfos(IDataset orderInfos) throws Exception;

    public abstract void setOrderInfo(IData orderInfo) throws Exception;

    public abstract void setDetailCondition(IData detailCondition) throws Exception;

    public void qryInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String beginDate = data.getString("cond_BEGIN_DATE");
        String endDate = data.getString("cond_END_DATE");
        if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            if(SysDateMgr.compareTo(beginDate, endDate) > 0) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "开始时间不能大于结束时间！");
            }
        }

        IData param = new DataMap();
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        IDataset subTypeList = pageutil.getList("TD_S_STATIC", "DATA_ID", "DATA_NAME", "TYPE_ID", "EOP_SUB_TYPE_CODE");
        if(IDataUtil.isEmpty(subTypeList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到流程信息，请检查TD_S_STATIC[TYPE_ID=EOP_SUB_TYPE_CODE]表配置信息！");
        }
        IDataset qryList = new DatasetList();
        for (int i = 0; i < subTypeList.size(); i++) {
            IData subTypeData = subTypeList.getData(i);
            param.put("SUB_TYPE_CODE", subTypeData.getString("DATA_ID"));
            IDataset output = CSViewCall.call(this, "SS.EsopOrderQuerySVC.queryIntfErrorForm", param);
            if(IDataUtil.isNotEmpty(output)) {
                for (int j = 0; j < output.size(); j++) {
                    output.getData(j).put("SUB_TYPE", subTypeData.getString("DATA_NAME"));
                }
            }
            qryList.addAll(output);
        }
        setInfos(qryList);
        setCondition(param);
    }

    public void qryDetailInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String beginDate = data.getString("BEGIN_DATE");
        String endDate = data.getString("END_DATE");
        String subTypeCode = data.getString("SUB_TYPE_CODE");
        String queryType = data.getString("QUERY_TYPE");
        IData param = new DataMap();
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        param.put("SUB_TYPE_CODE", subTypeCode);
        param.put("QUERY_TYPE", queryType);
        String subType = StaticUtil.getStaticValue("EOP_SUB_TYPE_CODE", subTypeCode);
        IDataset subTypeList = CSViewCall.call(this, "SS.EsopOrderQuerySVC.queryDetailIntfErrorForm", param);
        if(IDataUtil.isNotEmpty(subTypeList)) {
            for (int i = 0; i < subTypeList.size(); i++) {
                IData subTypeData = subTypeList.getData(i);
                subTypeData.put("SUB_TYPE", subType);
            }
        }
        setOrderInfos(subTypeList);
        setDetailCondition(param);
    }
}
