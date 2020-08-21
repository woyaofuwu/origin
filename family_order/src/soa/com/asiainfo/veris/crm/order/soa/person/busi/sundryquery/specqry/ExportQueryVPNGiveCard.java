
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.specqry;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class ExportQueryVPNGiveCard extends ExportTaskExecutor
{

    /**
     * 金额处理 分转化成元
     * 
     * @param str
     * @throws Exception
     */
    private String dataMoneyDeal(String str) throws Exception
    {
        String string = null;
        if (str == null || "".equals(str))
        {
            str = "0";
        }
        string = FeeUtils.Fen2Yuan(str);
        return string;
    }

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        // input.put(Route.CONN_CRM_CEN, );
        input.put("PARA_CODE1", input.getString("cond_PARA_CODE1", ""));
        input.put("PARA_CODE2", input.getString("cond_PARA_CODE2", "").replace("-", ""));
        input.put("PARA_CODE3", input.getString("cond_PARA_CODE3", ""));
        input.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        // 服务返回结果集
        IDataset result = new DatasetList();

        result = SundryQry.getVPNGiveCardInfo(input, pagination);

        if (!IDataUtil.isEmpty(result))
        {
            noChangeMoney(result);
        }
        // 设置页面返回数据

        return result;
    }

    /**
     * 累计未兑金额转换
     * 
     * @param cycle
     */
    public IDataset noChangeMoney(IDataset idataset) throws Exception
    {
        if (idataset == null)
        {
            return null;
        }
        for (int i = 0; i < idataset.size(); i++)
        {
            IData tdata = idataset.getData(i);
            if (tdata.getString("PARA_CODE7") == null || "".equals(tdata.getString("PARA_CODE7")))
            {
                tdata.put("PARA_CODE9", tdata.getString("PARA_CODE6"));
            }
            else
            {
                tdata.put("PARA_CODE9", Integer.valueOf(tdata.getString("PARA_CODE6")) - Integer.valueOf(tdata.getString("PARA_CODE7")));
            }
            tdata.put("PARA_CODE5", dataMoneyDeal(tdata.getString("PARA_CODE5")));
            tdata.put("PARA_CODE6", dataMoneyDeal(tdata.getString("PARA_CODE6")));
            tdata.put("PARA_CODE7", dataMoneyDeal(tdata.getString("PARA_CODE7")));
            tdata.put("PARA_CODE9", dataMoneyDeal(tdata.getString("PARA_CODE9")));
        }

        return idataset;
    }

}
