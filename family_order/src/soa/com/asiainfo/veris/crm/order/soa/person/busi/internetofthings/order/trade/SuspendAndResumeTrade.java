
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.trade;

import java.util.List;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.SuspendAndResumeReqData;

public class SuspendAndResumeTrade extends BaseTrade implements ITrade
{

    @SuppressWarnings("rawtypes")
	@Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        SuspendAndResumeReqData prd = (SuspendAndResumeReqData) btd.getRD();

        List<String> resumeList = prd.getResumeList();
        List<String> svcInstIdList = prd.getSvcInstIdList();
        
        if (resumeList != null && !resumeList.isEmpty())
        {
            for (int i = 0; i < resumeList.size(); i++)
            {
                createServiceResumeTrade(btd, resumeList.get(i), svcInstIdList.get(i));
            }
        }

        List<String> suspendList = prd.getSuspendList();
        if (suspendList != null && !suspendList.isEmpty())
        {
            for (int j = 0; j < suspendList.size(); j++)
            {
                createServiceSuspendTrade(btd, suspendList.get(j), svcInstIdList.get(j));
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createDiscntTrade(BusiTradeData btd, String svcId) throws Exception {
    	UcaData uca = btd.getRD().getUca();
    	IDataset compare3996 = CommparaInfoQry.getCommparaInfos("CSM", "3996", "IoTGprsDiscntSVC");
    	if(IDataUtil.isEmpty(compare3996)){
    		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "封顶解除资费不存在");
    	}else{
    		// PARA_CODE2标识封顶解除资费0：普通   1：通用，2：定向，
    		//如果是85服务，判断是否有订购通用流量套餐，如果有则订购通用封顶解除资费，如果是92服务，订购定向封顶解除资费，其他，订购普通封顶解除资费 
    		String fdjcID = "";
    		IDataset wlwSvclist = CommparaInfoQry.getCommpara("CSM", "9014", svcId, "0898");
    		if(IDataUtil.isNotEmpty(wlwSvclist)){
    			String paramCode7 = wlwSvclist.getData(0).getString("PARA_CODE7");//PARA_CODE7标记(TY)通用服务(DX)定向服务
    			if("TY".equals(paramCode7)){
        			IDataset userdiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(uca.getUserId());
        			IDataset wlwDiscntlist = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
        			for(int i = 0; i < userdiscnts.size(); i++){
        				IData userdiscnt = userdiscnts.getData(i);
        				IDataset commdisnts = DataHelper.filter(wlwDiscntlist, "PARA_CODE2=I00010101001,PARAM_CODE="+userdiscnt.getString("DISCNT_CODE"));
        				IDataset commdisnts1 = DataHelper.filter(wlwDiscntlist, "PARA_CODE2=I00010101003,PARAM_CODE="+userdiscnt.getString("DISCNT_CODE"));
        				if(IDataUtil.isNotEmpty(commdisnts) || IDataUtil.isNotEmpty(commdisnts1)){
        					IDataset commonfdjc = DataHelper.filter(compare3996, "PARA_CODE2=1,PARA_CODE3="+uca.getProductId());
        					if(IDataUtil.isNotEmpty(commonfdjc)){
        						fdjcID = commonfdjc.getData(0).getString("PARA_CODE1");
        						break;
        					}
        				}
        			}
        		}else if("DX".equals(paramCode7)){
        			IDataset commonfdjc = DataHelper.filter(compare3996, "PARA_CODE2=2,PARA_CODE3="+uca.getProductId());
    				if(IDataUtil.isNotEmpty(commonfdjc)){
    					fdjcID = commonfdjc.getData(0).getString("PARA_CODE1");
    				}
        		}else{
        			IDataset commonfdjc = DataHelper.filter(compare3996, "PARA_CODE2=0,PARA_CODE3="+uca.getProductId());
    				if(IDataUtil.isNotEmpty(commonfdjc)){
    					fdjcID = commonfdjc.getData(0).getString("PARA_CODE1");
    				}
        		}
    		}
    		if("".equals(fdjcID)){
    			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有查询出相应的封顶解除资费");
    		}
    		String strPackge = "-1";
    		List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcId);
            if (svcTradeData != null)
            {
            	strPackge = svcTradeData.get(0).getPackageId();
            }
            String strInstId = SeqMgr.getInstId();
    		String elementId = fdjcID;
    		DiscntTradeData discntData = new DiscntTradeData();
			discntData.setUserId(btd.getMainTradeData().getUserId());
			discntData.setProductId(btd.getMainTradeData().getProductId());
			discntData.setPackageId(strPackge);
			discntData.setElementId(elementId);
			discntData.setSpecTag("0");
			discntData.setInstId(strInstId);
			discntData.setStartDate(SysDateMgr.getSysTime());
			discntData.setEndDate(SysDateMgr.getLastDateThisMonth());
			discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
			discntData.setRemark("双封顶恢复时订购封顶解除资费");
			btd.add(btd.getMainTradeData().getSerialNumber(), discntData);
			
			OfferRelTradeData offerRelData = new OfferRelTradeData();
			offerRelData.setOfferCode(btd.getMainTradeData().getProductId());
			offerRelData.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			offerRelData.setOfferInsId(SeqMgr.getInstId());
			offerRelData.setUserId(btd.getMainTradeData().getUserId());
			offerRelData.setGroupId(strPackge);
			offerRelData.setRelOfferCode(elementId);
			offerRelData.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
			offerRelData.setRelOfferInsId(strInstId);
			offerRelData.setRelUserId(btd.getMainTradeData().getUserId());
			offerRelData.setRelType(BofConst.OFFER_REL_TYPE_LINK);
			offerRelData.setStartDate(SysDateMgr.getSysTime());
			offerRelData.setEndDate(SysDateMgr.getLastDateThisMonth());
			offerRelData.setModifyTag(BofConst.MODIFY_TAG_ADD);
			offerRelData.setRemark("双封顶恢复时订购封顶解除资费");
			offerRelData.setInstId(SeqMgr.getInstId());
			btd.add(btd.getMainTradeData().getSerialNumber(), offerRelData);
			
			IDataset userattrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(uca.getUserId(), svcId);
			if(IDataUtil.isNotEmpty(userattrs)){
				for(int a = 0; a < userattrs.size(); a++){
					IData userattr = userattrs.getData(a);
					if("APNNAME".equals(userattr.getString("ATTR_CODE"))){	
						AttrTradeData attrTD = new AttrTradeData();
				        attrTD.setUserId(uca.getUserId());
				        attrTD.setInstType("D");
				        attrTD.setInstId(SeqMgr.getInstId());
				        attrTD.setElementId(elementId);
				        attrTD.setAttrCode("APNNAME");
				        attrTD.setAttrValue(userattr.getString("ATTR_VALUE"));
				        attrTD.setStartDate(SysDateMgr.getSysTime());
				        attrTD.setEndDate(SysDateMgr.getTheLastTime());
				        attrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
				        attrTD.setRelaInstId(strInstId);
				        btd.add(btd.getMainTradeData().getSerialNumber(), attrTD);
					}
				}
			}	
    	}
	}

    // 将原状态暂停的状态删除，新增条开通的状态
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createServiceResumeTrade(BusiTradeData btd, String svcId, String instId) throws Exception
    {
    	SuspendAndResumeReqData rd = (SuspendAndResumeReqData) btd.getRD();
    	IData dtPageRequest = btd.getRD().getPageRequestData();
        String strOprCode = dtPageRequest.getString("OPR_CODE", "");
        UcaData uca = btd.getRD().getUca();
        List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcId);
        if (svcTradeData == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1010, svcId, "未开通");
        }
        boolean bSuspend = false;
        if(isOpenPcrf(uca.getUserId(), svcId)){//开通策略
          	createSvcAttrTrade(btd, rd, instId, "1");
          	SvcTradeData std = svcTradeData.get(0); 
          	if("WLWFD".equals(std.getRsrvStr1())){
          		bSuspend = true;
          	}
         }else{
	        String strRemark = "物联网流量双封顶恢复";
	        
	        SvcStateTradeData svcStateTradeData = uca.getUserSvcsStateByServiceId(svcId);
	        if (svcStateTradeData != null)
	        {
	            if (!"E".equals(svcStateTradeData.getStateCode()))
	            {
	                CSAppException.apperr(CrmUserException.CRM_USER_1010, svcId, "非暂停");
	            }
	
	            String strRsrvTag3 = svcStateTradeData.getRsrvTag3();
	            if("1".equals(strRsrvTag3))
	            {
	            	bSuspend = true;
	            }
	            
	            SvcStateTradeData oldSvcTradeData = svcStateTradeData.clone();
	            oldSvcTradeData.setEndDate(rd.getAcceptTime());
	            oldSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	            btd.add(uca.getSerialNumber(), oldSvcTradeData);
	        }
	
	        SvcStateTradeData newSvcTradeData = new SvcStateTradeData();
	        newSvcTradeData.setUserId(uca.getUserId());
	        newSvcTradeData.setServiceId(svcId);
	        newSvcTradeData.setStateCode("0");
	        newSvcTradeData.setMainTag("0");
	        newSvcTradeData.setStartDate(rd.getAcceptTime());
	        newSvcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
	        newSvcTradeData.setInstId(SeqMgr.getInstId());
	        newSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        if(StringUtils.isNotBlank(strOprCode) && "01".equals(strOprCode))
	        {
	        	newSvcTradeData.setRemark(strRemark);
	        }
	        btd.add(uca.getSerialNumber(), newSvcTradeData);
        }
        if(StringUtils.isBlank(strOprCode) && bSuspend)
        {
        	createDiscntTrade(btd, svcId);
        }
    }

    // 将原状态开通的状态删除，新增条暂停的状态
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createServiceSuspendTrade(BusiTradeData btd, String svcId, String instId) throws Exception
    {
    	SuspendAndResumeReqData rd = (SuspendAndResumeReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
    	IData dtPageRequest = btd.getRD().getPageRequestData();
        String strOprCode = dtPageRequest.getString("OPR_CODE", "");
        if(isOpenPcrf(uca.getUserId(), svcId)){//关停策略
			createSvcAttrTrade(btd, rd, instId, "0");
		}else{

	        String strRemark = "物联网流量双封顶暂停";
	        List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcId);
	        if (svcTradeData == null)
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_1010, svcId, "未开通");
	        }
	        SvcStateTradeData svcStateTradeData = uca.getUserSvcsStateByServiceId(svcId);
	        if (svcStateTradeData != null)
	        {
	            if (!"0".equals(svcStateTradeData.getStateCode()))
	            {
	                CSAppException.apperr(CrmUserException.CRM_USER_1010, svcId, "非正常");
	            }
	
	            SvcStateTradeData oldSvcTradeData = svcStateTradeData.clone();
	            oldSvcTradeData.setEndDate(rd.getAcceptTime());
	            oldSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	            btd.add(uca.getSerialNumber(), oldSvcTradeData);
	
	        }
	
	        SvcStateTradeData newSvcTradeData = new SvcStateTradeData();
	        newSvcTradeData.setUserId(uca.getUserId());
	        newSvcTradeData.setServiceId(svcId);
	        newSvcTradeData.setStateCode("E");
	        newSvcTradeData.setMainTag("0");
	        newSvcTradeData.setStartDate(rd.getAcceptTime());
	        newSvcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
	        newSvcTradeData.setInstId(SeqMgr.getInstId());
	        newSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        if(StringUtils.isNotBlank(strOprCode) && "02".equals(strOprCode))
	        {
	        	if(StringUtils.isNotBlank(rd.getMore_oper())){
	        		newSvcTradeData.setRsrvTag3("1");
		        	newSvcTradeData.setRemark(strRemark);
	        	}else if(StringUtils.isNotBlank(rd.getClose_oper())){
	        		newSvcTradeData.setRsrvTag3("2");
		        	newSvcTradeData.setRemark("物联网流量用尽关停");
	        	}
	        	
	        }
	        btd.add(uca.getSerialNumber(), newSvcTradeData);
	        
	    }
        if(StringUtils.isNotBlank(strOprCode) && "02".equals(strOprCode))
        {
        	String strDealId = SeqMgr.getTradeId();
        	String strUserId = btd.getMainTradeData().getUserId();
        	String strEparchyCode = btd.getMainTradeData().getEparchyCode();
        	String strExecTime = SysDateMgr.getFirstDayOfNextMonth4WEB();
        	
        	IData param = new DataMap();
        	param.put("OPR_CODE", "01");
        	param.put("ROUTE_EPARCHY_CODE", strEparchyCode);
        	param.put("USER_ID", strUserId);
        	param.put("SERVICE_ID", svcId);
        	param.put("INST_ID", instId);
        	
        	IData paramProduct = new DataMap();
        	paramProduct.put("DEAL_ID", strDealId);
        	paramProduct.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
        	paramProduct.put("USER_ID", strUserId);
        	paramProduct.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));
        	paramProduct.put("EPARCHY_CODE", strEparchyCode);
        	paramProduct.put("DEAL_COND", param);
        	paramProduct.put("DEAL_TYPE", "ResumeExpire");
        	paramProduct.put("EXEC_TIME", strExecTime);
        	paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(strExecTime));
        	paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
        	paramProduct.put("DEAL_STATE", "0");
        	paramProduct.put("REMARK", "物联网流量双封顶恢复");
        	paramProduct.put("TRADE_ID", btd.getTradeId());
        	Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);
        	
        }
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createSvcAttrTrade(BusiTradeData btd,SuspendAndResumeReqData prd,String instId,String opercode) throws Exception {
   	  UcaData uca = btd.getRD().getUca();
   	  if(StringUtils.isNotEmpty(instId)){ 
	    	 if("0".equals(opercode)){    //关停
	    		 SvcTradeData std  = uca.getUserSvcByInstId(instId);
        		 SvcTradeData std1 = std.clone();
        		 std1.setModifyTag(BofConst.MODIFY_TAG_UPD);
        		 if(StringUtils.isNotBlank(prd.getMore_oper())){
        			 std1.setRsrvStr1("WLWFD");     //标记为物联网双封顶
 	        	 }else if(StringUtils.isNotBlank(prd.getClose_oper())){
 	        		 std1.setRsrvStr1("WLWGT");     //标记为物联网流量用尽关停
 	        	 }
        		 std1.setRsrvTag1("1"); //标记双封顶关停
        		 btd.add(uca.getSerialNumber(), std1);
        		 
				IDataset usersvcAttrs = UserAttrInfoQry.getUserAttrByRelaInstId(uca.getUserId(), instId, CSBizBean.getVisit().getStaffEparchyCode());
				for(int i = 0; i < usersvcAttrs.size(); i++){
					IData usersvcAttr = usersvcAttrs.getData(i);
					String attr_code = usersvcAttr.getString("ATTR_CODE");
					String attrInstId =  usersvcAttr.getString("INST_ID");
					if("OperType".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setAttrValue("03");
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
						
					}
					if("ServiceUsageState".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setAttrValue("2");
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
					}
					if("ServiceCode".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
					}
				}
	    	 }else if("1".equals(opercode)){   //恢复
	    		 SvcTradeData std  = uca.getUserSvcByInstId(instId);
        		 SvcTradeData std1 = std.clone();
        		 std1.setModifyTag(BofConst.MODIFY_TAG_UPD);
        		 std1.setRsrvTag1(""); //去掉双封顶关停标记
        		 btd.add(uca.getSerialNumber(), std1);
	        		 
	    		 IDataset usersvcAttrs = UserAttrInfoQry.getUserAttrByRelaInstId(uca.getUserId(), instId, CSBizBean.getVisit().getStaffEparchyCode());
	 			 for(int i = 0; i < usersvcAttrs.size(); i++){
	 				IData usersvcAttr = usersvcAttrs.getData(i);
					String attr_code = usersvcAttr.getString("ATTR_CODE");
					String attrInstId =  usersvcAttr.getString("INST_ID");
					if("OperType".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setAttrValue("03");
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
						
					}
					if("ServiceUsageState".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setAttrValue("1");
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
					}
					if("ServiceCode".equals(attr_code)){
						List<AttrTradeData> svcTradeData = uca.getUserAttrsByInstId(attrInstId);
						if(svcTradeData!=null && svcTradeData.size()>0){
							AttrTradeData attrTD = svcTradeData.get(0).clone();
							attrTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                    btd.add(uca.getSerialNumber(), attrTD);
						}
					}
	 			}
	        }
      }
   }

    public static boolean isOpenPcrf(String userID ,String svcId) throws Exception{
     	 IDataset usersvcAttrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(userID, svcId);
          boolean PCRFflag = false;
          if(IDataUtil.isNotEmpty(usersvcAttrs)){
          	IDataset userattrsfilter = DataHelper.filter(usersvcAttrs, "ATTR_CODE=APNNAME");	
          	IDataset attrsPCRFfilter = DataHelper.filter(usersvcAttrs, "ATTR_CODE=ServiceCode");	
          	IDataset attrsstatefilter = DataHelper.filter(usersvcAttrs, "ATTR_CODE=ServiceUsageState");
          	if(IDataUtil.isNotEmpty(userattrsfilter) && IDataUtil.isNotEmpty(attrsPCRFfilter) &&IDataUtil.isNotEmpty(attrsstatefilter) ){
          		PCRFflag = true;
          	}
          }
     	return PCRFflag;
     }
}
