
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class GrpUseDestroyFlowMainHttpHandler extends CSBizHttpHandler
{

    /**
     * 提交方法
     * 
     * @throws Exception
     */
    public void submit() throws Exception
    {
        IData data = getData();

        String productId = data.getString("GRP_PRODUCT_ID", "");

        IData svcData = new DataMap();
        svcData.put("USER_ID", data.getString("GRP_USER_ID"));
        svcData.put(Route.USER_EPARCHY_CODE, data.getString("GRP_USER_EPARCHYCODE"));
        svcData.put("IF_BOOKING", data.getString("IF_BOOKING", "false").equals("true") ? "true" : "false");
        svcData.put("REASON_CODE", data.getString("param_REMOVE_REASON"));
        svcData.put("REMARK", data.getString("param_REMARK", ""));
        svcData.put("PRODUCT_ID", productId); // 产品ID
        svcData.put("AUDIT_STAFF_ID", data.getString("AUDIT_STAFF_ID", ""));

        // esop参数
        String eos = data.getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            svcData.put("EOS", new DatasetList(eos));
        }
        String brandCode = data.getString("GRP_BRAND_CODE", "");
        if (brandCode.equals(""))
        {
            brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        }
        if ("BOSG".equals(brandCode))
        {// BBOSS商品

            IData bbossData = new DataMap(getData().getString("productGoodInfos"));
            svcData.put("GOOD_INFO", bbossData.getString("GOOD_INFO"));
            if (StringUtils.isNotEmpty(getData().getString("BBossParamInfo")))
            {
                IData bbossparam = new DataMap(getData().getString("BBossParamInfo"));// BBOSS产品特殊参数信息
                svcData.put("BBossParamInfo", bbossparam);
            }

            IDataset result = CSViewCall.call(this, "CS.DestroyBBossUserSVC.dealDelBBossBiz", svcData);
            setAjax(result);
            return;
        }
        if ("6100".equals(productId))// 移动总机
        {
            IDataset result = CSViewCall.call(this, "SS.DestroySuperTeleGroupUserSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }
        if ("6130".equals(productId))// 融合总机
        {
            IDataset result = CSViewCall.call(this, "SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder", svcData);
            this.setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.DestroyGroupUserSvc.destroyGroupUser", svcData);
        setAjax(result);
    }
}
