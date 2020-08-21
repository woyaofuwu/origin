
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class GrpChangeOutNetSnIntf
{

    /**
     * 异网虚拟同步
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset VirCodeSync(IData data) throws Exception
    {
        String tradeId = IDataUtil.getMandaData(data, "TRADE_ID");
        String opr = "";
        String str10 = "";
        String str11 = "";
        IDataset dataList = TradeOtherInfoQry.getGrpOtherByTrade(tradeId, Route.CONN_CRM_CG, null);
        if (IDataUtil.isNotEmpty(dataList))
        {
            for (int i = 0; i < dataList.size(); i++)
            {
                IData param = dataList.getData(i);
                opr = param.getString("RSRV_STR1", "");

                if ("05".equals(opr))
                {
                    str10 = param.getString("RSRV_STR2", "");
                    str11 = param.getString("RSRV_STR3", "");
                    break;
                }
            }
        }
        IData param = new DataMap();
        param.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        String sysDate4Id = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
        param.put("OPERATE_ID", sysDate4Id);
        param.put("OPR", opr);
        param.put("RSRV_STR10", str10);
        param.put("RSRV_STR11", str11);
        param.put("KIND_ID", "BIP3A001_T2101001_0_0");
        IDataset datas = IBossCall.callHttpIBOSS("IBOSS", param);
        return datas;
    }

    /**
     * 删除异网虚拟号码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset delOutNetsn(IData data) throws Exception
    {
        String tradeId = IDataUtil.getMandaData(data, "TRADE_ID");
        String opr = "";
        String str10 = "";
        String str11 = "";
        String othertag = "";

        IDataset dataList = TradeOtherInfoQry.getGrpOtherByTrade(tradeId, Route.CONN_CRM_CG, null);
        if (IDataUtil.isNotEmpty(dataList))
        {
            for (int i = 0; i < dataList.size(); i++)
            {
                IData param = dataList.getData(i);
                othertag = param.getString("RSRV_VALUE_CODE", "");
                opr = param.getString("RSRV_STR1", "");

                if (opr.equals("02") && othertag.equals("ONSN"))
                {
                    str10 = param.getString("RSRV_STR2", "");
                    str11 = param.getString("RSRV_STR3", "");
                    break;
                }
            }
        }

        data.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码 ;
        String SysDate4Id = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
        data.put("OPERATE_ID", SysDate4Id);
        data.put("OPR", opr);
        data.put("RSRV_STR10", str10);
        data.put("RSRV_STR11", str11);
        data.put("KIND_ID", "BIP3A001_T2101001_0_0");

        IDataset datas = IBossCall.callHttpIBOSS("IBOSS", data);
        return datas;
    }

    /**
     * 调IBOSS接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset creatOutNetsn(IData data) throws Exception
    {
        String tradeId = IDataUtil.getMandaData(data, "TRADE_ID");
        String opr = "";
        String str10 = "";
        String str11 = "";
        String othertag = "";

        IDataset dataList = TradeOtherInfoQry.getGrpOtherByTrade(tradeId, Route.CONN_CRM_CG, null);
        if (IDataUtil.isNotEmpty(dataList))
        {
            for (int i = 0; i < dataList.size(); i++)
            {
                IData param = dataList.getData(i);
                othertag = param.getString("RSRV_VALUE_CODE", "");
                opr = param.getString("RSRV_STR1", "");

                if (opr.equals("01") && othertag.equals("ONSN"))
                {
                    str10 = param.getString("RSRV_STR2", "");
                    str11 = param.getString("RSRV_STR3", "");
                    break;
                }
            }
        }

        data.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        String SysDate4Id = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
        data.put("OPERATE_ID", SysDate4Id);
        data.put("OPR", opr);
        data.put("RSRV_STR10", str10);
        data.put("RSRV_STR11", str11);
        data.put("KIND_ID", "BIP3A001_T2101001_0_0");

        IDataset datas = IBossCall.callHttpIBOSS("IBOSS", data);
        return datas;
    }
}
