
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.grouptradefee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class GroupTradeFeeQryBean extends CSBizBean
{
    /**
     * 处理元素收费
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    protected static IDataset qryTradeTypeFeeForElement(IData inParam) throws Exception
    {
        IDataset retFeeList = new DatasetList();

        IDataset selectedElementList = inParam.getDataset("SELECTED_ELEMENTS");

        if (IDataUtil.isEmpty(selectedElementList))
        {
            return retFeeList;
        }

        // 遍历元素
        for (int i = 0, row = selectedElementList.size(); i < row; i++)
        {
            IData elementData = selectedElementList.getData(i);
            String productId = inParam.getString("PRODUCT_ID");
            String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE");

            IData inparam = new DataMap();
            inparam.put("IN_MODE_CODE", "0");
            inparam.put("CAMPN_ID", inParam.getString("CAMPN_ID"));
            inparam.put("TRADE_TYPE_CODE", tradeTypeCode);
            inparam.put("PRODUCT_ID", productId);
            inparam.put("PACKAGE_ID", elementData.getString("PACKAGE_ID"));
            inparam.put("ELEMENT_TYPE_CODE", elementData.getString("ELEMENT_TYPE_CODE", "D"));
            inparam.put("ELEMENT_ID", elementData.getString("ELEMENT_ID"));

            String eparchyCode = inParam.getString("EPARCHY_CODE");
            if (StringUtils.isEmpty(eparchyCode))
            {
                eparchyCode = CSBizBean.getTradeEparchyCode();
            }

            inparam.put("EPARCHY_CODE", eparchyCode);

            inparam.put("TRADE_FEE_TYPE", inParam.getString("TRADE_FEE_TYPE", "4"));

            IDataset tradeFeeList = ProductFeeInfoQry.getGrpTradeTypeFee(inparam);

            retFeeList.addAll(tradeFeeList);
        }

        return retFeeList;
    }

    /**
     * 处理产品收费
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    protected static IDataset qryTradeTypeFeeForProduct(IData inParam) throws Exception
    {
        return ProductFeeInfoQry.getGrpTradeTypeFee(inParam);
    }

    /**
     * 处理集团收费
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryTradeTypeFeeForGrp(IData inParam) throws Exception
    {
        String tradeFeeType = IDataUtil.chkParam(inParam, "TRADE_FEE_TYPE");

        IDataset retFeeList = new DatasetList();

        if ("0".equals(tradeFeeType)) // 处理产品收费情况
        {
            retFeeList = qryTradeTypeFeeForProduct(inParam);
        }
        else if ("4".equals(tradeFeeType)) // 处理元素收费情况
        {
            retFeeList = qryTradeTypeFeeForElement(inParam);
        }

        return retFeeList;
    }

}
