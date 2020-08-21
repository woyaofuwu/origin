
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleActiveInfoQry
{

    public static IDataset getUserBookSaleActive(String userId, String productId, String bookType) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("BOOK_TYPE", bookType);
        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_BY_UID", inparam);
    }
    
    public static IDataset getUserBookSaleActive2(String userId, String productId, String bookType) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("BOOK_TYPE", bookType);
        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_BOOKACTIVE_BY_UID", inparam);
    }

    /**
     * 获取用户有效的营销活动
     * 
     * @author chenzm
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserSaleActiveInfo(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID", inparam);
    }
    
    /**
     * 获取用户指定的营销活动
     * 
     * @author wangsc10
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserSaleActiveInfoByPRODUCTIDPACKAGEID(String userId,String productId,String packageId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID_PRODUCT_PACKAGE", inparam);
    }
    
    /**
     * 获取用户指定有效的营销活动
     * 
     * @author lizj
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getVaildSaleActiveByPackageId(String userId,String productId,String packageId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PACKAGE_ID", packageId);

        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", inparam);
    }

    /**
     * 获取用户有效的返还/约定消费类活动
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public static IDataset getUserSaleActiveNo155(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_SALEACTIVE_NO155", inparam);
    }

    public static IDataset queryBindUserActives(String serialNumber, String productId, String packageId) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BIND_USER_BY_SERIAL_NUMBER", inparam);
    }

    public static IDataset queryCustSalePromotionInfo(String promotionCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROMOTION_CODE", promotionCode);
        inparam.put("CRM_FLAG", "1");
        return Dao.qryByCode("TI_B_CUSTSALE_PROMOTION", "SEL_BY_PK", inparam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryHdfkActivesByResNo(String resCode) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("RES_CODE", resCode);

        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_HDFK_BY_RESCODE", inparam);
    }

    public static IDataset queryHdfkActivesByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_HDFK_BY_UID", inparam);
    }
    public static IDataset queryHdfkActivesByUserId2(String userId) throws Exception
    {	//book_type 为1
        IData inparam = new DataMap();

        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_HDFK_BY_UID2", inparam);
    }

    public static IDataset querySaleActiveDataDown(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVITE_ACTIVE", param);
    }

    /** 校园营销明细 */
    public static IDataset querySchoolSaleDetail(String serialNumber, String psptId, String orderType, String orderId, String orderStatus, String startDate, String endDate, Pagination pagin) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("PSPT_ID", psptId);
        inparam.put("ORDER_TYPE", orderType);
        inparam.put("ORDER_ID", orderId);
        inparam.put("ORDER_STATUS", orderStatus);
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TI_B_COLLEGES_SALE", "SEL_SALE_DETAIL", inparam, pagin,Route.CONN_CRM_CEN);
    }

    public static IData querySchoolSalePreTradeInfo(String orderCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ORDER_CODE", orderCode);
        IDataset datset = Dao.qryByCode("TF_B_TRADE_CUSTSALE_PROMOTION", "SEL_BY_ORDER", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return IDataUtil.isEmpty(datset) ? null : datset.getData(0);
    }

    /**
     * 查询是否存在预约办理的活动有限制主号不能取消统一付费关系
     * 
     * @param userId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-18
     */
    public static IDataset queryTradeLimitBookActives(String userId, String tradeTypeCode) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("USER_ID", userId);
        inparam.put("TRADE_TYPE_CODE", tradeTypeCode);

        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_ACTIVE_BOOK_LIMITTRADE", inparam);
    }

    public static IDataset queryWidenetActivesByIds(String userId, String productId, String packageId) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_WIDENET_BY_IDS", inparam);
    }
    
    /**
     * 获取用户某个营销活动的所有数据
     * 
     * @author songlm
     * @param userId,productId
     * @return
     * @throws Exception
     */
    public static IDataset getUserAllSaleActiveInfo(String userId, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALLACTIVE_BY_USER_ID", inparam);
    }
    
    /**
     * 根据limit_code配置的时间查询在155中的营销活动
     * 
     */
    public static IDataset getUserSaleActiveInfoByLimitCode(String userId, String limitCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("LIMIT_CODE", limitCode);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_LIMIT_CODE", inparam);
    }
    
    /**
     * 根据limit_code配置的时间查询不在155中的营销活动
     * 
     */
    public static IDataset getUserSaleActiveInfoByLimitCodeN(String userId, String limitCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("LIMIT_CODE", limitCode);

        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_LIMIT_CODE_N", inparam);
    }
    
    /**
     * 查询办理
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryLargessFluxGrpActiveInfo(IData inParam) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_SALEACTIVE_BY_PROID_FORGRP", inParam);
    }
    
    /**
     * 查询魔百和相应的活动
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryFreeDiscntByTradeSaleActive(IData inParam) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_FREEDISCNT", inParam);
    }
    
    /**
     * 查询魔百和相应的活动
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryFreeDiscntByUserSaleActive(IData inParam) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_SALE_ACTIVE", "SEL_BY_USER_FREEDISCNT", inParam);
    }
    
    /**
     * 魔百和预受理活动存在依赖的活动
     */
    public static IDataset querySaleActiveInfoAndTrade(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "QRY_SALEACTIVE_INFO_AND_TRADE", param);
    }
    
    /**
     * 魔百和预受理活动存在依赖的活动
     */
    public static IDataset querySaleActiveInfoByTrade(IData param) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT C.SERIAL_NUMBER ");
        parser.addSQL(" FROM TF_B_TRADE C, TF_B_TRADE_SALE_ACTIVE D ");
        parser.addSQL(" WHERE C.TRADE_TYPE_CODE = '240' ");
        parser.addSQL(" AND C.TRADE_ID = D.TRADE_ID ");
        parser.addSQL(" AND C.SERIAL_NUMBER = D.SERIAL_NUMBER ");
        parser.addSQL(" AND C.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND INSTR(:RSRV_STR5, '|' || D.PRODUCT_ID || '|') > 0 ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT E.SERIAL_NUMBER ");
        parser.addSQL(" FROM TF_B_TRADE E, TF_B_TRADE_SALEACTIVE_BOOK F ");
        parser.addSQL(" WHERE E.TRADE_TYPE_CODE = '230' ");
        parser.addSQL(" AND E.TRADE_ID = F.TRADE_ID ");
        parser.addSQL(" AND E.SERIAL_NUMBER = F.SERIAL_NUMBER ");
        parser.addSQL(" AND E.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND INSTR(:RSRV_STR5, '|' || F.PRODUCT_ID || '|') > 0 ");

        return Dao.qryByParse(parser, Route.getJourDbDefault());
    }
    
    /**
     * 获取用户某个营销活动的所有数据 
     * 有效的。
     */
    public static IDataset getUserSaleActiveInfoInUse(String userId, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_INUSE_BY_USER_ID", inparam);
    }
    /**
     * 查询用户是否办理了某些活动，若有则该用户不允许办理海洋通产品集团高级付费
     * REQ2018052100182018年海洋通约定消费送话费营销活动开发需求
     * @param userId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-7
     */
    public static IDataset getUserSaleActiveForGhytAdvPay(String userId) throws Exception
    {
    	IData params = new DataMap();
        params.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID,");
        sql.append("       A.SERIAL_NUMBER,");
        sql.append("       A.PRODUCT_ID,");
        sql.append("       A.PACKAGE_ID,");
        sql.append("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        sql.append("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
        sql.append("  FROM TF_F_USER_SALE_ACTIVE A, TD_S_COMMPARA B");
        sql.append(" WHERE A.PRODUCT_ID = B.PARA_CODE1");
        sql.append("   AND (A.PACKAGE_ID = NVL(B.PARA_CODE2, A.PACKAGE_ID))");
        sql.append("   AND B.SUBSYS_CODE = 'CSM'");
        sql.append("   AND B.PARAM_ATTR = 838");
        sql.append("   AND B.PARAM_CODE = '0'");
        sql.append("   AND A.PARTITION_ID = MOD(:USER_ID, 10000)");
        sql.append("   AND A.USER_ID = :USER_ID");
        sql.append("   AND A.PROCESS_TAG = '0'");
        sql.append("   AND A.END_DATE > SYSDATE");

        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }

	/**
	 * @description 查询用户的营销活动，没有时间限制
	 * @param @param userId
	 * @param @param saleProductId
	 * @param @param slaePackageId
	 * @param @return
	 * @return IDataset
	 * @author tanzheng
	 * @date 2019年6月25日
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @return
	 * @throws Exception 
	 */
	public static IDataset getUserActiveWithOutTime(String userId, String productId, String packageId) throws Exception {
		IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID,");
        sql.append("       A.SERIAL_NUMBER,");
        sql.append("       A.PRODUCT_ID,");
        sql.append("       A.PACKAGE_ID,");
        sql.append("       A.PACKAGE_NAME,");
        sql.append("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        sql.append("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
        sql.append("  FROM TF_F_USER_SALE_ACTIVE A");
        sql.append(" WHERE  A.USER_ID = :USER_ID");
        sql.append(" AND A.PRODUCT_ID = :PRODUCT_ID");
        sql.append(" AND A.PACKAGE_ID = :PACKAGE_ID");
        
        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
	}
	public static IDataset getUserSaleActiveByProductId(String userId,String proId) throws Exception
    {
		IData inparam = new DataMap();
		inparam.put("PRODUCT_ID", proId);
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BS_BY_USERID_PID", inparam);
		
    }

    public static IDataset getUserActiveByPackAble(String userId, String packageId) throws Exception {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PACKAGE_ID", packageId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID,");
        sql.append("       A.SERIAL_NUMBER,");
        sql.append("       A.PRODUCT_ID,");
        sql.append("       A.PACKAGE_ID,");
        sql.append("       A.PACKAGE_NAME,");
        sql.append("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        sql.append("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
        sql.append("  FROM TF_F_USER_SALE_ACTIVE A");
        sql.append(" WHERE  A.USER_ID = :USER_ID");
        sql.append(" AND A.PACKAGE_ID = :PACKAGE_ID");
        sql.append(" AND A.END_DATE > SYSDATE");

        IDataset result = Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
        return result;
    }
}
