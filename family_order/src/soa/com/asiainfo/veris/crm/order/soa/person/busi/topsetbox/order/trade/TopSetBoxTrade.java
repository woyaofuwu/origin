
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.requestdata.TopSetBoxRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 上午9:38:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class TopSetBoxTrade extends BaseTrade implements ITrade
{

    /**
     * @Function: createAddrTradeData()
     * @Description: TF_B_TRADE_WIDENET 地址信息台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午5:25:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createAddrTradeData(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
    	String tradeId = tsbReqData.getWideTradeId();
    	String standAddrCode = "" ;
    	String standAddr = "" ;
    	String contact = "" ;
    	String contactPhone = "" ;
		if (StringUtils.isNotBlank(tradeId))
		{
			// 1.在途工单直接查询宽带地址信息
			IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
			if (IDataUtil.isNotEmpty(addrTD))
			{
				standAddrCode = addrTD.first().getString("STAND_ADDRESS_CODE");
				standAddr = addrTD.first().getString("STAND_ADDRESS");
				contact = addrTD.first().getString("CONTACT");
				contactPhone = addrTD.first().getString("CONTACT_PHONE");
			}
		}
		else
		{
			// 2.已经是宽带用户，从用户资料表查询
			IDataset wideNetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + tsbReqData.getSerialNumber());
			if (IDataUtil.isNotEmpty(wideNetInfos))
			{
				standAddrCode = wideNetInfos.first().getString("STAND_ADDRESS_CODE");
				standAddr = wideNetInfos.first().getString("STAND_ADDRESS");
				contact = wideNetInfos.first().getString("CONTACT");
				contactPhone = wideNetInfos.first().getString("CONTACT_PHONE");
			}
		}
		WideNetTradeData widenetTD = new WideNetTradeData();
		widenetTD.setUserId(tsbReqData.getUca().getUserId());
		widenetTD.setStandAddressCode(standAddrCode);
		widenetTD.setStandAddress(StringUtils.isBlank(standAddr) ? tsbReqData.getWideAddr() : standAddr);
		widenetTD.setDetailAddress(tsbReqData.getWideAddr());
		widenetTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		widenetTD.setInstId(SeqMgr.getInstId());
		widenetTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		widenetTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
		widenetTD.setContact(StringUtils.isBlank(contact) ? tsbReqData.getUca().getCustomer().getCustName() : contact);
		widenetTD.setContactPhone(StringUtils.isBlank(contactPhone) ? tsbReqData.getSerialNumber() : contactPhone);
		btd.add(tsbReqData.getSerialNumber(), widenetTD);
    }

    /**
     * @Function: createBaseDiscntPlatSVCTradeData()
     * @Description: 基础优惠平台服务
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午2:51:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createBaseDiscntPlatSVCTradeData(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
        String userAction = tsbReqData.getUserAction();
        if (StringUtils.equals("0", userAction))
        {
            this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, tsbReqData.getBasePkgs(), BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
            String basePkgs = tsbReqData.getBasePkgs();
            String oldBasePkgs = tsbReqData.getOldBasePkgs().split(",")[0];
            String oldResNo = tsbReqData.getOldResNo();
            String resNo = tsbReqData.getResNo();
            // 1.套餐更换
            if (!StringUtils.equals(basePkgs, oldBasePkgs))
            {
            	 UcaData ucaData = tsbReqData.getUca();
            	 String user_id = ucaData.getUserId();
            	 IDataset userDiscnts = UserDiscntInfoQry.getHLWDDiscntByUserId(user_id);
                 if (IDataUtil.isNotEmpty(userDiscnts))
                 {
                     //CRM_COMM_103
                	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠期内不能进行套餐更换!");
                 }
                // 订购
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, basePkgs, BofConst.MODIFY_TAG_ADD);
                // 退订
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldBasePkgs, BofConst.MODIFY_TAG_DEL);
            }
            // 2.机顶盒更换，套餐不换
            else if (!StringUtils.equals(oldResNo, resNo) && StringUtils.equals(basePkgs, oldBasePkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_USER_DATA_MODIFY, basePkgs, BofConst.MODIFY_TAG_UPD);
            }
        }
        
        
       /* if (StringUtils.equals("0", userAction))
        {
            this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, tsbReqData.getBasePkgs(), BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
            String basePkgs = tsbReqData.getBasePkgs();
            String oldBasePkgs = tsbReqData.getOldBasePkgs().split(",")[0];
            String oldResNo = tsbReqData.getOldResNo();
            String resNo = tsbReqData.getResNo();
            // 1.套餐更换
            if (!StringUtils.equals(basePkgs, oldBasePkgs))
            {
            	 UcaData ucaData = tsbReqData.getUca();
            	 String user_id = ucaData.getUserId();
            	 IDataset userDiscnts = UserDiscntInfoQry.getHLWDDiscntByUserId(user_id);
                 if (IDataUtil.isNotEmpty(userDiscnts))
                 {
                     //CRM_COMM_103
                	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠期内不能进行套餐更换!");
                 }
                // 订购
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, basePkgs, BofConst.MODIFY_TAG_ADD);
                // 退订
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldBasePkgs, BofConst.MODIFY_TAG_DEL);
            }
            // 2.机顶盒更换，套餐不换
            else if (!StringUtils.equals(oldResNo, resNo) && StringUtils.equals(basePkgs, oldBasePkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_USER_DATA_MODIFY, basePkgs, BofConst.MODIFY_TAG_UPD);
            }
        }*/
    }
    
    
    /**
     * @Function: createBaseDiscntPlatSVCTradeData()
     * @Description: 基础优惠平台服务
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午2:51:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createBaseDiscntPlatSVCTradeDataForChangeproduct(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
        String userAction = tsbReqData.getUserAction();
        if (StringUtils.equals("0", userAction))
        {
            this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, tsbReqData.getBasePkgs(), BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
            String basePkgs = tsbReqData.getBasePkgs();
            String oldBasePkgs = tsbReqData.getOldBasePkgs().split(",")[0];
            // 1.套餐更换
            if (!StringUtils.equals(basePkgs, oldBasePkgs))
            {
            	 UcaData ucaData = tsbReqData.getUca();
            	 String user_id = ucaData.getUserId();
            	 IDataset userDiscnts = UserDiscntInfoQry.getHLWDDiscntByUserId(user_id);
                 if (IDataUtil.isNotEmpty(userDiscnts))
                 {
                     //CRM_COMM_103
                	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠期内不能进行套餐更换!");
                 }
                // 订购
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, basePkgs, BofConst.MODIFY_TAG_ADD);
                // 退订
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldBasePkgs, BofConst.MODIFY_TAG_DEL);
            }
        }
    }

    
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        TopSetBoxRequestData tsbReqData = (TopSetBoxRequestData) btd.getRD();
        
        IData pageParam=btd.getRD().getPageRequestData();
        if(IDataUtil.isNotEmpty(pageParam)){
        	String interTvSource=pageParam.getString("INTERNET_TV_SOURCE","");
        	
        	if(interTvSource!=null&&!interTvSource.trim().equals("")){
        		if(interTvSource.equals("OPEN_TOPSETBOX")){		//开户
        			
        			UcaData uca=btd.getRD().getUca();
        			
        			/*
        			 * 用户是否免费，如果免费就不收取押金
        			 */
        			String isHasSaleActive=tsbReqData.getIsHasSaleActive();
        			
        			//宽带开户时已经扣除了押金，不再重复扣除
        			if (!"1".equals(tsbReqData.getIsMergeWideUserCreate()))
        			{
        			if(isHasSaleActive.equals("0")){	//如果不免费
        				//获取费用信息
            			String money="20000";
            			IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
            			if(IDataUtil.isNotEmpty(moneyDatas)){
            				money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
            			}
            			
            			//start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            	        IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", tsbReqData.getProductId(), "0898");
            	        if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
            	        {
            	        	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            	        	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
            	        		if(PARA_CODE2.equals("IPTV")){
            	        			money="10000";
            	        		}
            	        	}
            	        }
            	        //end
            			
            			//获取转账存折
            			StringBuffer depositeNotes=new StringBuffer();
            			IDataset noteDatas=CommparaInfoQry.getCommNetInfo("CSM", "1627", "TOP_SET_BOX_NOTES");
            			if(IDataUtil.isNotEmpty(noteDatas)){
            				
            				for(int i=0,size=noteDatas.size();i<size;i++){
            					IData noteData=noteDatas.getData(i);
            					
            					depositeNotes.append(noteData.getString("PARA_CODE1"));
            					if(i<size-1){
            						depositeNotes.append("|");
            					}
            				}
            			}else{
            				depositeNotes.append("0");
            				depositeNotes.append("|");
            				depositeNotes.append("1");
            				depositeNotes.append("|");
            				depositeNotes.append("145");
            				depositeNotes.append("|");
            				depositeNotes.append("184");
            				depositeNotes.append("|");
            				depositeNotes.append("185");
            				depositeNotes.append("|");
            				depositeNotes.append("200");
            				depositeNotes.append("|");
            				depositeNotes.append("223");
            				depositeNotes.append("|");
            				depositeNotes.append("400");
            				depositeNotes.append("|");
            				depositeNotes.append("48");
            				depositeNotes.append("|");
            				depositeNotes.append("58");
            				depositeNotes.append("|");
            				depositeNotes.append("604");
            				depositeNotes.append("|");
            				depositeNotes.append("605");
            				depositeNotes.append("|");
            				depositeNotes.append("606");
            				depositeNotes.append("|");
            				depositeNotes.append("771");
            				depositeNotes.append("|");
            				depositeNotes.append("773");
            			}
            			
            			//调用账务的接口进行押金返回
    					IData params=new DataMap(); 
    					params.put("SERIAL_NUMBER", uca.getSerialNumber());
    					params.put("TRADE_FEE", money);
    					params.put("CHANNEL_ID", "15000");
    					params.put("TRADE_ID", btd.getRD().getTradeId());
    					params.put("OUT_DEPOSIT_CODE", depositeNotes.toString());
    					params.put("PAYMENT_ID", "15");
    			   		params.put("PAY_FEE_MODE_CODE", "9016"); 
    			   		params.put("ACTION_CODE", ""); 
    			   		params.put("DEPOSIT_CODE", "9016");
    			   		//调用接口，将【现金】——>【押金】
    					AcctCall.depositeDeduct(params);
                        }
        			}
        			
        			this.updateMainTradeData(btd, tsbReqData);
        			
        			// 购机
                    this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
                    // 地址台帐
                    this.createAddrTradeData(btd, tsbReqData);
                    
                    this.createBaseDiscntPlatSVCTradeData(btd, tsbReqData);
                    this.createOptionDiscntPlatSVCTradeData(btd, tsbReqData);
        			
        		}
//        		else if(interTvSource.equals("CHANGE_TOPSETBOX")){		//换机顶盒
//        			
//        			this.updateMainTradeData(btd, tsbReqData);
//        			
//        			this.createAddrTradeData(btd, tsbReqData);
//        			
//        			// 换机
//                    this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
//                    this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
//                    
//                    //做一笔发送平台的变更服务的指令，用于促使机顶盒变更
//                    String oldBasePkgs = tsbReqData.getOldBasePkgs().split(",")[0];
//                    this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_USER_DATA_MODIFY, 
//                    		oldBasePkgs, BofConst.MODIFY_TAG_UPD);
//                    
//                    
//        		}
        		else{	//原来的处理方式
        			//保留原来的方式
                	defaultDeal(btd);
        		}
        		
        	}else{
        		//保留原来的方式
            	defaultDeal(btd);
        	}      	
        }else{
        	//保留原来的方式
        	defaultDeal(btd);
        }
        
    }
    
    private void defaultDeal(BusiTradeData btd)throws Exception{
    	TopSetBoxRequestData tsbReqData = (TopSetBoxRequestData) btd.getRD();
    	
    	this.updateMainTradeData(btd, tsbReqData);
        String userAction = tsbReqData.getUserAction();
        if (StringUtils.equals("0", userAction))
        {
            // 购机
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
            // 地址台帐
            if (StringUtils.equals("1", tsbReqData.getArtificalSericesTag()))
            {
                this.createAddrTradeData(btd, tsbReqData);
            }
        }
        else
        {
            // 换机
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
        }
        this.createBaseDiscntPlatSVCTradeData(btd, tsbReqData);
        this.createOptionDiscntPlatSVCTradeData(btd, tsbReqData);
    }
    

    /**
     * @Function: createOptionDiscntPlatSVCTradeData()
     * @Description: 可选优惠平台服务
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午4:25:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createOptionDiscntPlatSVCTradeData(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
        String userAction = tsbReqData.getUserAction();
        if (StringUtils.equals("0", userAction) && StringUtils.isNotBlank(tsbReqData.getOptionPkgs()))
        {
            this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, tsbReqData.getOptionPkgs(), BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
        	String oldOptionPkgs = tsbReqData.getOldOptionPkgs().split(",")[0];
            String optionPkgs = tsbReqData.getOptionPkgs();
            if(!StringUtils.equals(optionPkgs, oldOptionPkgs)&&!("-1".equals(oldOptionPkgs)&&"".equals(optionPkgs))){
	       	 	UcaData ucaData = tsbReqData.getUca();
	       	 	String user_id = ucaData.getUserId();
	       	 	IDataset userDiscnts = UserDiscntInfoQry.getHLWDDiscntByUserId(user_id);
	            if (IDataUtil.isNotEmpty(userDiscnts))
	            {
	              //CRM_COMM_103
	           	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠期内不能进行套餐更换!");
	            }
            }
            // 1.未订购优惠平台服务,并且选了优惠平台服务
            if (StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isNotBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, optionPkgs, BofConst.MODIFY_TAG_ADD);
            }
            // 2.已定购，选了
            else if (!StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isNotBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, optionPkgs, BofConst.MODIFY_TAG_ADD);
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldOptionPkgs, BofConst.MODIFY_TAG_DEL);
            }
            // 3.已定购，未选
            else if (!StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldOptionPkgs, BofConst.MODIFY_TAG_DEL);
            }
        }
        
        
        /*if (StringUtils.equals("0", userAction) && StringUtils.isNotBlank(tsbReqData.getOptionPkgs()))
        {
            this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, tsbReqData.getOptionPkgs(), BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
        	String oldOptionPkgs = tsbReqData.getOldOptionPkgs().split(",")[0];
            String optionPkgs = tsbReqData.getOptionPkgs();
            if(!StringUtils.equals(optionPkgs, oldOptionPkgs)&&!("-1".equals(oldOptionPkgs)&&"".equals(optionPkgs))){
	       	 	UcaData ucaData = tsbReqData.getUca();
	       	 	String user_id = ucaData.getUserId();
	       	 	IDataset userDiscnts = UserDiscntInfoQry.getHLWDDiscntByUserId(user_id);
	            if (IDataUtil.isNotEmpty(userDiscnts))
	            {
	              //CRM_COMM_103
	           	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠期内不能进行套餐更换!");
	            }
            }
            // 1.未订购优惠平台服务,并且选了优惠平台服务
            if (StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isNotBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, optionPkgs, BofConst.MODIFY_TAG_ADD);
            }
            // 2.已定购，选了
            else if (!StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isNotBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_ORDER, optionPkgs, BofConst.MODIFY_TAG_ADD);
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldOptionPkgs, BofConst.MODIFY_TAG_DEL);
            }
            // 3.已定购，未选
            else if (!StringUtils.equals("-1", oldOptionPkgs) && StringUtils.isBlank(optionPkgs))
            {
                this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_CANCEL_ORDER, oldOptionPkgs, BofConst.MODIFY_TAG_DEL);
            }
        }*/
    }

    /**
     * @Function: createPlatSVCAndAttr()
     * @Description: 平台服务和属性原子台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午2:22:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createPlatSVCAndAttr(BusiTradeData btd, TopSetBoxRequestData tsbReqData, String operCode, String serviceId, String modifyTag) throws Exception
    {
        List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
        // 平台服务
        IData platParam = new DataMap();
        platParam.put("SERVICE_ID", serviceId);
        platParam.put("OPER_CODE", operCode);
        platParam.put("OPR_SOURCE", "08");
        PlatSvcData psd = new PlatSvcData(platParam);
        // 属性 1.宽带地址
        List<AttrData> attrs = new ArrayList<AttrData>();
        AttrData addrAttr = new AttrData();
        addrAttr.setAttrCode("ADDRESS");
        addrAttr.setAttrValue(tsbReqData.getWideAddr());
        addrAttr.setModifyTag(modifyTag);
        attrs.add(addrAttr);
        //start-wangsc10-20181127
        // CITY_CODE 添加业务区
        String citycode = "";
		IDataset wideNetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + tsbReqData.getSerialNumber());
		if (IDataUtil.isNotEmpty(wideNetInfos))
		{
			citycode = wideNetInfos.first().getString("RSRV_STR4");
		}
		if(null==citycode || "".equals(citycode))
		{
			citycode = tsbReqData.getIptvCityCode();
		}
        AttrData citycodeAttr = new AttrData();
        citycodeAttr.setAttrCode("CityCode");
        citycodeAttr.setAttrValue(citycode);
        citycodeAttr.setModifyTag(modifyTag);
        attrs.add(citycodeAttr);
        //end
        // 2.宽带帐号
        AttrData brandIDAttr = new AttrData();
        brandIDAttr.setAttrCode("BROADBANDID");
        brandIDAttr.setAttrValue("KD_" + tsbReqData.getSerialNumber());
        brandIDAttr.setModifyTag(modifyTag);
        attrs.add(brandIDAttr);
        // 3.旧终端号
        AttrData oldTSBIDAttr = new AttrData();
        oldTSBIDAttr.setAttrCode("OLDSTBID");
        oldTSBIDAttr.setAttrValue(tsbReqData.getOldResNo());
        oldTSBIDAttr.setModifyTag(modifyTag);
        attrs.add(oldTSBIDAttr);
        // 4.新终端号
        AttrData stbIdAttr = new AttrData();
        stbIdAttr.setAttrCode("STBID");
        stbIdAttr.setAttrValue(tsbReqData.getResNo());
        stbIdAttr.setModifyTag(modifyTag);
        attrs.add(stbIdAttr);
        
        /**
         * REQ201609010007_对携号转网客户开放办理魔百和业务
         * @author zhuoyingzhi
         * 20161124
         * 添加邮政编码
         */
        // 5.邮政编码
        AttrData zipCodeAttr = new AttrData();
        String zipCode=StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
        		new String[]{"TYPE_ID","EPARCHY_CODE","DATA_ID"}, "REMARK",
        		new String[]{"ZIPCODE","0898",getVisit().getCityCode()});
       
	    zipCodeAttr.setAttrCode("ZIPCODE");
	    zipCodeAttr.setAttrValue(zipCode);
	    zipCodeAttr.setModifyTag(modifyTag);
        attrs.add(zipCodeAttr);
        /************************end***********************************/
        if (!StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL))
        {
            psd.setAttrs(attrs);
        }
        bindPlatSvcs.add(psd);
        ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, btd.getRD().getUca(), btd);
    }
    

    /**
     * @Function: createResTradeData()
     * @Description: 终端台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午10:19:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createResTradeData(BusiTradeData btd, TopSetBoxRequestData tsbReqData, String modifyTag) throws Exception
    {
        IData oldResInfo = new DataMap();
        ResTradeData resTD = new ResTradeData();
        if (StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag))
        {
            IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(tsbReqData.getUca().getUserId(), "4", "J");
            oldResInfo = boxInfos.first();
        }
        boolean isOld = StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag);
        resTD.setUserId(tsbReqData.getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode(isOld ? oldResInfo.getString("RES_TYPE_CODE") : tsbReqData.getResTypeCode()); // 终端类型编码
        resTD.setResCode(isOld ? oldResInfo.getString("RES_CODE") : tsbReqData.getResKindCode()); // 终端型号编码
        resTD.setImsi(isOld ? oldResInfo.getString("IMSI") : tsbReqData.getResNo()); // 终端编码
        resTD.setKi(isOld ? oldResInfo.getString("KI") : tsbReqData.getSupplyId()); // 厂商编码
        resTD.setInstId(isOld ? oldResInfo.getString("INST_ID") : SeqMgr.getInstId());
        resTD.setStartDate(isOld ? oldResInfo.getString("START_DATE") : SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        resTD.setEndDate(isOld ? SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND) : SysDateMgr.END_DATE_FOREVER);
        resTD.setModifyTag(modifyTag);
        resTD.setRemark(tsbReqData.getRemark());
        resTD.setRsrvNum1(tsbReqData.getArtificalSericesTag()); // 上门标记
        resTD.setRsrvTag2("1");	//新业务规则标识
        resTD.setRsrvNum5(tsbReqData.getResFee());
        resTD.setRsrvStr1(tsbReqData.getProductId());
        resTD.setRsrvStr2(tsbReqData.getBasePkgs() + "," + tsbReqData.getProductId());
        if (StringUtils.isNotBlank(tsbReqData.getOptionPkgs()))
        {
            resTD.setRsrvStr3(tsbReqData.getOptionPkgs() + "," + tsbReqData.getProductId());
        }
        else
        {
            resTD.setRsrvStr3("-1" + "," + tsbReqData.getProductId());
        }
        resTD.setRsrvStr4(tsbReqData.getResBrandCode() + "," + tsbReqData.getResBrandName());
        resTD.setRsrvStr5(tsbReqData.getWideAddr());
        resTD.setRsrvTag1("J"); // 机顶盒标志
        
        //如果是新增就封装进货价格
        if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag)){
        	resTD.setRsrvNum4(btd.getRD().getPageRequestData().getString("DEVICE_COST", "0"));
        }
        
        
        /*
         * 如果收取押金
         */
      //BUS201907310012关于开发家庭终端调测费的需求
/*        String isHasSaleActive=tsbReqData.getIsHasSaleActive();
        if(isHasSaleActive.equals("0")){
        	//获取费用信息
    		String money="20000";
    		IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
    		if(IDataUtil.isNotEmpty(moneyDatas)){
    			money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
    		}
    		
    		//start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
	        IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", tsbReqData.getProductId(), "0898");
	        if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
	        {
	        	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
	        	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
	        		if(PARA_CODE2.equals("IPTV")){
	        			money="10000";
	        		}
	        	}
	        }
	        //end
    		int fee=Integer.parseInt(money);
    		resTD.setRsrvNum2(String.valueOf(fee/100));
            
            
        }*/
		resTD.setRsrvNum2("0");
		//BUS201907310012关于开发家庭终端调测费的需求
        btd.add(tsbReqData.getUca().getSerialNumber(), resTD);
        
    }
    
    
    private void createResTradeDataForChangeProduct(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
    	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
        IData oldResInfo = boxInfos.first();
        ResTradeData resTD = new ResTradeData(oldResInfo);
        resTD.setRsrvStr1(tsbReqData.getProductId());
        resTD.setRsrvStr2(tsbReqData.getBasePkgs() + "," + tsbReqData.getProductId());
        if (StringUtils.isNotBlank(tsbReqData.getOptionPkgs()))
        {
            resTD.setRsrvStr3(tsbReqData.getOptionPkgs() + "," + tsbReqData.getProductId());
        }
        else
        {
            resTD.setRsrvStr3("-1" + "," + tsbReqData.getProductId());
        }
        resTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        
        
        btd.add(tsbReqData.getUca().getSerialNumber(), resTD);
    }
    
    
    /**
     * @Function: createResTradeData()
     * @Description: 终端台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午10:19:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createResTradeDataForChange(BusiTradeData btd, TopSetBoxRequestData tsbReqData, String modifyTag) throws Exception
    {
    	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
    	IData oldResInfo = boxInfos.first();
    	
    	ResTradeData resTD =null;
    	
    	if(StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag)){	//新增
    		resTD =new ResTradeData();
    		
            resTD.setUserId(tsbReqData.getUca().getUserId());
            resTD.setUserIdA("-1");
            resTD.setResTypeCode(tsbReqData.getResTypeCode()); // 终端类型编码
            resTD.setResCode(tsbReqData.getResKindCode()); // 终端型号编码
            resTD.setImsi(tsbReqData.getResNo()); // 终端编码
            resTD.setKi(tsbReqData.getSupplyId()); // 厂商编码
            resTD.setInstId(SeqMgr.getInstId());
            resTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            resTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
            resTD.setModifyTag(modifyTag);
            resTD.setRemark(tsbReqData.getRemark());
            resTD.setRsrvNum1(tsbReqData.getArtificalSericesTag()); // 上门标记
            resTD.setRsrvNum5(tsbReqData.getResFee());
            resTD.setRsrvStr1(oldResInfo.getString("RSRV_STR1",""));
            resTD.setRsrvStr2(oldResInfo.getString("RSRV_STR2",""));
            resTD.setRsrvStr3(oldResInfo.getString("RSRV_STR3",""));
            resTD.setRsrvStr4(tsbReqData.getResBrandCode() + "," + tsbReqData.getResBrandName());
            resTD.setRsrvStr5(tsbReqData.getWideAddr());
            resTD.setRsrvTag1("J"); // 机顶盒标志
            resTD.setRsrvTag2("1");	//新业务规则标识
            resTD.setRsrvNum4(btd.getRD().getPageRequestData().getString("DEVICE_COST", "0"));
            
    	}else if(StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag)){	//删除
    		resTD = new ResTradeData(oldResInfo);
        	resTD.setModifyTag(modifyTag);
        	resTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    		
    	} 
        
        btd.add(tsbReqData.getUca().getSerialNumber(), resTD);
    }
    
    

    /**
     * @Function: updateMainTradeData()
     * @Description: 更新主台帐的预留字段值
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午9:40:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void updateMainTradeData(BusiTradeData btd, TopSetBoxRequestData tsbReqData) throws Exception
    {
        List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
        MainTradeData mainTD = (MainTradeData) mainList.get(0);
        mainTD.setRsrvStr1(tsbReqData.getUserAction());
        mainTD.setRsrvStr5(tsbReqData.getResKindCode());
        mainTD.setRsrvStr6(tsbReqData.getResNo());
        mainTD.setRsrvStr7(tsbReqData.getResFee());
        mainTD.setRsrvStr8(tsbReqData.getOldResNo());
        mainTD.setRsrvStr9(tsbReqData.getWideState());
        mainTD.setRsrvStr10(tsbReqData.getWideTradeId());
        mainTD.setOlcomTag("1");
        
        //如果是开户需要发送宽带的地址信息
        String userId=tsbReqData.getUca().getUserId();
        IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
        if(IDataUtil.isEmpty(boxInfos)){
        	mainTD.setRsrvStr2("1");	//如果为开户
        	
//        	String serialNumber=tsbReqData.getUca().getSerialNumber();
//        	String wSerialNumber="KD_"+serialNumber;
        	
//        	IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
//            if (IDataUtil.isEmpty(wideInfos))
//            {
//            	IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
//            	String areaCode=wideNetInfo.getString("RSRV_STR4","");
//            	mainTD.setRsrvStr4(areaCode);
//            }
//            else
//            {
//                IData wideTD = wideInfos.getData(0);
//                IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
//                if (IDataUtil.isNotEmpty(addrTD))
//                {
//                	String areaCode=addrTD.getData(0).getString("RSRV_STR4","");
//                	mainTD.setRsrvStr4(areaCode);
//                }
//            }
            
        }
        
        String userAction = tsbReqData.getUserAction();
        
        if (StringUtils.equals("1", userAction))
        {
        	mainTD.setRsrvStr2("2");
        }
    }

}
