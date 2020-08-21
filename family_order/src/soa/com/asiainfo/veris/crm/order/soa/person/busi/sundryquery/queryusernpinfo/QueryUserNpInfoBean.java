
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryusernpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryUserNpInfoBean extends CSBizBean
{

    public IDataset getUserNpTradeInfos(IData param, Pagination page) throws Exception
    {
        String serial_number = param.getString("SERIAL_NUMBER");
        String start_date = param.getString("START_DATE");
        String end_date = param.getString("END_DATE");
        IDataset ids = TradeNpQry.geytNpUserTradeInfos(serial_number, start_date, end_date, null, null, page);
        if(IDataUtil.isNotEmpty(ids))
        {
        	for(int i = 0;i<ids.size();i++)
        	{
        		IDataset resultscrm = TradeNpQry.queryStateByUserid(ids.getData(i).getString("USER_ID"), page);
                if(IDataUtil.isNotEmpty(resultscrm))
                {
                	ids.getData(i).put("MEMBER_KIND", resultscrm.getData(0).getString("MEMBER_KIND"));
                	ids.getData(i).put("GROUP_ID", resultscrm.getData(0).getString("GROUP_ID"));
                	ids.getData(i).put("GROUP_CUST_NAME", resultscrm.getData(0).getString("GROUP_CUST_NAME"));
                	ids.getData(i).put("User_Cust_Id", resultscrm.getData(0).getString("User_Cust_Id"));
                	ids.getData(i).put("Cust_User_Id", resultscrm.getData(0).getString("Cust_User_Id"));
                	ids.getData(i).put("User_Id_a", resultscrm.getData(0).getString("User_Id_a"));
                }
        	}
        }
        if (IDataUtil.isNotEmpty(ids))
        {
            String state = "";
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                String custUserId = data.getString("CUST_USER_ID", "");
                String user_cust_id = data.getString("USER_CUST_ID", "");
                String sn = data.getString("SERIAL_NUMBER", "");
                String acceptDate = data.getString("ACCEPT_DATE", "");
                String member_kind = data.getString("MEMBER_KIND", "");

                if (!"".equals(custUserId))
                {
                    IDataset groups = TradeNpQry.queryGroupInfoByUseridCustid(user_cust_id, custUserId);
                    if (IDataUtil.isNotEmpty(groups))
                    {
                        data.put("GROUP_ID", groups.getData(0).getString("GROUP_ID"));
                        data.put("GROUP_CUST_NAME", groups.getData(0).getString("CUST_NAME"));
                        data.put("MEMBER_KIND", groups.getData(0).getString("DATA_NAME"));
                        data.put("CUST_MANAGER_NAME", groups.getData(0).getString("STAFF_NAME"));
                        data.put("CUST_MANAGER_PHONE", groups.getData(0).getString("SERIAL_NUMBER"));
                        data.put("CUST_CITY", groups.getData(0).getString("AREA_NAME"));
                    }
                }

                if (!"".equals(sn) && !"".equals(acceptDate))
                {
                    IDataset vpns = UserVpnInfoQry.qryUserVpnInfoBySerialNumber(sn, acceptDate);
                    if (IDataUtil.isNotEmpty(vpns))
                    {

                        String vpn_no = vpns.getData(0).getString("VPN_NO");
                        String vpn_name = vpns.getData(0).getString("VPN_NAME");
                        String cust_manager = vpns.getData(0).getString("CUST_MANAGER");

                        String staff_name = UStaffInfoQry.getStaffNameByStaffId(cust_manager);
                        String serialNumber = UStaffInfoQry.getStaffSnByStaffId(cust_manager);
                        data.put("VPN_NO", vpn_no);
                        data.put("VPN_NAME", vpn_name);
                        data.put("VPN_MANAGER_NAME", staff_name);
                        data.put("VPN_MANAGER_PHONE", serialNumber);
                    }
                }

                state = data.getString("STATE");
                data.put("STATE_DESC", data.getString("STATE_NAME"));

                if (("021".equals(state) || "031".equals(state)) && "0".equals(data.getString("CANCEL_TAG")) && !"1".equals(data.getString("RSRV_STR1", "")))
                {
                    data.put("CAN_DEAL", "1");
                }
                else
                {
                    data.put("CAN_DEAL", "0");
                }

            }
        }

        return ids;
    }

    public IData updateDealTag(IData param) throws Exception
    {
        String tradeId = param.getString("TRADE_ID");
        String dealtag = param.getString("DEALTAG");
        String deal_staff_id = param.getString("DEAL_STAFF_ID");
        IDataset ids = TradeNpQry.getTradeNpByTradeId(tradeId);
        IData m = new DataMap();

        String tag = "0";
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            data.put("RSRV_STR1", dealtag);
            data.put("RSRV_STR2", SysDateMgr.getSysTime());
            data.put("RSRV_STR3", deal_staff_id);
            if (Dao.update("TF_B_TRADE_NP", data))
            {

                tag = "1";
            }
        }
        m.put("TAG", tag);
        return m;

    }

}
