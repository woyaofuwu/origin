
package com.asiainfo.veris.crm.order.web.group.bat.batchangewlwmeb;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 物联网集团产品成员优惠批量修改
 * 
 * @author think
 *
 */
public abstract class BatChangeMebWLWDiscnt extends GroupBasePage
{

	public abstract IData getCondition();

    public abstract IData getInfo();

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());

        productId = "20005013";
        IData param = getData();
        IData condition = new DataMap();
         
        condition.put("RELA_CODE", "1");
        // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        String productTreeLimitType = param.getString("PRODUCTTREE_LIMIT_TYPE", "1"); 
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
        condition.put("RELA_CODE", "1");
        condition.put("IS_RENDER", "false");
        param.put("IS_RENDER", "false");
        
        setCondition(condition);
         

        // 获取产品组件的已选区初始参数
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId); 
        cond.put("MEB_PRODUCT_ID", UpcViewCall.queryMemProductIdByProductId(this, productId));
        setCond(cond);
        
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);
    
    public abstract void setHidden(String hidden);
    
    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);
    
    public abstract void setPkgParam(IData pkgParam);
    
    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynParam);
    
}
