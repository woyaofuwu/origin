
package com.asiainfo.veris.crm.order.web.group.changewidenetusersvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{
    public void initial(IRequestCycle cycle) throws Exception
    {
        String userId = getData().getString("GRP_USER_ID");// 集团用户ID
        String productId = getData().getString("GRP_PRODUCT_ID", "");
        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userId);
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            setGrpCustInfo(grpUCA.getData("GRP_CUST_INFO"));
            setGrpUserInfo(grpUCA.getData("GRP_USER_INFO"));
            setGrpAcctInfo(grpUCA.getData("GRP_ACCT_INFO"));
        }
        
        String operType= getData().getString("OPER_TYPE");// STOP 暂停 ；OPEN 恢复
        if (StringUtils.isBlank(operType))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_517);
        }
        if(!"STOP".equals(operType) && !"OPEN".equals(operType))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_518);
        }
        
        IData userinfo=grpUCA.getData("GRP_USER_INFO");
        
        String userStateCode=userinfo.getString("USER_STATE_CODESET");
        
        if("STOP".equals(operType) && "1".equals(userStateCode) )//暂停
        {
            CSViewException.apperr(CrmUserException.CRM_USER_16);
        }
        else if("OPEN".equals(operType) && "0".equals(userStateCode))//本身正常状态
        {
            CSViewException.apperr(CrmUserException.CRM_USER_15);
        }
        
        
        IDataset productElements = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, userId);
        IData elemment = new DataMap();
        GroupBaseView.processProductElements(this, productElements, elemment);
        setUserProduct(elemment);

        // 查询成员信息
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);

        String mebCount = "0";

        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {
            mebCount = RelationBBInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);
        }
        else if("BNBD".equals(brandCode)) //商务宽带
        {
            mebCount= RelationUUInfoIntfViewUtil.qryCountKDUUForAllCrm(this, userId, "47"); 
        }
        else
        {
            mebCount = RelationUUInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);
        }

        setMebCount(mebCount);
    }

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpUserInfo(IData grpUserInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserProduct(IData info);

    public abstract void setMebCount(String mebCount);
}
