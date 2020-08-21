
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroupbysnpopup;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.ptypeproductinfo.PTypeProductInfoIntfViewUtil;

public class SelectGroupBySNPopupHttpHandler extends CSBizHttpHandler
{

    public void getGrpUCAInfoBySn() throws Exception
    {

        String grpsn = getData().getString("cond_GROUP_SERIAL_NUMBER");
        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);
        if (IDataUtil.isEmpty(result))
        {
            return;
        }

        IData userInfo = result.getData("GRP_USER_INFO");
        if (IDataUtil.isEmpty(userInfo))
        {
            return;
        }

        String productId = userInfo.getString("PRODUCT_ID");
        if (!PTypeProductInfoIntfViewUtil.ifGrpProductTypeBooByProductId(this, productId))
        {// 非集团产品树下的产品编码不允许办理业务
            CSViewException.apperr(GrpException.CRM_GRP_745, ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId));
            return;
        }

        String limitType = getData().getString("cond_LIMIT_TYPE", "");
        String limitProducts = getData().getString("cond_LIMIT_PRODUCTS");
        if (StringUtils.isNotBlank(limitProducts))
        {
            String products[] = limitProducts.split(",");
            // 当limit_type=0为限制limit_products不展示
            if (limitType.equals("0"))
            {
                for (int i = 0; i < products.length; i++)
                {
                    if (products[i].equals(productId))
                    {
                        CSViewException.apperr(GrpException.CRM_GRP_745, userInfo.getString("PRODUCT_NAME"));
                        return;
                    }
                }

            }
            else if (limitType.equals("1"))
            {// limit_type=1为只显示limit_products中的产品
                boolean hasProduct = false;
                for (int i = 0; i < products.length; i++)
                {
                    if (products[i].equals(productId))
                    {
                        hasProduct = true;
                        break;
                    }
                }
                if (!hasProduct)
                {
                    CSViewException.apperr(GrpException.CRM_GRP_745, userInfo.getString("PRODUCT_NAME"));
                    return;
                }

            }
        }

        this.setAjax(result);

    }

}
