
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AcctDiscntQry
{
    /**
     * 根据ACCT_ID查询TF_F_ACCT_DISCNT表
     * 
     * @param acctId
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset getAcctDisInfoByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);

        return Dao.qryByCode("TF_F_ACCT_DISCNT", "SEL_BY_ACCT", param);
    }

    /**
     * 根据ACCT_ID查询TF_F_ACCT_DISCNT表
     * 
     * @param acctId
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset getAcctDisnctTradeByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);

        return Dao.qryByCode("TF_B_TRADE_ACCT_DISCNT", "SEL_CURRENTMONTHTRADE_BY_ACCT", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 根据ACCT_ID查询TF_F_ACCT_DISCNT表
     * @param acctId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getAcctDisInfoByAcctIdCode(String acctId,String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DISCNT_CODE", discntCode);
        return Dao.qryByCode("TF_F_ACCT_DISCNT", "SEL_BY_ACCTID_DISCNTCODE", param);
    }
    
}
