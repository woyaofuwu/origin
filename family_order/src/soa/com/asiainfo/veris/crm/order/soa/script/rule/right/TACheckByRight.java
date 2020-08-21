
package com.asiainfo.veris.crm.order.soa.script.rule.right;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class TACheckByRight extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACheckByRight.class);

    /**
     * 判断用户产品，包，服务，优惠权限
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACheckByRight() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strTradeStaffId = databus.getString("TRADE_STAFF_ID");
        String orderTypeCode = databus.getString("ORDER_TYPE_CODE", "");

        /* 开始逻辑规则校验 */
        if ("240".equals(strTradeTypeCode))
        {
            if (!"210".equals(orderTypeCode))
            {
                // 因历史原因，老系统的合约计划是没有配置包权限和产品权限的
                // 然后新系统将合约计划的业务类型融到了营销活动240业务中
                // 故这里要排除ORDER_TYPE_CODE=210的
                String strPackageId = databus.getString("RSRV_STR2");

                if (!StaffPrivUtil.isPkgPriv(strTradeStaffId, strPackageId))
                {
                    String strName = BreQueryHelp.getNameByCode("PackageName", strPackageId);

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "#业务登记后条件判断:对不起，您没有办理包【" + strPackageId + "|" + strName + "】的权限！");
                }
            }
            else
            {
                String productId = databus.getString("RSRV_STR1");
                // 根据合约ID获取对应的权限
                IData productInfo = UProductInfoQry.qryProductByPK(productId);
                if (IDataUtil.isEmpty(productInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据PRODUCT_ID[" + productId + "]找不到记录");
                }
                String privCode = productInfo.getString("RSRV_STR2");
                if (StringUtils.isNotBlank(privCode))
                {
                    if (!StaffPrivUtil.isFuncDataPriv(strTradeStaffId, privCode))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "#业务登记后条件判断:对不起，您没有办理合约【" + productId + "|" + productInfo.getString("PRODUCT_ID") + "】的权限！");
                    }
                }
            }
        }
        else
        {
            if ("110".equals(strTradeTypeCode))
            {
                if (!databus.getString("PRODUCT_ID").equals(databus.getString("RSRV_STR2")))
                {
                    String strProductId = databus.getString("PRODUCT_ID");

                    if (!StaffPrivUtil.isProdPriv(strTradeStaffId, strProductId))
                    {
                        String strName = BreQueryHelp.getNameByCode("ProductName", strProductId);

                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "#业务登记后条件判断:对不起，您没有办理产品【" + strProductId + "|" + strName + "】的权限！");
                    }
                }
            }

            /* 判断资费 */
            IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
            IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
            IDataset listElement = new DatasetList();
            BreQryForProduct.getAllElement(listElement, listTradeSvc, listTradeDiscnt);

            for (Iterator iter = listElement.iterator(); iter.hasNext();)
            {
                IData element = (IData) iter.next();

                String strModifyTag = element.getString("MODIFY_TAG");
                String strElementId = element.getString("ELEMENT_ID");
                String strELementTypeCode = element.getString("ELEMENT_TYPE_CODE");

                if (("0".equals(strModifyTag) || "1".equals(strModifyTag)) && !StaffPrivUtil.isPriv(strTradeStaffId, strElementId, strELementTypeCode))
                {
                    String strName = BreQueryHelp.getNameByCode("D".equals(strELementTypeCode) ? "DISCNT_CODE" : "SERVICE_ID", strElementId);
                    String strType = "D".equals(strELementTypeCode) ? "优惠" : "服务";

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "#业务登记后条件判断:对不起，您没有办理【" + strType + "|" + strElementId + "|" + strName + "】的权限！");
                }
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACheckByRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
