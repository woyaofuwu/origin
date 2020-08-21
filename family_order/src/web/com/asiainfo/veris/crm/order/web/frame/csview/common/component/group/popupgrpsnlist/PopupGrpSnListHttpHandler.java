
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.popupgrpsnlist;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class PopupGrpSnListHttpHandler extends CSBizHttpHandler
{

    public void getGroupBaseInfo() throws Exception
    {

        String userId = getData().getString("GRP_USER_ID");
        String busiType = getData().getString("BUSI_TYPE", "");
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userId);
        if (IDataUtil.isEmpty(data))
            return;
        IData userinfo = data.getData("GRP_USER_INFO");
        // 获取产品类型
        if (IDataUtil.isEmpty(userinfo))
        {
            return;
        }
        String productId = userinfo.getString("PRODUCT_ID");
        IData productExplainInfoData = GroupProductUtilView.getProductExplainInfo(this, productId);
        data.put("PRODUCT_DESC_INFO", productExplainInfoData);
        if (StringUtils.isNotBlank(busiType))
        {
            IData productCtrlInfoData = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType);
            data.put("PRODUCT_CTRL_INFO", productCtrlInfoData);
        }
        this.setAjax(data);
    }

}
