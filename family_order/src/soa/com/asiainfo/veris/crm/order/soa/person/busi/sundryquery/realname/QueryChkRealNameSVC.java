
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.realname;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryChkRealNameSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfo(IData data) throws Exception
    {
        QueryChkRealNameBean bean = (QueryChkRealNameBean) BeanManager.createBean(QueryChkRealNameBean.class);
        return bean.getUserRealNameInfo(data, getPagination());
    }
    
    
    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfoValid(IData data) throws Exception
    {
        QueryChkRealNameBean bean = (QueryChkRealNameBean) BeanManager.createBean(QueryChkRealNameBean.class);
        return bean.getUserRealNameInfoValid(data, getPagination());
    }
    
    
    
    /**
     * 功能：用户实名制信息查询 作者：GongGuang
     */
    public IDataset getUserRealNameInfoByUserId(IData data) throws Exception
    {
        QueryChkRealNameBean bean = (QueryChkRealNameBean) BeanManager.createBean(QueryChkRealNameBean.class);
        return bean.getUserRealNameInfoByUserId(data, getPagination());
    }
}
