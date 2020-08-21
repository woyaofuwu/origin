
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 预约优惠变更，请到预约产品变更界面办理此业务 老系统SQL规则翻译代码
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a WHERE a.trade_id=TO_NUMBER(:VTRADE_ID) AND
 * a.accept_month=TO_NUMBER(:VACCEPT_MONTH) AND a.modify_tag = '0' AND a.user_id = (SELECT user_id FROM tf_b_trade WHERE
 * trade_id = TO_NUMBER(:VTRADE_ID)) AND EXISTS (SELECT 1 FROM tf_b_trade c WHERE c.trade_id = TO_NUMBER(:VTRADE_ID) AND
 * SUBSTR(c.process_tag_set,19,1) = '0') AND EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code = 'CSM' AND
 * b.param_attr = 8859 AND b.param_code = 'PRODUCT' AND b.para_code1 = '0' AND TRIM(b.para_code2) = :VPRODUCT_ID AND
 * SYSDATE < b.end_date AND b.eparchy_code = :VEPARCHY_CODE) AND EXISTS (SELECT 1 FROM td_b_product_package
 * c,td_b_package_element d WHERE c.package_id = d.package_id AND c.product_id = :VPRODUCT_ID AND d.element_id =
 * a.discnt_code AND c.force_tag = '1' AND d.Force_Tag = '0')
 */
public class ExistInvalidDiscntTrade extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistInvalidDiscntTrade.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistInvalidDiscntTrade() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点 如果在COMPARA表配置了8559的产品，这类产品下的优惠变更属于预约优惠
        boolean bExistsTwo = false; // 设置第二逻辑点 如果在INFO_TAG字段的第19位为0，表示预约优惠变更。

        if (!databus.containsKey("TF_B_TRADE_DISCNT"))
        {
            return false;
        }
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strProductId = databus.getString("PRODUCT_ID");// 获取当前用户受理的优惠所属的产品
        IData map = new DataMap();
        // 确认此产品下的优惠是否在预约优惠范畴之内。
        IDataset ComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 8559, "PRODUCT", "0", strProductId, strEparchyCode);
        if (IDataUtil.isEmpty(ComparaInfo))
        {
            bExistsOne = false;
            return false;
        }
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        String strUserId = databus.getString("USER_ID");
        for (int iListTrade = 0, iSize = listTrade.size(); iListTrade < iSize; iListTrade++)
        {
            if (listTrade.getData(iListTrade).getString("PROCESS_TAG_SET").substring(18, 19).equals("0"))
            {
                bExistsTwo = true;
            }
        }
        // 存在预约优惠台账，且存在了预约优惠的产品，且这个优惠在产品模型中为非必选，但所在包为必选包
        if (bExistsOne && bExistsTwo)
        {
            IDataset productPackageInfo = ProductPkgInfoQry.getPackageByProductId(strProductId, strEparchyCode);
            if (IDataUtil.isEmpty(productPackageInfo))
            {
                return false;
            }
            IDataset listDiscntTrade = databus.getDataset("TF_B_TRADE_DISCNT");
            for (int iDt = 0, iASize = productPackageInfo.size(); iDt < iASize; iDt++)
            {
                for (int ii = 0, iBSize = listDiscntTrade.size(); ii < iBSize; ii++)
                {
                    IData discntTrade = listDiscntTrade.getData(ii);
                    if (discntTrade.getString("DISCNT_CODE").equals(productPackageInfo.getData(iDt).getString("ELEMENT_ID")) && discntTrade.getString("MODIFY_TAG").equals("0") && discntTrade.getString("USER_ID").equals(strUserId))
                    {
                        bResult = true;
                        break;
                    }
                }

                if (bResult)
                {
                    break;
                }

            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistInvalidDiscntTrade() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;

    }
}
