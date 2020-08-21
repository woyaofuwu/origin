/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
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
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.ValueCardMgrBean;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardReqData;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 上午09:34:53
 */
public class ValueCardTrade extends BaseTrade implements ITrade
{

	static Logger logger=Logger.getLogger(ValueCardTrade.class);
	
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();
        if ("".equals(cardReqData.getUca().getSerialNumber()))
        {
            MainTradeData mainTd = btd.getMainTradeData();
            mainTd.setUserId(btd.getTradeId());
            mainTd.setCustName(cardReqData.getCustName());
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
    public void dealTradeFeeDevice(BusiTradeData btd, ValueCardInfoReqData cardInfo, String tradeTypeCode, float balance, float totalBalance, String advisePrice, String sysDate) throws Exception
    {
        ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();
        DeviceTradeData deviceTradeData = new DeviceTradeData();

        deviceTradeData.setFeeTypeCode("20"); // 20――有价卡购卡费
        deviceTradeData.setDeviceTypeCode(cardInfo.getResKindCode());
        deviceTradeData.setDeviceNoS(cardInfo.getStartCardNo());
        deviceTradeData.setDeviceNoE(cardInfo.getEndCardNo());
        deviceTradeData.setDeviceNum(String.valueOf(cardInfo.getRowCount()));
        if ("418".equals(tradeTypeCode) || "430".equals(tradeTypeCode) || "420".equals(tradeTypeCode))
        { // 赠送或换卡
            deviceTradeData.setSalePrice("0");
        }
        else if ("416".equals(tradeTypeCode))
        { // 销售

            deviceTradeData.setSalePrice(String.valueOf((int) (Double.parseDouble(cardInfo.getTotalPrice()) * 100)));
            deviceTradeData.setRsrvStr2("UNPRINT");
        }
        else if ("419".equals(tradeTypeCode))
        { // 退卡
            deviceTradeData.setSalePrice(String.valueOf((int) (Double.parseDouble(cardInfo.getTotalPrice()) * 100)));
        }

        deviceTradeData.setDevicePrice(advisePrice);

        deviceTradeData.setRemark(cardReqData.getRemark());

        if ("416".equals(tradeTypeCode) || "418".equals(tradeTypeCode) || "430".equals(tradeTypeCode))
        {//资源负责处理后续问题，不用管了
            if ("0".equals(cardInfo.getActiveFlag()))
            {
                deviceTradeData.setRsrvStr9("55560301");
            }
            else
            {
                deviceTradeData.setRsrvStr9("S000V927");
            }
        }
        else if ("420".equals(tradeTypeCode))
        {//资源负责处理后续问题，不用管了
           /* IData activeInfo = new DataMap(cardInfo.getActivateInfo());
            if ("0".equals(activeInfo.getString("ACTIVE_FLAG")) && "1".equals(activeInfo.getString("OLD_ACTIVE_FLAG")))
            {
                deviceTradeData.setRsrvStr9("55560301");
                deviceTradeData.setRsrvStr10("3");
            }
            else if ("0".equals(activeInfo.getString("ACTIVE_FLAG")) && "2".equals(activeInfo.getString("OLD_ACTIVE_FLAG")))
            {
                deviceTradeData.setRsrvStr9("55560301");
                deviceTradeData.setRsrvStr10("1");
            }
            else if ("3".equals(activeInfo.getString("OLD_ACTIVE_FLAG")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_171);
            }*/

        }
        else if ("419".equals(tradeTypeCode))
        {//资源负责处理后续问题，不用管了
            deviceTradeData.setRsrvStr9("55560301");
        }
        if ("418".equals(tradeTypeCode))
        {
            deviceTradeData.setRemark("拆分" + cardReqData.getAuditStaffId());
            /**
             * 说明：
             *   当页面上和excel中都存在客户号码时,以表格中的客户号码为主.
             *   放入客户号码到rsrvStr7
             * 20160616
             */
	 		   if("true".equals(cardInfo.getImportFlag())){
	 			   //批量导入
    	 		   //客户号码
    	 		  deviceTradeData.setRsrvStr7(cardInfo.getCustomerNo());
	 		   }else{
    	 		   //客户号码
    	 		  deviceTradeData.setRsrvStr7(cardReqData.getCustomerNumber());
	 		   }
/*            if(!"".equals(cardInfo.getImportCustomerNumber())||cardInfo.getImportCustomerNumber() != null){
            	//上传的excel中有客户号码
            	deviceTradeData.setRsrvStr7(cardInfo.getImportCustomerNumber());
            }*/
            
        }
        String resTypeCode ="";
        if(!StringUtils.isBlank(cardInfo.getResKindCode()) && cardInfo.getResKindCode().length()>=3){
            resTypeCode = cardInfo.getResKindCode().substring(0, 3);
        }

        deviceTradeData.setRsrvStr1(resTypeCode);
        // deviceTradeData.setRsrvStr10(tradeTypeCode); // 把业务类型编码记录于此，以便查询、处理问题。以后如少预留字段，可铲掉这个。
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), deviceTradeData);
        
        //记录有价卡赠送清单信息
        if("418".equals(cardReqData.getTradeTypeCode())){
        	//清单信息
        	giveValueCardDetailed(btd, cardInfo);
        }
        
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
        ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();
        
        float totalBalance = 0;// 总差价
        String sysDate = SysDateMgr.getSysTime();

        float[] theBalance = new float[cardReqData.getCardlist().size()]; // 分摊到各号段的减免差价，单位：（分）

        for (int i = 0; i < cardReqData.getCardlist().size(); i++)
        {
            ValueCardInfoReqData cardInfo = cardReqData.getCardlist().get(i);

            if ("416".equals(cardReqData.getTradeTypeCode()) || "418".equals(cardReqData.getTradeTypeCode()))
            {

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

            }
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
        ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();
        Float shouldPayFee = new Float(0);// 需要缴纳的费用
        Float deviceTotalFee = new Float(0); // 总面值
        int totalCount = 0;
        for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
        {
            ValueCardInfoReqData cardData = (ValueCardInfoReqData) iter.next();
            if ("416".equals(cardReqData.getTradeTypeCode()) || "419".equals(cardReqData.getTradeTypeCode()))
            { // 总额，折后的
                shouldPayFee += Float.valueOf(cardData.getTotalPrice());

                totalCount += cardData.getRowCount();
                if ("416".equals(cardReqData.getTradeTypeCode()))
                { //
                    deviceTotalFee += Float.valueOf(cardData.getDevicePrice()) * cardData.getRowCount();
                }
            }

        }
        cardReqData.setShouldPayFee(shouldPayFee);
        cardReqData.setDeviceTotalFee(deviceTotalFee);// 总原价
    }

    private void updateUserOther(String userId, String month, String vpmnFee) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("MONTH", month);
        param.put("VPMN_FEE", vpmnFee);

        String sql = "UPDATE tf_f_user_other SET rsrv_str2 = rsrv_str2+:VPMN_FEE" + " WHERE user_id = TO_NUMBER(:USER_ID) AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND rsrv_value_code = 'VCAD' AND rsrv_value =:MONTH "
                + " AND SYSDATE BETWEEN start_date AND end_date+0";
        int reset = Dao.executeUpdate(new StringBuilder(sql), param);
        if (reset < 1)
        {
            // common.error("更新用户可兑换金额出错！");
        }
    }

    /**
     * 更改卡状态
     * 
     * @param btd
     * @throws Exception
     */
    public void valueCardSale(BusiTradeData btd) throws Exception
    {
        ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();

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

        // 有价卡销售和赠送
        if (cardReqData.getTradeTypeCode().equals("416") || cardReqData.getTradeTypeCode().equals("418") || cardReqData.getTradeTypeCode().equals("430"))
        {
            String feeTag = "1";
            long allDevicePrice = 0;
            long allSellFee = 0;
            String resKindcode = "";
            String devicePrice = "";
            for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
            {
                ValueCardInfoReqData cardInfoReqData = (ValueCardInfoReqData) iter.next();
                inputParam.put("RES_NO_S", cardInfoReqData.getStartCardNo()); // 起始卡号
                inputParam.put("RES_NO_E", cardInfoReqData.getEndCardNo()); // 终止卡号
                if (cardReqData.getTradeTypeCode().equals("418") || cardReqData.getTradeTypeCode().equals("430")) // 赠送
                {
                    inputParam.put("SALE_MONEY", "0");
                    feeTag = "1";
                }
                else
                {
                    inputParam.put("SALE_MONEY", (int) (Double.parseDouble(cardInfoReqData.getSingleprice()) * 100));
                    feeTag = "3";
                }

                IDataset resSet = ResCall.updateValueCardInfoIntf(cardInfoReqData.getStartCardNo(), cardInfoReqData.getEndCardNo(), inputParam.getString("SALE_MONEY", "0"), feeTag, "3", CSBizBean.getVisit().getDepartId(), tradeTypeCode);

                if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
                {
                    // THROW_C(216201, "调用资源更新接口出错！");
                    CSAppException.apperr(CrmUserException.CRM_USER_867);
                }

                allSellFee += (int) (Double.parseDouble(cardInfoReqData.getTotalPrice()) * 100);
                allDevicePrice += cardInfoReqData.getRowCount() * (int) (Double.parseDouble(cardInfoReqData.getDevicePrice()));
                resKindcode = cardInfoReqData.getResKindCode();
                devicePrice = cardInfoReqData.getDevicePrice();
                if (cardReqData.getTradeTypeCode().equals("430"))
                {
                    this.genUserOther(btd, cardInfoReqData.getStartCardNo(), cardInfoReqData.getEndCardNo());
                }
            }
            MainTradeData mainTd = btd.getMainTradeData();
            mainTd.setRsrvStr5(resKindcode);
            mainTd.setRsrvStr6(FeeUtils.Fen2Yuan(devicePrice));
            
            String operFee = btd.getOperFee();
            if(!StringUtils.isBlank(operFee)){
               if(!operFee.equals(String.valueOf(allSellFee))){
                   CSAppException.apperr(CrmCommException.CRM_COMM_103,"台帐费用稽核:费用子表中的销售费用和台账表中的数据不符!("+allSellFee+","+operFee+")"); 
               }
            }
            
            mainTd.setRsrvStr9(FeeUtils.Fen2Yuan(String.valueOf(allSellFee)));

            if (cardReqData.getTradeTypeCode().equals("430"))
            {
                mainTd.setRsrvStr9(FeeUtils.Fen2Yuan(String.valueOf(allDevicePrice)));
                mainTd.setRsrvStr10(cardReqData.getGiveMonth());
                this.updateUserOther(cardReqData.getUca().getUserId(), cardReqData.getGiveMonth(), String.valueOf(allDevicePrice / 100));
            }
        }
        else if (cardReqData.getTradeTypeCode().equals("419")) // 退卡
        {
            long allSellFee = 0;
            String resKindcode = "";
            String devicePrice = "";
            for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
            {
                ValueCardInfoReqData cardInfoReqData = (ValueCardInfoReqData) iter.next();
                inputParam.put("RES_NO_S", cardInfoReqData.getStartCardNo()); // 起始卡号
                inputParam.put("RES_NO_E", cardInfoReqData.getEndCardNo()); // 终止卡号

                IDataset resSet = ResCall.updateValueCardReturnInfoIntf(cardInfoReqData.getStartCardNo(), cardInfoReqData.getEndCardNo(), "3", CSBizBean.getVisit().getDepartId(), "0", null, null, null, null, "419");
                if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
                {
                    // THROW_C(216201, "调用资源更新接口出错！");
                    CSAppException.apperr(CrmUserException.CRM_USER_867);
                }
                allSellFee += (int) (Double.parseDouble(cardInfoReqData.getTotalPrice()) * 100);
                resKindcode = cardInfoReqData.getResKindCode();
                devicePrice = cardInfoReqData.getDevicePrice();
            }

            MainTradeData mainTd = btd.getMainTradeData();
            mainTd.setRsrvStr5(resKindcode);
            mainTd.setRsrvStr6(FeeUtils.Fen2Yuan(devicePrice));
            mainTd.setRsrvStr9(FeeUtils.Fen2Yuan(String.valueOf(allSellFee)));
            
            /**
             * REQ201802260006_有价卡退卡增加付款方式选项的优化
             * <by/>
             * 记录付费方式
             * @author zhuoyingzhi
             * @date 20180428
             */
            String  backPayMoneyCode=btd.getRD().getPageRequestData().getString("BACK_PAY_MONEY_CODE", "");
            mainTd.setRsrvStr10(backPayMoneyCode);
            /*****************************************/
            
        }
        else if (cardReqData.getTradeTypeCode().equals("420")) // 换卡
        {
            String resKindcode = "";
            String devicePrice = "";
            for (Iterator iter = cardReqData.getCardlist().iterator(); iter.hasNext();)
            {
                ValueCardInfoReqData cardInfoReqData = (ValueCardInfoReqData) iter.next();

                IDataset resSet = ResCall.changeValuecardReturnInfo(cardInfoReqData.getStartCardNo(), cardInfoReqData.getStartCardNo(), "3", CSBizBean.getVisit().getDepartId(), tradeTypeCode);

                if (resSet == null || resSet.size() == 0 || resSet.getData(0).getInt("X_RESULTCODE") != 0)
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_236);
                }

                String saleMoney =String.valueOf((int) (Double.parseDouble(cardInfoReqData.getSingleprice()) * 100));
                IDataset newSet = ResCall.updateValueCardInfoIntf(cardInfoReqData.getEndCardNo(), cardInfoReqData.getEndCardNo(), saleMoney, "0", "3", CSBizBean.getVisit().getDepartId(), tradeTypeCode);
                if (newSet == null || newSet.size() == 0 || resSet.getData(0).getInt("X_RESULTCODE") != 0)
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_237);
                }
                resKindcode = cardInfoReqData.getResKindCode();
                devicePrice = cardInfoReqData.getDevicePrice();
            }

            MainTradeData mainTd = btd.getMainTradeData();
            mainTd.setRsrvStr5(resKindcode);
            mainTd.setRsrvStr6(FeeUtils.Fen2Yuan(devicePrice));
        }

    }
    
    /**
     * 
     * @param btd
     * @param cardInfo
     * @throws Exception
     */
    public void giveValueCardDetailed(BusiTradeData btd, ValueCardInfoReqData cardInfo) throws Exception
    {
    	try {
            ValueCardReqData cardReqData = (ValueCardReqData) btd.getRD();
            //有价卡赠送集合
            IDataset addDetailed=new DatasetList();
            int rowCount=cardInfo.getRowCount();
            ValueCardMgrBean bean = (ValueCardMgrBean) BeanManager.createBean(ValueCardMgrBean.class);
            if(rowCount > 1){
            	String cardNumber=cardInfo.getStartCardNo();
	           	 for(int i=0;i<rowCount;i++){
	           		   IData data=new DataMap();
	           		   bean.initValueCardDetailedInfo(btd,cardInfo, data, cardReqData,String.valueOf((Long.valueOf(cardNumber)+i)));
	           		   addDetailed.add(data);
	           	 }
            }else{
     	   	   IData data=new DataMap();
    	   	   bean.initValueCardDetailedInfo(btd,cardInfo, data, cardReqData,cardInfo.getStartCardNo());
    	   	   addDetailed.add(data);
            }
	   	   Dao.insert("TL_B_VALUECARD_DETAILED", addDetailed);
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }   

}
