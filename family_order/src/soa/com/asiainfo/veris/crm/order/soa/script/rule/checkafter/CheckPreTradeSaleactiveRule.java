
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:预存营销 未完工和顺延最大时间判断！【TradeCheckAfter】
 * @author: xiaocl
 */

public class CheckPreTradeSaleactiveRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckPreTradeSaleactiveRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPreSaleactiveTradeByTime() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bResultOne = false;
        boolean bResultTwo = false;

        /* 获取业务台账，用户资料信息 */
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strUserId = databus.getString("USER_ID");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        IDataset listUserSaleActive = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        IDataset listPreTradeInfo = TradeInfoQry.getUserTradeByUserID(strTradeTypeCode, strUserId, "", "0", strEparchyCode, null);
        if (IDataUtil.isEmpty(listPreTradeInfo))
            return false;

        String strRsrvStr2 = listPreTradeInfo.getData(0).getString("RSRV_STR2");
        IData listData = UPackageExtInfoQry.queryPkgExtInfoByPackageId(strRsrvStr2);//PkgInfoQry.getPackageByPackage(strRsrvStr2, strEparchyCode);
        if (IDataUtil.isEmpty(listData))
            return false;
        String strTagSet = SaleActiveUtil.getPackageExtTagSet2(strRsrvStr2, IDataUtil.idToIds(listData));
        if (strTagSet.length() >= 3 && strTagSet.substring(2, 1) != "1")
        {
            IDataset listComparaInfo = UserSaleActiveInfoQry.queryMaxEndData(strUserId);
            for (Iterator iter = listUserSaleActive.iterator(); iter.hasNext();)
            {
                IData useSaleActive = (IData) iter.next();
                if (useSaleActive.getString("PROCESS_TAG").equals("0") || useSaleActive.getString("PROCESS_TAG").equals("4"))
                {
                    for (int iListComparaInfo = 0; iListComparaInfo < listComparaInfo.size(); iListComparaInfo++)
                    {
                        if (!listComparaInfo.getData(iListComparaInfo).getString("PARA_CDOE1").equals(useSaleActive.getString("PRODUCT_ID")))
                        {
                            bResultOne = true;
                        }
                    }
                }
            }
            if (bResultOne)
            {
                for (Iterator iter = listUserSaleActive.iterator(); iter.hasNext();)
                {
                    IData useSaleActive = (IData) iter.next();
                    if (useSaleActive.getString("PROCESS_TAG").equals("0") || useSaleActive.getString("PROCESS_TAG").equals("4"))
                    {
                        for (int iListComparaInfo = 0, iSize = listComparaInfo.size(); iListComparaInfo < iSize; iListComparaInfo++)
                        {
                            if (!listComparaInfo.getData(iListComparaInfo).getString("PARA_CDOE1").equals(useSaleActive.getString("PRODUCT_ID")))
                            {
                                bResultTwo = true;
                            }
                        }
                    }
                }
            }
            if (bResultTwo)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201182, "业务登记后条件判断:当前办理营销活动开始时间[" + databus.getString("RSRV_STR5") + "]小于等于用户最晚结束营销活动时间[" + listUserSaleActive.first().getString("END_DATE") + "]，业务不能继续！");
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPreTradeSaleactiveRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
