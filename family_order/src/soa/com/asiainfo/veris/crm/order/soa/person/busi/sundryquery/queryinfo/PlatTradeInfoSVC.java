
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;


import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PlatTradeInfoSVC extends CSBizService
{
    static final Logger logger = Logger.getLogger(PlatTradeInfoSVC.class);

    private static final long serialVersionUID = 1L;
    
    public IData queryUserInfoInft(IData input) throws Exception
    {
        PlatTradeInfoBean bean = (PlatTradeInfoBean) BeanManager.createBean(PlatTradeInfoBean.class);
        return bean.queryUserInfoInft(input);
    }
    
    public IData platTradeInft(IData input) throws Exception
    {
        PlatTradeInfoBean bean = (PlatTradeInfoBean) BeanManager.createBean(PlatTradeInfoBean.class);
        return bean.platTradeInft(input);
    }
    
    public IData replyTradeInft(IData input) throws Exception
    {
        PlatTradeInfoBean bean = (PlatTradeInfoBean) BeanManager.createBean(PlatTradeInfoBean.class);
        return bean.replyTradeInft(input);
    }
    
    public IDataset queryTradeInft(IData input) throws Exception
    {
        PlatTradeInfoBean bean = (PlatTradeInfoBean) BeanManager.createBean(PlatTradeInfoBean.class);
        return bean.queryTradeInft(input);
    }
}
