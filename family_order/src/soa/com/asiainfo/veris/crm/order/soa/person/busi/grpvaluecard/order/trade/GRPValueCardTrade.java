/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata.GRPValueCardInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata.GRPValueCardReqData;


/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 上午09:34:53
 */
public class GRPValueCardTrade extends BaseTrade implements ITrade
{
	public static Logger logger = Logger.getLogger(GRPValueCardTrade.class);
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        GRPValueCardReqData cardReqData = (GRPValueCardReqData) btd.getRD();
        if ("".equals(cardReqData.getUca().getSerialNumber()))
        {
            MainTradeData mainTd = btd.getMainTradeData();
            mainTd.setUserId(btd.getTradeId());
            mainTd.setCustName(cardReqData.getCustName());
            mainTd.setRsrvStr1(cardReqData.getGroupID());
        }

        stringTableTradeFeeSub(btd);
        stringTableTradeFeeDevice(btd);
        valueCardSale(btd);
    }

    /**
     * 处理tf_b_Tradefee_Device的参数
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void dealTradeFeeDevice(BusiTradeData btd, GRPValueCardInfoReqData cardInfo, String tradeTypeCode, float balance, float totalBalance, String advisePrice, String sysDate) throws Exception
    {
    	GRPValueCardReqData cardReqData = (GRPValueCardReqData) btd.getRD();
        DeviceTradeData deviceTradeData = new DeviceTradeData();

        deviceTradeData.setFeeTypeCode("461"); // 461――流量卡购卡费
        deviceTradeData.setDeviceTypeCode(cardInfo.getResKindCode());
        deviceTradeData.setDeviceNoS(cardInfo.getStartCardNo());
        deviceTradeData.setDeviceNoE(cardInfo.getEndCardNo());
        deviceTradeData.setDeviceNum(String.valueOf(cardInfo.getRowCount()));

        deviceTradeData.setSalePrice(String.valueOf((int) (Double.parseDouble(cardInfo.getTotalPrice()) * 100)));
        deviceTradeData.setRsrvStr2("UNPRINT");
        deviceTradeData.setRsrvStr3(cardInfo.getCardType());
        deviceTradeData.setRsrvStr4(cardInfo.getSingleprice());
        deviceTradeData.setRsrvStr5(cardReqData.getGroupID());
        deviceTradeData.setRsrvStr6(cardReqData.getGroupUserID());
        
        deviceTradeData.setRsrvStr7(cardReqData.getRadio());
        
        if("b".equals(cardReqData.getRadio()) && !"".equals(cardReqData.getSaleprice()))
        {
        	deviceTradeData.setRsrvStr8(cardReqData.getSaleprice());//打折销售价格
        }else
        {
        	deviceTradeData.setRsrvStr8("");
        }
        
        if("b".equals(cardReqData.getRadio()) && !"".equals(cardReqData.getDiscount()))
        {
        	deviceTradeData.setRsrvStr10(cardReqData.getDiscount());//打折折扣率
        }else
        {
        	deviceTradeData.setRsrvStr10("");
        }
        
        deviceTradeData.setDevicePrice(advisePrice);

        deviceTradeData.setRemark(cardReqData.getRemark());

        //资源负责处理后续问题，不用管了
        if ("0".equals(cardInfo.getActiveFlag()))
        {
            deviceTradeData.setRsrvStr9("55560301");
        }
        else
        {
            deviceTradeData.setRsrvStr9("S000V927");
        }

        String resTypeCode ="";
        if(!StringUtils.isBlank(cardInfo.getResKindCode()) && cardInfo.getResKindCode().length()>=3){
            resTypeCode = cardInfo.getResKindCode().substring(0, 3);
        }

        deviceTradeData.setRsrvStr1(resTypeCode);
        // deviceTradeData.setRsrvStr10(tradeTypeCode); // 把业务类型编码记录于此，以便查询、处理问题。以后如少预留字段，可铲掉这个。
        //TF_B_TRADEFEE_DEVICE
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), deviceTradeData);
    }

    private void genUserOther(BusiTradeData btd, String startCardNo, String endCardNo) throws Exception
    {
        OtherTradeData otherTD = new OtherTradeData();

        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setUserId(btd.getRD().getUca().getUserId());
        otherTD.setStartDate(SysDateMgr.addYears(SysDateMgr.END_DATE_FOREVER, -50));
        otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        otherTD.setRsrvValueCode("VCAD");
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setRsrvStr1(startCardNo);
        otherTD.setRsrvStr2(endCardNo);

        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);

    }

    /**
     * 生成tf_b_Tradefee_Device表的数据
     * 
     * @throws Exception
     */
    private void stringTableTradeFeeDevice(BusiTradeData btd) throws Exception
    {
    	GRPValueCardReqData cardReqData = (GRPValueCardReqData) btd.getRD();

        float totalBalance = 0;// 总差价
        String sysDate = SysDateMgr.getSysTime();

        float[] theBalance = new float[cardReqData.getCardlist().size()]; // 分摊到各号段的减免差价，单位：（分）

        for (int i = 0; i < cardReqData.getCardlist().size(); i++)
        {
            GRPValueCardInfoReqData cardInfo = cardReqData.getCardlist().get(i);
            // 拼发票打印内容的卡段信息
            if (i != 0)
                cardReqData.getCardSegment().append(",");
            cardReqData.getCardSegment().append(cardInfo.getStartCardNo());
            if (!cardInfo.getStartCardNo().equals(cardInfo.getEndCardNo()))
            {
                cardReqData.getCardSegment().append("-").append(cardInfo.getEndCardNo());
            }

            // 拼发票打印内容的卡张数
            cardReqData.setCardCount(Integer.valueOf(cardInfo.getRowCount()));


            dealTradeFeeDevice(btd, cardInfo, cardReqData.getTradeTypeCode(), theBalance[i], totalBalance, cardInfo.getAdvisePrice(), sysDate);

        }

    }

    /**
     * 生成tf_b_Tradefee_Device表的数据
     * 
     * @throws Exception
     */
    private void stringTableTradeFeeSub(BusiTradeData btd) throws Exception
    {
        GRPValueCardReqData cardReqData = (GRPValueCardReqData) btd.getRD();
        Float shouldPayFee = new Float(0);// 需要缴纳的费用
        Float deviceTotalFee = new Float(0); // 总面值
        int totalCount = 0;
        for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
        {
            GRPValueCardInfoReqData cardData = (GRPValueCardInfoReqData) iter.next();
            shouldPayFee += Float.valueOf(cardData.getTotalPrice());
            totalCount += cardData.getRowCount();
            deviceTotalFee += Float.valueOf(cardData.getDevicePrice()) * cardData.getRowCount();
        }

        cardReqData.setShouldPayFee(shouldPayFee);
        cardReqData.setDeviceTotalFee(deviceTotalFee);// 总原价
    }


    /**
     * 更改卡状态
     * 
     * @param btd
     * @throws Exception
     */
    public void valueCardSale(BusiTradeData btd) throws Exception
    {
        GRPValueCardReqData cardReqData = (GRPValueCardReqData) btd.getRD();

        IData inputParam = new DataMap();
        inputParam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        inputParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inputParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("STOCK_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        inputParam.put("IN_MODE_CODE", "0");
        inputParam.put("RES_TYPE_CODE", "3"); //  

        String tradeTypeCode = cardReqData.getTradeTypeCode();

        String feeTag = "1";
        long allDevicePrice = 0;
        long allSellFee = 0;
        String resKindcode = "";
        String devicePrice = "";
        String startCardNo = "";
        String endCardNo = "";
        String allCardNo = "";
        for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
        {
            GRPValueCardInfoReqData cardInfoReqData = (GRPValueCardInfoReqData) iter.next();
            startCardNo = cardInfoReqData.getStartCardNo();
            endCardNo = cardInfoReqData.getEndCardNo();
            inputParam.put("RES_NO_S", startCardNo); // 起始卡号
            inputParam.put("RES_NO_E", endCardNo); // 终止卡号
            inputParam.put("SALE_MONEY", (int) (Double.parseDouble(cardInfoReqData.getSingleprice()) * 100));
            feeTag = "3";
            if (logger.isDebugEnabled())
            {
            	logger.debug("===================================.start======================================");
            	logger.debug("-----------huanghua---gprvValueCard01------------:"+inputParam);
            	logger.debug("=====================================.end======================================");
            }
            IDataset resSet = ResCall.updateValueFlowCardInfoIntf(cardInfoReqData.getStartCardNo(), cardInfoReqData.getEndCardNo(), inputParam.getString("SALE_MONEY", "0"), feeTag, "3", CSBizBean.getVisit().getDepartId(), tradeTypeCode, cardReqData.getGroupID());
            if (logger.isDebugEnabled())
            {
            	logger.debug("===================================.start======================================");
            	logger.debug("-----------huanghua---gprvValueCard02------------:"+resSet);
            	logger.debug("=====================================.end======================================");
            }
            if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
            {
                //THROW_C(216201, "调用资源更新接口出错！");
                CSAppException.apperr(CrmUserException.CRM_USER_867);
            }
            
            IData data = new DataMap();
            IDataset flowCard = new DatasetList();
            IDataset flowCardErr = new DatasetList();
            if(startCardNo.equals(endCardNo))
            {
            	data.put("CARDNUM", startCardNo);
            }else
            {
            	data.put("CARDNUM", startCardNo + "-" + endCardNo);
            }
            
            if("".equals(allCardNo))
            {
            	allCardNo = data.getString("CARDNUM");
            }else{
            	allCardNo = allCardNo + "," +data.getString("CARDNUM");
            }
            
           
            data.put("OPERATE", "Activate");
            data.put("GROUPID", cardReqData.getGroupID());
            data.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
            
            
            flowCard = CSAppCall.call("SS.GRPValueCardMgrSVC.trafficcardSales", data);
            
            
            if(IDataUtil.isNotEmpty(flowCard) && "10000".equals(flowCard.getData(0).getString("RspCode")))
            {
            	
            }else if (IDataUtil.isNotEmpty(flowCard) && !"10000".equals(flowCard.getData(0).getString("RspCode"))) {
            	data.put("OPERATE", "Deactivate");
            	data.put("CARDNUM", allCardNo);
            	flowCardErr = CSAppCall.call("SS.GRPValueCardMgrSVC.trafficcardSales", data);
            	CSAppException.apperr(CrmUserException.CRM_USER_783,"调用流量平台失败！错误编码："+flowCard.getData(0).getString("RspCode")+"，错误描述："+flowCard.getData(0).getString("RspDesc") +"，错误卡号："+flowCard.getData(0).getString("FailCardNum") );
			}
            else
            {
            	data.put("OPERATE", "Deactivate");
            	data.put("CARDNUM", allCardNo);
            	flowCardErr = CSAppCall.call("SS.GRPValueCardMgrSVC.trafficcardSales", data);
            	CSAppException.apperr(CrmUserException.CRM_USER_783,"调用流量平台失败！");
            }
            

            allSellFee += (int) (Double.parseDouble(cardInfoReqData.getTotalPrice()) * 100);
            allDevicePrice += cardInfoReqData.getRowCount() * (int) (Double.parseDouble(cardInfoReqData.getDevicePrice()));
            resKindcode = cardInfoReqData.getResKindCode();
            devicePrice = cardInfoReqData.getDevicePrice();
        }
        MainTradeData mainTd = btd.getMainTradeData();
        mainTd.setRsrvStr5(resKindcode);
        mainTd.setRsrvStr6(FeeUtils.Fen2Yuan(devicePrice));
        mainTd.setRsrvStr7(cardReqData.getRadio()); //销售方式
        
        if("b".equals(cardReqData.getRadio()) && !"".equals(cardReqData.getSaleprice()))
        {
        	mainTd.setRsrvStr8(cardReqData.getSaleprice());//打折销售价格
        }else{
        	mainTd.setRsrvStr8("");
        }
        
        if("b".equals(cardReqData.getRadio()) && !"".equals(cardReqData.getDiscount()))
        {
        	mainTd.setRsrvStr10(cardReqData.getDiscount());//打折折扣率
        }else{
        	mainTd.setRsrvStr10("");
        }

        String operFee = btd.getOperFee();
        if(!StringUtils.isBlank(operFee)){
           if(!operFee.equals(String.valueOf(allSellFee))){
               CSAppException.apperr(CrmCommException.CRM_COMM_103,"台帐费用稽核:费用子表中的销售费用和台账表中的数据不符!("+allSellFee+","+operFee+")"); 
           }
        }
        
        mainTd.setRsrvStr9(FeeUtils.Fen2Yuan(String.valueOf(allSellFee)));


    }

}
