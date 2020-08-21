
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class LimitbalckQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        AsynDealVisitUtil.dealVisitInfo(param);

        String queryway = param.getString("cond_LIMITBLACK_QUERY_WAY");

        IData data = new DataMap();
        IDataset sxinfos = new DatasetList();

        if ("0".equals(queryway))
        {
            // 根据手机号码查询
            data.put("SERIAL_NUMBER", param.getString("cond_SERIAL_NUMBER"));
        }

        else if ("1".equals(queryway))
        { // 根据挂账集团名称查询
            data.put("CUST_NAME", param.getString("cond_CUST_NAME"));
        }
        else if ("2".equals(queryway))
        { // 根据EC企业接入号
            data.put("BANK_ACCT_NO", param.getString("cond_BANK_ACCT_NO"));
        }

        // ---------------------- end ---------------------------------------
        if (queryway.equals("0") && !"".equals(param.getString("cond_SERIAL_NUMBER")))
        {
            IDataset info = CSAppCall.call("SS.BookTradeSVC.getAccountUniteBySN", data);
            for (int i = 0; i < info.size(); i++)
            {
                IData AUInfo = new DataMap();
                IData logData = info.getData(i);
                AUInfo.put("SERIAL_NUMBER", logData.getString("SERIAL_NUMBER", ""));
                AUInfo.put("BIZ_IN_CODE", logData.getString("BIZ_IN_CODE", ""));
                AUInfo.put("CUST_NAME", logData.getString("CUST_NAME", ""));
                AUInfo.put("ACCEPT_DATE", logData.getString("ACCEPT_DATE", ""));
                sxinfos.add(AUInfo);
            }

        }
        else if (queryway.equals("1") && !"".equals(param.getString("cond_CUST_NAME")))
        {
            IDataset custinfos = CSAppCall.call("SS.BookTradeSVC.getAccountUniteByCustName", data);

            for (int i = 0; i < custinfos.size(); i++)
            {
                IData custinfo = (IData) custinfos.get(i);
                IData AUInfo = new DataMap();
                AUInfo.put("SERIAL_NUMBER", custinfo.getString("SERIAL_NUMBER", ""));
                AUInfo.put("BIZ_IN_CODE", custinfo.getString("BIZ_IN_CODE", ""));
                AUInfo.put("CUST_NAME", custinfo.getString("CUST_NAME", ""));
                AUInfo.put("ACCEPT_DATE", custinfo.getString("ACCEPT_DATE", ""));
                sxinfos.add(AUInfo);
            }

        }
        else if (queryway.equals("2") && !"".equals(param.getString("cond_BANK_ACCT_NO")))
        {
            IDataset acctinfos = CSAppCall.call("SS.BookTradeSVC.getAccountUniteByBank", data);
            for (int i = 0; i < acctinfos.size(); i++)
            {
                IData custinfo = (IData) acctinfos.get(i);
                IData AUInfo = new DataMap();
                AUInfo.put("SERIAL_NUMBER", custinfo.getString("SERIAL_NUMBER", ""));
                AUInfo.put("BIZ_IN_CODE", custinfo.getString("BIZ_IN_CODE", ""));
                AUInfo.put("CUST_NAME", custinfo.getString("CUST_NAME", ""));
                AUInfo.put("ACCEPT_DATE", custinfo.getString("ACCEPT_DATE", ""));
                sxinfos.add(AUInfo);
            }
        }

        return sxinfos;
    }
}
