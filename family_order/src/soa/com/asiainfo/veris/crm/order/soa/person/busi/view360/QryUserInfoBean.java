
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import java.util.Calendar;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.QryViewDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.SerialNumberBQry;

public class QryUserInfoBean extends CSBizBean
{
    /**
     * @author luoz
     * @date 2013-07-25
     * @description 查询提示信息
     * @param userInfo
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public String qryPopuInfo(IData userInfo, String tradeTypeCode) throws Exception
    {
        String rtnPopuInfo = "";
        // add by caiy 增加查询客户综合资料时显示左边的业务信息
        IData hintData = new DataMap();
        // 有疑问的地方
        hintData.put("USER_STATE", "用户状态：" + userInfo.getString("X_SVCSTATE_EXPLAIN", ""));
        String rsrv_str9 = "";
        String rsrv_str10 = "";
        IData in = new DataMap();
        in.putAll(userInfo);
        in.put("TRADE_STAFF_ID", getVisit().getStaffId());
        in.put("TRADE_CITY_CODE", getVisit().getCityCode());
        in.put("TRADE_DEPART_ID", getVisit().getDepartId());
        // 修改，因为客服没有logineparchy，所以直接用用户号码的eparchy
        in.put("TRADE_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        in.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));

        // in.put("TRADE_TYPE_CODE","2101");//客户资料综合查询
        in.put("TRADE_TYPE_CODE", tradeTypeCode);
        in.put("RSRV_STR9", rsrv_str9);
        in.put("RSRV_STR10", rsrv_str10);

        in.put("CUST_NAME", userInfo.getString("USECUST_NAME", ""));
        in.put("VIP_CARD_NO", userInfo.getString("VIP_CARD_NO", ""));
        in.put("BIRTHDAY", userInfo.getString("BIRTHDAY", ""));
        in.put("VIP_TYPE_CODE", userInfo.getString("VIP_TYPE_CODE", ""));
        in.put("VIP_CLASS_ID", userInfo.getString("VIP_CLASS_ID", ""));
        in.put("CUST_MANAGER_ID", userInfo.getString("CUST_MANAGER_ID", ""));

        String brandNo = userInfo.getString("BRAND_NO", "1");

        if (brandNo.equals("1"))
        {
            in.put("GROUP_BRAND", "全球通");
        }
        else if (brandNo.equals("3"))
        {
            in.put("GROUP_BRAND", "动感地带");
        }
        else if (brandNo.equals("4"))
        {
            in.put("GROUP_BRAND", "神州行大众卡");
        }
        else
        {
            in.put("GROUP_BRAND", "神州行");
        }

        IDataset results = new DatasetList();
        IData result = new DataMap();
        try
        {
            // results = TuxedoHelper.callTuxSvc(pageData, "QCS_GetHintInfo", in,true);
            results = CSAppCall.call("CS.HintInfoSVC.getHintInfo", in);
        }
        catch (Exception e)
        {
            results = null;
        }

        if (results != null && results.size() > 0)
        {
            result = (IData) results.get(0);

            int resultCode = result.getInt("X_RESULTCODE");
            String resultInfo = result.getString("X_RESULTINFO");

            if (resultCode == 0)
            {
                String rsrv_str = "";

                rsrv_str9 = result.getString("RSRV_STR9", "");
                rsrv_str10 = result.getString("RSRV_STR10", "");

                if (!"".equals(rsrv_str9) || !"".equals(rsrv_str10))
                {
                    rsrv_str = rsrv_str9 + "~" + rsrv_str10;
                }
                else
                {
                    rtnPopuInfo = "";
                }

                String[] strArry = rsrv_str.split("~");
                int num = 1;

                for (int i = 0; i < strArry.length; i++)
                {
                    if ("".equals(strArry[i]))
                    {
                        continue;
                    }
                    hintData.put("RSRV_STR" + num, strArry[i]);

                    num++;
                }
                rtnPopuInfo = hintData.toString();
            }
            else
            {
                // common.error("701020:" + resultInfo);
                CSAppException.apperr(CustException.CRM_CUST_994, resultInfo);
            }
        }
        else
        {
            rtnPopuInfo = "";
        }
        return rtnPopuInfo;
    }

    /**
     * 查询接入号码信息
     * 
     * @param param
     * @param pagination
     * @return IDataset
     * @throws Exception
     */
    public IDataset qrySerialNumberBInfo(IData param) throws Exception
    {
        SerialNumberBQry dao = new SerialNumberBQry();
        String serialNumber = param.getString("SERIAL_NUMBER");
        String serialNumberB = param.getString("SERIAL_NUMBER_B");
        String IS_VIP_MANAGER = SerialNumberBQry.getIfManager(serialNumberB);
        String VIP_MANAGER_PASS = SerialNumberBQry.getManagerPass(serialNumber, serialNumberB);
        String IS_BOTH_SN = SerialNumberBQry.getIfBothSN(serialNumberB);

        IDataset out = new DatasetList();
        IData result = new DataMap();
        result.put("IS_VIP_MANAGER", IS_VIP_MANAGER);
        result.put("VIP_MANAGER_PASS", VIP_MANAGER_PASS);
        result.put("IS_BOTH_SN", IS_BOTH_SN);
        out.add(result);
        return out;
    }

    /**
     * 根据SIM卡号查询手机号码
     * 
     * @param pd
     * @param sim_number
     * @return
     */
    public IDataset qrySerialNumberBySim(IData param, Pagination pagination) throws Exception
    {
        QryViewDAO dao = new QryViewDAO();
        return dao.qrySerialNumberBySim(param, pagination);
    }

    /**
     * 查询用户信息（通过serial_number） used
     * 
     * @param param
     * @param pagination
     * @return IDataset
     * @throws Exception
     */
    public IDataset qryUserInfoBySerialNumber(IData param, Pagination pagination) throws Exception
    {
        QryViewDAO dao = new QryViewDAO();
        IDataset out = dao.qryUserInfoBySerialNumber(param, pagination);
        return out;
    }
    public IDataset qry_tf_sm_bi_mmsfunc_InfoByUserId(IData param, Pagination pagination) throws Exception
    {
        QryViewDAO dao = new QryViewDAO();
        IDataset out = dao.qry_tf_sm_bi_mmsfunc_InfoByUserId(param, pagination);
        return out;
    }

    /**
     * @author luoz
     * @date 2013-07-24
     * @description 写台账日志，直接插台账历史表。
     * @param param
     * @return
     * @throws Exception
     */

    public boolean writeTradeQueryLog(String userId, String netTypeCode, String serialNumber, String eparchyCode, String cityCode, String productId, String brandCode, String custId) throws Exception
    {
        IData param = new DataMap();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        String trade_date = SysDateMgr.getSysTime();
        String trade_staff_id = getVisit().getStaffId();
        String trade_staff_name = getVisit().getStaffName();
        String trade_staff_depart = getVisit().getDepartId();
        String trade_eparchy_code = CSBizBean.getTradeEparchyCode();
        String trade_city_code = getVisit().getCityCode();

        String trade_id = SeqMgr.getTradeId();
        if (trade_id == null || "".equals(trade_id))
        {
            CSAppException.apperr(CustException.CRM_CUST_993, "获取TRADE_ID失败");
        }

        param.put("USER_ID", userId);
        param.put("NET_TYPE_CODE", netTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CITY_CODE", cityCode);
        param.put("PRODUCT_ID", productId);
        param.put("BRAND_CODE", brandCode);
        param.put("CUST_ID", custId);

        param.put("TRADE_ID", trade_id);
        param.put("ACCEPT_MONTH", month + "");
        param.put("BPM_ID", "");
        param.put("TRADE_TYPE_CODE", "2101");
        param.put("PRIORITY", "200");
        param.put("SUBSCRIBE_TYPE", "0");
        param.put("SUBSCRIBE_STATE", "0");
        param.put("NEXT_DEAL_TAG", "0");
        param.put("IN_MODE_CODE", getVisit().getInModeCode());// 接入方式编码----------- "0"
        // param.put("CUST_NAME", "");
        param.put("ACCT_ID", "");
        param.put("CUST_ID_B", "");
        param.put("USER_ID_B", "");
        param.put("ACCT_ID_B", "");
        param.put("SERIAL_NUMBER_B", "");
        param.put("CUST_CONTACT_ID", "");
        param.put("SERV_REQ_ID", "");
        param.put("ACCEPT_DATE", trade_date);
        param.put("TRADE_STAFF_ID", trade_staff_id);
        param.put("TRADE_DEPART_ID", trade_staff_depart);
        param.put("TRADE_CITY_CODE", trade_city_code);
        param.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        param.put("TERM_IP", getVisit().getRemoteAddr());
        param.put("OPER_FEE", "0");
        param.put("FOREGIFT", "0");
        param.put("ADVANCE_PAY", "0");
        param.put("FEE_STATE", "0");
        param.put("PROCESS_TAG_SET", "                    ");
        param.put("OLCOM_TAG", "0");
        param.put("FINISH_DATE", trade_date);
        param.put("EXEC_TIME", trade_date);
        param.put("CANCEL_TAG", "0");
        param.put("UPDATE_TIME", trade_date);
        param.put("UPDATE_STAFF_ID", trade_staff_id);
        param.put("UPDATE_DEPART_ID", trade_staff_depart);
        param.put("REMARK", "用户360资料综合查询");
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", trade_staff_name);
        param.put("RSRV_STR3", trade_staff_id);
        param.put("RSRV_STR4", trade_staff_id + trade_staff_name);

        // update by zhouwei 20100108 根据参数决定插不同的历史表
        IData typedata = UTradeTypeInfoQry.getTradeType("2101", trade_eparchy_code);
        // 默认插入TF_BH_TRADE表
        String tradeTable = (null == typedata || "".equals(typedata.getString("RSRV_STR1", ""))) ? "TF_BH_TRADE" : typedata.getString("RSRV_STR1");

        Dao.insert(tradeTable, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        return true;
    }

}
