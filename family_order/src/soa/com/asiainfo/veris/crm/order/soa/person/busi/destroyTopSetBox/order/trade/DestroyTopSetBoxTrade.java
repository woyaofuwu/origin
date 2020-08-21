package com.asiainfo.veris.crm.order.soa.person.busi.destroyTopSetBox.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyTopSetBox.order.requestdata.DestroyTopSetBoxRequestData;


public class DestroyTopSetBoxTrade extends BaseTrade implements ITrade{

	
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		DestroyTopSetBoxRequestData req=(DestroyTopSetBoxRequestData)bd.getRD();;
		String serialNumber=req.getSerialNumber();
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");
		
		IData boxInfo = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J").first();
		
		IData mainTradeParam=new DataMap();
		mainTradeParam.put("IMSI", boxInfo.getString("IMSI",""));
		updateMainTradeData(bd, req, mainTradeParam);
		
		
		String isReturnTopsetBox=req.getIsReturnTopsetBox();

		
		String rsrvTag2=boxInfo.getString("RSRV_TAG2","");
		if(rsrvTag2.equals("1")){
			
			/*
			 * 如果是新用户，并且选择了退机顶盒才进行退机顶盒
			 */
			if(isReturnTopsetBox!=null&&isReturnTopsetBox.equals("1")){		//如果退还机顶盒
				//先调用华为进行回退机顶盒
				IData returnParam=new DataMap();
				returnParam.put("RES_NO", boxInfo.getString("IMSI"));
				returnParam.put("PARA_VALUE1", serialNumber);
				returnParam.put("SALE_FEE", boxInfo.getString("RSRV_NUM5",""));
				returnParam.put("PARA_VALUE7", "0");
				returnParam.put("DEVICE_COST", boxInfo.getString("RSRV_NUM4","0"));
				returnParam.put("X_CHOICE_TAG", "1");
				returnParam.put("RES_TYPE_CODE", "4");
				returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
				returnParam.put("PARA_VALUE14", boxInfo.getString("RSRV_NUM5","0"));
				returnParam.put("PARA_VALUE17", boxInfo.getString("RSRV_NUM5","0"));
				returnParam.put("PARA_VALUE1", serialNumber);
				returnParam.put("USER_NAME", bd.getRD().getUca().getCustomer().getCustName());
				returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
				returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
				
				IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
				if(IDataUtil.isEmpty(returnResult)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
				}else{
					String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
					if(!resultCode.equals("0")){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
								getData(0).getString("X_RESULTINFO",""));
					}
				}
				
			}
			
			
			/*
			 * 如果用户在开户的时候，没有缴200元的押金，就不用对押金进行处理
			 */
			String rsrvNum2=boxInfo.getString("RSRV_NUM2","0");
			//宽带融合开户撤单调用的魔百和退订 不走此退押金
			if (!"1".equals(req.getIsMergeWideCancel()))
			{
			    if(rsrvNum2!=null&&!rsrvNum2.equals("0"))
	            {
	                int rsrvNum2Int=Integer.parseInt(rsrvNum2);
	                
	                if(rsrvNum2Int>0)
	                {
	                    String topsetboxStartDate=null;
	                    
	                    IDataset userOnlineDate=UserResInfoQry.qryTopsetboxOnline(userId);
	                    if(IDataUtil.isNotEmpty(userOnlineDate))
	                    {
	                        topsetboxStartDate=userOnlineDate.getData(0).getString("IN_TIME","");
	                    }
	                    else
	                    {   
	                        topsetboxStartDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
	                    }
	                    
	                    
	                    String sysdate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
	                    
	                    //获取费用信息
	                    String money="20000";
	                    IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
	                    if(IDataUtil.isNotEmpty(moneyDatas))
	                    {
	                        money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
	                    }
	                    if( rsrvNum2Int <= 100 )
	                    {
	                    	money = Integer.toString(rsrvNum2Int*100);
	                    }
	                    UcaData uca=bd.getRD().getUca();
	                    
	                    //如果用户退还机顶盒或者用户使用的期限已经达到3年或以上
	                    int years=SysDateMgr.yearInterval(topsetboxStartDate, sysdate)+1;
	                    if(years<3)
	                    {
	                        if(isReturnTopsetBox.equals("1"))
	                        {
	                            //调用账务的接口进行押金返回
	                            IData params=new DataMap(); 
	                            params.put("SERIAL_NUMBER_1", serialNumber);
	                            params.put("SERIAL_NUMBER_2", serialNumber);
	                            params.put("DEPOSIT_CODE_1", "9016");
	                            params.put("DEPOSIT_CODE_2", "0");
	                            params.put("FEE", money);
	                            params.put("REMARK", "魔百和退机押金转预存");
	                            params.put("USER_ID_IN", uca.getUserId()); 
	                            params.put("USER_ID_OUT", uca.getUserId()); 
	                            //调用接口，将【押金】——>【现金】
	                            AcctCall.depositeToPhoneMoney(params);
	                            
	                        }
	                        else
	                        {
	                            //资金进行沉淀
	                            IData depositeParam=new DataMap();
	                            depositeParam.put("ACCT_ID", uca.getAcctId());
	                            depositeParam.put("CHANNEL_ID", "15000");
	                            depositeParam.put("PAYMENT_ID", "100021");
	                            depositeParam.put("PAY_FEE_MODE_CODE", "0");
	                            depositeParam.put("REMARK", "魔百和用户拆机不满三年并且未退机，资金进行沉淀！");
	                            
	                            IData depositeInfo=new DataMap();
	                            depositeInfo.put("DEPOSIT_CODE", "9016");
	                            depositeInfo.put("TRANS_FEE", money);
	                            
	                            IDataset depositeInfos=new DatasetList();
	                            depositeInfos.add(depositeInfo);
	                            
	                            depositeParam.put("DEPOSIT_INFOS", depositeInfos);
	                            AcctCall.foregiftDeposite(depositeParam);
	                            
	                        }
	                    }
	                    else
	                    {
	                        String isBack=userOnlineDate.getData(0).getString("IS_BACK","");
	                        if(!isBack.equals("1"))
	                        {   //说明还没有转换用户的金额
	                            //调用账务的接口进行押金返回
	                            IData params=new DataMap(); 
	                            params.put("SERIAL_NUMBER_1", serialNumber);
	                            params.put("SERIAL_NUMBER_2", serialNumber);
	                            params.put("DEPOSIT_CODE_1", "9016");
	                            params.put("DEPOSIT_CODE_2", "0");
	                            params.put("FEE", money);
	                            params.put("REMARK", "魔百和退机押金转预存");
	                            params.put("USER_ID_IN", uca.getUserId()); 
	                            params.put("USER_ID_OUT", uca.getUserId()); 
	                            //调用接口，将【押金】——>【现金】
	                            AcctCall.depositeToPhoneMoney(params);
	                            
	                        }
	                    }

	                }
	            }
			}
			
		}

		
		//创建资源台账数据
		ResTradeData resTD = new ResTradeData(boxInfo);
		resTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		resTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
		bd.add(req.getUca().getSerialNumber(), resTD);
		
		
		String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
		if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
			String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
			if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
				String basePlatSvcId=basePlatSvcIdArr[0];
				if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
					this.createPlatSVCAndAttr(bd, req, PlatConstants.OPER_CANCEL_ORDER, 
							basePlatSvcId, BofConst.MODIFY_TAG_DEL);
				}
			}
		}
		
        String optionPlatSvcIdTemp=boxInfo.getString("RSRV_STR3","");	//可选套餐
        if(!optionPlatSvcIdTemp.equals("")&&optionPlatSvcIdTemp.indexOf(",")!=-1){
        	String[] optionPlatSvcIdArr=optionPlatSvcIdTemp.split(",");
			if(optionPlatSvcIdArr!=null&&optionPlatSvcIdArr.length>0){
				String optionPlatSvcId=optionPlatSvcIdArr[0];
				if(optionPlatSvcId!=null&&!optionPlatSvcId.trim().equals("")&&
					!optionPlatSvcId.trim().equals("-1")){
					this.createPlatSVCAndAttr(bd, req, PlatConstants.OPER_CANCEL_ORDER, 
							optionPlatSvcId, BofConst.MODIFY_TAG_DEL);
				}
				
			}			
		}
        
        
        /*
         * 如果存在首免和5元的订购关系，也需要进行终止
         */
        IDataset configDiscnts1=CommparaInfoQry.getCommNetInfo("CSM", "3012", "free_discnt");
		String freeDiscnt=null;
		if(IDataUtil.isNotEmpty(configDiscnts1)){
			freeDiscnt=configDiscnts1.getData(0).getString("PARA_CODE1","3000002");
		}else{
			freeDiscnt="3000002";
		}
		
		IDataset orderFreeDiscntData=UserDiscntInfoQry.getAllDiscntByUser(userId, freeDiscnt);
		if(IDataUtil.isNotEmpty(orderFreeDiscntData)){
			DiscntTradeData discntFreeTrade=new DiscntTradeData(orderFreeDiscntData.getData(0));
			discntFreeTrade.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
			discntFreeTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
			
			bd.add(req.getUca().getSerialNumber(), discntFreeTrade);
		}

		
		//如果用户存在有效的为开户未完工的收费的优惠的话，进行删除
		IDataset discntConfigs=CommparaInfoQry.queryComparaByAttrAndCode1
													("CSM", "4022", "TOP_SET_OPEN_DISCNT", "4022");
		String discnt5=null;
		if(IDataUtil.isNotEmpty(discntConfigs)){
			discnt5=discntConfigs.getData(0).getString("PARA_CODE2","40229999");
		}else{
			discnt5="40229999";
		}
		IDataset order5DiscntData=UserDiscntInfoQry.getAllDiscntByUser(userId, discnt5);
		if(IDataUtil.isNotEmpty(order5DiscntData)){
			DiscntTradeData discnt5Trade=new DiscntTradeData(order5DiscntData.getData(0));
			discnt5Trade.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
			discnt5Trade.setModifyTag(BofConst.MODIFY_TAG_DEL);
			
			bd.add(req.getUca().getSerialNumber(), discnt5Trade);
		}
		
		
		/*
    	 * 停止停机保号费：
    	 */
		IDataset userSvcs=UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "3000003");
    	if(IDataUtil.isNotEmpty(userSvcs)){
    		/*
             * 绑定用户停机保号服务和优惠
             * 
             */
            //创建停机保号服务
            SvcTradeData svcTD = new SvcTradeData(userSvcs.getData(0));
//            svcTD.setEndDate(SysDateMgr.getLastDateThisMonth());
            svcTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            svcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, svcTD);
    	}
    	
    	IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, "1550");
    	if(IDataUtil.isNotEmpty(userDiscnts)){
    		//创建停机保号优惠
            DiscntTradeData discntTD = new DiscntTradeData(userDiscnts.getData(0));
//            discntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
            discntTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, discntTD);
    	}
    	
    	IDataset userSvcStates=UserSvcInfoQry.getUserSvcStateByUserId(userId, "3000003");
    	if(IDataUtil.isNotEmpty(userSvcStates)){	//如果存在服务的状态，且为生效的状态
    		
    		for(int i=0,size=userSvcStates.size();i<size;i++){
    			SvcStateTradeData stopSvcStateData = new SvcStateTradeData(userSvcStates.getData(i));
        		stopSvcStateData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        		stopSvcStateData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        		bd.add(serialNumber, stopSvcStateData);
    		}
    	}

    	//用户老的套餐也需要截止
        IDataset userOldDiscnts=UserDiscntInfoQry.getOldDiscntByUser(userId);
        if(IDataUtil.isNotEmpty(userOldDiscnts)){   //如果存在老的套餐的话，截止掉
            for(int i=0,size=userOldDiscnts.size();i<size;i++){
                DiscntTradeData discntOld = new DiscntTradeData(userOldDiscnts.getData(i));
                discntOld.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                discntOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
                bd.add(serialNumber, discntOld);
            }
        }
		
		/*
		 * 删除入网记录
		 */
		UserResInfoQry.delTopsetboxOnline(userId);
        
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
    private void createPlatSVCAndAttr(BusiTradeData btd, DestroyTopSetBoxRequestData tsbReqData, String operCode, String serviceId, String modifyTag) throws Exception
    {
        List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
        // 平台服务
        IData platParam = new DataMap();
        platParam.put("SERVICE_ID", serviceId);
        platParam.put("OPER_CODE", operCode);
        platParam.put("OPR_SOURCE", "08");
        PlatSvcData psd = new PlatSvcData(platParam);
//        // 属性 1.宽带地址
//        List<AttrData> attrs = new ArrayList<AttrData>();
//        AttrData addrAttr = new AttrData();
//        addrAttr.setAttrCode("ADDRESS");
//        addrAttr.setAttrValue(tsbReqData.getWideAddr());
//        addrAttr.setModifyTag(modifyTag);
//        attrs.add(addrAttr);
//        // 2.宽带帐号
//        AttrData brandIDAttr = new AttrData();
//        brandIDAttr.setAttrCode("BROADBANDID");
//        brandIDAttr.setAttrValue("KD_" + tsbReqData.getSerialNumber());
//        brandIDAttr.setModifyTag(modifyTag);
//        attrs.add(brandIDAttr);
//        // 3.旧终端号
//        AttrData oldTSBIDAttr = new AttrData();
//        oldTSBIDAttr.setAttrCode("OLDSTBID");
//        oldTSBIDAttr.setAttrValue(tsbReqData.getOldResNo());
//        oldTSBIDAttr.setModifyTag(modifyTag);
//        attrs.add(oldTSBIDAttr);
//        // 4.新终端号
//        AttrData stbIdAttr = new AttrData();
//        stbIdAttr.setAttrCode("STBID");
//        stbIdAttr.setAttrValue(tsbReqData.getResNo());
//        stbIdAttr.setModifyTag(modifyTag);
//        attrs.add(stbIdAttr);
//        if (!StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL))
//        {
//            psd.setAttrs(attrs);
//        }
        bindPlatSvcs.add(psd);
        ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, btd.getRD().getUca(), btd);
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
    private void updateMainTradeData(BusiTradeData btd, DestroyTopSetBoxRequestData tsbReqData, IData param) throws Exception
    {
        List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
        MainTradeData mainTD = (MainTradeData) mainList.get(0);
        mainTD.setRsrvStr1(param.getString("IMSI",""));		//用于打印免填单
        mainTD.setRsrvStr3(tsbReqData.getIsReturnTopsetBox());//记录是否退还机顶盒@by tanzheng 
    }
	
	
}
