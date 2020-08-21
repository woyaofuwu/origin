
package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.changegroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SimpleChangeGroupMember extends GroupBasePage
{

    public abstract IData getCondition();

    public abstract IData getInfo();

    public void initial(IRequestCycle cycle) throws Throwable
    {
    	productId = "8000";
        IData param = getData();
        IData condition = new DataMap();
         
        condition.put("RELA_CODE", "0");
        String productTreeLimitType = param.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        String productTreeLimitProducts = param.getString("PRODUCTTREE_LIMIT_PRODUCTS");

        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            condition.put("LIMIT_TYPE", productTreeLimitType);
            condition.put("LIMIT_PRODUCTS", productTreeLimitProducts);

            if (productTreeLimitType.equals("1"))
            {
                String[] productLimitS = productTreeLimitProducts.split(",");
                int limitLen = productLimitS.length;
                String relaCode = "";
                for (int i = 0; i < limitLen; i++)
                {
                    String productId = productLimitS[i];
                    String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
                    if (GroupBaseConst.BB_BRAND_CODE.toString().indexOf(brandCode) >= 0)
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.BB.getValue())) ? GroupBaseConst.RELA_TYPE.BB.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                    else
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.UU.getValue())) ? GroupBaseConst.RELA_TYPE.UU.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                }

                if (StringUtils.isNotBlank(relaCode))
                {
                    condition.put("RELA_CODE", relaCode);
                }
            }

        }
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	condition.put("MEB_FILE_SHOW","true");
        }
        
        setCondition(condition);
         

        // 获取产品组件的已选区初始参数
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId); 
        cond.put("MEB_PRODUCT_ID", UpcViewCall.queryMemProductIdByProductId(this, productId));
        setCond(cond);

    }

    public abstract void setCond(IData cond);
    
    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

}
