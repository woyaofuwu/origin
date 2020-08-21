
package com.asiainfo.veris.crm.iorder.soa.family.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.PhoneSubRole;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.UserProd;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * @Description 家庭成员处理工具类
 * @Auther: zhenggang
 * @Date: 2020/7/23 15:10
 * @version: V1.0
 */
public class FamilyMemUtil
{
    /**
     * @Description: 根据属性编码获取配置属性值
     * @Param: [chas, chaCode]
     * @return: java.lang.String
     * @Author: zhenggang
     * @Date: 2020/7/24 15:52
     */
    public static String getChaValue(IDataset chas, String chaCode) throws Exception
    {
        String chaValue = "";
        for (Object obj : chas)
        {
            IData cha = (IData) obj;
            String tmpChaCode = cha.getString("CHA_CODE");
            String tmpChaValue = cha.getString("CHA_VALUE");
            if (chaCode.equals(tmpChaCode))
            {
                chaValue = tmpChaValue;
                break;
            }
        }
        return chaValue;
    }

    /**
     * @Description: 获取用户所有有效主产品数据
     * @Param: [userId]
     * @return: com.ailk.common.data.IData
     * @Author: zhenggang
     * @Date: 2020/7/24 15:53
     */
    public static UserProd getUserAllValidMainProduct(String userId) throws Exception
    {
        // 查询家庭产品资料
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);

        UserProd userProd = new UserProd();

        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                String userProductId = userProduct.getString("PRODUCT_ID");
                String startDate = userProduct.getString("START_DATE");
                String endDate = userProduct.getString("END_DATE");
                if (startDate.compareTo(sysDate) < 0)
                {
                    userProd.setUserProductId(userProductId);
                    userProd.setUserProductStartDate(startDate);
                    userProd.setUserProductEndDate(endDate);
                }
                else
                {
                    userProd.setNextProductId(userProductId);
                    userProd.setNextProductStartDate(startDate);
                    userProd.setNextProductEndDate(endDate);
                }
            }
        }
        return userProd;
    }

    /**
     * @Description: 查询家庭产品角色构成
     * @Param: [familyProductId, eparchyCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/24 17:35
     */
    public static IDataset getFamilyRoleOffers(String familyProductId, String eparchyCode) throws Exception
    {
        IDataset elements = new DatasetList();
        IDataset groupInfos = UpcCallIntf.queryFamilyOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, familyProductId, null, null);
        if (IDataUtil.isEmpty(groupInfos))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_7);
        }

        for (int i = 0; i < groupInfos.size(); i++)
        {
            IData role = new DataMap();

            IData pkg = groupInfos.getData(i);
            role.put("ROLE_CODE", pkg.getString("ROLE_CODE"));
            role.put("ROLE_NAME", pkg.getString("GROUP_NAME"));
            role.put("GROUP_ID", pkg.getString("GROUP_ID"));
            elements.add(role);
            IDataset offers = UpcCallIntf.queryFamilyGroupComRelOfferPriv(role.getString("GROUP_ID"), eparchyCode);
            if (IDataUtil.isNotEmpty(offers))
            {
                IDataset groupOffers = new DatasetList();
                for (int j = 0; j < offers.size(); j++)
                {
                    IData offer = offers.getData(j);
                    IData temp = new DataMap();
                    temp.put("OFFER_CODE", offer.getString("OFFER_CODE"));
                    temp.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
                    temp.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                    temp.put("DESCRIPTION", offer.getString("DESCRIPTION"));
                    temp.put("GROUP_ID", offer.getString("GROUP_ID", "-1"));
                    temp.put("SELECT_FLAG", offer.getString("SELECT_FLAG"));
                    temp.put("ORDER_MODE", offer.getString("ORDER_MODE"));
                    temp.put("PRODUCT_ID", familyProductId);
                    groupOffers.add(temp);
                }
                role.put("OFFERS", groupOffers);// 得到组下的所有商品
            }
        }

        // 家庭产品下必选商品
        IDataset comRels = UpcCallIntf.queryOfferComRelOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, familyProductId, eparchyCode);
        // 家庭商品下可选商品
        IDataset joinRels = UpcCallIntf.queryOfferJoinRelAndOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, familyProductId, FamilyConstants.OFFER.JOIN_REL, null, null);

        IDataset subOffers = new DatasetList();
        fillSubOffers(subOffers, comRels, familyProductId);
        fillSubOffers(subOffers, joinRels, familyProductId);
        if (IDataUtil.isNotEmpty(subOffers))
        {
            IData role = new DataMap();
            role.put("ROLE_CODE", FamilyRolesEnum.FAMILY.getRoleCode());
            role.put("ROLE_NAME", FamilyRolesEnum.FAMILY.getRoleName());
            role.put("GROUP_ID", FamilyConstants.OFFER.FAKE_ID);
            role.put("OFFERS", subOffers);
            elements.add(role);
        }

        return elements;
    }

    private static void fillSubOffers(IDataset subOffers, IDataset relOffers, String familyProductId) throws Exception
    {
        if (IDataUtil.isNotEmpty(relOffers))
        {
            for (Object obj : relOffers)
            {
                IData temp = new DataMap();
                IData relOffer = (IData) obj;
                temp.put("OFFER_CODE", relOffer.getString("OFFER_CODE"));
                temp.put("OFFER_TYPE", relOffer.getString("OFFER_TYPE"));
                temp.put("OFFER_NAME", relOffer.getString("OFFER_NAME"));
                temp.put("DESCRIPTION", relOffer.getString("DESCRIPTION"));
                temp.put("GROUP_ID", relOffer.getString("GROUP_ID", "-1"));
                temp.put("SELECT_FLAG", relOffer.getString("SELECT_FLAG"));
                temp.put("ORDER_MODE", relOffer.getString("ORDER_MODE"));
                temp.put("PRODUCT_ID", familyProductId);
                subOffers.add(temp);
            }
        }
    }

    /**
     * @Description: 获取家庭产品构成属性
     * @Param: [familyProductId]
     * @return: com.ailk.common.data.IData
     * @Author: zhenggang
     * @Date: 2020/7/24 17:52
     */
    public static IData getFamilyOfferComChas(String familyProductId) throws Exception
    {
        IData result = new DataMap();

        IData minMaxCha = new DataMap();
        IData offerCha = new DataMap();
        IDataset comCha = UpcCallIntf.queryFamilyOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, familyProductId, null);
        if (IDataUtil.isNotEmpty(comCha))
        {
            for (int i = 0; i < comCha.size(); i++)
            {
                IData temp = comCha.getData(i);
                if (temp.getString("FIELD_NAME").indexOf("_MEMBER_") != -1)
                {
                    minMaxCha.put(temp.getString("FIELD_NAME"), temp.getString("FIELD_VALUE"));
                }
                else
                {
                    offerCha.put(temp.getString("FIELD_NAME"), temp.getString("FIELD_VALUE"));
                }
            }
        }
        result.put("MEMBER_NUM", minMaxCha);
        result.put("COMCHA", offerCha);
        return result;
    }

    /**
     * @Description: 根据用户号码查询家庭用户下所有成员和成员属性信息
     * @Param: [userId]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/8/19 15:21
     */
    public static IDataset getFamilyMemberAndChaInfos(String userId) throws Exception
    {
        IDataset memberList = FamilyUserMemberQuery.queryFamilyMemInfoByMemberUserIdAndRole(userId, FamilyRolesEnum.PHONE.getRoleCode());

        if (IDataUtil.isEmpty(memberList))
        {
            return null;
        }

        if (memberList.size() > 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_5, userId);
        }
        IData member = memberList.first();
        String familyUserId = member.getString("FAMILY_USER_ID");
        String familySerialNum = member.getString("FAMILY_SERIAL_NUM");
        IDataset allMemberList = FamilyUserMemberQuery.queryAllMembersByFmyUserIdAndFmySn(familyUserId, familySerialNum);
        if (IDataUtil.isNotEmpty(allMemberList))
        {
            for (Object obj : allMemberList)
            {
                IData allMember = (IData) obj;
                String memberUserId = allMember.getString("MEMBER_USER_ID");
                String instId = allMember.getString("INST_ID");
                String roleCode = allMember.getString("ROLE_CODE");
                allMember.put("ROLE_NAME", FamilyRolesEnum.getRoleName(roleCode));// 转义角色名称
                IDataset memerChas = FamilyMemberChaInfoQry.queryNowValidFamilyMemberChasByMemberUserIdAndRelInstId(memberUserId, instId);
                for (Object object : memerChas)
                {
                    IData memberCha = (IData) object;
                    allMember.put(memberCha.getString("CHA_CODE"), memberCha.getString("CHA_VALUE"));
                }
            }
        }
        DataHelper.sort(memberList, "INST_ID", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return memberList;
    }

    /**
     * @Description: 判断成员用户是否管理员
     * @Param: [allMembers, userId]
     * @return: boolean
     * @Author: zhenggang
     * @Date: 2020/8/19 16:08
     */
    public static boolean isFamilyManager(IDataset allMembers, String userId) throws Exception
    {
        boolean isManager = false;

        if (IDataUtil.isNotEmpty(allMembers))
        {
            for (Object obj : allMembers)
            {
                IData allMember = (IData) obj;
                String memberUserId = allMember.getString("MEMBER_USER_ID");
                if (!userId.equals(memberUserId))
                {
                    continue;
                }
                String fmyMgr = allMember.getString(FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
                if (FamilyConstants.SWITCH.YES.equals(fmyMgr))
                {
                    isManager = true;
                }
            }
        }
        return isManager;
    }

    /**
     * @Description: 根据角色编码和归属号码查询子成员
     * @Param: [roleList, roleCode, groupSn]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/8/18 15:39
     */
    public static IDataset getRoleListByRoleCodeGroupSn(IDataset roleList, String roleCode, String groupSn) throws Exception
    {
        IDataset singleRoleList = new DatasetList();
        for (Object obj : roleList)
        {
            IData singleRole = (IData) obj;
            String tmpRoleCode = singleRole.getString("ROLE_CODE");
            String memberMainSn = singleRole.getString("MEMBER_MAIN_SN");// 接口入参
            if (StringUtils.isEmpty(memberMainSn))
            {
                memberMainSn = singleRole.getString("MAIN_SERIAL_NUM");// 表字段
            }
            if (roleCode.equals(tmpRoleCode))
            {
                if (StringUtils.isNotEmpty(groupSn) && !groupSn.equals(memberMainSn))
                {
                    continue;
                }
                singleRoleList.add(singleRole);
            }
        }
        return singleRoleList;
    }

    /**
     * @author yuyz
     * @Description 查询手机角色下的固话成员信息
     * @Date 10:52 2020/8/6
     * @Param [data]
     * @return com.ailk.common.data.IDataset
     **/
    public static IDataset queryImsMembersOfPhone(String sn) throws Exception
    {
        if (StringUtils.isBlank(sn))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_17);
        }
        IDataset userInfos = UserInfoQry.getUserInfoBySn(sn, "0");
        if (IDataUtil.isNotEmpty(userInfos))
        {
            String userIdB = userInfos.getData(0).getString("USER_ID");
            IDataset virtualIms = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "1");
            if (IDataUtil.isNotEmpty(virtualIms))
            {
                IData virIms = virtualIms.getData(0);// 暂时取首条记录
                String userIdA = virIms.getString("USER_ID_A");
                IDataset imsRelas = RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
                if (IDataUtil.isNotEmpty(imsRelas))
                {
                    IDataset imsInfos = new DatasetList();
                    for (int i = 0; i < imsRelas.size(); i++)
                    {
                        IData imsRela = imsRelas.getData(i);
                        String imsUserId = imsRela.getString("USER_ID_B");
                        String imsNum = imsRela.getString("SERIAL_NUMBER_B");
                        IDataset imsUsers = UserInfoQry.getUserInfoByUserId(imsUserId, "0", "0898");
                        imsInfos.addAll(imsUsers);
                    }
                    return imsInfos;
                }
            }
        }
        return new DatasetList();
    }

    /**
     * @author yuyz
     * @Description 根据家庭用户虚拟号码查询家庭用户所有成员信息
     * @Date 15:27 2020/8/6
     * @Param [sn]
     * @return com.ailk.common.data.IDataset
     **/
    public static IDataset qryAllRoleMemsByFMSn(String sn) throws Exception
    {
        if (StringUtils.isBlank(sn))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_17);
        }
        IDataset familyUsers = UserInfoQry.getUserInfoBySn(sn, "0", "09");// 查询家庭用户信息
        if (IDataUtil.isNotEmpty(familyUsers))
        {
            IData familyUser = familyUsers.getData(0);
            String fUserId = familyUser.getString("USER_ID");
            IDataset allMembes = FamilyUserMemberQuery.queryMembersByUserFamilyUserId(fUserId);
            return allMembes;
        }
        return null;
    }

    /**
     * @author yuyz
     * @Description 根据家庭用户userid，查询出所有角色信息
     * @Date 15:47 2020/8/6
     * @Param [userId]
     * @return com.ailk.common.data.IDataset
     **/
    public static IDataset qryAllRoleMemsByFMUId(String userId) throws Exception
    {
        if (StringUtils.isBlank(userId))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_17);
        }
        IDataset allMembes = FamilyUserMemberQuery.queryMembersByUserFamilyUserId(userId);
        return allMembes;
    }

    /**
     * @author yuyz
     * @Description 根据管理员的userId,查询家庭用户下所有的角色信息
     * @Date 11:17 2020/8/18
     * @Param [manageUserId]
     * @return com.ailk.common.data.IDataset
     **/
    public static IDataset qryAllRoleMemsByManageUId(String manageUserId) throws Exception
    {
        if (StringUtils.isBlank(manageUserId))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_17);
        }
        IDataset allMembes = FamilyUserMemberQuery.queryAllMembersByMangerUId(manageUserId);
        return allMembes;
    }

    /**
     * 查询手机下的所有宽带，当前只有一条 duhj
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryWideMembers(String phoneSn) throws Exception
    {
        IDataset wideInfos = new DatasetList();
        IData wUserInfo = UcaInfoQry.qryUserInfoBySn("KD_" + phoneSn);// 暂时先这样查，后续47关系会改为48，后续再整理
        if (IDataUtil.isNotEmpty(wUserInfo))
        {
            wideInfos.add(wUserInfo);
        }
        return wideInfos;
    }

    /**
     * 判断用户是否是管理员
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static boolean isFamilyManagerUser(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, userId);
        IDataset familyChas = FamilyDao.qryByCode(FamilySQLEnum.FAMILY_USER_INFO_BY_MANAGER_USER_ID, param);
        if (IDataUtil.isNotEmpty(familyChas))
        {
            return true;
        }
        return false;
    }

    /**
     * @Description: 查看手机用户下是否已经办理宽带、固话或者魔百和
     * @Param: [phoneRoleSn, roleCode]
     * @return: boolean
     * @Author: zhenggang
     * @Date: 2020/8/20 15:07
     */
    public static PhoneSubRole phoneUserHasRoleByRoleCode(String phoneRoleSn, String roleCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", phoneRoleSn);
        param.put("ROLE_CODE", roleCode);
        IData result = CSAppCall.call("SS.FamilyWideNetSVC.checkIsHasChildrenRole", param).first();
        PhoneSubRole psr = new PhoneSubRole();
        if (FamilyRolesEnum.PHONE.getRoleCode().equals(roleCode))
        {
            if (IDataUtil.isNotEmpty(result) && FamilyConstants.SWITCH.YES.equals(result.getString("IS_HAS_WIDE")))
            {
                psr.setHasWideNet(true);
            }
        }

        if (FamilyRolesEnum.WIDENET.getRoleCode().equals(roleCode))
        {
            if (IDataUtil.isNotEmpty(result) && FamilyConstants.SWITCH.YES.equals(result.getString("IS_HAS_IMS")))
            {
                psr.setHasIms(true);
                psr.setImsSn(result.getString("USER_IMS_NUM"));
                psr.setImsUserId(result.getString("USER_IMS_USER_ID"));
            }
            if (IDataUtil.isNotEmpty(result) && FamilyConstants.SWITCH.YES.equals(result.getString("IS_HAS_TV")))
            {
                psr.setHasMbh(true);
            }
        }

        return psr;
    }
}
