
package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import java.math.BigDecimal;
 
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.nonbossfee.NonBossFeeLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.PrintDataSetBean;

public class NonBossFeeBean extends CSBizBean
{
    /**
     * 非BOSS收款补录返销
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData cancelNonBossFee(IData input) throws Exception
    {
        String staffId = getVisit().getStaffId();
        String tmpTradeId, tmpLogId;
        StringBuilder tradeSb = new StringBuilder();
        StringBuilder logSb = new StringBuilder();
        IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
        for (int i = 0; i < infos.size(); i++)
        {
            IData map = infos.getData(i);
            if (!(map.getString("TRADE_STAFF_ID", "")).equals(staffId))
            {
                // error("返销操作员工信息与原录入信息不符!");
                //CSAppException.apperr(CrmCommException.CRM_COMM_1110);
            }
            tmpLogId = map.getString("LOG_ID", "");
            tmpTradeId = map.getString("TRADE_ID", "");
            logSb.append(logSb.length() > 0 ? "," + tmpLogId : tmpLogId); 
        }
        IData returnData = new DataMap(); 
        String operMonth = SysDateMgr.getCurMonth();
        String operDate = SysDateMgr.getSysTime();

        if (logSb.length() > 0)
        {
            String[] logs = logSb.toString().split(",");
            for (String logId : logs)
            {
                insertRefundNonBossFee(logId, operDate, operMonth, false);
            }
        }
        /*
        if (tradeSb.length() > 0)
        {
            String[] trades = tradeSb.toString().split(",");
            IDataset nonBossFeeLogs = null;
            for (String tradeId : trades)
            {
                nonBossFeeLogs = NonBossFeeLogInfoQry.queryNonBossFeeLogByTrade(tradeId, "0");
                if (IDataUtil.isEmpty(nonBossFeeLogs))
                {
                    continue;
                }
                // 更新非BOSS收款补录信息状态
                for (int i = 0, size = nonBossFeeLogs.size(); i < size; i++)
                {
                    insertRefundNonBossFee(nonBossFeeLogs.getData(i).getString("LOG_ID", ""), operDate, operMonth, ygzTag);
                }

                // 发票作废
                IDataset ticketInfos = TicketInfoQry.queryTicketInfos(tradeId, staffId);
                if (IDataUtil.isNotEmpty(ticketInfos))
                {
                    for (int i = 0, size = ticketInfos.size(); i < size; i++)
                    {
                        updateTicket(ticketInfos.getData(i));
                    }

                }

            }
        }
        */
        returnData.put("RESULT_CODE", "0");
        return returnData;
    }
    
    /**
     * 非BOSS收款补录--作废、冲红发票
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 2015-03-10 chenxy3
     * 2015-06-15 改 屏蔽调接口，只做更新本地表状态
     */
    public IData cancelInvoice(IData input) throws Exception{
    	IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
    	IData returnData = new DataMap();
    	String staffId = getVisit().getStaffId();
    	String tradeId = "";
    	String logId = "";
    	String cenType= "";
    	for (int i = 0; i < infos.size(); i++){
    		IData map = infos.getData(i); 
    		tradeId = map.getString("TRADE_ID");
    		logId = map.getString("LOG_ID");
    		cenType = map.getString("CENCEL_TYPE").equals("ZF")  ? "2" : "3";
	        // 发票作废、冲红
    		/** 暂时先屏蔽掉调用接口
    		 * 待测通后再启用
            IDataset ticketInfos = TicketInfoQry.queryTicketInfos(tradeId, staffId);
            if (IDataUtil.isNotEmpty(ticketInfos))
            {
                for (int j = 0, size = ticketInfos.size(); j < size; j++)
                {
                    updateTicket(ticketInfos.getData(j));
                }

            }
            */
            //更新TF_F_NONBOSSFEE_LOG的发票状态，作废，冲红 UPD_NONBOSS_CEL_INVOICE
            IData updParam = new DataMap();
            updParam.put("TRADE_ID", tradeId);
            updParam.put("CENCEL_TYPE", cenType ); // 发票作废冲红标记  
            Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", "UPD_NONBOSS_CEL_INVOICE", updParam);
            
            returnData.put("LOG_ID"+i, logId);
    	} 
    	returnData.put("RESULT_CODE", "0");
    	return returnData;
    }

    /**
     * 非BOSS收款补录登记
     * REQ201409250007201409非出账业务收款及发票管理需求
     * chenxy3 @ 2015-03-02
     * @param input
     * @return
     * @throws Exception
     */
    public IData insertNonBossFee(IData input) throws Exception
    {
        IData returnData = new DataMap();
        IDataset dataset = new DatasetList();

        IDataset feeList = input.getDataset("FEE_LIST");
        String operMonth = SysDateMgr.getCurMonth();
        String operDate = SysDateMgr.getSysTime();
        /*
        boolean ygzTag = false;
        if (TaxUtils.isYgzTag())
        {
            ygzTag = true;
            for (int i = 0; i < feeList.size(); i++)
            {
                IData map = feeList.getData(i);
                String feetypecode = map.getString("FEE_NAME");
                IDataset list = CommparaInfoQry.getCommparaInfoByCode("CSM", "3011", "PAY_ITEM_NAME", feetypecode, "ZZZZ");

                if (null != list && list.size() > 0)
                {
                    IData taxInfo = list.getData(0);
                    map.put("TYPE", taxInfo.getString("PARA_CODE2")); // 计税方式
                    map.put("RATE", taxInfo.getString("PARA_CODE3")); // 税率
                    map.put("RSRV_NUM1", taxInfo.getString("PARA_CODE4"));// 混合税率
                    map.put("FACT_PAY_FEE", map.getString("FEE_AMOUNT"));
                    map.put("FEE", map.getString("FEE_AMOUNT"));
                }
                else
                {
                    // error("税率配置错误，请联系管理员!");
                    CSAppException.apperr(CrmCommException.CRM_COMM_1110);
                }
            }
            // 计税
            TaxCalcUtils.getTradeFeeTaxForCalculate(feeList);
        }
        // 营改增 end
         * *
         */ 
        String price = null;
        String feePrice = null;
        String printTrade = "";
        for (int i = 0; i < feeList.size(); i++)
        {
            feePrice = "";
            IData feeInfo = feeList.getData(i);
            /*
            price = feeInfo.getString("FEE_PRICE");
            if (StringUtils.isNotBlank(price))
            {
                float feePriceTmp = Float.parseFloat(price) * 100;
                feePrice = String.valueOf(feePriceTmp);
            }
			*/
            String tradeId = SeqMgr.getTradeId();//获取TRADE_ID
            printTrade += StringUtils.isNotBlank(printTrade) ? "," + tradeId : tradeId;

            IData data = new DataMap();
            data.put("LOG_ID", tradeId); 
            data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            data.put("TRADE_CITY_CODE", getVisit().getCityCode());
            data.put("TRADE_DEPART_ID", getVisit().getDepartId());
            data.put("TRADE_STAFF_ID", getVisit().getStaffId());
            data.put("OPER_MONTH", operMonth);
            data.put("OPER_DATE", operDate);

            data.put("PAY_MODE", feeInfo.get("FEE_PAY_MODE"));
            data.put("PAY_NAME", feeInfo.get("PAY_NAME"));
            data.put("PAY_NAME_REMARK", feeInfo.get("PAY_NAME_REMARK"));

            data.put("OPER_TYPE_NAME", feeInfo.get("FEE_NAME"));
            data.put("OPER_FEE", feeInfo.get("FEE"));
            //data.put("OPER_UNIT", feeInfo.get("FEE_UNIT"));
            //data.put("OPER_PRICE", feePrice);
            //data.put("OPER_NUM", feeInfo.get("FEE_NUM"));
            data.put("REFUND_TAG", "0"); // 0正常,1返销
            
            data.put("RSRV_STR1", feeInfo.get("COST_CENTER"));//成本中心
            data.put("RSRV_STR2", feeInfo.get("INVOICE_TYPE"));//发票项目
            data.put("RSRV_STR3", feeInfo.get("TAX_TYPE"));//应税类型
            
            data.put("REMARK", feeInfo.get("FEE_REMARK"));
            data.put("RSRV_STR5", feeInfo.get("ADD_REMARK"));
            
            /*
            String onlyPrint=""+feeInfo.get("ONLY_PRINT");
            if("√".equals(onlyPrint)){
            	onlyPrint="1";
            }else{
            	onlyPrint="0";
            }*/
            data.put("RSRV_STR4", feeInfo.get("ONLY_PRINT"));

            //if (ygzTag)
            //{
            data.put("NOTAX_FEE", feeInfo.get("FEE_NOTAX")); // 不含税价款
            data.put("TAX_FEE", feeInfo.get("TAX_PRICE")); // 税款
            data.put("TAX_RATE", feeInfo.get("TAX_RATE")); // 税率
            data.put("OPER_TYPE_NAME_DESC", feeInfo.get("FEE_NAME_DESC"));
            
            data.put("RSRV_STR6", "0");//发票打印
            
            data.put(""+feeInfo.get("FUZZY_QUERY"), feeInfo.get("FUZZY_COMMENT"));
                //data.put("RATE_TYPE", feeInfo.get("TYPE")); // 计税方式
                //data.put("RSRV_NUM1", feeInfo.get("FEE3")); // 提补税
                // data.put("RSRV_TAG1", feeInfo.get("RSRV_TAG1")); //如果是混合税率，此标记用来标记折扣计税的那条记录
            //}

            dataset.add(data);
        }
        Dao.insert("TF_F_NONBOSSFEE_LOG", dataset);
        returnData.put("PRINT_TRADE_ID", printTrade);
        return returnData;
    }

    /**
     * 更新原记录状态，并增加返销记录
     * 
     * @param logId
     * @param operDate
     * @param operMonth
     * @param ygzTag
     * @throws Exception
     */
    public void insertRefundNonBossFee(String logId, String operDate, String operMonth, boolean ygzTag) throws Exception
    {
        if (StringUtils.isBlank(logId))
        {
            return;
        }
        IData data = new DataMap();
        data.put("LOG_ID", SeqMgr.getTradeId());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_CITY_CODE", getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", getVisit().getDepartId());
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        data.put("OPER_MONTH", operMonth);
        data.put("OPER_DATE", operDate);

        data.put("REFUND_LOG_ID", logId);

        // 更新原来记录状态
        Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", "UPD_NOBOSSFEE_STATE", data);
        // 插入返销记录
        Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", "INS_NONBOSSFEE_CANCEL", data);

    }

    /**
     * 打印发票
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printNonBossFeeTrade(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID", "");
        if (StringUtils.isBlank(tradeId))
        {
            tradeId = SeqMgr.getTradeId();
        }
        IDataset feeList = input.getDataset("FEE_LIST");
        String tradeTypeCode = "1102";
        String tradeType = "非出账业务收款录入";
        //目前这2个参数还未给过来，另外，
        //String addre_phone    = input.getString("ADDRE_PHONE");                  
        //String bank_count     = input.getString("BANK_COUNT");                
        String ti_name        = "中国移动通信集团海南有限公司1111";                   
        String ti_taxno       = "460100710920952";                   
        String ti_addr_phone  = "海南省海口市金龙路88号，0898-31908602";                    
        String ti_bank_count  = "建行金茂支行 46001003236050001337";                    
        String check_staff    = "林书财"; 
        
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData receiptData = new DataMap();
        IDataset printRes = new DatasetList();
        double totalOperMoney = 0;//金额总额=税总额+不含税款总额（费用总额）
        double totalTaxFee = 0;//税总额
        double totalNotaxFee=0;//不含税款总额
        double totalFee=0;//费用
        StringBuilder feeTypeSb = new StringBuilder();
        StringBuilder feeAmountSb = new StringBuilder();
        StringBuilder noTaxFeeSb= new StringBuilder();
        StringBuilder taxFeeSb = new StringBuilder();
        StringBuilder taxRateSb = new StringBuilder();
        
        
        for (int i = 0; i < feeList.size(); i++)
        {
            IData fee = feeList.getData(i);
            
            double feeAmount = Double.parseDouble(fee.getString("FEE_AMOUNT"));//费用 
            double notaxFeeAmount = Double.parseDouble(fee.getString("NOTAX_FEE")); //不含税款
            double taxFeeAmount = Double.parseDouble(fee.getString("TAX_FEE"));//税额
            
            
            String taxRate = fee.getString("TAX_RATE")+"%";
            if (feeAmount > 0)
            {
                feeTypeSb.append(fee.getString("FEE_NAME_DESC") + "~");
                feeAmountSb.append(fee.getString("FEE_AMOUNT") + "~");
                noTaxFeeSb.append(fee.getString("NOTAX_FEE") + "~");
                taxFeeSb.append(fee.getString("TAX_FEE") + "~");
                taxRateSb.append(taxRate + "~");
                
            }
            
            totalFee += feeAmount;//费用 
            totalNotaxFee += notaxFeeAmount;
            totalTaxFee += taxFeeAmount;
            
        }
        totalOperMoney=totalFee;
        //由于使用double会以科学计数法展示，因此要转换一下，下同
        BigDecimal bd=new BigDecimal(totalOperMoney).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("CUST_NAME", input.getString("PAY_NAME_REMARK"));
        receiptData.put("CUST_TAXNO", input.getString("CUST_TAXNO"));
        receiptData.put("ADDRE_PHONE", input.getString("ADDRE_PHONE"));
        receiptData.put("BANK_COUNT", input.getString("BANK_COUNT"));
        
        receiptData.put("TRADE_TYPE", tradeType);
        receiptData.put("ALL_MONEY_LOWER", bd.toString());
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalOperMoney ));
        receiptData.put("FEE_TYPE", feeTypeSb.toString());
        receiptData.put("FEE", feeAmountSb.toString());
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", getVisit().getStaffName());
        receiptData.put("DEPART_NAME", getVisit().getDepartName());
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        receiptData.put("NOTAX_FEE",  noTaxFeeSb.toString());
        receiptData.put("TAX_FEE",  taxFeeSb.toString());
        receiptData.put("TAX_RATE",  taxRateSb.toString()); 
        receiptData.put("ALL_MONEY_FEE",new BigDecimal(totalNotaxFee).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        receiptData.put("ALL_MONEY_TAX",new BigDecimal(totalTaxFee).setScale(2, BigDecimal.ROUND_HALF_DOWN)); 
        
        receiptData.put("TI_NAME",  ti_name); 
        receiptData.put("TI_TAXNO",  ti_taxno);
        receiptData.put("TI_ADDR_PHONE",  ti_addr_phone); 
        receiptData.put("TI_BANK_COUNT", ti_bank_count);
        receiptData.put("CHECK_STAFF", check_staff);  

        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0001, "0", tradeEparchyCode);
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);

        IData printInfo = new DataMap();
        printInfo.put("NAME", "非出账业务补录发票");
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0001);
        printInfo.put("FEE_MODE", "0");
        printInfo.put("TOTAL_FEE",""+new BigDecimal(totalOperMoney*100));
        printInfo.put("ALL_MONEY_FEE",""+new BigDecimal(totalFee*100));
        printInfo.put("ALL_MONEY_TAX",""+new BigDecimal(totalTaxFee*100));
        printInfo.put("STAFF_NAME", getVisit().getStaffName());
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", "");
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        // 本次打印的非BOSS收款补录记录，逗号分隔连接
        printInfo.put("NONBOSSFEE_LOG_ID", input.getString("NONBOSSFEE_LOG_ID"));
        //冲红需要
        printInfo.put("O_TAX_NO", input.getString("O_TAX_NO",""));
        printInfo.put("O_TICKET_ID", input.getString("O_TICKET_ID",""));
        
        //printInfo.put("CH_TICKET", input.getString("CH_TICKET")); //补打&冲红标志 如果是冲红则传1        
        //printInfo.put("BD_CH_FLAG", input.getString("BD_CH_FLAG"));//补打&冲红标志 如果是冲红则传1  
        
        
        printRes.add(printInfo);

        return printRes;
    }

    /**
     * 补打发票
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset rePrintNonBossFee(IData input) throws Exception
    {
        String staffId = getVisit().getStaffId();
        String tmpLogId, payNameRemark = "";  
        IDataset feeList = new DatasetList();
        StringBuilder logSb = new StringBuilder();
        IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
        
      //如果是非增值税发票，只更新表的打印字段为已打印
        String invType= infos.getData(0).getString("INVOICE_TYPE"); 
        String o_tax_no="";
        String o_ticket_id="";
        String payName="";
        
        //非增值税打票。
//        if(!"".equals(invType)&&"0".equals(invType)){
        for (int i = 0; i < infos.size(); i++){
        		IData map = infos.getData(i);
        		String logId = map.getString("LOG_ID", "");
        		IData inparam=new DataMap();
        		inparam.put("TICKET_TYPE", invType);
        		inparam.put("LOG_ID", logId);
        		this.updTblInvType(inparam);
        }
        return new DatasetList();
//        }
        /** 暂时先屏蔽掉调用接口
        for (int i = 0; i < infos.size(); i++)
        {
            IData map = infos.getData(i);
//            if (!(map.getString("TRADE_STAFF_ID", "")).equals(staffId))
//            {
//                // error("打印发票操作员工信息与原录入信息员工不符!");
//                CSAppException.apperr(PrintException.CRM_PRINT_27);
//            }
            tmpLogId = map.getString("LOG_ID", "");
            payNameRemark=map.getString("PAY_NAME_DES","");
            o_tax_no=map.getString("O_TAX_NO", "");
            o_ticket_id=map.getString("O_TICKET_ID", "");
            payName=map.getString("PAY_NAME", "");
            
            if(payNameRemark.length()<3){
            	IData temData=new DataMap();
            	temData.put("DATA_ID", payNameRemark);
            	IDataset userInfos = NonBossFeeLogInfoQry.queryNonBossFeeUserItems2(temData); 
            	payNameRemark=userInfos.getData(0).getString("DATA_NAME"); 
            }else{
            	IData temData=new DataMap();
            	temData.put("DATA_ID", payName);
            	IDataset userInfos = NonBossFeeLogInfoQry.queryNonBossFeeUserItems2(temData);
            	String paraCode2=userInfos.getData(0).getString("PARA_CODE2");//取纳税人识别号，如果为空，不允许打专票
            	payNameRemark=userInfos.getData(0).getString("DATA_NAME");
            	if("".equals(paraCode2)||null==paraCode2){
            		CSAppException.appError("err01", "该用户【"+payNameRemark+"】没有配置纳税人识别号等信息，不允许打印专票！");
            	} 
            }
             
            logSb.append(logSb.length() > 0 ? "," + tmpLogId : tmpLogId);

            IDataset nonBossFees = NonBossFeeLogInfoQry.queryNonBossFeeByLog(tmpLogId);
            if (IDataUtil.isNotEmpty(nonBossFees))
            {
                IData fee = new DataMap(); 
                
                fee.put("FEE_NAME_DESC", nonBossFees.getData(0).getString("OPER_TYPE_NAME_DESC"));
                fee.put("FEE_AMOUNT", nonBossFees.getData(0).getString("OPER_FEE"));
                fee.put("NOTAX_FEE", nonBossFees.getData(0).getString("NOTAX_FEE"));
                fee.put("TAX_FEE", nonBossFees.getData(0).getString("TAX_FEE"));
                fee.put("TAX_RATE", nonBossFees.getData(0).getString("TAX_RATE"));
                feeList.add(fee);

                if (StringUtils.isBlank(payNameRemark))
                {
                    payNameRemark = nonBossFees.getData(0).getString("PAY_NAME_REMARK");
                }
            }

        }
        input.remove("NON_BOSS_INFO"); // 移除之前LOG_ID数据串
        input.put("NONBOSSFEE_LOG_ID", logSb.toString());
        input.put("PAY_NAME_REMARK", payNameRemark);
        input.put("O_TAX_NO", o_tax_no);
        input.put("O_TICKET_ID", o_ticket_id);
        input.put("FEE_LIST", feeList);
        
        IData userData=new DataMap();
        userData.put("DATA_NAME", payNameRemark);
        IDataset userInfos = NonBossFeeLogInfoQry.queryNonBossFeeUserItems(userData);
        input.put("CUST_TAXNO", userInfos.getData(0).getString("PARA_CODE2"));
        input.put("ADDRE_PHONE", userInfos.getData(0).getString("PARA_CODE3"));
        input.put("BANK_COUNT", userInfos.getData(0).getString("PARA_CODE4"));
        
        return printNonBossFeeTrade(input);
        */
    }
    
    /**
     * 作废
     * 
    public void updateTicket(IData input) throws Exception
    {

        String tradeId = input.getString("TRADE_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String ticketTypeCode = input.getString("TICKET_TYPE_CODE");
        
       
        String CHECK_RIGHT_CLASS="1"; 
        IDataset ids=CommparaInfoQry.getCommparaByCodeCode1("CSM", "6891", "ZF",this.getVisit().getStaffId());
        if(ids!=null&&ids.size()>0){
        	CHECK_RIGHT_CLASS="0";
        }
        
        IData inData = new DataMap(); 
    	inData.put("TAX_NO", input.getString("TAX_NO"));
        inData.put("TICKET_ID", input.getString("TICKET_ID"));
        inData.put("FWM", input.getString("FWM", ""));
        inData.put("FEE", input.getString("FEE", ""));// 开票类型
        inData.put("STAFF_ID", input.getString("TRADE_STAFF_ID")); 
        inData.put("TICKET_TYPE_CODE", ticketTypeCode);//票样类型
        //需求新增传值： 
        String tradeTypeName ="";
        if(!"".equals(tradeTypeCode)){
        	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
        }
        inData.put("TRADE_TYPE_NAME", tradeTypeName);// 业务名
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类别
        inData.put("TRADE_ID", tradeId);//业务ID
        inData.put("CHECK_RIGHT_CLASS", CHECK_RIGHT_CLASS);//校验标识 1:校验  0:不校验 COMMPARA表中配置了6891的才为1
        //StateTaxUtil.stateTaxInvoiceCancel(inData);
        
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("NEW_STATE_CODE", "4"); // 发票作废标志
        param.put("TICKET_STATE_CODE", input.getString("TICKET_STATE_CODE"));
        param.put("TICKET_TYPE_CODE", ticketTypeCode);
        Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPD_TICKET_STATE", param);

    }
    */
    /**
     * 如果是非增值税票打印，则只需要更新记录为已打印即可。
     * */
    public void updTblInvType(IData inparam) throws Exception
    {  
    	IData param = new DataMap();
    	param.put("TRADE_ID", SeqMgr.getTradeId());
        param.put("LOG_ID", inparam.getString("LOG_ID",""));
        param.put("TICKET_TYPE", inparam.getString("TICKET_TYPE","")); 
    	Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", "UPD_NOBOSSFEETICKET_STATE", param); 
    }
    
    /**
     * 冲红重新打票
     * */
    public IData rePrintByCH(IData input) throws Exception{    	
    	IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
    	String tradeId = infos.getData(0).getString("TRADE_ID", "");
        if (0 == tradeId.length())
        	CSAppException.apperr(TicketException.CRM_TICKET_9);
        /**
         * 2015-06-15 屏蔽掉冲红接口，等调试通过后再说
        // 根据PRINT_ID获取发票记录
        IDataset records = TicketInfoQry.qryTradeReceipt(tradeId);
        if (IDataUtil.isEmpty(records))
            CSAppException.apperr(TicketException.CRM_TICKET_11, tradeId);

        IData record = records.getData(0); 
        String feeMode = record.getString("FEE_MODE"); 
        */
        //发票冲红处理
    	IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("CENCEL_TYPE", "3");
        /**
         * 2015-06-15 屏蔽掉冲红接口，等调试通过后再说
        param.put("OPER_TYPE", "7");// 发票冲红标志
        param.put("TICKET_STATE_CODE", "0");
        param.put("TICKET_TYPE_CODE", "D");
        param.put("FEE_MODE", feeMode);
        
        
        // 更新原有发票记录,一个台账只会有一个TICKET_TYPE_CODE=0&FEE_MODE&TICKET_STATE_CODE的记录
        int num = Dao.executeUpdateByCodeCode("TF_B_TICKET", "UPDATE_TICKET_STATE_CODE", param);
        if (num < 1){
            CSAppException.apperr(TicketException.CRM_TICKET_6, input.getString("TRADE_ID", ""), input.getString("FEE_MODE", ""), "0",
                input.getString("TICKET_TYPE_CODE", ""));
        } 
        */
        
        int rtn=Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", "UPD_NONBOSS_CEL_INVOICE_CH", param);
        if(rtn!=-1){
        	rtn=0;
        }
        /**
        //由于冲红只能选一条，重打要将合打的记录都选上
        IData selData=new DataMap();
        selData.put("O_TAX_NO", record.getString("TAX_NO"));
        selData.put("O_TICKET_ID", record.getString("TICKET_ID"));
        selData.put("TRADE_ID", tradeId);
        selData.put("INVOICE_TYPE","1");
        IDataset rePrintSet = NonBossFeeLogInfoQry.queryNonBossLogByTradeid(selData); 
    	return rePrintSet;
    	*/
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", ""+rtn);
    	return returnData;
    }
    
    
    /**
     * 【非增值税发票】的作废和冲红
     * 该类票只存在修改数据状态
     * */
    public IData updateNonbossLogChZf(IData input) throws Exception{    	
    	IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
    	String tradeId = infos.getData(0).getString("TRADE_ID", "");
    	IData param=new DataMap();
    	String cenType=infos.getData(0).getString("CENCEL_TYPE"); 
    	String sqlRef="";
    	if("ZF".equals(cenType)){
    		param.put("CENCEL_TYPE", "2"); 
    		sqlRef="UPD_NONBOSS_CEL_INVOICE";
    	}else{
    		param.put("CENCEL_TYPE", "3");
    		sqlRef="UPD_NONBOSS_CEL_INVOICE_CH";
    	}
    	param.put("TRADE_ID", tradeId); 
    	int num=Dao.executeUpdateByCodeCode("TF_F_NONBOSSFEE_LOG", sqlRef, param);
    	IData rtnData=new DataMap();
    	if(num>0){
    		rtnData.put("RESULT_CODE", "1");//成功
    	}else{
    		rtnData.put("RESULT_CODE", "0");
    	}
    	return rtnData;
    }
}
