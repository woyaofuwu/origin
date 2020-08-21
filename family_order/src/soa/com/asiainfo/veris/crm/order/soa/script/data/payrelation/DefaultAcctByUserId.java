
package com.asiainfo.veris.crm.order.soa.script.data.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class DefaultAcctByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(DefaultAcctByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DefaultAcctByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset listPayRelationByUserId = new DatasetList();

        IData inParam = new DataMap();
        inParam.put("USER_ID", databus.getString("USER_ID", ""));

        /* 参考ExistMultiDiscntPay */
        listPayRelationByUserId.add(Dao.qryByCode("TF_A_PAYRELATION", "SEL_DEFAULT_BY_USERID", inParam));

        databus.put("TF_F_USER_DISCNT_NORPAY_NOSELF", listPayRelationByUserId);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DefaultAcctByUserId() <<<<<<<<<<<<<<<<<<<");

    }
}
