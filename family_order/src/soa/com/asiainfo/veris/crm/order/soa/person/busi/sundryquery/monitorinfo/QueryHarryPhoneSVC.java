
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryHarryPhoneQry;

public class QueryHarryPhoneSVC extends CSBizService
{
    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryHarryPhones(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        // 处理查询条件
        String queryType = input.getString("QUERY_TYPE");
        if (PersonConst.QUERY_TYPE_NORMAL.equals(queryType))
        {
            String paraCode1 = "86" + input.getString("BEGIN_SN");
            String paraCode2 = "86" + input.getString("END_SN");
            SpecialBizQryDealInput.phoneOfCondCheck(input.getDouble("BEGIN_SN"), input.getDouble("END_SN"));
            result = QueryHarryPhoneQry.getHarryPhonesByNormal(paraCode1, paraCode2, getPagination());
        }
        else if (PersonConst.QUERY_TYPE_REPORT.equals(queryType))
        {
            String paraCode1 = input.getString("QUERY_DATE") + SysDateMgr.START_DATE_FOREVER;
            String paraCode2 = input.getString("QUERY_DATE") + SysDateMgr.END_DATE;
            String paraCode3 = input.getString("VIOLATION_REASON");
            String paraCode4 = input.getString("DEAL_RESULT");
            result = QueryHarryPhoneQry.getDayReportHarryPhones(paraCode1, paraCode2, paraCode3, paraCode4, getPagination());
        }

        return result;
    }
}
