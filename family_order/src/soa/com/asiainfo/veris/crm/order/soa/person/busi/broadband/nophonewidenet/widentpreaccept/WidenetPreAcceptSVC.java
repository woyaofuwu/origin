
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widentpreaccept;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WidenetPreAcceptSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IDataset loadInfo(IData inparams) throws Exception
    { 
    	WidenetPreAcceptBean bean = BeanManager.createBean(WidenetPreAcceptBean.class);
    	IDataset infos = bean.loadInfo(inparams, this.getPagination());
    	return infos;
    }
    
    public IDataset loadWidenetInfo(IData inparams) throws Exception
    { 
    	WidenetPreAcceptBean bean = BeanManager.createBean(WidenetPreAcceptBean.class);
    	IDataset infos = bean.loadWidenetInfo(inparams);
    	return infos;
    }
    
    /**
     * 撤单
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset cancelPreTrade(IData inparams) throws Exception
    { 
    	WidenetPreAcceptBean bean = BeanManager.createBean(WidenetPreAcceptBean.class);
    	IDataset infos = bean.cancelPreTrade(inparams, this.getPagination());
    	return infos;
    }
    
    /**
     * 新增外呼记录
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset updatePreTrade(IData inparams) throws Exception
    { 
    	WidenetPreAcceptBean bean = BeanManager.createBean(WidenetPreAcceptBean.class);
    	IDataset infos = bean.updatePreTrade(inparams, this.getPagination());
    	return infos;
    }
    
    /**
     * 记录同步预受理信息，提供给北京使用
     */
    public IData syncPreTrade(IData inparams) throws Exception
    {
    	WidenetPreAcceptBean bean = BeanManager.createBean(WidenetPreAcceptBean.class);
    	IData infos = bean.syncPreTrade(inparams);
    	return infos;
    }
}
