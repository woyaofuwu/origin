
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class QryBOSSInfoDAO extends CSBizBean
{
	/**
     * 根据用户服务号码查询业务历史信息（tf_bh_trade）
     * 
     * @param param
     * @param pagination
     * @return
     */
    public static IDataset queryBOSSHistoryInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  /*+ index(a IDX_TF_BH_TRADE_SN)*/trade_id,in_mode_code,order_id,trade_type_code,");
        parser.addSQL(" decode(cancel_tag,'0','未返销','1','被返销','2','返销','') cancel_tag,");
        parser.addSQL(" eparchy_code,accept_date,trim(TRADE_STAFF_ID) TRADE_STAFF_ID,trim(TRADE_DEPART_ID)TRADE_DEPART_ID, substr(a.process_tag_set, 9,1) process_tag_set");
        parser.addSQL(" from tf_bh_trade a  ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.SERIAL_NUMBER=:SERIAL_NUMBER");
        parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
        parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");
        parser.addSQL(" and a.in_mode_code='6'");
        parser.addSQL(" and a.trade_type_code not in ('1170','2101') ");
        parser.addSQL(" order by ACCEPT_DATE desc ");
        IDataset results = Dao.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if (results == null || results.isEmpty())
        {
            return null;
        }
        else
        {
            IData result_info = null;
            for (int i = 0; i < results.size(); i++)
            {
                result_info = results.getData(i);
                String trade_type_code = result_info.getString("TRADE_TYPE_CODE", "");
                if (!"".equals(trade_type_code) && "110".equals(trade_type_code))
                {
                    String process_tag_set = result_info.getString("PROCESS_TAG_SET", "");
                    result_info.put("TAG_110", "1");
                    if ("1".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务变更");
                    }
                    else if ("2".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "优惠变更");
                    }
                    else if ("3".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "服务和优惠变更");
                    }
                    else if ("4".equals(process_tag_set))
                    {
                        result_info.put("PROCESS_TAG_SET", "产品变更");
                    }
                    else
                    {
                        result_info.put("TAG_110", "2");
                    }
                }
                else
                {
                    result_info.put("TAG_110", "2");
                }
                
                String staffId = result_info.getString("TRADE_STAFF_ID");
                String departId = result_info.getString("TRADE_DEPART_ID");
                IDataset commparaSet = CommparaInfoQry.getCommparaInfoByCode2("CSM", "7979", "UNI_CHANNEL_STAFF", staffId, "0898");
                if(IDataUtil.isNotEmpty(commparaSet)&&commparaSet.size()>0){
                	String departCode4 = commparaSet.getData(0).getString("PARA_CODE4");
                	String departName6 = commparaSet.getData(0).getString("PARA_CODE6");
                	result_info.put("TRADE_DEPART_CODE", departCode4);
                	result_info.put("TRADE_DEPART_NAME", departName6);
                	result_info.put("TRADE_STAFF_NAME", staffId);
                }else{
                    String departCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", departId, "未知");
                    String departName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId, "未知");
                    String staffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId, "未知");
                    result_info.put("TRADE_DEPART_CODE", departCode);
                    result_info.put("TRADE_DEPART_NAME", departName);
                    result_info.put("TRADE_STAFF_NAME", staffName);
                }
            }
        }
        return results;
    }
}
