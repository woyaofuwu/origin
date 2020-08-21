
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

public class PayRelaInfoQry
{
    /**
     * 根据 ACCT_ID， default_tag， act_tag 三个条件查询 acct_info_list auth : goayuan@asiainfo-linkage.com @ 2012-2-9 下午02:50:59
     * 
     * @param param
     *            {ACCT_ID， default_tag， act_tag}
     * @return
     * @throws Exception
     */
    public static IDataset getAcctPayReltionNow(String acctId, String defaultTag, String actTag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", actTag);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", param, pagination);
    }
    /**
     * 根据账户id和userid项查成员的付费关系
     * 
     * @param userId
     * @param acctId
     * @return
     * @throws Exception
     */

    public static IDataset qryInfosByUserIdAcctIdPayitem(String userId, String acctId, String stat_month) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        
        String firstTime = SysDateMgr.decodeTimestamp(stat_month, SysDateMgr.PATTERN_STAND);
        String lastTime = SysDateMgr.getAddMonthsLastDay(0, firstTime); 
        param.put("FIRST_TIME", firstTime);
        param.put("LAST_TIME", lastTime);
            
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND default_tag = '0' ");
        parser.addSQL(" AND acct_id = :ACCT_ID ");
        parser.addSQL(" AND act_tag = '1' ");
        parser.addSQL(" AND end_cycle_id> :FIRST_TIME ");
        parser.addSQL(" AND start_cycle_id < :LAST_TIME ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }
    /**
     * 获取高级付费关系的相关资料 流程: 1.根据手机号查正常用户的资料 2.根据USER_ID查该用户默认的帐户资料 3.根据不同的付费类型获得高级付费关系资料
     * 
     * @param inparams
     *            查询所要输入的参数(SERIAL_NUMBER,PAYRELATION_QUERY_TYPE,PAYRELATION_PAY_TYPE )
     * @param pagination
     *            TODO
     * @return IDataset 高级付费关系结果集
     * @throws Exception
     * @author chenhao 2009-2-19
     */
    public static IDataset getAdvPayRelation(IData inparams, Pagination pagination) throws Exception
    {

        String payType = inparams.getString("PAYRELATION_PAY_TYPE", "");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(inparams.getString("SERIAL_NUMBER"));
        // IDataset ids = UserInfoQry.getUserInfoBySn(inparams.getString("SERIAL_NUMBER"),
        // inparams.getString("REMOVE_TAG","0"), "00");
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_282);
        }
        // IData idata = ids.getData(0);
        inparams.put("USER_ID", userInfo.getString("USER_ID", ""));
        IDataset dataset = IDataUtil.idToIds(UcaInfoQry.qryAcctInfoByUserId(inparams.getString("USER_ID")));
        if (dataset != null && dataset.size() > 0)
        {
            inparams.put("ACCT_ID", ((IData) dataset.get(0)).getString("ACCT_ID", ""));
            inparams.put("EPARCHY_CODE", userInfo.get("EPARCHY_CODE"));
            inparams.put("USER_ID", userInfo.get("USER_ID"));
            IDataset dataset1 = null;
            IDataset dataset2 = null;
            dataset.clear();

            if ("0".equals(payType))
            {
                dataset1 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_PAY", inparams, pagination);
                dataset2 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_PAY", inparams, pagination, Route.CONN_CRM_CG);
            }
            else if ("1".equals(payType))
            {
                dataset1 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_PAYED", inparams, pagination);
                dataset2 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_PAYED", inparams, pagination, Route.CONN_CRM_CG);
            }
            else if ("2".equals(payType))
            {
                dataset1 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_ALL", inparams, pagination);
                dataset2 = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADV_PAYRELA_ALL", inparams, pagination, Route.CONN_CRM_CG);
            }

            if (null != dataset1 && !dataset1.isEmpty())
            {
                dataset.addAll(dataset1);
            }

            if (null != dataset2 && !dataset2.isEmpty())
            {
                dataset.addAll(dataset2);
            }

        }

        return dataset;
    }

    public static IDataset getAdvPayRelationAllByAcctID(String acctId, String payrelationQueryType, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ACCT_ID", acctId);
        inparams.put("PAYRELATION_QUERY_TYPE", payrelationQueryType);
        SQLParser sql = new SQLParser(inparams);
        sql.addSQL("select a.acct_id,b.serial_number serial_number_b,a.payitem_code,a.start_cycle_id,a.end_cycle_id, "); // select后注释掉
        // :SERIAL_NUMBER_A
        // serial_number_a,
        sql.addSQL(" to_number(nvl(a.LIMIT,0))/100.0 limit, ");
        sql.addSQL(" decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag ");
        sql.addSQL(" FROM tf_a_payrelation a, tf_f_user b ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND a.user_id=b.user_id ");
        sql.addSQL(" And a.acct_id=:ACCT_ID ");
        sql.addSQL(" AND a.act_tag='1' ");
        sql.addSQL(" AND a.default_tag='0' ");
        sql.addSQL(" AND (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id))");
        sql.addSQL(" AND a.start_cycle_id  <= a.end_cycle_id");
        sql.addSQL(" ORDER BY payitem_code");
        return Dao.qryByParse(sql, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 根据账户标识获取高级付费关系数量
     * 
     * @author liaoyi 2010-12-8
     * @param inparams
     *            查询所需参数(ACCT_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getAdvPayRelationByAcctID(String acctId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ACCT_ID", acctId);

        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationAdv", inparams, pagination);
    }

    /**
     * @auto fengsl
     * @date 2013-04-16
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getAdvPayRelationInfo_ALL(String acctId, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ACCT_ID", acctId);

        SQLParser sql = new SQLParser(inparam);
        sql.addSQL("SELECT a.acct_id,b.serial_number serial_number_b,a.payitem_code,a.start_cycle_id,a.end_cycle_id,");
        sql.addSQL(" decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') limit_type, ");
        sql.addSQL(" to_number(nvl(a.LIMIT,0))/100.0 limit, ");
        sql.addSQL(" decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag ");
        sql.addSQL(" FROM tf_a_payrelation a, tf_f_user b ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND a.user_id=b.user_id ");
        sql.addSQL(" And a.partition_id = mod(a.user_id,10000) ");
        sql.addSQL(" And a.acct_id=:ACCT_ID ");
        sql.addSQL(" AND a.act_tag='1' ");
        sql.addSQL(" AND a.default_tag='0' ");
        sql.addSQL(" AND (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id))");
        sql.addSQL(" AND a.start_cycle_id  <= a.end_cycle_id");
        sql.addSQL(" ORDER BY payitem_code");
        IDataset ids = Dao.qryByParse(sql, pagination);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData map = ids.getData(i);
            map.put("ITEM_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PAYITEM_CODE")));
        }
        return ids;
    }

    /**
     * @auto fengsl
     * @date 2013-04-16
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getAdvPayRelationInfo_Pay(String acctId, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ACCT_ID", acctId);

        SQLParser sql = new SQLParser(inparam);
        sql.addSQL("SELECT a.acct_id,b.serial_number serial_number_b,a.payitem_code,a.start_cycle_id,a.end_cycle_id,");
        sql.addSQL(" decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') limit_type, ");
        sql.addSQL(" to_number(nvl(a.LIMIT,0))/100.0 limit, ");
        sql.addSQL(" decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag ");
        sql.addSQL(" FROM tf_a_payrelation a, tf_f_user b ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND b.user_id=a.user_id ");
        sql.addSQL(" And b.partition_id = mod(a.user_id,10000) ");
        sql.addSQL(" And a.acct_id=:ACCT_ID ");
        sql.addSQL(" AND a.act_tag='1' ");
        sql.addSQL(" AND a.default_tag='0' ");
        sql.addSQL(" AND (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id))");
        sql.addSQL(" AND a.start_cycle_id  <= a.end_cycle_id");
        sql.addSQL(" ORDER BY payitem_code");
        IDataset ids = Dao.qryByParse(sql, pagination);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData map = ids.getData(i);
            map.put("ITEM_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PAYITEM_CODE")));
        }
        return ids;
    }

    /**
     * @auto fengsl
     * @date 2013-04-16
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getAdvPayRelationInfo_PayD(String acctId, String payrelationQueryType, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ACCT_ID", acctId);
        inparam.put("PAYRELATION_QUERY_TYPE", payrelationQueryType);

        SQLParser sql = new SQLParser(inparam);
        sql.addSQL("SELECT a.acct_id,b.serial_number serial_number_b,a.payitem_code,a.start_cycle_id,a.end_cycle_id,");
        sql.addSQL(" decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') limit_type, ");
        sql.addSQL(" to_number(nvl(a.LIMIT,0))/100.0 limit, ");
        sql.addSQL(" decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag ");
        sql.addSQL(" FROM tf_a_payrelation a, tf_f_user b ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND a.user_id=b.user_id ");
        sql.addSQL(" And a.partition_id = mod(a.user_id,10000) ");
        sql.addSQL(" And a.acct_id=:ACCT_ID ");
        sql.addSQL(" AND a.act_tag='1' ");
        sql.addSQL(" AND a.default_tag='0' ");
        sql.addSQL(" AND (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id))");
        sql.addSQL(" AND a.start_cycle_id  <= a.end_cycle_id");
        sql.addSQL(" ORDER BY payitem_code");
        IDataset ids = Dao.qryByParse(sql, pagination);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData map = ids.getData(i);
            map.put("ITEM_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PAYITEM_CODE")));
        }
        return ids;
    }

    public static IDataset getAllPayrelationbyUA(String userId, String acctId, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);

        SQLParser parser = new SQLParser(inparams);

        parser.addSQL(" SELECT PARTITION_ID,USER_ID,ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY,BIND_TYPE,START_CYCLE_ID,END_CYCLE_ID,DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,UPDATE_STAFF_ID,UPDATE_DEPART_ID,INST_ID,UPDATE_TIME  ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND default_tag = '0' ");
        parser.addSQL(" AND acct_id = :ACCT_ID ");
        parser.addSQL(" AND end_cycle_id>TO_CHAR(SYSDATE,'YYYYMMDD') ");
        parser.addSQL(" AND end_cycle_id>start_cycle_id ");

        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getAllUserPayRelationByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALLUSERID_BY_ACCTID", param);
    }

    /**
     * @methodName: getAllPayRelationByUU
     * @Description: 查询用户所在关系里所有用户的有效付费关系
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-3-26 下午5:20:36
     */
    public static IDataset getAllValidPayRelationByUU(String userId, String roleCodeB, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ROLE_CODE_B", roleCodeB);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ACCT_BY_UU", param);
    }

    /**
     * 根据ACCT_ID获取此账户下所有在网用户付费关系信息【包括预约付费关系信息】
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getAllValidUserPayByAcctId(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_VALID_PAYRELA_BY_ACCTID", inparams);
    }

    public static IDataset getAllValidUserPayByAcctId(String acct_id) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acct_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_VALID_PAYRELA_BY_ACCTID", param);
    }

    public static IDataset getByUserIdDefaultTag(String user_id, String defaulttag) throws Exception
    {
        CSAppException.apperr(TradeException.CRM_TRADE_95,
                "[AILK/j2ee/crm/hnan/apps/csserv/src-service/com/ailk/csservice/common/query/rela/PayRelaInfoQry.java][873][禁用TF_A_PAYRELATION.SEL_BY_USER_MAX，统一使用BizQuery.getLastDefaultPayRelationByUserId方法，该方法从全局缓存查询数据，能减少数据库访问，提高查询效率]");
        return null;
        /*
         * IData param = new DataMap(); param.put("USER_ID", user_id); param.put("DEFAULT_TAG", defaulttag); return
         * Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER_1MAX", param);
         */
    }

    /**
     * 根据集团编码获取该集团的所有集团产品及付费关系
     * 
     * @param ctx
     * @param inparams
     *            查询所要输入的参数(SERIAL_NUMBER,PAYRELATION_QUERY_TYPE,PAYRELATION_PAY_TYPE )
     * @param pagination
     *            TODO
     * @return IDataset 高级付费关系结果集
     * @throws Exception
     * @author liaoyi 2009-2-19 集团彩铃（6200）、移动总机（6100） 手机邮箱（9001） 企业随E行（7050）、行业应用卡（7051）、M2M（7500） 企业建站（9003）、
     *         企业邮箱（9022）、移动OA（9009）、办公助理（9007） MAS二次开发及短信流量费（8900、8901、8902、8910、8920） 移动传真（9143）
     *         移动400（22000333）、车务通（22000340、22000348） 移动一卡通（6083）、呼叫中心直联（6085）
     */
    public static IDataset getGroupPayRelationInfoByCustID(String custId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);

        SQLParser sql = new SQLParser(inparams);
        sql.addSQL("SELECT * from ");
        sql.addSQL("(SELECT a.cust_id, a.group_id,a.cust_name,d.acct_id,d.pay_name,d.Rsrv_Str10,d.Eparchy_Code,c.End_Cycle_Id,b.user_id,b.Serial_Number,p.Product_Id,b.Open_Date");
        sql.addSQL(" FROM tf_f_cust_group a,tf_f_user b,tf_a_payrelation c,Tf_f_Account d,TF_F_USER_PRODUCT P");
        sql.addSQL(" WHERE a.cust_id = b.cust_id");
        sql.addSQL(" And b.user_id = c.user_id");
        sql.addSQL(" AND c.Acct_Id = d.Acct_Id");
        sql.addSQL(" AND B.USER_ID = P.USER_ID");
        sql.addSQL(" AND B.PARTITION_ID = P.PARTITION_ID");
        sql.addSQL(" AND b.remove_tag = '0'");
        sql.addSQL(" AND a.remove_tag = '0'");
        sql.addSQL(" AND c.default_tag='1'");
        sql.addSQL(" AND c.act_tag='1'");
        sql.addSQL(" AND to_number(to_char(sysdate, 'yyyymmdd'))");
        sql.addSQL(" BETWEEN c.start_cycle_id AND c.end_cycle_id");
        sql.addSQL(" AND p.Product_Id in (Select Distinct Id From Td_b_Attr_Biz Where ATTR_OBJ = 'SalePay' And end_date > Sysdate) ");
        sql.addSQL(" AND a.cust_id = :CUST_ID ");
        sql.addSQL(" UNION ");
        sql.addSQL("SELECT a.cust_id, a.group_id,a.cust_name,b.acct_id,b.pay_name,b.Rsrv_Str10,b.Eparchy_Code,c.End_Cycle_Id,d.user_id,d.Serial_Number,p.Product_Id,d.Open_Date");
        sql.addSQL(" FROM tf_f_cust_group a,Tf_f_Account b,Tf_a_Payrelation c,tf_f_user d,TF_F_USER_PRODUCT P");
        sql.addSQL(" WHERE a.cust_id = b.cust_id");
        sql.addSQL(" AND d.USER_ID = P.USER_ID");
        sql.addSQL(" AND d.PARTITION_ID = P.PARTITION_ID");
        sql.addSQL(" AND c.Acct_Id(+)  = b.Acct_Id");
        sql.addSQL(" AND d.User_Id(+) = c.User_Id");
        sql.addSQL(" AND p.Product_Id in (Select Distinct Id From Td_b_Attr_Biz Where ATTR_OBJ = 'SalePay' And end_date > Sysdate) ");
        sql.addSQL(" AND a.remove_tag = '0'");
        sql.addSQL(" AND (d.remove_tag = '0' Or d.remove_tag Is Null)");
        sql.addSQL(" AND (c.default_tag = '1' Or c.default_tag Is Null)");
        sql.addSQL(" AND (c.act_tag='1'  Or c.act_tag Is Null)");
        sql.addSQL(" AND ((to_number(to_char(sysdate, 'yyyymmdd'))");
        sql.addSQL(" BETWEEN c.start_cycle_id AND c.end_cycle_id) OR c.end_cycle_id Is Null)");
        sql.addSQL(" AND a.cust_id = :CUST_ID");
        sql.addSQL(" ) t where t.Serial_Number is not null ");
        IDataset resultset = Dao.qryByParse(sql, pagination, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
        }
        return resultset;
    }

    /**
     * 根据集团编码获取该集团的所有集团产品及付费关系
     * 
     * @param inparams
     *            查询所要输入的参数(GROUP_ID )
     * @param pagination
     * @return IDataset 高级付费关系结果集
     * @throws Exception
     * @author liaoyi 2011-03-26
     */
    public static IDataset getGroupPayRelationInfosByCustID(IData inparams) throws Exception
    {

        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_GRPPAYRELA_BY_CUSTID", inparams, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编码获取该集团的所有集团产品及付费关系
     * 
     * @param inparams
     *            查询所要输入的参数(GROUP_ID )
     * @param pagination
     * @return IDataset 高级付费关系结果集
     * @throws Exception
     * @author liaoyi 2011-03-26
     */
    public static IDataset getGroupPayRelationInfosByGroupId(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_GRPPAYRELA_BY_GROUPID", inparams, Route.CONN_CRM_CG);
    }

    /**
     * 根据账户标识获取集团成员数量
     * 
     * @author fuzn 2013-03-20
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getGrpMemCountRelationByUserID(String userIdA, String relationTypeCode, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID_A", userIdA);
        inparams.put("RELATION_TYPE_CODE", relationTypeCode);

        IDataset tmpDS = Dao.qryByCode("TD_S_CPARAM", "ExistsRelationUUA", inparams, pagination);
        return tmpDS;
    }

    /**
     * 根据账户标识获取集团普通付费关系数量
     * 
     * @author liaoyi 2011-7-21
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getGrpNorPayRelationByAcctID(String acctId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ACCT_ID", acctId);

        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationNor", inparams, pagination, Route.CONN_CRM_CG);
    }

    public static IDataset getItemByItemId(String itemId) throws Exception
    {
        IData param = new DataMap();
        param.put("ITEM_ID", itemId);
        return Dao.qryByCode("TD_B_ITEM", "SEL_ITEM_BY_ITEMID", param, Route.CONN_CRM_CEN);

    }

    public static IDataset getItemByItemType(String item_type, String item_use_type) throws Exception
    {
        IData param = new DataMap();
        param.put("ITEM_TYPE", item_type);
        param.put("ITEM_USE_TYPE", item_use_type);
        return Dao.qryByCode("TD_B_ITEM", "SEL_ITEM_BY_ITEMTYPE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getMemberPayRelaxc(String userId, String acctId, String payItemCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("ACCT_ID", acctId);
        data.put("PAYITEM_CODE", payItemCode);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_UNPAY_MEMBER_PAYRELA", data);
    }

    // add by zhaoyi@2011-01-04 end----------------

    /**
     * 查询明细账目(高级付费关系查询)
     * 
     * @param inparams
     *            输入参数(PAYITEM_CODE,EPARCHY_CODE)
     * @return IDataset 明细账目集
     * @throws Exception
     * @author chenhao 2009-2-19
     */
    public static IDataset getNoCachePayItemDetail(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_DETAILITEM", "SEL_DETAIL_BY_PAYITEMCODE_NOCACHE", inparams, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询普通付费关系结果集. 流程: 1.根据号码查询用户资料(正常) 2.根据用户ID查该用户默认的帐户资料 3.根据默认的帐户ID查普通付费关系
     * 
     * @param inparams
     *            查询输入的参数(SERIAL_NUMBER,PAYRELATION_QUERY_TYPE)
     * @param pagination
     *            分页
     * @return IDataset 普通付费关系结果集
     * @throws Exception
     * @author susw 2010-3-15
     */
    public static IDataset getNormalPayRelation(IData inparams, Pagination pagination) throws Exception
    {

        // putUserIdIntoParamBySn(inparams);// 根据号码 查询出user_id
        String serialNumber = inparams.getString("SERIAL_NUMBER");
        IData ids = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
            inparams.put("USER_ID", ids.getString("USER_ID"));
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_33);
        }

        // ---------------------苏盛伟修改 按用户 开始
        String payrelationMode = inparams.getString("PAYRELATION_QUERY_MODE", "");
        String payrelation_query_type = inparams.getString("PAYRELATION_QUERY_TYPE", "");
        IDataset dataset = new DatasetList();
        if ("1".equals(payrelationMode))
        {// 按用户
            String user_id = inparams.getString("USER_ID", "");

            // getVisit().setRouteEparchyCode(Route.CONN_CRM_CG);
            IDataset dataset2 = AcctInfoQry.getALLAcctInfoByUserIDForCG(user_id, payrelation_query_type, pagination);

            if (dataset2 != null && dataset2.size() > 0)
            {
                dataset.addAll(dataset2);
            }

            IDataset dataset1 = AcctInfoQry.getALLAcctInfoByUserID(user_id, payrelation_query_type, pagination);
            if (dataset1 != null && dataset1.size() > 0)
            {
                dataset.addAll(dataset1);
            }

            // IDataset dataset1 = new DatasetList();
            // IData data1 = new DataMap();
            // data1.put("SERIAL_NUMBER_A", "13467559999");
            // data1.put("SERIAL_NUMBER_B", "15084797777");
            // data1.put("ACCT_ID", "111111111111111");
            // data1.put("PAYITEM_CODE", "123");
            // data1.put("LIMIT", "100");
            // data1.put("START_CYCLE_ID", "201011");
            // data1.put("END_CYCLE_ID", "20131212");
            // data1.put("UPDATE_TIME", "2013-02-26 23:59:58");
            // data1.put("UPDATE_STAFF_ID", "SUPERUSR");
            // data1.put("UPDATE_DEPART_ID", "00000");
            //
            // dataset1.add(data1);
            // dataset.addAll(dataset1);

        }
        else
        {
            IData datas = UcaInfoQry.qryAcctInfoByUserId(inparams.getString("USER_ID"));
            if (datas != null && datas.size() > 0)
            {
                String acctid = datas.getString("ACCT_ID", "");

                IDataset dataset2 = AcctInfoQry.getALLAcctInfoByAcctIDForCG(acctid, payrelation_query_type, pagination);

                if (dataset2 != null && dataset2.size() > 0)
                {
                    dataset.addAll(dataset2);
                }

                IDataset dataset1 = AcctInfoQry.getALLAcctInfoByAcctID(acctid, payrelation_query_type, pagination);
                if (dataset1 != null && dataset1.size() > 0)
                {
                    dataset.addAll(dataset1);
                }

            }
            // IDataset dataset1 = new DatasetList();
            // IData data1 = new DataMap();
            // data1.put("SERIAL_NUMBER_A", "13467559999");
            // data1.put("SERIAL_NUMBER_B", "15084797777");
            // data1.put("ACCT_ID", "111111111111111");
            // data1.put("PAYITEM_CODE", "123");
            // data1.put("LIMIT", "100");
            // data1.put("START_CYCLE_ID", "201011");
            // data1.put("END_CYCLE_ID", "20131212");
            // data1.put("UPDATE_TIME", "2013-02-26 23:59:58");
            // data1.put("UPDATE_STAFF_ID", "SUPERUSR");
            // data1.put("UPDATE_DEPART_ID", "00000");
            //
            // dataset1.add(data1);
            // dataset1.add(data1);
            // dataset1.add(data1);
            // dataset1.add(data1);
            // dataset1.add(data1);
            // dataset1.add(data1);
            // dataset.addAll(dataset1);
        }
        return dataset;
    }

    /**
     * 根据账户标识获取普通付费关系数量
     * 
     * @author liaoyi 2010-4-8
     * @param inparams
     *            查询所需参数(ACCT_ID)
     * @return IDataset 默认帐户资料
     * @throws Exception
     */
    public static IDataset getNorPayRelationByAcctID(String acctId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ACCT_ID", acctId);
        return Dao.qryByCode("TD_S_CPARAM", "ExistPayRelationNor", inparams, pagination);
    }

    /**
     * 查询明细账目(高级付费关系查询)
     * 
     * @param inparams
     *            输入参数(PAYITEM_CODE,EPARCHY_CODE)
     * @return IDataset 明细账目集
     * @throws Exception
     * @author chenhao 2009-2-19
     */
    public static IDataset getPayItemDetail(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_DETAILITEM", "SEL_DETAIL_BY_PAYITEMCODE", inparams, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 判断tf_a_payrelation,根据USER_ID, PARTITION_ID, PAYITEM_CODE,一个用户同一个付费帐目只能有一条记录当前生效
     */
    public static Boolean getPayrelaAdv(String userId, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT * FROM tf_a_payrelation WHERE 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID  ");
        parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL(" AND PAYITEM_CODE=:PAYITEM_CODE  ");
        parser.addSQL(" AND start_cycle_id<=end_cycle_id AND end_cycle_id>= to_char(SYSDATE,'yyyymmdd')");
        IDataset results = Dao.qryByParse(parser, pagination);
        if (results != null && results.size() > 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 判断tf_a_payrelation,根据USER_ID, PARTITION_ID, PAYITEM_CODE,一个用户同一个付费帐目只能有一条记录当前生效
     */
    public static IDataset getPayrelaByPayItemCode(String userId, String payitemCode) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("PAYITEM_CODE", payitemCode);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT * FROM tf_a_payrelation WHERE 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID  ");
        parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL(" AND PAYITEM_CODE=:PAYITEM_CODE  ");
        parser.addSQL(" AND start_cycle_id<=end_cycle_id AND end_cycle_id>= to_char(SYSDATE,'yyyymmdd')");
        IDataset results = Dao.qryByParse(parser);
        return results;
    }
    /**
     * k3
     *查询失效的付费关系
     * @param userId
     * @param payitemCode
     * @return
     * @throws Exception
     */
    public static IDataset qryEndPayrelaByPayItemCode(String userId, String payitemCode) throws Exception
    {
    	
    	IData inparams = new DataMap();
    	inparams.put("USER_ID", userId);
    	inparams.put("PAYITEM_CODE", payitemCode);
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL("SELECT * FROM tf_a_payrelation WHERE 1=1 ");
    	parser.addSQL(" AND USER_ID=:USER_ID  ");
    	parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID),10000)  ");
    	parser.addSQL(" AND PAYITEM_CODE=:PAYITEM_CODE  ");
    	parser.addSQL(" AND end_cycle_id< to_char(SYSDATE,'yyyymmdd')");
    	IDataset results = Dao.qryByParse(parser);
    	return results;
    }

    public static IDataset getPayRelaBySelbyAcctDa1(String acctId, String defaultTag, String actTag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", actTag);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA_1", param, pagination);
    }

    public static IDataset getPayRelaBySelUserValidDefault(String acct_id) throws Exception
    {

        IData param = new DataMap();
        param.put("ACCT_ID", acct_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_VALID_DEFAULT", param);
    }

    public static IDataset getPayRelaByUserEffected(IData inparams, String routeId) throws Exception
    {

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_EFFECTED", inparams, routeId);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelaByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER", param);
    }

    /**
     * 作用：根据账户ID、USERID查询所有字段 SEL_ALL_ACCTID_USERID
     * 
     * @author luojh 2010-08-04
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayRelaByUserIDA(IData inparams, String routeId) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_USERIDA", inparams, routeId);
    }

    /**
     * 作用：根据账户ID、USERID查询所有字段 SEL_ALL_ACCTID_USERID
     * 
     * @author luojh 2010-08-04
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayRelaByUserIDAndAcctId(String userId, String acctId, String routeId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_ACCTID_USERID", inparams, pagination, routeId);
    }

    /**
     * 作用：根据账户ID、USER_ID、ACT_TAG查询所有字段 SEL_ALL_ACCTID_USERID_ACTTAG
     * 
     * @author luojh 2010-08-04
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayRelaByUserIDAndAcctIdActTag(String userId, String routeId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_ACCTID_USERID_ACTTAG", inparams, pagination, routeId);
    }

    /**
     * 作用：根据账户ID、USER_ID、DEFAULT_TAG查询所有字段 SEL_ALL_ACCTID_USERID_DEFAULTTAG
     * 
     * @author luojh 2010-08-04
     * @param inparams
     *            查询所需参数(USER_ID)
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayRelaByUserIDAndAcctIdDefaultTag(String userId, String acctId, String actTag, Pagination pagination, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);
        inparams.put("ACT_TAG", actTag);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_ACCTID_USERID_DEFAULTTAG", inparams, pagination, routeId);
    }

    public static IDataset getPayRelaByUserNow(String userId, Pagination pagination, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_NOW", inparams, pagination, routeId);
    }

    public static IDataset getPayRelaByUserNowCancel(IData inparams, String routeId) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_CANCEL", inparams, routeId);
    }

    /**
     * 高级付费查询
     * 
     * @author tengg
     * @date 2009-5-13
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelatByUidAid(String acctId, String defaultTag, String payitemCode,String user_id,Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("ACCT_ID", acctId);
        params.put("DEFAULT_TAG", defaultTag);
        params.put("PAYITEM_CODE", payitemCode);
        params.put("USER_ID", user_id);
        
        IDataset dataset = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_UID_AID", params, pagination);
        return dataset;
    }

    public static IDataset getPayRelatInfoByAcctIdActTagDefaultTag(String acct_id, String default_tag, String act_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acct_id);
        param.put("DEFAULT_TAG", default_tag);
        param.put("ACT_TAG", act_tag);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_DISTINCT_PAYRELATION", param);
    }

    public static IDataset getPayRelatInfoByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_PAYRELATION_BY_USER", param);
    }

    public static IDataset getPayRelatInfoByUserIdNow(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_NOW", param);
    }

    public static IDataset getPayRelatInfoByUserIdNow2(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_NOW2", param);
    }

    public static IDataset getPayRelatInfoByUserIdVALID2(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_SPECIALEPAY_VALID2", param);
    }

    /**
     * 获取用户付费关系资料信息 superLimit使用
     * 
     * @author GaoYuan PageData 页面对象
     * @param inData
     *            IData 规则参数
     * @return IData
     * @throws Exception
     */
    public static IData getPayRelation(IData inData) throws Exception
    {
        IData payRelation = UcaInfoQry.qryDefaultPayRelaByUserId(inData.getString("ID"));

        if (IDataUtil.isEmpty(payRelation))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_591);
        }

        return payRelation;
    }

    /**
     * 主键查询payitem
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayrelationbyAcct(String userId, String acctId, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND default_tag = '0' ");
        parser.addSQL(" AND acct_id = :ACCT_ID ");
        parser.addSQL(" AND act_tag = '1' ");

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            parser.addSQL(" AND end_cycle_id>=TO_CHAR(SYSDATE,'YYYYMMDD') ");
            parser.addSQL(" AND end_cycle_id>=start_cycle_id ");
        }
        else
        {
            // if(AppUtil.getProvinceId().equals("SHXI")&&"true".equals(DiversifyAcctUtil.getJudeAcctDayTag()))
            if (ProvinceUtil.isProvince(ProvinceUtil.SHXI))
            {
                parser.addSQL(" AND end_cycle_id>TO_CHAR(SYSDATE,'YYYYMMDD') ");
            }
            else
            {
                parser.addSQL(" AND end_cycle_id>TO_CHAR(SYSDATE,'YYYYMMDD') ");
            }
            parser.addSQL(" AND end_cycle_id>start_cycle_id ");
        }
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 主键查询payitem
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPayrelationbyUserIda(String userId, String userIdA, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("USER_ID_A", userIdA);

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND default_tag = '0' ");
        parser.addSQL(" AND RSRV_STR1 = :USER_ID_A ");
        parser.addSQL(" AND act_tag = '1' ");
        parser.addSQL(" AND end_cycle_id>TO_CHAR(SYSDATE,'YYYYMMDD') ");
        parser.addSQL(" AND end_cycle_id>start_cycle_id ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 作用：根据账户ID、USERID查询记录中的所有字段
     * 
     * @param acctId
     *            集团用户付费账户
     * @param mebUserId
     *            成员用户标识
     * @return
     * @throws Exception
     */
    public static IDataset getPayrelationByUserIdAndAcctId(String acctId, String mebUserId, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("USER_ID", mebUserId);
        param.put("DEFAULT_TAG", "0");// 非默认账户

        IDataset iDataset = Dao.qryByCode("TF_A_PAYRELATION", "SEL_USERID_ACCTID_EXISTS_ALL_PAY", param, page);

        return iDataset;
    }

    /**
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelationByUserIDandStr1(String userId, String defaultTag, String rsrvStr1, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("DEFAULT_TAG", defaultTag);
        inparams.put("RSRV_STR1", rsrvStr1);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BYUSERID_STR1", inparams, pagination);
    }

    /**
     * 根据tab_name,eparchy_code查询用户信息
     * 
     * @param inparams
     *            查询所与参数
     * @param eparchyCode
     *            地市
     * @return 查询结果
     * @throws Exception
     * @author ykx
     */
    public static IDataset getPayrelationInfo(String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_NOW", inparams);
    }

    /**
     * 根据tab_name,,eparchy_code查询用户信息
     * 
     * @param inparams
     *            查询参数
     * @param page
     *            分页参数
     * @return 查询结果
     * @throws Exception
     * @author ykx
     */
    public static IDataset getPayrelationInfo(String userId, String defaultTag, Pagination page) throws Exception
    {
        CSAppException.apperr(TradeException.CRM_TRADE_95,
                "[AILK/j2ee/crm/hnan/apps/csserv/src-service/com/ailk/csservice/common/query/rela/PayRelaInfoQry.java][873][禁用TF_A_PAYRELATION.SEL_BY_USER_MAX，统一使用BizQuery.getLastDefaultPayRelationByUserId方法，该方法从全局缓存查询数据，能减少数据库访问，提高查询效率]");
        return null;
        /*
         * IData inparams = new DataMap(); inparams.put("USER_ID", userId); inparams.put("DEFAULT_TAG", defaultTag);
         * return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER_1MAX", inparams, page);
         */
    }

    public static IDataset getPayrelationInfoForGrp(IData inparams) throws Exception
    {

        // CSAppException.apperr(TradeException.CRM_TRADE_95, "sqlRef作为参数被勾住了，不能使用该方法。请修改！");
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_VALID", inparams, Route.CONN_CRM_CG);
    }

    public static IDataset getPayrelationInfoForGrpByUU(String userIda, String relaTypeCode, String acctId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userIda);
        params.put("RELATION_TYPE_CODE", relaTypeCode);
        params.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_BY_UU", params, Route.CONN_CRM_CG);
    }

    /**
     * @Description:根据user_id、payitem_code查询付费信息
     * @author wusf
     * @date 2010-2-4
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelByUserIdPayCode(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USERID_PAYCODE", inparams);
    }

    /**
     * @Description:查询本月终止的付费关系
     * @author wusf
     * @date 2010-2-5
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPayRelThisMonthEnd(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_PAYREL_THISMONTH_END", inparams);
    }

    public static IDataset getPreAdvPayrelaByUserNow(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_PREADVPAYRELA_BY_USER_NOW", param);
    }

    /**
     * 主键查询payrelation
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPyrlByPk(IData inparam, Pagination page) throws Exception
    {
        return Dao.qryByCodeParser("TF_A_PAYRELATION", "SEL_BY_PK", inparam, page);
    }

    /**
     * 主键查询payrelation
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getPyrlByPk(String userId, String acctId, String payitemCode, String start_cycle_id, Pagination page) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);
        inparams.put("PAYITEM_CODE", payitemCode);
        inparams.put("START_CYCLE_ID", start_cycle_id);

        return Dao.qryByCodeParser("TF_A_PAYRELATION", "SEL_BY_PK", inparams, page);
    }

    public static IDataset getPyrlByPkLLCycle(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_PK_LLCYCLE", inparams);
    }

    /**
     * 主键查询specialepay
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getSpecPayByPk(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_PK", inparams);
    }

    public static IDataset getSpecPayByUserId(String usrtId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", usrtId);
        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_CHEK_BY_ID", param);
    }

    /**
     * 主键查询specialepay
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getSpecPayByUserIdA(String userId, String userida) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("USER_ID_A", userida);

        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_USERIDA", inparams);
    }

    // add by zhaoyi@2011-01-04 start----------------
    public static IDataset getSpecPayByUserIdALLCycle(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_USERIDA_LLCYCLE", inparams);
    }

    /**
     * 获得正常用户信息通过服务号码
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author chenhao 2009-4-3
     */
    public static IData getUserInfoBySn(IData inparams) throws Exception
    {

        IData ids = UcaInfoQry.qryUserInfoBySn(inparams.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_282);
        }
        IData idata = ids;
        return idata;
        /*
         * inparams.put("SQL_", "SEL_BY_SN"); inparams.put("NET_TYPE_CODE", "00"); inparams.put("REMOVE_TAG", "0");
         * IDataset dataset = UserInfoQry.getUserInfo(inparams); if (dataset != null && dataset.size() > 0) { return
         * dataset.getData(0); } else { CSAppException.apperr(BizException.CRM_BIZ_103 CrmUserException.CRM_USER_811); }
         * return null;
         */
    }

    /**
     * 根据相关信息查询还有效的付费关系
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getValidPayRelaByEndCycleId(String userId, String defaultTag, String actTag, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", actTag);
        param.put("END_CYCLE_ID", endCycleId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_UPD_NOR_END", param);
    }

    /**
     * 根据USER_ID获取默认有效用户付费关系信息【包括预约付费关系信息】
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getValidUserPayByUserId(IData inparams) throws Exception
    {
        // TODO
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_VALID_DEFAULT_BY_USERID", inparams);
    }

    /**
     * 检测是否独立帐户，如果返回1，则表示独立帐户，如果返回大于1，则不是独立帐户
     * 
     * @author chenzm
     * @param acct_id
     * @param default_tag
     * @param act_tag
     * @return String
     * @throws Exception
     */
    public static String isAcctOnly(String acct_id, String default_tag, String act_tag) throws Exception
    {

        int count = 0;

        IData inParams = new DataMap();
        inParams.put("ACCT_ID", acct_id);
        inParams.put("DEFAULT_TAG", default_tag);
        inParams.put("ACT_TAG", act_tag);

        IDataset dataset = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", inParams);

        if (dataset.size() == 0)
        {
            CSAppException.apperr(FeeException.CRM_FEE_80);
        }
        else
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                String userId = dataset.getData(i).getString("USER_ID");
                IData temp = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
                if (IDataUtil.isNotEmpty(temp) && "0".equals(temp.getString("REMOVE_TAG")))
                {
                    count = count + 1;
                }
            }
        }

        if (count == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_227);
        }

        return count == 1 ? "true" : "false";
    }

    /**
     * 通过服务号码获得正常用户的USER_ID,将其放到inparams参数中 ,作为下次查询的条件
     * 
     * @param inparams
     * @throws Exception
     * @author chenhao 2009-4-3
     */
    public static void putUserIdIntoParamBySn(IData inparams) throws Exception
    {

        CSAppException.apperr(TradeException.CRM_TRADE_95, "sqlRef作为参数被勾住了，不能使用该方法。请修改！");

        /*
         * inparams.put("SQL_RE", "SEL_BY_SN"); inparams.put("NET_TYPE_CODE", "00"); inparams.put("REMOVE_TAG", "0");
         * IDataset dataset = UserInfoQry.getUserInfo(inparams); if (dataset != null && dataset.size() > 0) {
         * inparams.put("USER_ID", ((IData) dataset.get(0)).getString("USER_ID", "")); } else {
         * CSAppException.apperr(BBizException.CRM_BIZ_103CrmUserException.CRM_USER_811); }
         */
    }

    /*
     * vpmn账户信息
     */
    public static IDataset qryAcctInfo(String userId, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT a.pay_name pay_name,a.pay_mode_code pay_mode_code ");
        parser.addSQL(" FROM   tf_a_payrelation p, tf_f_account a ");
        parser.addSQL(" WHERE  a.acct_id = TO_NUMBER(p.acct_id)  ");
        parser.addSQL("       AND p.act_tag='1' AND p.act_tag='1' ");
        parser.addSQL("       AND p.user_id=to_number(:USER_ID) AND p.partition_id=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL("       AND p.start_cycle_id <= p.end_cycle_id ");
        parser.addSQL("       AND p.end_cycle_id=  ");
        parser.addSQL("       (SELECT max(a.end_cycle_id) FROM tf_a_payrelation a  ");
        parser.addSQL("       WHERE a.user_id = TO_NUMBER(:USER_ID)  ");
        parser.addSQL("       AND a.partition_id=mod(TO_NUMBER(:USER_ID),10000)  ");
        // parser.addSQL(" AND a.default_tag = '1' AND p.act_tag='1' ");
        parser.addSQL("       AND p.start_cycle_id <= p.end_cycle_id)   ");

        IDataset resIds = Dao.qryByParse(parser, page);

        return resIds;
    }

    /**
     * 根据账户ID查询状态正常的默认付费用户
     * 
     * @param acct_id
     * @param default_tag
     * @param act_tag
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryAcctValidUserInfos(String acct_id, String default_tag, String act_tag) throws Exception
    {
        IDataset retSet = new DatasetList();

        IData inParams = new DataMap();
        inParams.put("ACCT_ID", acct_id);
        inParams.put("DEFAULT_TAG", default_tag);
        inParams.put("ACT_TAG", act_tag);
        IDataset dataset = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", inParams);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(FeeException.CRM_FEE_80);
        }
        else
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                String userId = dataset.getData(i).getString("USER_ID");
                IData temp = UcaInfoQry.qryUserInfoByUserId(userId);
                if (IDataUtil.isNotEmpty(temp) && "0".equals(temp.getString("REMOVE_TAG")))
                {
                    retSet.add(temp);
                }
            }
        }

        if (retSet.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_227);
        }

        return retSet;
    }

    public static IDataset qryGrpAcctPayrelatinByMebUserId(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_GRPACCT_USERID", param, routeId);
    }

    public static IDataset qryGrpSpecialPayByCustId(String custId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_SPECIALPAY_BY_CUSTID", param, pg);
    }

    /**
     * 查询统一付费产品信息
     * 
     * @param custId
     * @param acctId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpSpecialPayByCustIdAcctId(String custId, String acctId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("CUST_ID", custId);
        param.put("ACCT_ID", acctId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_SPECIALPAY_BY_CUSTID_ACCTID", param, pg);
    }

    public static IDataset qryGrpSpecialPayByCustIdAcctIdProductId(String custId, String acctId, String productId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("CUST_ID", custId);
        param.put("ACCT_ID", acctId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_SPECIALPAY_BY_CUSTID_ACCTID_PRODUCTID", param, pg);
    }

    public static IDataset qryGrpSpecialPayBySerialNumber(String serialNumber, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_SPECIALPAY_BY_SN", param, pg);
    }

    // 查询付费名称
    public static IDataset qryPayName(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.* ");
        parser.addSQL(" from tf_f_account a,tf_a_payrelation b ");
        parser.addSQL(" where a.acct_id=b.acct_id and b.default_tag = '1' and b.act_tag = '1' and to_char(sysdate,'yyyymmdd') between  b.start_cycle_id and b.end_cycle_id and rownum <2 ");
        parser.addSQL(" and b.partition_id=mod(to_number(:USER_ID),10000) ");
        parser.addSQL(" and b.user_id=to_number(:USER_ID) ");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset qryPayRealInfoByItemAndAcctId(String payItem, String acctId) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("PAYITEM_CODE", payItem);
        inParams.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ITEM_AID", inParams);
    }
    /**
     * k3
     * 查询失效的付费关系
     * @param payItem
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset qryEndPayRealByItemAndAcctId(String payItem, String acctId) throws Exception
    {
    	IData inParams = new DataMap();
    	inParams.put("PAYITEM_CODE", payItem);
    	inParams.put("ACCT_ID", acctId);
    	return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ITEM_EXPIRE", inParams);
    }

    /**
     * 根据acctId查询所有的付费关系
     * 
     * @param acctId
     * @param defaultTag
     * @param acctTag
     * @param endCycleId
     * @return
     * @throws Exception
     */
    public static IDataset qryPayRelaByAcctID(String acctId, String defaultTag, String acctTag, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", acctTag);
        param.put("END_CYCLE_ID", endCycleId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID, USER_ID, ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY, ");
        sql.append("TO_CHAR(ADDUP_MONTHS) ADDUP_MONTHS,ADDUP_METHOD,BIND_TYPE, START_CYCLE_ID,END_CYCLE_ID, ");
        sql.append("DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,INST_ID,UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, UPDATE_TIME,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4, ");
        sql.append("RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10 ");
        sql.append("FROM TF_A_PAYRELATION ");
        sql.append("WHERE ACCT_ID = TO_NUMBER(:ACCT_ID) ");
        sql.append("AND DEFAULT_TAG = :DEFAULT_TAG ");
        sql.append("AND ACT_TAG = :ACT_TAG ");
        sql.append("AND END_CYCLE_ID >= :END_CYCLE_ID ");
        sql.append("AND END_CYCLE_ID >= START_CYCLE_ID ");
        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }
    /**
     * 根据acctId查询所有的付费关系
     * 
     * @param acctId
     * @param defaultTag
     * @param acctTag
     * @param endCycleId
     * @return
     * @throws Exception
     */
    public static IDataset qryPayRelaByAcctID2(String acctId, String defaultTag, String acctTag, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", acctTag);
        param.put("END_CYCLE_ID", endCycleId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID, USER_ID, ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY, ");
        sql.append("TO_CHAR(ADDUP_MONTHS) ADDUP_MONTHS,ADDUP_METHOD,BIND_TYPE, START_CYCLE_ID,END_CYCLE_ID, ");
        sql.append("DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,INST_ID,UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, UPDATE_TIME,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4, ");
        sql.append("RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10 ");
        sql.append("FROM TF_A_PAYRELATION ");
        sql.append("WHERE ACCT_ID = TO_NUMBER(:ACCT_ID) ");
        sql.append("AND DEFAULT_TAG = :DEFAULT_TAG ");
        sql.append("AND ACT_TAG = :ACT_TAG ");
        sql.append("AND END_CYCLE_ID > :END_CYCLE_ID ");
        sql.append("AND END_CYCLE_ID >= START_CYCLE_ID ");
        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }

    /**
     * 查询用户付费关系
     * 
     * @param userId
     * @param acctId
     * @param defaultTag
     * @return
     * @throws Exception
     */
    public static IDataset qryPayRelaByUserAcctIdDefTag(String userId, String acctId, String defaultTag) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USERID_ACCTID_DEFTAG", param);
    }

    /**
     * 作用：根据USER_ID查询用户的默认付费账户
     * 
     * @param eparchyCode
     * @param userId
     * @return
     */
    public static IDataset qryPayRelationByUserId(String routeId, String userId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        IDataset idata = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USERID_INTF", inparam, routeId);
        return idata;
    }

    /**
     * 根据用户标识获有效的付费关系[包括正在有效的和未生效的]
     * 
     * @author liuke
     * @return IDataset
     * @throws Exception
     */
    public static IDataset qryValidPayRelationByUserId(String userId, String defaultTag, String actTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", actTag);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, USER_ID, ACCT_ID, PAYITEM_CODE, ACCT_PRIORITY, ");
        sql.append("USER_PRIORITY,ADDUP_MONTHS,ADDUP_METHOD, ");
        sql.append("BIND_TYPE,START_CYCLE_ID, END_CYCLE_ID, DEFAULT_TAG, ");
        sql.append("ACT_TAG, LIMIT_TYPE, LIMIT, COMPLEMENT_TAG, INST_ID, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, UPDATE_TIME,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3 ");
        sql.append("RSRV_STR4, RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10 ");
        sql.append("FROM TF_A_PAYRELATION ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND DEFAULT_TAG = :DEFAULT_TAG ");
        sql.append("AND ACT_TAG = :ACT_TAG ");
        sql.append("AND END_CYCLE_ID > START_CYCLE_ID ");
        sql.append("AND END_CYCLE_ID > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) ");
        return Dao.qryBySql(sql, param);
    }

    public static IDataset queryDefaultPayRelaByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_BY_USERID", param);
    }

    public static String queryLastLastCycle() throws Exception
    {

        SQLParser parser = new SQLParser(new DataMap());
        parser.addSQL("select to_char(add_months(sysdate,-2),'yyyy-mm-dd') OUTSTR from dual");

        IDataset out = Dao.qryByParse(parser);
        return ((IData) out.get(0)).getString("OUTSTR", "");
    }

    /**
     * 查询集团代付费的所有有效成员
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryMemSpecialepay(String userId, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_RELATION_UU_USERID", param);
        return dataset;
    }

    public static IDataset queryNormalPayre(String userId, String acctId, String payItemCode, String defaultTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        param.put("PAYITEM_CODE", payItemCode);
        param.put("DEFAULT_TAG", defaultTag);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_UID_AID", param);
    }

    public static IData queryPayRealion(String acctId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("ACCT_ID", acctId);

        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("SELECT t.* FROM tf_a_payrelation t ");
        parser.addSQL("WHERE  t.end_cycle_id>to_number(to_char(SYSDATE,'yyyymmdd')) ");
        parser.addSQL("AND t.acct_id=:ACCT_ID ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        if (dataset != null && dataset.size() > 0)
        {
            return dataset.getData(0);
        }
        else
        {
            return null;
        }
    }

    public static IDataset queryPayreInfoByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT", param);
    }

    /**
     * liuke 查询晚于传入账期的所有有效付费关系
     * 
     * @param userId
     * @param cycId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAfterPayRelByCycId(String userId, String cycId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACYC_ID", cycId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_NOT_EXPIRE_PAYREL_BY_CYCID", param);
    }

    /**
     * yuezy
     * 
     * @param userId
     * @param cycId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserPayRelation(String userId, String cycId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACYC_ID", cycId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_VALID", param);
    }

    /**
     * liuke
     * 
     * @param userId
     * @param cycId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserValidPay(String userId, String cycId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACYC_ID", cycId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_VALID", param);
    }

    /**
     * liuke
     * 
     * @param userId
     * @param cycId
     * @return
     * @throws Exception
     */
    public static IDataset queryValidPayByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ACCT_VALID_ALL_ACYCID", param);
    }
    
    /**
     * yuezy
     * 
     * @param acctId
     * @param cycId
     * @return
     * @throws Exception
     */
    public static IDataset queryNowValidPayByAcctId(String acctId,String cycId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("ACYC_ID", cycId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ACCT_VALID", param);
    }

    
    public static IDataset getPayRelatInfoAllColsByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_PAYRELATION_COLS_BY_USER", param);
    }
    
    public static IDataset queryAcctValidPayRelation(String acctId, String defaultTag, String actTag) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("DEFAULT_TAG", defaultTag);
        param.put("ACT_TAG", actTag);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", param);
    }
    
    public static IDataset queryAllPayrelationByProductUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset accountList = Dao.qryByCode("TF_A_PAYRELATION", "SEL_ALL_PAYRELA_BY_PRODUCT_USERID", param);
        
        if(IDataUtil.isNotEmpty(accountList)){
        	 for (int row = 0, size = accountList.size(); row < size; row++)
             {
                 IData account = accountList.getData(row);

                 account.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(account.getString("EPARCHY_CODE")));

                 String payModeCode = account.getString("PAY_MODE_CODE");

                 account.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(payModeCode));

                 if (StringUtils.isNotBlank(payModeCode))
                 {
                     account.put("BANK", UBankInfoQry.getBankNameByBankCode(account.getString("BANK_CODE")));
                 }
             }
        }
       
        return accountList;
    }
    
    /**
     * 查询集团统一付费产品的付费关系信息
     * @param serialNumber
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryUnifyPayRelaBySerialNumber(String serialNumber, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_UNIFYPAY_RELA_BY_SN", param, pg);
    }
    
    /**
     * 查询集团统一付费产品的默认账户
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryUnifyAccountByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_UNIFYACCOUNT_BY_USERID", param);
    }
    
    /**
     * 获取集团代付关系的暂停账期
     * @param strDate
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public static String getPayrelationbyEndCycleId(String strDate) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("STR_DATE", strDate);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT to_char(Last_day(add_months(to_date(:STR_DATE, 'yyyy-mm-dd hh24:mi:ss'),-1)), 'yyyymmdd') END_CYCLE_ID  ");
        parser.addSQL(" FROM dual ");
        IDataset ds = Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(ds)){
            return ds.getData(0).getString("END_CYCLE_ID");
        }
        
        return "";
    }
    
    /**
     * 根据个人用户ID和账户ID查询可恢复的代付关系
     * @param userId
     * @param acctId
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public static IDataset getAllPayrelationbyUAForRestart(String userId, String acctId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT PARTITION_ID,USER_ID,ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY,BIND_TYPE,START_CYCLE_ID,END_CYCLE_ID,DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,UPDATE_STAFF_ID,UPDATE_DEPART_ID,INST_ID,UPDATE_TIME,RSRV_STR10  ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND default_tag = '0' ");
        parser.addSQL(" AND acct_id = :ACCT_ID ");
        parser.addSQL(" AND RSRV_STR10 like 'CREDIT_STOP%' ");
        return Dao.qryByParse(parser);
    }
    
    /**
     * 根据集团用户ID和账户ID查询代付号码
     * @param grpUsrId
     * @param acctId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-6
     */
    public static IDataset getAdvPayRelaByGrpUserIdAndAcctId(String grpUsrId, String acctId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", grpUsrId);
        inparams.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT U.SERIAL_NUMBER,														"));
        sql.append(SqlUtil.trimSql("       A.PARTITION_ID,                                                      "));
        sql.append(SqlUtil.trimSql("       A.USER_ID,                                                           "));
        sql.append(SqlUtil.trimSql("       A.USER_ID_A,                                                         "));
        sql.append(SqlUtil.trimSql("       A.ACCT_ID,                                                           "));
        sql.append(SqlUtil.trimSql("       A.ACCT_ID_B,                                                         "));
        sql.append(SqlUtil.trimSql("       A.PAYITEM_CODE,                                                      "));
        sql.append(SqlUtil.trimSql("       A.START_CYCLE_ID,                                                    "));
        sql.append(SqlUtil.trimSql("       A.END_CYCLE_ID,                                                      "));
        sql.append(SqlUtil.trimSql("       A.BIND_TYPE,                                                         "));
        sql.append(SqlUtil.trimSql("       A.LIMIT_TYPE,                                                        "));
        sql.append(SqlUtil.trimSql("       A.LIMIT,                                                             "));
        sql.append(SqlUtil.trimSql("       A.COMPLEMENT_TAG,                                                    "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR1,                                                         "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR2,                                                         "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR3,                                                         "));
        sql.append(SqlUtil.trimSql("       A.UPDATE_STAFF_ID,                                                   "));
        sql.append(SqlUtil.trimSql("       A.UPDATE_DEPART_ID,                                                  "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,         "));
        sql.append(SqlUtil.trimSql("       A.INST_ID                                                            "));
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER_SPECIALEPAY A, TF_F_USER U                                 "));
        sql.append(SqlUtil.trimSql(" WHERE A.PARTITION_ID = U.PARTITION_ID                                      "));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID = U.USER_ID                                                "));
        sql.append(SqlUtil.trimSql("   AND U.REMOVE_TAG = '0'                                                   "));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID_A = :USER_ID                                               "));
        sql.append(SqlUtil.trimSql("   AND A.ACCT_ID = :ACCT_ID                                                 "));
        sql.append(SqlUtil.trimSql("   AND TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) BETWEEN A.START_CYCLE_ID AND "));
        sql.append(SqlUtil.trimSql("       A.END_CYCLE_ID                                                       "));
        sql.append(SqlUtil.trimSql("   AND A.START_CYCLE_ID <= A.END_CYCLE_ID                                   "));
        return Dao.qryBySql(sql, inparams);
    }
    /**
     * 根据集团用户ID和账户ID查询可恢复的代付关系
     * @param grpUsrId
     * @param acctId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public static IDataset getAdvPayRelaForRestart(String grpUsrId, String acctId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", grpUsrId);
        inparams.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT U.SERIAL_NUMBER,													"));
        sql.append(SqlUtil.trimSql("       A.PARTITION_ID,                                                  "));
        sql.append(SqlUtil.trimSql("       A.USER_ID,                                                       "));
        sql.append(SqlUtil.trimSql("       A.USER_ID_A,                                                     "));
        sql.append(SqlUtil.trimSql("       A.ACCT_ID,                                                       "));
        sql.append(SqlUtil.trimSql("       A.ACCT_ID_B,                                                     "));
        sql.append(SqlUtil.trimSql("       A.PAYITEM_CODE,                                                  "));
        sql.append(SqlUtil.trimSql("       A.START_CYCLE_ID,                                                "));
        sql.append(SqlUtil.trimSql("       A.END_CYCLE_ID,                                                  "));
        sql.append(SqlUtil.trimSql("       A.BIND_TYPE,                                                     "));
        sql.append(SqlUtil.trimSql("       A.LIMIT_TYPE,                                                    "));
        sql.append(SqlUtil.trimSql("       A.LIMIT,                                                         "));
        sql.append(SqlUtil.trimSql("       A.COMPLEMENT_TAG,                                                "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR1,                                                     "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR2,                                                     "));
        sql.append(SqlUtil.trimSql("       A.RSRV_STR3,                                                     "));
        sql.append(SqlUtil.trimSql("       A.UPDATE_STAFF_ID,                                               "));
        sql.append(SqlUtil.trimSql("       A.UPDATE_DEPART_ID,                                              "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,     "));
        sql.append(SqlUtil.trimSql("       A.INST_ID                                                        "));
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER_SPECIALEPAY A, TF_F_USER U                             "));
        sql.append(SqlUtil.trimSql(" WHERE A.PARTITION_ID = U.PARTITION_ID                                  "));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID = U.USER_ID                                            "));
        sql.append(SqlUtil.trimSql("   AND U.REMOVE_TAG = '0'                                               "));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID_A = :USER_ID                                           "));
        sql.append(SqlUtil.trimSql("   AND A.ACCT_ID = :ACCT_ID                                             "));
        sql.append(SqlUtil.trimSql("   AND A.RSRV_STR2 LIKE 'CREDIT_STOP%'                                  "));
        return Dao.qryBySql(sql, inparams);
    }
    /**
     * 查询已被集团信控发起暂停集团代付关系的集团产品用户
     * @param groupId
     * @param groupSn
     * @param pagination
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-1
     */
    public static IDataset getCreditStopPayRelInfoByCond(String groupId, String groupSn, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("GROUP_ID", groupId);
        inparam.put("SERIAL_NUMBER", groupSn);
        SQLParser sql = new SQLParser(inparam);
        sql.addSQL("SELECT A.GROUP_ID, A.CUST_NAME, B.USER_ID, B.SERIAL_NUMBER, C.PRODUCT_ID");
        sql.addSQL("  FROM TF_F_CUST_GROUP                              A,");
        sql.addSQL("       TF_F_USER                                    B,");
        sql.addSQL("       TF_F_USER_PRODUCT                            C,");
        sql.addSQL("       UCR_ACT1.TF_O_PAYRELATION_SIGN@DBLNK_ACTDBN1 D");
        sql.addSQL(" WHERE A.CUST_ID = B.CUST_ID");
        sql.addSQL("   AND B.USER_ID = C.USER_ID");
        sql.addSQL("   AND C.USER_ID = D.USER_ID");
        sql.addSQL("   AND A.GROUP_ID = :GROUP_ID");
        sql.addSQL("   AND B.SERIAL_NUMBER = :SERIAL_NUMBER");
        sql.addSQL("   AND B.RSRV_STR10 = 'CRDIT_STOP'");
        sql.addSQL("   AND D.ACT_TAG = '0'");
        sql.addSQL("   AND A.REMOVE_TAG = '0'");
        sql.addSQL("   AND B.REMOVE_TAG = '0'");
        sql.addSQL("   AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE");
        IDataset ids = Dao.qryByParse(sql, pagination);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData each = ids.getData(i);
            OfferCfg offerCfg = OfferCfg.getInstance(each.getString("PRODUCT_ID",""), "P");
    		if(offerCfg != null){
    			each.put("PRODUCT_NAME", offerCfg.getOfferName());
    			each.put("BRAND_CODE", offerCfg.getBrandCode());
    			each.put("PRODUCT_EXPLAIN", offerCfg.getDescription());
    		}
        }
        return ids;
    }
    /**
     * 是否订购了集团工作手机成员产品（产品编码：601301）
     * @param userId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-9
     */
    public static IDataset qryUserGpwpPrdtInfo(String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT *"));
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER_PRODUCT A"));
        sql.append(SqlUtil.trimSql(" WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)"));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID = :USER_ID"));
        sql.append(SqlUtil.trimSql("   AND A.PRODUCT_ID = 601301"));
        sql.append(SqlUtil.trimSql("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE"));
        return Dao.qryBySql(sql, inparams);
    }
    /**
     * 去账户ID对应个和商务融合产品统付关系
     * @param acctId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-12
     */
    public static IDataset queryUserPgPayreByAcctId(String acctId) throws Exception
    {
    	 IData inparams = new DataMap();
         inparams.put("ACCT_ID", acctId);
         StringBuilder sql = new StringBuilder();
         sql.append("SELECT A.PARTITION_ID,");
         sql.append("       A.USER_ID,");
         sql.append("       A.ACCT_ID,");
         sql.append("       A.PAYITEM_CODE,");
         sql.append("       A.ACCT_PRIORITY,");
         sql.append("       A.USER_PRIORITY,");
         sql.append("       A.ADDUP_MONTHS,");
         sql.append("       A.ADDUP_METHOD,");
         sql.append("       A.BIND_TYPE,");
         sql.append("       A.DEFAULT_TAG,");
         sql.append("       A.ACT_TAG,");
         sql.append("       A.LIMIT_TYPE,");
         sql.append("       A.LIMIT,");
         sql.append("       A.COMPLEMENT_TAG,");
         sql.append("       A.INST_ID,");
         sql.append("       A.START_CYCLE_ID,");
         sql.append("       A.END_CYCLE_ID,");
         sql.append("       A.UPDATE_TIME,");
         sql.append("       A.UPDATE_STAFF_ID,");
         sql.append("       A.UPDATE_DEPART_ID,");
         sql.append("       A.REMARK,");
         sql.append("       A.RSRV_STR1,");
         sql.append("       A.RSRV_STR2,");
         sql.append("       A.RSRV_STR3,");
         sql.append("       A.RSRV_STR4,");
         sql.append("       A.RSRV_STR5,");
         sql.append("       A.RSRV_STR6,");
         sql.append("       A.RSRV_STR7,");
         sql.append("       A.RSRV_STR8,");
         sql.append("       A.RSRV_STR9,");
         sql.append("       A.RSRV_STR10");
         sql.append("  FROM TF_A_PAYRELATION A");
         sql.append(" WHERE A.ACCT_ID = :ACCT_ID");
         sql.append("   AND A.PAYITEM_CODE = '41000'");
         sql.append("   AND A.RSRV_STR9 = 'PGPAY'");
         sql.append("   AND A.END_CYCLE_ID > TO_CHAR(LAST_DAY(SYSDATE), 'yyyymmdd')");

         return Dao.qryBySql(sql, inparams);
    }
    /**
     * 取可以恢复的统付关系
     * @param acctId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-12
     */
    public static IDataset queryUserPgPayreForRestartByAcctId(String acctId) throws Exception
    {
    	 IData inparams = new DataMap();
         inparams.put("ACCT_ID", acctId);
         StringBuilder sql = new StringBuilder();
         sql.append("SELECT A.PARTITION_ID,");
         sql.append("       A.USER_ID,");
         sql.append("       A.ACCT_ID,");
         sql.append("       A.PAYITEM_CODE,");
         sql.append("       A.ACCT_PRIORITY,");
         sql.append("       A.USER_PRIORITY,");
         sql.append("       A.ADDUP_MONTHS,");
         sql.append("       A.ADDUP_METHOD,");
         sql.append("       A.BIND_TYPE,");
         sql.append("       A.DEFAULT_TAG,");
         sql.append("       A.ACT_TAG,");
         sql.append("       A.LIMIT_TYPE,");
         sql.append("       A.LIMIT,");
         sql.append("       A.COMPLEMENT_TAG,");
         sql.append("       A.INST_ID,");
         sql.append("       A.START_CYCLE_ID,");
         sql.append("       A.END_CYCLE_ID,");
         sql.append("       A.UPDATE_TIME,");
         sql.append("       A.UPDATE_STAFF_ID,");
         sql.append("       A.UPDATE_DEPART_ID,");
         sql.append("       A.REMARK,");
         sql.append("       A.RSRV_STR1,");
         sql.append("       A.RSRV_STR2,");
         sql.append("       A.RSRV_STR3,");
         sql.append("       A.RSRV_STR4,");
         sql.append("       A.RSRV_STR5,");
         sql.append("       A.RSRV_STR6,");
         sql.append("       A.RSRV_STR7,");
         sql.append("       A.RSRV_STR8,");
         sql.append("       A.RSRV_STR9,");
         sql.append("       A.RSRV_STR10");
         sql.append("  FROM TF_A_PAYRELATION A");
         sql.append(" WHERE A.ACCT_ID = :ACCT_ID");
         sql.append("   AND A.PAYITEM_CODE = '41000'");
         sql.append("   AND A.RSRV_STR9 = 'PGPAY'");
         sql.append("   AND A.RSRV_STR10 = 'PGCREDIT_STOP'");

         return Dao.qryBySql(sql, inparams);
    }
    
    /**
     * 查询用户已生效或将生效的付费关系
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getPayrelaByUserId(String userId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT * FROM tf_a_payrelation WHERE 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID  ");
        parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL(" AND PAYITEM_CODE=:PAYITEM_CODE  ");
        parser.addSQL(" AND start_cycle_id<=end_cycle_id AND end_cycle_id>= to_char(SYSDATE,'yyyymmdd')");
        IDataset results = Dao.qryByParse(parser);
        return results;
    }

    public static IDataset getPayrelaByUserbIdAndUseraAcctid(String userId, String acctid) throws Exception {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctid);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT partition_id,INST_ID,to_char(user_id) user_id,to_char(acct_id) acct_id,inst_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,to_char(limit) limit,complement_tag,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time FROM TF_A_PAYRELATION WHERE 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID  ");
        parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL(" AND ACCT_ID = :ACCT_ID ");
        parser.addSQL(" AND DEFAULT_TAG = '0'");
        parser.addSQL(" AND (TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN START_CYCLE_ID AND END_CYCLE_ID OR (TO_CHAR(SYSDATE, 'YYYYMMDD') < END_CYCLE_ID))");
        IDataset results = Dao.qryByParse(parser);
        return results;
    }

}
