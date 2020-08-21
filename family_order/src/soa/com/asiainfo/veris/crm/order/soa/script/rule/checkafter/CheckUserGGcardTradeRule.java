
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:刮刮卡号已被使用,不能再次使用办理此业务!【TradeCheckAfter】 废除：业务已经下线
 * @author: xiaocl
 */
public class CheckUserGGcardTradeRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckUserGGcardTradeRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckUserGGcardTradeRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strRsrvStr3 = databus.getString("RSRV_STR3");
        String strRsrvStr4 = databus.getString("strRsrvStr4");
        String StrRsrvStr10 = databus.getString("RSRV_STR10");
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        /* 参数获取区域 */
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");
        /*
         * 外部查询相关信息获取区域
         */
        IData map = new DataMap();
        map.put("EPARCHY_CODE", strEparchyCode);
        map.put("TAG_CODE", "CS_CHR_USEGGCARD");
        map.put("SUBSYS_CODE", "CSM");
        map.put("USE_TAG", "0");
        IDataset userTagInfo = Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", map);
        String strUseGgCard = "";
        if (!strUseGgCard.equals("") || !strUseGgCard.equals("0"))
        {
            strUseGgCard = userTagInfo.getData(0).getString("TAG_CHAR");
        }
        int iCount = 0;
        int iCount1 = 0;
        StringBuilder strbError = new StringBuilder();
        if (strUseGgCard == "1")
        {
            for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
            {
                IData userOther = (IData) iter.next();
                if (userOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode))
                {
                    iCount++;
                }
            }
            if (iCount <= 0)
            {
                strbError.append("业务登记后条件判断:用户受理刮刮卡情况判断：用户未办理刮刮卡相关业务!");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201036, strbError.toString());
            }
            else if (iCount > 1)
            {
                strbError.append("业务登记后条件判断:用户受理刮刮卡情况判断:用户办理过多次刮刮卡相关业务!");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201037, strbError.toString());
            }
            else
            {
                if (listUserOther.getData(0).getString("RSRV_STR3").length() > 0)
                {
                    strbError.append("业务登记后条件判断:用户受理刮刮卡情况判断:用户已办理过刮刮卡兑换业务!");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201038, strbError.toString());

                    for (Iterator userOtheriter = listUserOther.iterator(); userOtheriter.hasNext();)
                    {
                        IData userOther = (IData) userOtheriter.next();
                        if (userOther.getString("RSRV_VALUE").equals(strRsrvStr3) && userOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode))
                        {
                            iCount1++;
                        }
                    }

                    if (iCount1 > 0)
                    {
                        if (listUserOther.getData(0).getString("RSRV_STR3").length() > 0)
                        {
                            strbError.append("业务登记后条件判断:用户受理刮刮卡情况判断:该刮刮卡已办理过兑换业务!");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201040, strbError.toString());
                        }
                    }
                }
            }
            if (StrRsrvStr10.equals("0"))
            {
                for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
                {
                    IData userOther = (IData) iter.next();
                    if (userOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode) && userOther.getString("RSRV_STR4").equals(strRsrvStr4))
                    {
                        strbError.append("业务登记后条件判断:用户受理刮刮卡情况判断:刮刮卡号已被使用,不能再次使用办理此业务!");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201040, strbError.toString());
                        break;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckUserGGcardTradeRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
