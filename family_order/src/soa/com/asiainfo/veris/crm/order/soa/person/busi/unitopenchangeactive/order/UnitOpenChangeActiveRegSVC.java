package com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePostPersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.firstcall.FirstCallTimeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order.requestdata.UnitOpenChangeActiveReqData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

import java.util.List;

public class UnitOpenChangeActiveRegSVC extends OrderService
{
    // 未激活用户实名制时进行 首话单激活
    private void firstCall(String userId, String serialNumber, BusiTradeData btd) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("FIRST_CALL_TIME", SysDateMgr.getSysTime());
        
        /*
         * 神州行超享卡优化规则（首月优惠及约定在网）前移,QR-20170824-01,请协助核查并补捆绑：18789786348号码激活未捆绑约定在网4个月的优惠,核查不出来什么原因
         */
        IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
        if (IDataUtil.isEmpty(userMainProductInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主产品信息表时出错");
        }
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主表时出错");
        }
        String iv_product_id = userMainProductInfo.getString("PRODUCT_ID");
        String iv_in_staff_id = userInfo.getString("IN_STAFF_ID");
        String iv_in_depart_id = userInfo.getString("IN_DEPART_ID");
        FirstCallTimeBean bean = (FirstCallTimeBean) BeanManager.createBean(FirstCallTimeBean.class);
        IDataset commparaInfos9227 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9227", iv_product_id, "0898");
        if (commparaInfos9227!=null && commparaInfos9227.size()>0)
        {
			
        	String discntCode=commparaInfos9227.getData(0).getString("PARA_CODE1");//para_code1=后台绑定优惠
        	String continuous=commparaInfos9227.getData(0).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
        	String effTime=commparaInfos9227.getData(0).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效
        	String giftTime=commparaInfos9227.getData(0).getString("PARA_CODE4");//para_code4=1-特殊判断时间

	        String startDate="";
	        String endData="";
	        if("0".equals(effTime)){
	        	startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
	        	if(!"".equals(continuous) && !"null".equals(continuous)){
	            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
	            }else{
	            	endData= SysDateMgr.END_DATE_FOREVER;
	            }
	        }else{
	        	startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
	        	if(!"".equals(continuous) && !"null".equals(continuous)){
	            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1);//绑定期限(数字代表几个月，null则到2050）
	            }else{
	            	endData= SysDateMgr.END_DATE_FOREVER;
	            }
	        }
	        
	        boolean flag = true;
	        if("1".equals(giftTime)){
	        	IDataset flagds = UserProductInfoQry.queryUserMainProductByCommpara(userId, iv_product_id);
	        	if(IDataUtil.isNotEmpty(flagds))
	        	{
	        		flag=false;
	        	}
	        }
	        if(flag)
	        {
	        	IData discntData=new DataMap();
		        discntData.put("USER_ID", userId);
		        discntData.put("PRODUCT_ID", "-1");
		        discntData.put("PACKAGE_ID", "-1");
		        discntData.put("DISCNT_CODE", discntCode);
		        discntData.put("START_DATE", startDate);
		        discntData.put("END_DATE", endData);
		        discntData.put("UPDATE_STAFF_ID", iv_in_staff_id);
		        discntData.put("UPDATE_DEPART_ID", iv_in_depart_id);
		            
		        bean.del9227UserDiscnt(discntData);
		        bean.ins9227UserDiscntnew(discntData);
	        }   
        }
        
        
        IDataset commparaInfos9228 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9228", iv_product_id, "0898");
        if (IDataUtil.isNotEmpty(commparaInfos9228))
        {
			for(int i=0;i<commparaInfos9228.size();i++)
			{
				String discntCode=commparaInfos9228.getData(i).getString("PARA_CODE1");//para_code1=后台绑定优惠
	        	String continuous=commparaInfos9228.getData(i).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
	        	String effTime=commparaInfos9228.getData(i).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效 2-绝对时间
	        	String giftTime=commparaInfos9228.getData(i).getString("PARA_CODE4","");//para_code4=1-特殊判断时间
	        	String endTime=commparaInfos9228.getData(i).getString("PARA_CODE5","");//para_code5=绝对时间
	        	String smsContent=commparaInfos9228.getData(i).getString("PARA_CODE20","");//para_code20=开通短信内容

		        String startDate="";
		        String endData="";
		        if("0".equals(effTime)){
		        	startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
		        	if(!"".equals(continuous) && !"null".equals(continuous)){
		            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
		            }else{
		            	endData= SysDateMgr.END_DATE_FOREVER;
		            }
		        }else if ("1".equals(effTime)) {
		        	startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
		        	if(!"".equals(continuous) && !"null".equals(continuous)){
		            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1);//绑定期限(数字代表几个月，null则到2050）
		            }else{
		            	endData= SysDateMgr.END_DATE_FOREVER;
		            }
				}else if ("2".equals(effTime)) {
					startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
					if(StringUtils.isNotBlank(endTime)){
		        		endData= endTime;
		        	}else{
		        		endData= SysDateMgr.END_DATE_FOREVER;
		        	}
				}     
		        else{
		        	startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
		        	if(StringUtils.isNotBlank(endTime)){
		        		endData= endTime;
		        	}else{
		        		endData= SysDateMgr.END_DATE_FOREVER;
		        	}
		        }
		        
		        boolean flag = true;
		        if("1".equals(giftTime)){
		        	IDataset flagds = UserProductInfoQry.queryUserMainProductByCommpara(userId, iv_product_id);
		        	if(IDataUtil.isNotEmpty(flagds))
		        	{
		        		flag=false;
		        	}
		        }
		        if(flag)
		        {
		        	String instId = SeqMgr.getInstId();
		        	IData discntData=new DataMap();
			        discntData.put("USER_ID", userId);
			        discntData.put("USER_ID_A","-1");
			        discntData.put("PRODUCT_ID", iv_product_id);
			        discntData.put("PACKAGE_ID", iv_product_id);
			        discntData.put("DISCNT_CODE", discntCode);
			        discntData.put("SPEC_TAG","0");
			        discntData.put("RELATION_TYPE_CODE","");
			        discntData.put("INST_ID",instId);
			        discntData.put("CAMPN_ID","");
			        discntData.put("START_DATE", startDate);
			        discntData.put("END_DATE", endData);
			        discntData.put("UPDATE_TIME",SysDateMgr.getSysTime());
			        discntData.put("UPDATE_STAFF_ID", iv_in_staff_id);
			        discntData.put("UPDATE_DEPART_ID", iv_in_depart_id);
			            
			        bean.del9227UserDiscnt(discntData);
			        bean.insOfferRel(discntData);
			        bean.ins9228UserDiscntnew(discntData);		        
		        }
		        
		        //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng start
		        if(StringUtils.isNotBlank(smsContent)){
		        	IData ObjectsmsData = new DataMap(); // 短信数据
	                ObjectsmsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
	                ObjectsmsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
	                ObjectsmsData.put("FORCE_OBJECT", "10086");// 发送对象
	                ObjectsmsData.put("RECV_OBJECT", serialNumber);// 接收对象
	                ObjectsmsData.put("NOTICE_CONTENT", smsContent);// 短信内容
	                PerSmsAction.insTradeSMS(btd, ObjectsmsData);
		        }
		        //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng end
			}   
        }
        
        createOtherTradeData(btd, serialNumber);
        
        CSAppCall.call("SS.FirstCallDealSVC.dealFirstCall", params);
        modifyEPostInfo(userId, serialNumber);

    }
	
	private void modifyEPostInfo(String userId, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("POST_TYPE_BUSI", "2");
        params.put("POST_CHANNEL", "02,12");
        params.put("postinfo_RECEIVE_NUMBER", serialNumber);
        params.put("postinfo_POST_ADR", serialNumber+"@139.com");
        params.put("postinfo_DPOST_ADR", serialNumber+"@139.com");
        params.put("AUTH_SERIAL_NUMBER", serialNumber);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("postinfo_POST_DATE_MON", "");
        params.put("POST_TYPE_CASH", "1");
        params.put("POST_TYPE_MON", "0");
        params.put("TRADE_EPARCHY_CODE", "0898");
        params.put("PUSH", "on");
        
        CSAppCall.call("SS.ModifyEPostInfoSVC.modiTradeReg", params);
    }
    
	/**
	 * 
	 * @param btd
	 * @param input
	 * @throws Exception
	 */
	public void createOtherTradeData(BusiTradeData btd, String serialNumber) throws Exception
    {
		IDataset idsOther = null;
		boolean isNew4G = false;
 		boolean isCancel4G = false;
        boolean isLimit = true;
        UcaData uca = btd.getRD().getUca();
        String strUserID = uca.getUserId();
        List<SvcTradeData> userSvcTD = uca.getUserSvcBySvcId("190");
		if(CollectionUtils.isEmpty(userSvcTD))
 		{
			//限制9981,VoLteAutoServiceAction TD_S_COMMPARA配置编码
			IDataset CommparaParas = CommparaInfoQry.getCommparaAllCol("CSM", "9981", "VoLteAutoServiceAction", "0898");
			if(IDataUtil.isNotEmpty(CommparaParas))
			{
				IData CommparaPara = CommparaParas.first();
				String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
				if("1".equals(strParaCode1))
				{
					isLimit = true;
				}
				else
				{
					isLimit = false;
				}
			}
			
			if(!isLimit)
			{
				ChangeProductBean cpBean = (ChangeProductBean) BeanManager.createBean(ChangeProductBean.class);
				IData idLimit = cpBean.checkVoLTELimit(uca);
				String strResultCode = idLimit.getString("X_RESULTCODE", "2998");
		        //String strResultInfo = idLimit.getString("X_RESULTINFO", "4G特殊用户过滤");
		        //log.info("("VoLTE业务自动开通 " + strResultCode + "|" + strResultInfo);
		        if("0".equals(strResultCode))
		        {
		        	List<ResTradeData> resTrades = uca.getUserAllRes();	
					if(CollectionUtils.isNotEmpty(resTrades) && resTrades.size() > 0)
					{
						for(int i = 0 ; i < resTrades.size(); i++)
						{
							String strResTypeCode = resTrades.get(i).getResTypeCode();
							String strResRsrvTag3 = resTrades.get(i).getRsrvTag3();
							if("1".equals(strResTypeCode) && "1".equals(strResRsrvTag3))
							{
								//用户不存在VOLTE服务，则新增 IMS APN 签约。
								if(CollectionUtils.isEmpty(userSvcTD))
								{
									idsOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(strUserID, "VoLteAutoService");
									if(IDataUtil.isEmpty(idsOther))
									{
										isNew4G = true;
										break;
									}			
			 					} 	
							}
							else
							{
								if("1".equals(strResTypeCode) && "0".equals(strResRsrvTag3))
								{
									idsOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(strUserID, "VoLteAutoService");
									if(CollectionUtils.isEmpty(userSvcTD) && IDataUtil.isNotEmpty(idsOther))
									{
		 								isCancel4G = true;
		 	 	 	 					break;					
				 					} 	
								}
							}
						}
					}
					
					if(isNew4G)
					{
						//IMS APN 签约
						IData idOther = new DataMap();
						idOther.put("SERIAL_NUMBER", uca.getSerialNumber());
						idOther.put("USER_ID", strUserID);
						idOther.put("RSRV_VALUE_CODE", "VoLteAutoService");
						idOther.put("RSRV_VALUE", "1");
						idOther.put("RSRV_STR1", uca.getSerialNumber());
						idOther.put("RSRV_STR2", SysDateMgr.getSysTime());
						idOther.put("RSRV_STR3", CSBizBean.getVisit().getStaffId());
						idOther.put("RSRV_STR4", CSBizBean.getVisit().getStaffName());
						idOther.put("RSRV_STR5", btd.getTradeTypeCode());
						idOther.put("INST_ID", SeqMgr.getInstId());
		        		idOther.put("START_DATE", SysDateMgr.getSysTime());
		        		idOther.put("END_DATE", SysDateMgr.getTheLastTime());
		        		idOther.put("REMARK", "关于优化无感知开通VoLTE业务");
		        		idOther.put("END_DATE", SysDateMgr.getTheLastTime());
		        		idOther.put("MODIFY_TAG", "0");
		        		//createOtherTradeData(btd, idOther);
		        		OtherTradeData tdOther = new OtherTradeData(idOther);
		            	btd.add(serialNumber, tdOther);
		            	MainTradeData mtd = btd.getMainTradeData();
						mtd.setOlcomTag("1");
		        		
					}
					else if(isCancel4G)
					{
						//IMS APN 去签约
						if(IDataUtil.isNotEmpty(idsOther))
						{
							IData idOther = idsOther.first();
							idOther.put("RSRV_VALUE", "0");
							idOther.put("RSRV_STR6", btd.getTradeTypeCode());
							idOther.put("RSRV_STR7", SysDateMgr.getSysTime());
							idOther.put("RSRV_STR8", CSBizBean.getVisit().getStaffId());
							idOther.put("RSRV_STR9", CSBizBean.getVisit().getStaffName());
							idOther.put("SERIAL_NUMBER", uca.getSerialNumber());
							idOther.put("END_DATE", SysDateMgr.getSysTime());
							idOther.put("MODIFY_TAG", "1");
			        		//createOtherTradeData(btd, idOther);
							OtherTradeData tdOther = new OtherTradeData(idOther);
					    	btd.add(serialNumber, tdOther);
						}
					}
		        }
			}
 		}
    	//String serialNumber = input.getString("SERIAL_NUMBER"); 
    	//OtherTradeData tdOther = new OtherTradeData(input);
    	//btd.add(btd.getMainTradeData().getSerialNumber(), tdOther);
    }

    /**
     * 60:前台客户资料变更 3811：无线固话客户资料变更（TRADE_TYPE_CODE从页面传） 接口包括（客户资料变更、手机实名制办理提前校验、手机实名制办理）从接口过来的未传TRADE_TYPE_CODE则默认为60
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "494");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "494");
    }

    // 对用户进行局方开机
    private void openMobile(String tradeId, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_TYPE_CODE", "7232");
        params.put("REMARK", "单位证件开户激活完工流水" + tradeId);
        CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
    }
    
    /**
     *  REQ201606270002 非实名用户关停改造需求 chenxy3 20160629 非实名制局方停机要做
     * */
    private void stopUserOpenMobile(String tradeId, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_TYPE_CODE", "126");
        params.put("REMARK", "实名制登记局方开机" + tradeId);
        CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
    }

    @Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {

		UnitOpenChangeActiveReqData custInfoRD = (UnitOpenChangeActiveReqData) btd.getRD();
        // 客户本次办理了实名制
        if (StringUtils.equals("1", custInfoRD.getIsRealName()))
        {
            String userId = custInfoRD.getUca().getUserId();
            String serialNumber = custInfoRD.getUca().getSerialNumber();
            String tradeId = custInfoRD.getTradeId(); 
            // 邮寄卡只能微信激活 
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
            CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
            IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);

            if (IDataUtil.isNotEmpty(postinfo)) {
                IData returnData = new DataMap();
                returnData = postinfo.getData(0);
                /**
                 * REQ201811010018 关于O2O功能完善的紧急功能 
                 * 营业侧（客户资料变更）放开线上开户号码在线下激活的限制
                 * mengqx 20181108
                 */
      //          if ("1".equals(returnData.getString("STATE")) && "WX".equals(idata.getString("WX_MODE"))) {//预开，并且微信方式过来
                if ("1".equals(returnData.getString("STATE"))) {//预开，放开线上开户号码在线下激活的限制，不限制为微信激活方式

                    //需要调用本地一证多号 和 全国一证多号接口 进行限制。
                    String psptType = idata.getString("PSPT_TYPE_CODE", "").trim();
                    String psptName = idata.getString("CUST_NAME", "").trim();
                    String psptId = idata.getString("PSPT_ID", "").trim();
                    //1.先判断是否超过本省限制的数量，2.在判断是否超过全国1证5号的数据
                    if (psptType.equals("0") || psptType.equals("1") || psptType.equals("3") || psptType.equals("A")) {//本地外地户口护照军人
                        IDataset localLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "7637", "0", "0898");// 本地实名制开户数目默认阙值
                        if (IDataUtil.isNotEmpty(localLimitResult)) {
                            int local_limit_number = localLimitResult.getData(0).getInt("PARA_CODE1", 0);
                            //IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType2(psptType, psptId, psptName);
                            //REQ201906040031 一证五号校验规则优化，去掉身份证类型的条件来查询
                            IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType3(null, psptId, psptName);
                            if (!ds.isEmpty()) {
                                if (local_limit_number - ds.size() <= 0) {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该证件号码[" + psptId + "]已达到本地最大可开户数量，不能开户!");
                                }
                            }
                        }

                        IData inparam = new DataMap();
                        inparam.put("CUSTOMER_NAME", psptName);
                        inparam.put("IDCARD_TYPE", psptType);
                        inparam.put("IDCARD_NUM", psptId);
                        IDataset globalLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptType, "ZZZZ");//全国1证5号不同证件类型的限制数量
                        if (IDataUtil.isNotEmpty(globalLimitResult)) {
                            int global_limit_number = globalLimitResult.getData(0).getInt("PARA_CODE1", 0);

                            NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
                            IDataset nationalOpenLimit = bean.idCheck(inparam);

                            if (IDataUtil.isNotEmpty(nationalOpenLimit)) {
                                IData responseInfo = nationalOpenLimit.getData(0);
                                int globaltotal = responseInfo.getInt("TOTAL", 0);
                                if (global_limit_number - globaltotal <= 0) {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该证件号码[" + psptId + "]已达到全网最大可开户数量，不能开户!");
                                }
                            }
                        }
                    }

                    //邮寄卡状态变更，1变更为2，激活状态
                    IData input = new DataMap();
                    input.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
                    input.put("STATE", "2");
                    input.put("OLD_STATE", "1");  
                    
                    input.put("PICNAMEV2", idata.getString("PICNAMEV2"));
                    input.put("PICNAMER", idata.getString("PICNAMER"));
                    input.put("RESERMARK", idata.getString("RESERMARK"));
                    input.put("CHECKTYPE", idata.getString("CHECKTYPE"));
                    input.put("SALECHANNLE",idata.getString("SALECHANNLE"));   
                    
                    CreatePostPersonUserBean.updatePostCardInfo(input);
                }else {
              //      CSAppException.apperr(CrmCommException.CRM_COMM_103, idata.getString("SERIAL_NUMBER") + ",邮寄卡用户只能通过微信平台激活!");
                   CSAppException.apperr(CrmCommException.CRM_COMM_103, idata.getString("SERIAL_NUMBER") + ",邮寄卡不是预开状态，不能激活!");
                }
            } 
            // 邮寄卡只能微信激活 
            /**
             *  REQ201606270002 非实名用户关停改造需求 chenxy3 20160629 非实名制局方停机要做
             * */
            String service_id="0";
            if("G005".equals(custInfoRD.getUca().getBrandCode())){
            	service_id="1002";
            }
            IDataset userStateSet=UserSvcStateInfoQry.getUserLastStateByUserSvc(userId,service_id);
            String userStateCodeset="0";
            if(userStateSet!=null && userStateSet.size()>0){
            	userStateCodeset=userStateSet.getData(0).getString("STATE_CODE");
            }
            /**
             * REQ201608260010 关于非实名用户关停改造需求
             * 20160912 chenxy3
             * 非实名制欠费停机允许变更开机
             * */
            String userStateCodeset_now=userStateCodeset;
            IData inData=new DataMap();
            inData.put("USER_ID",userId);
            if("5".equals(userStateCodeset_now)){
            	IDataset stopUsers = CSAppCall.call("SS.GetUser360ViewSVC.qryUserIfNotRealNameForOpen", inData);
            	if(stopUsers!=null && stopUsers.size()>0){
            		String recordcount=stopUsers.getData(0).getString("RECORDCOUNT");
            		if(!"0".equals(recordcount)){
            			userStateCodeset_now="AT";
            		}
            	}
            }
            //局方开机
            this.openMobile(tradeId, serialNumber);
            if (StringUtils.equals("2", custInfoRD.getUca().getUser().getAcctTag())&&!"MOSP".equals(custInfoRD.getUca().getBrandCode())&&!"4".equals(userStateCodeset))
				{
					this.firstCall(userId, serialNumber, btd);




					boolean bindFlag = true;
					String limitDate = getSysTagInfo("LIMIT_DATE", "TAG_INFO", "2016-03-31 23:59:59");// 参数字段
					String sysdate = SysDateMgr.getSysTime();
					String GJDate = SysDateMgr.getDateForSTANDYYYYMMDD(limitDate);
					IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdSn("20160112", serialNumber);
					if(sysdate.compareTo(GJDate)<=0&&IDataUtil.isNotEmpty(troopMemberSet)){
						bindFlag = false;
					}
					IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
					if (IDataUtil.isNotEmpty(dataSet)&&bindFlag){
						IData mphonecodeInfo = dataSet.first();
						String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
						if (StringUtils.equals("1", beautifulTag)){
							String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
							if(StringUtils.isBlank(productId)){
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
							}
							String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
							if(StringUtils.isBlank(packageId)){
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销包编码为空，请检查资源侧配置！");
							}
							IData saleactiveData = new DataMap();
							saleactiveData.put("SERIAL_NUMBER",serialNumber);
							saleactiveData.put("PRODUCT_ID",productId);
							saleactiveData.put("PACKAGE_ID", packageId);
							CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
							/****************** REQ201805160026关于吉祥号码特殊减免约定消费权限开发需求 BEGIN ************************/
							List<DiscntTradeData> discntTradeDatas=btd.getRD().getUca().getUserDiscnts();
                             /*IData inDataNum = new DataMap();
                             inDataNum.put("SERIAL_NUMBER", serialNumber);*/
							IDataset rss = ResCall.getMphonecodeInfo(serialNumber);
							if(IDataUtil.isNotEmpty(rss)&& IDataUtil.isNotEmpty(rss.getData(0))){//调用资源判断是否为代理商免约定消费号码池
								IData rssData=rss.getData(0);
								if(rssData.getString("BEAUTIFUL_TAG", "").equals("1")&&rssData.getString("POOL_CODE", "").equals("9")){
									IDataset beautifulDiscntCommpara=ParamInfoQry.getCommparaByAttrCode("CSM","912","9","","0898");
									if (IDataUtil.isNotEmpty(beautifulDiscntCommpara) && IDataUtil.isNotEmpty(beautifulDiscntCommpara.getData(0)))
									{
										for(int k=0;k<beautifulDiscntCommpara.size();k++){
											IData beautifulDiscntCommparaData=beautifulDiscntCommpara.getData(k);
											String discntCode=beautifulDiscntCommparaData.getString("PARA_CODE1", "");
											for(int z=0;z<discntTradeDatas.size();z++){
												DiscntTradeData discntTradeData=discntTradeDatas.get(z);
												if(discntTradeData.getDiscntCode().equals(discntCode)){
													discntTradeData.setEndDate(SysDateMgr.getSysTime());
													discntTradeData.setRemark("吉祥号码特殊减免约定消费");
													break;
												}
											}
										}
									}
								}

							}
							/****************** REQ201805160026关于吉祥号码特殊减免约定消费权限开发需求 END************************/
						}
					}
				}

        }
    }
    
    protected String getSysTagInfo(String tagCode, String key, String defaultValue) throws Exception
    {

        IData param = new DataMap();

        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("TAG_CODE", tagCode);
        param.put("SUBSYS_CODE", "CSM");
        param.put("USE_TAG", 0);

        return TagInfoQry.getSysTagInfo(tagCode, key, defaultValue, CSBizBean.getTradeEparchyCode());


    }

}
