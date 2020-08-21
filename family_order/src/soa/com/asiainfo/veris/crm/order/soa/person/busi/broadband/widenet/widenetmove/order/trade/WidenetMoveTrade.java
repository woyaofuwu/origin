package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.requestdata.WidenetMoveRequestData;

public class WidenetMoveTrade extends BaseTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {
        btd.getMainTradeData().setUserIdB(reqData.getGponUserId());
        btd.getMainTradeData().setRsrvStr5(UProductInfoQry.getProductNameByProductId(btd.getRD().getUca().getProductId()));
        btd.getMainTradeData().setSubscribeType("300");
        if(reqData.isChgProd()){
        	btd.getMainTradeData().setRsrvStr6("1");
        }
    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createAddSpecTradeWidenet(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, IData widenetInfo) throws Exception
    {
        IDataset newWidenetInfos = WidenetInfoQry.getUserWidenetInfo(reqData.getGponUserId());
        IData newWidenetInfo = newWidenetInfos.getData(0);
        WideNetTradeData wtd = new WideNetTradeData(widenetInfo);
        wtd.setOldStandAddress(widenetInfo.getString("STAND_ADDRESS"));
        wtd.setOldStandAddressCode(widenetInfo.getString("STAND_ADDRESS_CODE"));
        wtd.setOldDetailAddress(widenetInfo.getString("DETAIL_ADDRESS"));
        wtd.setStandAddress(newWidenetInfo.getString("STAND_ADDRESS"));
        wtd.setStandAddressCode(newWidenetInfo.getString("STAND_ADDRESS_CODE"));
        wtd.setDetailAddress(newWidenetInfo.getString("DETAIL_ADDRESS"));
        wtd.setSuggestDate(reqData.getSuggestDate());//预约时间
        wtd.setContact(reqData.getNewContact());
        wtd.setContactPhone(reqData.getNewContactPhone());
        wtd.setPhone(reqData.getNewPhone());
        wtd.setStartDate(SysDateMgr.getSysTime());
        wtd.setEndDate(SysDateMgr.getTheLastTime());
        wtd.setInstId(SeqMgr.getInstId());
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        wtd.setRsrvStr1(newWidenetInfo.getString("RSRV_STR1"));
        //wtd.setRsrvStr2(newWidenetInfo.getString("RSRV_STR2"));
        wtd.setRsrvStr4(reqData.getNewAreaCode());
        wtd.setRsrvNum1(reqData.getDeviceId());
        if ("2".equals(reqData.getPreWideType()))
        {
            wtd.setRsrvStr3("2");
        }
        else
        {
            wtd.setRsrvStr3("1");
        }
        if(reqData.isChgProd()){
        	wtd.setRsrvNum5("1");
        }else{
        	wtd.setRsrvNum5("0");
        }
        if (!"".equals(reqData.getNewWideType()))
        {
            wtd.setRsrvStr2(reqData.getNewWideType());
        }else{
        	wtd.setRsrvStr2(widenetInfo.getString("RSRV_STR2"));
        }
        wtd.setRsrvNum3("");
        wtd.setRsrvNum4("");
        wtd.setRemark("用户宽带特殊移机操作");
        wtd.setRsrvTag1(widenetInfo.getString("RSRV_TAG1"));
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }
    
    public IData getIMSInfoBySerialNumber(IData param) throws Exception{
    	
        String  serialNumber=param.getString("SERIAL_NUMBER","");
        String userIdB="";
        if(!"".equals(serialNumber) && serialNumber !=null){
       	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
       	 userIdB =  userInfoData.getString("USER_ID","").trim();
        }else{
       	 //如果手机号码为空则用userid
       	 userIdB=param.getString("USER_ID","");
        }
        //获取主号信息
        IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "1");
        if(IDataUtil.isNotEmpty(iDataset)){
       	 //获取虚拟号
       	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
       	 //通过虚拟号获取关联的IMS家庭固话号码信息
       	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
       	 
       	 if(IDataUtil.isNotEmpty(userBInfo)){
       		 return userBInfo.getData(0);
       	 }
        }
   	 //不存在IMS家庭固话
   	 return null;
   }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createAddTradeWidenet(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, IData widenetInfo) throws Exception
    {
        WideNetTradeData wtd = new WideNetTradeData(widenetInfo);
        wtd.setOldStandAddress(widenetInfo.getString("STAND_ADDRESS"));
        wtd.setOldStandAddressCode(widenetInfo.getString("STAND_ADDRESS_CODE"));
        wtd.setOldDetailAddress(widenetInfo.getString("DETAIL_ADDRESS"));
        wtd.setSuggestDate(reqData.getSuggestDate()); //预约时间
        wtd.setStandAddress(reqData.getNewStandAddress());
        wtd.setStandAddressCode(reqData.getNewStandAddressCode());
        wtd.setDetailAddress(reqData.getNewDetailAddress());
        wtd.setContact(reqData.getNewContact());
        wtd.setContactPhone(reqData.getNewContactPhone());
        wtd.setPhone(reqData.getNewPhone());
        wtd.setStartDate(SysDateMgr.getSysTime());
        wtd.setEndDate(SysDateMgr.getTheLastTime());
        wtd.setInstId(SeqMgr.getInstId());
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        wtd.setRsrvStr1(widenetInfo.getString("RSRV_STR1"));
        //wtd.setRsrvStr2(widenetInfo.getString("RSRV_STR2"));
        wtd.setRsrvNum1(reqData.getDeviceId());
        wtd.setRsrvStr4(reqData.getNewAreaCode());
        wtd.setRsrvTag1(widenetInfo.getString("RSRV_TAG1"));
        if(reqData.isChgProd()){
        	wtd.setRsrvNum5("1");
        }else{
        	wtd.setRsrvNum5("0");
        }
        wtd.setRsrvNum3("");
        //add by zhangxing3 for REQ201810080022同市县宽带移机关联和家固话移机 start
        wtd.setRsrvStr3("");
    	String oldAreaCode = widenetInfo.getString("RSRV_STR4", "");
    	String newAreaCode = reqData.getNewAreaCode();
    	IData imsInfo = null;IData data = new DataMap();
    	String mSerialNumber = btd.getRD().getPageRequestData().getString("SERIAL_NUMBER","");
    	if(mSerialNumber.startsWith("KD_1")){
    		data.put("SERIAL_NUMBER", mSerialNumber.substring(3));
    	}
    	if(!"".equals(newAreaCode) && !"".equals(oldAreaCode) && oldAreaCode.equals(newAreaCode))
    	{
    		imsInfo = getIMSInfoBySerialNumber(data);
    		if (IDataUtil.isNotEmpty(imsInfo)) {
    	        wtd.setRsrvStr3("MOVE_IMS"); //IMS固话移机标识
    	        wtd.setRsrvStr5(imsInfo.getString("SERIAL_NUMBER_B", "")); //IMS固话号码

    		}
    	}
        //add by zhangxing3 for REQ201810080022同市县宽带移机关联和家固话移机 start
        if (!"".equals(reqData.getNewWideType()))
        {
            wtd.setRsrvStr2(reqData.getNewWideType());
        }else{
        	wtd.setRsrvStr2(widenetInfo.getString("RSRV_STR2"));
        }
        
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }

    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        WidenetMoveRequestData reqData = (WidenetMoveRequestData) btd.getRD();
        createDelTradeRelation(btd, reqData);// 移机删除UU关系
        IDataset WidenetInfos = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
        createDelTradeWidenet(btd, reqData, WidenetInfos.getData(0));
        
        if ("636".equals(btd.getTradeTypeCode()))
        {
            createAddSpecTradeWidenet(btd, reqData, WidenetInfos.getData(0));
            IDataset relaInfos = RelaUUInfoQry.isMasterAccount(reqData.getGponUserId(), "77");
            if (IDataUtil.isNotEmpty(relaInfos))
            {
                relaInfos = RelaUUInfoQry.isMasterAccount(reqData.getGponUserId(), "78");
            }
            if (IDataUtil.isNotEmpty(relaInfos))
            {
                createModifySpecTradeWidenet(btd, reqData);
            }

            Boolean tag = true;
            String userIdA = reqData.getUserIdA();
            if (StringUtils.isBlank(userIdA))
            {
                userIdA = SeqMgr.getUserId();
                tag = false;
                btd.addOpenUserAcctDayData(userIdA, "1");
            }
            String gponUserId = reqData.getGponUserId();// 主账号user_id
            String gponSerialNumber = reqData.getGponSerialNumber();// 主账号用户
            String relationtypecode = "";
            if ("2".equals(reqData.getPreWideType()))// 根据不同的宽带类型取不同的relationtypecode
                relationtypecode = "77";// 平行账号
            else
                relationtypecode = "78"; // 家庭账号
            if (!tag)
            {
                genTradeRelaInfoWideA(userIdA, gponUserId, gponSerialNumber, "1", relationtypecode, btd, reqData);
            }
            genTradeRelaInfoWideB(userIdA, reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", relationtypecode, btd, reqData);
        }
        else
        {
            createAddTradeWidenet(btd, reqData, WidenetInfos.getData(0));

        }
        
        appendTradeMainData(btd, reqData);
        String mainSvcId = "";
        IDataset idataSet = new DatasetList();
        genProductChgTrade(btd, reqData, mainSvcId, idataSet);
        if(!reqData.isIsBusiness()){
        	IData cancelTrade = new DataMap();
            cancelUserSaleActive(btd, reqData, cancelTrade);
            genSaleActiveTrade(btd, reqData, idataSet, cancelTrade);
        }
        cancelYearDiscnt(btd, reqData);
        genModelChgTrade(btd, reqData);
        
        //包年套餐转宽带1+费用处理
        
        //包年套餐转包年活动
        
        //包年活动转宽带1+费用处理
        
        //包年活动转包年活动费用处理
        
    }
    
    private void addLimitTradeId(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, String tradeId, String limitTradeId) throws Exception
    {
    	if(tradeId.equals(limitTradeId)||"".equals(tradeId)||"".equals(limitTradeId)) return ;
        MebCommonBean.regTradeLimitInfo(tradeId, limitTradeId);
    }
    
    /**
     * 用户办理新营销活动之后，需要对原来的包年套餐做终止
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void cancelYearDiscnt(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {
    	if(reqData.isSaleactive()&&reqData.isEffYearDnt()&&!reqData.isChgProd()){
            String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    		DiscntTradeData userDnt = new  DiscntTradeData(new DataMap(reqData.getEffYearDiscnt()));
    		userDnt.setModifyTag("1");
    		userDnt.setEndDate(SysDateMgr.getLastDateThisMonth());
            btd.add(serialNumber, userDnt);   
    	}
    }
    
    /**
     * 1,用户办理包年套餐或者新营销活动之后，需要对原来的营销活动做终止
     * 2,用户的营销活动周期已经结束，但是营销活动结束时间配置的是2050年，此时tf_f_user_saleactive表的结束时间同样是2050.
     * 此时，如果用户办理产品变更，同样需要对原来的营销活动做终止
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void cancelUserSaleActive(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, IData cancelTrade) throws Exception
    {

    	//if(((reqData.isYearDiscnt()||reqData.isSaleactive())&&(reqData.getEffActiveInfo()!=null&&!"".equals(reqData.getEffActiveInfo())))
    	//		||((reqData.isChgProd())&&(reqData.getEffActiveInfo()!=null&&!"".equals(reqData.getEffActiveInfo())))){
    	//if(true){if (reqData.getNewMainProduct() != null && !uca.getProductId().equals(reqData.getNewMainProduct().getProductId()))
    	if(reqData.isYearDiscnt()||reqData.isSaleactive()||(reqData.isChgProd()&&!reqData.getUca().getProductId().equals(reqData.getNewMainProduct().getProductId()))){
    		//查询用户是否有生效的营销活动
    		String seriUserId = reqData.getUserIdMobileA();
        	IData effActive = new DataMap();
    		IDataset saleActive = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(seriUserId);
    		IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, btd.getTradeTypeCode(), CSBizBean.getUserEparchyCode());
    		IDataset kdAct1 = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
            if (IDataUtil.isNotEmpty(kdAct1)) kdActives.addAll(kdAct1);
    		if ((saleActive != null && saleActive.size() > 0)&&(kdActives != null && kdActives.size() > 0))
            {
    			for (int i = 0; i < saleActive.size(); i++)
                {
                    IData element = saleActive.getData(i);
                    for(int j=0;j<kdActives.size();j++){
                    	if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
                    		effActive = element;
                    	}
                    }
                }
            }
    		
    		if(effActive==null||effActive.size()==0||"".equals(effActive)) return;
    		else {
                //danglt
                if ("1".equals(btd.getRD().getPageRequestData().getString("SAME_YEAR_SALE_ACTIVE_FLAG", "0"))) {
                    return;
                }
    			btd.getMainTradeData().setRsrvStr9("1");
    			return ;
    		}
    		
    		//如果存在退费，封装操作费用
            /*String returnFee="0";
            BigDecimal returnFeeD=new BigDecimal(returnFee);
            int finalReturnFee=returnFeeD.multiply(new BigDecimal(100)).intValue();
            
            IData svcParam = new DataMap();
            svcParam.put("SERIAL_NUMBER", effActive.getString("SERIAL_NUMBER"));
            svcParam.put("PRODUCT_ID", effActive.getString("PRODUCT_ID"));
            svcParam.put("PACKAGE_ID", effActive.getString("PACKAGE_ID"));
            svcParam.put("RELATION_TRADE_ID", effActive.getString("RELATION_TRADE_ID"));
            svcParam.put("CAMPN_TYPE", effActive.getString("CAMPN_TYPE"));
            svcParam.put("REMARK", effActive.getString("REMARK"));
            svcParam.put("RETURNFEE",finalReturnFee);
            
            svcParam.put("INTERFACE", "1");
            svcParam.put(Route.ROUTE_EPARCHY_CODE, effActive.getString("EPARCHY_CODE"));
            svcParam.put("END_DATE_VALUE", effActive.getString("END_DATE_VALUE"));//QR-20150109-14 营销活动终止时间不对BUG by songlm @20150114 

            if(finalReturnFee>0){
            	IData tradeFeeSub=new DataMap();
            	tradeFeeSub.put("TRADE_TYPE_CODE", "237");
            	tradeFeeSub.put("FEE_TYPE_CODE", "602");
            	tradeFeeSub.put("FEE", finalReturnFee);
            	tradeFeeSub.put("OLDFEE", finalReturnFee);
            	tradeFeeSub.put("FEE_MODE", "0");	//营业费用
            	tradeFeeSub.put("ELEMENT_ID", "");
            	
            	IData tradePayMoney=new DataMap();
            	tradePayMoney.put("PAY_MONEY_CODE", "0");
            	tradePayMoney.put("MONEY", finalReturnFee);
            	
            	IDataset tradeFeeSubs=new DatasetList();
            	tradeFeeSubs.add(tradeFeeSub);
            	
            	IDataset tradePayMoneys=new DatasetList();
            	tradePayMoneys.add(tradePayMoney);
            	
            	
            	svcParam.put("X_TRADE_FEESUB", tradeFeeSubs);
            	svcParam.put("X_TRADE_PAYMONEY", tradePayMoneys);
            }

            IDataset saleActives = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", svcParam);*/

            /*IData endActiveParam = new DataMap();
            endActiveParam.put("SERIAL_NUMBER", effActive.getString("SERIAL_NUMBER"));
            endActiveParam.put("PRODUCT_ID", effActive.getString("PRODUCT_ID"));
            endActiveParam.put("PACKAGE_ID", effActive.getString("PACKAGE_ID"));
            endActiveParam.put("RELATION_TRADE_ID", effActive.getString("RELATION_TRADE_ID"));
            endActiveParam.put("IS_RETURN", "0");
            endActiveParam.put("FORCE_END_DATE", SysDateMgr.getLastDateThisMonth());
            endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
            endActiveParam.put("EPARCHY_CODE",reqData.getUca().getUserEparchyCode());//认证方式
            String checkMode = btd.getRD().getCheckMode();
            endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
            //endActiveParam.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
            //endActiveParam.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));

            IDataset saleActives = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
            
            if(saleActives!=null&&saleActives.size()>0){
            	addLimitTradeId(btd, reqData, saleActives.getData(0).getString("TRADE_ID", ""), btd.getTradeId());
            }
            cancelTrade.put("CANCEL_TRADE_ID", saleActives.getData(0).getString("TRADE_ID", ""));
        	btd.getMainTradeData().setRsrvStr8(saleActives.getData(0).getString("TRADE_ID", ""));*/
    	}
    }
    
    /**
     * 营销活动受理
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void genSaleActiveTrade(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, IDataset idataSet, IData cancelTrade) throws Exception
    {
/*		IData param=new DataMap(reqData.getWidenetMoveNew());
		//param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
		
        IDataset widenetResult = CSAppCall.call("SS.DestroyWidenetUserNowRegSVC.tradeReg", param);
        */
    	if(reqData.isSaleactive()){
    		IData data = new DataMap(reqData.getWidenetMoveNew());
    		
			//IData saleActive = new DataMap(data.toString());
			IData saleActive = new DataMap();
			String mSerialNumber = data.getString("SERIAL_NUMBER","");
	    	if(mSerialNumber.startsWith("KD_1")){
	    		mSerialNumber = mSerialNumber.substring(3);
	    		//input.put("SERIAL_NUMBER", serialNumber);
	    	}
			
			saleActive.put("SERIAL_NUMBER", mSerialNumber);
			saleActive.put("PRODUCT_ID", data.getString("SALEACTIVE_PRODUCT_ID"));
			saleActive.put("PACKAGE_ID", data.getString("SALEACTIVE_PACKAGE_ID"));
			saleActive.put("SALE_STAFF_ID", data.getString("SALE_STAFF_ID"));
			saleActive.put("CAMPN_TYPE", data.getString("SALEACTIVE_CAMPN_TYPE"));
			saleActive.put("START_DATE", SysDateMgr.getSysDate());
			saleActive.put("END_DATE", SysDateMgr.getTheLastTime());
			saleActive.put("BOOK_DATE", data.getString("SALEACTIVE_BOOK_DATE"));
			saleActive.put("TRADE_TYPE_CODE", "240");
			saleActive.put("WIDE_ORDER_TYPE_CODE", "606");
			saleActive.put("ORDER_TYPE_CODE", "606");
			saleActive.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
			saleActive.put("WIDENET_MOVE_SALEACTIVE_SIGN", "1");
			if(!"".equals(cancelTrade.getString("CANCEL_TRADE_ID"))){
				saleActive.put("ACCEPT_TRADE_ID", cancelTrade.getString("CANCEL_TRADE_ID"));
			}else{
				saleActive.put("ACCEPT_TRADE_ID", btd.getTradeId());
			}
			saleActive.put("WIDE_MOVE_ACTIVE_PCKID", data.getString("SALEACTIVE_PACKAGE_ID"));
			saleActive.put("WIDE_MOVE_ACTIVE_PRODID", data.getString("SALEACTIVE_PRODUCT_ID"));
			String allSvc = "|";
			if(idataSet!=null&&idataSet.size()>0){
				for(int k=0;k<idataSet.size();k++){
					allSvc = allSvc + idataSet.getData(k).getString("PKG_CHECK_SVCID") + "|";
				}
			}
			saleActive.put("WIDE_USER_SELECTED_SERVICEIDS", allSvc);
			saleActive.put("WIDE_MOVE_ACTIVE_TRADEID", btd.getTradeId());
			
//			IDataset pkgs = PkgElemInfoQry.getPackageElementByPackageId(data.getString("SALEACTIVE_PACKAGE_ID"));
//			if(pkgs!=null&&pkgs.size()>0){
//				for(int k=0;k<pkgs.size();k++){
//					pkgs.getData(k).put("MODIFY_TAG", "0");
//				}
//			}
//			
//			saleActive.put("SELECTED_ELEMENTS", pkgs);
			
            String checkMode = btd.getRD().getCheckMode();
            saleActive.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
			//saleActive.put("WIDE_MOVE_ACTIVE_DISCNTID", input.getString("WIDE_MOVE_ACTIVE_DISCNTID"));
            //data.putAll(new DataMap(data.getString("SALEACITVEDATA")));
            //data.remove("SALEACITVEDATA");
    		saleActive.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
    		
            IDataset rtDataset = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleActive);//SS.SaleActiveRegSVC.tradeReg

            if (!SaleActiveUtil.isWideNetAciveTrade(data.getString("SALEACTIVE_PRODUCT_ID"),data.getString("SALEACTIVE_PACKAGE_ID"),btd.getRD().getUca().getUser().getEparchyCode()))//判断如果是配了commpara942即预受理转正式的配置中的预受理包，则判断为预受理营销活动业务类型230
            if(rtDataset!=null&&rtDataset.size()>0){
            	addLimitTradeId(btd, reqData, rtDataset.getData(0).getString("TRADE_ID", ""), btd.getTradeId());
            }
        	btd.getMainTradeData().setRsrvStr7(rtDataset.getData(0).getString("TRADE_ID", ""));
    	}
    	
    	//BUS201907310012关于开发家庭终端调测费的需求
    	if(reqData.isSaleactive2()){
    		IData data = new DataMap(reqData.getWidenetMoveNew());
			IData saleActive2 = new DataMap();
			String mSerialNumber = data.getString("SERIAL_NUMBER","");
	    	if(mSerialNumber.startsWith("KD_1")){
	    		mSerialNumber = mSerialNumber.substring(3);
	    	}
			
	    	saleActive2.put("SERIAL_NUMBER", mSerialNumber);
	    	saleActive2.put("PRODUCT_ID", data.getString("SALEACTIVE_PRODUCT_ID2",""));
	    	saleActive2.put("PACKAGE_ID", data.getString("SALEACTIVE_PACKAGE_ID2",""));
			//saleActive2.put("SALE_STAFF_ID", data.getString("SALE_STAFF_ID"));
			//saleActive2.put("CAMPN_TYPE", data.getString("SALEACTIVE_CAMPN_TYPE"));
	    	saleActive2.put("START_DATE", SysDateMgr.getSysDate());
	    	saleActive2.put("END_DATE", SysDateMgr.getTheLastTime());
			saleActive2.put("BOOK_DATE", data.getString("SALEACTIVE_BOOK_DATE"));
			saleActive2.put("TRADE_TYPE_CODE", "240");
			saleActive2.put("WIDE_ORDER_TYPE_CODE", "606");
			saleActive2.put("ORDER_TYPE_CODE", "606");
			saleActive2.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
			saleActive2.put("WIDENET_MOVE_SALEACTIVE_SIGN", "1");
			if(!"".equals(cancelTrade.getString("CANCEL_TRADE_ID"))){
				saleActive2.put("ACCEPT_TRADE_ID", cancelTrade.getString("CANCEL_TRADE_ID"));
			}else{
				saleActive2.put("ACCEPT_TRADE_ID", btd.getTradeId());
			}
			saleActive2.put("WIDE_MOVE_ACTIVE_PCKID", data.getString("SALEACTIVE_PACKAGE_ID2"));
			saleActive2.put("WIDE_MOVE_ACTIVE_PRODID", data.getString("SALEACTIVE_PRODUCT_ID2"));
			String allSvc = "|";
			if(idataSet!=null&&idataSet.size()>0){
				for(int k=0;k<idataSet.size();k++){
					allSvc = allSvc + idataSet.getData(k).getString("PKG_CHECK_SVCID") + "|";
				}
			}
			saleActive2.put("WIDE_USER_SELECTED_SERVICEIDS", allSvc);
			saleActive2.put("WIDE_MOVE_ACTIVE_TRADEID", btd.getTradeId());			
			
            String checkMode = btd.getRD().getCheckMode();
            saleActive2.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
            saleActive2.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
    		
            IDataset rtDataset = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleActive2);

            if (!SaleActiveUtil.isWideNetAciveTrade(data.getString("SALEACTIVE_PRODUCT_ID2",""),data.getString("SALEACTIVE_PACKAGE_ID2",""),btd.getRD().getUca().getUser().getEparchyCode()))//判断如果是配了commpara942即预受理转正式的配置中的预受理包，则判断为预受理营销活动业务类型230
            if(rtDataset!=null&&rtDataset.size()>0){
            	addLimitTradeId(btd, reqData, rtDataset.getData(0).getString("TRADE_ID", ""), btd.getTradeId());
            }
        	String tradeId = rtDataset.getData(0).getString("TRADE_ID", "");
            if(null == btd.getMainTradeData().getRsrvStr7() || "".equals(btd.getMainTradeData().getRsrvStr7()))
            {
          	  btd.getMainTradeData().setRsrvStr7("|"+tradeId);
            }
            else
            {
          	  btd.getMainTradeData().setRsrvStr7(btd.getMainTradeData().getRsrvStr7()+"|"+tradeId);
            }
    	}
    	//BUS201907310012关于开发家庭终端调测费的需求
    }
    
    /**
     * 光猫处理
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void genModelChgTrade(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {
    	if(reqData.isChgModel()){
    		String printModelInfo = "";
    		WidenetMoveSVC ws = new WidenetMoveSVC();
    		IData idata = new DataMap();
    		idata.put("EPARCHY_CODE", btd.getRD().getUca().getUser().getEparchyCode());
    		idata.put("PARAM_ATTR", "210");
    		idata.put("PARAM_CODE", "PRINT_MODEL_INFO");
    		IDataset prints = ws.getSaleActiveComm(idata);
    		IData print = prints.getData(0);
    		/*if("0".equals(reqData.getModelOldNew())){
    			printModelInfo = print.getString("PARA_CODE22");
    		}else if("2".equals(reqData.getModelOldNew())){
    			printModelInfo = print.getString("PARA_CODE21");
    		}else if("3".equals(reqData.getModelOldNew())){
    			printModelInfo = print.getString("PARA_CODE20");
    		}
    		btd.getMainTradeData().setRsrvStr10(printModelInfo);*/
    		
    		String modelMode = reqData.getModelMode();
    		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    		
    		//处理光猫取消的业务
    		String printModelOld = "";
    		IData param=new DataMap();
    		param.put("SERIAL_NUMBER", serialNumber);
            IDataset infos = ws.getModelInfo(param);
            if(infos!=null&&infos.size()>0){
            	for(int i=0;i<infos.size();i++){
            		//租借未归还的光猫
            		if(!"1".equals(infos.getData(i).getString("RSRV_STR9",""))&&!"2".equals(infos.getData(i).getString("RSRV_STR9",""))&&("FTTH".equals(infos.getData(i).getString("RSRV_VALUE_CODE"))||"FTTH_GROUP".equals(infos.getData(i).getString("RSRV_VALUE_CODE")))) {
            			String startDate = infos.getData(i).getString("START_DATE");
            			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);
            			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
            			if(midMonths>0){
            				OtherTradeData otherT = new OtherTradeData(infos.getData(i));
            				otherT.setModifyTag(BofConst.MODIFY_TAG_UPD);
            				otherT.setRsrvStr9("1");
            				otherT.setRsrvDate1(SysDateMgr.getSysTime());
            				printModelOld = print.getString("PARA_CODE22");
            				btd.add(serialNumber, otherT);
            			}
            		}
            	}
            }
        	
        	//商务宽带租借未退还
    		param.put("USER_ID", reqData.getUca().getUserId());
    		param.put("RSRV_VALUE_CODE", "FTTH_GROUP");
        	IDataset busiFtthInfos = UserOtherInfoQry.vipBXInfo(param);
            if(busiFtthInfos!=null&&busiFtthInfos.size()>0){
            	for(int i=0;i<busiFtthInfos.size();i++){
            		if(!"1".equals(busiFtthInfos.getData(i).getString("RSRV_STR9"))) {
            			String startDate = busiFtthInfos.getData(i).getString("START_DATE");
            			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);
            			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
            			if(midMonths>0){
            				OtherTradeData otherT = new OtherTradeData(busiFtthInfos.getData(i));
            				otherT.setModifyTag(BofConst.MODIFY_TAG_UPD);
            				otherT.setRsrvStr9("1");
            				otherT.setRsrvDate1(SysDateMgr.getSysTime());
            				printModelOld = print.getString("PARA_CODE22");
            				btd.add(serialNumber, otherT);
            			}
            		}
            	}
            }
        	
    		btd.getMainTradeData().setRsrvStr10(printModelOld);
        	if("".equals(modelMode)){
        		return;
    		}
    		
        	String citycode=btd.getRD().getUca().getUser().getCityCode();
        	String deposit="",depositStatus="0",strModelMode="0";
        	
        	//改动，以开户的为准。
            if("3".equals(modelMode)){
                deposit = "0";
                strModelMode="3";
            }else if("2".equals(modelMode)){
                deposit = "0";//reqData.getModelPurchase();
                strModelMode="2";
    		}else{
    			strModelMode="0";
    			deposit = reqData.getModelDeposit();
    			printModelInfo = print.getString("PARA_CODE20") + Integer.parseInt(deposit)/100 + print.getString("PARA_CODE21");
    			
        		/*param.put("SERIAL_NUMBER", serialNumber);
        		//修改用户当前生效租借光猫为移机未退还
            	//查看用户是否租借过光猫
        		WidenetMoveSVC ws = new WidenetMoveSVC();
                IDataset infos = ws.getModelInfo(param);
            	for(int i=0;i<infos.size();i++){
            		//租借未归还的光猫
            		if("0".equals(infos.getData(i).getString("RSRV_TAG1"))) {
            			String startDate = infos.getData(i).getString("START_DATE");
            			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);
            			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
            			if(midMonths>0){
            				OtherTradeData otherT = new OtherTradeData(infos.getData(i));
            				otherT.setModifyTag(BofConst.MODIFY_TAG_UPD);
            				otherT.setRsrvStr9("1");
            				btd.add(serialNumber, otherT);
            			}
            		}
            	}*/
        		
        		//3、获取默认账户  （acct_id)
    			if(!reqData.isIsBusiness() && !deposit.equals("0")){
        	    	//4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
    	    		StringBuffer depositeNotes = getCashItemType();
    	    		String serialNumberWide = serialNumber;
    	        	if(serialNumber.startsWith("KD_")){
    	        		serialNumberWide = serialNumber.substring(3);
    	        	}
    	    		IDataset allAccountDeposit = AcctCall.queryAccountDepositBySn(serialNumberWide);
    	            String [] balanceDepositCodesArray = depositeNotes.toString().split("\\|");
    	            int totalDepositBalance = 0;
    	            if (IDataUtil.isNotEmpty(allAccountDeposit))
    	            {
    	                for (int i = 0, size = allAccountDeposit.size(); i < size; i++)
    	                {
    	                    IData depositData = allAccountDeposit.getData(i);
    	                    String tempDepositCode = depositData.getString("DEPOSIT_CODE", "");
    	                    String depositBalance = depositData.getString("DEPOSIT_BALANCE", "0");
    	                    
    	                    //判断当前用户存折是否属于可转出的现金类存折
    	                    for (int j = 0,length = balanceDepositCodesArray.length; j < length; j++ )
    	                    {
    	                        if (balanceDepositCodesArray[j].contains(tempDepositCode))
    	                        {
    	                            totalDepositBalance += Integer.parseInt(depositBalance);
    	                            break;
    	                        }
    	                    }
    	                }
    	            }
    	    		
        	    	if(totalDepositBalance<Integer.parseInt(deposit)){
        	    		CSAppException.appError("61311", "账户存折可用余额不足，请先办理缴费。账户余额："+totalDepositBalance/100+"元，押金金额："+Integer.parseInt(deposit)/100+"元");
        	    	}else{
        	    		//获取用户转出的现金存折
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
            	    		depositeNotes.append("0|1|145|184|185|200|223|400|48|58|604|605|606|771|773");
            	    	}
        	    		
        	    		//5、调账务提供的接口将现金存折的钱转到宽带光猫押金存折； 
        	    		if(Integer.parseInt(deposit) > 0)
        	    		{
	        	    		IData inparams=new DataMap();
	        	    		inparams.put("SERIAL_NUMBER", serialNumberWide);
	        	    		inparams.put("OUTER_TRADE_ID", btd.getTradeId());
	        	    		inparams.put("DEPOSIT_CODE_OUT", depositeNotes);
	        	    		inparams.put("DEPOSIT_CODE_IN", "9002");
	        	    		inparams.put("TRADE_FEE", deposit);
	        	    		inparams.put("CHANNEL_ID", "15000");
	        	    		inparams.put("TRADE_CITY_CODE", citycode);
	        	    		inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	        	    		inparams.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	        	    		inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        	    		inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        	    		IData inAcct=AcctCall.transFeeInADSL(inparams);
	        	    		String result=inAcct.getString("RESULT_CODE","");
	        	    		if(!"".equals(result) && "0".equals(result)){
	        	    			// 成功！ 处理other表
	        	    		}else{
	        	    			CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInFTTH转存押金错误:"+inAcct.getString("RESULT_INFO"));
	        	    		}
        	    		}
        	    	}
        	    	if(!"".equals(printModelOld))	printModelInfo = printModelInfo + "~~" + printModelOld;
            		btd.getMainTradeData().setRsrvStr10(printModelInfo);
    			}
    		}

	        createOtherTradeInfo(btd, deposit, depositStatus, strModelMode,reqData); 
    	}
    }

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd,String deposit,String depositStatus,String strModelMode, WidenetMoveRequestData reqData) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValue("FTTH光猫申领");
        if (serialNumber.substring(0, 4).equals("KD_1"))
        {
        	otherTradeData.setUserId(reqData.getUserIdMobileA());
            otherTradeData.setRsrvValueCode("FTTH");
        }else{
            otherTradeData.setUserId(btd.getRD().getUca().getUser().getUserId());
            otherTradeData.setRsrvValueCode("FTTH_GROUP");
            otherTradeData.setRsrvStr3(serialNumber);//宽带号码
            otherTradeData.setRsrvStr4(serialNumber.substring(3,serialNumber.length()-4));//主号号码
            //otherTradeData.setRsrvStr5(kdTradeId);//开户的TRADE_ID
        }
        
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(reqData.getRemark()); 
        otherTradeData.setRsrvStr2(deposit);//押金
        //BUG20191028162821调测费以及度假宽带问题优化:宽带移机撤单去调用接口退光猫费用
        if(Integer.parseInt(deposit) > 0)
        {
        	otherTradeData.setRsrvStr7("0");//押金状态
        }
        //BUG20191028162821调测费以及度假宽带问题优化:宽带移机撤单去调用接口退光猫费用

        otherTradeData.setRsrvStr8(btd.getTradeId());
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());
        otherTradeData.setRsrvTag1(strModelMode);//申领模式  0租赁，1购买，2赠送，3自备
        otherTradeData.setRsrvTag2("1");//光猫状态  1:申领，2:更改，3:退还，4:丢失
        
        IDataset depositInfo = CommparaInfoQry.getCommpara("CSM", "6131", "2", reqData.getUca().getUserEparchyCode());
        String isExchangeModel = reqData.getExchangeModel();
        if(depositInfo!=null&&depositInfo.size()>0&&"0".equals(strModelMode)&&Integer.parseInt(deposit)<depositInfo.getData(0).getInt("PARA_CODE1")&&"3".equals(isExchangeModel)){
        	otherTradeData.setRsrvTag3("1");
        }
        btd.add(serialNumber, otherTradeData);
        if("0".equals(strModelMode)||"1".equals(strModelMode)||"2".equals(strModelMode))btd.getMainTradeData().setRsrvStr2("1");
    }

    private void createDelTradeRelation(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {
        IDataset relationUUInfos = RelaUUInfoQry.isMasterAccount(reqData.getUca().getUserId(), "77");
        if (IDataUtil.isEmpty(relationUUInfos))
        {
            relationUUInfos = RelaUUInfoQry.isMasterAccount(reqData.getUca().getUserId(), "78");
        }
        if (IDataUtil.isNotEmpty(relationUUInfos))
        {
            RelationTradeData relationtd = new RelationTradeData(relationUUInfos.getData(0));
            relationtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            relationtd.setEndDate(SysDateMgr.getSysTime());
            btd.add(btd.getRD().getUca().getSerialNumber(), relationtd);
        }
    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createDelTradeWidenet(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, IData widenetInfo) throws Exception
    {
        WideNetTradeData wtd = new WideNetTradeData(widenetInfo);
        wtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        wtd.setEndDate(SysDateMgr.getSysTime());
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }

    /**
     * 特殊移机主账号为普通账号修改RSRV_STR3 字段
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createModifySpecTradeWidenet(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {
        IDataset newWidenetInfos = WidenetInfoQry.getUserWidenetInfo(reqData.getGponUserId());
        IData newWidenetInfo = newWidenetInfos.getData(0);
        WideNetTradeData wtd = new WideNetTradeData(newWidenetInfo);
        if ("2".equals(reqData.getPreWideType()))
        {
            wtd.setRsrvStr3("2");
        }
        else
        {
            wtd.setRsrvStr3("1");
        }
        wtd.setRemark("用户宽带特殊移机操作");
        wtd.setRsrvNum3("");
        wtd.setRsrvNum4("");
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }

    private void genTradeRelaInfoWideA(String userIdVirtual, String userId, String serialNumber, String roleCodeB, String relationTypeCode, BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {

        RelationTradeData rtdA = new RelationTradeData();
        rtdA.setUserIdA(userIdVirtual);
        rtdA.setUserIdB(userId);
        rtdA.setSerialNumberA("-1");
        rtdA.setInstId(SeqMgr.getInstId());
        rtdA.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtdA.setRelationTypeCode(relationTypeCode);
        rtdA.setSerialNumberB(serialNumber);
        rtdA.setRoleCodeA("0");
        rtdA.setRoleCodeB(roleCodeB);// 1表示主卡
        rtdA.setOrderno("0");
        rtdA.setStartDate(SysDateMgr.getSysTime());
        rtdA.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtdA);
    }

    private void genTradeRelaInfoWideB(String userIdVirtual, String userId, String serialNumber, String roleCodeB, String relationTypeCode, BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData) throws Exception
    {

        RelationTradeData rtdB = new RelationTradeData();
        rtdB.setUserIdA(userIdVirtual);
        rtdB.setUserIdB(userId);
        rtdB.setSerialNumberA("-1");
        rtdB.setInstId(SeqMgr.getInstId());
        rtdB.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtdB.setRelationTypeCode(relationTypeCode);
        rtdB.setSerialNumberB(serialNumber);
        rtdB.setRoleCodeA("0");
        rtdB.setRoleCodeB(roleCodeB);// 2表示副卡
        rtdB.setOrderno("0");
        rtdB.setStartDate(SysDateMgr.getSysTime());
        rtdB.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtdB);
    }
    

    /**
     * 产品变更台账处理
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void genProductChgTrade(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, String mainSvcId, IDataset idataSet) throws Exception
    {
    	//BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
    	if(!reqData.isChgProd()) return ;
		UcaData uca = reqData.getUca();
		String productChangeDate = null;
		String oldProductEndDate = null;
		boolean isProductChange = false;
		// 取得用户选择开通关闭或者修改的元素
		List<ProductModuleData> userSelected = reqData.getProductElements();
		if (userSelected == null)
		{
			userSelected = new ArrayList<ProductModuleData>();
		}
		List<ProductModuleData> operElements = new ArrayList<ProductModuleData>();
		operElements.addAll(userSelected);
        
		btd.getMainTradeData().setRsrvStr1(uca.getBrandCode());
		btd.getMainTradeData().setRsrvStr4(uca.getProductId());
		// 设置预约时间 不是预约 设置为受理时间
		if (reqData.isBookingTag())
		{
			btd.getMainTradeData().setRsrvStr3(reqData.getBookingDate());
		}
		else
		{
			btd.getMainTradeData().setRsrvStr3(reqData.getAcceptTime());
		}
		
		if (reqData.getNewMainProduct() != null && !uca.getProductId().equals(reqData.getNewMainProduct().getProductId()))
		{
			btd.getMainTradeData().setRsrvStr5(UProductInfoQry.getProductNameByProductId(reqData.getNewMainProduct().getProductId()));
			if (uca.getUserNextMainProduct() != null)
			{
				CSAppException.apperr(ProductException.CRM_PRODUCT_195);
			}
			// 主产品变更
			isProductChange = true;
			btd.getMainTradeData().setProductId(reqData.getNewMainProduct().getProductId());
			btd.getMainTradeData().setBrandCode(reqData.getNewMainProduct().getBrandCode());
			
			//查询虚拟用户的userid
			String serialNumber = reqData.getUca().getSerialNumber();
			String virUserId="";
			String virSerialNumber="";
			if(serialNumber.startsWith("KD")){
				/*virSeriNumber = "KV_" + serialNumber.substring(3);
				IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", virSeriNumber);
				if(!(userInfo!=null&&userInfo.size()>0)){
					CSAppException.appError("61311", "宽带用户"+virSeriNumber+"虚拟用户资料查询不存在");
				}*/
				
				IDataset relaUU = RelaUUInfoQry.getRelationUusBySnBTypeCode(serialNumber, "47");
				if(!(relaUU!=null&&relaUU.size()>0)){
					CSAppException.appError("61311", "宽带用户"+serialNumber+"的虚拟用户资料查询不存在");
				}
				
				virUserId = relaUU.getData(0).getString("USER_ID_A");
				
				if (StringUtils.isNotBlank(virUserId))
				{
				    
				    IData virUserInfo = UcaInfoQry.qryUserInfoByUserId(virUserId);
				    
				    if (IDataUtil.isEmpty(virUserInfo))
				    {
				        CSAppException.appError("-1", "宽带虚拟用户不存在！");
				    }
				    
				    //设置宽带虚拟用户UCA
				    UcaData virUca = new UcaData();
				    UserTradeData virUserTradeData = new UserTradeData(virUserInfo);
				    virUca.setUser(virUserTradeData);
		            DataBusManager.getDataBus().setUca(virUca);
				    
				    virSerialNumber = virUserInfo.getString("SERIAL_NUMBER");
				}
	    	}
			
			// 计算新的主产品的生效时间和老主产品的失效时间
			String newProductId = reqData.getNewMainProduct().getProductId();
			String oldProductId = reqData.getUca().getProductId();
			if (reqData.isEffectNow())
			{
				productChangeDate = reqData.getAcceptTime();
			}
			else if (reqData.isBookingTag())
			{
				productChangeDate = reqData.getBookingDate();
			}
			else
			{
				productChangeDate = this.getProductChangeDate(oldProductId, newProductId, reqData);
			}
			oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);
			// 生成主产品的相关台帐
			this.createProductTrade(newProductId, oldProductId, productChangeDate, oldProductEndDate, reqData, btd, virUserId, virSerialNumber);
			List<ProductTradeData> userProducts = uca.getUserProducts();
			// 拼装需要继承的元素
			List<SvcTradeData> userSvcs = uca.getUserSvcs();
			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
			int svcSize = userSvcs.size();
			int discntSize = userDiscnts.size();
			IDataset newProductElements = ProductInfoQry.getProductElements(reqData.getNewMainProduct().getProductId(), uca.getUserEparchyCode());
			for (int i = 0; i < svcSize; i++)
			{
				SvcTradeData userSvc = userSvcs.get(i);
				if (!this.isNeedDeal(userProducts, userSvc.getProductId()))
				{
					continue;
				}
				if (userSvc.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_SVC, userSvc.getElementId(), userSvc.getInstId(), userSelected))
				{
					// 是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userSvc.getElementId(), userSvc.getElementType());
					if (transElement != null)
					{
						if (!userSvc.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userSvc.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							SvcTradeData svcTradeData = userSvc.clone();
							svcTradeData.setRsrvStr3(svcTradeData.getProductId());
							svcTradeData.setRsrvStr4(svcTradeData.getPackageId());
							svcTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							svcTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							svcTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), svcTradeData);
						}
					}
					else
					{
						// 不能继承,则删除
						SvcData delSvcData = new SvcData(userSvc.toData());
						delSvcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
						operElements.add(delSvcData);
					}
				}
			}
			for (int i = 0; i < discntSize; i++)
			{
				DiscntTradeData userDiscnt = userDiscnts.get(i);
				if (userDiscnt.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_DISCNT, userDiscnt.getElementId(), userDiscnt.getInstId(), userSelected))
				{
					if (!this.isNeedDeal(userProducts, userDiscnt.getProductId()))
					{
						continue;
					}
					// 是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userDiscnt.getElementId(), userDiscnt.getElementType());
					if (transElement != null)
					{
						if (!userDiscnt.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userDiscnt.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							DiscntTradeData discntTradeData = userDiscnt.clone();
							discntTradeData.setRsrvStr3(userDiscnt.getProductId());
							discntTradeData.setRsrvStr4(userDiscnt.getPackageId());
							discntTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							discntTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							discntTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), discntTradeData);
						}
					}
					else
					{
						// 不能继承,则删除
						DiscntData delDiscnt = new DiscntData(userDiscnt.toData());
						delDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
						// delDiscnt.setEndDate(oldProductEndDate);屏蔽后续重算 接口过来流量包时间会有问题
						operElements.add(delDiscnt);
					}
				}
			}
			// 针对元素修改时的处理，需要转换产品ID和包ID
			int selectSize = userSelected.size();
			for (int i = 0; i < selectSize; i++)
			{
				ProductModuleData pmd = userSelected.get(i);
				if (BofConst.MODIFY_TAG_UPD.equals(pmd.getModifyTag()))
				{
					IData transElement = this.getTransElement(newProductElements, pmd.getElementId(), pmd.getElementType());
					if (transElement != null)
					{
						if (!transElement.getString("PRODUCT_ID", "").equals(pmd.getProductId()) || !transElement.getString("PACKAGE_ID", "").equals(pmd.getPackageId()))
						{
							pmd.setProductId(transElement.getString("PRODUCT_ID"));
							pmd.setPackageId(transElement.getString("PACKAGE_ID"));
						}
					}
				}
			}

			// 转换主产品时，处理必选元素
			IDataset forceElements = this.getProductForceElement(newProductElements);
			if (IDataUtil.isNotEmpty(forceElements))
			{
				int forceSize = forceElements.size();
				for (int i = 0; i < forceSize; i++)
				{
					IData forceElement = forceElements.getData(i);
					if (!this.isExistInUserSelected(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), null, userSelected))
					{
						// 不在用户的选择列表里面
						IDataset attrs = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), uca.getUserEparchyCode());
						List<AttrData> attrDatas = new ArrayList<AttrData>();
						if (IDataUtil.isNotEmpty(attrs))
						{
							int length = attrs.size();
							for (int j = 0; j < length; j++)
							{
								IData attr = attrs.getData(j);
								AttrData attrData = new AttrData();
								attrData.setAttrCode(attr.getString("ATTR_CODE"));
								attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
								attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								attrDatas.add(attrData);
							}
						}
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							if (uca.getUserSvcBySvcId(forceElement.getString("ELEMENT_ID")).size() > 0)
							{
								continue;
							}

							SvcData svcData = new SvcData(forceElement);
							svcData.setStartDate(SysDateMgr.getSysDate());
							svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							if (attrDatas.size() > 0)
							{
								svcData.setAttrs(attrDatas);
							}
							operElements.add(svcData);
						}
						else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							DiscntData discntData = null;

							String maxEndDate = reqData.getAcceptTime();

							List<DiscntTradeData> existUserDiscnts = uca.getUserDiscntByDiscntId(forceElement.getString("ELEMENT_ID"));

							if (existUserDiscnts != null && existUserDiscnts.size() > 0)// 必选元素是否已经存在
							{
								for (DiscntTradeData existUserDiscnt : existUserDiscnts)
								{
									maxEndDate = SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND);

									if (maxEndDate.compareTo(SysDateMgr.decodeTimestamp(existUserDiscnt.getEndDate(), SysDateMgr.PATTERN_STAND)) < 0)
									{
										maxEndDate = existUserDiscnt.getEndDate();
									}
								}

								if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.END_TIME_FOREVER, SysDateMgr.PATTERN_STAND)) < 0)
								{
									if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(productChangeDate, SysDateMgr.PATTERN_STAND)) > 0)// 已经存在的必选元素大于本次产品生效时间
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(SysDateMgr.addSecond(maxEndDate, 1));
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, SysDateMgr.addSecond(maxEndDate, 1)));
									}
									else
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(productChangeDate);
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, productChangeDate));
									}
								}
							}
							else
							{
								discntData = new DiscntData(forceElement);
							}

							if (discntData != null)
							{
								discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								if (attrDatas.size() > 0)
								{
									discntData.setAttrs(attrDatas);
								}
								operElements.add(discntData);
							}
						}
					}
				}
			}
			
			//转换主产品时，对虚拟用户绑定的优惠修改
			if(virUserId!=null&&!"".equals(virUserId))
				createTradeVirtualDiscnt(btd, reqData, virUserId, virSerialNumber, productChangeDate, oldProductEndDate);
			
		}
		else
		{
			// 仅元素变更时，需要检查用户是否存在预约产品，如果存在预约产品的话，用户操作的元素如果在当前产品下没有，则要检查用户在预约产品下是否存在，如果存在，则根据预约产品的配置计算生失效时间
			ProductTradeData nextProduct = uca.getUserNextMainProduct();
			String sysDate = reqData.getAcceptTime();
			if (nextProduct != null)
			{
				IDataset oldProductElements = ProductInfoQry.getProductElements(uca.getProductId(), uca.getUserEparchyCode());
				int size = operElements.size();
				for (int i = 0; i < size; i++)
				{
					ProductModuleData pmd = operElements.get(i);
					IData oldConfig = this.getTransElement(oldProductElements, pmd.getElementId(), pmd.getElementType());
					if (oldConfig == null)// 新产品下元素
					{
						if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
						{
							pmd.setStartDate(nextProduct.getStartDate());
						}
						else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
						{
							pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
						}
					}
					else
					{
						pmd.setPkgElementConfig(oldConfig.getString("PACKAGE_ID"));
						// 如果元素是删除 且开始时间大于系统时间 那么终止此元素的结束时间为开始时间的前一秒
						if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
						{
							pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
						}
					}
				}
			}
		}

		ProductTradeData nextProduct = uca.getUserNextMainProduct();

		ProductTimeEnv env = new ProductTimeEnv();
		if (reqData.isEffectNow())
		{
			env.setBasicAbsoluteStartDate(reqData.getAcceptTime());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(reqData.getAcceptTime()));
		}
		// 仅元素预约变更且没有预约产品情况
		else if (!isProductChange && reqData.isBookingTag() && nextProduct == null)
		{
			env.setBasicAbsoluteStartDate(reqData.getBookingDate());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(reqData.getBookingDate()));
		}
		else if (isProductChange)
		{
			env.setBasicAbsoluteStartDate(productChangeDate);
			env.setBasicAbsoluteCancelDate(oldProductEndDate);
		}
		// 存在预约产品变更 且 本次业务又预约 报错
		else if (reqData.isBookingTag() && nextProduct != null)
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_243);
		}
		for(int i=0;i<operElements.size();i++){
			if(operElements.get(i).getElementType().equals(BofConst.ELEMENT_TYPE_CODE_SVC)){
				ProductModuleData prmd = operElements.get(i);
				String modifyTag = prmd.getModifyTag();
	            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)||BofConst.MODIFY_TAG_INHERIT.equals(modifyTag))
	            {
	                String eleId = prmd.getElementId();
	                
	                IData elementCfg = ProductElementsCache.getElement(prmd.getProductId(), eleId, "S");
	                if(!"1".equals(elementCfg.getString("IS_MAIN"))){
	                	mainSvcId = eleId; //
	                	IData idata = new DataMap(); 
	                	idata.put("PKG_CHECK_SVCID", eleId);
	                	idataSet.add(idata);
	                }
	            }
			}
		}
		
		// 元素拼串处理
		ProductModuleCreator.createProductModuleTradeData(operElements, btd, env);
		
		//修改服务生效时间为立即生效
		List<BaseTradeData> svcTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		String oldRate = "",newRate = "";
		if(svcTradeData!=null&&svcTradeData.size()>0){
			for(int i=0;i<svcTradeData.size();i++){
				SvcTradeData svc = (SvcTradeData)svcTradeData.get(i);
				IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "4000", svc.getElementId(), btd.getRD().getUca().getUserEparchyCode());
	            if (IDataUtil.isNotEmpty(commparaInfos) && commparaInfos.size() == 1)
	            {
	            	if("0".equals(svc.getModifyTag()))
	            	{
	            		newRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            		
	            		
		            	List<BaseTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		            	if(discntTradeDatas!=null&&discntTradeDatas.size()>0){
		            		for(int y=0;y<discntTradeDatas.size();y++)
		            		{
		            			DiscntTradeData dis = (DiscntTradeData)discntTradeDatas.get(y);
		            			String discntCode = dis.getDiscntCode();
		            			if (commparaInfos.getData(0).getString("PARA_CODE4","").equals(discntCode))
		                        {
		            				newRate =commparaInfos.getData(0).getString("PARA_CODE5");
		            				break;
		                        }
		            		}
	            		}
	            		
	            	}	
	            		
	            	if("1".equals(svc.getModifyTag())) oldRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            	if("2".equals(svc.getModifyTag())||"U".equals(svc.getModifyTag())){
	            		newRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            		oldRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            	}
	            }
				
				//移机时产品变更， 与服开约定不同步svc和svcstate表。
				svc.setIsNeedPf("0");
			}
		}
		List<BaseTradeData> wideTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
		if(wideTradeData!=null&&wideTradeData.size()>0){
			for(int i=0;i<wideTradeData.size();i++){
				WideNetTradeData wntd = (WideNetTradeData)wideTradeData.get(i);
				if(BofConst.MODIFY_TAG_ADD.equals(wntd.getModifyTag())){
					wntd.setRsrvNum3(oldRate);
					wntd.setRsrvNum4(newRate);

                    //变更非FTTH制式
                    System.out.println("========WidenetMoveTrade===wideNetType:" + wntd.getRsrvStr2());
                    if (!BofConst.WIDENET_TYPE_FTTH.equals(wntd.getRsrvStr2())
                            && !BofConst.WIDENET_TYPE_TTFTTH.equals(wntd.getRsrvStr2())) {
                        continue;
                    }

                    //移机到非城区
                    String deviceId = wntd.getRsrvNum1();
                    IData param = new DataMap();
                    param.put("DEVICE_ID",deviceId);
                    IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
                    System.out.println("========WidenetMoveTrade===城区判断==param:"+param+";rs:"+rs);
                    if(IDataUtil.isNotEmpty(rs))
                    {
                        IData data = rs.first();
                        if("0".equals(data.getString("status",""))){
                            continue;
                        }
                    }

                    String serialNumber = reqData.getUca().getSerialNumber();
                    if (serialNumber.startsWith("KD_")) {
                        serialNumber = serialNumber.substring(3);
                    }
                    System.out.println("========WidenetMoveTrade===serialNumber:" + serialNumber);

                    IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
                    System.out.println("========WidenetMoveTrade===userInfo:" + userInfo);

                    if (IDataUtil.isEmpty(userInfo)) {
                        continue;
                    }

                    String userId = userInfo.getString("USER_ID");

                    IDataset userSPAMDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(userId, "80176874");
                    System.out.println("========WidenetMoveTrade===userSPAMDiscnts:" + userSPAMDiscnts);

                    if (IDataUtil.isNotEmpty(userSPAMDiscnts)) {//有300M免费提速包优惠
                        String discntEndDate = userSPAMDiscnts.getData(0).getString("END_DATE");//优惠截止时间
                        System.out.println("========WidenetMoveTrade==productChangeDate="+productChangeDate+";discntEndDate="+discntEndDate+";timeCompare:" + SysDateMgr.compareTo(productChangeDate,discntEndDate));

                        if(SysDateMgr.compareTo(productChangeDate,discntEndDate) <= 0) {//开始时间大于优惠截止时间，则不做处理
                            if (Integer.valueOf(newRate) < (300 * 1024)) {//此次变更小于300M
                                wntd.setRsrvNum4("307200");//300M速率保留
                            }
                        }

                    }
				}
			}
		}
		List<BaseTradeData> svcStateData = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
		if(svcStateData!=null&&svcStateData.size()>0){
			for(int i=0;i<svcStateData.size();i++){
				SvcStateTradeData svcState = (SvcStateTradeData)svcStateData.get(i);
				//移机时产品变更， 与服开约定不同步svc和svcstate表。
				svcState.setIsNeedPf("0");
			}
		}
    }
    


	public String getProductChangeDate(String oldProductId, String newProductId, WidenetMoveRequestData request) throws Exception
	{
		String productChangeDate = null;
//		IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
		
		IDataset productTrans = UpcCall.queryJoinEnableModeBy2OfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, oldProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId);
		if (IDataUtil.isNotEmpty(productTrans))
		{
			IData productTran = productTrans.getData(0);
			String enableTag = productTran.getString("ENABLE_MODE");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = request.getAcceptTime();
			}
			else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效
				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			}
			else if (enableTag.equals("3"))
			{// 按原产品的生效方式
				ProductData oldProductData = new ProductData(request.getUca().getProductId());
				String enableTagOld = oldProductData.getEnableTag();

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagOld.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
			else if (enableTag.equals("4"))
			{// 按新产品的生效方式
				String enableTagNew = request.getNewMainProduct().getEnableTag();

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagNew.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		else
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_4);
		}
		return productChangeDate;
	}
    

	public void createProductTrade(String newProductId, String oldProductId, String productChangeDate, String oldProductEndDate, WidenetMoveRequestData request, BusiTradeData btd, String virUserId, String virSerialNumber) throws Exception
	{
		UcaData uca = request.getUca();

		// 新主产品新增台帐
		ProductTradeData newProductTradeData = new ProductTradeData();
		newProductTradeData.setProductId(newProductId);
		newProductTradeData.setBrandCode(request.getNewMainProduct().getBrandCode());
		newProductTradeData.setProductMode(request.getNewMainProduct().getProductMode());
		newProductTradeData.setStartDate(productChangeDate);
		newProductTradeData.setEndDate(SysDateMgr.getTheLastTime());
		newProductTradeData.setInstId(SeqMgr.getInstId());
		newProductTradeData.setUserId(uca.getUserId());
		newProductTradeData.setUserIdA("-1");
		newProductTradeData.setMainTag("1");
		newProductTradeData.setOldProductId(uca.getProductId());
		newProductTradeData.setOldBrandCode(uca.getBrandCode());
		newProductTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

		// 老主产品结束台帐
		ProductTradeData oldProduct = uca.getUserProduct(oldProductId).get(0);
		ProductTradeData oldProductTrade = oldProduct.clone();
		oldProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
		oldProductTrade.setEndDate(oldProductEndDate);
		btd.add(uca.getSerialNumber(), newProductTradeData);
		btd.add(uca.getSerialNumber(), oldProductTrade);
		
		if(virUserId!=null&&!"".equals(virUserId)){
		    
			// 新虚拟用户产品新增台帐
			ProductTradeData newVirProductTradeData = new ProductTradeData();
			newVirProductTradeData.setProductId(newProductId);
			newVirProductTradeData.setBrandCode(request.getNewMainProduct().getBrandCode());
			newVirProductTradeData.setProductMode(request.getNewMainProduct().getProductMode());
			newVirProductTradeData.setStartDate(productChangeDate);
			newVirProductTradeData.setEndDate(SysDateMgr.getTheLastTime());
			newVirProductTradeData.setInstId(SeqMgr.getInstId());
			newVirProductTradeData.setUserId(virUserId);
			newVirProductTradeData.setUserIdA("-1");
			newVirProductTradeData.setMainTag("1");
			newVirProductTradeData.setOldProductId(uca.getProductId());
			newVirProductTradeData.setOldBrandCode(uca.getBrandCode());
			newVirProductTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

			// 老虚拟用户产品结束台帐
			IDataset uPIQ = UserProductInfoQry.queryMainProduct(virUserId);
			if(uPIQ!=null&&uPIQ.size()>0){
				ProductTradeData oldVirProductTrade = new ProductTradeData(uPIQ.getData(0));
				oldVirProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
				oldVirProductTrade.setEndDate(oldProductEndDate);
				btd.add(virSerialNumber, oldVirProductTrade);
			}
			btd.add(virSerialNumber, newVirProductTradeData);
		}
	}
	


	private boolean isNeedDeal(List<ProductTradeData> userProducts, String elementProductId) throws Exception
	{
		int size = userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData ptd = userProducts.get(i);
			if (ptd.getProductId().equals(elementProductId)
					&& ("00".equals(ptd.getProductMode()) || "01".equals(ptd.getProductMode()) || "07".equals(ptd.getProductMode()) || "09".equals(ptd.getProductMode()) || "11".equals(ptd.getProductMode()) || "13".equals(ptd.getProductMode()) || "16".equals(ptd.getProductMode()) || "17".equals(ptd.getProductMode())))
			{
				return true;
			}
		}
		return false;
	}
	

	private boolean isExistInUserSelected(String elementTypeCode, String elementId, String instId, List<ProductModuleData> userSelected)
	{
		if (userSelected == null || userSelected.size() <= 0)
		{
			return false;
		}
		else
		{
			int size = userSelected.size();
			for (int i = 0; i < size; i++)
			{
				ProductModuleData selected = userSelected.get(i);
				if (selected.getElementType().equals(elementTypeCode) && selected.getElementId().equals(elementId))
				{
					if (!StringUtils.isBlank(selected.getInstId()))
					{
						if (selected.getInstId().equals(instId))
						{
							return true;
						}
						else
						{
							continue;
						}
					}
					return true;
				}
			}
			return false;
		}
	}


	public IData getTransElement(IDataset productElements, String elementId, String elementType)
	{
		if (productElements == null || productElements.size() <= 0)
		{
			return null;
		}
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}


	private IDataset getProductForceElement(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		IDataset result = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if ("1".equals(element.getString("PACKAGE_FORCE_TAG")) && "1".equals(element.getString("ELEMENT_FORCE_TAG")))
			{
				result.add(element);
			}
		}
		//默认元素
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && !"1".equals(element.getString("ELEMENT_FORCE_TAG")))
            {
                result.add(element);
            }
        }
		return result;
	}
	
	public static StringBuffer getCashItemType() throws Exception
	{
		//获取用户转出的现金存折
		StringBuffer depositeNotes = new StringBuffer();
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
    		depositeNotes.append("0|1|145|184|185|200|223|400|48|58|604|605|606|771|773");
    	}
		
		return depositeNotes;
	}

    private void createTradeVirtualDiscnt(BusiTradeData<BaseTradeData> btd, WidenetMoveRequestData reqData, String virUserId, String virSerialNumber, String productChangeDate, String oldProductEndDate) throws Exception
    {
    	//if(serialNumber.startsWith("KD_1")) serialNumber = serialNumber.substring(3);
    	String discnt = "";
    	
    	// 虚拟优惠：GPON-5906；ADSL-5907；FTTH-5908; 校园宽带-5909
        if (StringUtils.equals("2", reqData.getNewWideType()))
        {
        	discnt = "5907";// adsl
        }
        else if (StringUtils.equals("3", reqData.getNewWideType()))
        {
        	discnt = "5908";// 移动FTTH
        }
        else if (StringUtils.equals("5", reqData.getNewWideType()))
        {
        	discnt = "59072";// 铁通FTTH
        }
        else if (StringUtils.equals("6", reqData.getNewWideType()))
        {
        	discnt = "59071";// 铁通FTTB
        }
        else
        {
        	discnt = "5906";// 移动FTTB
        }
    	
        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(virUserId);
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId(discnt);
        newDiscnt.setRelationTypeCode("47");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("2");
        newDiscnt.setStartDate(productChangeDate);
        newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
        newDiscnt.setRemark("宽带保底优惠");
        btd.add(virSerialNumber, newDiscnt);
        
        IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(virUserId);
        for(int i=0;i<useDiscnts.size();i++){
            DiscntTradeData oldDiscnt = new DiscntTradeData(useDiscnts.getData(i));
            oldDiscnt.setEndDate(oldProductEndDate);
            oldDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(virSerialNumber, oldDiscnt);
        }
        
    }
}
