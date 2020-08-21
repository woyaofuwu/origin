
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tradenetbook.TradeNetBookInfoQry;

public class TradeNetBookInfoBean extends CSBizBean
{

    public IDataset qryBookInfo(IData data) throws Exception
    {
        String serialNum = data.getString("SERIAL_NUMBER");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");
        IData param = new DataMap();
        if (serialNum == null || serialNum.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "9001:请输入手机号码！");
        }
        param.put("SERIAL_NUMBER", serialNum);

        param.put("BOOK_DATE", startDate);
        if (startDate == null || startDate.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "9001:请输入开始日期参数！");
        }
        param.put("BOOK_END_DATE", endDate);

        if (endDate == null || endDate.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "9002:请输入结束日期参数！");
        }
        String month = new String();
        /*
         * if(startDate!=null&&startDate.length()>0) month = DualMgr.getMonthForDate(pd, startDate);
         * if(month==null||month.length() == 0){ month = DualMgr.getCurMonth(pd); } param.put("ACCEPT_MONTH", month);
         */

        // 查询预约开始时间在查询时间区间之内的预约单
        IDataset list = TradeNetBookInfoQry.qryBookInfoForInf(param);

        IDataset re = new DatasetList();
        if (list != null && list.size() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                IData element = new DataMap();
                IData obj = list.getData(i);
                element.put("TRADE_ID", obj.getString("TRADE_ID", ""));
                element.put("ACCEPT_MONTH", obj.getString("ACCEPT_MONTH"));
                element.put("USER_ID", obj.getString("USER_ID"));
                element.put("SERIAL_NUMBER", obj.getString("SERIAL_NUMBER"));
                element.put("PSPT_TYPE_CODE", obj.getString("PSPT_TYPE_CODE"));
                element.put("PSPT_ID", obj.getString("PSPT_ID"));
                element.put("CUST_NAME", obj.getString("CUST_NAME"));
                element.put("CONTACT_PHONE", obj.getString("CONTACT_PHONE"));
                element.put("BOOK_TYPE_CODE", obj.getString("BOOK_TYPE_CODE"));
                element.put("BOOK_TYPE", obj.getString("BOOK_TYPE"));
                element.put("BOOK_DATE", obj.getString("BOOK_DATE"));
                element.put("BOOK_STATUS", obj.getString("BOOK_STATUS"));
                element.put("BOOK_PHONE", obj.getString("BOOK_PHONE"));
                element.put("GOODS_ID", obj.getString("GOODS_ID"));
                element.put("BOOK_END_DATE", obj.getString("BOOK_END_DATE"));
                element.put("DOOR_END_DATE", obj.getString("DOOR_END_DATE"));
                element.put("TRADE_STAFF_ID", obj.getString("TRADE_STAFF_ID"));
                element.put("TRADE_DEPART_ID", obj.getString("TRADE_DEPART_ID"));
                element.put("TRADE_CITY_CODE", obj.getString("TRADE_CITY_CODE"));
                element.put("TRADE_EPARCHY_CODE", obj.getString("TRADE_EPARCHY_CODE"));
                element.put("RSRV_NUM1", obj.getString("RSRV_NUM1"));
                element.put("RSRV_NUM2", obj.getString("RSRV_NUM2"));
                element.put("RSRV_NUM3", obj.getString("RSRV_NUM3"));
                element.put("RSRV_NUM4", obj.getString("RSRV_NUM4"));
                element.put("RSRV_NUM5", obj.getString("RSRV_NUM5"));
                element.put("DEPART_NAME", obj.getString("RSRV_STR1"));
                element.put("TRADE_TYPE", obj.getString("RSRV_STR2"));
                element.put("RSRV_STR3", obj.getString("RSRV_STR3"));
                element.put("RSRV_STR4", obj.getString("RSRV_STR4"));
                element.put("RSRV_STR5", obj.getString("RSRV_STR5"));
                element.put("RSRV_STR6", obj.getString("RSRV_STR6"));
                element.put("RSRV_STR7", obj.getString("RSRV_STR7"));
                element.put("RSRV_STR8", obj.getString("RSRV_STR8"));
                element.put("RSRV_STR9", obj.getString("RSRV_STR9"));
                element.put("RSRV_STR10", obj.getString("RSRV_STR10"));
                element.put("RSRV_STR11", obj.getString("RSRV_STR11"));
                element.put("RSRV_STR12", obj.getString("RSRV_STR12"));
                element.put("RSRV_STR13", obj.getString("RSRV_STR13"));
                element.put("RSRV_STR14", obj.getString("RSRV_STR14"));
                element.put("RSRV_STR15", obj.getString("RSRV_STR15"));
                element.put("RSRV_STR16", obj.getString("RSRV_STR16"));
                element.put("RSRV_STR17", obj.getString("RSRV_STR17"));
                element.put("RSRV_STR18", obj.getString("RSRV_STR18"));
                element.put("RSRV_STR19", obj.getString("RSRV_STR19"));
                element.put("RSRV_STR20", obj.getString("RSRV_STR20"));
                element.put("RSRV_STR21", obj.getString("RSRV_STR21"));
                element.put("RSRV_STR22", obj.getString("RSRV_STR22"));
                element.put("RSRV_STR23", obj.getString("RSRV_STR23"));
                element.put("RSRV_STR24", obj.getString("RSRV_STR24"));
                element.put("RSRV_STR25", obj.getString("RSRV_STR25"));
                element.put("RSRV_STR26", obj.getString("RSRV_STR26"));
                element.put("RSRV_STR27", obj.getString("RSRV_STR27"));
                element.put("RSRV_STR28", obj.getString("RSRV_STR28"));
                element.put("RSRV_STR29", obj.getString("RSRV_STR29"));
                element.put("RSRV_STR30", obj.getString("RSRV_STR30"));
                // RSRV_DATE1 预留时间字段1作为预约单的处理时间
                element.put("DEAL_DATE", obj.getString("DEAL_DATE"));
                element.put("RSRV_DATE2", obj.getString("RSRV_DATE2"));
                element.put("RSRV_DATE3", obj.getString("RSRV_DATE3"));
                element.put("RSRV_TAG1", obj.getString("RSRV_TAG1"));
                element.put("RSRV_TAG2", obj.getString("RSRV_TAG2"));
                element.put("RSRV_TAG3", obj.getString("RSRV_TAG3"));
                element.put("REMARK", obj.getString("REMARK"));
                re.add(element);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入号码有误或者用户资料不存在！");
        }
        return re;
    }
}
