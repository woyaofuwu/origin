
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroupbysn;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.ptypeproductinfo.PTypeProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class SelectGroupBySNHttpHandler extends CSBizHttpHandler
{

    public void getGrpUCAInfoBySn() throws Exception
    {

        String grpsn = getData().getString("cond_GROUP_SERIAL_NUMBER");
        String busiType = getData().getString("BUSI_TYPE");
        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);
        IData userInfo = result.getData("GRP_USER_INFO");
        userInfo.put("CHECKED", "true");
        String productId = userInfo.getString("PRODUCT_ID", "");

        String limitType = getData().getString("cond_LIMIT_TYPE", "");
        String limitProducts = getData().getString("cond_LIMIT_PRODUCTS", "");

        if (limitType.equals("0") && limitProducts.indexOf(productId) >= 0)
        {
            CSViewException.apperr(GrpException.CRM_GRP_745, ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId));
            return;
        }

        if (!PTypeProductInfoIntfViewUtil.ifGrpProductTypeBooByProductId(this, productId))
        {// 非集团产品树下的产品编码不允许办理业务
            CSViewException.apperr(GrpException.CRM_GRP_745, ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId));
            return;
        }

        IData productInfoData = GroupProductUtilView.getProductExplainInfo(this, productId);
        if (StringUtils.isNotBlank(busiType))
        {
            IData productCtrlInfoData = AttrBizInfoIntfViewUtil.qrySimpleNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType);
            result.put("PRODUCT_CTRL_INFO", productCtrlInfoData);
        }
        result.put("PRODUCT_DESC_INFO", productInfoData);
        this.setAjax(result);

    }

}
