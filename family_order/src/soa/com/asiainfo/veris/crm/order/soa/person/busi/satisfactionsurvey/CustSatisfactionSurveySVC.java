package com.asiainfo.veris.crm.order.soa.person.busi.satisfactionsurvey;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import org.apache.log4j.Logger;

/**
 * Created by zhaohj3 on 2018/12/24.
 */
public class CustSatisfactionSurveySVC extends CSBizService {
    private static transient Logger logger = Logger.getLogger(CustSatisfactionSurveySVC.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IDataset sendCustSatisfactionSurvey(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.sendCustSatisfactionSurvey(data);
    }

    public IDataset sendCustSatisfactionSurveyBroadBand(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.sendCustSatisfactionSurveyBroadBand(data);
    }

    public IDataset sendRequestCSSForOnline(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.sendRequestCSSForOnline(data);
    }

    public IDataset queryCSSDayRecon(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.queryCSSDayRecon(data);
    }

    public IDataset queryCSSDayList(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.queryCSSDayList(data);
    }

    public IDataset queryCSSDayListDetail(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.queryCSSDayListDetail(data, getPagination());
    }
    
    public IDataset sendGroupSpecialLine(IData data) throws Exception
    {
        CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
        return bean.sendGroupSpecialLine(data);
    }
}