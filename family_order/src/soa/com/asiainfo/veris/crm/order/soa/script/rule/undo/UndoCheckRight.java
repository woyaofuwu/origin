
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckRight.java
 * @Description: 业务返销校验权限
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 上午11:13:23
 */
public class UndoCheckRight extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckFee.class);

    /**
     * 获取各个业务允许的返销权限
     * 
     * @param hisTrade
     * @throws Exception
     */
    private String getDayTradeRighCode(IData hisTrade) throws Exception
    {
        String sRightCode = "SYS004"; // 默认允许返销权限

        String tradeTypeCode = hisTrade.getString("TRADE_TYPE_CODE");
        String eparchyCode = hisTrade.getString("EPARCHY_CODE");
        IDataset dataSet = CommparaInfoQry.getCommpara("CSM", "1022", tradeTypeCode, eparchyCode);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            IData tempData = dataSet.getData(0);
            if (StringUtils.isNotEmpty(tempData.getString("PARA_CODE2")))
            {
                sRightCode = tempData.getString("PARA_CODE2");
            }
        }
        return sRightCode;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckRight() >>>>>>>>>>>>>>>>>>");

        IData param = new DataMap();

        IData tradeInfo = databus.getData("TRADE_INFO");

        String sTradeId = tradeInfo.getString("TRADE_ID");
        String sTradeStaffId = tradeInfo.getString("TRADE_STAFF_ID");
        String sTradeDepartId = tradeInfo.getString("TRADE_DEPART_ID");
        String sTradeCityCode = tradeInfo.getString("TRADE_CITY_CODE");
        String sTradeEparchyCode = tradeInfo.getString("TRADE_EPARCHY_CODE");
        String sCancelStaffId = databus.getString("TRADE_STAFF_ID");
        String sCancelDepartId = databus.getString("TRADE_DEPART_ID");
        String sCancelCityCode = databus.getString("TRADE_CITY_CODE");
        String sCancelEparchyCode = databus.getString("TRADE_EPARCHY_CODE");

        String sRightCode = getDayTradeRighCode(tradeInfo);

        // 1.标准权限校验
        IDataset dataSet = new DatasetList();
        param.clear();
        param.put("STAFF_ID", sCancelStaffId);
        param.put("DATA_CODE", sRightCode);

        // dataSet = Dao.qryByCode("TF_M_STAFFDATARIGHT", "SEL_STAFF_EXISTS_DATACODE", param);
        String sRightClass = StaffPrivUtil.getFieldPrivClass(sCancelStaffId, sRightCode);
        if (StringUtils.equals("0", sRightClass))
        {
            String errInfo = "您无权进行此类型业务的返销！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751101, errInfo);
            return true;
        }
        else if (StringUtils.equals("1", sRightClass))
        {
            if (!StringUtils.equals(sCancelStaffId, sTradeStaffId))
            {
                String errInfo = "您无权返销其它员工所做的业务！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751102, errInfo);
                return true;
            }
        }
        else if (StringUtils.equals("2", sRightClass))
        {
            if (!StringUtils.equals(sCancelDepartId, sTradeDepartId))
            {
                String errInfo = "您无权返销其它部门所做的业务！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751103, errInfo);
                return true;
            }
        }
        else if (StringUtils.equals("3", sRightClass))
        {
            if (!StringUtils.equals(sCancelCityCode, sTradeCityCode))
            {
                String errInfo = "您无权返销其它业务区所做的业务！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751104, errInfo);
                return true;
            }
        }
        else if (StringUtils.equals("4", sRightClass))
        {
            if (!StringUtils.equals(sCancelEparchyCode, sTradeEparchyCode))
            {
                String errInfo = "您无权返销其它地市所做的业务！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751105, errInfo);
                return true;
            }
        }
        // 2.特殊权限校验

        // 二维码返销权限
        dataSet.clear();
        param.clear();
        param.put("TRADE_ID", sTradeId);
        param.put("MODIFY_TAG", "0");
        param.put("GIFT_MODE", "4");
        dataSet = TradeSaleGoodsInfoQry.getTradeSaleGoods(sTradeId, "0");

        if (IDataUtil.isNotEmpty(dataSet))
        {
            IData tempData = dataSet.getData(0);
            if (StringUtils.equals("4", tempData.getString("GIFT_MODE")))
            {
                dataSet.clear();
                param.clear();
                param.put("STAFF_ID", sCancelStaffId);
                param.put("DATA_CODE", "SYS_CRM_SPEPUECHASECANCEL");
                // dataSet = Dao.qryByCode("TF_M_STAFFDATARIGHT", "SEL_STAFF_EXISTS_DATACODE", param);
                sRightClass = StaffPrivUtil.getFieldPrivClass(sCancelStaffId, "SYS_CRM_SPEPUECHASECANCEL");
                if (StringUtils.isEmpty(sRightClass))
                {
                    String errInfo = "您无权返销二维码相关业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751107, errInfo);
                    return true;
                }
            }
        }
        return false;
    }
}
