
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.queryvipcustinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;

public class QueryVIPCustInfoBean extends CSBizBean
{
    /**
     * 记录操作历史
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private String insTradeHis(IData data) throws Exception
    {
        String systime = SysDateMgr.getSysTime();
        String trade_id = SeqMgr.getTradeId();
        String order_id = SeqMgr.getOrderId();
        String trade_type_code = "2101";
        String net_type_code = "00";

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", trade_id);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", order_id);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);
        inparam.put("PRIORITY", "0");
        inparam.put("SUBSCRIBE_TYPE", "0");
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", "");
        inparam.put("CUST_NAME", "");
        inparam.put("USER_ID", "");
        inparam.put("ACCT_ID", "");
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", net_type_code);
        inparam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", "");
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", "0");
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "                    ");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", data.getString("REMARK"));

        Dao.insert("TF_BH_TRADE", inparam);
        return order_id;
    }

    /**
     * 异地大客户信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryVIPCustInfo(IData data) throws Exception
    {
        String kindId = "BIP1A001_T1000002_0_0";
        String xTransCode = "";
        String routeType = data.getString("ROUTETYPE"); // 路由类型 00-省代码，01-手机号
        String routeValue = "";
        if ("00".equals(routeType))
        {
            routeValue = data.getString("PROVINCE_CODE");
        }
        else
        {
            routeValue = data.getString("MOBILENUM");
        }
        String serialNumber = data.getString("MOBILENUM");
        String idType = data.getString("IDTYPE");
        String idValue = data.getString("IDVALUE");
        // String idCardType = data.getString("IDCARDTYPE");
        String idCardType = "99";
        String idCardNum = data.getString("IDCARDNUM");
        String userPasswd = data.getString("USER_PASSWD");
        String typeIdSet = "5";
        /*
         * 0 基本资料 1 实时话费 2 账户资料 3 账本资料 4 帐单资料 5 大客户资料 6 积分信息 8 业务开通资料
         */

        IDataset dataset = new DatasetList();
        try
        {
             dataset = IBossCall.queryVIPCustInfo(kindId, xTransCode, routeType, routeValue, serialNumber, idType,
             idValue, idCardType, idCardNum, userPasswd, typeIdSet);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // 写tf_bh_trade记录日志
            String remark = "未处理请求！";
            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark = xRspCode + ":" + xRspDesc;
            }
            data.put("REMARK", remark);
            insTradeHis(data);
        }

        // TODO 测试数据

//        dataset.clear();
//        IData tmp = new DataMap();
//        tmp.put("REGISTER_NAME", "IBOSS无法返回，测试数据");
//        tmp.put("SEX_NAME", "男");
//        tmp.put("AGE", "10000");
//        tmp.put("USERSCORE", "50000");
//        tmp.put("X_RSPCODE", "0000");
//        tmp.put("X_RSPDESC", "查询成功");
//        dataset.add(tmp);
//        tmp.put("X_RSPTYPE", "0");
//        tmp.put("CLASS_ID", "100");
//        tmp.put("LEVEL", "1");
//        tmp.put("USER_STATE_CODESET", "01");
//        tmp.put("CUST_NAME", "100");
//        tmp.put("ALL_BALANCE", "1");
//        tmp.put("IDCARDTYPE", "1");
//        tmp.put("CUST_NAME", "100");
//        tmp.put("ALL_BALANCE", "1");
//        tmp.put("IDCARDTYPE", "1");
//        tmp.put("USERRANK", "100");
//        tmp.put("SCORE", "1222");
//        tmp.put("VIP_NO", "112222222221");
//        tmp.put("BRAND_CODE", "01");
//        tmp.put("PUK", "1222");
//        tmp.put("SVCPHNUM", "112222222221");
//        tmp.put("GPRS_TAG", "1");
//        // TODO， 员工操作日志，配置服务实现即可

        IData vipInfo = dataset.getData(0);
        vipInfo = transCustInfo(vipInfo, vipInfo);
        IDataset ret = new DatasetList();
        ret.add(vipInfo);
        return ret;
    }

    public IData transCustInfo(IData vipInfo, IData input) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        vipInfo.put("IDCARDTYPE", logutil.encodeIdType(input.getString("IDCARDTYPE")));

        String cust_class_type = "";
        if (input.getString("CLASS_ID") != null)
        {
            cust_class_type = logutil.decodeCustClassType(input.getString("CLASS_ID"));
        }
        else if (input.getString("LEVEL") != null)
        {
            cust_class_type = StaticUtil.getStaticValue("IBOSS_LEVEL", input.getString("LEVEL"));
        }

        String userStatusName = "";
        if (input.getString("USER_STATE_CODESET") != null)
        {
            userStatusName = StaticUtil.getStaticValue("INTERBOSS_STATUS", input.getString("USER_STATE_CODESET"));
        }

        vipInfo.put("USERRANK", cust_class_type);
        vipInfo.put("USER_STATUS_NAME", userStatusName);

        return vipInfo;
    }
}
