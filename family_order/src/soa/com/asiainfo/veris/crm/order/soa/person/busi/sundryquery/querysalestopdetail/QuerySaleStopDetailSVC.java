
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querysalestopdetail;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class QuerySaleStopDetailSVC extends CSBizService
{

    public IDataset init(IData input) throws Exception
    {
        IDataset result = SundryQry.initCityArea(true);
        if (IDataUtil.isNotEmpty(result))
        {
            return result;
        }
        else
        {
            IDataset result2 = new DatasetList();
            return result2;
        }
    }

    public IDataset queryDetailInfo(IData input) throws Exception
    {
    	QuerySaleStopDetailBean userBillList = (QuerySaleStopDetailBean) BeanManager.createBean(QuerySaleStopDetailBean.class);
        return userBillList.queryDetailInfo(input, getPagination());
    }

}
