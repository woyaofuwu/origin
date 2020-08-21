
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;

public class GroupPersonBookQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        IDataset dataset = new DatasetList();
        String sn = inParam.getString("cond_SERIAL_NUMBER");
        String queryType = inParam.getString("cond_QUERY_TYPE");

        if (queryType != null && queryType.equals("0"))
        {// 根据个人手机号码查询
            dataset = UserGrpMebPlatSvcInfoQry.getUserProductBySnUidPagination(sn, null, null, pg);
        }
        if (queryType != null && queryType.equals("1"))
        {// 根据集团端口(服务号码)查询
            dataset = UserGrpMebPlatSvcInfoQry.getUserProductBySnUidPagination(null, null, sn, pg);
        }
        if (queryType != null && queryType.equals("2"))
        {// 根据集团用户手机号码
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(sn);

            if (IDataUtil.isEmpty(userInfo))
                return dataset;
            String ecUserId = userInfo.getString("USER_ID");
            dataset = UserGrpMebPlatSvcInfoQry.getUserProductBySnUidPagination(null, ecUserId, null, pg);
        }

        return dataset;

    }
}
