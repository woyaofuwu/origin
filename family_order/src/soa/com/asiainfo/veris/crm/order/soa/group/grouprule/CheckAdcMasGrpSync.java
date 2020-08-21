
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;

public class CheckAdcMasGrpSync extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckAdcMasGrpSync.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入规则 CheckAdcMasGrpSync() >>>>>>>>>>>>>>>>>>");

        String resultStr = "";

        String custId = databus.getString("CUST_ID", "");
        String grpProductId = databus.getString("PRODUCT_ID", "");

        // 0.判断集团客户资料是否同步
        resultStr = runAdcMasGrpSync(custId, grpProductId);
        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408290001", resultStr);
            return true;
        }

        return false;
    }

    /**
     * 判断集团客户资料是否同步
     * 
     * @param custId
     * @param busiType
     * @param productId
     * @return
     * @throws Exception
     */
    public static String runAdcMasGrpSync(String custId, String productId) throws Exception
    {
        String retStr = ""; // 返回错误信息
        boolean isSyns = true;
        // 从查看NG系统td_b_platsvc_pf表，ADC商信通，商户管用单机版 这三个产品的服务都不用发服开,
        if ("8199".equals(productId) || "9311".equals(productId))
        {
            return retStr;
        }
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        // ADCG 判 RSRV_TAG12 同步标记
        if ("ADCG".equals(brandCode)||"9976".equals(productId)||"9975".equals(productId))
        {
            isSyns = CParamQry.IsCustAdcSyncTag12(custId);
            if (isSyns == false)
                retStr = "该集团客户没有同步资料，请先同步资料！再进行此操作!";
        }
        // MASG 判 RSRV_TAG14 同步标记
        else if ("MASG".equals(brandCode))
        {
            isSyns = CParamQry.IsCustMasSyncTag14(custId);
            if (isSyns == false)
                retStr = "该集团客户没有同步基本接入号，请先同步资料！再进行此操作!";
        }
        return retStr;
    }

}
