package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BbossXmlMainInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;

public class GroupTwoCheckIntfBean extends CSBizBean
{
	private static final transient Logger log = Logger.getLogger(GroupTwoCheckIntfBean.class);

	
	
	
	/**
	 * TF_TP_BBOSS_XML_INFO 
	 * 二次短息确认的 ，回复 是 的处理需修改 TF_TP_BBOSS_XML_INFO表的OPEN_RESULT_CODE字段
	 * @param inParam
	 * @return
	 */
	public IDataset callBoss(IData inParam) throws Exception {
		//成员号码
		IDataset results = new  DatasetList();
		IData result = new DataMap();
		result.put("RESULT_CODE", "99");
		result.put("RESULT_DESC", "失败");
		String sn = inParam.getString("SERIAL_NUMBER","");
		String pkgSeq= inParam.getString("PKGSEQ","");
		String bipcode= inParam.getString("BIPCODES","MemberService_CKRSP");
		if (log.isDebugEnabled())
      	{
      		log.debug("=======GroupTwoCheckIntfBean===sn==="+sn+"==pkgSeq=="+pkgSeq+"====bipcode=="+bipcode);
      	}
		//查询成员数据
		IDataset  bbossXmlMainInfo = BbossXmlMainInfoQry.qryXmlMainInfoByPkgseqAndSn(pkgSeq, sn, bipcode);
		
		if(IDataUtil.isNotEmpty(bbossXmlMainInfo)){
			//按理说只能查出一条数据
			IData bbossInfo = bbossXmlMainInfo.getData(0);
			if (log.isDebugEnabled())
	      	{
	      		log.debug("=======GroupTwoCheckIntfBean===bbossInfo==="+bbossInfo);
	      	}
			String seqId = bbossInfo.getString("SEQ_ID");//主键
			//更新表字段OPEN_RESULT_CODE
			BbossXmlMainInfoQry.updXmlMainopenResultCode(seqId,"00","二次短信回复确认订购");
			result.put("RESULT_CODE", "00");
			result.put("RESULT_DESC", "成功");
		}
		results.add(result);
		return results;
	}
	
	/**
	 * TF_TP_BBOSS_XML_INFO    daidl
	 * 集客大厅二次短息确认的 ，回复 是 的处理后进行开通的处理
	 * @param inParam
	 * @return
	 */
	public IDataset callJKDT(IData inParam) throws Exception {
		//成员号码
		IDataset results = new  DatasetList();
		IData result = new DataMap();
		result.put("RESULT_CODE", "99");
		result.put("RESULT_DESC", "失败");
		String sn = inParam.getString("SERIAL_NUMBER","");
		String pkgSeq= inParam.getString("PKGSEQ","");
		String bipcode= inParam.getString("BIPCODES","MemberService_CKRSP");
		if (log.isDebugEnabled())
      	{
      		log.debug("=====jkdt==GroupTwoCheckIntfBean===sn==="+sn+"==pkgSeq=="+pkgSeq+"====bipcode=="+bipcode);
      	}
		//查询成员数据
		IDataset  bbossXmlMainInfo = BbossXmlMainInfoQry.qryXmlMainInfoByPkgseqAndSn(pkgSeq, sn, bipcode);
		
		if(IDataUtil.isNotEmpty(bbossXmlMainInfo)){
			//按理说只能查出一条数据
			IData bbossInfo = bbossXmlMainInfo.getData(0);
			if (log.isDebugEnabled())
	      	{
	      		log.debug("=====jkdt==GroupTwoCheckIntfBean===bbossInfo==="+bbossInfo);
	      	}
			String seqId = bbossInfo.getString("SEQ_ID");//主键
			//更新表字段OPEN_RESULT_CODE
			BbossXmlMainInfoQry.updXmlMainDealState(seqId,"0");//置为延时工单进行开通处理
			BbossXmlMainInfoQry.updXmlMainXmlAction(seqId,"16");//置为实时接口成员开通类别
			result.put("RESULT_CODE", "00");
			result.put("RESULT_DESC", "成功");
		}
		results.add(result);
		return results;
	}
	
	/**
	 * 对回复否的特殊处理
	 * @param inParam
	 * @return
	 */
	public IDataset failBackCallBoss(IData inParam) throws Exception{
		//配置需要进行回复否的以及超过24小时的，需要反馈BBoss处理  的业务类型配置
		IDataset config = CommparaInfoQry.getCommparaCode1("CSM", "2018", "GROUP_VIRTUAL_TRADE","ZZZZ");
    	IData configs = new DataMap();
		IDataset orderPreinfos = new DatasetList();
		if(IDataUtil.isNotEmpty(config)){
			for(int i = 0; i< config.size();i++){
				String tradeTypeCode = config.getData(i).getString("PARA_CODE1","");
				if(StringUtils.isNotBlank(tradeTypeCode)){
					IDataset orderPreinfoset = OrderPreInfoQry.queryOrderPreInfoByPtTTCode(BofConst.GRP_BUSS_SEC,tradeTypeCode,"0");
					configs  = config.getData(i);
					orderPreinfos.addAll(orderPreinfoset);
				}
	    	}
		}else{
			CSAppException.apperr(GrpException.CRM_GRP_713,"配置缺少：GROUP_VIRTUAL_TRADE");
		}
		IDataset result = new DatasetList();//响应
		IData res = new DataMap();
		res.put("RESULT_CODE", "99");
    	res.put("RESULT_DESC", "没有相关数据");
		for(int i = 0; i < orderPreinfos.size(); i++){
			IData orderPreinfo = orderPreinfos.getData(i);
            boolean flag = false ;
            String acceptState = orderPreinfo.getString("ACCEPT_STATE","");
            String endDate = orderPreinfo.getString("END_DATE","");
            String type = "";
            if (log.isDebugEnabled())
          	{
            	log.debug("======GroupTwoCheckIntfBean====endDate===="+endDate);
            	log.debug("======GroupTwoCheckIntfBean====SysDateMgr.getSysTime()===="+SysDateMgr.getSysTime());
          	}
            //用户超过24小时未回复 超时虽然不这处理了，但是预订单表的超时数据需要处理
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
		
			if(flag){
				StringBuilder idataString = new StringBuilder();
	    	    idataString.append(orderPreinfo.getString("ACCEPT_DATA1", "")).append(orderPreinfo.getString("ACCEPT_DATA2", "")).append(orderPreinfo.getString("ACCEPT_DATA3", "")).append(orderPreinfo.getString("ACCEPT_DATA4", "")).append(
	    	    		orderPreinfo.getString("ACCEPT_DATA5", ""));

	    	    String jsonObject = idataString.toString();
	    	    IData iDataMap = new DataMap(jsonObject);
	            
	    	    
	    	    if (log.isDebugEnabled())
	          	{
	            	log.debug("======GroupTwoCheckIntfBean====iDataMap===="+iDataMap);
	          	}
	            try {
	        		//成员号码
	        		String sn = iDataMap.getString("SERIAL_NUMBER","");
					configs.put("TRADE_NAME", iDataMap.getString("TRADE_NAME","集团V网（全国版）业务"));
					configs.put("SERIAL_NUMBER", sn);
	        		String pkgSeq= iDataMap.getString("PKGSEQ","");
	        		if(StringUtils.isBlank(pkgSeq)){
						pkgSeq =iDataMap.getString("PKG_SEQ");
					}
	        		String bipcode= iDataMap.getString("BIPCODES","MemberService_CKRSP");
	            	
	        		String openResultCode = "00";
	        		String openResultDesc = "二次短信回复拒绝订购";
	            	if("2".equals(type)){//拒绝
	            		 openResultCode = "99";
		        		 openResultDesc = "二次短信回复拒绝订购";
	            	 }else if ("1".equals(type)){//预订单表的超时数据需要处理
	            		 openResultCode = "99";
	 	        		 openResultDesc = "超时未回复";

						//查询成员数据
						IDataset  bbossXmlMainInfo = BbossXmlMainInfoQry.qryXmlMainInfoByPkgseqAndSn(pkgSeq, sn, bipcode);
						if(IDataUtil.isNotEmpty(bbossXmlMainInfo)) {
							//按理说只能查出一条数据
							IData bbossInfo = bbossXmlMainInfo.getData(0);
							if (log.isDebugEnabled()) {
								log.debug("=======GroupTwoCheckIntfBean===bbossInfo===" + bbossInfo);
							}
							String seqId = bbossInfo.getString("SEQ_ID");//主键

							//发送失败短信
							sendSMS(configs);
							//更新表字段OPEN_RESULT_CODE
							BbossXmlMainInfoQry.updXmlMainopenResultCode(seqId, openResultCode, openResultDesc);
							//更新oder_pre表
							IData idata = new DataMap();
							idata.put("ACCEPT_RESULT", "deal overtime data");
							idata.put("PRE_ID", orderPreinfo.get("PRE_ID"));
							idata.put("ACCEPT_STATE", "5");
							updateOrderPre(idata);
							result.add(res);
							continue;
						}else{//没有查到，不是这个业务的  不作处理
							continue;
						}
						
	            	 }
	            	res.put("RESULT_CODE", "0");
	            	res.put("RESULT_DESC", openResultDesc);
	            	//查询成员数据
	        		IDataset  bbossXmlMainInfo = BbossXmlMainInfoQry.qryXmlMainInfoByPkgseqAndSn(pkgSeq, sn, bipcode);
	        		if(IDataUtil.isNotEmpty(bbossXmlMainInfo)){
	        			//按理说只能查出一条数据
	        			IData bbossInfo = bbossXmlMainInfo.getData(0);
	        			if (log.isDebugEnabled())
	        	      	{
	        	      		log.debug("=======GroupTwoCheckIntfBean===bbossInfo==="+bbossInfo);
	        	      	}
	        			String seqId = bbossInfo.getString("SEQ_ID");//主键

						//发送失败短信
						sendSMS(configs);
	        			//更新表字段OPEN_RESULT_CODE
	        			BbossXmlMainInfoQry.updXmlMainopenResultCode(seqId,openResultCode,openResultDesc);
	        			//更新oder_pre表
	        			IData idata = new DataMap();
		            	idata.put("ACCEPT_RESULT", "to deal with success");
		            	idata.put("PRE_ID", orderPreinfo.get("PRE_ID"));
		            	idata.put("ACCEPT_STATE", "6");
						updateOrderPre(idata);
	        		}else{
	        			//没有查到，不是这个业务的  不作处理
	        		}
	            } catch (Exception e) {
	            	IData idata = new DataMap();
	            	idata.put("ACCEPT_RESULT", "To deal with failure!");
	            	idata.put("PRE_ID", orderPreinfo.getString("PRE_ID"));
	            	idata.put("ACCEPT_STATE", "0");
	            	updateOrderPre(idata);
				}
			}
		}
		result.add(res);
		return result ;
	}

	public void sendSMS(IData param) throws Exception{
		String  isSend = param.getString("PARA_CODE2","");//配置GROUP_VIRTUAL_TRADE ;PARA_CODE2 :  1 ：需要发短信
		if("1".equals(isSend)){
			//尊敬的客户，您已拒绝订购中国移动的集团V网（全国版）业务，未收取您任何费用。【中国移动】
			String templateContent = param.getString("PARA_CODE18","");//配置短信模板
			String tradeName = param.getString("TRADE_NAME","");
			String sn = param.getString("SERIAL_NUMBER","");
			templateContent = templateContent.replaceAll("@\\{TRADE_NAME\\}", tradeName);

			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(sn));
			sendInfo.put("RECV_OBJECT", sn);
			sendInfo.put("RECV_ID", sn);
			sendInfo.put("SMS_PRIORITY", "50");
			sendInfo.put("NOTICE_CONTENT",templateContent );
			sendInfo.put("REMARK", "回复否的");
			sendInfo.put("FORCE_OBJECT", "10086");
			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(sn));
		}


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
            parser.append(" SET ACCEPT_STATE=?, ");
            parser.append(" ACCEPT_RESULT=? ");
            parser.append(" WHERE PRE_ID =?");

            stmt = conn.prepareStatement(parser.toString());
            stmt.setString(1, idata.getString("ACCEPT_STATE", "6"));
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
	
}
