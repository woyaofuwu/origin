
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryNetChooseUserSVC extends CSBizService
{
    static transient final Logger logger = Logger.getLogger(QueryNetChooseUserSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset queryNetChooseUserInfo(IData input) throws Exception
    {
        QueryNetChooseUserBean result = (QueryNetChooseUserBean) BeanManager.createBean(QueryNetChooseUserBean.class);

        IDataset infos = result.queryNetChooseUserInfo(input, getPagination());

        return infos;
    }

}
