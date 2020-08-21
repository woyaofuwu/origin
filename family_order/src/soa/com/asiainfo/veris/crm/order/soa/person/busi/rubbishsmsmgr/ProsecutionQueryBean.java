
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProsecutionInfoQry;

public class ProsecutionQueryBean extends CSBizBean
{

    public IDataset queryProsecution(IData data, Pagination page) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER");
        String prosecuteeNum = data.getString("PROSECUTEE_NUM");
        String prosecutionWay = data.getString("PROSECUTION_WAY");
        String endDate = data.getString("END_DATE");
        String startDate = data.getString("START_DATE");
        if (StringUtils.isNotBlank(endDate))
        {
            endDate = endDate + SysDateMgr.END_DATE;
        }

        IDataset result = UserProsecutionInfoQry.queryProsecution(serialNumber, prosecuteeNum, prosecutionWay, startDate, endDate, page);

        return result;
    }
}
