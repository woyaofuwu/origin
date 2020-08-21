
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckTime.java
 * @Description: 业务返销校验[时间]
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午2:46:59
 */
public class UndoCheckTime extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckTime.class);

    /**
     * 获取各个业务允许的返销天数
     * 
     * @param hisTrade
     * @throws Exception
     */
    private int getDayTradeCancel(IData hisTrade) throws Exception
    {
        // 获取配置的允许返销天数及权限
        int days = 1; // 默认允许返销天数

        String tradeTypeCode = hisTrade.getString("TRADE_TYPE_CODE");
        String eparchyCode = hisTrade.getString("EPARCHY_CODE");
        IDataset dataSet = CommparaInfoQry.getCommpara("CSM", "1022", tradeTypeCode, eparchyCode);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            String strParaCode3 = null;
            IData tempData = dataSet.getData(0);
            if (StringUtils.isNotEmpty(tempData.getString("PARA_CODE3")))
            {
                strParaCode3 = tempData.getString("PARA_CODE3");
            }
            else
            {
                strParaCode3 = "7";
            }

            if (StringUtils.isNotEmpty(tempData.getString("PARA_CODE1")))
            {
                days = Integer.parseInt(tempData.getString("PARA_CODE1"));
            }
            if (StringUtils.equals("240", tradeTypeCode))
            {
                String productId = hisTrade.getString("RSRV_STR1");
                IData productData = UProductInfoQry.qryProductByPK(productId);
                if (IDataUtil.isNotEmpty(productData))
                {
                    if (StringUtils.equals("YX03", productData.getString("RSRV_STR2")))
                    {
                        days = Integer.parseInt(strParaCode3);
                    }
                    // if (StringUtils.equals("TERMIANL_CANCEL_TRADE", databus.getString("TRADE_PAGE", "")))
                    // {
                    // // 增加定制机终端返销分支判断.
                    // sDays = "365";
                    // }
                }
            }
        }

        return days;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckTime() >>>>>>>>>>>>>>>>>>");

        IDataset dataSet = new DatasetList();
        IData param = new DataMap();

        IData tradeInfo = databus.getData("TRADE_INFO");

        int iTradeTypeCode = databus.getInt("TRADE_TYPE_CODE");
        String sEparchyCode = databus.getString("EPARCHY_CODE");
        String sAcceptDate = tradeInfo.getString("ACCEPT_DATE");
        String sCancelDate = databus.getString("UNDO_TIME");
        String sCancelStaffId = databus.getString("TRADE_STAFF_ID");
        String sRsrvStr4 = tradeInfo.getString("RSRV_STR4");

        int iDays = getDayTradeCancel(tradeInfo);

        IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(String.valueOf(iTradeTypeCode), sEparchyCode);
        String sBackTag = tradeTypeInfo.getString("BACK_TAG");
        Date acceptDate = SysDateMgr.string2Date(sAcceptDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        Date cancelDate = SysDateMgr.string2Date(sCancelDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        Date acceptDateMonth = SysDateMgr.string2Date(sAcceptDate, "yyyy-MM");
        Date cancelDateMonth = SysDateMgr.string2Date(sCancelDate, "yyyy-MM");
        if (StringUtils.equals("1", sBackTag))
        {
            String lastCancelDate = SysDateMgr.addDays(sAcceptDate, iDays);
            Date tempLastCancelDate = SysDateMgr.string2Date(lastCancelDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            Date lastCancelDateMonth = SysDateMgr.string2Date(lastCancelDate, "yyyy-MM");
            if (StringUtils.equals("A", sRsrvStr4))
            {
                if (!lastCancelDateMonth.equals(cancelDateMonth))
                {
                    String errInfo = "此类型业务[兑换海航金鹏里程]只允许当月返销！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751110, errInfo);
                    return true;
                }
            }
            else
            {
                if (cancelDate.after(tempLastCancelDate))
                {
                    StringBuilder errinfoBuilder = new StringBuilder(50);
                    errinfoBuilder.append("此类型业务只允许返销[ ");
                    errinfoBuilder.append(iDays);
                    errinfoBuilder.append(" ]天内的业务！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751111, errinfoBuilder.toString());
                    return true;
                }
            }
        }
        else if (sBackTag.equals("2"))
        {
            if (!acceptDate.equals(cancelDate))
            {
                String errInfo = "此类型业务只允许当日返销！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751112, errInfo);
                return true;
            }
        }
        else if (sBackTag.equals("3"))
        {
            if (!acceptDateMonth.equals(cancelDateMonth))
            {
                if (iTradeTypeCode == 240 || iTradeTypeCode == 630)
                {
                    dataSet.clear();
                    param.clear();
                    param.put("STAFF_ID", sCancelStaffId);
                    param.put("DATA_CODE", "SYS_CRM_BIMONTHCANCEL");
                    dataSet = Dao.qryByCode("TF_M_STAFFDATARIGHT", "SEL_STAFF_EXISTS_DATACODE", param);
                    if (IDataUtil.isEmpty(dataSet))
                    {
                        String errInfo = "此类型业务只允许当月返销！";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751113, errInfo);
                        return true;
                    }
                }
                else
                {
                    String errInfo = "此类型业务只允许当月返销！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751114, errInfo);
                    return true;
                }
            }
        }

        return false;
    }
}
