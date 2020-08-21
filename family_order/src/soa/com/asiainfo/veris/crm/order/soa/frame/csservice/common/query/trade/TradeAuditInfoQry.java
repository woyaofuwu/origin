
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.TradeAuditException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class TradeAuditInfoQry extends CSBizBean
{

    public static IData queryAuditBathInfo(IData inparams) throws Exception
    {
        IData auditBatchInfo = new DataMap();
        IDataset results = new DatasetList();

        results = Dao.qryByCode("TF_B_TRADE_AUDIT", "SEL_FOR_AUDIT_BATCH_NO", inparams);
        if (results.size() > 0 && results != null)
        {
            auditBatchInfo = (IData) results.get(0);

            if (!"".equals(auditBatchInfo.getString("BATCH_NO_B", "")) && !"".equals(auditBatchInfo.getString("AUDITED_NO_B", "")))
            {
                if (auditBatchInfo.getInt("AUDITED_NO_B") > 99)
                {
                    results = Dao.qryByCode("TF_B_TRADE_AUDIT", "SEL_FOR_AUDIT_BATCH_NO_2", inparams);
                    auditBatchInfo = (IData) results.get(0);
                }
            }
            else
            {
                auditBatchInfo.put("BATCH_NO_B", "00001");
                auditBatchInfo.put("AUDITED_NO_B", "0");
                auditBatchInfo.put("BATCH_NO_A", "00001");
                auditBatchInfo.put("AUDITED_NO_A", "001");

            }
        }
        else
        {

            CSAppException.apperr(TradeAuditException.CRM_TRADEAUDIT_1);
        }
        return auditBatchInfo;
    }

    public static IDataset queryAuditInfo(IData inparams) throws Exception
    {
        IDataset result = new DatasetList();
        IDataset auditInfos = new DatasetList();
        result = Dao.qryByCode("TF_BH_TRADE", "SEL_FOR_AUDIT", inparams);
        if (result != null)
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData tmpTradeInfo = new DataMap();
                IData tmpUserInfo = new DataMap();
                IDataset tmpUserInfos = new DatasetList();
                tmpTradeInfo = (IData) result.get(i);
                tmpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserId(tmpTradeInfo.getString("USER_ID", ""));

                tmpTradeInfo.put("OPEN_DATE", tmpUserInfo.getString("OPEN_DATE", ""));
                auditInfos.add(tmpTradeInfo);
            }
        }
        return auditInfos;
    }

    public static IDataset queryAuditInfo(IData inparams, Pagination pagination) throws Exception
    {

        IDataset auditInfos = new DatasetList();

        auditInfos = Dao.qryByCode("TF_B_TRADE_AUDIT", "SEL_BY_AUDIT", inparams, pagination);

        return auditInfos;
    }
}
