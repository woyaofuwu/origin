package com.asiainfo.veris.crm.order.soa.person.busi.stopTopSetBox.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.stopTopSetBox.order.requestdata.StopTopSetBoxReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.TopSetBoxBean;

public class StopTopSetBoxTrade extends BaseTrade implements ITrade {

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		StopTopSetBoxReqData reqData=(StopTopSetBoxReqData)bd.getRD();
		
		/*
		 * 
		 * 暂停平台服务
		 * 订购暂停保号服务、优惠，包括服务属性
		 * 在资源表当中做记录说明
		 *  
		 */
		UcaData uca=bd.getRD().getUca();
		
		String userId=uca.getUserId();
		
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
		IData boxInfo=boxInfos.first();
		
		/*
		 * 暂停必选服务
		 */
		String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
		if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
			String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
			if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
				String basePlatSvcId=basePlatSvcIdArr[0];
				if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
					
					IDataset userBaseServices=UserPlatSvcInfoQry.
							queryUserPlatSvcByUserIdAndServiceId(userId, basePlatSvcId);
					if(IDataUtil.isNotEmpty(userBaseServices)){
						PlatSvcTradeData baseNewPlatSvcTradeData=new PlatSvcTradeData(userBaseServices.getData(0));
						baseNewPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_PAUSE);
						baseNewPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
						baseNewPlatSvcTradeData.setOperCode(PlatConstants.OPER_PAUSE);
						baseNewPlatSvcTradeData.setOprSource("08");
						//BUS202004170006优化度假宽带2019业务规则
						String DJKD_TAG = bd.getRD().getPageRequestData().getString("DJKD_TAG", "");
						//System.out.println("-------StopTopSetBoxTrade--------DJKD_TAG:"+DJKD_TAG);						
						if("1".equals(DJKD_TAG))
						{
				            String strFirstDayThisAcct = SysDateMgr.getFirstDayOfThisMonth4WEB();							
				            String operTime = SysDateMgr.getLastSecond(strFirstDayThisAcct);
				            //System.out.println("-------StopTopSetBoxTrade--------baseNewPlatSvcTradeData:"+operTime);
							baseNewPlatSvcTradeData.setOperTime(operTime);
						}
						else
						{
							baseNewPlatSvcTradeData.setOperTime(bd.getRD().getAcceptTime());
						}
						//System.out.println("-------StopTopSetBoxTrade--------baseNewPlatSvcTradeData:"+baseNewPlatSvcTradeData);
						//BUS202004170006优化度假宽带2019业务规则
						baseNewPlatSvcTradeData.setAllTag("01");
						baseNewPlatSvcTradeData.setIsNeedPf("1");
						// 体验基础包到期标识
						baseNewPlatSvcTradeData.setRsrvStr8(reqData.getExperienceTag());
						
						bd.add(uca.getSerialNumber(), baseNewPlatSvcTradeData);
						
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
					}
					
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
				}
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
		}
		
		
		
		/*
		 * 暂停可选服务
		 */
        String optionPlatSvcIdTemp=boxInfo.getString("RSRV_STR3","");	//可选套餐
        if(!optionPlatSvcIdTemp.equals("")&&optionPlatSvcIdTemp.indexOf(",")!=-1){
        	String[] optionPlatSvcIdArr=optionPlatSvcIdTemp.split(",");
			if(optionPlatSvcIdArr!=null&&optionPlatSvcIdArr.length>0){
				String optionPlatSvcId=optionPlatSvcIdArr[0];
				if(optionPlatSvcId!=null&&!optionPlatSvcId.trim().equals("")&&
					!optionPlatSvcId.trim().equals("-1")){
					
					IDataset userOptionServices=UserPlatSvcInfoQry.
							queryUserPlatSvcByUserIdAndServiceId(userId, optionPlatSvcId);
					if(IDataUtil.isNotEmpty(userOptionServices)){
						PlatSvcTradeData optionalNewPlatSvcTradeData=new PlatSvcTradeData(userOptionServices.getData(0));
						optionalNewPlatSvcTradeData.setOprSource("08");
						optionalNewPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_PAUSE);
						optionalNewPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
						optionalNewPlatSvcTradeData.setIsNeedPf("1");
						optionalNewPlatSvcTradeData.setOperCode(PlatConstants.OPER_PAUSE);
						optionalNewPlatSvcTradeData.setOperTime(bd.getRD().getAcceptTime());
						optionalNewPlatSvcTradeData.setAllTag("01");
						
						bd.add(uca.getSerialNumber(), optionalNewPlatSvcTradeData);
					}
				}
				
			}			
		}
        
    	
        
        
    	/*
    	 * 如果用户没有办理免费的营销活动，就为用户办理停机保号费
    	 */
        boolean isHasSaleActive=TopSetBoxBean.saleActiveFee(userId);
        
        if(!isHasSaleActive){
        	IDataset userSvcs=UserSvcInfoQry.getSvcUserId(userId, "3000003");
        	if(IDataUtil.isEmpty(userSvcs)){
        		/*
                 * 绑定用户停机保号服务和优惠
                 * 
                 */
                //创建停机保号服务
                SvcTradeData svcTD = new SvcTradeData();
                svcTD.setUserId(userId);
                svcTD.setUserIdA("-1");
                svcTD.setProductId("-1");
                svcTD.setPackageId("-1");
                svcTD.setElementId("3000003");
                svcTD.setMainTag("0");
                svcTD.setRsrvTag1("1");
                svcTD.setCampnId("0");
                svcTD.setInstId(SeqMgr.getInstId());
                svcTD.setStartDate(SysDateMgr.addDays(SysDateMgr.getSysTime(),1));
                svcTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
                svcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                svcTD.setRemark("魔百和报开前台处理停机保号费用");
                bd.add(uca.getSerialNumber(), svcTD);
        		
        	}
        	
        	IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, "1550");
        	if(IDataUtil.isEmpty(userDiscnts)){
        		//创建停机保号优惠
                DiscntTradeData discntTD = new DiscntTradeData();
                discntTD.setUserId(userId);
                discntTD.setUserIdA("-1");
                discntTD.setProductId("-1");
                discntTD.setPackageId("-1");
                discntTD.setElementId("1550");
                discntTD.setSpecTag("0");
                discntTD.setRelationTypeCode("");
                discntTD.setCampnId("0");
                discntTD.setInstId(SeqMgr.getInstId());
                discntTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                discntTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
                discntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                discntTD.setRemark("魔百和报开前台处理停机保号费用");
                bd.add(uca.getSerialNumber(), discntTD);
        		
        	}
        	
        	
        	IDataset userSvcStates=UserSvcInfoQry.getUserSvcStateByUserId(userId, "3000003");
        	if(IDataUtil.isNotEmpty(userSvcStates)){
        		IData userSvcState=userSvcStates.getData(0);
        		
        		/*
        		 * 删除掉原来的停机保号费服务状态
        		 */
        		SvcStateTradeData delSvcStateData = new SvcStateTradeData(userSvcState);
        		delSvcStateData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        		delSvcStateData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        		bd.add(uca.getSerialNumber(), delSvcStateData);
        		
        	}
        	
    		/*
    		 * 增加新的状态
    		 */
    		SvcStateTradeData svcStateData = new SvcStateTradeData();
            svcStateData.setUserId(uca.getUserId());
            svcStateData.setInstId(SeqMgr.getInstId());
            svcStateData.setMainTag("0");
            svcStateData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            svcStateData.setServiceId("3000003");
            svcStateData.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            svcStateData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            svcStateData.setStateCode("0");		//报停就将服务状态置为正常状态
            bd.add(uca.getSerialNumber(), svcStateData);
            
        	
        }
    	
        
        /*
         * 修改资源信息
         * 
         */
        ResTradeData resTD = new ResTradeData(boxInfo);
        resTD.setRsrvTag3("1");		//报停标识
        resTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        bd.add(uca.getSerialNumber(), resTD);
 
	}
	
}
