package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupDiversifyUtilCommon;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca.UCAInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class UCAInfoIntfViewUtil {
    /**
     * 查询成员号码信息，号码路由不到信息时不报错，通过USER_RESULT_CODE判断是否原因 USER_RESULT_CODE 0 有效成员用户 RESULT_CODE_DETAIL 0 本地市移动号码 RESULT_CODE_DETAIL 1 非本地市的移动号码 RESULT_CODE_DETAIL 2 非移动号码 USER_RESULT_CODE 1 无效用户号码 RESULT_CODE_DETAIL 0 本省移动号段内号码 RESULT_CODE_DETAIL 1
     * 本省非移动号段内号码 RESULT_CODE_DETAIL 2 其它 USER_RESULT_CODE 2 有效集团用户
     * 
     * @param bc
     * @param strMebSn
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData checkMebUserInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException) throws Exception {
        // 查询成员用户信息
        if (StringUtils.isEmpty(strMebSn)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_114);
            return null;
        }
        IData idUserInfo = UCAInfoIntf.checkMebUserInfoBySn(bc, strMebSn);
        if (IDataUtil.isEmpty(idUserInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_117, strMebSn);
            return null;
        }

        return idUserInfo;
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
        return qryGrpAcctInfoByAcctId(bc, grpAcctId, true);
    }

    /**
     * 根据集团账户id查找账户信息
     * 
     * @param bc
     * @param grpAcctId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpAcctInfoByAcctId(IBizCommon bc, String grpAcctId, boolean isThrowException) throws Exception {

        if (StringUtils.isEmpty(grpAcctId)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_68);
            return null;
        }

        IData acctInfo = UCAInfoIntf.qryGrpAcctInfoByAcctId(bc, grpAcctId);
        if (IDataUtil.isEmpty(acctInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_15, grpAcctId);
            return null;
        }
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
        return qryGrpAcctInfoByContractNo(bc, grpContractNo, true);
    }

    /**
     * 根据合同号查询集团账户资料
     * 
     * @param bc
     * @param grpContractNo
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAcctInfoByContractNo(IBizCommon bc, String grpContractNo, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(grpContractNo)) {
            if (isThrowException) {
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_139);
                return null;
            }
        }

        IDataset dataset = UCAInfoIntf.qryGrpAcctInfoByContractNo(bc, grpContractNo);
        if (IDataUtil.isEmpty(dataset)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_138);
            return null;
        }
        for (int row = 0; row < dataset.size(); row++) {
            IData data = (IData) dataset.get(row);
            data.put("CONTRACT_NO", data.getString("CONTRACT_ID"));
            String grpAcctId = data.getString("ACCT_ID");
            IData acctInfoData = qryGrpAcctInfoByAcctId(bc, grpAcctId, isThrowException);
            if (IDataUtil.isNotEmpty(acctInfoData)) {
                data.put("PAY_NAME", acctInfoData.getString("PAY_NAME"));
                data.remove("RSRV_STR8");
                data.put("RSRV_STR8", acctInfoData.getString("RSRV_STR8"));
                data.remove("RSRV_STR9");
                data.put("RSRV_STR9", acctInfoData.getString("RSRV_STR9"));
            }
        }
        return dataset;

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
        IDataset acctInfos = UCAInfoIntf.qryGrpAcctInfosByCustId(bc, grpCustId);
        return acctInfos;
    }

    /**
     * 通过集团名称模糊查询集团资料
     * 
     * @param bc
     * @param grpCustName
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput qryGrpCustInfoByCustName(IBizCommon bc, String grpCustName, Pagination pagination) throws Exception {
        return UCAInfoIntf.qryGrpCustInfoByCustName(bc, grpCustName, pagination);
    }

    /**
     * 通过客户标识查询集团信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByGrpCustId(IBizCommon bc, String grpCustId) throws Exception {
        return qryGrpCustInfoByGrpCustId(bc, grpCustId, true);
    }

    /**
     * 通过客户标识查询集团信息
     * 
     * @param bc
     * @param custId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByGrpCustId(IBizCommon bc, String grpCustId, boolean isThrowException) throws Exception {
        IData result = UCAInfoIntf.qryGrpCustInfoByCustId(bc, grpCustId);
        if (IDataUtil.isEmpty(result)) // 未找到对应的集团
        {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_791, grpCustId);

            return null;
        }
        String removeTag = result.getString("REMOVE_TAG", "");
        if (!"0".equals(removeTag)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_792, grpCustId);

            return null;
        }

        // qryGrpCustomerInfoByCustId(bc,grpCustId, true);

        return result;
    }

    /**
     * 通过集团编码查询集团客户信息
     * 
     * @param bc
     * @param grpId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByGrpId(IBizCommon bc, String grpId) throws Exception {
        return qryGrpCustInfoByGrpId(bc, grpId, true);
    }

    /**
     * 通过集团编码查询集团客户信息
     * 
     * @param bc
     * @param grpId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustInfoByGrpId(IBizCommon bc, String grpId, boolean isThrowException) throws Exception {
        IData result = UCAInfoIntf.qryGrpCustInfoByGrpId(bc, grpId);
        if (IDataUtil.isEmpty(result)) // 未找到对应的集团
        {
            if (isThrowException) {
                CSViewException.apperr(GrpException.CRM_GRP_472, grpId);
            }
            return null;
        }

        // qryGrpCustomerInfoByCustId(bc,result.getString("CUST_ID",""), true);
        return result;
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
        return qryGrpCustInfoByPsptId(bc, psptTypeCode, psptId, true);
    }

    /**
     * 通过集团证件号码查询集团资料
     * 
     * @param bc
     * @param psptTypeCode
     * @param psptId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpCustInfoByPsptId(IBizCommon bc, String psptTypeCode, String psptId, boolean isThrowException) throws Exception {
        IDataset grpInfos = UCAInfoIntf.qryGrpCustInfoByPsptId(bc, psptTypeCode, psptId);

        if (IDataUtil.isEmpty(grpInfos)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_192);
            return null;
        }
        return grpInfos;
    }

    /**
     * 根据客户ID查询客户核心资料信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustomerInfoByCustId(IBizCommon bc, String custId) throws Exception {
        return qryGrpCustomerInfoByCustId(bc, custId, true);
    }

    /**
     * 根据客户ID查询客户核心资料信息
     * 
     * @param bc
     * @param custId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpCustomerInfoByCustId(IBizCommon bc, String custId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(custId)) {
            if (isThrowException)
                CSViewException.apperr(CustException.CRM_CUST_77);
            return null;
        }

        IData customerData = UCAInfoIntf.qryGrpCustomerInfoByCustId(bc, custId);

        if (IDataUtil.isEmpty(customerData)) {
            if (isThrowException)
                CSViewException.apperr(CustException.CRM_CUST_71);
            return null;
        }

        return customerData;
    }

    /**
     * 通过集团用户查询集团的默认付费账户
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IData qryGrpDefAcctInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        return qryGrpDefAcctInfoByUserId(bc, grpUserId, true);
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
    public static IData qryGrpDefAcctInfoByUserId(IBizCommon bc, String grpUserId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(grpUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_429);
            return null;
        }
        IData grpAcctData = UCAInfoIntf.qryGrpDefAcctInfoByUserId(bc, grpUserId);
        if (IDataUtil.isEmpty(grpAcctData)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14, grpUserId);
            return null;
        }

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
        return qryGrpDefaultPayRelaInfoByUserId(bc, grpUserId, true);
    }

    /**
     * 查询集团用户的默认付费关系信息
     * 
     * @param bc
     * @param grpUserId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpDefaultPayRelaInfoByUserId(IBizCommon bc, String grpUserId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(grpUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_429);
            return null;
        }
        IData payRelaInfo = UCAInfoIntf.qryGrpDefaultPayRelaInfoByUserId(bc, grpUserId);
        if (IDataUtil.isEmpty(payRelaInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14, grpUserId);
            return null;
        }
        return payRelaInfo;
    }

    /**
     * 根据集团用户ID查询集团三户信息（A信息暂时不查）
     * 
     * @author luoyong
     * @throws Throwable
     */

    public static IData qryGrpUCAInfoByGrpSn(IBizCommon bc, String grpSn) throws Exception {
        return qryGrpUCAInfoByGrpSn(bc, grpSn, true);
    }

    /**
     * 根据集团用户ID查询集团三户信息（A信息暂时不查）
     * 
     * @author luoyong
     * @throws Throwable
     */
    public static IData qryGrpUCAInfoByGrpSn(IBizCommon bc, String grpSn, boolean isThrowException) throws Exception {

        if (StringUtils.isBlank(grpSn)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_115);
            return null;
        }
        // 查询用户信息
        IData userInfo = qryGrpUserInfoByGrpSn(bc, grpSn, false);
        if (IDataUtil.isEmpty(userInfo)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_197, grpSn);
            return null;
        }

        // 查询客户信息
        IData grpcustInfo = qryGrpCustInfoByGrpCustId(bc, userInfo.getString("CUST_ID", ""), isThrowException);

        if (IDataUtil.isEmpty(grpcustInfo)) // 未找到对应的集团
        {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_345);
            return null;
        }

        // 查询账户信息
        IData grpAcctInfo = qryGrpDefAcctInfoByUserId(bc, userInfo.getString("USER_ID"), isThrowException);
        if (IDataUtil.isEmpty(grpAcctInfo)) // 未找到对应的集团账户
        {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14);
            return null;
        }

        IData redata = new DataMap();
        redata.put("GRP_USER_INFO", userInfo);
        redata.put("GRP_CUST_INFO", grpcustInfo);
        redata.put("GRP_ACCT_INFO", grpAcctInfo);
        return redata;

    }

    public static IData qryGrpUCAInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        return qryGrpUCAInfoByUserId(bc, grpUserId, true);
    }

    /**
     * 根据集团用户ID查询集团三户信息（A信息暂时不查）
     * 
     * @author luoyong
     * @throws Throwable
     */
    public static IData qryGrpUCAInfoByUserId(IBizCommon bc, String grpUserId, boolean isThrowException) throws Exception {
        if (StringUtils.isBlank(grpUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_864);
            return null;
        }
        // 查询用户信息
        IData userInfo = qryGrpUserInfoByUserId(bc, grpUserId, false);
        if (IDataUtil.isEmpty(userInfo)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_197, grpUserId);
            return null;
        }

        // 查询客户信息
        IData grpcustInfo = qryGrpCustInfoByGrpCustId(bc, userInfo.getString("CUST_ID", ""), isThrowException);

        if (IDataUtil.isEmpty(grpcustInfo)) // 未找到对应的集团
        {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_345);
            return null;
        }

        // 查询账户信息
        IData grpAcctInfo = qryGrpDefAcctInfoByUserId(bc, grpUserId, isThrowException);
        if (IDataUtil.isEmpty(grpAcctInfo)) // 未找到对应的集团账户
        {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14);
            return null;
        }
        IData redata = new DataMap();
        redata.put("GRP_USER_INFO", userInfo);
        redata.put("GRP_CUST_INFO", grpcustInfo);
        redata.put("GRP_ACCT_INFO", grpAcctInfo);
        return redata;

    }

    /**
     * 根据集团客户标志查询集团用户信息
     * 
     * @param bc
     * @param grpCustId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByCustId(IBizCommon bc, String grpCustId) throws Exception {
        return qryGrpUserInfoByCustId(bc, grpCustId, true);
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
    public static IDataset qryGrpUserInfoByCustId(IBizCommon bc, String grpCustId, boolean isThrowException) throws Exception {

        IDataset userInfos = UCAInfoIntf.qryGrpUserInfoByCustId(bc, grpCustId);
        if (IDataUtil.isEmpty(userInfos) && isThrowException) {
            CSViewException.apperr(GrpException.CRM_GRP_197, "集团编码:" + grpCustId);
            return null;
        }
        return userInfos;
    }

    /**
     * 根据集团客户标志和产品ID查询集团用户信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByCustIdAndProId(IBizCommon bc, String grpCustId, String productId) throws Exception {

        return qryGrpUserInfoByCustIdAndProId(bc, grpCustId, productId, true);
    }

    /**
     * 根据集团客户标志和产品ID查询集团用户信息
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByCustIdAndProId(IBizCommon bc, String custId, String productId, boolean isThrowException) throws Exception {
        IDataset userInfoData = UCAInfoIntf.qryGrpUserInfoByCustIdAndProId(bc, custId, productId);

        if (IDataUtil.isEmpty(userInfoData) && isThrowException) {
            CSViewException.apperr(GrpException.CRM_GRP_670, custId, productId);
        }
        return userInfoData;
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
        return qryGrpUserInfoByGrpSn(bc, grpSn, true);
    }

    /**
     * 通过集团服务号码查询集团用户信息
     * 
     * @param bc
     * @param grpSn
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserInfoByGrpSn(IBizCommon bc, String grpSn, boolean isThrowException) throws Exception {
        IData userInfo = UCAInfoIntf.qryGrpUserInfoByGrpSn(bc, grpSn);
        if (IDataUtil.isEmpty(userInfo)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_315, grpSn);
            return null;
        }
        return userInfo;
    }

    /**
     * 通过集团用户ID查询集团用户信息
     * 
     * @param bc
     * @param grpUserId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        return qryGrpUserInfoByUserId(bc, grpUserId, true);
    }

    /**
     * 通过集团用户ID查询集团用户信息
     * 
     * @param bc
     * @param grpUserId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserInfoByUserId(IBizCommon bc, String grpUserId, boolean isThrowException) throws Exception {
        IData userInfoData = UCAInfoIntf.qryGrpUserInfoByUserId(bc, grpUserId);
        if (IDataUtil.isEmpty(userInfoData)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_189, grpUserId);
            return null;
        }
        String removeTag = userInfoData.getString("REMOVE_TAG", "");
        if (!removeTag.equals("0")) {
            if (isThrowException) {
                CSViewException.apperr(CrmUserException.CRM_USER_593);
            }
            return null;
        }
        return userInfoData;
    }

    /**
     * 根据集团客户标志查询集团用户信息--分页
     * 
     * @param bc
     * @param grpCustId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataOutput qryGrpUserInfoPagByCustId(IBizCommon bc, String grpCustId, Pagination pagination) throws Exception {
        return qryGrpUserInfoPagByCustIdHasPriv(bc, grpCustId, "false", pagination);
    }

    /**
     * 根据集团客户标志查询集团用户信息--分页,支持产品权限过滤
     * 
     * @param bc
     * @param grpCustId
     * @param privForProduct
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataOutput qryGrpUserInfoPagByCustIdHasPriv(IBizCommon bc, String grpCustId, String privForProduct, Pagination pagination) throws Exception {
        return UCAInfoIntf.qryGrpUserInfoPagByCustIdHasPriv(bc, grpCustId, privForProduct, pagination);
    }

    /**
     * 根据集团用户id查询有效的付费关系
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpValidPayRelaInfoByUserId(IBizCommon bc, String grpUserId) throws Exception {
        String nowCycleId = SysDateMgr.getNowCycle();
        return UCAInfoIntf.qryGrpPayRelaInfoByUserIdAndCycleId(bc, grpUserId, nowCycleId);
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
    public static IData qryMebAcctInfoByAcctId(IBizCommon bc, String mebAcctId, String routeId) throws Exception {
        return qryMebAcctInfoByAcctId(bc, mebAcctId, routeId, true);
    }

    /**
     * 通过成员账户id查询账户信息
     * 
     * @param bc
     * @param grpAcctId
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryMebAcctInfoByAcctId(IBizCommon bc, String mebAcctId, String routeId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(mebAcctId)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_68);
            return null;
        }
        IData acctInfo = UCAInfoIntf.qryUserAcctInfoByAcctIdAndRoute(bc, mebAcctId, routeId);
        if (IDataUtil.isEmpty(acctInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_15, mebAcctId);
            return null;
        }
        return acctInfo;

    }

    /**
     * 提供custid查询成员客户资料
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebCustInfoByCustIdAndRoute(IBizCommon bc, String mebCustId, String routeId) throws Exception {
        return qryMebCustInfoByCustIdAndRoute(bc, mebCustId, routeId, true);
    }

    /**
     * 提供custid查询成员客户资料
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebCustInfoByCustIdAndRoute(IBizCommon bc, String mebCustId, String routeId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(mebCustId)) {
            if (isThrowException)
                CSViewException.apperr(CustException.CRM_CUST_77);
            return null;
        }

        IData idCustInfo = UCAInfoIntf.qryCustInfoByCustIdAndRoute(bc, mebCustId, routeId);
        if (IDataUtil.isEmpty(idCustInfo)) {
            if (isThrowException)
                CSViewException.apperr(CustException.CRM_CUST_194, mebCustId + "[" + routeId + "]");
            return null;
        }
        return idCustInfo;
    }

    /**
     * @param bc
     * @param mebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryMebDefAcctInfoByUserId(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        return qryMebDefAcctInfoByUserId(bc, mebUserId, routeId, true);
    }

    /**
     * 通过成员用户ID查询默认付费账户
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryMebDefAcctInfoByUserId(IBizCommon bc, String mebUserId, String routeId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(mebUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_429);
            return null;
        }
        IData mebAcctData = UCAInfoIntf.qryUserDefAcctInfoByUserId(bc, mebUserId, routeId);
        if (IDataUtil.isEmpty(mebAcctData)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14, mebUserId);
            return null;
        }
        return mebAcctData;
    }

    /**
     * 根据成员用户查询默认付费关系
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryMebDefaultPayRelaInfoByUserIdAndRoute(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        return qryMebDefaultPayRelaInfoByUserIdAndRoute(bc, mebUserId, routeId, true);
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
    public static IData qryMebDefaultPayRelaInfoByUserIdAndRoute(IBizCommon bc, String mebUserId, String routeId, boolean isThrowException) throws Exception {
        if (StringUtils.isEmpty(mebUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_429);
            return null;
        }
        IData payRelaInfo = UCAInfoIntf.qryUserDefaultPayRelaInfoByUserIdAndRoute(bc, mebUserId, routeId);
        if (IDataUtil.isEmpty(payRelaInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_14, mebUserId);
            return null;
        }
        return payRelaInfo;
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
    public static String qryMebEparchyCodeBySnForCrm(IBizCommon bc, String serialNumber, String routeEparchyCode) throws Exception {
        IData eaprchyCodeInfoData = UCAInfoIntf.qryMebEparchyCodeBySnForCrm(bc, serialNumber, routeEparchyCode);
        if (IDataUtil.isEmpty(eaprchyCodeInfoData)) {
            return null;
        } else {
            return eaprchyCodeInfoData.getString("EPARCHY_CODE");
        }
    }

    /**
     * 查询成员用户ID订购的集团产品信息
     * 
     * @param bc
     * @param strMebUserId
     * @param relationCode
     *            1 查询UU表 2查询BB表 其它 全部查询
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryMebOrderedGroupInfosByMebUserIdAndRelationCode(IBizCommon bc, String strMebUserId, String relationCode, String routeId, boolean isThrowException) throws Exception {
        return qryMebOrderedGroupInfosByMebUserIdAndRelationCodeHasPriv(bc, strMebUserId, relationCode, routeId, "false", isThrowException);
    }

    /**
     * 查询成员用户ID订购的集团产品信息 支持产品权限过滤
     * 
     * @param bc
     * @param strMebUserId
     * @param relationCode
     * @param routeId
     * @param privForProduct
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryMebOrderedGroupInfosByMebUserIdAndRelationCodeHasPriv(IBizCommon bc, String strMebUserId, String relationCode, String routeId, String privForProduct, boolean isThrowException) throws Exception {
        IDataset groupinfo = UCAInfoIntf.qryMebOrderedGroupInfosByMebUserIdAndRelationCodeHasPriv(bc, strMebUserId, relationCode, routeId, privForProduct);
        if (IDataUtil.isEmpty(groupinfo)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_519, strMebUserId);
            return null;

        }
        return groupinfo;
    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IData qryMebOrderedGroupInfosBySn(IBizCommon bc, String strMebSn, String relationCode) throws Exception {
        return qryMebOrderedGroupInfosBySn(bc, strMebSn, relationCode, true);
    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IData qryMebOrderedGroupInfosBySn(IBizCommon bc, String strMebSn, String relationCode, boolean isThrowException) throws Exception {
        return qryMebOrderedGroupInfosBySnAndLimitProducts(bc, strMebSn, relationCode, "", "", isThrowException);

    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param relationCode
     * @param limitType
     * @param limitProducts
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryMebOrderedGroupInfosBySnAndLimitProducts(IBizCommon bc, String strMebSn, String relationCode, String limitType, String limitProducts, boolean isThrowException) throws Exception {
        return qryMebOrderedGroupInfosBySnAndLimitProducts(bc, strMebSn, relationCode, limitType, limitProducts, isThrowException, true);
    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param relationCode
     * @param limitType
     * @param limitProducts
     * @param isThrowException
     * @param judgeUserStateCode
     * @return
     * @throws Exception
     */
    public static IData qryMebOrderedGroupInfosBySnAndLimitProducts(IBizCommon bc, String strMebSn, String relationCode, String limitType, String limitProducts, boolean isThrowException, boolean judgeUserStateCode) throws Exception {
        IData mebinfo = qryMebUCAAndAcctDayInfoBySn(bc, strMebSn, isThrowException, judgeUserStateCode);
        if (IDataUtil.isEmpty(mebinfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_117, strMebSn);
            return null;
        }

        IData mebUserInfo = mebinfo.getData("MEB_USER_INFO");
        IData mebCustInfo = mebinfo.getData("MEB_CUST_INFO");
        if (IDataUtil.isEmpty(mebUserInfo))
            mebUserInfo = new DataMap();
        if (IDataUtil.isEmpty(mebCustInfo))
            mebCustInfo = new DataMap();

        String user_id = mebUserInfo.getString("USER_ID");
        String routeEparchyCode = mebUserInfo.getString("EPARCHY_CODE");

        IDataset groupinfo = qryMebOrderedGroupInfosByMebUserIdAndRelationCodeHasPriv(bc, user_id, relationCode, routeEparchyCode, "true", false);
        if (IDataUtil.isEmpty(groupinfo)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_268, strMebSn);
            return null;

        }

        if (StringUtils.isBlank(limitProducts)) {

            mebinfo.put("ORDERED_GROUPINFOS", groupinfo);
            return mebinfo;
        }

        if (StringUtils.isBlank(limitType)) {
            limitType = "1";
        }

        GroupProductUtilView.dealLimitProduct(groupinfo, limitType, limitProducts, "PRODUCT_ID");
        if (IDataUtil.isEmpty(groupinfo)) {
            if (isThrowException) {
                String productNames = GroupProductUtilView.getProductNames(bc, limitProducts);
                if (limitType.equals("1")) {
                    CSViewException.apperr(GrpException.CRM_GRP_773, productNames);
                } else {
                    CSViewException.apperr(GrpException.CRM_GRP_268, strMebSn);
                }
            }
            return null;

        }

        mebinfo.put("ORDERED_GROUPINFOS", groupinfo);
        return mebinfo;
    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param relationCode1
     *            查询UU表 2查询BB表 其它 全部查询
     * @return
     * @throws Exception
     */

    public static IData qryMebOrderedGroupInfosBySnAndRela(IBizCommon bc, String strMebSn, String relationCode) throws Exception {
        return qryMebOrderedGroupInfosBySnAndRela(bc, strMebSn, relationCode, true);
    }

    /**
     * 查询号码订购的集团产品信息
     * 
     * @param bc
     * @param strMebSn
     * @param relationCode
     *            1 查询UU表 2查询BB表 其它 全部查询
     * @param isThrowException
     * @return
     * @throws Exception
     */

    public static IData qryMebOrderedGroupInfosBySnAndRela(IBizCommon bc, String strMebSn, String relationCode, boolean isThrowException) throws Exception {
        IData mebinfo = qryMebUCAInfoBySn(bc, strMebSn, isThrowException);
        if (IDataUtil.isEmpty(mebinfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_117, strMebSn);
            return null;
        }

        IData mebUserInfo = mebinfo.getData("MEB_USER_INFO");
        IData mebCustInfo = mebinfo.getData("MEB_CUST_INFO");
        if (IDataUtil.isEmpty(mebUserInfo))
            mebUserInfo = new DataMap();
        if (IDataUtil.isEmpty(mebCustInfo))
            mebCustInfo = new DataMap();

        String user_id = mebUserInfo.getString("USER_ID");
        String routeEparchyCode = mebUserInfo.getString("EPARCHY_CODE");

        IDataset groupinfo = qryMebOrderedGroupInfosByMebUserIdAndRelationCode(bc, user_id, relationCode, routeEparchyCode, false);
        if (IDataUtil.isEmpty(groupinfo)) {
            if (isThrowException) {
                CSViewException.apperr(GrpException.CRM_GRP_268, strMebSn);
            }

            return null;

        }

        mebinfo.put("ORDERED_GROUPINFOS", groupinfo);
        return mebinfo;

    }

    /**
     * 提供号码的UCA资料查询（A暂时没查） (包含用户账期的信息)
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAAndAcctDayInfoBySn(IBizCommon bc, String strMebSn) throws Exception {
        return qryMebUCAAndAcctDayInfoBySn(bc, strMebSn, true);
    }

    /**
     * 提供号码的UCA资料查询 (包含用户账期的信息)
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAAndAcctDayInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException) throws Exception {
        return qryMebUCAAndAcctDayInfoBySn(bc, strMebSn, isThrowException, isThrowException);
    }

    /**
     * 提供号码的UCA资料查询 (包含用户账期的信息)
     * 
     * @param bc
     * @param strMebSn
     * @param isThrowException
     * @param judgeUserStateCode
     *            是否查询非正常状态的号码（海南允许非正常状态的用户办理业务。烦躁的很，查询逻辑套的多了，为了不影响已有的逻辑，增加一个判断用户状态的参数）
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAAndAcctDayInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException, boolean judgeUserStateCode) throws Exception {

        IData mebUCAInfo = qryMebUCAInfoBySn(bc, strMebSn, isThrowException, judgeUserStateCode);
        if (IDataUtil.isEmpty(mebUCAInfo)) {
            return mebUCAInfo;
        }

        // 取成员账期信息
        IData idUserInfo = mebUCAInfo.getData("MEB_USER_INFO");
        String mebUserId = idUserInfo.getString("USER_ID", "");
        String mebEparchyCode = idUserInfo.getString("EPARCHY_CODE", "");

        // 成员用户结账日信息
        IData mebUserAcctDay = qryMebUserAcctDayInfoUserId(bc, mebUserId, mebEparchyCode, isThrowException);
        mebUCAInfo.put("MEB_ACCTDAY_INFO", mebUserAcctDay);

        return mebUCAInfo;

    }

    /**
     * 提供号码的UCA资料查询（A暂时没查）
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAInfoBySn(IBizCommon bc, String strMebSn) throws Exception {
        return qryMebUCAInfoBySn(bc, strMebSn, true);
    }

    /**
     * 提供号码的UCA资料查询
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException) throws Exception {
        return qryMebUCAInfoBySn(bc, strMebSn, isThrowException, isThrowException);

    }

    /**
     * 提供号码的UCA资料查询
     * 
     * @param bc
     * @param strMebSn
     * @param isThrowException
     * @param judgeUserStateCode
     *            是否查询非正常状态的号码（海南允许非正常状态的用户办理业务。烦躁的很，查询逻辑套的多了，为了不影响已有的逻辑，增加一个判断用户状态的参数）
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException, boolean judgeUserStateCode) throws Exception {
        // 查询成员用户信息
        IData idUserInfo = qryMebUserInfoBySn(bc, strMebSn, isThrowException, judgeUserStateCode);
        if (IDataUtil.isEmpty(idUserInfo)) {
            return null;
        }
        // 查询成员客户信息
        IData idCustInfo = qryMebCustInfoByCustIdAndRoute(bc, idUserInfo.getString("CUST_ID"), idUserInfo.getString("EPARCHY_CODE"), isThrowException);

        // 查询成员账户信息
        IData idAcctInfo = qryMebDefAcctInfoByUserId(bc, idUserInfo.getString("USER_ID"), idUserInfo.getString("EPARCHY_CODE"), isThrowException);
        // 查询结果
        IData resultInfo = new DataMap();
        resultInfo.put("MEB_CUST_INFO", idCustInfo);
        resultInfo.put("MEB_USER_INFO", idUserInfo);
        resultInfo.put("MEB_ACCT_INFO", idAcctInfo);
        return resultInfo;

    }

    /**
     * 提供成员用户的UCA资料查询（A暂时没查）
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId) throws Exception {
        return qryMebUCAInfoByUserIdAndRoute(bc, strMebUserId, routeId, true);
    }

    /**
     * 提供成员用户的UCA资料查询（A暂时没查）
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUCAInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId, boolean isThrowException) throws Exception {

        // 查询成员用户信息
        IData idUserInfo = qryMebUserInfoByUserIdAndRoute(bc, strMebUserId, routeId, isThrowException);

        // 查询成员客户信息
        IData idCustInfo = qryMebCustInfoByCustIdAndRoute(bc, idUserInfo.getString("CUST_ID"), idUserInfo.getString("EPARCHY_CODE"), isThrowException);

        // 查询成员账户信息
        IData idAcctInfo = qryMebDefAcctInfoByUserId(bc, strMebUserId, idUserInfo.getString("EPARCHY_CODE"), isThrowException);

        IData resultInfo = new DataMap();
        resultInfo.put("MEB_CUST_INFO", idCustInfo);
        resultInfo.put("MEB_USER_INFO", idUserInfo);
        resultInfo.put("MEB_ACCT_INFO", idAcctInfo);
        return resultInfo;

    }

    /**
     * 查询用户账期信息
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUserAcctDayInfoUserId(IBizCommon bc, String userId, String route) throws Exception {
        return qryMebUserAcctDayInfoUserId(bc, userId, route, true);
    }

    /**
     * 查询用户账期信息
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUserAcctDayInfoUserId(IBizCommon bc, String userId, String route, boolean isThrowException) throws Exception {
        // 取用户账期信息
        IData mebUserAcctDay = UCAInfoIntf.qryUserAcctDayAndFirstDateInfoByUserId(bc, userId, route);
        if (IDataUtil.isEmpty(mebUserAcctDay)) {
            if (isThrowException)
                CSViewException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
            return null;
        }

        // 获取成员账期分布标志
        if (IDataUtil.isNotEmpty(mebUserAcctDay)) {
            String acctdayDistribution = GroupDiversifyUtilCommon.getUserAcctDayDistribute(mebUserAcctDay, "1");
            mebUserAcctDay.put("USER_ACCTDAY_DISTRIBUTION", acctdayDistribution);
        }
        return mebUserAcctDay;
    }

    /**
     * 提供号码的用户资料查询
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUserInfoBySn(IBizCommon bc, String strMebSn) throws Exception {
        return qryMebUserInfoBySn(bc, strMebSn, true);
    }

    /**
     * 提供号码的用户资料查询
     * 
     * @param bc
     * @param strMebSn
     * @return
     * @throws Exception
     */
    public static IData qryMebUserInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException) throws Exception {
        return qryMebUserInfoBySn(bc, strMebSn, isThrowException, isThrowException);
    }

    /**
     * 提供号码的用户资料查询 支持查询非正常状态的号码
     * 
     * @param bc
     * @param strMebSn
     * @param isThrowException
     *            查询失败是否抛出异常
     * @param judgeUserStateCode
     *            是否查询非正常状态的号码（海南允许非正常状态的用户办理业务。烦躁的很，查询逻辑套的多了，为了不影响已有的逻辑，增加一个判断用户状态的参数）
     * @return
     * @throws Exception
     */
    public static IData qryMebUserInfoBySn(IBizCommon bc, String strMebSn, boolean isThrowException, boolean judgeUserStateCode) throws Exception {
        IData idUserInfo = qryUserInfoBySn(bc, strMebSn, isThrowException);
        if (IDataUtil.isNotEmpty(idUserInfo)) {
            // 是否集团服务号码
            String strGrpSn = idUserInfo.getString("IsGrpSn", "");
            if ("Yes".equals(strGrpSn)) {
                if (isThrowException)
                    CSViewException.apperr(GrpException.CRM_GRP_120, strMebSn);
                return null;
            }

            if (judgeUserStateCode) {
                String userStateCodeSet = idUserInfo.getString("USER_STATE_CODESET", "");
                if (!userStateCodeSet.equals("0") && !userStateCodeSet.equals("N") && !userStateCodeSet.equals("00")) {
                    if (isThrowException)
                        CSViewException.apperr(CrmUserException.CRM_USER_298);
                    return null;
                }
            }

            return idUserInfo;
        }

        if (isThrowException) {
            CSViewException.apperr(CrmUserException.CRM_USER_117, strMebSn);
            return null;
        }

        return idUserInfo;
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
    public static IData qryMebUserInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId) throws Exception {
        return qryMebUserInfoByUserIdAndRoute(bc, strMebUserId, routeId, true);
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
    public static IData qryMebUserInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId, boolean isThrowException) throws Exception {
        return qryMebUserInfoByUserIdAndRoute(bc, strMebUserId, routeId, isThrowException, isThrowException);
    }

    /**
     * 提供用户ID查询用户资料
     * 
     * @param bc
     * @param strMebUserId
     * @param routeId
     * @param isThrowException
     * @param judgeUserStateCode
     * @return
     * @throws Exception
     */
    public static IData qryMebUserInfoByUserIdAndRoute(IBizCommon bc, String strMebUserId, String routeId, boolean isThrowException, boolean judgeUserStateCode) throws Exception {
        // 查询成员用户信息
        if (StringUtils.isEmpty(strMebUserId)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_429);
            return null;
        }
        IData idUserInfo = UCAInfoIntf.qryUserInfoByUserIdAndRoute(bc, strMebUserId, routeId);
        if (IDataUtil.isEmpty(idUserInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_554, strMebUserId);
            return null;
        }

        if (judgeUserStateCode) {
            String userStateCodeSet = idUserInfo.getString("USER_STATE_CODESET", "");
            if (!userStateCodeSet.equals("0") && !userStateCodeSet.equals("N") && !userStateCodeSet.equals("00")) {
                CSViewException.apperr(CrmUserException.CRM_USER_298);
                return null;
            }
        }

        return idUserInfo;
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
    public static IDataset qryMebValidPayRelaInfoByUserIdAndRoute(IBizCommon bc, String mebUserId, String routeId) throws Exception {
        return UCAInfoIntf.qryUserValidPayRelaInfoByUserIdAndRoute(bc, mebUserId, routeId);
    }

    /**
     * 通过集团名称模糊查询铁通集团资料
     * 
     * @param bc
     * @param grpCustName
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput qryTTGrpCustInfoByCustName(IBizCommon bc, String grpCustName, Pagination pagination) throws Exception {
        return UCAInfoIntf.qryTTGrpCustInfoByCustName(bc, grpCustName, pagination);
    }

    /**
     * 通过集团标识查询铁通集团客户信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryTTGrpCustInfoByGrpCustId(IBizCommon bc, String grpCustId, boolean isThrowException) throws Exception {
        IData result = qryGrpCustInfoByGrpCustId(bc, grpCustId, isThrowException);
        if (IDataUtil.isNotEmpty(result)) {
            String ttrhTag = result.getString("RSRV_NUM3", "");
            if (!"1".equals(ttrhTag)) {
                if (isThrowException)
                    CSViewException.apperr(GrpException.CRM_GRP_801, grpCustId);
                return null;
            }
        }
        return result;
    }

    /**
     * 通过集团编码查询铁通集团客户信息
     * 
     * @param bc
     * @param grpId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryTTGrpCustInfoByGrpId(IBizCommon bc, String grpId, boolean isThrowException) throws Exception {
        IData result = qryGrpCustInfoByGrpId(bc, grpId, isThrowException);
        if (IDataUtil.isNotEmpty(result)) {
            String ttrhTag = result.getString("RSRV_NUM3", "");
            if (!"1".equals(ttrhTag)) {
                if (isThrowException)
                    CSViewException.apperr(GrpException.CRM_GRP_800, grpId);
                return null;
            }
        }
        return result;
    }

    /**
     * 通过证件号码查询铁通集团资料
     * 
     * @param bc
     * @param psptTypeCode
     * @param psptId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryTTGrpCustInfoByPsptId(IBizCommon bc, String psptTypeCode, String psptId, boolean isThrowException) throws Exception {
        IDataset grpInfos = UCAInfoIntf.qryTTGrpCustInfoByPsptId(bc, psptTypeCode, psptId);

        if (IDataUtil.isEmpty(grpInfos)) {
            if (isThrowException)
                CSViewException.apperr(GrpException.CRM_GRP_192);
            return null;
        }
        return grpInfos;
    }

    /**
     * 根据集团用户ID查询铁通集团三户信息（A信息暂时不查）
     * 
     * @author luoyong
     * @throws Throwable
     */
    public static IData qryTTGrpUCAInfoByGrpSn(IBizCommon bc, String grpSn, boolean isThrowException) throws Exception {

        IData resultData = qryGrpUCAInfoByGrpSn(bc, grpSn, isThrowException);
        if (IDataUtil.isNotEmpty(resultData)) {
            IData grpInfoData = resultData.getData("GRP_CUST_INFO");
            if (IDataUtil.isNotEmpty(grpInfoData)) {
                String ttrhTag = grpInfoData.getString("RSRV_NUM3");
                if (!"1".equals(ttrhTag)) {
                    if (isThrowException)
                        CSViewException.apperr(GrpException.CRM_GRP_345);
                    return null;

                }
            }
        }
        return resultData;

    }

    /**
     * 查询号码的用户信息
     * 
     * @param bc
     * @param strSn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySn(IBizCommon bc, String strSn) throws Exception {
        return qryUserInfoBySn(bc, strSn, true);
    }

    /**
     * 查询号码的用户信息
     * 
     * @param bc
     * @param strMebSn
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySn(IBizCommon bc, String strSn, boolean isThrowException) throws Exception {
        // 查询用户信息
        if (StringUtils.isEmpty(strSn)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_114);
            return null;
        }
        IData idUserInfo = UCAInfoIntf.qryUserInfoBySn(bc, strSn);
        if (IDataUtil.isEmpty(idUserInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_117, strSn);
            return null;
        }

        return idUserInfo;

    }

    /**
     * 查询号码的用户信息，(不关联用户产品表查询)
     * 
     * @param bc
     * @param strSn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySnNoProduct(IBizCommon bc, String strSn) throws Exception {
        return qryUserInfoBySnNoProduct(bc, strSn, true);
    }

    /**
     * 查询号码的用户信息(不关联用户产品表查询)
     * 
     * @param bc
     * @param strSn
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySnNoProduct(IBizCommon bc, String strSn, boolean isThrowException) throws Exception {
        // 查询用户信息
        if (StringUtils.isEmpty(strSn)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_114);
            return null;
        }
        IData idUserInfo = UCAInfoIntf.qryUserInfoBySnNoProduct(bc, strSn, "0");
        if (IDataUtil.isEmpty(idUserInfo)) {
            if (isThrowException)
                CSViewException.apperr(CrmUserException.CRM_USER_117, strSn);
            return null;
        }

        return idUserInfo;

    }

    /**
     * 查询集团用户VPN信息
     * 
     * @param bc
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserVpnInfoByUserId(IBizCommon bc, String userId) throws Exception {
        return qryUserVpnInfoByUserId(bc, userId, true);
    }

    /**
     * 查询集团用户VPN信息
     * 
     * @param bc
     * @param userId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserVpnInfoByUserId(IBizCommon bc, String userId, boolean isThrowException) throws Exception {
        IData userVpnData = UCAInfoIntf.qryUserVpnInfoByUserId(bc, userId);

        if (IDataUtil.isEmpty(userVpnData) && isThrowException) {
            CSViewException.apperr(VpmnUserException.VPMN_USER_6, userId);
        }

        return userVpnData;
    }

    /**
     * 查询携转用户
     * 
     * @param bc
     * @param userId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryUserNumberPortabilityInfoByUserId(IBizCommon bc, String userId, String remove_tag, boolean isThrowException) throws Exception {
        return UCAInfoIntf.qryUserInfoBySnNoProduct(bc, userId, remove_tag);
    }

    /**
     * 根据集团客户标志custId 查询物联网产品是否同步平台 RSRV_TAG5,'0','未同步','1','已同步','2','正在同步','3','同步失败'
     * 
     * @param bc
     * @param custId
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public static String queryUserInfoByCustId(IBizCommon bc, String custId) throws Exception {
        String flg = "0";
        IDataset userInfoData = UCAInfoIntf.queryUserInfoByCustId(bc, custId);
        if (userInfoData.size() > 0) {
            IData idata = (IData) userInfoData.get(0);
            flg = idata.getString("RSRV_TAG5", "0");
        }
        return flg;
    }

    public static IData qryCustManagerByCustManagerId(IBizCommon bc, String custManagerId) throws Exception {
        return UCAInfoIntf.qryCustManagerByCustManagerId(bc, custManagerId);
    }

    /**
     * 根据集团客户标志查询集团用户信息
     *
     * @param bc
     * @param groupId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfoByGroupId(IBizCommon bc, String groupId, boolean isThrowException) throws Exception
    {

        IDataset userInfos = UCAInfoIntf.qryGrpUserInfoByGroupId(bc, groupId);
        if (IDataUtil.isEmpty(userInfos) && isThrowException)
        {
            CSViewException.apperr(GrpException.CRM_GRP_197, "集团编码:" + groupId);
            return null;
        }
        return userInfos;
    }
    
}
