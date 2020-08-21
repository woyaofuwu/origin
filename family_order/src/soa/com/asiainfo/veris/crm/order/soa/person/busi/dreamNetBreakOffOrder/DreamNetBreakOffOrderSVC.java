package com.asiainfo.veris.crm.order.soa.person.busi.dreamNetBreakOffOrder;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.person.busi.interroam.airline.AirlinesWhiteUserBean;
 
public class DreamNetBreakOffOrderSVC  extends CSBizService{

    //梦网包月类业务暂停恢复新增订购查询
    public  IDataset dreamNetBreakOffOrder(IData input) throws Exception{
        
    	DreamNetBreakOffOrderBean bean = BeanManager.createBean(DreamNetBreakOffOrderBean.class);
    	IDataset idata= bean.dreamNetBreakOffOrder(input,getPagination()); 
        return idata; 
    }
    
    public  IData dreamNetImportOrder(IData input) throws Exception{
       
    	DreamNetBreakOffOrderBean bean = BeanManager.createBean(DreamNetBreakOffOrderBean.class);
    	IData idata = bean.batImportDreamNetList(input);
        return idata; 
    }
     
}
