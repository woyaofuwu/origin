
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npreturnvisit;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class NpReturnVisitBean extends CSBizBean
{

    public IDataset getNpOutInfos(IData param, Pagination page) throws Exception
    {
        String areaCode = param.getString("AREA_CODE", "");
        String isReturn = param.getString("IS_RETURN", "");
        String start_date = param.getString("START_DATE", "");
        String serial_number = param.getString("SERIAL_NUMBER", "");
        String end_date = param.getString("END_DATE", "");

        String tag = start_date.substring(5, 6);
        String month = "";
        if ("0".equals(tag))
        {
            month = start_date.substring(6, 7);// 提取月
        }
        else
        {
            month = start_date.substring(5, 7);// 提取月
        }
        
        start_date = start_date+SysDateMgr.START_DATE_FOREVER;
        end_date = end_date +SysDateMgr.END_DATE;
        
        String staffid =  getVisit().getStaffId();
        
        IDataset staffs = StaffInfoQry.qryStaffInfoByStaffId(staffid);
        if(IDataUtil.isNotEmpty(staffs)){
            if (!areaCode.equals("HNAL"))
            {
                String cityCode = staffs.getData(0).getString("CITY_CODE","");
               

                if ("HNSJ".equals(cityCode) || "HNYD".equals(cityCode) || "HNKF".equals(cityCode))
                {
                    if ("0898".equals(areaCode) || "HNSJ".equals(areaCode) || "HNYD".equals(areaCode) || "HNKF".equals(areaCode))
                    {
                        param.put("AREA_CODE", "HAIN");
                    }
                }
                else
                {
                    if (!areaCode.equals(cityCode))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "你归属区域[" + cityCode + "]，你没有权限查询区域[" + areaCode + "]的数据！");

                    }
                }
            }
            else if (areaCode.equals("HNAL"))
            {
                if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_QUERYNPRETURNVISITEXPORT"))
                {
                    param.put("AREA_CODE", "HAIN");
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "你没有权限查询所有区域的数据！");

                }
                // if(pd.getContext().hasPriv("CRM_QUERYNPRETURNVISITEXPORT")){
                // inparam.put("AREA_CODE", "HAIN");
                // }else{
                // common.warn("你没有权限查询所有区域的数据！");
                // return null;
                // }
            }
        }else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取当前登录工号信息出错！");
        }
        
        IDataset ids = null;

        IDataset npOutInfos = null;

        ids = TradeNpQry.getNpReturnSoaInfosByNpCode(isReturn, month, serial_number, start_date, end_date, page);
        IDataset list = new DatasetList();

        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                if ("HNGK".equals(areaCode))
                {
                    npOutInfos = TradeNpQry.getNpOutInfos(data.getString("USER_ID"));

                }
                else
                {
                    npOutInfos = TradeNpQry.getNpOutInfos(data.getString("USER_ID"), param.getString("AREA_CODE"));

                }

                if (IDataUtil.isNotEmpty(npOutInfos))
                {
                    data.putAll(npOutInfos.getData(0));
                    list.add(data);
                }

            }
        }

        if (IDataUtil.isNotEmpty(list))
        {
            for (int i = 0, len = list.size(); i < len; i++)
            {
                IData data = list.getData(i);

                String custUserId = data.getString("CUST_USER_ID", "");
                String user_cust_id = data.getString("USER_CUST_ID", "");
                String sn = data.getString("SERIAL_NUMBER", "");
                String acceptDate = data.getString("ACCEPT_DATE", "");
                String member_kind = data.getString("MEMBER_KIND", "");
                String user_id = data.getString("USER_ID");

                if (!"".equals(custUserId))
                {
                    String member_name = StaticUtil.getStaticValue("GMB_MEMBERKIND", member_kind);
                    data.put("MEMBER_KIND", member_name);
                    IDataset groups = GrpMebInfoQry.qryGroupInfoByMemberCustId(user_cust_id);
                    if (IDataUtil.isNotEmpty(groups))
                    {
                        String cust_manager_id = groups.getData(0).getString("CUST_MANAGER_ID");
                        String city_code = groups.getData(0).getString("CITY_CODE");
                        String staff_name = UStaffInfoQry.getStaffNameByStaffId(cust_manager_id);
                        String serialNumber = UStaffInfoQry.getStaffSnByStaffId(cust_manager_id);
                        String area_name = UAreaInfoQry.getAreaNameByAreaCode(city_code);

                        //data.put("CITY_CODE", area_name);
                        data.put("CUST_MANAGER_NAME", staff_name);
                        data.put("CUST_MANAGER_PHONE", serialNumber);
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
                IDataset userIntfs = TradeNpQry.getUserIntf(user_id);
                String active_giftbag = "", active_terminal = "";
                if (IDataUtil.isNotEmpty(userIntfs))
                {
                    IData userIntf = userIntfs.getData(0);
                    active_giftbag = userIntf.getString("RSRV_NUM1", "");
                    active_terminal = userIntf.getString("RSRV_NUM2", "");
                }
                data.put("ACTIVE_GIFTBAG", active_giftbag);
                data.put("ACTIVE_TERMINAL", active_terminal);
            }
        }
        return list;
    }
    
    
    public IDataset getNpOutInfos(IData param) throws Exception
    {
        String areaCode = param.getString("AREA_CODE", "");
        String isReturn = param.getString("IS_RETURN", "");
        String start_date = param.getString("START_DATE", "");
        String serial_number = param.getString("SERIAL_NUMBER", "");
        String end_date = param.getString("END_DATE", "");

        String tag = start_date.substring(5, 6);
        String month = "";
        if ("0".equals(tag))
        {
            month = start_date.substring(6, 7);// 提取月
        }
        else
        {
            month = start_date.substring(5, 7);// 提取月
        }
        
        start_date = start_date+SysDateMgr.START_DATE_FOREVER;
        end_date = end_date +SysDateMgr.END_DATE;
        
        String staffid =  getVisit().getStaffId();
        String cityCode = param.getString("TRADE_CITY_CODE");
        IDataset staffs = StaffInfoQry.qryStaffInfoByStaffId(staffid);
        if(IDataUtil.isNotEmpty(staffs)){
            if (!areaCode.equals("HNAL"))
            {
                String cityCode1 = staffs.getData(0).getString("CITY_CODE","");
                if(StringUtils.isNotBlank(cityCode1)){
                    cityCode = cityCode1;
                }
                if ("HNSJ".equals(cityCode) || "HNYD".equals(cityCode) || "HNKF".equals(cityCode))
                {
                    if ("0898".equals(areaCode) || "HNSJ".equals(areaCode) || "HNYD".equals(areaCode) || "HNKF".equals(areaCode))
                    {
                        param.put("AREA_CODE", "HAIN");
                    }
                }
                else
                {
                    if (!areaCode.equals(cityCode))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "你归属区域[" + cityCode + "]，你没有权限查询区域[" + areaCode + "]的数据！");

                    }
                }
            }
            else if (areaCode.equals("HNAL"))
            {
                if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_QUERYNPRETURNVISITEXPORT"))
                {
                    param.put("AREA_CODE", "HAIN");
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "你没有权限查询所有区域的数据！");

                }
                // if(pd.getContext().hasPriv("CRM_QUERYNPRETURNVISITEXPORT")){
                // inparam.put("AREA_CODE", "HAIN");
                // }else{
                // common.warn("你没有权限查询所有区域的数据！");
                // return null;
                // }
            }
        }else{
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取当前登录工号信息出错！");
        }
        
        IDataset ids = null;

        IDataset npOutInfos = null;

        IDataset list = new DatasetList();

        IDataset soasDataset = TradeNpQry.getNpReturnSoaInfosByNpCode(isReturn,month, serial_number, start_date, end_date, null);
        if(IDataUtil.isNotEmpty(soasDataset)){
            for(int i=0,len = soasDataset.size();i<len;i++){
                IData data  = soasDataset.getData(i);
                
                if ("HNGK".equals(areaCode))
                {
                    npOutInfos = TradeNpQry.getNpOutInfos(data.getString("USER_ID"));

                }
                else
                {
                    npOutInfos = TradeNpQry.getNpOutInfos(data.getString("USER_ID"),param.getString("AREA_CODE"));

                }
                
                if(IDataUtil.isNotEmpty(npOutInfos)){
                    data.putAll(npOutInfos.getData(0));
                    list.add(data);
                }
            }
            
        }
            
       
        
//        if(IDataUtil.isNotEmpty(npOutInfos)){
//            for(int i =0,len =npOutInfos.size();i<len;i++){
//                IData data  = npOutInfos.getData(i);
//                IDataset uifs = TradeNpQry.getReturnVisitForUif(isReturn, data.getString("FLOW_ID"));
//                if(IDataUtil.isNotEmpty(uifs)){
//                    data.putAll(uifs.getData(0));
//                    list.add(data);
//                }
//            }
//        }
       

        if (IDataUtil.isNotEmpty(list))
        {
            for (int i = 0, len = list.size(); i < len; i++)
            {
                IData data = list.getData(i);

                String brandName = UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND_CODE"));
                data.put("BRAND_NAME", brandName);
                
                
                String custUserId = data.getString("CUST_USER_ID", "");
                String user_cust_id = data.getString("USER_CUST_ID", "");
                String sn = data.getString("SERIAL_NUMBER", "");
                String acceptDate = data.getString("ACCEPT_DATE", "");
                String member_kind = data.getString("MEMBER_KIND", "");
                String user_id = data.getString("USER_ID");

                if (!"".equals(custUserId))
                {
                    String member_name = StaticUtil.getStaticValue("GMB_MEMBERKIND", member_kind);
                    data.put("MEMBER_KIND", member_name);
                    IDataset groups = GrpMebInfoQry.qryGroupInfoByMemberCustId(user_cust_id);
                    if (IDataUtil.isNotEmpty(groups))
                    {
                        String cust_manager_id = groups.getData(0).getString("CUST_MANAGER_ID");
                        String city_code = groups.getData(0).getString("CITY_CODE");
                        String staff_name = UStaffInfoQry.getStaffNameByStaffId(cust_manager_id);
                        String serialNumber = UStaffInfoQry.getStaffSnByStaffId(cust_manager_id);
                        String area_name = UAreaInfoQry.getAreaNameByAreaCode(city_code);

                        //data.put("CITY_CODE", area_name);
                        data.put("CUST_MANAGER_NAME", staff_name);
                        data.put("CUST_MANAGER_PHONE", serialNumber);
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
                IDataset userIntfs = TradeNpQry.getUserIntf(user_id);
                String active_giftbag = "", active_terminal = "";
                if (IDataUtil.isNotEmpty(userIntfs))
                {
                    IData userIntf = userIntfs.getData(0);
                    active_giftbag = userIntf.getString("RSRV_NUM1", "");
                    active_terminal = userIntf.getString("RSRV_NUM2", "");
                }
                data.put("ACTIVE_GIFTBAG", active_giftbag);
                data.put("ACTIVE_TERMINAL", active_terminal);
            }
        }
        return list;
    }

    public IDataset getNpSoasBySnUserId(IData param) throws Exception
    {
        String sn = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        return TradeNpQry.getNpSoasBySnUserId(sn, userId);
    }

    public IData submitNpInfo(IData param) throws Exception
    {

        String month = param.getString("MONTH").trim();
        String messageId = param.getString("MESSAGE_ID").trim();
        String serialNumber = param.getString("SERIAL_NUMBER").trim();
        String acceptDate = param.getString("ACCEPT_DATE").trim();
        String npReason = param.getString("NP_REASON").trim();

        String remark1 = param.getString("REMARK1", "").trim();
        String npMeasure = param.getString("NP_MEASURE").trim();
        String remark2 = param.getString("REMARK2", "").trim();
        IData data = new DataMap();
        data.put("MONTH", month);

        data.put("MESSAGE_ID", messageId);
        data.put("NP_CODE", serialNumber);
        data.put("CREATE_TIME", acceptDate);
        data.put("RSRV_STR4", npReason);
        data.put("RSRV_STR5", remark1);
        data.put("RSRV_STR6", npMeasure);
        data.put("RSRV_STR7", remark2);

        // --------------add by wukw3 20111017 Start-----------------------
        // 增加回访记录提交员工工号、提交时间、最后一次修改时间；
        data.put("RSRV_STR8", getVisit().getStaffId());
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("RSRV_STR9", SysDateMgr.getSysDate());
        // --------------add by wukw3 20111017 End-------------------------

        Dao.save("TL_B_NPTRADE_SOA", data, new String[]
        { "MESSAGE_ID", "NP_CODE", "MONTH", "CREATE_TIME" }, Route.CONN_UIF);
        param.put("MSG", "提交成功！");
        return param;
    }

}
