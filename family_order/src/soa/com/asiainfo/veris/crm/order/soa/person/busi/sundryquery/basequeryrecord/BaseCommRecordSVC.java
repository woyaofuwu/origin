
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class BaseCommRecordSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(BaseCommRecordSVC.class);

    /**
     * 协助请求归档
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData AssistRequestArchival(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestArchival(input);
    }

    public IDataset AssistRequestHasten(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestHasten(input);
    }

    public IDataset AssistRequestLogQurey(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestLogQurey(input, getPagination());
    }

    /**
     * 协助请求回复 接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset AssistRequestQurey(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestQurey(input);
    }

    /**
     * 协助请求回复
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData AssistRequestReply(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestReply(input);
    }

    /**
     * 协助请求派发 -
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData AssistRequestSend(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.AssistRequestSend(input);
    }

    public IDataset queryAcceptContent(IData input) throws Exception
    {
        String operaType = input.getString("OPERATE_CODE");

        return ParamInfoQry.qryAcceptContent(operaType);
    }

    @Override
    public void setTrans(IData input) throws Exception
    {
        super.setTrans(input);

        input.put("SERIAL_NUMBER", input.getString("CALLERNO"));
    }

    public IDataset subBussQureySerive(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subBussQureySerive(input);
    }

    public IDataset subClosePage(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subClosePage(input);
    }

    public IDataset subEparchyBussQureySerive(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);
        return queryRecordBean.subBussQureySerive(input);
    }

    public IDataset subEparchyClosePage(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subClosePage(input);
    }

    public IDataset subEparchyUnfinishBussQureySerive(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subUnfinishBussQureySerive(input);
    }

    public IDataset subSubmitProcess(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        IDataset dataset = new DatasetList();

        IData data = queryRecordBean.subSubmitProcess(input);

        dataset.add(data);

        return dataset;
    }

    public IDataset subUnfinishBussQureySerive(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subUnfinishBussQureySerive(input);
    }
    
    public IDataset subBussQureyAction(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subBussQureyAction(input);
    }
    
    public IDataset subUnfinishBussQureyAction(IData input) throws Exception
    {
        BaseQueryRecordBean queryRecordBean = BeanManager.createBean(BaseQueryRecordBean.class);

        return queryRecordBean.subUnfinishBussQureyAction(input);
    }
}
