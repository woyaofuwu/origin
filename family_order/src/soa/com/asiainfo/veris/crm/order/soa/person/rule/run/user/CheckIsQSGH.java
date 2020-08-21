
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckIsQSGH extends BreBase implements IBREScript
{
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // tag ： 0--业务受理时子类业务规则校验 1--业务提交时子类业务规则校验

        String userId = databus.getString("USER_ID");
        IDataset productDataset = UserProductInfoQry.queryMainProduct(userId);
        if (IDataUtil.isEmpty(productDataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }
        else
        {
            String productId = productDataset.getData(0).getString("PRODUCT_ID", "0");
            IDataset productTypes = ProductTypeInfoQry.getProductTypeByProductId(productId);
            if (IDataUtil.isEmpty(productTypes))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据产品ID找不到对应的产品类型ID");
            }
            else
            {
                for (int i = 0; i < productTypes.size(); i++)
                {
                    if ("4100".equals(productTypes.getData(0).getString("PRODUCT_TYPE_CODE", "")) || "4200".equals(productTypes.getData(0).getString("PRODUCT_TYPE_CODE", "")))
                    {
                        return false;
                    }
                }
            }

        }
        // 判断是否千群百号代码号
        return true;
    }

}
