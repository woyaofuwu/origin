
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForUser;

public class TACFamilyTradeByAttachSnDiscntLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACFamilyTradeByAttachSnDiscntLimit.class);

    /**
     * 判断符号码优惠限制
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACFamilyTradeByAttachSnDiscntLimit() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        int irlCount = 0;

        String strtpUserId = null, strSerialNumber = "";
        String strtpDiscntCode = null, strtpStartDate = null, strtpEndDate = null;
        String strueDiscntCode = null, strueStartDate = null, strueEndDate = null, strueElementTypeCode = "D";
        String strrlElementId = null, strrlElementTypeCode = null;

        String strUserDiscnt = null, strTradeDiscnt = null;

        IDataset listEvERelationLimit;
        IDataset listUserDiscnt = new DatasetList();
        IDataset listUser = null;

        IData param = new DataMap();

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        String strEparchyCode = databus.getString("EPARCHY_CODE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
        {
            IData tradediscnt = (IData) iter.next();

            if ("0".equals(tradediscnt.getString("MODIFY_TAG")))
            {
                strtpUserId = tradediscnt.getString("USER_ID");
                strtpDiscntCode = tradediscnt.getString("DISCNT_CODE");
                strtpStartDate = tradediscnt.getString("START_DATE");
                strtpEndDate = tradediscnt.getString("END_DATE");

                listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit("", strtpDiscntCode, "0", "D", "A", strEparchyCode, false);
                if (IDataUtil.isNotEmpty(listEvERelationLimit))
                {
                    irlCount = listEvERelationLimit.size();
                    listUserDiscnt = BreQryForUser.getUserDiscntByUserId(strtpUserId);

                    for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
                    {
                        IData userdiscnt = (IData) iterUserDiscnt.next();

                        strueDiscntCode = userdiscnt.getString("DISCNT_CODE");
                        strueStartDate = userdiscnt.getString("START_DATE");
                        strueEndDate = userdiscnt.getString("END_DATE");

                        for (Iterator iterElementLimit = listEvERelationLimit.iterator(); iterElementLimit.hasNext();)
                        {
                            IData listLimit = (IData) iterElementLimit.next();

                            strrlElementId = listLimit.getString("ELEMENT_ID_B");

                            if (strueElementTypeCode.equals(strrlElementTypeCode) && strueDiscntCode.equals(strrlElementId) && strueEndDate.compareTo(strueStartDate) > 0
                                    && ((strueEndDate.compareTo(strtpStartDate) >= 0 && strueStartDate.compareTo(strtpStartDate) <= 0)
                                            || (strueStartDate.compareTo(strtpStartDate) >= 0 && strueStartDate.compareTo(strtpEndDate) <= 0 && strueEndDate.compareTo(strtpStartDate) >= 0)))
                            {
                                param.put("USER_ID", strtpUserId);
                                listUser = Dao.qryByCode("TF_F_USER", "SEL_BY_USERID", param);

                                if (listUser.size() == 1)
                                {
                                    strSerialNumber = listUser.getData(0).getString("SERIAL_NUMBER");
                                }

                                strUserDiscnt = BreQueryHelp.getNameByCode("DISCNT_CODE", strueDiscntCode);
                                strTradeDiscnt = BreQueryHelp.getNameByCode("DISCNT_CODE", strtpDiscntCode);

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201123, "业务登记后条件判断:用户【" + strSerialNumber + "】现有的优惠【" + strUserDiscnt + "】与当前订购的优惠【" + strTradeDiscnt + "】互斥!");
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACFamilyTradeByAttachSnDiscntLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
