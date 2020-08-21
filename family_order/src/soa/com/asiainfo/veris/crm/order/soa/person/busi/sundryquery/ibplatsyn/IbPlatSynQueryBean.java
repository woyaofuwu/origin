
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ibplatsyn;

import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.IbPlaySynQry;

public class IbPlatSynQueryBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(IbPlatSynQueryBean.class);

    public IDataset queryIbPlatSynInfo(IData params, Pagination pagination) throws Exception
    {
        String msisdn = params.getString("SERIAL_NUMBER", "");
        String subscribeId = params.getString("SUBSCRIBE_ID", "");
        String operateDate = params.getString("OPERATE_DATE", "");
        if (StringUtils.isEmpty(msisdn))
        {

        }
        if (StringUtils.isEmpty(subscribeId))
        {

        }
        if (StringUtils.isEmpty(operateDate))
        {

        }
        IDataset results = IbPlaySynQry.queryIbPlatSyn(params, pagination);
        if (IDataUtil.isEmpty(results))
        {
            results = IbPlaySynQry.queryHisIbPlatSyn(params, pagination);
        }
        return results;
    }
}
