
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class MDENSmsGatherOrigSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset gatherSms(IData param) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
        String smsContent = IDataUtil.chkParam(param, "SMS_CONTENT");
        String accptDate = IDataUtil.chkParam(param, "ACCEPT_DATE");
        String destination = IDataUtil.chkParam(param, "DESTINATION");

        IData result = new DataMap();
        IData inparam = new DataMap();
        inparam.put("PROVINCE_CODE", CSBizService.getVisit().getProvinceCode());// 省别编码
        inparam.put("IN_MODE_CODE", "0");// 接入方式：td_s_static 表 type_id ='OPEN_INMODECODE'

        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 交易地州编码

        inparam.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
        inparam.put("ROUTEVALUE", "000");

        inparam.put("KIND_ID", "BIP2B020_T2001015_0_0");// 交易唯一标识

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("DESTINATION", destination);// 目的号码
        inparam.put("ACCEPT_DATE", accptDate);
        inparam.put("SMS_CONTENT", smsContent);

        // IData data = (IData)HttpHelper.callHttpSvc(pd, "IBOSS", inparam);
        IDataset dataSet = IBossCall.dealInvokeUrl("BIP2B020_T2001015_0_0", "IBOSS7", inparam);
        IData data = dataSet.getData(0);
        if ("0".equals(data.getString("X_RSPTYPE")) && "0000".equals(data.getString("X_RSPCODE")))
        {
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPDESC", "success");
        }
        else
        {
            result.put("X_RESULTCODE", data.getString("X_RSPCODE"));// 用户信息不存在
            result.put("X_RESULTINFO", data.getString("X_RSPDESC"));
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }
        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }
}
