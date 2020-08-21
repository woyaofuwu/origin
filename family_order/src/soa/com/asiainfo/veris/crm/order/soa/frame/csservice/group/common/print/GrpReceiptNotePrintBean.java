
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.print;

import java.util.List;

import org.apache.log4j.Logger;

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
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr.PrintMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CNoteTempletInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.FeeNotePrintMgr;

public class GrpReceiptNotePrintBean extends CSBizBean
{
    private static Logger log = Logger.getLogger(GrpReceiptNotePrintBean.class);

    private final String RECEIPT_G0001 = "G0001"; // 集团业务发票

    private final String RECEIPT_G0002 = "G0002"; // 集团业务收据

    private final String RECEIPT_G0003 = "G0003"; // 集团业务受理单
    
    private final String TRADE_NAME = "通信服务业";

    private final String PRINTNAME_G0001 = "发票";

    private final String PRINTNAME_G0002 = "收据";

    private final String PRINTNAME_G0003 = "业务受理单";

    private final String PRINT_HTML = "HTML"; // 押金

    private final String PRINT_WORD = "WORD"; // 预存

    private IDataset templetItemG001 = null; // 集团业务发票配置项

    private IDataset templetItemG002 = null; // 集团业务收据配置项

    private IDataset templetItemG003 = null; // 集团业务受理单配置项

    private boolean printG0003 = false; // 是否打印受理单信息,默认不打印

    private IData tradeTypeData = null; // 业务类型

    private RegTradeData regTradeData = null; // 主台账数据

    private String operFee = null; // 营业费用

    private String foregift = null; // 押金

    private String advancePay = null; // 预存

    private String totalFee = null; // 总费用

    private IData printNameData = null;

    private IDataset printList = null;

    public GrpReceiptNotePrintBean() throws Exception
    {
        operFee = "0";
        foregift = "0";
        advancePay = "0";
        totalFee = "0";

        printNameData = new DataMap();
        printNameData.put(RECEIPT_G0001, PRINTNAME_G0001);
        printNameData.put(RECEIPT_G0002, PRINTNAME_G0002);
        printNameData.put(RECEIPT_G0003, PRINTNAME_G0003);

        printList = new DatasetList();
    }

    /**
     * 备份收据和发票打印信息
     * 
     * @param data
     * @throws Exception
     */
    private String bakPrintReceipt(IData data) throws Exception
    {
        String printId = SeqMgr.getPrintId();

        String templet = data.getString("TEMPLET_TYPE", "");
        if (StringUtils.equals(templet, RECEIPT_G0001))
        {
            templet = "0";
        }
        else if (StringUtils.equals(templet, RECEIPT_G0002))
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
        printSaveData.put("START_CYCLE_ID", data.getString("START_CYCLE_ID", "-1"));
        printSaveData.put("END_CYCLE_ID", data.getString("END_CYCLE_ID", "-1"));
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
        printSaveData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
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

        Dao.executeUpdateByCodeCode("TF_B_NOTEPRINTLOG", "INSERT_NOTEPRINT_LOG", printSaveData, Route.getJourDb(data.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())));

        return printId;
    }

    /**
     * 根据内容配置, 组合打印数据
     * 
     * @param tradeReceiptData
     * @param operFeePaperType
     * @throws Exception
     */
    private void combineReceiptContent(IData tradeReceiptData, String printType) throws Exception
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

    public String getAdvancePay()
    {
        return advancePay;
    }

    /**
     * 获取所有费用信息
     * 
     * @param orderId
     * @param mainTradeId
     * @return
     * @throws Exception
     */
    private IDataset getAllTradeFeeSubByOrderId(String orderId, String mainTradeId) throws Exception
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

    public String getForegift()
    {
        return foregift;
    }

    /**
     * 获取费用打印配置信息(费用打印纸张，合并配置)
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    private IData getNotePrintCfg(IData inParam) throws Exception
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
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset paramList = ParamInfoQry.getPrintCfg(param);

        if (IDataUtil.isEmpty(paramList))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_3);
        }

        return paramList.getData(0);
    }

    public String getOperFee()
    {
        return operFee;
    }

    private IData getReceiptCombineTemlet(IData receiptPrintData, String printType) throws Exception
    {
        String type = "0";

        if (printType.equals(RECEIPT_G0001)) // 发票
        {
            type = "2";
        }
        else if (printType.equals(RECEIPT_G0002))// 收据
        {
            type = "3";
        }
        else if (printType.equals(RECEIPT_G0003))// 免填单
        {
            type = "4";
        }

        // 获得原始打印内容
        String tradeTypeCode = receiptPrintData.getString("TRADE_TYPE_CODE");
        String productId = receiptPrintData.getString("PRODUCT_ID", "-1");
        String brandCode = receiptPrintData.getString("BRAND_CODE", "ZZZZ");

        // 获得原始打印内容
        IDataset templetList = TemplateQry.qryByReceipt(tradeTypeCode, brandCode, productId, type, CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isEmpty(templetList))
        {
            return null;
        }

        return templetList.getData(0);
    }

    public RegTradeData getRegTradeData()
    {
        return regTradeData;
    }

    /**
     * 根据打印格式模板类型编码（纸张编码）获得打印各项格式
     * 
     * @throws Exception
     */
    private IDataset getTempletItemByType(String type) throws Exception
    {
        if (type.equals(RECEIPT_G0001))
        {
            return getTempletItemG001();
        }
        else if (type.equals(RECEIPT_G0002))
        {
            return getTempletItemG002();
        }
        else if (type.equals(RECEIPT_G0003))
        {
            return getTempletItemG003();
        }
        else
        {
            CSAppException.apperr(PrintException.CRM_PRINT_8);
        }
        return null;
    }

    public IDataset getTempletItemG001()
    {
        return templetItemG001;
    }

    public IDataset getTempletItemG002()
    {
        return templetItemG002;
    }

    public IDataset getTempletItemG003()
    {
        return templetItemG003;
    }

    public String getTotalFee()
    {
        return totalFee;
    }

    public IData getTradeTypeData()
    {
        return tradeTypeData;
    }

    public boolean isPrintG0003()
    {
        return printG0003;
    }

    /**
     * 解析打印模板, 绑定打印参数
     * 
     * @param tradeReceiptData
     * @param receiptTempletItem
     * @return
     * @throws Exception
     */
    private IData parsePrintData(IData tradeReceiptData, IDataset receiptTempletItem) throws Exception
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
        printData.put("TEMP_TYPE", "0".equals(receiptData.getString("PRINT_TYPE")) ? PRINT_WORD : PRINT_HTML);        
        IData bakDt = new DataMap();
        bakDt.putAll(tradeReceiptData);
        bakDt.remove("USER_ID");  //后面js调服务，参数里面也存在了userId，防止重复而取不到，这里干掉
        printData.put("PRINT_DATA_SOURCE_BAK", new DataMap(bakDt)); //添加此代码将打印源数据存储，以备打印调接口时有数据可取。(此代码放置于此,是因为所有打印都会调用此方法)(同时用new是防止receiptData被引用篡改)

        return printData;
    }

    /**
     * 集团打印(发票、收据、免填单)
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset printReceipt(IData inParam) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("进入集团打印方法: " + this.getClass().getName() + ".printReceipt");
            log.debug("入参inParam == " + inParam);
        }

        // 1、设置打印数据基本信息
        setReceiptBase(inParam);

        // 2、设置打印模板项配置信息
        setReceiptTempletItem(inParam);

        // 如果未获取到打印的模板项配置信息则返回
        if (IDataUtil.isEmpty(getTempletItemG001()) && IDataUtil.isEmpty(getTempletItemG002()) && IDataUtil.isEmpty(getTempletItemG003()))
        {
            return null;
        }

        // 3、获取打印数据
        // 3.1、如果需要打印免填单，则获取免填单打印数据
        IData tradeReceiptData = new DataMap();

        if (IDataUtil.isNotEmpty(getTempletItemG003()))
        {
            tradeReceiptData = qryTradeReceiptData();

            if (IDataUtil.isNotEmpty(tradeReceiptData))
            {
                setPrintG0003(true);
            }
        }

        // 3.2、如果不需要打印受理单信息, 且未获取到发票和收据模板项配置信息, 则返回
        if (!isPrintG0003() && IDataUtil.isEmpty(getTempletItemG001()) && IDataUtil.isEmpty(getTempletItemG002()))
        {
            return null;
        }

        // 3.3、设置打印数据
        setTradeReceiptData(tradeReceiptData);

        // 4、绑定打印模板和打印数据, 生成票据信息
        setReceiptPrintData(tradeReceiptData);

        if (log.isDebugEnabled())
        {
            log.debug("结束集团打印方法: " + this.getClass().getName() + ".printReceipt");
            log.debug("获取打印数据[printList]: " + printList);
        }

        return printList;
    }

    /**
     * 查询打印模板信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    private IDataset qryReceiptTempletItem(IData inParam) throws Exception
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

    /**
     * 获取受理单台账信息
     * 
     * @return
     * @throws Exception
     */
    private IData qryTradeReceiptData() throws Exception
    {
        IData retData = new DataMap();

        // 获取主台账信息
        RegTradeData regTradeData = getRegTradeData();
        MainTradeData mainTradeData = regTradeData.getMainTradeData();

        String tradeTypeCode = mainTradeData.getTradeTypeCode();
        String brandCode = mainTradeData.getBrandCode();
        String productId = mainTradeData.getProductId();
        String eparchyCode = mainTradeData.getEparchyCode();

        String tradeAttr = getTradeTypeData().getString("PRT_TRADEFF_TAG", "1");

        // 获取模板信息
        IDataset tradeReceiptList = TradeReceiptInfoQry.getReceiptInfoByPk(tradeTypeCode, brandCode, productId, tradeAttr, eparchyCode, null);

        if (IDataUtil.isEmpty(tradeReceiptList))
        {
            return retData;
        }

        // 获取模板TEMPLATE_ID
        String templateId = tradeReceiptList.getData(0).getString("TEMPLATE_ID");

        IData templatData = TemplateQry.qryTemplateContentByTempateId(templateId);

        if (IDataUtil.isEmpty(templatData))
        {
            return retData;
        }

        // 解析模板数据
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        exector.prepare(regTradeData);

        for (int i = 1; i <= 5; i++)
        {
            String contentStr = templatData.getString("TEMPLATE_CONTENT" + i, "");
            if (StringUtils.isNotEmpty(contentStr))
            {
                retData.put("RECEIPT_INFO" + i, exector.applyTemplate(contentStr));
            }
            else
            {
                retData.put("RECEIPT_INFO" + i, "");
            }
        }

        // 登记解析串日志记录
        String tradeId = regTradeData.getTradeId();
        String acceptDate = regTradeData.getAcceptTime();
        regCnoteInfo(tradeId, acceptDate, retData);

        return retData;
    }

    /**
     * 登记解析串
     * 
     * @param tradeData
     * @param receiptData
     * @throws Exception
     */
    private void regCnoteInfo(String tradeId, String acceptTime, IData receiptData) throws Exception
    {
        String noteType = "1";
        if (StringUtils.isBlank(acceptTime))
        {
            acceptTime = SysDateMgr.getSysTime();
        }
        String acceptMonth = acceptTime.substring(5, 7);

        IDataset tradeNoteInfoList = TradeReceiptInfoQry.getNoteInfoByPk(tradeId, acceptMonth, noteType);
        if (IDataUtil.isEmpty(tradeNoteInfoList))
        {
            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            param.put("ACCEPT_MONTH", acceptMonth);
            param.put("RECEIPT_INFO1", receiptData.getString("RECEIPT_INFO1", ""));
            param.put("RECEIPT_INFO2", receiptData.getString("RECEIPT_INFO2", ""));
            param.put("RECEIPT_INFO3", receiptData.getString("RECEIPT_INFO3", ""));
            param.put("RECEIPT_INFO4", receiptData.getString("RECEIPT_INFO4", ""));
            param.put("RECEIPT_INFO5", receiptData.getString("RECEIPT_INFO5", ""));
            param.put("NOTE_TYPE", noteType);
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());

            param.put("ACCEPT_DATE", acceptTime);
            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INS_RECEIPT_LOGINFO", param, Route.getJourDb());
        }
    }

    public void setAdvancePay(String advancePay)
    {
        this.advancePay = advancePay;
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
    private void setFeeNeedPrintReceipt(IData tradeReceiptData, double totalMoney, String content, String operFeePaperType, String operFeePaperName) throws Exception
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

            setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), RECEIPT_G0001, operFeePaperName1, true);
            setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), RECEIPT_G0002, operFeePaperName2, true);
        }
        setNeedPrintReceipt(tradeReceiptData, getTempletItemByType(operFeePaperType), operFeePaperType, operFeePaperName, true);
    }

    /**
     * 设置费用打印信息
     * 
     * @param tradeReceiptData
     * @throws Exception
     */
    private void setFeeReceiptData(IData tradeReceiptData) throws Exception
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
        String tradeTypeCode = tradeReceiptData.getString("TRADE_TYPE_CODE", getTradeTypeData().getString("TRADE_TYPE_CODE"));

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

    public void setForegift(String foreGift)
    {
        this.foregift = foreGift;
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
    private void setNeedPrintReceipt(IData tradeReceiptData, IDataset receiptTemletItem, String printType, String printName, boolean isBack) throws Exception
    {
        if (IDataUtil.isEmpty(receiptTemletItem))
        {
            return;
        }

        String printId = "";
        // 备份票据打印信息(目前收据和发票需要备份)
        if (isBack)
        {
            IData param = new DataMap();
            param.putAll(tradeReceiptData);

            param.put("TEMPLET_CODE", receiptTemletItem.getData(0).getString("TEMPLET_CODE"));
            param.put("TEMPLET_TYPE", receiptTemletItem.getData(0).getString("TEMPLET_TYPE"));

            // 备份票据信息
            printId = bakPrintReceipt(param);
        }else {
        	if ("G0001".equals(printType) || "G0002".equals(printType)) { //重打发票/收据，需要获取PRINT_ID(通过拆分组合来获取)
        		String assembFlag = "";
        		String containOperFlag = tradeReceiptData.getString("MERGE_FEE_LIST_OPER_FLAG");
        		assembFlag += "1".equals(containOperFlag) ? "1": "0".equals(containOperFlag) ? "0" : "X";
            	String containForegiftFlag = tradeReceiptData.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
            	assembFlag += "1".equals(containForegiftFlag) ? "1": "0".equals(containForegiftFlag) ? "0" : "X";
            	String containAdvanceFlag = tradeReceiptData.getString("MERGE_FEE_LIST_ADVANCE_FLAG");
            	assembFlag += "1".equals(containAdvanceFlag) ? "1": "0".equals(containAdvanceFlag) ? "0" : "X";
        		if (!"XXX".equals(assembFlag) && !"000".equals(assembFlag)) {
        			IDataset rePrintTrades = Dao.qryByCode("TF_B_NOTEPRINTLOG", "SEL_BY_TRADEID_FLAG_2", tradeReceiptData, Route.getJourDb());
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
            printData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
            printData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
            printData.put("NAME", "".equals(printName) ? printNameData.getString(printType) : printName);
            printData.put("PRINT_DATA", printInfoData);
            
            if (!printType.equals(RECEIPT_G0003))
            {
            	
                printData.put("TOTAL_FEE", printInfoData.getDouble("ALL_MONEY_LOWER", 0.00) * 100);
                printData.put("SERIAL_NUMBER", printInfoData.getString("SERIAL_NUMBER"));
                String feeCode = printInfoData.getString("FEE_MODE", "");
                if(StringUtils.isEmpty(feeCode)){
                	feeCode = tradeReceiptData.getString("FEE_MODE","0");
                }
                String tradeTypeCodde = getTradeTypeData().getString("TRADE_TYPE_CODE");
                printData.put("FEE_MODE", feeCode);
                printData.put("TRADE_TYPE_CODE", tradeTypeCodde);
                
                //集团保证金清退
                if(StringUtils.isNotBlank(tradeTypeCodde) && StringUtils.equals("8905", tradeTypeCodde))
                {
                	printData.put("HAS_TICKET", "0");
                } 
                else 
                {
                	printData.put("HAS_TICKET", "1");
                }
            }
            
            printList.add(printData);
        }
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setPrintG0003(boolean printG0003)
    {
        this.printG0003 = printG0003;
    }

    /**
     * 设置打印数据基本信息
     * 
     * @param inParam
     * @throws Exception
     */
    protected void setReceiptBase(IData inParam) throws Exception
    {
        String orderId = IDataUtil.chkParam(inParam, "ORDER_ID");

        // 获取台账信息
        RegOrderData regOrderData = new RegOrderData(orderId);

        RegTradeData regTradeData = regOrderData.getMainRegData();

        if (regTradeData == null || regTradeData.getMainTradeData() == null)
        {
            CSAppException.apperr(PrintException.CRM_PRINT_18, orderId);
        }

        // 设置主台账信息
        setRegTradeData(regTradeData);

        IData mainTradeData = regTradeData.getMainTradeData().toData();

        int operFee = mainTradeData.getInt("OPER_FEE", 0); // 营业费用
        int foregift = mainTradeData.getInt("FOREGIFT", 0); // 押金
        int advancePay = mainTradeData.getInt("ADVANCE_PAY", 0); // 预存
        int totalFee = 0; // 总费用

        // 其他台账数据
        List<RegTradeData> otherRegDataList = regOrderData.getOtherRegData();

        if (otherRegDataList != null && otherRegDataList.size() > 0)
        {
            for (RegTradeData otherRegTradeData : otherRegDataList)
            {
                IData otherMainTradeData = otherRegTradeData.getMainTradeData().toData();
                operFee += otherMainTradeData.getInt("OPER_FEE", 0);
                foregift += otherMainTradeData.getInt("FOREGIFT", 0);
                advancePay += otherMainTradeData.getInt("ADVANCE_PAY", 0);
            }
        }

        totalFee = operFee + foregift + advancePay;

        setOperFee(String.valueOf(operFee));
        setForegift(String.valueOf(foregift));
        setAdvancePay(String.valueOf(advancePay));
        setTotalFee(String.valueOf(totalFee));

        // 设置业务类型信息
        setTradeTypeInfo(regTradeData.getTradeTypeCode(), CSBizBean.getUserEparchyCode());
    }

    /**
     * 绑定打印模板项和打印数据
     * 
     * @param tradeReceiptData
     * @return
     * @throws Exception
     */
    protected void setReceiptPrintData(IData tradeReceiptData) throws Exception
    {
        // 1、加工原始数据(包括费用和免填单)
        String printType = "";

        int totalOperFee = tradeReceiptData.getInt("TOTAL_OPER_FEE", 0);
        int totalForegift = tradeReceiptData.getInt("TOTAL_FOREGIFT", 0);
        int totalAdvancePay = tradeReceiptData.getInt("TOTAL_ADVANCE_PAY", 0);

        if (totalOperFee != 0)
            printType += "0";

        if (totalForegift != 0)
            printType += "1";

        if (totalAdvancePay != 0)
            printType += "2";

        if (isPrintG0003())
            printType += "3";

        tradeReceiptData.put("PRINT_TYPE", printType);

        // 调用PrintMgr构造打印数据
        IData commonPrintData = new DataMap();
        PrintMgr.createPrintData(tradeReceiptData, commonPrintData);
        tradeReceiptData.putAll(commonPrintData);

        // 2、根据免填单模板解析成打印串
        if (isPrintG0003())
        {
            String printName = tradeReceiptData.getString("PAPER_NAME", "");
            setNeedPrintReceipt(tradeReceiptData, getTempletItemG003(), RECEIPT_G0003, printName, false);
        }

        if (IDataUtil.isEmpty(getTempletItemG001()) && IDataUtil.isEmpty(getTempletItemG002()))// 发票和收据配置项为空则一定不打印发票和收据了，可以直接return掉
        {
            return;
        }

        if (StringUtils.isEmpty(tradeReceiptData.getString("TRADE_TYPE_CODE")))
        {
            tradeReceiptData.put("TRADE_TYPE_CODE", getTradeTypeData().getString("TRADE_TYPE_CODE"));
        }

        if (totalOperFee + totalForegift + totalAdvancePay == 0)
        {
            return;
        }

        // 3、根据发票和收据合并配置, 合并打印内容, 解析成打印串
        setFeeReceiptData(tradeReceiptData);
    }

    /**
     * 设置打印的模板项配置信息
     * 
     * @param inParam
     * @throws Exception
     */
    protected void setReceiptTempletItem(IData inParam) throws Exception
    {
        // 发票项和收据项模板配置信息(有费用)
        if (!"0".equals(getOperFee()) || !"0".equals(getForegift()) || !"0".equals(getAdvancePay()))
        {
            IData param = new DataMap();
            param.put("TRADE_TYPE_CODE", getTradeTypeData().getString("TRADE_TYPE_CODE"));
            param.put("TEMPLET_TYPE", RECEIPT_G0001); // 发票
            param.put("RELATION_KIND", "0"); // 按地州查询标志
            param.put("RELATION_ATTR", CSBizBean.getTradeEparchyCode());

            String isNeedPrintFP = StaticUtil.getStaticValue("IS_NEED_PRINT_G001", getTradeTypeData().getString("TRADE_TYPE_CODE"));// 是否打印发票，data_name配置y为打印，否则不打印

            if (StringUtils.isNotBlank(isNeedPrintFP) && StringUtils.equalsIgnoreCase("Y", isNeedPrintFP))
            {
                // 设置发票模板项配置信息
                setTempletItemG001(qryReceiptTempletItem(param));
            }

            param.clear();
            param.put("TRADE_TYPE_CODE", getTradeTypeData().getString("TRADE_TYPE_CODE"));
            param.put("TEMPLET_TYPE", RECEIPT_G0002); // 收据
            param.put("RELATION_KIND", "0"); // 按地州查询标志
            param.put("RELATION_ATTR", CSBizBean.getTradeEparchyCode());

            String isNeedPrintSJ = StaticUtil.getStaticValue("IS_NEED_PRINT_G002", getTradeTypeData().getString("TRADE_TYPE_CODE"));// 是否打印收据，data_name配置y为打印，否则不打印

            if (StringUtils.isNotBlank(isNeedPrintSJ) && StringUtils.equalsIgnoreCase("Y", isNeedPrintSJ))
            {
                // 设置收据模板项配置信息
                setTempletItemG002(qryReceiptTempletItem(param));
            }
        }

        // 查询免填单模板项信息
        if ("1".equals(getTradeTypeData().getString("PRT_TRADEFF_TAG", "0")))
        {
            IData param = new DataMap();
            param.put("TRADE_TYPE_CODE", getTradeTypeData().getString("TRADE_TYPE_CODE"));
            param.put("TEMPLET_TYPE", RECEIPT_G0003); // 免填单
            param.put("RELATION_KIND", "0"); // 按地州查询标志
            param.put("RELATION_ATTR", CSBizBean.getTradeEparchyCode());

            // 设置免填单模板项配置信息
            setTempletItemG003(qryReceiptTempletItem(param));
        }
    }

    public void setRegTradeData(RegTradeData regTradeData)
    {
        this.regTradeData = regTradeData;
    }

    public void setTempletItemG001(IDataset templetItemG001)
    {
        this.templetItemG001 = templetItemG001;
    }

    public void setTempletItemG002(IDataset templetItemG002)
    {
        this.templetItemG002 = templetItemG002;
    }

    public void setTempletItemG003(IDataset templetItemG003)
    {
        this.templetItemG003 = templetItemG003;
    }

    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }

    /**
     * 准备打印公用数据
     * 
     * @param tradeReceiptData
     * @throws Exception
     */
    protected void setTradeReceiptData(IData tradeReceiptData) throws Exception
    {
        // 获取台账信息
        RegTradeData regTradeData = getRegTradeData();
        MainTradeData mainTradeData = regTradeData.getMainTradeData();

        tradeReceiptData.put("ORDER_ID", mainTradeData.getOrderId());
        tradeReceiptData.put("TRADE_ID", regTradeData.getTradeId());
        tradeReceiptData.put("TRADE_TYPE_CODE", mainTradeData.getTradeTypeCode());
        tradeReceiptData.put("SERIAL_NUMBER", mainTradeData.getSerialNumber());
        tradeReceiptData.put("ACCT_ID", mainTradeData.getAcctId());
        IData acctData = UcaInfoQry.qryAcctInfoByAcctId(mainTradeData.getAcctId());
        String payName = IDataUtil.isNotEmpty(acctData) ? acctData.getString("PAY_NAME") : mainTradeData.getCustName();
        tradeReceiptData.put("PAY_NAME", payName);
        tradeReceiptData.put("CUST_NAME", mainTradeData.getCustName());
        tradeReceiptData.put("OPERATION_TIME", regTradeData.getAcceptTime());
        tradeReceiptData.put("TRADE_ATTR", getTradeTypeData().getString("TRADE_ATTR"));
        tradeReceiptData.put("PRT_TRADEFF_TAG", getTradeTypeData().getString("PRT_TRADEFF_TAG"));

        tradeReceiptData.put("USER_ID", mainTradeData.getUserId());
        tradeReceiptData.put("BRAND_CODE", mainTradeData.getBrandCode());
        tradeReceiptData.put("PRODUCT_ID", mainTradeData.getProductId());

        tradeReceiptData.put("TOTAL_OPER_FEE", getOperFee());
        tradeReceiptData.put("TOTAL_FOREGIFT", getForegift());
        tradeReceiptData.put("TOTAL_ADVANCE_PAY", getAdvancePay());
        tradeReceiptData.put("TOTAL_MONEY", getTotalFee());

        tradeReceiptData.put("CUR_CYCLE", SysDateMgr.getNowCyc()); // 话费账期
        tradeReceiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());// 缴费日期
        tradeReceiptData.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());// 部门名称
        tradeReceiptData.put("OPEN_NOTE_TYPE", mainTradeData.getRsrvStr10());// 开户时设置发票类型
        tradeReceiptData.put("PROCESS_TAG_SET", mainTradeData.getProcessTagSet());// 产品变更内容标识

        tradeReceiptData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeReceiptData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeReceiptData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        tradeReceiptData.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());

        // 品牌名称
        String brandName = UBrandInfoQry.getBrandNameByBrandCode(mainTradeData.getBrandCode());

        // 产品名称
        String productName = UProductInfoQry.getProductNameByProductId(mainTradeData.getProductId());

        tradeReceiptData.put("BRAND_NAME", brandName);
        tradeReceiptData.put("PRODUCT_NAME", productName);
        tradeReceiptData.put("TRADE_TYPE", getTradeTypeData().getString("TRADE_TYPE"));
        tradeReceiptData.put("REMARK", mainTradeData.getRemark()); // 订购页面的备注信息
        
        IData customer = UcaInfoQry.qryCustomerInfoByCustId(mainTradeData.getCustId(), BizRoute.getRouteId());
        tradeReceiptData.put("PSPT_ID", (IDataUtil.isEmpty(customer)) ? "" : customer.getString("PSPT_ID"));
        
        tradeReceiptData.put("TRADE_NAME", TRADE_NAME);

        /*
         * 处理费用信息, 费用不为零处理
         */
        if (!"0".equals(getTotalFee()))
        {

            // IDataset feeSubList = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(regTradeData.getTradeId(),
            // BizRoute.getRouteId());
            IDataset feeSubList = getAllTradeFeeSubByOrderId(mainTradeData.getOrderId(), regTradeData.getTradeId()); // 取所有的fee_sub表

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
                tradeReceiptData.put("OPER_FEE_SUB", operFeeData);
            }

            if (IDataUtil.isNotEmpty(foregiftData))
            {
                tradeReceiptData.put("FOREGIFT_SUB", foregiftData);
            }

            if (IDataUtil.isNotEmpty(advancePayData))
            {
                tradeReceiptData.put("PRE_FEE_SUB", advancePayData);
            }
        }
    }

    public void setTradeTypeData(IData tradeTypeData)
    {
        this.tradeTypeData = tradeTypeData;
    }

    /**
     * 根据业务类型编码查询业务类型信息
     * 
     * @param tradeTypeCode
     * @param eparchyCode
     * @throws Exception
     */
    private void setTradeTypeInfo(String tradeTypeCode, String eparchyCode) throws Exception
    {
        IData tradeTypeList = UTradeTypeInfoQry.getTradeType(tradeTypeCode, eparchyCode);

        if (IDataUtil.isEmpty(tradeTypeList))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_17, tradeTypeCode);
        }

        setTradeTypeData(tradeTypeList);
    }
    
    public void mergeFeeList(IData printData, boolean containOper, boolean containForegift, boolean containPre) throws Exception 
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
    
}
