
package com.asiainfo.veris.crm.order.soa.script.data.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class UserDiscntNoyPayUnSelf extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserDiscntNoyPayUnSelf.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserDiscntNoyPayUnSelf() >>>>>>>>>>>>>>>>>>");

        IDataset listUserDiscnt = new DatasetList();
        IData inParam = new DataMap();

        IDataset listPayRelation = databus.getDataset("TF_A_PAYRELATION_DEFAULT_BY_ACCTID");

        int iCountPayRelation = listPayRelation.size();
        for (int idxPay = 0; idxPay < iCountPayRelation; idxPay++)
        {
            inParam.put("USER_ID", listPayRelation.get(idxPay, "USER_ID"));

            listUserDiscnt.add(Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", inParam));
        }

        databus.put("TF_F_USER_DISCNT_NORPAY_NOSELF", listUserDiscnt);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserDiscntNoyPayUnSelf() <<<<<<<<<<<<<<<<<<<");

    }

}
