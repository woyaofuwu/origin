
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.usertradescore;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：积分兑换明细信息的导出 作者：GongGuang
 */
public class ExportQueryUserTradeScore extends ExportTaskExecutor
{
    protected static Logger log = Logger.getLogger(ExportQueryUserTradeScore.class);

    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {

        IData data = paramIData.subData("cond", true);
        getVisit().setStaffId(data.getString("STAFF_ID", "SUPERUSR"));// 设置登陆员工,因为导出需要校验权限
        IDataset res = CSAppCall.call("SS.QueryUserTradeScoreSVC.queryUserTradeScore", data);
        return res;
    }
}
