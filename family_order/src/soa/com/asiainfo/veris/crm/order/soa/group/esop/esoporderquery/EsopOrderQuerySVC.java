package com.asiainfo.veris.crm.order.soa.group.esop.esoporderquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class EsopOrderQuerySVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset queryWaitCheck(IData param) throws Exception {
        EsopOrderQueryBean esopOrderQueryBean = new EsopOrderQueryBean();
        return esopOrderQueryBean.queryWaitCheck(param, getPagination());
    }

    public IData smsSender(IData param) throws Exception {
        EsopOrderQueryBean esopOrderQueryBean = new EsopOrderQueryBean();
        return esopOrderQueryBean.smsSender2DealStaff(param);
    }

    /**
     * 归档情况查询
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryCheckinForm(IData param) throws Exception {
        return EsopOrderQueryBean.queryCheckinForm(param);
    }

    public IDataset showCheckinDetailTotalAll(IData param) throws Exception
    {
        return EsopOrderQueryBean.showCheckinDetailTotalAll(param);
    }

    public IDataset showCheckinDetailUncheckinAll(IData param) throws Exception {
        return EsopOrderQueryBean.showCheckinDetailUncheckinAll(param);
    }

    public IDataset showCheckinDetailCheckinAll(IData param) throws Exception {
        return EsopOrderQueryBean.showCheckinDetailCheckinAll(param);
    }

    public IDataset showCheckinDetailTotal(IData param) throws Exception {
        return EsopOrderQueryBean.showCheckinDetailTotal(param);
    }

    public IDataset showCheckinDetailUncheckin(IData param) throws Exception {
        return EsopOrderQueryBean.showCheckinDetailUncheckin(param);
    }

    public IDataset showCheckinDetailCheckin(IData param) throws Exception {
        return EsopOrderQueryBean.showCheckinDetailCheckin(param);
    }

    /**
     * 报错信息查询
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryIntfErrorForm(IData param) throws Exception {
        return EsopOrderQueryBean.queryIntfErrorFormInfo(param);
    }

    public IDataset queryDetailIntfErrorForm(IData param) throws Exception {
        String queryType = param.getString("QUERY_TYPE");
        if("TOTAL".equals(queryType)) {
            return EsopOrderQueryBean.queryDetailIntfErrorFormTotalInfo(param);
        } else if("ERR_COUNT".equals(queryType)) {
            return EsopOrderQueryBean.queryDetailIntfErrorFormInfo(param);
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未知查询类型！");
        }
        return null;
    }

    public IData chckeLineData(IData param) throws Exception {
        return EsopOrderQueryBean.chckeLineData(param);
    }
}
