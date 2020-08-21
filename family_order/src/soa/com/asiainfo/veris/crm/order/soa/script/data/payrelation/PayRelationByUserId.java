
package com.asiainfo.veris.crm.order.soa.script.data.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class PayRelationByUserId extends BreBase implements IBREDataPrepare
{
    private static Logger logger = Logger.getLogger(PayRelationByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 PayRelationByUserId() >>>>>>>>>>>>>>>>>>");

        IDataset listPayRelationByUserId = new DatasetList();

        IData inParam = new DataMap();
        inParam.put("USER_ID", databus.getString("USER_ID"));

        listPayRelationByUserId = IDataUtil.idToIds(UcaInfoQry.qryDefaultPayRelaByUserId(inParam.getString("USER_ID")));

        // 欠费销号\立即销户的用户没有有效的付费关系，只能去查询最近的那条

        if (IDataUtil.isEmpty(listPayRelationByUserId))
        {
            String tradeTypeCode = databus.getString("TRADE_TYPE_CODE", "");
            // 查询最近的一条付费关系
            if (StringUtils.equals("310", tradeTypeCode) || StringUtils.equals("7230", tradeTypeCode) || StringUtils.equals("191", tradeTypeCode) || StringUtils.equals("290", tradeTypeCode))
            {
                IData lastPayRelation = UcaInfoQry.qryLastPayRelaByUserId(databus.getString("USER_ID"));
                if (null != lastPayRelation)
                {
                    listPayRelationByUserId.add(lastPayRelation);
                }
            }
        }

        databus.put("TF_A_PAYRELATION", listPayRelationByUserId);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 PayRelationByUserId() <<<<<<<<<<<<<<<<<<<");
    }
}
