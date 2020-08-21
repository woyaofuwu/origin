
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ECFetionConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;

public class UserGrpPkgInfoQry
{
    /**
     * todo code_code 表里没有查到
     * 
     * @description:根据user_id_a、user_id_b查询成员可订购的定制优惠
     * @author wusf
     * @date Sep 29, 2010
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGdDiscntsByUserId(IData data, String eparchyCode) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT '' user_id,d.discnt_code,d.discnt_name,d.discnt_explain,t.product_id,t.package_id,p.package_name,'' start_date,'' end_date, ");
        parser.addSQL("      p.min_number,p.max_number,e.default_tag,e.force_tag ");
        parser.addSQL("   FROM tf_f_user_grp_package t,td_b_package p,td_b_discnt d,td_b_package_element e ");
        parser.addSQL("  where 1=1 ");
        parser.addSQL("  and t.element_id = d.discnt_code ");
        parser.addSQL("  and t.package_id = e.package_id ");
        parser.addSQL("  and p.package_id = t.package_id ");
        parser.addSQL("  and t.element_id = e.element_id ");
        parser.addSQL("  and (d.eparchy_code = 'ZZZZ' or d.eparchy_code = :EPARCHY_CODE) ");
        parser.addSQL("  and t.element_type_code = 'D' ");
        parser.addSQL("  and t.user_id = :USER_ID_A ");
        parser.addSQL("  and t.partition_id=mod(TO_NUMBER(:USER_ID_A),10000) ");
        parser.addSQL("      AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");
        parser.addSQL("      and sysdate between e.start_date and e.end_date ");
        parser.addSQL(" and sysdate between d.start_date and d.end_date ");
        parser.addSQL("  order by t.element_type_code desc ");

        IDataset iDataset = Dao.qryByParse(parser, eparchyCode);

        if (IDataUtil.isEmpty(iDataset))
        {
            return new DatasetList();
        }

        for (int i = iDataset.size() - 1; i >= 0; i--)
        {
            boolean isPriv = StaffPrivUtil.isDistPriv(data.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()), iDataset.getData(i).getString("DISCNT_CODE"));
            if (!isPriv)
            {
                iDataset.remove(i);
            }
        }

        // IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERIDAB", data, eparchyCode);
        return iDataset;
    }

    /**
     * 通过集团user_id查询成员所有营销优惠信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getMembVPMNDiscntByGrpProductId(IData inparams, String eparchyCode) throws Exception
    {
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT d.discnt_name element_name, ");
        parser.addSQL(" t.element_id element_id, ");
        parser.addSQL(" t.product_id, ");
        parser.addSQL(" t.package_id, ");
        parser.addSQL(" to_char(t.start_date, 'yyyy-mm-dd') start_date, ");
        parser.addSQL(" to_char(t.end_date, 'yyyy-mm-dd') end_date, ");
        parser.addSQL(" p.min_number, ");
        parser.addSQL(" p.max_number ");
        parser.addSQL(" FROM tf_f_user_grp_package t, td_b_discnt d, td_b_package p ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" AND t.element_id = d.discnt_code ");
        parser.addSQL(" AND t.element_type_code = 'D' ");
        parser.addSQL(" AND p.package_id = t.package_id ");
        parser.addSQL(" and t.user_id = :USER_ID ");
        parser.addSQL(" AND partition_id = mod(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");
        parser.addSQL(" ORDER by t.element_type_code desc ");
        IDataset dataset = Dao.qryByParse(parser, eparchyCode);
        return dataset;
    }

    /**
     * @Function: getUserGrpPackage
     * @Description: 查询用户订购信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:02:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserGrpPackage(String user_id, String eparchcode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_NOW", param, eparchcode);
        return dataset;
    }

    /**
     * @Function: getUserGrpPackageForGrp
     * @Description: 从集团库里 SEL_BY_USERID_NOW
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:03:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserGrpPackageForGrp(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_NOW", param, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(dataset))
            return dataset;

        ElemInfoQry.fillElementName(dataset);
        
        return dataset;
    }

    public static IDataset getUserGrpPackageIdForGrp(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_PACKAGEID_BY_USERID", param, Route.CONN_CRM_CG);

        return dataset;
    }

    /**
     * @Function: getUserSingleGrpPackage
     * @Description: 查询用户订购信息
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:04:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserSingleGrpPackage(String user_id, String product_id, String package_id, String element_id, String element_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", product_id);
        param.put("PACKAGE_ID", package_id);
        param.put("ELEMENT_ID", element_id);
        param.put("ELEMENT_TYPE_CODE", element_type_code);
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_PK", param);
        return dataset;
    }
    
    public static IDataset getUserGrpPkgInfoByPk(String user_id, String product_id, String package_id, String element_id, String element_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", product_id);
        param.put("PACKAGE_ID", package_id);
        param.put("ELEMENT_ID", element_id);
        param.put("ELEMENT_TYPE_CODE", element_type_code);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_GRP_PACKAGE", "SEL_BY_PK", param, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 查询可选优惠
     * 
     * @param userId
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryDiscntInfosByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ELEMENT_ID DISCNT_CODE ");
        parser.addSQL(" FROM TF_F_USER_GRP_PACKAGE T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND T.ELEMENT_TYPE_CODE = 'D' ");
        parser.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
        parser.addSQL(" ORDER BY T.ELEMENT_TYPE_CODE DESC ");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
            }
        }
        return dataset;
    }

    public static IDataset qryDiscntLimitByUserId(String userId, String productId, String elementIda) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT D.DISCNT_NAME ELEMENT_NAME, D.DISCNT_CODE ELEMENT_ID                                                                                                             ");
        sb.append("   FROM TF_F_USER_GRP_PACKAGE T, TD_B_DISCNT D                                                                     ");
        sb.append("  WHERE 1 = 1                                                                                                      ");
        sb.append("    AND T.ELEMENT_ID = D.DISCNT_CODE                                                                               ");
        sb.append("    AND T.ELEMENT_TYPE_CODE = 'D'                                                                                  ");
        sb.append("    AND T.USER_ID = TO_NUMBER(:USER_ID)                                                                            ");
        sb.append("    AND (T.PRODUCT_ID = :PRODUCT_ID OR :PRODUCT_ID = '-1')                                                         ");
        sb.append("    AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)                                                             ");
        sb.append("    AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE                                                                ");
        sb.append("    AND T.ELEMENT_ID NOT IN(SELECT PARAM_CODE FROM TD_S_COMMPARA WHERE PARAM_ATTR='8860' AND SUBSYS_CODE='CSM')    ");
        sb.append("    AND T.ELEMENT_ID NOT IN                                                                                        ");
        sb.append("        (SELECT TMP.ELEMENT_ID                                                                                     ");
        sb.append("           FROM TD_B_ELEMENT_LIMIT A,                                                                              ");
        sb.append("                (SELECT D.DISCNT_NAME ELEMENT_NAME, D.DISCNT_CODE ELEMENT_ID                                       ");
        sb.append("                   FROM TF_F_USER_GRP_PACKAGE T, TD_B_DISCNT D                                                     ");
        sb.append("                  WHERE 1 = 1                                                                                      ");
        sb.append("                    AND T.ELEMENT_ID = D.DISCNT_CODE                                                               ");
        sb.append("                    AND T.ELEMENT_TYPE_CODE = 'D'                                                                  ");
        sb.append("                    AND T.USER_ID = TO_NUMBER(:USER_ID)                                                            ");
        sb.append("                    AND (T.PRODUCT_ID = :PRODUCT_ID OR :PRODUCT_ID = '-1')                                         ");
        sb.append("                    AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)                                             ");
        sb.append("                    AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE) TMP                                           ");
        sb.append("          WHERE A.ELEMENT_ID_A IN (                                                                                ");
        sb.append(elementIda);
        sb.append("                                                         )                                                         ");
        sb.append("            AND A.ELEMENT_ID_B = TMP.ELEMENT_ID                                                                    ");
        sb.append("            AND A.ELEMENT_TYPE_CODE_A = 'D'                                                                        ");
        sb.append("            AND A.ELEMENT_TYPE_CODE_B = 'D'                                                                        ");
        sb.append("            AND A.LIMIT_TAG = '0')                                                                                 ");
        sb.append("    AND (:TRADE_STAFF_ID = 'SUPERUSR' OR EXISTS                                                                    ");
        sb.append("         (SELECT 1                                                                                                 ");
        sb.append("            FROM (SELECT B.DATA_CODE                                                                               ");
        sb.append("                    FROM TF_M_STAFFDATARIGHT A, TF_M_ROLEDATARIGHT B                                               ");
        sb.append("                   WHERE A.DATA_TYPE = 'D'                                                                         ");
        sb.append("                     AND A.RIGHT_ATTR = 1                                                                          ");
        sb.append("                     AND A.RIGHT_TAG = 1                                                                           ");
        sb.append("                     AND A.DATA_CODE = B.ROLE_CODE                                                                 ");
        sb.append("                     AND A.STAFF_ID = :TRADE_STAFF_ID                                                              ");
        sb.append("                  UNION                                                                                            ");
        sb.append("                  SELECT A.DATA_CODE                                                                               ");
        sb.append("                    FROM TF_M_STAFFDATARIGHT A                                                                     ");
        sb.append("                   WHERE A.DATA_TYPE = 'D'                                                                         ");
        sb.append("                     AND A.RIGHT_ATTR = 0                                                                          ");
        sb.append("                     AND A.RIGHT_TAG = 1                                                                           ");
        sb.append("                     AND A.STAFF_ID = :TRADE_STAFF_ID) DATATMP                                                     ");
        sb.append("           WHERE DATATMP.DATA_CODE = TO_CHAR(D.DISCNT_CODE)))                                                      ");

        return Dao.qryBySql(sb, param);
    }

    /**
     * todo 里面逻辑很多 查询企业飞信产品订购关系
     * 
     * @param params
     * @return
     * @author Liuyt3
     */
    public static IDataset qryECFetionProductOfferId(String user_id_b) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_B", user_id_b);
        params.put("EFPRODUCTID1", ECFetionConstants.FC_POP_ID_LOCAL);
        params.put("EFPRODUCTID2", ECFetionConstants.FC_POP_ID_WHOLE);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT U.USER_ID_A FROM TF_F_RELATION_UU U WHERE SYSDATE BETWEEN U.START_DATE AND U.END_DATE ");
        parser.addSQL(" AND U.USER_ID_B=TO_NUMBER(:USER_ID_B) ");
        parser.addSQL(" AND U.PARTITION_ID=MOD(TO_NUMBER(:USER_ID_B),10000)");
        IDataset ds = Dao.qryByParse(parser);
        if (IDataUtil.isEmpty(ds))
        {
            return null;
        }

        IDataset result = new DatasetList();
        for (int i = 0; i < ds.size(); i++)
        {
            params.put("USER_ID", ds.getData(i).getString("USER_ID_A", ""));
            SQLParser parser2 = new SQLParser(params);
            parser2.addSQL(" SELECT MP.INST_ID,MP.PARTITION_ID, MP.USER_ID, MP.MERCH_SPEC_CODE, MP.PRODUCT_SPEC_CODE, MP.PRODUCT_ORDER_ID, MP.PRODUCT_OFFER_ID, MP.GROUP_ID, ");
            parser2.addSQL(" MP.SERV_CODE, MP.BIZ_ATTR, MP.STATUS, MP.START_DATE, MP.END_DATE, MP.UPDATE_TIME, MP.UPDATE_STAFF_ID, MP.UPDATE_DEPART_ID, MP.REMARK, MP.RSRV_NUM1, ");
            parser2.addSQL(" MP.RSRV_NUM2, MP.RSRV_NUM3, MP.RSRV_NUM4, MP.RSRV_NUM5, MP.RSRV_STR1, MP.RSRV_STR2, MP.RSRV_STR3, MP.RSRV_STR4, MP.RSRV_STR5, MP.RSRV_DATE1, MP.RSRV_DATE2, ");
            parser2.addSQL(" MP.RSRV_DATE3, MP.RSRV_TAG1, MP.RSRV_TAG2, MP.RSRV_TAG3 FROM TF_F_USER_GRP_MERCHP MP ");
            parser2.addSQL(" WHERE MP.USER_ID=:USER_ID  ");
            parser2.addSQL(" AND MP.PRODUCT_SPEC_CODE IN (:EFPRODUCTID1, :EFPRODUCTID2) ");
            parser2.addSQL(" AND SYSDATE BETWEEN MP.START_DATE AND MP.END_DATE ");
            IDataset ids = Dao.qryByParse(parser, Route.CONN_CRM_CG);
            if (IDataUtil.isNotEmpty(ids))
            {
                result.addAll(ids);
            }
        }
        return result;
    }

    /**
     * 根据集团用户标识查询集团定制资费信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpCustomizeDiscntByUserId(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERID", param, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		String elementId = result.getData(i).getString("ELEMENT_ID");
        		result.getData(i).put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(elementId));
        	}
        }

        return result;
    }

    /**
     * 查询可选优惠
     * 
     * @param userId
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpCustomizeDiscntByUserId(String userId, String staffId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("STAFF_ID", staffId);
        return Dao.qryByCodeParser("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERID_STAFFID", param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: qryMerchpInfos
     * @Description: 根据通用条件查询BBOSS产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:08:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryMerchpInfos(String group_id, String user_id, String merch_spec_code, String product_spec_code, String status, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("PRODUCT_SPEC_CODE", product_spec_code);
        param.put("STATUS", status);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.* ");
        parser.addSQL("  FROM  TF_F_USER_GRP_MERCHP  a, td_f_poproduct t ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND t.pospecnumber=a.MERCH_SPEC_CODE ");
        parser.addSQL("   AND t.PRODUCTSPECNUMBER=a.PRODUCT_SPEC_CODE ");
        parser.addSQL("   AND a.group_id = :GROUP_ID ");
        parser.addSQL("   AND a.USER_ID = :USER_ID ");
        parser.addSQL("   AND a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE ");
        parser.addSQL("   AND a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE  ");
        parser.addSQL("   AND a.status = :STATUS  ");
        parser.addSQL("   AND a.end_date > sysdate ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Function: queryGrpPayTagByUserID
     * @Description: 根据USER_ID查询集团用户是否支持高级付费标记
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author:
     * @date: 2013-4-26 上午10:13:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 v1.0.0 修改原因
     */
    public static IDataset queryGrpPayTagByUserID(String userid) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID", userid);
        params.put("RSRV_VALUE_CODE", "GRPI");
        // params.put("RSRV_VALUE", "集团付费及其群号码");
        params.put("RSRV_STR1", "01");

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", params, Route.CONN_CRM_CG);
    }

    /**
     * @Function: queryUserAttrByUserIDServiceId
     * @Description: 作用：查询用户的扩展属性TF_F_USER_ATTR::SEL_BY_USERID_INSTTYPE
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:16:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryUserAttrByUserIDServiceId(String userid, String inst_type, String service_id, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("INST_TYPE", inst_type);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_USERID_INSTTYPE", param, eparchyCode);
    }
}
