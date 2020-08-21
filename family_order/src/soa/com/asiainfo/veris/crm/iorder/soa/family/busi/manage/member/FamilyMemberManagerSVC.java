
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.PhoneSubRole;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.UserProd;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * @Description 家庭成员管理
 * @Auther: zhenggang
 * @Date: 2020/8/19 16:59
 * @version: V1.0
 */
public class FamilyMemberManagerSVC extends CSBizService
{
    public IDataset loadChildInfo(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userId = input.getString("USER_ID");

        // 根据家庭管理员号码查询家庭信息
        IDataset allMembers = FamilyMemUtil.getFamilyMemberAndChaInfos(userId);

        if (IDataUtil.isEmpty(allMembers))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_14, serialNumber);
        }

        boolean isManager = FamilyMemUtil.isFamilyManager(allMembers, userId);

        if (!isManager)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_6, serialNumber);
        }

        IData existRoleCount = new DataMap();
        for (Object obj : allMembers)
        {
            IData allMember = (IData) obj;
            String roleCode = allMember.getString("MEMBER_ROLE_CODE");
            if (existRoleCount.containsKey(roleCode))
            {
                existRoleCount.put(roleCode, existRoleCount.getInt(roleCode) + 1);
            }
            else
            {
                existRoleCount.put(roleCode, 1);
            }
        }

        // 家庭现有成员个数
        result.put("EXIST_ROLE_COUNT", existRoleCount);

        // 1.找出所有的手机成员
        // 2.根据手机成员找出下级角色成员并排序
        IDataset phoneRoles = FamilyMemUtil.getRoleListByRoleCodeGroupSn(allMembers, FamilyRolesEnum.PHONE.getRoleCode(), null);

        String familySerialNum = "";
        String familyUserId = "";

        IDataset roleList = new DatasetList();
        for (Object obj : phoneRoles)
        {
            IDataset singleRoleList = new DatasetList();
            IData phoneRole = (IData) obj;
            // 简化参数
            this.getSimpleRoleData(singleRoleList, phoneRole);

            String memberSerialNum = phoneRole.getString("MEMBER_SERIAL_NUM");
            familySerialNum = phoneRole.getString("FAMILY_SERIAL_NUM");
            familyUserId = phoneRole.getString("FAMILY_USER_ID");

            // 找宽带成员
            IDataset wideNetRoles = FamilyMemUtil.getRoleListByRoleCodeGroupSn(allMembers, FamilyRolesEnum.WIDENET.getRoleCode(), memberSerialNum);
            if (IDataUtil.isEmpty(wideNetRoles))
            {
                // 没有宽带时，判断手机号码下是否存在宽带没有就新增，有就存量
                phoneRole.put("ADD_WIDENET_TAG", FamilyConstants.TYPE_NEW);
                PhoneSubRole psr = FamilyMemUtil.phoneUserHasRoleByRoleCode(memberSerialNum, FamilyRolesEnum.PHONE.getRoleCode());
                if (psr.isHasWideNet())
                {
                    phoneRole.put("ADD_WIDENET_TAG", FamilyConstants.TYPE_OLD);
                }
            }
            else
            {
                IData wideNetRole = wideNetRoles.first();
                // 简化参数
                this.getSimpleRoleData(singleRoleList, phoneRole);
                // 找出固话成员
                IDataset imsRoles = FamilyMemUtil.getRoleListByRoleCodeGroupSn(allMembers, FamilyRolesEnum.IMS.getRoleCode(), memberSerialNum);
                if (IDataUtil.isEmpty(imsRoles))
                {
                    wideNetRole.put("ADD_IMS_TAG", FamilyConstants.TYPE_NEW);
                    PhoneSubRole psr = FamilyMemUtil.phoneUserHasRoleByRoleCode(memberSerialNum, FamilyRolesEnum.WIDENET.getRoleCode());
                    if (psr.isHasIms())
                    {
                        wideNetRole.put("ADD_IMS_TAG", FamilyConstants.TYPE_OLD);
                    }
                }
                else
                {
                    // 简化参数
                    this.getSimpleRoleData(singleRoleList, phoneRole);
                }
                // 找出魔百和成员
                IDataset mbhRoles = FamilyMemUtil.getRoleListByRoleCodeGroupSn(allMembers, FamilyRolesEnum.MBH.getRoleCode(), memberSerialNum);
                if (IDataUtil.isEmpty(mbhRoles))
                {
                    wideNetRole.put("ADD_MBH_TAG", FamilyConstants.TYPE_NEW);
                    PhoneSubRole psr = FamilyMemUtil.phoneUserHasRoleByRoleCode(memberSerialNum, FamilyRolesEnum.WIDENET.getRoleCode());
                    if (psr.isHasMbh())
                    {
                        wideNetRole.put("ADD_MBH_TAG", FamilyConstants.TYPE_OLD);
                    }
                }
                else
                {
                    // 简化参数
                    this.getSimpleRoleData(singleRoleList, phoneRole);
                }
            }
            DataHelper.sort(singleRoleList, "ROLE_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
            roleList.add(singleRoleList);
        }

        // 初始化规则校验
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", familySerialNum);
        param.put("ROLE_CODE", FamilyRolesEnum.FAMILY.getRoleCode());
        param.put("ROLE_TYPE", FamilyConstants.TYPE_OLD);
        param.put(KeyConstants.BUSI_TYPE, FamilyConstants.FamilyTradeType.UPDATE_MEMBER.getValue());
        FamilyCallerBean.busiCheckNoCatch(param, FamilyConstants.TriggerPoint.INIT.toString());

        // 家庭用户产品展示
        UserProd userProd = FamilyMemUtil.getUserAllValidMainProduct(familyUserId);
        IDataset familyOffers = new DatasetList();
        IData prod = new DataMap();
        prod.put("OFFER_NAME", "[" + userProd.getUserProductId() + "]" + userProd.getUserProductName());
        prod.put("OFFER_DESC", userProd.getUserProductDesc());
        prod.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        prod.put("START_DATE", userProd.getUserProductStartDate());
        prod.put("END_DATE", userProd.getUserProductEndDate());
        familyOffers.add(prod);

        // 加载家庭产品构成元素
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset roles = FamilyMemUtil.getFamilyRoleOffers(userProd.getUserProductId(), eparchyCode);
        DataHelper.sort(roles, "ROLE_CODE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        result.put("ROLES", roles);

        // 加载家庭产品构成
        IData comCha = FamilyMemUtil.getFamilyOfferComChas(userProd.getUserProductId());
        result.putAll(comCha);// MEMBER_NUM,COMCHA

        // 返回参数
        result.put("FAMILY_SERIAL_NUM", familySerialNum);
        result.put("FAMILY_USER_ID", familyUserId);
        result.put("FAMILY_PRODUCT_ID", userProd.getUserProductId());
        result.put("MANAGER_SERIAL_NUMBER", serialNumber);
        result.put("EPARCHY_CODE", eparchyCode);
        result.put("FAMILY_ROLES", roleList);
        result.put("FAMILY_OFFERS", familyOffers);
        results.add(result);
        return results;
    }

    private void getSimpleRoleData(IDataset singleRoleList, IData role) throws Exception
    {
        IData temp = new DataMap();
        temp.put("INST_ID", role.getString("INST_ID"));
        temp.put("MEMBER_SN", role.getString("MEMBER_SERIAL_NUM"));
        temp.put("MAIN_SN", role.getString("MAIN_SERIAL_NUM"));
        temp.put("ROLE_CODE", role.getString("MEMBER_ROLE_CODE"));
        temp.put("ROLE_TYPE", role.getString("MEMBER_ROLE_TYPE"));
        temp.put("START_DATE", role.getString("START_DATE"));
        temp.put("END_DATE", role.getString("END_DATE"));
        singleRoleList.add(temp);
    }
}
