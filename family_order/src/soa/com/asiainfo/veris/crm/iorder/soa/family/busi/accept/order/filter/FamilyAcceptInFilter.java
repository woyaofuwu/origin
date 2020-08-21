
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

import java.util.Iterator;

/**
 * @Description 家庭业务受理入参转换
 * @Auther: zhenggang
 * @Date: 2020/8/18 9:40
 * @version: V1.0
 */
public class FamilyAcceptInFilter implements IFilterIn
{
    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // ---------------------------测试使用-------------------------------
        // String str = "{" + " \"CUST_NAME\": \"测试测试测试\"," + " \"EPARCHY_CODE\": \"0898\"," + " \"FAMILY_BRAND_CODE\":
        // \"RH01\"," + " \"FAMILY_PRODUCT_ID\": \"96041734\"," + " \"FAMILY_PRODUCT_MODE\": \"05\","
        // + " \"HEAD_PSPT_ID\": \"782346287\"," + " \"HEAD_PSPT_TYPE_CODE\": \"A\"," + " \"HEAD_SERIAL_NUMBER\":
        // \"18889252722\"," + " \"HOME_ADDRESS\": \"陕西省西安市长安区\"," + " \"HOME_NAME\": \"测试测试测试的家\","
        // + " \"HOME_PHONE\": \"18889252722\"," + " \"IS_EFFECT_NOW\": \"off\"," + " \"MANAGER_SN\": \"18889252722\","
        // + " \"OFFERS\": \"[{\\\"ELEMENT_ID\\\":\\\"4868\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 00:00:00.0\\\",\\\"ELEMENT_NAME\\\":\\\"0元500M流量包\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-09-01\\\",\\\"ROLE_CODE\\\":\\\"0\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"-1\\\"},{\\\"ELEMENT_ID\\\":\\\"5898\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 00:00:00.0\\\",\\\"ELEMENT_NAME\\\":\\\"0元1G国内流量体验包\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-09-01\\\",\\\"ROLE_CODE\\\":\\\"0\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"-1\\\"},{\\\"ELEMENT_ID\\\":\\\"80020292\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-11-01
        // 23:59:59.0\\\",\\\"ELEMENT_NAME\\\":\\\"50元500分钟语音加油包\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-08-08
        // 16:39:56\\\",\\\"ROLE_CODE\\\":\\\"0\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"-1\\\"}]\","
        // + " \"REMARK\": \"\"," + " \"ROUTE_EPARCHY_CODE\": \"0898\"," + " \"SUBMIT_SOURCE\": \"CRM_PAGE\"," + "
        // \"SUBMIT_TYPE\": \"submit\","
        // + " \"SUB_ROLES\":
        // \"[{\\\"SERIAL_NUMBER\\\":\\\"18889252722\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889252722\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"ROLE_TYPE\\\":\\\"OLD\\\",\\\"FAMILY_PAY\\\":\\\"true\\\",\\\"FAMILY_SHARE\\\":\\\"true\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"10004000\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"动感地带15元音乐套餐\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"P\\\",\\\"START_DATE\\\":\\\"2020-09-01\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000816\\\"},{\\\"ELEMENT_ID\\\":\\\"84018120\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"语音翻番包10元50分钟\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-08-08
        // 16:40:35\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000816\\\"}],\\\"SUB_ROLES\\\":[{\\\"SERIAL_NUMBER\\\":\\\"KD_18889252722\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889252722\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"2\\\",\\\"ROLE_TYPE\\\":\\\"NEW\\\",\\\"FAMILY_PAY\\\":\\\"true\\\",\\\"FAMILY_SHARE\\\":\\\"false\\\",\\\"PHONE_ACCT_ID\\\":\\\"18889252722\\\",\\\"AUTH_SERIAL_NUMBER\\\":\\\"18889252722\\\",\\\"WIDE_ACCT_ID\\\":\\\"KD_18889252722\\\",\\\"TRADE_EPARCHY_CODE\\\":\\\"\\\",\\\"CONTACT\\\":\\\"郑大爷\\\",\\\"CONTACT_PHONE\\\":\\\"18889252722\\\",\\\"DETAIL_ADDRESS\\\":\\\"海南省海口市龙华区国贸路国贸中心大院16号楼（覆盖本层）\\\",\\\"DEVICE_ID\\\":\\\"111111\\\",\\\"FLOOR_AND_ROOM_NUM\\\":\\\"11\\\",\\\"FLOOR_AND_ROOM_NUM_FLAG\\\":\\\"0\\\",\\\"HGS_WIDE\\\":\\\"0\\\",\\\"IS_NEED_MODEM\\\":\\\"1\\\",\\\"IS_WIDEBING\\\":\\\"0\\\",\\\"MODEM_DEPOSIT\\\":\\\"0\\\",\\\"MODEM_STYLE\\\":\\\"0\\\",\\\"OPEN_TYPE\\\":\\\"FTTH\\\",\\\"PHONE\\\":\\\"18889252722\\\",\\\"SALE_ACTIVE_EXPLAIN2\\\":\\\"智能网关调测费0折包(预受理)\\\",\\\"SALE_ACTIVE_FEE2\\\":\\\"0\\\",\\\"SALE_ACTIVE_ID2\\\":\\\"2810\\\",\\\"STAND_ADDRESS\\\":\\\"海南省海口市龙华区国贸路国贸中心大院16号楼（覆盖本层）\\\",\\\"STAND_ADDRESS_CODE\\\":\\\"\\\",\\\"SUGGEST_DATE\\\":\\\"\\\",\\\"TRADE_TYPE_CODE\\\":\\\"600\\\",\\\"WIDENET_PAY_MODE\\\":\\\"A\\\",\\\"WIDE_PRODUCT_TYPE\\\":\\\"3\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"84010440\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"FTTH宽带产品100M套餐(2018)\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"P\\\",\\\"START_DATE\\\":\\\"2020-08-08\\\",\\\"ROLE_CODE\\\":\\\"2\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000821\\\"}],\\\"SUB_ROLES\\\":[{\\\"SERIAL_NUMBER\\\":\\\"089831305245\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889252722\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"3\\\",\\\"ROLE_TYPE\\\":\\\"NEW\\\",\\\"FAMILY_PAY\\\":\\\"true\\\",\\\"FAMILY_SHARE\\\":\\\"false\\\",\\\"BRAND\\\":\\\"IMS固话\\\",\\\"PRODUCT_TYPE_CODE\\\":\\\"IMSP\\\",\\\"TRADE_TYPE_CODE\\\":\\\"6800\\\",\\\"FIX_NUMBER\\\":\\\"31305245\\\",\\\"OLD_FIX_NUMBER\\\":\\\"31305245\\\",\\\"WIDE_SERIAL_NUMBER\\\":\\\"31305245\\\",\\\"TT_TRANSFER\\\":\\\"0\\\",\\\"HAS_REAL_NAME_INFO\\\":\\\"HAS_REAL_NAME_INFO\\\",\\\"PSPT_ID\\\":\\\"61012119891127107X\\\",\\\"PSPT_TYPE_CODE\\\":\\\"0\\\",\\\"CUST_NAME\\\":\\\"郑大爷\\\",\\\"BIRTHDAY\\\":\\\"\\\",\\\"PSPT_END_DATE\\\":\\\"2024-12-31\\\",\\\"PSPT_ADDR\\\":\\\"陕西省西安市长安区\\\",\\\"SEX\\\":\\\"M\\\",\\\"FOLK_CODE\\\":\\\"05\\\",\\\"AGENT_CUST_NAME\\\":\\\"\\\",\\\"AGENT_PSPT_TYPE_CODE\\\":\\\"\\\",\\\"AGENT_PSPT_ID\\\":\\\"\\\",\\\"AGENT_PSPT_ADDR\\\":\\\"\\\",\\\"USE\\\":\\\"\\\",\\\"USE_PSPT_TYPE_CODE\\\":\\\"\\\",\\\"USE_PSPT_ID\\\":\\\"\\\",\\\"USE_PSPT_ADDR\\\":\\\"\\\",\\\"PIC_ID\\\":\\\"\\\",\\\"AGENT_PIC_ID\\\":\\\"\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"84018046\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"和家固话9元套餐\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"P\\\",\\\"START_DATE\\\":\\\"2020-08-08\\\",\\\"ROLE_CODE\\\":\\\"3\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000211\\\"}]},{\\\"SERIAL_NUMBER\\\":\\\"18889252722\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889252722\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"4\\\",\\\"ROLE_TYPE\\\":\\\"NEW\\\",\\\"FAMILY_PAY\\\":\\\"false\\\",\\\"FAMILY_SHARE\\\":\\\"false\\\",\\\"PRODUCT_ID\\\":\\\"19091909\\\",\\\"BASE_PACKAGES\\\":\\\"40227762\\\",\\\"PLATSVC_PACKAGES\\\":\\\"80175705\\\",\\\"OPTION_PACKAGES\\\":\\\"\\\",\\\"WIDE_ADDRESS\\\":\\\"\\\",\\\"RSRV_STR4\\\":\\\"\\\",\\\"MO_PRODUCT_ID2\\\":\\\"66000308\\\",\\\"MO_PACKAGE_ID2\\\":\\\"79082910\\\",\\\"TOP_SET_BOX_SALE_ACTIVE_ID2\\\":\\\"2910\\\",\\\"TOP_SET_BOX_SALE_ACTIVE_FEE2\\\":\\\"0\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"99418142\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"宽带融合套餐10元魔百和优惠\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-08-08
        // 16:44:16\\\",\\\"ROLE_CODE\\\":\\\"4\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000216\\\"}]}]}]},{\\\"SERIAL_NUMBER\\\":\\\"18889973785\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889973785\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"ROLE_TYPE\\\":\\\"OLD\\\",\\\"FAMILY_PAY\\\":\\\"true\\\",\\\"FAMILY_SHARE\\\":\\\"true\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"10004000\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"动感地带15元音乐套餐\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"P\\\",\\\"START_DATE\\\":\\\"2020-09-01\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000816\\\"},{\\\"ELEMENT_ID\\\":\\\"84018120\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"语音翻番包10元50分钟\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"D\\\",\\\"START_DATE\\\":\\\"2020-08-08
        // 17:08:48\\\",\\\"ROLE_CODE\\\":\\\"1\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000816\\\"}],\\\"SUB_ROLES\\\":[{\\\"SERIAL_NUMBER\\\":\\\"KD_18889973785\\\",\\\"MEMBER_MAIN_SN\\\":\\\"18889973785\\\",\\\"EPARCHY_CODE\\\":\\\"0898\\\",\\\"ROLE_CODE\\\":\\\"2\\\",\\\"ROLE_TYPE\\\":\\\"OLD\\\",\\\"FAMILY_PAY\\\":\\\"true\\\",\\\"FAMILY_SHARE\\\":\\\"false\\\",\\\"PHONE_ACCT_ID\\\":\\\"18889973785\\\",\\\"AUTH_SERIAL_NUMBER\\\":\\\"18889973785\\\",\\\"WIDE_ACCT_ID\\\":\\\"KD_18889973785\\\",\\\"TRADE_EPARCHY_CODE\\\":\\\"\\\",\\\"OFFERS\\\":[{\\\"ELEMENT_ID\\\":\\\"84010440\\\",\\\"MODIFY_TAG\\\":\\\"0\\\",\\\"END_DATE\\\":\\\"2050-12-31
        // 23:59:59\\\",\\\"ELEMENT_NAME\\\":\\\"FTTH宽带产品100M套餐(2018)\\\",\\\"ELEMENT_TYPE_CODE\\\":\\\"P\\\",\\\"START_DATE\\\":\\\"2020-09-01\\\",\\\"ROLE_CODE\\\":\\\"2\\\",\\\"PRODUCT_ID\\\":\\\"96041734\\\",\\\"PACKAGE_ID\\\":\\\"1000821\\\"}]}]}]\","
        // + " \"TRADE_TYPE_CODE\": \"450\"," + " \"listener\": \"onTradeSubmit\"," + " \"page\": \"family.Accept\"," + "
        // \"service\": \"ajax\"" + "}";
        // // 必填参数校验
        // IData temp = new DataMap(str);
        // input = temp;
        // ---------------------------测试使用-------------------------------
        IDataUtil.chkParam(input, "EPARCHY_CODE");
        String familyProductId = IDataUtil.chkParam(input, "FAMILY_PRODUCT_ID");// 家庭融合产品
        String managerSn = IDataUtil.chkParam(input, "MANAGER_SN");// 管理员号码
        IDataUtil.chkParam(input, "ROUTE_EPARCHY_CODE");

        // 家庭融合受理公共入参
        input.put("ROLE_CODE", FamilyRolesEnum.FAMILY.getRoleCode());
        input.put("ROLE_TYPE", FamilyConstants.TYPE_NEW);
        input.put("TRADE_TYPE_CODE", FamilyConstants.FamilyTradeType.ACCEPT.getValue());
        input.put("MEMBER_MAIN_SN", managerSn);// 成员归属号码，家庭角色默认为管理员号码

        // 家庭产品相关入参校验和补齐
        IData fmyProdCha = FamilyMemUtil.getFamilyOfferComChas(familyProductId);
        IData memberNum = fmyProdCha.getData("MEMBER_NUM");// 成员最大最小数配置
        IData comChas = fmyProdCha.getData("COMCHA");// 商品构成属性配置
        String productMode = comChas.getString("PRODUCT_MODE");
        String brandCode = comChas.getString("BRAND_CODE");

        // 校验家庭商品是否正确
        if (!FamilyConstants.FAMILY_PRODUCT_MODE.equals(productMode) || !FamilyConstants.FAMILY_BRAND_CODE.equals(brandCode))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_20, familyProductId);
        }

        // 管理员信息查询
        IData userInfo = UcaInfoQry.qryUserInfoBySn(managerSn);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, managerSn);
        }
        String custId = userInfo.getString("CUST_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE");
        // 加载管理员客户信息
        IData custInfo = UcaInfoQry.qryPerInfoByCustId(custId, eparchyCode);
        input.put("HEAD_SERIAL_NUMBER", managerSn);
        input.put("HEAD_PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
        input.put("HEAD_PSPT_ID", custInfo.getString("PSPT_ID"));
        input.put("HOME_PHONE", managerSn);
        input.put("HOME_ADDRESS", custInfo.getString("HOME_ADDRESS"));
        input.put("HOME_NAME", custInfo.getString("CUST_NAME") + "的家");
        input.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        // 单个成员校验
        this.checkSubRoles(input, memberNum);
        // 家庭业务提交校验
        FamilyCallerBean.busiCheckNoCatch(input, FamilyConstants.TriggerPoint.BEFORE_CHECK.toString());
    }

    private void checkSubRoles(IData input, IData memberNum) throws Exception
    {
        String managerSn = IDataUtil.chkParam(input, "MANAGER_SN");// 管理员号码

        IData roleCount = new DataMap();
        IDataset roleList = new DatasetList();
        // 获取组装角色信息
        this.getSubRoles(input, null, roleCount, roleList);
        // 1.管理员号码必须在手机角色中
        boolean isManagerRole = false;
        for (Object obj : roleList)
        {
            IData role = (IData) obj;
            String roleCode = role.getString("ROLE_CODE");
            String serialNumber = role.getString("SERIAL_NUMBER");

            if (FamilyRolesEnum.PHONE.getRoleCode().equals(roleCode) && managerSn.equals(serialNumber))
            {
                isManagerRole = true;
                break;
            }
        }
        if (!isManagerRole)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_21, managerSn);
        }

        // 2.校验家庭融合产品配置的角色最大最小数
        for (Iterator iter = memberNum.keySet().iterator(); iter.hasNext();)
        {
            String key = (String) iter.next();
            if (key.startsWith("MIN_MEMBER_"))
            {
                int minNum = memberNum.getInt(key);
                if (minNum == -1)
                {
                    continue;
                }
                String tempRoleCode = key.replace("MIN_MEMBER_", "");
                int rowNum = roleCount.getInt(tempRoleCode, 0);
                if (rowNum < minNum)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_22, minNum, FamilyRolesEnum.getRoleName(tempRoleCode));
                }
            }
            else if (key.startsWith("MAX_MEMBER_"))
            {
                int maxNum = memberNum.getInt(key);
                if (maxNum == -1)
                {
                    continue;
                }
                String tempRoleCode = key.replace("MAX_MEMBER_", "");
                int rowNum = roleCount.getInt(tempRoleCode, 0);
                if (rowNum > maxNum)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_23, maxNum, FamilyRolesEnum.getRoleName(tempRoleCode));
                }
            }
        }

        // 3.成员个数校验
        this.checkSubRoleCount(roleCount, roleList);

        // 4.递归角色平铺处理，循环调用单个角色校验规则
        for (Object obj : roleList)
        {
            IData temp = new DataMap();
            IData role = (IData) obj;
            temp.putAll(role);
            input.put(KeyConstants.BUSI_TYPE, FamilyConstants.FamilyTradeType.ACCEPT.getValue());
            FamilyCallerBean.busiCheckNoCatch(input, FamilyConstants.TriggerPoint.ADD_MEMBER.toString());
        }
    }

    /**
     * @Description: 每个手机成员下子成员个数校验
     * @Param: [roleCount, roleList]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/8/18 15:39
     */
    private void checkSubRoleCount(IData roleCount, IDataset roleList) throws Exception
    {
        IDataset phoneRoleList = FamilyMemUtil.getRoleListByRoleCodeGroupSn(roleList, FamilyRolesEnum.PHONE.getRoleCode(), null);
        for (Object obj : phoneRoleList)
        {
            IData phoneRole = (IData) obj;

            String serialNumber = phoneRole.getString("SERIAL_NUMBER");

            for (Iterator iter = roleCount.keySet().iterator(); iter.hasNext();)
            {
                String key = (String) iter.next();
                if (FamilyRolesEnum.PHONE.getRoleCode().equals(key))
                {
                    // 手机角色不处理
                    continue;
                }
                IDataset subRoleList = FamilyMemUtil.getRoleListByRoleCodeGroupSn(roleList, key, serialNumber);
                if (subRoleList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_24, serialNumber, FamilyRolesEnum.getRoleName(key));
                }
            }

        }
    }

    /**
     * @Description: 获取角色列表
     * @Param: [param, groupSn, roleCount, roleList]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/8/18 14:49
     */
    private void getSubRoles(IData param, String groupSn, IData roleCount, IDataset roleList) throws Exception
    {
        if (param.containsKey("SUB_ROLES"))
        {
            IDataset subRoles = FamilyCallerUtil.getDataset(param, "SUB_ROLES");

            for (Object obj : subRoles)
            {
                IData subRole = (IData) obj;
                String serialNumber = IDataUtil.chkParam(subRole, "SERIAL_NUMBER");// 成员角色号码
                String roleCode = IDataUtil.chkParam(subRole, "ROLE_CODE");// 成员角色编码

                // 1.给手机角色下的子角色赋值MEMBER_MAIN_SN归属号码
                if (FamilyRolesEnum.PHONE.getRoleCode().equals(roleCode))
                {
                    subRole.put("MEMBER_MAIN_SN", serialNumber);
                }
                else
                {
                    subRole.put("MEMBER_MAIN_SN", groupSn);
                }

                if (roleCount.containsKey(roleCode))
                {
                    roleCount.put(roleCode, roleCount.getInt(roleCode) + 1);
                }
                else
                {
                    roleCount.put(roleCode, 1);
                }
                roleList.add(subRole);
                getSubRoles(subRole, subRole.getString("MEMBER_MAIN_SN"), roleCount, roleList);
            }

            // 覆盖入参传入SUB_ROLES
            param.put("SUB_ROLES", subRoles);
        }
    }
}
