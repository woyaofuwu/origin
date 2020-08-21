
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print;  

import java.util.List;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl.CTRL_TYPE;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CNoteTempletInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;

/**
 * 个人业务票据打印处理 P0001:发票，P0002:收据，P0003:业务受理单，
 */
public class ReceiptNotePrintMgr extends CSBizBean
{
    /**
     * 根据内容配置，组合打印数据 数据结构[TRADE_CONTANT=#RECEIPT_INFO1##RECEIPT_INFO2##RECEIPT_INFO3#]
     * 
     * @param receiptPrintData
     * @param operFeePaperType
     * @throws Exception
     */
    public static void combineReceiptContent(IData receiptPrintData, String operFeePaperType) throws Exception
    {

        IData originalPrintContent = getReceiptCombineTemlet(receiptPrintData, operFeePaperType);
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

                    value2 = receiptPrintData.getString(key2, "");
                    midReceiptInfo2 = midReceiptInfo2.replace(subStr2, value2);
                    index4++;
                }

                receiptPrintData.put(subStrArry[0], midReceiptInfo2);
                index2++;
            }
        }
    }

    /**
     * 获得费用打印内容配置
     * 
     * @param param
     * @param operFeePaperType
     * @return
     * @throws Exception
     */
    private static IData getReceiptCombineTemlet(IData param, String operFeePaperType) throws Exception
    {
        String type = "0";
        if (operFeePaperType.equals(RECEIPT_P0001)) // 发票
        {
            type = "2";
        }
        else if (operFeePaperType.equals(RECEIPT_P0002))// 收据
        {
            type = "3";
        }
        else if (operFeePaperType.equals(RECEIPT_P0003))// 免填单
        {
            type = "4";
        }

        // 获得原始打印内容
        IDataset templets = TemplateQry.qryByReceipt(param.getString("TRADE_TYPE_CODE"), param.getString("BRAND_CODE", "ZZZZ"), param.getString("PRODUCT_ID", "-1"), type, CSBizBean.getTradeEparchyCode());
        return (templets == null || templets.size() == 0) ? null : templets.getData(0);
    }

    /**
     * 获取打印模板项信息
     * 
     * @param data
     *            : TEMPLET_TYPE(模板类型)、TRADE_TYPE_CODE(业务类型)、RELATION_KIND(关系类型，0按地州)、 RELATION_ATTR(关系属性，地州编码，可配置ZZZZ)
     * @return : 模板信息 处理方法：先取模板，在根据模板取模板信息， 取模板处理如下： (1)按地市编码、业务类型来取 (2)如果(1)没配置，则地市编码为ZZZZ (3)如果(2)没配置，则业务类型为-1
     *         (4)如果(3)没配置，则返回null sql中排序处理，取第一条
     */
    public static IDataset getReceiptTempletItems(String tradeTypeCode, String templetType, String relationKind, String relationAttr) throws Exception
    {
        // 获取打印模板
        IDataset receiptTemplets = CNoteTempletInfoQry.getReceiptTemplets(tradeTypeCode, templetType, relationKind, relationAttr);
        if (IDataUtil.isEmpty(receiptTemplets))
        {
            return new DatasetList();
        }

        // 获取打印模板信息
        IDataset receiptTempletItems = CNoteTempletInfoQry.getReceiptTempletItems((String) receiptTemplets.get(0, "TEMPLET_CODE"));
        if (IDataUtil.isEmpty(receiptTempletItems))
        {
            return new DatasetList();
        }
        return receiptTempletItems;
    }

    /**
     * 解析打印参数
     * 
     * @param receiptData
     * @param receiptTempletItems
     * @return
     * @throws Exception
     */
    public static IData parsePrintData(IData receiptData, IDataset receiptTempletItems) throws Exception
    {
        IData printData = new DataMap();
        if (receiptTempletItems == null || receiptTempletItems.size() == 0)
        {
            return printData;
        }
        String key;
        // 设置打印数据项
        for (int i = 0; i < receiptTempletItems.size(); i++)
        {
            key = (String) receiptTempletItems.get(i, "ITEM_CONTENT");
            String tmpItemContent = receiptData.getString(key, "");
            // 将空格，回车换行，和自定义字符转换成特定格式
            tmpItemContent = tmpItemContent.replaceAll("\\~", "<br/>").replaceAll("\\s", "&nbsp;").replaceAll("null", "");
            printData.put(key, tmpItemContent);
        }

        IData receipt = receiptTempletItems.getData(0);
        // 设置打印位置
        printData.put("TEMP_PATH", receipt.getString("TEMPLET_PATH") + "?template_code=" + receipt.getString("TEMPLET_CODE") + "&version=" + receipt.getString("PRINT_VERSION"));
        printData.put("TEMP_TYPE", "0".equals(receipt.getString("PRINT_TYPE")) ? PRINT_WORD : PRINT_HTML);
        printData.put("PRINT_DATA_SOURCE_BAK", new DataMap(receiptData)); //添加此代码将打印源数据存储，以备打印调接口时有数据可取。(此代码放置于此,是因为所有打印都会调用此方法)(同时用new是防止receiptData被引用篡改)
        
        return printData;
    }

    /**
     * 获取业务类型编码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData queryTradeType(String tradeTypeCode) throws Exception
    {
        String eparchCode;
        if ("1".equals(CSBizBean.getVisit().getInModeCode()))
        {
            eparchCode = BizRoute.getRouteId();
        }
        else
        {
            eparchCode = CSBizBean.getTradeEparchyCode();
        }

        IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, eparchCode);

        if (IDataUtil.isEmpty(tradeType))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_7, tradeTypeCode);
        }
        return tradeType;

    }

    private IData tradeType; // 业务类型，每次打印，维护一个，融合业务，以主台账为准

    private final IDataset printReceipts; // 打印串组

    public static final String PRINT_HTML = "HTML";

    public static final String PRINT_WORD = "WORD";

    // 业务受理单和发票，收据共享数据
    public static IData PRINTNAME_DATA = null;

    public static final String RECEIPT_P0001 = "P0001";

    public static final String RECEIPT_P0002 = "P0002";

    public static final String RECEIPT_P0003 = "P0003";

    public static final String PRINTNAME_P0001 = "发票";

    public static final String PRINTNAME_P0002 = "收据";

    public static final String PRINTNAME_P0003 = "受理单";

    public static final String TRADE_NAME = "通信服务业";

    public static IData COMPILED_TEMPLATE = null;

    static
    {
        PRINTNAME_DATA = new DataMap();
        COMPILED_TEMPLATE = new DataMap();
        PRINTNAME_DATA.put(RECEIPT_P0001, PRINTNAME_P0001);
        PRINTNAME_DATA.put(RECEIPT_P0002, PRINTNAME_P0002);
        PRINTNAME_DATA.put(RECEIPT_P0003, PRINTNAME_P0003);
    }

    public ReceiptNotePrintMgr() throws Exception
    {
        tradeType = new DataMap();
        printReceipts = new DatasetList();
    }

    /**
     * 获取费用打印数据
     * 
     * @param tradeSet
     * @param input
     * @throws Exception
     */
    private void createtFeePrintDatas(IDataset tradeSet, IData input) throws Exception
    {
        if (tradeSet.isEmpty())
            return;
        for (int i = 0; i < tradeSet.size(); i++)
        {
            RegTradeData rtd = (RegTradeData) tradeSet.get(i);
            FeeNotePrintMgr feePrintMgr = new FeeNotePrintMgr();
            IDataset feePrintDatas = feePrintMgr.printTradeFee(rtd, input);
            if (IDataUtil.isEmpty(feePrintDatas))
            {
                continue;
            }
            printReceipts.addAll(feePrintDatas);
        }
    }

    /**
     * 绑定打印数据和打印模板，生成打印票据信息
     * 
     * @param receiptPrintData
     * @throws Exception
     */
    private void geneReceiptInfo(IData printData, IDataset tmpletItemP0003) throws Exception
    {
        combineReceiptContent(printData, RECEIPT_P0003);

        IData printReceiptData = parsePrintData(printData, tmpletItemP0003);

        // ----购物车
        String tradeTypeCode = tradeType.getString("TRADE_TYPE_CODE", "");
        if ("5178".equals(tradeTypeCode))
        {
            String tc = printReceiptData.getString("RECEIPT_INFO1", "");
            StringBuilder tempSb = new StringBuilder(20);
            tempSb.append("购物车");
            tempSb.append(PRINTNAME_P0003);
            if (tc.indexOf("!5178!") > -1)
            {
                String[] tcs = tc.split("!5178!");
                for (int i = 0; i < tcs.length; i++)
                {
                    IData tempPrintData = new DataMap();
                    tempPrintData.putAll(printReceiptData);
                    String strPrintData = tcs[i];
                    if (strPrintData != null && !"".equals(strPrintData))
                    {
                        tempPrintData.put("RECEIPT_INFO1", strPrintData);
                        IData printInfo = new DataMap();
                        StringBuilder printName = new StringBuilder(20);
                        printName.append(tempSb);
                        printName.append((i + 1));
                        printInfo.put("NAME", printName.toString());
                        printInfo.put("PRINT_DATA", tempPrintData);
                        printInfo.put("TYPE", RECEIPT_P0003);
                        printInfo.put("TRADE_ID", printData.getString("TRADE_ID"));
                        printInfo.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE"));
                        printReceipts.add(printInfo);
                    }
                }
                return;
            }
            else
            {
                IData printInfo = new DataMap();
                printInfo.put("NAME", tempSb.toString());
                printInfo.put("PRINT_DATA", printReceiptData);
                printInfo.put("TYPE", RECEIPT_P0003);
                printInfo.put("TRADE_ID", printData.getString("TRADE_ID"));
                printInfo.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE"));

                printReceipts.add(printInfo);
                return;
            }
        }
        // ----购物车

        if (printReceiptData.size() > 0)
        {
            IData printInfo = new DataMap();
            printInfo.put("NAME", PRINTNAME_P0003);
            printInfo.put("PRINT_DATA", printReceiptData);
            printInfo.put("TYPE", RECEIPT_P0003);
            printInfo.put("TRADE_ID", printData.getString("TRADE_ID"));
            printInfo.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE"));

            printReceipts.add(printInfo);
        }
    }

    /**
     * 获取公用打印数据
     * 
     * @param oderData
     * @throws Exception
     */
    private IData getCommonPrintData(RegTradeData rtd) throws Exception
    {
        IData commonData = new DataMap();
        MainTradeData trade = rtd.getMainTradeData();
        UcaData uca = rtd.getUca();
        CustomerTradeData cust = uca.getCustomer();
        String operationTime = SysDateMgr.getSysTime();
        String tradeTypeCode = rtd.getTradeTypeCode();
        if (null == tradeType || tradeType.isEmpty())
        {
            tradeType = queryTradeType(tradeTypeCode);
        }
        String serialNumber = trade.getSerialNumber();
        if (tradeTypeCode.equals("3520") || tradeTypeCode.equals("3521") || tradeTypeCode.equals("3523"))
        {
            serialNumber = "1" + serialNumber.substring(1);
        }
        // TD无线固话免填单SERIAL_NUMBER不打印898前缀
        if (!StringUtils.isBlank(serialNumber) && serialNumber.startsWith("898"))
        {
            serialNumber = serialNumber.substring(3);
        }
        commonData.put("TRADE_ID", rtd.getTradeId());
        commonData.put("EPARCHY_CODE", trade.getEparchyCode());
        commonData.put("TRADE_TYPE_CODE", tradeTypeCode);
        commonData.put("SERIAL_NUMBER", serialNumber);
        commonData.put("PSPT_ID", (null == cust) ? "" : cust.getPsptId());
        commonData.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(trade.getBrandCode()));

        commonData.put("TRADE_TYPE", tradeType.getString("TRADE_TYPE"));

        commonData.put("CUST_NAME", trade.getCustName());

        commonData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        commonData.put("OPERATION_YEAR", operationTime.substring(0, 4));
        commonData.put("OPERATION_MONTH", operationTime.substring(5, 7));
        commonData.put("OPERATION_DAY", operationTime.substring(8, 10));

        commonData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        commonData.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());
        commonData.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());// 部门名称
        
        //start REQ201406110014 关于纸质业务受理单分类需求 by songlm 
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
        exector.prepare(rtd);
        String TRADE_CLASS_INFO = exector.applyTemplate("@{TRADE_TYPE_CLASS}(@{ACCEPT_MODE})");
        commonData.put("TRADE_CLASS_INFO", TRADE_CLASS_INFO);
        //end

        return commonData;
    }

    /**
     * 获取解析模板编译对象
     * 
     * @param expression
     * @return
     * @throws Exception
     */
    private CompiledTemplate getCompiledTemplate(String expression) throws Exception
    {
        if (!COMPILED_TEMPLATE.containsKey(expression))
        {
            COMPILED_TEMPLATE.put(expression, TemplateCompiler.compileTemplate(expression));
        }
        return (CompiledTemplate) COMPILED_TEMPLATE.get(expression);

    }

    /**
     * 获取打印解析后的串对象
     * 
     * @param tradeSet
     * @return
     * @throws Exception
     */
    public IData getPrintReceiptData(IDataset tradeSet) throws Exception
    {
        IData tradeReceiptData = new DataMap();

        IDataset receiptSet = new DatasetList();

        // ----购物车
        String tradeTypeCode = tradeType.getString("TRADE_TYPE_CODE", "");
        int tempCount = 1;
        // ----购物车

        int count = 1;

        if (IDataUtil.isNotEmpty(tradeSet))
        {
            RegTradeData rtd = null;
            for (int i = 0; i < tradeSet.size(); i++)
            {
                rtd = (RegTradeData) tradeSet.get(i);
                IData tmpReceipt = getTradeReceipt(rtd);
                if (IDataUtil.isNotEmpty(tmpReceipt))
                {
                    // 购物车
                    tmpReceipt.put("TRADE_TYPE_CODE", rtd.getTradeTypeCode());
                    // 购物车
                    receiptSet.add(tmpReceipt);
                }
            }
        }

        if (receiptSet.size() == 1)
        {
            tradeReceiptData = receiptSet.getData(0);
        }
        else if (receiptSet.size() > 1)
        {
            // 循环去解析打印串
            for (int i = 0; i < receiptSet.size(); i++)
            {
                IData tmp = receiptSet.getData(i);

                // 拼接打印数据
                StringBuilder receiptBuf = new StringBuilder();
                receiptBuf.append(count).append(".");
                if (tmp.getString("RECEIPT_INFO1", "") != null && !tmp.getString("RECEIPT_INFO1", "").equals(""))
                {
                    receiptBuf.append(tmp.getString("RECEIPT_INFO1"));
                }
                if (tmp.getString("RECEIPT_INFO2", "") != null && !tmp.getString("RECEIPT_INFO2", "").equals(""))
                {
                    receiptBuf.append(tmp.getString("RECEIPT_INFO2"));
                }
                if (tmp.getString("RECEIPT_INFO3", "") != null && !tmp.getString("RECEIPT_INFO3", "").equals(""))
                {
                    receiptBuf.append(tmp.getString("RECEIPT_INFO3"));
                }
                if (tmp.getString("RECEIPT_INFO4", "") != null && !tmp.getString("RECEIPT_INFO4", "").equals(""))
                {
                    receiptBuf.append(tmp.getString("RECEIPT_INFO4"));
                }
                if (tmp.getString("RECEIPT_INFO5", "") != null && !tmp.getString("RECEIPT_INFO5", "").equals(""))
                {
                    receiptBuf.append(tmp.getString("RECEIPT_INFO5"));
                }
                if (i < receiptSet.size() - 1)
                    receiptBuf.append("~~");
                // --------购物车
                if ("5178".equals(tradeTypeCode))
                {
                    String tempTradeTypeCode = tmp.getString("TRADE_TYPE_CODE");
                    if (TradeCtrl.getCtrlBoolean(tempTradeTypeCode, CTRL_TYPE.IS_PRT_SINGLE, false))
                    {
                        tempCount = 0;
                        tradeReceiptData.put("RECEIPT_INFO1", tradeReceiptData.getString("RECEIPT_INFO1", "") + "!5178!" + receiptBuf.toString() + "!5178!");
                    }
                    else if (tempCount > 5)
                    {
                        tempCount = 0;
                        tradeReceiptData.put("RECEIPT_INFO1", tradeReceiptData.getString("RECEIPT_INFO1", "") + "!5178!" + receiptBuf.toString());
                    }
                    else
                    {
                        tradeReceiptData.put("RECEIPT_INFO1", tradeReceiptData.getString("RECEIPT_INFO1", "") + receiptBuf.toString());
                    }
                }
                else
                {
                    tradeReceiptData.put("RECEIPT_INFO" + count, receiptBuf.toString());
                }
                // tradeReceiptData.put("RECEIPT_INFO" + count, receiptBuf.toString());
                tempCount++;
                // --------购物车
                count++;
            }
        }
        return tradeReceiptData;
    }

    /**
     * 获取免填单打印配置信息
     * 
     * @param tradeData
     * @return
     * @throws Exception
     */
    private IData getReceiptTemplate(RegTradeData rtd) throws Exception
    {
        MainTradeData trade = rtd.getMainTradeData();
        String userId = trade.getUserId();
        String eparchCode = trade.getEparchyCode();
        String tradeTypeCode = trade.getTradeTypeCode();
        String brandCode = trade.getBrandCode();
        String productId = trade.getProductId();

        // 判断当前业务是否需要打印[融合业务判断其他业务是否需要打印]
        IData tradeTypeData = UTradeTypeInfoQry.getTradeType(tradeTypeCode, eparchCode);
        if (IDataUtil.isEmpty(tradeTypeData))
        {
            return null;
        }
        if ("0".equals(tradeTypeData.getString("PRT_TRADEFF_TAG", "0")))
        {
            return null;
        }
        String tradeAttr = tradeTypeData.getString("TRADE_ATTR", "1");

        if (StringUtils.isBlank(eparchCode))
        {
            if ("1".equals(CSBizBean.getVisit().getInModeCode()))
            {
                eparchCode = BizRoute.getRouteId();
            }
            else
            {
                eparchCode = CSBizBean.getTradeEparchyCode();
            }
        }

        if (StringUtils.isBlank(brandCode) || StringUtils.isBlank(productId))
        {
            if (StringUtils.isBlank(userId))
            {
                brandCode = "ZZZZ";
                productId = "-1";

            }
            else
            {
                IData userInfo = UcaInfoQry.qryUserInfoByUserId(rtd.getUca().getUser().getUserId()); 
                
                if (rtd.getUca().getUser().getUserId().equals(rtd.getTradeId())
                        && ("461".equals(tradeTypeCode) || "416".equals(tradeTypeCode) || "418".equals(tradeTypeCode) || "419".equals(tradeTypeCode) || "420".equals(tradeTypeCode) || "421".equals(tradeTypeCode) || "424".equals(tradeTypeCode)))
                {// 有价卡特殊处理 售卡的时候不输入号码
                    brandCode = "ZZZZ";
                    productId = "-1";
                }
                else if ("290".equals(tradeTypeCode) && (IDataUtil.isEmpty(userInfo)||!"0".equals(userInfo.getString("REMOVE_TAG"))))
                {
                    // 无主发票清退,销号用户清退
                    brandCode = "ZZZZ";
                    productId = "-1";
                }
                else if ("149".equals(tradeTypeCode) || "79".equals(tradeTypeCode) || "1990".equals(tradeTypeCode))
                {
                    brandCode = "ZZZZ";
                    productId = "-1";
                }
                else
                {
                    IData product = TradeInfoBean.getUserProductInfo(userId, rtd.getUca().getUser().getRemoveTag());
                    if (IDataUtil.isEmpty(product))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_75);
                    }
                    brandCode = product.getString("BRAND_CODE");
                    productId = product.getString("PRODUCT_ID");
                }
            }
        }
        IDataset templates = TemplateQry.qryByReceipt(tradeTypeCode, brandCode, productId, tradeAttr, eparchCode);
        if (IDataUtil.isEmpty(templates))
        {
            // CSAppException.apperr(PrintException.CRM_PRINT_12);
            // 直接返回，不作打印
            return null;
        }
        int tempCount = templates.size();
        int idx = -1; // 需要返回的解析模板序列
        int[] errIdx = new int[tempCount];
        String expression = null;
        CompiledTemplate compiled = null;
        for (int i = 0; i < tempCount; i++)
        {
            expression = templates.getData(i).getString("EXPRESSION");
            if (StringUtils.isBlank(expression))
            {
                errIdx[i] = 0; // 设至为空的标记为0
                continue;
            }
            compiled = getCompiledTemplate(expression);
            boolean flag = ((Boolean) TemplateRuntime.execute(compiled, rtd)).booleanValue();
            if (flag)
            {
                idx = i;
                break;
            }
            errIdx[i] = 1; // 设置不符合表达式的标记为1
        }
        // 如果没有符合条件的解析模板，则取表达式为空模板
        if (idx == -1)
        {
            for (int i = 0; i < errIdx.length; i++)
            {
                if (errIdx[i] == 0)
                {
                    idx = i;
                    break;
                }
            }
            if (idx == -1)
            {
                CSAppException.apperr(PrintException.CRM_PRINT_12);
            }
        }
        return templates.getData(idx); // 返回匹配的打印解析模板
    }

    /**
     * 执行解析动作
     * 
     * @param RegTradeData
     * @param rtd
     * @return
     * @throws Exception
     */
    public IData getTradeReceipt(RegTradeData rtd) throws Exception
    {
        IData receipt = new DataMap();
        
        // ======购物车
        if ("5178".equals(rtd.getTradeTypeCode()))
            return receipt;
        // ======购物车

        // 查询模板配置
        IData receiptData = getReceiptTemplate(rtd);
        if (IDataUtil.isEmpty(receiptData))
        {
            return receipt;
        }
        
        
        // 根据模板ID解析变量串
        receipt = parsePrintReceipt(rtd, receiptData);
        
        /**
         * REQ201410230014电子化存储系统优化（一）
         * 2015-04-15 chenxy3
         * 获取用户的是待激活用户，则给予保存表TF_B_TRADE_CNOTE_INFO新字段RSRV_STR1，用于提示
         * 显示电子协议
         * */
        String isRealName=rtd.getUca().getCustomer().getIsRealName();
        String tradeTypeCode=rtd.getTradeTypeCode();
        if(tradeTypeCode!=null&&"60".equals(tradeTypeCode)){
	        if(isRealName==null||"".equals(isRealName)){
	        	receipt.put("EPRINT_FLAG", "1");
	        }
        }
        // 登记解析串日志记录
        regCnoteInfo(rtd.getTradeId(), rtd.getAcceptTime(), receipt);

        return receipt;
    }

    /**
     * 根据登记对象解析模板变量串
     * 
     * @param regOrderData
     * @param templetId
     * @return
     * @throws Exception
     */
    private IData parsePrintReceipt(RegTradeData rtd, IData templetData) throws Exception
    {
        String content = null;
        String contentStr = null;
        IData contentData = new DataMap();
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        exector.prepare(rtd);

        for (int i = 1; i <= 5; i++)
        {
            content = templetData.getString("TEMPLATE_CONTENT" + i, "");
            if (StringUtils.isNotBlank(content))
            {
                contentStr = exector.applyTemplate(content);
                // 如果有嵌套,则继续解析
                int idx = contentStr.indexOf("@{", 0);
                if (idx > -1 && contentStr.indexOf("}", idx) > -1)
                {
                    contentData.put("RECEIPT_INFO" + i, exector.applyTemplate(contentStr));
                }
                else
                {
                    contentData.put("RECEIPT_INFO" + i, contentStr);
                }

            }
            else
            {
                contentData.put("RECEIPT_INFO" + i, "");
            }
        }
        return contentData;
    }

    /**
     * 一级BOSS打印接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset printInterBossReceipt(IData data) throws Exception
    {
        IDataset printRes = new DatasetList();
        String tradeId = data.getString("TRADE_ID", "");

        // 获得原始打印内容
        IDataset templets = TemplateQry.qryByReceipt(data.getString("TRADE_TYPE_CODE"), data.getString("BRAND_CODE", "ZZZZ"), data.getString("PRODUCT_ID", "-1"), data.getString("TRADE_ATTR", "8"), CSBizBean.getTradeEparchyCode());

        if (templets == null || templets.isEmpty())
        {
            CSAppException.apperr(PrintException.CRM_PRINT_7, data.getString("TRADE_TYPE_CODE"));
        }
        IData templetData = templets.getData(0);
        String receiptInfo = "";
        String midReceiptInfo = "";
        String subStr = "";
        String key = "";
        String value = "";
        int index1 = 0, index2 = 0;
        IData printData = new DataMap();
        for (int i = 1; i <= 5; i++)
        {
            index1 = 0;
            index2 = 0;
            receiptInfo = templetData.getString("TEMPLATE_CONTENT" + i);
            if (receiptInfo == null)
                continue;
            midReceiptInfo = receiptInfo;
            while (true)
            {
                index1 = receiptInfo.indexOf('#', index2);
                if (index1 == -1)
                    break;
                index1++;
                index2 = receiptInfo.indexOf('#', index1);
                if (index2 == -1)
                    break;

                subStr = receiptInfo.substring(index1 - 1, index2 + 1);
                key = receiptInfo.substring(index1, index2);

                value = data.getString(key, "");
                midReceiptInfo = midReceiptInfo.replace(subStr, value);
                index2++;
            }

            if (i == 1)
            {
                printData.put("RECEIPT_INFO1", midReceiptInfo);
            }
            else if (i == 2 || i == 3)
            {
                String str = printData.getString("RECEIPT_INFO2", "");
                printData.put("RECEIPT_INFO2", str + midReceiptInfo);
            }
            else if (i == 4)
            {
                printData.put("RECEIPT_INFO3", midReceiptInfo);
            }
            else if (i == 5)
            {
                printData.put("RECEIPT_INFO4", midReceiptInfo);
            }
        }
        String operationTime = SysDateMgr.getSysTime();
        printData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        printData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        printData.put("TRADE_ID", tradeId);

        printData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        printData.put("STAFF_NAME", data.getString("TRADE_STAFF_NAME", CSBizBean.getVisit().getStaffEparchyName()));
        printData.put("DEPART_NAME", data.getString("TRADE_DEPART_NAME", CSBizBean.getVisit().getDepartName()));

        printData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        printData.put("CUST_NAME", data.getString("CUST_NAME"));
        if(StringUtils.isNotBlank(data.getString("BRAND_CODE"))){
        	printData.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND_CODE")));
        }
        printData.put("PSPT_ID", data.getString("PSPT_ID",""));

        printData.put("OPERATION_TIME", operationTime);
        printData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        printData.put("OPERATION_YEAR", operationTime.substring(0, 4));
        printData.put("OPERATION_MONTH", operationTime.substring(5, 7));
        printData.put("OPERATION_DAY", operationTime.substring(8, 10));
        
        //记录trade_info日志
        if(!StringUtils.equals(tradeId, "")){
        	regCnoteInfo(tradeId, operationTime, printData);
        }
        
        /**
         * 组合模板，这个还需要后续进一步确认
         */
        combineReceiptContent(printData, RECEIPT_P0003);
        // 得到地州模版配置
        IDataset templetItems = CNoteTempletInfoQry.getCommonTempletItem(CSBizBean.getTradeEparchyCode(), RECEIPT_P0003);
        if (null == templetItems || templetItems.isEmpty())
        {
            // 如果没有地州模板，则获取通用模板
            templetItems = CNoteTempletInfoQry.getCommonTempletItem("ZZZZ", RECEIPT_P0003);
        }
        IData templetPrintData = ReceiptNotePrintMgr.parsePrintData(printData, templetItems);

        if (templetPrintData != null && !templetPrintData.isEmpty())
        {
            IData printInfo = new DataMap();
            printInfo.put("NAME", "移动客户业务受理单");
            printInfo.put("PRINT_DATA", templetPrintData);
            printInfo.put("TYPE", RECEIPT_P0003);
            printRes.add(printInfo);
        }
        return printRes;
    }

    /**
     * 普通登记订单台账打印 会根据订单编号或者台账流水号进行判断
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset printTradeReceipt(IData data) throws Exception
    {
        String orderId = data.getString("ORDER_ID", "");
        RegOrderData rod = new RegOrderData(orderId);
        RegTradeData mrtd = rod.getMainRegData();
        // 如果没有登记台账  则返回'' add by fangwz
        if (rod==null&&mrtd==null) { 
        	return printReceipts;
		}
        IDataset tradeSet = new DatasetList();
        IDataset allTradeSet = new DatasetList();
        tradeSet.add(mrtd);
        allTradeSet.add(mrtd);
        String tradeTypeCode = mrtd.getTradeTypeCode();
        List<RegTradeData> rtds = rod.getOtherRegData();
        if (null != rtds && rtds.size() > 0)
        {
            for (RegTradeData rtd : rtds)
            {
                allTradeSet.add(rtd);
                // 如果其他台账登记的业务类型与主台账一致，则不打印
                if (tradeTypeCode.equals(rtd.getTradeTypeCode()))
                {
                    continue;
                }
                tradeSet.add(rtd);
            }
        }

        // 创建费用打印数据
        createtFeePrintDatas(allTradeSet, data);

        tradeType = queryTradeType(mrtd.getTradeTypeCode());

        // 如果不需要打印免填单则直接返回
        if ("0".equals(tradeType.getString("PRT_TRADEFF_TAG", "0")))
        {
            return printReceipts;
        }
        //根据业务类型、和传入衡量RECEIPT_P0003 即P0003 ，获取到templet/P0003_3.html中需要的各个参数名称，如 BRAND RECEIPT_INFO1 等
        IDataset tmpletItemP0003 = getReceiptTempletItems(mrtd.getTradeTypeCode(), RECEIPT_P0003, "0", CSBizBean.getTradeEparchyCode());

        // 获取免填单解析串数据
        IData receiptData = new DataMap();
        if (IDataUtil.isNotEmpty(tmpletItemP0003))
        {
            receiptData = getPrintReceiptData(tradeSet);//貌似这里只包含RECEIPT_INFO1  --- RECEIPT_INFO5的信息，且是已经解析好的
        }

        // 如果没有获取到打印免填单解析数据，则返回
        if (IDataUtil.isEmpty(receiptData))
        {
            return printReceipts;
        }

        IData printData = getCommonPrintData(mrtd);//这里面包含了用户的通用业务信息，如品牌、手机号码、年、月、日、客户姓名、受理工号等
        // 自定义数据，后续可能会覆盖前面拼接的打印数据串
        IData params = new DataMap(data.getString("PRINT_PARAMS", "{}"));
        if (!params.isEmpty())
            printData.putAll(params);
        printData.put("ORDER_ID", data.getString("ORDER_ID"));
        printData.putAll(receiptData);
        // 绑定打印解析模板数据
        geneReceiptInfo(printData, tmpletItemP0003);

        return printReceipts;
    }

    /**
     * 登记解析串
     * 
     * @param tradeData
     * @param receiptData
     * @throws Exception
     */
    public static void regCnoteInfo(String tradeId, String acceptTime, IData receiptData) throws Exception
    {
        String noteType = "1";
        if (StringUtils.isBlank(acceptTime))
        {
            acceptTime = SysDateMgr.getSysTime();
        }
        String acceptMonth = acceptTime.substring(5, 7);

        IDataset noteInfoLogs = TradeReceiptInfoQry.getNoteInfoByPk(tradeId, acceptMonth, noteType);
        // 如果没有解析串记录，则记录打印模板数据到TF_B_TRADE_CNOTE_INFO表
        if (IDataUtil.isEmpty(noteInfoLogs))
        {
            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            param.put("ACCEPT_MONTH", acceptMonth);
            param.put("RECEIPT_INFO1", receiptData.getString("RECEIPT_INFO1", ""));
            param.put("RECEIPT_INFO2", receiptData.getString("RECEIPT_INFO2", ""));
            param.put("RECEIPT_INFO3", receiptData.getString("RECEIPT_INFO3", ""));
            param.put("RECEIPT_INFO4", receiptData.getString("RECEIPT_INFO4", ""));
            param.put("RECEIPT_INFO5", receiptData.getString("RECEIPT_INFO5", ""));
            /**
             * REQ201410230014电子化存储系统优化（一）
             * 2015-04-15 chenxy3
             * 获取用户的是待激活用户，则给予保存表TF_B_TRADE_CNOTE_INFO新字段RSRV_STR1，用于提示
             * 显示电子协议
             * */
            if(receiptData.containsKey("EPRINT_FLAG")){
            	param.put("RSRV_STR1", "1");
            }
            param.put("NOTE_TYPE", noteType);
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

            param.put("ACCEPT_DATE", acceptTime);
            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INS_RECEIPT_LOGINFO", param, Route.getJourDbDefault());
        }
    }
}
