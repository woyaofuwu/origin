
package com.asiainfo.veris.crm.order.web.group.modifyuserdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class AccountInfo extends GroupBasePage
{

    private String productId;

    private String userId;

    public AccountInfo()
    {
    }

    public abstract IData getInfo();

    /**
     * @return productId
     */
    public String getProductId()
    {
        return productId;
    }

    /**
     * @return productId
     */
    public String getUserId()
    {
        return userId;
    }

    public void initial(IRequestCycle cycle) throws Exception
    {

        // j2ee IDataset feeList = TradeFeeMgr.getOperFee(pd, new DataMap());
        userId = getData().getString("GRP_USER_ID");
        productId = getData().getString("GRP_PRODUCT_ID");
        // j2ee IDataset selProdcuts = getSelectedProduct(cycle);
        // j2ee if (selProdcuts.size() > 0)
        // j2ee {
        // j2ee this.productId = ((IData) selProdcuts.get(0)).getString("PRODUCT_ID", "");
        // j2ee }

        // 获取产品控制信息
        // j2ee IData productCtrlInfo = GroupUtil.queryProductCtrlInfo(pd, productId,
        // GroupBaseFactory.CtrlProductType.ModifyUser);
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryModUsProductCtrlInfoByProductId(this, productId); // ModUs
        if (productCtrlInfo != null && productCtrlInfo.size() > 0)
            setProductCtrlInfo(productCtrlInfo);
        else
        {
            // j2ee common.warn("该产品无需此功能!");
        }

        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userId);
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            setGrpAcctInfo(grpUCA.getData("GRP_ACCT_INFO"));
        }

    }

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    /**
     * @param productId
     *            要设置的 productId
     */
    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    /**
     * @param productId
     *            要设置的 productId
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

}
