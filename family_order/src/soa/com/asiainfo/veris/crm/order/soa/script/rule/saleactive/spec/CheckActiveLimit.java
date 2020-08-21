
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 2.3.7.1param_attr = 68 >> 购机业务受限处理(有para_code1营销包，不能办理param_code活动) 有para_code2产品下的包para_code1，
 * 不能办理param_code活动（目前只有69908012活动有配置）
 * 
 * @author Mr.Z
 */
public class CheckActiveLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -8326901260882774337L;

    private static Logger logger = Logger.getLogger(CheckActiveLimit.class);

    private boolean getExsitsSaleActive(IDataset userSaleActives, String productId, String packageId) throws Exception
    {
        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            IData userSaleActive = userSaleActives.getData(i);
            if (productId.equals(userSaleActive.getString("PRODUCT_ID")) && packageId.equals(userSaleActive.getString("PACKAGE_ID")))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userSaleActiveDataset = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        IDataset limit68Set = CommparaInfoQry.getCommparaByParaAttr("CSM", "68", databus.getString("EPARCHY_CODE"));
        String saleProductId = databus.getString("PRODUCT_ID");
        if (IDataUtil.isEmpty(limit68Set) || IDataUtil.isEmpty(userSaleActiveDataset))
            return true;

        boolean isExists = false;
        for (int index = 0, size = limit68Set.size(); index < size; index++)
        {
            IData limit68Data = limit68Set.getData(index);
            String limitProductId = limit68Data.getString("PARA_CODE2");
            String limitPackageId = limit68Data.getString("PARA_CODE1");
            if (saleProductId.equals(limit68Data.getString("PARAM_CODE")))
            {
                isExists = getExsitsSaleActive(userSaleActiveDataset, limitProductId, limitPackageId);
                if (isExists)
                    break;
            }
        }

        if (isExists)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20018, "需要取消宽带1+并进行产品变更成8m才可办理此活动!");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
