
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.grouptradefee;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ForeGiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PayMentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class GroupFeeListMgr extends CSBizBean
{
    /**
     * 新增费用列表数据
     * 
     * @param dataBuf
     * @param feeData
     * @throws Exception
     */
    public static void addFeeList(IDataset feeList, IData feeData) throws Exception
    {

        String tradeTypeCode = feeData.getString("TRADE_TYPE_CODE");
        String feeMode = feeData.getString("MODE");
        String feeTypeCode = feeData.getString("CODE");
        String fee = feeData.getString("FEE");

        if (StringUtils.isBlank(fee))
            return;

        int feeListTag = 0;

        if (IDataUtil.isNotEmpty(feeList))
        {
            int iSrcFee = Integer.parseInt(fee);
            int iSrcFactFee = Integer.parseInt(fee);

            for (int i = 0, size = feeList.size(); i < size; i++)
            {
                IData feeInfo = feeList.getData(i);

                String srcTradeType = feeInfo.getString("TRADE_TYPE_CODE");
                String srcFeeMode = feeInfo.getString("MODE");
                String srcFeeTypeCode = feeInfo.getString("CODE");
                String srcFee = feeInfo.getString("FEE");
                String srcFactFee = feeInfo.getString("PAY");

                if (!StringUtils.equals(tradeTypeCode, srcTradeType))
                {
                    feeInfo.put("TRADE_TYPE_CODE", tradeTypeCode); // 费用表配置的TRADE_TYPE_CODE与办理业务时的不一致 取办理业务时的
                }

                if (StringUtils.equals(feeMode, srcFeeMode) && StringUtils.equals(feeTypeCode, srcFeeTypeCode))
                {
                    iSrcFee = iSrcFee + Integer.parseInt(srcFee);
                    iSrcFactFee = iSrcFactFee + Integer.parseInt(srcFactFee);

                    feeInfo.put("FEE", String.valueOf(iSrcFee));
                    feeInfo.put("PAY", String.valueOf(iSrcFactFee));

                    feeListTag = 1;
                    break;
                }
            }

            if (feeListTag == 0)
            {
                IData feeInfo = new DataMap();
                feeInfo.putAll(feeData);
                if (!feeData.containsKey("PAY"))
                {
                    feeInfo.put("PAY", feeData.getString("FEE"));
                }
                feeList.add(feeInfo);
            }
        }
        else
        {
            IData feeInfo = new DataMap();
            feeInfo.putAll(feeData);
            if (!feeData.containsKey("PAY"))
            {
                feeInfo.put("PAY", feeData.getString("FEE"));
            }
            feeList.add(feeInfo);
        }
    }

    public static void addFeeList(IDataset feeList, String tradeTypeCode, String feeMode, String feeTypeCode, String fee) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("MODE", feeMode);
        data.put("CODE", feeTypeCode);
        data.put("FEE", fee);
        addFeeList(feeList, data);
    }

    /**
     * 查询费用大类描述
     * 
     * @param typeCode
     * @return
     * @throws Exception
     */
    public static String getFeeModeDesc(String typeCode) throws Exception
    {
        IDataset feeTypes = StaticUtil.getStaticList("CS_FEE_MODE");
        String result = "未知费用类型";
        for (int i = 0, size = feeTypes.size(); i < size; i++)
        {
            IData tmp = feeTypes.getData(i);
            if (StringUtils.equals(typeCode, tmp.getString("DATA_ID")))
            {
                result = tmp.getString("DATA_NAME");
                break;
            }
        }
        return result;
    }

    /**
     * 查询费用小类描述
     * 
     * @param feeMode
     * @param typeCode
     * @return
     * @throws Exception
     */
    public static String getFeeTypeCodeDesc(String feeMode, String typeCode) throws Exception
    {
        String result = "未知费用类型";
        IDataset temp = null;

        if ("0".equals(feeMode))
        {
            // TD_B_FEEITEM
            temp = FeeItemInfoQry.getFeeItem(CSBizBean.getTradeEparchyCode());
        }
        else if ("1".equals(feeMode))
        {
            // TD_S_FOREGIFT
            temp = ForeGiftInfoQry.getForegift();
        }
        else if ("2".equals(feeMode))
        {
            // TD_B_PAYMENT
            temp = PayMentInfoQry.getPayment();
        }
        if (temp == null)
            return result;
        for (int i = 0, size = temp.size(); i < size; i++)
        {
            IData tmp = temp.getData(i);
            if (StringUtils.equals(typeCode, tmp.getString("CODE")))
            {
                result = tmp.getString("NAME");
                break;
            }
        }
        return result;
    }

    /**
     * 查询业务办理费用
     * 
     * @param inparam
     *            [TRADE_TYPE_CODE PRODUCT_ID VIP_CLASS_ID TRADE_EPARCHY_CODE]
     * @return
     * @throws Exception
     */
    public static IDataset getTradeOperFee(IData input) throws Exception
    {
        IDataset feeList = new DatasetList();

        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "");

        IDataset results = new DatasetList();
        // 0- 业务费用, 根据业务类型编码、产品标识、大客户等级标识、地市编码获取对应业务的通用费用，进入页面需要获取的费用
        input.put("TRADE_FEE_TYPE", "0");
        results = ProductFeeInfoQry.getTradeFee4Grp(input);

        if (IDataUtil.isNotEmpty(results))
        {
            /**
             * 以下逻辑海南集团侧没有，这里直接注释 feelists = getRealfeeByRule(results);
             */
            for (int i = 0, size = results.size(); i < size; i++)
            {
                IData feelist = results.getData(i);

                String feeMode = feelist.getString("FEE_MODE");
                String feeTypeCode = feelist.getString("FEE_TYPE_CODE");
                String fee = feelist.getString("FEE", "0");

                if (StringUtils.equals(fee, "0"))
                    continue;

                addFeeList(feeList, tradeTypeCode, feeMode, feeTypeCode, fee);
            }
        }
        return feeList;
    }

}
