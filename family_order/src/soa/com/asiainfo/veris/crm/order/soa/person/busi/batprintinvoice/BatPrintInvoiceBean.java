
package com.asiainfo.veris.crm.order.soa.person.busi.batprintinvoice;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.bat.BatDataImportBean;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BatPrintInvoiceBean extends CSBizBean
{
    private static transient final Logger log = Logger.getLogger(BatPrintInvoiceBean.class); 

    /**
     * 转义成认证方式
     * 
     * @param tag
     * @return
     */
    private String getCheckModeName(String tag)
    {
        String checkName = null;
        if (StringUtils.equals(tag, "0"))
        {
            checkName = "证件号码校验";
        }
        else if (StringUtils.equals(tag, "1"))
        {
            checkName = "服务密码校验";
        }
        else if (StringUtils.equals(tag, "2"))
        {
            checkName = "SIM卡号+服务密码校验";
        }
        else if (StringUtils.equals(tag, "3"))
        {
            checkName = "服务号码+证件号码校验   ";
        }
        else if (StringUtils.equals(tag, "4"))
        {
            checkName = "证件号码+服务密码校验";
        }
        else if (StringUtils.equals(tag, "5"))
        {
            checkName = "SIM卡号+短信验证码校验";
        }
        /**
         * REQ201512020036 用户押金发票号码查询界面优化
         * chenxy3 20160106 增加换卡（写卡）新的认证组合方式
         * */
        else if (StringUtils.equals(tag, "6"))
        {
            checkName = "服务密码+验证码";
        }
        /**
		   * REQ201606230019非实名用户关停改造需求
		   * chenxy3 新的独立认证方式
		   * */
        else if (StringUtils.equals(tag, "7"))
        {
            checkName = "验证码";
        }
        else if (StringUtils.equals(tag, "8"))
        {
            checkName = "SIM卡号(或白卡号)";
        }
        /**
		   * REQ201610200008 补换卡业务调整需求
		   * chenxy3 新的独立认证方式 客户证件+证件类型+验证码
		   * */
        else if (StringUtils.equals(tag, "9")){
        	checkName = "有效证件+验证码";
 		  }
        else if (StringUtils.equals(tag, "F"))
        {
            checkName = "免认证";
        }
        else
        {
            checkName = "未知校验方式";
        }

        return checkName;
    }

    public IData getPrintData(IDataset printTrades, boolean isSameStaff) throws Exception
    {
        IData returnData = new DataMap();
        boolean bFirst = true;
        StringBuilder receipts = new StringBuilder();

        for (int i = 0; i < printTrades.size(); i++)
        {
            IData printInfo = printTrades.getData(i);
            String checkMode = printInfo.getString("CHECK_MODE", "");

            String staffName = printInfo.getString("STAFF_NAME", "");
            String staffId = printInfo.getString("STAFF_ID", "");
            String acceptDate = printInfo.getString("ACCEPT_DATE", "");
            if (acceptDate != null && !acceptDate.equals(""))
            {
                acceptDate = acceptDate.substring(0, 19);
            }
            String tradeType = printInfo.getString("PRIORITY", "");
            tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeType);
            String receiptInfo1 = printInfo.getString("RECEIPT_INFO1", "");
            String receiptInfo2 = printInfo.getString("RECEIPT_INFO2", "");
            String receiptInfo3 = printInfo.getString("RECEIPT_INFO3", "");
            String receiptInfo4 = printInfo.getString("RECEIPT_INFO4", "");
            String receiptInfo5 = printInfo.getString("RECEIPT_INFO5", "");
            if (receiptInfo1.indexOf("受理员工") == -1 && receiptInfo1.indexOf("业务类型") == -1)
            {
                if (isSameStaff && bFirst)
                {
                    receipts.append("受理员工：" + staffName + "  " + staffId + "   " + "业务受理时间：" + acceptDate + "~~");
                    bFirst = false;
                }
                receipts.append("业务类型：" + tradeType + "             ");
                receipts.append("受理方式：" + getCheckModeName(checkMode) + "~~");

                if (!isSameStaff)
                {
                    receipts.append("受理员工：" + staffName + "  " + staffId + "   " + "业务受理时间：" + acceptDate + "~~");
                }
            }
            if (StringUtils.isNotBlank(receiptInfo1))
            {
                receipts.append(receiptInfo1 + "~~");
            }
            if (StringUtils.isNotBlank(receiptInfo2))
            {
                receipts.append(receiptInfo2 + "~~");
            }
            if (StringUtils.isNotBlank(receiptInfo3))
            {
                receipts.append(receiptInfo3 + "~~");
            }
            if (StringUtils.isNotBlank(receiptInfo4))
            {
                receipts.append(receiptInfo4 + "~~");
            }
            if (StringUtils.isNotBlank(receiptInfo5))
            {
                receipts.append(receiptInfo5 + "~~");
            }

            receipts.append("~~");
        }

        if (isSameStaff)
        {
            receipts.append("本次办理" + printTrades.size() + "项业务！");
        }
        returnData.put("RECEIPT_INFO1", receipts.toString());
        return returnData;
    }

    /**
     * 判断需要批量打印的业务是否为同一个员工受理
     * 
     * @param printTrades
     * @return
     * @throws Exception
     */
    private boolean isSameStaff(IDataset printTrades) throws Exception
    {
        boolean flag = true, isSameStaff = true;
        String strStaffId = "", strAcceptDate = "";
        for (int i = 0; i < printTrades.size(); i++)
        {
            IData printData = printTrades.getData(i);
            // 第一次进来初始化
            if (flag)
            {
                strStaffId = printData.getString("STAFF_ID", "");
                strAcceptDate = printData.getString("ACCEPT_DATE", "");
                if (StringUtils.isNotBlank(strAcceptDate))
                {
                    strAcceptDate = strAcceptDate.substring(0, 10);
                }
                flag = false;
            }
            if (!StringUtils.equals(strStaffId, printData.getString("STAFF_ID", "")) || !strAcceptDate.equals(printData.getString("ACCEPT_DATE", "")))
            {
                isSameStaff = false;
                break;
            }
        }
        return isSameStaff;
    }

    /**
     * 批量打印
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData printTrade(IData input) throws Exception
    {
        IData returnData = new DataMap();
        IDataset printDataset = new DatasetList();
        String brandCode = "", custName = "";
        String psptId = "",tradeTypeCode = "";
        String serialNumber = input.getString("SERIAL_NUMBER");
        String prtTradeId = input.getString("PRINT_TRADE_ID");
        if (StringUtils.isBlank(prtTradeId))
        {
            CSAppException.apperr(PrintException.CRM_PRINT_24);
        }
        String[] prtTradeIds = prtTradeId.split(",");
        for (String tradeId : prtTradeIds)
        {
            IDataset printNoteInfos = TradeReceiptInfoQry.getPrintNoteInfoByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(printNoteInfos))
            {
                IData print = new DataMap();
                for (int i = 0; i < printNoteInfos.size(); i++)
                {
                    print = printNoteInfos.getData(i);
                    print.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(print.getString("STAFF_ID")));

                    if (i == 0)
                    {
                        custName = print.getString("CUST_NAME");
                        tradeTypeCode = print.getString("PRIORITY");
                    }
                }
                printDataset.add(print);
            }
            
            if ("149".equals(tradeTypeCode)) {//跨区补卡 
            	String eparchyCode = CSBizBean.getTradeEparchyCode();
                IDataset reCardInfos = TradeReceiptInfoQry.queryReCardResult(tradeId,new Pagination(),eparchyCode);
                if (IDataUtil.isNotEmpty(reCardInfos))
                {
                    IData reCardInfo = reCardInfos.getData(0);
                    if (IDataUtil.isNotEmpty(reCardInfo))
                    {
                    	psptId = reCardInfo.getString("CUST_CERT_NO");
                    }
                }
            }
        }

        IData receiptData = getPrintData(printDataset, isSameStaff(printDataset));

        IData user = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(user))
        {
            brandCode = user.getString("BRAND_CODE", "");
        }

        String operatTime = SysDateMgr.getSysTime();
        String operYear = operatTime.substring(0, 4);
        String operMonth = operatTime.substring(5, 7);
        String operDay = operatTime.substring(8, 10);
        receiptData.put("TRADE_ID", SeqMgr.getTradeId());
        receiptData.put("OPERATION_YEAR", operYear);
        receiptData.put("OPERATION_MONTH", operMonth);
        receiptData.put("OPERATION_DAY", operDay);
        receiptData.put("SERIAL_NUMBER", serialNumber);
        receiptData.put("CUST_NAME", custName);
        if ("149".equals(tradeTypeCode)) {//跨区补卡
        	receiptData.put("PSPT_ID", psptId);
        }
        receiptData.put("STAFF_ID", getVisit().getStaffId());
        receiptData.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(brandCode));
        IDataset templetItems = ReceiptNotePrintMgr.getReceiptTempletItems("-1", ReceiptNotePrintMgr.RECEIPT_P0003, "0", CSBizBean.getTradeEparchyCode());
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItems);

        returnData.put("NAME", "免填单");
        returnData.put("PRINT_DATA", printData);
        returnData.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0003);
        return returnData;
    }

    /**
     * 打印信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPrintInfo(IData input, Pagination pagination) throws Exception
    {
        String eparchyCode = CSBizBean.getTradeEparchyCode();
        String searchType = input.getString("SEARCH_TYPE", "1");
        String serialNumber = input.getString("SERIAL_NUMBER", "").trim();
        String tradeId = input.getString("TRADE_ID");
        String batchId = input.getString("BATCH_ID");
        IDataset printsInfos = null ;       
        
        if(StringUtils.equals(searchType, "3")){
        	String endDate = input.getString("END_DATE", "");
        	if(StringUtils.isBlank(batchId)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"请输入批次号!" );
        	}
            if (StringUtils.isNotBlank(endDate))
            {
                endDate = endDate + SysDateMgr.getEndTime235959();
            }        
            IData data = new DataMap();
            IDataset returnValues = new DatasetList();
            data.put("BATCH_ID", batchId);
            SQLParser parser = new SQLParser(data);
            parser.addSQL(" SELECT A.BATCH_OPER_TYPE,T.* FROM TF_B_TRADE_CNOTE_INFO T ,TF_B_TRADE_BATDEAL A");
            parser.addSQL(" WHERE T.TRADE_ID = :BATCH_ID ");
            parser.addSQL(" AND A.BATCH_ID = :BATCH_ID ");
            printsInfos = Dao.qryByParse(parser);
            IData printsInfo = new DataMap();
                        
            if(IDataUtil.isNotEmpty(printsInfos) && printsInfos.size() > 0){
            	
        		printsInfo = printsInfos.getData(0);
        		
                if("CREATEPREUSER_PWLW".equals(printsInfo.getString("BATCH_OPER_TYPE")))
                {
                	printsInfo.put("TRADE_TYPE", "物联网批量开户");
                }else
                {
                	if("CREATEPREUSER_M2M".equals(printsInfo.getString("BATCH_OPER_TYPE")))
                	{
                		printsInfo.put("TRADE_TYPE", "行业应用卡批量开户");
                	}else
                	{
                		printsInfo.put("TRADE_TYPE", "其它");
                	}
                }								
				printsInfo.put("STAFF_ID", printsInfo.getString("TRADE_STAFF_ID", ""));//员工编码
                printsInfo.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(printsInfo.getString("TRADE_STAFF_ID", "")));//员工姓名
                printsInfo.put("DEPART_ID", printsInfo.getString("TRADE_DEPART_ID", ""));//部门编码             
                printsInfo.put("DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(printsInfo.getString("TRADE_DEPART_ID", "")));//部门名称                
                printsInfo.put("BRAND", printsInfo.getString("ACCEPT_DATE", ""));//办理时间
                
                returnValues.add(printsInfo);          	
            }
            return returnValues;        	
        }
        if(StringUtils.equals(searchType, "1")){
            // REQ201707170018 电子工单补打界面增加支持无手机宽带号码优化 add by zhanglin3 start
            if ("true".equalsIgnoreCase(input.getString("NO_PHONE_WIDEUSER_FLAG"))) {
                if (!serialNumber.startsWith("KD_")) {
                    serialNumber = "KD_" + serialNumber;
                }
            }

        	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);
            if (IDataUtil.isEmpty(userInfo))
            {
                // 先查询tf_f_user表再查询tf_fh_user
            	IDataset hisUserDataset = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);
            	if(IDataUtil.isEmpty(hisUserDataset)){
            		IDataset reUserDataset =UserInfoQry.qryAllUserInfoBySnFromHis(serialNumber);
            		if(IDataUtil.isEmpty(reUserDataset))
            		{           			
            			//CSAppException.apperr(CrmUserException.CRM_USER_672);
            			String endDate = input.getString("END_DATE", "");
        		        if (StringUtils.isNotBlank(endDate))
        		        {
        		            endDate = endDate + SysDateMgr.getEndTime235959();
        		        }
        		        
        	            printsInfos = TradeReceiptInfoQry.queryPrintNoteInfoCt(serialNumber, tradeId, input.getString("STAFF_ID", ""), input.getString("START_DATE", ""), endDate, "0", pagination, eparchyCode);
            		        
        		        IDataset returnValues = new DatasetList();
        		        IData printsInfo = new DataMap();
        		        if (IDataUtil.isNotEmpty(printsInfos))
        		        {
        		            for (int i = 0; i < printsInfos.size(); i++)
        		            {
        		                printsInfo = (IData) printsInfos.getData(i);
        		                
        		                if("40".equals(printsInfo.getString("PRIORITY", ""))|| "141".equals(printsInfo.getString("PRIORITY", "")) || "6800".equals(printsInfo.getString("PRIORITY", "")) && "0".equals(printsInfo.getString("CANCEL_TAG")))
        		                {
            		                printsInfo.put("VIP_CLASS", getCheckModeName(printsInfo.getString("VIP_CLASS", "")));

            		                printsInfo.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(printsInfo.getString("PRIORITY", "")));

            		                printsInfo.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(printsInfo.getString("STAFF_ID", "")));

            		                printsInfo.put("DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(printsInfo.getString("DEPART_ID", "")));

            		                returnValues.add(printsInfo);
        		                
        		                }
        		            }
        		        }

        		        return returnValues;
            		}
            	}
            }
        }else{
        	if(StringUtils.isBlank(tradeId)){
        		CSAppException.apperr(TradeException.CRM_TRADE_84);
        	}
        }

        String endDate = input.getString("END_DATE", "");
        if (StringUtils.isNotBlank(endDate))
        {
            endDate = endDate + SysDateMgr.getEndTime235959();
        }

        printsInfos = TradeReceiptInfoQry.queryPrintNoteInfo(serialNumber, tradeId, input.getString("STAFF_ID", ""), input.getString("START_DATE", ""), endDate, "0", pagination, eparchyCode);

        IDataset returnValues = new DatasetList();
        IData printsInfo = new DataMap();
        if (IDataUtil.isNotEmpty(printsInfos))
        {
            for (int i = 0; i < printsInfos.size(); i++)
            {
                printsInfo = (IData) printsInfos.getData(i);

                printsInfo.put("VIP_CLASS", getCheckModeName(printsInfo.getString("VIP_CLASS", "")));

                printsInfo.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(printsInfo.getString("PRIORITY", "")));

                printsInfo.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(printsInfo.getString("STAFF_ID", "")));

                printsInfo.put("DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(printsInfo.getString("DEPART_ID", "")));

                returnValues.add(printsInfo);
            }
        }

        return returnValues;
    }

    
    public IData createPrintInfo(IData input) throws Exception
    {
    	IData returnData = new DataMap();
    	returnData.put("RESULT_CODE", "1");
    	String eparchyCode = CSBizBean.getTradeEparchyCode();
    	String searchType = input.getString("SEARCH_TYPE", "1");
        String serialNumber = input.getString("SERIAL_NUMBER", "").trim();
        String tradeId = input.getString("TRADE_ID");
        
        String staffId = input.getString("STAFF_ID", "");
        String startDate =input.getString("START_DATE", "");
        
        if(StringUtils.equals(searchType, "1")){
            // REQ201707170018 电子工单补打界面增加支持无手机宽带号码优化 add by zhanglin3 start
            if ("true".equalsIgnoreCase(input.getString("NO_PHONE_WIDEUSER_FLAG"))) {
                if (!serialNumber.startsWith("KD_")) {
                    serialNumber = "KD_" + serialNumber;
                }
            }
            // end

            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);
        	IDataset tradeUserInfos = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
            if (IDataUtil.isEmpty(userInfo) && IDataUtil.isEmpty(tradeUserInfos))
            {
            	//先查询tf_f_user表再查询tf_fh_user
            	IDataset hisUserDataset = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);
            	if(IDataUtil.isEmpty(hisUserDataset)){
            		IDataset reUserDataset =UserInfoQry.qryAllUserInfoBySnFromHis(serialNumber);
            		if(IDataUtil.isEmpty(reUserDataset))
            		CSAppException.apperr(CrmUserException.CRM_USER_672);
            	}
            }
        }        
		String endDate = input.getString("END_DATE", "");
        if(StringUtils.equals(searchType, "2")){        	
			if(StringUtils.isBlank(tradeId)){
        		CSAppException.apperr(TradeException.CRM_TRADE_84);
        	}
        }
        if (StringUtils.isNotBlank(endDate))
        {
            endDate = endDate + SysDateMgr.getEndTime235959();
        }
        RegTradeData rtd = null;
        int tradeNum = 0;
        ReceiptNotePrintMgr rnpMgr = new ReceiptNotePrintMgr();    	
        IDataset tradeInfos = TradeInfoQry.qryNoPrintTrade(serialNumber,tradeId, staffId, startDate, endDate, "0", eparchyCode);
        if(IDataUtil.isNotEmpty(tradeInfos))
        {
        	for(int i=0;i<tradeInfos.size();i++)
        	{
        		IData temp = tradeInfos.getData(i);
        		String tradeTypeCode = temp.getString("TRADE_TYPE_CODE");
        		String epcCode = temp.getString("EPARCHY_CODE");
        		String custId = temp.getString("CUST_ID");
        		String tempTradeId = temp.getString("TRADE_ID");
        		IData tradeTypeData = UTradeTypeInfoQry.getTradeType(tradeTypeCode, epcCode);
        		if(IDataUtil.isNotEmpty(tradeTypeData))
        		{
        			if(!StringUtils.equals("1", tradeTypeData.getString("PRT_TRADEFF_TAG")))
        			{
        				tradeInfos.remove(i);i--;continue;
        			}
        		}else
        		{
        			tradeInfos.remove(i);i--;continue;
        		}
        		
        		IData custInfo = UcaInfoQry.qryCustInfoByCustId(custId);
        		IDataset tradeCustInfo = TradeCustomerInfoQry.getTradeCustomerByTradeId(tempTradeId);
        		
        		if(IDataUtil.isNotEmpty(custInfo))
        		{
        			String removeTag = custInfo.getString("REMOVE_TAG");
        			if(!StringUtils.equals("0",removeTag))
        			{
        				custInfo = null;
        			}
        		}
        		if(IDataUtil.isEmpty(custInfo) && IDataUtil.isEmpty(tradeCustInfo))
        		{
        			tradeInfos.remove(i);i--;continue;
        		}
        		IDataset printInfo = TradeReceiptInfoQry.queryPrintNotByTradeId(tempTradeId);
        		if(IDataUtil.isNotEmpty(printInfo))
        		{
        			tradeInfos.remove(i);i--;continue;
        		}
        	}
        }
        IDataset tradeHisInfos = TradeInfoQry.qryNoPrintHisTrade(serialNumber,tradeId, staffId, startDate, endDate, "0", eparchyCode);
        if(IDataUtil.isNotEmpty(tradeHisInfos))
        {
        	for(int i=0;i<tradeHisInfos.size();i++)
        	{
        		IData temp = tradeHisInfos.getData(i);
        		String tradeTypeCode = temp.getString("TRADE_TYPE_CODE");
        		String epcCode = temp.getString("EPARCHY_CODE");
        		String custId = temp.getString("CUST_ID");
        		String tempTradeId = temp.getString("TRADE_ID");
        		IData tradeTypeData = UTradeTypeInfoQry.getTradeType(tradeTypeCode, epcCode);
        		if(IDataUtil.isNotEmpty(tradeTypeData))
        		{
        			if(!StringUtils.equals("1", tradeTypeData.getString("PRT_TRADEFF_TAG")))
        			{
        				tradeHisInfos.remove(i);i--;continue;
        			}
        		}else
        		{
        			tradeHisInfos.remove(i);i--;continue;
        		}
        		
        		IData custInfo = UcaInfoQry.qryCustInfoByCustId(custId);
        		
        		if(IDataUtil.isNotEmpty(custInfo))
        		{
        			String removeTag = custInfo.getString("REMOVE_TAG");
        			if(!StringUtils.equals("0",removeTag))
        			{
        				custInfo = null;
        			}
        		}
        		if(IDataUtil.isEmpty(custInfo))
        		{
        			tradeHisInfos.remove(i);i--;continue;
        		}
        		
        		IDataset printInfo = TradeReceiptInfoQry.queryPrintNotByTradeId(tempTradeId);
        		if(IDataUtil.isNotEmpty(printInfo))
        		{
        			tradeHisInfos.remove(i);i--;continue;
        		}
        	}
        }
        
        if(IDataUtil.isNotEmpty(tradeHisInfos)){
        	tradeInfos.addAll(tradeHisInfos);
        }
        
        if(IDataUtil.isNotEmpty(tradeInfos)){
    		IData tradeInfo = null;
    		for(int i=0; i<tradeInfos.size(); i++){
    			tradeInfo = tradeInfos.getData(i);
    			rtd = new RegTradeData(tradeInfo.getString("TRADE_ID"));
    			try{
    				rnpMgr.getTradeReceipt(rtd);
    				tradeNum++;
    			}catch(Exception e){
    			    log.error(e);
    				//如果抛错继续执行
    			}
    		}
    		returnData.put("RESULT_CODE", tradeNum<1? "1":"0");
    	}
    	return returnData;
    }

	//REQ201811130004优化物联网卡相关界面及功能——BOSS侧 wuhao5
    public IDataset importData(IData batchId) throws Exception
    {
    	IData data = new DataMap();
    	SQLParser parser = new SQLParser(batchId);
        parser.addSQL(" SELECT T.* FROM UCR_CRM1.TF_B_TRADE_BATDEAL T WHERE 1=1 ");
        parser.addSQL(" AND T.BATCH_ID = TO_NUMBER(:BATCH_ID) AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        IDataset dataset = Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
        	data = dataset.getData(0);
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "无数据,请检查批次号!");
        }        	
        String batOperType = data.getString("BATCH_OPER_TYPE");
        
        /**
         * REQ201707170020_新增物联卡开户人像采集功能
         * <br/>
         * 返回批量打印电子工单需要的信息
         * @author zhuoyingzhi
         * @date 20170828
         */
        if(batOperType.equals("CREATEPREUSER_PWLW")){
            IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));
            if (IDataUtil.isEmpty(batchTaskInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
            }
            JSONArray array_rx = new JSONArray();
            StringBuffer sb = new StringBuffer();
            String createTime = batchTaskInfo.getString("CREATE_TIME","");
            String staffId = batchTaskInfo.getString("CREATE_STAFF_ID","");
            String staffName = StaticUtil.getStaticValue(null, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
            String departId = batchTaskInfo.getString("CREATE_DEPART_ID","");
            String departName = StaticUtil.getStaticValue(null, "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
            
        	sb.append(batchTaskInfo.getString("CODING_STR1", "")).
            append(batchTaskInfo.getString("CODING_STR2", "")).
            append(batchTaskInfo.getString("CODING_STR3", "")).
            append(batchTaskInfo.getString("CODING_STR4", "")).
            append(batchTaskInfo.getString("CODING_STR5", ""));
            array_rx.element(sb.toString());
            DatasetList ds_rx = new DatasetList(array_rx.toString());
            if (DataSetUtils.isNotBlank(ds_rx)) {

                //客户名称
            	String  custName=ds_rx.getData(0).getString("CUST_NAME", "");
            	dataset.getData(0).put("CUST_NAME", custName);
            	
            	//证件号码
            	String psptid=ds_rx.getData(0).getString("PSPT_ID", "");
            	dataset.getData(0).put("ID_CARD", psptid);
            	
            	//受理工号
            	dataset.getData(0).put("TRADE_STAFF_ID", staffId);
            	
            	//受理工单名称
            	dataset.getData(0).put("TRADE_STAFF_NAME", staffName);
            	
            	//部门编码
            	dataset.getData(0).put("ORG_INFO", departId);
            	//部门名称
            	dataset.getData(0).put("ORG_NAME", departName);

            	String serialNumber=ds_rx.getData(0).getString("PHONE", "");
            	//联系电话
            	dataset.getData(0).put("SERIAL_NUMBER", serialNumber);
            	
            	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
            	//用户id
            	dataset.getData(0).put("USER_ID", ucaData.getUserId());
            	
            	//受理业务时间
            	dataset.getData(0).put("ACCEPT_DATE", createTime);
            	
            	//受理品牌
            	String productName=ds_rx.getData(0).getString("PRODUCT_NAME", "");
            	dataset.getData(0).put("PRODUCT_NAME", productName);
            	
            	//业务类型
            	dataset.getData(0).put("TRADE_TYPE_NAME", "物联网卡批量开户");
            	
            	//客户摄像标识
            	String custInfoPicId=ds_rx.getData(0).getString("custInfo_PIC_ID", "");
            	
            	//经办人摄像标识
            	String agentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID", "");
            	//0-已采集，1-未采集
            	if(!"".equals(custInfoPicId)&&custInfoPicId != null){
            		//客户已经摄像
            		dataset.getData(0).put("PIC_ID", "0");
            	}else{
                	if(!"".equals(agentPicId)&&agentPicId != null){
                		//经办人已经摄像
                		dataset.getData(0).put("PIC_ID", "0");
                	}else{
                		//未摄像
                		dataset.getData(0).put("PIC_ID", "1");
                	}
            	}
            }            	
        }
        /*****************************/
        
        else
        {
            /**
             * REQ201806190020_新增行业应用卡批量开户人像比对功能
             * @author zhuoyingzhi
             * @date 20180725
             */
            if(batOperType.equals("CREATEPREUSER_M2M")){
            	//行业应用卡
                IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));
                if (IDataUtil.isEmpty(batchTaskInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
                }
                JSONArray array_rx = new JSONArray();
//                array_rx.element(batchTaskInfo.getString("CODING_STR1", ""));
                String createTime = batchTaskInfo.getString("CREATE_TIME","");
                String staffId = batchTaskInfo.getString("CREATE_STAFF_ID","");
                String staffName = StaticUtil.getStaticValue(null, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                String departId = batchTaskInfo.getString("CREATE_DEPART_ID","");
                String departName = StaticUtil.getStaticValue(null, "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId);
                
                /**
                 * 随E行问题
                 * mqx
                 */
                StringBuffer sb = new StringBuffer();
                sb.append(batchTaskInfo.getString("CODING_STR1", "")).
                append(batchTaskInfo.getString("CODING_STR2", "")).                	
                append(batchTaskInfo.getString("CODING_STR3", "")).
                append(batchTaskInfo.getString("CODING_STR4", "")).
                append(batchTaskInfo.getString("CODING_STR5", ""));
                
                array_rx.element(sb.toString());
                //
                
                DatasetList ds_rx = new DatasetList(array_rx.toString());
                if (DataSetUtils.isNotBlank(ds_rx)) {
                	
                   	//记录信息到tf_b_trade_cnote_info表
                	//BatDataImportBean.insertIntoTradeCnoteInfoBat(dataset.getData(0), batOperType);
                	
                    //客户名称
                	String  custName=ds_rx.getData(0).getString("CUST_NAME", "");
                	dataset.getData(0).put("CUST_NAME", custName);
                	
                	//证件号码
                	String psptid=ds_rx.getData(0).getString("PSPT_ID", "");
                	dataset.getData(0).put("ID_CARD", psptid);
                	
                	//受理工号
                	dataset.getData(0).put("TRADE_STAFF_ID", staffId);
                	
                	//受理工单名称
                	dataset.getData(0).put("TRADE_STAFF_NAME", staffName);
                	
                	//部门编码
                	dataset.getData(0).put("ORG_INFO", departId);
                	//部门名称
                	dataset.getData(0).put("ORG_NAME", departName);

                	String serialNumber=ds_rx.getData(0).getString("PHONE", "");
                	//联系电话
                	dataset.getData(0).put("SERIAL_NUMBER", serialNumber);
                	
                	if(!"".equals(serialNumber)&&serialNumber!=null){
                	 	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
                    	//用户id
                    	dataset.getData(0).put("USER_ID", ucaData.getUserId());
                	}else{
                    	//用户id
                    	dataset.getData(0).put("USER_ID", "");
                	}
               
                	
                	//受理业务时间
                	dataset.getData(0).put("ACCEPT_DATE", createTime);
                	
                	//受理品牌
                	String productName=ds_rx.getData(0).getString("PRODUCT_NAME", "");
                	dataset.getData(0).put("PRODUCT_NAME", productName);
                	
                	//业务类型
                	dataset.getData(0).put("TRADE_TYPE_NAME", "行业应用卡批量开户");
                	
                	//责任人摄像标识
                	String custInfoPicId=ds_rx.getData(0).getString("custInfo_RSRV_STR4_PIC_ID", "");
                	
                	//经办人摄像标识
                	String agentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID", "");
                	//0-已采集，1-未采集
                	if(!"".equals(custInfoPicId)&&custInfoPicId != null){
                		//责任人已经摄像
                		dataset.getData(0).put("PIC_ID", "0");
                	}else{
                    	if(!"".equals(agentPicId)&&agentPicId != null){
                    		//经办人已经摄像
                    		dataset.getData(0).put("PIC_ID", "0");
                    	}else{
                    		//未摄像
                    		dataset.getData(0).put("PIC_ID", "1");
                    	}
                	}
                }            	
            }           
        }
        return dataset;

    }
    
    public void getTradeBatPicIdSyn(IData input) throws Exception
    {
    	IData param = new DataMap();
    	param.put("BATCH_ID", input.getString("BATCH_ID"));
    	printLog("TRADE_ID = ",param.getString("BATCH_ID"));
		//先到tf_b_trade_batdeal表根据BATCH_ID查到operate_id 
    	IDataset tradeBatdealLists = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_ALL_BY_BATCH_MONTH", param);
    	if(IDataUtil.isNotEmpty(tradeBatdealLists) && tradeBatdealLists.size()>0){
    		
			for(int i=0;i<tradeBatdealLists.size();i++){	
				
				IData data = tradeBatdealLists.getData(i);
				String operateId = data.getString("OPERATE_ID");
			    String serialNunmber = data.getString("SERIAL_NUMBER");
				IData batchIdData = new DataMap();
				batchIdData.put("BATCH_ID", operateId);	
				batchIdData.put("SERIAL_NUMBER", serialNunmber);
				//再到tf_bh_trade表根据operate_id对应到BATCH_ID,获取trade_id等信息
				IDataset tradeDatas = Dao.qryByCode("TF_BH_TRADE", "SEL_BY_BATCH_ID", batchIdData);
				if(IDataUtil.isNotEmpty(tradeDatas) && tradeDatas.size()>0){
					
					IData tradeData = tradeDatas.getData(0);
					
					String tradeId = tradeData.getString("TRADE_ID");
				    String tradeTypeCode = tradeData.getString("TRADE_TYPE_CODE");
				    String staffId = tradeData.getString("TRADE_STAFF_ID");
				    String departId = tradeData.getString("TRADE_DEPART_ID");
				    String acceptDate = tradeData.getString("ACCEPT_DATE");
				    
				    IDataset others = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
					List<OtherTradeData> other = new ArrayList<OtherTradeData>();
					for(int j=0;j < others.size();j++){
						IData id=others.getData(j);
						OtherTradeData otd=new OtherTradeData(id);	
						other.add(otd);
					}
							
					IData picinfo = getPicId(other,tradeTypeCode);
					// 调用接口
					IData inParam = new DataMap();
					if(null != picinfo && !picinfo.isEmpty()){
						//开户first_pic_id表示新户主或经办人
						if("0".equals(picinfo.getString("PIC_TAG"))){
							
							inParam.put("first_pic_id", picinfo.get("PIC_ID"));
							
						}else if("0".equals(picinfo.getString("AGENT_PIC_TAG"))){
							
							inParam.put("first_pic_id", picinfo.get("AGENT_PIC_ID"));
							
						}else if("0".equals(picinfo.getString("STR4_PIC_TAG"))){
							//责任人
							inParam.put("first_pic_id", picinfo.get("STR4_PIC_ID"));
						}
					}				
					
					inParam.put("trade_id", tradeId);
					inParam.put("op_code", tradeTypeCode);
					inParam.put("phone", serialNunmber);
					inParam.put("work_no", staffId);
					inParam.put("org_info", departId);
					IDataset ds = StaffInfoQry.qryStaffInfoByStaffId(staffId);
					if (ds != null && ds.size() == 1) {
						inParam.put("work_name", ds.getData(0).getString("STAFF_ID"));
					}
					IData departData = new DataMap();
					departData.put("DEPART_ID", departId);
					ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", departData);
					if (ds != null && ds.size() == 1) {
						inParam.put("org_name", ds.getData(0).getString("DEPART_ID"));
					}
					inParam.put("op_time", acceptDate);
					
					//批次号
					inParam.put("batch_number", input.getString("BATCH_ID"));			
					inParam.put("batsup_tag", "1");//补打标识
					
					JSONObject jSONObject = null;
					jSONObject = JSONObject.fromObject(inParam);
		
					String contentJson = jSONObject.toString();
					IData ibossData = new DataMap();
					ibossData.put("buffer", contentJson);
					
					try {
						printLog("--BatPrintInvoiceBean-SynCall=======contentJson==",contentJson);
						String strResult=sendAutoAudit(contentJson);
						printLog("---BatPrintInvoiceBean--SynCall result = ",strResult);
					} catch (Exception e) {
						if(log.isInfoEnabled()) log.info(e.getMessage());
						throw e;
					}	
				}
			}
		}  
    }

	/**
     * 获取照片ID
     * @param btd
     * @throws Exception
     */
	private IData getPicId (List<OtherTradeData> others, String tradeTypeCode) throws Exception
	{
		IData pictada = new DataMap();
		
		String picid = "";
		String agentpicid = "";
		String picTag = "0";
		String str4picId="";//责任人
		
		if(others != null && others.size() > 0)
        {
            for(OtherTradeData other : others)
            {
                if("BAT_PIC_ID".equals(other.getRsrvValueCode())){
                	picid = other.getRsrvStr1();
                	agentpicid = other.getRsrvStr3();
                	str4picId=other.getRsrvStr5();//责任人
                	picTag = "1";
                }
            }
        }
		
		if("0".equals(picTag)){
			return new DataMap();
		}
		
    	if(StringUtils.isNotBlank(picid))
    	{//客户照片ID存在
    		pictada.put("PIC_ID", picid);
    		pictada.put("PIC_TAG", "0");
    	}
    	else
    	{
    		pictada.put("PIC_TAG", "1");
    	}
    	if(StringUtils.isNotBlank(agentpicid))
    	{//经办人照片ID存在
    		pictada.put("AGENT_PIC_ID", agentpicid);
    		pictada.put("AGENT_PIC_TAG", "0");
    	}else{
    		pictada.put("AGENT_PIC_TAG", "1");
    	}
    	
    	
    	if(StringUtils.isNotBlank(str4picId))
    	{//责任人照片ID存在
    		pictada.put("STR4_PIC_ID", str4picId);
    		pictada.put("STR4_PIC_TAG", "0");
    	}
    	else
    	{
    		pictada.put("STR4_PIC_TAG", "1");
    	}
    	
		return pictada;
	}
	
	/**
     * 日志打印
     * @param name,value
     * @throws Exception
     */
	public void printLog(String name ,String value)
	{
		if(log.isDebugEnabled()){
			log.debug("<<<<<<<<<<<<<<<<<<<<<<<<BatPrintInvoiceBean "+name+value+"<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		
	}
	
	/**
	 * 1.2受理单编号与照片编号同步接口
	 * @param saveBillRequ
	 */
	private String sendAutoAudit(String str){
		OutputStreamWriter out = null;
		URL httpurl = null;
		HttpURLConnection httpConn=null;
		boolean flag = false;
		try{
	       String url = BizEnv.getEnvString("crm.batsup.custpic.url");
		  //String url ="http://localhost:8080/idvs/get_boss_custpic_info"; 
			httpurl = new URL(url);
			httpConn = (HttpURLConnection) httpurl.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(10000);
			httpConn.setReadTimeout(10000);
			httpConn.setRequestProperty("content-type", "text/html");
			out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");
			out.write(str);
			out.flush();
			//serviceLogger.info("|#受理单保存|#发送自动稽核请求|#工单流水号=" + saveBillRequ.getTradeId() + "|#消息内容=" + autoAuditStr+"|#发送成功");
			flag =true;
		}catch(Exception e){
			//serviceLogger.error("|#受理单保存|#发送自动稽核请求失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
		}finally{
			if(null!=out){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	serviceLogger.error("|#受理单保存|#发送自动稽核请求|#关闭流失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
				}
			}
		}
		InputStream inStream = null;
		String strResult = null;
		BufferedReader br = null;
		InputStreamReader input=null;
		if (flag) {
			try {
					String line = null; 
					inStream = (InputStream) httpConn.getInputStream();
				//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId());
					input=new InputStreamReader(inStream,"UTF-8");
					br = new BufferedReader(input);
					StringBuilder sb = new StringBuilder();
			        while((line = br.readLine())!=null){
						sb.append(line);				
			        }
				strResult = sb.toString();
				//System.out.print(strResult);
			//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId() + "|#响应状态码=" +strResult);
			} catch (Exception e) {
			//	serviceLogger.info("|#受理单保存|#接收自动稽核响应失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
						br.close();
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#关闭流失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
						if(log.isInfoEnabled()) log.info(e.getMessage());
					}
				}
				if (httpConn != null) {
					httpConn.disconnect();
					httpConn = null;
				}
			}
		}
		return strResult;
	}
}
