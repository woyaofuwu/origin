package com.asiainfo.veris.crm.order.soa.person.busi.openAccountInfoQuery;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
 
public class OpenAccountInfoQuerySVC  extends CSBizService{

    //查询已有记录
    public  IDataset queryOpenAccountInfo(IData input) throws Exception{
        
    	OpenAccountInfoQueryBean bean = BeanManager.createBean(OpenAccountInfoQueryBean.class);
    	IDataset idata= bean.queryOpenAccountInfo(input,getPagination()); 
        return idata; 
    }
    //调用能开查询渠道联系人
    public  IDataset qryChnlInfo(IData input) throws Exception{

        OpenAccountInfoQueryBean bean = BeanManager.createBean(OpenAccountInfoQueryBean.class);
        IDataset idatas= bean.qryChnlInfo(input);
        return idatas;
    }
    	 
}
