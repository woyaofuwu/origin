
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.TaxException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class TaxPrintBean extends CSBizBean
{

    public static final String RECEIPT_G0004 = "G0004"; // 专用增值税发票

    public static final String RECEIPT_G0005 = "G0005"; // 普通增值税发票

    private static IData commPrintData = new DataMap();;

    private static IDataset templetItems = new DatasetList();

    // 缴费不为0，并存在折扣的，发票上增加打印一条折扣信息
    public static IDataset addDiscountRecords(IDataset taxInfoList) throws Exception
    {

        IDataset disdataset = new DatasetList();
        for (int i = 0, iSize = taxInfoList.size(); i < iSize; i++)
        {
            IData map = taxInfoList.getData(i);
            Float discount = Float.valueOf(map.getString("DISCOUNT", "0.0"));
            Float busifee = Float.valueOf(map.getString("BUSI_FEE", "0.0"));
            Float taxrate = Float.valueOf(map.getString("RATE", "0.0"));
            int icount = Integer.valueOf(map.getString("COUNT", "1"));
            boolean sumFlag = map.getBoolean("SUM_FLAG");
            // 计税科目缴费不为0，并存在折扣的
            if (busifee > 0.00000001 && discount > 0.00000001)
            {
                IData disdata = new DataMap();
                disdata.putAll(map);

                float notaxfee = TaxCalcUtils.getExclusTaxPrice(discount, taxrate);
                float taxfee = TaxCalcUtils.getVatTaxValue(discount, notaxfee);
                disdata.put("FEEITEM_NAME", "销售折扣-" + map.getString("FEEITEM_NAME", ""));
                disdata.put("SALE_PRICE", String.valueOf(discount * (-1)));
                disdata.put("FEE1", String.valueOf(notaxfee));
                disdata.put("FEE2", String.valueOf(taxfee));

                disdata.put("UNIT_FEE", String.valueOf(notaxfee * (-1)));// 单价
                disdata.put("COUNT", String.valueOf(icount));// 数量
                if (sumFlag)
                {
                    disdata.put("SUMFEE", String.valueOf(notaxfee * (-1)));// 已经合并，无需再次*数量
                    disdata.put("SUMTAX", String.valueOf(taxfee * (-1)));// 已经合并，无需再次*数量
                }
                else
                {
                    disdata.put("SUMFEE", String.valueOf(notaxfee * icount * (-1)));// 该折扣金额=单价*数量*(-1)
                    disdata.put("SUMTAX", String.valueOf(taxfee * icount * (-1)));// 该折扣总税=税额*数量*(-1)
                }

                disdataset.add(disdata);
            }
        }

        return disdataset;
    }

    // 积分抵扣，发票上增加打印一条积分抵扣折扣信息
    public static IDataset addScoreRecords(IDataset taxInfoList) throws Exception
    {
        IDataset dataset = new DatasetList();

        for (int i = 0, tSize = taxInfoList.size(); i < tSize; i++)
        {
            IData map = taxInfoList.getData(i);
            Float scoremoney = Float.valueOf(map.getString("SCORE_VALUE", "0.0"));
            Float busifee = Float.valueOf(map.getString("BUSI_FEE", "0.0"));
            Float taxrate = Float.valueOf(map.getString("RATE", "0.0"));
            int icount = Integer.valueOf(map.getString("COUNT", "1"));
            boolean sumFlag = map.getBoolean("SUM_FLAG");
            // 计税科目缴费不为0，并存在折扣的
            if (scoremoney > 0.00000001)
            {
                IData data = new DataMap();
                data.putAll(map);

                float notaxfee = TaxCalcUtils.getExclusTaxPrice(scoremoney, taxrate);
                float taxfee = TaxCalcUtils.getVatTaxValue(scoremoney, notaxfee);

                data.put("FEEITEM_NAME", "积分抵扣折扣-" + map.getString("FEEITEM_NAME", ""));
                data.put("SALE_PRICE", String.valueOf(scoremoney * (-1)));
                data.put("FEE1", String.valueOf(notaxfee));
                data.put("FEE2", String.valueOf(taxfee));

                data.put("UNIT_FEE", String.valueOf(notaxfee));// 单价
                data.put("COUNT", String.valueOf(icount));// 数量
                if (sumFlag)
                {
                    data.put("SUMFEE", String.valueOf(notaxfee));// 已经合并，无需再次*数量
                    data.put("SUMTAX", String.valueOf(taxfee));// 已经合并，无需再次*数量
                }
                else
                {
                    data.put("SUMFEE", String.valueOf(notaxfee * icount));// 该折扣金额=单价*数量
                    data.put("SUMTAX", String.valueOf(taxfee * icount));// 该折扣总税=税额*数量
                }
                dataset.add(data);
            }
        }

        return dataset;

    }

    // tax表折扣折让计税的记录转换成视同销售的发票打印记录
    public static IDataset changeToTaxPrintModeData(IDataset taxInfoList) throws Exception
    {

        IDataset saledataset = new DatasetList();
        for (int i = 0; i < taxInfoList.size(); i++)
        {
            IData map = taxInfoList.getData(i);
            String ratetype = map.getString("TYPE");
            float saleprice = Float.valueOf(map.getString("SALE_PRICE", "0.0"));
            float taxrate = Float.valueOf(map.getString("RATE", "0.0"));
            float notaxfee = TaxCalcUtils.getExclusTaxPrice(saleprice, taxrate);
            float taxfee = TaxCalcUtils.getVatTaxValue(saleprice, notaxfee);
            int icount = Integer.valueOf(map.getString("COUNT", "1"));
            boolean sumFlag = map.getBoolean("SUM_FLAG");

            // 折扣折让
            if ("3".equals(ratetype))
            {
                map.put("FEE1", String.valueOf(notaxfee));
                map.put("FEE2", String.valueOf(taxfee));
            }
            map.put("UNIT_FEE", String.valueOf(notaxfee));// 单价
            map.put("COUNT", String.valueOf(icount));// 数量
            if (sumFlag)
            {
                map.put("SUMFEE", String.valueOf(notaxfee));// 已经合并，无需再次*数量
                map.put("SUMTAX", String.valueOf(taxfee));// 已经合并，无需再次*数量
            }
            else
            {
                map.put("SUMFEE", String.valueOf(notaxfee * icount));// 该商品金额=单价*数量
                map.put("SUMTAX", String.valueOf(taxfee * icount));// 该商品总税=税额*数量
            }
            saledataset.add(map);
        }
        return saledataset;
    }

    private static void createCommonPrintData(IData inparams, IData mstPrintData) throws Exception
    {

        String operationTime = SysDateMgr.getSysDate();
        mstPrintData.put("STAFF_ID", getVisit().getStaffId());
        mstPrintData.put("STAFF_NAME", getVisit().getStaffName());
        mstPrintData.put("DEPT_NAME", getVisit().getDepartName());
        mstPrintData.put("TRADE_ID", inparams.getString("TRADE_ID", ""));
        mstPrintData.put("CUST_NAME", inparams.getString("CUST_NAME", ""));
        mstPrintData.put("TEMPLET_CODE", inparams.getString("TEMPLET_CODE", ""));
        mstPrintData.put("TEMPLET_TYPE", inparams.getString("TEMPLET_TYPE", ""));

        mstPrintData.put("OPERATION_YEAR", operationTime.substring(0, 4));
        mstPrintData.put("OPERATION_MONTH", operationTime.substring(5, 7));
        mstPrintData.put("OPERATION_DAY", operationTime.substring(8, 10));

    }

    // 集团业务票据打印数据准备
    public static IDataset getAllTaxInfoByTradeIdArray(IData inParam) throws Exception
    {

        IDataset taxInfoList = new DatasetList();
        IData param = new DataMap();
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));
        IDataset taxInfos = TradeFeeTaxInfoQry.getTradeOperFeeTaxInfosForCG(param);
        if (IDataUtil.isNotEmpty(taxInfos))
        {
            // 湖南CRM增值税发票只打营业费,费用为0的不打
            IDataset operfees = getTradeOperFeeTaxFromFeeList(taxInfos);

            for (int j = 0, jSize = operfees.size(); j < jSize; j++)
            {
                IData map = operfees.getData(j);
                taxInfoList.add(map);
            }
        }

        return taxInfoList;
    }

    /**
     * 获取发票打印数据
     * 
     * @param pageData
     * @return
     * @throws Exception
     */
    public static IDataset getPrintGrpTaxTicketDate(IData inParam) throws Exception
    {
        // 获取要打印的所有数据，湖南CRM增值税发票要打营业费，预存费用，且实缴为0元的不打发票
        IDataset taxInfoList = TaxPrintBean.getAllTaxInfoByTradeIdArray(inParam);

        if (IDataUtil.isEmpty(taxInfoList))
        {
            CSAppException.apperr(TaxException.CRM_TAX_5);
        }
        else
        {
            float icountfee = 0.0f;
            for (int i = 0, iSize = taxInfoList.size(); i < iSize; i++)
            {
                IData map = taxInfoList.getData(i);
                icountfee = icountfee + Float.valueOf(map.getString("BUSI_FEE", "0.0f"));// 实收金额
            }
            if (icountfee < 0.00000001)
            {
                CSAppException.apperr(TaxException.CRM_TAX_5);
            }
        }

        // 打印
        IData data = new DataMap();
        data.put("TRADE_ID", inParam.getString("TRADE_ID")); // 业务流水
        data.put("CUST_NAME", inParam.getString("CUST_NAME")); // 发票抬头
        data.put("TEMPLET_TYPE", TaxPrintBean.RECEIPT_G0004);// 增值税专用发票
        data.put("CUST_ID", inParam.getString("CUST_ID"));

        // 集团业务票据打印数据准备
        IDataset returnResults = TaxPrintBean.printTaxReceiptForGrp(data, taxInfoList);

        return returnResults;
    }

    public static IDataset getReceiptTempletItems(IData templat) throws Exception
    {
        IData data = new DataMap();
        data.put("TEMPLET_TYPE", templat.getString("TEMPLET_TYPE", ""));// 发票
        data.put("TRADE_TYPE_CODE", templat.getString("TRADE_TYPE_CODE", "-1"));
        data.put("RELATION_KIND", templat.getString("RELATION_KIND", "0")); // 按地州
        data.put("RELATION_ATTR", templat.getString("RELATION_ATTR", "")); // 地州编码

        // 获取打印模板
        IDataset receiptTemplets = NoteBean.getReceiptTempletItems(data);

        return receiptTemplets;
    }

    // 获取不同的税率及个数
    public static List<String> getTaxRateArrayList(IDataset taxInfoList) throws Exception
    {

        IData map = new DataMap();
        List<String> rateArray = new ArrayList<String>();

        for (int i = 0; i < taxInfoList.size(); i++)
        {
            String rate = taxInfoList.getData(i).getString("RATE");
            map.put(rate, "RATE");
        }

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext())
        {
            String tempKey = (String) it.next();
            rateArray.add(tempKey);
        }

        return rateArray;
    }

    public static IDataset getTradeOperFeeTaxFromFeeList(IDataset feelist) throws Exception
    {
        IDataset dataset = new DatasetList();

        for (int i = 0, row = feelist.size(); i < row; i++)
        {
            IData feeData = feelist.getData(i);
            String feeMode = feeData.getString("FEE_MODE", "");
            String feeTypeCode = feeData.getString("FEE_TYPE_CODE", "");
            // 0-营费用,2-预存,费用为0的 才需要计价分离
            if (feeMode.matches("0|2") && Double.valueOf(feeData.getString("FEE1", "0")) != 0)
            {
                String feeitemName = FeeListMgr.getFeeTypeCodeDesc(feeMode, feeTypeCode);
                feeData.put("FEEITEM_NAME", feeitemName);
                dataset.add(feeData);
            }
        }
        return dataset;
    }

    // 拼打印模板数据
    public static IData prepareTaxPrintData(IDataset taxInfoList, IData sumData) throws Exception
    {
        IData printData = new DataMap();
        // 计税科目名称
        StringBuilder print_data1 = new StringBuilder();
        // 型号
        StringBuilder print_data2 = new StringBuilder();
        // 单位
        StringBuilder print_data3 = new StringBuilder();
        // 数量
        StringBuilder print_data4 = new StringBuilder();
        // 单价
        StringBuilder print_data5 = new StringBuilder();
        // 金额
        StringBuilder print_data6 = new StringBuilder();
        // 税率
        StringBuilder print_data7 = new StringBuilder();
        // 税额
        StringBuilder print_data8 = new StringBuilder();

        NumberFormat formatter = new DecimalFormat("#0.00");

        for (int i = 0, iSize = taxInfoList.size(); i < iSize; i++)
        {
            IData map = taxInfoList.getData(i);
            float unitfee = Float.valueOf(map.getString("UNIT_FEE", "")) / 100;
            float sumfee = Float.valueOf(map.getString("SUMFEE", "")) / 100; // 某一行不含税金额
            float rate = Float.valueOf(map.getString("RATE", "")) / 100;
            float sumtax = Float.valueOf(map.getString("SUMTAX", "")) / 100;

            print_data1.append(map.getString("FEEITEM_NAME", "") + "~~");// 计税科目名称
            print_data2.append(map.getString("GOODS_NAME", "") + "~~");// 型号
            print_data3.append(map.getString("UNIT", "") + "~~"); // 单位
            print_data4.append(map.getString("COUNT", "") + "~~");// 数量
            print_data5.append(formatter.format(unitfee) + "~~"); // 单价
            print_data6.append(formatter.format(sumfee) + "~~"); // 金额
            String rate1 = "";
            if (rate % 1.0 == 0)
            {
                rate1 = (String.valueOf((int) rate));
            }
            else
            {
                rate1 = formatter.format(rate);
            }

            print_data7.append(rate1 + "%" + "~~"); // 税率

            print_data8.append(formatter.format(sumtax) + "~~"); // 税额

        }

        String allMoney = (String) formatter.format(Float.valueOf(sumData.getString("TOTAL_FEE", "")));

        String alltaxfee = (String) formatter.format(Float.valueOf(sumData.getString("TOTAL_TAXFEE", "")));
        printData.put("PRINT_DATA1", print_data1);// 计税科目名称
        printData.put("PRINT_DATA2", print_data2);// 型号
        printData.put("PRINT_DATA3", print_data3);// 单位
        printData.put("PRINT_DATA4", print_data4);// 数量
        printData.put("PRINT_DATA5", print_data5);// 单价
        printData.put("PRINT_DATA6", print_data6);// 金额
        printData.put("PRINT_DATA7", print_data7);// 税率
        printData.put("PRINT_DATA8", print_data8);// 税额

        printData.put("ALL_MONEY", allMoney); // 总金额
        printData.put("ALL_TAXFEE", alltaxfee); // 总税额
        printData.put("PRINT_TIME", SysDateMgr.getSysDate().substring(0, 4) + "年" + SysDateMgr.getSysDate().substring(5, 7) + "月" + SysDateMgr.getSysDate().substring(8, 10) + "日");// 打印时间

        float totalMoney = Float.valueOf(allMoney) + Float.valueOf(alltaxfee);
        printData.put("ALL_MONEY_LOWER", String.format("%1$3.2f", totalMoney));// 总价值
        printData.put("MONEY_UPPER", PrintMgr.floatToRMB(totalMoney)); // 零壹贰叁肆伍陆柒捌玖

        return printData;
    }

    // 增值税专票打印 , 不同税率的计税科目打在不同增值税发票上
    public static IDataset printTaxProprietaryReceipt(IDataset taxInfoList) throws Exception
    {

        IDataset returnResults = new DatasetList();

        // 税率
        List<String> rateArray = getTaxRateArrayList(taxInfoList);
        int a = rateArray.size();

        // 计税数据
        IDataset[] rateDs = new IDataset[a];
        // 合计金额
        IData[] sumdata = new DataMap[a];

        if (a > 1)
        {
            rateDs = separateTaxIntoGroupByRate(taxInfoList, rateArray);
        }
        else
        {
            rateDs[0] = new DatasetList();
            rateDs[0].addAll(taxInfoList);
        }

        for (int i = 0; i < rateDs.length; i++)
        {
            IDataset taxDataset = rateDs[i];
            sumdata[i] = new DataMap();

            // tradefee_tax表保存的是给财务化boss的数据，一个计税科目一条数据，
            // 发票上则需要按照视同销售将营业费和折扣折让拆分成两条分开体现
            // 1、tax表折扣折让计税的记录转换成视同销售的发票打印记录
            // 2、缴费不为0，并存在折扣的，发票上增加打印折扣信息
            IDataset printdataset = changeToTaxPrintModeData(taxDataset);
            IDataset disdataset = addDiscountRecords(taxDataset);
            IDataset scoreset = addScoreRecords(taxDataset);

            if (IDataUtil.isNotEmpty(disdataset))
            {
                printdataset.addAll(disdataset);
            }
            if (IDataUtil.isNotEmpty(scoreset))
            {
                printdataset.addAll(scoreset);
            }

            // 累计各发票上的总费用、税额、不含税价
            sumdata[i] = sumAllTaxFeesBySameRate(printdataset);

            // 数据准备
            IData printData = prepareTaxPrintData(printdataset, sumdata[i]);

            printData.putAll(commPrintData);

            // 绑定打印模板
            IData printFeeData = ReceiptNotePrintMgr.parsePrintData(printData, templetItems);

            IData returnData = new DataMap();
            int icount = rateDs.length;
            String papername = "增值税专用发票";

            if (icount > 1)
            {
                String title = papername + "  第" + i + 1 + "/" + icount + "张";
                returnData.put("NAME", title);
            }
            else
            {
                returnData.put("NAME", papername);
            }

            returnData.put("PRINT_DATA", printFeeData);
            returnData.put("TYPE", "1");
            returnData.put("TEMPLET_CODE", commPrintData.getString("TEMPLET_CODE"));
            returnData.put("TEMPLET_TYPE", commPrintData.getString("TEMPLET_TYPE"));
            returnResults.add(returnData);

            // 金税接口
            // TODO

            // 插增值税打印日志明细表
            // TODO
        }

        return returnResults;

    }

    /**
     * 集团业务票据打印数据准备 （1）获取需要打印的模板项信息： data （2）获取打印数据 taxInfoList （3）处理打印票据，包括对发票和收据是否合打的处理、对打印数据的格式化处理等 （4）拼串返回
     */
    public static IDataset printTaxReceiptForGrp(IData data, IDataset taxInfoList) throws Exception
    {

        IData grpData = UcaInfoQry.qryGrpInfoByCustId(data.getString("CUST_ID"));
        String groupId = grpData.getString("GROUP_ID");
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("OPER_TYPE", "2");// 2:表示获得集团客户资质信息

        // 客户管理资质鉴定接口
        IDataset resultinfos = CSAppCall.call("CM_TAX_GETTAXAPPLYINFO", param);

        if (IDataUtil.isEmpty(resultinfos))
        {
            CSAppException.apperr(TaxException.CRM_TAX_12, groupId);
        }
        IData resutl = resultinfos.getData(0);
        if (IDataUtil.isEmpty(resutl) || !"0".equals(resutl.getString("X_RESULTCODE", "")))
        {
            CSAppException.apperr(TaxException.CRM_TAX_12, groupId);
        }
        String quatype = resutl.getString("QUA_TYPE", "");
        if ("3".equals(quatype))// 非增值税纳税人
        {
            CSAppException.apperr(TaxException.CRM_TAX_13);
        }

        // 增值税普票:RECEIPT_G0004
        // 增值税专票:RECEIPT_G0005
        String taxType = data.getString("TEMPLET_TYPE", ""); // 发票类型:
        IDataset returnResults = new DatasetList();

        // 获取需要打印的模板项信息：
        IData templet = new DataMap();
        templet.put("TEMPLET_TYPE", taxType);
        templet.put("TRADE_TYPE_CODE", "-1");
        templet.put("RELATION_KIND", "0");// 按地州
        templet.put("RELATION_ATTR", CSBizBean.getTradeEparchyCode());// 地州编码

        // 获取打印模板项信息
        templetItems = getReceiptTempletItems(templet);

        if (IDataUtil.isEmpty(templetItems))
        {
            return null;
        }
        data.put("TEMPLET_CODE", templetItems.getData(0).getString("TEMPLET_CODE", ""));

        IData mstPrintData = new DataMap();

        // 设置通用参数
        createCommonPrintData(data, mstPrintData);
        commPrintData.putAll(mstPrintData);
        commPrintData.put("COMP_NAME", resutl.getString("COMP_NAME", ""));// 企业名称
        commPrintData.put("QUA_FLAG", resutl.getString("QUA_FLAG", ""));// 增值税纳税人识别码
        commPrintData.put("ADDRESS", resutl.getString("ADDRESS", ""));// 企业地址
        commPrintData.put("TELEPHONE", resutl.getString("TELEPHONE", ""));// 电话
        commPrintData.put("OPEN_BANK", resutl.getString("OPEN_BANK", ""));// 开户行
        commPrintData.put("BANK_NO", resutl.getString("BANK_NO", ""));// 帐号

        // 增值税专用发票，不同税率需要打在不同的发票上
        if (RECEIPT_G0004.equals(taxType))
        {
            // 按税率分组打印增值税专用发票
            returnResults = printTaxProprietaryReceipt(taxInfoList);

        }
        else
        {
            CSAppException.apperr(TaxException.CRM_TAX_6);
        }

        // 插增值税打印日志表
        // TODO

        return returnResults;
    }

    // 根据不同税率将专票计税数据分组
    public static IDataset[] separateTaxIntoGroupByRate(IDataset taxInfoList, List<String> ratelist) throws Exception
    {

        int b = ratelist.size();
        IDataset[] rateDs = new IDataset[b];

        for (int row = 0; row < b; row++)
        {
            String rateTmp = ratelist.get(row).toString();
            rateDs[row] = new DatasetList();

            for (int k = 0; k < taxInfoList.size(); k++)
            {
                IData map = taxInfoList.getData(k);
                if (rateTmp.equals(map.getString("RATE", "")))
                {
                    rateDs[row].add(map);
                }
            }
        }

        return rateDs;
    }

    // 累计发票上的总费用、税额、不含税价
    public static IData sumAllTaxFeesBySameRate(IDataset taxInfoList) throws Exception
    {
        IData sumData = new DataMap();
        Float sumfee = 0.0f;
        Float sumtax = 0.0f;
        Float taxrate = 0.0f;

        for (int row = 0, tSize = taxInfoList.size(); row < tSize; row++)
        {
            IData map = taxInfoList.getData(row);

            sumfee = Float.valueOf(map.getString("SUMFEE", "0.0")) + sumfee;// 不含税金额
            sumtax = Float.valueOf(map.getString("SUMTAX", "0.0")) + sumtax;// 税额
            taxrate = Float.valueOf(map.getString("RATE", "0"));
        }

        sumData.put("TOTAL_FEE", sumfee / 100); // 各项不含税费用总金额
        sumData.put("TOTAL_TAXFEE", sumtax / 100);
        sumData.put("RATE", taxrate / 100);

        return sumData;
    }

}
