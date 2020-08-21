
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.UserProd;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

/**
 * @Description 家庭受理服务类
 * @Auther: zhenggang
 * @Date: 2020/7/24 15:25
 * @version: V1.0
 */
public class FamilyAcceptSVC extends CSBizService
{
    public IDataset initFamily(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData data = new DataMap();
        // 管理员手机号码
        String mainSn = input.getString("SERIAL_NUMBER");
        // 家庭产品
        String familyProductId = input.getString("FAMILY_PRODUCT_ID");

        // 1.用户基础信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSn);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }

        String mainUserId = userInfo.getString("USER_ID");
        String custId = userInfo.getString("CUST_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE");
        UserProd userProd = FamilyMemUtil.getUserAllValidMainProduct(mainUserId);
        userProd.setUserFamilyProductId(familyProductId);

        if (StringUtils.isNotEmpty(userProd.getNextProductId()))
        {
            // TODO 预约产品变更校验

        }
        else
        {
            // TODO 用户产品转换校验

        }

        // 2.校验
        // TODO 此处预留校验手机号码规则 是走规则还是自己写代码校验？
        IData ruleData = new DataMap();
        ruleData.put("SERIAL_NUMBER", mainSn);
        ruleData.put("FAMILY_PRODUCT_ID", familyProductId);

        this.checkInitRule(input);
        // TODO 规则
        data.put("SERIAL_NUMBER", mainSn);
        data.put("USER_ID", mainUserId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("FAMILY_PRODUCT_ID", userProd.getUserFamilyProductId());
        data.put("FAMILY_PRODUCT_NAME", userProd.getUserFamilyProductName());
        data.put("FAMILY_PRODUCT_DESC", userProd.getUserFamilyProductDesc());
        // 3.家庭产品加载
        IDataset roles = this.getProductInfos(data);
        data.put("ROLES", roles);
        // 4.家庭产品构成加载
        IDataset comChas = this.getFamilyOfferComChas(input);
        if (IDataUtil.isNotEmpty(comChas))
        {
            data.putAll(comChas.first());
        }
        // 加载管理员客户信息
        IData custInfo = UcaInfoQry.qryPerInfoByCustId(custId, eparchyCode);
        custInfo.put("HOME_NAME", custInfo.getString("CUST_NAME") + "的家");
        custInfo.put("HOME_REGION", custInfo.getString("RELIGION_CODE"));
        custInfo.put("SERIAL_NUMBER", mainSn);
        data.put("CUST_DATA", custInfo);
        results.add(data);
        return results;
    }

    public void checkInitRule(IData input) throws Exception
    {
        // FusionCheckCaller.check("301", "-1", "-1", input, FusionConstants.TRIGGER_POINT_INIT, false, false);
    }

    public IDataset getProductInfos(IData input) throws Exception
    {
        String familyProductId = input.getString("FAMILY_PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset roles = FamilyMemUtil.getFamilyRoleOffers(familyProductId, eparchyCode);
        DataHelper.sort(roles, "ROLE_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        return roles;
    }

    public IDataset getFamilyOfferComChas(IData input) throws Exception
    {
        IDataset comChas = new DatasetList();
        String familyProductId = input.getString("FAMILY_PRODUCT_ID");
        IData comCha = FamilyMemUtil.getFamilyOfferComChas(familyProductId);
        comChas.add(comCha);
        return comChas;
    }
}
