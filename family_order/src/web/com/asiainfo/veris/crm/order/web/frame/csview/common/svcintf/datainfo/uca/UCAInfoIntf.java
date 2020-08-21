package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UCAInfoIntf {

    /**
     * 查询成员号码信息，号码路由不到信息时不报错，通过USER_RESULT_CODE判断是否原因 USER_RESULT_CODE 0 有效成员用户 RESULT_CODE_DETAIL 0 本地市号码 RESULT_CODE_DETAIL 1 非本地市的移动号码 2 非移动号码 USER_RESULT_CODE 1 无效用户号码 RESULT_CODE_DETAIL 0 本省移动号段内号码 1 本省非移动号段内号码 2 其它 USER_RESULT_CODE 2 有效集团用户
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData checkMebUserInfoBySn(IBizCommon bc, String strMebSn) throws Exception {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);
        return CSViewCall.callone(bc, "CS.UserInfoQrySVC.checkMebUserInfoBySn", inparam);
    }

    /**
     * 提供custid查询成员客户资料
     * 
     * @param bc
     * @param mebCustId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryCustInfoByCustIdAndRoute(IBizCommon bc, String mebCustId, String routeId) throws Exception {
        IData inparam = new DataMap();
        inparam.put("CUST_ID", mebCustId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryCustInfoByCustId", inparam);
    }

    /**
     * 根据集团账户id查找账户信息
     * 
     * @param bc
     * @param grpAcctId
     * @return
     * @throws Exception
     */
    public static IData qryGrpAcctInfoByAcctId(IBizCommon bc, String grpAcctId) throws Exception {
        IData param = new DataMap();
        param.put("ACCT_ID", grpAcctId);
        IData acctInfo = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryAcctInfoByAcctIdForGrp", param);
        return acctInfo;
    }

    /**
     * 根据合同号查询集团账户资料
     * 
     * @param bc
     * @param grpContractNo
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAcctInfoByContractNo(IBizCommon bc, String grpContractNo) throws Exception {

        IData param = new DataMap();
        param.put("CONTRACT_ID", grpContractNo);
        return CSViewCall.call(bc, "CS.AcctInfoQrySVC.getAcctInfoByContractNoForGrp", param);

    }

    /**
     * 根据集团客户标识查询集团账户信息
     * 
     * @param bc
     * @param grpCustId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAcctInfosByCustId(IBizCommon bc, String grpCustId) throws Exception {

        IData params = new DataMap();
        params.put("CUST_ID", grpCustId);
        IDataset acctInfos = CSViewCall.call(bc, "CS.AcctInfoQrySVC.getAcctInfoByCustIDForGrp", params);
        return acctInfos;
    }

    /**
     * 通过集团标示查询集团资料信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByCustId(IBizCommon bc, String custId) throws Exception {
        IData params = new DataMap();
        params.put("CUST_ID", custId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", params);
    }

    /**
     * 通过集团名称模糊查询集团资料
     * 
     * @param bc
     * @param grpCustName
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataOutput qryGrpCustInfoByCustName(IBizCommon bc, String grpCustName, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("CUST_NAME", grpCustName);
        IDataOutput grpInfos = CSViewCall.callPage(bc, "CS.GrpInfoQrySVC.qryGrpInfoByGrpName", param, pagination);
        return grpInfos;
    }

    /**
     * 通过集团编码查询集团资料
     * 
     * @param bc
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByGrpId(IBizCommon bc, String groupId) throws Exception {
        IData params = new DataMap();
        params.put("GROUP_ID", groupId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryGrpInfoByGrpId", params);
    }

    /**
     * 通过集团证件号码查询集团资料
     * 
     * @param bc
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpCustInfoByPsptId(IBizCommon bc, String psptTypeCode, String psptId) throws Exception {
        IData params = new DataMap();
        params.put("CUST_TYPE", "1");
        params.put("PSPT_TYPE_CODE", psptTypeCode);
        params.put("PSPT_ID", psptId);
        IDataset grpInfos = CSViewCall.call(bc, "CS.GrpInfoQrySVC.qryGrpInfoByGrpPspt", params);
        return grpInfos;
    }

    /**
     * 查询集团客户核心资料信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustomerInfoByCustId(IBizCommon bc, String custId) throws Exception {
        IData svcData = new DataMap();
        svcData.put("CUST_ID", custId);

        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryCustomerInfoByCustIdForGrp", svcData);
    }

    /**
     * 通过集团用户查询集团的默认付费账户
     * 
     * @param bc
     * @param grpUserId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpDefAcctInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {

        IData param = new DataMap();
        param.put("USER_ID", grpUserId);
        IData grpAcctData = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryAcctInfoByUserIdForGrp", param);
        return grpAcctData;
    }

    /**
     * 查询集团用户的默认付费关系信息
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IData qryGrpDefaultPayRelaInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", grpUserId);
        IData payRelaInfo = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryDefaultPayRelaByUserIdForGrp", param);
        return payRelaInfo;
    }

    /**
     * 根据集团用户id查询有效的付费关系
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpPayRelaInfoByUserIdAndCycleId(IBizCommon bc, String grpUserId, String cycleId) throws Exception {

        IData param = new DataMap();
        param.put("USER_ID", grpUserId);
        param.put("ACYC_ID", cycleId);
        IDataset payRelaInfos = CSViewCall.call(bc, "CS.PayRelaInfoQrySVC.getPayrelationInfoForGrp", param);
        return payRelaInfos;
    }

    /**
     * 根据集团客户标志查询集团用户信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByCustId(IBizCommon bc, String grpCustId) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);

        IDataset grpUserList = CSViewCall.call(bc, "CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", param);

        return grpUserList;
    }

    /**
     * 根据集团客户标志和产品ID查询集团用户信息
     * 
     * @param bc
     * @param grpCustId
     * @param productId
     * @return
     * @throws Exception
     */

    public static IDataset qryGrpUserInfoByCustIdAndProId(IBizCommon bc, String grpCustId, String productId) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);
        param.put("PRODUCT_ID", productId);
        IDataset grpUserInfo = CSViewCall.call(bc, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", param);

        return grpUserInfo;
    }

    /**
     * 通过集团服务号码查询集团用户信息
     * 
     * @param bc
     * @param grpSn
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserInfoByGrpSn(IBizCommon bc, String grpSn) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", grpSn);
        IData userInfo = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", param);
        return userInfo;
    }

    /**
     * 通过集团用户ID查询集团用户信息
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", grpUserId);
        IData userInfo = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", param);
        return userInfo;
    }

    /**
     * 根据集团客户标志查询集团用户信息--分页
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput qryGrpUserInfoPagByCustIdHasPriv(IBizCommon bc, String grpCustId, String privForProduct, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);
        param.put("PRIV_FOR_PRODUCT", privForProduct);
        IDataOutput grpInfos = CSViewCall.callPage(bc, "CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", param, pagination);
        return grpInfos;
    }

    /**
     * 查询成员服务号码归属地州
     * 
     * @param bc
     * @param serialNumber
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IData qryMebEparchyCodeBySnForCrm(IBizCommon bc, String serialNumber, String routeEparchyCode) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        return CSViewCall.callone(bc, "CS.RouteInfoQrySVC.qryEparchyCodeBySnForCrm", param);

    }

    /**
     * 查询成员用户ID订购的集团产品信息
     * 
     * @param bc
     * @param strMebUserId
     * @param relationCode
     *            1 查询UU表 2查询BB表 其它 全部查询
     * @param routeId
     * @param privForProduct
     * @return
     * @throws Exception
     */
    public static IDataset qryMebOrderedGroupInfosByMebUserIdAndRelationCodeHasPriv(IBizCommon bc, String strMebUserId, String relationCode, String routeId, String privForProduct) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", strMebUserId);
        data.put("RELATION_CODE", relationCode);
        data.put("PRIV_FOR_PRODUCT", privForProduct);
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.GrpInfoQrySVC.getGroupInfo", data);

    }

    /**
     * 通过集团名称模糊查询铁通集团资料
     * 
     * @param bc
     * @param grpCustName
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataOutput qryTTGrpCustInfoByCustName(IBizCommon bc, String grpCustName, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("CUST_NAME", grpCustName);
        IDataOutput grpInfos = CSViewCall.callPage(bc, "CS.GrpInfoQrySVC.qryTTGrpInfoByGrpName", param, pagination);
        return grpInfos;
    }

    /**
     * 通过集团证件号码查询铁通集团资料
     * 
     * @param bc
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset qryTTGrpCustInfoByPsptId(IBizCommon bc, String psptTypeCode, String psptId) throws Exception {
        IData params = new DataMap();
        params.put("CUST_TYPE", "1");
        params.put("PSPT_TYPE_CODE", psptTypeCode);
        params.put("PSPT_ID", psptId);
        IDataset grpInfos = CSViewCall.call(bc, "CS.GrpInfoQrySVC.qryTTGrpInfoByGrpPspt", params);
        return grpInfos;
    }

    /**
     * 查询用户账期信息
     * 
     * @param bc
     * @param userId
     * @param route
     * @return
     * @throws Exception
     */
    public static IData qryUserAcctDayAndFirstDateInfoByUserId(IBizCommon bc, String userId, String route) throws Exception {
        // 取用户账期信息
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put(Route.ROUTE_EPARCHY_CODE, route);
        return CSViewCall.callone(bc, "CS.UserAcctDayInfoQrySVC.getUserAcctDayAndFirstDateInfo", param);
    }

    /**
     * 通过成员账户id查询账户信息
     * 
     * @param bc
     * @param grpAcctId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserAcctInfoByAcctIdAndRoute(IBizCommon bc, String mebAcctId, String routeId) throws Exception {
        IData param = new DataMap();
        param.put("ACCT_ID", mebAcctId);
        param.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IData acctInfo = CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryAcctInfoByAcctId", param);
        return acctInfo;
    }

    /**
     * 通过成员用户ID查询默认付费账户
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserDefAcctInfoByUserId(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", mebUserId);
        param.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryAcctInfoByUserId", param);
    }

    /**
     * 根据成员用户查询默认付费关系
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserDefaultPayRelaInfoByUserIdAndRoute(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", mebUserId);
        param.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryPayRelaByUserId", param);
    }

    /**
     * 提供号码的用户资料查询
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySn(IBizCommon bc, String strMebSn) throws Exception {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);
        return CSViewCall.callone(bc, "CS.UserInfoQrySVC.getUserInfoBySN", inparam);
    }

    /**
     * 提供号码的用户资料查询(不关联用户产品表查询)
     * 
     * @param bc
     * @param strMebSn
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySnNoProduct(IBizCommon bc, String strMebSn, String removeTag) throws Exception {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);
        inparam.put("REMOVE_TAG", removeTag);
        return CSViewCall.callone(bc, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", inparam);
    }

    /**
     * 提供用户ID查询用户资料
     * 
     * @param bc
     * @param strMebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId) throws Exception {
        IData inparam = new DataMap();
        inparam.put("USER_ID", strMebUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.callone(bc, "CS.UcaInfoQrySVC.qryUserInfoByUserId", inparam);
    }

    /**
     * 根据成员用户标示查询有效的付费关系
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserValidPayRelaInfoByUserIdAndRoute(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        IData payParam = new DataMap();
        payParam.put("USER_ID", mebUserId);
        payParam.put("ACYC_ID", SysDateMgr.getNowCycle());
        payParam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset defPayRelaInfos = CSViewCall.call(bc, "CS.PayRelaInfoQrySVC.queryUserValidPay", payParam);
        return defPayRelaInfos;
    }

    /**
     * 查询集团用户VPN信息
     * 
     * @param bc
     * @param userId
     *            用户ID
     * @return
     * @throws Exception
     */
    public static IData qryUserVpnInfoByUserId(IBizCommon bc, String userId) throws Exception {
        IData svcData = new DataMap();

        svcData.put("USER_ID", userId);

        return CSViewCall.callone(bc, "CS.UserVpnInfoQrySVC.qryUserVpnByUserId", svcData);
    }

    /**
     * 根据集团客户标志
     * 
     * @param bc
     * @param grpCustId
     * @param productId
     * @return
     * @throws Exception
     * @author chenhh6
     */

    public static IDataset queryUserInfoByCustId(IBizCommon bc, String grpCustId) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);
        return CSViewCall.call(bc, "CS.UserInfoQrySVC.queryUserInfoByCustId", param);
    }

    /**
     * 根据客户经理id查询客户经理信息
     * 
     * @param bc
     * @param custManagerId
     * @return
     * @throws Exception
     */
    public static IData qryCustManagerByCustManagerId(IBizCommon bc, String custManagerId) throws Exception {
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", custManagerId);
        IData custManager = CSViewCall.callone(bc, "CS.StaffInfoQrySVC.queryCustManagerStaffById", param);
        return custManager;
    }


    /**
     * 根据集团客户标志查询集团用户信息
     *
     * @param bc
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByGroupId(IBizCommon bc, String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        IDataset grpUserList = CSViewCall.call(bc, "CS.UserInfoQrySVC.getServceInfoForProductId", param);

        return grpUserList;
    }
}
