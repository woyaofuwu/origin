
package com.asiainfo.veris.crm.iorder.soa.family.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * 家庭用户关系查询
 *
 * @author wych
 */
public class FamilyUserMemberQuery
{
    /**
     * 通过成员用户id查询家庭用户关系
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryMembersByMemberUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, userId);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.MEMBER_BY_MEMBER_USER_ID, param);
        return result;
    }

    /**
     * 通过成员用户id、成员角色查询家庭成员
     *
     * @Param memberSerialNumber,roleCode
     * @Author wuwangfeng
     * @Date 2020/8/6 17:30
     */
    public static IDataset queryFamilyMemInfoByMemberUserIdAndRole(String userId, String roleCode) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, userId);
        param.put(KeyConstants.MEMBER_ROLE_CODE, roleCode);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_FAMILY_MEMBER_BY_MEMBER_USER_ID_AND_ROLE, param);
        return result;
    }

    /**
     * 通过家庭用户id查询家庭用户关系
     *
     * @param familyUserId
     * @return
     * @throws Exception
     */
    public static IDataset queryMembersByUserFamilyUserId(String familyUserId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.FAMILY_USER_ID, familyUserId);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.MEMBER_BY_FAMILY_USER_ID, param);
        return result;
    }

    /**
     * 通过家庭用户id查询所有家庭用户关系
     *
     * @param familyUserId
     * @return
     * @throws Exception
     */
    public static IDataset queryAllMembersByFamilyUserId(String familyUserId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.FAMILY_USER_ID, familyUserId);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.ALL_MEMBER_BY_FAMILY_USER_ID, param);
        return result;
    }

    /**
     * 通过家庭用户id和成员ID查询家庭用户关系
     *
     * @param familyUserId
     * @return
     * @throws Exception
     */
    public static IDataset queryinfoByFamilyUserIdMemebrUserId(String familyUserId, String memberUserId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.FAMILY_USER_ID, familyUserId);
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);

        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_BY_FAMILY_USER_ID_MEMBER_USER_ID, param);
        return result;
    }

    /**
     * @Description: 根据成员ID和成员角色查询家庭中该类角色的所有有效的成员
     * @Param: [memberUserId, roleCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 16:48
     */
    public static IDataset queryFamilyMemInfoByMemNumAndRole(String memberUserId, String roleCode) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);
        param.put(KeyConstants.MEMBER_ROLE_CODE, roleCode);

        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_SAME_KIND_MEMBER_BY_MEMBER_USERID_ROLE, param);
        return result;
    }

    /**
     * @Description: 根据成员ID查询家庭中所有实体用户
     * @Param: [memberUserId]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 17:08
     */
    public static IDataset queryFamilyMemInfoByMemNum(String memberUserId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);

        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_FAMILY_MEMBER_INFO_BY_MEMBER_USER_ID, param);
        return result;
    }

    /**
     * @Description: 根据inst_id查询家庭成员信息
     * @Param: [instId]
     * @Author:zhangxi
     */
    public static IDataset queryMembersByUserInstId(String instId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.INST_ID, instId);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_FAMILY_MEMBER_INFO_BY_INST_ID, param);
        return result;
    }

    /**
     * 根据家庭用户编码，和成员角色，查询家庭下所有该角色成员
     * 
     * @Description:
     * @Param: [familyUserId, roleCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 16:55
     */
    public static IDataset queryMembersByFamilyUserIdAndRole(String familyUserId, String roleCode) throws Exception
    {

        IData param = new DataMap();
        param.put(KeyConstants.FAMILY_USER_ID, familyUserId);
        param.put(KeyConstants.MEMBER_ROLE_CODE, roleCode);

        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.MEMBER_BY_FAMILY_USER_ID_AND_ROLE, param);
        return result;

    }

    /**
     * @author yuyz
     * @Description 根据管理员的userId,查询家庭用户下所有的角色信息
     * @Date 11:10 2020/8/18
     * @Param [manageUserId]
     * @return com.ailk.common.data.IDataset
     **/
    public static IDataset queryAllMembersByMangerUId(String manageUserId) throws Exception
    {
        IData param = new DataMap();
        IData result = FamilyUserInfoQry.qryFamilyUserInfoByManagerUserId(manageUserId);
        if (IDataUtil.isNotEmpty(result))
        {
            String fmUserId = result.getString(KeyConstants.USER_ID);
            IDataset allMembes = FamilyUserMemberQuery.queryMembersByUserFamilyUserId(fmUserId);
            return allMembes;
        }
        return null;
    }

    /**
     * @Description: 根据家庭用户ID和家庭用户号码查询所有成员
     * @Param: [familyUserId, familySn]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/8/19 15:28
     */
    public static IDataset queryAllMembersByFmyUserIdAndFmySn(String familyUserId, String familySn) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.FAMILY_USER_ID, familyUserId);
        param.put(KeyConstants.FAMILY_SERIAL_NUM, familySn);
        IDataset result = FamilyDao.qryByCode(FamilySQLEnum.SEL_ALL_MEMBER_BY_FMY_USERID_AND_FMY_SN, param);
        return result;
    }
}
