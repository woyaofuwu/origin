
package com.asiainfo.veris.crm.iorder.soa.family.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;

/**
 * @Description 家庭关系扩展属性查询
 * @Auther: zhenggang
 * @Date: 2020/7/23 14:45
 * @version: V1.0
 */
public class FamilyMemberChaInfoQry
{
    /**
     * @Description: 全部有效属性
     * @Param: [memberUserId, relInstId]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/23 15:19
     */
    public static IDataset queryAllValidFamilyMemberChasByMemberUserIdAndRelInstId(String memberUserId, String relInstId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);
        param.put(KeyConstants.REL_INST_ID, relInstId);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_ALL_VALID_BY_MEMBER_USER_ID_REL_INST_ID, param);
    }

    /**
     * @Description: 当前有效属性
     * @Param: [memberUserId, relInstId]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/23 15:20
     */
    public static IDataset queryNowValidFamilyMemberChasByMemberUserIdAndRelInstId(String memberUserId, String relInstId) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);
        param.put(KeyConstants.REL_INST_ID, relInstId);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_NOW_VALID_BY_MEMBER_USER_ID_REL_INST_ID, param);
    }

    /**
     * @Description: 根据属性编码，用户编码，家庭编码，查询有效属性
     * @Param: [memberUserId, familyUserId, chaCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 11:14
     */
    public static IDataset queryNowValidByMemberUserIdAndFamilyUserIdAndChaCode(String memberUserId, String familyUserId, String chaCode) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);
        param.put(KeyConstants.FAMILY_USER_ID,familyUserId);
        param.put(KeyConstants.CHA_CODE,chaCode);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_BY_MEMBER_USER_ID_FAMILY_ID_CHA_CODE, param);
    }

    /**
     * @Description: 根据用户编码，属性编码查询有效属性
     * @Param: [memberUserId, familyUserId, chaCode]
     * @return: com.ailk.common.data.IDataset
     * @Author:zhangxi
     */
    public static IDataset queryNowValidByMemberUserIdAndChaCode(String memberUserId, String chaCode) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.MEMBER_USER_ID, memberUserId);
        param.put(KeyConstants.CHA_CODE,chaCode);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_NOW_VALID_BY_MEMBER_USER_ID_CHA_CODE, param);
    }

    /**
     * @Description: 根据关联实例ID、属性编码查询当前有效属性
     * @Param: [relInstId, chaCode]
     * @return: com.ailk.common.data.IDataset
     * @Author: wuwangfeng
     * @Date: 2020/8/6 20:19
     */
    public static IDataset queryNowValidFamilyMemberChasByRelInstIdAndChaCode(String relInstId, String chaCode) throws Exception
    {
        IData param = new DataMap();
        param.put(KeyConstants.REL_INST_ID, relInstId);
        param.put(KeyConstants.CHA_CODE,chaCode);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_NOW_VALID_BY_REL_INST_ID_AND_CHA_CODE, param);
    }

}
