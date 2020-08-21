package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.StaffException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQrySVC;

public class GrpUserQryIntf {

    /**
     * 判断产品是否可以办理
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset checkOperProduct(IData inParam) throws Exception {
        IData result = new DataMap();

        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String staffId = IDataUtil.getMandaData(inParam, "STAFF_ID");
        String operCode = IDataUtil.getMandaData(inParam, "OPER_CODE");

        // 查询客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        String custId = grpCustData.getString("CUST_ID", "");

        boolean isPriv = StaffPrivUtil.isProdPriv(staffId, productId);

        if (isPriv == false) {
            CSAppException.apperr(StaffException.CRM_STAFF_3, staffId, productId);
        }

        IDataset userInfoList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);

        if ("0".equals(operCode)) {
            boolean canOrderMore = false;
            // 查询是否可以重复订购配置
            IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CGM", "5", productId, "ZZZZ");

            if (IDataUtil.isNotEmpty(commparaList)) {
                canOrderMore = true;
            }

            if (!canOrderMore) {
                // 查询用户品牌信息
                String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

                // BBOSS接口可以办理多次
                if ("BOSG".equals(brandCode) && "6".equals(CSBizBean.getVisit().getInModeCode())) {
                    canOrderMore = true;
                }
            }

            if (IDataUtil.isNotEmpty(userInfoList) && !canOrderMore) {
                CSAppException.apperr(CrmUserException.CRM_USER_1188);
            }
        } else if ("1".equals(operCode) || "2".equals(operCode)) {
            if (IDataUtil.isEmpty(userInfoList)) {
                CSAppException.apperr(CrmUserException.CRM_USER_1189, productId);
            }
        }

        result.put("OPER_CODE", "0");

        return IDataUtil.idToIds(result);
    }

    /**
     * 查询动力100用户信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryUserPowerProductInfo(IData inParam) throws Exception {
        return UserPowerProductQryBean.qryUserPowerProductInfo(inParam);
    }

    /**
     * 根据GROUP_ID查询集团客户信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpCustInfo(IData inParam) throws Exception {
        String groupId = IDataUtil.chkParam(inParam, "GROUP_ID");
        String removeTag = inParam.getString("REMOVE_TAG", "0");

        return GrpInfoQry.qryGrpInfoByGroupIdAndRemoveTag(groupId, removeTag);
    }

    /**
     * 根据集团编码查询集团产品订购关系
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserProInfo(IData inParam, Pagination pg) throws Exception {
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");

        String removeTag = inParam.getString("REMOVE_TAG", "0");// 0:已经订购的; 1:全部产品; 2:退订产品

        switch (Integer.valueOf(removeTag)) {
        case 0:
            removeTag = "0";
            break;
        case 1:
            removeTag = null;
            break;
        case 2:
            removeTag = "1";
            break;
        default:
            CSAppException.apperr(ParamException.CRM_PARAM_360, removeTag);
            break;
        }

        IDataset userInfoList = UserInfoQry.getGrpUserInfoByGrpId(groupId, removeTag);

        int totalNum = userInfoList.size();

        for (int i = 0; i < totalNum; i++) {
            IData userData = userInfoList.getData(i);

            String productName = UProductInfoQry.getProductNameByProductId(userData.getString("PRODUCT_ID"));
            userData.put("PRODUCT_NAME", productName);
            userData.put("TOTAL_NUM", String.valueOf(totalNum));

            if (pg != null) {
                userData.put("X_PAGE_COUNT", String.valueOf(totalNum));
            }
        }
        return userInfoList;
    }

    /**
     * 根据集团编码查询ADC用户信息
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAdcUserInfo(IData inParam, Pagination pg) throws Exception {
        String groupId = IDataUtil.chkParam(inParam, "GROUP_ID");

        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
        }

        String custId = grpCustData.getString("CUST_ID");
        String custName = grpCustData.getString("CUST_NAME");
        String juristicCustId = grpCustData.getString("JURISTIC_CUST_ID");
        String busiLicenceNo = grpCustData.getString("BUSI_LICENCE_NO");
        String eparchyCode = grpCustData.getString("EPARCHY_CODE");

        IDataset userList = UserInfoQry.qryGrpAdcUserByCustId(custId, pg);

        // 遍历用户信息
        for (int i = 0, row = userList.size(); i < row; i++) {
            IData userData = userList.getData(i);

            userData.put("CUST_NAME", custName);
            userData.put("JURISTIC_CUST_ID", juristicCustId);
            userData.put("BUSI_LICENCE_NO", busiLicenceNo);
            userData.put("EPARCHY_CODE", eparchyCode);

            String productId = userData.getString("PRODUCT_ID");
            String productName = UProductInfoQry.getProductNameByProductId(productId);

            userData.put("PRODUCT_NAME", productName);
        }

        return userList;
    }

    /**
     * 根据集团USER_ID查询集团用户客户信息
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserInfo(IData inParam, Pagination pg) throws Exception {
        String userId = IDataUtil.getMandaData(inParam, "USER_ID");

        // 查询集团用户信息
        IData grpUserData = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        if (IDataUtil.isEmpty(grpUserData)) {
            CSAppException.apperr(CrmUserException.CRM_USER_212, userId);
        }

        String custId = grpUserData.getString("CUST_ID", "");

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(custId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(CustException.CRM_CUST_181, custId);
        }

        grpUserData.putAll(grpCustData);

        return IDataUtil.idToIds(grpUserData);
    }

    /**
     * 查询用户参数信息
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryBBOSSUserOrderInfo(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryBBOSSUserOrderInfo(inParam);
    }

    /**
     * 查询用户平台服务数据, 供ESOP使用
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserPlatSvcParam(IData inParam, Pagination pg) throws Exception {

        IDataset retDataset = new DatasetList();

        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");

        // String userId = inParam.getString("USER_ID");

        String staffId = IDataUtil.chkParam(inParam, "TRADE_STAFF_ID");
        boolean isPriv = StaffPrivUtil.isProdPriv(staffId, productId);
        if (!isPriv) {
            CSAppException.apperr(CrmCommException.CRM_COMM_498);
        }

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        // 查询集团用户信息
        IData grpUserData = null;
        String custId = grpCustData.getString("CUST_ID");
        IDataset grpUserList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);
        if (IDataUtil.isNotEmpty(grpUserList)) {
            grpUserData = grpUserList.getData(0);
        }

        if (IDataUtil.isEmpty(grpUserData)) {
            CSAppException.apperr(CrmUserException.CRM_USER_1016, productId);
        }

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        String userId = grpUserData.getString("USER_ID");

        IDataset platSvcParamList = new DatasetList();

        // 根据不同的品牌, 取不同的参数表
        if ("ADCG".equals(brandCode) || "MASG".equals(brandCode)) {
            platSvcParamList = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(userId);
        } else if ("VGPR".equals(brandCode)) // E网类取USER表备注字段
        {
            platSvcParamList.add(grpUserData);
        } else if ("BOSG".equals(brandCode)) // BBOSS类取属性表
        {
            IDataset userAttrList = UserAttrInfoQry.getUserAttrByUserIda(userId);
            if (IDataUtil.isNotEmpty(userAttrList)) {
                platSvcParamList.add(IDataUtil.hTable2StdSTable(userAttrList, "ATTR_CODE", "ATTR_VALUE"));
            }
        }

        // 处理平台服务列表
        if (IDataUtil.isNotEmpty(platSvcParamList)) {
            for (int i = 0, row = platSvcParamList.size(); i < row; i++) {
                IData platSvcData = platSvcParamList.getData(i);

                String paramCode = productId;

                String serviceId = platSvcData.getString("SERVICE_ID", "");

                if ("ADCG".equals(brandCode) || "MASG".equals(brandCode)) {
                    IDataset userAttrList = UserAttrInfoQry.getuserAttrBySvcId(userId, serviceId);

                    platSvcData.putAll(IDataUtil.hTable2StdSTable(userAttrList, "ATTR_CODE", "ATTR_VALUE"));

                    paramCode = serviceId;
                }

                IDataset commParaList = CommparaInfoQry.getCommpara("CGM", "1215", paramCode, CSBizBean.getTradeEparchyCode());

                if (IDataUtil.isNotEmpty(commParaList)) {
                    IData retData = new DataMap();

                    for (int j = 0, jRow = commParaList.size(); j < jRow; j++) {
                        IData commParaData = commParaList.getData(j);

                        String crmKey = commParaData.getString("PARA_CODE2", "").trim();
                        String d2dKey = commParaData.getString("PARA_CODE3", "").trim();
                        String value = platSvcData.getString(crmKey, "");

                        if (StringUtils.isNotEmpty(crmKey) && StringUtils.isNotEmpty(d2dKey) && StringUtils.isNotEmpty(value)) {
                            retData.put(d2dKey, value);
                        }
                    }

                    retDataset.add(retData);
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_1142);
                }
            }
        }
        return retDataset;
    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公开户号码查询
     *
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpPubUserInfo(IData inParam, Pagination pg) throws Exception {
        // 集团编码
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");

        // 根据集团GROUP_ID查询集团用户信息
        IDataset idsGrpAcctUserInfo = new DatasetList();
        idsGrpAcctUserInfo = UserGrpInfoQry.getGrpPubUserInfo(groupId);
        for (int i = 0; i < idsGrpAcctUserInfo.size(); i++) {
            idsGrpAcctUserInfo.getData(i).put("TOTAL_NUM", idsGrpAcctUserInfo.size());// 把TOTAL_NUM放入每一条记录
        }

        return idsGrpAcctUserInfo;

    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公托收号码查询
     *
     * @author liujy
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpAcctUserInfo(IData inParam, Pagination pg) throws Exception {
        // 集团编码
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");

        // 根据集团GROUP_ID查询集团用户信息总记录数
        IDataset idsTotalNum = UserGrpInfoQry.getGrpAcctUserInfoTotalNum(groupId);
        IData idtotalNum = (IData) idsTotalNum.get(0);

        IDataset idsGrpAcctUserInfo = new DatasetList();
        idsGrpAcctUserInfo = UserGrpInfoQry.getGrpAcctUserInfo(groupId);
        for (int i = 0; i < idsGrpAcctUserInfo.size(); i++) {
            idsGrpAcctUserInfo.getData(i).put("TOTAL_NUM", idtotalNum.getString("COUNT(*)"));// 把TOTAL_NUM放入每一条记录
        }

        return idsGrpAcctUserInfo;
    }

    /**
     * chenyi 2014-6-10 根据group_id查询集团信息化产品
     *
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getGroupProductInfo(IData data, Pagination pg) throws Exception {
        String grpId = IDataUtil.getMandaData(data, "GROUP_ID");

        IData imparam = new DataMap();
        imparam.put("GROUP_ID", grpId);
        IDataset idsGrpId = GrpInfoQry.getGroupProductInfoByGID(imparam);
        if (IDataUtil.isNotEmpty(idsGrpId)) {
            IData idsgrpData = idsGrpId.getData(0);
            String class_id = idsgrpData.getString("CLASS_ID");

            String cust_class_type = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_GROUPEXT", new String[] { "PARAMTYPE", "PARAMCODE" }, "PARAMVALUE", new String[] { "groupclass", class_id });

            idsgrpData.put("CUST_CLASS_TYPE", cust_class_type);
            String city_code = idsgrpData.getString("CITY_CODE");

            String city = UAreaInfoQry.getAreaNameByAreaCode(city_code);
            idsgrpData.put("CITY", city);

            String cust_manager_id = idsgrpData.getString("MANAGER_STAFF_ID");
            String staff_name = UStaffInfoQry.getStaffNameByStaffId(cust_manager_id);
            idsgrpData.put("STAFF_NAME", staff_name);

            String product_id = idsgrpData.getString("PRODUCT_ID");
            String product_name = UProductInfoQry.getProductNameByProductId(product_id);
            idsgrpData.put("PRODUCT_NAME", product_name);

        } else {

            CSAppException.apperr(CustException.CRM_CUST_996, grpId);
        }

        return idsGrpId;
    }

    /**
     * chenyi 2014-6-10 集团产品信息查询
     *
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByGroupId(IData data, Pagination pg) throws Exception {
        String grpId = IDataUtil.getMandaData(data, "GROUP_ID");

        IDataset idsGrpId = GrpInfoQry.qryUserPrdByGrpId(grpId);

        if (IDataUtil.isNotEmpty(idsGrpId)) {
            for (int i = 0; i < idsGrpId.size(); i++) {

                IData idsGrpIdData = idsGrpId.getData(i);
                String productId = idsGrpIdData.getString("PRODUCT_ID");

                IData productInfo = UProductInfoQry.qryProductByPK(productId);

                if (IDataUtil.isNotEmpty(productInfo)) {

                    String product_name = productInfo.getString("PRODUCT_NAME");
                    idsGrpIdData.put("PRODUCT_NAME", product_name);

                    String start_date = productInfo.getString("START_DATE");
                    idsGrpIdData.put("START_DATE", start_date);

                    String end_date = productInfo.getString("END_DATE");
                    idsGrpIdData.put("END_DATE", end_date);
                }
            }
        }

        return idsGrpId;
    }

    /**
     * chenyi 2014-6-10 根据 GROUP_ID 查询集团客户已开通业务的接口
     *
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getqryECBizByGrpID(IData data, Pagination pg) throws Exception {
        String grpId = IDataUtil.getMandaData(data, "GROUP_ID");

        IDataset idsGrpId = UserGrpPlatSvcInfoQry.qryECBizByGrpID(grpId);

        return idsGrpId;
    }

    /**
     * 根据USER_ID判断是否集团服务号码
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset isGrpSerialNumber(IData inParam) throws Exception {
        String userId = IDataUtil.chkParam(inParam, "USER_ID");

        IData userData = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        if (IDataUtil.isEmpty(userData)) {
            return new DatasetList();
        }

        // 根据产品PRODUCT_MODE判断是否集团服务号码
        if (!GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(userData.getString("PRODUCT_MODE"))) {
            return new DatasetList();
        }

        return IDataUtil.idToIds(userData);
    }

    /**
     * 根据集团编号PRODUCT_ID，GROUP_ID查询BBOSS办理过的子产品信息
     *
     * @author liuxx3
     * @date 2014-06-27
     */
    public static IDataset qryOperProductForBBOSS(IData inParam, Pagination pg) throws Exception {

        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryOperProductForBBOSS(inParam, pg);
    }

    /**
     * 输入：商品实例ID或产品实例ID（下表格中） 输出：GROUP_ID 集团ID EPARCHY_CODE 集团所在地市
     *
     * @author liuxx3
     * @date 2014-06-27
     */
    public static IDataset qryDataByMerchOfferId(IData inParam, Pagination pg) throws Exception {

        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryDataByMerchOfferId(inParam, pg);
    }

    /**
     * 为esop查询BBOSS商品订单号和产品订单号 add by esop
     *
     * @author liuxx3
     * @date 2014-06-30
     */
    public static IDataset getBBossOrderInfoForEsop(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.getBBossOrderInfoForEsop(inParam, pg);
    }

    /**
     * 作用：根据员工号、产品类型查询集团可办理的产品,网厅不支持一个产品可以订购多次
     *
     * @author liuxx3
     * @date 2014-06-30
     */
    public static IDataset getCanOpenProduct(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.getCanOpenProduct(inParam, pg);
    }

    /**
     * 查询集团用户订购了的套餐和可以订购的套餐接口
     *
     * @author liuxx3
     * @date 2014-07-01
     */
    public static IDataset qryGrpUserDiscntInfos(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpUserDiscntInfos(inParam, pg);
    }

    /**
     * 集团产品下成员列表查询接口(带分页查询)
     *
     * @author liuxx3
     * @date 2014-07-04
     */
    public static IDataset qryGrpMemberListInfo(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpMemberListInfo(inParam, pg);
    }

    /**
     * 作用：获取集团用户资费
     *
     * @author liuxx3
     * @date 2014-07-04
     */
    public static IDataset getGrpUserDiscnt(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.getGrpUserDiscnt(inParam, pg);
    }

    public static IDataset qryGrpMebOrderInfo(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpMebOrderInfo(inParam, pg);
    }

    /**
     * 查询集团用户平台服务信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpPlatSvcInfo(IData inParam) throws Exception {
        String userId = IDataUtil.chkParam(inParam, "USER_ID");

        IDataset userGrpPlatSvcList = UserGrpPlatSvcInfoQry.qryUserGrpPlatSvcByUserId(userId);

        if (IDataUtil.isNotEmpty(userGrpPlatSvcList)) {
            for (int i = 0, row = userGrpPlatSvcList.size(); i < row; i++) {
                IData userGrpPlatSvcData = userGrpPlatSvcList.getData(i);

                userGrpPlatSvcData.put("SERV_NAME", userGrpPlatSvcData.getString("BIZ_NAME"));
            }
        }

        return userGrpPlatSvcList;
    }

    /**
     * 根据静态参数表的类型查询静态参数
     *
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryDestroyGroupUserKind() throws Exception {
        IDataset kindList = StaticUtil.getStaticList("TD_B_REMOVE_REASON_GROUP");
        IData result = new DataMap();
        IDataset key = new DatasetList();
        IDataset value = new DatasetList();
        for (int i = 0, size = kindList.size(); i < size; i++) {
            IData kind = kindList.getData(i);

            key.add(kind.getString("DATA_ID"));
            result.put("KEY", key);

            value.add(kind.getString("DATA_NAME"));
            result.put("VALUE", value);
        }
        result.put("TOTAL_NUM", kindList.size());
        return IDataUtil.idToIds(result);
    }

    /**
     * 根据集团编号GROUP_ID查询集团产品订购关系
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getGroupUserInfoByGroupId(IData inparam) throws Exception {
        String groupId = IDataUtil.getMandaData(inparam, "GROUP_ID");

        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
        }

        String custId = grpCustData.getString("CUST_ID");
        String custName = grpCustData.getString("CUST_NAME");

        IDataset results = new DatasetList();
        IDataset userInfos = UserInfoQry.getUserInfoByCstIdForGrpHasPriv(custId, "false", null);
        for (int i = 0, size = userInfos.size(); i < size; i++) {
            IData userInfo = userInfos.getData(0);
            IData result = new DataMap();

            // 获取集团客户编码
            result.put("GROUP_ID", groupId);

            // 获取集团客户标识
            result.put("CUST_ID", custId);

            // 获取集团客户名称
            result.put("CUST_NAME", custName);

            // 获取用户分区编码
            result.put("PARTITION_ID", userInfo.getString("PARTITION_ID"));

            // 获取用户编码
            result.put("USER_ID", userInfo.getString("USER_ID"));

            // 获取集团用户服务编码
            result.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));

            // 获取用户建档时间
            result.put("IN_DATE", userInfo.getString("IN_DATE"));

            // 获取用户开户时间
            result.put("OPEN_DATE", userInfo.getString("OPEN_DATE"));

            // 获取用户服务状态编码
            result.put("USER_STATE_CODESET", userInfo.getString("USER_STATE_CODESET"));

            // 获取地市编码
            result.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

            // 获取产品编码
            String productId = userInfo.getString("PRODUCT_ID");
            result.put("PRODUCT_ID", productId);

            // 获取产品名称
            result.put("PRODUCT_NAME", userInfo.getString("PRODUCT_NAME"));

            // 获取产品说明
            IData productInfo = UProductInfoQry.qryProductByPK(productId);
            result.put("PRODUCT_EXPLAIN", productInfo.getString("PRODUCT_EXPLAIN"));

            // 获取产品生效时间
            result.put("START_DATE", productInfo.getString("START_DATE"));

            // 获取产品失效时间
            result.put("END_DATE", productInfo.getString("END_DATE"));

            // 获取产品状态编码
            result.put("X_SVCSTATE_EXPLAIN", productInfo.getString("PRODUCT_STATE"));

            // 获取品牌编码
            String brandCode = userInfo.getString("BRAND_CODE");
            result.put("BRAND_CODE", brandCode);

            // 获取品牌名称
            result.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(brandCode));

            // 获取产品类型编码
            result.put("PRODUCT_TYPE_CODE", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PTYPE_PRODUCT", "PRODUCT_ID", "PRODUCT_TYPE_CODE", productId));

            results.add(result);
        }
        return results;
    }

    /**
     * 根据集团产品编码查询集团用户客户信息
     *
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserCustInfoBySn(IData inParam, Pagination pg) throws Exception {
        String sn = IDataUtil.getMandaData(inParam, "SERIAL_NUMBER");

        // 查询集团用户信息
        IData grpUserData = UcaInfoQry.qryUserMainProdInfoBySnForGrp(sn);

        if (IDataUtil.isEmpty(grpUserData)) {
            CSAppException.apperr(CrmUserException.CRM_USER_178, sn);
        }

        String custId = grpUserData.getString("CUST_ID", "");

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(custId);

        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(CustException.CRM_CUST_181, custId);
        }

        grpUserData.putAll(grpCustData);

        return IDataUtil.idToIds(grpUserData);
    }

    /**
     * 根据集团编号GROUP_ID查询集团已订购的可进行集团业务特殊开机的产品
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpSpecialOpenProductByGroupId(IData inparam) throws Exception {
        IDataset result = new DatasetList();
        String groupId = IDataUtil.getMandaData(inparam, "GROUP_ID");
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpCustData)) {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        String custId = grpCustData.getString("CUST_ID");
        String eparchy_code = inparam.getString("TRADE_EPARCHY_CODE");
        // 调用后台服务，查集团客户信息
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        // 获取配置参数判断是否属于可特殊开机的品牌
        IDataset commParamList = CommparaInfoQry.getCommparaInfoBy16("CSM", "1111", "TYPECODE", "csGroupSpecialOpen", "15", eparchy_code);

        if (IDataUtil.isEmpty(commParamList)) {
            CSAppException.apperr(ParamException.CRM_PARAM_506, "错误", "1111");
        }

        for (int i = 0, iRow = commParamList.size(); i < iRow; i++) {
            IData commParamData = commParamList.getData(i);
            String brandCode = commParamData.getString("PARA_CODE4", "");

            // 根据CUST_ID和品牌编码查询集团的用户信息
            IDataset userList = UserInfoQry.getUserInfoByCandB(custId, brandCode, Route.CONN_CRM_CG);
            if (IDataUtil.isNotEmpty(userList)) {
                for (int j = 0, jRow = userList.size(); j < jRow; j++) {
                    IData userData = userList.getData(j);

                    String productId = userData.getString("PRODUCT_ID", "");
                    String userId = userData.getString("USER_ID");

                    if (productId.matches("9127|9188")) {
                        continue;
                    }

                    // 查询产品信息
                    IData infoData = null;
                    IDataset infosDataset = IDataUtil.idToIds(UProductInfoQry.qryProductByPK(productId));
                    if (IDataUtil.isNotEmpty(infosDataset)) {
                        infoData = infosDataset.getData(0);
                    }
                    if (IDataUtil.isEmpty(infoData)) {
                        continue;
                    }
                    String productNameString = infoData.getString("PRODUCT_NAME", "");
                    if (StringUtils.isBlank(productNameString)) {
                        continue;
                    }

                    IData addProductData = new DataMap();
                    addProductData.put("PRODUCT_ID", productId);
                    addProductData.put("PRODUCT_NAME", productNameString);
                    addProductData.put("USER_ID", userId);

                    result.add(addProductData);
                }
            }
        }
        // 设置返回数据
        return result;
    }

    /**
     * 根据集团用户编码USER_ID查询已订购服务信息
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpSpecialOpenProductByUserId(IData inparam) throws Exception {
        IDataset result = new DatasetList();
        String userid = IDataUtil.getMandaData(inparam, "USER_ID");

        IDataset userStates = UserGrpPlatSvcInfoQry.getUserAttrByUserIda(userid);// 查询用户当前订购的服务
        if (IDataUtil.isNotEmpty(userStates)) {
            for (int i = 0; i < userStates.size(); i++) {
                IData userState = new DataMap();
                IData data = userStates.getData(i);
                userState.put("NOW_STATE", "A".equals(data.getString("BIZ_STATE_CODE")) ? "正常" : "暂停");
                userState.put("SERVICE_ID", data.getString("SERVICE_ID"));
                // 根据user_id,service_id查询最后一次暂停的时间
                IData svc = new DataMap();
                svc.put("USER_ID", data.getString("USER_ID"));
                svc.put("BIZ_STATE_CODE", "N");
                svc.put("SERVICE_ID", data.getString("SERVICE_ID"));

                IDataset svcInfos = IDataUtil.idToIds(USvcInfoQry.qryServInfoBySvcId(data.getString("SERVICE_ID")));
                if (IDataUtil.isNotEmpty(svcInfos)) {
                    userState.put("SERVICE_NAME", svcInfos.getData(0).getString("SERVICE_NAME"));
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取服务名称失败");
                }
                IDataset querLasts = UserGrpPlatSvcInfoQry.getUserAttrByUserIdandSvc(svc);

                String lastParse = "0";
                if (querLasts != null && querLasts.size() > 0) {
                    for (int j = 0; j < querLasts.size(); j++) {
                        IData datat = querLasts.getData(j);
                        if ("1".equals(lastParse.compareTo(SysDateMgr.string2Date(datat.get("START_DATE").toString(), "yyyyMMddhhmmss").toString()))) {
                            // 如果lastParse大于取出的Start_date则不做任何操作,否则就赋值
                        } else if ("-1".equals(lastParse.compareTo(SysDateMgr.string2Date(datat.get("START_DATE").toString(), "yyyyMMddhhmmss").toString()))) {
                            lastParse = datat.getString("START_DATE");
                        } else {
                            lastParse = datat.getString("START_DATE");
                        }
                    }
                    userState.put("LAST_PARSE", lastParse);
                } else {
                    userState.put("LAST_PARSE", "无暂停记录");
                }
                result.add(userState);
            }
        }
        // 设置返回数据
        return result;
    }

    /**
     * xuyt 2014-6-10 集团产品信息查询
     *
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByGroupIdFlux(IData data, Pagination pg) throws Exception {
        String grpId = IDataUtil.getMandaData(data, "GROUP_ID");

        IDataset idsGrpId = GrpInfoQry.qryUserPrdByGrpIdFlux(grpId);

        IDataset ds = new DatasetList();
        IData map = new DataMap();

        if (IDataUtil.isNotEmpty(idsGrpId)) {
            for (int i = 0; i < idsGrpId.size(); i++) {

                IData idsGrpIdData = idsGrpId.getData(i);
                String serial_number = idsGrpIdData.getString("SERIAL_NUMBER");
                String campnType = "01GPYX03";
                String productid = "69901002";
                IData da = new DataMap();
                da.put("SERIAL_NUMBER", serial_number);
                da.put("CAMPN_TYPE", campnType);
                da.put("PRODUCT_ID", productid);

                IDataset productInfo = UserSaleActiveInfoQrySVC.qrySaleActiveBySnFLUX(da, pg);

                if (IDataUtil.isNotEmpty(productInfo)) {
                    String grp_serial_number = productInfo.getData(0).getString("SERIAL_NUMBER");
                    map.put("GRP_SERIAL_NUMBER", grp_serial_number);

                    String product_name = productInfo.getData(0).getString("PRODUCT_NAME");
                    map.put("PRODUCT_NAME", product_name);

                    String package_name = productInfo.getData(0).getString("PACKAGE_NAME");
                    map.put("PACKAGE_NAME", package_name);

                    String start_date = productInfo.getData(0).getString("START_DATE");
                    map.put("START_DATE", start_date);

                    String end_date = productInfo.getData(0).getString("END_DATE");
                    map.put("END_DATE", end_date);

                    ds.add(map);
                }

            }

        }

        return ds;
    }

    public static IDataset getGrpUserServiceDiscnt(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.getGrpUserServiceDiscnt(inParam, pg);
    }

    public static IDataset getGrpUserServiceDiscnt4Child(IData inParam, Pagination pg) throws Exception {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.getGrpUserServiceDiscnt4Child(inParam, pg);
    }

    /**
     * 根据集团专线名称查询集团专线的信息
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IData qryGrpUserDatalineByName(IData inparam) throws Exception {
        String userName = IDataUtil.getMandaData(inparam, "DATA_LINE_NAME");
        IDataset userData = TradeDataLineAttrInfoQry.qryGrpUserDatalineByName(userName);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(userData)) {
            result = userData.getData(0);
        }
        return result;
    }

    public static IDataset qryStaffinfoForESOPNEW(IData inparam) throws Exception {
        IDataset userData = TradeDataLineAttrInfoQry.qryStaffinfoForESOPNEW(inparam);
        return userData;
    }

    public static IDataset qryAuditinfoForESOP(IData inparam) throws Exception {
        IDataset userData = TradeDataLineAttrInfoQry.qryAuditinfoForESOP(inparam);
        return userData;
    }

    public static IDataset qryUserDatalineByProductNO(IData inparam) throws Exception {
        IDataset userData = TradeDataLineAttrInfoQry.qryUserDatalineByProductNO(inparam);
        return userData;
    }
}
