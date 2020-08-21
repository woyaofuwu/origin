package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
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
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy.order.requestdata.NoPhoneTopSetBoxDestroyRequestData;


public class NoPhoneTopSetBoxDestroyTrade extends BaseTrade implements ITrade{

	
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		NoPhoneTopSetBoxDestroyRequestData req=(NoPhoneTopSetBoxDestroyRequestData)bd.getRD();;
		String serialNumber=req.getSerialNumber();
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");
		
		IData boxInfo = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J").first();
		
		//修改主台账表
		updateMainTradeData(bd, req, boxInfo);
		
		String isReturnTopsetBox=req.getIsReturnTopsetBox(); //是否退还机顶盒
		String rsrvTag2=boxInfo.getString("RSRV_TAG2",""); //新业务规则标识
		if(rsrvTag2.equals("1"))
		{
			/*
			 * 如果是新用户，并且选择了退机顶盒才进行退机顶盒
			 */
			if(isReturnTopsetBox!=null&&isReturnTopsetBox.equals("1"))  // 1:退还 
			{	
				/*
				 * 如果退还机顶盒
				 * 先调用华为进行回退机顶盒
				*/
				rollBackTopSetBox(bd, req, boxInfo);
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
			    	/*
			    	 * 魔百和押金处理
			    	 * */
	                dealWithTopSexBoxModel(bd, req, boxInfo,userId);
	            }
			}
		}
		
		//创建资源台账数据
		deleteResTradeData(bd, req, boxInfo);
		
		//创建必选套餐数据
		deleteBasePlatSvc(bd, req, boxInfo);
		
		//创建可选套餐数据
		deleteOptionPlatSvc(bd, req, boxInfo);
        
        /*
         * 如果存在首免和5元的订购关系，也需要进行终止
         */
		stopFirstFee(bd,req,userId);
        
		//如果用户存在有效的为开户未完工的收费的优惠的话，进行删除
		deleteUserCreateDiscnt(bd,req,userId);
		
		/*
    	 * 停止停机保号费：
    	 */
		IDataset userSvcs=UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "3000003");
    	if(IDataUtil.isNotEmpty(userSvcs)){
    		/*
             * 绑定用户停机保号服务和优惠
             */
            //创建停机保号服务
            SvcTradeData svcTD = new SvcTradeData(userSvcs.getData(0));
//            svcTD.setEndDate(SysDateMgr.getLastDateThisMonth());
            svcTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            svcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, svcTD);
    	}
    	
    	IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, "1550");
    	if(IDataUtil.isNotEmpty(userDiscnts))
    	{
    		//创建停机保号优惠
            DiscntTradeData discntTD = new DiscntTradeData(userDiscnts.getData(0));
            discntTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(serialNumber, discntTD);
    	}
    	
    	IDataset userSvcStates=UserSvcInfoQry.getUserSvcStateByUserId(userId, "3000003");
    	
    	if(IDataUtil.isNotEmpty(userSvcStates))
    	{	
    		//如果存在服务的状态，且为生效的状态
    		for(int i=0,size=userSvcStates.size();i<size;i++)
    		{
    			SvcStateTradeData stopSvcStateData = new SvcStateTradeData(userSvcStates.getData(i));
        		stopSvcStateData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        		stopSvcStateData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        		bd.add(serialNumber, stopSvcStateData);
    		}
    	}

    	//用户老的套餐也需要截止
        IDataset userOldDiscnts=UserDiscntInfoQry.getOldDiscntByUser(userId);
        if(IDataUtil.isNotEmpty(userOldDiscnts))
        {   //如果存在老的套餐的话，截止掉
            for(int i=0,size=userOldDiscnts.size();i<size;i++)
            {
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
     * @author: yuyj3
     */
    private void createPlatSVCAndAttr(BusiTradeData btd, NoPhoneTopSetBoxDestroyRequestData tsbReqData, String operCode, String serviceId, String modifyTag) throws Exception
    {
        List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
        // 平台服务
        IData platParam = new DataMap();
        platParam.put("SERVICE_ID", serviceId);
        platParam.put("OPER_CODE", operCode);
        platParam.put("OPR_SOURCE", "08");
        PlatSvcData psd = new PlatSvcData(platParam);
        bindPlatSvcs.add(psd);
        ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, btd.getRD().getUca(), btd);
    }
    
    
    /**
     * @Function: updateMainTradeData()
     * @Description: 更新主台帐的预留字段值
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yuyj3
     */
    private void updateMainTradeData(BusiTradeData btd, NoPhoneTopSetBoxDestroyRequestData tsbReqData, IData param) throws Exception
    {
        List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
        MainTradeData mainTD = (MainTradeData) mainList.get(0);
        mainTD.setRsrvStr1(param.getString("IMSI",""));		//用于打印免填单
        mainTD.setRsrvStr3(param.getString("SERIAL_NUMBER_B"));  //宽带号码
    }
	
	public void deleteResTradeData(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,IData boxInfo) throws Exception 
	{
		ResTradeData resTD = new ResTradeData(boxInfo);
		resTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		resTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
		bd.add(req.getUca().getSerialNumber(), resTD);
	}
	
	//创建必选套餐数据
	public void deleteBasePlatSvc(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,IData boxInfo) throws Exception 
	{
		String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
		if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1)
		{
			String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
			if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
				String basePlatSvcId=basePlatSvcIdArr[0];
				if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
					this.createPlatSVCAndAttr(bd, req, PlatConstants.OPER_CANCEL_ORDER, 
							basePlatSvcId, BofConst.MODIFY_TAG_DEL);
				}
			}
		}
	}
	
	//创建可选套餐数据
	public void deleteOptionPlatSvc(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,IData boxInfo) throws Exception 
	{
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
	}
	
	/*
     * 如果存在首免和5元的订购关系，也需要进行终止
     */
	public void stopFirstFee(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,String userId) throws Exception
	{
		IDataset configDiscnts1=CommparaInfoQry.getCommNetInfo("CSM", "3012", "free_discnt");
		String freeDiscnt=null;
		if(IDataUtil.isNotEmpty(configDiscnts1))
		{
			freeDiscnt=configDiscnts1.getData(0).getString("PARA_CODE1","3000002");
		}
		else
		{
			freeDiscnt="3000002";
		}
		
		IDataset orderFreeDiscntData=UserDiscntInfoQry.getAllDiscntByUser(userId, freeDiscnt);
		
		if(IDataUtil.isNotEmpty(orderFreeDiscntData))
		{
			DiscntTradeData discntFreeTrade=new DiscntTradeData(orderFreeDiscntData.getData(0));
			discntFreeTrade.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
			discntFreeTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
			
			bd.add(req.getUca().getSerialNumber(), discntFreeTrade);
		}
	}
	
	public void deleteUserCreateDiscnt(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,String userId) throws Exception
	{
		IDataset discntConfigs=CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "4022", "TOP_SET_OPEN_DISCNT", "4022");
		String discnt5=null;
		if(IDataUtil.isNotEmpty(discntConfigs))
		{
			discnt5=discntConfigs.getData(0).getString("PARA_CODE2","40229999");
		}
		else
		{
			discnt5="40229999";
		}
		
		IDataset order5DiscntData=UserDiscntInfoQry.getAllDiscntByUser(userId, discnt5);
		if(IDataUtil.isNotEmpty(order5DiscntData))
		{
			DiscntTradeData discnt5Trade=new DiscntTradeData(order5DiscntData.getData(0));
			discnt5Trade.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
			discnt5Trade.setModifyTag(BofConst.MODIFY_TAG_DEL);
			
			bd.add(req.getUca().getSerialNumber(), discnt5Trade);
		}
	}
	
	/*
	 * 如果退机顶盒，调用华为接口进行机顶盒回退
	 * */
	public void rollBackTopSetBox(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,IData boxInfo) throws Exception
	{
		String serialNumber = req.getSerialNumber();
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
		
		if(IDataUtil.isEmpty(returnResult))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
		}
		else
		{
			String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
			if(!resultCode.equals("0")){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
						getData(0).getString("X_RESULTINFO",""));
			}
		}
	}
	
	/*
	 * 魔百和押金处理
	 * */
    public void dealWithTopSexBoxModel(BusiTradeData bd, NoPhoneTopSetBoxDestroyRequestData req,IData boxInfo,String userId) throws Exception
    {
    	String rsrvNum2 = boxInfo.getString("RSRV_NUM2","0");
    	String isReturnTopsetBox=req.getIsReturnTopsetBox(); //是否退还机顶盒
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
            
            //调测费用户没有押金不再对押金处理
//            IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId,"84073843",Route.CONN_CRM_CG);
            IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUser_2(userId, "84073843");//后面现场调测费优惠只有一个月有效期，不能根据有效的去判断了  modify_by_duhj_kd
            if(IDataUtil.isEmpty(discntInfo))
            {
            	if(years<3)
                {
                    if(isReturnTopsetBox.equals("1"))   //退还机顶盒
                    {
                    	//无手机宽带魔百和业务调用退费接口
                        AcctCall.backFee(uca.getUserId(), req.getTradeId(), "15000", "9016", "16001", money);
                        
                    }
                    else
                    {
                        //资金进行沉淀
                        IData depositeParam=new DataMap();
                        depositeParam.put("ACCT_ID", uca.getAcctId());
                        depositeParam.put("CHANNEL_ID", "15000");
                        depositeParam.put("PAYMENT_ID", "100021");
                        depositeParam.put("PAY_FEE_MODE_CODE", "0");
                        depositeParam.put("REMARK", "无手机魔百和用户拆机不满三年并且未退机，资金进行沉淀！");
                        
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
                    	//无手机宽带魔百和业务调用退费接口
                        AcctCall.backFee(uca.getUserId(), req.getTradeId(), "15000", "9016", "16001", money);
                    }
                }
            }
            
        }
    }
}
