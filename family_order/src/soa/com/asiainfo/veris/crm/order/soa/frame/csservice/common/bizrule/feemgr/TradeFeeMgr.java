
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bizrule.feemgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.svc.BreEngine;

public class TradeFeeMgr
{

    /**
     * 得到有价卡销售费用
     * 
     * @author luoy
     * @throws Exception
     */
    public static IDataset getCardSaleFee(IData params) throws Exception
    {

        String captypeCode = params.getString("CAPACITY_TYPE_CODE");
        if ((captypeCode == null) || captypeCode.equals(""))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_125);
        }
        IData idataRule = new DataMap();

        params.put("RULE_BIZ_TYPE_CODE", "TradeFeeMgr");
        params.put("RULE_BIZ_KIND_CODE", "CardSaleFee");
        idataRule = BreEngine.bre4SuperLimit(params);
        IDataset feeList = (DatasetList) idataRule.get("feelist");
        return feeList;
    }

    /**
     * 得到补换卡费用
     * 
     * @author luoy
     * @throws Exception
     */
    public static IDataset getChgSimCardFee(IData params) throws Exception
    {

        String userID = params.getString("USER_ID");
        if ((userID == null) || userID.equals(""))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_630);
        }
        IData idataRule = new DataMap();

        params.put("RULE_BIZ_TYPE_CODE", "TradeFeeMgr");
        params.put("RULE_BIZ_KIND_CODE", "ChgSimCardFee");
        idataRule = BreEngine.bre4SuperLimit(params);
        IDataset feeList = (DatasetList) idataRule.get("feelist");
        return feeList;
    }

    public static IDataset getChgSvcFee(IData params) throws Exception
    {

        // 得到服务编码
        String svcId = params.getString("SVC_ID", "");
        // 业务类型编码
        String tradeTypeCode = params.getString("TRADE_TYPE_CODE", "");
        IDataset dataList = new DatasetList();
        IData data = null;
        if (svcId.equals("15")) // 国际长途
        {
            boolean b = false;
            // 得到大客户类型
            String vipTypeCode = params.getString("VIP_TYPE_CODE", "");
            // 得到大客户级别
            String vipClassId = params.getString("VIP_CLASS_ID", "");
            if (vipTypeCode.equals("0")) // 商业
            {
                if (vipClassId.equals("3") || vipClassId.equals("4"))
                {
                    b = true;
                }
            }
            else if (vipTypeCode.equals("2")) // 重要
            {
                if (vipClassId.equals("3") || vipClassId.equals("4"))
                {
                    b = true;
                }
            }
            else if (vipTypeCode.equals("0")) // 战略
            {
                if (vipClassId.equals("3") || vipClassId.equals("4"))
                {
                    b = true;
                }
            }
            if (b)
            {
                data = new DataMap();
                data.put("FEE_MODE", "1"); // 押金
                data.put("FEE_TYPE_CODE", "8"); // 开国际押金
                data.put("FEE", "-300000"); // 单位 分
                dataList.add(data);
            }
        }
        return dataList;
    }

    /**
     * 集团业务一次性费用接口
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset getGrpOneOffFee(IData inData) throws Exception
    {

        inData.put("RULE_BIZ_TYPE_CODE", "TradeFeeMgr");
        inData.put("RULE_BIZ_KIND_CODE", "GrpOneOffFee");
        inData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());

        IData resultData = BreEngine.bre4SuperLimit(inData);
        return resultData.getDataset("FEELIST");
    }

    /**
     * 得到营业业务操作费，一般都不收费，所以返回空
     * 
     * @author luoy
     * @throws Exception
     */
    public static IDataset getOperFee(IData params) throws Exception
    {

        IDataset dataList = new DatasetList();
        // IData data = new DataMap();
        //
        // // 营业费用参数表TD_B_OPERFEE，目前云南没有数据，估计其它省也没有
        //
        // // 假定输出几个，用于测试
        //
        // data = new DataMap();
        //
        // data.put("FEE_MODE", "0"); // 营业费用
        //
        // data.put("FEE_TYPE_CODE", "10"); // 购卡费
        //
        // data.put("FEE", "10000"); // 100 元
        //
        // dataList.add(data);
        //
        // data = new DataMap();
        //
        // data.put("FEE_MODE", "0"); // 营业费用
        //
        // data.put("FEE_TYPE_CODE", "30"); // 普通选号费
        //
        // data.put("FEE", "30000"); // 300 元
        //
        // dataList.add(data);
        return dataList;
    }

    /**
     * 得到资源号费
     * 
     * @author luoy
     * @throws Exception
     */
    public static IDataset getResSnFee(IData params) throws Exception
    {

        IDataset list = CSAppCall.call("QCS_getResSnFee", params);
        return list;
    }

    /**
     * 营业费用接口
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset TradeOperFee(IData inData) throws Exception
    {

        inData.put("RULE_BIZ_TYPE_CODE", "TradeOperFee");
        BreEngine.bre4SuperLimit(inData);
        return (IDataset) inData.get("feeList");
    }
}
