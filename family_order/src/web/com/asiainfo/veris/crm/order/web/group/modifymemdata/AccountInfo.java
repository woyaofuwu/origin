
package com.asiainfo.veris.crm.order.web.group.modifymemdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class AccountInfo extends GroupBasePage
{
    public AccountInfo()
    {
    }

    public abstract IData getInfo();

    public void initial(IRequestCycle cycle) throws Exception
    {
        String grpUserId = getData().getString("GRP_USER_ID", "");
        String grpSn = getData().getString("GRP_USER_ID", "");
        String grpId = getData().getString("GROUP_ID", "");
        String mebuserId = getData().getString("MEB_USER_ID", "");
        String mebsn = getData().getString("MEB_SERIAL_NUMBER", "");
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        productId = getData().getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryModMbProductCtrlInfoByProductId(this, productId); // ModMb
        setProductCtrlInfo(productCtrlInfo);

        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, grpUserId);
        IData grpAcctInfo = new DataMap();
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            grpAcctInfo = grpUCA.getData("GRP_ACCT_INFO");
            setGrpAcctInfo(grpAcctInfo);
        }

        IDataset attrBizs = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "PayRelChg", "EsopBat");
        if (IDataUtil.isNotEmpty(attrBizs))
        {
            IData tempParam = new DataMap();
            tempParam.put("ACCT_ID", grpAcctInfo.getString("ACCT_ID", "")); // 集团账户
            tempParam.put("USER_ID", mebuserId);
            tempParam.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);
            IDataset creditMemAcctInfo = CSViewCall.call(this, "CS.PayRelaInfoQrySVC.getPayrelationByUserIdAndAcctId", tempParam);
            if (IDataUtil.isEmpty(creditMemAcctInfo))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_125);
            }
            for (int i = 0; i < creditMemAcctInfo.size(); i++)
            {
                IData tempAcctInfo = creditMemAcctInfo.getData(i);
                if ("2".equals(tempAcctInfo.getString("ACT_TAG", "")))
                {
                    CSViewException.apperr(GrpException.CRM_GRP_704);// 该集团成员处于信控暂停状态
                }
            }
        }
        IDataset planInfos = UserPayPlanInfoIntfViewUtil.getGrpMemPayPlanByUserId(this, mebuserId, grpUserId, mebeparchycode);

        if (IDataUtil.isNotEmpty(planInfos))
        {
            IData planInfo = planInfos.getData(0);
            String oldPlanType = planInfo.getString("PLAN_TYPE_CODE");
            getData().put("OLD_PLAN_TYPE_CODE", oldPlanType);
        }
        else
        {
            getData().put("OLD_PLAN_TYPE_CODE", "");
        }

    }

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);
}
