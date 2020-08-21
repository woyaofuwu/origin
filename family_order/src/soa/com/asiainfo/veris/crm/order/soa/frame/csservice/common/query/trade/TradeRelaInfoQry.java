
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeRelaInfoQry extends CSBizBean
{

    public static IDataset getAllMebByUerIdARela(String userIdA, String relationTypeCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", userIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);

        return Dao.qryByCodeAllCrm("TF_B_TRADE_RELATION", "SEL_BY_USER_IDA", inparam, true);
    }

    /**
     * 根据tradeId查询所有的用户关系备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakUURelaByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_RELATION_UU_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    // todo
    /**
     * @return
     * @throws Exception
     */
    public static IDataset getBBOSSTradeUU(String USER_ID_A, String USER_ID_B, String conName) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode(conName);
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("USER_ID_B", USER_ID_B);

        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            sp.addSQL(" and a.trade_type_code in ('4694','4695','4697')");
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.XINJ))
        {
            sp.addSQL(" and a.trade_type_code in ('2904','2905','2907')");
        }
        else
        {
            sp.addSQL(" and a.trade_type_code in ('3514','3515','3517')");
        }
        sp.addSQL(" and b.user_id_a=:USER_ID_A");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and a.update_staff_id='IBOSS000'");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, conName);
    }

    // todo
    /**
     * @Description:根据商品USER_ID 查找 relationUU台帐中用户和商品关系是否存在
     * @author hud
     * @date
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchTradeRelationUU(String userId, String men_user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID_A", userId);
        param.put("USER_ID_B", men_user_id);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/  a.trade_id ");
        parser.addSQL(" from tf_b_trade_relation a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id ");
        parser.addSQL(" AND a.user_id_a = :USER_ID_A");
        parser.addSQL(" AND a.user_id_b = :USER_ID_B");
        parser.addSQL(" AND a.MODIFY_TAG = '0'");
        parser.addSQL(" and b.exec_time <= sysdate");
        parser.addSQL(" AND rownum = 1");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    // todo
    public static IDataset getNoFiniTradeUUbySnB(String USER_ID_A, String SERIAL_NUMBER_B, String conName) throws Exception
    {

        // getVisit().setRouteEparchyCode(conName);
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("SERIAL_NUMBER_B", SERIAL_NUMBER_B);
        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.user_id_b,b.serial_number_b,b.user_id_a,b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and b.user_id_a=:USER_ID_A");
        sp.addSQL(" and b.serial_number_b=:SERIAL_NUMBER_B");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, conName);
    }

    // todo
    public static IDataset getNotFinishTradeUU(String USER_ID_B, String conName) throws Exception
    {

        // getVisit().setRouteEparchyCode(conName);
        IData inData = new DataMap();
        inData.put("USER_ID_B", USER_ID_B);
        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, conName);
    }

    /**
     * 通过TRADE_ID、MODIFY_TAG、ROUTE_ID查询RELATION子台账表数据
     * 
     * @param tradeId
     * @param modifyTag
     * @param routeId
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-9-17
     */
    public static IDataset getTradeRelaByTidMtag(String tradeId, String modifyTag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_TRADEID_TAG", param, routeId);
    }

    /**
     * 获取关系子台帐
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeRelaByTradeIdRelaType(String trade_id, String relation_type_code) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("RELATION_TYPE_CODE", relation_type_code);

        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_TRADEID_RELACODE", inparams,Route.getJourDb());
    }

    /**
     * 根据trade_id,accept_month,modify_tag查询tf_b_trade_relation数据 gaoyuan3@asiainfo-linkage.com @ 2012-2-10 上午09:35:07
     * 
     * @param param
     *            {trade_id,accept_month,modify_tag}
     * @return
     * @throws Exception
     */
    public static IDataset getTradeRelationByModifyTag(String trade_id, String modify_tag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("MODIFY_TAG", modify_tag);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_TRADEID_TAG", param, pagination);
    }

    /**
     * 根据tradeId查询所有的用户关系台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUURelaByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_RELATION", "SEL_BY_PK", params,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    // todo
    /*
     * 查询台帐的短号码是否已使用
     */
    public static IDataset qryTradeRelaUUInfo(String USER_ID_A, String TRADE_TYPE_CODE, String RELATION_TYPE_CODE, String SHORT_CODE) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
        inData.put("RELATION_TYPE_CODE", RELATION_TYPE_CODE);
        inData.put("SHORT_CODE", SHORT_CODE);
        SQLParser parser = new SQLParser(inData);
        parser.addSQL("SELECT /* +index(r,PK_TF_B_TRADE_RELATION) */  1  ");
        parser.addSQL(" FROM tf_b_trade_relation r, TF_B_TRADE b ");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND b.user_id_b=TO_NUMBER(:USER_ID_A) ");
        parser.addSQL(" AND b.TRADE_TYPE_CODE =:TRADE_TYPE_CODE");
        parser.addSQL(" AND r.TRADE_ID = b.TRADE_ID ");
        parser.addSQL(" AND r.ACCEPT_MONTH = b.ACCEPT_MONTH ");
        parser.addSQL(" AND r.relation_type_code=:RELATION_TYPE_CODE");
        parser.addSQL(" AND r.short_code=:SHORT_CODE");
        parser.addSQL(" AND r.MODIFY_TAG = '0'");
        parser.addSQL(" AND r.end_date > last_day(trunc(sysdate))+1-1/24/3600");
        parser.addSQL(" AND rownum < 2");

        return Dao.qryByParseAllCrm(parser, false);
    }

    /**
     * 查询UU台帐表
     * 
     * @param tradeId
     * @param relationTypeCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeRelaByTradeIdModTag(String tradeId, String relationTypeCode, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_TRADEID_MODIFY", param);
    }

    /**
     * 查询备份的优惠属性
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeRelaFromBak(String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_TRADE_RELATION_FROM_BAK", param);
    }

    /**
     * 查询uu订单备份表的数据
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeRelaFromBak(String tradeId, String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID_B", userId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_TRADE_RELATION_FROM_BAK_2", param);
    }

    /**
     * 查询备份的优惠属性
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeRelaFromBakOther(String tradeId, String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_TRADE_RELATION_FROM_BAK_1", param);
    }

    // todo
    /**
     * 获取组合产品uu关系台帐子表数据,用于后续插入cen库关系表Tf_f_User_Comp_Rela
     * 
     * @param iData
     * @return
     */
    public IDataset getCompProUUTradeInfo(String TRADE_ID, String TRADE_TYPE_CODE) throws Exception
    {

        // String tradeId = iData.getString("TRADE_ID");
        if (TRADE_ID == null || "".equals(TRADE_ID))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }
        // String tradeTypeCode = iData.getString("TRADE_TYPE_CODE");
        IData params = new DataMap();
        params.put("VTRADE_ID", TRADE_ID);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_RELATION", "SEL_BY_PK", params, Route.CONN_CRM_CEN);
            if (!IDataUtil.isNotEmpty(iDataset))
            {
                ((IData) iDataset.get(0)).put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
            }
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
            return null;
        }
    }
    
    
    public static IDataset getTradeUUByUserIdBAnd(String USER_ID_B,String RELATION_TYPE_CODE,String ROLE_CODE_B, String conName) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_B", USER_ID_B);
        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.*");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and b.RELATION_TYPE_CODE=:RELATION_TYPE_CODE");
        sp.addSQL(" and b.ROLE_CODE_B=:ROLE_CODE_B");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, conName);
    }
    
    public static IDataset getTradeUUByUserIdBAndTypecode(String USER_ID_B,String RELATION_TYPE_CODE,String roleCodeB) throws Exception
    {
    	 IData inData = new DataMap();
         inData.put("USER_ID_B", USER_ID_B);
         inData.put("RELATION_TYPE_CODE", RELATION_TYPE_CODE);
         inData.put("ROLE_CODE_B", roleCodeB);
         SQLParser sp = new SQLParser(inData);
         sp.addSQL("select * ");
         sp.addSQL(" from TF_B_TRADE_RELATION ");
         sp.addSQL(" where 1=1");
         sp.addSQL(" and user_id_b=:USER_ID_B");
         sp.addSQL(" and RELATION_TYPE_CODE=:RELATION_TYPE_CODE");
         sp.addSQL(" and ROLE_CODE_B=:ROLE_CODE_B");
         return Dao.qryByParse(sp,Route.getJourDb());
    }
    
    public static IDataset getRelaUUByTradeIdRelationTypeCode(String tradeId, String relationTypeCode) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_TRADEID_RELATIONTYPECODE", param, Route.getJourDb());
    }
    
    /**
     * 6800接口过来的，查询未完工工单wangsc
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUUByUserIdBAndUnfinish(String USER_ID_B,String RELATION_TYPE_CODE,String ROLE_CODE_B) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_B", USER_ID_B);
        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select a.*");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and a.trade_type_code='6800'");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and b.RELATION_TYPE_CODE=:RELATION_TYPE_CODE");
        sp.addSQL(" and b.ROLE_CODE_B=:ROLE_CODE_B");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, Route.getJourDb());
    }

    public static IDataset getRelaUUByUserIdARelationTypeCode(String userIdA, String tradeTypeCode,String relationTypeCode,String roleCodeB) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        param.put("ROLE_CODE_B", roleCodeB);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_USERIDA_RELATIONTYPECODE_ROLECODEB", param, Route.getJourDb());
    }
    //查询userIdA查询
    public static IDataset getRelaUUByUserIdARelaTypeCodeAndRoleCodeA(String userIdA, String relationTypeCode, String roleCodeB) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        param.put("ROLE_CODE_B", roleCodeB);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_USERIDA_RELATYPECODE_ROLECODEB", param, Route.getJourDb());
    }
}
