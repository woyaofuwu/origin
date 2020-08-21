
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACGrayLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACGrayLimit.class);

    public void isGrayLimitElement(IData databus, boolean bTestSn, boolean bSwitchElement, String strElementTypeCode, String strElementKeyName, String strElementTypeName, IDataset listTradeElement) throws Exception
    {
        String strElementName = null;

        for (Iterator iter = listTradeElement.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();

            if (("5".equals(strElementTypeCode) || "0".equals(element.getString("MODIFY_TAG"))) && !bTestSn && bSwitchElement && this.isGrayLimitElement(strElementTypeCode, element.getString(strElementKeyName)))
            {
                // strElementName = BreQueryHelp.getNameByCode(strElementKeyName, element.getString(strElementKeyName));

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201188, "当前【" + strElementTypeName + "】目前未通过上线后验证，只能测试号码进行试商用。");
            }
        }
    }

    public boolean isGrayLimitElement(String strElementTypeCode, String strElementId) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_TYPE", strElementTypeCode);
        param.put("ELEMENT_VALUE", strElementId);
        param.put("X_CONN_DB_CODE", Route.CONN_CRM_CEN);

        return Dao.qryByRecordCount("TD_B_ELEMENTBIND_INFO", "SEL_ELEMENT_BIND_BY_TYPE_VALUE", param);
    }

    /**
     * 判断当前是否测试号码，业务，产品，服务，资费，平台业务，relation关系
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACGrayLimit() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        boolean bTestSN = false;

        boolean bSwitchProduct = true;
        boolean bSwitchDiscnt = true;
        boolean bSwitchSvc = true;
        boolean bSwitchRelation = true;
        boolean bSwitchPlatSvc = true;

        String strProductId = null;

        IData param = new DataMap();

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        param.clear();
        param.put("SERIAL_NUMBER", databus.getString("SERIAL_NUMBER"));
        param.put("X_CONN_DB_CODE", Route.CONN_CRM_CEN);

        bTestSN = Dao.qryByRecordCount("TD_B_ELEMENTBIND_INFO", "SEL_TEST_SERIAL_NUMBER_BIND", param);

        if (!bTestSN && this.isGrayLimitElement("4", databus.getString("TRADE_TYPE_CODE")))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201188, "当前业务类型目前未通过上线后验证，只能测试号码进行试商用。");
        }

        /* 获取各个element 是否需要走灰度的配置 */
        param.put("ELEMENT_TYPE", "Z");
        param.put("ELEMENT_VALUE", "SWITCH_DO_NOT_DEL");
        IDataset listSwitch = Dao.qryByCode("TD_B_ELEMENTBIND_INFO", "SEL_ELEMENT_BIND_BY_TYPE_VALUE", param, Route.CONN_CRM_CEN);

        if (listSwitch.size() > 0)
        {
            bSwitchProduct = "1".equals(listSwitch.getData(0).getString("RSRV_STR1")) ? true : false;
            bSwitchDiscnt = "1".equals(listSwitch.getData(0).getString("RSRV_STR2")) ? true : false;
            bSwitchSvc = "1".equals(listSwitch.getData(0).getString("RSRV_STR3")) ? true : false;
            bSwitchRelation = "1".equals(listSwitch.getData(0).getString("RSRV_STR4")) ? true : false;
            bSwitchPlatSvc = "1".equals(listSwitch.getData(0).getString("REMARK")) ? true : false;
        }

        /* 校验产品 */
        strProductId = "240".equals(databus.getString("TRADE_TYPE_CODE")) ? databus.getString("RSRV_STR1") : databus.getString("PRODUCT_ID");
        if (!bTestSN && bSwitchProduct && this.isGrayLimitElement("0", strProductId))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201188, "当前产品目前未通过上线后验证，只能测试号码进行试商用。");
        }

        /* 资费 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE_DISCNT");
        if (listTrade.size() > 0)
        {
            this.isGrayLimitElement(databus, bTestSN, bSwitchDiscnt, "1", "DISCNT_CODE", "优惠", listTrade);
        }

        /* 服务 */
        listTrade = databus.getDataset("TF_B_TRADE_SVC");
        if (listTrade.size() > 0)
        {
            this.isGrayLimitElement(databus, bTestSN, bSwitchSvc, "2", "SERVICE_ID", "服务", listTrade);
        }

        /* UU关系 */
        listTrade = databus.getDataset("TF_B_TRADE_RELATION");
        if (listTrade.size() > 0)
        {
            this.isGrayLimitElement(databus, bTestSN, bSwitchRelation, "3", "RELATION_TYPE_CODE", "用户关系", listTrade);
        }

        /* 平台服务 */
        listTrade = databus.getDataset("TF_B_TRADE_PLATSVC");
        if (listTrade.size() > 0)
        {
            this.isGrayLimitElement(databus, bTestSN, bSwitchPlatSvc, "5", "SERVICE_ID", "平台服务", listTrade);
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACGrayLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
