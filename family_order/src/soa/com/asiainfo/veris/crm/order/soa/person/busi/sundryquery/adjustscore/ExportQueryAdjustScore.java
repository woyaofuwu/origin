
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.adjustscore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportQueryAdjustScore extends CSExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination paramPagination) throws Exception
    {
        super.transImpexpData(param);
        QueryAdjustScoreBean bean = (QueryAdjustScoreBean) BeanManager.createBean(QueryAdjustScoreBean.class);
        return bean.queryAdjustScore(param, param.getString("TRADE_EPARCHY_CODE"));
    }

}
