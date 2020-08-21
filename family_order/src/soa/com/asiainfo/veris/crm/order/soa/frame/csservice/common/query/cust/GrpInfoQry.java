package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class GrpInfoQry {
    /**
     * 获取集团定制规则表
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDesignRuleByPk(String productId, String packageId, String elementTypeCode, String elementId) throws Exception {

        IData designparam = new DataMap();
        designparam.put("PRODUCT_ID", productId);
        designparam.put("PACKAGE_ID", packageId);
        designparam.put("ELEMENT_TYPE_CODE", elementTypeCode);
        designparam.put("ELEMENT_ID", elementId);
        return Dao.qryByCode("TD_B_GROUP_DESIGN_RULE", "SEL_BY_PK", designparam, Route.CONN_CRM_CEN);
    }

    /**
     * 查询集团信息
     * 
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupCustInfoByGroupId(String groupId) throws Exception {
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.* ");
        parser.addSQL(" from tf_f_cust_group a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.GROUP_ID = :GROUP_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 获取集团定制规则明细表
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDetailRuleByRuleId(String ruleId) throws Exception {

        IData data = new DataMap();
        data.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_B_GROUP_DESIGN_DETAILRULE", "SEL_BY_RULEID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据MP_GROUP_CUST_CODE查询集团客户资料
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getGroupByMpGroup(IData param) throws Exception {

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_MP_CUST_CODE", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据group_Id，CustType查询集团客户信息
     * 
     * @param groupId
     * @param custType
     * @return
     * @throws Exception
     */
    public static IDataset getGroupCustInfoByGrpIdCustType(String groupId, String custType) throws Exception {

        IData cond = new DataMap();
        cond.put("GROUP_ID", groupId);
        cond.put("CUST_TYPE", custType);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_GROUPID_GROUP", cond);
    }

    /**
     * 根据GROUP_MGR_SN查询集团客户信息
     * 
     * @param grpMgrSN
     * @return
     * @throws Exception
     */
    public static IDataset getGroupCustInfoByGrpMgrSN(String grpMgrSN) throws Exception {
        IData param = new DataMap();
        param.put("GROUP_MGR_SN", grpMgrSN);
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据group_Id，CustType查询集团客户信息
     * 
     * @param psptId
     * @param psptType
     * @return
     * @throws Exception
     */
    public static IDataset getGroupCustInfoByPsptIdPsptTypeCustType(String psptId, String psptType, String custType) throws Exception {

        IData cond = new DataMap();
        cond.put("PSPT_ID", psptId);
        cond.put("CUST_TYPE", custType);
        cond.put("PSPT_TYPE_CODE", psptType);
        return Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_PSPT_GROUP", cond);
    }

    public static IDataset getGroupCustInfoForPay(IData inparams, Pagination pagination) throws Exception {
        SQLParser parser = new SQLParser(inparams);

        parser.addSQL(" SELECT A.CUST_ID, ");
        parser.addSQL(" A.GROUP_ID, ");
        parser.addSQL(" A.CUST_NAME, ");
        parser.addSQL(" B.ACCT_ID, ");
        parser.addSQL(" B.PAY_NAME, ");
        parser.addSQL(" B.RSRV_STR10, ");
        parser.addSQL(" B.EPARCHY_CODE, ");
        parser.addSQL(" C.END_CYCLE_ID, ");
        parser.addSQL(" D.USER_ID, ");
        parser.addSQL(" D.SERIAL_NUMBER, ");
        parser.addSQL(" P.PRODUCT_ID, ");
        parser.addSQL(" TO_CHAR(D.OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE ");
        parser.addSQL(" FROM TF_F_CUST_GROUP   A, ");
        parser.addSQL(" TF_F_ACCOUNT      B, ");
        parser.addSQL(" TF_A_PAYRELATION  C, ");
        parser.addSQL(" TF_F_USER         D, ");
        parser.addSQL(" TF_F_USER_PRODUCT P ");
        parser.addSQL(" WHERE A.CUST_ID = B.CUST_ID ");
        parser.addSQL(" AND C.ACCT_ID(+) = B.ACCT_ID ");
        parser.addSQL(" AND D.USER_ID(+) = C.USER_ID ");
        parser.addSQL(" AND D.USER_ID = P.USER_ID ");
        parser.addSQL(" AND A.REMOVE_TAG = '0' ");
        parser.addSQL(" AND D.REMOVE_TAG = '0' ");
        parser.addSQL(" AND (C.DEFAULT_TAG = '1' OR C.DEFAULT_TAG IS NULL) ");
        parser.addSQL(" AND (P.Main_Tag='1' OR P.Main_Tag is null) ");
        parser.addSQL(" AND (C.ACT_TAG = '1' OR C.ACT_TAG IS NULL) ");
        parser.addSQL(" AND ((TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) BETWEEN C.START_CYCLE_ID AND ");
        parser.addSQL(" C.END_CYCLE_ID) OR C.END_CYCLE_ID IS NULL) ");
        parser.addSQL(" AND A.GROUP_ID = :GROUP_ID ");

        IDataset resultset = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++) {
            IData result = resultset.getData(i);
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
        }
        return resultset;

    }

    /**
     * @description 根据黑白名单成员服务号码查询所属集团信息
     * @param serial_number
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getGroupFromBWBySn(String serial_number, Pagination page) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);

        IDataset results = Dao.qryByCode("TF_F_USER_BLACKWHITE", "SEL_ALL_BY_SN", param, page);
        IDataset resultSet = new DatasetList();
        for (int i = 0; i < results.size(); i++) {
            IData result = results.getData(i);
            // 查询集团用户
            String userIdA = result.getString("EC_USER_ID");
            IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
            if (IDataUtil.isEmpty(grpUserInfo)) {
                continue;
            }
            String productId = grpUserInfo.getString("PRODUCT_ID", "");
            String brandCode = grpUserInfo.getString("BRAND_CODE", "");
            result.put("PRODUCT_ID", productId);
            result.put("BRAND_CODE", brandCode);
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
            result.put("OPEN_DATE", grpUserInfo.getString("OPEN_DATE"));
            result.put("USER_ID", grpUserInfo.getString("USER_ID"));
            result.put("SERIAL_NUMBER", grpUserInfo.getString("SERIAL_NUMBER"));

            // 查询集团客户资料
            String grpCustId = grpUserInfo.getString("CUST_ID", "");
            if (StringUtils.isBlank(grpCustId)) {
                continue;
            }
            IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
            if (IDataUtil.isEmpty(grpCustInfos)) {
                continue;
            }
            IData grpCustInfo = grpCustInfos;
            result.put("GROUP_ID", grpCustInfo.getString("GROUP_ID"));
            result.put("CUST_ID", grpCustInfo.getString("CUST_ID"));
            result.put("CUST_NAME", grpCustInfo.getString("CUST_NAME"));
            result.put("CLASS_ID", grpCustInfo.getString("CLASS_ID"));
            result.put("JURISTIC_TYPE_CODE", grpCustInfo.getString("JURISTIC_TYPE_CODE"));
            result.put("JURISTIC_NAME", grpCustInfo.getString("JURISTIC_NAME"));
            result.put("ENTERPRISE_TYPE_CODE", grpCustInfo.getString("ENTERPRISE_TYPE_CODE"));
            result.put("CALLING_TYPE_CODE", grpCustInfo.getString("CALLING_TYPE_CODE"));
            result.put("CALLING_TYPE_NAME", grpCustInfo.getString("CALLING_TYPE_NAME"));
            result.put("SUB_CALLING_TYPE_NAME", grpCustInfo.getString("SUB_CALLING_TYPE_NAME"));
            result.put("ENTERPRISE_TYPE_NAME", grpCustInfo.getString("ENTERPRISE_TYPE_NAME"));
            result.put("CLASS_NAME", grpCustInfo.getString("CLASS_NAME"));
            result.put("GROUP_TYPE_NAME", grpCustInfo.getString("GROUP_TYPE_NAME"));
            result.put("JURISTIC_TYPE_NAME", grpCustInfo.getString("JURISTIC_TYPE_NAME"));
            result.put("RSRV_NUM3", grpCustInfo.getString("RSRV_NUM3"));

            resultSet.add(result);
        }
        return resultSet;
    }

    /**
     * 根据成员的USER_ID和地州编码查询集团信息
     * 
     * @param userId
     * @param relationCode
     *            1查询UU 2查询BB 否则全部查
     * @param privForProduct
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getGroupInfo(String userId, String relationCode, String privForProduct, Pagination page) throws Exception {
        IData uuParam = new DataMap();
        IData userAcctDayInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
        String firstTimeNextAcct = userAcctDayInfo.getString("FIRST_DAY_NEXTACCT", "") + SysDateMgr.START_DATE_FOREVER;

        uuParam.put("USER_ID_B", userId);
        uuParam.put("END_DATE", firstTimeNextAcct);
        IDataset idsDataset = new DatasetList();
        if (!relationCode.equals("2")) {
            IDataset uuSet = Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDB_ENDDATE", uuParam, page);
            idsDataset.addAll(uuSet);
        }

        if (!relationCode.equals("1")) {
            IDataset bbSet = Dao.qryByCode("TF_F_RELATION_BB", "SEL_ALL_BY_USERIDB", uuParam, page);
            idsDataset.addAll(bbSet);
        }

        if (IDataUtil.isEmpty(idsDataset))
            return idsDataset;

        IDataset resultSet = new DatasetList();
        for (int i = 0, sz = idsDataset.size(); i < sz; i++) {
            IData result = new DataMap();
            IData uuData = idsDataset.getData(i);

            // 排除调集团彩铃的付费号码
            String roleCodeB = uuData.getString("ROLE_CODE_B", "");
            String relationTypeCode = uuData.getString("RELATION_TYPE_CODE", "");
            if (relationTypeCode.equals("26") && roleCodeB.equals("9")) {
                continue;
            }

            // 查询集团用户
            String userIdA = uuData.getString("USER_ID_A");
            IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
            if (IDataUtil.isEmpty(grpUserInfo)) {
                continue;
            }
            String productId = grpUserInfo.getString("PRODUCT_ID", "");
            String brandCode = grpUserInfo.getString("BRAND_CODE", "");
            String productMode = grpUserInfo.getString("PRODUCT_MODE", "");
            if (!productMode.equals("10")) {// 亲情网的虚拟用户CUSTID是集团的 需要排除调
                continue;
            }

            result.put("PRODUCT_ID", productId);
            result.put("BRAND_CODE", brandCode);
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
            result.put("OPEN_DATE", grpUserInfo.getString("OPEN_DATE"));
            result.put("USER_ID", grpUserInfo.getString("USER_ID"));
            result.put("SERIAL_NUMBER", grpUserInfo.getString("SERIAL_NUMBER"));

            // 查询集团客户资料
            String grpCustId = grpUserInfo.getString("CUST_ID", "");
            if (StringUtils.isBlank(grpCustId)) {
                continue;
            }
            IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
            if (IDataUtil.isEmpty(grpCustInfos)) {
                continue;
            }
            IData grpCustInfo = grpCustInfos;
            result.put("GROUP_ID", grpCustInfo.getString("GROUP_ID"));
            result.put("CUST_ID", grpCustInfo.getString("CUST_ID"));
            result.put("CUST_NAME", grpCustInfo.getString("CUST_NAME"));
            result.put("CLASS_ID", grpCustInfo.getString("CLASS_ID"));
            result.put("JURISTIC_TYPE_CODE", grpCustInfo.getString("JURISTIC_TYPE_CODE"));
            result.put("JURISTIC_NAME", grpCustInfo.getString("JURISTIC_NAME"));
            result.put("ENTERPRISE_TYPE_CODE", grpCustInfo.getString("ENTERPRISE_TYPE_CODE"));
            result.put("CALLING_TYPE_CODE", grpCustInfo.getString("CALLING_TYPE_CODE"));
            result.put("CALLING_TYPE_NAME", grpCustInfo.getString("CALLING_TYPE_NAME"));
            result.put("SUB_CALLING_TYPE_NAME", grpCustInfo.getString("SUB_CALLING_TYPE_NAME"));
            result.put("ENTERPRISE_TYPE_NAME", grpCustInfo.getString("ENTERPRISE_TYPE_NAME"));
            result.put("CLASS_NAME", grpCustInfo.getString("CLASS_NAME"));
            result.put("GROUP_TYPE_NAME", grpCustInfo.getString("GROUP_TYPE_NAME"));
            result.put("JURISTIC_TYPE_NAME", grpCustInfo.getString("JURISTIC_TYPE_NAME"));
            result.put("RSRV_NUM3", grpCustInfo.getString("RSRV_NUM3"));
            // 查集团产品

            if (brandCode.equals("BOSG")) {
                IDataset bbyyProducts = ProductInfoQry.getProductsByTypeForGroup("BBYY", CSBizBean.getTradeEparchyCode(), null);
                boolean ifExist = false;
                if (IDataUtil.isNotEmpty(bbyyProducts)) {
                    for (int k = 0; k < bbyyProducts.size(); k++) {
                        if (productId.equals(bbyyProducts.getData(k).getString("PRODUCT_ID"))) {
                            ifExist = true;
                            break;
                        }
                    }
                }
                if (!ifExist) {
                    continue;
                }

            }

            resultSet.add(result);
        }

        if (StringUtils.isNotBlank(privForProduct) && privForProduct.equals("true")) {
            ProductPrivUtil.filterProductListByPriv(CSBizBean.getVisit().getStaffId(), resultSet);
        }

        return resultSet;

    }

    /**
     * 根据成员的USER_ID和地州编码查询集团信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGroupInfoByRela(IData data, Pagination page) throws Exception {
        IData uuParam = new DataMap();
        String userId = data.getString("USER_ID", "");
        IData userAcctDayInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
        String relationCode = data.getString("RELATION_CODE", "");
        uuParam.put("USER_ID_B", userId);
        uuParam.put("RELATION_TYPE_CODE", relationCode);
        IDataset idsDataset = new DatasetList();
        IDataset uuSet = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_RELATION", uuParam, page);
        idsDataset.addAll(uuSet);

        if (IDataUtil.isEmpty(idsDataset))
            return idsDataset;

        IDataset resultSet = new DatasetList();
        for (int i = 0, sz = idsDataset.size(); i < sz; i++) {
            IData result = new DataMap();
            IData uuData = idsDataset.getData(i);

            // 排除调集团彩铃的付费号码
            String roleCodeB = uuData.getString("ROLE_CODE_B", "");
            String relationTypeCode = uuData.getString("RELATION_TYPE_CODE", "");
            if (relationTypeCode.equals("26") && roleCodeB.equals("9")) {
                continue;
            }

            // 查询集团用户
            String userIdA = uuData.getString("USER_ID_A");
            IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
            if (IDataUtil.isEmpty(grpUserInfo)) {
                continue;
            }
            String productId = grpUserInfo.getString("PRODUCT_ID", "");
            String brandCode = grpUserInfo.getString("BRAND_CODE", "");
            result.put("PRODUCT_ID", productId);
            result.put("BRAND_CODE", brandCode);
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
            result.put("OPEN_DATE", grpUserInfo.getString("OPEN_DATE"));
            result.put("USER_ID", grpUserInfo.getString("USER_ID"));
            result.put("SERIAL_NUMBER", grpUserInfo.getString("SERIAL_NUMBER"));

            // 查询集团客户资料
            String grpCustId = grpUserInfo.getString("CUST_ID", "");
            if (StringUtils.isBlank(grpCustId)) {
                continue;
            }
            IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
            if (IDataUtil.isEmpty(grpCustInfos)) {
                continue;
            }
            IData grpCustInfo = grpCustInfos;
            result.put("GROUP_ID", grpCustInfo.getString("GROUP_ID"));
            result.put("CUST_ID", grpCustInfo.getString("CUST_ID"));
            result.put("CUST_NAME", grpCustInfo.getString("CUST_NAME"));
            result.put("CLASS_ID", grpCustInfo.getString("CLASS_ID"));
            result.put("JURISTIC_TYPE_CODE", grpCustInfo.getString("JURISTIC_TYPE_CODE"));
            result.put("JURISTIC_NAME", grpCustInfo.getString("JURISTIC_NAME"));
            result.put("ENTERPRISE_TYPE_CODE", grpCustInfo.getString("ENTERPRISE_TYPE_CODE"));
            result.put("CALLING_TYPE_CODE", grpCustInfo.getString("CALLING_TYPE_CODE"));
            result.put("CALLING_TYPE_NAME", grpCustInfo.getString("CALLING_TYPE_NAME"));
            result.put("SUB_CALLING_TYPE_NAME", grpCustInfo.getString("SUB_CALLING_TYPE_NAME"));
            result.put("ENTERPRISE_TYPE_NAME", grpCustInfo.getString("ENTERPRISE_TYPE_NAME"));
            result.put("CLASS_NAME", grpCustInfo.getString("CLASS_NAME"));
            result.put("GROUP_TYPE_NAME", grpCustInfo.getString("GROUP_TYPE_NAME"));
            result.put("JURISTIC_TYPE_NAME", grpCustInfo.getString("JURISTIC_TYPE_NAME"));
            result.put("RSRV_NUM3", grpCustInfo.getString("RSRV_NUM3"));

            resultSet.add(result);
        }

        return resultSet;

    }

    /**
     * 根据成员的USER_ID和地州编码查询铁通集团信息
     * 
     * @author tengg
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGroupInfoTtrh(IData data, Pagination pagination) throws Exception {

        String eparchyCode = data.getString("EPARCHY_CODE");
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GROUPINFOTT", data, pagination, eparchyCode);
    }

    /**
     * 根据group_Id查询集团信息化产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGroupProductInfoByGID(IData data) throws Exception {

        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GROUP_ID", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公托收号码查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGrpAcctUserInfo(IData data, Pagination pg) throws Exception {

        // 集团编码
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_3", data, pg, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公托收号码查询总记录数
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGrpAcctUserInfoTotalNum(IData data) throws Exception {

        // 集团编码
        return Dao.qryByCode("TF_F_CUST_GROUP", "CNT_BY_GRPID_3", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编号查询客户资料
     * 
     * @author lim
     * @param params
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param pagination
     * @return IDataset 客户资料列表
     * @throws Exception
     */
    public static IDataset getGrpInfoByGrpIdTtrh(IData idata, Pagination page) throws Exception {

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GIDTT", idata, page, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团用户信息查询集团客户信息
     * 
     * @author liaoyi
     * @param idata
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getGrpInfoByUserId(IData idata) throws Exception {

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GROUP_FOR_MEM", idata, Route.CONN_CRM_CG);
    }

    /**
     * 根据group_Id查询集团信息化产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpProductinfoByProductId(String groupId, String productId) throws Exception {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("GROUP_ID", groupId);
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_PROINFO_BY_ID", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据GROUP_ID和产品ID查询M2M集团用户信息
     * 
     * @param data
     * @return
     * @throws Exception
     * @author lixin
     */
    public static IDataset getM2MGrpMemberInfo(IData param, Pagination page) throws Exception {

        String eparchy_code = CSBizBean.getVisit().getStaffEparchyCode();

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT DISTINCT g.group_id,g.cust_name,u.product_id,uu.serial_number_b,a.ATTR_VALUE||'--'||i.attr_field_name apn,b.attr_value ip, ");
        parser.addSQL(" to_char(uu.start_date,'yyyy-mm-dd') start_date,to_char(uu.End_Date,'yyyy-mm-dd') end_date ");
        parser.addSQL(" FROM tf_f_cust_group g,tf_f_user u,tf_f_relation_uu uu,td_b_ptype_product p,TF_F_USER_ATTR a,TF_F_USER_ATTR b ,td_b_attr_itemb i");
        parser.addSQL(" WHERE g.cust_id=u.cust_id  AND u.user_id= uu.user_id_a  AND u.product_id=p.product_id AND p.product_type_code='M2MG'");
        parser.addSQL(" AND a.ATTR_CODE ='apn' AND a.INST_TYPE= 'S' AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE ");
        parser.addSQL(" AND b.ATTR_CODE ='ip' AND b.INST_TYPE= 'S' AND SYSDATE BETWEEN b.START_DATE AND b.END_DATE ");
        parser.addSQL(" AND SYSDATE BETWEEN uu.START_DATE AND uu.END_DATE AND u.remove_tag='0' ");
        parser.addSQL(" AND uu.user_id_b=a.user_id AND uu.user_id_b=b.user_id(+) AND i.ID IN (83,110) AND a.attr_value=i.attr_field_code ");
        parser.addSQL(" AND u.product_id=:PRODUCT_ID ");
        parser.addSQL(" AND g.group_id=:GROUP_ID ");
        return Dao.qryByParse(parser, page, eparchy_code);
    }

    /**
     * 查询网外号码组信息
     * 
     * @param param
     * @param pd
     * @throws Exception
     */
    public static IDataset getOutGrpInfos(IData param) throws Exception {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("   SELECT   t.user_id OUT_GROUP_ID,t.RSRV_VALUE_CODE,t.RSRV_VALUE OUT_GROUP_NAME,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE  FROM tf_f_user_other t");

        parser.addSQL("   WHERE 1=1");
        parser.addSQL("   AND t.RSRV_VALUE_CODE ='oGrp'");
        parser.addSQL("   AND t.START_DATE<=SYSDATE");
        parser.addSQL("   AND t.END_DATE>SYSDATE");
        parser.addSQL("   AND t.RSRV_VALUE=:Group_name");
        parser.addSQL("   AND t.user_id=to_number(:Group_id)");
        parser.addSQL("   AND t.PARTITION_ID=mod(to_number(:Group_id),10000)");
        parser.addSQL("   ORDER BY user_id DESC");

        return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
    }

    /**
     * 根据关联用户的USER_ID和地州编码查询集团信息
     * 
     * @author liaoyi
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getRelaBbGroupInfo(IData data, Pagination pagination) throws Exception {

        String eparchyCode = data.getString("EPARCHY_CODE");
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_RELABB_GROUPINFO", data, pagination, eparchyCode);
    }

    /**
     * 根据关联用户的USER_ID和地州编码查询集团信息
     * 
     * @author liaoyi
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getRelaGroupInfo(IData data, Pagination pagination) throws Exception {

        String eparchyCode = data.getString("EPARCHY_CODE");
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_RELA_GROUPINFO", data, pagination, eparchyCode);
    }

    /* VPMN 集团 客户信息 */
    public static IDataset qryCustInfo(IData param) throws Exception {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT p.cust_name cust_name,p.cust_class_type cust_class_type,p.enterprise_size_code enterprise_size_code,p.class_id class_id,to_char(u.user_id) user_id, ");
        parser.addSQL("        p.enterprise_type_code enterprise_type_code,p.calling_type_code calling_type_code,p.unify_pay_code unify_pay_code,");
        parser.addSQL("        p.juristic_type_code juristic_type_code,p.juristic_name juristic_name,b.brand brand,d.product_name product_name, ");
        parser.addSQL("        p.group_id group_id,d.product_id product_id ");
        parser.addSQL(" FROM   tf_f_user u, tf_f_cust_group p, td_s_brand b, td_b_product d  ");
        parser.addSQL(" WHERE  u.cust_id = p.cust_id ");
        parser.addSQL("        AND u.brand_code = b.brand_code ");
        parser.addSQL("        AND u.product_id = d.product_id ");
        parser.addSQL("        AND u.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("        AND u.remove_tag = '0'");
        IDataset resIds = Dao.qryByParse(parser);
        return resIds;
    }

    /**
     * 根据集团编码查询集团账户和付费信息
     * 
     * @param groupId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupAcctUserPayInfo(String groupId, Pagination pg) throws Exception {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_ACCT_BY_GROUPID", param, pg, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编码查询集团订购产品信息
     * 
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupProductInfoByGroupId(IData inParam, Pagination pg) throws Exception {
        String groupId = inParam.getString("GROUP_ID", "");

        IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(custData)) {
            return null;
        }

        String custId = custData.getString("CUST_ID", "");

        IDataset userList = UserInfoQry.qryUserAndProductByCustIdForGrp(custId, pg);

        if (IDataUtil.isEmpty(userList)) {
            return null;
        }

        String custName = custData.getString("CUST_NAME");

        for (int i = 0, row = userList.size(); i < row; i++) {
            IData userData = userList.getData(i);

            userData.put("CUST_ID", custId);
            userData.put("CUST_NAME", custName);
            userData.put("GROUP_ID", groupId);

            String areaName = UAreaInfoQry.getAreaNameByAreaCode(userData.getString("CITY_CODE"));

            if (StringUtils.isBlank(areaName))
                areaName = "其他";

            userData.put("AREA_NAME", areaName);
            userData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userData.getString("PRODUCT_ID")));
        }

        return userList;
    }

    /**
     * 根据集团用户服务号码查询集团订购产品信息
     * 
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupProductInfoBySn(IData inParam, Pagination pg) throws Exception {
        String serialNumber = inParam.getString("SERIAL_NUMBER", "");

        IData userData = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);

        if (IDataUtil.isEmpty(userData)) {
            return null;
        }

        userData.put("AREA_NAME", userData.getString("CITY_NAME"));

        IData custData = UcaInfoQry.qryGrpInfoByCustId(userData.getString("CUST_ID"));
        if (IDataUtil.isNotEmpty(custData)) {
            userData.put("GROUP_ID", custData.getString("GROUP_ID"));
            userData.put("CUST_ID", custData.getString("CUST_ID"));
            userData.put("CUST_NAME", custData.getString("CUST_NAME"));
        }

        return IDataUtil.idToIds(userData);
    }

    public static IDataset qryGrpBusyByApplyId(String applyId, Pagination pg) throws Exception {
        IData param = new DataMap();
        param.put("APPLY_ID", applyId);

        return Dao.qryByCode("TF_B_GRP_BUSAPPLY", "SEL_BY_PK", param, pg);
    }

    public static IDataset qryGrpInfoByGroupIdAndRemoveTag(String groupId, String removeTag) throws Exception {
        IData param = new DataMap();

        param.put("GROUP_ID", groupId);
        param.put("REMOVE_TAG", removeTag);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GROUPID_REMOVE_TAG", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团名称获取客户资料
     * 
     * @author xiajj
     * @param params
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param pagination
     * @return IDataset 客户资料列表
     * @throws Exception
     */
    public static IDataset qryGrpInfoByGrpName(String custname, Pagination pagination) throws Exception {
        return qryTTGrpInfoByGrpName(custname, false, pagination);
    }

    /**
     * 根据集团证件号码获取客户资料
     * 
     * @author xiajj
     * @param params
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param pagination
     * @return IDataset 客户资料列表
     * @throws Exception
     */
    public static IDataset qryGrpInfoByGrpPspt(IData idata, Pagination pagination) throws Exception {
        return qryTTGrpInfoByGrpPspt(idata, false, pagination);
    }

    /**
     * 根据集团编码和产品编码查询集团成员信息
     * 
     * @param groupId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebByGroupProductId(String groupId, String productId) throws Exception {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCodeAllCrm("TF_F_CUST_GROUP", "SEL_BY_GRPID_PRODID", param, true);
    }

    public static IDataset qrySerNumAByGrpId(IData dt) throws Exception {

        SQLParser parser = new SQLParser(dt);
        parser.addSQL(" SELECT u.serial_number ");
        parser.addSQL(" FROM   tf_f_user u ,tf_f_cust_group p ");
        parser.addSQL(" WHERE  u.cust_id=p.cust_id ");
        parser.addSQL("        AND p.group_id = :GROUP_ID AND u.product_id= :PRODUCT_ID ");
        parser.addSQL("        AND u.remove_tag='0' ");
        IDataset resIds = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return resIds;
    }

    /**
     * 支持查询铁通集团信息
     * 
     * @param custname
     * @param ifTTGrp
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryTTGrpInfoByGrpName(String custname, boolean ifTTGrp, Pagination pagination) throws Exception {
        IData data = new DataMap();
        data.put("CUST_NAME", custname);

        StringBuilder sql = new StringBuilder(4000);

        sql.append("SELECT TO_CHAR(CUST_ID) CUST_ID, GROUP_ID, CUST_NAME, GROUP_TYPE, GROUP_ROLE, ");
        sql.append("CLASS_ID, CLASS_ID2, LAST_CLASS_ID, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("CUST_CLASS_TYPE, GROUP_ATTR, GROUP_STATUS, GROUP_ADDR, GROUP_SOURCE, ");
        sql.append("PROVINCE_CODE, EPARCHY_CODE, CITY_CODE, CITY_CODE_U, SUPER_GROUP_ID, ");
        sql.append("SUPER_GROUP_NAME, PNATIONAL_GROUP_ID, PNATIONAL_GROUP_NAME, ");
        sql.append("MP_GROUP_CUST_CODE, UNIFY_PAY_CODE, ORG_STRUCT_CODE, CUST_MANAGER_ID, ");
        sql.append("CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("ASSIGN_STAFF_ID, CALLING_TYPE_CODE, SUB_CALLING_TYPE_CODE, ");
        sql.append("CALLING_AREA_CODE, CALL_TYPE, ACCEPT_CHANNEL, AGREEMENT, BUSI_TYPE, ");
        sql.append("GROUP_CONTACT_PHONE, ENTERPRISE_TYPE_CODE, ENTERPRISE_SIZE_CODE, ");
        sql.append("ENTERPRISE_SCOPE, JURISTIC_TYPE_CODE, ");
        sql.append("TO_CHAR(JURISTIC_CUST_ID) JURISTIC_CUST_ID, JURISTIC_NAME, ");
        sql.append("BUSI_LICENCE_TYPE, BUSI_LICENCE_NO, ");
        sql.append("TO_CHAR(BUSI_LICENCE_VALID_DATE, 'yyyy-mm-dd hh24:mi:ss') BUSI_LICENCE_VALID_DATE, ");
        sql.append("GROUP_MEMO, BANK_ACCT, BANK_NAME, TO_CHAR(REG_MONEY) REG_MONEY, ");
        sql.append("TO_CHAR(REG_DATE, 'yyyy-mm-dd hh24:mi:ss') REG_DATE, CUST_AIM, SCOPE, ");
        sql.append("MAIN_BUSI, MAIN_TRADE, EMP_LSAVE, ");
        sql.append("TO_CHAR(LATENCY_FEE_SUM) LATENCY_FEE_SUM, TO_CHAR(YEAR_GAIN) YEAR_GAIN, ");
        sql.append("TO_CHAR(TURNOVER) TURNOVER, CONSUME, TO_CHAR(COMM_BUDGET) COMM_BUDGET, ");
        sql.append("TO_CHAR(GTEL_BUDGET) GTEL_BUDGET, TO_CHAR(LTEL_BUDGET) LTEL_BUDGET, ");
        sql.append("GROUP_ADVERSARY, VPMN_GROUP_ID, TO_CHAR(VPMN_NUM) VPMN_NUM, ");
        sql.append("TO_CHAR(USER_NUM) USER_NUM, TO_CHAR(EMP_NUM_LOCAL) EMP_NUM_LOCAL, ");
        sql.append("TO_CHAR(EMP_NUM_CHINA) EMP_NUM_CHINA, TO_CHAR(EMP_NUM_ALL) EMP_NUM_ALL, ");
        sql.append("TO_CHAR(TELECOM_NUM_GH) TELECOM_NUM_GH, ");
        sql.append("TO_CHAR(TELECOM_NUM_XLT) TELECOM_NUM_XLT, ");
        sql.append("TO_CHAR(MOBILE_NUM_CHINAGO) MOBILE_NUM_CHINAGO, ");
        sql.append("TO_CHAR(MOBILE_NUM_GLOBAL) MOBILE_NUM_GLOBAL, ");
        sql.append("TO_CHAR(MOBILE_NUM_MZONE) MOBILE_NUM_MZONE, ");
        sql.append("TO_CHAR(MOBILE_NUM_LOCAL) MOBILE_NUM_LOCAL, ");
        sql.append("TO_CHAR(UNICOM_NUM_G) UNICOM_NUM_G, TO_CHAR(UNICOM_NUM_C) UNICOM_NUM_C, ");
        sql.append("TO_CHAR(UNICOM_NUM_GC) UNICOM_NUM_GC, ");
        sql.append("TO_CHAR(PRODUCT_NUM_LOCAL) PRODUCT_NUM_LOCAL, ");
        sql.append("TO_CHAR(PRODUCT_NUM_OTHER) PRODUCT_NUM_OTHER, ");
        sql.append("TO_CHAR(PRODUCT_NUM_USE) PRODUCT_NUM_USE, ");
        sql.append("TO_CHAR(EMPLOYEE_ARPU) EMPLOYEE_ARPU, ");
        sql.append("TO_CHAR(NETRENT_PAYOUT) NETRENT_PAYOUT, ");
        sql.append("TO_CHAR(MOBILE_PAYOUT) MOBILE_PAYOUT, ");
        sql.append("TO_CHAR(UNICOM_PAYOUT) UNICOM_PAYOUT, ");
        sql.append("TO_CHAR(TELECOM_PAYOUT_XLT) TELECOM_PAYOUT_XLT, GROUP_PAY_MODE, ");
        sql.append("PAYFOR_WAY_CODE, WRITEFEE_COUNT, TO_CHAR(WRITEFEE_SUM) WRITEFEE_SUM, ");
        sql.append("TO_CHAR(USER_NUM_FULLFREE) USER_NUM_FULLFREE, ");
        sql.append("TO_CHAR(USER_NUM_WRITEOFF) USER_NUM_WRITEOFF, ");
        sql.append("TO_CHAR(BOSS_FEE_SUM) BOSS_FEE_SUM, DOYEN_STAFF_ID, NEWTRADE_COMMENT, ");
        sql.append("LIKE_MOBILE_TRADE, LIKE_DISCNT_MODE, ");
        sql.append("TO_CHAR(FINANCE_EARNING) FINANCE_EARNING, EARNING_ORDER, ");
        sql.append("CALLING_POLICY_FORCE, SUBCLASS_ID, WEBSITE, FAX_NBR, EMAIL, POST_CODE, ");
        sql.append("TO_CHAR(GROUP_VALID_SCORE) GROUP_VALID_SCORE, ");
        sql.append("TO_CHAR(GROUP_SUM_SCORE) GROUP_SUM_SCORE, GROUP_MGR_SN, ");
        sql.append("TO_CHAR(GROUP_MGR_USER_ID) GROUP_MGR_USER_ID, GROUP_MGR_CUST_NAME, ");
        sql.append("BASE_ACCESS_NO, BASE_ACCESS_NO_KIND, CUST_SERV_NBR, EC_CODE, IF_SHORT_PIN, ");
        sql.append("AUDIT_STATE, TO_CHAR(AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE, ");
        sql.append("AUDIT_STAFF_ID, AUDIT_NOTE, REMOVE_FLAG, REMOVE_METHOD, ");
        sql.append("REMOVE_REASON_CODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_CHANGE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("RSRV_STR6, RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_CUST_GROUP ");
        sql.append("WHERE CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
        sql.append("AND REMOVE_TAG = '0' ");

        if (ifTTGrp)
            sql.append("AND RSRV_NUM3 = 1 ");

        IDataset ids = Dao.qryBySql(sql, data, pagination, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(ids)) {
            return ids;
        }

        for (int i = 0, size = ids.size(); i < size; i++) {
            IData map = ids.getData(i);

            map.put("GROUP_TYPE_NAME", StaticUtil.getStaticValue("CUSTGROUP_GROUPTYPE", map.getString("GROUP_TYPE")));
            map.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", map.getString("CLASS_ID")));
            map.put("ENTERPRISE_TYPE_NAME", StaticUtil.getStaticValue("TD_S_ENTERPRISETYPE", map.getString("ENTERPRISE_TYPE_CODE")));
            map.put("CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGTYPE", map.getString("CALLING_TYPE_CODE")));
            map.put("SUB_CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGSUBTYPE", map.getString("SUB_CALLING_TYPE_CODE")));
        }

        return ids;
    }

    /**
     * 根据集团名称获取铁通客户资料
     * 
     * @author xiajj
     * @param params
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param pagination
     * @return IDataset 客户资料列表
     * @throws Exception
     */
    public static IDataset qryTTGrpInfoByGrpName(String custname, Pagination pagination) throws Exception {
        return qryTTGrpInfoByGrpName(custname, true, pagination);
    }

    /**
     * 支持根据证件号码查询铁通的集团信息
     * 
     * @param idata
     * @param isTTGrp
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryTTGrpInfoByGrpPspt(IData idata, boolean isTTGrp, Pagination pagination) throws Exception {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(CG.CUST_ID) CUST_ID, CG.GROUP_ID, CG.CUST_NAME, CG.GROUP_TYPE, ");
        sql.append("CG.GROUP_ROLE, CG.CLASS_ID, CG.CLASS_ID2, CG.LAST_CLASS_ID, ");
        sql.append("TO_CHAR(CG.CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("CG.CUST_CLASS_TYPE, CG.GROUP_ATTR, CG.GROUP_STATUS, CG.GROUP_ADDR, ");
        sql.append("CG.GROUP_SOURCE, CG.PROVINCE_CODE, CG.EPARCHY_CODE, CG.CITY_CODE, ");
        sql.append("CG.CITY_CODE_U, CG.SUPER_GROUP_ID, CG.SUPER_GROUP_NAME, ");
        sql.append("CG.PNATIONAL_GROUP_ID, CG.PNATIONAL_GROUP_NAME, CG.MP_GROUP_CUST_CODE, ");
        sql.append("CG.UNIFY_PAY_CODE, CG.ORG_STRUCT_CODE, CG.CUST_MANAGER_ID, ");
        sql.append("CG.CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(CG.ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("CG.ASSIGN_STAFF_ID, CG.CALLING_TYPE_CODE, CG.SUB_CALLING_TYPE_CODE, ");
        sql.append("CG.CALLING_AREA_CODE, CG.CALL_TYPE, CG.ACCEPT_CHANNEL, CG.AGREEMENT, ");
        sql.append("CG.BUSI_TYPE, CG.GROUP_CONTACT_PHONE, CG.ENTERPRISE_TYPE_CODE, ");
        sql.append("CG.ENTERPRISE_SIZE_CODE, CG.ENTERPRISE_SCOPE, CG.JURISTIC_TYPE_CODE, ");
        sql.append("TO_CHAR(CG.JURISTIC_CUST_ID) JURISTIC_CUST_ID, CG.JURISTIC_NAME, ");
        sql.append("CG.BUSI_LICENCE_TYPE, CG.BUSI_LICENCE_NO, ");
        sql.append("TO_CHAR(CG.BUSI_LICENCE_VALID_DATE, 'yyyy-mm-dd hh24:mi:ss') BUSI_LICENCE_VALID_DATE, ");
        sql.append("CG.GROUP_MEMO, CG.BANK_ACCT, CG.BANK_NAME, ");
        sql.append("TO_CHAR(CG.REG_MONEY) REG_MONEY, ");
        sql.append("TO_CHAR(CG.REG_DATE, 'yyyy-mm-dd hh24:mi:ss') REG_DATE, CG.CUST_AIM, ");
        sql.append("CG.SCOPE, CG.MAIN_BUSI, CG.MAIN_TRADE, CG.EMP_LSAVE, ");
        sql.append("TO_CHAR(CG.LATENCY_FEE_SUM) LATENCY_FEE_SUM, ");
        sql.append("TO_CHAR(CG.YEAR_GAIN) YEAR_GAIN, TO_CHAR(CG.TURNOVER) TURNOVER, ");
        sql.append("CG.CONSUME, TO_CHAR(CG.COMM_BUDGET) COMM_BUDGET, ");
        sql.append("TO_CHAR(CG.GTEL_BUDGET) GTEL_BUDGET, TO_CHAR(CG.LTEL_BUDGET) LTEL_BUDGET, ");
        sql.append("CG.GROUP_ADVERSARY, CG.VPMN_GROUP_ID, TO_CHAR(CG.VPMN_NUM) VPMN_NUM, ");
        sql.append("TO_CHAR(CG.USER_NUM) USER_NUM, TO_CHAR(CG.EMP_NUM_LOCAL) EMP_NUM_LOCAL, ");
        sql.append("TO_CHAR(CG.EMP_NUM_CHINA) EMP_NUM_CHINA, ");
        sql.append("TO_CHAR(CG.EMP_NUM_ALL) EMP_NUM_ALL, ");
        sql.append("TO_CHAR(CG.TELECOM_NUM_GH) TELECOM_NUM_GH, ");
        sql.append("TO_CHAR(CG.TELECOM_NUM_XLT) TELECOM_NUM_XLT, ");
        sql.append("TO_CHAR(CG.MOBILE_NUM_CHINAGO) MOBILE_NUM_CHINAGO, ");
        sql.append("TO_CHAR(CG.MOBILE_NUM_GLOBAL) MOBILE_NUM_GLOBAL, ");
        sql.append("TO_CHAR(CG.MOBILE_NUM_MZONE) MOBILE_NUM_MZONE, ");
        sql.append("TO_CHAR(CG.MOBILE_NUM_LOCAL) MOBILE_NUM_LOCAL, ");
        sql.append("TO_CHAR(CG.UNICOM_NUM_G) UNICOM_NUM_G, ");
        sql.append("TO_CHAR(CG.UNICOM_NUM_C) UNICOM_NUM_C, ");
        sql.append("TO_CHAR(CG.UNICOM_NUM_GC) UNICOM_NUM_GC, ");
        sql.append("TO_CHAR(CG.PRODUCT_NUM_LOCAL) PRODUCT_NUM_LOCAL, ");
        sql.append("TO_CHAR(CG.PRODUCT_NUM_OTHER) PRODUCT_NUM_OTHER, ");
        sql.append("TO_CHAR(CG.PRODUCT_NUM_USE) PRODUCT_NUM_USE, ");
        sql.append("TO_CHAR(CG.EMPLOYEE_ARPU) EMPLOYEE_ARPU, ");
        sql.append("TO_CHAR(CG.NETRENT_PAYOUT) NETRENT_PAYOUT, ");
        sql.append("TO_CHAR(CG.MOBILE_PAYOUT) MOBILE_PAYOUT, ");
        sql.append("TO_CHAR(CG.UNICOM_PAYOUT) UNICOM_PAYOUT, ");
        sql.append("TO_CHAR(CG.TELECOM_PAYOUT_XLT) TELECOM_PAYOUT_XLT, CG.GROUP_PAY_MODE, ");
        sql.append("CG.PAYFOR_WAY_CODE, CG.WRITEFEE_COUNT, ");
        sql.append("TO_CHAR(CG.WRITEFEE_SUM) WRITEFEE_SUM, ");
        sql.append("TO_CHAR(CG.USER_NUM_FULLFREE) USER_NUM_FULLFREE, ");
        sql.append("TO_CHAR(CG.USER_NUM_WRITEOFF) USER_NUM_WRITEOFF, ");
        sql.append("TO_CHAR(CG.BOSS_FEE_SUM) BOSS_FEE_SUM, CG.DOYEN_STAFF_ID, ");
        sql.append("CG.NEWTRADE_COMMENT, CG.LIKE_MOBILE_TRADE, CG.LIKE_DISCNT_MODE, ");
        sql.append("TO_CHAR(CG.FINANCE_EARNING) FINANCE_EARNING, CG.EARNING_ORDER, ");
        sql.append("CG.CALLING_POLICY_FORCE, CG.SUBCLASS_ID, CG.WEBSITE, CG.FAX_NBR, CG.EMAIL, ");
        sql.append("CG.POST_CODE, TO_CHAR(CG.GROUP_VALID_SCORE) GROUP_VALID_SCORE, ");
        sql.append("TO_CHAR(CG.GROUP_SUM_SCORE) GROUP_SUM_SCORE, CG.GROUP_MGR_SN, ");
        sql.append("TO_CHAR(CG.GROUP_MGR_USER_ID) GROUP_MGR_USER_ID, CG.GROUP_MGR_CUST_NAME, ");
        sql.append("CG.BASE_ACCESS_NO, CG.BASE_ACCESS_NO_KIND, CG.CUST_SERV_NBR, CG.EC_CODE, ");
        sql.append("CG.IF_SHORT_PIN, CG.AUDIT_STATE ");
        sql.append("FROM TF_F_CUST_GROUP CG, TF_F_CUSTOMER CC ");
        sql.append("WHERE CC.CUST_ID = CG.CUST_ID ");
        sql.append("AND CC.PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        sql.append("AND CC.PSPT_ID = :PSPT_ID ");
        sql.append("AND CC.CUST_TYPE = :CUST_TYPE ");

        if (isTTGrp) {
            sql.append("AND CG.RSRV_NUM3 = 1 ");
        }

        return Dao.qryBySql(sql, idata, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团证件号码获取铁通集团客户资料
     * 
     * @param idata
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryTTGrpInfoByGrpPspt(IData idata, Pagination pagination) throws Exception {
        return qryTTGrpInfoByGrpPspt(idata, true, pagination);
    }

    /**
     * CHENYI 2014-6-10 集团产品信息查询
     * 
     * @param groupid
     * @return
     * @throws Exception
     */
    public static IDataset qryUserPrdByGrpId(String groupid) throws Exception {

        IData param = new DataMap();
        param.put("GROUP_ID", groupid);
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPINFO_BY_GRPID", param);
    }

    /**
     * 根据编号查询挂账集团信息
     * 
     * @author liaoyi
     * @param params
     *            查询所需参数
     * @param pagination
     * @return IDataset 客户标志资料列表
     * @throws Exception
     */
    public static IDataset queryAccountUniteGrp(IData data) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT C.CUST_NAME,C.CUST_ID,C.EPARCHY_CODE,C.CITY_CODE,U.USER_ID,U.PRODUCT_ID,U.BRAND_CODE,U.SERIAL_NUMBER,U.RSRV_STR1 URSRV_STR1,U.RSRV_STR2 URSRV_STR2,U.RSRV_STR3 URSRV_STR3,O.RSRV_STR1,O.RSRV_STR2,O.RSRV_STR3 FROM TF_F_CUSTOMER C, TF_F_USER U, TF_F_USER_OTHER O ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND C.CUST_ID = U.CUST_ID");
        parser.addSQL(" AND U.USER_ID = O.USER_ID(+)");
        parser.addSQL(" AND U.REMOVE_TAG = 0");
        parser.addSQL(" AND U.PRODUCT_ID = 7000");
        parser.addSQL(" AND (O.RSRV_VALUE_CODE(+) = 'GrpU')");
        parser.addSQL(" AND U.USER_ID = :USER_ID");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据编号查询挂账集团信息
     * 
     * @author liaoyi
     * @param params
     *            查询所需参数
     * @param pagination
     * @return IDataset 客户标志资料列表
     * @throws Exception
     */
    public static IDataset queryAccountUniteGrps(IData data, Pagination pagination) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT C.CUST_NAME,C.CUST_ID,C.EPARCHY_CODE,C.CITY_CODE,U.USER_ID,U.PRODUCT_ID,U.BRAND_CODE,U.SERIAL_NUMBER,U.RSRV_STR1 URSRV_STR1,U.RSRV_STR2 URSRV_STR2,U.RSRV_STR3 URSRV_STR3,O.RSRV_STR1,O.RSRV_STR2,O.RSRV_STR3 FROM TF_F_CUSTOMER C, TF_F_USER U, TF_F_USER_OTHER O ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND C.CUST_ID = U.CUST_ID");
        parser.addSQL(" AND U.USER_ID = O.USER_ID(+)");
        parser.addSQL(" AND U.REMOVE_TAG = 0");
        parser.addSQL(" AND U.PRODUCT_ID = 7000");
        parser.addSQL(" AND (O.RSRV_VALUE_CODE(+) = 'GrpU')");
        parser.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND C.CUST_NAME LIKE '%' || :CUST_NAME || '%'");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Description: 根据EC_CODE 查询出TF_F_CUST_GROUP表的集团信息，走cg库
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryCustGroupInfoByMpCustCode(String mp_group_cust_code, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("MP_GROUP_CUST_CODE", mp_group_cust_code);

        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT ");
        sql.append("* ");
        sql.append("FROM TF_F_CUST_GROUP t ");
        sql.append("where t.MP_GROUP_CUST_CODE =:MP_GROUP_CUST_CODE AND t.REMOVE_TAG='0' ");

        return Dao.qryBySql(sql, param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 获取号码对应的集团信息
     * 
     * @author fengsl
     * @date 2013-02-27
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryCustGroupInfosBySerialNum(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.GROUP_ID GROUP_ID, A.CUST_NAME GROUP_CUST_NAME, A.CUST_MANAGER_ID, B.USER_ID ");
        parser.addSQL("     FROM TF_F_CUST_GROUP A, TF_F_USER B, TF_F_USER_PRODUCT T ");
        parser.addSQL(" WHERE A.REMOVE_TAG = 0 ");
        parser.addSQL("     AND A.GROUP_CONTACT_PHONE = :SERIAL_NUMBER ");
        parser.addSQL("     AND A.CUST_ID = B.CUST_ID AND B.USER_ID = T.USER_ID(+) AND B.PARTITION_ID = T.PARTITION_ID(+) ");
        parser.addSQL("     AND T.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL("     AND B.REMOVE_TAG = '0' AND T.MAIN_TAG = '1' AND T.END_DATE > SYSDATE ");

        IDataset ids = Dao.qryByParse(parser, Route.CONN_CRM_CG);

        for (int i = 0, size = ids.size(); i < size; i++) {
            IData map = ids.getData(i);

            map.put("CUST_MANAGER_PHONE", UStaffInfoQry.getStaffSnByStaffId(map.getString("CUST_MANAGER_ID")));
            map.put("CUST_MANAGER_NAME", UStaffInfoQry.getStaffNameByStaffId(map.getString("CUST_MANAGER_ID")));
        }

        return ids;
    }

    public static IDataset queryCustGroupInfosBySerialNum7121(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.group_id GROUP_ID,a.cust_name GROUP_CUST_NAME,a.cust_manager_id,b.user_id from  tf_f_cust_group a,       tf_f_user b where a.remove_tag=0   and a.GROUP_CONTACT_PHONE=:SERIAL_NUMBER  and a.cust_id=b.cust_id   and b.product_id='7121'   and b.remove_tag=0 and rownum <2 ");
        return Dao.qryByParse(parser);
    }

    /**
     * 获取号码对应的集团信息
     * 
     * @author fengsl
     * @date 2013-02-26
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryCustGroupInfosByUserId(IData param) throws Exception {
        SQLParser parsergrp = new SQLParser(param);
        parsergrp
                .addSQL("SELECT cust.GROUP_ID,cust.CUST_ID,cust.CUST_NAME,cust.CLASS_ID,cust.JURISTIC_TYPE_CODE,cust.JURISTIC_NAME,cust.ENTERPRISE_TYPE_CODE,cust.CALLING_TYPE_CODE,u.PRODUCT_ID,u.BRAND_CODE,u.SERIAL_NUMBER,u.USER_ID,to_char(u.OPEN_DATE,'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, u.PRODUCT_ID");
        parsergrp.addSQL("FROM TF_F_CUST_GROUP cust, TF_F_USER u, TF_F_RELATION_UU uu ");
        parsergrp.addSQL("WHERE uu.USER_ID_B = :USER_ID_B ");
        parsergrp.addSQL("AND uu.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        parsergrp.addSQL("And (UU.RELATION_TYPE_CODE='86' Or UU.RELATION_TYPE_CODE='89') ");
        parsergrp.addSQL("AND cust.CUST_ID = u.CUST_ID ");
        parsergrp.addSQL("AND uu.END_DATE > last_day(trunc(SYSDATE)) + 1 - 1 / 24 / 3600 ");
        parsergrp.addSQL("AND u.USER_ID = uu.USER_ID_A ");
        parsergrp.addSQL("AND u.PARTITION_ID = MOD(uu.USER_ID_A, 10000) ");
        parsergrp.addSQL("AND u.REMOVE_TAG = '0' ");
        parsergrp.addSQL("AND (null is null or u.SERIAL_NUMBER = null) ");
        parsergrp.addSQL("ORDER BY u.CUST_ID, u.USER_ID ");
        return Dao.qryByParse(parsergrp);
    }

    public static IDataset queryGroupCustInfo(IData data, Pagination pagination) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select a.cust_id, a.group_id,a.cust_name,b.user_id,b.serial_number,b.product_id,b.open_date ");
        parser.addSQL(" from tf_f_cust_group a,tf_f_user b");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and a.cust_id = b.cust_id");
        parser.addSQL(" and b.remove_tag = '0'");
        parser.addSQL(" and a.remove_tag = '0'");
        parser.addSQL(" and a.group_id = :GROUP_ID");
        parser.addSQL(" and b.serial_number = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    public static IDataset queryGroupCustInfo_SerialB(IData data, Pagination pagination) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select g.cust_id, u.user_id,u.serial_number,g.group_id,g.cust_name,u.product_id,u.open_date,u.eparchy_code");
        parser.addSQL(" from tf_f_cust_group g, tf_f_user u, tf_f_relation_uu uu where 1 = 1");
        parser.addSQL(" and g.cust_id = u.cust_id");
        parser.addSQL(" and u.user_id = uu.user_id_a");
        parser.addSQL(" and u.partition_id = mod(uu.user_id_a,10000)");
        parser.addSQL(" and g.remove_tag = '0'");
        parser.addSQL(" and u.remove_tag = '0'");
        parser.addSQL(" and uu.serial_number_b = :SERIAL_NUMBER");
        parser.addSQL(" and sysdate between uu.start_date and uu.end_date");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 重载以上方法
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupCustInfo1(IData data, Pagination pagination) throws Exception {
        boolean flag = data.getBoolean("FLAG");

        return queryGroupCustInfo1(data, pagination, flag);
    }

    public static IDataset queryGroupCustInfo1(IData data, Pagination pagination, boolean flag) throws Exception {
        String queryType = data.getString("QueryType", "");

        if ("0".equals(queryType))// 根据集团客户编码查询
        {
            String groupId = data.getString("GROUP_ID");

            IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

            if (IDataUtil.isEmpty(custData)) {
                return null;
            }

            String custId = custData.getString("CUST_ID", "");

            IDataset userList = UserInfoQry.getUserInfoByCustID(custId, Route.CONN_CRM_CG);

            if (IDataUtil.isEmpty(userList)) {
                return null;
            }

            String custName = custData.getString("CUST_NAME");

            for (int i = 0, row = userList.size(); i < row; i++) {
                IData userData = userList.getData(i);

                userData.put("CUST_ID", custId);
                userData.put("CUST_NAME", custName);
                userData.put("GROUP_ID", groupId);
                userData.put("AREA_NAME", UAreaInfoQry.getAreaNameByAreaCode(userData.getString("AREA_CODE")));

                IData userProductData = UcaInfoQry.qryMainProdInfoByUserIdForGrp(userData.getString("USER_ID"));

                if (IDataUtil.isNotEmpty(userProductData)) {
                    userData.put("PRODUCT_NAME", userProductData.getString("PRODUCT_NAME"));
                }
            }

            return userList;
        } else if ("1".equals(queryType))// 根据服务号码查询
        {
            String serialNumber = data.getString("SERIAL_NUMBER");

            IData userData = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);

            if (IDataUtil.isEmpty(userData)) {
                return null;
            }

            userData.put("AREA_NAME", userData.getString("CITY_NAME"));

            IData custData = UcaInfoQry.qryGrpInfoByCustId(userData.getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(custData)) {
                userData.put("GROUP_ID", custData.getString("GROUP_ID"));
                userData.put("CUST_ID", custData.getString("CUST_ID"));
                userData.put("CUST_NAME", custData.getString("CUST_NAME"));
            }

            return IDataUtil.idToIds(userData);
        }

        return null;
    }

    /**
     * 根据产品关系类型、手机号码查CG库查询集团服务号码
     * 
     * @param iData
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupCustInfoBySnAndRelationTypeCode(IData iData, Pagination pagination) throws Exception {

        SQLParser parser = new SQLParser(iData);

        parser.addSQL(" Select GROUP_ID From tf_f_cust_group cg,tf_f_user u,tf_f_relation_uu uu  ");
        parser.addSQL("Where 1=1 and uu.relation_type_code = :RELATION_TYPE_CODE ");
        parser.addSQL("And uu.serial_number_b = :SERIAL_NUMBER ");
        parser.addSQL("And uu.User_Id_a = u.User_Id ");
        parser.addSQL("And u.cust_id = cg.cust_id ");
        parser.addSQL("And uu.End_Date > Sysdate");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);

    }

    public static IDataset queryGroupInfosByCustId(IData param) throws Exception {
        SQLParser parserCG = new SQLParser(param);
        parserCG.addSQL(" Select u.user_id,cg.group_id, s.data_name GROUP_CUST_NAME,s.data_name GROUP_PRODUCT_NAME,CG.GROUP_CONTACT_PHONE PHONE   From tf_f_cust_group cg ,tf_f_user u,td_S_static s  ");
        parserCG.addSQL("Where u.serial_number=:GROUP_ID  ");
        parserCG.addSQL("And DATA_ID=u.serial_number  ");
        parserCG.addSQL("And type_id='GRP_FARMERCC'  ");
        parserCG.addSQL("And u.cust_id=cg.cust_id  ");
        parserCG.addSQL("And u.remove_tag='0'  ");
        return Dao.qryByParse(parserCG, Route.CONN_CRM_CG);

    }

    /**
     * 根据集团GROUP_ID查询集团预开信息
     * 
     * @author fengsl
     * @date 2013-02-26
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupInfosById(IData param, Pagination page) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT g.class_id, ");
        parser.addSQL(" t.GROUP_ID, ");
        parser.addSQL(" t.PRODUCT_ID, ");
        parser.addSQL(" t.CANCEL_TAG, ");
        parser.addSQL(" t.PHONE, ");
        parser.addSQL(" t.REGISTER_NAME, ");
        parser.addSQL(" t.ADDR, ");
        parser.addSQL(" TO_CHAR(t.EXE_DATE,'YYYY-MM-DD HH:MM:SS') EXE_DATE, ");
        parser.addSQL(" TO_CHAR(t.PRE_EXE_DATE,'YYYY-MM-DD HH:MM:SS') PRE_EXE_DATE, ");
        parser.addSQL(" t.INDUSTRY_TYPE, ");
        parser.addSQL(" t.REMARK, ");
        parser.addSQL(" t.PREVALUEC1, ");
        parser.addSQL(" t.EPARCHY_CODE ");
        parser.addSQL(" FROM TF_F_QDGROUP t, TF_F_CUST_GROUP g ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND g.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND t.group_id = g.GROUP_ID ");
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团GROUP_ID查询集团预开信息
     * 
     * @author fengsl
     * @date 2013-02-26
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupInfosById(String group_id, Pagination page) throws Exception {
        IData data = new DataMap();
        data.put("GROUP_ID", group_id);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT g.class_id, ");
        parser.addSQL(" t.GROUP_ID, ");
        parser.addSQL(" t.PRODUCT_ID, ");
        parser.addSQL(" t.CANCEL_TAG, ");
        parser.addSQL(" t.PHONE, ");
        parser.addSQL(" t.REGISTER_NAME, ");
        parser.addSQL(" t.ADDR, ");
        parser.addSQL(" TO_CHAR(t.EXE_DATE,'YYYY-MM-DD HH:MM:SS') EXE_DATE, ");
        parser.addSQL(" TO_CHAR(t.PRE_EXE_DATE,'YYYY-MM-DD HH:MM:SS') PRE_EXE_DATE, ");
        parser.addSQL(" t.INDUSTRY_TYPE, ");
        parser.addSQL(" t.REMARK, ");
        parser.addSQL(" t.PREVALUEC1, ");
        parser.addSQL(" t.EPARCHY_CODE ");
        parser.addSQL(" FROM TF_F_QDGROUP t, TF_F_CUST_GROUP g ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND g.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND t.group_id = g.GROUP_ID ");
        IDataset resultset = Dao.qryByParse(parser, page, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;

        for (int i = 0; i < resultset.size(); i++) {
            IData result = resultset.getData(i);
            result.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", result.getString("CLASS_ID")));
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
            result.put("CANCEL_NAME", StaticUtil.getStaticValue("CANCEL_TAG", result.getString("CANCEL_TAG")));
            result.put("INDUSTRY_NAME", StaticUtil.getStaticValue("TD_S_CALLINGTYPE", result.getString("INDUSTRY_TYPE")));
            result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(result.getString("EPARCHY_CODE")));
        }
        return resultset;
    }

    /**
     * 原小栏框逻辑，根据USER_ID查询集团关键人信息 TODO 看谁倒霉，迁移sql
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpKeyManByUserId(String userId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT GROUP_ID, RSRV_STR1, GROUP_KEYMAN_ID, GROUP_CUST_ID ");
        parser.addSQL("FROM TF_F_CUST_GROUP_KEYMAN N ");
        parser.addSQL("WHERE N.USER_ID = :USER_ID ");

        return Dao.qryByParse(parser);
    }

    /**
     * 查询成员使用产品信息 --订购产品情况
     * 
     * @author fuzn 2013-07-24
     * @param serialNumber
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpProuctByUserId(String userId, Pagination pagination) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT g.GROUP_ID,g.CUST_NAME,p.PRODUCT_ID,g.CUST_MANAGER_ID ");
        parser.addSQL("FROM TF_F_CUST_GROUP g,TF_F_USER u,TF_F_USER_PRODUCT p ");
        parser.addSQL("WHERE  g.CUST_ID = u.CUST_ID ");
        parser.addSQL("AND u.USER_ID = p.USER_ID ");
        parser.addSQL("AND u.USER_ID = :USER_ID ");
        parser.addSQL("AND u.PARTITION_ID = MOD(u.USER_ID, 10000) ");
        parser.addSQL("AND u.REMOVE_TAG = '0' ");

        IDataset dataset = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);

        return dataset;
    }

    /**
     * 根据编号查询挂账集团信息
     * 
     * @author liaoyi
     * @param params
     *            查询所需参数
     * @param pagination
     * @return IDataset 客户标志资料列表
     * @throws Exception
     */
    public static IDataset queryGrpUserBysn(IData data) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT C.CUST_NAME,C.CUST_ID,C.EPARCHY_CODE,C.CITY_CODE,U.USER_ID,U.PRODUCT_ID,U.BRAND_CODE,U.SERIAL_NUMBER,U.RSRV_STR1,U.RSRV_STR2,U.RSRV_STR3 FROM TF_F_CUSTOMER C, TF_F_USER U");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND C.CUST_ID = U.CUST_ID");
        parser.addSQL(" AND U.REMOVE_TAG = 0");
        parser.addSQL(" AND U.PRODUCT_ID = 7000");
        parser.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据编号查询挂账集团信息
     * 
     * @author liaoyi
     * @param params
     *            查询所需参数
     * @param pagination
     * @return IDataset 客户标志资料列表
     * @throws Exception
     */
    public static IDataset queryGrpUserBysnMen(IData data) throws Exception {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT C.CUST_NAME,C.CUST_ID,C.EPARCHY_CODE,C.CITY_CODE,U.USER_ID,U.PRODUCT_ID,U.BRAND_CODE,U.SERIAL_NUMBER,U.RSRV_STR1,U.RSRV_STR2,U.RSRV_STR3 FROM TF_F_CUSTOMER C, TF_F_USER U");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND C.CUST_ID = U.CUST_ID");
        parser.addSQL(" AND U.REMOVE_TAG = 0");
        parser.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据USER_ID_A查询
     * 
     * @param USER_ID_A
     * @return
     * @throws Exception
     */
    public static IDataset queryUserGroupInfos(String USER_ID, String REMOVE_TAG) throws Exception {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("REMOVE_TAG", REMOVE_TAG);
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_USERID", cond);
    }

    public static int updateGrpBusyByApplyId(String applyId) throws Exception {
        IData param = new DataMap();
        param.put("APPLY_ID", applyId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("update TF_B_GRP_BUSAPPLY T");
        parser.addSQL("   set T.DEAL_STATE = '9'");
        parser.addSQL(" where 1 = 1");
        parser.addSQL("   AND T.APPLY_ID=:APPLY_ID");

        return Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

    /**
     * xuyt 2014-6-10 集团产品信息查询
     * 
     * @param groupid
     * @return
     * @throws Exception
     */
    public static IDataset qryUserPrdByGrpIdFlux(String groupid) throws Exception {

        IData param = new DataMap();
        param.put("GROUP_ID", groupid);
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPINFO_BY_GRPIDFLUX", param);
    }

    /**
     * 根据[MP_GROUP_CUST_CODE]查询集团客户信息
     * 
     * @param mpGrpCustCode
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-27
     */
    public static IDataset queryGrpInfoByGrpCustCode(String mpGrpCustCode) throws Exception {
        IData param = new DataMap();
        param.put("MP_GROUP_CUST_CODE", mpGrpCustCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_CUST_GROUP A");
        parser.addSQL(" WHERE A.MP_GROUP_CUST_CODE=:MP_GROUP_CUST_CODE");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据cust_id 查询group group_id,cust_name信息
     * 
     * @param param
     * @return
     * @throws Exception
     *             2019/06/03
     */
    public static IDataset queryGrpIdBygrpcustId(String custid) throws Exception {

        IData param = new DataMap();
        param.put("CUST_ID", custid);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select g.group_id ,g.cust_name ");
        parser.addSQL("from tf_f_cust_group g ");
        parser.addSQL("WHERE 1 = 1 and g.remove_tag='0'  ");
        parser.addSQL("AND g.cust_id = :CUST_ID ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据grouId 查询集团客户物联网EC白名单信息
     * 
     * @param custid
     * @return
     * @throws Exception
     *             2019/06/03
     */
    public static IDataset queryGrpWLWECWhiteBygrpId(IData param) throws Exception {
        if (param == null) {
            return null;
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select  ge.group_id,ge.cust_name,ge.white_flag,ge.syn_flag,ge.m_product  ");
        parser.addSQL("from TF_F_CUST_GROUP_ECWHITE ge ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND ge.group_id = :GROUP_ID ");
        return Dao.qryByParse(parser);
    }

    /**
     * 
     * 通过custid查询集团信息
     *
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryCustGroupInfoByCustid(String custid) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", custid);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select cg.CUST_ID,cg.GROUP_ID,cg.CUST_NAME,cg.GROUP_TYPE ");
        parser.addSQL(" from  tf_f_cust_group cg where 1=1 ");
        parser.addSQL(" and cg.REMOVE_TAG = '0'   ");
        parser.addSQL(" and cg.CUST_ID = :CUST_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 根据CUST_ID查询集团客户信息
     * @param custId
     * @return
     * @throws Exception
     * @Author:songxw
     * @Date:2019-10-21
     */
    public static IDataset queryGrpInfoByCustId(String custId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("CUST_ID", custId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_CUST_GROUP A");
        parser.addSQL(" WHERE A.CUST_ID=:CUST_ID");
        return Dao.qryByParse(parser);
    }
	
	    /*
     * 关于开发“集团客户，乐享有礼”活动的需求
     */
    public static IData qryGrpInfoByGIDNUM(IData param, Pagination pagination) throws Exception {
    	IData result = new DataMap();
    	
    	String groupId = param.getString("GROUP_ID", "");
    	String groupSn = param.getString("SERIAL_NUMBER", "");
    	if(StringUtils.isBlank(groupId) && StringUtils.isBlank(groupSn) )
    	{
    		result.put("RESULTCODE", "9999");
    		result.put("RESULTINFO", "查询失败,无有效条件");
    		return result;
    	}
    	
    	IDataset ds = new DatasetList();
    	Boolean flag = false;
    	if(StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(groupSn) )
		{
			result.put("RESULTCODE", "9999");
    		result.put("RESULTINFO", "根据手机号码、集团编码查询不到集团信息");
    		
    		ds = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPINFO_BY_GIDNUM", param);
    		if(IDataUtil.isNotEmpty(ds))
    		{
    			flag = true;
    		}
    			
		}else if(StringUtils.isNotBlank(groupSn)){
			result.put("RESULTCODE", "9999");
    		result.put("RESULTINFO", "根据手机号码查询不到集团信息");
    		
    		ds = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPINFO_BY_NUM", param);
    		if(IDataUtil.isNotEmpty(ds))
    		{
    			flag = true;
    		}
		}else{
			result.put("RESULTCODE", "9999");
    		result.put("RESULTINFO", "根据集团编码查询不到集团信息");
    		
    		ds = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPINFO_BY_GID", param);
    		if(IDataUtil.isNotEmpty(ds))
    		{
    			flag = true;
    		}
		}
    	
    	if(flag)
    	{
    		result.put("GROUPID", ds.getData(0).getString("GROUP_ID", ""));
    		result.put("GROUPNAME", ds.getData(0).getString("GROUP_NAME", ""));
    		result.put("GROUPMGRSN", ds.getData(0).getString("GROUP_MGR_SN", ""));
    		result.put("RESULTCODE", "0000");
    		result.put("RESULTINFO", "查询成功");
    	}
    	
		return result;
    	      
    }
    
    /**
     * 
     * @param custId
     * @param cityCode
     * @return
     * @throws Exception
     */
    public static int updateCustGrpByCustId(String custId,String cityCode) throws Exception
    {
    	IData param = new DataMap();
        param.put("CITY_CODE", cityCode);
        param.put("CUST_ID", custId);
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_F_CUST_GROUP T ");
        parser.addSQL(" SET T.CITY_CODE = :CITY_CODE ");
        parser.addSQL("WHERE T.CUST_ID = :CUST_ID");
        parser.addSQL(" AND T.REMOVE_TAG = '0' ");
        return Dao.executeUpdate(parser);
    }
    
}
