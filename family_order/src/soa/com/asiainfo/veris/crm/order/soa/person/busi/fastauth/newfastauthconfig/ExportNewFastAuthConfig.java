
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.newfastauthconfig;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：授权业务配置清单的导出 作者：GongGuang
 */
public class ExportNewFastAuthConfig extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("COND", true);
        data.put("COND_FASTAUTH_CONFIG_STATE", data.getString("COND_FASTAUTH_CONFIG_STATE"));
        data.put("MENU_ID", data.getString("MENU_ID"));
        IDataset res = CSAppCall.call("SS.NewFastAuthConfigSVC.queryAuthTradeType2", data);
        return res;
    }
}
