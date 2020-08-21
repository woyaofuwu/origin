
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.privm.CheckPriv;

public final class StaffPrivUtil
{
    public static final String PRIV_TYPE_ROLE = "O";// 角色

    public static final String PRIV_TYPE_FIELD = "1";// 域权

    public static final String PRIV_TYPE_DISCNT = "D";// 数据权限(资费权限)

    public static final String PRIV_TYPE_FUNCTION = "F";// 功能权限

    public static final String PRIV_TYPE_PACKAGE = "K";// 包权限(数据权限)

    public static final String PRIV_TYPE_SERVICE = "S";// 服务权限(数据权限)

    public static final String PRIV_TYPE_PRODUCT = "P";// 产品权限(数据权限)

    public static final String PRIV_YD_TRADE_CTRL = "PRIV_YD_TRADE_CTRL"; // 异地业务控制

    public static String getFieldPrivClass(String staffId, String privId) throws Exception
    {
        String rightClass = CheckPriv.checkFieldPermission(staffId, privId, PRIV_TYPE_FIELD);

        return rightClass;
    }

    /**
     * 判断某个工号有哪些资费权限
     * 
     * @param staffId
     * @param checkList
     * @return
     * @throws Exception
     */
    public static Set hasDistPrivList(String staffId, LinkedList<String> checkList) throws Exception
    {
        return CheckPriv.hasDistPrivList(staffId, checkList);
    }

    /**
     * 判断某个工号有哪些域权限
     * 
     * @param staffId
     * @param checkList
     * @return
     * @throws Exception
     */
    public static Set hasFieldPrivList(String staffId, LinkedList<String> checkList) throws Exception
    {
        return CheckPriv.hasPrivList(staffId, checkList, PRIV_TYPE_FIELD);
    }

    /**
     * 判断某个工号有哪些包权限
     * 
     * @param staffId
     * @param checkList
     * @return
     * @throws Exception
     */
    public static Set hasPkgPrivList(String staffId, LinkedList<String> checkList) throws Exception
    {
        return CheckPriv.hasPkgPrivList(staffId, checkList);
    }

    /**
     * 判断某个工号有哪些产品权限
     * 
     * @param staffId
     * @param checkList
     * @return
     * @throws Exception
     */
    public static Set hasProdPrivList(String staffId, LinkedList<String> checkList) throws Exception
    {
        return CheckPriv.hasProdPrivList(staffId, checkList);
    }

    /**
     * 判断某个工号有哪些服务权限
     * 
     * @param staffId
     * @param checkList
     * @return
     * @throws Exception
     */
    public static Set hasSvcPrivList(String staffId, LinkedList<String> checkList) throws Exception
    {
        return CheckPriv.hasSvcPrivList(staffId, checkList);
    }

    /**
     * 是否有资费(如果有多个资费编码,用英文逗号分隔)权限
     * 
     * @param staffId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static boolean isDistPriv(String staffId, String discntCode) throws Exception
    {
        boolean isPriv = isPriv(staffId, discntCode, PRIV_TYPE_DISCNT);

        return isPriv;
    }

    /**
     * 是否有数据权限
     * 
     * @param staffId
     * @param privId
     * @return
     * @throws Exception
     */
    public static boolean isFuncDataPriv(String staffId, String privId) throws Exception
    {
        if (StringUtils.isBlank(privId) || StringUtils.isBlank(staffId))
        {
            return false;
        }
        

        if ("SUPERUSR".equals(staffId))
        {
            return true;
        }

        return CheckPriv.checkPermission(staffId, privId);
    }

    /**
     * 是否有包(如果有多个包ID,用英文逗号隔开)权限
     * 
     * @param staffId
     * @param packageId
     * @return
     * @throws Exception
     */
    public static boolean isPkgPriv(String staffId, String packageId) throws Exception
    {
        boolean isPriv = isPriv(staffId, packageId, PRIV_TYPE_PACKAGE);

        return isPriv;
    }

    /**
     * 是否有指定编码指定类别的权限
     * 
     * @param staffId
     * @param privId
     * @param privType
     * @return
     * @throws Exception
     */
    public static boolean isPriv(String staffId, String privId, String privType) throws Exception
    {
        if (StringUtils.isBlank(privId))
        {
            return false;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return true;
        }

        return CheckPriv.checkPermission(staffId, privId, privType);
    }

    /**
     * 是否有产品权限
     * 
     * @param staffId
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean isProdPriv(String staffId, String productId) throws Exception
    {
        boolean isPriv = isPriv(staffId, productId, PRIV_TYPE_PRODUCT);

        return isPriv;
    }

    /**
     * 是否有传入的OP编码的权限
     * 
     * @param staffId
     * @param opRole
     * @return
     * @throws Exception
     */
    public static boolean isRolePriv(String staffId, String opRole) throws Exception
    {
        if (StringUtils.isBlank(opRole))
        {
            return false;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return true;
        }

        boolean isPriv = CheckPriv.checkOpRolePermission(staffId, opRole);

        return isPriv;
    }

    /**
     * 是否有指定服务的权限
     * 
     * @param staffId
     * @param svcId
     * @return
     * @throws Exception
     */
    public static boolean isSvcPriv(String staffId, String svcId) throws Exception
    {
        boolean isPriv = isPriv(staffId, svcId, PRIV_TYPE_SERVICE);

        return isPriv;
    }

    /**
     * 是否具有省级权限
     * 
     * @param staffId
     * @return
     * @throws Exception
     */
    public static boolean isSysProvince(String staffId) throws Exception
    {
        return isFuncDataPriv(staffId, "SYS_PROVINCE");
    }
}
