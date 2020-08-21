
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall;

import java.util.Date;

import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.bre.query.BreQryForCommparaOrTag;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RuleCfgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserExpandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

public class FirstCallTimeSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(FirstCallTimeSVC.class);
    private static final long serialVersionUID = -1756769134328846009L;

    private IDataset delKeys = null;

    private void delUCAKey() throws Exception
    {
        if (IDataUtil.isEmpty(delKeys))
        {
            return;
        }

        IData map = null;
        String cacheKey = "";

        for (int row = 0, size = delKeys.size(); row < size; row++)
        {
            map = delKeys.getData(row);

            cacheKey = map.getString("ID_KEY");

            // 在共享缓存中删除Key所对应的Value
            SharedCache.delete(cacheKey);
        }
    }

    public void destroy(IDataInput input, IDataOutput output) throws Exception
    {
        super.destroy(input, output);

        // uca缓存清空（必须放到最后一步）
        delUCAKey();
    }

    private void firstCallDeal(IData userInfo, String iv_userid, String iv_firstcalltime, String v_resultcode, String v_resultinfo ,String flag) throws Exception
    {
        FirstCallTimeBean bean = (FirstCallTimeBean) BeanManager.createBean(FirstCallTimeBean.class);
        String iv_sync_sequence = "";
        String iv_bet_month = "";
        String iv_notice_content = "";
        String iv_rsrv_str5 = "";
        String iv_notice_content_TD1 = "";
        String iv_notice_content_TD2 = "";
        String iv_notice_content_temp = "";
        String IV_getprice = "";
        String IV_getbillflg = "";
        String IV_getunit = "";
        String iv_notice_content_new = "";
        int iv_count0 = 0;
        int iv_count3 = 0;
        int iv_count4 = 0;
        int iv_count5 = 0;
        int iv_count6 = 0;
        int IV_i;
        int IV_j;
        String iv_needmodifyindate = "";
        String iv_needmodifyopenmode = "";
        Date iv_opendate = new Date();
        iv_sync_sequence = SeqMgr.getSyncIncreId();
        if (StringUtils.isEmpty(iv_firstcalltime))
        {
            iv_opendate = SysDateMgr.string2Date(iv_firstcalltime, SysDateMgr.PATTERN_STAND_SHORT);
        }
        IData tagInfoMode = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyOpenmode");
        if (IDataUtil.isEmpty(tagInfoMode) || StringUtils.isEmpty(tagInfoMode.getString("TAG_CHAR")))
        {
            iv_needmodifyopenmode = "0";
        }
        else
        {
            iv_needmodifyopenmode = tagInfoMode.getString("TAG_CHAR");
        }

        IData tagInfoDate = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyIndate");
        if (IDataUtil.isEmpty(tagInfoDate) || StringUtils.isEmpty(tagInfoDate.getString("TAG_CHAR")))
        {
            iv_needmodifyindate = "0";
        }
        else
        {
            iv_needmodifyindate = tagInfoDate.getString("TAG_CHAR");
        }

        String iv_serial_number = userInfo.getString("SERIAL_NUMBER");
        String iv_user_passwd = userInfo.getString("USER_PASSWD");
        String iv_net_type_code = userInfo.getString("NET_TYPE_CODE");
        String iv_acct_tag = userInfo.getString("ACCT_TAG");
        String iv_cust_id = userInfo.getString("CUST_ID");
        String iv_eparchy_code = userInfo.getString("EPARCHY_CODE");
        String iv_in_staff_id = userInfo.getString("IN_STAFF_ID");
        String iv_in_depart_id = userInfo.getString("IN_DEPART_ID");
        String iv_city_code = userInfo.getString("CITY_CODE");
        IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(iv_userid);
        if (IDataUtil.isEmpty(userMainProductInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主产品信息表时出错");
        }
        String iv_product_id = userMainProductInfo.getString("PRODUCT_ID");
        String iv_brand_code = userMainProductInfo.getString("BRAND_CODE");

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(iv_cust_id);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询客户资料异常:未找到客户资料");
        }
        
        //String realNameSwitch = StaticUtil.getStaticValue("REAL_NAME_SWITCH", "0");
        String realNameSwitch = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
        		new java.lang.String[]{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new java.lang.String[]{ "REAL_NAME_SWITCH", "ON" });
        if(!("1".equals(userInfo.getString("IS_REAL_NAME_TAG",""))&&"60".equals(userInfo.getString("TRADE_TYPE_CODE",""))
        		&&("G001".equals(iv_brand_code)||"G002".equals(iv_brand_code)||"G010".equals(iv_brand_code)))// G001：全球通 G002：神州行 G010：动感地带 
        		||StringUtils.equals("1", realNameSwitch)){//客户资料变更接口实名登记挪到60工单reg的ModifyCustRealNameAction
        	 // 更新用户主表资料
            if (("1".equals(iv_needmodifyopenmode) || "1".equals(iv_needmodifyindate)) && !"PWLW".equals(iv_brand_code))
            {
                bean.updUserModeDate(SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND), iv_needmodifyopenmode, iv_needmodifyindate, iv_userid);
            }

            bean.insTibUser(iv_sync_sequence, iv_userid);
        }
       

        String iv_mindateStr = SysDateMgr.getEndCycle20501231();
        String hms = SysDateMgr.date2String(new Date(), "HHmmss");
        iv_mindateStr += hms;
        Date iv_mindate = SysDateMgr.string2Date(iv_mindateStr, SysDateMgr.PATTERN_STAND_SHORT);
        /*
        IDataset userCreditInfos = UserCreditInfoQry.queryCreditMinStartDate(iv_userid);
        if (IDataUtil.isEmpty(userCreditInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "取最早的礼包、购机信用度赠送开始时间出错");
        }
        String hms = SysDateMgr.date2String(new Date(), "HHmmss");
        iv_mindateStr += hms;
        if (IDataUtil.isNotEmpty(userCreditInfos.getData(0)) && StringUtils.isNotEmpty(userCreditInfos.getData(0).getString("START_DATE")))
        {
            iv_mindateStr = userCreditInfos.getData(0).getString("START_DATE");
        }
        // 更新用户购机赠送信用度资料
        if (iv_opendate.compareTo(iv_mindate) == 1)
        {
            String lastDayOpenDate = SysDateMgr.getDateLastMonthSec(SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND));
            String lastDayIv_mindate = SysDateMgr.getDateLastMonthSec(SysDateMgr.date2String(iv_mindate, SysDateMgr.PATTERN_STAND));
            int betweenMonths = SysDateMgr.monthInterval(lastDayOpenDate, lastDayIv_mindate);
            iv_bet_month = String.valueOf(betweenMonths + 1);
            try
            {
                bean.updateCreditMinDate(iv_userid, iv_bet_month, SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND_SHORT));
            }
            catch (Exception e)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "更新用户购机赠送信用度资料出错");
            }
        }
        iv_mindate = SysDateMgr.string2Date(SysDateMgr.END_DATE_FOREVER, SysDateMgr.PATTERN_STAND);
        */
        int result1 = 0 ;
        IDataset userSaleActiveInfos = UserSaleActiveInfoQry.querySaleActiveStartDate(iv_userid);
        if (IDataUtil.isNotEmpty(userSaleActiveInfos))
        {
            for (int i = 0 ; i < userSaleActiveInfos.size(); i++ )
            {
            	iv_mindateStr = userSaleActiveInfos.getData(i).getString("START_DATE","");
            	String productId = userSaleActiveInfos.getData(i).getString("PRODUCT_ID","");
            	String packageId = userSaleActiveInfos.getData(i).getString("PACKAGE_ID","");
            	String acceptDate = userSaleActiveInfos.getData(i).getString("ACCEPT_DATE","");

            	if (StringUtils.isNotEmpty(iv_mindateStr))
                {
                    iv_mindate = SysDateMgr.string2Date(iv_mindateStr, SysDateMgr.PATTERN_STAND_SHORT);
                }
            	if (iv_opendate.compareTo(iv_mindate) == 1)
                {
                    int betweenMonths = 0;
                    //enableModeTag=0,立即生效；enableModeTag=1,下月生效。
                    int enableModeTag = SysDateMgr.monthIntervalNoAbs(acceptDate, iv_mindateStr);
                    //add by zhangxing3 for QR-20180813-06缴费工单错单
            		//如果commpara=8912存在，则不同步TI_A_SYNC_RECV
                    boolean syncTag = true;
                    IDataset commparaInfos8912 = CommparaInfoQry.getCommparaAllColByParser("CSM", "8912", productId, "0898");
                    if (commparaInfos8912!=null && commparaInfos8912.size()>0)
                    {
                    	syncTag = false;
                    }
                    //add by zhangxing3 for QR-20180813-06缴费工单错单
                    //add by zhangxing3 for BUG20180614230037红海行动过程中用户激活后活动绑定套餐无法顺延问题优化
                    int c = 0;
                    try
                    {
                    	if( 0 == enableModeTag){//enableModeTag=0,活动立即生效
	                    	String iv_opendateStr = SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND);
	                    	betweenMonths = SysDateMgr.monthIntervalNoAbs(iv_mindateStr, iv_opendateStr);
                    		iv_bet_month = String.valueOf(betweenMonths);

	                        bean.updUserSaleActive(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);
	                        //使用c记录更新的TF_F_USER_SALE_DEPOSIT表记录数
	                        c = bean.updUserSaleDeposit(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);
	                        //System.out.println("============firstCallDeal==============c:"+c);
	                        result1 += bean.updUserDiscntDate(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);
	                        
	                        //更新的TF_F_USER_SALE_DEPOSIT表记录数>0，才插7044工单给计费账务，否则计费账务侧会有错单。
	                        if ( c > 0 && syncTag)
	                        {
	                        	bean.insTiaRecv(iv_sync_sequence, iv_userid, iv_opendateStr,iv_bet_month);
	                        }
                    	}
                    	else if (1 == enableModeTag)//enableModeTag=1,活动下月生效
                    	{
                    		String iv_opendateStr = SysDateMgr.getFirstDayOfNextMonth()+SysDateMgr.getFirstTime00000();
                    		betweenMonths = SysDateMgr.monthIntervalNoAbs(iv_mindateStr, iv_opendateStr);
                    		iv_bet_month = String.valueOf(betweenMonths);

	                        bean.updUserSaleActive(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);
	                        //使用c记录更新的TF_F_USER_SALE_DEPOSIT表记录数
	                        c = bean.updUserSaleDeposit(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);
	                        //System.out.println("============firstCallDeal==============c:"+c);
	                        result1 += bean.updUserDiscntDate(iv_opendateStr,iv_bet_month, iv_userid,productId,packageId);;
	                        
	                        //更新的TF_F_USER_SALE_DEPOSIT表记录数>0，才插7044工单给计费账务，否则计费账务侧会有错单。
	                        if ( c > 0 && syncTag)
	                        {
	                        	bean.insTiaRecv(iv_sync_sequence, iv_userid, iv_opendateStr,iv_bet_month);
	                        }
                    	}
                    	else
                    	{
                    		//
                    	}
                        
                    }
                    catch (Exception e)
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "更新用户购机资料出错:"+e.getMessage());
                    }
                }
            }
        }

        /****************** 处理数据包 ************************/
        int iv_sync_user_discnt = bean.updUserDiscntEndDate(iv_userid);
        int result = 0 ;
        //修改吉祥号码6067_6070优惠开始时间
        //各种激活方式（首话单和手工买断激活，实名制）都可以激活买断的号码
        if ("PWLW".equals(iv_brand_code))
        {
        	//
        }
        else
        {
        	IDataset dataSet = ResCall.getMphonecodeInfo(iv_serial_number);
        	if (IDataUtil.isNotEmpty(dataSet))
    		{
    			IData mphonecodeInfo = dataSet.first();
    			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
    			String bindMonth = mphonecodeInfo.getString("DEPOSIT_MONTH");
    			// 只有吉祥号码才会订购6067和6070
    			if (StringUtils.equals("1", beautifulTag))
    			{
    /*				int month = Integer.parseInt(bindMonth)+1;
    				String endDate = SysDateMgr.endDate(SysDateMgr.getSysTime(), "1", "", month + "", "3");
    				result = bean.upd60676070UserDiscntStartDate(iv_userid,endDate);*/
    			}
    		}
		}
		
		if("10003370".equals(iv_product_id)||"10003372".equals(iv_product_id))
		{
			/**
		     * REQ201606070017 神州行超享卡优化规则（首月优惠及约定在网）
		     * chenxy3 20160627
		     * */
			IDataset ifExist=bean.check33753376Exist(iv_userid, iv_product_id);
			if(ifExist!=null && ifExist.size()>0){
				bean.del33753376UserDiscnt(iv_userid, iv_product_id);
				result = bean.ins33753376UserDiscnt(iv_userid, iv_product_id, iv_in_staff_id, iv_in_depart_id);
				bean.upd33703372UserDiscnt(iv_userid, iv_product_id, iv_in_staff_id, iv_in_depart_id);
			}
		}
		
		if("10003373".equals(iv_product_id)||"10003374".equals(iv_product_id)){
			/**
		     * REQ201606070017 神州行超享卡优化规则（首月优惠及约定在网）
		     * chenxy3 20160627
		     * */
			IDataset ifExist=bean.check33753376Exist(iv_userid, iv_product_id);
			if(ifExist!=null && ifExist.size()>0){
				bean.del33753376UserDiscnt(iv_userid, iv_product_id);
				result = bean.ins33753376UserDiscnt(iv_userid, iv_product_id, iv_in_staff_id, iv_in_depart_id);
				bean.upd33773378UserDiscnt(iv_userid, iv_product_id, iv_in_staff_id, iv_in_depart_id);
			}
//			bean.del20160406UserDiscnt(iv_userid, iv_product_id);
//			bean.ins20160406UserDiscnt(iv_userid, iv_product_id, iv_in_staff_id, iv_in_depart_id);
			/*if("10003373".equals(iv_product_id)){
				String templateId6 = "CRM_SMS_PER_COMM_16040601";
	   	        IData templateInfo6 = bean.getTemplateInfo(templateId6);
	   	        String iv_notice_content_6 = bean.getSmsContent(templateInfo6, null);
	   	        IData smsInfo6 = new DataMap();
	   	        smsInfo6.put("IN_MODE_CODE", "3");
	   	        smsInfo6.put("CHAN_ID", "C006");
	   	        smsInfo6.put("SMS_KIND_CODE", "08");
	   	        smsInfo6.put("DEAL_STATE", "15");
	   	        smsInfo6.put("NOTICE_CONTENT", iv_notice_content_6);
	   	        smsInfo6.put("RECV_OBJECT", iv_serial_number);
	   	        smsInfo6.put("RECV_ID", iv_userid);
	   	        SmsSend.insSms(smsInfo6);
			}else{
				String templateId7 = "CRM_SMS_PER_COMM_16040602";
	   	        IData templateInfo7 = bean.getTemplateInfo(templateId7);
	   	        String iv_notice_content_7 = bean.getSmsContent(templateInfo7, null);
	   	        IData smsInfo7 = new DataMap();
	   	        smsInfo7.put("IN_MODE_CODE", "3");
	   	        smsInfo7.put("CHAN_ID", "C006");
	   	        smsInfo7.put("SMS_KIND_CODE", "08");
	   	        smsInfo7.put("DEAL_STATE", "15");
	   	        smsInfo7.put("NOTICE_CONTENT", iv_notice_content_7);
	   	        smsInfo7.put("RECV_OBJECT", iv_serial_number);
	   	        smsInfo7.put("RECV_ID", iv_userid);
	   	        SmsSend.insSms(smsInfo7);	
			}*/
		}
		
		//1、要求commpara=9227存在，且要求用户的不存在该优惠（包括本次办理及已有的）         BUG20170216083931激活同步绑定优惠BUG（紧急）
        IDataset commparaInfos9227 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9227", iv_product_id, iv_eparchy_code);
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
	        
	        boolean flags = true;
	        if("1".equals(giftTime)){
	        	IDataset flagds = UserProductInfoQry.queryUserMainProductByCommpara(iv_userid, iv_product_id);
	        	if(IDataUtil.isNotEmpty(flagds))
	        	{
	        		flags=false;
	        	}
	        }
	        if(flags)
	        {
	        	result = 1;
	        	IData discntData=new DataMap();
		        discntData.put("USER_ID", iv_userid);
		        discntData.put("PRODUCT_ID", "-1");
		        discntData.put("PACKAGE_ID", "-1");
		        discntData.put("PACKAGE_ID", "-1");
		        discntData.put("DISCNT_CODE", discntCode);
		        discntData.put("START_DATE", startDate);
		        discntData.put("END_DATE", endData);
		        discntData.put("UPDATE_STAFF_ID", iv_in_staff_id);
		        discntData.put("UPDATE_DEPART_ID", iv_in_depart_id);
		            
		        bean.del9227UserDiscnt(discntData);
		        bean.ins9227UserDiscnt(discntData);
	        }   
        }
        
		//神州行超享卡 激活需要特殊处理的优惠，param_code为discnt_code，para_code1为product_id，para_code2为0表示立即生效，为1表示下月生效
		IDataset setInfos = CommparaInfoQry.getCommparaByCode1("CSM", "9217", iv_product_id, "0898");
		if(IDataUtil.isNotEmpty(setInfos))
		{
		    for(int i=0; i < setInfos.size(); i++)
		    {
		    	String strDiscntCode = setInfos.getData(i).getString("PARAM_CODE");  	//套餐编码
		    	String strEnableFlag = setInfos.getData(i).getString("PARA_CODE2");  	//0表示开始时间立即生效，为1表示下月生效 
		    	String strEndOffset = setInfos.getData(i).getString("PARA_CODE3", "");  //配置结束时间的月份或天（空值，不修改结束时间）
		    	String strEndUnit = setInfos.getData(i).getString("PARA_CODE4", "0");   //默认空或0，PARA_CODE3按月份计算，1按天计算
		    	String strIsFlag = setInfos.getData(i).getString("PARA_CODE5", "");  	//配置更新套餐标识（空，只针对有效的套餐修改，1 无需判断套餐是否失效）
			
		    	result = bean.updUserDiscntA(iv_userid, iv_product_id, strDiscntCode, strEnableFlag, strEndOffset, strEndUnit, strIsFlag, iv_in_staff_id, iv_in_depart_id);
		    
		    }
		}
		
		
		//REQ201601110005 关于新入网客户下发安心包业务的需求（渠道激活）@author yanwu
        /*String strTradeTypeCode = userInfo.getString("TRADE_TYPE_CODE", "");
        String strModifyCustInfoTag = userInfo.getString("ModifyCustInfo_Tag", "");*/
        
        if (("G001".equals(iv_brand_code) || "G002".equals(iv_brand_code) || "G010".equals(iv_brand_code)))
        {
        	IDataset discnts = UserDiscntInfoQry.queryUserDiscntV(iv_userid, "6633");
        	if( IDataUtil.isNotEmpty(discnts) ){
        		// CRM_SMS_PER_COMM_160113
    	        String templateId1 = "CRM_SMS_PER_COMM_160113";
    	        IData templateInfo1 = bean.getTemplateInfo(templateId1);
    	        String iv_notice_content_2 = bean.getSmsContent(templateInfo1, null);
    	        IData smsInfo1 = new DataMap();
    	        smsInfo1.put("IN_MODE_CODE", "3");
    	        smsInfo1.put("CHAN_ID", "C006");
    	        smsInfo1.put("SMS_KIND_CODE", "08");
    	        smsInfo1.put("DEAL_STATE", "15");
    	        smsInfo1.put("NOTICE_CONTENT", iv_notice_content_2);
    	        smsInfo1.put("RECV_OBJECT", iv_serial_number);
    	        smsInfo1.put("RECV_ID", iv_userid);
    	        SmsSend.insSms(smsInfo1);
        	}else{
        		
        		IDataset newProductElements = ProductInfoQry.getProductElements(iv_product_id, iv_eparchy_code);
                
            	if( IDataUtil.isNotEmpty(newProductElements) ){
            		boolean bTag = false;
            		String iv_packge_id = "";
            		for (int i = 0; i < newProductElements.size(); i++) {
            			
            			IData ProductElement = newProductElements.getData(i);
            			String strElement = ProductElement.getString("ELEMENT_ID", "");
            			if("6633".equals(strElement)){
            				iv_packge_id = ProductElement.getString("PACKAGE_ID", "-1");
            				bTag = true; 
            				break;
            			}
            				
    				}
            		
            		if(bTag){
            			
            			result = bean.ins6633UserDiscnt(iv_userid, iv_product_id, iv_packge_id, iv_in_staff_id, iv_in_depart_id);
            			
            			// CRM_SMS_PER_COMM_160113
            	        String templateId1 = "CRM_SMS_PER_COMM_160113";
            	        IData templateInfo1 = bean.getTemplateInfo(templateId1);
            	        String iv_notice_content_2 = bean.getSmsContent(templateInfo1, null);
            	        IData smsInfo1 = new DataMap();
            	        smsInfo1.put("IN_MODE_CODE", "3");
            	        smsInfo1.put("CHAN_ID", "C006");
            	        smsInfo1.put("SMS_KIND_CODE", "08");
            	        smsInfo1.put("DEAL_STATE", "15");
            	        smsInfo1.put("NOTICE_CONTENT", iv_notice_content_2);
            	        smsInfo1.put("RECV_OBJECT", iv_serial_number);
            	        smsInfo1.put("RECV_ID", iv_userid);
            	        SmsSend.insSms(smsInfo1);
            		}
            		
            	}
            	
        	}
        	
        }
        //REQ201601110005 关于新入网客户下发安心包业务的需求（渠道激活）@author yanwu
        
        //start--REQ201904240016 预开户号码激活后，修改基础套餐及其必选优惠的开始时间@author wangsc10-20190730
        int result2 = 0 ;
        if(!("1".equals(userInfo.getString("IS_REAL_NAME_TAG",""))&&"60".equals(userInfo.getString("TRADE_TYPE_CODE",""))
        		&&("G001".equals(iv_brand_code)||"G002".equals(iv_brand_code)||"G010".equals(iv_brand_code)))
        		||StringUtils.equals("1", realNameSwitch)){
        	IDataset newProductElements = ProductElementsCache.getProductElements(iv_product_id);
            if(IDataUtil.isNotEmpty(newProductElements))
        	{
            	for (int i = 0; i < newProductElements.size(); i++)
        		{
            		IData ProductElement = newProductElements.getData(i);
            		String strElementID = ProductElement.getString("ELEMENT_ID", "");
        			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
        			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
        			String strGroupForceTag = ProductElement.getString("PACKAGE_FORCE_TAG", "");
        			//取主产品下的构成必选优惠，或者主产品下必选组的优惠
    		        if("D".equals(strElementTypeCode) && ("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)))
    				{
    		        	IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(iv_userid);
    		        	if(IDataUtil.isNotEmpty(userDiscnts))
    		        	{
    			        	for (int j = 0; j < userDiscnts.size(); j++) {
    			        		IData userDiscnt = userDiscnts.getData(j);
    			        		String discntCode = userDiscnt.getString("DISCNT_CODE", "");
    			        		if(strElementID.equals(discntCode)){
    			        			result2 = bean.updUserDiscntStartDate(iv_userid, discntCode,SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND));
    			        		}
    						}
    		        	}
    				}
    		        
    		        
    		        
        		}
        	}
            
            bean.updUserProductStartDate(iv_userid, iv_product_id,SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND));//修改主产品的开始时间
            bean.updTfFUserInfochangeStartDate(iv_userid, iv_product_id,SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND));//更新用户重要信息异动的开始时间
            bean.updTiBUserInfochangeStartDate(iv_sync_sequence,iv_userid,iv_product_id);//插入用户重要信息异动给账务
        }

		/****************** REQ202003240010 增加套餐赠送权益领取引导的触点短信 ************************/
		logger.debug("激活触点短信-办理套餐：" + iv_product_id);
		//触点短信
		IDataset proParas = CommparaInfoQry.getCommpara("CSM", "1301", iv_product_id, iv_eparchy_code);
		logger.debug("激活触点短信-配置：" + proParas);
		if(IDataUtil.isNotEmpty(proParas)) {
			String isSend = proParas.getData(0).getString("PARA_CODE6");
			String templateId = proParas.getData(0).getString("PARA_CODE7");
			logger.debug("激活触点短信-发送短信：" + isSend);
			if(StringUtils.equals("1", isSend)) {
				logger.debug("激活触点短信-发送短信：" + isSend);
				IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
				String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
				// 注意：短信模板的占位名需要和iData里的key保持一致
				String regex = "@\\{"+"PRODUCT_NAME"+"\\}";
				smsContent = smsContent.replaceAll(regex, UProductInfoQry.getProductNameByProductId(iv_product_id));
				if(!smsContent.equals("")){
					IData data = new DataMap();
					data.put("RECV_OBJECT", iv_serial_number);
					data.put("NOTICE_CONTENT", smsContent);
					data.put("BRAND_CODE", "");
					data.put("RECV_ID", iv_userid);
					data.put("REMARK", "触点短信");
					SmsSend.insSms(data);
				}
			}
		}
		/****************** REQ202003240010 增加套餐赠送权益领取引导的触点短信 ************************/
        
        //end
		
        /****************** 同步优惠表 ************************/
        if (iv_sync_user_discnt > 0 || result > 0 || result1 > 0 || result2>0)
        {
            bean.insTibDiscnt(iv_sync_sequence, iv_userid);
        }
        
        /**
         * 1、校园套餐新入网用户成功激活号码卡（成功激活号码卡的判断条件为：用户实名制认证激活成功）后，在24小时内将收到咪咕视频尝鲜活动推荐短信：
         * 2、在24小时-48小时内收到咪咕阅读尝鲜活动推荐短信：
         */
        IDataset commparaInfossms = CommparaInfoQry.getCommparaInfoByCode5("CSM", "8705", "SMS", iv_product_id, "", "", iv_eparchy_code);
        if(IDataUtil.isNotEmpty(commparaInfossms))
        {
        	String iv_notice_content_2 = commparaInfossms.getData(0).getString("PARA_CODE20","");  //24小时内
        	String iv_notice_content_4 = commparaInfossms.getData(0).getString("PARA_CODE23","");  //24小时-48小时内
        	
        	IData smsInfo2 = new DataMap();
	        smsInfo2.put("IN_MODE_CODE", "0");
	        smsInfo2.put("CHAN_ID", "C006");
	        smsInfo2.put("SMS_KIND_CODE", "08");
	        smsInfo2.put("DEAL_STATE", "15");
	        smsInfo2.put("NOTICE_CONTENT", iv_notice_content_2);
	        smsInfo2.put("RECV_OBJECT", iv_serial_number);
	        smsInfo2.put("RECV_ID", iv_userid);
	        smsInfo2.put("FORCE_START_TIME", SysDateMgr.getSysTime());
	        SmsSend.insSms(smsInfo2);
	        
	        IData smsInfo4 = new DataMap();
	        smsInfo4.put("IN_MODE_CODE", "0");
	        smsInfo4.put("CHAN_ID", "C006");
	        smsInfo4.put("SMS_KIND_CODE", "08");
	        smsInfo4.put("DEAL_STATE", "15");
	        smsInfo4.put("NOTICE_CONTENT", iv_notice_content_4);
	        smsInfo4.put("RECV_OBJECT", iv_serial_number);
	        smsInfo4.put("RECV_ID", iv_userid);
	        smsInfo4.put("FORCE_START_TIME", SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 24));
	        SmsSend.insSms(smsInfo4);
        }
        
        /************************* REQ201910220009 关于向校园营销活动期间开户校园卡客户发送防诈提醒短信的需求 by wuwangfeng *************************/
        logger.debug("commparaInfos23------------------->iv_product_id="+iv_product_id+", iv_serial_number="+iv_serial_number);
        IDataset commparaInfos23 = CommparaInfoQry.getCommparaByCode23("CSM", "1910", "0", iv_product_id);
        logger.debug("commparaInfos23------------------->commparaInfos23="+commparaInfos23);
        if(IDataUtil.isNotEmpty(commparaInfos23))
        {
        	String paraCode23 = "";
	        for (int i = 0; i < commparaInfos23.size(); i++) {
	        	//获取短信内容
	        	paraCode23 = commparaInfos23.getData(i).getString("PARA_CODE23");
	        	logger.debug("paraCode23---------短信内容---------->"+paraCode23);
	        	
	        	//发送短信
	        	IData smsData = new DataMap();
	        	smsData.clear();
        	
                smsData.put("TRADE_ID", SeqMgr.getTradeId());
                smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                smsData.put("SMS_PRIORITY", "5000");
                smsData.put("NOTICE_CONTENT_TYPE", "0");
                smsData.put("SMS_TYPE_CODE", "I0");
	        	smsData.put("RECV_ID", iv_userid);	        	
	        	smsData.put("REMARK", "关于向校园营销活动期间开户校园卡客户发送防诈提醒短信");
	        	
	        	smsData.put("RECV_OBJECT", iv_serial_number);
	        	smsData.put("NOTICE_CONTENT", paraCode23);	        	
	        	SmsSend.insSms(smsData);
			}
        }
        
        /****************** REQ201211120015 关于开发对新入网用户系统自动下发透明消费服务提示短信的功能 ************************/
        // CRM_SMS_PER_COMM_2317 620
        String templateId1 = "CRM_SMS_PER_FC_2317";
        IData templateInfo1 = bean.getTemplateInfo(templateId1);
        String iv_notice_content_2 = bean.getSmsContent(templateInfo1, null);
        IData smsInfo1 = new DataMap();
        smsInfo1.put("IN_MODE_CODE", "3");
        smsInfo1.put("CHAN_ID", "C006");
        smsInfo1.put("SMS_KIND_CODE", "08");
        smsInfo1.put("DEAL_STATE", "15");
        smsInfo1.put("NOTICE_CONTENT", iv_notice_content_2);
        smsInfo1.put("RECV_OBJECT", iv_serial_number);
        smsInfo1.put("RECV_ID", iv_userid);
        SmsSend.insSms(smsInfo1);
        
        /****************** 2015年新春入网预存有礼活动开发需求************************/
        // CRM_SMS_PER_COMM_2317 620
        String templateId5 = "CRM_SMS_PER_FC_2327";
        IData templateInfo5 = bean.getTemplateInfo(templateId5);
        if(templateInfo5 !=null &&!templateInfo5.isEmpty()){
	        String iv_notice_content_5 = bean.getSmsContent(templateInfo5, null);
	        IData smsInfo5 = new DataMap();
	        smsInfo5.put("IN_MODE_CODE", "3");
	        smsInfo5.put("CHAN_ID", "C006");
	        smsInfo5.put("SMS_KIND_CODE", "08");
	        smsInfo5.put("DEAL_STATE", "15");
	        smsInfo5.put("NOTICE_CONTENT", iv_notice_content_5);
	        smsInfo5.put("RECV_OBJECT", iv_serial_number);
	        smsInfo5.put("RECV_ID", iv_userid);
	        SmsSend.insSms(smsInfo5);
        }

        /****************** 买断开户激活密码管理短信提示 ************************/
        // CRM_SMS_PER_COMM_2318 716
        IData paramSms2 = new DataMap();
        String iv_codename = UBrandInfoQry.getBrandNameByBrandCode(iv_brand_code);
        String templateId2 = "CRM_SMS_PER_FC_2318";
        paramSms2.put("CODE_NAME", iv_codename);
        paramSms2.put("USER_PASSWD", iv_user_passwd);
        IData templateInfo2 = bean.getTemplateInfo(templateId2);
        iv_notice_content = bean.getSmsContent(templateInfo2, paramSms2);
        IData smsInfo2 = new DataMap();
        smsInfo2.put("IN_MODE_CODE", "3");
        smsInfo2.put("CHAN_ID", "C006");
        smsInfo2.put("SMS_KIND_CODE", "08");
        smsInfo2.put("DEAL_STATE", "15");
        smsInfo2.put("NOTICE_CONTENT", iv_notice_content);
        smsInfo2.put("RECV_OBJECT", iv_serial_number);
        smsInfo2.put("RECV_ID", iv_userid);
        SmsSend.insSms(smsInfo2);

        /****************** 飞信、139邮箱、彩云业务激活短信 ************************/
        // CRM_SMS_PER_COMM_2319 760
        IDataset paltIICInfos = UserPlatInfoQry.getUserPlatByUserIdCodes(iv_userid, "IIC", "A");
        IDataset paltMailInfos = UserPlatInfoQry.getUserPlatByUserIdCodes(iv_userid, "'+MAILMF", "A");
        IDataset paltYdcyInfos = UserPlatInfoQry.getUserPlatByUserIdCodes(iv_userid, "YDCY", "A");
        if (IDataUtil.isNotEmpty(paltIICInfos))
        {
            iv_count0 = paltIICInfos.size();
        }
        if (IDataUtil.isNotEmpty(paltMailInfos))
        {
            iv_count0 += paltMailInfos.size();
        }
        if (IDataUtil.isNotEmpty(paltYdcyInfos))
        {
            iv_count0 += paltYdcyInfos.size();
        }
        if (iv_count0 > 3)
        {
            String templateId3 = "CRM_SMS_PER_FC_2319";
            IData templateInfo3 = bean.getTemplateInfo(templateId3);
            iv_notice_content = bean.getSmsContent(templateInfo3, null);
            IData smsInfo3 = new DataMap();
            smsInfo3.put("IN_MODE_CODE", "3");
            smsInfo3.put("CHAN_ID", "C006");
            smsInfo3.put("SMS_KIND_CODE", "08");
            smsInfo3.put("DEAL_STATE", "15");
            smsInfo3.put("NOTICE_CONTENT", iv_notice_content_2);
            smsInfo3.put("RECV_OBJECT", iv_serial_number);
            smsInfo3.put("RECV_ID", iv_userid);
            SmsSend.insSms(smsInfo3);
        }

        /****************** 办理星火计划下发告知短信(REQ201301220014) ************************/
        IDataset userSaleInfos = UserSaleActiveInfoQry.querySaleInfoByUserId(iv_userid);
        for (int i = 0; i < userSaleInfos.size(); i++)
        {
            IData userSaleInfo = userSaleInfos.getData(i);
            String para22 = userSaleInfo.getString("PARA_CODE22", "");
            String para23 = userSaleInfo.getString("PARA_CODE23", "");
            if (StringUtils.isEmpty(para22) && StringUtils.isEmpty(para23))
            {

            }
            else
            {
                iv_notice_content = para22 + para23;
                iv_notice_content = iv_notice_content.replace("%PACKAGE_NAME!", userSaleInfo.getString("PACKAGE_NAME"));
                String activeYearMonthDay = SysDateMgr.getChinaDate(userSaleInfo.getString("ACTIVE_START_DATE"), SysDateMgr.PATTERN_CHINA_DATE);
                iv_notice_content = iv_notice_content.replace("%START_DATE!", activeYearMonthDay);
                String openYearMonthDay = SysDateMgr.getChinaDate(SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_CHINA_DATE);
                iv_notice_content = iv_notice_content.replace("%ACTIVE_DATE!", openYearMonthDay);
                IData smsInfo4 = new DataMap();
                smsInfo4.put("IN_MODE_CODE", "3");
                smsInfo4.put("CHAN_ID", "C006");
                smsInfo4.put("SMS_KIND_CODE", "08");
                smsInfo4.put("DEAL_STATE", "15");
                smsInfo4.put("REMARK", "firstcalldeal生成");
                smsInfo4.put("NOTICE_CONTENT", iv_notice_content);
                smsInfo4.put("RECV_OBJECT", iv_serial_number);
                smsInfo4.put("RECV_ID", iv_userid);
                SmsSend.insSms(smsInfo4);
            }

        }

        /****************** HNYD-REQ-20110729-002关于对新入网客户发送短信的需求 ************************/
        String IV_OTHER_EXPLAIN = "";
        String IV_PRODUCT_EXPLAIN = "";
        String iv_package_id_AF = "0";
        String iv_discnt_code_AF = "0";
        if ("18".equals(iv_net_type_code))
        {
            IData productInfo = UProductInfoQry.qryProductByPK(iv_product_id);
            IV_PRODUCT_EXPLAIN = productInfo.getString("PRODUCT_EXPLAIN");
            IDataset elementInfos = UserSvcInfoQry.getElementInfoByUserIdProductId(iv_userid, iv_product_id);
            if (IDataUtil.isNotEmpty(elementInfos))
            {
                for (int i = 0; i < elementInfos.size(); i++)
                {
                    IData recaTD = elementInfos.getData(i);
                    String RSRV_STR5 = recaTD.getString("RSRV_STR5", "");
                    if (StringUtils.isNotEmpty(RSRV_STR5))
                    {
                        iv_rsrv_str5 = "," + RSRV_STR5;
                    }
                    if ("1".equals(recaTD.getString("ELEMENT_STATE")))
                    {
                        iv_count5 += 1;
                        StringBuilder sb = new StringBuilder();
                        sb.append(iv_count5);
                        sb.append("、");
                        sb.append(recaTD.getString("ELEMENT_NAME"));
                        sb.append(iv_rsrv_str5);
                        sb.append(";");
                        iv_notice_content_TD1 = sb.toString();
                    }
                    if ("0".equals(recaTD.getString("ELEMENT_STATE")))
                    {
                        iv_count6 += 1;
                        StringBuilder sb = new StringBuilder();
                        sb.append(iv_count6);
                        sb.append("、");
                        sb.append(recaTD.getString("ELEMENT_NAME"));
                        sb.append(iv_rsrv_str5);
                        sb.append(";");
                        iv_notice_content_TD2 = sb.toString();
                    }
                }
                if (StringUtils.isNotEmpty(iv_notice_content_TD2))
                {
                    iv_notice_content_TD2 = iv_notice_content_TD2.substring(0, iv_notice_content_TD2.length() - 1);
                    iv_notice_content_TD2 = "除本套餐业务外，您还可订购以下业务：" + iv_notice_content_TD2;
                    iv_notice_content_TD2 += "。";
                }
                if (StringUtils.isNotEmpty(iv_notice_content_TD1))
                {
                    iv_notice_content_TD2 = "";
                    iv_notice_content_TD1 = iv_notice_content_TD1.substring(0, iv_notice_content_TD1.length() - 1);
                    iv_notice_content_TD1 = "除套餐内包含的业务外，您还订购了以下业务：" + iv_notice_content_TD1;
                    iv_notice_content_TD1 += "。";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("尊敬的客户，您好！欢迎您使用");
                sb.append(iv_codename);// 931
                sb.append("业务，您已订购");
                sb.append(IV_PRODUCT_EXPLAIN);
                sb.append("。").append(iv_notice_content_TD1).append(iv_notice_content_TD2);
                sb.append("详情咨询10050。");

                iv_notice_content_temp = sb.toString();

            }
        }
        else
        {
            IDataset productPkgInfos = ProductPkgInfoQry.querySpecByProductId(iv_product_id);
            iv_count3 = productPkgInfos.size();
            if (iv_count3 == 1)
            {
                iv_package_id_AF = productPkgInfos.getData(0).getString("PACKAGE_ID");

            }
            if (!"0".equals(iv_package_id_AF))
            {
                IDataset userDiscntInfos = UserDiscntInfoQry.getUserProdDisByUserIdProdIdPkgIdDisIdStartDate(iv_userid, iv_product_id, iv_package_id_AF, null, null);
                iv_discnt_code_AF = userDiscntInfos.getData(0).getString("DISCNT_CODE");
            }
            if ("10001139".equals(iv_product_id))
            {
                IData discntInfos = DiscntInfoQry.getDiscntInfoByCode2(iv_discnt_code_AF);
                IV_PRODUCT_EXPLAIN = discntInfos.getString("DISCNT_EXPLAIN");
            }
            else
            {
                IData productInfos = UProductInfoQry.qryProductByPK(iv_product_id);
                IV_PRODUCT_EXPLAIN = productInfos.getString("PRODUCT_EXPLAIN");
            }
            IV_i = 1;
            //start-wangsc10-20190403-BUG20190226160500 +产品变更短信提醒内容优化
            //IDataset svcinfos = UserPlatSvcInfoQry.querySvcByUserIdStr9(iv_userid, "1");
            IDataset svcinfos = UserPlatSvcInfoQry.querySvcByUserIdServiceId(iv_userid, "1");
            //end-wangsc10-20190403-BUG20190226160500 +产品变更短信提醒内容优化
            IDataset svcinfosT = UserPlatSvcInfoQry.querySvcByUserIdTag3(iv_userid);
            IDataset svcinfosca = new DatasetList();
            svcinfos.addAll(svcinfosT);
            for (int i = 0; i < svcinfos.size(); i++)
            {
                IData svcinfo = svcinfos.getData(i);
                String serviceId = svcinfo.getString("SERVICE_ID");
                boolean check1 = RuleCfgInfoQry.hasInfosForNoQry(serviceId);// 不是不可查询服务
                if (check1)
                {
                    continue;
                }
                boolean check2 = RuleCfgInfoQry.hasInfosForNoPkg(serviceId, iv_product_id, "0898");// 不是套餐的基本服务
                if (check2)
                {
                    continue;
                }
                boolean check3 = RuleCfgInfoQry.hasInfosForNoBrand(iv_brand_code, serviceId, "0898");// 不是品牌基本服务
                if (check3)
                {
                    continue;
                }
                boolean check4 = RuleCfgInfoQry.hasInfosForNoDiscnt(serviceId, iv_userid, "0898");// 不是优惠的基本服务
                if (check4)
                {
                    continue;
                }
                svcinfosca.add(svcinfo);
            }

            for (int i = 0; i < svcinfosca.size(); i++)
            {
                IData reca = svcinfosca.getData(i);
                IV_getprice = reca.getString("PRICE", "0");
                IV_getbillflg = reca.getString("BILLFLG", "2");
                if (null == IV_getprice || "".equals(IV_getprice) || "0".equals(IV_getprice))
                {
                    IV_getbillflg = "0";
                    IV_getprice = "";
                }
                if (StringUtils.isEmpty(IV_getbillflg))
                {
                    IV_getbillflg = "2";
                }
                if ("0".equals(IV_getbillflg))
                {
                    IV_getunit = "免费";
                }
                else if ("1".equals(IV_getbillflg))
                {
                    IV_getunit = "元/条";
                }
                else if ("2".equals(IV_getbillflg))
                {
                    IV_getunit = "元/月";
                }
                else if ("3".equals(IV_getbillflg))
                {
                    IV_getunit = "元/小时";
                }
                else if ("4".equals(IV_getbillflg))
                {
                    IV_getunit = "元/次";
                }
                else if ("7".equals(IV_getbillflg))
                {
                    IV_getunit = "元/天";
                }
                else if ("9".equals(IV_getbillflg))
                {
                    IV_getunit = "元/月";
                }
                else if ("11".equals(IV_getbillflg))
                {
                    IV_getunit = "元/时长";
                }
                else if ("12".equals(IV_getbillflg))
                {
                    IV_getunit = "元/周";
                }

                if ((IV_OTHER_EXPLAIN.length() + reca.getString("BIZ_NAME", "").length()) < 900)
                {
                    StringBuilder sb = new StringBuilder();
                    byte[] bytes =
                    { 0x10 };
                    String seprator = new String(bytes);
                    sb.append(IV_OTHER_EXPLAIN).append(IV_i).append(reca.getString("BIZ_NAME")).append("，");
                    sb.append(IV_getprice).append(IV_getunit).append("；");
                    IV_OTHER_EXPLAIN = sb.toString();
                }
                else
                {
                    IV_OTHER_EXPLAIN += "等；";
                    continue;
                }

                IV_i++;

            }
            if (IV_i > 1)
            {
                StringBuilder sb = new StringBuilder();
                byte[] bytes =
                { 0x10 };
                String seprator = new String(bytes);
                sb.append("中国移动业务：").append(IV_OTHER_EXPLAIN);
                IV_OTHER_EXPLAIN = sb.toString();
            }
            IV_j = 1;
            //start-wangsc10-20190403-BUG20190226160500 +产品变更短信提醒内容优化
            //IDataset svcinfos2 = UserPlatSvcInfoQry.querySvcByUserIdStr9(iv_userid, "2");
            IDataset svcinfos2 = UserPlatSvcInfoQry.querySvcByUserIdServiceId(iv_userid, "2");
            //end-wangsc10-20190403-BUG20190226160500 +产品变更短信提醒内容优化
            IDataset svcinfosca2 = new DatasetList();
            for (int i = 0; i < svcinfos2.size(); i++)
            {
                IData svcinfo = svcinfos2.getData(i);
                String serviceId = svcinfo.getString("SERVICE_ID");
                boolean check1 = RuleCfgInfoQry.hasInfosForNoQry(serviceId);// 不是不可查询服务
                if (check1)
                {
                    continue;
                }
                boolean check2 = RuleCfgInfoQry.hasInfosForNoPkg(serviceId, iv_product_id, "0898");// 不是套餐的基本服务
                if (check2)
                {
                    continue;
                }
                boolean check3 = RuleCfgInfoQry.hasInfosForNoBrand(iv_brand_code, serviceId, "0898");// 不是品牌基本服务
                if (check3)
                {
                    continue;
                }
                boolean check4 = RuleCfgInfoQry.hasInfosForNoDiscnt(serviceId, iv_userid, "0898");// 不是优惠的基本服务
                if (check4)
                {
                    continue;
                }
                svcinfosca2.add(svcinfo);
            }
            for (int i = 0; i < svcinfosca2.size(); i++)
            {
                if (1 == IV_j)
                {
                    byte[] bytes =
                    { 0x10 };
                    String seprator = new String(bytes);
                    IV_OTHER_EXPLAIN += "由中国移动代收费的业务：";
                    IV_OTHER_EXPLAIN += seprator;
                }
                IData reca = svcinfosca2.getData(i);
                IV_getprice = reca.getString("PRICE", "0");
                IV_getbillflg = reca.getString("BILLFLG", "2");
                if (null == IV_getprice || "".equals(IV_getprice) || "0".equals(IV_getprice))
                {
                    IV_getbillflg = "0";
                    IV_getprice = "";
                }
                if (StringUtils.isEmpty(IV_getbillflg))
                {
                    IV_getbillflg = "2";
                }
                if ("0".equals(IV_getbillflg))
                {
                    IV_getunit = "免费";
                }
                else if ("1".equals(IV_getbillflg))
                {
                    IV_getunit = "元/条";
                }
                else if ("2".equals(IV_getbillflg))
                {
                    IV_getunit = "元/月";
                }
                else if ("3".equals(IV_getbillflg))
                {
                    IV_getunit = "元/小时";
                }
                else if ("4".equals(IV_getbillflg))
                {
                    IV_getunit = "元/次";
                }
                else if ("7".equals(IV_getbillflg))
                {
                    IV_getunit = "元/天";
                }
                else if ("9".equals(IV_getbillflg))
                {
                    IV_getunit = "元/月";
                }
                else if ("11".equals(IV_getbillflg))
                {
                    IV_getunit = "元/时长";
                }
                else if ("12".equals(IV_getbillflg))
                {
                    IV_getunit = "元/周";
                }
                if ((IV_OTHER_EXPLAIN.length() + reca.getString("BIZ_NAME", "").length() + reca.getString("SP_NAME", "").length()) < 900)
                {
                    StringBuilder sb = new StringBuilder();
                    byte[] bytes =
                    { 0x10 };
                    String seprator = new String(bytes);
                    sb.append(IV_OTHER_EXPLAIN).append(IV_i).append(reca.getString("BIZ_NAME")).append("（");
                    sb.append(reca.getString("SP_NAME")).append("提供)，");
                    sb.append(IV_getprice).append(IV_getunit).append("；");
                    IV_OTHER_EXPLAIN = sb.toString();
                }
                else
                {
                    IV_OTHER_EXPLAIN += "等；";
                    continue;
                }

                IV_i++;
                IV_j++;
            }
            if (IV_i > 1)
            {
                StringBuilder sb = new StringBuilder();
                byte[] bytes =
                { 0x10 };
                String seprator = new String(bytes);
                sb.append("除套餐内包含的业务外，您还订购了以下业务：").append(IV_OTHER_EXPLAIN);
                IV_OTHER_EXPLAIN = sb.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("尊敬的客户，您好！欢迎您加入中国移动").append(iv_codename);// 1279
            sb.append("。您订购的套餐为").append(IV_PRODUCT_EXPLAIN).append(IV_OTHER_EXPLAIN).append("详情咨询10086。中国移动");
            iv_notice_content_temp = sb.toString();
        }
        if(StringUtils.isNotEmpty(iv_notice_content_temp)){
	        iv_count4 = iv_notice_content_temp.length() / 500 + 1;
	        for (int IV_k = 1; IV_k <= iv_count4; IV_k++)
	        {
	            int beginIndex = IV_k * 500 - 500;
	            int noticeLength = iv_notice_content_temp.length();
	            int endIndex = (IV_k * 500) > noticeLength ? noticeLength : (IV_k * 500);
	            iv_notice_content_new = iv_notice_content_temp.substring(beginIndex, endIndex);
	            if (iv_count4 > 1)
	            {
	                iv_notice_content_new = "[页" + IV_k + "]" + iv_notice_content_new;
	            }
	            IData smsInfo5 = new DataMap();
	            smsInfo5.put("IN_MODE_CODE", "3");
	            smsInfo5.put("CHAN_ID", "C006");
	            smsInfo5.put("SMS_KIND_CODE", "08");
	            smsInfo5.put("DEAL_STATE", "15");
	            smsInfo5.put("NOTICE_CONTENT", iv_notice_content_new);
	            smsInfo5.put("RECV_OBJECT", iv_serial_number);
	            smsInfo5.put("RECV_ID", iv_userid);
	            SmsSend.insSms(smsInfo5);
	        }
        }
        
        /****************** 首次激活开通手机邮箱增加校验修改 ************************/
        IDataset userPlatSvcInfos = UserPlatSvcInfoQry.querySvcByUserId(iv_userid);
        if (("G001".equals(iv_brand_code) || "G002".equals(iv_brand_code) || "G010".equals(iv_brand_code)) && IDataUtil.isEmpty(userPlatSvcInfos))
        {
            IData platSvcInfo = new DataMap();
            platSvcInfo.put("SERIAL_NUMBER", iv_serial_number);
            platSvcInfo.put("SERVICE_ID", "99817947");
            platSvcInfo.put("OPER_CODE", "06");
            platSvcInfo.put("OPR_SOURCE", "11");
            platSvcInfo.put("UDSUM", "0");
            platSvcInfo.put("TRADE_TYPE_CODE", "3700");
            platSvcInfo.put("ORDER_TYPE_CODE", "3700");
            CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", platSvcInfo);
        }

        /****************** REQ201210220022 139邮箱和电子账单服务升级 ************************/
        if (("G001".equals(iv_brand_code) || "G002".equals(iv_brand_code) || "G010".equals(iv_brand_code)) && "2".equals(iv_acct_tag))
        {
            IDataset userSvcInfos = UserSvcInfoQry.queryUserSvcByUseridSvcid(iv_userid, "91310107");
            if (IDataUtil.isEmpty(userSvcInfos))
            {
                String iv_package_id_139 = "-1";
                IDataset packageInfos = ProductPkgInfoQry.queryUserPackage(iv_product_id, "91310107", "S");

                if (IDataUtil.isNotEmpty(packageInfos) && packageInfos.size() == 1)
                {
                    iv_package_id_139 = packageInfos.getData(0).getString("PACKAGE_ID");
                    IDataset userExpandInfos = UserExpandInfoQry.getUserExpandByUserIdUpdType(iv_userid, "CHANGE_01");

                    if (IDataUtil.isEmpty(userExpandInfos))
                    {
                        long userIdN = Long.parseLong(iv_userid);
                        long partitionId = userIdN % 10000;
                        IData expandInfo = new DataMap();
                        expandInfo.put("USER_ID", iv_userid);
                        expandInfo.put("PARTITION_ID", String.valueOf(partitionId));
                        expandInfo.put("UPDATE_TYPE", "CHANGE_01");
                        expandInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                        expandInfo.put("UPDATE_STAFF_ID", "SUPERUSR");
                        expandInfo.put("UPDATE_DEPART_ID", "36601");
                        expandInfo.put("REMARK", "p_csm_firstcalldeal:新增用户资料最后更新时间");
                        bean.insUserExpand(expandInfo);
                    }
                    else
                    {
                        bean.updateUserExpandTime(iv_userid, "CHANGE_01", "p_csm_firstcalldeal:修改用户资料最后更新时间");
                    }

                    IData svcInfo = new DataMap();
                    svcInfo.put("TRADE_TYPE_CODE", "120");
                    svcInfo.put("ORDER_TYPE_CODE", "120");
                    svcInfo.put("USER_ID", iv_userid);
                    svcInfo.put("PRODUCT_ID", iv_product_id);
                    svcInfo.put("PACKAGE_ID", iv_package_id_139);
                    svcInfo.put("ELEMENT_ID", "91310107");
                    svcInfo.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
                    svcInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    svcInfo.put("UPDATE_STAFF_ID", "SUPERUSR");
                    svcInfo.put("UPDATE_DEPART_ID", "36601");
                    svcInfo.put("SERIAL_NUMBER", iv_serial_number);

                    CSAppCall.call("SS.OtnMailRegSVC.tradeReg", svcInfo);

                }

            }
        }

        /****************** 批量预开户首次通话激活时默认为全球通畅听套餐（超值版）用户开通新闻早晚报平台业务 ************************/
        if ("G001".equals(iv_brand_code))
        {
            IDataset userBindDiscnts = UserDiscntInfoQry.queryFirstCallBindDiscnts(iv_userid, "909", "10", "1");
            if (IDataUtil.isNotEmpty(userBindDiscnts) && IDataUtil.isNotEmpty(userBindDiscnts.getData(0)))
            {
                iv_count0 = userBindDiscnts.getData(0).getInt("IV_COUNT0");
                String iv_sp_service_id = userBindDiscnts.getData(0).getString("SP_SERVICE_ID");
                if (iv_count0 > 0 && StringUtils.isNotEmpty(iv_sp_service_id))
                {
                    IData platSvcInfo3 = new DataMap();
                    platSvcInfo3.put("SERIAL_NUMBER", iv_serial_number);
                    platSvcInfo3.put("SERVICE_ID", iv_sp_service_id);
                    platSvcInfo3.put("OPER_CODE", "06");
                    platSvcInfo3.put("OPR_SOURCE", "11");
                    platSvcInfo3.put("UDSUM", "0");
                    platSvcInfo3.put("TRADE_TYPE_CODE", "3700");
                    platSvcInfo3.put("ORDER_TYPE_CODE", "3700");
                    CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", platSvcInfo3);
                }
            }
        }

        /****************** 物联网新增 ************************/

        if ("PWLW".equals(iv_brand_code))
        {
            //scthtradepwlwDeal(iv_userid, iv_cust_id, iv_serial_number, iv_eparchy_code, iv_city_code);
            
            IData userdata = new DataMap(); 
            userdata.put("USER_ID", iv_userid);
            userdata.put("REMOVE_TAG", "0");
            IDataset users = UserInfoQry.getTradeUserInfoByUserIdAndTag(userdata);
            if(IDataUtil.isNotEmpty(users)){
            	IData data = users.getData(0);
            	data.put("ACCT_TAG", "0");
        		data.put("USER_TYPE_CODE", "0");
        		data.put("REMARK", "首话单激活物联网优惠");
        		data.put("FIRST_CALL_TIME", SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND));
        		data.put("OPEN_MODE", "0");
//    	        Dao.update("TF_F_USER", data, new String[]
//    	        { "USER_ID", "PARTITION_ID" });
    	      //同步用户表
    	        bean.insTibUser(iv_sync_sequence, iv_userid);
            }
            
            // @Modify yanwu bengin REQ201403090003 关于下发物联网专网专号业务支撑改造要求的通知
            IDataset commparaInfos = CommparaInfoQry.getOnlyByAttr("CSM", "1551", iv_eparchy_code);
            if (IDataUtil.isEmpty(commparaInfos)){
            	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "物理网测试产品无配置，TD_S_COMMPARA_1551");
            	IDataset userDiss = UserDiscntInfoQry.getVirUserDiscnts(iv_userid, iv_product_id);
            	if(IDataUtil.isNotEmpty(userDiss)){
            		for (int i = 0; i < userDiss.size(); i++) {
            			IData userDis = userDiss.getData(i);
            			String instId = userDis.getString("INST_ID");
	                	String strStartDate = userDis.getString("START_DATE", "");
	                	String strSysTime = SysDateMgr.getSysTime();
	                	if( strStartDate.compareToIgnoreCase(strSysTime) > 0 ){
	                		IData data = userDis;
	                		data.put("END_DATE", strSysTime);
	                		data.put("REMARK", "首话单激活物联网优惠");
		        	        
		        	        Dao.update("TF_F_USER_DISCNT", data, new String[]
		        	        { "INST_ID", "PARTITION_ID" });
		        	        //同步优惠表
		        	        bean.insTibDiscnt_instId(iv_sync_sequence, iv_userid, instId);
	                	}
            		}
            	}
            }else{
	            IDataset userDiss = UserDiscntInfoQry.getVirUserDiscnts(iv_userid, iv_product_id);
	            if(IDataUtil.isNotEmpty(userDiss)){
	            	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网用户没有主产品优惠，请核实数据！");
		            for (int i = 0; i < userDiss.size(); i++) {
		            	IData userDis = userDiss.getData(i);
		            	boolean isModfy = true;
		                for (int j = 0; j < commparaInfos.size(); j++){
		                    String paraCode = commparaInfos.getData(j).getString("PARAM_CODE", "");
		                    String strElementId = userDis.getString("DISCNT_CODE", "");
		                    if ( strElementId.equals(paraCode) ){
		                    	isModfy = false;
		                        break;
		                    }
		                }
		                if (isModfy){
		                	String instId = userDis.getString("INST_ID");
		                	String strStartDate = userDis.getString("START_DATE", "");
		                	String strSysTime = SysDateMgr.getSysTime();
		                	if( strStartDate.compareToIgnoreCase(strSysTime) > 0 ){
		                		IData data = userDis;
		                		data.put("START_DATE", strSysTime);
		                		data.put("REMARK", "首话单激活物联网优惠");
			        	        
			        	        Dao.update("TF_F_USER_DISCNT", data, new String[]
			        	        { "INST_ID", "PARTITION_ID" });
			        	        //同步优惠表
			        	        bean.insTibDiscnt_instId(iv_sync_sequence, iv_userid, instId);
		                	}
		                }else{
		                	String instId = userDis.getString("INST_ID");
		            		String strSysTime = SysDateMgr.getSysTime();
		            		IData data = userDis;
		            		data.put("END_DATE", strSysTime);
		            		data.put("REMARK", "首话单激活物联网优惠");
		        	        
		        	        Dao.update("TF_F_USER_DISCNT", data, new String[]
		        	        { "INST_ID", "PARTITION_ID" });	
		        	        //同步优惠表
		        	        bean.insTibDiscnt_instId(iv_sync_sequence, iv_userid, instId);
		                }
		            }
	            }
            }
            // @Modify yanwu end REQ201403090003 关于下发物联网专网专号业务支撑改造要求的通知
            
            IData input = new DataMap();
            input.put("SERIAL_NUMBER", iv_serial_number);
            CSAppCall.call("SS.SilenceTransNormalSVC.tradeReg", input );
            
        }
        
        
        
    	/****************** 无线固话批量预开户话单激活 ****************/
		System.out.println("iv_serial_number===c==="+iv_serial_number);
		System.out.println("iv_brand_code===c==="+iv_brand_code);
		if ("TDYD".equals(iv_brand_code)) {
			IDataset dataSet = ResCall.getMphonecodeInfo(iv_serial_number);
	        if (IDataUtil.isNotEmpty(dataSet)){
	        	IData mphonecodeInfo = dataSet.first();
	        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
	        	if (StringUtils.equals("1", beautifulTag)){
	        		String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
	        		if(StringUtils.isBlank(productId)){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+iv_brand_code+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
	        		}
	        		String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
	        		if(StringUtils.isBlank(packageId)){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+iv_brand_code+"需绑定的营销包编码为空，请检查资源侧配置！");
	        		}
	        		
	        	    IData saleactiveData = new DataMap();
	                saleactiveData.put("SERIAL_NUMBER",iv_serial_number);
	                saleactiveData.put("PRODUCT_ID",productId);
	                saleactiveData.put("PACKAGE_ID", packageId);
	                CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
	        	}
	        	
	        }
		}
        /****************** 无线固话批量预开户话单激活 ****************/
        
        
        
        
        /****************** 关于一级能力开放平台新增移动花卡的补充通知 @Modify by tanzheng start************************/

		IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2578,"ZZZZ");
		for(Object temp : comparas){
				IData tempData = (IData) temp;
				if(iv_product_id.equals(tempData.getString("PARA_CODE1"))){
					//如果主套餐为配置的套餐，则调用能开接口
					String Abilityurl = "";
					IData param1 = new DataMap();
				    param1.put("PARAM_NAME", "crm.ABILITY.CIP85");
				    StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
					IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
					if (Abilityurls != null && Abilityurls.size() > 0)
					{
						Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
					}
					else
					{
						CSAppException.appError("-1", "crm.ABILITY.CIP85接口地址未在TD_S_BIZENV表中配置");
					}
					String apiAddress = Abilityurl;
					IData param = new DataMap();

					IData data = new DataMap();
                    data.put("SERIAL_NUMBER", iv_serial_number);
                    IDataset postinfo = Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_FLOWER_ACTIVE_BY_SN", data, Route.getCrmDefaultDb());

                    String orderNum = "";
                    String subOrderNum = "";
                    if (IDataUtil.isNotEmpty(postinfo)) {
                    	orderNum = postinfo.getData(0).getString("ORDER_NO"); // 一级能开子订单编码，示例：SC01204T18101000000010-01
                        /*int end = subOrderNum.indexOf("-") != -1 ? subOrderNum.indexOf("-") : subOrderNum.length();
                        orderNum = StringUtils.substring(subOrderNum, 0, end); // 一级能开订单编码，示例：SC01204T18101000000010
                    */}

					param.put("number",iv_serial_number);//号卡号码
					param.put("numberActivateTime",SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND_SHORT));//号卡激活时间
					param.put("planId",tempData.getString("PARA_CODE4"));//主套餐编码,转换为一级能开产品ID
					param.put("planName",userMainProductInfo.getString("PRODUCT_NAME"));//号码主套餐
					String startDate =  userMainProductInfo.getString("START_DATE","");
					param.put("startTime",startDate.replaceAll("-","").replaceAll(":","").replaceAll(" ",""));//套餐生效时间
 					param.put("reserve1",orderNum);

					logger.debug("调用能开参数："+param.toString());
					IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,param);
					String resCode=stopResult.getString("resCode");
            		IData out=stopResult.getData("result");
                	String X_RSPCODE="";
                	String X_RSPDESC="";
            		X_RSPCODE=out.getString("bizCode");
            		X_RSPDESC=out.getString("bizDesc");
            		if(!"00000".equals(resCode)){
            			logger.error("调用能开参数："+param.toString());
            			logger.error("调用能开返回结果："+stopResult.toString());
            			CSAppException.appError("-1", "同步能力开发平台出错" + stopResult.getString("resMsg"));
            		}
            		if(!"0000".equals(X_RSPCODE))
            		{   
            			logger.error("调用能开参数："+param.toString());
            			logger.error("调用能开返回结果："+stopResult.toString());
            			CSAppException.appError("-1", "同步能力开发平台出错" + X_RSPDESC);

					logger.debug("调用能开返回结果："+stopResult.toString());
            		String tradeId = SeqMgr.getTradeId();
            		String orderId = SeqMgr.getOrderId();
                    IData insertData = new DataMap();
                    insertData.put("TRADE_ID", tradeId);
                    insertData.put("ORDER_ID", orderId);
                    insertData.put("SERIAL_NUMBER", iv_serial_number); // 用户激活手机号码
                    insertData.put("ACTIVATE_TIME", SysDateMgr.date2String(iv_opendate, SysDateMgr.PATTERN_STAND)); // 号卡激活时间
                    insertData.put("PRODUCT_ID", iv_product_id); // 号码主套餐编码(一级能开产品ID)
                    insertData.put("PRODUCT_NAME", param.getString("planName")); // 号码主套餐名称
                    insertData.put("START_DATE", startDate); // 套餐生效时间
                    insertData.put("STATE", "-1"); // -1-发送成功，0-未发送，1-发送失败一次，2-发送失败二次，3-发送失败三次
                    insertData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    insertData.put("RESULT_CODE", "0000");
                    insertData.put("RESULT_INFO", "ok");
                    insertData.put("RSRV_STR1", orderNum); // 能开订单号
                    insertData.put("RSRV_TAG1", "0"); // 操作类型：0-用户激活；1-系统释放
                    insertData.put("RSRV_NUM1", "898"); // 省编码
                    try {
                        Dao.insert("TF_B_SYNC_ACTIVATE_TIME", insertData, Route.CONN_CRM_CEN);
                    } catch (Exception e) {
                        CSAppException.appError("-1", "insert TF_B_SYNC_ACTIVATE_TIME error" + e.getMessage());
                       
                    }


					logger.debug("调用能开返回结果："+stopResult.toString());
	
					//REQ201901300008移动花卡预充值方式的优化 lizj --start
					if("1".equals(tempData.getString("PARA_CODE6"))){
						 AcctCall.recvFee(iv_serial_number,SeqMgr.getTradeId(), tempData.getString("PARA_CODE7"), "15001", tempData.getString("PARA_CODE8"), "0", "移动花卡预充值");				
					}
					//REQ201901300008移动花卡预充值方式的优化 lizj --end
				}
		}
		}
/****************** 关于一级能力开放平台新增移动花卡的补充通知 @Modify by tanzheng end************************/
		
		/****************** REQ202005070031_动感地带联名号卡产品的配置需求 @Modify by wangsc10 start************************/
		if(iv_product_id.equals("84019040") || iv_product_id.equals("84019041")){
			IData mzoneInfo = new DataMap();
			mzoneInfo.put("SERIAL_NUMBER", iv_serial_number);
			mzoneInfo.put("PRODUCT_ID", iv_product_id);
			mzoneInfo.put("USER_ID", iv_userid);
			mzoneInfo.put("CUST_NAME", custInfo.getString("CUST_NAME"));
			IDataset resultZones = CSAppCall.call("SS.BenefitMZoneIntfSVC.benefitMZone", mzoneInfo);
			if(IDataUtil.isNotEmpty(resultZones)){
				IData resultZone = resultZones.first();
				String resultCode = resultZone.getString("X_RESULTCODE");
				String resultInfo = resultZone.getString("X_RESULTINFO");
				if(!resultCode.equals("0000")){
					//CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo);
				}
			}

		}
		/****************** REQ202005070031_动感地带联名号卡产品的配置需求 @Modify by wangsc10 end************************/
		
		/****************** REQ201906170001 新增“京东物流专项套餐” @Modify by lizj start************************/
		IDataset comparas2588 =BreQryForCommparaOrTag.getCommpara("CSM",2588,"ZZZZ");
		for(Object temp1 : comparas2588){
			IData tempData = (IData) temp1;
			//如果主套餐为配置的套餐，则调用接口
			if(iv_product_id.equals(tempData.getString("PARA_CODE1"))){
				String remark = tempData.getString("REMARK");
	    		String tradeFee = tempData.getString("PARA_CODE7");
	    		String channelId = tempData.getString("PARA_CODE5","15001");
	    		String paymentId = tempData.getString("PARA_CODE8");
	    		String payFeeModeCode = tempData.getString("PARA_CODE6");
	    		if(!"N".equals(tempData.getString("PARA_CODE9"))){
	    			AcctCall.recvFee(iv_serial_number,SeqMgr.getTradeId(), tradeFee, channelId, paymentId, payFeeModeCode, remark);
	    		}
			}
			
		}
		
        
        String iv_cust_name = custInfo.getString("CUST_NAME");
		String oper_staff_id = CSBizBean.getVisit().getStaffId();
        String oper_depart_id = CSBizBean.getVisit().getDepartId();
        
        if(!("1".equals(userInfo.getString("IS_REAL_NAME_TAG",""))&&"60".equals(userInfo.getString("TRADE_TYPE_CODE",""))
        		&&("G001".equals(iv_brand_code)||"G002".equals(iv_brand_code)||"G010".equals(iv_brand_code)))
        		||StringUtils.equals("1", realNameSwitch)){
        	
        	  bean.insTradeH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
              bean.insTradeStaffH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
        }
           
       

        // 同步数据给资源并且更新TS_S_USER_BACK表 sunxin
        // 将user_id、IMSI或SIM卡号传给资源

        String imsi = "";
        String simNo = "";
        IDataset userRes = UserResInfoQry.getUserResInfoByUserIdRestype(iv_userid, "-1", "1");
        if(!"PWLW".equals(iv_brand_code))
        {
        	if (IDataUtil.isNotEmpty(userRes))
            {
                imsi = userRes.getData(0).getString("IMSI");
                simNo = userRes.getData(0).getString("RES_CODE");
                ResCall.cardSaleActive(imsi);
            }
            else
            {
            	CSAppException.apperr(ResException.CRM_RES_6);
            }   
        }
        // 根据userId，serial_number，simno更新TS_S_USER_BACK
        IData param = new DataMap();
        param.put("SIM_CARD_NO", simNo);
        param.put("SERIAL_NUMBER", iv_serial_number);
        param.put("USER_ID", iv_userid);
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TS_S_USER_BACK SET TAG = '3'");
        sql.append(" WHERE USER_ID = :USER_ID ");
        sql.append(" AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND SIM_CARD_NO = :SIM_CARD_NO ");
        Dao.executeUpdate(sql, param);

        bean.insTiSync(iv_sync_sequence);

    }

    public IDataset firstCallDealForCrm(IData param) throws Exception
    {
        String v_resultcode = "-1";
        String v_resultinfo = "TradeOk";
        String iv_firstcalltime = param.getString("FIRST_CALL_TIME");
        String iv_userid = param.getString("USER_ID");
        String flag = param.getString("FLAG");
        if (StringUtils.isEmpty(iv_firstcalltime))
        {
            iv_firstcalltime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        }
        else
        {
            iv_firstcalltime = SysDateMgr.decodeTimestamp(iv_firstcalltime, SysDateMgr.PATTERN_STAND_SHORT);
        }

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(iv_userid);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主表时出错");
        }

        userInfo.put("IS_REAL_NAME_TAG", "1");
        userInfo.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE",""));
        firstCallDeal(userInfo, iv_userid, iv_firstcalltime, v_resultcode, v_resultinfo,flag);

        // 得到要清空的keylist
        getUCAKey(userInfo, iv_userid);

        v_resultcode = "0";
        IDataset returninfos = new DatasetList();

        IData returninfo = new DataMap();
        returninfo.put("V_RESULTCODE", v_resultcode);
        returninfo.put("V_RESULTINFO", v_resultinfo);
        returninfos.add(returninfo);

        return returninfos;
    }

    private void getUCAKey(IData userInfo, String iv_userid) throws Exception
    {
        // 加缓存清空key
        delKeys = new DatasetList();
        IData map = null;

        // userid
        String cacheKey = CacheKey.getUcaKeyUserByUserId(iv_userid, null);
        map = new DataMap();
        map.put("ID_KEY", cacheKey);
        delKeys.add(map);

        // sn
        String sn = userInfo.getString("SERIAL_NUMBER");
        cacheKey = CacheKey.getUcaKeyUserBySn(sn, null);
        map = new DataMap();
        map.put("ID_KEY", cacheKey);
        delKeys.add(map);

        // main prodcut
        cacheKey = CacheKey.getUcaKeyMainProdByUserId(iv_userid, null);
        map = new DataMap();
        map.put("ID_KEY", cacheKey);
        delKeys.add(map);
    }

    public IDataset intoFirstCall(IData data) throws Exception
    {
        String v_resultcode = "-1";
        String v_resultinfo = "TradeOk";
        String iv_firstcalltime = "";
        String startDate = data.getString("START_DATE");
        String startTime = data.getString("START_TIME");
        iv_firstcalltime = startDate + startTime;
        String iv_userid = data.getString("USER_ID");
        FirstCallTimeBean bean = (FirstCallTimeBean) BeanManager.createBean(FirstCallTimeBean.class);

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(iv_userid);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "取用户的出帐标记时出错");
        }

        String rsrvStr2 = data.getString("RSRV_STR2");
        if (StringUtils.isNotEmpty(rsrvStr2))
        {
            firstCallDeal(userInfo, iv_userid, iv_firstcalltime, v_resultcode, v_resultinfo,"");
            if (!"M".equals(rsrvStr2))
            {
                IData productparams = new DataMap();
                productparams.put("PRODUCT_ID", rsrvStr2);
                IData productInfo = ProductInfoQrySVC.getProductByPK(productparams);
                if (IDataUtil.isNotEmpty(productInfo))
                {
                    IData smsData = new DataMap();
                    String templateId2 = "CRM_SMS_PER_FC_2320";
                    smsData.put("FC_PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
                    IData templateInfo2 = bean.getTemplateInfo(templateId2);
                    String content = bean.getSmsContent(templateInfo2, smsData);

                    smsData.put("RECV_OBJECT", userInfo.getString("SERIAL_NUMBER"));
                    smsData.put("NOTICE_CONTENT", content);
                    smsData.put("IN_MODE_CODE", "0");
                    smsData.put("CHAN_ID", "24");
                    smsData.put("SEND_OBJECT_CODE", "1");
                    smsData.put("SEND_TIME_CODE", "1");
                    smsData.put("SEND_COUNT_CODE", "2");
                    smsData.put("RECV_OBJECT_TYPE", "00");
                    smsData.put("RECV_ID", userInfo.getString("USER_ID"));
                    smsData.put("SMS_TYPE_CODE", "83");
                    smsData.put("SMS_KIND_CODE", "01");
                    smsData.put("NOTICE_CONTENT_TYPE", "0");
                    smsData.put("REFERED_COUNT", "0");
                    smsData.put("FORCE_REFER_COUNT", "2");
                    smsData.put("FORCE_OBJECT", "08981861");
                    smsData.put("SMS_PRIORITY", "6000");
                    smsData.put("REFER_TIME", SysDateMgr.getSysTime());
                    smsData.put("REFER_DEPART_ID", "XXXXX");
                    smsData.put("DEAL_TIME", SysDateMgr.getSysTime());
                    smsData.put("DEAL_STATE", "15");
                    SmsSend.prepareSmsData(smsData);
                }
            }
        }
        else
        {

            if (IDataUtil.isEmpty(userInfo) || !"0".equals(userInfo.getString("REMOVE_TAG")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "取用户的出帐标记时出错");
            }
            String iv_acct_tag = userInfo.getString("ACCT_TAG");
            String cust_id = userInfo.getString("CUST_ID");
            String iv_user_firsttime = userInfo.getString("FIRST_CALL_TIME");
            if (StringUtils.isEmpty(iv_user_firsttime))
            {
                iv_user_firsttime = SysDateMgr.addDays(1000);
                String hms = SysDateMgr.date2String(new Date(), "HH:mm:ss");
                iv_user_firsttime = iv_user_firsttime + " " + hms;
            }

            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(cust_id);
            if (IDataUtil.isEmpty(custInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到该用户的客户资料信息");
            }

            int iv_count = 0;

            boolean realNameFlag = true;

            String iv_net_type_code = userInfo.getString("NET_TYPE_CODE");
            IDataset userMainProductInfos = UserProductInfoQry.queryMainProduct(iv_userid);
            if (IDataUtil.isEmpty(userMainProductInfos) || IDataUtil.isEmpty(userMainProductInfos.getData(0)))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户主产品信息表时出错");
            }
            String iv_brand_code = userMainProductInfos.getData(0).getString("BRAND_CODE");

            IDataset noRealNames = CommparaInfoQry.getCommparaAllCol("CSM", "2317", "2317", "");
            if (IDataUtil.isNotEmpty(noRealNames))
            {
                for (int i = 0; i < noRealNames.size(); i++)
                {
                    String netTypeCode = noRealNames.getData(i).getString("PARA_CODE1");
                    String brandCode = noRealNames.getData(i).getString("PARA_CODE2");
                    if (StringUtils.isNotEmpty(netTypeCode) && netTypeCode.equals(iv_net_type_code))
                    {
                        realNameFlag = false;
                        break;
                    }
                    if (StringUtils.isNotEmpty(brandCode) && brandCode.equals(iv_brand_code))
                    {
                        realNameFlag = false;
                        break;
                    }
                }
            }
            if (realNameFlag && StringUtils.isEmpty(custInfo.getString("IS_REAL_NAME")) || "0".equals(custInfo.getString("IS_REAL_NAME")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户不是实名制用户，不能激活");
            }
            if (!realNameFlag)
            {
                iv_count = 1;
            }

            if ("1".equals(custInfo.getString("IS_REAL_NAME")) && "0".equals(custInfo.getString("REMOVE_TAG")))
            {
                iv_count = 1;
            }

            //long iv_firstcalltimeL = Long.parseLong(iv_firstcalltime);
            //Date d = SysDateMgr.string2Date(iv_user_firsttime, SysDateMgr.PATTERN_STAND);
            //String ds = SysDateMgr.date2String(d, SysDateMgr.PATTERN_STAND_SHORT);
            //long iv_user_firsttimeL = Long.parseLong(ds);
            if (iv_count == 1 && "2".equals(iv_acct_tag))
            {
                firstCallDeal(userInfo, iv_userid, iv_firstcalltime, v_resultcode, v_resultinfo,"");
            }
        }
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        input.put("USER_ID", data.getString("USER_ID"));
        input.put("START_DATE", data.getString("START_DATE"));
        input.put("START_TIME", data.getString("START_TIME"));
        input.put("OPEN_DATE", data.getString("OPEN_DATE"));
        input.put("FILE_NO", data.getString("FILE_NO"));
        input.put("RSRV_STR1", data.getString("RSRV_STR1"));
        input.put("RSRV_STR2", data.getString("RSRV_STR2"));
        input.put("RSRV_STR3", data.getString("RSRV_STR3"));
        input.put("STATE", "3");
        input.put("DEAL_DATE", SysDateMgr.getSysTime());
        if (StringUtils.isNotEmpty(v_resultinfo) && v_resultinfo.length() < 500)
        {
            input.put("DEAL_DESC", v_resultinfo);
        }
        else
        {
            v_resultinfo = v_resultinfo.substring(0, 500);
            input.put("DEAL_DESC", v_resultinfo);
        }
        bean.transHisFirstCall(input);
        bean.deleteFirstCallTime(data.getString("SERIAL_NUMBER"), iv_userid);

        // 得到要清空的keylist
        getUCAKey(userInfo, iv_userid);

        v_resultcode = "0";
        IDataset returninfos = new DatasetList();
        IData returninfo = new DataMap();
        returninfo.put("V_RESULTCODE", v_resultcode);
        returninfo.put("V_RESULTINFO", v_resultinfo);
        returninfos.add(returninfo);
        return returninfos;
    }

    public void scthtradepwlwDeal(String iv_userid, String iv_cust_id, String iv_serial_number, String iv_eparchy_code, String iv_city_code) throws Exception
    {
        FirstCallTimeBean bean = BeanManager.createBean(FirstCallTimeBean.class);
        String trade_id = SeqMgr.getTradeId();
        String accept_month = trade_id.substring(4, 6);
        String iv_order_id = SeqMgr.getOrderId();
        IDataset userSvcInfos = UserSvcInfoQry.getSvcUserId(iv_userid, "99010000");
        IData userSvcInfo = userSvcInfos.getData(0);
        String product_id = userSvcInfo.getString("PRODUCT_ID");
        String inst_id = userSvcInfo.getString("INST_ID");
        String start_date = userSvcInfo.getString("START_DATE");

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(iv_cust_id);
        IData params = new DataMap();
        params.put("USER_ID", iv_userid);
        IDataset acctInfos = UAcctInfoQry.qryAcctInfoByCustId(iv_cust_id);
        String acct_id = "";
        String cust_name = "";
        if (IDataUtil.isNotEmpty(custInfo))
        {
            cust_name = custInfo.getString("CUST_NAME");
        }
        if (acctInfos != null && acctInfos.size() > 0)
        {
            acct_id = acctInfos.getData(0).getString("ACCT_ID");
        }
        bean.addTrade(trade_id, accept_month, null, iv_order_id, null, null, "0", "273", "350", "0", "0", "0", "0", iv_cust_id, cust_name, iv_userid, acct_id, iv_serial_number, "00", iv_eparchy_code, iv_city_code, product_id, "", "0", "0", "0", "",
                "", "", "", SysDateMgr.getSysTime(), "ZZC4BACK", "00000", "A311", iv_city_code, iv_eparchy_code, "0", "0", "0", "", "0", "", "", "00000011300000000000", "0", null, SysDateMgr.getSysTime(), "", "", "", "0", "", "", "", "", "",
                SysDateMgr.getSysTime(), "ZZC4BACK", "00000", "物联网脚本生成工单", "", "", "", "", "", "", "", "", "", "");
        bean.addOrder(iv_order_id, accept_month, null, "0", "0", "0", "0", "273", "350", "0", "0", "0", iv_cust_id, cust_name, "", "", iv_eparchy_code, iv_city_code, null, null, null, "0", null, SysDateMgr.getSysTime(), "ZZC4BACK", "00000", "A311",
                iv_eparchy_code, null, "0", "0", "0", null, "0", null, null, "", null, SysDateMgr.getSysTime(), null, null, null, null, "0", "0", "0", "0", null, null, "0", "0", null, null, null, null, null, SysDateMgr.getSysTime(), "ZZC4BACK",
                "00000", "", "", "", "", "", "", "", "", "", "", "");
        bean.addTradeSvcState(inst_id, trade_id, accept_month, iv_userid, "99010000", "1", "0", start_date, "2");

    }

}
