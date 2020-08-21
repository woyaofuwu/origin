
package com.asiainfo.veris.crm.order.web.group.param.Econnet;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class EconnetMebParamInfo extends IProductParamDynamic
{

    @SuppressWarnings("unchecked")
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        // j2ee PageData pd = getPageData();

        String userIdA = result.getString("USER_ID", "");// 当前集团客户标识
        // String userIdB = pd.getParameter("MEM_USER_ID", "");//成员用户标识
        String eparchyCode = result.getString("MEM_EPARCHY_CODE", "");
        // j2ee CSBaseBean.setDbConCode(pd, eparchyCode);
        IData idata = new DataMap();
        idata.put("USER_ID", userIdA);

        idata.clear();
        idata.put("USER_ID", userIdA);
        idata.put("DISCNT_CODE", "4013");
        // j2ee CSAppEntity dao = new CSAppEntity(pd);
        // IDataset infos = dao.queryListByCodeCode("TF_F_USER_DISCNT", "SEL_BY_ID_USERD_DISCNT", idata, null);
        IDataset infos = CSViewCall.call(bp, "CS.UserDiscntInfoQrySVC.queryDiscntByUserIdAndDiscntCode", idata);
        String CAR_APN_DISCNT = "";
        if (IDataUtil.isNotEmpty(infos))
        {
            IData tmp = infos.getData(0);
            String elementId = tmp.getString("DISCNT_CODE", "");
            if (!"".equals(elementId))
            {
                CAR_APN_DISCNT = elementId;
            }
        }

        IData discnt = new DataMap();
        discnt.put("ATTR_VALUE", CAR_APN_DISCNT);
        this.getAttrItem().put("CAR_APN_DISCNT", discnt);
        result.put("ATTRITEM", this.getAttrItem());
        return result;
    }
}
