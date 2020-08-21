package com.asiainfo.veris.crm.order.soa.person.busi.score.scoretransfer;




import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryScoreAssigneeSVC extends CSBizService
{
    
    public IDataset queryAssigneeInfo(IData input) throws Exception
    {
    	
    	QueryScoreAssigneeBean queryScoreAssigneeBean = (QueryScoreAssigneeBean) BeanManager.createBean(QueryScoreAssigneeBean.class);
        return queryScoreAssigneeBean.queryAssigneeInfo(input);
    }
    
    public IDataset updateAssigneeInfo(IData input) throws Exception
    {
    	QueryScoreAssigneeBean queryScoreAssigneeBean = (QueryScoreAssigneeBean) BeanManager.createBean(QueryScoreAssigneeBean.class);
        return queryScoreAssigneeBean.ModifyAssigneeInfo(input);
    }
}
