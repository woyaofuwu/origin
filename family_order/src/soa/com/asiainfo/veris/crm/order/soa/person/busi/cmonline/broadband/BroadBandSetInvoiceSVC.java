package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband;


import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.groupcustinfo.CustGroupInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.CustManagerTJNumQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.util.RealNameMsDesPlus;
import org.apache.log4j.Logger;

public class BroadBandSetInvoiceSVC extends CSBizService {
	protected static final Logger log = Logger.getLogger(BroadBandSetInvoiceSVC.class);
	/**
	 * 查询电子发票类型接口
	 * @param params
	 * @return
	 */
	public IData qryInvoiceType(IData params){
		IData param = new DataMap(params.toString()).getData("params");
		IData returnResult=new DataMap();
		IDataset result = new DatasetList();
		IData returnInfo1 = new DataMap();
		returnInfo1.put("invoiceType", "1");
		returnInfo1.put("invoiceTypeName", "月结电子发票");
		returnInfo1.put("remark", "月结电子发票");
		result.add(returnInfo1);
		DataMap returnInfo2 = new DataMap();
		returnInfo2.put("invoiceType", "2");
		returnInfo2.put("invoiceTypeName", "现金电子发票");
		returnInfo2.put("remark", "现金电子发票");
		result.add(returnInfo2);
		IData returnInfo3 = new DataMap();
		returnInfo3.put("invoiceType", "3");
		returnInfo3.put("invoiceTypeName", "营业业务电子发票");
		returnInfo3.put("remark", "营业业务电子发票");
		result.add(returnInfo3);
		
		IData object = new DataMap();
		
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("resultRows", result.size()+"");
		object.put("result", result);
		
		returnResult.put("object",object );
		returnResult.put("rtnCode", "0");
		returnResult.put("rtnMsg", "成功");
		return returnResult;
	}
	
	/**
	 * 电子发票设置接口
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData setInvoice(IData params) throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		IDataset setParams = param.getDataset("setParams");
		IData crmpfPubInfo = param.getData("crmpfPubInfo");
		setLoginInfo(crmpfPubInfo);
		IData data = setParams.getData(0);
		param.put("SERIAL_NUMBER", param.getString("telNumber"));
		String invoiceType = data.getString("invoiceType");
		if("0".equals(invoiceType)){
			param.put("POST_TYPE_MON","0" );
			param.put("POST_TYPE_CASH","1" );
			param.put("POST_TYPE_BUSI","2" );
		}else if("1".equals(invoiceType)){
			param.put("POST_TYPE_MON","0" );
		}else if("2".equals(invoiceType)){
			param.put("POST_TYPE_CASH","1" );
		}else if("3".equals(invoiceType)){
			param.put("POST_TYPE_BUSI","2" );
		}else if("1,2".equals(invoiceType)){
			param.put("POST_TYPE_MON","0" );
			param.put("POST_TYPE_CASH","1" );
		}else if("1,3".equals(invoiceType)){
			param.put("POST_TYPE_MON","0" );
			param.put("POST_TYPE_BUSI","2" );
		}else if("2,3".equals(invoiceType)){
			param.put("POST_TYPE_CASH","1" );
			param.put("POST_TYPE_BUSI","2" );
		}
		param.put("postinfo_POST_DATE_MON", data.getString("sendDate",""));
		String sendType = data.getString("sendType");
		if("0".equals(sendType)){
			param.put("POST_CHANNEL","02,12");
			String post_adr = data.getString("mail","");
			if(!"".equals(post_adr)&&!post_adr.endsWith("@139.com"))
			{
				param.put("postinfo_POST_ADR", post_adr + "@139.com");
			}else{
				param.put("postinfo_POST_ADR", post_adr);
			}
			param.put("postinfo_RECEIVE_NUMBER", data.getString("telNum"));
			param.put("postinfo_POST_ADR_SEC", data.getString("mail2",""));
		}else if("1".equals(sendType)){
			param.put("POST_CHANNEL","02");
			param.put("postinfo_RECEIVE_NUMBER", data.getString("telNum"));
		}else if("2".equals(sendType)){
			param.put("POST_CHANNEL","12");
			String post_adr = data.getString("mail","");
			if(!"".equals(post_adr)&&!post_adr.endsWith("@139.com"))
			{
				param.put("postinfo_POST_ADR", post_adr + "@139.com");
			}else{
				param.put("postinfo_POST_ADR", post_adr);
			}
			param.put("postinfo_POST_ADR_SEC", data.getString("mail2",""));
		}
	
		IDataset dataset = CSAppCall.call("SS.ModifyEPostInfoSVC.modiTradeReg", param);
		IData returnInfo = new DataMap();
		IDataset list = new DatasetList();
		IData dataMap = new DataMap();
		IData object = new DataMap();
		if(IDataUtil.isNotEmpty(dataset)){
			returnInfo.put("rtnCode", "0");
			returnInfo.put("rtnMsg", "成功");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			dataMap.put("setStatus", "0");
			if("0".equals(sendType)||"1".equals(sendType)||"2".equals(sendType)){
				dataMap.put("isSend", "是");
				dataMap.put("sendType",sendType);//0:短信,邮箱  1:短信  2:邮箱
			}else{
				dataMap.put("isSend", "否");
				dataMap.put("sendType","");
			}
			list.add(dataMap);
			object.put("result",list );
			returnInfo.put("object", object);
			return returnInfo;
		}else{
			returnInfo.put("rtnCode", "-9999");
			returnInfo.put("rtnMsg", "失败");
			object.put("respCode", "1001");
			object.put("respDesc", "操作失败");
			dataMap.put("setStatus", "-9999");
			list.add(dataMap);
			object.put("result",list );
			returnInfo.put("object", object);
			return returnInfo;
		}
	}
	/**
	 * 已订购国漫产品查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData queryOrderedIntlRoamInfo(IData params) throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		IData retResult = new DataMap();
		IData object = new DataMap();
		IData result  = new DataMap();
		IData outData = new DataMap();   
		String busiCode = param.getString("userMobile","");
		 if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
		IData crmpfPubInfo = param.getData("crmpfPubInfo");
		setLoginInfo(crmpfPubInfo);
		IDataset userSvcInfos = new DatasetList();
		IDataset userDiscntInfos = new DatasetList();
		IDataset intlRoamIBaseInfo = new DatasetList();
		IDataset intlRoamIPackageInfo = new DatasetList();
		object.put("respCode", "0");
		object.put("resultRows", "1");
		object.put("respDesc", "success");
		retResult.put("rtnCode", "0");
		retResult.put("rtnMsg", "成功");
		try {
			UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
			if(ucaData!=null){
				userSvcInfos = UserSvcInfoQry.queryUserAllSvc(ucaData.getUserId());
				userDiscntInfos = UserDiscntInfoQry.getAllDiscntInfo(ucaData.getUserId());
			}
			//IDataset interRoamDayDiscntInfos = CSAppCall.call("SS.InterRoamDaySVC.interRoamQuery", param);
		
			if(IDataUtil.isNotEmpty(userSvcInfos)){
				for(int i=0;i<userSvcInfos.size();i++){
					String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID", "");
					if("15".equals(serviceId)||"19".equals(serviceId)){
						IData dataMap2 = new DataMap();
						dataMap2.put("offerId", serviceId+"|S");
						dataMap2.put("offerName", USvcInfoQry.getSvcNameBySvcId(serviceId));
						
						String orderedEffTime = SysDateMgr.decodeTimestamp(userSvcInfos.getData(i).getString("START_DATE", ""),SysDateMgr.PATTERN_STAND_SHORT);
						String orderedExpTime = SysDateMgr.decodeTimestamp(userSvcInfos.getData(i).getString("END_DATE", ""),SysDateMgr.PATTERN_STAND_SHORT);
						dataMap2.put("orderedEffTime",orderedEffTime);
						dataMap2.put("orderedExpTime",orderedExpTime);
						dataMap2.put("canUnsub","1");
						dataMap2.put("bossId",serviceId+"|S");
						intlRoamIBaseInfo.add(dataMap2);
					}
				}
			}
			if(IDataUtil.isNotEmpty(userDiscntInfos)){
				IDataset orderedInfo = new DatasetList();
				for(int k=0;k<userDiscntInfos.size();k++){
					IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",userDiscntInfos.getData(k).getString("DISCNT_CODE","")
							,CSBizBean.getVisit().getStaffEparchyCode());
					int orderedNum=0;
					if(IDataUtil.isNotEmpty(commparaSet)){
						IData dataMap3 = new DataMap();
						orderedNum++;
						dataMap3.put("offerId",userDiscntInfos.getData(k).getString("DISCNT_CODE","")+"|D");
						dataMap3.put("offerName", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscntInfos.getData(k).getString("DISCNT_CODE","")));
						dataMap3.put("offerFee",commparaSet.getData(0).getString("PARA_CODE1","0")+"元");
						dataMap3.put("orderedNum", orderedNum+"");
						IData orderedInfoMap =new DataMap();
						orderedInfoMap.put("orderedEffTime", SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("START_DATE",""),SysDateMgr.PATTERN_STAND_SHORT));
						orderedInfoMap.put("orderedExpTime", SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("END_DATE",""),SysDateMgr.PATTERN_STAND_SHORT));
						orderedInfoMap.put("offerEffTime", SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("START_DATE",""),SysDateMgr.PATTERN_STAND_SHORT));
						orderedInfoMap.put("offerExpTime", SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("END_DATE",""),SysDateMgr.PATTERN_STAND_SHORT));
						String rsrv_date1 = userDiscntInfos.getData(k).getString("RSRV_DATE1");//国漫产品的激活时间
						String offerEffTime ="";
						if(StringUtils.isNotBlank(rsrv_date1)){
							offerEffTime=SysDateMgr.decodeTimestamp(rsrv_date1, SysDateMgr.PATTERN_STAND_SHORT);
							orderedInfoMap.put("offerState", "已激活，正在使用");
						}else{
							offerEffTime=SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("START_DATE",""),SysDateMgr.PATTERN_STAND_SHORT);
							orderedInfoMap.put("offerState", "未激活，未过期");
						}
						orderedInfoMap.put("activationEffTime", offerEffTime);
						orderedInfoMap.put("activationExpTime", SysDateMgr.decodeTimestamp(userDiscntInfos.getData(k).getString("END_DATE",""),SysDateMgr.PATTERN_STAND_SHORT));
						orderedInfoMap.put("offerType", "一次性产品");
						orderedInfoMap.put("prodInstId",userDiscntInfos.getData(k).getString("INST_ID",""));
						orderedInfoMap.put("orderedChannel","省BOSS");
						orderedInfoMap.put("bossId", userDiscntInfos.getData(k).getString("DISCNT_CODE","")+"|D");
						orderedInfo.add(orderedInfoMap);
						dataMap3.put("orderedInfo",orderedInfo);
						intlRoamIPackageInfo.add(dataMap3);
					}
				}
			}
			if(IDataUtil.isEmpty(intlRoamIBaseInfo)&&IDataUtil.isEmpty(intlRoamIPackageInfo)){
				retResult.put("rtnCode", "-9999");
				retResult.put("rtnMsg", "该用户无国漫数据");
				object.put("respCode", "1001");
				object.put("respDesc", "该用户无国漫数据");
				object.put("resultRows", "0");
				result.put("intlRoamIBaseInfo",intlRoamIBaseInfo);
				result.put("intlRoamIPackageInfo",intlRoamIPackageInfo);
				object.put("result",result);
				retResult.put("object",object);
				return retResult;
			}
			result.put("intlRoamIBaseInfo",intlRoamIBaseInfo);
			result.put("intlRoamIPackageInfo",intlRoamIPackageInfo);
			object.put("result",result);
			retResult.put("object",object);
			return retResult;
		} catch (Exception e) {
			retResult.put("rtnCode", "-9999");
			retResult.put("rtnMsg", e.getMessage());
			object.put("respCode", "1001");
			object.put("respDesc", e.getMessage());
			object.put("resultRows", "0");
			result.put("intlRoamIBaseInfo",intlRoamIBaseInfo);
			result.put("intlRoamIPackageInfo",intlRoamIPackageInfo);
			object.put("result",result);
			retResult.put("object",object);
			return retResult;
		}
	}
	/**
	 * 短信统一下行接口
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData sendUnifySms(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		param.put("SERIAL_NUMBER", param.getString("smsCode"));
		param.put("SMS_CONTENT", param.getString("smsContent"));
		IData data = CSAppCall.callOne("SS.InterRoamingSVC.smsNotice", param);
		IData rtnResult = new DataMap();
		IData object = new DataMap();
		IDataset result = new DatasetList();
		IData map = new DataMap();
		if(IDataUtil.isNotEmpty(data)){
			rtnResult.put("rtnCode", data.getString("X_RESULTCODE"));
			rtnResult.put("rtnMsg", data.getString("X_RESULTINFO"));
			object.put("respCode", "0");
			object.put("respDesc", "success");
			
		}else{
			rtnResult.put("rtnCode", "-9999");
			rtnResult.put("rtnMsg", "失败");
			object.put("respCode", "1001");
			object.put("respDesc", "操作失败");
		}
		map.put("oprTime", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		result.add(map);
		object.put("result", result);
		rtnResult.put("object",object);
		return rtnResult;
	}
	
	/**
	 * 短信统一上行接口
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData receiveUnifySms(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		param.put("SERIAL_NUMBER", param.getString("serverNumber"));
		param.put("SMS_CONTENT", param.getString("smsContent"));
		IDataset dataset = CSAppCall.call("SS.InterRoamingSVC.smsNoticeUp", param);
		IData rtnResult = new DataMap();
		IData bean = new DataMap();
		IData object =new DataMap();
		IDataset beans = new DatasetList();
		if(IDataUtil.isNotEmpty(dataset)){
			rtnResult.put("rtnCode", "0");
			rtnResult.put("rtnMsg", "成功！");
			object.put("respCode", "0");
			object.put("respDesc", "success");

		}else{
			rtnResult.put("rtnCode", "-9999");
			rtnResult.put("rtnMsg", "失败！");
			object.put("respCode", "1001");
			object.put("respDesc", "操作失败");
		}
		rtnResult.put("bean", bean);
		rtnResult.put("beans", beans);
		rtnResult.put("object",object);
		return rtnResult;
	}

	/**
	 * 子商品信息查询
	 * @param params
	 * @return
	 */
	public IData querySubOffersInfo(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		String offerCode = param.getString("offerId");
		param.put("NEW_PRODUCT_ID",offerCode);
		param.put("TRADE_TYPE_CODE","10");
		param.put("ROUTE_EPARCHY_CODE","0898");
		param.put("PRODUCT_ID",offerCode);
		IDataset selectedElements = CSAppCall.call("CS.SelectedElementSVC.getUserOpenElements", param);
		IDataset groupList = CSAppCall.call("SS.CreatePersonIntfSVC.queryGroupByProductId", param);
		IData rtnResult = new DataMap();
		IData object = new DataMap();
		IDataset result = new DatasetList();
		IData map =new DataMap();
		IDataset offerList = new DatasetList();
		int maxCount=0;
		int minCount=0;
		if(IDataUtil.isNotEmpty(groupList)){
			for(int i=0;i<groupList.size();i++){
				IData group = groupList.getData(i);
				IData groupId=new DataMap();
				groupId.put("GROUP_ID", group.getString("GROUP_ID"));
				IDataset groupElements = CSAppCall.call("SS.CreatePersonIntfSVC.queryOfferByGroupId", groupId);
				if(IDataUtil.isNotEmpty(groupElements)){
					maxCount=maxCount+groupElements.size();
					for(int j=0;j<groupElements.size();j++){
						IData rtnGroupElemenInfo= new DataMap();
						IData groupElement = groupElements.getData(j);
						rtnGroupElemenInfo.put("offerId", groupElement.getString("OFFER_CODE"));
						rtnGroupElemenInfo.put("offerName",groupElement.getString("OFFER_NAME"));
						rtnGroupElemenInfo.put("spCode","");
						rtnGroupElemenInfo.put("bizCode","");
						rtnGroupElemenInfo.put("servType","");
						rtnGroupElemenInfo.put("upOfferId",group.getString("PRODUCT_ID"));
						rtnGroupElemenInfo.put("offerPrice","");
						rtnGroupElemenInfo.put("offerDesc",groupElement.getString("DESCRIPTION"));
						rtnGroupElemenInfo.put("offerType",groupElement.getString("OFFER_TYPE"));
						String offerTypeName="";
						String offerResType="";
						if("D".equals(groupElement.getString("OFFER_TYPE"))){
							offerTypeName="优惠";
							offerResType="0";
						}else if("S".equals(groupElement.getString("OFFER_TYPE"))){
							offerTypeName="服务";
							if("0".equals(groupElement.getString("OFFER_CODE"))){
								offerResType="1";
							}else{
								offerResType="3";
							}
						}else{
							offerResType="3";
						}
						rtnGroupElemenInfo.put("offerTypeName",offerTypeName);
						rtnGroupElemenInfo.put("offerResType",offerResType);
						String chooseTag="";
						if("0".equals(groupElement.getString("SELECT_FLAG"))){
							chooseTag="2";
						}
						if("2".equals(groupElement.getString("SELECT_FLAG"))){
							chooseTag="0";
						}else{
							chooseTag=groupElement.getString("SELECT_FLAG","");
						}
						rtnGroupElemenInfo.put("chooseTag",chooseTag);
						rtnGroupElemenInfo.put("effMode","Type_Default");
						rtnGroupElemenInfo.put("effTime","");
						rtnGroupElemenInfo.put("validate",SysDateMgr.getChinaDate(groupElement.getString("VALID_DATE"),SysDateMgr.PATTERN_STAND_SHORT));
						rtnGroupElemenInfo.put("expireDate",SysDateMgr.getChinaDate(groupElement.getString("EXPIRE_DATE"),SysDateMgr.PATTERN_STAND_SHORT));
						rtnGroupElemenInfo.put("isPackage","0");
						rtnGroupElemenInfo.put("subMinCount","0");
						rtnGroupElemenInfo.put("subMaxCount","0");
						offerList.add(rtnGroupElemenInfo);
					}
					
				}
			}
			rtnResult.put("rtnCode","0");
        	rtnResult.put("rtnMsg","成功");
        	object.put("respCode","0");
        	object.put("respDesc","success");
        	object.put("resultRows","1");
		}
		if(IDataUtil.isNotEmpty(selectedElements)){
			IDataset selected_elements= selectedElements.getData(0).getDataset("SELECTED_ELEMENTS");
			for(int k=0;k<selected_elements.size();k++){
				IData selectedElement = selected_elements.getData(k);
				if("1".equals(selectedElement.getString("FORCE_TAG"))){
					minCount++;
					IData rtnSelected = new DataMap();
					rtnSelected.put("offerId", selectedElement.getString("ELEMENT_ID"));
					rtnSelected.put("offerName",selectedElement.getString("ELEMENT_NAME"));
					rtnSelected.put("spCode","");
					rtnSelected.put("bizCode","");
					rtnSelected.put("servType","");
					rtnSelected.put("upOfferId",selectedElement.getString("PRODUCT_ID"));
					rtnSelected.put("offerPrice","");
					rtnSelected.put("offerDesc",selectedElement.getString("ELEMENT_EXPLAIN"));
					rtnSelected.put("offerType",selectedElement.getString("ELEMENT_TYPE_CODE"));
					String offerTypeName="";
					String offerResType="";
					if("D".equals(selectedElement.getString("ELEMENT_TYPE_CODE"))){
						offerTypeName="优惠";
						offerResType="0";
					}else if("S".equals(selectedElement.getString("ELEMENT_TYPE_CODE"))){
						offerTypeName="服务";
						if("0".equals(selectedElement.getString("ELEMENT_ID"))){
							offerResType="1";
						}else{
							offerResType="3";
						}
					}else{
						offerResType="3";
					}
					rtnSelected.put("offerTypeName",offerTypeName);
					rtnSelected.put("offerResType",offerResType);
					rtnSelected.put("chooseTag","1");
					rtnSelected.put("effMode","Type_Default");
					rtnSelected.put("effTime","");
					rtnSelected.put("validate",SysDateMgr.getChinaDate(selectedElement.getString("START_DATE"),SysDateMgr.PATTERN_STAND_SHORT));
					rtnSelected.put("expireDate",SysDateMgr.getChinaDate(selectedElement.getString("END_DATE"),SysDateMgr.PATTERN_STAND_SHORT));
					rtnSelected.put("isPackage","0");
					rtnSelected.put("subMinCount","0");
					rtnSelected.put("subMaxCount","0");
					offerList.add(rtnSelected);
				}
			}
		}
	 	    map.put("minCount",minCount+"");
			map.put("maxCount",maxCount+minCount+"");
		if(IDataUtil.isEmpty(selectedElements)||IDataUtil.isEmpty(groupList)){
			rtnResult.put("rtnCode","-9999");
        	rtnResult.put("rtnMsg","失败");
        	object.put("respCode","-9999");
        	object.put("respDesc","fail");
        	object.put("resultRows","");
        	map.put("minCount","");
        	map.put("maxCount","");
		}
      
		map.put("offerList",offerList);
    	result.add(map);
    	object.put("result",result);
    	rtnResult.put("object",object);
		return rtnResult;
	}
	/**
	 * 客户身份验证
	 * @param params
	 * @return
	 */
	public IData authUserInfo(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		String authType  = param.getString("authType");
		RealNameMsDesPlus desPlus = new RealNameMsDesPlus();
		String authParam=desPlus.decrypt( param.getString("authParam"));
		UcaData ucaData = UcaDataFactory.getNormalUca(param.getString("userMobile"));
		CustomerTradeData customer = ucaData.getCustomer();
		IData rtnResult = new DataMap();
		IData object = new DataMap();
		IData result = new DataMap();
		if("0".equals(authType)&&authParam.equals(customer.getPsptId())||("1".equals(authType)&&authParam.equals(customer.getCustName()))){
			rtnResult.put("rtnCode","0");
			rtnResult.put("rtnMsg","成功");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			result.put("authFlag", "0");
			object.put("result", result);
			rtnResult.put("object", object);
		}else{
			rtnResult.put("rtnCode","-9999");
			rtnResult.put("rtnMsg","失败");
			object.put("respCode", "-9999");
			object.put("respDesc", "fail");
			result.put("authFlag", "-9999");
			object.put("result", result);
			rtnResult.put("object", object);
		}
		return rtnResult;
	}
	/**
	 * 宽带信息查询
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public IData queryBroadBand(IData params) throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		String serialNumber = param.getString("userMobile");
		serialNumber="KD_"+serialNumber;
		IData inparam=new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);
		inparam.put("REMOVE_TAG", "0");
		IDataset userInfo = Dao.qryByCode("TF_F_USER", "SEL_BY_SERIAL_NUMBER", inparam);
		IData rtnResult = new DataMap();
		IData object = new DataMap();
		IData result = new DataMap();
		if(IDataUtil.isNotEmpty(userInfo)){
			String userId = userInfo.getData(0).getString("USER_ID");
			IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
			if(IDataUtil.isNotEmpty(widenetInfos)){
				IData widenetInfo = widenetInfos.getData(0);
				rtnResult.put("rtnCode", "0");
				rtnResult.put("rtnMsg", "成功");
				object.put("respCode", "0");
				object.put("respDesc","success");
				result.put("broadBandAddress",widenetInfo.getString("STAND_ADDRESS",""));
				String string = widenetInfo.getString("START_DATE","");
				String startDate = SysDateMgr.getDateForYYYYMMDD(string);
				result.put("broadBandBegin",startDate);
				object.put("result",result);
				rtnResult.put("object", object);
				
			}
		}else{
			rtnResult.put("rtnCode", "-9999");
			rtnResult.put("rtnMsg", "失败");
			object.put("respCode", "-9999");
			object.put("respDesc","fail");
			result.put("broadBandAddress","");
			result.put("broadBandBegin","");
			object.put("result",result);
			rtnResult.put("object", object);
		}
		return rtnResult;
	}
	/**
	 * 停复机办理历史记录查询
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public IData queryStopAndOpenRecord(IData params) throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		IData crmpfPubInfo = param.getData("crmpfPubInfo");
		setLoginInfo(crmpfPubInfo);
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER",param.getString("userMobile"));
		inparam.put("START_DATE",param.getString("beginDate"));
		inparam.put("END_DATE",param.getString("endDate"));
		
		String monthParamStr="";
		String startMonth=param.getString("beginDate").substring(4,6);
		if("0".equals(startMonth.charAt(0))){
			startMonth=startMonth.charAt(1)+"";
		}
		String endMonth=param.getString("endDate").substring(4,6);
		if("0".equals(endMonth.charAt(0))){
			endMonth=endMonth.charAt(1)+"";
		}
		int smonth=Integer.parseInt(startMonth);
		int emonth=Integer.parseInt(endMonth);
		if(smonth==emonth){
			monthParamStr=emonth+"";
		}
		
		if(smonth<emonth){
			//mParaStr="10,11,12";
			monthParamStr=smonth+",";
			for(int i=smonth;i<emonth;i++){
				smonth=smonth+1;
				monthParamStr=monthParamStr+smonth+",";
				if(smonth==emonth){
					monthParamStr=monthParamStr.substring(0, monthParamStr.lastIndexOf(","));
					break;
				}
			}
		}
		IDataset hisMainInfos = TradeInfoQry.getReportStopAndOpen(inparam,monthParamStr);
		IData rtnResult=new DataMap();
		IData object=new DataMap();
		IData result=new DataMap();
		IDataset recordInfo=new DatasetList(); 
		if(IDataUtil.isNotEmpty(hisMainInfos)){
			rtnResult.put("rtnCode", "0");
			rtnResult.put("rtnMsg", "成功");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			for(int i=0;i<hisMainInfos.size();i++){
				IData data = new DataMap();
				IData hisMainInfo = hisMainInfos.getData(i);
				data.put("userMobile",hisMainInfo.getString("SERIAL_NUMBER"));
				data.put("opTypeCode",hisMainInfo.getString("TRADE_TYPE_CODE"));
				String tradeType = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_TRADETYPE","TRADE_TYPE_CODE","TRADE_TYPE",hisMainInfo.getString("TRADE_TYPE_CODE"));
				data.put("opTypeName",tradeType);
				data.put("opStaffId",hisMainInfo.getString("TRADE_STAFF_ID"));
				String staffName = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_STAFF","STAFF_ID","STAFF_NAME",hisMainInfo.getString("TRADE_STAFF_ID"));
				data.put("opStaffName",staffName);
				String accept_date = SysDateMgr.getChinaDate(hisMainInfo.getString("ACCEPT_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
				data.put("opTime",accept_date);
				data.put("opChannelCode",hisMainInfo.getString("IN_MODE_CODE"));
				String inMode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_INMODE","IN_MODE_CODE","IN_MODE",hisMainInfo.getString("IN_MODE_CODE"));
				data.put("opChannelName",inMode);
				recordInfo.add(data);
			}
			result.put("recordInfo",recordInfo);
			object.put("result",result);
			rtnResult.put("object", object);
		}else{
			rtnResult.put("rtnCode", "-9999");
			rtnResult.put("rtnMsg", "失败");
			object.put("respCode", "-9999");
			object.put("respDesc", "fail");
			result.put("recordInfo",recordInfo);
			object.put("result",result);
			rtnResult.put("object", object);
		}
		return rtnResult;
	}
	/**
	 * 统一付费关系查询接口
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData queryPayRelation(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		String userMobile = param.getString("userMobile", "");
		String queryMode = param.getString("queryMode", "");
		String effMode = param.getString("effMode", "");
		IData outData = new DataMap();
		IData result = new DataMap();
		IDataset vidData = new DatasetList();
		IDataset payList = new DatasetList();
		IDataset paiData = new DatasetList();
		try {
				if("".equals(userMobile))
				{        		            
					result = prepareOutResult(1,"userMobile不能为空",outData);
					return result;
				}
				if("".equals(queryMode))
				{        		            
					result = prepareOutResult(1,"queryType不能为空",outData);
					return result;
				}
				
				UcaData ucaData = UcaDataFactory.getNormalUca(userMobile);
				String acctId="";
				String userId="";
				if(null!=ucaData){
					acctId = ucaData.getAcctId();
					userId=ucaData.getUser().getUserId();
				}
				if("0".equals(queryMode)&&("0".equals(effMode)||"".equals(effMode)||"1".equals(effMode))){
					vidData=PayRelaInfoQry.qryPayRealInfoByItemAndAcctId("41000",acctId);//付费方-查当前
					paiData=PayRelaInfoQry.getPayrelaByPayItemCode(userId,"41000");//被付费方-查当前
					if(IDataUtil.isNotEmpty(vidData)){
						queryMode="1";
					}else if(IDataUtil.isNotEmpty(paiData)){
						queryMode="2";
					}else{
						result = prepareOutResult(1,"无法查询到该号码作为付费方与被付费方的有效信息",outData);
					}
				}
				if("1".equals(queryMode)&&("0".equals(effMode)||"1".equals(effMode)||"".equals(effMode))){//付费方并查当前
					vidData=PayRelaInfoQry.qryPayRealInfoByItemAndAcctId("41000",acctId);
					if(IDataUtil.isNotEmpty(vidData)){
						for(int j=0;j<vidData.size();j++){
							IData data = vidData.getData(j);
							String user_id = data.getString("USER_ID");
							IDataset relaUUIdB = RelaUUInfoQry.getRelaFKByUserIdB(user_id,"56","2");
							if(IDataUtil.isNotEmpty(relaUUIdB)){
								IData data2 = relaUUIdB.getData(0);
								data.put("START_CYCLE_ID", data2.getString("START_DATE"));
								data.put("PAID_NUMBER", data2.getString("SERIAL_NUMBER_B",""));//副号
							}else{
								data.put("START_CYCLE_ID", data.getString("UPDATE_TIME"));
							}
						}
						payList = payFor(vidData,userMobile);
						outData.put("payList",payList);
						outData.put("resultRows",payList.size()+"");
						result = prepareOutResult(0,"",outData);
					}else{
						result = prepareOutResult(1,"无法查询到该号码作为付费方的有效信息",outData);
					}
				}
				if("2".equals(queryMode)&&("0".equals(effMode)||"1".equals(effMode)||"".equals(effMode))){//被付方并查当前有效的
					vidData=PayRelaInfoQry.getPayrelaByPayItemCode(userId,"41000");
					IData paiparam = new DataMap();
					if(IDataUtil.isNotEmpty(vidData)){
						for(int j=0;j<vidData.size();j++){
							IData data = vidData.getData(j);
							String user_id = data.getString("USER_ID");
							IDataset relaUUIdB = RelaUUInfoQry.getRelaFKByUserIdB(user_id,"56","2");
							if(IDataUtil.isNotEmpty(relaUUIdB)){
								IData data2 = relaUUIdB.getData(0);
								data.put("START_CYCLE_ID", data2.getString("START_DATE"));
								String user_idA = data2.getString("USER_ID_A");
								paiparam.put("USER_ID_A", user_idA);
								paiparam.put("RELATION_TYPE_CODE", "56");
								paiparam.put("ROLE_CODE_B", "1");
								IDataset mainDatas = RelaUUInfoQry.qryAllMebInfoRelaAndMain(paiparam);
								if(IDataUtil.isNotEmpty(mainDatas)){
									IData mainData = mainDatas.getData(0);
									data.put("MAIN_NUMBER", mainData.getString("SERIAL_NUMBER_B",""));//主号
								}
							}else{
								data.put("START_CYCLE_ID", data.getString("UPDATE_TIME"));
							}
						}
						payList = beiPayFor(vidData,userMobile);
						outData.put("payList",payList);
						outData.put("resultRows",payList.size()+"");
						result = prepareOutResult(0,"",outData);
					}else{
						result = prepareOutResult(1,"无法查询到该号码作为被付费方的有效信息",outData);
					}
				}
				return result;
		} catch (Exception e) {
				e.printStackTrace();
				result = prepareOutResult(1,e.getMessage(),outData);
	            return result;
		}
	}
	
	public IDataset payFor(IDataset dataset,String userMobile)throws Exception{//付费方
			IDataset payList = new DatasetList();
			for(int k=0;k<dataset.size();k++){
				IData vidData1 = dataset.getData(k);
				IData payMap = new DataMap();
				payMap.put("payType", "家庭统付");
				payMap.put("payMobileCity", "0898");
				payMap.put("payMobile", userMobile);
				payMap.put("paidMobileCity", "0898");
				/*String countermarkId = vidData1.getString("USER_ID");//副号Id
				UcaData countermarkInfo = UcaDataFactory.getUcaByUserId(countermarkId);//副号信息
				if(null!=countermarkInfo){
					payMap.put("paidMobile",countermarkInfo.getSerialNumber());
				}*/
				payMap.put("paidMobile",vidData1.getString("PAID_NUMBER"));
				payMap.put("priority","");
				payMap.put("comAccId",vidData1.getString("PAYITEM_CODE"));
				payMap.put("comAccName","家庭统付");
				payMap.put("invoiceAccId","");
				payMap.put("invoiceAccName","");
				String limit_type = vidData1.getString("LIMIT_TYPE");
				if("0".equals(limit_type)){
					payMap.put("limitWay","不限定");
					payMap.put("limitFee","");
				}
				if("1".equals(limit_type)){
					payMap.put("limitWay","按金额");
					payMap.put("limitFee",vidData1.getString("LIMIT",""));
				}
				if("2".equals(limit_type)){
					payMap.put("limitWay","按比例");
					payMap.put("limitFee",vidData1.getString("LIMIT",""));
				}
				String effDate = vidData1.getString("START_CYCLE_ID");
				if(effDate.length()>8){
					effDate=SysDateMgr.decodeTimestamp(effDate, SysDateMgr.PATTERN_STAND);
					payMap.put("effDate",effDate);
				}else{
					effDate=SysDateMgr.decodeTimestamp(effDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+SysDateMgr.START_DATE_FOREVER;
					payMap.put("effDate",effDate);
				}
				String expDate = vidData1.getString("END_CYCLE_ID");
				expDate=SysDateMgr.decodeTimestamp(expDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+SysDateMgr.END_DATE;
				payMap.put("expDate",expDate);
				String orderDate = vidData1.getString("UPDATE_TIME");
				orderDate=SysDateMgr.decodeTimestamp(orderDate, SysDateMgr.PATTERN_STAND);
				payMap.put("orderDate",orderDate);
				payMap.put("orderStaffId",vidData1.getString("UPDATE_STAFF_ID"));
				String orderDep = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", vidData1.getString("UPDATE_DEPART_ID"));
				payMap.put("orderDep",orderDep);
				payMap.put("orderChannel","省boss");
				payMap.put("ext1","");
				payMap.put("ext2","");
				payMap.put("ext3","");
				payMap.put("ext4","");
				payMap.put("ext5","");
				payList.add(payMap);
			}
			return payList;
		}
	public IDataset beiPayFor(IDataset dataset,String userMobile)throws Exception{//被付费方
		IDataset payList = new DatasetList();
		for(int n=0;n<dataset.size();n++){
			IData vidData1 = dataset.getData(n);
			IData payMap = new DataMap();
			payMap.put("payType", "家庭统付");
			payMap.put("payMobileCity", "0898");
			/*String payAcctId = vidData1.getString("ACCT_ID");//付费方账户标识
			IDataset payValidDefault = PayRelaInfoQry.getPayRelaBySelUserValidDefault(payAcctId);
			if(IDataUtil.isNotEmpty(payValidDefault)){
				IData payDefault = payValidDefault.getData(0);
				String payUserId = payDefault.getString("USER_ID");
				UcaData payUca = UcaDataFactory.getUcaByUserId(payUserId);
				if(null!=payUca){
					payMap.put("payMobile",payUca.getSerialNumber());//付费方
				}
			}*/
			payMap.put("payMobile",vidData1.getString("MAIN_NUMBER"));//付费方
			payMap.put("paidMobileCity", "0898");
			payMap.put("paidMobile",userMobile);
			payMap.put("priority","");
			payMap.put("comAccId",vidData1.getString("PAYITEM_CODE"));
			payMap.put("comAccName","家庭统付");
			payMap.put("invoiceAccId","");
			payMap.put("invoiceAccName","");
			String limit_type = vidData1.getString("LIMIT_TYPE");
			if("0".equals(limit_type)){
				payMap.put("limitWay","不限定");
				payMap.put("limitFee","");
			}
			if("1".equals(limit_type)){
				payMap.put("limitWay","按金额");
				payMap.put("limitFee",vidData1.getString("LIMIT"));
			}
			if("2".equals(limit_type)){
				payMap.put("limitWay","按比例");
				payMap.put("limitFee",vidData1.getString("LIMIT"));
			}
			String effDate = vidData1.getString("START_CYCLE_ID");
			if(effDate.length()>8){
				effDate=SysDateMgr.decodeTimestamp(effDate, SysDateMgr.PATTERN_STAND);
				payMap.put("effDate",effDate);
			}else{
				effDate=SysDateMgr.decodeTimestamp(effDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+SysDateMgr.START_DATE_FOREVER;
				payMap.put("effDate",effDate);
			}
			String expDate = vidData1.getString("END_CYCLE_ID");
			expDate=SysDateMgr.decodeTimestamp(expDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)+SysDateMgr.END_DATE;
			payMap.put("expDate",expDate);
			String orderDate = vidData1.getString("UPDATE_TIME");
			orderDate=SysDateMgr.decodeTimestamp(orderDate, SysDateMgr.PATTERN_STAND);
			payMap.put("orderDate",orderDate);
			payMap.put("orderStaffId",vidData1.getString("UPDATE_STAFF_ID"));
			String orderDep = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", vidData1.getString("UPDATE_DEPART_ID"));
			payMap.put("orderDep",orderDep);
			payMap.put("orderChannel","省boss");
			payMap.put("ext1","");
			payMap.put("ext2","");
			payMap.put("ext3","");
			payMap.put("ext4","");
			payMap.put("ext5","");
			payList.add(payMap);
		}
		return payList;
	}
	/**
	 * k3
	 * 营销活动保底信息查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData queryBottomInfo(IData params)throws Exception{
		IData param = new DataMap(params.toString()).getData("params");
		IData crmpfPubInfo = param.getData("crmpfPubInfo");
		setLoginInfo(crmpfPubInfo);
		String userMobile = param.getString("userMobile","");
		String packageId = param.getString("packageId","");
		IDataset outData = new DatasetList();
		IData result = new DataMap();
		try {
			if("".equals(userMobile))
			{        		            
				result = prepareOutResult2(1,"userMobile不能为空",outData);
				return result;
			}
			UcaData ucaData = UcaDataFactory.getNormalUca(userMobile);
			IDataset userSaleActives = new DatasetList();
			if("".equals(packageId)){
				userSaleActives =  UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(ucaData.getUserId());
				if(IDataUtil.isNotEmpty(userSaleActives)){
					for(int k=0;k<userSaleActives.size();k++){
						IData saleActive = userSaleActives.getData(k);
						IData retSaleActive = new DataMap();
						retSaleActive.put("userMobile", userMobile);
						retSaleActive.put("bottomAmount", "");
						retSaleActive.put("consumptionAmount", "");
						retSaleActive.put("highAmount", "");
						retSaleActive.put("CumulativeAmount", "");
						retSaleActive.put("specialAmount", "");
						retSaleActive.put("ext1", "");
						retSaleActive.put("ext2", "");
						retSaleActive.put("ext3", "");
						retSaleActive.put("ext4", "");
						retSaleActive.put("ext5", "");
						IDataset bottomList = new DatasetList();
						IDataset comRel = UpcCallIntf.queryOfferComRelOfferByOfferId("K",saleActive.getString("PACKAGE_ID"));
						if(IDataUtil.isNotEmpty(comRel)){
							for(int z=0;z<comRel.size();z++){
								IData comMap = comRel.getData(z);
								String discn = comMap.getString("OFFER_CODE");
								//String discnName = comMap.getString("OFFER_NAME");
								String discnType = comMap.getString("OFFER_TYPE");
								if("D".equals(discnType)){
									//调账务接口
									IDataset bottomInfos = AcctCall.queryBottomInfo(discn);
									if(IDataUtil.isNotEmpty(bottomInfos)){
										IData bottomInfo = bottomInfos.getData(0);
										String bottomSubject = bottomInfo.getString("BD_ITEM_ID");
										String bottomTpye = bottomInfo.getString("BD_TYPE");
										String bottomNum = bottomInfo.getString("BD_FEE");
										IData bottomMap = new DataMap();
										bottomMap.put("bottomSubject", bottomSubject);//保底科目-账务
										bottomMap.put("bottomTpye", bottomTpye);//保底类别-账务
										bottomMap.put("productName", saleActive.getString("PACKAGE_NAME",""));
										bottomMap.put("bottomNum", bottomNum);//保底金额-账务
										String startTime = saleActive.getString("START_DATE","");
										String endTime = saleActive.getString("END_DATE","");
										int monthInterval=1;
										if(!"".equals(startTime)&&!"".equals(endTime)){
											monthInterval = SysDateMgr.monthInterval(startTime,endTime);//月差值
											startTime=SysDateMgr.decodeTimestamp(startTime, SysDateMgr.PATTERN_STAND_SHORT);
											endTime=SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND_SHORT);
										}
										bottomMap.put("startTime", startTime);
										bottomMap.put("endTime", endTime);
										bottomMap.put("productCode", saleActive.getString("PACKAGE_ID",""));
										if(StringUtils.isNotBlank(bottomNum)){
											bottomMap.put("bottomInfluence", "按约定扣费");
											int IntbottomNum = Integer.parseInt(bottomNum);
											bottomMap.put("consumptionNum", String.valueOf(IntbottomNum*monthInterval));//实际已消费金额-bottomNum*月差值
										}else{
											bottomMap.put("bottomInfluence", "非约定扣费");
											bottomMap.put("consumptionNum", "");//实际已消费金额-bottomNum*月差值
										}
										bottomList.add(bottomMap);
									}
								}
							}
						}
						if(IDataUtil.isEmpty(bottomList)){
							continue;
						}
						retSaleActive.put("bottomList", bottomList);
						outData.add(retSaleActive);
					}
					result=prepareOutResult2(0,"",outData);
					if(IDataUtil.isEmpty(outData)){
						result=prepareOutResult2(1,"无该用户的营销活动保底信息",outData);
					}
				}else{
					result=prepareOutResult2(1,"无该用户的营销活动信息",outData);
				}
			}else{
				String productId = "";
	        	IDataset catalogInfo = UpcCall.qryCatalogByOfferId(packageId,"K");
		        if(!IDataUtil.isEmpty(catalogInfo)){
		        	productId = catalogInfo.getData(0).getString("CATALOG_ID", "");
		        }
		        IDataset saleActives = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(ucaData.getUserId(),productId,packageId);
		        if(IDataUtil.isNotEmpty(saleActives)){
		        	IData saleActive = saleActives.getData(0);
		        	IData retSaleActive = new DataMap();
					retSaleActive.put("userMobile", userMobile);
					retSaleActive.put("bottomAmount", "");
					retSaleActive.put("consumptionAmount", "");
					retSaleActive.put("highAmount", "");
					retSaleActive.put("CumulativeAmount", "");
					retSaleActive.put("specialAmount", "");
					retSaleActive.put("ext1", "");
					retSaleActive.put("ext2", "");
					retSaleActive.put("ext3", "");
					retSaleActive.put("ext4", "");
					retSaleActive.put("ext5", "");
					IDataset bottomList = new DatasetList();
					IDataset comRel = UpcCallIntf.queryOfferComRelOfferByOfferId("K",packageId);
					if(IDataUtil.isNotEmpty(comRel)){
						for(int z=0;z<comRel.size();z++){
							IData comMap = comRel.getData(z);
							String discn = comMap.getString("OFFER_CODE");
							//String discnName = comMap.getString("OFFER_NAME");
							String discnType = comMap.getString("OFFER_TYPE");
							if("D".equals(discnType)){
								//调账务接口
								IDataset bottomInfos = AcctCall.queryBottomInfo(discn);
								if(IDataUtil.isNotEmpty(bottomInfos)){
									IData bottomInfo = bottomInfos.getData(0);
									String bottomSubject = bottomInfo.getString("BD_ITEM_ID");
									String bottomTpye = bottomInfo.getString("BD_TYPE");
									String bottomNum = bottomInfo.getString("BD_FEE");
									IData bottomMap = new DataMap();
									bottomMap.put("bottomSubject", bottomSubject);//保底科目-账务
									bottomMap.put("bottomTpye", bottomTpye);//保底类别-账务
									bottomMap.put("productName", saleActive.getString("PACKAGE_NAME",""));
									bottomMap.put("bottomNum", bottomNum);//保底金额-账务
									String startTime = saleActive.getString("START_DATE","");
									String endTime = saleActive.getString("END_DATE","");
									int monthInterval=1;
									if(!"".equals(startTime)&&!"".equals(endTime)){
										monthInterval = SysDateMgr.monthInterval(startTime,endTime);//月差值
										startTime=SysDateMgr.decodeTimestamp(startTime, SysDateMgr.PATTERN_STAND_SHORT);
										endTime=SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND_SHORT);
									}
									bottomMap.put("startTime", startTime);
									bottomMap.put("endTime", endTime);
									bottomMap.put("productCode", packageId);
									if(StringUtils.isNotBlank(bottomNum)){
										bottomMap.put("bottomInfluence", "按约定扣费");
										int IntbottomNum = Integer.parseInt(bottomNum);
										bottomMap.put("consumptionNum", String.valueOf(IntbottomNum*monthInterval));//实际已消费金额-bottomNum*月差值
									}else{
										bottomMap.put("bottomInfluence", "非约定扣费");
										bottomMap.put("consumptionNum", "");//实际已消费金额-bottomNum*月差值
									}
									bottomList.add(bottomMap);
								}
							}
						}
					}
					retSaleActive.put("bottomList", bottomList);
					outData.add(retSaleActive);
					result=prepareOutResult2(0,"",outData);
					if(IDataUtil.isEmpty(bottomList)){
						outData.clear();
						result=prepareOutResult2(1,"无该用户的营销活动保底信息",outData);
					}
		        }else{
		        	result=prepareOutResult2(1,"无该营销活动信息【"+packageId+"】",outData);
		        }
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result = prepareOutResult2(1,e.getMessage(),outData);
            return result;
		}
	}
	/**
	 * 客户全球通等级信息查询接口
	 * k3
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData queryBrandLevelCode(IData param)throws Exception{
		System.out.println("============param============="+param);
		String serviceNumber = param.getString("ServiceNumber", "");
		String serviceType = param.getString("ServiceType", "");
		IData rtnData = new DataMap();
		IData inMap = new DataMap();
		rtnData.put("BizCode", "0000");
		rtnData.put("BizDesc", "成功");
		rtnData.put("OprTime", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
		if(StringUtils.isBlank(serviceNumber)){
			rtnData.put("BizCode", "2999");
			rtnData.put("BizDesc", "手机号不能为空");
			return rtnData;
		}
		if(!"01".equals(serviceType)){
			rtnData.put("BizCode", "2999");
			rtnData.put("BizDesc", "请输入正确的标识类型");
			return rtnData;
		}
		try {
			UcaData ucaData = UcaDataFactory.getNormalUca(serviceNumber);
			if(null!=ucaData){
				String userId = ucaData.getUser().getUserId();
				inMap.put("USER_ID",userId);
				IDataset dataList = UserClassInfoQry.queryUserClass(inMap);
				if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){	 	 
	                IData outMap = dataList.getData(0);	 	 
	            switch (Integer.parseInt(outMap.getString("USER_CLASS"))) {	 	 
	            case 1:	 	 
	            	rtnData.put("BrandLevel", "01");//全球通-银卡	 	 
	                break;	 	 
	            case 2:	 	 
	            	rtnData.put("BrandLevel", "02");//全球通-金卡	 
	                break;	 	 
	            case 3:	 	 
	            	rtnData.put("BrandLevel", "03");//全球通-白金卡	 	 
	                break;	 	 
	            case 4:	 	 
	            	rtnData.put("BrandLevel", "04");//全球通-钻石卡	 
	                break;	 	 
	            case 5:	 	 
	            	rtnData.put("BrandLevel", "06");//非全球通用户	 	 
	                break;	 	 
	            case 6:	 	 
	            	rtnData.put("BrandLevel", "05");//6、全球通体验用户	 	 
	                break;	 	 
	            default:	 	 
	            	rtnData.put("BrandLevel", "");
	                break;	 	 
	            }	 	 
	        }else{	 	 
	        	rtnData.put("BrandLevel", ""); 
	        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			rtnData.put("BizCode", "2999");
			rtnData.put("BizDesc", e.getMessage());
		}
		
		return rtnData;
	}
	/**
	 * 是否具有赠送光猫的权限
	 * k3
	 * @param staffParam
	 * @return
	 * @throws Exception
	 */
	public IData isHasModemStyleRight(IData staffParam)throws Exception{
		IData modemRight=new DataMap();
		modemRight.put("RSP_CODE", "00");
		modemRight.put("RSP_DESC", "查询成功");
		try {
			
			String staffId = IDataUtil.chkParam(staffParam, "STAFF_ID");
			IDataset result = ActiveStockInfoQry.querySparkPlans("0898",staffId,"FTTH");
			if(IDataUtil.isEmpty(result)){
				modemRight.put("SURPLUS_VALUE", "0");
			}else{
				modemRight.put("SURPLUS_VALUE", result.getData(0).getString("SURPLUS_VALUE"));//剩余光猫数量
				//modemRight.put("ALLOCAT_VALUE", result.getData(0).getInt("WARNNING_VALUE_D"));//光猫分配量
				//modemRight.put("TRANSACT_VALUE", result.getData(0).getInt("WARNNING_VALUE_U"));//已办理量
			}
		} catch (Exception e) {
			modemRight.put("RSP_CODE", "2998");
			modemRight.put("RSP_DESC",e.getMessage());
		}
		return modemRight;
		
	}
	
	
    public void setLoginInfo(IData crmpfPubInfo)throws Exception{
		BizVisit visit = CSBizBean.getVisit();
    	visit.setDepartId(crmpfPubInfo.getString("orgId","36601"));
    	visit.setCityCode(crmpfPubInfo.getString("cityCode","HNSJ"));
    	visit.setLoginEparchyCode(crmpfPubInfo.getString("countryCode","0898"));	
    	visit.setStaffId(crmpfPubInfo.getString("staffId","SUPERUSR"));
	}
	public IData prepareOutResult(int i,String rtnMsg,IData outData)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("resultRows", outData.getString("resultRows","1"));
        	outData.remove("resultRows");
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
	
	/**
	 * k3
	 * IVR来电客户关键信息 REQ201910230025
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData ivrCustomerKeyMessage(IData input)throws Exception{
		IData rtnData = new DataMap();
		IData object = new DataMap();
		IDataset beans = new DatasetList();
		IData bean = new DataMap();
		IData levelParam = new DataMap();
		rtnData.put("rtnCode", "0");
		rtnData.put("rtnMsg", "成功");
		object.put("respCode", "0");
		object.put("respDesc", "success");
		try {
			IData params=new DataMap(input.toString()).getData("params");
			String sn = IDataUtil.chkParam(params, "userMobile");
			IData crmpfPubInfo = params.getData("crmpfPubInfo");
			setLoginInfo(crmpfPubInfo);
			
			String ivrTag = BizEnv.getEnvString("ivr.customer.tag");
			//查表查询
			if("1".equals(ivrTag)){
				StringBuilder sql=new StringBuilder();
				sql.append("SELECT * FROM TF_F_IVR_CUSTINFO WHERE SERIAL_NUMBER=:SERIAL_NUMBER");
				IData inParam=new DataMap();
				inParam.put("SERIAL_NUMBER", sn);
				IDataset custDatas=Dao.qryBySql(sql, inParam, Route.CONN_CRM_UPCCUS);
				if(IDataUtil.isEmpty(custDatas)){
					rtnData.put("rtnCode", "2999");
					rtnData.put("rtnMsg", "未查到有效的用户资料");
					object.put("respCode", "2999");
					object.put("respDesc", "未查到有效的用户资料");
				}else{
					IData custData=custDatas.getData(0);
					String creditClass = custData.getString("CREDIT_CLASS", "");
					if ("0".equals(creditClass)){
	        			//outData.put("starLevel","准星");
						bean.put("telNumStarCode","12");
	        		}
	        		else if ("1".equals(creditClass)){
	        			//outData.put("starLevel","一星");
	        			bean.put("telNumStarCode","11");
	        		}
	        		else if ("2".equals(creditClass)){
	        			//outData.put("starLevel","二星");
	        			bean.put("telNumStarCode","10");
	        		}
	        		else if ("3".equals(creditClass)){
	        			//outData.put("starLevel","三星");
	        			bean.put("telNumStarCode","09");
	        		}
	        		else if ("4".equals(creditClass)){
	        			//outData.put("starLevel","四星");
	        			bean.put("telNumStarCode","08");
	        		}
	        		else if ("5".equals(creditClass)){
	        			//outData.put("starLevel","五星（银/普通）");
	        			bean.put("telNumStarCode","07");
	        		}
	        		else if ("6".equals(creditClass)){
	        			//outData.put("starLevel","五星（金）");
	        			bean.put("telNumStarCode","06");
	        		}
	        		else if ("7".equals(creditClass)){
	        			//outData.put("starLevel","五星（钻）");
	        			bean.put("telNumStarCode","05");
	        		}
	        		else if ("-1".equals(creditClass)){
		        		//outData.put("starLevel","未评级");
	        			bean.put("telNumStarCode","13");
	        		}
	        		else{
		        		//outData.put("starLevel","未评级");
	        			bean.put("telNumStarCode","13");
	        		}
					
					switch (Integer.parseInt(custData.getString("USER_CLASS","0"))) {	 	 
		            case 1:	 	 
		            	bean.put("brandLevelCode", "04");//全球通-银卡	 	 
		                break;	 	 
		            case 2:	 	 
		            	bean.put("brandLevelCode", "03");//全球通-金卡	 
		                break;	 	 
		            case 3:	 	 
		            	bean.put("brandLevelCode", "02");//全球通-白金卡	 	 
		                break;	 	 
		            case 4:	 	 
		            	bean.put("brandLevelCode", "01");//全球通-钻石卡	 
		                break;	 	 
		            case 5:	 	 
		            	bean.put("brandLevelCode", "");//非全球通用户	 	 
		                break;	 	 
		            case 6:	 	 
		            	bean.put("brandLevelCode", "05");//6、全球通体验用户	 	 
		                break;	 	 
		            default:	 	 
		            	bean.put("brandLevelCode", "99");
		                break;	 	 
		            }
					bean.put("userBrand", custData.getString("USER_BRAND"));
					//bean.put("userBrandName",custData.getString("USER_BRAND_NAME"));
					//bean.put("telNumState",custData.getString("USER_STATE_NAME"));
					bean.put("isBroadbandCustomer",custData.getString("IS_BROADBAND_USER","0"));
					
					if(StringUtils.isNotEmpty(custData.getString("LAST_STOP_TIME"))){
						bean.put("lastStopTime", SysDateMgr.date2String((SysDateMgr.string2Date(custData.getString("LAST_STOP_TIME"), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					}else{
						bean.put("lastStopTime","");
					}
					
				    bean.put("isRealNameInfo", custData.getString("IS_REAL_NAME","0"));
					bean.put("isImportantCustomer", custData.getString("IS_IMPORTANT_CUSTOMER","0"));
					bean.put("isGrpKeyPerson", custData.getString("IS_GRPKEY_PERSON","0"));
					bean.put("isGrpContact", custData.getString("IS_GRP_COMTACT","0"));
					bean.put("isGrpManager", custData.getString("IS_GRP_MANAGER","0"));
					//bean.put("flag5G", custData.getString("FLAG_5G",""));
					bean.put("currentBusinessArea", custData.getString("BUSINESS_AREA",""));
					
					beans.add(bean);
				}			
			//正常查询	
			}else{
			
				UcaData uca = UcaDataFactory.getNormalUca(sn);
				String userId="";
				String custId="";
				if(uca!=null){
					userId=uca.getUser().getUserId();
					custId=uca.getCustId();
					//客户星级
					IDataset resutls =CreditCall.getCreditInfo(userId, "0");
					if(IDataUtil.isNotEmpty(resutls)){
						String creditClass = resutls.getData(0).getString("CREDIT_CLASS", "");
						if ("0".equals(creditClass)){
		        			//outData.put("starLevel","准星");
							bean.put("telNumStarCode","12");
		        		}
		        		else if ("1".equals(creditClass)){
		        			//outData.put("starLevel","一星");
		        			bean.put("telNumStarCode","11");
		        		}
		        		else if ("2".equals(creditClass)){
		        			//outData.put("starLevel","二星");
		        			bean.put("telNumStarCode","10");
		        		}
		        		else if ("3".equals(creditClass)){
		        			//outData.put("starLevel","三星");
		        			bean.put("telNumStarCode","09");
		        		}
		        		else if ("4".equals(creditClass)){
		        			//outData.put("starLevel","四星");
		        			bean.put("telNumStarCode","08");
		        		}
		        		else if ("5".equals(creditClass)){
		        			//outData.put("starLevel","五星（银/普通）");
		        			bean.put("telNumStarCode","07");
		        		}
		        		else if ("6".equals(creditClass)){
		        			//outData.put("starLevel","五星（金）");
		        			bean.put("telNumStarCode","06");
		        		}
		        		else if ("7".equals(creditClass)){
		        			//outData.put("starLevel","五星（钻）");
		        			bean.put("telNumStarCode","05");
		        		}
		        		else if ("-1".equals(creditClass)){
			        		//outData.put("starLevel","未评级");
		        			bean.put("telNumStarCode","13");
		        		}
		        		else{
			        		//outData.put("starLevel","未评级");
		        			bean.put("telNumStarCode","13");
		        		}
					}else{
						//outData.put("starLevel","未评级");
		    			bean.put("telNumStarCode","13");
					}
					//全球通用户级别
					levelParam.put("USER_ID",userId);
					IDataset dataList = UserClassInfoQry.queryUserClass(levelParam);
					if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){	 	 
		                IData outMap = dataList.getData(0);	 	 
		            switch (Integer.parseInt(outMap.getString("USER_CLASS"))) {	 	 
		            case 1:	 	 
		            	bean.put("brandLevelCode", "04");//全球通-银卡	 	 
		                break;	 	 
		            case 2:	 	 
		            	bean.put("brandLevelCode", "03");//全球通-金卡	 
		                break;	 	 
		            case 3:	 	 
		            	bean.put("brandLevelCode", "02");//全球通-白金卡	 	 
		                break;	 	 
		            case 4:	 	 
		            	bean.put("brandLevelCode", "01");//全球通-钻石卡	 
		                break;	 	 
		            case 5:	 	 
		            	bean.put("brandLevelCode", "");//非全球通用户	 	 
		                break;	 	 
		            case 6:	 	 
		            	bean.put("brandLevelCode", "05");//6、全球通体验用户	 	 
		                break;	 	 
		            default:	 	 
		            	bean.put("brandLevelCode", "99");
		                break;	 	 
		            }	 	 
		        }else{	 	 
		        	bean.put("brandLevelCode", ""); 
		        }
					//品牌编码
					bean.put("userBrand", uca.getBrandCode());
					String band_name=UBrandInfoQry.getBrandNameByBrandCode(uca.getBrandCode());
					//品牌名称
					//bean.put("userBrandName", band_name);
					//客户状态
					String stateCodeset = uca.getUser().getUserStateCodeset();
					String stateDesc = StaticUtil.getStaticValue("USER_STATE_CODESET",stateCodeset);
					//bean.put("telNumState",stateDesc);
					//宽带用户
					IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + sn);
					if(IDataUtil.isNotEmpty(userInfo)){
						bean.put("isBroadbandCustomer","1");//宽带用户
					}else{
						bean.put("isBroadbandCustomer","0");//非宽带用户
					}
					if("".equals(uca.getUser().getLastStopTime()) || uca.getUser().getLastStopTime() == null)
			        {
						bean.put("lastStopTime", "");
			        }
			        else{
			        	bean.put("lastStopTime", SysDateMgr.date2String((SysDateMgr.string2Date(uca.getUser().getLastStopTime(), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			        }
					//是否实名
					if(StringUtils.isNotBlank(uca.getCustomer().getIsRealName())){
						bean.put("isRealNameInfo", uca.getCustomer().getIsRealName());
					}else {
						bean.put("isRealNameInfo", "");
					}
					/*//是否集团联系人+是否集团关键人
					levelParam.clear();
					levelParam.put("SERIAL_NUMBER", sn);
					bean.put("isGrpKeyPersons", "0");//非集团关键人
					bean.put("isGrpContacts", "0");//非集团联系人
					IDataset groupInfos = GrpExtInfoQry.queryGroupKeyAndLinkman(levelParam);
					if(IDataUtil.isNotEmpty(groupInfos)){
						bean.put("isGrpKeyPersons", "1");//集团关键人
						for(int k=0;k<groupInfos.size();k++){
							IData groupInfo = groupInfos.getData(k);
							String rsrvStr1 = groupInfo.getString("RSRV_STR1");
							if("3".equals(rsrvStr1)){
								bean.put("isGrpContacts", "1");//集团联系人
								break;
							}
						}
					}*/
					//是否党政军客户+是否集团联系人+是否集团关键人
					bean.put("isImportantCustomer", "0");//非党政军客户
					bean.put("isGrpKeyPerson", "0");//非集团关键人
					bean.put("isGrpContact", "0");//非集团联系人
					IDataset cusGroupInfo = CustGroupInfoQry.qryGroupInfoByCustId(userId);
					if (IDataUtil.isNotEmpty(cusGroupInfo))
			        {
						IData groupInfo = cusGroupInfo.getData(0);
						String memberKind = groupInfo.getString("MEMBER_KIND","");
						if("3".equals(memberKind)) {
							bean.put("isImportantCustomer", "1");//党政军客户
						}else if ("2".equals(memberKind)) {
							bean.put("isGrpKeyPerson", "1");//集团关键人
						}else if ("1".equals(memberKind)) {
							bean.put("isGrpContact", "1");//集团联系人
						}
			        }
					//是否集团客户经理
					levelParam.put("CUST_SERIAL_NUMBER", sn);
					bean.put("isGrpManager", "0");//非集团客户经理
					IDataset custManager = CustManagerTJNumQry.getCustManagerBySerialNumber(levelParam);
					if(IDataUtil.isNotEmpty(custManager)){
						bean.put("isGrpManager", "1");//集团客户经理
					}
					//bean.put("flag5G", "");
					String cityCode = uca.getUser().getCityCode();
					String cityName = UAreaInfoQry.getAreaNameByAreaCode(cityCode);
					bean.put("currentBusinessArea",cityCode);
					//bean.put("flag5G", "");
					beans.add(bean);
				}else{
					rtnData.put("rtnCode", "2999");
					rtnData.put("rtnMsg", "未查到有效的用户资料");
					object.put("respCode", "2999");
					object.put("respDesc", "未查到有效的用户资料");
				}
			}
		} catch (Exception e) {
			log.error(e);
			rtnData.put("rtnCode", "2999");
			rtnData.put("rtnMsg", e.getMessage());
			object.put("respCode", "2999");
			object.put("respDesc",  e.getMessage());
		}
		object.put("bean", bean);
		rtnData.put("object", object);
		return rtnData;
	}
	
	public IData prepareOutResult2(int i,String rtnMsg,IDataset outData)
	{
		IData object = new DataMap();
		IData result = new DataMap();
		
		if (i==0)//成功
		{
			object.put("result", outData);
			object.put("respCode", "0");
			object.put("respDesc", "success");
			
			result.put("object", object);
			result.put("rtnCode", "0");	
			result.put("rtnMsg", "成功!");	
			return result;
		}
		else if(i==1)//失败
		{
			object.put("result", outData);
			object.put("respCode", "-1");
			object.put("respDesc", rtnMsg);
			
			result.put("object", object);
			result.put("rtnCode", "-9999");	
			result.put("rtnMsg", "失败");	
			
			return result;
		}
		return null;
	}

}
