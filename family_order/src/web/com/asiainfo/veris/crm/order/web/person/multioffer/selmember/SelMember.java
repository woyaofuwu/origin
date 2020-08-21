/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.multioffer.selmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.asiainfo.veris.crm.order.web.person.multioffer.MultiOfferClient;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SelMember.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-28 下午02:48:06 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-28 chengxf2 v1.0.0 修改原因
 */

public abstract class SelMember extends PersonBasePage
{

    public IDataset rolePackList;

    public IDataset packProdList;

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        this.setRolePackList(null);
        this.setPackProdList(null);
        super.cleanupAfterRender(cycle);
    }

    public IDataset getPackProdList()
    {
        return packProdList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:42:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public void getPackProdList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String packageId = data.getString("PACKAGE_ID");
        IDataset packProdList = MultiOfferClient.getPackProdList(this, packageId);
        this.setPackProdList(packProdList);
    }

    public IDataset getRolePackList()
    {
        return rolePackList;
    }

    public void setPackProdList(IDataset packProdList)
    {
        this.packProdList = packProdList;
    }

    public void setRolePackList(IDataset rolePackList)
    {
        this.rolePackList = rolePackList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 上午08:01:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    public void setUCAViewInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData ucaParams = new DataMap(data.getString("UCAInfoParam"));
        IDataset ucaCustInfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryPerInfoByCustId", ucaParams);
        if (IDataUtil.isNotEmpty(ucaCustInfo))
        {
            this.setCustInfoView(ucaCustInfo.getData(0));
        }
        IDataset ucaUserInfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySn", ucaParams);
        if (IDataUtil.isNotEmpty(ucaUserInfo))
        {
            this.setUserInfoView(ucaUserInfo.getData(0));
        }
        String productId = data.getString("PRODUCT_ID");
        String roleId = data.getString("ROLE_CODE");
        String itemKindId = "ROLE_INCLUDE_PROD";
        IDataset rolePackList = MultiOfferClient.getRolePackList(this, productId, roleId, itemKindId);
        this.setRolePackList(rolePackList);
    }
}
