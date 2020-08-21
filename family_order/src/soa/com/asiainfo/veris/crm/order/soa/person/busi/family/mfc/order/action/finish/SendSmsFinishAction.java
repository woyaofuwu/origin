package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

public class SendSmsFinishAction implements ITradeFinishAction
{
    private static transient Logger log = Logger.getLogger(SendSmsFinishAction.class);

    //private String productCode = "";//产品编码MFC000001-统付 ，MFC000002-个付，MFC000003-5G家庭会员群组，MFC000004-5G家庭套餐群组，MFC000005-5G融合套餐群组
	//MFC000006-全国亲情网(支付宝版月包)，MFC000007-全国亲情网(支付宝版季包)，MFC000008-全国亲情网(支付宝版年包)
    //MFC000009-全国亲情网(异网版月包)，MFC000010-全国亲情网(异网版季包)，MFC000011-全国亲情网(异网版年包)
	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("11111111111111111111111111SendSmsFinishAction11111111111111");
        }
        //对账过来的，不发短信
        String sendType = mainTrade.getString("RSRV_STR6","");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
        if("1".equals(sendType)){
        	return;
        }
        //报停短信
        if("2584".equals(tradeTypeCode)){
			//productCode = mainTrade.getString("RSRV_STR2","");
        	stopMachineSendSms(mainTrade);
        	return;
        }
		//报开短信
		if("2585".equals(tradeTypeCode)){
			//productCode = mainTrade.getString("RSRV_STR2","");
			openMachineSendSms(mainTrade);
			return;
		}

        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset relaUUDatas = MfcCommonUtil.getUserRelationByTradeId(tradeId);
        if(DataUtils.isEmpty(relaUUDatas)){
        	return;
        }
        String orderSource = mainTrade.getString("RSRV_STR7","");
        String operType = relaUUDatas.getData(0).getString("RSRV_STR5","");
        if (log.isDebugEnabled())
        {
            log.debug("==========SendSmsFinishAction===operType=============="+operType);
            log.debug("==========SendSmsFinishAction===orderSource=============="+orderSource);
            log.debug("==========SendSmsFinishAction===relaUUDatas=============="+relaUUDatas);
            log.debug("==========SendSmsFinishAction===mainTrade=============="+mainTrade);
        }
        if("01".equals(operType) && !"05".equals(orderSource)){
        	openFamilySendSms(mainTrade,relaUUDatas);
        }else if("02".equals(operType) && !"05".equals(orderSource)){ //取消家庭亲情网
        	cancelFamilySendSms(mainTrade,relaUUDatas);
        }else if("03".equals(operType) && !"05".equals(orderSource)){//新增成员
        	addFamilySendSms(mainTrade,relaUUDatas);
        }else if("04".equals(operType) && !"05".equals(orderSource)){//删除成员
        	//主号删除副号和副号主动退出区分
        	dealQuitFamilySendSms(mainTrade,relaUUDatas);
        }else{
        	return;
        }
    }

	private void openMachineSendSms(IData mainTrade) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("================openMachineSendSms============");
		}
		String userId = mainTrade.getString("USER_ID");
		String productOfferingId = mainTrade.getString("RSRV_STR3");
		String productCode = mainTrade.getString("RSRV_STR2","");
		IData inData = new DataMap();
		inData.put("PRODUCT_OFFERING_ID",productOfferingId);
		IDataset allUUInfos=MfcCommonUtil.getSEL_USER_ROLEA(userId,"","MF",inData);
		if(log.isDebugEnabled()){
			log.debug("================allUUInfos============"+allUUInfos);
		}
		String custPhone = mainTrade.getString("RSRV_STR1","");//主号
		if(IDataUtil.isNotEmpty(allUUInfos)){
			for(int i=0;i<allUUInfos.size();i++){
				IData smsdata=new DataMap();
				IData uuData=allUUInfos.getData(i);
				String privTag=uuData.getString("RSRV_STR1","");//本省、外省
				String roleCodeB=uuData.getString("ROLE_CODE_B","");//主号、副号
				String serialNum= uuData.getString("SERIAL_NUMBER_B","");
				IData userinfo =  UcaInfoQry.qryUserInfoBySn(serialNum,RouteInfoQry.getEparchyCodeBySn(serialNum));
				log.debug("--insertTradeSMS-userinfo="+userinfo);
				if(IDataUtil.isEmpty(userinfo)){
					continue;
				}
				if(StringUtils.isBlank(productCode)){
					productCode = MfcCommonUtil.PRODUCT_CODE_TF;
				}
				/**
				 * 个付修改：MCF_OPEN_SMS 配置；
				 */
				IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_OPEN_SMS", "ZZZZ");
				String templateId = "";
				if(IDataUtil.isNotEmpty(config)){
					if(StringUtils.equals(productCode,"MFC000001")){//统付
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE1","");//统付主号复机短信的模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE2","");//统付成员复机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000002")){//个付
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE3","");//个付主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE4","");//个付成员复机短信模板ID
						}
					}
				}

				IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_OPEN_5GSMS", "ZZZZ");
				if(IDataUtil.isNotEmpty(fgconfig)){
					if(StringUtils.equals(productCode,"MFC000003")){//5G家庭会员群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE1","");//5G家庭会员群组主号复机短信的模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE2","");//5G家庭会员群组成员复机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000004")){//5G家庭套餐群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE3","");//5G家庭套餐群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE4","");//5G家庭套餐群组成员复机短信模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000005")){//5G融合套餐群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE5","");//5G融合套餐群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE6","");//5G融合套餐群组成员复机短信模板ID
						}
					}
				}
				
				IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_OPEN_ZFBSMS", "ZZZZ");
				if(IDataUtil.isNotEmpty(zfbconfig)){
					if(StringUtils.equals(productCode,"MFC000006")){//全国亲情网(支付宝版月包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE1","");//全国亲情网(支付宝版月包)群组主号复机短信的模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE2","");//全国亲情网(支付宝版月包)群组成员复机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000007")){//全国亲情网(支付宝版季包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE3","");//全国亲情网(支付宝版季包)群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE4","");//全国亲情网(支付宝版季包)群组成员复机短信模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000008")){//全国亲情网(支付宝版年包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE5","");//全国亲情网(支付宝版年包)群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE6","");//全国亲情网(支付宝版年包)群组成员复机短信模板ID
						}
					}
				}
				
				IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_OPEN_YWSMS", "ZZZZ");
				if(IDataUtil.isNotEmpty(ywconfig)){
					if(StringUtils.equals(productCode,"MFC000009")){//全国亲情网(异网版月包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE1","");//全国亲情网(异网版月包)群组主号复机短信的模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE2","");//全国亲情网(异网版月包)群组成员复机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000010")){//全国亲情网(异网版季包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE3","");//全国亲情网(异网版季包)群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE4","");//全国亲情网(异网版季包)群组成员复机短信模板ID
						}
					}else if(StringUtils.equals(productCode,"MFC000011")){//全国亲情网(异网版年包)
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE5","");//全国亲情网(异网版年包)群组主号复机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE6","");//全国亲情网(异网版年包)群组成员复机短信模板ID
						}
					}
				}

				if(StringUtils.isBlank(templateId)){
					continue;
				}
				//根据模板ID获取短信
				IData iData = new DataMap();
				iData.put("MFC_CUST_PHONE",custPhone);
				String poidCode = MfcCommonUtil.getPoidInfoAndLable(custPhone,productOfferingId,StringUtils.equals(custPhone,serialNum)? null : serialNum ).getString("POID_CODE");
				iData.put("PRODUCT_OFFERING_ID",poidCode);
				String content = MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
				smsdata.put("RECV_OBJECT",serialNum); // 成员号码
				smsdata.put("RECV_ID",serialNum);
				smsdata.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(serialNum));
				smsdata.put("SMS_PRIORITY", "50");
				smsdata.put("NOTICE_CONTENT", content);
				smsdata.put("SMS_NOTICE_ID", SeqMgr.getInstId());
				smsdata.put("FORCE_OBJECT", "10086");
				smsdata.put("REMARK", "跨省家庭网(全国)主号复机");
				log.debug("--openMachineSendSms-smsdata="+smsdata);
				SmsSend.insSms(smsdata,RouteInfoQry.getEparchyCodeBySn(serialNum));
				log.debug("--openMachineSendSms-smsdata2="+smsdata);
			}
		}

	}

	/**
     * 报停
     * @throws Exception
     */
    private void stopMachineSendSms(IData mainTrade) throws Exception {
    	String userId = mainTrade.getString("USER_ID");
    	String productOfferingId = mainTrade.getString("RSRV_STR3");
    	String productCode = mainTrade.getString("RSRV_STR2");
    	IData inData = new DataMap();
    	inData.put("PRODUCT_OFFERING_ID",productOfferingId);
    	IDataset allUUInfos=MfcCommonUtil.getSEL_USER_ROLEA(userId,"","MF",inData);
    	String custPhone = mainTrade.getString("RSRV_STR1","");//主号
    	log.debug("--insertTradeSMS-allUUInfos="+allUUInfos);
    	if(IDataUtil.isNotEmpty(allUUInfos)){
    		for(int i=0;i<allUUInfos.size();i++){
    			IData smsdata=new DataMap();
    			IData uuData=allUUInfos.getData(i);
    			String privTag=uuData.getString("RSRV_STR1","");//本省、外省
    			//String productOfferingId = uuData.getString("RSRV_STR2","");//业务订购实例ID
    			String roleCodeB=uuData.getString("ROLE_CODE_B","");//主号、副号
    			String serialNum= uuData.getString("SERIAL_NUMBER_B","");
    			IData userinfo =  UcaInfoQry.qryUserInfoBySn(serialNum,RouteInfoQry.getEparchyCodeBySn(serialNum));
    			log.debug("--insertTradeSMS-userinfo="+userinfo);
    			if(IDataUtil.isEmpty(userinfo)){
    				continue;
    			}
    			if(StringUtils.isBlank(productCode)){
					productCode = MfcCommonUtil.PRODUCT_CODE_TF;
				}
				/**
				 * 个付修改：MCF_SEND_SMS 配置；PARA_CODE3配置统付主号停机短信的模板ID ，PARA_CODE4配置个付主号停机短信模板ID
				 * PARA_CODE5配置统付成员停机短信的模板ID ，PARA_CODE6配置个付成员停机短信模板ID
				 */
				IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_SMS", "ZZZZ");

				IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_5GSMS", "ZZZZ");
				
				IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_ZFBSMS", "ZZZZ");

				IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_YWSMS", "ZZZZ");

				String templateId = "";
				if(IDataUtil.isNotEmpty(config)){
					if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF)){//统付
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE3","");//统付主号停机短信的模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE5","");//统付成员停机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_ZF)){//个付
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE4","");//个付主号停机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=config.getData(0).getString("PARA_CODE6","");//个付成员停机短信模板ID
						}
					}
				}

				if(IDataUtil.isNotEmpty(fgconfig)){
					if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_5G3)){//5G家庭会员群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE1","");//5G家庭会员群组主号停机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE2","");//5G家庭会员群组成员停机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_5G4)){//5G家庭套餐群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE3","");//5G家庭套餐群组主号停机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE4","");//5G家庭套餐群组成员停机短信模板ID
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_5G5)){//5G融合套餐群组
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE5","");//5G融合套餐群组主号停机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=fgconfig.getData(0).getString("PARA_CODE6","");//个付成员停机短信模板ID
						}
					}
				}
				
				
				if(IDataUtil.isNotEmpty(zfbconfig)){
					if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF6)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE1","");//主号停机短信模板ID
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE2","");//成员停机短信的模板ID
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF7)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE3","");
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE4","");
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF8)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE5","");
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=zfbconfig.getData(0).getString("PARA_CODE6","");
						}
					}
				}
				
				
				if(IDataUtil.isNotEmpty(ywconfig)){
					if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF9)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE1","");
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE2","");
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF10)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE3","");
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE4","");
						}
					}else if(StringUtils.equals(productCode,MfcCommonUtil.PRODUCT_CODE_TF11)){
						if("1".equals(privTag)&&"1".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE5","");
						}else if("1".equals(privTag)&&"2".equals(roleCodeB)){
							templateId=ywconfig.getData(0).getString("PARA_CODE6","");
						}
					}
				}
				
				if(StringUtils.isBlank(templateId)){
					continue;
				}
				//根据模板ID获取短信
				IData iData = new DataMap();
				iData.put("MFC_CUST_PHONE",custPhone);
				String poidCode = MfcCommonUtil.getPoidInfoAndLable(custPhone,productOfferingId,StringUtils.equals(custPhone,serialNum)? null : serialNum ).getString("POID_CODE");
				iData.put("PRODUCT_OFFERING_ID",poidCode);
				String content = MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
    			smsdata.put("RECV_OBJECT",serialNum); // 成员号码
			    smsdata.put("RECV_ID", serialNum); 
			    smsdata.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(serialNum));
			    smsdata.put("SMS_PRIORITY", "50");
			    smsdata.put("SMS_NOTICE_ID", SeqMgr.getInstId());
			    smsdata.put("NOTICE_CONTENT", content);
			    smsdata.put("REMARK", "跨省家庭网(全国)主号欠费停机");
			    smsdata.put("FORCE_OBJECT", "10086");
			    log.debug("--stopMachineSendSms-smsdata="+smsdata);
			    SmsSend.insSms(smsdata,RouteInfoQry.getEparchyCodeBySn(serialNum));
    		    log.debug("--stopMachineSendSms-smsdata2="+smsdata);
    		}
    	}

    }
    
    
    /**
     * 主号删除副号和副号主动退出区分
     * @throws Exception
     */
    private void dealQuitFamilySendSms(IData mainTrade,IDataset relaUUDatas) throws Exception {
    	String custPhone = mainTrade.getString("RSRV_STR3","");
    	IData in = new DataMap();
    	in.put("CUSTOMER_PHONE", custPhone);
    	in.put("MEM_NUMBER", relaUUDatas.getData(0).getString("SERIAL_NUMBER_B",""));
    	in.put("ACTION", "51");
    	in.put("PRODUCT_OFFERING_ID", relaUUDatas.getData(0).getString("RSRV_STR2",""));
    	IDataset familyLogInfo = getFamilyLogInfo(in);
    	String flag = "";
    	if(IDataUtil.isNotEmpty(familyLogInfo)){
    		flag = familyLogInfo.getData(0).getString("RSRV_STR1");
    	}
		//副号删除
		delFamilySendSms(mainTrade,relaUUDatas);	
    }
    
    /**
     * 查询TF_B_FAMILY_LOG的RSRV_STR1 来区分： ZD 主号删除副号，BD:副号删除副号
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyLogInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        IData inparam =new DataMap();
		inparam.put("CUSTOMER_PHONE",param.get("CUSTOMER_PHONE") );
		inparam.put("ACTION",param.get("ACTION") );
		inparam.put("MEM_NUMBER",param.get("MEM_NUMBER") );
		inparam.put("PRODUCT_OFFERING_ID",param.get("PRODUCT_OFFERING_ID") );
        parser.addSQL(" SELECT a.PRODUCT_OFFERING_ID,a.CUSTOMER_PHONE,a.CUSTOMER_TYPE,a.ACTION,a.PO_ORDER_NUMBER, ");
        parser.addSQL(" a.PRODUCT_CODE,a.ORDER_TYPE,a.SEQ_ID,a.PARTITION_ID,a.ORDER_SOURCE_ID,a.COMPANY_ID,");
        parser.addSQL(" a.MEM_NUMBER,a.RSRV_STR1,a.RSRV_STR2,a.RSRV_STR3,a.RSRV_STR4,a.RSRV_STR5");
        parser.addSQL(" FROM TF_B_FAMILY_LOG a WHERE 1=1");
        parser.addSQL(" AND a.CUSTOMER_PHONE = :CUSTOMER_PHONE");
        parser.addSQL(" AND a.ACTION = :ACTION");
        parser.addSQL(" AND a.MEM_NUMBER = :MEM_NUMBER");
        parser.addSQL(" AND a.PRODUCT_OFFERING_ID = :PRODUCT_OFFERING_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    
    private void openFamilySendSms(IData mainTrade,IDataset relaUUDatas) throws Exception {
    	String custPhone = mainTrade.getString("RSRV_STR3","");
    	String productOfferingID = relaUUDatas.getData(0).getString("RSRV_STR2","");
		 String productCode = mainTrade.getString("RSRV_STR1","");
    	IData param = new DataMap();
		String poidCode = MfcCommonUtil.getPoidInfoAndLable(custPhone,productOfferingID,null).getString("POID_CODE");
    	param.put("PRODUCT_OFFERING_ID",poidCode);

    	if (IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(custPhone))) {
    		IData sendInfo = new DataMap();
    		sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(custPhone));
    		sendInfo.put("RECV_OBJECT", custPhone);
    		sendInfo.put("RECV_ID", custPhone);
    		sendInfo.put("SMS_PRIORITY", "50");
    		sendInfo.put("NOTICE_CONTENT", getSmsInfo(1,param,productCode));
    		sendInfo.put("REMARK", "开通全国亲情网");
    		if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){
				sendInfo.put("REMARK", "开通5G家庭会员群组");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){
				sendInfo.put("REMARK", "开通5G家庭套餐群组");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){
				sendInfo.put("REMARK", "开通5G融合套餐群组");
			}
    		sendInfo.put("FORCE_OBJECT", "10086");
    		SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
    	}
	}





	/**3
     * 删除成员
     * @param relaUUDatas
     * @throws Exception
     */
    public void delFamilySendSms(IData mainTrade,IDataset relaUUDatas) throws Exception {

    	String userIdA = relaUUDatas.getData(0).getString("USER_ID_A");
    	String productOfferingId = relaUUDatas.getData(0).getString("RSRV_STR2","");
    	IData param = new DataMap();
    	String custPhone = mainTrade.getString("RSRV_STR3","");
		String productCode = mainTrade.getString("RSRV_STR1","");
		String poidCode = getPoidInfoAndLable(custPhone,productOfferingId).getString("POID_CODE");
		param.put("PRODUCT_OFFERING_ID",poidCode);
    	String snInfos = "";
    	//被删减副号短信
    	param.put("num", getMenbers(relaUUDatas,"DEL"));
    	for(int i = 0; i < relaUUDatas.size();i++){
    		String memNumber="";
    		if("1".equals(relaUUDatas.getData(i).getString("RSRV_STR1","")) && "2".equals(relaUUDatas.getData(i).getString("ROLE_CODE_B",""))){
    			//本省副号
    			memNumber = relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","");
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memNumber));
    			sendInfo.put("RECV_OBJECT", memNumber);
    			sendInfo.put("RECV_ID", memNumber);
    			sendInfo.put("SMS_PRIORITY", "50");
    			param.put("MFC_CUST_PHONE", custPhone);
//    			sendInfo.put("NOTICE_CONTENT", "ZD".equals(flag)?getSmsInfo(5,param):getSmsInfo(10,param));
    			sendInfo.put("NOTICE_CONTENT", getSmsInfo(5,param,productCode));
    			sendInfo.put("REMARK", "被删减副号短信");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memNumber));
    		}
    		//所有副号，本省加外省
    		if("2".equals(relaUUDatas.getData(i).getString("ROLE_CODE_B",""))){
    			snInfos = relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","");
    			if(relaUUDatas.size()>1){
    				snInfos = snInfos+","+memNumber;
    			}
    		}
    	}
    	//其他副号短信
    	IDataset relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , "2","MF",null);
    	if(DataUtils.isNotEmpty(relationAll)){
    		for(int i = 0; i < relationAll.size(); i++){
    		 if("1".equals(relationAll.getData(i).getString("RSRV_STR1","")) && !snInfos.contains(relationAll.getData(i).getString("SERIAL_NUMBER_B",""))){
    			String memNumber = relationAll.getData(i).getString("SERIAL_NUMBER_B","");
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memNumber));
    			sendInfo.put("RECV_OBJECT", memNumber);
    			sendInfo.put("RECV_ID", memNumber);
    			sendInfo.put("SMS_PRIORITY", "50");
    			param.put("MFC_MEM_PHONES", snInfos);
    			param.put("MFC_CUST_PHONE", custPhone);

    			sendInfo.put("NOTICE_CONTENT", getSmsInfo(6,param,productCode));
    			sendInfo.put("REMARK", "删除的其他副号短信");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memNumber));
    		 }
    		}
    	}
    	
    	//主号短信
    	if (IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(custPhone))) {
			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(custPhone));
			sendInfo.put("RECV_OBJECT", custPhone);
			sendInfo.put("RECV_ID", custPhone);
			sendInfo.put("SMS_PRIORITY", "50");
			param.put("MFC_MEM_PHONES", snInfos);
			param.put("PHONE_NUM", getMenbers(relaUUDatas,"DEL"));
			sendInfo.put("NOTICE_CONTENT", getSmsInfo(8,param,productCode));
			sendInfo.put("REMARK", "全国亲情网删除成员");
			sendInfo.put("FORCE_OBJECT", "10086");
			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
		}
	}

	public void addFamilySendSms(IData mainTrade,IDataset relaUUDatas) throws Exception {
    	String userIdA = relaUUDatas.getData(0).getString("USER_ID_A");
    	String custPhone = mainTrade.getString("RSRV_STR3","");
    	String productOfferingId = relaUUDatas.getData(0).getString("RSRV_STR2");
		String productCode = mainTrade.getString("RSRV_STR1","");
    	IData param = new DataMap();
    	String poidCode = MfcCommonUtil.getPoidInfoAndLable(custPhone,productOfferingId,null).getString("POID_CODE");
		param.put("PRODUCT_OFFERING_ID",poidCode);

    	String snInfos = "";
    //	String custPhone = mainTrade.getString("RSRV_STR3","");
    	//被添加副号短信
    	for(int i = 0; i < relaUUDatas.size();i++){//ROLE_CODE_B
    		if("1".equals(relaUUDatas.getData(i).getString("RSRV_STR1","")) && "2".equals(relaUUDatas.getData(i).getString("ROLE_CODE_B",""))){
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","")));
    			sendInfo.put("RECV_OBJECT", relaUUDatas.getData(i).getString("SERIAL_NUMBER_B",""));
    			sendInfo.put("RECV_ID", relaUUDatas.getData(i).getString("SERIAL_NUMBER_B",""));
    			sendInfo.put("SMS_PRIORITY", "50");
    			
    			param.put("MFC_CUST_PHONE", custPhone);
    			param.put("PHONE_NUM", getMenbers(relaUUDatas,"ADD"));
    			sendInfo.put("NOTICE_CONTENT", getSmsInfo(3,param,productCode));
    			sendInfo.put("REMARK", "被添加副号短信");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","")));
    			snInfos = relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","")+",";
    		}else{
    			snInfos = relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","")+",";
    		}
    	}
    	//其他副号短信
    	IDataset relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , "2","MF",null);
    	if(DataUtils.isNotEmpty(relationAll)){
    		for(int i = 0; i < relationAll.size(); i++){
    		 if("1".equals(relationAll.getData(i).getString("RSRV_STR1","")) && !snInfos.contains(relationAll.getData(i).getString("SERIAL_NUMBER_B",""))){
    			String memNumber = relationAll.getData(i).getString("SERIAL_NUMBER_B","");
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memNumber));
    			sendInfo.put("RECV_OBJECT", memNumber);
    			sendInfo.put("RECV_ID", memNumber);
    			sendInfo.put("SMS_PRIORITY", "50");
    			param.put("MFC_MEM_PHONES", snInfos.substring(0, snInfos.length()-1));
    			param.put("MFC_CUST_PHONE", custPhone);
    			//param.put("snInfo", snInfos);
    			sendInfo.put("NOTICE_CONTENT", getSmsInfo(4,param,productCode));
    			sendInfo.put("REMARK", "添加其他副号短信");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memNumber));
    		 }
    		}
    	}
    	//主号短信
    	if (IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(custPhone))) {
			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(custPhone));
			sendInfo.put("RECV_OBJECT", custPhone);
			sendInfo.put("RECV_ID", custPhone);
			sendInfo.put("SMS_PRIORITY", "50");
			param.put("MFC_MEM_PHONES", snInfos.substring(0, snInfos.length()-1));
			param.put("PHONE_NUM", getMenbers(relaUUDatas,"ADD"));
			sendInfo.put("NOTICE_CONTENT", getSmsInfo(7,param,productCode));
			sendInfo.put("REMARK", "全国亲情网新增成员");

			if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){
				sendInfo.put("REMARK", "5G家庭会员群组新增成员");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){
				sendInfo.put("REMARK", "5G家庭套餐群组新增成员");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){
				sendInfo.put("REMARK", "5G融合套餐群组新增成员");
			}
			sendInfo.put("FORCE_OBJECT", "10086");
			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
		}
    	
    }

	private String getMenbers(IDataset relaUUDatas,String operType) throws Exception {
		
		IDataset res = MfcCommonUtil.getSEL_USER_ROLEA(relaUUDatas.getData(0).getString("USER_ID_A",""),"2","MF",null);
		if (log.isDebugEnabled())
        {
            log.debug("==========SendSmsFinishAction===res=============="+res);
            log.debug("==========SendSmsFinishAction===relaUUDatas=============="+relaUUDatas);
        }
		String num ="";
		if("ADD".equals(operType)){
			 num = String.valueOf(res.size()+1);
		}else if("DEL".equals(operType)){
			 num = String.valueOf(res.size()+1);
		}
		return num;
	}
	/**
     * 取消全国亲情网 
     * @param mainTrade
     * @param relaUUDatas
     * @throws Exception 
     */
    public void cancelFamilySendSms(IData mainTrade,IDataset relaUUDatas) throws Exception {
    	String custPhone = mainTrade.getString("RSRV_STR3","");
    	String productCode = mainTrade.getString("RSRV_STR1","");
    	String productOfferingId = relaUUDatas.getData(0).getString("RSRV_STR2","");
		IData iData = new DataMap();
		//注销 归档时会更新Other表，所以 查finishTag为2的
		String poidCode = getPoidInfoAndLable(custPhone,productOfferingId).getString("POID_CODE");
		iData.put("PRODUCT_OFFERING_ID",poidCode);
		iData.put("MFC_CUST_PHONE",custPhone);

    	if(DataUtils.isNotEmpty(relaUUDatas)){
    		for(int i = 0; i < relaUUDatas.size(); i++){
    			if("1".equals(relaUUDatas.getData(i).getString("RSRV_STR1","")) && "2".equals(relaUUDatas.getData(i).getString("ROLE_CODE_B",""))){
    				String memNumber = relaUUDatas.getData(i).getString("SERIAL_NUMBER_B","");
    				IData sendInfo = new DataMap();
        			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memNumber));
        			sendInfo.put("RECV_OBJECT", memNumber);
        			sendInfo.put("RECV_ID", memNumber);
        			sendInfo.put("SMS_PRIORITY", "50");
        			sendInfo.put("NOTICE_CONTENT", getSmsInfo(2,iData,productCode));
        			sendInfo.put("REMARK", "取消全国亲情网");
        			sendInfo.put("FORCE_OBJECT", "10086");
        			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memNumber));
    			}
    			
    		}
    	}
    	if (IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(custPhone))) {
    		IData sendInfo = new DataMap();
    		sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(custPhone));
    		sendInfo.put("RECV_OBJECT", custPhone);
    		sendInfo.put("RECV_ID", custPhone);
    		sendInfo.put("SMS_PRIORITY", "50");
    		sendInfo.put("NOTICE_CONTENT", getSmsInfo(9, iData,productCode));
    		sendInfo.put("REMARK", "取消全国亲情网");
    		sendInfo.put("FORCE_OBJECT", "10086");
    		SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
    	}
    	
	}
    
    /**
     * 
     * @param operType  操作类型
     * @return
     * @throws Exception
     */
	public static String getSmsInfo(int operType,IData param ,String productCode) throws Exception{
		String res ="";
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_SMS", "ZZZZ");

		IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_5GSMS", "ZZZZ");
		
		IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_ZFBSMS", "ZZZZ");
		
		IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_YWSMS", "ZZZZ");

		IData iData = new DataMap();
		String templateId = "";
		switch (operType) {
		case 1:
			 // 主号成功办理组网
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE7","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE8","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE23","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE23","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
   	case 2:
			// 主号取消全国亲情网 成员号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 3:
			//主号成功添加成员号-被添加副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 4:
			//主号成功添加成员号-其他副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 5:
			// 主号删减成员号或成员号主动退出 被删减副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE21","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 6:
			// 主号删减成员号或成员号主动退出 其他副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE23","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 7:
			// 主号成功添加成员号 主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE25","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 8:
			// 主号删减成员号或成员号主动退出 主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE19","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE20","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE25","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 9:
			// 取消全国亲情网主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE9","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		default:
			break;
		}
		return res;

	}

	public static IData getPoidInfoAndLable(String custPhone ,String PRODUCT_OFFERING_ID)throws Exception{
		IData output = new DataMap();

		String poidCode = "";
		if(StringUtils.isNotBlank(PRODUCT_OFFERING_ID)){
			poidCode =  PRODUCT_OFFERING_ID.substring(16);//默认 PRODUCT_OFFERING_ID 后两位
			output.put("POID_CODE",poidCode);
			output.put("POID_LABLE","群"+poidCode);
		}

		IDataset qryOtherinfo = MfcCommonUtil.qryMemberinfo(PRODUCT_OFFERING_ID,custPhone);
		if (IDataUtil.isNotEmpty(qryOtherinfo)){
			if(StringUtils.isNotBlank(qryOtherinfo.getData(0).getString("POID_CODE"))){
				output.put("POID_CODE",qryOtherinfo.getData(0).getString("POID_CODE"));
			}
			if(StringUtils.isNotBlank(qryOtherinfo.getData(0).getString("POID_LABLE"))){
				output.put("POID_LABLE",qryOtherinfo.getData(0).getString("POID_LABLE"));
			}
		}
		return  output;
	}

}
