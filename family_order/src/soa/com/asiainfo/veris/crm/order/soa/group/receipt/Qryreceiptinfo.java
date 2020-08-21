package com.asiainfo.veris.crm.order.soa.group.receipt;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr.PrintMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CNoteTempletInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.FeeNotePrintMgr;

public class Qryreceiptinfo extends CSBizBean{

	private static final long serialVersionUID = 1L;
	
	private static String tradeAttr = "";
	
	private static String prtTradeffTag = "";
	
	private static String totalFee = "0";
	
	private static String tradeType = "";
	
	private static boolean temple003 = false;
	
	private static IDataset templetItemG001 = new DatasetList();
	private static IDataset templetItemG002 = new DatasetList();
	private static IDataset templetItemG003 = new DatasetList();
	
	private static IDataset printList = new DatasetList();
	
	public static IDataset qryreceiptinfo(String tradeId) throws Exception
    {
		//先通过tradeId查询出tf_bh_trade表的数据
		IDataset tradeinfo = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(tradeId);
		if(IDataUtil.isEmpty(tradeinfo)){
			tradeinfo = TradeInfoQry.queryTradeSet(tradeId, null);
		}
		if(IDataUtil.isNotEmpty(tradeinfo)){
			IData data = tradeinfo.first();
			//设置打印模板项配置信息
			
			setTempletItem(data,templetItemG001,templetItemG002,templetItemG003);
			// 如果未获取到打印的模板项配置信息则返回
	        if (IDataUtil.isEmpty(templetItemG001) && IDataUtil.isEmpty(templetItemG002) && IDataUtil.isEmpty(templetItemG003))
	        {
	            return printList;
	        }
	        //先尝试获取下免填单信息
	        IDataset cnoteinfo = TradeReceiptInfoQry.getPrintNoteInfoByTradeId(tradeId);
	        //如果没有查询到受理单信息, 且未获取到发票和收据模板项配置信息, 则返回
	        if (IDataUtil.isEmpty(cnoteinfo) && IDataUtil.isEmpty(templetItemG001) && IDataUtil.isEmpty(templetItemG002))
	        {
	            return printList;
	        }
	        if(IDataUtil.isNotEmpty(cnoteinfo)) 	        
	        	temple003 = true;

	        IData cnote = cnoteinfo.first();
	        data.put("RECEIPT_INFO1", cnote.getString("RECEIPT_INFO1", ""));
	        data.put("RECEIPT_INFO2", cnote.getString("RECEIPT_INFO2", ""));
	        data.put("RECEIPT_INFO3", cnote.getString("RECEIPT_INFO3", ""));
	        data.put("RECEIPT_INFO4", cnote.getString("RECEIPT_INFO4", ""));
	        data.put("RECEIPT_INFO5", cnote.getString("RECEIPT_INFO5", ""));
	        data.put("NOTE_TYPE", cnote.getString("NOTE_TYPE", ""));
	        //准备打印公用数据
	        setTradeReceiptData(data);
	        //绑定打印模板和打印数据, 生成票据信息
	        setReceiptPrintData(data,templetItemG001,templetItemG002,templetItemG003);
		}		
		return printList;
    }
	
	public static void setTempletItem(IData data,IDataset templetItemG001,IDataset templetItemG002,IDataset templetItemG003) throws Exception
	{
		String trade_type_code = data.getString("TRADE_TYPE_CODE");
		String trade_eparchy_code = data.getString("TRADE_EPARCHY_CODE");
		String eparchy_code = data.getString("EPARCHY_CODE");
		//根据业务类型编码查询业务类型信息
		IData tradeTypeList = UTradeTypeInfoQry.getTradeType(trade_type_code, eparchy_code);
        if (IDataUtil.isEmpty(tradeTypeList))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_17, trade_type_code);
        }
        
        tradeAttr = tradeTypeList.getString("TRADE_ATTR");
        prtTradeffTag = tradeTypeList.getString("PRT_TRADEFF_TAG");
        tradeType = tradeTypeList.getString("TRADE_TYPE");
        
        if ("1".equals(tradeTypeList.getString("PRT_TRADEFF_TAG", "0")))
        {
            IData param = new DataMap();
            param.put("TRADE_TYPE_CODE", trade_type_code);
            param.put("TEMPLET_TYPE", "G0003"); // 免填单
            param.put("RELATION_KIND", "0"); // 按地州查询标志
            param.put("RELATION_ATTR", trade_eparchy_code);

            // 设置免填单模板项配置信息
            IDataset templetG003 = qryReceiptTempletItem(param);
            templetItemG003.addAll(templetG003);
        }
        
        int oper_fee = data.getInt("OPER_FEE",0);
        int foregift = data.getInt("FOREGIFT",0);
        int advance_pay = data.getInt("ADVANCE_PAY",0);
        //发票项和收据项模板配置信息(有费用)
        if ((oper_fee!=0) || (foregift!=0) || (advance_pay!=0))
        {
        	int totalfee = oper_fee + foregift + advance_pay;
        	totalFee = String.valueOf(totalfee);
            IData param = new DataMap();
            param.put("TRADE_TYPE_CODE", trade_type_code);
            param.put("TEMPLET_TYPE", "G0001"); // 发票
            param.put("RELATION_KIND", "0"); // 按地州查询标志
            param.put("RELATION_ATTR", trade_eparchy_code);

            String isNeedPrintFP = StaticUtil.getStaticValue("IS_NEED_PRINT_G001", trade_type_code);// 是否打印发票，data_name配置y为打印，否则不打印
            if (StringUtils.isNotBlank(isNeedPrintFP) && StringUtils.equalsIgnoreCase("Y", isNeedPrintFP))
            {
                // 设置发票模板项配置信息
            	IDataset templetG001=qryReceiptTempletItem(param);
            	templetItemG001.addAll(templetG001);
            }

            param.put("TEMPLET_TYPE", "G0002"); // 收据
            String isNeedPrintSJ = StaticUtil.getStaticValue("IS_NEED_PRINT_G002", trade_type_code);// 是否打印收据，data_name配置y为打印，否则不打印

            if (StringUtils.isNotBlank(isNeedPrintSJ) && StringUtils.equalsIgnoreCase("Y", isNeedPrintSJ))
            {
                // 设置收据模板项配置信息
            	IDataset templetG002=qryReceiptTempletItem(param);
            	templetItemG002.addAll(templetG002);
            }
        }
	}
	
    /**
     * 查询打印模板信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryReceiptTempletItem(IData inParam) throws Exception
    {
        String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE", "");
        String templetType = inParam.getString("TEMPLET_TYPE", "");
        String relationKind = inParam.getString("RELATION_KIND", "");
        String relationAttr = inParam.getString("RELATION_ATTR", "");

        // 获取打印模板
        IDataset templetList = CNoteTempletInfoQry.getReceiptTemplets(tradeTypeCode, templetType, relationKind, relationAttr);

        if (IDataUtil.isEmpty(templetList))
        {
            return null;
        }

        // 获取打印模板项
        String templetCode = templetList.getData(0).getString("TEMPLET_CODE", "");

        IDataset templetItemList = CNoteTempletInfoQry.getReceiptTempletItems(templetCode);

        return templetItemList;
    }
    
    //准备打印公用数据
    public static void setTradeReceiptData(IData data) throws Exception
    {
    	String acct_id = data.getString("ACCT_ID");
    	IData acctData = UcaInfoQry.qryAcctInfoByAcctId(acct_id);
    	String payName = IDataUtil.isNotEmpty(acctData) ? acctData.getString("PAY_NAME") :data.getString("CUST_NAME","");
    	data.put("PAY_NAME", payName);
    	data.put("OPERATION_TIME", data.getString("ACCEPT_DATE"));
    	data.put("TRADE_ATTR", tradeAttr);
    	data.put("PRT_TRADEFF_TAG", prtTradeffTag);
    	data.put("TOTAL_MONEY", totalFee);
    	data.put("TOTAL_OPER_FEE", data.getString("OPER_FEE","0"));
    	data.put("TOTAL_FOREGIFT", data.getString("FOREGIFT","0"));
    	data.put("TOTAL_ADVANCE_PAY", data.getString("ADVANCE_PAY","0"));
    	data.put("CUR_CYCLE", SysDateMgr.getNowCyc()); // 话费账期
    	data.put("OPERATION_DATE", SysDateMgr.getSysDate());// 缴费日期
    	data.put("DEPART_NAME", StaticUtil.getStaticValue(null, "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", data.getString("DEPART_ID")));// 部门名称
    	data.put("OPEN_NOTE_TYPE", data.getString("RSRV_STR10",""));// 开户时设置发票类型
    	data.put("STAFF_NAME", StaticUtil.getStaticValue(null, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", data.getString("STAFF_ID")));
        data.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND_CODE")));
        data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID")));
        data.put("TRADE_TYPE", tradeType);
        
        IData customer = UcaInfoQry.qryCustomerInfoByCustId(data.getString("CUST_ID"), BizRoute.getRouteId());
        data.put("PSPT_ID", (IDataUtil.isEmpty(customer)) ? "" : customer.getString("PSPT_ID"));        
        data.put("TRADE_NAME", "通信服务业");

        /*
         * 处理费用信息, 费用不为零处理
         */
        if (!"0".equals(totalFee))
        {
            IDataset feeSubList = getAllTradeFeeSubByOrderId(data.getString("ORDER_ID"), data.getString("TRADE_ID")); // 取所有的fee_sub表

            IData operFeeData = new DataMap();
            IData foregiftData = new DataMap();
            IData advancePayData = new DataMap();

            int operFeeCount = 0;
            int foregiftCount = 0;
            int advancePayCount = 0;

            for (int i = 0, size = feeSubList.size(); i < size; i++)
            {
                IData feeSubData = feeSubList.getData(i);

                String feeMode = feeSubData.getString("FEE_MODE");// 费用类型
                String feeTypeCodeDesc = FeeListMgr.getFeeTypeCodeDesc(feeMode, feeSubData.getString("FEE_TYPE_CODE"));// 费用描述
                String fee = feeSubData.getString("FEE", "0");// 费用金额

                if ("0".equals(fee)) // 费用项金额为零, 则不处理
                {
                    continue;
                }

                if ("0".equals(feeMode)) // 营业费用
                {
                    operFeeData.put("FEE_TYPE" + operFeeCount, feeTypeCodeDesc);
                    operFeeData.put("FEE" + operFeeCount, fee);
                    operFeeCount++;
                }
                else if ("1".equals(feeMode)) // 押金
                {
                    foregiftData.put("FEE_TYPE" + foregiftCount, feeTypeCodeDesc);
                    foregiftData.put("FEE" + foregiftCount, fee);
                    foregiftCount++;
                }
                else if ("2".equals(feeMode)) // 预存
                {
                    advancePayData.put("FEE_TYPE" + advancePayCount, feeTypeCodeDesc);
                    advancePayData.put("FEE" + advancePayCount, fee);
                    advancePayCount++;
                }
            }

            if (IDataUtil.isNotEmpty(operFeeData))
            {
                data.put("OPER_FEE_SUB", operFeeData);
            }

            if (IDataUtil.isNotEmpty(foregiftData))
            {
                data.put("FOREGIFT_SUB", foregiftData);
            }

            if (IDataUtil.isNotEmpty(advancePayData))
            {
                data.put("PRE_FEE_SUB", advancePayData);
            }
        }
    }
        
    /**
     * 获取所有费用信息
     * 
     * @param orderId
     * @param mainTradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeFeeSubByOrderId(String orderId, String mainTradeId) throws Exception
    {
        IDataset feeSubList = new DatasetList();

        IData orderData = UOrderInfoQry.qryOrderAllByOrderId(orderId);

        if (IDataUtil.isNotEmpty(orderData))
        {
            IDataset orderSubs = null;
            if ("1".equals(orderData.getString("ORDER_KIND_CODE")))
            {
                orderSubs = UOrderSubInfoQry.qryOrderSubByOrderId(orderId);
            }

            if (IDataUtil.isNotEmpty(orderSubs))
            {
                for (Object sub : orderSubs)
                {
                    IData tradeSub = (IData) sub;

                    String tradeId = tradeSub.getString("TRADE_ID");
                    String routeId = tradeSub.getString("ROUTE_ID");

                    IDataset feeSubTempList = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(tradeId, routeId);

                    feeSubList.addAll(feeSubTempList);
                }
            }
            else
            {
                IDataset feeSubTempList = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(mainTradeId, BizRoute.getRouteId());

                feeSubList.addAll(feeSubTempList);
            }
        }

        return feeSubList;
    }
    
    public static void setReceiptPrintData(IData data,IDataset dataset1, IDataset dataset2,IDataset dataset3) throws Exception{
    	// 1、加工原始数据(包括费用和免填单)
        String printType = "";

        int totalOperFee = data.getInt("TOTAL_OPER_FEE", 0);
        int totalForegift = data.getInt("TOTAL_FOREGIFT", 0);
        int totalAdvancePay = data.getInt("TOTAL_ADVANCE_PAY", 0);

        if (totalOperFee != 0)
            printType += "0";

        if (totalForegift != 0)
            printType += "1";

        if (totalAdvancePay != 0)
            printType += "2";

        if (temple003)
            printType += "3";

        data.put("PRINT_TYPE", printType);

        // 调用PrintMgr构造打印数据
        IData commonPrintData = new DataMap();
        PrintMgr.createPrintData(data, commonPrintData);
        data.putAll(commonPrintData);

        // 2、根据免填单模板解析成打印串
        if (temple003)
        {
            setNeedPrintReceipt(data, dataset3, "G0003", "", false);
        }

        if (IDataUtil.isEmpty(dataset1) && IDataUtil.isEmpty(dataset2))// 发票和收据配置项为空则一定不打印发票和收据了，可以直接return掉
        {
            return;
        }

        if (totalOperFee + totalForegift + totalAdvancePay == 0)
        {
            return;
        }

        // 3、根据发票和收据合并配置, 合并打印内容, 解析成打印串
        setFeeReceiptData(data);
    }

    /**
     * 设置打印数据
     * 
     * @param tradeReceiptData
     *            打印数据
     * @param receiptTemletItem
     *            票据模板项信息
     * @param printType
     *            票据打印类型
     * @param printName
     *            票据打印名称
     * @param isBack
     *            是否备份票据数据
     * @throws Exception
     */
    private static void setNeedPrintReceipt(IData tradeReceiptData, IDataset receiptTemletItem, String printType, String printName, boolean isBack) throws Exception
    {
        if (IDataUtil.isEmpty(receiptTemletItem))
        {
            return;
        }

        String printId = "";
        IDataset rePrintTrades = Dao.qryByCode("TF_B_NOTEPRINTLOG", "SEL_BY_TRADEID_FLAG_2", tradeReceiptData, Route.getJourDb());
        if(IDataUtil.isNotEmpty(rePrintTrades))
        	printId = rePrintTrades.getData(0).getString("PRINT_ID","");
        // 备份票据打印信息(目前收据和发票需要备份)
        

        // 在此根据printType(G0001,G0002,G0003)获得发票、收据、免填单组合内容模板(主要是配置中间空白块)，
        // 组合成新的数据以符合格式模板特殊要求。
        combineReceiptContent(tradeReceiptData, printType);

        IData printInfoData = parsePrintData(tradeReceiptData, receiptTemletItem);

        if (IDataUtil.isNotEmpty(printInfoData))
        {
            IData printData = new DataMap();
            
            printData.put("PRINT_ID", printId);
            printData.put("TRADE_ID", tradeReceiptData.getString("TRADE_ID"));
            printData.put("EPARCHY_CODE", BizRoute.getRouteId());
            printData.put("TYPE", printType);
            printData.put(Route.USER_EPARCHY_CODE, tradeReceiptData.getString("EPARCHY_CODE"));
            printData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
            String name ="";
            if("G0001".equals(printType)){
            	name = "发票";
            }else if ("G0002".equals(printType)){
            	name = "收据";
            }else if ("G0003".equals(printType)){
            	name = "业务受理单";
            }
            printData.put("NAME", "".equals(printName) ? name : printName);
            printData.put("PRINT_DATA", printInfoData);
            
            if (!printType.equals("G0003"))
            {
            	
                printData.put("TOTAL_FEE", printInfoData.getDouble("ALL_MONEY_LOWER", 0.00) * 100);
                printData.put("SERIAL_NUMBER", printInfoData.getString("SERIAL_NUMBER"));
                String feeCode = printInfoData.getString("FEE_MODE", "");
                if(StringUtils.isEmpty(feeCode)){
                	feeCode = tradeReceiptData.getString("FEE_MODE","0");
                }
                printData.put("FEE_MODE", feeCode);
                printData.put("TRADE_TYPE_CODE", tradeReceiptData.getString("TRADE_TYPE_CODE"));
                printData.put("HAS_TICKET", "1");
            }
            
            printList.add(printData);
        }
    }
    
    /**
     * 设置费用打印信息
     * 
     * @param tradeReceiptData
     * @throws Exception
     */
    public static void setFeeReceiptData(IData tradeReceiptData) throws Exception
    {
        int totalOperFee = tradeReceiptData.getInt("TOTAL_OPER_FEE", 0);
        int totalForegift = tradeReceiptData.getInt("TOTAL_FOREGIFT", 0);
        int totalAdvancePay = tradeReceiptData.getInt("TOTAL_ADVANCE_PAY", 0);
        IData noteCfg = getNotePrintCfg(tradeReceiptData);

        int mergeType = noteCfg.getInt("PARA_CODE2", 0); // 合并类型
        String mergePaperType = noteCfg.getString("PARA_CODE3", "G0001"); // 合并打印默认票据类型
        String operFeePaperType = noteCfg.getString("PARA_CODE4", "G0001"); // 营业费打印默认票据类型
        String foregiftPaperType = noteCfg.getString("PARA_CODE5", "G0002"); // 押金打印默认票据类型
        String preFeePaperType = noteCfg.getString("PARA_CODE6", "G0002"); // 预存打印默认票据类型
        String mergePaperName = noteCfg.getString("PARA_CODE7", "发票"); // 合并打印默认票据名称

        String operFeePaperName = noteCfg.getString("PARA_CODE8", "发票"); // 营业费打印默认票据名称
        if (StringUtils.isNotEmpty(tradeReceiptData.getString("DIY_OPERFEENAME")))
        {
            operFeePaperName = tradeReceiptData.getString("DIY_OPERFEENAME");
        }

        String foregiftPaperName = noteCfg.getString("PARA_CODE9", "收据"); // 押金打印默认票据名称
        if (StringUtils.isNotEmpty(tradeReceiptData.getString("DIY_FORENAME")))
        {
            foregiftPaperName = tradeReceiptData.getString("DIY_FORENAME");
        }

        String preFeePaperName = noteCfg.getString("PARA_CODE10", "收据"); // 预存打印默认票据名称
        if (StringUtils.isNotEmpty(tradeReceiptData.getString("DIY_ADVANCENAME")))
        {
            preFeePaperName = tradeReceiptData.getString("DIY_ADVANCENAME");
        }

        String content = "";
        String trade_id = tradeReceiptData.getString("TRADE_ID");
        String tradeTypeCode = tradeReceiptData.getString("TRADE_TYPE_CODE");

        switch (mergeType)
        {
            case 0: // 不合并
                if (totalOperFee != 0)
                {
                    //获取费用明细打印串 其实合并打印已经不支持了
                    tradeReceiptData.putAll(FeeNotePrintMgr.getPrintReceiptFee(trade_id, tradeTypeCode, "0", 1).getData("0"));
                    tradeReceiptData.put("FEE_MODE", "0");
                    
                    content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, true, false, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee, content, operFeePaperType, operFeePaperName);
                }
                if (totalForegift != 0)
                {
                    //获取费用明细打印串 其实合并打印已经不支持了
                    tradeReceiptData.putAll(FeeNotePrintMgr.getPrintReceiptFee(trade_id, tradeTypeCode, "1", 1).getData("1"));
                    tradeReceiptData.put("FEE_MODE", "1");
                    
                    content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                    mergeFeeList(tradeReceiptData, false, true, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalForegift, content, foregiftPaperType, foregiftPaperName);
                }
                if (totalAdvancePay > 0)
                {
                    //获取费用明细打印串 其实合并打印已经不支持了
                    tradeReceiptData.putAll(FeeNotePrintMgr.getPrintReceiptFee(trade_id, tradeTypeCode, "2", 1).getData("2"));
                    tradeReceiptData.put("FEE_MODE", "2");
                    
                    content = tradeReceiptData.getString("PREFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, false, false, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalAdvancePay, content, preFeePaperType, preFeePaperName);
                }
                break;
            case 1: // 合并营业费用和押金
                if (totalOperFee > 0 && totalForegift > 0)
                {
                    content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                    content += tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                    mergeFeeList(tradeReceiptData, true, true, false);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee + totalForegift, content, mergePaperType, mergePaperName);
                }
                else
                {
                    if (totalOperFee != 0)
                    {
                        content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, true, false, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee, content, operFeePaperType, operFeePaperName);
                    }

                    if (totalForegift != 0)
                    {
                        content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, true, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalForegift, content, foregiftPaperType, foregiftPaperName);
                    }
                }

                if (totalAdvancePay > 0)
                {
                    content = tradeReceiptData.getString("PREFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, false, false, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalAdvancePay, content, preFeePaperType, preFeePaperName);
                }
                break;
            case 2: // 合并营业费用和预存
                if (totalOperFee > 0 && totalAdvancePay > 0)
                {
                    content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                    content += tradeReceiptData.getString("PREFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, true, false, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee + totalAdvancePay, content, mergePaperType, mergePaperName);
                }
                else
                {
                    if (totalOperFee != 0)
                    {
                        content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, true, false, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee, content, operFeePaperType, operFeePaperName);
                    }

                    if (totalAdvancePay > 0)
                    {
                        content = tradeReceiptData.getString("PREFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, false, true);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalAdvancePay, content, preFeePaperType, preFeePaperName);
                    }
                }

                if (totalForegift != 0)
                {
                    content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                    mergeFeeList(tradeReceiptData, false, true, false);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalForegift, content, foregiftPaperType, foregiftPaperName);
                }
                break;
            case 3: // 合并押金和预存
                if (totalForegift > 0 && totalAdvancePay > 0)
                {
                    content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                    content += tradeReceiptData.getString("PREFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, false, true, false);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalForegift + totalAdvancePay, content, mergePaperType, mergePaperName);
                }
                else
                {
                    if (totalForegift != 0)
                    {
                        content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, true, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalForegift, content, foregiftPaperType, foregiftPaperName);
                    }

                    if (totalAdvancePay > 0)
                    {
                        content = tradeReceiptData.getString("PREFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, false, true);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalAdvancePay, content, preFeePaperType, preFeePaperName);
                    }
                }

                if (totalOperFee != 0)
                {
                    content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, true, false, false);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee, content, operFeePaperType, operFeePaperName);
                }

                break;
            case 4: // 三都全部合并
                if (totalForegift > 0 && totalAdvancePay > 0 && totalOperFee > 0)
                {
                    content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                    content += tradeReceiptData.getString("PREFEE_CONTENT", "");
                    content += tradeReceiptData.getString("OPERFEE_CONTENT", "");
                    mergeFeeList(tradeReceiptData, true, true, true);
                    setFeeNeedPrintReceipt(tradeReceiptData, totalForegift + totalAdvancePay + totalOperFee, content, mergePaperType, mergePaperName);
                }
                else
                {
                    if (totalOperFee != 0)
                    {
                        content = tradeReceiptData.getString("OPERFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, true, false, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalOperFee, content, operFeePaperType, operFeePaperName);
                    }
                    if (totalForegift != 0)
                    {
                        content = tradeReceiptData.getString("FOREGIFT_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, true, false);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalForegift, content, foregiftPaperType, foregiftPaperName);
                    }
                    if (totalAdvancePay > 0)
                    {
                        content = tradeReceiptData.getString("PREFEE_CONTENT", "");
                        mergeFeeList(tradeReceiptData, false, false, true);
                        setFeeNeedPrintReceipt(tradeReceiptData, totalAdvancePay, content, preFeePaperType, preFeePaperName);
                    }
                }
                break;
            default:
                CSAppException.apperr(PrintException.CRM_PRINT_10);
        }

    }
    
    /**
     * 设置费用打印数据
     * 
     * @param tradeReceiptData
     * @param totalMoney
     * @param content
     * @param operFeePaperType
     * @param operFeePaperName
     * @throws Exception
     */
    public static void setFeeNeedPrintReceipt(IData tradeReceiptData, double totalMoney, String content, String operFeePaperType, String operFeePaperName) throws Exception
    {
        tradeReceiptData.put("ALL_MONEY_LOWER", String.format("%1$3.2f", totalMoney / 100.0));
        tradeReceiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalMoney / 100.0));
        tradeReceiptData.put("FEE_CONTENT", content);
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

            setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), "G0001", operFeePaperName1, true);
            setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), "G0002", operFeePaperName2, true);
        }
        setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), operFeePaperType, operFeePaperName, true);
    }
    
    /**
     * 获取费用打印配置信息(费用打印纸张，合并配置)
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IData getNotePrintCfg(IData inParam) throws Exception
    {
        IData param = new DataMap();
        if ("0".equals(inParam.getString("OPEN_NOTE_TYPE", "")) && "10".equals(inParam.getString("TRADE_TYPE_CODE")))
        {
            inParam.put("TRADE_TYPE_CODE", "-1");
        }

        param.put("SUBSYS_CODE", "BMS");
        param.put("PARAM_ATTR", "9901");
        param.put("PARAM_CODE", "CNOTE");
        param.put("PARA_CODE1", inParam.getString("TRADE_TYPE_CODE"));
        param.put("EPARCHY_CODE", inParam.getString("TRADE_EPARCHY_CODE"));

        IDataset paramList = ParamInfoQry.getPrintCfg(param);

        if (IDataUtil.isEmpty(paramList))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_3);
        }

        return paramList.getData(0);
    }
    
    public static void mergeFeeList(IData printData, boolean containOper, boolean containForegift, boolean containPre) throws Exception 
    {
    	IDataset mergeFeeList = new DatasetList();
    	if (containOper&& null != printData.getDataset("OPERFEE_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("OPERFEE_LIST"));
    	}
    	if (containForegift&& null != printData.getDataset("FOREGIFT_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("FOREGIFT_LIST"));
    	}
    	if (containPre&& null != printData.getDataset("PREFEE_LIST")) {
    		mergeFeeList.addAll(printData.getDataset("PREFEE_LIST"));
    	}
        printData.put("MERGE_FEE_LIST", mergeFeeList);
        printData.put("MERGE_FEE_LIST_OPER_FLAG", containOper ? "1" : "0"); 
        printData.put("MERGE_FEE_LIST_FOREGIFT_FLAG", containForegift ? "1" : "0"); 
        printData.put("MERGE_FEE_LIST_ADVANCE_FLAG", containPre ? "1" : "0"); 
    }
    
    /**
     * 根据打印格式模板类型编码（纸张编码）获得打印各项格式
     * 
     * @throws Exception
     */
    public static IDataset getTempletItemByType(String type) throws Exception
    {
        if (type.equals("G0001"))
        {
            return templetItemG001;
        }
        else if (type.equals("G0002"))
        {
            return templetItemG002;
        }
        else if (type.equals("G0003"))
        {
            return templetItemG003;
        }
        else
        {
            CSAppException.apperr(PrintException.CRM_PRINT_8);
        }
        return null;
    }
    
    /**
     * 根据内容配置, 组合打印数据
     * 
     * @param tradeReceiptData
     * @param operFeePaperType
     * @throws Exception
     */
    public static void combineReceiptContent(IData tradeReceiptData, String printType) throws Exception
    {
        IData originalPrintContent = getReceiptCombineTemlet(tradeReceiptData, printType);

        if (IDataUtil.isNotEmpty(originalPrintContent))
        {
            String receiptInfo = "";
            String subStr = "";
            int index1 = 0, index2 = 0;
            for (int i = 1; i <= 5; i++)
            {
                receiptInfo += originalPrintContent.getString("TEMPLATE_CONTENT" + i, "").trim();
            }
            while (true)
            {
                index1 = receiptInfo.indexOf('[', index2);
                if (index1 == -1)
                    break;
                index1++;
                index2 = receiptInfo.indexOf(']', index1);
                if (index2 == -1)
                    break;

                // subStr = receiptInfo.substring(index1-1,index2+1);//包含[]
                subStr = receiptInfo.substring(index1, index2);// 不包含[]
                String[] subStrArry = subStr.split("=");
                if (subStrArry == null || subStrArry.length != 2)
                    CSAppException.apperr(PrintException.CRM_PRINT_8);

                int index3 = 0, index4 = 0;
                String subStr2 = "";
                String key2 = "";
                String receiptInfo2 = subStrArry[1];
                String midReceiptInfo2 = subStrArry[1];
                String value2 = "";
                while (true)
                {
                    index3 = receiptInfo2.indexOf('#', index4);
                    if (index3 == -1)
                        break;
                    index3++;
                    index4 = receiptInfo2.indexOf('#', index3);
                    if (index4 == -1)
                        break;

                    subStr2 = receiptInfo2.substring(index3 - 1, index4 + 1);// 包含##
                    key2 = receiptInfo2.substring(index3, index4);// 不包含##

                    value2 = tradeReceiptData.getString(key2, "");
                    midReceiptInfo2 = midReceiptInfo2.replace(subStr2, value2);
                    index4++;
                }

                tradeReceiptData.put(subStrArry[0], midReceiptInfo2);
                index2++;
            }
        }
    }
    
    public static IData getReceiptCombineTemlet(IData receiptPrintData, String printType) throws Exception
    {
        String type = "0";

        if (printType.equals("G0001")) // 发票
        {
            type = "2";
        }
        else if (printType.equals("G0002"))// 收据
        {
            type = "3";
        }
        else if (printType.equals("G0003"))// 免填单
        {
            type = "4";
        }

        // 获得原始打印内容
        String tradeTypeCode = receiptPrintData.getString("TRADE_TYPE_CODE");
        String productId = receiptPrintData.getString("PRODUCT_ID", "-1");
        String brandCode = receiptPrintData.getString("BRAND_CODE", "ZZZZ");

        // 获得原始打印内容
        IDataset templetList = TemplateQry.qryByReceipt(tradeTypeCode, brandCode, productId, type, tradeTypeCode);

        if (IDataUtil.isEmpty(templetList))
        {
            return null;
        }

        return templetList.getData(0);
    }
    
    /**
     * 解析打印模板, 绑定打印参数
     * 
     * @param tradeReceiptData
     * @param receiptTempletItem
     * @return
     * @throws Exception
     */
    public static IData parsePrintData(IData tradeReceiptData, IDataset receiptTempletItem) throws Exception
    {
        IData printData = new DataMap();

        if (IDataUtil.isEmpty(receiptTempletItem))
        {
            return printData;
        }

        for (int i = 0; i < receiptTempletItem.size(); i++)
        {
            String itemContent = receiptTempletItem.getData(i).getString("ITEM_CONTENT");
            printData.put(itemContent, tradeReceiptData.getString(itemContent, "").replaceAll("\\~\\~", "<br/>").replaceAll("\\~", "<br/>"));
        }
        
        IData receiptData = receiptTempletItem.getData(0);

        // 设置打印位置
        printData.put("TEMP_PATH", receiptData.getString("TEMPLET_PATH") + "?template_code=" + receiptData.getString("TEMPLET_CODE") + "&version=" + receiptData.getString("PRINT_VERSION"));
        printData.put("TEMP_TYPE", "0".equals(receiptData.getString("PRINT_TYPE")) ? "WORD" : "HTML");        
        IData bakDt = new DataMap();
        bakDt.putAll(tradeReceiptData);
        bakDt.remove("USER_ID");  //后面js调服务，参数里面也存在了userId，防止重复而取不到，这里干掉
        printData.put("PRINT_DATA_SOURCE_BAK", new DataMap(bakDt)); //添加此代码将打印源数据存储，以备打印调接口时有数据可取。(此代码放置于此,是因为所有打印都会调用此方法)(同时用new是防止receiptData被引用篡改)

        return printData;
    }
    

	public static void main (String[] args) throws Exception{
		String trade_id = "1117051900275772";
		IDataset printinfos = qryreceiptinfo(trade_id);
		
	}
}
