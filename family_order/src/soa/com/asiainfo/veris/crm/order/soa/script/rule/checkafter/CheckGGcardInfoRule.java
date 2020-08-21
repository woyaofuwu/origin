
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:刮刮卡号已被使用,不能再次使用办理此业务!【TradeCheckAfter】 废除：业务已经下线
 * @author: xiaocl
 */
public class CheckGGcardInfoRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckGGcardInfoRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGGcardInfoRule() >>>>>>>>>>>>>>>>>>");
        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strRsrvStr4 = databus.getString("RSRV_STR4");
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");
        /* 参数获取区域 */
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");
        /*
         * 外部查询相关信息获取区域
         */
        IData map = new DataMap();
        map.put("RSRV_STR4", strRsrvStr4);
        map.put("RSRV_VALUE_CODE", strRsrvValueCode);
        IDataset userTagInfo = Dao.qryByCode("TD_B_CPARAM", "SELCNT_BY_GGCARD_CARD", map);
        String strUseGgCard = "";
        if (IDataUtil.isNotEmpty(userTagInfo) && userTagInfo.size() == 1)
        {
            strUseGgCard = userTagInfo.getData(0).getString("TAG_CHAR");
        }
        if (strUseGgCard.equals("") || strUseGgCard.equals("0"))
        {
            for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
            {
                IData userOther = (IData) iter.next();
                if (userOther.getString("RSRV_VALUE_CODE").equals(strRsrvValueCode) && userOther.getString("RSRV_STR4").equals(strRsrvStr4))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckGGcardInfoRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
