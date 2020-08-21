
package com.asiainfo.veris.crm.order.soa.script.data.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class DefaultAcctByAcctId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(DefaultAcctByAcctId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DefaultAcctByAcctId() >>>>>>>>>>>>>>>>>>");

        IDataset listPayRelationByAcctId = new DatasetList();

        IDataset listPayRelation = databus.getDataset("TF_A_PAYRELATION_DEFAULT_BY_USERID");

        IData inParam = new DataMap();

        int iCountPayRelation = listPayRelation.size();
        for (int idxPay = 0; idxPay < iCountPayRelation; idxPay++)
        {
            inParam.put("ACCT_ID", listPayRelation.get(idxPay, "ACCT_ID"));

            /* 参考ExistMultiDiscntPay */
            listPayRelationByAcctId.add(Dao.qryByCode("TF_A_PAYRELATION", "SEL_DEFAULT_BY_ACCTID", inParam));
        }

        databus.put("TF_F_USER_DISCNT_NORPAY_NOSELF", listPayRelationByAcctId);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DefaultAcctByAcctId() <<<<<<<<<<<<<<<<<<<");

    }

}
