
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class PrintMgr
{
    /**
     * 构造通用打印数据
     * 
     * @param inparams
     *            ---OPERATION_TIME,TOTAL_MONEY
     * @param mstPrintData
     *            --STAFF_ID,STAFF_NAME,DEPT_NAME,OPERATION_YEAR,OPERATION_MONTH,
     *            OPERATION_DAY,ALL_MONEY_LOWER,ALL_MONEY_UPPER
     * @throws Exception
     */
    private static void createCommonPrintData(IData inparams, IData mstPrintData) throws Exception
    {

        String operationTime = inparams.getString("OPERATION_TIME");
        double totalMoney = inparams.getDouble("TOTAL_MONEY", 0);
        String staffId = CSBizBean.getVisit().getStaffId();
        String staffName = CSBizBean.getVisit().getStaffName();
        String departName = CSBizBean.getVisit().getDepartName();
        mstPrintData.put("STAFF_ID", staffId);
        mstPrintData.put("STAFF_NAME", staffName);
        mstPrintData.put("DEPART_NAME", departName);
        mstPrintData.put("OPERATION_YEAR", operationTime.substring(0, 4));
        mstPrintData.put("OPERATION_MONTH", operationTime.substring(5, 7));
        mstPrintData.put("OPERATION_DAY", operationTime.substring(8, 10));
        mstPrintData.put("OPERATION_TIME_2", operationTime.substring(11, 19));
        mstPrintData.put("ALL_MONEY_LOWER", String.format("%1$3.2f", totalMoney / 100.0));
        mstPrintData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalMoney / 100.0));
    }

    private static String createFeeList(IData feeData)
    {
        int size = feeData.size() / 2;
        StringBuilder feeList = new StringBuilder();
        for (int i = 0; i < size; i++)
        {
            feeList.append(feeData.getString("FEE_TYPE" + i) + "        " + String.format("%1$3.2f", (float) (feeData.getInt("FEE" + i) / 100.0)) + "~~");
        }
        return feeList.toString();
    }

    /**
     * 构造打印数据
     * 
     * @param inparams
     *            ---OPERATION_TIME,TOTAL_MONEY,PRINT_TYPE --0 TOTAL_OPER_FEE,OPER_FEE_SUB --1
     *            TOTAL_FOREGIFT,FOREGIFT_SUB --2 TOTAL_ADVANCE_PAY,PRE_FEE_SUB
     * @param mstPrintData
     *            返回的打印数据 ---STAFF_ID,STAFF_NAME,DEPT_NAME,OPERATION_YEAR,OPERATION_MONTH,
     *            OPERATION_DAY,ALL_MONEY_LOWER,ALL_MONEY_UPPER 0--OPERFEE_CONTENT 1--FOREGIFT_CONTENT 2--PREFEE_CONTENT
     * @throws Exception
     */
    public static void createPrintData(IData inparams, IData mstPrintData) throws Exception
    {

        createCommonPrintData(inparams, mstPrintData);
        createSpecialPrintData(inparams, mstPrintData);
    }

    /**
     * 构造押金打印数据
     * 
     * @param inParam
     *            ---TOTAL_FOREGIFT,FOREGIFT_SUB
     * @param mstPrintData
     *            ---FOREGIFT_CONTENT
     * @throws Exception
     */
    private static void createSpecialForegiftPrintData(IData inParam, IData mstPrintData) throws Exception
    {

        double totalForegift = inParam.getDouble("TOTAL_FOREGIFT", 0);

        if (totalForegift != 0)
        {
            IData foregiftSub = inParam.getData("FOREGIFT_SUB");
            if (foregiftSub == null || foregiftSub.size() <= 1)
            {
                CSAppException.apperr(PrintException.CRM_PRINT_20);
            }

            mstPrintData.put("FOREGIFT_CONTENT", createFeeList(foregiftSub));
        }
    }

    /**
     * 构造营业费打印数据
     * 
     * @param inParam
     *            ---TOTAL_OPER_FEE,OPER_FEE_SUB
     * @param mstPrintData
     *            --OPERFEE_CONTENT
     * @throws Exception
     */
    private static void createSpecialOperFeePrintData(IData inParam, IData mstPrintData) throws Exception
    {

        double totalOperFee = inParam.getDouble("TOTAL_OPER_FEE", 0);

        if (totalOperFee != 0)
        {
            IData operFeeSub = inParam.getData("OPER_FEE_SUB");
            if (operFeeSub == null || operFeeSub.size() <= 1)
            {
                CSAppException.apperr(PrintException.CRM_PRINT_19);
            }

            mstPrintData.put("OPERFEE_CONTENT", createFeeList(operFeeSub));
        }
    }

    /**
     * 构造预存打印数据
     * 
     * @param inParam
     *            ---TOTAL_ADVANCE_PAY,PRE_FEE_SUB
     * @param mstPrintData
     *            ---PREFEE_CONTENT
     * @throws Exception
     */
    private static void createSpecialPreFeePrintData(IData inParam, IData mstPrintData) throws Exception
    {

        double totalAdvancePay = inParam.getDouble("TOTAL_ADVANCE_PAY", 0);

        if (totalAdvancePay != 0)
        {
            IData preFeeSub = inParam.getData("PRE_FEE_SUB");
            if (preFeeSub == null || preFeeSub.size() <= 1)
            {
                CSAppException.apperr(PrintException.CRM_PRINT_21);
            }

            mstPrintData.put("PREFEE_CONTENT", createFeeList(preFeeSub));
        }
    }

    /**
     * 构造特殊打印数据
     * 
     * @param inParam
     *            --- PRINT_TYPE --0 TOTAL_OPER_FEE,OPER_FEE_SUB --1 TOTAL_FOREGIFT,FOREGIFT_SUB --2
     *            TOTAL_ADVANCE_PAY,PRE_FEE_SUB
     * @param mstPrintData
     *            0--OPERFEE_CONTENT 1--FOREGIFT_CONTENT 2--PREFEE_CONTENT
     * @throws Exception
     */
    private static void createSpecialPrintData(IData inParam, IData mstPrintData) throws Exception
    {
        String printType = inParam.getString("PRINT_TYPE");

        if (printType.indexOf("0") != -1)
        {
            createSpecialOperFeePrintData(inParam, mstPrintData);
        }
        if (printType.indexOf("1") != -1)
        {
            createSpecialForegiftPrintData(inParam, mstPrintData);
        }
        if (printType.indexOf("2") != -1)
        {
            createSpecialPreFeePrintData(inParam, mstPrintData);
        }
        // 构造客户资料登记受理单或业务受理单打印数据
        if (printType.indexOf("3") != -1)
        {
            createSpecialReceiptPrintData(inParam, mstPrintData);
        }
    }

    /**
     * 构造客户资料登记受理单或业务受理单打印数据
     * 
     * @param inParam
     *            RECEIPT_INFO1,RECEIPT_INFO2,RECEIPT_INFO3,RECEIPT_INFO4,RECEIPT_INFO5
     * @param mstPrintData
     *            RECEIPT_INFO1,RECEIPT_INFO2
     * @throws Exception
     */
    private static void createSpecialReceiptPrintData(IData inParam, IData mstPrintData) throws Exception
    {

        /*
         * 在此不做处理，放在数据库td_b_trade_receipt中配置 String rsrvStr1 = inparams.getString("RECEIPT_INFO1",""); String rsrvStr2 =
         * inparams.getString("RECEIPT_INFO2",""); String rsrvStr3 = inparams.getString("RECEIPT_INFO3",""); String
         * rsrvStr4 = inparams.getString("RECEIPT_INFO4",""); String rsrvStr5 = inparams.getString("RECEIPT_INFO5","");
         * inparams.remove("RECEIPT_INFO1"); inparams.remove("RECEIPT_INFO2"); inparams.remove("RECEIPT_INFO3");
         * inparams.remove("RECEIPT_INFO4"); inparams.remove("RECEIPT_INFO5"); mstPrintData.put("RECEIPT_INFO1",
         * rsrvStr1+rsrvStr2+rsrvStr3+rsrvStr4); mstPrintData.put("RECEIPT_INFO2", rsrvStr5);
         */
    }

    public static String floatToRMB(double money)
    {

        String sRMBUnit = "分角元拾佰仟万拾佰仟亿拾百";
        String sRMBNumber = "零壹贰叁肆伍陆柒捌玖";
        String sMoney;
        String sChar, sNumber, sUnit;
        StringBuilder sRMBMoney = new StringBuilder();
        int iMoneyLength, iPosDecimal;
        double fAfterDecimal; // 小数部分
        int iBeforeDecimal; // 整数部分
        boolean bIsZeroEnd;

        iBeforeDecimal = (int) money;
        fAfterDecimal = Math.abs(money - iBeforeDecimal);

        if (fAfterDecimal == 0)
        {
            sRMBMoney.append("整");
        }

        sMoney = String.format("%1$+3.2f", money);
        // 删除小数点
        iPosDecimal = sMoney.indexOf('.');

        if (iPosDecimal > 0)
            sMoney = sMoney.substring(1, iPosDecimal) + sMoney.substring(iPosDecimal + 1);

        iMoneyLength = sMoney.length();

        int i = 0;
        bIsZeroEnd = true;

        while (iMoneyLength > 0)
        {
            sChar = sMoney.substring(iMoneyLength - 1, iMoneyLength);
            sNumber = sRMBNumber.substring(Integer.valueOf(sChar), Integer.valueOf(sChar) + 1);
            sUnit = sRMBUnit.substring(i, i + 1);

            if (sChar.equals("0"))
            {
                if (i != 2 && i != 6 && i != 10)
                    sUnit = "";

                if (bIsZeroEnd || i == 2 || i == 6 || i == 10)
                    sNumber = "";

                if (Math.abs(money) < 1 && i <= 2)
                {
                    sUnit = "";
                    sNumber = "";
                }
                bIsZeroEnd = true;
            }
            else
                bIsZeroEnd = false;

            sRMBMoney.insert(0, sNumber + sUnit);
            iMoneyLength--;
            i++;
        }
        if (money < 0)
        {
            sRMBMoney.insert(0, "负");
        }
        return sRMBMoney.toString();
    }

}
