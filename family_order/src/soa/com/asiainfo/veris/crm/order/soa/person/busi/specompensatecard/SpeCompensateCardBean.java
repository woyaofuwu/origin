/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

/**
 * @CREATED by gongp@2014-4-11 修改历史 Revision 2014-4-11 下午03:08:28
 */
public class SpeCompensateCardBean extends CSBizBean
{

    /**
     * 打印发票
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printSpeCompensateCard(IData input) throws Exception
    {
	    
        String tradeId = input.getString("TRADE_ID");

        long feeAmount = input.getLong("FEE_AMOUNT", 0);
        String totalFee = String.format("%1$3.2f", feeAmount / 100.0);

        String feeName = "SIM卡丢失赔偿款";

        String tradeTypeCode = "234";
        String tradeType = "特殊补偿卡业务";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData receiptData = new DataMap();
        IDataset printRes = new DatasetList();
        StringBuilder reamrkSb = new StringBuilder();
        
        reamrkSb.append("SIM卡号："+input.getString("SIM_CARD_NO")+"~");
        reamrkSb.append("卡数量："+"1张"+"~");
        reamrkSb.append("卡类型："+input.getString("SIM_CARD_TYPE")+"~");
        reamrkSb.append("归属员工："+input.getString("STAFF_ID")+"    ");
        reamrkSb.append("归属部门："+input.getString("STOCK_ID"));
        
        receiptData.put("REMARK", reamrkSb);
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("TRADE_TYPE", tradeType);
        receiptData.put("CUR_CYCLE", SysDateMgr.getSysDate("yyyyMM"));

        receiptData.putAll(input);
        receiptData.put("FEE_NAME", "SIM卡");
        receiptData.put("UNIT", "张");
        receiptData.put("QUANTITY", "1");
        receiptData.put("ALL_MONEY_LOWER", totalFee);
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(feeAmount / 100.0));
        receiptData.put("FEE_TYPE", feeName);
        receiptData.put("FEE", totalFee);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", getVisit().getStaffName());
        receiptData.put("DEPART_NAME", getVisit().getDepartName());
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        receiptData.put("STAFF_ID", getVisit().getStaffId());

        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0002, "0", tradeEparchyCode);
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);

        IData printInfo = new DataMap();
        printInfo.put("NAME", "特殊补偿卡收据");
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0002);
        printInfo.put("FEE_MODE", "2");
        printInfo.put("TOTAL_FEE", feeAmount);
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", "");
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        printRes.add(printInfo);

        return printRes;
    }


    public void createMainTradeData(IData input, String tradeId, String orderId) throws Exception
    {

        // 插主台账
        String sysTime = SysDateMgr.getSysTime();
        long fee = (long) Double.parseDouble(input.getString("FEE")) * 100;

        IData mainTradeData = new DataMap();

        if (input.containsKey("USER_ID"))
        {
            mainTradeData.put("USER_ID", input.getString("USER_ID", "0"));
            mainTradeData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", "-1"));
            mainTradeData.put("PRODUCT_ID", input.getString("PRODUCT_ID", "0"));
            mainTradeData.put("BRAND_CODE", input.getString("BRAND_CODE", ""));
            mainTradeData.put("OLCOM_TAG", "1");
            mainTradeData.put("PROCESS_TAG_SET", "1000000000000000000000000000000000000000");
        }
        else
        {
            mainTradeData.put("OLCOM_TAG", "0");
            mainTradeData.put("PROCESS_TAG_SET", "0000000000000000000000000000000000000000");
            mainTradeData.put("USER_ID", "0");
            mainTradeData.put("SERIAL_NUMBER", "-1");
        }

        mainTradeData.put("TRADE_ID", tradeId);
        mainTradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        mainTradeData.put("ORDER_ID", orderId);
        mainTradeData.put("TRADE_TYPE_CODE", "234");
        mainTradeData.put("PRIORITY", "250");
        mainTradeData.put("SUBSCRIBE_TYPE", "0");
        mainTradeData.put("SUBSCRIBE_STATE", "0");
        mainTradeData.put("NEXT_DEAL_TAG", "0");
        mainTradeData.put("IN_MODE_CODE", getVisit().getInModeCode());
        mainTradeData.put("CUST_ID", "-1");
        mainTradeData.put("CUST_NAME", "-1");

        mainTradeData.put("ACCT_ID", "0");

        mainTradeData.put("NET_TYPE_CODE", "00");
        mainTradeData.put("INTF_ID", "TF_B_TRADE_SIMCARDCOMPFEE,TF_B_TRADE");
        mainTradeData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        mainTradeData.put("CITY_CODE", getVisit().getCityCode());
        mainTradeData.put("SERIAL_NUMBER_B", "");
        mainTradeData.put("ACCEPT_DATE", sysTime);
        mainTradeData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        mainTradeData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        mainTradeData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        mainTradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        mainTradeData.put("TERM_IP", getVisit().getRemoteAddr());
        mainTradeData.put("OPER_FEE", input.getString("OPER_FEE", "0"));
        mainTradeData.put("ADVANCE_PAY", input.getString("ADVANCE_PAY", "0"));
        mainTradeData.put("FOREGIFT", input.getString("FOREGIFT", "0"));
        mainTradeData.put("FEE_STATE", "0");
        mainTradeData.put("EXEC_TIME", sysTime);
        mainTradeData.put("CANCEL_TAG", BofConst.CANCEL_TAG_NO);
        mainTradeData.put("UPDATE_TIME", sysTime);
        mainTradeData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        mainTradeData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        mainTradeData.put("REMARK", input.getString("REASON", "特殊补偿卡"));
        mainTradeData.put("RSRV_STR1", "1");
        mainTradeData.put("RSRV_STR2", String.valueOf(fee));
        mainTradeData.put("RSRV_STR3", String.valueOf(fee));
        mainTradeData.put("RSRV_STR4", input.getString("PAY_MODE"));
        mainTradeData.put("RSRV_STR5", input.getString("RES_TYPE_CODE"));
        mainTradeData.put("RSRV_STR6", input.getString("SIM_CARD_NO"));
        mainTradeData.put("RSRV_STR7", input.getString("SIM_CARD_NO"));
        mainTradeData.put("RSRV_STR8", input.getString("CAPACITY_TYPE_CODE", ""));
        mainTradeData.put("RSRV_STR9", input.getString("CARD_KIND_CODE", ""));

        mainTradeData.put("PF_WAIT", "0");

        Dao.insert("TF_B_TRADE", mainTradeData);

    }

    public void createOrderData(IData input, String orderId) throws Exception
    {

        MainOrderData mainOrderData = new MainOrderData();

        String inModeCode = CSBizBean.getVisit().getInModeCode();

        mainOrderData.setOrderTypeCode("234");
        mainOrderData.setAcceptDate(SysDateMgr.getSysDate());
        mainOrderData.setOrderId(orderId);
        mainOrderData.setTradeTypeCode("234");
        mainOrderData.setOrderState("0");
        mainOrderData.setPriority("0");
        mainOrderData.setNextDealTag("0");
        mainOrderData.setInModeCode(inModeCode);
        mainOrderData.setTradeStaffId(CSBizBean.getVisit().getStaffId());
        mainOrderData.setTradeDepartId(CSBizBean.getVisit().getDepartId());
        mainOrderData.setTradeCityCode(CSBizBean.getVisit().getCityCode());
        mainOrderData.setTradeEparchyCode(CSBizBean.getTradeEparchyCode());

        mainOrderData.setOperFee("0");
        mainOrderData.setForegift("0");
        mainOrderData.setAdvancePay("0");
        mainOrderData.setExecTime(SysDateMgr.getSysTime());
        mainOrderData.setCancelTag("0");
        mainOrderData.setBatchId("");

        mainOrderData.setCustId("-1");
        mainOrderData.setCustName("-1");
        mainOrderData.setPsptTypeCode("0");
        mainOrderData.setPsptId("1");
        mainOrderData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        mainOrderData.setCityCode(CSBizBean.getVisit().getCityCode());
        mainOrderData.setFeeState(BofConst.FEE_STATE_NO);

        mainOrderData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);

        IData data = mainOrderData.toData();
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));

        Dao.insert("TF_B_ORDER", data);
    }

    public void createSimCardFee(IData param, String tradeId) throws Exception
    {
        IData tradeSimCardCompFee = new DataMap();

        long fee = (long) Double.parseDouble(param.getString("FEE")) * 100;

        tradeSimCardCompFee.put("TRADE_ID", tradeId);
        tradeSimCardCompFee.put("SIMCARDNUM", "1");
        tradeSimCardCompFee.put("FEE", String.valueOf(fee));
        tradeSimCardCompFee.put("TOTAL_FEE", String.valueOf(fee));
        tradeSimCardCompFee.put("PAY_MONEY_CODE", param.getString("PAY_MODE"));
        tradeSimCardCompFee.put("SIM_TYPE_CODE", param.getString("RES_TYPE_CODE"));
        tradeSimCardCompFee.put("START_VALUE", param.getString("SIM_CARD_NO"));
        tradeSimCardCompFee.put("END_VALUE", param.getString("SIM_CARD_NO"));
        tradeSimCardCompFee.put("RSRV_STR1", "");
        tradeSimCardCompFee.put("RSRV_STR2", "");
        tradeSimCardCompFee.put("RSRV_STR3", "");
        tradeSimCardCompFee.put("REMARK", param.getString("REASON"));
        tradeSimCardCompFee.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeSimCardCompFee.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        Dao.insert("TF_B_TRADE_SIMCARDCOMPFEE", tradeSimCardCompFee);

    }
		
		 /**
     * QR-20150115-04特殊补偿卡业务错单问题
     * 增加查询时候判断是否预开户未返单用户，如果是给予提示！
     * */
    public void getOperModeBySimCardNo(String simCardNo)throws Exception{
    	String open_mode="";
    	IData inparams=new DataMap();
    	inparams.put("SIMCARDNO", simCardNo);
    	SQLParser parser = new SQLParser(inparams);  
        parser.addSQL(" select t1.open_mode from tf_f_user t1 where t1.user_id=( "); 
        parser.addSQL(" select  user_id from tf_f_user_res t "); 
        parser.addSQL(" where t.res_code=:SIMCARDNO)");  
        IDataset ids= Dao.qryByParse(parser);
        if(ids!=null && ids.size()>0){
        	open_mode=ids.getData(0).getString("OPEN_MODE");
        	if(!"".equals(open_mode)&&open_mode.equals("1")){
        		CSAppException.apperr(ResException.CRM_RES_92); 
        	}
        }
        
    }
		
    public IDataset getInfoBySimCardNo(IData param) throws Exception
    {
				this.getOperModeBySimCardNo(param.getString("cond_SIM_CARD_NO"));
        IDataset results = ResCall.getSpecCompCardInfo(param.getString("cond_SIM_CARD_NO"), this.getVisit().getStaffEparchyCode());

        if (results.size() == 1)
        {
            IData result = results.getData(0);

            /*
             * int X_RECORDNUM = result.getInt("X_RECORDNUM", 0); if (X_RECORDNUM <= 0) {
             * //common.error("557509:获取资源资料信息未发现记录！"); CSAppException.apperr(CrmCardException.CRM_CARD_269); }
             */
            if(IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(ResException.CRM_RES_10); 
            }            
            
            IData feeData = this.getSpeCompCardFee(result);

            result.put("FEE", feeData.getString("PARA_CODE2"));
            result.put("SIM_CARD_TYPE", feeData.getString("SIM_CARD_TYPE"));
            result.put("FEENAME", feeData.getString("PARA_CODE4"));

        }
        else
        {
            // common.error("557503:特殊补偿卡业务：获取SIM卡资料异常！");
            CSAppException.apperr(CrmCardException.CRM_CARD_268);
        }
        return results;
    }

    public IData getSpeCompCardFee(IData param) throws Exception
    {

        IDataset dataset = CommparaInfoQry.getCommPkInfo("CSM", "234", param.getString("RES_TYPE_CODE"), "0898");
        IData result = new DataMap();

        if (dataset.size() == 1)
        {
            result.put("PARA_CODE2", FeeUtils.Fen2Yuan(dataset.getData(0).getString("PARA_CODE2")));
            result.put("PARA_CODE4", dataset.getData(0).getString("PARA_CODE4"));
            result.put("SIM_CARD_TYPE", dataset.getData(0).getString("PARAM_NAME"));
        }
        else
        {
            CSAppException.apperr(ResException.CRM_RES_83);
        }
        return result;
    }

    public IDataset tradeReg(IData param) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String orderId = SeqMgr.getOrderId();

        IDataset userInfos = UserInfoQry.getValidUserInfoByResCode(param.getString("SIM_CARD_NO"));

        if (IDataUtil.isNotEmpty(userInfos))
        {

            IData userInfo = userInfos.getData(0);
            UcaData ucaData = UcaDataFactory.getNormalUca(userInfo.getString("SERIAL_NUMBER"));

            param.put("USER_ID", ucaData.getUserId());
            param.put("SERIAL_NUMBER", ucaData.getUser().getSerialNumber());
            param.put("PRODUCT_ID", ucaData.getProductId());
            param.put("BRAND_CODE", ucaData.getBrandCode());
        }

        this.createMainTradeData(param, tradeId, orderId);
        this.createOrderData(param, orderId);
        this.createSimCardFee(param, tradeId);

        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("ORDER_ID", orderId);
        data.put("TRADE_TYPE_CODE", "234");
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        dataset.add(data);

        return dataset;
    }

}
