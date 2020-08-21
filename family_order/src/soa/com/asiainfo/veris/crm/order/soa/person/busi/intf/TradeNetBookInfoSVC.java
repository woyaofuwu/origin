
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 网上预约登记接口
 * 
 * @author liutt
 */
public class TradeNetBookInfoSVC extends CSBizService
{
    /**
     * 历史台账
     * 
     * @param data
     * @param ucaData
     * @param tradeId
     * @param accpetMonth
     * @throws Exception
     */
    private void insHistoTrade(IData data, UcaData ucaData, String tradeId, String accpetMonth) throws Exception
    {
    	String userId = "-1";
    	String custId = "-1";
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	String custName = data.getString("USER_NAME");
    	String eparchyCode = "0898";
    	String productId = "-1";
    	String brandCode = "-1";
    	String acctId = "-1";
    	String cityCode = "HNYD";
    	String netTypeCode = "-1";
        String sysdate = SysDateMgr.getSysTime();
        //有手机宽带从客户资料表获取数据
    	if(data.getString("USER_FLAG").equals("1")){
    		userId = ucaData.getUserId();
    		custId = ucaData.getCustId();
    		brandCode = ucaData.getBrandCode();
    		productId =	ucaData.getProductId();
    		acctId = ucaData.getAcctId();
    		serialNumber = ucaData.getSerialNumber();
    		custName = ucaData.getCustomer().getCustName();
    		eparchyCode = ucaData.getUser().getEparchyCode();
    		cityCode = ucaData.getUser().getCityCode();
    		netTypeCode = ucaData.getUser().getNetTypeCode();
    	}
        IData trade = new DataMap();
        trade.put("TRADE_ID", tradeId);
        trade.put("ACCEPT_MONTH", accpetMonth);
        trade.put("ORDER_ID", SeqMgr.getOrderId());
        trade.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        trade.put("PRIORITY", "100");
        trade.put("SUBSCRIBE_TYPE", "0");
        trade.put("SUBSCRIBE_STATE", "9");
        trade.put("NEXT_DEAL_TAG", "0");
        trade.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()));
        trade.put("USER_ID", userId);
        trade.put("CUST_ID", custId);
        trade.put("PRODUCT_ID", productId);
        trade.put("BRAND_CODE", brandCode);
        trade.put("CUST_NAME", custName);
        trade.put("ACCT_ID", acctId);
        trade.put("SERIAL_NUMBER", serialNumber);
        trade.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", netTypeCode));
        trade.put("EPARCHY_CODE", eparchyCode);
        trade.put("CITY_CODE", cityCode);
        trade.put("ACCEPT_DATE", sysdate);
        trade.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        trade.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        trade.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
        trade.put("TRADE_EPARCHY_CODE", eparchyCode);
        trade.put("FINISH_DATE", sysdate);
        trade.put("EXEC_TIME", sysdate);
        trade.put("UPDATE_TIME", sysdate);
        trade.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        trade.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        trade.put("OPER_FEE", "0");
        trade.put("FOREGIFT", "0");
        trade.put("ADVANCE_PAY", "0");
        trade.put("FEE_STATE", "0");
        trade.put("OLCOM_TAG", "0");
        trade.put("CANCEL_TAG", "0");
        trade.put("PROCESS_TAG_SET", "0");
//        trade.put("REMARK", data.getString("REMARK"));
        trade.put("RSRV_STR10", data.getString("REMARK"));//由于字段不允许超过500，将remark字段记录至RSRV_STR10 REQ201604060012 宽带业务预约功能需求-网厅、wap厅（微信）
        trade.put("PF_WAIT", "0");// 是否发开通
        Dao.insert("TF_BH_TRADE", trade,Route.getJourDb());

    }

    /**
     * 插入预约记录表
     * 
     * @param data
     * @param ucaData
     * @param departInfo
     * @param tradeId
     * @param accpetMonth
     * @param tradeType
     * @return
     * @throws Exception
     */
    private IData instBookInfo(IData data, UcaData ucaData, IData departInfo, String tradeId, String accpetMonth, String tradeType, String cityCode, String userName, String psptId,String channelNum,String chatNumber) throws Exception
    {
    	String userId = "-1";
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	String custName = userName;
    	String eparchyCode = "0898";
    	if(data.getString("USER_FLAG").equals("1")){
    		userId = ucaData.getUserId();
    		serialNumber = ucaData.getSerialNumber();
    		custName = ucaData.getCustomer().getCustName();
    		eparchyCode = ucaData.getUser().getEparchyCode();
    	}
        IData bookInfo = new DataMap();
        bookInfo.put("TRADE_ID", tradeId);
        bookInfo.put("ACCEPT_MONTH", accpetMonth);
        bookInfo.put("USER_ID", userId);
        bookInfo.put("SERIAL_NUMBER", serialNumber);
        bookInfo.put("PSPT_TYPE_CODE", "");
        bookInfo.put("RSRV_STR9", userName);
        bookInfo.put("PSPT_ID", psptId);
        bookInfo.put("CUST_NAME", custName);
        bookInfo.put("CONTACT_PHONE", "");
        bookInfo.put("IN_MOD_CODE", data.getString("IN_MODE_CODE"));
        bookInfo.put("BOOK_TYPE_CODE", data.getString("BOOK_TYPE_CODE"));
        bookInfo.put("BOOK_TYPE", data.getString("BOOK_TYPE"));
        bookInfo.put("BOOK_DATE", data.getString("BOOK_DATE"));
        bookInfo.put("BOOK_STATUS", "0");
        bookInfo.put("BOOK_PHONE", "");
        bookInfo.put("GOODS_ID", "");
        bookInfo.put("BOOK_END_DATE", data.getString("BOOK_END_DATE"));
        bookInfo.put("DOOR_END_DATE", "");
        bookInfo.put("TRADE_STAFF_ID", "");
        bookInfo.put("TRADE_DEPART_ID", data.getString("NETTRADE_DEPART_ID"));
        bookInfo.put("TRADE_CITY_CODE", departInfo.getString("AREA_CODE"));
        bookInfo.put("TRADE_EPARCHY_CODE", eparchyCode);
        bookInfo.put("REMARK", data.getString("REMARK"));
        bookInfo.put("DEPART_NAME", departInfo.getString("DEPART_NAME"));
        bookInfo.put("TRADE_TYPE", tradeType);
        bookInfo.put("RSRV_STR1", cityCode);
		bookInfo.put("RSRV_TAG1", data.getString("USER_FLAG","").trim());//新增标识区分有无手机宽带预约,0代表无手机,1代表有手机
		bookInfo.put("RSRV_STR8", channelNum);//新增渠道工号
		bookInfo.put("RSRV_STR11", chatNumber);//新增联系号码
        /**
         * 20160819
         * 局方需求变动(20160825)
         * 预约订单来源  用 in_mod_code
         */
        String  THT1171=data.getString("RSRV_STR4");
        //有无资源
        if(!"".equals(THT1171)&&THT1171!=null){
        	bookInfo.put("RSRV_STR4", THT1171);
        }
        Dao.insert("TF_B_TRADE_BOOK", bookInfo, Route.CONN_CRM_CEN);
        return bookInfo;
    }

    /**
     * 查询订单信息 - 网上预约查询接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryBookInfo(IData data) throws Exception
    {
        TradeNetBookInfoBean bean = BeanManager.createBean(TradeNetBookInfoBean.class);
        return bean.qryBookInfo(data);
    }

    private String replaceAll(String base, IData data)
    {
        Iterator iter = data.keySet().iterator();
        while (iter.hasNext())
        {
            String key = (String) iter.next();
            String value = (String) data.get(key);
            Pattern pattern = Pattern.compile(key);
            Matcher matcher = pattern.matcher(base);
            base = matcher.replaceAll(value);

        }
        return base;
    }

    /**
     * 插入短信表
     * 
     * @param content
     * @param bookInfo
     * @throws Exception
     */
    private void sendSms(String content, IData bookInfo) throws Exception
    {
        IData newParam = new DataMap();
        newParam.put("EPARCHY_CODE", bookInfo.getString("TRADE_EPARCHY_CODE"));
        newParam.put("RECV_OBJECT", bookInfo.getString("SERIAL_NUMBER"));
        newParam.put("RECV_ID", bookInfo.getString("USER_ID"));// 被叫对象标识:传用户标识
        newParam.put("NOTICE_CONTENT", content);// 短信内容类型:0－指定内容发送
        newParam.put("SMS_PRIORITY", 1000);// 短信优先级
        newParam.put("REFER_STAFF_ID", bookInfo.getString("TRADE_STAFF_ID"));// 提交员工
        newParam.put("REFER_DEPART_ID", bookInfo.getString("TRADE_DEPART_ID"));// 提交部门
        newParam.put("REMARK", "网上预约通知");
        newParam.put("IN_MODE_CODE", bookInfo.getString("IN_MOD_CODE", CSBizBean.getVisit().getInModeCode()));
        SmsSend.insSms(newParam);
    }

    /**
     * 网上预约登记 参数准备NET_TYPE_CODE,TRADE_STAFF_ID,TRADE_TYPE_CODE,TRADE_CITY_CODE,TRADE_DEPART_ID
     * BOOK_TYPE_CODE,IN_MODE_CODE,SERIAL_NUMBER,NETTRADE_DEPART_ID,BOOK_TYPE,BOOK_DATE,BOOK_END_DATE,REMARK,USER_NAME,PSPT_ID ,Channel_Num--渠道工号,chatNumber联系号码 
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData setBookInfo(IData data) throws Exception
    {
        String bookTypeCode = data.getString("BOOK_TYPE_CODE","").trim();
        String serialNum = data.getString("SERIAL_NUMBER","").trim();
        String userName = data.getString("USER_NAME","").trim();
        String psptId = data.getString("PSPT_ID","").trim();
        String netTradeDepartId = data.getString("NETTRADE_DEPART_ID","").trim();
        String bookType = data.getString("BOOK_TYPE","").trim();// 0
        String bookDate = data.getString("BOOK_DATE","").trim();
        String bookEndDate = data.getString("BOOK_END_DATE","").trim();
        String remark = data.getString("REMARK","").trim();
        String cityCode = data.getString("CITY_CODE","").trim();
        String userFlag = data.getString("USER_FLAG","").trim(); //0代表无手机,1代表有手机
        //新增渠道工号
        String channelNum = data.getString("Channel_Num","").trim();
        //新增联系号码
        String chatNumber = data.getString("CHAT_NUMBER","").trim(); 
        IDataset userInfo = new DatasetList();
        IData custInfo = new DataMap();
        /**
         * 由于在记录  历史台账 时  用的是 TRADE_CITY_CODE
         */
        data.put("TRADE_CITY_CODE", cityCode);
        
        if (StringUtils.isBlank(bookDate))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1048);// "9001","请输入开始日期参数！"
        }
        if (StringUtils.isBlank(bookEndDate))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1049);// "9002","请输入结束日期参数！"
        }
        if (StringUtils.isNotBlank(remark) && remark.length() > 500)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1050);// "9003","预约说明字符数请小于等于500!"
        }
        String eparchyCode = "0898"; //无手机宽带默认地州编码为0898
        //有手机宽带查询客户资料begin
        if(userFlag.equals("1")){
        userInfo = UserInfoQry.getUserInfoBySn(serialNum, "0", "00");
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// "9004","用户资料不存在或以销户!"
        }
        custInfo = CustomerInfoQry.qryCustInfo(userInfo.getData(0).getString("USECUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_35);// "9005","客户资料不存在!"
        }
        eparchyCode = userInfo.getData(0).getString("EPARCHY_CODE");
        }
        //有手机宽带查询客户资料end
        IDataset commparainfo = CommparaInfoQry.getCommParas("CSM", "195", "TRADE_TYPE", bookTypeCode, eparchyCode);
        if (IDataUtil.isEmpty(commparainfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1051);// "9006","该业务类型未在预约承载配置表中配置，请配置!"
        }
        IDataset bookset = CommparaInfoQry.getCommParaByBookDate("CSM", "195", "COUNT", netTradeDepartId, eparchyCode, bookDate);
        int maxCount = -1;
        if (IDataUtil.isNotEmpty(bookset))
        {
            maxCount = ((IData) bookset.get(0)).getInt("PARA_CODE2");
        }

        String tradeId = SeqMgr.getTradeId();
        String accpetMonth = null;
        if (bookDate != null && bookDate.length() > 0)
        {
            accpetMonth = SysDateMgr.getMonthForDate(bookDate);
            if (StringUtils.isBlank(accpetMonth))
            {
                if (tradeId != null)
                    accpetMonth = StrUtil.getAcceptMonthById(tradeId);
            }
        }
        // 获取未处理预约数量，根据预约日期获得分区key month
        IDataset tradeInfo = TradeInfoQry.getTradeBookInfos(accpetMonth, bookDate, netTradeDepartId, "0");
        int count = -1;// -1表示没有配置未处理预约数量
        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            count = ((IData) tradeInfo.get(0)).getInt("CC");
        }
        if (maxCount > 0 && count >= maxCount)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1052);// "9008","该部门的未处理预约数量已达到配置的上限!"
        }
        IData departInfo = UDepartInfoQry.qryDepartByDepartId(netTradeDepartId);
        if (IDataUtil.isEmpty(departInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1053);// "9009","该部门信息未配置!"
        }
        UcaData ucaData = new UcaData();
        //有手机宽带才查询用户信息
        if(userFlag.equals("1")){ 
            ucaData = UcaDataFactory.getNormalUca(serialNum);
        }
        String tradeType = commparainfo.getData(0).getString("PARA_CODE2");
        //新增渠道工号,联系号码
        IData bookInfo = this.instBookInfo(data, ucaData, departInfo, tradeId, accpetMonth, tradeType, cityCode, userName, psptId,channelNum,chatNumber);
        this.insHistoTrade(data, ucaData, tradeId, accpetMonth);
        //有手机宽带下发短信begin
        if(userFlag.equals("1"))
        {
	        String productId = "2";
	        if ("0".equals(bookType))
	        {
	            productId = "1";
	        }
	        IDataset smsinfo = TradeSmsInfoQry.getTradeSmsInfo("195", null, productId, null, null, null, null);
	        if (IDataUtil.isEmpty(smsinfo))
	        {
	            CSAppException.apperr(CrmCommException.CRM_COMM_1047);// "90010","请配置在TD_B_TRADE_SMS中配置短信模板"
	
	        }
	        String s = smsinfo.getData(0).getString("NOTICE_CONTENT");
	        IData map = new DataMap();
	        map.put("#name#", custInfo.getString("CUST_NAME"));
	        map.put("#date#", bookDate);
	        map.put("#deptName#", departInfo.getString("DEPART_NAME"));
	        map.put("#tradeType#", tradeType);
	        map.put("#tradeId#", tradeId);
	        String content = replaceAll(s, map);
	        if(!bookTypeCode.equals("118")&&!bookTypeCode.equals("119")&&!bookTypeCode.equals("120")){//不是宽带开户、宽带移机、宽带拆机
	            sendSms(content, bookInfo); // 插入短信表，发送给客户的短信
	        }        
        
	        if ("1".equals(bookType)&&tradeType.contains("宽带")){
	        	if(StringUtils.isNotBlank(cityCode)){
	        		IDataset setInfos = CommparaInfoQry.getCommparaByCode1("CSM", "9218", cityCode, "0898");
	        		if(IDataUtil.isNotEmpty(setInfos)){
	        			String templateId = "CRM_SMS_PER_COMM_16041401";
	    	   	        IData templateInfo =getTemplateInfo(templateId);
	    	   	        String staffId = "";
	    	   	        String staffSn = "";
	    	   	        String staffUId = "";
	    	   	        String contentNew = "";
	    	   	        IData inparam = new DataMap();
	    	   	        String sNew = getSmsContent(templateInfo, null);
	    	   	        for(int i=0;i<setInfos.size();i++){
	    	   	        	staffId = setInfos.getData(i).getString("PARAM_CODE");
	    	   	        	staffSn = setInfos.getData(i).getString("PARA_CODE2");
	    	   	        	UcaData staffUd = UcaDataFactory.getNormalUca(staffSn);
	    	   	        	staffUId = staffUd.getUserId();
	    	   	        	inparam.put("#staffId#", staffId);
	    	   	        	inparam.put("#tradeId#", tradeId);
	    	   	        	contentNew = replaceAll(sNew,inparam);
	    	   	        	IData smsInfo = new DataMap();
	    		   	        smsInfo.put("IN_MODE_CODE", "2");
	    		   	        smsInfo.put("CHAN_ID", "C006");
	    		   	        smsInfo.put("SMS_KIND_CODE", "08");
	    		   	        smsInfo.put("DEAL_STATE", "15");
	    		   	        smsInfo.put("NOTICE_CONTENT", contentNew);
	    		   	        smsInfo.put("RECV_OBJECT", staffSn);
	    		   	        smsInfo.put("RECV_ID", staffUId);
	    		   	        SmsSend.insSms(smsInfo);	
	    	   	        }
	        		}
	        	}
	        }        
        }
        //有手机宽带下发短信end
        IData result = new DataMap();
        result.put("TRADE_ID", tradeId);
        return result;
    }
    
    public IData getTemplateInfo(String templateId) throws Exception
    {
        IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
        return templateInfo;
    }
    
    public String getSmsContent(IData templateInfo, IData params) throws Exception
    {
        if (IDataUtil.isEmpty(templateInfo))
        {
            CSAppException.apperr(BizException.CRM_BIZ_3);
        }
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        StringBuilder sb = new StringBuilder();
        sb.append(templateInfo.getString("TEMPLATE_CONTENT1", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT2", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT3", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT4", ""));
        sb.append(templateInfo.getString("TEMPLATE_CONTENT5", ""));

        String templateContent = sb.toString();
        if (IDataUtil.isNotEmpty(params))
        {
            exector.prepare(params);// 模板变量解析
            templateContent = exector.applyTemplate(templateContent);
        }
        return templateContent;
    }
}
