
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.VpmnSaleActiveQry;

/**
 * 集团V网营销活动受理接口
 * 
 * @author sungq3
 */
public class VpmnSaleActiveIntf
{
    /**
     * 受理集团V网营销活动
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset vpmnSaleActive(IData inparam) throws Exception
    {
        // 活动类型（2-V网体验活动，9-双网有礼活动）
        String activeType = inparam.getString("ACTIVE_TYPE");

        // 被推荐号码
        String serialNumber = inparam.getString("SERIAL_NUMBER");

        // 推荐号码
        String serialNumberA = inparam.getString("SERIAL_NUMBER_A");

        // 校验被推荐号码和推荐号码
        chkSNandSNAByActiveType(serialNumber, serialNumberA, activeType);

        // 构建受理的业务数据
        IData param = builderSvcData(serialNumber, serialNumberA, activeType);

        // 调用V网营销活动受理服务
        IDataset dataset = CSAppCall.call("SS.VpmnSaleActiveSVC.crtTrade", param);

        // 设置返回数据
        IData result = new DataMap();
        result.put("X_LAST_RESULTINFO", "Trade OK!");
        result.put("X_RESULTCODE", "0");
        result.put("ORDER_ID", dataset.getData(0).getString("ORDER_ID"));
        return IDataUtil.idToIds(result);
    }

    /**
     * 根据活动类型校验被推荐号码和推荐号码
     * 
     * @param serialNumber
     *            被推荐号码
     * @param serialNumberA
     *            推荐号码
     * @param activeType
     *            活动类型
     * @throws Exception
     */
    private static void chkSNandSNAByActiveType(String serialNumber, String serialNumberA, String activeType) throws Exception
    {
        if ("2".equals(activeType))
        {
            // 被推荐号码信息查询(V网免费体验活动)
            querySerialNumberInfo4FeeCheck(serialNumber, activeType);
        }
        else if ("9".equals(activeType))
        {
            // 被推荐号码信息查询(双网有礼活动)
            querySerialNumberInfo4BothWeb(serialNumber, activeType);
        }
        if (!StringUtils.isBlank(serialNumberA))
        { // 推荐号码信息查询
            querySerialNumberAInfo(serialNumberA);
        }
    }

    /**
     * 构建受理的业务数据
     * 
     * @param serialNumber
     *            被推荐号码
     * @param serialNumberA
     *            推荐号码
     * @param activeType
     *            活动类型
     * @throws Exception
     */
    private static IData builderSvcData(String serialNumber, String serialNumberA, String activeType) throws Exception
    {
        IData svcData = new DataMap();
        // 获取被推荐人用户信息
        IDataset memUserInfo = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        IData mem_user_info = memUserInfo.getData(0);
        mem_user_info.put("ACTIVE_TYPE", activeType);
        // 获取被推荐人客户信息
        IData memCustInfo = UcaInfoQry.qryPerInfoByCustId(mem_user_info.getString("CUST_ID"));
        IData mem_cust_info = memCustInfo;
        // 获取被推荐人账户信息
        IData memAcctInfo = UcaInfoQry.qryAcctInfoByUserId(mem_user_info.getString("USER_ID"));
        IData mem_acct_info = memAcctInfo;
        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(mem_user_info.getString("USER_ID"), "20");
        IData uu = uuInfos.getData(0);

        // 获取推荐人用户信息
        String user_id_a = "-1";
        if (!StringUtils.isBlank(serialNumberA))
        {
            IDataset userInfo = UserInfoQry.getUserInfoBySN(serialNumberA, "0", "00");
            IData user_info = userInfo.getData(0);
            user_id_a = user_info.getString("USER_ID");
        }
        else
        {
            serialNumberA = "-1";
        }

        svcData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        svcData.put("MEM_USER_INFO", mem_user_info);
        svcData.put("MEM_CUST_INFO", mem_cust_info);
        svcData.put("MEM_ACCT_INFO", mem_acct_info);
        svcData.put("TRADE_TYPE_CODE", "3603");
        svcData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        svcData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        svcData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        svcData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        svcData.put(Route.USER_EPARCHY_CODE, mem_user_info.getString("EPARCHY_CODE"));
        svcData.put("SERIAL_NUMBER", serialNumber); // 被推荐号码
        svcData.put("USER_ID", mem_user_info.getString("USER_ID")); // 被推荐号码用户id
        svcData.put("GRP_SERIAL_NUMBER", uu.getString("SERIAL_NUMBER_A")); // 集团sn
        svcData.put("GRP_USER_ID", uu.getString("USER_ID_A")); // 集团userId
        svcData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        svcData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        svcData.put("ACTIVE_TYPE", activeType);
        svcData.put("SERIAL_NUMBER_A", serialNumberA); // 推荐号码
        svcData.put("USER_ID_A", user_id_a); // 推荐号码用户id

        return svcData;
    }

    /**
     * 被推荐号码信息查询(V网免费体验活动)
     * 
     * @param serialNumber
     * @throws Exception
     */
    private static void querySerialNumberInfo4FeeCheck(String serialNumber, String activeType) throws Exception
    {
        // 1、获取用户信息
        IDataset memUserInfo = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(memUserInfo))
        {
            userInfo = memUserInfo.getData(0);
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_146, serialNumber);
        }
        // 2、被推荐号码客户信息查询
        String custId = userInfo.getString("CUST_ID");
        IData memCustInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        if (IDataUtil.isEmpty(memCustInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_147, serialNumber);
        }
        // 3、被推荐号码账户信息查询
        String userId = userInfo.getString("USER_ID");
        IData memAcctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
        if (IDataUtil.isEmpty(memAcctInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_148, serialNumber);
        }
        // 4、检查被推荐号码是否办理过V网免费体验活动
        IDataset relaset = VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdBActype(userId, activeType, userInfo.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(relaset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_152, serialNumber);
        }
        // 5、根据UU关系查用户是否为VPMN集团成员
        String userIdB = userInfo.getString("USER_ID");
        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, "20");
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_149, serialNumber);
        }
        IData uuInfo = uuInfos.getData(0);
        // 6、判断被推荐号码是否当月加入V网集团(多账期)
        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(userId);
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_153, serialNumber);
        }
        String startDate = SysDateMgr.getEnableDate(mebUserAcctDay.getString("FIRST_DAY_THISACCT"));
        String endDate = SysDateMgr.getEndDate(mebUserAcctDay.getString("LAST_DAY_THISACCT"));
        IDataset uudataset = RelaUUInfoQry.getRelaUUInfoByUserIdBAndSDate(userIdB, "20", startDate, endDate);
        if (IDataUtil.isEmpty(uudataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_150, serialNumber);
        }
        // 7、被推荐号码的V网套餐必须是集团3元套餐费（JDD）
        IDataset discntset = UserDiscntInfoQry.queryDiscntsByUserIdProdIdPkgId(userInfo.getString("USER_ID"), uuInfo.getString("USER_ID_A", ""), "800001", "80000102");
        if (IDataUtil.isNotEmpty(discntset))
        {
            IData discnt = (IData) discntset.get(0);
            if (!"1285".equals(discnt.getString("DISCNT_CODE", "")))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_151, serialNumber, discnt.getString("DISCNT_CODE", ""));
            }
        }
    }

    /**
     * 被推荐号码信息查询(双网有礼活动)
     * 
     * @param serialNumber
     * @throws Exception
     */
    private static void querySerialNumberInfo4BothWeb(String serialNumber, String activeType) throws Exception
    {
        // 1、获取用户信息
        IDataset memUserInfo = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(memUserInfo))
        {
            userInfo = memUserInfo.getData(0);
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_146, serialNumber);
        }
        // 2、被推荐号码客户信息查询
        String custId = userInfo.getString("CUST_ID");
        IData memCustInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        if (IDataUtil.isEmpty(memCustInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_147, serialNumber);
        }
        // 3、被推荐号码账户信息查询
        String userId = userInfo.getString("USER_ID");
        IData memAcctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
        if (IDataUtil.isEmpty(memAcctInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_148, serialNumber);
        }
        // 4、判断是否办理过双网有礼活动
        IDataset relaset = VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdBActype(userId, activeType, userInfo.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(relaset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_168, serialNumber);
        }
        // 5、判断是否参加其他的互斥的营销活动
        IDataset activeSet = UserSaleActiveInfoQry.qrySaleActiveByUserId(userId);
        if (IDataUtil.isNotEmpty(activeSet))
        {
            IData actData = activeSet.getData(0);
            if (IDataUtil.isNotEmpty(actData))
            {
                String proName = actData.getString("PRODUCT_NAME", "");
                CSAppException.apperr(VpmnUserException.VPMN_USER_162, serialNumber, proName);
            }
        }
        // 6、用户必须是亲亲网主号码
        IData uuRelaParam = new DataMap();
        uuRelaParam.put("USER_ID_B", userInfo.getString("USER_ID"));
        uuRelaParam.put("RELATION_TYPE_CODE", "45");
        uuRelaParam.put("ROLE_CODE_B", "1");
        IDataset outSet = RelaUUInfoQry.getRelatInfosBySelUserIdA(userInfo.getString("USER_ID"), "45", "1");
        if (IDataUtil.isEmpty(outSet))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_163, serialNumber);
        }
        String userIdA = outSet.getData(0).getString("USER_ID_A", "");
        if (StringUtils.isEmpty(userIdA))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_112, serialNumber);
        }
        // 7、用户所组建的亲亲网是否有2个或以上副号码
        IData uuRelaParam1 = new DataMap();
        uuRelaParam1.put("USER_ID_A", userIdA);
        uuRelaParam1.put("RELATION_TYPE_CODE", "45");
        // uuRelaParam1.put("ROLE_CODE_B", "2");
        IDataset uusets = RelaUUInfoQry.getRelaUUInfoByUserIda(userIdA, "45", null);
        if (IDataUtil.isEmpty(uusets))
        {
            // 该号码【"+serial_number+"】未组建亲亲网,不能办理双网有礼活动!
            CSAppException.apperr(VpmnUserException.VPMN_USER_164, serialNumber);
        }
        else if (uusets.size() < 3)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_165, serialNumber);
        }
        // 8、是否加入了V网
        IData vpmnParam = new DataMap();
        vpmnParam.put("USER_ID_B", userInfo.getString("USER_ID"));
        vpmnParam.put("RELATION_TYPE_CODE", "20");
        IDataset vpnSet = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userInfo.getString("USER_ID"), "20", null);
        if (IDataUtil.isEmpty(vpnSet))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_166, serialNumber);
        }
        // 9、办理某个套餐,才能办理双网有礼活动的配置,集团V网包下套餐配置
        IDataset discntSet = UserDiscntInfoQry.queryDiscntByUserIdVpmnActive(userInfo.getString("USER_ID"), vpnSet.getData(0).getString("USER_ID_A"), "800001", "80000102", "CSM", "9087", null, "0898");
        if (IDataUtil.isEmpty(discntSet))
        {
            IDataset paramSet = ParamInfoQry.getCommparaByParamattr("CSM", "9087", null, "0898");
            StringBuilder sBuilder = new StringBuilder();
            String resultStr = "";
            for (int i = 0; i < paramSet.size(); i++)
            {
                IData vpmnData = paramSet.getData(i);
                if (vpmnData != null && vpmnData.size() > 0)
                {
                    String paramCode = vpmnData.getString("PARAM_CODE", "");
                    String paramName = vpmnData.getString("PARAM_NAME", "");
                    sBuilder.append(paramCode + "=" + paramName + ",");
                }
            }
            if (sBuilder.length() > 0)
            {
                resultStr = sBuilder.substring(0, sBuilder.length() - 1);
            }
            CSAppException.apperr(VpmnUserException.VPMN_USER_167, serialNumber, resultStr);
        }
        // 10、个人产品套餐的配置,配置后,则必须用户必须订购这些套餐
        IDataset paramSet = ParamInfoQry.getCommparaByParamattr("CSM", "9086", null, "0898");
        if (IDataUtil.isNotEmpty(paramSet))
        {
            IDataset personDisSet = UserDiscntInfoQry.queryDiscntByUserIdVpmnActive(userInfo.getString("USER_ID"), null, null, null, "CSM", "9086", null, "0898");
            if (IDataUtil.isEmpty(personDisSet))
            {
                StringBuilder sBuilder = new StringBuilder();
                String resultStr = "";
                for (int i = 0; i < paramSet.size(); i++)
                {
                    IData perData = paramSet.getData(i);
                    if (perData != null && perData.size() > 0)
                    {
                        String paramCode = perData.getString("PARAM_CODE", "");
                        String paramName = perData.getString("PARAM_NAME", "");
                        sBuilder.append(paramCode + "=" + paramName + ",");
                    }
                }
                if (sBuilder.length() > 0)
                {
                    resultStr = sBuilder.substring(0, sBuilder.length() - 1);
                }
                CSAppException.apperr(VpmnUserException.VPMN_USER_167, serialNumber, resultStr);
            }
        }
    }

    /**
     * 推荐号码信息查询
     * 
     * @param serialNumberA
     * @throws Exception
     */
    private static void querySerialNumberAInfo(String serialNumberA) throws Exception
    {
        // 1、推荐号码用户信息查询
        IDataset memUserInfo = UserInfoQry.getUserInfoBySN(serialNumberA, "0", "00");
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(memUserInfo))
        {
            userInfo = memUserInfo.getData(0);
            // 校验号码状态
            String userStateCodeset = userInfo.getString("USER_STATE_CODESET", "");
            if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset) && !"00".equals(userStateCodeset))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_161, serialNumberA);
            }
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_154, serialNumberA);
        }
        // 2、推荐号码客户信息查询
        String custId = userInfo.getString("CUST_ID");
        IData memCustInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        if (IDataUtil.isEmpty(memCustInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_155, serialNumberA);
        }
        // 3、被推荐号码账户信息查询
        String userId = userInfo.getString("USER_ID");
        IData memAcctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
        if (IDataUtil.isEmpty(memAcctInfo))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_156, serialNumberA);
        }
        // 4、根据UU关系查用户是否为VPMN集团成员
        String userIdB = userInfo.getString("USER_ID");
        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, "20");
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_157, serialNumberA);
        }
        // 5、判断推荐号码的成员级别必须是联系人
        IDataset mebInfos = GrpMebInfoQry.getGroupInfoByMember(userInfo.getString("USER_ID"), userInfo.getString("EPARCHY_CODE"), null);
        if (IDataUtil.isNotEmpty(mebInfos))
        {
            IData mebInfo = mebInfos.getData(0);
            String memberKind = mebInfo.getString("MEMBER_KIND", "");
            if (!"1".equals(memberKind))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_158, serialNumberA);
            }
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_159, serialNumberA);
        }
    }
}
