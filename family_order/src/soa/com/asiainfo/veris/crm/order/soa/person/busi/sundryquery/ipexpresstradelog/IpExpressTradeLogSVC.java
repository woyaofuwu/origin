
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ipexpresstradelog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IpExpressTradeLogSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * IP直通车工作记录查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryIPExpressLog(IData data) throws Exception
    {
        IpExpressTradeLogBean bean = (IpExpressTradeLogBean) BeanManager.createBean(IpExpressTradeLogBean.class);
        String queryMode = data.getString("QUERY_MODE", "");
        if ("2".equals(queryMode))
        {
            return bean.queryIPExpressLogByStaffId(data, getPagination());// '2','按员工工号查询'
        }
        else if ("1".equals(queryMode))
        {
            return bean.queryIPExpressLogByFixedNo(data, getPagination());// '1','按固定号码查询'
        }
        else
        {
            return bean.queryIPExpressLogByMobileNo(data, getPagination());// '0','按手机号码查询
        }

    }
}
