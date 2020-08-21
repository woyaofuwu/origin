
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.sql.Timestamp;

import net.sf.json.JSONArray;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqSmsSendId;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;

public class EntityCardIntfSVC extends CSBizService
{

    public IData entityCardOrder(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        ;
        IDataUtil.chkParam(data, "OPER_CODE");
        IDataUtil.chkParam(data, "START_DATE");
        String spCode = IDataUtil.chkParam(data, "SP_CODE");
        String bizCode = IDataUtil.chkParam(data, "BIZ_CODE");
        IDataUtil.chkParam(data, "CARD_NO");
        IDataUtil.chkParam(data, "TRANS_ID");
        // 回传参数
        result.put("AFFIRM", "0");
        result.put("SERIAL_NUMBER", serialNumber);
        result.put("OPR_NUM", data.getString("TRANS_ID", data.getString("INTF_TRADE_ID")));

        IDataset spBizList = SpInfoQry.querySpBizInfo(spCode, bizCode);
        if (IDataUtil.isEmpty(spBizList))
        {
            CSAppException.apperr(BofException.CRM_BOF_016);
        }
        String bizTypeCode = spBizList.getData(0).getString("BIZ_TYPE_CODE");
        // 如果是流量经营类
        if (IDataUtil.isNotEmpty(spBizList) && "VS".equals(bizTypeCode))
        {
            Timestamp timestamp = SysDateMgr.encodeTimestamp(data.getString("START_DATE"));
            String startDate = DateFormatUtils.format(timestamp.getTime(), "yyyy-MM-dd HH:mm:ss");
            String modifyTag = BofConst.MODIFY_TAG_ADD;
            if (PlatConstants.OPER_CANCEL_ORDER.equals(data.getString("OPER_CODE")))
            {
                modifyTag = BofConst.MODIFY_TAG_DEL;
            }
            //modified by liaosw 2014-12-05 begin
            String elementId = getElementId(data.getString("PRODUCT_ID"));
            //如果根据productID从配置表中获取不到优惠ID，则还是按照老的方式拼接优惠ID
            if(null == elementId)
            {
            	elementId = "99" + data.getString("BIZ_CODE");
            }
            data.put("ELEMENT_ID", elementId);
            data.put("MODIFY_TAG", modifyTag);
            data.put("ELEMENT_TYPE_CODE", "D");
            data.put("NUM", "1");
            data.put("START_DATE", startDate);
            data.put("BOOKING_TAG", "0");

            IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
            if (resultList != null && !resultList.isEmpty())
            {
                result.putAll(resultList.getData(0));
                result = resultList.getData(0);
				result.put("OPER_CODE", data.getString("OPER_CODE",""));
				result.put("START_DATE", data.getString("START_DATE").substring(0, 10).replace("-", ""));
				String tradeId = resultList.getData(0).getString("TRADE_ID");				
		        IData dataInput = new DataMap();
		        dataInput.put("TRADE_ID", tradeId);
		        dataInput.put("MODIFY_TAG", modifyTag);		        
		        
		        IDataset resultDiscnts = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADE_ID", dataInput,Route.getJourDb(BizRoute.getRouteId()));
	            if(resultDiscnts.size()>0)
	            {	            	
	            	String endDate=resultDiscnts.getData(0).getString("END_DATE").substring(0, 10).replace("-", "");
		            result.put("END_DATE", endDate);
		            data.put("END_DATE", resultDiscnts.getData(0).getString("END_DATE"));
	            }
            }
            insertPlatLog(data);
            return result;
        }
        else
        {
            IDataset resultList = CSAppCall.call("SS.PlatEntityCardSVC.process", data);
            if (resultList != null && !resultList.isEmpty())
            {
				result.put("OPER_CODE", data.getString("OPER_CODE",""));
                result.putAll(resultList.getData(0));
            }
            return result;
        }
    }
    public IData insertPlatLog(IData param) throws Exception {
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));//业务流水号
	
	    inparam.put("BIZ_STATE_CODE","01" );
		
		if("07".equals(param.getString("OPER_CODE"))){
			inparam.put("BIZ_STATE_CODE","02" );
		}
		
		inparam.put("EFF_TIME",  param.getString("START_DATE"));
		inparam.put("END_DATE", param.getString("END_DATE"));
		inparam.put("INTF_TRADE_ID", param.getString("TRANS_ID"));
		inparam.put("DEAL_STATE", "0");
		inparam.put("CARD_NO", param.getString("CARD_NO"));
		inparam.put("ORG_DOMAIN", "VSCP");
		inparam.put("BEGIN", "1");
		inparam.put("BILLFLG","0");
		inparam.put("SP_CODE", param.getString("SP_CODE"));
		inparam.put("BIZ_CODE", param.getString("BIZ_CODE"));
		inparam.put("IN_TIME", SysDateMgr.getSysTime());
		inparam.put("BIZ_TYPE_CODE","64");
		inparam.put("OPR_SOURCE","64");

		Dao.insert("TI_B_TD_RECON", inparam, Route.CONN_CRM_CEN );
		
		return inparam;
	}
    public IData timeoutFlowCardQuery(IData data) throws Exception{
    	IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "OPER_CODE");
        String spCode = IDataUtil.chkParam(data, "SP_CODE");
        String bizCode = data.getString("OPER_CODE");
        IDataUtil.chkParam(data, "CARD_NO");

        //modified by liaosw 2014-12-05 begin
        String elementId = getElementId(data.getString("PRODUCT_ID"));
        //如果根据productID从配置表中获取不到优惠ID，则还是按照老的方式拼接优惠ID
        if(null == elementId)
        {
        	elementId = "99" + data.getString("OPER_CODE");
        }
        
        data.put("DISCNT_CODE", elementId);
        
        return PlatOrderIntfBean.timeoutFlowCardQuery(data);

	}
	
	public IData timeoutFlowCardRollback(IData data) throws Exception{
		IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "OPER_CODE");
        String spCode = IDataUtil.chkParam(data, "SP_CODE");
        String bizCode = data.getString("OPER_CODE");
        IDataUtil.chkParam(data, "CARD_NO");
//        IDataset spBizList = SpInfoQry.queryBizServiceInfosBySpBizCode(bizCode, spCode);
//        if (IDataUtil.isEmpty(spBizList))
//        {
//            CSAppException.apperr(BofException.CRM_BOF_016);
//        }
        try{
            String modifyTag = BofConst.MODIFY_TAG_DEL;
            
          //modified by liaosw 2014-12-05 begin
            String elementId = getElementId(data.getString("PRODUCT_ID"));
            //如果根据productID从配置表中获取不到优惠ID，则还是按照老的方式拼接优惠ID
            if(null == elementId)
            {
            	elementId = "99" + data.getString("OPER_CODE");
            }
            data.put("ELEMENT_ID", elementId);
            data.put("MODIFY_TAG", modifyTag);
            data.put("ELEMENT_TYPE_CODE", "D");
            data.put("NUM", "1");
            data.put("BOOKING_TAG", "0");

            IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
            
            result.put("TRADE_ID", resultList.getData(0).getString("TRADE_ID"));
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "TRADE OK!");
			return result;
        	
        }catch (Exception e) {
    		try
            {
                String[] errorMessage = e.getMessage().split("`");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                result.put("X_RESULTCODE", errorMessage[0]);
                result.put("X_RSPCODE", errorMessage[0]);
                result.put("X_RESULTINFO", errorMessage[1]);
                result.put("X_RSPDESC", errorMessage[1]);
				return result;
            }
            catch (Exception ex2)
            {
            	result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
            	result.put("X_RESULTCODE", "99");
                result.put("X_RSPCODE", "99");
                result.put("X_RESULTINFO", "其它错误");
                result.put("X_RSPDESC", "其它错误");
				return result;
            }
        }
	}
    
    
    //根据productID从td_s_commpara表中获取对应的优惠编码
	private String getElementId(String productId) throws Exception 
	{
		IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "3722");
        param.put("PARAM_CODE", productId);
        param.put("EPARCHY_CODE", "ZZZZ");
        IDataset params =  Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
		if(null == params || params.isEmpty())
			return null;
		return params.getData(0).getString("PARA_CODE1");
	}
    /**
     * @author liaosw
     * @date 2014-10-14
     * @description 一级营销资源管理系统将流量实体卡产品配置信息同步给各个省CRM
	 * 省CRM将产品配置信息入库到中心库的TI_O_DATAIMPORT_DETAIL表中
	 * 入库完成后短信通知产品配置人员
     * @param data
     * @return
     * @throws Exception
     */
	public void synProductInfo(IData data) throws Exception{
		IData param = new DataMap();
		param.put("DATA_TYPE", "VSCPSYN"); //任务类型暂时定义为VSCPSYN
		param.put("INSERT_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));  //插入时间
		param.put("DEAL_FLAG", "0");   //处理标志为初始状态0 未处理
		//循环处理产品列表
//		String productListStr = data.getString("PRODUCT_LIST"); 
//		IDataset productList = new DatasetList(productListStr);
		String productId = data.getString("PRODUCT_ID");
		//如果有多条记录
		if (productId != null && productId.startsWith("["))
		{
			JSONArray productIds = JSONArray.fromObject(data.getString("PRODUCT_ID"));
			JSONArray productNames = JSONArray.fromObject(data.getString("PRODUCT_NAME"));
			JSONArray modelIds = JSONArray.fromObject(data.getString("MODEL_ID"));
			JSONArray oprCodes = JSONArray.fromObject(data.getString("OPR_CODE"));
			JSONArray resources = JSONArray.fromObject(data.getString("RESOURCES"));
			JSONArray units = JSONArray.fromObject(data.getString("UNIT"));
			JSONArray prices = JSONArray.fromObject(data.getString("PRICE"));
			JSONArray effectiveWays = JSONArray.fromObject(data.getString("EFFECTIVE_WAY"));
			JSONArray overNums = JSONArray.fromObject(data.getString("OVER_NUM"));
			JSONArray startTimes = JSONArray.fromObject(data.getString("START_TIME"));
			JSONArray endTimes = JSONArray.fromObject(data.getString("END_TIME"));
			JSONArray startUseTimes = JSONArray.fromObject(data.getString("START_USE_TIME"));
			JSONArray cycleValues = JSONArray.fromObject(data.getString("CYCLE_VALUE"));
			JSONArray cycleUnits = JSONArray.fromObject(data.getString("CYCLE_UNIT"));
			int length = productIds.size();
			for (int i = 0; i < length; i++)
			{
				param.put("OPRNUMB", 1);
				param.put("FILE_NAME", data.getString("SEQ"));
				param.put("FIELD1", data.getString("SP_ID"));  //SP企业代码   
				param.put("FIELD2", data.getString("SP_SERV_ID"));  //SP企业代码  
				param.put("FIELD3", productIds.get(i).toString());  //产品编码  
				param.put("FIELD4", productNames.get(i).toString());  //产品名称
				param.put("FIELD5", modelIds.get(i).toString());  //产品模型编码     
				param.put("FIELD6", oprCodes.get(i).toString());  //操作代码  
				param.put("FIELD7", resources.get(i).toString());  //流量 
				param.put("FIELD8", units.get(i).toString());  //流量单位  
				param.put("FIELD9", prices.get(i).toString());  //价格  
				param.put("FIELD10", effectiveWays.get(i).toString());  //产品生效规则    
				param.put("FIELD11", overNums.get(i).toString());  //叠加次数  
				param.put("FIELD12", startTimes.get(i).toString());  //产品生效时间  
				param.put("FIELD13", endTimes.get(i).toString());  //产品失效时间  
				param.put("FIELD14", startUseTimes.get(i).toString());  //使用生效时间（预约） 
				param.put("FIELD15", cycleValues.get(i).toString());  //周期数
				param.put("FIELD16", cycleUnits.get(i).toString());  //周期单位
				param.put("FIELD17", data.getString("PKG_SEQ"));  //交易包流水号
				Dao.insert("TI_O_DATAIMPORT_DETAIL", param, Route.CONN_CRM_CEN );
			}
		}
		else
		{
			//只有一条记录
			param.put("OPRNUMB", 1);
			param.put("FILE_NAME", data.getString("SEQ"));
			param.put("FIELD1", data.getString("SP_ID"));  //SP企业代码   
			param.put("FIELD2", data.getString("SP_SERV_ID"));  //SP企业代码  
			param.put("FIELD3", data.getString("PRODUCT_ID"));  //产品编码  
			param.put("FIELD4", data.getString("PRODUCT_NAME"));  //产品名称
			param.put("FIELD5", data.getString("MODEL_ID"));  //产品模型编码     
			param.put("FIELD6", data.getString("OPR_CODE"));  //操作代码  
			param.put("FIELD7", data.getInt("RESOURCES"));  //流量 
			param.put("FIELD8", data.getString("UNIT"));  //流量单位  
			param.put("FIELD9", data.getInt("PRICE"));  //价格  
			param.put("FIELD10", data.getString("EFFECTIVE_WAY"));  //产品生效规则    
			param.put("FIELD11", data.getInt("OVER_NUM"));  //叠加次数  
			param.put("FIELD12", data.getString("START_TIME"));  //产品生效时间  
			param.put("FIELD13", data.getString("END_TIME"));  //产品失效时间  
			param.put("FIELD14", data.getString("START_USE_TIME"));  //使用生效时间（预约） 
			param.put("FIELD15", data.getInt("CYCLE_VALUE"));  //周期数
			param.put("FIELD16", data.getString("CYCLE_UNIT"));  //周期单位
			param.put("FIELD17", data.getString("PKG_SEQ"));  //交易包流水号
			Dao.insert("TI_O_DATAIMPORT_DETAIL", param, Route.CONN_CRM_CEN );
		}
//		for(int i=0 ; i<productList.size();i++)
//		{
//			IData productInfo = (IData) productList.get(i);
//		}
		
		//产品信息同步完后发送短信通知产品配置人员进行流量实体卡产品的配置
		//获取产品配置联系人电话
		IDataset params = CommparaInfoQry.getCommpara("CSM", "3526", "ContactPhone",Route.CONN_CRM_CEN);
		data.put("NOTICE_CONTENT", "您好！流量实体卡产品信息已经同步，请尽快进行相应的产品配置，谢谢！");
		for(int i=0; i<params.size();i++)
		{
			String contactPhone = params.getData(i).getString("PARA_CODE1");
			data.put("RECV_OBJECT", contactPhone);
			//根据手机号获取路由
	        String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(contactPhone);
			data.put("EPARCHY_CODE",strEparchyCode);
			SmsSend(data);
			
		}
	}
	
	private void SmsSend(IData data) throws Exception{
        IData sendData = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String smsNoticeId =  Dao.getSequence(SeqSmsSendId.class ,data.getString("EPARCHY_CODE"));
        sendData.put("SMS_NOTICE_ID", smsNoticeId);
        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        if(StringUtils.isNotBlank(data.getString("EPARCHY_CODE")))
        {
        	sendData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        }
        else
        {
        	sendData.put("EPARCHY_CODE",  CSBizBean.getUserEparchyCode());
        }
        sendData.put("RECV_OBJECT", data.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
        sendData.put("RECV_ID", data.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务

        // 短信截取
        String content = data.getString("NOTICE_CONTENT", "");
        // int charLength = Utility.getCharLength(content, 500);
        int charLength = getCharLength(content, 4000);
        content = content.substring(0, charLength);
        sendData.put("NOTICE_CONTENT", content);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_COUNT_CODE", data.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
        sendData.put("REFERED_COUNT", data.getString("REFERED_COUNT", "0"));// 发送次数？
        sendData.put("CHAN_ID", data.getString("CHAN_ID", "11"));
        sendData.put("RECV_OBJECT_TYPE", data.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        sendData.put("SMS_TYPE_CODE", "20");// 20用户办理业务通知
        sendData.put("SMS_KIND_CODE", data.getString("SMS_KIND_CODE", "02"));// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", data.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", data.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
        sendData.put("SMS_PRIORITY", "50");// 短信优先级
        sendData.put("REFER_TIME", data.getString("REFER_TIME", sysdate));// 提交时间
        sendData.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));// 员工ID
        sendData.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));// 部门ID
        sendData.put("DEAL_TIME", data.getString("DEAL_TIME", sysdate));// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", data.getString("SEND_OBJECT_CODE", "6"));// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", data.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", data.getString("REMARK"));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", data.getString("BRAND_CODE"));
        sendData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
        sendData.put("SMS_NET_TAG", data.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", data.getString("FORCE_OBJECT"));// 发送方号码
        sendData.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", data.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", data.getString("DEAL_STAFFID"));// 完成员工
        sendData.put("DEAL_DEPARTID", data.getString("DEAL_DEPARTID"));// 完成部门
        sendData.put("REVC1", data.getString("REVC1"));
        sendData.put("REVC2", data.getString("REVC2"));
        sendData.put("REVC3", data.getString("REVC3"));
        sendData.put("REVC4", data.getString("REVC4"));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期
        
        Dao.insert("TI_O_SMS", sendData, data.getString("EPARCHY_CODE"));
    }

	/**
	 * 一级营销资源管理系统将催办通知下发至省BOSS
	 * 省BOSS负责短信通知产品配置人员进行产品配置
	 * @param data
	 * @throws Exception
	 */
	public void productConfigReminder(IData data) throws Exception{
		//循环处理产品列表
		String seqStr = "";
		String seq = data.getString("SEQ");
		//如果有多条记录
		if (seq != null && seq.startsWith("["))
		{
			JSONArray seqs = JSONArray.fromObject(data.getString("SEQ"));
			JSONArray oprcodes = JSONArray.fromObject(data.getString("OPR_CODE"));
			for(int i=0 ; i<seqs.size();i++)
			{
				//如果操作类型是产品延期结果反馈，则不做任何处理，直接return
				if("05".equals(oprcodes.get(i).toString()))
				{
					return;
				}
				seqStr += seqs.get(i).toString();
				if(i <seqs.size()-1)
				{
					seqStr +=",";
				}

			}
		}
		else
		{
			//如果操作类型是产品延期结果反馈，则不做任何处理，直接return
			if("05".equals(data.getString("OPR_CODE")))
			{
				return;
			}
			seqStr = data.getString("SEQ");
		}
		
		String noticeContent = "您好！一级营销资源管理系统对流量实体卡产品的配置进行了催办，请尽快完成相应的产品配置，催办的产品序列号为["+seqStr+"]，谢谢！";
		//接受到催办通知后发送短信通知产品配置人员进行相应产品的配置
		//获取产品配置联系人电话
		data.put("NOTICE_CONTENT", noticeContent); //短信内容
		IDataset params = CommparaInfoQry.getCommpara("CSM", "3526", "ContactPhone", Route.CONN_CRM_CEN);
		for(int i=0; i<params.size();i++)
		{
			String contactPhone = params.getData(i).getString("PARA_CODE1");
			data.put("RECV_OBJECT", contactPhone);
			//根据手机号获取路由
	        String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(contactPhone);
			data.put("EPARCHY_CODE",strEparchyCode);
			SmsSend(data);
		}
		
	}
	
	  public int getCharLength(String value, int length)
	    {
	        char chars[] = value.toCharArray();
	        int charidx = 0;
	        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
	            if (chars[charidx] > '\200')
	            {
	                charlen += 2;
	                if (charlen > length)
	                {
	                    charidx = charidx - 1;
	                }
	            }
	            else
	            {
	                charlen++;
	            }

	        return charidx;
	    }
    

}
