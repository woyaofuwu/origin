
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeePayMoneyInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;

/**
 * 个人业务票据打印处理 P0001:发票，P0002:收据，P0003:业务受理单，
 */
public class FeeNotePrintMgr extends CSBizBean
{
    /**
     * 备份票据打印信息
     * 
     * @param data
     * @throws Exception
     */
    public static String regPrintTicketLog(IData data) throws Exception
    {
        String printId = SeqMgr.getPrintId();
        String templet = data.getString("TEMPLET_TYPE", "");
        if (StringUtils.equals(templet, ReceiptNotePrintMgr.RECEIPT_P0001))
        {
            templet = "0";
        }
        else if (StringUtils.equals(templet, ReceiptNotePrintMgr.RECEIPT_P0002))
        {
            templet = "1";
        }
        IData printSaveData = new DataMap();
        printSaveData.put("PRINT_ID", printId);
        printSaveData.put("ORDER_ID", data.getString("ORDER_ID", ""));
        printSaveData.put("TRADE_ID", data.getString("TRADE_ID"));
        printSaveData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID")));
        printSaveData.put("TEMPLET_CODE", data.getString("TEMPLET_CODE", ""));
        printSaveData.put("TEMPLET_TYPE", templet);
        printSaveData.put("NOTE_NO", data.getString("NOTE_NO", "")); // 单联票据打印时候更新
        printSaveData.put("TAX_NO", data.getString("TAX_NO", "")); // 单联票据打印时候更新
        printSaveData.put("SOURCE_TYPE", "1");
        printSaveData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        printSaveData.put("ACCT_ID", data.getString("ACCT_ID", "-1"));
        printSaveData.put("PAY_NAME", data.getString("PAY_NAME", ""));
        printSaveData.put("PRINT_MODE", "");
        printSaveData.put("START_CYCLE_ID", "-1");
        printSaveData.put("END_CYCLE_ID", "-1");
        printSaveData.put("TRADE_TIME", data.getString("OPERATION_TIME", SysDateMgr.getSysTime()));
        printSaveData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        printSaveData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        printSaveData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        printSaveData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        printSaveData.put("TRADE_REASON_CODE", "-1");
        printSaveData.put("TOTAL_FEE", String.valueOf((Double.valueOf(data.getString("TOTAL_MONEY", "0"))).longValue()));
        printSaveData.put("REPRINT_FLAG", "0"); // 重打印标记
        printSaveData.put("PRINTED_FEE", "0");
        printSaveData.put("SPECITEM_PRINTFLAG", "");
        printSaveData.put("PREPRINT_FLAG", "0"); // 预打印标记
        printSaveData.put("REMARK", "");
        printSaveData.put("CANCEL_TAG", "0");
        printSaveData.put("POST_TAG", "");
        printSaveData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 这个后续需要确认，使用用户归属地州
        printSaveData.put("CANCEL_TIME", "");
        printSaveData.put("CANCEL_STAFF_ID", "");
        printSaveData.put("CANCEL_DEPART_ID", "");
        printSaveData.put("CANCEL_CITY_CODE", "");
        printSaveData.put("CANCEL_EPARCHY_CODE", "");
        printSaveData.put("CANCEL_REASON_CODE", "-1");
        printSaveData.put("RSRV_FEE1", "0");
        printSaveData.put("RSRV_FEE2", "0");
        printSaveData.put("RSRV_INFO1", data.getString("FEE_CONTENT", ""));
        printSaveData.put("RSRV_INFO2", data.getString("CUST_NAME", ""));
        printSaveData.put("RSRV_INFO3", data.getString("TRADE_TYPE_CODE", "-1"));
        printSaveData.put("RSRV_INFO4", data.getString("REMARK", ""));
        printSaveData.put("RSRV_INFO5", data.getString("ALL_MONEY_UPPER"));
        
        String assembFlag = "";
		String containOperFlag = data.getString("MERGE_FEE_LIST_OPER_FLAG");
		assembFlag += "1".equals(containOperFlag) ? "1": "0".equals(containOperFlag) ? "0" : "X";
    	String containForegiftFlag = data.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
    	assembFlag += "1".equals(containForegiftFlag) ? "1": "0".equals(containForegiftFlag) ? "0" : "X";
    	String containAdvanceFlag = data.getString("MERGE_FEE_LIST_ADVANCE_FLAG");
    	assembFlag += "1".equals(containAdvanceFlag) ? "1": "0".equals(containAdvanceFlag) ? "0" : "X";
		printSaveData.put("MERGE_FEE_TAG", assembFlag);

//        Dao.executeUpdateByCodeCode("TF_B_NOTEPRINTLOG", "INSERT_NOTEPRINT_LOG", printSaveData, data.getString("EPARCHY_CODE"));
        
        Dao.executeUpdateByCodeCode("TF_B_NOTEPRINTLOG", "INSERT_NOTEPRINT_LOG", printSaveData, Route.getJourDb(BizRoute.getRouteId()));

        return printId;
    }

    private String advancePay; // 预存

    private String foregift; // 押金

    private String operFee; // 营业费用

    private String totalFee; // 总费用

    private IDataset printReceipts; // 打印串组

    private IDataset TEMPLETITEM_P0001; // P0001:发票，发票配置项

    private IDataset TEMPLETITEM_P0002; // P0002:收据，收据配置项

    public FeeNotePrintMgr()
    {
        operFee = "0";
        foregift = "0";
        advancePay = "0";
        totalFee = "0";

        printReceipts = new DatasetList();

        TEMPLETITEM_P0001 = new DatasetList();
        TEMPLETITEM_P0002 = new DatasetList();
    }

    /**
     * 根据票据合并配置，合并打印内容，解析成打印串
     * 这里新发票处理，已经不走合并打印，所以清理了相关合并打印这套逻辑,全部按照分开打印
     * @param printData
     * @throws Exception
     */
    private void genFeeReceiptInfo(IData printData) throws Exception
    {
        double totalOperFee = printData.getDouble("TOTAL_OPER_FEE", 0);
        double totalForegift = printData.getDouble("TOTAL_FOREGIFT", 0);
        double totalAdvancePay = printData.getDouble("TOTAL_ADVANCE_PAY", 0);
        IData noteCfg = getNotePrintCfg(printData);

        int mergeType = noteCfg.getInt("PARA_CODE2", 0); // 合并类型
        String mergePaperType = noteCfg.getString("PARA_CODE3", "P0001"); // 合并打印默认票据类型
        String operFeePaperType = noteCfg.getString("PARA_CODE4", "P0001"); // 营业费打印默认票据类型
        String foregiftPaperType = noteCfg.getString("PARA_CODE5", "P0002"); // 押金打印默认票据类型
        String preFeePaperType = noteCfg.getString("PARA_CODE6", "P0002"); // 预存打印默认票据类型
        String mergePaperName = noteCfg.getString("PARA_CODE7", ReceiptNotePrintMgr.PRINTNAME_P0001); // 合并打印默认票据名称
        String operFeePaperName = noteCfg.getString("PARA_CODE8", ReceiptNotePrintMgr.PRINTNAME_P0001); // 营业费打印默认票据名称
        if (null != printData.getString("DIY_OPERFEENAME") && (!"".equals(printData.getString("DIY_OPERFEENAME"))))
        {
            operFeePaperName = printData.getString("DIY_OPERFEENAME");
        }
        String foregiftPaperName = noteCfg.getString("PARA_CODE9", ReceiptNotePrintMgr.PRINTNAME_P0002); // 押金打印默认票据名称
        if (null != printData.getString("DIY_FORENAME") && (!"".equals(printData.getString("DIY_FORENAME"))))
        {
            foregiftPaperName = printData.getString("DIY_FORENAME");
        }
        String preFeePaperName = noteCfg.getString("PARA_CODE10", ReceiptNotePrintMgr.PRINTNAME_P0002); // 预存打印默认票据名称
        if (null != printData.getString("DIY_ADVANCENAME") && (!"".equals(printData.getString("DIY_ADVANCENAME"))))
        {
            preFeePaperName = printData.getString("DIY_ADVANCENAME");
        }
        // 有价卡退卡打印收据
        if (StringUtils.equals("419", printData.getString("TRADE_TYPE_CODE")))
        {
            operFeePaperType = "P0002";
        }
        if (totalOperFee != 0)
        {
            printData.putAll(printData.getData("0"));
            printData.remove("0");
            mergeFeeList(printData, true, false, false);
            setFeeNeedPrintReceipt(printData, totalOperFee, operFeePaperType, operFeePaperName);
        }
        if (totalForegift != 0)
        {
            printData.putAll(printData.getData("1"));
            printData.remove("0");
            mergeFeeList(printData, false, true, false);
            setFeeNeedPrintReceipt(printData, totalForegift, foregiftPaperType, foregiftPaperName);
        }
        if (totalAdvancePay > 0)
        {
            printData.putAll(printData.getData("2"));
            printData.remove("0");
            mergeFeeList(printData, false, false, true);
            setFeeNeedPrintReceipt(printData, totalAdvancePay, preFeePaperType, preFeePaperName);
        }
    }

    public String getAdvancePay()
    {
        return advancePay;
    }

    /**
     * 获取打印数据，包括基本数据和特殊数据，特殊数据是指的传过来的一些数据
     * 
     * @param commonPrintData
     * @param tradeData
     * @throws Exception
     */
    private IData getCommonPrintData(RegTradeData rtd) throws Exception
    {
        IData commData = new DataMap();
        MainTradeData trade = rtd.getMainTradeData();
        UcaData uca = rtd.getUca();
        UserTradeData user = uca.getUser();
        CustomerTradeData cust = uca.getCustomer();
        AccountTradeData acct = uca.getAccount();
        String operationTime = SysDateMgr.getSysTime();
        String tradeTypeCode = rtd.getTradeTypeCode();
        IData tradeTypeData = ReceiptNotePrintMgr.queryTradeType(tradeTypeCode);
        String serialNumber = trade.getSerialNumber();

        commData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        commData.put("TRADE_ID", rtd.getTradeId());
        commData.put("TRADE_TYPE_CODE", rtd.getTradeTypeCode());
        // TD无线固话免填单SERIAL_NUMBER不打印898前缀
        if (!StringUtils.isBlank(serialNumber) && serialNumber.startsWith("898"))
        {
            serialNumber = serialNumber.substring(3);
        }
        commData.put("SERIAL_NUMBER", serialNumber);
        commData.put("TRADE_TYPE", tradeTypeData.getString("TRADE_TYPE"));
        commData.put("CUST_NAME", trade.getCustName());
        commData.put("PAY_MODE", getPayModeDesc(rtd.getTradeId())); // 支付方式
        commData.put("TOTAL_OPER_FEE", getOperFee());
        commData.put("TOTAL_FOREGIFT", getForegift());
        commData.put("TOTAL_ADVANCE_PAY", getAdvancePay());
        commData.put("TOTAL_MONEY", getTotalFee());

        commData.put("OPERATION_TIME", operationTime);
        commData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        commData.put("OPERATION_YEAR", operationTime.substring(0, 4));
        commData.put("OPERATION_MONTH", operationTime.substring(5, 7));
        commData.put("OPERATION_DAY", operationTime.substring(8, 10));

        commData.put("PSPT_ID", (null == cust) ? "" : cust.getPsptId());
        commData.put("PSPT_TYPE_CODE", (null == cust) ? "" : cust.getPsptTypeCode());
        commData.put("BRAND_CODE", trade.getBrandCode());
        commData.put("PRODUCT_ID", trade.getProductId());
        commData.put("ACCT_ID", (null == acct) ? "" : acct.getAcctId());
        commData.put("BANK_ACCT_NO", (null == acct) ? "" : acct.getBankAcctNo());
        commData.put("CUR_CYCLE", SysDateMgr.getNowCyc());
        /**
         * IDataset userAcctDays = UserAcctDayInfoQry.getUserAcctDay(user.getUserId()); if (userAcctDays != null &&
         * userAcctDays.size() > 0) { commData.put("CUR_CYCLE", userAcctDays.getData(0).getString("ACCT_DAY")); }
         */
        String bankName = "其他";
        if (null != acct)
        {
            bankName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
            { "BANK_CODE", "EPARCHY_CODE", "CITY_CODE" }, "BANK", new String[]
            { acct.getBankCode(), CSBizBean.getTradeEparchyCode(), user.getCityCode() });
        }
        commData.put("BANK_NAME", bankName);

        commData.put("PAY_NAME", (null == acct) ? commData.getString("CUST_NAME") : acct.getPayName());// 户名
        commData.put("EPARCHY_CODE", trade.getEparchyCode()); // 后续登记日志需要

        commData.put("STAFF_ID", getVisit().getStaffId());
        commData.put("STAFF_NAME", getVisit().getStaffName());
        commData.put("DEPART_NAME", getVisit().getDepartName());// 部门名称
        
        if(tradeTypeCode.equals("9115")){	//如果是铁通终端销售，需要存放一些销售终端的信息
        	commData.put("RSRV_STR1", trade.getRsrvStr1());
        	commData.put("RSRV_STR2", trade.getRsrvStr2());
        	commData.put("RSRV_STR3", trade.getRsrvStr3());
        	commData.put("RSRV_STR4", trade.getRsrvStr4());
        }

        return commData;
    }

    public String getForegift()
    {
        return foregift;
    }

    /**
     * 获取费用打印配置信息(费用打印纸张，合并配置)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IData getNotePrintCfg(IData param) throws Exception
    {
        IData compParam = new DataMap();
        if ("0".equals(param.getString("OPEN_NOTE_TYPE", "")) && "10".equals(param.getString("TRADE_TYPE_CODE")))
        {
            param.put("TRADE_TYPE_CODE", "-1");
        }

        compParam.put("SUBSYS_CODE", "CSM");
        compParam.put("PARAM_ATTR", "9901");
        compParam.put("PARAM_CODE", "CNOTE");
        compParam.put("PARA_CODE1", param.getString("TRADE_TYPE_CODE"));
        compParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset notPara = ParamInfoQry.getPrintCfg(compParam);

        if (notPara == null || notPara.size() == 0)
        {
            CSAppException.apperr(PrintException.CRM_PRINT_3);
        }

        return notPara.getData(0);
    }

    public String getOperFee()
    {
        return operFee;
    }

    /**
     * 获取支付方式
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private String getPayModeDesc(String tradeId) throws Exception
    {
        String payModeDesc = "";
        IDataset payMoneys = TradeFeePayMoneyInfoQry.getPayMoneyInfo(tradeId, null);
        if (null != payMoneys && !payMoneys.isEmpty())
        {
            String payMode = null;
            for (int i = 0; i < payMoneys.size(); i++)
            {
                payMode = UPayModeInfoQry.getPayModeNameByPayModeCode((String) payMoneys.get(i, "PAY_MONEY_CODE"));
                if (null != payMode && !payMode.equals(""))
                {
                    payModeDesc += payModeDesc.equals("") ? payMode : "+" + payMode;
                }
            }
        }
        return payModeDesc.equals("") ? "现金" : payModeDesc;
    }

    public IDataset getPrintReceipts()
    {
        return printReceipts;
    }

    /**
     * 根据打印格式模板类型编码（纸张编码）获得打印各项格式
     * 
     * @throws Exception
     */
    private IDataset getTempletItemByType(String type) throws Exception
    {
        if (type.equals(ReceiptNotePrintMgr.RECEIPT_P0001))
        {
            return this.TEMPLETITEM_P0001;
        }
        else if (type.equals(ReceiptNotePrintMgr.RECEIPT_P0002))
        {
            return this.TEMPLETITEM_P0002;
        }
        else
        {
            CSAppException.apperr(PrintException.CRM_PRINT_8);
        }
        return null;
    }

    public String getTotalFee()
    {
        return totalFee;
    }

    /**
     * 普通登记订单台账打印 会根据订单编号或者台账流水号进行判断
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset printTradeFee(RegTradeData rtd, IData data) throws Exception
    {
        String tradeTypeCode = rtd.getTradeTypeCode();
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData td = rtd.getMainTradeData().toData();
        int fee = td.getInt("OPER_FEE", 0) + td.getInt("FOREGIFT", 0) + td.getInt("ADVANCE_PAY", 0);
        // 如果无费用数据，则返回
        if (fee == 0)
        {
            return null;
        }
        setOperFee(td.getString("OPER_FEE", "0"));
        setForegift(td.getString("FOREGIFT", "0"));
        setAdvancePay(td.getString("ADVANCE_PAY", "0"));
        setTotalFee(String.valueOf(fee));

        //REQ201505150014 请更改抢4G手机红包营销活动打印发票的货品名称 20150515 by songlm
        //1、先判断是否是营销活动   2、再获取主台帐的包ID，通过包ID查找td_b_package_ext配置数据
        //3、再获取td_b_package_ext表的TAG_SET3，看是否配置为采用特殊打印模版   4、获取主台帐的RSRV_STR7，判断是否是购机活动
        //5、主台帐的REMARK存放了终端串码在华为返回的【品牌型号】，是通过reg的action【ResetMainTradeAction.java】设置的，来源于营销活动台帐的rsrvStr23
        
        String relationKind = "0";//将原来写死的0，改为变量，默认还是0
        String allMoneyName = "";
        
        //1、先判断是否是营销活动
        if("240".equals(tradeTypeCode)){
        	//2、获取到主台帐中的RSRV_STR2值，即包ID
        	String packegeId = td.getString("RSRV_STR2","");
        	        	if(StringUtils.isNotEmpty(packegeId))
        	{
        		//通过包ID查看td_b_package_ext表数据
            	IDataset packageExtDataset = PkgExtInfoQry.queryPackageExtInfo(packegeId, tradeEparchyCode);
            	
            	//3、获取到td_b_package_ext的TAG_SET3值，并判断第一位是不是1，1代表特殊模版，空或0代表正常
                String tagSet3 = packageExtDataset.getData(0).getString("TAG_SET3","");
                
                //如果TAG_SET3非空，有值，并且第一位是1，即代表采用特殊模版打印
                if (StringUtils.isNotBlank(tagSet3) && tagSet3.length() > 0 && tagSet3.substring(0, 1).equals("1"))
                {
                	//4、获取主台帐表中的RSRV_STR7字段，如果是营销活动的主台帐，该值存的是营销活动类型，即YX03之类的值
                	String rsrvStr7 = td.getString("RSRV_STR7","");
                	
                	//判断是否是购机类活动，是则继续
                	if("YX03".equals(rsrvStr7) || "YX07".equals(rsrvStr7) || "YX08".equals(rsrvStr7) || "YX09".equals(rsrvStr7))
                	{
                		//取到主台帐表中的REMARK字段值，如果是购机活动，该值存的是终端串码在华为返回的【品牌型号】
                        String rsrvStr23 = td.getString("REMARK");

                        if(StringUtils.isNotEmpty(rsrvStr23)){
                        	allMoneyName = rsrvStr23;
                        	relationKind = "2";
                        }
                	}
                }
        	}
        }
        //end
        
        // 获取需要打印的模板项信息： TEMPLETITEM_P0001=发票、TEMPLETITEM_P0002=收据
        if (!StringUtils.equals(getOperFee(), "0") || !StringUtils.equals(getForegift(), "0") || !StringUtils.equals(getAdvancePay(), "0"))
        {
            TEMPLETITEM_P0001 = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0001, relationKind, tradeEparchyCode);
            TEMPLETITEM_P0002 = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0002, "0", tradeEparchyCode);
        }

        // 如果获取到任何需要打印的模板项信息，则返回
        if (TEMPLETITEM_P0001.isEmpty() && TEMPLETITEM_P0002.isEmpty())
        {
            return null;
        }

        // 获取公用数据
        IData printData = getCommonPrintData(rtd);
        printData.put("ALL_MONEY_NAME", allMoneyName);//REQ201505150014 请更改抢4G手机红包营销活动打印发票的货品名称 20150515 by songlm

        // 设置费用数据
        setPrintFeeData(printData, rtd);

        // 自定义数据，后续可能会覆盖前面拼接的打印数据串
        IData params = new DataMap(data.getString("PRINT_PARAMS", "{}"));
        if (!params.isEmpty())
            printData.putAll(params);
        printData.put("ORDER_ID", data.getString("ORDER_ID", "-1"));
        printData.put("BACKUP_FLAG", data.getBoolean("BACKUP_FLAG", true)); // 默认进行备份

        // 生成打印票据信息
        genFeeReceiptInfo(printData);

        return getPrintReceipts();
    }

    public void setAdvancePay(String advancePay)
    {
        this.advancePay = advancePay;
    }

    /**
     * 设置费用打印串
     * 
     * @param printData
     * @param totalMoney
     * @param operFeePaperType
     * @throws Exception
     */
    private void setFeeNeedPrintReceipt(IData printData, double totalMoney, String operFeePaperType, String operFeePaperName) throws Exception
    {
        boolean backupFalg = printData.getBoolean("BACKUP_FLAG", true);
        String fee = String.format("%1$3.2f", totalMoney / 100.0);
        printData.put("ALL_MONEY_LOWER", fee);
        printData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalMoney / 100.0));
        printData.put("ALL_MONEY", fee);
        if ("-1".equals(operFeePaperType))
        {
            int index1 = 0, index2 = 0;
            index1 = operFeePaperName.indexOf('|', index2);
            if (index1 == -1)
            {
                CSAppException.apperr(PrintException.CRM_PRINT_9);
            }
            String operFeePaperName1 = operFeePaperName.substring(0, index1);
            String operFeePaperName2 = operFeePaperName.substring(index1 + 1);
            setNeedPrintReceipt(printData, getTempletItemByType(operFeePaperType), ReceiptNotePrintMgr.RECEIPT_P0001, operFeePaperName1, backupFalg);
            setNeedPrintReceipt(printData, getTempletItemByType(operFeePaperType), ReceiptNotePrintMgr.RECEIPT_P0002, operFeePaperName2, backupFalg);
        }
        setNeedPrintReceipt(printData, getTempletItemByType(operFeePaperType), operFeePaperType, operFeePaperName, backupFalg);
    }

    public void setForegift(String foregift)
    {
        this.foregift = foregift;
    }

    /**
     * 设置票据信息
     * 
     * @param printData
     *            : 打印数据 receiptTempletItems: 打印模板项信息 printPaperName: 打印提示的纸张名称 needBake:
     *            是否需要备份票据打印信息，收据和发票需要备份，true-备份，false-不备份
     */
    private void setNeedPrintReceipt(IData printData, IDataset receiptTempletItems, String operFeePaperType, String paperName, boolean needBake) throws Exception
    {
        // 备份票据打印信息，目前收据和发票需要备份
    	String printId = "";
        if (needBake)
        {
            IData inParam = new DataMap();
            inParam.putAll(printData);
            inParam.put("TEMPLET_CODE", receiptTempletItems.getData(0).getString("TEMPLET_CODE"));
            inParam.put("TEMPLET_TYPE", receiptTempletItems.getData(0).getString("TEMPLET_TYPE"));
            printId = regPrintTicketLog(inParam);
        }else {
        	if ("P0001".equals(operFeePaperType) || "P0002".equals(operFeePaperType)) { //重打发票/收据，需要获取PRINT_ID(通过拆分组合来获取)
        		String assembFlag = "";
        		String containOperFlag = printData.getString("MERGE_FEE_LIST_OPER_FLAG");
        		assembFlag += "1".equals(containOperFlag) ? "1": "0".equals(containOperFlag) ? "0" : "X";
            	String containForegiftFlag = printData.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
            	assembFlag += "1".equals(containForegiftFlag) ? "1": "0".equals(containForegiftFlag) ? "0" : "X";
            	String containAdvanceFlag = printData.getString("MERGE_FEE_LIST_ADVANCE_FLAG");
            	assembFlag += "1".equals(containAdvanceFlag) ? "1": "0".equals(containAdvanceFlag) ? "0" : "X";
        		if (!"XXX".equals(assembFlag) && !"000".equals(assembFlag)) {
        			IDataset rePrintTrades = Dao.qryByCode("TF_B_NOTEPRINTLOG", "SEL_BY_TRADEID_FLAG_2", printData);
                	if(null != rePrintTrades && !rePrintTrades.isEmpty()){
                		for(int i=0; i<rePrintTrades.size(); i++){
                			if (assembFlag.equals(rePrintTrades.getData(i).getString("MERGE_FEE_TAG"))) {
                				printId = rePrintTrades.getData(i).getString("PRINT_ID");
                				break;
                			}
                		}
                	}
        		}
        	}
        }

        /**
         * 在此根据operFeePaperType(P0001,P0002)获得发票、收据组合内容模板(主要是配置中间空白块)， 组合成新的数据以符合格式模板特殊要求。
         */
        ReceiptNotePrintMgr.combineReceiptContent(printData, operFeePaperType);

        IData printFeeData = ReceiptNotePrintMgr.parsePrintData(printData, receiptTempletItems);

        if (IDataUtil.isNotEmpty(printFeeData))
        {
            IData printInfo = new DataMap();
            printInfo.put("NAME", "".equals(paperName) ? ReceiptNotePrintMgr.PRINTNAME_DATA.getString(operFeePaperType) : paperName);
            printInfo.put("PRINT_DATA", printFeeData);
            printInfo.put("TYPE", operFeePaperType);
            printInfo.put("PRINT_ID", printId);
            
            // 后续更新台账日志记录需要
            printInfo.put("TRADE_ID", printData.getString("TRADE_ID"));
            printInfo.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE"));
            printInfo.put("TRADE_TYPE_CODE", printData.getString("TRADE_TYPE_CODE"));
            printInfo.put("SERIAL_NUMBER", printData.getString("SERIAL_NUMBER"));

            // 以下为电子受理单
            printInfo.put("HAS_TICKET", "0"); // 电子票据标记

            String tradeTypeCode = printData.getString("TRADE_TYPE_CODE");
            String feeMode = printData.getString("FEE_MODE");
            String feeTypeCodes = printData.getString("FEE_TYPE_CODES"); // 数据结构为[|10|89|65|]
            //有价卡销售退卡不进行票据打印
            if (!StringUtils.equals("419", tradeTypeCode))
            {
                String totalFee = "";
                if (!"".equals(feeMode))
                {
                    // 营业费
                    if ("0".equals(feeMode))
                    {
                        totalFee = printData.getString("TOTAL_OPER_FEE", "0");
                    }
                    // 押金
                    if ("1".equals(feeMode))
                    {
                        totalFee = printData.getString("TOTAL_FOREGIFT", "0");
                    }
                    // 预存
                    if ("2".equals(feeMode))
                    {
                        totalFee = printData.getString("TOTAL_ADVANCE_PAY", "0");
                    }
                }

                if (!StringUtils.equals("", totalFee) && Integer.parseInt(totalFee) > 0)
                {
                    printInfo.put("FEE_MODE", feeMode);
                    printInfo.put("TOTAL_FEE", totalFee);

                    printInfo.put("HAS_TICKET", "1");
                }
            }
            printInfo.put("FEE_TYPE_CODES", feeTypeCodes);
            if (tradeTypeCode.equals("234"))
            {
                printInfo.put("NAME", "收据");
            }
            if (tradeTypeCode.equals("2108"))
            {
                printInfo.put("NAME", "发票");
            }

            printReceipts.add(printInfo);
        }

    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    /**
     * 设置费用列表打印数据
     * 
     * @param commonPrintData
     * @param tradeSet
     * @throws Exception
     */
    private void setPrintFeeData(IData commonPrintData, RegTradeData rtd) throws Exception
    {
        if (getTotalFee() == null || "0".equals(getTotalFee()))
        {
            return;
        }
        IDataset feeLists = rtd.getDataset(TradeTableEnum.TRADE_FEESUB.getValue());
        if (IDataUtil.isEmpty(feeLists)) return;
        
        commonPrintData.putAll(getPrintReceiptFee(rtd.getTradeId(), rtd.getTradeTypeCode(), feeLists, "-1", 1));
    }
    
    /**
     * 获取费用明细打印串
     * 返回格式为DataMap[0,1,2]
     * 对应的键值数据，为DataMap类型，可以直接提交给打印模板
     * @param tradeId
     * @param feeMode	feeMode=-1返回营业费，押金，预存，否则返回对应的类型的打印数据
     * @param mark
     * @return
     * @throws Exception
     */
    public static IData getPrintReceiptFee(String tradeId, String tradeTypeCode, String feeMode, int mark) throws Exception
    {
    	IDataset feeLists = TradefeeSubInfoQry.getFeeListByTrade(tradeId);
    	if (IDataUtil.isEmpty(feeLists)){
    		//获取流量直充业务费用 lihb
    		IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            feeLists = Dao.qryByCode("TF_BH_TRADE", "SEL_PRINT_FEELIST_FLOW", param, Route.getJourDb());
            if(IDataUtil.isEmpty(feeLists)){
        		return new DataMap();
            }
    	} 
    	return getPrintReceiptFee(tradeId, tradeTypeCode, feeLists, feeMode, mark);
    }
    
    /**
     * 获取费用明细打印串
     * @param tradeId
     * @param feeLists
     * @param feeMode   feeMode=-1返回营业费，押金，预存，否则返回对应的类型的打印数据
     * @param mark[-1,1] 表示费用是否需要打印负数金额
     * @return
     * @throws Exception
     */
    public static IData getPrintReceiptFee(String tradeId, String tradeTypeCode, IDataset feeLists, String feeMode, int mark) throws Exception
    {
        IData printData=new DataMap();
    	IData feeData = null;
        String tmpFeeMode, feeTypCode, feeTypeDes, payFeeStr;
        int payFee;
        StringBuilder operFeeContentSb = new StringBuilder();
        StringBuilder operFeeTypeSb = new StringBuilder();
        StringBuilder operFeeSb = new StringBuilder();
       
        StringBuilder brandModelSb = new StringBuilder();
        StringBuilder unitSb = new StringBuilder();
        StringBuilder quantitySb = new StringBuilder();
        StringBuilder priceSb = new StringBuilder();

        StringBuilder foregiftContentSb = new StringBuilder();
        StringBuilder foregiftFeeTypeSb = new StringBuilder();
        StringBuilder foregiftSb = new StringBuilder();
        
        StringBuilder preContentSb = new StringBuilder();
        StringBuilder preFeeTypeSb = new StringBuilder();
        StringBuilder preSb = new StringBuilder();
        
        IData operFeeData = new DataMap();
        IData foregiftData = new DataMap();
        IData advancePay = new DataMap();
        boolean flag= false;
        
        for (int i = 0; i < feeLists.size(); i++)
        {
            feeData = feeLists.getData(i);
            tmpFeeMode = feeData.getString("FEE_MODE");
            if(StringUtils.equals("9", tmpFeeMode))//商务电话开户,FEE_MODE=9,当做2来处理
            	tmpFeeMode = "2";
            feeTypCode = feeData.getString("FEE_TYPE_CODE");
            feeTypeDes = FeeListMgr.getFeeTypeCodeDesc(tmpFeeMode, feeTypCode);
            payFee = feeData.getInt("FEE", 0);
            if (payFee == 0)
                continue;
            payFee=payFee*mark;
            
            flag=StringUtils.equals(feeMode, tmpFeeMode) || StringUtils.equals(feeMode, "-1")? true : false;
            
            payFeeStr = String.format("%1$3.2f", payFee / 100.0);
            //营业费
            if (flag && StringUtils.equals("0", tmpFeeMode))
            {
            	operFeeContentSb.append(feeTypeDes + "        " + payFeeStr + "~");
            	
            	if (StringUtils.equals(feeTypCode, "10")){
            		IData simPrintData=getSimCardPrintInfo(tradeId, tradeTypeCode);

            		operFeeTypeSb.append(simPrintData.getString("FEE_TYPE")+"~");
            		brandModelSb.append(simPrintData.getString("BRAND_MODEL")+"~");
            		unitSb.append(simPrintData.getString("UNIT")+"~");
            		quantitySb.append(simPrintData.getString("QUANTITY")+"~");
            		priceSb.append(payFeeStr+"~");
            		operFeeSb.append(payFeeStr + "~");
                }else{
                	operFeeTypeSb.append(feeTypeDes + "~");
                	operFeeSb.append(payFeeStr + "~");
                }
                operFeeData.put("FEE_TYPE_CODES", operFeeData.getString("FEE_TYPE_CODES", "") + "|" + feeTypCode);
            }
            else if (flag && StringUtils.equals("1", tmpFeeMode))
            { // 押金
            	foregiftContentSb.append(feeTypeDes + "        " + payFeeStr + "~");
            	foregiftFeeTypeSb.append(feeTypeDes + "~");
            	foregiftSb.append(payFeeStr + "~");
            	
                foregiftData.put("FEE_TYPE_CODES", foregiftData.getString("FEE_TYPE_CODES", "") + "|" + feeTypCode);
            }
            else if (flag && StringUtils.equals("2", tmpFeeMode))
            { // 预存
            	preContentSb.append(feeTypeDes + "        " + payFeeStr + "~");
                preFeeTypeSb.append(feeTypeDes + "~");
                preSb.append(payFeeStr + "~");
            	
                advancePay.put("FEE_TYPE_CODES", advancePay.getString("FEE_TYPE_CODES", "") + "|" + feeTypCode);
            }
        }
        if (IDataUtil.isNotEmpty(operFeeData))
        {
        	//发票打印字段
        	operFeeData.put("FEE_TYPE", operFeeTypeSb.toString());
        	operFeeData.put("BRAND_MODEL", brandModelSb.toString());
        	operFeeData.put("UNIT", unitSb.toString());
        	operFeeData.put("QUANTITY", quantitySb.toString());
        	operFeeData.put("PRICE", priceSb.toString());
        	operFeeData.put("FEE", operFeeSb.toString());
        	
        	//收据打印字段
        	operFeeData.put("FEE_CONTENT", operFeeContentSb.toString());
        	
            operFeeData.put("FEE_TYPE_CODES", operFeeData.getString("FEE_TYPE_CODES", "") + "|");
            operFeeData.put("FEE_MODE", "0");
            printData.put("0", operFeeData);
        }
        if (IDataUtil.isNotEmpty(foregiftData))
        {
        	//发票打印字段
        	foregiftData.put("FEE_TYPE", foregiftFeeTypeSb.toString());
        	foregiftData.put("FEE", foregiftSb.toString());
        	
        	//收据打印字段
        	foregiftData.put("FEE_CONTENT", foregiftContentSb.toString());
            foregiftData.put("FEE_TYPE_CODES", foregiftData.getString("FEE_TYPE_CODES", "") + "|");
            foregiftData.put("FEE_MODE", "1");
            printData.put("1", foregiftData);

        }
        if (IDataUtil.isNotEmpty(advancePay))
        {
        	//发票打印字段
        	advancePay.put("FEE_TYPE", preFeeTypeSb.toString());
        	advancePay.put("FEE", preSb.toString());
        	
        	//收据打印字段
        	advancePay.put("FEE_CONTENT", preContentSb.toString());
            advancePay.put("FEE_TYPE_CODES", advancePay.getString("FEE_TYPE_CODES", "") + "|");
            advancePay.put("FEE_MODE", "2");
            
            printData.put("2", advancePay);
        }
        operFeeContentSb = operFeeTypeSb = operFeeSb = null;
        foregiftContentSb = foregiftFeeTypeSb = foregiftSb = null;
        preContentSb = preFeeTypeSb =  preSb = null;
        
        return printData;
    }
    
    /**
     * 获取SIM打印费用数据
     * @param tradeId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IData getSimCardPrintInfo(String tradeId, String tradeTypeCode) throws Exception{
    	String resCode = null;
    	boolean useEmptyCard=true;
    	IData tradeInfos = null;
    	// 异地写卡直接插历史台账，取保留字段4为白卡号
    	if (StringUtils.equals(tradeTypeCode,"141"))
        {
    		tradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
    		if(IDataUtil.isEmpty(tradeInfos)){
    			CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);
    		}
    		resCode = tradeInfos.getString("RSRV_STR4");
        }
    	//大客户备卡申请
    	else if(StringUtils.equals(tradeTypeCode,"146")){
			tradeInfos = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", null);
			if(IDataUtil.isEmpty(tradeInfos)){
    			tradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
        		if(IDataUtil.isEmpty(tradeInfos)){
        			CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);
        		}
    		}
			resCode = tradeInfos.getString("RSRV_STR4");
			useEmptyCard=false;
		}
    	else{
        	IDataset tradeResInfos = TradeResInfoQry.qryTradeResInfosByType(tradeId, "1", null);
            if (IDataUtil.isEmpty(tradeResInfos)){
                CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);
            }
            IData tradeRes=tradeResInfos.getData(0);
            if (StringUtils.isNotBlank(tradeRes.getString("RSRV_STR5"))){
                resCode = tradeRes.getString("RSRV_STR5");
            }else{
            	resCode = tradeRes.getString("RES_CODE");
            	useEmptyCard=false;
            }   
        }
    	IDataset resInfos = null;
    	//先查SIM卡空闲表，如没有再查SIM在用表
    	if (useEmptyCard){
            resInfos = ResCall.getEmptycardInfo(resCode, "", "");
    	}else{
            resInfos = ResCall.getSimCardInfo("0", resCode, "", "");
        }    
    	if(IDataUtil.isEmpty(resInfos)){
    		CSAppException.apperr(ResException.CRM_RES_12);
    	}
    	IData resInfo = resInfos.getData(0);
    	
    	IData printData=new DataMap();
    	printData.put("FEE_TYPE", "1".equals(resInfo.getString("CARD_MODE_TYPE")) ? "SIM卡" : "USIM卡");
    	printData.put("BRAND_MODEL", resInfo.getString("RES_KIND_NAME"));
    	printData.put("UNIT", "张");
    	printData.put("QUANTITY", "1");
    	return printData;
    }

    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }
    

    public void mergeFeeList(IData printData, boolean containOper, boolean containForegift, boolean containPre) throws Exception 
    {
    	IDataset mergeFeeList = new DatasetList();
    	if (containOper && null != printData.getDataset("OPERFEE_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("OPERFEE_LIST"));
    	}
    	if (containForegift && null != printData.getDataset("FOREGIFT_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("FOREGIFT_LIST"));
    	}
    	if (containPre && null != printData.getDataset("PREFEE_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("PREFEE_LIST"));
    	}
        printData.put("MERGE_FEE_LIST", mergeFeeList);
        printData.put("MERGE_FEE_LIST_OPER_FLAG", containOper ? "1" : "0"); 
        printData.put("MERGE_FEE_LIST_FOREGIFT_FLAG", containForegift ? "1" : "0"); 
        printData.put("MERGE_FEE_LIST_ADVANCE_FLAG", containPre ? "1" : "0"); 
    }

}
