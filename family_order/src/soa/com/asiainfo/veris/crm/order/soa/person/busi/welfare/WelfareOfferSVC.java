
package com.asiainfo.veris.crm.order.soa.person.busi.welfare;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 权益自有商品受理服务
 * @Auther: zhenggang
 * @Date: 2020/7/3 9:58
 * @version: V1.0
 */
public class WelfareOfferSVC extends CSBizService
{
    protected static final Logger log = Logger.getLogger(WelfareOfferSVC.class);

    /**
     * @Description: 权益关联订单查询
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/8 11:03
     */
    public IDataset qryWelfareRelOrder(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();
        String orderId = input.getString("ORDER_ID");
        String tradeId = input.getString("TRADE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        String orderTypeCode = input.getString("ORDER_TYPE_CODE");
        String accessNum = "";
        String action = "";

        // 这里只判断ORDER_TYPE_CODE是否在配置的业务类型里
        // 目前先不考虑一笔ORDER多笔TRADE的情况
        IDataset configs = StaticUtil.getStaticList("WELFARE_TRADE_TYPE_CODE");

        if (IDataUtil.isEmpty(configs))
        {
            return results;
        }

        boolean isContinue = false;
        for (int i = 0; i < configs.size(); i++)
        {
            IData config = configs.getData(i);
            String dataId = config.getString("DATA_ID");
            if (orderTypeCode.equals(dataId))
            {
                isContinue = true;
                break;
            }
        }

        if (!isContinue)
        {
            return results;
        }

        // 查询工单表
        // 如果工单记录ORDER_ID和前台返回的不一致不处理
        isContinue = false;
        IDataset mainTrades = TradeInfoQry.queryUserTradeByBTradeAndBhTrade(tradeId);

        if (IDataUtil.isNotEmpty(mainTrades))
        {
            for (int i = 0; i < mainTrades.size(); i++)
            {
                IData mainTrade = mainTrades.getData(i);
                String tmpOrderId = mainTrade.getString("ORDER_ID");
                if (orderId.equals(tmpOrderId))
                {
                    accessNum = mainTrade.getString("SERIAL_NUMBER");
                    isContinue = true;
                    break;
                }
            }
        }

        if (!isContinue)
        {
            return results;
        }

        // 查产品台账表
        IDataset tradeProducts = TradeProductInfoQry.getTradeProductByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeProducts))
        {
            return results;
        }

        // 新增产品
        String addProductId = "";
        // 删除产品
        String delProductId = "";
        // 新增产品开始时间
        String addStartDate = "";
        // 删除产品结束时间
        String delEndDate = "";
        // 用户ID
        String userId = "";

        for (int i = 0; i < tradeProducts.size(); i++)
        {
            IData tradeProduct = tradeProducts.getData(i);

            String productId = tradeProduct.getString("PRODUCT_ID");
            String modifyTag = tradeProduct.getString("MODIFY_TAG");
            String startDate = tradeProduct.getString("START_DATE");
            String endDate = tradeProduct.getString("END_DATE");
            userId = tradeProduct.getString("USER_ID");

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                addProductId = productId;
                addStartDate = startDate;
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                delProductId = productId;
                delEndDate = endDate;
            }
        }

        List<String> addWelfareOfferList = new ArrayList<String>();
        List<String> delWelfareOfferList = new ArrayList<String>();

        IDataset addRelWelfareOfferCfgs = UpcCall.queryMainOfferRelaWelfareOffers(addProductId);

        if (IDataUtil.isNotEmpty(addRelWelfareOfferCfgs))
        {
            // 新增产品存在关联权益包
            for (int i = 0; i < addRelWelfareOfferCfgs.size(); i++)
            {
                String tmpStr = addRelWelfareOfferCfgs.getData(i).getString("OFFER_NAME");
                addWelfareOfferList.add(tmpStr);
                String offerCode = addRelWelfareOfferCfgs.getData(i).getString("OFFER_CODE");
                IData addWelFare = UpcCall.queryOfferByOfferId(WelfareConstants.RelType.WEFFARE.getValue(),offerCode,"Y");
                if(IDataUtil.isNotEmpty(addWelFare)){
                    String addWelFareId = addWelFare.getString("RIGHTS_CODE","");
                    log.debug("-----delWelFareId:----- " + addWelFareId);
                    result.put("ADD_OFFERS_ID",addWelFareId);
                    result.put("USER_ID",userId);
                }
            }
        }

        if (StringUtils.isNotBlank(delProductId))
        {
            action = BofConst.MODIFY_TAG_UPD;
            IDataset mainProductInfo = UserProductInfoQry.queryMainProductNow(userId);
            String mainInstId = mainProductInfo.getData(0).getString("INST_ID");
            IDataset userWelfareOfferRels = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferTypeAndInstId("Q", mainInstId);
                if (IDataUtil.isNotEmpty(userWelfareOfferRels))
                {
                    //需要增加剔除重复的权益包ID
                    //userWelfareOfferRels = DataHelper.distinct(userWelfareOfferRels, "REL_OFFER_CODE", ",");
                    for (int i = 0; i < userWelfareOfferRels.size(); i++)
                    {
                        IData userWelfareOfferRel = userWelfareOfferRels.getData(i);
                        String relType = userWelfareOfferRel.getString("REL_TYPE");
                        String relOfferCode = userWelfareOfferRel.getString("REL_OFFER_CODE");
                        String relOfferInsId = userWelfareOfferRel.getString("REL_OFFER_INS_ID");
                        if (!WelfareConstants.RelType.WEFFARE.getValue().equals(relType))
                        {
                            continue;
                        }
                        //for (int j = 0; j < userWelfareOfferRels.size(); j++)
                        //{
                        IDataset delRelWelfareOfferCfgs = UpcCall.queryOfferNameByOfferCodeAndType("Q", relOfferCode);
                        //IDataset delRelWelfareOfferCfgs = UpcCall.queryMainOfferRelaWelfareOffers(delProductId);
                        String tmpStr = delRelWelfareOfferCfgs.getData(0).getString("OFFER_NAME","");

                            if(relOfferInsId.equals(mainInstId))
                             {
                                // 这里判断按复杂的情况 要区分是权益订购过来的 还是订单中心关联订购经过权益中心过来的
                                // 订单中心关联订购发起的才在这里处理
                                 delWelfareOfferList.add(tmpStr);
                             }
                       // }
                    }
                }
            }
        //}

        if (ArrayUtil.isEmpty(addWelfareOfferList) && ArrayUtil.isEmpty(delWelfareOfferList))
        {
            // 没有要处理的权益包
            return results;
        }

        // 生效时间预约标记
        String bookingTag = SysDateMgr.compareTo(addStartDate, SysDateMgr.getSysTime()) > 0 ? "1" : "0";

        // 本次订前置订单
        result.put("ADVANCE_ORDER_ID", orderId);
        result.put("ADVANCE_TRADE_ID", tradeId);
        // 预约标记
        result.put("BOOKING_TAG", bookingTag);
        // 开始时间
        result.put("ADD_START_DATE", addStartDate);
        // 新增产品ID
        result.put("ADD_PRODUCT_ID", addProductId);
        // 退订产品ID
        result.put("DEL_PRODUCT_ID", delProductId);

        if (StringUtils.isNotBlank(delEndDate))
        {
            result.put("DEL_END_DATE", delEndDate);
        }

        if (ArrayUtil.isNotEmpty(addWelfareOfferList))
        {
            //result.put("ADD_WELFARE_OFFER_STR", StringUtils.join(addWelfareOfferList, ","));
            //订购页面一次只传一个权益包
            result.put("ADD_WELFARE_OFFER_STR",addWelfareOfferList.get(0).toString());
        }

        if (ArrayUtil.isNotEmpty(delWelfareOfferList))
        {
            //result.put("DEL_WELFARE_OFFER_STR", StringUtils.join(delWelfareOfferList, ","));
            result.put("DEL_WELFARE_OFFER_STR",delWelfareOfferList.get(0).toString() );
        }

        if (ArrayUtil.isNotEmpty(addWelfareOfferList) && "".equals(delProductId))
        {
            // 新增有删除没有只新增
            action = BofConst.MODIFY_TAG_ADD;
        }
        else if (ArrayUtil.isEmpty(addWelfareOfferList) && ArrayUtil.isNotEmpty(delWelfareOfferList))
        {
            // 新的没有旧的有只删除
            action = BofConst.MODIFY_TAG_DEL;
        }

        result.put("ACTION", action);
        result.put("SERIAL_NUMBER", accessNum);
        results.add(result);
        return results;
    }

    /**
     * @Description 组装弹窗参数
     * @Auther: zhenggang
     * @Date: 2020/7/9 18:04
     * @version: V1.0
     */
    public IDataset getPopPageParam(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String action = input.getString("ACTION");
        String bookingTag = input.getString("BOOKING_TAG");
        String addWelfareOfferStr = input.getString("ADD_OFFER_STR");
        String delWelfareOfferStr = input.getString("DEL_OFFER_STR");
        String addStartDate = input.getString("ADD_START_DATE");
        String delEndDate = input.getString("DEL_END_DATE");
        String advanceOrderId = input.getString("ADVANCE_ORDER_ID");
        //String advanceTradeId = input.getString("ADVANCE_TRADE_ID");
        String url = StaticUtil.getStaticValue("WELFARE_URL", "WELFARE_URL");
        String addProductId = input.getString("ADD_PRODUCT_ID");
        String delProductId = input.getString("DEL_PRODUCT_ID");
        String addOfferId = input.getString("ADD_OFFERS_ID","");
        String userId = input.getString("USER_ID");

        IData data = new DataMap();

        String prodName = UpcCall.qryOfferNameByOfferTypeOfferCode(addProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        prodName = "[" + addProductId + "]" + prodName;
        String tipMsg = "本次业务新增产品 " + prodName + " 存在关联订购权益包 [" + addWelfareOfferStr + "]！";
        data.put("ADD_PRODUCT_NAME", prodName);
        if (!BofConst.MODIFY_TAG_ADD.equals(action))
        {
            prodName = UpcCall.qryOfferNameByOfferTypeOfferCode(delProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            prodName = "[" + delProductId + "]" + prodName;
            tipMsg += "\n 本次业务退订产品 " + prodName + " 存在关联退订权益包 [" + delWelfareOfferStr + "]，后台统一退订！";
            data.put("DEL_PRODUCT_NAME", prodName);
        }

        StringBuilder urlSb = new StringBuilder();
        urlSb.append(url);
        urlSb.append("&telNum=").append(serialNumber);
        urlSb.append("&ACTION=").append(action);
        urlSb.append("&ADD_OFFERS=").append(addWelfareOfferStr);
        urlSb.append("&DEL_OFFERS=").append(delWelfareOfferStr);
        urlSb.append("&rightId=").append(addOfferId);
        urlSb.append("&ADD_START_DATE=").append(addStartDate);
        urlSb.append("&DEL_END_DATE=").append(delEndDate);
        urlSb.append("&BOOKING_TAG=").append(bookingTag);
        urlSb.append("&outOrderId=").append(advanceOrderId);
        //urlSb.append("&ADVANCE_TRADE_ID=").append(advanceTradeId);
        urlSb.append("&userId=").append(userId);
        log.debug("-----urlSb:----- " + urlSb);
        IDataset dataset = new DatasetList();
        data.put("URL", urlSb.toString());
        data.put("TIP_MSG", tipMsg);
        data.putAll(input);
        dataset.add(data);
        return dataset;
    }

    public IDataset getPrintData(IData data) throws Exception
    {
        ReceiptNotePrintMgr mgr = new ReceiptNotePrintMgr();
        IDataset printReceipts = new DatasetList();

        // 前置订单信息
        String orderId = data.getString("ORDER_ID", "");
        // 权益受理业务类型
        String tradeTypeCode = WelfareConstants.TradeType.ACCEPT.getValue();

        RegOrderData rod = new RegOrderData(orderId);

        if (rod == null)
        {
            return printReceipts;
        }

        RegTradeData mrtd = rod.getMainRegData();
        mrtd.getMainTradeData().setTradeTypeCode(tradeTypeCode);
        // TODO 调用权益中心接口获取打印信息
        // TODO 测试使用 start
        // 权益订单信息
        String welfareFlowId = data.getString("WELFARE_FLOW_ID", "1120040704614748");// 测试使用 要改掉
        String printContent = "权益订购1212121212121212121212121212121";// 测试使用 要改掉
        // TODO 测试使用 end

        // 权益打印信息设置到预留字段1属性
        mrtd.getMainTradeData().setRsrvStr1(printContent);

        IDataset tradeSet = new DatasetList();
        IDataset allTradeSet = new DatasetList();
        tradeSet.add(mrtd);
        allTradeSet.add(mrtd);

        List<RegTradeData> rtds = rod.getOtherRegData();

        if (null != rtds && rtds.size() > 0)
        {
            for (RegTradeData rtd : rtds)
            {
                allTradeSet.add(rtd);
                if (mrtd.getTradeTypeCode().equals(rtd.getTradeTypeCode()))
                {
                    continue;
                }
                tradeSet.add(rtd);
            }
        }

        IData tradeType = ReceiptNotePrintMgr.queryTradeType(tradeTypeCode);

        if ("0".equals(tradeType.getString("PRT_TRADEFF_TAG", "0")))
        {
            return printReceipts;
        }

        IDataset tmpletItemP0003 = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0003, "0", CSBizBean.getTradeEparchyCode());

        IData receiptData = new DataMap();

        if (IDataUtil.isNotEmpty(tmpletItemP0003))
        {
            receiptData = mgr.getPrintReceiptData(tradeSet);
        }

        if (IDataUtil.isEmpty(receiptData))
        {
            return printReceipts;
        }
        IData printData = getCommonPrintData(mrtd, tradeType, welfareFlowId);

        printData.put("ORDER_ID", orderId);

        printData.putAll(receiptData);

        // 绑定打印解析模板数据
        geneReceiptInfo(printData, tmpletItemP0003, printReceipts);

        return printReceipts;
    }

    private void geneReceiptInfo(IData printData, IDataset tmpletItemP0003, IDataset printReceipts) throws Exception
    {
        ReceiptNotePrintMgr.combineReceiptContent(printData, ReceiptNotePrintMgr.RECEIPT_P0003);

        IData printReceiptData = ReceiptNotePrintMgr.parsePrintData(printData, tmpletItemP0003);

        if (printReceiptData.size() > 0)
        {
            IData printInfo = new DataMap();
            printInfo.put("NAME", ReceiptNotePrintMgr.PRINTNAME_P0003);
            printInfo.put("PRINT_DATA", printReceiptData);
            printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0003);
            printInfo.put("TRADE_ID", printData.getString("TRADE_ID"));// 订单流水换成权益订单流水
            printInfo.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE"));
            printReceipts.add(printInfo);
        }
    }

    private IData getCommonPrintData(RegTradeData rtd, IData tradeType, String welfareFlowId) throws Exception
    {
        IData commonData = new DataMap();
        MainTradeData trade = rtd.getMainTradeData();
        UcaData uca = rtd.getUca();
        CustomerTradeData cust = uca.getCustomer();
        String operationTime = SysDateMgr.getSysTime();
        String tradeTypeCode = tradeType.getString("TRADE_TYPE_CODE");
        commonData.put("TRADE_ID", welfareFlowId);// 订单流水换成权益订单流水
        commonData.put("EPARCHY_CODE", trade.getEparchyCode());
        commonData.put("TRADE_TYPE_CODE", tradeTypeCode);
        commonData.put("SERIAL_NUMBER", trade.getSerialNumber());
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
        commonData.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
        exector.prepare(rtd);
        String TRADE_CLASS_INFO = exector.applyTemplate("@{TRADE_TYPE_CLASS}(@{ACCEPT_MODE})");
        commonData.put("TRADE_CLASS_INFO", TRADE_CLASS_INFO);
        return commonData;
    }

    /**
     * @Description: 查三户资料权益中心使用
     * @author liwei29
     * @Date: 2020/7/15
     */
    public IData queryUseCustInfoBySn(IData input) throws Exception
    {
        WelfareOfferBean bean = (WelfareOfferBean) BeanManager.createBean(WelfareOfferBean.class);
        return bean.queryUseCustInfoBySn(input);
    }

    /**
     * @Description: 选择产品后做权益提醒
     * @author liwei29
     * @Date: 2020/7/15
     */
    public IData queryWelfareByUpProduct(IData input) throws Exception
    {
        WelfareOfferBean bean = (WelfareOfferBean) BeanManager.createBean(WelfareOfferBean.class);
        return bean.queryWelfareByUpProduct(input);
    }

    /**
     * @Description: 调业务能力运营中心接口测试
     * @author liwei29
     * @Date: 2020/7/15
     */
    public IData queryBusinessCenterTest(IData input) throws Exception
    {
        WelfareOfferBean bean = (WelfareOfferBean) BeanManager.createBean(WelfareOfferBean.class);
        return bean.queryBusinessCenterTest(input);
    }

    /**
     * @Description: 调业务能力运营中心退订接口测试
     * @author liwei29
     * @Date: 2020/7/15
     */
    public IData welFareOrderReturn(IData input) throws Exception
    {
        WelfareOfferBean bean = (WelfareOfferBean) BeanManager.createBean(WelfareOfferBean.class);
        return bean.welFareOrderReturn(input);
    }


}
