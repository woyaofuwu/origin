
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/*
 * * 获取tl_f_cust_person表中客户信息
 */
public class THTradePlatSvcInfoDAO
{
    /**
     * 根据Trade_Id查询平台服务业务历史属性信息
     * 
     * @param param
     * @return
     */
    public IDataset queryPlatAttrInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.info_code,b.attr_name,decode(c.data_name,null,a.info_value,c.data_name) info_value  ");
        parser.addSQL(" from tf_b_trade_platsvc_attr a,td_b_platsvc_attr b,td_s_static c where 1 = 1 ");
        parser.addSQL("AND  A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("AND  A.INFO_CODE = B.ATTR_CODE(+) ");
        parser.addSQL("AND  A.SERVICE_ID = B.SERVICE_ID(+) ");
        parser.addSQL("AND  A.INFO_CODE||'_'||A.INFO_VALUE = C.DATA_ID(+)  ");
        parser.addSQL("AND  C.TYPE_ID(+) = 'PLAT_ATTR' ");
        parser.addSQL("AND  A.SERVICE_ID = :SERVICE_ID ");
        parser.addSQL("AND  B.RSRV_STR1 IS NULL "); // 屏蔽密码属性
        return Dao.qryByParse(parser);
    }

    /**
     * 根据Trade_Id查询平台服务业务历史
     * 
     * @param param
     * @return
     */
    public IDataset queryTradePlatSvcInfo(IData param) throws Exception
    {
        String tradeId = param.getString("TRADE_ID", "");
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        if (StringUtils.isBlank(tradeId))
        {
            return new DatasetList();
        }
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        String tableName = "Tf_b_Trade_Platsvc";
        if("G".equals(history_query_type)){
        	String year_id = param.getString("QUERY_YEAR","");
            if(StringUtils.isBlank(year_id)){
            	return new DatasetList();
            }
            tableName = "TF_BHB_TRADE_PLATSVC_"+year_id;
        }
        IData paramsData = new DataMap();
        paramsData.put("TRADE_ID", tradeId);
        paramsData.put("ACCEPT_MONTH", acceptMonth);

        SQLParser parser = new SQLParser(paramsData);

//        parser.addSQL("Select a.trade_id,a.SERVICE_ID,b.Sp_Code,b.Biz_Code,b.Biz_Type_Code,decode(a.Biz_State_Code, 'A','正常', 'N','暂停','E','终止','P','预退订','L','锁定','') Biz_State_Code,OPER_CODE");
//        parser.addSQL(" From "+tableName+" a,td_b_platsvc b ");
//        parser.addSQL(" where a.Trade_Id = :TRADE_ID And a.SERVICE_ID=b.service_id ");
//        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL("Select a.trade_id,a.SERVICE_ID,decode(a.Biz_State_Code, 'A','正常', 'N','暂停','E','终止','P','预退订','L','锁定','') Biz_State_Code,OPER_CODE");
        parser.addSQL(" From "+tableName+" a ");
        parser.addSQL(" where a.Trade_Id = :TRADE_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        if("G".equals(history_query_type))
        {
            return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
        }
        else
        {
            return Dao.qryByParse(parser, Route.getJourDb());
        }    
     }
}
