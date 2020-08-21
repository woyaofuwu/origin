package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.SmsException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class VirtulFamilyTwoCheckIntfSVC extends CSBizService{
	   
    private static final long serialVersionUID = 1L;
    
    
    private static transient final Logger log = Logger.getLogger(VirtulFamilyTwoCheckIntfSVC.class);
	
    
    /**
     * 家庭网新增成员的二次短信确认
     * 
     * @param param  SERIAL_NUMBER(主号)  PRODUCT_ORDER_MEMBER(Dataset)
     * @return
     * @throws Exception
     */
    public IData addMemberVirtulFamilyTwoCheck(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IData inputData = new DataMap();
    	
    	String customerPhone = param.getString("CUSTOMER_PHONE","");
    	IDataset productOrderMembers = param.getDataset("PRODUCT_ORDER_MEMBER");
    	if(IDataUtil.isEmpty(productOrderMembers)){
        	CSAppException.apperr(GrpException.CRM_GRP_857,"参数错误：PRODUCT_ORDER_MEMBER");
    	}
    	
    	for(int i = 0; i< productOrderMembers.size() ;i++){
    		IData inData = new DataMap();
			IData productOrderMember = productOrderMembers.getData(i);
			inData.put("PRODUCT_ORDER_MEMBER",new DatasetList(productOrderMember));
			inData.put("ACTION",param.getString("ACTION"));
			inData.put("PRODUCT_CODE", param.getString("PRODUCT_CODE",""));
			inData.put("MEM_TYPE", productOrderMember.getString("MEM_TYPE",""));
    		// 成员号码校验
    		IData rs = new FamilyAllNetBusiManageBean().checkMebManage(inData);
    		
            productOrderMember.put("RSP_CODE","00" );       
            boolean flag = false ;

            if(!"00".equals(rs.getString("RSP_CODE"))){
            	flag = true ;
            }
            if(flag){
            	/*productOrderMember.put("RSP_CODE","14");
            	productOrderMember.put("RSP_DESC","添加号码状态异常");*/
            	productOrderMember.put("RSP_CODE",rs.getString("RSP_CODE"));
            	productOrderMember.put("RSP_DESC",rs.getString("RSP_DESC"));
            	
            	param.put("PRODUCT_ORDER_MEMBER", new DatasetList(productOrderMember));
            	IData inpData = new DataMap(param);
            	inpData.put("RSP_TYPE", "11");
            	//家庭网校验响应与反馈同时发送的优化改造
            	IData paramProduct = new DataMap();
            	int second = 60 ;
            	IDataset timeconfig = CommparaInfoQry.getCommparaCode1("CSM", "2018", "MFC_BACK_TIME", "ZZZZ");
        	    if(IDataUtil.isNotEmpty(timeconfig)){
        	    	second = Integer.valueOf(timeconfig.getData(0).getString("PARA_CODE1"));
        	    }
            	String execTime = SysDateMgr.addSecond(SysDateMgr.getSysTime(), second);
            	paramProduct.put("DEAL_ID", SeqMgr.getTradeId());
            	paramProduct.put("SERIAL_NUMBER", customerPhone);
            	paramProduct.put("DEAL_COND", inpData);
            	paramProduct.put("DEAL_TYPE", "MfcBack");
            	paramProduct.put("EXEC_TIME", execTime);
            	paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
            	paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
            	paramProduct.put("DEAL_STATE", "0");
            	paramProduct.put("REMARK", "全国亲情网资质校验失败");
            	Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);

        		result.put("RSP_CODE", "00");
            	result.put("RSP_DESC","成功");
            	result.put("OPR_TIME",SysDateMgr.getSysTime());
            	continue;
            }
           // 自付版、5G家庭群组取消二次确认流程
            String proCode =param.getString("PRODUCT_CODE");
            if(("MFC000001".equals(proCode) || "MFC000003".equals(proCode) || "MFC000004".equals(proCode) || "MFC000005".equals(proCode) || "MFC000006".equals(proCode) || "MFC000007".equals(proCode) || "MFC000008".equals(proCode) || "MFC000009".equals(proCode) || "MFC000010".equals(proCode) || "MFC000011".equals(proCode)) && !flag){
            	productOrderMember.put("RSP_CODE",rs.getString("RSP_CODE"));
            	productOrderMember.put("RSP_DESC",rs.getString("RSP_DESC"));
            	param.put("PRODUCT_ORDER_MEMBER", new DatasetList(productOrderMember));
            	param.put("RSP_TYPE", "12");
             	//家庭网校验响应与反馈同时发送的优化改造
            	IData paramProduct = new DataMap();
            	int second = 60 ;
            	IDataset timeconfig = CommparaInfoQry.getCommparaCode1("CSM", "2018", "MFC_BACK_TIME", "ZZZZ");
        	    if(IDataUtil.isNotEmpty(timeconfig)){
        	    	second = Integer.valueOf(timeconfig.getData(0).getString("PARA_CODE1"));
        	    }
            	String execTime = SysDateMgr.addSecond(SysDateMgr.getSysTime(), second);
            	paramProduct.put("DEAL_ID", SeqMgr.getTradeId());
            	paramProduct.put("SERIAL_NUMBER", customerPhone);
            	paramProduct.put("DEAL_COND", param);
            	paramProduct.put("DEAL_TYPE", "MfcBack");
            	paramProduct.put("EXEC_TIME", execTime);
            	paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
            	paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
            	paramProduct.put("DEAL_STATE", "0");
				paramProduct.put("REMARK", "全国亲情网统付反馈");
            	if("MFC000003".equals(proCode)){
					paramProduct.put("REMARK", "5G家庭会员群组反馈");
				}else if("MFC000004".equals(proCode)){
					paramProduct.put("REMARK", "5G家庭套餐群组反馈");
				}else if("MFC000005".equals(proCode)){
					paramProduct.put("REMARK", "5G融合套餐群组反馈");
				}else if("MFC000006".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(支付宝版月包)群组反馈");
				}else if("MFC000007".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(支付宝版季包)群组反馈");
				}else if("MFC000008".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(支付宝版年包)群组反馈");
				}else if("MFC000009".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(异网版月包)群组反馈");
				}else if("MFC000010".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(异网版季包)群组反馈");
				}else if("MFC000011".equals(proCode)){
					paramProduct.put("REMARK", "全国亲情网(异网版年包)群组反馈");
				}

            	Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);
				result.put("RSP_CODE", "00");
            	result.put("RSP_DESC","成功");
            	result.put("OPR_TIME",SysDateMgr.getSysTime());
            	continue;
            }
//            //自付版取消二次确认流程
//            String proCode =param.getString("PRODUCT_CODE");
//            if("MFC000001".equals(proCode)){
//            	productOrderMember.put("RSP_CODE",rs.getString("RSP_CODE"));
//            	productOrderMember.put("RSP_DESC",rs.getString("RSP_DESC"));
//            	param.put("PRODUCT_ORDER_MEMBER", new DatasetList(productOrderMember));
//            	param.put("RSP_TYPE", "12");
//            	//virtulFamilyCheckFail(param);
//            	AsynchronousFeedBackThread pro = new AsynchronousFeedBackThread(param, (BizVisit)CSBizBean.getVisit(),SessionManager.getInstance().peek()) {};
//        		pro.start();
//        		result.put("RSP_CODE", "00");
//            	result.put("RSP_DESC","成功");
//            	result.put("OPR_TIME",SysDateMgr.getSysTime());
//            	return result;
//            }
            
    		inputData.put("SERIAL_NUMBER", productOrderMembers.getData(i).getString("MEM_NUMBER"));
    		inputData.put("PRE_TYPE", BofConst.PLAT_SVC_SEC);
    		inputData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    		inputData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
    		inputData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    		inputData.put("SVC_NAME", "SS.VirtulFamilyTwoCheckIntfSVC.controlInfo");
    		
    		IDataset productIdConfig = CommparaInfoQry.getCommparaCode1("CSM", "2018", "KSJTW_VIRTUAL_PRO", "ZZZZ");
    	    if(IDataUtil.isEmpty(productIdConfig)){
    	    	CSAppException.apperr(GrpException.CRM_GRP_857,"配置错误：KSJTW_VIRTUAL_PRO");
    	    }
    		
    	    
    		IData input = new DataMap(param);
    		input.put("CUSTOMER_PHONE", customerPhone);
    		input.put("PRODUCT_ORDER_MEMBER", new DatasetList(productOrderMembers.getData(i)));
    		input.put("RSP_TYPE", "12");
    		//inputData.put("RSRV_STR2", "0");
    		//input.put("ORDER_SOURCE", "02");
    		IDataset dataset = getSpiltSet(input.toString(), 3800);
            for (int j = 0; j < dataset.size(); j++) {
            	inputData.put("ACCEPT_DATA" + (j + 1), dataset.get(j));
            }
    		IData twoCheckSms = new DataMap();

    		twoCheckSms.put("SERIAL_NUMBER", productOrderMembers.getData(i).getString("MEM_NUMBER"));
			/**
			 * 个付修改：MCF_SEND_SMS配置PARA_CODE1配置统付短信的模板ID ，PARA_CODE2配置个付短信模板ID
			 */
    		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_SMS", "ZZZZ");
			IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_5GSMS", "ZZZZ");

			IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_ZFBSMS", "ZZZZ");

			IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_YWSMS", "ZZZZ");

        	String productCode = param.getString("PRODUCT_CODE","");

    		String templateId = "";
    		if(IDataUtil.isNotEmpty(config)){
				if(StringUtils.equals(productCode,"MFC000001")){//统付
					templateId=config.getData(0).getString("PARA_CODE1","");
				}else if(StringUtils.equals(productCode,"MFC000002")){//个付
					templateId=config.getData(0).getString("PARA_CODE2","");
				}
    		}

			if(IDataUtil.isNotEmpty(fgconfig)){
				if(StringUtils.equals(productCode,"MFC000003")){//5G家庭会员群组
					templateId=fgconfig.getData(0).getString("PARA_CODE7","");
				}else if(StringUtils.equals(productCode,"MFC000004")){//5G家庭套餐群组
					templateId=fgconfig.getData(0).getString("PARA_CODE8","");
				}else if(StringUtils.equals(productCode,"MFC000005")){//5G融合套餐群组
					templateId=fgconfig.getData(0).getString("PARA_CODE9","");
				}
			}


			if(IDataUtil.isNotEmpty(zfbconfig)){
				if(StringUtils.equals(productCode,"MFC000006")){//全国亲情网(支付宝版月包)
					templateId=zfbconfig.getData(0).getString("PARA_CODE7","");
				}else if(StringUtils.equals(productCode,"MFC000007")){//全国亲情网(支付宝版季包)
					templateId=zfbconfig.getData(0).getString("PARA_CODE8","");
				}else if(StringUtils.equals(productCode,"MFC000008")){//全国亲情网(支付宝版年包)
					templateId=zfbconfig.getData(0).getString("PARA_CODE9","");
				}
			}

			if(IDataUtil.isNotEmpty(ywconfig)){
				if(StringUtils.equals(productCode,"MFC000009")){//全国亲情网(异网版月包)
					templateId=ywconfig.getData(0).getString("PARA_CODE7","");
				}else if(StringUtils.equals(productCode,"MFC000010")){//全国亲情网(异网版季包)
					templateId=ywconfig.getData(0).getString("PARA_CODE8","");
				}else if(StringUtils.equals(productCode,"MFC000011")){//全国亲情网(异网版年包)
					templateId=ywconfig.getData(0).getString("PARA_CODE9","");
				}
			}

    		//根据模板ID获取短信
			IData iData = new DataMap();
    		iData.put("MFC_CUST_PHONE",customerPhone);
    		iData.put("PRODUCT_OFFERING_ID",param.getString("PRODUCT_OFFERING_ID"));
    		String content =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
    		twoCheckSms.put("SMS_CONTENT", content);
    		twoCheckSms.put("SMS_TYPE", BofConst.PLAT_SVC_SEC);
    		twoCheckSms.put("OPR_SOURCE", "1");
    		
    		TwoCheckSms.twoCheck("2582", 24, inputData, twoCheckSms);
    	}
    	
    	result.put("RSP_CODE", "00");
    	result.put("RSP_DESC","成功");
    	result.put("OPR_TIME",SysDateMgr.getSysTime());
    	return 	result;
    }

    
    

    
    /**
     * 调bboss
     * @param param  RSP_TYPE   11-成员省针对BBOSS下发省BOSS开通资质验证请求交易资质校验失败后给BBOSS的应答 
     *							12-成员省针对个人客户二次确认通过后给BBOSS的交易应答
     *							13-成员省对个人客户二次确认不通过后给BBOSS的交易应答
     * @return
     * @throws Exception
     */
    public IDataset controlInfo(IData param) throws Exception{
    	IData bBossInput = new DataMap();
    	StringBuilder acceptData = new StringBuilder();
    	IData inparam = new DataMap();
        if(StringUtils.isNotBlank(param.getString("ACCEPT_DATA1"))){
        	for (int j = 1; j < 4; j++) {
            	if(StringUtils.isNotBlank(param.getString("ACCEPT_DATA"+j))){
            		acceptData = acceptData.append(param.getString("ACCEPT_DATA"+j));
                }
            }
        	inparam.putAll(new DataMap(acceptData.toString()));
        }else{
        	inparam.putAll(param);
        }
    	//5-22 异步反馈 到期处理过来
        if(StringUtils.isNotBlank(param.getString("DEAL_COND",""))){
        	inparam.putAll(new DataMap(param.getString("DEAL_COND")));
        }
        if (log.isDebugEnabled())
      	{
      		log.debug("============VirtulFamilyTwoCheckIntfSVC========inparam="+inparam);
      	}
    	
    	
    	
    	if(StringUtils.isBlank(inparam.getString("PRODUCT_OFFERING_ID",""))){
    		bBossInput.put("PRODUCT_OFFERING_ID",param.getString("PRODUCT_OFFERING_ID") );
    	}else{
        	bBossInput.put("PRODUCT_OFFERING_ID",inparam.getString("PRODUCT_OFFERING_ID") );
    	}

    	bBossInput.put("PO_ORDER_NUMBER", inparam.getString("PO_ORDER_NUMBER"));
    	bBossInput.put("CUSTOMER_PHONE", inparam.getString("CUSTOMER_PHONE",""));
    	bBossInput.put("BUSINESS_TYPE", "1"); //1-个人业务 2-集团业务
    	bBossInput.put("ORDER_TYPE", "0");//    0-省BOSS申请变更成员  1-BBOSS下发成员省开通确认
    	if(StringUtils.isNotBlank(param.getString("RSP_TYPE",""))&& "1".equals(param.getString("TYPE",""))){
    		bBossInput.put("RSP_TYPE", param.getString("RSP_TYPE",""));
    	}else{
    		bBossInput.put("RSP_TYPE", inparam.getString("RSP_TYPE",""));
    	}
    	if(IDataUtil.isNotEmpty(inparam.getDataset("PRODUCT_ORDER_MEMBER"))){
    		bBossInput.put("RSLT", inparam.getDataset("PRODUCT_ORDER_MEMBER"));
    	}else{
    		bBossInput.put("RSLT", inparam.getDataset("RSLT"));
    	}
    	
    	
    	bBossInput.put("BIZ_VERSION", "1.0.0");
    	bBossInput.put("KIND_ID", "MFCMemRsp_BBOSS_0_0");
    	if (log.isDebugEnabled())
      	{
      		log.debug("=======VirtulFamilyTwoCheckIntfSVC===IBossCall=====bBossInput="+bBossInput);
      	}
    	IDataset  res = IBossCall.dealInvokeUrl("MFCMemRsp_BBOSS_0_0","IBOSS6", bBossInput);
		return res;
    }
    
    /**
     * 新增失败的结果反馈，超过24小时未回复   调用
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset virtulFamilyTwoCheckFailBack(IData param) throws Exception{
    	IDataset orderPreinfos = OrderPreInfoQry.queryOrderPreInfoByPtTradeCode(BofConst.PLAT_SVC_SEC,"2582");
    	IDataset result = new DatasetList();
    	if(IDataUtil.isEmpty(orderPreinfos)){
           return null;
        }
        
        for (int i = 0; i < orderPreinfos.size(); i++){
            IData orderPreinfo = orderPreinfos.getData(i);
            boolean flag = false ;
            String acceptState = orderPreinfo.getString("ACCEPT_STATE","");
            String endDate = orderPreinfo.getString("END_DATE","");
            String type = "";
            log.debug("======virtulFamilyTwoCheckFailBack====endDate===="+endDate);
            log.debug("======virtulFamilyTwoCheckFailBack====SysDateMgr.getSysTime()===="+SysDateMgr.getSysTime());
            //用户超过24小时未回复
            if("0".equals(acceptState) && endDate.compareTo(SysDateMgr.getSysTime())<0){
            	flag = true ;
            	type = "1";
            }
            //用户回复否 
            if("0".equals(acceptState) && StringUtils.isNotBlank(orderPreinfo.getString("REPLY_CONTENT",""))){
            	flag = true ;
            	type = "2";
            }
            //ACCEPT_STATE = -1  受理失败  （处理异常）
            if("-1".equals(acceptState)){
            	flag = true ;
            	type = "3";
            }
            //处理过的不在重复处理
			if("1".equals(orderPreinfo.get("RSRV_STR2"))){
				flag = false ;
			}
			
			if(flag){
				StringBuilder acceptData = new StringBuilder();
	            for (int j = 1; j < 4; j++) {
	            	if(StringUtils.isNotBlank(orderPreinfo.getString("ACCEPT_DATA"+j))){
	            		acceptData = acceptData.append(orderPreinfo.getString("ACCEPT_DATA"+j));
	                }
	            }
	            
	            IData inparam = new DataMap(acceptData.toString());
	            StringBuilder acceptData2 = new StringBuilder();
	            if(StringUtils.isNotBlank(inparam.getString("ACCEPT_DATA1"))){
	            	for (int j = 1; j < 4; j++) {
		            	if(StringUtils.isNotBlank(inparam.getString("ACCEPT_DATA"+j))){
		            		acceptData2 = acceptData2.append(inparam.getString("ACCEPT_DATA"+j));
		                }
		            }
	            	inparam.putAll(new DataMap(acceptData2.toString()));
	            }
	            
	            try {
	            	IDataset  menInfo = inparam.getDataset("PRODUCT_ORDER_MEMBER");
	            	//TODO
	            	IData in = new DataMap();
	            	IDataset  meminfo = new DatasetList();
	            	System.out.println("==FailBack=CALLBOSS==========controlInfo====type="+type);
	            	if("1".equals(type)){//超过24小时未回复
	            		 
	            		 IData men = menInfo.getData(0);
	            		 men.put("RSP_CODE", "15");
	            		 men.put("RSP_DESC", "超过24小时成员未做二次确认");
	            		 meminfo.add(men);
	            		 
	            	 }else if("2".equals(type)){//拒绝
	            		 IData men = menInfo.getData(0);
	            		 men.put("RSP_CODE", "99");
	            		 men.put("RSP_DESC", "用户拒绝");
	            		 meminfo.add(men);
	            	 }else{
	            		 IData men = menInfo.getData(0);
	            		 men.put("RSP_CODE", "99");
	            		 men.put("RSP_DESC", "受理失败");
	            		 meminfo.add(men);
	            	 }
	            	 in.put("PRODUCT_ORDER_MEMBER", meminfo);
	            	 in.put("PRODUCT_OFFERING_ID", inparam.getString("PRODUCT_OFFERING_ID",""));
	            	 in.put("PO_ORDER_NUMBER", inparam.getString("PO_ORDER_NUMBER",""));
	            	 in.put("CUSTOMER_PHONE", inparam.getString("CUSTOMER_PHONE",""));
	            	 if (log.isDebugEnabled())
	               	{
	               		log.debug("==FailBack=CALLBOSS==========controlInfo====inparam="+inparam);
	               	}
	            	 in.put("RSP_TYPE", "13");
	            	 in.put("TYPE", "1");
	            	 controlInfo(in);
	            	 
	            	 IData idata = new DataMap();
	            	 idata.put("ACCEPT_RESULT", "to deal with success");
	            	 idata.put("PRE_ID", orderPreinfo.get("PRE_ID"));
	            	 idata.put("RSRV_STR2", "1");
	            	 if (log.isDebugEnabled())
		               	{
		               		log.debug("=======controlInfo==updateOrderPre==idata="+idata);
		               	}
					 updateOrderPre(idata);
	            } catch (Exception e) {
	            	IData idata = new DataMap();
	            	idata.put("ACCEPT_RESULT", "To deal with failure!");
	            	idata.put("PRE_ID", orderPreinfo.getString("PRE_ID"));
	            	idata.put("RSRV_STR2", "0");
	            	updateOrderPre(idata);
				}
	            
			}
        }
        return result;
    }

    /**
     * 修改预受理订单
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    private static boolean updateOrderPre(IData idata) throws Exception
    {
        DBConnection conn = null;
        PreparedStatement stmt = null;
        String errorInfo = "";
        try
        {
        	conn = new DBConnection(DBRouteCfg.getRoute(DBRouteCfg.getGroup(CSBizBean.getVisit().getSubSysCode()), Route.getJourDb(Route.CONN_CRM_CG)), true, false);
            StringBuilder parser = new StringBuilder();
            parser.append(" UPDATE TF_B_ORDER_PRE ");
            parser.append(" SET RSRV_STR2=?, ");
            parser.append(" ACCEPT_RESULT=? ");
            parser.append(" WHERE PRE_ID =?");

            stmt = conn.prepareStatement(parser.toString());
            stmt.setString(1, idata.getString("RSRV_STR2", ""));
            stmt.setString(2, idata.getString("ACCEPT_RESULT", ""));
            stmt.setString(3, idata.getString("PRE_ID", ""));
            if (log.isDebugEnabled())
           	{
           		log.debug("=======controlInfo==updateOrderPre==stmt="+parser);
           		log.debug("=======controlInfo==updateOrderPre==idata="+idata);
           	}
            stmt.executeUpdate();
            conn.commit();
        }
        catch (Exception e)
        {
            if (null != conn)
            {
                conn.rollback();
            }

            Utility.print(e);
            errorInfo = Utility.getBottomException(e).getMessage();
            CSAppException.apperr(SmsException.CRM_SMS_8, errorInfo);
        }
        finally
        {
            if (null != stmt)
            {
                stmt.close();
            }

            if (null != conn)
            {
                conn.close();
            }
        }

        /*
         * if (StringUtils.isNotEmpty(errorInfo)) { CSAppException.apperr(SmsException.CRM_SMS_8, errorInfo); }
         */
        return true;
    }
    
	/**
	 * @param source
	 * @param byteLength
	 * @return
	 */
	private IDataset getSpiltSet(String source, int byteLength){
        byte[] sByte = source.getBytes();
        char[] sChar = source.toCharArray();

        IDataset dataset = new DatasetList();

        if (sByte.length <= byteLength)  {
            dataset.add(source);
        } else  {
            int byleCount = 0;
            int first = 0;
            for (int i = 0; i < sChar.length; i++) {
                if ((int) sChar[i] > 0x80) {
                    byleCount += 2;
                } else {
                    byleCount += 1;
                }
                if (byleCount == byteLength){
                    if (first == 0) {
                        dataset.add(new String(sChar, first, i + 1));
                    } else {
                        dataset.add(new String(sChar, first, i - first + 1));
                    }
                    first = i + 1;
                    byleCount = 0;
                }
                if (byleCount == byteLength + 1)  {
                    if (first == 0)  {
                        dataset.add(new String(sChar, first, i));
                        first = i;
                    } else {
                        dataset.add(new String(sChar, first, i - first));
                        first = i;
                    }
                    i -= 1;
                    byleCount = 0;
                }
            }
            if (byleCount != 0){
                dataset.add(new String(sChar, first, sChar.length - first));
            }
        }
        return dataset;
    }
	/** 定时任务处理家庭网内无成员
	 *  @param param
	 * @return
	 * @throws Exception
	 */
	public IData sendSmsForNoMember(IData param) throws Exception{
		//查询出家庭网内无成员 (新办理家庭网且无同步添加成员号或已订购家庭网且删除最后一张成员号)
		IDataset relationUUDataset = MfcCommonUtil.getSEL_USERIDA_NOMEMBER("1","MF");
		IData result = new DataMap();
		result.put("RES_CODE", "0");
		result.put("RES_DESC", "成功");
		if(log.isDebugEnabled()){
			log.debug("==============定时任务家庭网内无成员发短信==relationUUset===="+relationUUDataset);
		}
		//获取家庭网内无成员主号短信模板ID
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_SMS", "ZZZZ");

		IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_5GSMS", "ZZZZ");

		IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_ZFBSMS", "ZZZZ");

		IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "MCF_SEND_YWSMS", "ZZZZ");

		if(log.isDebugEnabled()){
			log.debug("==============MCF_SEND_SMS==config===="+config);
		}
		if(DataUtils.isNotEmpty(relationUUDataset)){
			String templateId = "";
			for(int i=0;i<relationUUDataset.size();i++){
				IData relationUU = relationUUDataset.getData(i);
				String remark = relationUU.getString("REMARK","");
				String custPhone = relationUU.getString("SERIAL_NUMBER_B","");
				if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_ZF) && DataUtils.isNotEmpty(config)){//自付
					templateId = config.getData(0).getString("PARAM_NAME","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE19","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE20","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE21","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE19","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE20","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE21","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE19","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE20","");
				}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11) && DataUtils.isNotEmpty(fgconfig)){
					templateId = fgconfig.getData(0).getString("PARA_CODE21","");
				}else{//统付
					templateId = config.getData(0).getString("PARA_CODE25","");
				}
				//短信内容
				String smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,null);
				if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(custPhone))) {
					IData sendInfo = new DataMap();
					sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(custPhone));
					sendInfo.put("RECV_OBJECT", custPhone);
					sendInfo.put("RECV_ID", custPhone);
					sendInfo.put("SMS_PRIORITY", "50");
					sendInfo.put("NOTICE_CONTENT", smsContent);
					sendInfo.put("REMARK", "全国亲情网网内无成员");
					sendInfo.put("FORCE_OBJECT", "10086");
					SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
				}

			}
		}else{
			result.put("RES_CODE", "99");
			result.put("RES_DESC", "失败");
		}

		return result;
	}

}
