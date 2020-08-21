
package com.asiainfo.veris.crm.order.soa.person.busi.score.scorecheck;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

public class ScoreCheckQryBean extends CSBizBean
{
	/**
     * 根据条件查询积分对账汇总信息
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150420  
     */
    public static IDataset queryScoreTrade(IData inparams,Pagination page) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select t.CHECK_ID, t.SCORE_TRADE_TYPE, t.BUSS_TRADE_COUNT, t.BUSS_SCORE_COUNT, t.FINANCES_COUNT, ");
        parser.addSQL(" t.FINANCES_INTG_COUNT, t.CHECK_DATE, t.CHECK_ALL, t.RSRV_STR1, t.RSRV_STR2, t.RSRV_STR3, t.RSRV_STR4, t.RSRV_STR5 ");
        parser.addSQL(" from TL_B_INTEGRAL_EXCHAN_UNI t ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and t.check_id = :CHECK_ID ");
        parser.addSQL(" AND T.SCORE_TRADE_TYPE LIKE '%'|| :SCORE_TRADE_TYPE ||'%'");
        parser.addSQL(" and T.CHECK_DATE >= to_date(:START_DATE,'yyyy-mm-dd') ") ;
        parser.addSQL(" and T.CHECK_DATE <= to_date(:END_DATE,'yyyy-mm-dd') ");
 
        return Dao.qryByParse(parser,page);
    }
    
    /**
     * 根据条件查询积分对账-明细
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150420  
     */
    public static IDataset queryScoreTradeDet(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select t.CHECK_ID,t.USER_ID,t.USER_SCORE,t.SCORE_TRADE_TYPE,t.CHECK_DATE, ");
        parser.addSQL(" t.RSRV_STR1,t.RSRV_STR2,t.RSRV_STR3,t.RSRV_STR4,t.RSRV_STR5 ");
        parser.addSQL(" from TL_B_INTEGRAL_EXCHAN_DET t "); 
        parser.addSQL(" where t.check_id = :CHECK_ID ");
        return Dao.qryByParse(parser);
    }
}
