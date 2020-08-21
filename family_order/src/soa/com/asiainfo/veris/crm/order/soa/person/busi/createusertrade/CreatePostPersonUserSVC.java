
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.ExchangeCradStateEnum;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserWhitCardChoiceSnInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatOrderBean;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal.SelfTerminalBean;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class CreatePostPersonUserSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger(CreatePostPersonUserSVC.class);

    /**
     * 网厅下单后调用，记录邮寄号码信息
     *
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData loadPostCardInfo(IData data) throws Exception {
		log.info("CreatePostPersonUserSVCxxxxxxxxxxxxxxxxx49 "+data);
		IData returnData = new DataMap();
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "CUST_NAME");
		IDataUtil.chkParam(data, "PSPT_ID");
		IDataUtil.chkParam(data, "PSPT_ADDR");
//		IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(data, "POST_PHONE");
		IDataUtil.chkParam(data, "POST_ADDR");
		IDataUtil.chkParam(data, "IN_MODE"); 	//1网厅，2，移动商城
		IDataUtil.chkParam(data, "ORDER_NO"); //唯一序列
		String state = "0";
		String agentFlag = data.getString("AGENT_FLAG");
		if(StringUtils.isNotBlank(agentFlag) && "1".equals(agentFlag)){
			state = "1";
		}
		
		//REQ201906110023 019年线上销售吉祥号码活动开发需求
		data.put("RSRT_STR4", data.getString("BEAUTIFUL_TAG"));//BEAUTIFUL_TAG  : 0 非吉祥号码  1 吉祥号码
		data.put("RSRT_STR3", data.getString("BEAUTIFUL_FEE"));//BEAUTIFUL_FEE : 预存款金额
		//
				
		//REQ202005110005线上号卡增加出入境证件办理电话入网手续 add by wuwangfeng
		String  psptTypeCode=data.getString("PSPT_TYPE_CODE","");
		if(StringUtils.isEmpty(psptTypeCode)){
			data.put("PSPT_TYPE_CODE", "0");
		}else{
			IDataset psptTypeCodeTrans=CommparaInfoQry.getCommparaAllCol("CSM", "2555", psptTypeCode, "ZZZZ");
			if(IDataUtil.isNotEmpty(psptTypeCodeTrans)){
				String idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
				data.put("PSPT_TYPE_CODE", idCardType);
			}else{
				data.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE", "0"));
			}
		}		
		
		data.put("STATE", state);
		data.put("SEND_SMS_FLAG", "0");
		data.put("CANCEL_FLAG", "0");
		data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		data.put("UPDATA_TIME", SysDateMgr.getSysTime());
		data.put("END_DATE", SysDateMgr.addDays(30)+" 23:59:59");
		log.error("CreatePostPersonUserSVC.java60xxxxxxxxx "+ this.getVisit().getStaffId());
		log.error("CreatePostPersonUserSVC.java61xxxxxxxxx "+ this.getVisit().getDepartId());
		data.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
		data.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
        CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);

        if(IDataUtil.isNotEmpty(postinfo))
		{
			IData postdata=  postinfo.getData(0);

			  if("0".equals(postdata.getString("STATE")) || "1".equals(postdata.getString("STATE")) )
				 {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("SERIAL_NUMBER") + "号码已经下单！");
				 }
		}
       
        if(data.getString("SALECHANNLE","").trim().length()==0){
        	 data.put("SALECHANNLE", "");
        }
        if(data.getString("PICNAMERPATH","").trim().length()==0){
       	 data.put("PICNAMERPATH", "");
        }
        
		
		Dao.insert("TD_B_POSTCARD_INFO", data, Route.getCrmDefaultDb());
		returnData.put("BIZ_CODE", "0000");
		returnData.put("BIZ_DESC", "下单成功");
	    return returnData;
	}
	   /**
     * 号码信息查询
     *
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData queryNumberState(IData data) throws Exception {
		IData returnData = new DataMap();
		IDataUtil.chkParam(data, "SERIAL_NUMBER");

        CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);

        if(IDataUtil.isNotEmpty(postinfo))
          {
        	  returnData=  postinfo.getData(0);
          }

	    return returnData;
	}
	   /**
     * 号码状态修改
     *
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public void updateNumberState(IData data) throws Exception {

        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "STATE");
        IDataUtil.chkParam(data, "OLD_STATE");
        CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        CreatePostPersonUserBean.updatePostCardInfo(data);

	}
	
	/**
     * 审核结果通知
     *
     * @param 
     * @param 
     * @return
     * @throws Exception
     */
	public IData updateAuditResult(IData data) throws Exception {

        IDataUtil.chkParam(data, "SERIALNUMBER");
        IDataUtil.chkParam(data, "CUSTCERTNO");
        CreatePostPersonUserBean createPostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        int num = 0 ;
		if("44".equals(data.getString("BUSITYPE"))){
		
			num = createPostPersonUserBean.updateExchangeCardResult(data);
		}else {
			num = 	createPostPersonUserBean.updateAuditResult(data);
		}



        IData returnData = new DataMap();
        if(num>0){
            returnData.put("BIZ_CODE", "0000");
            returnData.put("BIZ_DESC", "操作成功");
        }else{
            returnData.put("BIZ_CODE", "2999");
            returnData.put("BIZ_DESC", "未找到对应的记录");
        }
        
        return returnData;
	}
	
	/**
     * 号码sim校验
     *
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData checkNumber(IData data) throws Exception {
		IData returnData = new DataMap();

		log.debug("checkNumber params:"+data.toString());
		//政企卡激活接口规范-V1.0 add by tanzheng 20191121

			//21，线上售卡激活
			//29:固话认证激活
			//42：政企卡激活
			//44:补换卡认证激活
		if("42".equals(data.getString("BUSI_TYPE"))){
			return checkUnitOpenNumber(data);
		}
		if("44".equals(data.getString("BUSI_TYPE"))){
			return checkExchangeSimCardNumber(data);
		}

		try {
				IDataUtil.chkParam(data, "SERIAL_NUMBER");
				String iccid = IDataUtil.chkParam(data, "SIM_CARD_NO");
				IDataUtil.chkParam(data, "TRANS_ID");
				returnData.put("TRANS_ID", data.getString("TRANS_ID"));

				CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
				IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);  //邮寄号码信息表

				if(IDataUtil.isNotEmpty(postinfo))
				{
					IData postdata=  postinfo.getData(0);
					 if("1".equals(postdata.getString("STATE")))
					 {
		//	        		if(data.getString("PSPT_ID").equals(returnData.getString("PSPT_ID")))
		//	        		{
							returnData.put("BIZ_CODE", "0000");
							returnData.put("BIZ_DESC", "校验成功");
		//	        		}
		//	        		else
		//	        		{
		//	        			returnData.put("BIZ_CODE", "0003");
		//	    		    	returnData.put("BIZ_DESC", "号码与身份信息不一致");
		//
		//	        		}

							UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

							//String imsi = "";
							String simNo = "";
							IDataset userRes = UserResInfoQry.getUserResInfoByUserIdRestype(uca.getUserId(), "-1", "1");
							if (IDataUtil.isNotEmpty(userRes))
							{
								//imsi = userRes.getData(0).getString("IMSI");
								simNo = userRes.getData(0).getString("RES_CODE");
							}

						   /* if(!simNo.equals(data.getString("SIM_CARD_NO")))
							{
								returnData.put("BIZ_CODE", "2999");
								returnData.put("BIZ_DESC", "号码与SIM卡不一致");
							}*/

							/**
							 * REQ201812040020
							 * 实名认证激活ICCID卡号支持6或20位输入需求
							 *
							 */
						  //输入为6为时，需获得simNo后六位进行比较，输入为20位时直接比较
							String simCardNo = data.getString("SIM_CARD_NO");
							if(simCardNo.length() == 6){
								simNo = simNo.substring(simNo.length()-6,simNo.length());
							}
							if(!simNo.equals(data.getString("SIM_CARD_NO"))){
								returnData.put("BIZ_CODE", "2999");
								returnData.put("BIZ_DESC", "号码与SIM卡不一致");
							};
					 /******************************结束***************************************/

							IDataset resds  = new DatasetList();
							IData resinfo = new DataMap();

							resinfo.put("PSPT_ID", postdata.getString("PSPT_ID"));
							resinfo.put("CUST_NAME", postdata.getString("CUST_NAME"));
							resinfo.put("BUSI_CATEGORY","1");
							resinfo.put("ORDER_NO",postdata.getString("ORDER_NO"));
							resinfo.put("ORDER_CREATE",postdata.getString("ACCEPT_DATE"));
							resinfo.put("PRESTATION_CREATE",postdata.getString("END_DATE"));

							resinfo.put("CHECKTYPE","0");
							resinfo.put("PICNAMERPATH",postdata.getString("PICNAMERPATH"));
							resinfo.put("SALECHANNLE",postdata.getString("SALECHANNLE"));
							
							// REQ202005110005线上号卡增加出入境证件办理电话入网手续 add by wuwangfeng
							String  psptTypeCode=postdata.getString("PSPT_TYPE_CODE","");
							if(StringUtils.isNotEmpty(psptTypeCode)){
								IDataset psptTypeCodeTrans=CommparaInfoQry.getCommparaAllColRevert("CSM", "2555", psptTypeCode, "ZZZZ");
						        if(IDataUtil.isNotEmpty(psptTypeCodeTrans)){
						        	String idCardType = psptTypeCodeTrans.getData(0).getString("PARAM_CODE", "");
						        	resinfo.put("CERT_TYPE", idCardType);
						        }
				    		}					        

							resds.add(resinfo);
							returnData.put("RES_INFO", resds);
					 }
					 else {
						returnData.put("BIZ_CODE", "2999");
						returnData.put("BIZ_DESC", "号码不是待激活状态");

					  }
				}
				else
				{

					/**
					 * 白卡换号流程   tf_f_sel_whitecard_flow
					 * 查询该号码是否具有白卡换号流程，如果是，走白卡换号流程；
					 *  校验 iccid 号码，并返回 临时号码
					 * 2018-08-07  zhengkai5
					 * */
					IData whiteCardInfo = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySn(data.getString("SERIAL_NUMBER"));
					if (IDataUtil.isNotEmpty(whiteCardInfo))
					{

						//如果已超时也可进行实名认证
						//2.短信超时校验
						String startDate = whiteCardInfo.getString("UPDATE_TIME");

						//短信超时结束时间  偏移两小时
						String endDate = SysDateMgr.getAddHoursDate(startDate,2);

						long time = SysDateMgr.hoursBetween(endDate,SysDateMgr.getSysTime());

						//流程节点控制
						if(!"A".equals(whiteCardInfo.getString("STATE")) && !"X".equals(whiteCardInfo.getString("STATE")) && time<=0 )
						{
							/*
							* 短信超时校验
							* 如果用户实名制认证成功后，回复短信超时，需要重新进行实名制认证，
							* 所以短信超时时，跳过该流程节点控制
							* */
							returnData.put("BIZ_CODE", "2999");
							returnData.put("BIZ_DESC", "白卡换号流程节点不在该状态! ");
							return returnData;
						}


						//调用资源 校验 iccid 是否合法
						IDataset resIccInfo =  ResCall.checkICCId(iccid);

						if (IDataUtil.isEmpty(resIccInfo))
						{
							returnData.put("BIZ_CODE", "2999");
							returnData.put("BIZ_DESC", "资源找不到该卡号信息! ");
							return returnData;
						}

						String serialNumberTemp = resIccInfo.first().getString("ACCESS_NUMBER");
						whiteCardInfo.put("SERIAL_NUMBER_TEMP",serialNumberTemp);
						whiteCardInfo.put("SIM_CARD_NO_TEMP",iccid);

						//预存  临时号码信息 至 日志表
						UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);

						IDataset resds  = new DatasetList();
						IData resinfo = new DataMap();

						resinfo.put("PSPT_ID", whiteCardInfo.getString("PSPT_ID"));
						resinfo.put("CUST_NAME", whiteCardInfo.getString("CUST_NAME"));
						resinfo.put("BUSI_CATEGORY","1");
						resinfo.put("ORDER_NO","BKKH"+SeqMgr.getTradeId());
						resinfo.put("ORDER_CREATE",whiteCardInfo.getString("UPDATE_TIME"));
						resinfo.put("PRESTATION_CREATE",whiteCardInfo.getString("END_DATE"));
						resds.add(resinfo);
						returnData.put("RES_INFO", resds);

						returnData.put("BIZ_CODE", "0000");
						returnData.put("BIZ_DESC", "校验成功");

						return returnData;
					}else{
						//进行信用购机号码验证
						IDataset ordersInfos=SelfTerminalBean.queryPreOrderInfoBySn(data.getString("SERIAL_NUMBER"));
						if (IDataUtil.isNotEmpty(ordersInfos)){
							returnData.put("BIZ_CODE", "0000");
							returnData.put("BIZ_DESC", "校验成功");
							IData orderInfo=ordersInfos.getData(0);
							if("1".equals(orderInfo.getString("STATE"))
									&&("0".equals(orderInfo.getString("EXEC_STATE"))||"4".equals(orderInfo.getString("EXEC_STATE")))){

								String simCardNo = data.getString("SIM_CARD_NO");
								String simNo=orderInfo.getString("SIM_CARD_NO");
								String emptyCardId=orderInfo.getString("RSRV_STR7");

								//输入为6为时，需获得simNo后六位进行比较，输入为20位时直接比较
								if(StringUtils.isNotEmpty(simCardNo)){
									boolean isPass=false;
									if(simCardNo.length() == 6){
										simNo = simNo.substring(simNo.length()-6,simNo.length());
									}

									if(data.getString("SIM_CARD_NO").equals(simNo)){
										isPass=true;
									}
									if(!isPass){
										if(data.getString("SIM_CARD_NO").equals(emptyCardId)){
											isPass=true;
										}
									}

									if(!isPass){
										returnData.put("BIZ_CODE", "2999");
										returnData.put("BIZ_DESC", "号码与SIM卡不一致");
									}else{
										IDataset subOrders=AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderInfo.getString("ORDER_ID"), orderInfo.getString("SUBORDER_ID"));

										IDataset resds  = new DatasetList();
										IData resinfo = new DataMap();

										resinfo.put("PSPT_ID", subOrders.first().getString("CERTIFICATE_NO"));
										resinfo.put("CUST_NAME", subOrders.first().getString("LEGAL_NAME"));
										resinfo.put("BUSI_CATEGORY","1");
										resinfo.put("ORDER_NO",subOrders.first().getString("ORDER_ID"));
										resinfo.put("ORDER_CREATE",subOrders.first().getString("CREATE_TIME","").replace(".0", ""));
										resinfo.put("PRESTATION_CREATE","");

										resinfo.put("CHECKTYPE","0");
										resinfo.put("PICNAMERPATH",subOrders.first().getString("PIC_NAME_R_PATH"));
										resinfo.put("SALECHANNLE","");

										resds.add(resinfo);
										returnData.put("RES_INFO", resds);
									}
								}
							}else{
								returnData.put("BIZ_CODE", "2999");
								returnData.put("BIZ_DESC", "该号码订单非预开户成功状态!");
							}
							return returnData;
						}

					}

			returnData.put("BIZ_CODE", "2999");
			returnData.put("BIZ_DESC", "该号码无有效的选号开户订单!");

		}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			returnData.put("TRANS_ID", data.getString("TRANS_ID"));
			returnData.put("BIZ_CODE", "2999");
			returnData.put("BIZ_DESC", e.getMessage());
		}
		return returnData;
	}

	private IData checkExchangeSimCardNumber(IData data) {
		IData resultMap = new DataMap();
		resultMap.put("BIZ_CODE", "0000");
		resultMap.put("BIZ_DESC", "校验成功");

		resultMap.put("TRANS_ID", data.getString("TRANS_ID"));

		if(log.isDebugEnabled()){
			log.debug("补换卡认证激活参数:" + data.toString());
		}
		try
		{
			checkExchangeParam(data,resultMap);
			if(!"0000".equals(resultMap.getString("BIZ_CODE"))){
				return resultMap;
			}

			
			IData resInfo = new DataMap();
			//custCertNo
			//custName
			UcaData ucaData = UcaDataFactory.getNormalUca(IDataUtil.chkParam(data,"SERIAL_NUMBER"));
			CustPersonTradeData personData = ucaData.getCustPerson();


			String useName = ucaData.getCustomer().getCustName();
			String usePsptId = ucaData.getCustomer().getPsptId();
			resInfo.put("PSPT_ID",usePsptId);
			resInfo.put("CUST_NAME",useName);
			resInfo.put("PICNAMERPATH",resultMap.getString("PICNAMERPATH"));
			resInfo.put("BUSI_CATEGORY","1");
			resInfo.put("CHECKTYPE","0");
			resInfo.put("ORDER_CREATE",resultMap.getString("CREATE_TIME"));
			resInfo.put("SALECHANNLE","");
			IDataset resds  = new DatasetList();
			resds.add(resInfo);
			resultMap.put("BUSI_TYPE","44");
			resultMap.put("RES_INFO",resds);

		}catch (Exception e) {
			log.error("补换卡认证激活异常参数:"+data.toString());
			log.error(e.getMessage(),e);
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", e.getMessage());
		}


		return resultMap;
	}

	private IData checkUnitOpenNumber(IData data) {
		IData resultMap = new DataMap();
		resultMap.put("BIZ_CODE", "0000");
		resultMap.put("BIZ_DESC", "校验成功");

		resultMap.put("TRANS_ID", data.getString("TRANS_ID"));

		String serialNumber = data.getString("SERIAL_NUMBER");
		String simCardNo = data.getString("SIM_CARD_NO");

		try {
			UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
			IDataset resInfos = UserResInfoQry.queryUserSimInfo(ucaData.getUserId(),"1");
			if(IDataUtil.isEmpty(resInfos)){
				resultMap.put("BIZ_CODE", "2999");
				resultMap.put("BIZ_DESC", "无SIM卡信息！");
				return resultMap;
			}else{
				String localImsi = resInfos.getData(0).getString("RES_CODE");
				String emptyCardId = resInfos.getData(0).getString("RSRV_STR5");//白卡卡号
				if(simCardNo.length()==6){
					localImsi= localImsi.substring(localImsi.length()-6,localImsi.length());
					if(StringUtils.isNotBlank(emptyCardId)){
						emptyCardId = emptyCardId.substring(emptyCardId.length()-6,emptyCardId.length());
					}
				}

				if(!simCardNo.equals(localImsi)&&!simCardNo.equals(emptyCardId)){
					resultMap.put("BIZ_CODE", "2999");
					resultMap.put("BIZ_DESC", "SIM卡不一致！");
					return resultMap;
				}
			}
			if("0".equals(ucaData.getUser().getAcctTag())){
				resultMap.put("BIZ_CODE", "2999");
				resultMap.put("BIZ_DESC", "号码已激活！");
				return resultMap;
			}
			String psptTypeCode = ucaData.getCustomer().getPsptTypeCode();
			if(!("E".equals(psptTypeCode)
					||"M".equals(psptTypeCode)
					||"G".equals(psptTypeCode)
					||"L".equals(psptTypeCode)
					||"D".equals(psptTypeCode))){
				resultMap.put("BIZ_CODE", "2999");
				resultMap.put("BIZ_DESC", "号码不是单位证件开户的！");
				return resultMap;
			}

			//校验通过后需要返回使用人信息并加密
			IData resInfo = new DataMap();
			//custCertNo
			//custName
			CustPersonTradeData personData = ucaData.getCustPerson();


			String useName = personData.getRsrvStr5();
			String usePsptId = personData.getRsrvStr7();
			resInfo.put("PSPT_ID",usePsptId);
			resInfo.put("CUST_NAME",useName);
			IDataset resds  = new DatasetList();
			resds.add(resInfo);
			resultMap.put("BUSI_TYPE","42");
			resultMap.put("RES_INFO",resds);

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", e.getMessage());
		}
		return resultMap;
	}


    /**
     * 微信平台激活接口
     * @param SERIAL_NUMBER,CUST_NAME,PSPT_ID..
     *
     * 白卡开户流程
     *     用户请求校验接口
     * @param SERIAL_NUMBER
     * @return
     * @throws Exception
     */
	public IData opencard(IData data) throws Exception {
        //SS.CreatePostPersonUserSVC.opencard
        //SERIAL_NUMBER=13907622102,CUST_NAME=冼乃捷,PSPT_ID=460102198804061213,PSPT_TYPE_CODE=0,PSPT_END_DATE=2036-03-04,PSPT_ADDR=海口市美兰区五指山路22号,SEX=0
		//政企卡激活接口规范-V1.0 add by tanzheng 20191121

			//21，线上售卡激活
			//29:固话认证激活
			//42：政企卡激活
			//44：补换卡认证激活
		if("42".equals(data.getString("BUSI_TYPE"))){
			return unitOpenActive(data);
		}
		if("44".equals(data.getString("BUSI_TYPE"))){
			return exchangeCardActive(data);
		}

		IData returnData = new DataMap();

        /**
         * 查询白卡换号流程
         * 如果该号码存在白卡换号，即走白卡换号流程
         * 2018-8-7   zhengkai5
         * */
        IData whiteCardInfo = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(whiteCardInfo))
        {
            data.put("SERIAL_NUMBER_TEMP",whiteCardInfo.getString("SERIAL_NUMBER_TEMP"));

            //data.put("ICCID",whiteCardInfo.getString("SIM_CARD_NO_TEMP"));

            IDataset results = CSAppCall.call("SS.WhiteCardChoiceSnSVC.checkUserRequest", data);

            returnData.put("BIZ_CODE", "0000");
            returnData.put("BIZ_DESC", "申请成功！");

            if (IDataUtil.isNotEmpty(results))
            {
                if (!"0".equals(results.first().getString("X_RESULTCODE")))
                {
                    returnData.put("BIZ_CODE", "2999");
                    returnData.put("BIZ_DESC", results.first().getString("X_RESULTINFO"));
                }
            }else
            {
                returnData.put("BIZ_CODE", "2999");
                returnData.put("BIZ_DESC", "用户申请失败！");
            }
            return returnData;
        }


        CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);
        if (IDataUtil.isNotEmpty(postinfo)) {

            IDataUtil.chkParam(data, "SERIAL_NUMBER");
            IDataUtil.chkParam(data, "CUST_NAME");
            IDataUtil.chkParam(data, "PSPT_ID");
            //data.put("PSPT_TYPE_CODE", "0");
            //          IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
            IDataUtil.chkParam(data, "PSPT_END_DATE");
            if("00".equals(data.getString("PSPT_TYPE_CODE","00"))){
            	IDataUtil.chkParam(data, "PSPT_ADDR");
            }
            IDataUtil.chkParam(data, "SEX");
            
            //REQ202005110005线上号卡增加出入境证件办理电话入网手续 add by wuwangfeng
    		String  psptTypeCode=data.getString("PSPT_TYPE_CODE","");
    		if(StringUtils.isEmpty(psptTypeCode)){
    			data.put("PSPT_TYPE_CODE", "0");
    		}else{
    			IDataset psptTypeCodeTrans=CommparaInfoQry.getCommparaAllCol("CSM", "2555", psptTypeCode, "ZZZZ");
	    		if(IDataUtil.isNotEmpty(psptTypeCodeTrans)){
	    			String idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
	    			data.put("PSPT_TYPE_CODE", idCardType);
	    		}else{
	    			data.put("PSPT_TYPE_CODE", "0");
	    		}
    		}	    		

            if ("0".equals(data.getString("SEX"))) {
                data.put("SEX", "F");
            } else {
                data.put("SEX", "M");
            }

            IData cardinfo = new DataMap();
            cardinfo = postinfo.getData(0);

            if (!"1".equals(cardinfo.getString("STATE"))) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("SERIAL_NUMBER") + ",号码不是待激活状态!");
            }

            UcaData uca = null;
            IData inputData = new DataMap();
            uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

            inputData.put("USER_ID", uca.getUserId());
            inputData.put("CUST_ID", uca.getCustId());
            inputData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));

            String acctTag = uca.getUser().getAcctTag();
            if ("0".equals(acctTag)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("SERIAL_NUMBER") + ",用户不能通过登记实名制激活!");
            }

            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);

            IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("CHECK_MODE", "F");// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
            params.put("TRADE_TYPE_CODE", "60");
            params.put("WX_MODE", "WX");//表示微信激活
            params.putAll(data);

            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
			//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
			externalChannelPicInfo(data, results.getData(0).getString("TRADE_ID",""));

//            IData input = new DataMap();
//            input.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
//            input.put("STATE", "2");
//            input.put("OLD_STATE", "1");
//                        
// 
//            
//            input.put("PICNAMEV2", data.getString("PICNAMEV2",""));
//            input.put("PICNAMER", data.getString("PICNAMER",""));
//            input.put("RESERMARK", data.getString("RESERMARK",""));
//            input.put("CHECKTYPE", data.getString("CHECKTYPE",""));
//            input.put("SALECHANNLE",data.getString("SALECHANNLE",""));   
//            
//            CreatePostPersonUserBean.updatePostCardInfo(input);
            
            returnData.put("BIZ_CODE", "0000");
            returnData.put("BIZ_DESC", "激活成功");
            return returnData;

        } else {
        	//信用购机需求增加处理
            IDataset orderInfos=SelfTerminalBean.querySubOrderInfo(data.getString("SERIAL_NUMBER"));
            if(IDataUtil.isNotEmpty(orderInfos)){
            	data.put("CREDIT_PAY_TAG", "1");
            	data.put("CREDIT_ORDER_ID", orderInfos.getData(0).getString("ORDER_ID"));
            	data.put("CREDIT_SUBORDER_ID", orderInfos.getData(0).getString("SUBORDER_ID"));
            }
        	//转向原有接口的调用
            IDataset results = CSAppCall.call("SS.RealNameJudgeSVC.realNameRegiste", data);
			//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
			externalChannelPicInfo(data, results.getData(0).getString("TRADE_ID",""));
            if(IDataUtil.isNotEmpty(results)){
                returnData.put("BIZ_CODE", results.first().getString("RETURN_CODE"));
                returnData.put("BIZ_DESC", results.first().getString("RETURN_MESSAGE"));
                
                if(IDataUtil.isNotEmpty(orderInfos)){
                	String orderId=orderInfos.getData(0).getString("ORDER_ID");
                	String subOrderId = orderInfos.getData(0).getString("SUBORDER_ID");
	                //激活成功
	                if("0000".equals(results.first().getString("RETURN_CODE"))
	                		||"号码已经实名".equals(results.first().getString("RETURN_MESSAGE"))){
	                	SelfTerminalBean.updatePreOrderInfo(null,"2", orderId, subOrderId);
	                	SelfTerminalBean.updateSubOrderInfo("AC", "激活成功", orderId, subOrderId);
	                //激活失败
	                }else{
	                	SelfTerminalBean.updatePreOrderInfo(null,"4", orderId, subOrderId);
	                	SelfTerminalBean.updateSubOrderInfo("AF", "激活失败-"+results.first().getString("RETURN_MESSAGE"), orderId, subOrderId);
	                }
                }
            }
            return returnData;
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("SERIAL_NUMBER") + ",号码不是待激活状态!");            
        }

    }

	/**
	 * 外部渠道人像比对信息
	 * @param data
	 * @throws Exception
	 */
	private void externalChannelPicInfo(IData data, String tradeId) throws Exception {
		if(log.isDebugEnabled())
		{
			log.debug("外部渠道人像比对信息：" + data);
			log.debug("外部渠道人像比对信息tradeId：" + tradeId);
		}
		if(StringUtils.isNotEmpty(tradeId)) {
			IDataset mainTradeDataSet = TradeInfoQry.queryUserTradeByBTradeAndBhTrade(tradeId);
			if(IDataUtil.isNotEmpty(mainTradeDataSet)){
				String userId = mainTradeDataSet.getData(0).getString("USER_ID");
				IData reqInfo = new DataMap();
				reqInfo.put("TRADE_ID", tradeId);
				reqInfo.put("USER_ID", userId);
				reqInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
				reqInfo.put("CARD_ID", data.getString("PSPT_ID"));
				reqInfo.put("BUSINESS_TYPE", mainTradeDataSet.getData(0).getString("TRADE_TYPE_CODE"));
				reqInfo.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
				reqInfo.put("PIC_NNAME_Z", data.getString("PIC_NAME_Z"));
				reqInfo.put("PIC_NNAME_F", data.getString("PIC_NAME_F"));
				reqInfo.put("PIC_NNAME_R", data.getString("PIC_NAME_R"));
				reqInfo.put("PIC_NNAME_V1", data.getString("PIC_NNAME_V1"));
				reqInfo.put("PIC_NNAME_V2", data.getString("PIC_NNAME_V2"));
				reqInfo.put("DEAL_TAG", "0");
				reqInfo.put("CHANNEL_TYPE", "2");
				reqInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				reqInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
				Dao.insert("TD_B_PICTURE_INFO", reqInfo, Route.CONN_CRM_CEN);
			}
        }
	}

	private IData exchangeCardActive(IData data) throws Exception {
		IData resultMap = new DataMap();
		resultMap.put("BIZ_CODE","0000");
		resultMap.put("BIZ_DESC","success");
		IData param = new DataMap();
		try {
			String simCardId = checkExchangeParam(data,resultMap);
			if(!"0000".equals(resultMap.getString("BIZ_CODE"))){
				return resultMap;
			}
			data.put("SIM_CARD_NO",resultMap.getString("ICCID"));
			log.debug("resultMap222"+resultMap.toString());
			if(log.isDebugEnabled()){
				log.debug("对应的空卡号："+simCardId);
				log.debug("对应的ICCID："+resultMap.getString("ICCID"));
				log.debug("对应的IMSI："+resultMap.getString("IMSI"));
			}
			//预占资源
			CSBizService.getVisit().setInModeCode("12");
			CSBizService.getVisit().setLoginEparchyCode("0898");
			CSBizService.getVisit().setStaffEparchyCode("0898");
			preOccupySimCard(data.getString("SERIAL_NUMBER"),resultMap.getString("ICCID") , "2", "");
			data.put("IMSI",resultMap.getString("IMSI"));
			data.put("SKU_NAME",resultMap.getString("SKU_NAME"));
			pottingSVCMapForExchange(data,param);
			//修改调用营业服务时的接口入参
			IDataset outparams  = CSAppCall.call( "SS.SimCardTrade.tradeReg", param);
			//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
			externalChannelPicInfo(data, outparams.getData(0).getString("TRADE_ID",""));
			CreatePostPersonUserBean createPostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
			data.put("ICCID",resultMap.getString("ICCID"));
			createPostPersonUserBean.updateExchangeCardFinish(data);


		} catch (Exception e) {

			log.error("补换卡认证激活参数："+data.toString());
			log.error("补换卡认证激活异常"+e.getMessage(),e);
			CreatePostPersonUserBean createPostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);

			String exceptionMsg = ExceptionUtils.getStackTrace(e);
			if(exceptionMsg.length()>2000){
				exceptionMsg = exceptionMsg.substring(0,2000);

			}
			data.put("EXCEPTION_STR",exceptionMsg);
			data.put("ICCID",resultMap.getString("ICCID"));
			createPostPersonUserBean.updateExchangeCardException(data);

			resultMap.put("BIZ_CODE","2999");
			resultMap.put("BIZ_DESC",e.getMessage());
		}

		return resultMap;
	}
	public void preOccupySimCard(String serialNumber, String simCardNo, String remoteMode, String isNotRelease) throws Exception
	{
		String isNp = "0";
		IData uData = MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
		if (IDataUtil.isNotEmpty(uData))
		{
			String asp = uData.getString("ASP", "");
			if (!"1".equals(asp))
			{
				isNp = "1";
			}
		}
		String netTypeCode = "";
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isNotEmpty(userInfo)){
			netTypeCode = userInfo.getString("NET_TYPE_CODE","");
		}
		// SIM卡选占
	
		ResCall.checkResourceForSim(simCardNo, serialNumber, "0", isNotRelease, "", remoteMode, "1", isNp, "一级电渠补换卡", netTypeCode);
	}
	private String checkExchangeParam(IData data, IData resultMap) throws Exception {
		String serialNumber = IDataUtil.chkParam(data,"SERIAL_NUMBER");
		String simCardNo = IDataUtil.chkParam(data,"SIM_CARD_NO");
		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
		IDataset resInfos = UserResInfoQry.queryUserSimInfo(ucaData.getUserId(),"1");
		if(IDataUtil.isEmpty(resInfos)){
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", "无历史SIM卡信息！");
			return "";

		}
		//获取订单信息
		CreatePostPersonUserBean createPostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
		IDataset orderList = createPostPersonUserBean.queryExchangeOrderForSerialNumber(serialNumber);
		//String state ="00";
		String executeState ="00"; //执行状态
		String simCardDb = "";
		String imsi = "";
		String iccid = "";
		if(IDataUtil.isNotEmpty(orderList)){

				for(Object orderObj : orderList){
					IData order  = (IData)orderObj;
					if(StringUtils.isBlank(order.getString("EMPTY_CARD_ID"))){
						continue;
					}
					if(order.getString("EMPTY_CARD_ID").length()< 7){
						continue;
					}
					boolean simCardRight = false ;
					simCardDb = order.getString("EMPTY_CARD_ID");
					String simCardDbTemp = simCardDb ;
					if(simCardNo.length()==6){
						simCardDbTemp = simCardDb.substring(simCardDb.length()-6,simCardDb.length());
					}

					if(simCardNo.equals(simCardDbTemp)){
						//state = order.getString("STATE");
						executeState = order.getString("RSRV_STR1");
						simCardDb = order.getString("EMPTY_CARD_ID");
						iccid = order.getString("ICCID");
						resultMap.put("EMPTY_CARD_ID",simCardDb);
						resultMap.put("ICCID",order.getString("ICCID"));
						resultMap.put("IMSI",order.getString("IMSI"));
						resultMap.put("SKU_NAME",order.getString("SKU_NAME"));
						resultMap.put("CREATE_TIME",order.getString("CREATE_TIME"));
						resultMap.put("PICNAMERPATH",order.getString("IMAGES"));
						CSBizService.getVisit().setStaffId(order.getString("OPERA_STAFF"));
						CSBizService.getVisit().setDepartId(order.getString("OPERA_DEPART"));
						CSBizService.getVisit().setCityCode(order.getString("OPERA_CITY_CODE"));
						log.debug("resultMap111"+resultMap.toString());
					}
			}

		}else{
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", "无订单数据！");
			return "";
		}

		if(StringUtils.isBlank(iccid)){
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", "sim卡信息不正确！");
			return "";
		}


		//REQ202003260020线上补换卡激活规则优化 update by wuwangfeng
		/*if(!ExchangeCradStateEnum.WAIT_SIGN.getValue().equals(state)){
			String bizDesc = "订单不是可激活状态，当前状态为【"+ExchangeCradStateEnum.getDescriptionByValue(state)+"】";*/
		if(!ExchangeCradStateEnum.YJ_SUCCESS.getValue().equals(executeState)){
			String bizDesc = "订单不是邮寄成功状态，当前执行状态为【"+ExchangeCradStateEnum.getDescriptionByValue(executeState)+"】";
			resultMap.put("BIZ_CODE", "2999");
			resultMap.put("BIZ_DESC", bizDesc);
			return "";
		}
		return simCardDb;
	}

	private void pottingSVCMapForExchange(IData data, IData param) {
		param.put("CHECK_MODE","F");
		param.put("SERIAL_NUMBER",data.getString("SERIAL_NUMBER"));
		param.put("IMSI",data.getString("IMSI"));
		param.put("REMARK","一级电渠补换卡");
		String skuName = data.getString("SKU_NAME");
		if("在线换卡".equals(skuName)){
			param.put("REMOTECARD_TYPE","1");
		}else{
			param.put("REMOTECARD_TYPE","0");
		}


	}

	private IData unitOpenActive(IData data) {


		IData resultMap = new DataMap();
		resultMap.put("BIZ_CODE","0000");
		resultMap.put("BIZ_DESC","success");
		IData param = new DataMap();
		try {
			pottingSVCMap(data,param);

			CSAppCall.call( "SS.UnitOpenChangeActiveSVC.checkPsptId", param);
			IDataset dataset = CSAppCall.call("SS.UnitOpenChangeActiveRegSVC.tradeReg", param);
			//REQ202002270010  [实名制检查整改]关于在线公司人像比对回传流水部分渠道没有记录优化
			externalChannelPicInfo(data, dataset.getData(0).getString("TRADE_ID",""));
		} catch (Exception e) {
			log.error("单位证件激活异常参数："+data.toString());
			log.error("单位证件激活异常"+e.getMessage(),e);
			resultMap.put("BIZ_CODE","2999");
			resultMap.put("BIZ_DESC",e.getMessage());
		}

		return resultMap;
	}

	/**
	 * 构建服务请求参数
	 * @param data
	 * @param param
	 */
	private void pottingSVCMap(IData data, IData param) throws Exception {
		String psptId = IDataUtil.chkParam(data,"PSPT_ID");
		String custName = IDataUtil.chkParam(data,"CUST_NAME");
		String serialNumber = data.getString("SERIAL_NUMBER");
		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
		if(!psptId.equals(ucaData.getCustPerson().getRsrvStr7())){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, psptId+"使用人证件号与开户时不一致!");
		}
		if(!custName.equals(ucaData.getCustPerson().getRsrvStr5())){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, custName+"使用人姓名与开户时不一致!");
		}
		param.put("SEX","1".equals(data.getString("GENDER"))?"M":"F");
		param.put("FOLK_CODE",data.getString("NATION"));
		param.put("SERIAL_NUMBER",serialNumber);
		param.put("USE",custName);
		param.put("USE_PSPT_ID",psptId);
		param.put("BIRTHDAY",data.getString("BIRTHDAY"));
		param.put("ISSUING_AUTHORITY",data.getString("ISSUING_AUTHORITY"));
		param.put("PSPT_START_DATE",data.getString("CERT_VALIDDATE"));
		param.put("PSPT_END_DATE",data.getString("CERT_EXPDATE"));
		param.put("CHANNEL_ID",data.getString("CHANNEL_ID"));
		param.put("TRANSACTION_ID",data.getString("TRANSACTION_ID"));
		param.put("USE_PSPT_ADDR",data.getString("CUST_CERT_ADDR"));

		param.put("IS_REAL_NAME","1");
		param.put("CUST_NAME",ucaData.getCustomer().getCustName());
		param.put("PSPT_ID",ucaData.getCustomer().getPsptId());
		param.put("PSPT_ADDR",ucaData.getCustPerson().getPsptAddr());
		param.put("POST_ADDRESS",ucaData.getCustPerson().getPostAddress());
		param.put("HOME_ADDRESS",ucaData.getCustPerson().getHomeAddress());
		param.put("PSPT_TYPE_CODE",ucaData.getCustomer().getPsptTypeCode());
		param.put("PHONE",ucaData.getCustPerson().getPhone());
		param.put("WORK_NAME",ucaData.getCustPerson().getWorkName());
		param.put("EMAIL",ucaData.getCustPerson().getEmail());
		param.put("AGENT_CUST_NAME",ucaData.getCustomer().getRsrvStr7());
		param.put("AGENT_PSPT_ID",ucaData.getCustomer().getRsrvStr9());
		param.put("AGENT_PSPT_ADDR",ucaData.getCustomer().getRsrvStr10());







	}


	/**
     * 全网用户数据查询 手机号码查询接口
     * 
     * @author liusj
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCustNumber(IData param) throws Exception
    {
//    	String psptTypeCode = IDataUtil.chkParam(param, "PSPT_TYPE_CODE");
    	IData returnData = new DataMap();
    	returnData.put("TRANS_ID", param.getString("TRANS_ID","").trim());
    	
        String psptId = IDataUtil.chkParam(param, "PSPT_ID");
        //String custname = IDataUtil.chkParam(param, "CUST_NAME");
        param.put("PSPT_TYPE_CODE", "0");
    	
        IDataset ChlInfos = ChnlInfoQry.getGlobalChlId(getVisit().getDepartId());

        if (IDataUtil.isNotEmpty(ChlInfos)) {
        	param.put("CHANNEL_ID", ChlInfos.getData(0).getString("GLOBAL_CHNL_ID", ""));
        }
        else
        {
        	param.put("CHANNEL_ID", "0000");
        }
        NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
        IDataset returnDataset =  bean.queryCustNumber(param);
        if(IDataUtil.isNotEmpty(returnDataset))
        {
        	IDataset resDataset = new DatasetList();
 	
        	for(int i = 0; i < returnDataset.size(); i++)
        	{
        		IData resinfo = new DataMap();
        		
        		resinfo.put("SERIAL_NUMBER", returnDataset.getData(i).getString("IDV"));
        		resinfo.put("CUST_NAME", returnDataset.getData(i).getString("CUSTOMER_NAME"));
        		resDataset.add(resinfo);
        	}
        	
        	returnData.put("RES_INFO", resDataset);
        	returnData.put("BIZ_CODE", "0000");
        	
        }
        else
        {
        	returnData.put("BIZ_CODE", "1005");	
        }
		
        returnData.put("BIZ_DESC", "查询成功");
	    return returnData;
        
    }
    
	   /**
     * 用户状态信息查询
     * 
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData getUserState (IData data) throws Exception {
		
		   IData returnData = new DataMap();
		   IData resinfo = new DataMap();
			// 入参校验
	        IDataUtil.chkParam(data, "SERIAL_NUMBER");
	        IDataUtil.chkParam(data, "TRANS_ID");
	        returnData.put("TRANS_ID", data.getString("TRANS_ID"));
	        String serialNumber = data.getString("SERIAL_NUMBER");
	        
//	        IDataset users = UserInfoQry.queryAllUserInfoBySn(serialNumber);
	        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	        if(IDataUtil.isNotEmpty(userInfo))
	        {
	        	String stauts = userInfo.getString("USER_STATE_CODESET");
	         	if("0".equals(stauts))
	        	{
	         		resinfo.put("STAUTS","1");
	         		resinfo.put("STAUTS_DESC","正常");
	        	}
	        	if("5".equals(stauts))
	        	{
	        		resinfo.put("STAUTS","2");
	        		resinfo.put("STAUTS_DESC","欠费");
	        	}
	        	if("1".equals(stauts) || "4".equals(stauts))
	        	{
	        		resinfo.put("STAUTS","3");
	        		resinfo.put("STAUTS_DESC","停机");
	        	}
	        	
	        	UcaData uca = null;
	            
	        	uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
	        	
	        	resinfo.put("CUST_NAME",uca.getCustomer().getCustName());
	        	resinfo.put("PSPT_ID",uca.getCustomer().getPsptId());
	        	resinfo.put("CUST_LEVEL","D1");
	        	String credit_class = "";
	        	try
	               {
	        		    credit_class =uca.getUserCreditClass();
	               }
	               catch (Exception e)
	               {
	               }
	        	if(null != credit_class && !"".equals(credit_class))
	        	{
	        		resinfo.put("CREDIT_LEVEL","0"+credit_class);	
	        	}
	        	else
	        	{	        	
	        	resinfo.put("CREDIT_LEVEL","09");
	        	}
	        	resinfo.put("OPEN_TIME",userInfo.getString("OPEN_DATE"));
	        	resinfo.put("CUST_ID",uca.getCustomer().getCustId());
	        	resinfo.put("PRODUCT_ID",uca.getProductId());
	        	IDataset prodInfo= ProductInfoQry.getProductInfoByid(uca.getProductId());
				if(prodInfo!=null&&prodInfo.size()>0){
		        resinfo.put("PRODUCT_NAME",prodInfo.getData(0).getString("PRODUCT_NAME"));
		        IDataset resinfos = new DatasetList();
		        resinfos.add(resinfo);
		        returnData.put("RES_INFO", resinfos);
		        returnData.put("BIZ_CODE", "0000");
		        returnData.put("BIZ_DESC", "操作成功");
				}
				
	        }
	        else
	        {
	        	returnData.put("BIZ_CODE", "2999");	
	        	returnData.put("BIZ_DESC", "号码不存在");
	        }
	    	
	    return returnData;
	}
    /**
     * 25天后到期提醒短信
     * 
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData remindSMS(IData data) throws Exception {
		IData returnData = new DataMap();
	    CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        IDataset postinfo = CreatePostPersonUserBean.getPostSMSInfo();
        
        if(IDataUtil.isNotEmpty(postinfo))
        {
         	for(int i = 0; i < postinfo.size(); i++)
        	{
        		IData input = new DataMap();
        		
        		String serial_number =postinfo.getData(i).getString("SERIAL_NUMBER");
        		String end_data =postinfo.getData(i).getString("END_DATE");

        		input.put("POST_PHONE", postinfo.getData(i).getString("POST_PHONE"));
        		      		
        		String content = "尊敬的客户，您好！您办理的手机号码："+serial_number+"将在"+end_data+"到期，请及时到微信平台激活,过期后将被销号。如有疑问，请咨询10086。中国移动";
        	
        		input.put("CONTENT", content);
        		
        	    CreatePostPersonUserBean.sendSMS(input);
        	    
        	    input.put("SERIAL_NUMBER", serial_number);
        	    
        	    CreatePostPersonUserBean.updateSMSfalg(input);
        	
        	}
		
        }
        returnData.put("BIZ_CODE", "0000");
        returnData.put("BIZ_DESC", "操作成功");
	    return returnData;
	}
	
	
    /**
     * 25天后到期销户
     * 
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
	public IData destroyUser(IData data) throws Exception {
		IData returnData = new DataMap();
	    CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
        IDataset postinfo = CreatePostPersonUserBean.getDestroyuser();
        
        if(IDataUtil.isNotEmpty(postinfo))
        {
         	for(int i = 0; i < postinfo.size(); i++)
        	{
         		try
	               {
	        	
        		      String serial_number =postinfo.getData(i).getString("SERIAL_NUMBER");
        		      IData params = new DataMap();
                      params.put("TRADE_TYPE_CODE", "192");
                      params.put("ORDER_TYPE_CODE", "192");
                      params.put("SERIAL_NUMBER",serial_number);
                 
                      CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);
                
                      CreatePostPersonUserBean.updateDUfalg(params);
	               }
	                  catch (Exception e)
	               {
	                  
	               }
        	}
		
        }
        returnData.put("BIZ_CODE", "0000");
        returnData.put("BIZ_DESC", "操作成功");
	    return returnData;
	}
	
	public IDataset sendIdentityMsg(IData data) throws Exception {
		log.info("CreatePostPersonUserSVC-data:"+data);
		IDataset commparaInfos = CommparaInfoQry.getCommByParaAttr("CSM", "5220", "0898");
		
		String url="http://10.200.130.23:7001/weblobby/weblobby/wxMessage/notice/acquireMsg.do";
		String urlNameString="";
		if(commparaInfos==null){
			commparaInfos = new DatasetList();
		}
		int size=commparaInfos.size();
		IData outParam = new DataMap();
		try{
			IData inParam = new DataMap();
			
			if(data!=null){
				for (int i=0;i<size;i++){
					IData commparaInfo=commparaInfos.getData(i);
					if(commparaInfo.getString("PARAM_CODE","").equals("SEND")&&commparaInfo.getString("PARA_CODE3","").equals("-1")){
						urlNameString+="&"+commparaInfo.getString("PARA_CODE2","")+"="+data.getString(commparaInfo.getString("PARA_CODE1",""), "");
						inParam.put(commparaInfo.getString("PARA_CODE2",""), data.get(commparaInfo.getString("PARA_CODE1","")));
					}else if(commparaInfo.getString("PARAM_CODE","").equals("URL")){
						url=commparaInfo.getString("PARA_CODE3","");
					}else if(commparaInfo.getString("PARAM_CODE","").equals("SENDMAP")){
						IData inParamMap = new DataMap();
						for (int j=0;j<size;j++){
							IData commparaInfoj=commparaInfos.getData(j);
							if(commparaInfoj.getString("PARAM_CODE","").equals("SEND")
									&&commparaInfoj.getString("PARA_CODE3","").equals(commparaInfo.getString("PARA_CODE1",""))){
								inParamMap.put(commparaInfoj.getString("PARA_CODE2",""), data.get(commparaInfoj.getString("PARA_CODE1","")));
							}
						}
						inParam.put(commparaInfo.getString("PARA_CODE2",""), inParamMap);
					}
				} 
			}
			log.info("CreatePostPersonUserSVC-url:"+url);
			log.info("CreatePostPersonUserSVC-commparaInfo:"+inParam);
			try{
				outParam=HttpSvcTool.sendHttpPostMsg(url,inParam.toString(),null);
			}
			catch (Exception e) {
				outParam.put("callbackURL", "http://localhost");
				outParam.put("returnCode", "2999");
				outParam.put("returnMessage", "调用新大陆电渠失败！");
				outParam.put("transactionID", "20180831101010100001");
				log.info("CreatePostPersonUserSVC-Exception",e);
			}
		}
		catch (Exception e) {
			outParam.put("callbackURL", "http://localhost");
			outParam.put("returnCode", "1001");
			outParam.put("returnMessage", "BOSS异常！");
			outParam.put("transactionID", "20180831101010100002");
			log.info("CreatePostPersonUserSVC-Exception",e);
		}
		log.info("CreatePostPersonUserSVC-outParam:"+outParam);
		if(outParam==null){
			outParam = new DataMap();
			outParam.put("callbackURL", "http://localhost");
			outParam.put("returnCode", "2999");
			outParam.put("returnMessage", "调用新大陆电渠失败！");
			outParam.put("transactionID", "20180831101010100001");
			log.info("根据URL："+url+" 调用异常或返回为空!");
		}
		IData returnData = outParam;
		for (int j=0;j<size;j++){
			IData commparaInfo=commparaInfos.getData(j);
			if(commparaInfo.getString("PARAM_CODE","").equals("RETURN")){
				if(commparaInfo.getString("PARA_CODE1")!=null&&commparaInfo.getString("PARA_CODE2")!=null){
					returnData.put(commparaInfo.getString("PARA_CODE1"), outParam.getString(commparaInfo.getString("PARA_CODE2"),""));
				}
				
			}
		} 
		log.info("CreatePostPersonUserSVC-returnData:"+returnData);
		IDataset inParams = new DatasetList();
		inParams.add(returnData);
		return inParams;
	}
}

