
package com.asiainfo.veris.crm.order.soa.script.data.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: AccountByAcctId.java
 * @Description: 根据acctId获取账户信息
 * @version: v1.0.0
 * @author: liuke
 * @date: 上午10:37:48 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-8-23 liuke v1.0.0 修改原因
 */
public class AccountByAcctId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(Logger.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 AccountByAcctId() >>>>>>>>>>>>>>>>>>");
        if (databus.containsKey("UCADATA"))
        {
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            AccountTradeData list = ucaData.getAccount();
            databus.put("TF_F_ACCOUNT", list);
        }
        else
        {
            IData listAccountByAcctId = UcaInfoQry.qryAcctInfoByAcctId(databus.getString("ACCT_ID"));
            databus.put("TF_F_ACCOUNT", IDataUtil.idToIds(listAccountByAcctId));
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 AccountByAcctId() <<<<<<<<<<<<<<<<<<<");
    }

}
