
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.VpmnSaleActiveQry;

public class GrpMebQryIntf
{
    /**
     * 查询集团成员集团相关优惠（外围接口与电子渠道 门户共用）
     * 
     * @param inparam
     *            （SERIAL_NUMBER_A, SERIAL_NUMBER_B）
     * @return
     * @throws Exception
     */
    public static IDataset getMemberDiscnt(IData inparam) throws Exception
    {
        // 集团SERIAL_NUMBER
        String serialNumberA = inparam.getString("SERIAL_NUMBER_A");

        // 成员SERIAL_NUMBER
        String serialNumberB = inparam.getString("SERIAL_NUMBER_B");

        // 根据SERIAL_NUMBER_A获取product_id和user_id_a
        IDataset userDatasetA = UserInfoQry.getUserInfoBySN(serialNumberA, "0", "00");
        if (IDataUtil.isEmpty(userDatasetA))
        { // 该号码【%s】没有有效的用户信息！
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumberA);
        }
        String userIdA = userDatasetA.getData(0).getString("USER_ID");
        String productId = userDatasetA.getData(0).getString("PRODUCT_ID");

        // 根据SERIAL_NUMBER_B获取user_id_b
        IDataset userDatasetB = UserInfoQry.getUserInfoBySN(serialNumberB, "0", "00");
        if (IDataUtil.isEmpty(userDatasetB))
        { // 该号码【%s】没有有效的用户信息！
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumberB);
        }
        String userIdB = userDatasetB.getData(0).getString("USER_ID");

        // 获取成员优惠信息
        IDataset memDiscntDataset = UserDiscntInfoQry.getMemberDiscntByUserIdUserIdA(StrUtil.getPartition4ById(userIdB), userIdB, userIdA, productId);
        for (int i = 0, size = memDiscntDataset.size(); i < size; i++)
        {
            IData memDiscntData = memDiscntDataset.getData(i);
            // 获取优惠信息名称
            IData discntData = UDiscntInfoQry.getDiscntInfoByPk(memDiscntData.getString("DISCNT_CODE"));
            IDataset userProducts = UserProductInfoQry.qryGrpMebProduct(memDiscntData.getString("USER_ID"), memDiscntData.getString("USER_ID_A"));
            if(IDataUtil.isNotEmpty(userProducts))
            {
            	memDiscntDataset.getData(i).put("PRODUCT_ID", userProducts.getData(0).getString("PRODUCT_ID"));
            }
            String discntName = discntData.getString("DISCNT_NAME");
            memDiscntDataset.getData(i).put("DISCNT_NAME", discntName);
        }
        return memDiscntDataset;
    }

    /**
     * 成员新增时，可选的集团产品
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getMemAddCanSelProd(IData inparam) throws Exception
    {
        // 集团客户编码
        String groupId = inparam.getString("GROUP_ID");

        // 产品id
        String productId = inparam.getString("PRODUCT_ID");

        // 查询集团订购的产品
        IDataset grpUserDataset = UserInfoQry.qryGrpUserInfoByGroupIdProdIdSN(groupId, productId, null, null, null);
        if (IDataUtil.isEmpty(grpUserDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_682, groupId);
        }
        return grpUserDataset;
    }

    /**
     * 成员新增时，可选的集团产品优惠（优惠变更时共用）
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getMemAddCanSelDis(IData inparam) throws Exception
    {
        // 集团sn
        String serialNumber = inparam.getString("SERIAL_NUMBER");

        // 产品id
        String productId = inparam.getString("PRODUCT_ID");

        // 员工号
        String staffId = inparam.getString("TRADE_STAFF_ID");

        // 获取集团订购产品的userId
        IDataset grpUserDataset = UserInfoQry.qryGrpUserInfoByGroupIdProdIdSN(null, productId, serialNumber, "00", "0");
        if (IDataUtil.isEmpty(grpUserDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_683, serialNumber, productId);
        }
        String userId = grpUserDataset.getData(0).getString("USER_ID");

        // 获取集团开户时选择的产品优惠
        IDataset userDiscntDataset = UserGrpPkgInfoQry.qryGrpCustomizeDiscntByUserId(userId);
        if (IDataUtil.isEmpty(userDiscntDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_685, grpUserDataset.getData(0).getString("GROUP_ID", ""));
        }
        else
        {
            if (!"SUPERUSR".equals(staffId))
            {
                for (int i = userDiscntDataset.size() - 1; i > -1; i--)
                {
                    boolean rigthts = StaffPrivUtil.isDistPriv(staffId, userDiscntDataset.getData(i).getString("ELEMENT_ID"));
                    if (!rigthts)
                    {
                        // 如果没有权限，则将该记录删除
                        userDiscntDataset.remove(i);
                    }
                }
            }
            if (IDataUtil.isEmpty(userDiscntDataset))
            {
                CSAppException.apperr(GrpException.CRM_GRP_685, grpUserDataset.getData(0).getString("GROUP_ID", ""));
            }
        }
        return userDiscntDataset;
    }

    /**
     * 查询集团V网营销活动资料
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnSaleActive(IData inparam) throws Exception
    {
        // 推荐号码
        String serialNumber = inparam.getString("SERIAL_NUMBER");

        // 活动类型
        String activeType = inparam.getString("ACTIVE_TYPE");

        // 获取推荐人的userId
        IDataset userDataset = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        if (IDataUtil.isEmpty(userDataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumber);
        }
        String userId = userDataset.getData(0).getString("USER_ID");
        String eparchyCode = userDataset.getData(0).getString("EPARCHY_CODE");

        // 获取推荐号码推荐的客户
        IDataset activeDataset = VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdA(userId, eparchyCode);
        if (IDataUtil.isEmpty(activeDataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_175, serialNumber);
        }
        // 推荐客户总量
        int totalCount = activeDataset.size();

        // 本月起始时间
        String startDate = SysDateMgr.getFirstDayOfThisMonth() + SysDateMgr.getFirstTime00000();

        // 本月结束时间
        String endDate = SysDateMgr.getLastDateThisMonth();

        // 查询本月推荐客户
        IDataset dataset1 = VpmnSaleActiveQry.qryVPMNSaleActiveByUserIdAActTypeSDate(userId, activeType, startDate, endDate, eparchyCode);
        int monthCount = 0; // 本月推荐客户量
        if (IDataUtil.isNotEmpty(dataset1))
        {
            monthCount = dataset1.size();
        }

        // 查询本月赠送客户
        IDataset dataset2 = VpmnSaleActiveQry.qryVPMNSaleActiveByUserIdAActiveGDate(userId, activeType, startDate, endDate, eparchyCode);
        int saleCount = 0; // 本月赠送话费金额
        if (IDataUtil.isNotEmpty(dataset2))
        {
            saleCount = dataset2.size();
        }

        // 查询总赠送客户
        IDataset totalSaleset = VpmnSaleActiveQry.getTotalSaleActiveByGtag(userId, activeType);
        int totalSaleCount = 0; // 赠送话费总金额
        if (IDataUtil.isNotEmpty(totalSaleset))
        {
            totalSaleCount = totalSaleset.size();
        }

        // 返回参数设置
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0"); // 正常状态
        result.put("X_RESULTINFO", "ok"); // 正常状态描述
        result.put("X_TOTALCOUNT", totalCount);
        result.put("X_MONTHCOUNT", monthCount);
        result.put("X_SALECOUNT", saleCount * 3);
        result.put("X_TOTAL_SALECOUNT", totalSaleCount * 3);
        return IDataUtil.idToIds(result);
    }

    /**
     * 校验是否能够满足办理集团V网双网有礼活动
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset checkVpmnBothWebActive(IData inparam) throws Exception
    {
        // 被推荐号码
        String serialNumber = inparam.getString("SERIAL_NUMBER");

        // 活动类型
        String activeType = inparam.getString("ACTIVE_TYPE");

        if (!"9".equals(activeType))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_205, activeType);
        }

        // 1、获取用户信息
        IDataset memUserInfo = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(memUserInfo))
        {
            userInfo = memUserInfo.getData(0);
            String userStateCodeset = userInfo.getString("USER_STATE_CODESET", "");
            if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset) && !"00".equals(userStateCodeset))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_108, serialNumber);
            }
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

        // 返回参数设置
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0"); // 正常状态
        result.put("X_RESULTINFO", "ok"); // 正常状态描述
        return IDataUtil.idToIds(result);
    }

    /**
     * 根据集团编号GROUP_ID、ProductId查询
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebUUInfo(IData inParam, Pagination pg) throws Exception
    {
        String strProductId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String strGroupId = IDataUtil.getMandaData(inParam, "GROUP_ID");
        String serNumA = IDataUtil.getMandaData(inParam, "SERIAL_NUMBER_A");

        IData param = new DataMap();
        param.put("PRODUCT_ID", strProductId);
        param.put("GROUP_ID", strGroupId);
        param.put("SERIAL_NUMBER", serNumA);

        IData idsGrpUserInfo = UcaInfoQry.qryUserInfoBySn(serNumA);

        IDataset idsGrpMebUUInfo = new DatasetList();// 集团下面成员信息

        if (idsGrpUserInfo != null && idsGrpUserInfo.size() > 0)
        {
            param.put("USER_ID_A", idsGrpUserInfo.getString("USER_ID", ""));
            idsGrpMebUUInfo = UserGrpInfoQry.getUsrMem(param);
            String serNumB = "";
            String shortCode = "";
            IData paramData = new DataMap();
            for (int i = 0; i < idsGrpMebUUInfo.size(); i++)
            {
                serNumB = idsGrpMebUUInfo.getData(i).getString("SERIAL_NUMBER");
                if ("8000".equals(strProductId))
                {// 如果product_id=8000,则返回 短号
                    paramData.put("GROUP_ID", strGroupId);
                    paramData.put("VPN_NO", serNumA);
                    paramData.put("SERIAL_NUMBER_B", serNumB);

                    IDataset idsGrpId = UserGrpInfoQry.qryVpnMemShrtCod(paramData);
                    IData dm = new DataMap();
                    if (idsGrpId.size() > 0)
                    {
                        dm = (IData) idsGrpId.get(0);
                    }
                    shortCode = dm.size() > 0 ? dm.getString("SHORT_CODE") : "";
                    idsGrpMebUUInfo.getData(i).put("SHORT_CODE", shortCode);// 把SHORT_CODE放入VPN成员记录
                }
                idsGrpMebUUInfo.getData(i).put("TOTAL_NUM", String.valueOf(idsGrpMebUUInfo.size()));// 把TOTAL_NUM放入每一条记录
            }
        }
        return idsGrpMebUUInfo;

    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员订购关系
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebProOrder(IData data, Pagination pg) throws Exception
    {
        // 集团成员用户号码
        String strSerialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        // 集团编码
        String strGroupId = IDataUtil.getMandaData(data, "GROUP_ID");

        // 根据集团GROUP_ID查询集团用户信息
        IDataset idsGrpMebProOrder = new DatasetList();
        idsGrpMebProOrder = UserGrpInfoQry.getGrpMebProOrderByGIdSN(strSerialNumber, strGroupId);

        for (int i = 0; i < idsGrpMebProOrder.size(); i++)
        {
            idsGrpMebProOrder.getData(i).put("TOTAL_NUM", idsGrpMebProOrder.size());// 把TOTAL_NUM放入每一条记录
        }

        return idsGrpMebProOrder;
    }

    /**
     * 根据集团编号GROUP_ID、集团管理员电话号码查询集团成员信息
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebInfo(IData data, Pagination pg) throws Exception
    {
        // 集团编码
        String strGroupId = IDataUtil.getMandaData(data, "GROUP_ID");

        IData param = new DataMap();
        param.put("GROUP_ID", strGroupId);

        IDataset idsGrpMebInfo = new DatasetList();

        idsGrpMebInfo = UserGrpInfoQry.qryGrpMebInfo(strGroupId);

        for (int i = 0; i < idsGrpMebInfo.size(); i++)
        {
            idsGrpMebInfo.getData(i).put("TOTAL_NUM", String.valueOf(idsGrpMebInfo.size()));// 把TOTAL_NUM放入每一条记录
        }

        return idsGrpMebInfo;
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员个人业务信息
     * 
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */

    public static IDataset qryGrpMebBus(IData data, Pagination pg) throws Exception
    {
        // 用户手机号码
        String strSerialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        // 集团编码
        String strGroupId = IDataUtil.getMandaData(data, "GROUP_ID");

        // 根据集团编号GROUP_ID、成员电话号码查询集团成员个人业务信息
        IDataset idsGrpMebBusiness = new DatasetList();
        idsGrpMebBusiness = UserGrpInfoQry.getGrpMebProOrderByGIdSN(strSerialNumber, strGroupId);
        for (int i = 0; i < idsGrpMebBusiness.size(); i++)
        {
            idsGrpMebBusiness.getData(i).put("TOTAL_NUM", idsGrpMebBusiness.size());// 把TOTAL_NUM放入每一条记录
        }
        return idsGrpMebBusiness;
    }

    /**
     * 本月订购次数
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getAddCountMonth(IData data) throws Exception
    {
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String relaTypCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        String memSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        UcaData memUcaData = new UcaData();

        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(memSn);
        boolean bool = uca.getBoolean("RESULT");
        if (!bool)
        {
            return uca.getDataset("RESULT_DATA");
        }
        else
        {
            memUcaData = (UcaData) uca.get("UCADATA");
        }

        String userId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();

        IDataset ids = RelaUUInfoQry.getRelaInfoByUserIdbAndRelaTypeCode(userId, relaTypCode, memEparchyCode);

        return ids;
    }
}
