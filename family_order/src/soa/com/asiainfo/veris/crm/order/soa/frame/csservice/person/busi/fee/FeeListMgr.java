
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ForeGiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PayMentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class FeeListMgr extends CSBizBean
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

        if ("".equals(fee) && fee.length() == 0)
            return;

        int feeListTag = 0;

        if (feeList != null && feeList.size() > 0)
        {
            int iSrcFee = Integer.parseInt(fee);
            int iSrcFactFee = Integer.parseInt(fee);

            for (int i = 0; i < feeList.size(); i++)
            {
                IData feeInfo = new DataMap();

                feeInfo = (IData) feeList.get(i);

                String srcTradeType = feeInfo.getString("TRADE_TYPE_CODE");
                String srcFeeMode = feeInfo.getString("MODE");
                String srcFeeTypeCode = feeInfo.getString("CODE");
                String srcFee = feeInfo.getString("FEE");
                String srcFactFee = feeInfo.getString("PAY");

                if (tradeTypeCode.equals(srcTradeType) && feeMode.equals(srcFeeMode) && feeTypeCode.equals(srcFeeTypeCode))
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
     * 获取费用配置
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IData getFeeConfigInfos(IData input) throws Exception
    {
        IData data = new DataMap();

        // 费用大类
        IData feeMode = new DataMap();
        IDataset feeTypes = StaticUtil.getStaticList("CS_FEE_MODE");
        for (int i = 0; i < feeTypes.size(); i++)
        {
            IData mode = (IData) feeTypes.get(i);
            feeMode.put(mode.getString("DATA_ID", ""), mode.getString("DATA_NAME"));
        }
        data.put("FEE_MODES", feeMode);

        boolean isSuper = hasPriv("SYS002");
        data.put("SUPER", isSuper); // 超级用户权限
        data.put("SYSCHANGEACTIVEFEE", hasPriv("SYSCHANGEACTIVEFEE")); // 营销活动特殊权限
        data.put("KD001", hasPriv("KD001")); // 宽带特殊权限

        // 营业费配置数据
        IDataset feeItems = FeeItemInfoQry.getFeeItem(CSBizBean.getTradeEparchyCode());
        if (null != feeItems && feeItems.size() > 0)
        {
            IData feeItem = new DataMap();
            feeItem.put("PRIV", hasPriv("SYSOPERFEE"));
            for (int i = 0; i < feeItems.size(); i++)
            {
                IData tmp = feeItems.getData(i);
                tmp.put("PRIV", hasPriv("SYSOPERFEE" + tmp.getString("CODE")));
                feeItem.put(tmp.getString("CODE"), tmp);
            }

            data.put("0", feeItem);
        }

        // 押金配置数据
        IDataset foreGifts = ForeGiftInfoQry.getForegift();
        if (null != foreGifts && foreGifts.size() > 0)
        {
            IData foreGift = new DataMap();
            foreGift.put("PRIV", hasPriv("SYSFOREGIFT"));
            
            for (int i = 0; i < foreGifts.size(); i++)
            {
                IData tmp = foreGifts.getData(i);
                tmp.put("PRIV", hasPriv("SYSFOREGIFT" + tmp.getString("CODE")));
                foreGift.put(tmp.getString("CODE"), tmp);
            }

            data.put("1", foreGift);

        }

        // 预存配置数据
        IDataset payments = PayMentInfoQry.getPayment();
        if (null != payments && payments.size() > 0)
        {
            IData payment = new DataMap();
            payment.put("PRIV", hasPriv("SYSADVANCEPAY"));
            for (int i = 0; i < payments.size(); i++)
            {
                IData tmp = payments.getData(i);
                tmp.put("PRIV", hasPriv("SYSADVANCEPAY" + tmp.getString("CODE")));
                payment.put(tmp.getString("CODE"), tmp);
            }

            data.put("2", payment);
        }

        IDataset payMoneys = StaticUtil.getStaticList("TD_S_PAYMONEY");
        data.put("PAY_MODES", payMoneys);
        IDataset checks = StaticUtil.getStaticList("PAY_MODE_CHECK");
        if (null != checks && checks.size() > 0)
        {
            data.put("PAY_MODE_CHECK", String.valueOf(checks.get(0, "DATA_ID", "")));
        }

        /**
         * POS机刷卡次数限制，为0表示不限制
         */
        int posCountLimit = 0;
        IDataset commSet = ParamInfoQry.getCommparaByCode("CSM", "1800", "", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commSet))
        {
            posCountLimit = commSet.getData(0).getInt("PARA_CODE1", 0);
        }
        data.put("POS_COUNT_LIMIT", posCountLimit);

        return data;
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
        for (int i = 0; i < feeTypes.size(); i++)
        {
            IData tmp = (IData) feeTypes.get(i);
            if (tmp.getString("DATA_ID").equals(typeCode))
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
        for (int i = 0; i < temp.size(); i++)
        {
            IData tmp = (IData) temp.get(i);
            if (tmp.getString("CODE").equals(typeCode))
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

        // 0- 业务费用, 根据业务类型编码、产品标识、大客户等级标识、地市编码获取对应业务的通用费用，进入页面需要获取的费用
        String tradeFeeType = "0";//input.put("TRADE_FEE_TYPE", "0");
        String productId =input.getString("PRODUCT_ID");
        IDataset results = ProductFeeInfoQry.getTradeFee(tradeTypeCode,tradeFeeType, input.getString("EPARCHY_CODE"));

        IDataset upcFees = UpcCall.qryDynamicPrice(productId, "P", "-1", null, tradeTypeCode, null, input.getString("VIP_CLASS_ID"), null); //ProductFeeInfoQry.getTradeFee(input);
        if(IDataUtil.isEmpty(upcFees))
        {
        	results.addAll(upcFees);
        }
        
        if (results != null && results.size() > 0)
        {
            /**
             * 以下逻辑湖南没有，这里直接注释 feelists = getRealfeeByRule(results);
             */
            for (int i = 0; i < results.size(); i++)
            {
                IData feelist = new DataMap();
                feelist = results.getData(i);

                String feeMode = feelist.getString("FEE_MODE");
                String feeTypeCode = feelist.getString("FEE_TYPE_CODE");
                String fee = feelist.getString("FEE", "0");

                if ("0".equals(fee))
                    continue;
                addFeeList(feeList, tradeTypeCode, feeMode, feeTypeCode, fee);
            }
        }
        return feeList;
    }

    private static boolean hasPriv(String privCode) throws Exception
    {
        return StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), privCode);
    }

}
