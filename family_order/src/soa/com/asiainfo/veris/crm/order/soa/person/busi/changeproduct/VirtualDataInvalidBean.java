package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;


import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.KjPrintBean;



/**
 * 虚拟流量购买
 * 
 * @author xieyf5@asiainfo.com
 * @date 2017年6月19日
 * @time 上午10:01:21
 */
public class VirtualDataInvalidBean extends CSBizService {
	private static final long serialVersionUID = 1L;	

	/**
	 * 获取发票打印相关信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getNotePrintTradeInfo(IData input) throws Exception {
		
		IData result = new DataMap();
		IDataset receiptInfos = new DatasetList();
		result.put("RSP_CODE", "0000");
		result.put("RSP_DESC", "OK");
		result.put("OPR_TIME", SysDateMgr.getSysTime());
		// 根据号码获流量直冲业务信息
		IDataset notePrintTradeInfo = Dao.qryByCode("TF_F_USER_OTHER", "SELT_REPRINTTRADEINFO_NEW", input,Route.getJourDb());//查的台账表与台账历史表				
		if(IDataUtil.isNotEmpty(notePrintTradeInfo)){	
		for (int i = 0; i < notePrintTradeInfo.size(); i++) {
			IData temp = notePrintTradeInfo.getData(i);
			 String rsrv4=temp.getString("RSRV_STR4","");
             String tag=StaticUtil.getStaticValue("PLAT_ELEC_INVOICE", rsrv4);
             if(StringUtils.isBlank(tag)){//配置该标记即为打发票
             	continue;
             }
			IData receipt = new DataMap();
			receipt.put("RECEIPT_ID", temp.getString("TRADE_ID"));
			receipt.put("ACTION_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, temp.getString("ACCEPT_DATE")));
			receipt.put("AMOUNT", temp.getString("RSRV_STR5"));
			receipt.put("CHANNEL_ID","07");
			receipt.put("PAY_TYPE", "01");
			receipt.put("RECEIPT_STATUS", "0");
			String FlowNum=getFlowNumInfo(temp.getString("TRADE_ID"));
			receipt.put("PAY_INFO",FlowNum);
		// 获取电子发票打印日志,有日志则说明已经打印过
		    IDataset printPdfLog = Dao.qryByCode("TF_B_PRINTPDF_LOG", "SELECT_PRINTPDFLOG_BY_TRADEID", temp);
			if (IDataUtil.isNotEmpty(printPdfLog)) 
			{
			receipt.put("RECEIPT_STATUS", "1");		
			receipt.put("REQUEST_ID", printPdfLog.getData(0).getString("PRINT_ID", ""));
			receipt.put("PRINT_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, printPdfLog.getData(0).getString("IN_DATE", "")));
			}										
			receiptInfos.add(receipt);					
		}
		}
		// 根据号码获流量虚拟流量信息
		IDataset virtualFlowInfo=Dao.qryByCode("TD_S_VIRTUAL_DATA", "SEL_INFO_BY_SN", input,Route.CONN_CRM_CEN);
		if(IDataUtil.isNotEmpty(virtualFlowInfo)){	
		for (int i = 0; i < virtualFlowInfo.size(); i++) {
		IData info=virtualFlowInfo.getData(i);
		IData virtual= new DataMap();
		virtual.put("RECEIPT_ID", "X"+info.getString("TRANSACTION_ID"));//虚拟流量加个标识
		virtual.put("ACTION_DATE", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("ACTION_TIME")));
		virtual.put("AMOUNT", info.getString("PAYMENT"));
		virtual.put("CHANNEL_ID","07");
		virtual.put("PAY_TYPE", "01");
		virtual.put("RECEIPT_STATUS", "0");
		virtual.put("PAY_INFO", info.getString("VIRT_FLOW")+"M");
		// 获取电子发票打印日志,有日志则说明已经打印过
		IData queryData=new DataMap();
		queryData.put("TRADE_ID", info.getString("TRANSACTION_ID"));
	    IDataset printPdfLog = Dao.qryByCode("TF_B_PRINTPDF_LOG", "SELECT_PRINTPDFLOG_BY_TRADEID", queryData);
		if (IDataUtil.isNotEmpty(printPdfLog)) 
		{
			virtual.put("RECEIPT_STATUS", "1");		
			virtual.put("REQUEST_ID", printPdfLog.getData(0).getString("PRINT_ID", ""));
			virtual.put("PRINT_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, printPdfLog.getData(0).getString("IN_DATE", "")));
		}										
		receiptInfos.add(virtual);	
		}
		}
		result.put("RECEIPT_INFOS", receiptInfos);
		return result;
	}
	
	private String  getFlowNumInfo(String tradeId) throws Exception {
	  String flowNum="0";
	  IDataset tradeInfos=TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
	  if(IDataUtil.isNotEmpty(tradeInfos)){
		for(int i=0;i<tradeInfos.size();i++){
			String discntId= tradeInfos.getData(i).getString("DISCNT_CODE","");
			IDataset dataset = CommparaInfoQry.getCommparaByAttrCode1("CSM","2788",discntId,"ZZZZ",null);
			if(IDataUtil.isNotEmpty(dataset)){
			flowNum=dataset.getData(0).getString("PARA_CODE20");//存流量直冲的流量兆数，单位为M
			}
		}
	  }
	 return flowNum;	
	}
	public void insertTable(IData input,String tabName,String route) throws Exception {
		Dao.insert(tabName, input, route);
	}
	/**
	 * 获取费用打印配置信息(费用打印纸张，合并配置)
	 * 
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	private IData getNotePrintCfg(IData inParam) throws Exception {
		IData param = new DataMap();

		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "9901");
		param.put("PARAM_CODE", "CNOTE");
		param.put("PARA_CODE1", inParam.getString("TRADE_TYPE_CODE"));
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

		IDataset paramList = ParamInfoQry.getPrintCfg(param);

		if (IDataUtil.isEmpty(paramList)) {
			return new DataMap();
		}

		return paramList.getData(0);
	}

	/**
	 * 电子发票
	 * 
	 * @param printData
	 * @return
	 * @throws Exception
	 */
	public IDataset printKJ4SC(IData printData) throws Exception {
		String rspCode="0000";
		String mess="发票开具成功";
		IDataset rs=null;
      try{
    	 String oldtradeId=printData.getString("TRADE_ID");
    	 String newtradeId=oldtradeId.startsWith("X")?oldtradeId.substring(1):oldtradeId;
  		printData.put("TRADE_ID", newtradeId);
 		checkBefore(printData);
		IData userInfo=UcaInfoQry.qryUserInfoBySn(printData.getString("SERIAL_NUMBER"));
		if(IDataUtil.isEmpty(userInfo)){
	      CSAppException.apperr(CrmUserException.CRM_USER_1);  
		}
		String userId=userInfo.getString("USER_ID","");
		String eparchyCode=userInfo.getString("EPARCHY_CODE","");
		
		
		// 默认CUST_NAME为服务号码
		//printData.put("CUST_NAME", printData.getString("SERIAL_NUMBER"));
		String strPrintId = SeqMgr.getPrintId(); // 新生成发票PRINT_ID
		printData.put("PRINT_ID", strPrintId);
		printData.put("TYPE", "P0001"); // 票据类型：发票P0001、收据P0002、免填单(业务受理单)P0003
		printData.put("CUST_TYPE", "PERSON");//客户类型
		printData.put("APPLY_CHANNEL", "0");// 开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
		
		String containOperFlag = "1";
		String containForegiftFlag = "0";
		String containAdvanceFlag = "0";
		printData.put("MERGE_FEE_LIST_OPER_FLAG", containOperFlag);
		printData.put("MERGE_FEE_LIST_FOREGIFT_FLAG", containForegiftFlag);
		printData.put("MERGE_FEE_LIST_ADVANCE_FLAG", containAdvanceFlag);
		
		//获取推送信息  
		 IData inparam = new DataMap();
	        inparam.put("USER_ID",userId);
	        inparam.put("EPARCHY_CODE", eparchyCode);
	        inparam.put(Route.ROUTE_EPARCHY_CODE, inparam.getString("EPARCHY_CODE"));	        
	        IDataset ttinfos = CSAppCall.call("SS.ModifyEPostInfoSVC.qryEPostInfoByUserId", inparam);
	        if (null != ttinfos && ttinfos.size() > 0) {
	        	for (int i = 0; i < ttinfos.size(); i++) {
	        		IData tmp = ttinfos.getData(i);
	        		if ("2".equals(tmp.getString("POST_TAG"))||"0".equals(tmp.getString("POST_TAG"))) { //日常营业电子设置
	        			printData.put("RECEIVER_SENDWAY", tmp.getString("POST_CHANNEL"));
	        			printData.put("RECEIVER_MOBILE", tmp.getString("RECEIVE_NUMBER"));
	        			printData.put("RECEIVER_EMAIL", tmp.getString("POST_ADR"));
	        	    	break;
	        		}
	        	}
	        }
		IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(printData.getString("TRADE_ID"));//流量充值
		IData virtualInfo=VirtualDataInvalidBean.getVirtualInfos(printData);
		if(IDataUtil.isNotEmpty(mainTradeInfos)){//流量直冲
		IData mainTradeInfo=mainTradeInfos.getData(0);
		  printData.put("TOTAL_MONEY",mainTradeInfo.getString("RSRV_STR5",""));
		  String rsrv4=mainTradeInfo.getString("RSRV_STR4","");
		  IData set = StaticInfoQry.getStaticInfoByTypeIdDataId("PLAT_ELEC_INVOICE", rsrv4);
//        String dataName=StaticUtil.getStaticValue("PLAT_ELEC_INVOICE", rsrv4);
          printData.put("NAME","流量直充");
          printData.put("TAG","LLZC");
          if(IDataUtil.isNotEmpty(set)){
        	printData.put("NAME",set.getString("DATA_NAME",""));
        	printData.put("TAG",set.getString("PDATA_ID",""));
          }
		  printData.putAll(mainTradeInfo);
		}else if(oldtradeId.startsWith("X")&&IDataUtil.isNotEmpty(virtualInfo)){//虚拟流量
		  printData.put("TOTAL_MONEY",virtualInfo.getString("PAYMENT",""));
		  printData.put("NAME","虚拟流量");
		  printData.put("TAG","XNLL");
		  printData.putAll(virtualInfo);
		}else{
			CSAppException.apperr(PrintException.CRM_PRINT_2);
		}		
		//流量直冲不在CRM缴费，费用放在RSRV_STR5字段		
		KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
		rs=bean.printKJForSC(printData);
      }catch(Exception ex){
    	rspCode="9999";
    	mess=ex.getMessage(); 
      }
		IDataset ret=new DatasetList();
		IData info=new DataMap();		
		info.put("RSP_CODE", rspCode);
		info.put("RSP_DESC", mess);
		if(IDataUtil.isNotEmpty(rs)){
			info.put("PRINT_ID", rs.getData(0).getString("PRINT_ID",""));
		}
		ret.add(info);
		return ret;
	}

	/**
	 * 校验是否已经开具过发票
	 * 
	 * @param input
	 * @throws Exception
	 */
	private void checkBefore(IData input) throws Exception {

		String strErrInfo = "该业务[" + input.getString("TRADE_ID") + "]已经打印过发票，不能重复打印！";

		IDataset printPdfLog = Dao.qryByCode("TF_B_PRINTPDF_LOG", "SELECT_PRINTPDFLOG_BY_TRADEID", input);
		if (IDataUtil.isNotEmpty(printPdfLog)) {
			CSAppException.appError("-2", strErrInfo);
		}
	}
	public static IData getVirtualInfos(IData printData) throws Exception {
		IData info=new DataMap();
		String serialNumber=printData.getString("SERIAL_NUMBER");
//		printData.put("TRANSTION_ID", printData.getString("TRADE_ID"));
		IDataset virtualFlowInfo=Dao.qryByCode("TD_S_VIRTUAL_DATA", "SEL_INFO_BY_TRADEID", printData,Route.CONN_CRM_CEN);//虚拟流量
		if(IDataUtil.isNotEmpty(virtualFlowInfo)){//有虚拟流量购买记录
			IData virtualInfo=virtualFlowInfo.getData(0);
			info.putAll(virtualInfo);
			info.put("ACCEPT_MONTH", virtualInfo.getString("ACTION_DATE").substring(5,7));//取月份
			info.put("ACCEPT_DATE",  Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND, virtualInfo.getString("ACTION_TIME")));
			info.put("IN_MODE_CODE", "7");//移动商城
			info.put("ORDER_ID", virtualInfo.getString("ORDER_NO"));
			info.put("TRADE_STAFF_ID", printData.getString("TRADE_STAFF_ID"));
			info.put("TRADE_DEPART_ID", printData.getString("TRADE_DEPART_ID"));
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			info.put("ACCT_ID", uca.getAcctId());
			info.put("CUST_NAME", uca.getCustPerson().getCustName());
			info.put("EPARCHY_CODE", uca.getUser().getEparchyCode());
			info.put("CUST_ID", uca.getCustId());	
			info.put("USER_ID", uca.getUser().getUserId());	
		}		
		return info;
	}
	
}
