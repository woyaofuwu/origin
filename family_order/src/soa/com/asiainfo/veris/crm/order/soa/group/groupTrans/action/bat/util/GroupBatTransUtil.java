
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class GroupBatTransUtil
{
    /**
     * 构建产品参数信息
     *
     * @param productId
     *            产品ID
     * @param productParam
     *            产品参数
     * @param productParamDataset
     *            保存产品参数的结果DatasetList
     * @throws Exception
     */
    public static void buildProductParam(String productId, IData productParam, IDataset productParamDataset) throws Exception
    {
        IData mapData = new DataMap();
        mapData.put("PRODUCT_ID", productId);
        mapData.put("PRODUCT_PARAM", IDataUtil.iData2iDataset(productParam, "ATTR_CODE", "ATTR_VALUE"));
        productParamDataset.add(mapData);
    }

    /**
     * 获取成员的账户信息
     *
     * @param user_id
     * @param userEparchyCode
     * @return
     * @throws Exception
     */
    public static IData getMemAcctData(String user_id, String userEparchyCode) throws Exception
    {
        // 查有效付费关系
        IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(user_id, userEparchyCode);

        if (IDataUtil.isEmpty(payrela))
        {
            payrela = UcaInfoQry.qryLastPayRelaByUserId(user_id, userEparchyCode);

            if (IDataUtil.isEmpty(payrela))
            {
                CSAppException.apperr(BofException.CRM_BOF_003);
            }
        }

        IData acct = UcaInfoQry.qryAcctInfoByAcctId(payrela.getString("ACCT_ID"), userEparchyCode);
        if (IDataUtil.isEmpty(acct))
        {
            CSAppException.apperr(BofException.CRM_BOF_004);
        }

        return acct;

    }

    public static IData getGrpAcctData(String user_id) throws Exception
    {
        // 查有效付费关系
        IData payrela = UcaInfoQry.qryPayRelaByUserIdForGrp(user_id);

        if (IDataUtil.isEmpty(payrela))
        {
            CSAppException.apperr(BofException.CRM_BOF_003);
        }

        String acctId = payrela.getString("ACCT_ID");
        IData acct = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);

        if (IDataUtil.isEmpty(acct))
        {
            CSAppException.apperr(BofException.CRM_BOF_004);
        }

        return acct;
    }


    //批量业务，校讯通平台校验学生信息
    public static IDataset checkStuInfoForXXTPlat(IData httpData) throws Exception
    {
        IData httpStr = new DataMap();
        httpStr.put("EC_ID", httpData.getString("EC_ID"));
        httpStr.put("MOB_NUM", httpData.getString("MOB_NUM"));
        httpStr.put("FEE_MOB_NUM", httpData.getString("FEE_MOB_NUM"));
        httpStr.put("STU_NAME", httpData.getString("STU_NAME"));
        httpStr.put("OPR_CODE", httpData.getString("OPR_CODE"));
        httpStr.put("KIND_ID", httpData.getString("KIND_ID"));

        String kindId = httpData.getString("KIND_ID");

        return  IBossCall.dealInvokeUrl(kindId, "IBOSS7", httpStr);
    }

    public static IDataset checkMemberForXHK(IData httpData) throws Exception
    {
        IData httpStr = new DataMap();
        httpStr.put("EC_ID", httpData.getString("EC_ID"));
        httpStr.put("MOB_NUM", httpData.getString("MOB_NUM"));
        httpStr.put("OPR_CODE", httpData.getString("OPR_CODE"));
        httpStr.put("BIZ_SERV_CODE", httpData.getString("BIZ_SERV_CODE"));
        httpStr.put("KIND_ID", httpData.getString("KIND_ID"));

        String kindId = httpData.getString("KIND_ID");

        return  IBossCall.dealInvokeUrl(kindId, "IBOSS", httpStr);
    }

}
