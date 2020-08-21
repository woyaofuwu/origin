
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatDestroyGroupUserSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.DestoryUser);
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // 校验集团用户信息
        validateGroupUser(batData);
    }

    @Override
    public void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpUserDestory");
        ruleData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        ruleData.put("CUST_ID", condData.getString("CUST_ID"));// 集团客户标识
        ruleData.put("USER_ID", condData.getString("USER_ID"));// 集团用户标识
        ruleData.put("CHECK_TAG", batData.getString("CHECK_TAG", "-1"));
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        String productId = condData.getString("PRODUCT_ID");

        svcName = "CS.DestroyGroupUserSvc.destroyGroupUser";

        if ("6100".equals(productId))
        {
            svcName = "SS.DestroySuperTeleGroupUserSVC.crtOrder";
        }
        else if ("6130".equals(productId))
        {
            svcName = "SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder";
        }

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", productId);
        svcData.put("REASON_CODE", condData.getString("REMOVE_REASON"));
        svcData.put("REMARK", batData.getString("REMARK"));
        svcData.put(Route.USER_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    }

    /**
     * 校验集团用户信息
     * 
     * @param batData
     * @throws Exception
     */
    public void validateGroupUser(IData batData) throws Exception
    {
        String groupId = IDataUtil.getMandaData(condData, "GROUP_ID");

        String productId = IDataUtil.getMandaData(condData, "PRODUCT_ID");

        String userId = condData.getString("USER_ID");

        // 判断用户ID是否为空
        if (StringUtils.isBlank(userId))
        {
            IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

            if (IDataUtil.isEmpty(custData))
            {
                CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
            }

            String custId = custData.getString("CUST_ID");

            // 查询集团订购的产品信息
            IDataset userList = UserProductInfoQry.getMainUserProductInfoByCstId(custId, productId, null);

            if (IDataUtil.isEmpty(userList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_197, productId);
            }

            userId = userList.getData(0).getString("USER_ID");

            batData.put("USER_ID", userId);
        }

        // 校验集团用户三户信息
        IData param = new DataMap();
        param.put("USER_ID", userId);
        chkGroupUCAByUserId(param);

        // 判断集团用户下面是否还存在成员
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        // 根据品牌判断UU关系
        IDataset relaList = null;
        if ("ADCG".equals(brandCode) || "MASG".equals(brandCode))
        {
            relaList = RelaBBInfoQry.getExistsByUserIdA(userId, relationTypeCode);
        }
        else
        {
            relaList = RelaUUInfoQry.qryExistsMebByUserIdA(userId, relationTypeCode);
        }

        // 如果存在关系则不能注销
        if (IDataUtil.isNotEmpty(relaList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_651);
        }
    }

}
