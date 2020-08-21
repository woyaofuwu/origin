
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.PrintAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;

public class NotePrintBean extends CSBizBean
{

    public static final String PRT_NORMAL_TAG = "1";

    public static final String PRT_ONENOTE_TAG = "2";

    /**
     * 校验票据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void chkStaffPrintTicket(IData input) throws Exception
    {
    	input.put("FEE_LIST", new DatasetList(input.getString("TICKET_FEE", "[]")));
        IData inData = StateTaxUtil.genStateTaxPackage(input);
        
        /**
         * REQ201501270018关于票据作废界面走域权控制的优化
         * 2015-03-27 CHENXY3
         * 由于冲红需要重打发票，而冲红的资源接口是在获得O_TICKET_NO后才对旧票进行冲红操作，因此
         * 根据要传CHECK_RIGHT_CLASS，必须确认是O_TICKET_ID和O_TAX_NO不为空即为冲红，才传
         * 正常打印不传CHECK_RIGHT_CLASS,只有冲红重打才传，冲红需要重打发票
         * 
        if(input.containsKey("CHECK_RIGHT_CLASS")){
        	inData.put("CHECK_RIGHT_CLASS", input.getString("CHECK_RIGHT_CLASS",""));
        }
        */
        inData.put("TRADE_ID", input.getString("TRADE_ID",""));
        inData.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE",""));
        inData.put("TRADE_TYPE_NAME", input.getString("TRADE_TYPE_NAME",""));
        input.remove("TICKET_FEE");
        input.remove("FEE_LIST");
        
        IData data = new DataMap();
        IDataset invoiceInfos = new DatasetList();
        IData invoiceData = new DataMap();
        IDataset ticketInfos = ResCall.stateTaxInvoiceCheck(inData);
        if (IDataUtil.isNotEmpty(ticketInfos))
        {
            data = ticketInfos.getData(0);
            invoiceInfos = data.getDataset("OUTDATA");
            invoiceData = invoiceInfos.getData(0);
        }
        if (IDataUtil.isEmpty(invoiceInfos) || !StringUtils.equals("0000", invoiceInfos.getData(0).getString("RETURNCODE")))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_25, data.getString("RETURNMSG"));
        }
        input.putAll(invoiceData);
    }

    /**
     * 获取员工的票据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getStaffPrintTicket(IData input) throws Exception
    {
        IData data = new DataMap();
        String feeMode = input.getString("FEE_MODE");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "0");
        String ticketTypeCode = getTicketTypeCode(feeMode, tradeTypeCode, 
        		StringUtils.equals(input.getString("ADVANCE_TICKET", "0"), "1"));
        
        if(tradeTypeCode.equals("237")|| tradeTypeCode.equals("461")){	//如果是营销活动终止，使用收据的打印方式
        	ticketTypeCode="B";
        }
        
        
        IDataset invoiceInfos = new DatasetList();
        IDataset ticketInfos = ResCall.getStaffPrintTicket(getVisit().getStaffId(), ticketTypeCode);
        if (IDataUtil.isNotEmpty(ticketInfos))
        {
            data = ticketInfos.getData(0);
            invoiceInfos = data.getDataset("OUTDATA");
        }

        if (IDataUtil.isEmpty(invoiceInfos) || !StringUtils.equals("0000", invoiceInfos.getData(0).getString("RETURNCODE")))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_23);
        }
        
        IData invoiceData = new DataMap();
        invoiceData=invoiceInfos.getData(0);
        return invoiceData;
    }

    /**
     * 解析票据类型
     * 
     * @param feeMode
     * @param tradeTypeCode
     * @return
     */
    public String getTicketTypeCode(String feeMode, String tradeTypeCode, boolean advancePayTicket)
    {
        String ticketTypeCode = "";
        int tradeType = Integer.parseInt(tradeTypeCode);
        if (StringUtils.equals(feeMode, "0") || advancePayTicket)
        {
            ticketTypeCode = tradeType > 9000 ? "E" : "D";
        }
        else if (StringUtils.equals(feeMode, "1"))
        {
            ticketTypeCode = tradeType > 9000 ? "F" : "B";
        }
        else if (StringUtils.equals(feeMode, "2"))
        {
            ticketTypeCode = tradeType > 9000 ? "F" : "B";
        }
        return ticketTypeCode;
    }

    public void registerTicketTrade(IData params) throws Exception
    {
        IData inparams = new DataMap();
        String kprq = params.getString("KPRQ", ""); // 开票日期 YYYYMMDDhh24MiSS例如：20140504090803如果是作废发票填写作废日期
        String sysdate = SysDateMgr.getSysTime();
        String instId = SeqMgr.getPrintId();
        String tradeTypeCode = params.getString("TRADE_TYPE_CODE", "");
        String tickeState = "0"; // 非发票重打
        String tradeId = params.getString("TRADE_ID", "");

        // ---2010-09-30跟吴丽甘确认，负值、零值发票不打印--
        long totalFee = params.getLong("TOTAL_FEE", 0);
        if (totalFee > 0 || (totalFee < 0 && StringUtils.equals(params.getString("CH_TICKET", "0"), "1")))
        {
            inparams.put("PRINT_ID", instId);
            inparams.put("PARTITION_ID", tradeId.substring(tradeId.length() - 4));
            inparams.put("TRADE_ID", tradeId);
            if ("".equals(kprq))
            {
                inparams.put("ACCEPT_MONTH", sysdate.substring(5, 7));
                inparams.put("ACCEPT_TIME", sysdate);
            }
            else
            {// 打印发票
                inparams.put("ACCEPT_MONTH", kprq.substring(4, 6));
                inparams.put("ACCEPT_TIME", 
                		SysDateMgr.date2String(SysDateMgr.string2Date(kprq, SysDateMgr.PATTERN_STAND_SHORT), SysDateMgr.PATTERN_STAND));
            }
            inparams.put("FEE", totalFee);
            inparams.put("TICKET_ID", params.getString("TICKET_ID", "")); // 发票编号每一张正常的话就是加一
            inparams.put("TAX_NO", params.getString("TAX_NO", "")); // 税务登记号，一本发票一般就只有一个税务登记号
            inparams.put("FEE_MODE", params.getString("FEE_MODE"));
            inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
            inparams.put("TRADE_STAFF_ID", getVisit().getStaffId());
            inparams.put("TRADE_DEPART_ID", getVisit().getDepartId());
            inparams.put("TRADE_CITY_CODE", getVisit().getCityCode());
            inparams.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            inparams.put("TICKET_TYPE_CODE", params.getString("TICKET_TYPE_CODE", "")); // 票据类别
            inparams.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER", ""));
            inparams.put("USER_ID", params.getString("USER_ID", ""));
            if(StringUtils.equals(params.getString("CH_TICKET", "0"), "1")){
            	 tickeState = "6";
            }  

            inparams.put("TICKET_STATE_CODE", tickeState); // 发票状态
            inparams.put("SECURITY_CODE", params.getString("FWM", "")); // 防伪码
            inparams.put("QR_CODE", params.getString("EWM", "")); // 二维码

//            Dao.insert("TF_B_TICKET", inparams);
            Dao.insert("TF_B_TICKET", inparams,Route.getJourDb(BizRoute.getRouteId()));

        }
    }

    /**
     * 登记打印票据日志
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData regPrintTicketLog(IData input) throws Exception
    {
        IData data = new DataMap();
        
        String tradeId = input.getString("TRADE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String ticketId = input.getString("TICKET_ID");
        String taxNo = input.getString("TAX_NO");
        String fee = input.getString("TOTAL_FEE");

        String feeMode = input.getString("FEE_MODE");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "0");
        String ticketTypeCode = getTicketTypeCode(feeMode, tradeTypeCode, 
        		StringUtils.equals(input.getString("ADVANCE_TICKET", "0"), "1"));
        input.put("TICKET_TYPE_CODE", ticketTypeCode);
        
        if(tradeTypeCode.equals("237")|| tradeTypeCode.equals("461")){	//如果是营销活动终止，使用收据的打印方式
        	ticketTypeCode="B";
        	input.put("TICKET_TYPE_CODE", "B");
        }
        /**
         * REQ201503040003关于资源侧发票使用界面走权限控制的优化
         * 2015-03-27 CHENXY3
         * 新增调用接口传入 TRADE_TYPE_CODE   TRADE_TYPE_NAME 
         * */
        String tradeTypeName ="";
        if(!"".equals(tradeTypeCode)){
        	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
        }
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        input.put("TRADE_TYPE_NAME", tradeTypeName);
        input.put("TICKET_TYPE_CODE", ticketTypeCode);
        
        // 校验票据
        chkStaffPrintTicket(input);
        

        // 占用当前票据号
        String tradeTypeCodes=tradeTypeCode+"|"+tradeTypeName;
        StateTaxUtil.receiptConfirm(taxNo, ticketId, tradeTypeCodes, ticketTypeCode, tradeId, serialNumber, fee,
        		input.getString("ADVANCE_TICKET", "0"),input.getString("CH_TICKET", "0"));
        
        // 登记票据信息台账记录
        registerTicketTrade(input);
        // 更新其他打印标记或者相关参数
        updateOtherTicketTrade(input);

        //发票补打&冲红操作
        printCHTicketOper(input);

        IData outData = new DataMap();
        outData.put("SECURITY_CODE", input.getString("FWM"));
        outData.put("EWM", input.getString("EWM"));
        outData.put("TAX_NO", input.getString("TAX_NO"));
        outData.put("TICKET_ID", input.getString("TICKET_ID"));
        outData.put("RESULT_CODE", "0");
        return outData;
    }

	/**
     * 打印标识更新 根据TRADE_ID更新打印标记，多个TRADE_ID以逗号连接 TF_B_TRADE_CNOTE_INFO.RSRV_TAG3
     * 
     * @param data
     *            [TRADE_ID,EPARCHY_CODE,EDOC_PRINT]
     * @return
     * @throws Exception
     */
    public boolean updataCnoteTag(IData data) throws Exception
    {
        String tradeId = data.getString("TRADE_ID", "");
        String eparchyCode = data.getString("EPARCHY_CODE", getTradeEparchyCode());
        String[] tradeIds = tradeId.split(",");
        String sqlRef = "UPDATE_STATETO1_BY_TRADEID";
        
        String rsrvTag1=null;	//纸质打印免填单为1
        String rsrvTag2=null;	//电子打印麦田单位1
       
        //获取是否电子打印
        boolean eDocPrint=data.getBoolean("EDOC_PRINT", false);
        
        if (eDocPrint)	//电子打印
        {
        	rsrvTag2="1";
            sqlRef = "UPDATE_STATETO2_BY_TRADEID";
            
            if(!data.getString("page", "").equals("")&&
            		data.getString("page", "").indexOf("batprintinvoice")!=-1){		//说明是从电子工单补打进行补打电子免填单
            	rsrvTag2="";
            }
            
        }else{
        	rsrvTag1="1";
        }
        
        if (StringUtils.isNotBlank(tradeId))
        {
            for (int i = 0, max = tradeIds.length; i < max; i++)
            {
                IData param = new DataMap();
                param.put("TRADE_ID", tradeIds[i]);
                param.put("RSRV_TAG1", rsrvTag1);
                param.put("RSRV_TAG2", rsrvTag2);
                
//                Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", sqlRef, param, eparchyCode);
                Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", sqlRef, param, Route.getJourDb(BizRoute.getRouteId())); 
                   //更新工单为可执行状态
                   //this.updateTradeInfo(tradeIds[i]);
                   //this.checkAndUpdateOrderState(tradeIds[i]);
            }
            
        }
        return true;
    }
    
    public void updateTradeInfo(String tradeId)throws Exception
    {
    	IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
    	
    	if(IDataUtil.isNotEmpty(tradeInfos))
    	{
    		IData tradeInfo = tradeInfos.getData(0);
    		if(StringUtils.equals("Y", tradeInfo.getString("SUBSCRIBE_STATE")))
    		{
    			StringBuilder sql = new StringBuilder(300);
    			sql.append("UPDATE TF_B_TRADE  ");
    			sql.append("SET SUBSCRIBE_STATE =:SUBSCRIBE_STATE ");
    			sql.append("WHERE TRADE_ID = :TRADE_ID ");
    			sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
    			sql.append("AND SUBSCRIBE_STATE = 'Y' ");
    			
    			IData param = new DataMap();
    			param.put("TRADE_ID", tradeId);
    			param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
    			param.put("SUBSCRIBE_STATE", "0");
    			
    			Dao.executeUpdate(sql, param,Route.getJourDb());
    		}
    	}
    }

    /**
     * 根据订单编号去更新台账打印记录
     * 
     * @param data
     *            [ORDER_ID,EPARCHY_CODE,EDOC_PRINT]
     * @return
     * @throws Exception
     */
    public boolean updataCnoteTagByOrder(IData data) throws Exception
    {
        boolean resultFlag = true;
        IData param = new DataMap();
        List<RegTradeData> rtds = new ArrayList<RegTradeData>();
        RegOrderData rod = new RegOrderData(data.getString("ORDER_ID"));

        rtds.add(rod.getMainRegData());
        // 判断是否为融合业务，如果不是，则直接返回
        if (null != rod.getOtherRegData() && !rod.getOtherRegData().isEmpty())
        {
            rtds.addAll(rod.getOtherRegData());
        }
        // 更新其他台账打印标记
        for (RegTradeData rtd : rtds)
        {
            param.clear();
            param.put("TRADE_ID", rtd.getTradeId());
            param.put("EDOC_PRINT", data.getBoolean("EDOC_PRINT"));
            param.put("EPARCHY_CODE", rtd.getMainTradeData().getEparchyCode());
            resultFlag = resultFlag && updataCnoteTag(param);
        }
        
        this.updateOrderState(data);
        return resultFlag;
    }

    /**
     * 更新打印标记
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public boolean updataPrintTag(IData input) throws Exception
    {
        boolean resultFlag = false;
        if (input.containsKey("ORDER_ID"))
        {
            resultFlag = updataCnoteTagByOrder(input);
        }
        else if(input.containsKey("TRADE_ID"))
        {
        	resultFlag = updataCnoteTag(input);
        }
        return resultFlag;
    }
    
    
    public IDataset getNotPrintedInfo(IData input) throws Exception
    {
    	IDataset result = new DatasetList();
    	result.addAll(getNotPrintedInfoByTradeId(input));

    	return result;
    }
    public IDataset getNotPrintedInfoByOrderId(IData input)throws Exception
    {
    	IDataset printedInfo = new DatasetList();
    	IData param = new DataMap();
        List<RegTradeData> rtds = new ArrayList<RegTradeData>();
        RegOrderData rod = new RegOrderData(input.getString("ORDER_ID"));

        rtds.add(rod.getMainRegData());
        // 判断是否为融合业务，如果不是，则直接返回
        if (null != rod.getOtherRegData() && !rod.getOtherRegData().isEmpty())
        {
            rtds.addAll(rod.getOtherRegData());
        }
        // 更新其他台账打印标记
        for (RegTradeData rtd : rtds)
        {
            param.clear();
            param.put("TRADE_ID", rtd.getTradeId());
            param.put("EPARCHY_CODE", rtd.getMainTradeData().getEparchyCode());
            printedInfo.addAll(getNotPrintedInfoByTradeId(param));
        }
    	return printedInfo;
    }
    public IDataset getNotPrintedInfoByTradeId(IData input)throws Exception
    {
    	String tradeId = input.getString("TRADE_ID");
    	String orderId = input.getString("ORDER_ID");
    	
    	IData orderInfo = TradeInfoQry.getOrderByOrderId(orderId);
    	if(IDataUtil.isNotEmpty(orderInfo))
    	{
    		String orderState = orderInfo.getString("ORDER_STATE");
    		if(StringUtils.equals("Y", orderState))
    		{
    			IData param = new DataMap();
    	    	param.put("TRADE_ID", tradeId);
    	    	param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
    	    	StringBuilder sql = new StringBuilder(100);
    	    	sql.append("SELECT * FROM TF_B_TRADE_CNOTE_INFO WHERE TRADE_ID =:TRADE_ID AND ACCEPT_MONTH =ACCEPT_MONTH AND RSRV_TAG1 IS NULL AND RSRV_TAG2 IS NULL");
    	    	return Dao.qryBySql(sql, param, Route.getJourDb());
    		}
    	}
    	return new DatasetList();
    }

    /**
     * 更新其他票据打印后表记录信息
     * 
     * @param input
     * @throws Exception
     */
    public void updateOtherTicketTrade(IData input) throws Exception
    {
        String nonBossFeeTradeId = input.getString("NONBOSSFEE_TRADE_ID", "");
        if (StringUtils.isBlank(nonBossFeeTradeId))
        {
            return;
        }
        String tradeId = input.getString("TRADE_ID"); // 生成的虚拟流水，主要是关联非BOSS收款记录与票据记录，方便后面返销判断
        String prtTag = "printed"; // 已打印标记
        String[] logs = nonBossFeeTradeId.split(",");
        for (int i = 0; i < logs.length; i++)
        {
            if (StringUtils.isBlank(logs[i]))
            {
                continue;
            }
            IData param = new DataMap();
            param.put("LOG_ID", logs[i]);
            param.put("TRADE_ID", tradeId); // 更新本次打印统一批次流水
            param.put("RSRV_STR4", prtTag); // 更新打印标记
            Dao.save("TF_F_NONBOSSFEE_LOG", param);
        }        
    }
    
    /**
     * @Description 发票补打&冲红操作
     * 
     * @param input
     * @throws Exception
     */
    public void printCHTicketOper(IData input) throws Exception{
    	if(StringUtils.equals("1", input.getString("BD_CH_FLAG",""))){
        	if(StringUtils.equals("1", input.getString("CH_TICKET",""))){//发票冲红处理
        		IData param = new DataMap();
                param.put("TRADE_ID", input.getString("TRADE_ID", ""));
                param.put("OPER_TYPE", "7");// 发票冲红标志
                param.put("TICKET_STATE_CODE", "0");
                param.put("TICKET_TYPE_CODE", input.getString("TICKET_TYPE_CODE", ""));
                param.put("FEE_MODE", input.getString("FEE_MODE", ""));

                // 更新原有发票记录,一个台账只会有一个TICKET_TYPE_CODE=0&FEE_MODE&TICKET_STATE_CODE的记录
//                int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param);
                int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param,Route.getJourDb(BizRoute.getRouteId()));
                if (num < 1)
                    CSAppException.apperr(TicketException.CRM_TICKET_6, input.getString("TRADE_ID", ""), input.getString("FEE_MODE", ""), "0",
                    		input.getString("TICKET_TYPE_CODE", ""));

                if (StringUtils.equals("2", input.getString("FEE_MODE", "")) && 
                		!StringUtils.equals("9721", input.getString("TRADE_TYPE_CODE",""))){
                    // 通知账务更新预存款可打金额
                    StateTaxUtil.updateAcctPrintFee(input.getString("SERIAL_NUMBER", ""), input.getString("TRADE_ID", ""), 
                    		input.getString("TOTAL_FEE", "").substring(1), "false");
                }
        	}else{//发票补打处理
        		if(StringUtils.equals("1", input.getString("ADVANCE_TICKET",""))){//预存款补打发票
        			IDataset printedTickets = TicketInfoQry.qryTradeTickets(input.getString("TRADE_ID",""));
        			boolean advancePayTicket = false;
        			for(int i=0,size=printedTickets.size();i<size;++i){
        				if(StringUtils.equals("2",printedTickets.getData(i).getString("FEE_MODE","")) && 
        						TaxUtils.voucherTicket(printedTickets.getData(i).getString("TICKET_TYPE_CODE",""))){
        					advancePayTicket = true;
        					break;
        				}
        			}
        			
					if (advancePayTicket) {// 已经打印过收据
						// 预存款补打发票时,更新原来的收据票据状态
						IData updParam = new DataMap();
						updParam.put("TRADE_ID", input.getString("TRADE_ID", ""));
						updParam.put("OPER_TYPE", "5");// 收据换成发票标志
						updParam.put("TICKET_STATE_CODE", "0");
						String advancePayTicketTypeCode = "F";
						if (StringUtils.equals(input.getString("TICKET_TYPE_CODE", ""), "D"))// 如果当前打印移动发票的话,则更新移动收据
							advancePayTicketTypeCode = "B";

						updParam.put("TICKET_TYPE_CODE",advancePayTicketTypeCode);
						updParam.put("FEE_MODE", input.getString("FEE_MODE", ""));

						// 更新原有收据发票记录,第一次收据补打发票时更新成功,后续更新记录为0,所以此处不做返回值判断
//						int num = Dao.executeUpdateByCodeCode("TF_B_TICKET","UPDATE_TICKET_STATE_CODE", updParam);
						int num = Dao.executeUpdateByCodeCode("TF_B_TICKET","UPDATE_TICKET_STATE_CODE", updParam,Route.getJourDb(BizRoute.getRouteId()));
						if (num < 1)
							CSAppException.apperr(TicketException.CRM_TICKET_6,input.getString("TRADE_ID", ""), input.getString("FEE_MODE", ""), "0",
									advancePayTicketTypeCode);
					}
                    
                	if(!StringUtils.equals("9721", input.getString("TRADE_TYPE_CODE",""))){//商务电话开户,费用不同步给账务,不需要更新预存款可打金额
                		StateTaxUtil.updateAcctPrintFee(input.getString("SERIAL_NUMBER", ""), input.getString("TRADE_ID", ""), 
                				"-" + input.getString("TOTAL_FEE", ""), "false");
                	}
        		}
        	}
        }
    }
    
    /**
     * 更新打印标记
     * 
     * @param input
     * @return
     * @author wukw3
     * @throws Exception
     */
    public boolean errorLog(IData input) throws Exception
    {
    	IData inparams = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String instId = SeqMgr.getPrintId();
        
        String trade_id = input.getString("TRADE_ID",SeqMgr.getPrintId());
        String error_info = input.getString("ERROR_INFO","");
        if(error_info.length()>2000){
        	error_info = error_info.substring(0, 1999);
        }
        String error_detail = input.getString("ERROR_DETAIL","");
        if(error_detail.length()>2000){
        	error_detail = error_detail.substring(0, 1999);
        }
        inparams.put("TRADE_ID", trade_id);
        inparams.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER",""));
        inparams.put("RESULTE_CODE", input.getString("ERROR_CODE",""));
        inparams.put("ERROR_INFO", error_info);
        inparams.put("ERROR_DETAIL", error_detail);
        inparams.put("CREATE_TIME", sysdate);

        return Dao.insert("TL_B_PRINT_LOG", inparams,Route.getJourDb(BizRoute.getRouteId()));
//        return Dao.insert("TL_B_PRINT_LOG", inparams);
    }
    public void changePrintPDFlog(IData data) throws Exception
    {
    	    	
        IData param = new DataMap();
        param.put("PRINT_ID", data.getString("PRINT_ID"));
        param.put("PRINT_FLAG", "1");        
        Dao.executeUpdateByCodeCode("TF_B_PRINTPDF_LOG", "UPD_BY_PRINT_ID", param,Route.getJourDb(BizRoute.getRouteId()));

    }
    
    
    /**
     * 获取照片信息设置照片获取标志
     * @param input
     * @return
     * @throws Exception
     */
    public IData getPicTag(IData input) throws Exception 
    {   
    	IData param = new DataMap();
    	IData result = new DataMap();
    	result.put("PIC_TAG", "1");
    	
    	String sn = input.getString("SERIAL_NUMBER","");
    	String tradeId = input.getString("TRADE_ID","");
    	if(StringUtils.isBlank(tradeId)){
    		//根据手机号码业务类型获取客户ID
    		IData in = new DataMap();
    		in.put("SERIAL_NUMBER", sn);
    		in.put("CANCEL_TAG", "0");
    		in.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE",""));
    		IDataset tradeIdInfo = Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN", in);
    		if(tradeIdInfo != null && tradeIdInfo.size() > 0){
    			tradeId = tradeIdInfo.getData(0).getString("TRADE_ID","");
    		}
    	} 	
    	param.put("TRADE_ID", tradeId);
    	//新客户照片信息查询
    	param.put("PIC_ID_TAG", "PIC_ID");    	
    	IDataset picinfo = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_USERID_RSRVVALUE", param, Route.getJourDbDefault());
    	
    	if(null != picinfo && !picinfo.isEmpty())
    	{
    		String picid = picinfo.getData(0).getString("RSRV_STR1","");
    		if(StringUtils.isNotBlank(picid))
    		{//新客户照片ID存在
    			result.put("PIC_TAG", "0");
    		}else{
    			result.put("PIC_TAG", "1");
    		}
    		String agentpicid = picinfo.getData(0).getString("RSRV_STR3","");
    		if(StringUtils.isNotBlank(agentpicid))
    		{//经办人照片ID存在
    			result.put("AGENT_PIC_TAG", "0");
    		}else{
    			result.put("AGENT_PIC_TAG", "1");
    		}
    		if("100".equals(input.getString("TRADE_TYPE_CODE","")))
    		{//过户业务时，查询原客户照片信息
    			
    			String formpicid = picinfo.getData(0).getString("RSRV_STR2","");
    			if(StringUtils.isNotBlank(formpicid))
    			{//原客户照片ID存在
    				result.put("FORM_PIC_TAG", "0");
    			}else{
    				result.put("FORM_PIC_TAG", "1");
    			}
    		}
    	}
    	return result;
    } 
    
    /**
     * 检查是否有打印数据生成，没有打印数据就直接改订单状态为0
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset checkReceiptAndSetTradeState(IData inParam)throws Exception
    {
    	 String orderId = inParam.getString("ORDER_ID");
    	 String acceptMonth = StrUtil.getAcceptMonthById(orderId);
    	 
    	 StringBuilder sql = new StringBuilder(1000);
    	 sql.append("SELECT ORDER_ID,TRADE_ID,SUBSCRIBE_STATE FROM TF_B_TRADE WHERE ORDER_ID =:ORDER_ID AND ACCEPT_MONTH = :ACCEPT_MONTH ");
    	 
    	 StringBuilder sql1 = new StringBuilder(300);
 		 sql1.append("UPDATE TF_B_TRADE  ");
 		 sql1.append("SET SUBSCRIBE_STATE =:SUBSCRIBE_STATE ");
 		 sql1.append("WHERE TRADE_ID = :TRADE_ID ");
 		 sql1.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
 		 sql1.append("AND SUBSCRIBE_STATE = 'Y' ");
 		 
 		 StringBuilder sql2 = new StringBuilder(300);
		 sql2.append("UPDATE TF_B_ORDER  ");
		 sql2.append("SET ORDER_STATE =:ORDER_STATE ");
		 sql2.append("WHERE ORDER_ID = :ORDER_ID ");
		 sql2.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		 sql2.append("AND ORDER_STATE = 'Y' ");
    	 
    	 IData param = new DataMap();
    	 param.put("ORDER_ID", orderId);
    	 param.put("ACCEPT_MONTH", acceptMonth);
    	 
    	 //IDataset tradeInfos = Dao.qryBySql(sql, param, Route.getJourDb(this.getTradeEparchyCode()));
    	 IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
    	 
    	 if(IDataUtil.isNotEmpty(tradeInfos))
    	 {
    		 boolean isPrintReceipt = false;
    		 for(int i=0;i<tradeInfos.size();i++)
    		 {
    			 IData data = tradeInfos.getData(i);
    			 String tradeId = data.getString("TRADE_ID");
    			 String subscribeState = data.getString("SUBSCRIBE_STATE");
    			 
    			 if(StringUtils.equals("0", subscribeState))
    			 {
    				 continue;
    			 }
    			 IDataset tradePrintInfo = TradeReceiptInfoQry.queryPrintNotByTradeId(tradeId);
    			 if(IDataUtil.isEmpty(tradePrintInfo))
    			 {
    				 if(StringUtils.equals("Y", subscribeState))
        			 {
    					param.clear();
    					param.put("TRADE_ID", tradeId);
    					param.put("ACCEPT_MONTH", acceptMonth);
    					param.put("SUBSCRIBE_STATE", "0");
    						
    					Dao.executeUpdate(sql1, param,Route.getJourDb(this.getTradeEparchyCode()));
        			 } 
    			 }
    			 else
    			 {//整个order下有一个需打印，整个oder改为需要打印
    				 isPrintReceipt = true;
    			 }
    		 }
    		 
    		 if(!isPrintReceipt)//无需要打印的trade，直接改状态为0
    		 {
    			param.clear();
				param.put("ORDER_ID", orderId);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("ORDER_STATE", "0");
						
				Dao.executeUpdate(sql2, param,Route.getJourDb(this.getTradeEparchyCode()));
				
				for(int i=0;i<tradeInfos.size();i++)
	    		{
					IData data = tradeInfos.getData(i);
					PrintAction.action(data);
	    		}
    		 }
    	 }
    	 
    	return tradeInfos; 
    }
    
    public boolean checkAndUpdateOrderState(String tradeId)throws Exception
    {
    	boolean returnFlag = true;
    	String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
    	
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
    	param.put("ACCEPT_MONTH", acceptMonth);
    	
    	StringBuilder tradeSql = new StringBuilder(300);
    	tradeSql.append(" SELECT B.ORDER_ID FROM TF_B_TRADE B");
    	tradeSql.append(" WHERE B.TRADE_ID =:TRADE_ID  ");
    	tradeSql.append(" AND B.ACCEPT_MONTH = :ACCEPT_MONTH ");
    	
    	IDataset orderInfos = Dao.qryBySql(tradeSql, param,Route.getJourDb());
    	if(IDataUtil.isNotEmpty(orderInfos))
    	{
    		String orderId = orderInfos.getData(0).getString("ORDER_ID");
    		
    		StringBuilder sql = new StringBuilder(300);
        	sql.append(" SELECT A.TRADE_ID FROM TF_B_TRADE A ,TF_B_TRADE B ");
        	sql.append(" WHERE A.ORDER_ID = B.ORDER_ID  ");
        	sql.append(" AND A.SUBSCRIBE_STATE ='Y' ");
        	sql.append(" AND B.TRADE_ID =:TRADE_ID  ");
        	sql.append(" AND B.ACCEPT_MONTH = :ACCEPT_MONTH ");
        	
        	IDataset tradeInfos = Dao.qryBySql(sql, param,Route.getJourDb());
        	
        	if(IDataUtil.isEmpty(tradeInfos) && StringUtils.isNotBlank(orderId))
        	{
        		IData input = new DataMap();
        		input.put("ORDER_ID", orderId);
        		input.put("ORDER_STATE", "0");
        		this.updateOnlyOrderState(input);
        	}
    	}
    	
    	return returnFlag;
    }
    
    public IDataset updateOnlyOrderState(IData inParam)throws Exception
    {
    	String orderId = inParam.getString("ORDER_ID");
   	 	String acceptMonth = StrUtil.getAcceptMonthById(orderId);
   	 	
   	 	this.excutePrintAction(orderId);
   	 	
   	 	StringBuilder sql = new StringBuilder(300);
		sql.append("UPDATE TF_B_ORDER  ");
		sql.append("SET ORDER_STATE =:ORDER_STATE ");
		sql.append("WHERE ORDER_ID = :ORDER_ID ");
		sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("AND ORDER_STATE = 'Y' ");
		 
		IData param = new DataMap();
    	param.put("ORDER_ID", orderId);
    	param.put("ACCEPT_MONTH", acceptMonth);
    	param.put("ORDER_STATE", inParam.getString("ORDER_STATE","0"));
    	 
    	Dao.executeUpdate(sql, param,Route.getJourDb());
    	
    	return new DatasetList();
    }
    
    public IDataset updateOrderState(IData inParam)throws Exception
    {
    	String orderId = inParam.getString("ORDER_ID");
   	 	String acceptMonth = StrUtil.getAcceptMonthById(orderId);
   	 	
   	 	this.updateTradeState(inParam);
   	 	this.excutePrintAction(orderId);
   	 	
   	 	StringBuilder sql = new StringBuilder(300);
		sql.append("UPDATE TF_B_ORDER  ");
		sql.append("SET ORDER_STATE =:ORDER_STATE ");
		sql.append("WHERE ORDER_ID = :ORDER_ID ");
		sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("AND ORDER_STATE = 'Y' ");
		 
		IData param = new DataMap();
    	param.put("ORDER_ID", orderId);
    	param.put("ACCEPT_MONTH", acceptMonth);
    	param.put("ORDER_STATE", inParam.getString("ORDER_STATE","0"));
    	 
    	Dao.executeUpdate(sql, param,Route.getJourDb());
    	
    	return new DatasetList();
    }
    
    public void excutePrintAction(String orderId)throws Exception
    {
    	IDataset tradeInfos =  TradeInfoQry.queryTradeByOrerId(orderId, "0");
    	if(IDataUtil.isNotEmpty(tradeInfos))
    	{
    		for(int i=0;i<tradeInfos.size();i++)
    		{
    			IData tradeInfo = tradeInfos.getData(i);
    			PrintAction.action(tradeInfo);
    		}
    	}
    }
    
    public IDataset updateTradeState(IData inParam)throws Exception
    {
    	 String orderId = inParam.getString("ORDER_ID");
    	 String acceptMonth = StrUtil.getAcceptMonthById(orderId);
    	 
    	 StringBuilder sql = new StringBuilder(1000);
    	 sql.append("SELECT ORDER_ID,TRADE_ID,SUBSCRIBE_STATE FROM TF_B_TRADE WHERE ORDER_ID =:ORDER_ID AND ACCEPT_MONTH = :ACCEPT_MONTH ");
    	 
    	 StringBuilder sql1 = new StringBuilder(300);
 		 sql1.append("UPDATE TF_B_TRADE  ");
 		 sql1.append("SET SUBSCRIBE_STATE =:SUBSCRIBE_STATE ");
 		 sql1.append("WHERE TRADE_ID = :TRADE_ID ");
 		 sql1.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
 		 sql1.append("AND SUBSCRIBE_STATE = 'Y' ");
    	 
    	 IData param = new DataMap();
    	 param.put("ORDER_ID", orderId);
    	 param.put("ACCEPT_MONTH", acceptMonth);
    	 
    	 IDataset tradeInfos = Dao.qryBySql(sql, param, Route.getJourDb(this.getTradeEparchyCode()));
    	 
    	 if(IDataUtil.isNotEmpty(tradeInfos))
    	 {
    		 for(int i=0;i<tradeInfos.size();i++)
    		 {
    			 IData data = tradeInfos.getData(i);
    			 String tradeId = data.getString("TRADE_ID");
    			 String subscribeState = data.getString("SUBSCRIBE_STATE");
    			 
    			 if(StringUtils.equals("0", subscribeState))
    			 {
    				 continue;
    			 }
				if (StringUtils.equals("Y", subscribeState)) 
				{
					param.clear();
					param.put("TRADE_ID", tradeId);
					param.put("ACCEPT_MONTH", acceptMonth);
					param.put("SUBSCRIBE_STATE", inParam.getString("SUBSCRIBE_STATE","0"));

					Dao.executeUpdate(sql1, param,Route.getJourDb(this.getTradeEparchyCode()));
				}
    		 }
    	 }
    	 
    	return tradeInfos; 
    }
    
    /**
     * REQ201805090016_在线公司电子稽核报表优化需求
     * <br/>
     * IS_MAIN_PRODUCT  0:是主产品变更   1：非主产品变更
     * @author zhuoyingzhi
     * @date 20180518
     * @param input
     * @return
     * @throws Exception
     */
    public IData isMainProductChange(IData input) throws Exception 
    {   
    	IData result = new DataMap();
    	result.put("IS_MAIN_PRODUCT", "1");//默认不是
    	
    	String sn = input.getString("SERIAL_NUMBER","");
    	String tradeId = input.getString("TRADE_ID","");
    	String  tradeTypeCode=input.getString("TRADE_TYPE_CODE","");
    	if(StringUtils.isBlank(tradeId)){
    		//根据手机号码业务类型获取客户ID
    		IData in = new DataMap();
    		in.put("SERIAL_NUMBER", sn);
    		in.put("CANCEL_TAG", "0");
    		in.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE",""));
    		IDataset tradeIdInfo = Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN", in);
    		if(tradeIdInfo != null && tradeIdInfo.size() > 0){
    			tradeId = tradeIdInfo.getData(0).getString("TRADE_ID","");
    		}
    	}
    	String saleActionName="";
    	if("240".equals(tradeTypeCode)){
    		//获取主台帐信息
    		IDataset  tradeInfo=TradeInfoQry.getTradeAndBHTradeByTradeId(tradeId);
    		if(IDataUtil.isNotEmpty(tradeInfo)){
    			saleActionName=tradeInfo.getData(0).getString("RSRV_STR4", "");
    		}
    	}
    	
    	
    	String newProductId="";
    	String productName="";
    	if("110".equals(tradeTypeCode)){
    	   	IDataset tradeProduct=TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        	if(IDataUtil.isNotEmpty(tradeProduct)){
        		//存在主产品变更
        		for(int i=0;i<tradeProduct.size();i++){
        			  IData  product=tradeProduct.getData(i);
        			  if("0".equals(product.getString("MODIFY_TAG",""))){
        				  //取新的产品编码
        				  newProductId=product.getString("PRODUCT_ID","");
        				  //获取新产品的名称
        				  productName=UProductInfoQry.getProductNameByProductId(newProductId);
        				  
        				  result.put("IS_MAIN_PRODUCT", "0");//是
        				  break;
        			  }
        		}
        	}else{
        		//无主产品变更
        		result.put("IS_MAIN_PRODUCT", "1");//是
        	}    		
    	}
 
    	result.put("PRODUCT_ID", newProductId);
    	//新增产品名称
    	result.put("PRODUCT_NAME", productName);
    	
    	//营销活动名称
    	result.put("SALE_ACTION_NAME", saleActionName);
    	
    	return result;
    }   
    
    /**
     * REQ201805090016_在线公司电子稽核报表优化需求
     * <br/>
     * 判断是否为  行业应用卡
     * <br/>
     * IS_HYYYK_BAT_CHOPEN  1:是行业应用卡    0：非行业应用卡
     * @author zhuoyingzhi
     * @date 20180523
     * @param input
     * @return
     * @throws Exception
     */
    public IData isHyyykBatChopen(IData input) throws Exception 
    {   
    	IData param = new DataMap();
    	IData result = new DataMap();
    	result.put("IS_HYYYK_BAT_CHOPEN", "0");//默认不是
    	  
    	String userid = input.getString("USER_ID","");
    	if(!"".equals(userid) && userid!= null){
    		param.put("USER_ID", userid);
    	  	//行业应用卡
        	IDataset userOtherInfo=UserOtherInfoQry.getOtherInfoByCodeUserId(userid, "HYYYKBATCHOPEN");
    		if(IDataUtil.isNotEmpty(userOtherInfo)){
    			result.put("IS_HYYYK_BAT_CHOPEN", "1");//是行业应用卡
    		}
    	}
    	return result;
    }
    /**
     * k3
     * 跨区销户身份证正反面查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryPsptFrontBack(IData input)throws Exception {
    	IData param = new DataMap();
    	IData rtn = new DataMap();
    	param.put("TRADE_ID", input.getString("TRADE_ID"));
    	param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
    	StringBuilder psptQry = new StringBuilder(300);
    	psptQry.append(" SELECT * FROM TF_F_USER_PSPT_FRONTBACK T ");
    	psptQry.append(" WHERE T.TRADE_ID=:TRADE_ID ");
    	psptQry.append(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER ");
    	IDataset psptFrontBack = Dao.qryBySql(psptQry, param,Route.getCrmDefaultDb());
    	if(IDataUtil.isNotEmpty(psptFrontBack)){
    		rtn=psptFrontBack.getData(0);
    		String cardBorn = rtn.getString("BORN");
    		if(StringUtils.isNotBlank(cardBorn)){
    			cardBorn=SysDateMgr.decodeTimestamp(cardBorn, SysDateMgr.PATTERN_TIME_YYYYMMDD);
    			rtn.put("BORN", cardBorn);
    		}
    	}
    	return rtn;
    }

    /**
     * REQ201910180018押金业务受理及清退电子化存储需求
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryForegiftTrade(IData input)throws Exception {
        IData rtn = new DataMap();
        rtn.put("IS_QING_TUI","0");
        String tradeId=input.getString("TRADE_ID");
        String eparchyCode=input.getString("ROUTE_EPARCHY_CODE");
        IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", eparchyCode);
        if(IDataUtil.isEmpty(tradeInfo))
        {
            tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", eparchyCode);
        }
        if(IDataUtil.isNotEmpty(tradeInfo)){

          if("1".equals(tradeInfo.getString("RSRV_STR7"))||"2".equals(tradeInfo.getString("RSRV_STR7"))){
              rtn.put("IS_QING_TUI","1");
              rtn.put("liushuihao",tradeInfo.getString("INVOICE_NO"));
              rtn.put("tuikuanzhonglei",tradeInfo.getString("RSRV_STR9"));
              rtn.put("tuikuanjine",tradeInfo.getString("RSRV_STR10","0"));
              rtn.put("jinedaxie", FeeUtils.floatToRMB(Double.parseDouble(tradeInfo.getString("RSRV_STR10","0"))));
          }

        }
        return rtn;
    }
}
