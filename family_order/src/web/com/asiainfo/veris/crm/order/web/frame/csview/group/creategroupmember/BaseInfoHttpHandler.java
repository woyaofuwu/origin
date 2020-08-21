
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class BaseInfoHttpHandler extends CSBizHttpHandler
{
    public void queryProductInfo() throws Exception
    {
        IData seleUserInfo = new DataMap();

        String productId = getData().getString("PRODUCT_ID");
        String custid = getData().getString("CUST_ID", "");

        IData productDescInfoData = GroupProductUtilView.getProductExplainInfo(this, productId);

        IData productCtrlInfoData = AttrBizInfoIntfViewUtil.qrySimpleCrtMbProductCtrlInfoByProductId(this, productId);

        IDataset grpUserList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custid, getData().getString("PRODUCT_ID"), false);

        if (IDataUtil.isNotEmpty(grpUserList) && grpUserList.size() == 1)
        {
            grpUserList.getData(0).put("CHECKED", "true");

            seleUserInfo = grpUserList.getData(0);
        }
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(productDescInfoData))
            result.put("PRODUCT_DESC_INFO", productDescInfoData);
        if (IDataUtil.isNotEmpty(productCtrlInfoData))
            result.put("PRODUCT_CTRL_INFO", productCtrlInfoData);
        if (IDataUtil.isNotEmpty(grpUserList))
            result.put("GRP_USER_LIST", grpUserList);
        if (IDataUtil.isNotEmpty(seleUserInfo))
            result.put("SEL_USER_INFO", seleUserInfo);
        setAjax(result);

    }
}
