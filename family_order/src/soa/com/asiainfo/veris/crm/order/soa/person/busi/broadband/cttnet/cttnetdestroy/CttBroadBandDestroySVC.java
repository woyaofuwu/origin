
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CttBroadBandDestroySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryBroadBandUser(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData data = new DataMap();
        // 查询宽带信息
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userId = input.getString("USER_ID");
        IDataset accessInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isNotEmpty(accessInfos))
        {
            data.putAll(accessInfos.getData(0));
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, serialNumber);
        }
        IDataset widenetAct = WidenetInfoQry.getUserWidenetActInfosByUserid(userId);
        if (IDataUtil.isEmpty(widenetAct))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, serialNumber);
        }
        data.put("ACCT_ID", widenetAct.getData(0).getString("ACCT_ID"));

        /*
         * // 查询宽带用户速率信息 IDataset rateInfos = BroadBandInfoQry.queryUserRateByUserId(userId); if
         * (IDataUtil.isNotEmpty(rateInfos)) { data.putAll(rateInfos.getData(0)); } else {
         * CSAppException.apperr(BroadBandException.CRM_BROADBAND_12, serialNumber); } // 查询宽带用户地址信息 IData param = new
         * DataMap(); param.put("USER_ID", userId); IDataset addrInfos =
         * BroadBandInfoQry.queryBroadBandAddressInfo(param); if (IDataUtil.isNotEmpty(addrInfos)) {
         * data.putAll(addrInfos.getData(0)); } else { CSAppException.apperr(BroadBandException.CRM_BROADBAND_13,
         * serialNumber); }
         */

        // 查询宽带主产品信息
        IDataset productInfos = UserProductInfoQry.queryMainProduct(userId);
        if (IDataUtil.isNotEmpty(productInfos))
        {
            String productId = productInfos.getData(0).getString("PRODUCT_ID");
            String productName = UProductInfoQry.getProductNameByProductId(productId);
            productInfos.getData(0).put("PRODUCT_NAME", productName);
            data.putAll(productInfos.getData(0));
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_14, serialNumber);
        }

        // 查询宽带用户优惠信息
        IDataset discntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        if (IDataUtil.isNotEmpty(discntInfos))
        {
            int discntSize = discntInfos.size();
            for (int i = 0; i < discntSize; i++)
            {
                String discntCode = discntInfos.getData(i).getString("DISCNT_CODE");
                String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
                String discntExplain = UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode);
                discntInfos.getData(i).put("DISCNT_NAME", discntName);
                discntInfos.getData(i).put("DISCNT_EXPLAIN", discntExplain);

                String packageId = discntInfos.getData(i).getString("PACKAGE_ID");
                String packageName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PACKAGE", "PACKAGE_ID", "PACKAGE_NAME", packageId);
                discntInfos.getData(i).put("PACKAGE_NAME", packageName);
            }
        }
        else
        {
            // CSAppException.apperr(BroadBandException.CRM_BROADBAND_15, serialNumber);
        }

        data.put("DISCNT_INFO", discntInfos);
        result.add(data);
        return result;

    }

}
