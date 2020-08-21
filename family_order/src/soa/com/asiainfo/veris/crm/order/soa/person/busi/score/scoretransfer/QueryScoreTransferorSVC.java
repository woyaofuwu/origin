package com.asiainfo.veris.crm.order.soa.person.busi.score.scoretransfer;




import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryScoreTransferorSVC extends CSBizService
{
    
    public IDataset queryTransferorInfo(IData input) throws Exception
    {
    	
    	QueryScoreTransferorBean queryScoreTransferorBean = (QueryScoreTransferorBean) BeanManager.createBean(QueryScoreTransferorBean.class);
        return queryScoreTransferorBean.queryTransferorInfo(input);
    }
    
    public IDataset queryTransferorHis(IData input) throws Exception
    {
    	
    	QueryScoreTransferorBean queryScoreTransferorBean = (QueryScoreTransferorBean) BeanManager.createBean(QueryScoreTransferorBean.class);
        return queryScoreTransferorBean.queryTransferorInfoHis(input);
    }

    
    public IDataset queryTransferorPoint(IData input) throws Exception
    {
    	
    	QueryScoreTransferorBean queryScoreTransferorBean = (QueryScoreTransferorBean) BeanManager.createBean(QueryScoreTransferorBean.class);
        return queryScoreTransferorBean.queryTransferPoint(input);
    }
    
    
    public IDataset TransferorPoint(IData input) throws Exception
    {
    	QueryScoreTransferorBean queryScoreTransferorBean = (QueryScoreTransferorBean) BeanManager.createBean(QueryScoreTransferorBean.class);
 

        return queryScoreTransferorBean.submitProcess(input);
    }
}
