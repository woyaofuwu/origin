
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class SaleActiveElementFeeBean extends CSBizBean
{
    public IData calElementFee(String productId, String packageId, String elementTypeCode, String elementId, String resNo, String eparchyCode) throws Exception
    {
//        IDataset productTradeFeeDataset = ProductFeeInfoQry.getElementFee(BofConst.TRADE_TYPE_CODE_SALEACTIVE, getVisit().getInModeCode(), null, elementTypeCode, productId, packageId, null, elementId, eparchyCode, "3");
        IDataset productTradeFeeDataset = ProductFeeInfoQry.getSaleActiveFee(BofConst.TRADE_TYPE_CODE_SALEACTIVE, BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, elementTypeCode, elementId, productId);

        if (IDataUtil.isEmpty(productTradeFeeDataset))
            return null;

        IData elementFeeData = new DataMap();

        IData productTradeFeeData = productTradeFeeDataset.getData(0);
        String ruleBizTypeCode = productTradeFeeData.getString("RULE_BIZ_TYPE_CODE", "-1");

        if ("-1".equals(ruleBizTypeCode))
        {
            elementFeeData.put("FEE", productTradeFeeData.getString("FEE", "0"));
            elementFeeData.put("FEE_MODE", productTradeFeeData.getString("FEE_MODE", ""));
            elementFeeData.put("FEE_TYPE_CODE", productTradeFeeData.getString("FEE_TYPE_CODE", ""));
            elementFeeData.put("PAY_MODE", productTradeFeeData.getString("PAY_MODE", ""));
            elementFeeData.put("IN_DEPOSIT_CODE", productTradeFeeData.getString("IN_DEPOSIT_CODE", ""));
            elementFeeData.put("OUT_DEPOSIT_CODE", productTradeFeeData.getString("OUT_DEPOSIT_CODE", ""));  
            if (BofConst.ELEMENT_TYPE_CODE_SALEGOODS.equals(elementTypeCode) && StringUtils.isNotBlank(resNo))
            {
                String fee = productTradeFeeData.getString("FEE");
                String rsrvStr1 = productTradeFeeData.getString("RSRV_STR1");
                String rsrvStr2 = productTradeFeeData.getString("RSRV_STR2");
                String rsrvStr3 = productTradeFeeData.getString("RSRV_STR3");
                String rsrvStr4 = productTradeFeeData.getString("RSRV_STR4");
                String feeTypeCode = productTradeFeeData.getString("FEE_TYPE_CODE", "");
                String feeMode = productTradeFeeData.getString("FEE_MODE", "");
                if ("0".equals(feeMode) && ("60".equals(feeTypeCode) || "61".equals(feeTypeCode)))
                {
                    String terminalOperFee = getTerminalOperFeeByResNo(productId, packageId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, resNo, eparchyCode);
                    elementFeeData.put("FEE", terminalOperFee);
                    /**
    	             * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
    	             * chenxy3 2016-08-26
    	             * 单独返回用于判断购机款是否以TD_B_PRODUCT_TRADEFEE配置为准
    	             * */
                    elementFeeData.put("TRADE_FEE_RSRV_STR1", rsrvStr1);
                    elementFeeData.put("TRADE_FEE_FEE", productTradeFeeData.getString("FEE", "0"));
                }
            }
        }

        return elementFeeData;
    }

    public String getTerminalOperFee(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID", "");
        String packageId = input.getString("PACKAGE_ID", "");
        String rsrvStr1 = input.getString("RSRV_STR1"); // product_tradefee
        String rsrvStr2 = input.getString("RSRV_STR2"); // product_tradefee
        String rsrvStr3 = input.getString("RSRV_STR3", "0"); // product_tradefee
        String rsrvStr4 = input.getString("RSRV_STR4", "0"); // product_tradefee

        int feeInt = input.getInt("FEE", 0);
        int deviceCost = input.getInt("DEVICE_COST", 0);
        int salePrice = input.getInt("SALE_PRICE", 0);
        String terminalType = input.getString("IS_INTELL_TERMINAL", "");

        if ("1".equals(rsrvStr1)) // 以终端结算价为准
        {
            feeInt = deviceCost;
        }
        else if ("2".equals(rsrvStr1)) // 以终端销售价为准
        {
            feeInt = salePrice;
        }

        if ("3".equals(rsrvStr1)) // 如果是合约机的活动，则以合约价减去购机优惠金额作为购机款
        {
            IDataset commpara1741set = CommparaInfoQry.getCommparaInfoByCode45("CSM", "1741", productId, deviceCost);
            if (IDataUtil.isNotEmpty(commpara1741set))
            {
                IDataset commpara1742set = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "1742", productId, packageId, "0898");// 获取营销包对应的购机优惠金额
                if (IDataUtil.isNotEmpty(commpara1742set))
                {
                    int contractMoney = commpara1741set.getData(0).getInt("PARA_CODE1", 0); // 合约价
                    int phoneDiscntMoney = commpara1742set.getData(0).getInt("PARA_CODE3", 0); // 活动包优惠购机款
                    feeInt = contractMoney - phoneDiscntMoney;
                    feeInt = feeInt < 0 ? 0 : feeInt; // 负数的时候，当0处理
                }
            }
        }

        if ("1".equals(rsrvStr2)) // 是否处理智能机优惠
        {
            if ("1".equals(terminalType)) // 智能机
            {
                feeInt = feeInt - Integer.parseInt(rsrvStr3);
            }
            else if ("0".equals(terminalType)) // 非智能机
            {
                feeInt = feeInt - Integer.parseInt(rsrvStr4);
            }else
            {
            	feeInt = feeInt - Integer.parseInt(rsrvStr3);
            }
            feeInt = feeInt < 0 ? 0 : feeInt; // 负数的时候，当0处理
        }
        return String.valueOf(feeInt);
    }

    public String getTerminalOperFeeByDeviceModelCode(String productId, String packageId, String fee, String rsrvStr1, String rsrvStr2, String rsrvStr3, String rsrvStr4, String deviceModeCode, String resTypeId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("FEE", fee);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR2", rsrvStr2);
        param.put("RSRV_STR3", rsrvStr3);
        param.put("RSRV_STR4", rsrvStr4);

        IData terminalParam = new DataMap();
        terminalParam.put("DEVICE_MODEL_CODE", deviceModeCode);
        terminalParam.put("RES_TYPE_ID", resTypeId);

        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        IData terminalInfo = terminalBean.getTerminalByDeviceModelCode(terminalParam);

        param.put("DEVICE_COST", terminalInfo.getString("DEVICE_COST"));
        param.put("SALE_PRICE", terminalInfo.getString("SALE_PRICE"));
        param.put("IS_INTELL_TERMINAL", terminalInfo.getString("IS_INTELL_TERMINAL"));

        return getTerminalOperFee(param);
    }

    public String getTerminalOperFeeByResNo(String productId, String packageId, String fee, String rsrvStr1, String rsrvStr2, String rsrvStr3, String rsrvStr4, String resNo, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("FEE", fee);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR2", rsrvStr2);
        param.put("RSRV_STR3", rsrvStr3);
        param.put("RSRV_STR4", rsrvStr4);
        
        String labelId = LabelInfoQry.getLogicLabelIdByElementId(productId);

        IData terminalParam = new DataMap();
        terminalParam.put("RES_NO", resNo);
        terminalParam.put("PRODUCT_ID", productId);
        terminalParam.put("CAMPN_TYPE", labelId);
        TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
        IData terminalInfo = terminalBean.getTerminalByResNoOnly(terminalParam);

        param.put("DEVICE_COST", terminalInfo.getString("DEVICE_COST"));
        param.put("SALE_PRICE", terminalInfo.getString("SALE_PRICE"));
        param.put("IS_INTELL_TERMINAL", terminalInfo.getString("IS_INTELL_TERMINAL"));

        return getTerminalOperFee(param);
    }

}
