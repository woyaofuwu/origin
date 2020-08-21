
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class QueryDerateFeeQry extends CSBizBean
{
    public static IDataset queryDerateFee(String tradeType, String startDate, String endDate, String areaCode, String startStaffId, String endStaffId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PARA_CODE1", tradeType);// 业务类型
        params.put("PARA_CODE2", startDate);// 开始时间
        params.put("PARA_CODE3", SysDateMgr.getLastSecond(SysDateMgr.addDays(endDate, 1)));// 结束时间 取本日最后一秒
        params.put("PARA_CODE4", areaCode);// 业务区
        params.put("PARA_CODE5", startStaffId);// 开始工号
        params.put("PARA_CODE6", endStaffId);// 结束工号
        String acceptMonth = SysDateMgr.getMonthForDate(startDate);
        params.put("ACCEPT_MONTH", acceptMonth);
        //TODO huanghua 21 业务涉及超时管理，暂时先用crm或jour代替，测试环境重测---sql不涉及超时，不需要超时管理
//        return Dao.qryByCode("TD_S_COMMPARA", "SEL_DERATEFEE", params, pagination, Route.CONN_CRM_LXF);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_DERATEFEE", params, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
    }
    public static IDataset queryDerateFee1(String tradeType, String startDate, String endDate, String areaCode, String startStaffId, String endStaffId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        if(!"-1".equals(tradeType)){
            params.put("PARA_CODE1", tradeType);// 业务类型
        }
        params.put("PARA_CODE2", startDate);// 开始时间
        params.put("PARA_CODE3", SysDateMgr.getLastSecond(SysDateMgr.addDays(endDate, 1)));// 结束时间 取本日最后一秒
        params.put("PARA_CODE4", areaCode);// 业务区
        params.put("PARA_CODE5", startStaffId);// 开始工号
        params.put("PARA_CODE6", endStaffId);// 结束工号
        String acceptMonth = SysDateMgr.getMonthForDate(startDate);
        params.put("ACCEPT_MONTH", acceptMonth);
        //TODO huanghua 21 业务涉及超时管理，暂时先用crm或jour代替，测试环境重测，将sql中的翻译语句移出来---sql不涉及超时，不需要超时管理
//        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_DERATEFEE1", params, pagination, Route.CONN_CRM_LXF);
        IDataset dataset = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_DERATEFEE11", params, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        IData temp = new DataMap();
        IData departInfo = new DataMap();
        IData staffInfo = new DataMap();
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
				temp = dataset.getData(i);
				departInfo = UDepartInfoQry.qryDepartByDepartId(temp.getString("TRADE_DEPART_ID",""));
				staffInfo = UStaffInfoQry.qryStaffInfoByPK(temp.getString("TRADE_STAFF_ID",""));
				temp.put("PARA_CODE3", StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", temp.getString("TRADE_TYPE_CODE")));
				temp.put("PARA_CODE7", staffInfo.getString("STAFF_ID")+"|"+staffInfo.getString("STAFF_NAME"));
				temp.put("PARA_CODE8", departInfo.getString("DEPART_CODE")+"|"+departInfo.getString("DEPART_NAME"));
			}
        }
        return dataset;
    }

    public static IDataset queryTradeTypeListByBothAttrNameCode(String subsysCode, String paramAttr, String paramCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subsysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        params.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", params, pagination);
    }

    public static IDataset queryTradeTypeListOnlyByAttr(String subsysCode, String paramAttr, String eparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subsysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", params, pagination);
    }
}
