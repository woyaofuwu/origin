
package com.asiainfo.veris.crm.order.soa.person.busi.selfhelp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SelfHelpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.financeboss.FinancialFeeUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.SelfHelpInfoUtil;

/**
 * 自助终端服务实现类
 * 
 * @author zhouwu
 * @date 2014-07-29 21:34:40
 */
public class TerminalManageBean extends CSBizBean
{

    /**
     * 查询归属业务区
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUnionCancelInfos(IData data) throws Exception
    {
    	return SelfHelpInfoUtil.queryUnionCancelInfos(data);
    }

    /**
     * 自助终端资料查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryTerminals(IData data, Pagination pagination) throws Exception
    {
		return SelfHelpInfoUtil.queryTerminals(data, pagination);
    }

    /**
     * 保存自助终端
     * 
     * @param data
     * @throws Exception
     */
    public void saveTerminal(IData data) throws Exception
    {
		String departCode = data.getString("DEPART_CODE");
		String deviceStaffId = data.getString("DEVICE_STAFF_ID");
		String departCode4 = departCode.substring(0, 4);//渠道编码前四位
		String departCode5 = departCode.substring(0, 5);//渠道编码前五位
		String deviceStaffId4 = deviceStaffId.substring(0, 4);//自助终端工号前四位
		String deviceStaffId4And6 = deviceStaffId.substring(0, 4)+deviceStaffId.substring(5, 6);
		String deviceStaffId5 = deviceStaffId.substring(4, 5);//自助终端工号第五位必须为S
		boolean checkFlag = false;
		
		if((StringUtils.equals(departCode4, "HNSJ") || StringUtils.equals(departCode4, "HNKF"))
				&& StringUtils.equals(deviceStaffId5, "S") && StringUtils.equals(departCode4, deviceStaffId4))
		{
			checkFlag = true;
		}
		else if(departCode5.equals(deviceStaffId4And6) && StringUtils.equals(deviceStaffId5, "S"))
		{
			checkFlag = true;
		}
		else
		{
			checkFlag = false;
		}
		
		if(checkFlag)
		{
			SelfHelpInfoUtil.checkRuleBeforeSave(data);//保存前的规则校验
			
			data.put("DEVICE_ID", SeqMgr.getTradeId()); // 设备标识
			data.put("DEVICE_TYPE_CODE", "1"); // 设备类型
			data.put("EPARCHY_CODE", "0898"); // 地州编码
			data.put("END_DATE", SysDateMgr.getTheLastTime()); // 有效时间
			data.put("RENT_MONTHS", "0"); // 已租时长
			data.put("FEE", "0"); // 已缴纳金额
			data.put("BALANCE", "0"); // 未缴纳金额
			data.put("RECV_FEE", "0"); // 预存款
			data.put("REMOVE_TAG", "0"); // 注销标志：0-正常、1-注销
			data.put("OWE_TAG", "0"); // 欠费标志：0-欠费、1-未欠费
			data.put("CREATE_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));  // 创建时间
			data.put("CREATE_STAFF_ID", getVisit().getStaffId());  // 创建员工
			data.put("CREATE_DEPART_ID", getVisit().getDepartId());  // 创建部门
			
			boolean isSuccess = Dao.insert("TF_R_SELF_HELP_DEVICE", data);
			
			if (!isSuccess)
			{
				//新增失败
				CSAppException.apperr(SelfHelpException.CRM_SELFHELP_3);
			}
		}
		else
		{
			//自助终端工号[%s]显示工号的第5位必须为S，且需与渠道编码对应，请点击重置，重新添加。
			CSAppException.apperr(SelfHelpException.CRM_SELFHELP_4, deviceStaffId);
		}
    }

    /**
     * 自助终端资料删除
     * 
     * @param data
     * @throws Exception
     */
    public void deleteTerminal(IData data) throws Exception
    {
        String dealList = data.getString("DEAL_LIST");

        String[] checks = StringUtils.split(dealList, ";");

        for (int i = 0, size = checks.length; i < size; i++)
        {
            String deviceId = checks[i];

            IData params = new DataMap();
            params.put("DEVICE_ID", deviceId);
            params.put("DESTROY_STAFF_ID", getVisit().getStaffId());
            params.put("DESTROY_DEPART_ID", getVisit().getDepartId());
            params.put("DESTROY_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            params.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            params.put("REMOVE_TAG", "1");// 注销标识：0-正常、1-注销

            Dao.save("TF_R_SELF_HELP_DEVICE", params, new String[]
            {"DEVICE_ID"});
        }
    }
    
    /**
     * 自助终端资料单个查询
     * 
     * @param data
     * @throws Exception
     */
    public IDataset queryTerminal(IData data, Pagination pagination) throws Exception
    {
		return SelfHelpInfoUtil.queryTerminal(data, pagination);
    }
    
    /**
     * 自助终端保存费用信息
     * @param data
     * @return
     * @throws Exception
     */
    public IData saveTerminalFee(IData data) throws Exception
    {
		String acceptTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND); // 缴费时间
		String year = SysDateMgr.getSysDate("yyyy"); // 缴费年份
		String acceptMonth = SysDateMgr.getCurMonth(); // 缴费月份
		String cityCode = getVisit().getCityCode(); // 业务区
		String stockId = getVisit().getDepartId(); // 部门编码
		String staffId = getVisit().getStaffId(); // 费用工号

		String deviceId = data.getString("DEVICE_ID","");  // 设备标识

		long balance = Long.valueOf(StringUtils.split(data.getString("BALANCE",""), ".")[0]) * 100; // 未缴纳金额
		long recvFeePay = Long.valueOf(StringUtils.split(data.getString("TOTAL_FEE",""), ".")[0]) * 100; // 缴费金额
		long recvFeeOld = Long.valueOf(StringUtils.split(data.getString("RECV_FEE",""), ".")[0]) * 100; // 原有预存款
		long recvFeeNew = recvFeeOld + recvFeePay; // 当前预存款
		
		//插入费用台帐
		LanuchUtil util = new LanuchUtil();
        data.put("TRADE_TYPE_CODE", "2108");//供应商缴费
        data.put("USER_ID", "-1");
        data.put("OPER_FEE", recvFeePay);//卡费

        String[] id  = util.writeLanuchLog(data).split(",");
        String tradeId = id[0];
        String order_id  =id[1];
        data.put("ORDER_ID", order_id);
        
        // 插入费用台账表
//        createFeeSubTrade(tradeId, data);

		data.put("DEVICE_CHARGE_ID", tradeId); // 缴费流水
		data.put("ACCEPT_TIME", acceptTime); // 缴费时间
		data.put("YEAR", year); // 缴费年份
		data.put("ACCEPT_MONTH", acceptMonth); // 缴费月份
		data.put("FEE", balance); // 应缴纳金额
		data.put("BALANCE", balance); // 未缴纳金额
		data.put("RECV_FEE", recvFeePay); // 缴费金额
		data.put("DEVICE_NUMBER", data.getString("DEVICE_NUMBER2", ""));
		
		data.put("EPARCHY_CODE", "0898"); // 地州编码
		data.put("CITY_CODE", cityCode); // 业务区
		data.put("STOCK_ID", stockId); // 部门编码
		data.put("STAFF_ID", staffId); // 费用工号

		boolean flagPaylog = false;
		boolean flagDevice = false;

		boolean flag = false;

		flagPaylog = Dao.insert("TF_R_SELF_HELP_DEVICE_PAYLOG", data);

		/**
		 * 更新设备预存款
		 */
		IData paramRecvFee = new DataMap();

		paramRecvFee.put("RECV_FEE", recvFeeNew);
		paramRecvFee.put("DEVICE_ID", deviceId);
		
		flagDevice = Dao.save("TF_R_SELF_HELP_DEVICE", paramRecvFee);
		

		if (flagPaylog == true && flagDevice == true)
		{
			flag = true;
		}
        //财务化日志tf_a_bfas_in
		if(flag)
		{
			data.put("TRADE_ID", data.getString("DEVICE_CHARGE_ID"));
			//insertTradeBfasIn(data);
			
//			String[] id  = util.writeLanuchLog(input).split(",");
		}
		return data;
    }
    
    public void insertTradeBfasIn(IData data) throws Exception
    {
		IDataset listdata = new DatasetList();
        IData param = new DataMap();
        param.put("SALE_TYPE_CODE", "1"); // 销售类型
        param.put("PAY_MONEY_CODE", data.getString("PAY_MODE")); // 收款方式
        param.put("FEE_ITEM_TYPE_CODE", "1"); // 费用明细类型
        param.put("FEE_TYPE_CODE", "16"); // 费用类型
        param.put("RECE_FEE", data.getString("FEE"));// 应收金额
        param.put("FEE", data.getString("RECV_FEE"));// 实收金额
        param.put("ACC_DATE", SysDateMgr.getSysDate());// 会计日期
        param.put("OPER_DATE", SysDateMgr.getSysDate());// 交易日期
        param.put("CANCEL_TAG", "0"); // 返销标记 0 正业务 1返销业务
        param.put("PROC_TAG", "0"); // 0 未处理 1正在拆分 3拆分成功 4错单
        param.put("IN_MODE_CODE", getVisit().getInModeCode());
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("CITY_CODE", getVisit().getCityCode());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("OPER_STAFF_ID", getVisit().getStaffId());
        // 销售数量/笔数 RSRV_NUM2
        param.put("RSRV_NUM2", "1");
        // 业务类型
        param.put("OPER_TYPE_CODE", "CHNL02");
        param.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));
        param.put("TRADE_ID", data.getString("TRADE_ID"));

        listdata.add(param);
        FinancialFeeUtil.insertTradeBfasIn(listdata);

        IData inparam = new DataMap();
        inparam.put("SUB_LOG_ID", data.getString("TRADE_ID"));
        inparam.put("CANCEL_TAG", "0");
        FinancialFeeUtil.insertBfasIn(inparam);
    }
    
    /**
     * 加载打印信息
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset loadPrintData(IData data) throws Exception
    {
    	String tradeId = data.getString("TRADE_ID");

        long feeAmount = data.getLong("FEE_AMOUNT", 0)*100;
        String totalFee = String.format("%1$3.2f", feeAmount / 100.0);

        String feeName = data.getString("FEE_NAME");

        String tradeTypeCode = "2108";
        String tradeType = "自助终端缴费";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData receiptData = new DataMap();
        IDataset printRes = new DatasetList();
        
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("TRADE_TYPE", tradeType);
        receiptData.put("ALL_MONEY_LOWER", totalFee);
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(feeAmount / 100.0));
        receiptData.put("FEE_TYPE", feeName);
        receiptData.put("FEE", totalFee);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", getVisit().getStaffName());
        receiptData.put("DEPART_NAME", getVisit().getDepartName());
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());

        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0001, "0", tradeEparchyCode);
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);

        IData printInfo = new DataMap();
        printInfo.put("NAME", "自助终端服务费");
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0001);
        printInfo.put("FEE_MODE", "0");
        printInfo.put("TOTAL_FEE", feeAmount);
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", "");
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        printRes.add(printInfo);

        return printRes;
    }

}
