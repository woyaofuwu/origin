
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.telaudit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.NpAuditInfoQry;

public class NpAuditBean extends CSBizBean
{

    public IData getNpAuditInfo(IData param) throws Exception
    {
        String npsysid = param.getString("NPSYSID");

        IDataset ids = NpAuditInfoQry.qryNpAuditInfos(npsysid);
        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }
        else
        {
            return ids.getData(0);
        }
    }

    public IDataset getNpAuditInfos(IData param, Pagination page) throws Exception
    {
        String serviceType = param.getString("SERVICE_TYPE");
        String state = param.getString("STATE");
        String createTime = param.getString("CREATE_TIME");
        String npcodeList = param.getString("NPCODE_LIST");
        return NpAuditInfoQry.qryNpAuditInfos(serviceType, state, createTime, npcodeList, page);
    }

}
