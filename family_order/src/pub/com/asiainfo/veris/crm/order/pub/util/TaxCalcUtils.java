
package com.asiainfo.veris.crm.order.pub.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class TaxCalcUtils
{
    /**
     * 获取不含税价格[不含税价格 = 含税价格 / (1 ＋ 税率)]
     * 
     * @param salePrice
     *            含税价格
     * @param taxRate
     *            税率
     * @return
     * @throws Exception
     */
    public static float getExclusTaxPrice(float salePrice, float taxRate) throws Exception
    {
        float exclusTaxPrice = 0.0f;

        taxRate = taxRate / 10000;

        if (taxRate < 0.00000001)
            return salePrice;

        exclusTaxPrice = salePrice / (1 + taxRate);

        exclusTaxPrice = Math.round(exclusTaxPrice);

        return exclusTaxPrice;
    }

    /**
     * 处理营业费各计税科目的增值税
     * 
     * @param taxFeeList
     * @throws Exception
     */
    public static void getTradeFeeTaxForCalculate(IDataset taxFeeList) throws Exception
    {
        if (IDataUtil.isEmpty(taxFeeList))
        {
            return;
        }

        for (int i = 0, row = taxFeeList.size(); i < row; i++)
        {
            IData taxFeeData = taxFeeList.getData(i);

            String taxType = taxFeeData.getString("TYPE", ""); // 计税方式

            float taxRate = Float.valueOf(taxFeeData.getString("RATE")); // 税率 * 10000, 如11.75%写成1175
            float busiFee = Float.valueOf(taxFeeData.getString("FACT_PAY_FEE", "0.0")); // 实收金额, 精确到分
            float saleFee = Float.valueOf(taxFeeData.getString("FEE", "0.0")); // 销售价格, 精确到分
            float discount = Float.valueOf(taxFeeData.getString("DISCOUNT", "0.0")); // 折扣

            float exclusTaxFee = 0.0f; // 不含税价格
            float taxFee = 0.0f; // 税额

            // 提补税
            float fee3 = 0.0f;

            if ("1".equals(taxType)) // 视同销售
            {
                discount = saleFee - busiFee;

                exclusTaxFee = getExclusTaxPrice(saleFee, taxRate); // 不含税价
                taxFee = getVatTaxValue(saleFee, exclusTaxFee); // 税额

                float busFeeExclusTaxFee = getExclusTaxPrice(busiFee, taxRate);// 实收部分不含税价

                taxFee = taxFee / 10;// 最新规范，要+10%

                fee3 = busiFee - busFeeExclusTaxFee - taxFee;
                taxFeeData.put("FEE1", busFeeExclusTaxFee);
            }
            else if ("2".equals(taxType))
            {

            }
            else if ("3".equals(taxType))//
            {
                exclusTaxFee = getExclusTaxPrice(busiFee, taxRate);// 不含税价格
                taxFee = getVatTaxValue(busiFee, exclusTaxFee); // 税额
                taxFeeData.put("FEE1", exclusTaxFee);
            }
            else if ("4".equals(taxType))
            {
                exclusTaxFee = getExclusTaxPrice(saleFee, taxRate);
                taxFee = getVatTaxValue(saleFee, exclusTaxFee); // 税额
                taxFeeData.put("FEE1", exclusTaxFee);
            }

            taxFeeData.put("BUSI_FEE", (int) busiFee);
            taxFeeData.put("SALE_PRICE", (int) saleFee);
            taxFeeData.put("FEE2", taxFee);
            taxFeeData.put("FEE3", fee3);
        }

        roundData(taxFeeList);
    }

    /**
     * 获取单价[单价 = 不含税价格 / 数量]
     * 
     * @param exclusTaxPrice
     *            不含税价格
     * @param amount
     *            数量
     * @return
     * @throws Exception
     */
    public static float getUnitPrice(float exclusTaxPrice, int amount) throws Exception
    {
        return Math.round(exclusTaxPrice / amount);
    }

    /**
     * 获取税额[税额 = 含税价格-不含税价格]
     * 
     * @param salePrice
     *            含税价格
     * @param exclusTaxPrice
     *            不含税价格
     * @return
     * @throws Exception
     */
    public static float getVatTaxValue(float salePrice, float exclusTaxPrice) throws Exception
    {
        return salePrice - exclusTaxPrice;
    }

    private static void roundData(IDataset taxfees)
    {
        for (int i = 0, size = taxfees.size(); i < size; i++)
        {
            IData map = (IData) taxfees.get(i);
            float fee1 = Float.valueOf(map.getString("FEE1"));
            map.put("FEE1", Math.round(fee1));
            float fee2 = Float.valueOf(map.getString("FEE2"));
            map.put("FEE2", Math.round(fee2));

            float fee3 = Float.valueOf(map.getString("FEE3"));
            map.put("FEE3", Math.round(fee3));
        }

    }
}
