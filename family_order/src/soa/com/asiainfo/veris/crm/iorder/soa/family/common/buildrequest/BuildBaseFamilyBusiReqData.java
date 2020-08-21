
package com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilySaleActiveData;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;

/**
 * @Description 构建家庭基础请求对象
 * @Auther: zhenggang
 * @Date: 2020/7/30 16:30
 * @version: V1.0
 */
public class BuildBaseFamilyBusiReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        BaseFamilyBusiReqData reqData = (BaseFamilyBusiReqData) brd;
        // 1.参数构建
        this.buildCommonData(param, reqData);

        // 2.构建角色对象
        String roleCode = param.getString("ROLE_CODE");

        if (StringUtils.isNotEmpty(roleCode))
        {
            this.buildSingleRole(param, reqData);
        }
        // 3.商品构建
        this.buildRoleOffers(param, reqData);
    }

    private void buildRoleOffers(IData param, BaseFamilyBusiReqData reqData) throws Exception
    {
        IDataset offers = new DatasetList(param.getString("OFFERS", "[]"));

        if (IDataUtil.isNotEmpty(offers))
        {
            int size = offers.size();

            for (int i = 0; i < size; i++)
            {
                IData element = offers.getData(i);

                ProductModuleData pmd = null;
                String offerType = element.getString("ELEMENT_TYPE_CODE");
                String modifyTag = element.getString("MODIFY_TAG");

                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
                {
                    pmd = new DiscntData(element);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(offerType))
                {
                    pmd = new SvcData(element);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(offerType))
                {
                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                    {
                        element.put("OPER_CODE", PlatConstants.OPER_ORDER);
                    }
                    else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                    {
                        element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                    }
                    pmd = new PlatSvcData(element);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PACKAGE.equals(offerType))
                {
                    // 以融合产品生效时间作为偏移基准时间
                    element.put("BASIC_CAL_START_DATE", reqData.getFamilyEffectiveDate());
                    pmd = new FamilySaleActiveData(element);
                    pmd.setProductId(reqData.getFmyProductId());
                    pmd.setRemark("RH:" + reqData.getOrderTypeCode());
                    reqData.getFamilyRole().addRoleSaleActiveData(pmd);
                    continue;
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType))
                {
                    // 各个成员P元素
                    reqData.setMemProductId(element.getString("ELEMENT_ID"));
                    continue;
                }
                pmd.setProductId(reqData.getFmyProductId());
                pmd.setRemark("RH:" + reqData.getOrderTypeCode());
                reqData.getFamilyRole().addRoleOfferData(pmd);
            }
        }
    }

    private void buildCommonData(IData param, BaseFamilyBusiReqData reqData) throws Exception
    {
        String fmyProductId = param.getString("FAMILY_PRODUCT_ID");
        String managerSn = param.getString("MANAGER_SN");
        String familySn = param.getString("FAMILY_SERIAL_NUMBER");
        String familyUserId = param.getString("FAMILY_USER_ID");
        String familyAcctId = param.getString("FAMILY_ACCT_ID");
        String topTradeId = param.getString("TOP_TRADE_ID");
        String topEparchyCode = param.getString("TOP_EPARCHY_CODE");
        String middleTradeId = param.getString("MIDDLE_TRADE_ID");
        String middleEparchyCode = param.getString("MIDDLE_EPARCHY_CODE");
        String familyMemberInstId = param.getString("FAMILY_MEMBER_INST_ID");
        reqData.setFmyProductId(fmyProductId);
        reqData.setManagerSn(managerSn);
        reqData.setFamilySn(familySn);
        reqData.setFamilyUserId(familyUserId);
        reqData.setFmyAcctId(familyAcctId);
        reqData.setTopTradeId(topTradeId);
        reqData.setTopEparchyCode(topEparchyCode);
        reqData.setMiddleTradeId(middleTradeId);
        reqData.setMiddleEparchyCode(middleEparchyCode);
        reqData.setFamilyMemberInstId(familyMemberInstId);
    }

    private void buildSingleRole(IData param, BaseFamilyBusiReqData reqData) throws Exception
    {
        String memberMainSn = param.getString("MEMBER_MAIN_SN");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String roleCode = param.getString("ROLE_CODE");
        String roleType = param.getString("ROLE_TYPE");
        String modifyTag = param.getString("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        boolean familyPay = param.getBoolean("FAMILY_PAY", true);
        boolean familyShare = param.getBoolean("FAMILY_SHARE", true);

        FamilyRole familyRole = new FamilyRole();
        familyRole.setMemberMainSn(memberMainSn);
        familyRole.setEparchyCode(eparchyCode);
        familyRole.setRoleCode(roleCode);
        familyRole.setRoleType(roleType);
        familyRole.setModifyTag(modifyTag);
        familyRole.setFamilyPay(familyPay);
        familyRole.setFamilyShare(familyShare);
        reqData.setFamilyRole(familyRole);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseFamilyBusiReqData();
    }
}
