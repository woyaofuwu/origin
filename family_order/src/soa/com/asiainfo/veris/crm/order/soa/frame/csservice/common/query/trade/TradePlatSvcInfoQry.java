
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradePlatSvcInfoQry
{
    private static final Logger log = Logger.getLogger(TradePlatSvcInfoQry.class);

    /**
     * 根据tradeId查询所有的用户优惠备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakPlatSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PLATSVC_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    // todo
    /**
     * 获取平台服务台帐
     * 
     * @param iData
     * @return
     */
    public static IDataset getPlatSvcInfo(String TRADE_ID) throws Exception
    {

        // String tradeId = iData.getString("TRADE_ID");
        if (TRADE_ID == null || "".equals(TRADE_ID))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }
        IData params = new DataMap();
        params.put("TRADE_ID", TRADE_ID);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_PLATSVC", "SEL_BY_TRADE_ID", params, Route.CONN_CRM_CEN);
            if (!IDataUtil.isNotEmpty(iDataset))
            {
                ((IData) iDataset.get(0)).put("X_RECORDNUM", iDataset.size());
            }
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_31);
            return null;
        }
    }

    /**
     * 根据tradeId查询所有的用户优惠台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradePlatSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PLATSVC", "SEL_BY_TRADEID", params, Route.getJourDb(CSBizBean.getUserEparchyCode()));
    }

    // todo
    /**
     * Adc/Mas业务查询(黑白名单查询)
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryAdcMasBizInfo(String GROUP_ID, String PRODUCT_ID, String BIZ_CODE, String RB_LIST, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", GROUP_ID);
        param.put("PRODUCT_ID", PRODUCT_ID);
        param.put("BIZ_CODE", BIZ_CODE);
        param.put("RB_LIST", RB_LIST);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT A.PARTITION_ID,TO_CHAR(A.USER_ID) USER_ID, B.CUST_NAME,a.serial_number,");
        parser.addSQL("       decode(a.BIZ_TYPE_CODE,'001','17201','002','WLAN', '003','WAP', '004','SMS','005','MMS','006','KJAVA', '007','LBS', '008','EMAIL') BIZ_TYPE_CODE,");
        parser.addSQL("       a.SERV_CODE, a.BIZ_NAME,");
        parser.addSQL("       decode(a.ACCESS_MODE,'01','WEB','02','网上营业厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','1860/营业厅','08','BOSS') ACCESS_MODE,");
        parser.addSQL("       a.ACCESS_NUMBER, decode(a.OPER_STATE,'0','新增','04','暂停','05','恢复','08','变更','1','终止') OPER_STATE, a.PRICE, decode(a.BILLING_TYPE,'0','免费','1','包月','2','按次') BILLING_TYPE,");
        parser.addSQL("       F_SYS_GETCODENAME('static','PLAT_GRP_BIZ_PRI', a.BIZ_PRI, '') BIZ_PRI, ");
        parser.addSQL("       decode(a.BIZ_STATUS,'A','正常商用','N','暂停','S','内部测试','T','测试待审','R','试商用','E','终止') BIZ_STATUS,");
        parser.addSQL("       decode(a.BIZ_ATTR,'0','订购关系','1','白名单','2','黑名单','3','企业级','4','全局级') BIZ_ATTR,");
        parser.addSQL("       a.CS_URL, a.USAGE_DESC,a.INTRO_URL,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,");
        parser.addSQL("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, TO_CHAR(A.FIRST_DATE, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE, a.BIZ_CODE,");
        parser.addSQL("       F_SYS_GETCODENAME('static','PLAT_BILLINGMODE', a.BILLING_MODE, '') BILLING_MODE, a.rsrv_num1,c.product_id");
        parser.addSQL(" FROM TF_F_USER_GRP_PLATSVC A,TF_F_CUST_GROUP B,TF_F_USER C ");
        parser.addSQL(" WHERE B.REMOVE_TAG = '0' ");
        parser.addSQL("     AND C.REMOVE_TAG = '0' ");
        parser.addSQL("     AND B.CUST_ID=C.CUST_ID ");
        parser.addSQL("     AND C.USER_ID=A.USER_ID ");
        parser.addSQL("     AND (SYSDATE BETWEEN a.START_DATE AND a.END_DATE) ");
        parser.addSQL("     AND B.GROUP_ID=:GROUP_ID ");
        parser.addSQL("     AND C.PRODUCT_ID = TO_NUMBER(:PRODUCT_ID) ");
        parser.addSQL("     AND A.SERV_CODE=:BIZ_CODE ");
        parser.addSQL("     AND A.BIZ_ATTR =:RB_LIST ");// 黑白名单标记

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @param serviceId
     *            平台服务id
     * @param userId
     *            用户id
     * @return
     * @throws Exception
     * @author ygh
     */
    public static IDataset query3788Trade(IData inParam) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_PLATSVC", "SEL_3788_TRADE", inParam);
    }

    /**
     * 根据Trade_Id查询平台服务业务历史属性信息
     * 
     * @param param
     * @return
     */
    public static IDataset queryInfo(String tradeId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SERVICE_ID", serviceId);
        SQLParser parser = new SQLParser(param);
        // parser.addSQL(" select a.info_code,b.attr_name,decode(c.data_name,null,a.info_value,c.data_name) info_value  ");
        // parser.addSQL(" from tf_b_trade_platsvc_attr a,td_b_platsvc_attr b,td_s_static c where 1 = 1 ");
        // parser.addSQL("AND  A.TRADE_ID = :TRADE_ID ");
        // parser.addSQL("AND  A.INFO_CODE = B.ATTR_CODE(+) ");
        // parser.addSQL("AND  A.SERVICE_ID = B.SERVICE_ID(+) ");
        // parser.addSQL("AND  A.INFO_CODE||'_'||A.INFO_VALUE = C.DATA_ID(+)  ");
        // parser.addSQL("AND  C.TYPE_ID(+) = 'PLAT_ATTR' ");
        // parser.addSQL("AND  A.SERVICE_ID = :SERVICE_ID ");
        // parser.addSQL("AND  B.RSRV_STR1 IS NULL "); // 屏蔽密码属性
        // return Dao.qryByParse(parser);
        parser.addSQL(" select a.USER_ID, A.ATTR_CODE ,A.ATTR_VALUE ,a.ELEMENT_ID ");
        parser.addSQL(" from TF_B_TRADE_ATTR a where 1 = 1 ");
        parser.addSQL(" AND  A.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" and a.ELEMENT_ID = :SERVICE_ID ");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据Trade_Id查询平台服务业务历史属性信息
     * 
     * @param param
     * @return IDataset
     * @exception Exception
     * @author huanghui@asiainfo.com
     */
    public static IDataset queryPlatAttrInfo(String tradeId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SERVICE_ID", serviceId);
        if (StringUtils.isNotBlank(tradeId))
        {
            param.put("ACCEPT_MONTH", tradeId.substring(4, 6));
        }
        SQLParser parser = new SQLParser(param);
        // parser.addSQL(" select a.attr_code,b.attr_lable,decode(c.attr_field_name,null,a.ATTR_VALUE,c.attr_field_name) attr_value  ");
        // parser.addSQL(" from tf_b_trade_attr a,td_b_attr_itema b,td_b_attr_itemb c where 1 = 1 ");
        // parser.addSQL(" AND  A.TRADE_ID = :TRADE_ID ");
        // parser.addSQL(" AND  A.INST_TYPE = 'Z' ");
        // parser.addSQL(" AND b.id_type = 'Z' ");
        // parser.addSQL(" AND b.id = :SERVICE_ID ");
        // parser.addSQL(" AND  A.attr_code = B.ATTR_CODE ");
        // parser.addSQL(" AND c.id_type(+) = 'Z' ");
        // parser.addSQL(" AND c.id(+) = b.id ");
        // parser.addSQL(" AND  B.RSRV_STR1 IS NULL "); // 屏蔽密码属性
        parser.addSQL(" select a.attr_code,b.attr_lable,nvl((select c.attr_field_name from td_b_attr_itemb c where c.id = :SERVICE_ID and c.id_type = 'Z' ");
        parser.addSQL(" and c.attr_code = a.attr_code and c.attr_field_code= a.attr_value), a.attr_value) attr_value ");
        parser.addSQL(" from tf_b_trade_attr a, td_b_attr_itema b ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" AND A.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" and a.accept_month = :ACCEPT_MONTH ");
        parser.addSQL(" AND A.INST_TYPE = 'Z' ");
        parser.addSQL(" AND b.id_type = 'Z' ");
        parser.addSQL(" AND b.id = :SERVICE_ID ");
        parser.addSQL(" AND A.attr_code = B.ATTR_CODE ");
        parser.addSQL(" AND b.attr_type_code <> '4' ");
        return Dao.qryByParse(parser);
    }
    
   
    
    public static void updatePlatsvcTradeStartDate(String tradeId,String startDate, String eparchyCode)throws Exception{
    	 IData param = new DataMap();
         param.put("TRADE_ID", tradeId);
         param.put("START_DATE", startDate);
         
         Dao.executeUpdateByCodeCode("TF_B_TRADE_PLATSVC", "UPD_PLATSVC_START_DATE", param, Route.getJourDb(eparchyCode));
    }
}
