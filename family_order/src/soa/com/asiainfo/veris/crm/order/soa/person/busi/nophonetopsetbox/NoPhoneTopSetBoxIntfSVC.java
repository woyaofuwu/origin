package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.NoPhoneTradeUtil;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.ResTypeEnum;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

import dnapay.common.Converter;
public class NoPhoneTopSetBoxIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    private static Logger logger = Logger.getLogger(NoPhoneTopSetBoxIntfSVC.class);

    /**
     * 魔百和终端校验，并更新魔百和受理信息临时表(提供给PBOSS校验)
     * @param input
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IData checkTopSetBoxTerminal(IData input) throws Exception
    {
    	//重设交易工号信息
    	setVisitTradeStaff(input);

        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        
        String tradeId = input.getString("TRADE_ID");
        
        if (StringUtils.isBlank(resNo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "魔百和终端编码为空!");
        }
        
        if (StringUtils.isBlank(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TRADE_ID参数为空!");
        }
        
        //判断是否是4900无手机魔百和开户
        IData trade4900Info = TradeInfoQry.queryTradeByTradeIdAndTradeType(tradeId, "4900");
        
        //如果是魔百和开户工单，则取出工单内手机号码，判断用户表是否存在有效的宽带用户
        if(IDataUtil.isNotEmpty(trade4900Info))
        {
        	//通过UU关系表查询宽带账户
        	IDataset tradeRelas = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(tradeId,"47");
        	String WideNetSerialNumber = tradeRelas.getData(0).getString("SERIAL_NUMBER_B").replace("KD_", "");
        	
        	boolean isWidenetUser = BreQry.isWideNetUser(WideNetSerialNumber);//判断是否为宽带完工用户
        	if (!isWidenetUser)//如果不是有效的宽带完工用户
            {
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "由于宽带还未完工，暂时无法办理!");
            }
        }
        //判断结束
        
        IDataset topSetBoxInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "TOPSETBOX");   
        
        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            IData topSetBoxInfo = topSetBoxInfos.first();
            
            //扩展字段1存放的手机号码
            String serialNumber = topSetBoxInfo.getString("RSRV_STR1");
            String top_Type = "";//魔百和产品类型，OTT还是IPTV，modify_by_duhj_kd  20200519
            String topSetproductId = topSetBoxInfo.getString("RSRV_STR3");//魔百和开户产品
            
            IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", topSetproductId, "0898");
            if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
            {
                top_Type = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            }
            if("IPTV".equals(top_Type)){
                this.checkIsTopset(resNo,ResTypeEnum.IPTV.getValue(),"IPTV");
            }else {
                this.checkIsTopset(resNo,ResTypeEnum.TOPSET.getValue(),"JDH");
            }
            
            //调用华为接口进行终端查询校验
            IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
            
            if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
            {
                IData res = retDataset.first();
                String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
                String supplyId = res.getString("SUPPLY_COOP_ID", "");
                retData.put("X_RESULTCODE", "0");
                retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
                retData.put("RES_ID", resNo); // 终端串号
                retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
                retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
                retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
                retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
                retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
                retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
                String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
                retData.put("RES_STATE_CODE", resStateCode);
                retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
                retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
                retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
                
                retData.put("DEVICE_COST", res.getString("DEVICE_COST","0")); // 进货价格
                
                //将终端校验信息设置到魔百和临时受理信息中
                topSetBoxInfo.put("RSRV_STR8", resNo);
                topSetBoxInfo.put("RSRV_STR9", "4");
                topSetBoxInfo.put("RSRV_STR10", retData.getString("RES_BRAND_CODE"));
                topSetBoxInfo.put("RSRV_STR11", retData.getString("RES_BRAND_NAME"));
                topSetBoxInfo.put("RSRV_STR12", resKindCode);
                topSetBoxInfo.put("RSRV_STR13", retData.getString("RES_KIND_NAME", ""));
                topSetBoxInfo.put("RSRV_STR14", retData.getString("RES_STATE_CODE"));
                topSetBoxInfo.put("RSRV_STR15", retData.getString("RES_STATE_NAME"));
                topSetBoxInfo.put("RSRV_STR16", supplyId);
                topSetBoxInfo.put("RSRV_STR17", retData.getString("RES_FEE"));
                topSetBoxInfo.put("RSRV_STR20", topSetBoxInfo.getString("RSRV_STR20"));
                topSetBoxInfo.put("TRADE_ID", tradeId);
                topSetBoxInfo.put("RSRV_VALUE_CODE", "TOPSETBOX");
                //更新TF_B_TRADE_OTHER表
                Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TOPSETBOX_RESINFO", topSetBoxInfo, Route.getJourDb());
            }
            else
            {
                String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID查询不到魔百和业务受理信息!");
        }
        
        return retData;
    }
    
    /**
     * 魔百和开通处理处理
     * @param input
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IDataset sureCreateTopSetBox(IData input) throws Exception
    {
    	//setVisitTradeStaff(input);//宽带开户、魔百和开户，在施工环节回单后，业务办理工号变成施工人员的工号，影响店员积分、营业员量酬、渠道酬金
        IData result = new DataMap();
        
        String tradeId = input.getString("TRADE_ID","").trim();
       
        String serialNumber = input.getString("SERIAL_NUMBER","").trim();
        
        String reversion = input.getString("REVERSION","").trim();
        
        if (StringUtils.isEmpty(tradeId)) 
        {
            //TRADE_ID不能为空！
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"TRADE_ID不能为空！");
        }
        
        if (StringUtils.isEmpty(serialNumber)) 
        {
        	//SERIAL_NUMBER不能为空！
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"SERIAL_NUMBER不能为空！");
        }
       
        if (StringUtils.isEmpty(reversion)) 
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户回复信息不能为空!");
        }
        
        //宽带开户、魔百和开户，在施工环节回单后，业务办理工号变成施工人员的工号，影响店员积分、营业员量酬、渠道酬金
        IDataset mainTradeInfos = TradeInfoQry.queryUserTradeByBTradeAndBhTrade(tradeId);
        IData mainTradeInfo = mainTradeInfos.getData(0);
        String tradeStaffId = mainTradeInfo.getString("TRADE_STAFF_ID","");
        String tradeCityCode = mainTradeInfo.getString("TRADE_CITY_CODE","");
        String tradeDepartId = mainTradeInfo.getString("TRADE_DEPART_ID","");
        CSBizBean.getVisit().setStaffId(tradeStaffId);
        CSBizBean.getVisit().setCityCode(tradeCityCode);
        CSBizBean.getVisit().setDepartId(tradeDepartId);
        //end
        
        
        String tradeTypeCode = mainTradeInfo.getString("TRADE_TYPE_CODE");
        if("4900".equals(tradeTypeCode))    // 无手机魔百和开户
        {
        	//4900工单完工了，4800工单也生成了，魔百和已开通，不能再调出库接口，只能拆机。
        	IData tradeInfo = TradeInfoQry.queryTradeByTradeIdAndTradeType(tradeId,tradeTypeCode);
        	if(IDataUtil.isEmpty(tradeInfo))
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "魔百和已出库!");
        	}
        }else if ("680".equals(tradeTypeCode))   //无手机宽带开户
        {
        	// 无手机宽带是立即完工  ，所以需要查历史表  ； 
        	IDataset tradeInfo = TradeInfoQry.queryTradeBhByTradeIdAndTypeCode(tradeId,tradeTypeCode);
        	if(IDataUtil.isEmpty(tradeInfo))
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "无手机宽带订单未完工!");
        	}
        }
        
        IDataset topSetBoxInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "TOPSETBOX");   
        
        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            IData topSetBoxInfo = topSetBoxInfos.first();
            
            String rsrvTag1 =   topSetBoxInfo.getString("RSRV_TAG1");
            
            //如果不是初始状态，说明用户已经回复过了，不能再进行回复
            if (!"0".equals(rsrvTag1))
            { 
                CSAppException.appError("101008","您已经回复["+rsrvTag1+"],不能再次回复!");
            }
            
            
            //Y-为同意；其它不同意
            if ("Y".equals(reversion.trim().toUpperCase())) 
            {
                IData tradeInput = new DataMap();
                
                //拼装魔百和开户受理数据，用来标记是做开户
                tradeInput.put("INTERNET_TV_SOURCE", "OPEN_TOPSETBOX");       
                tradeInput.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                
                tradeInput.put("SERIAL_NUMBER", topSetBoxInfo.getString("RSRV_STR1"));
                
                //购机
                tradeInput.put("USER_ACTION", "0");
                tradeInput.put("PRODUCT_ID", topSetBoxInfo.getString("RSRV_STR3"));
                tradeInput.put("BASE_PACKAGES", topSetBoxInfo.getString("RSRV_STR4"));
                tradeInput.put("OPTION_PACKAGES", topSetBoxInfo.getString("RSRV_STR5"));
                tradeInput.put("Artificial_services", topSetBoxInfo.getString("RSRV_STR7"));
                
                tradeInput.put("RES_NO",topSetBoxInfo.getString("RSRV_STR8"));
                tradeInput.put("RES_TYPE_CODE", topSetBoxInfo.getString("RSRV_STR9")); // 4
                tradeInput.put("RES_BRAND_CODE", topSetBoxInfo.getString("RSRV_STR10"));
                tradeInput.put("RES_BRAND_NAME", topSetBoxInfo.getString("RSRV_STR11"));
                tradeInput.put("RES_KIND_CODE", topSetBoxInfo.getString("RSRV_STR12"));
                tradeInput.put("RES_KIND_NAME", topSetBoxInfo.getString("RSRV_STR13"));
                tradeInput.put("RES_STATE_CODE", topSetBoxInfo.getString("RSRV_STR14"));
                tradeInput.put("RES_STATE_NAME", topSetBoxInfo.getString("RSRV_STR15"));
                tradeInput.put("RES_SUPPLY_COOPID", topSetBoxInfo.getString("RSRV_STR16"));
                tradeInput.put("RES_FEE", Double.valueOf(topSetBoxInfo.getString("RSRV_STR17","0"))/100);
                tradeInput.put("IS_HAS_SALE_ACTIVE", topSetBoxInfo.getString("RSRV_TAG2"));
                
                //标记是融合宽带开户调用的，融合宽带开户调用不需要再次扣除押金
                tradeInput.put("IS_MERGE_WIDE_USER_CREATE", "1");
                
                tradeInput.put("WIDE_TRADE_ID", tradeId);
                
                tradeInput.put("WIDE_ADDRESS", topSetBoxInfo.getString("RSRV_STR21"));
                
                // 1-未完工
                tradeInput.put("WIDE_STATE", "1");
                
                //为了能够在同一笔业务中即存营业员信息用于3800业务的店员积分酬金等，又需要存施工人员信息，用于ExpandTopSetBoxTempOccuTime.java机顶盒预占时间的延长
                tradeInput.put("WORK_STAFF_ID",input.getString("TRADE_STAFF_ID", ""));
                tradeInput.put("WORK_DEPART_ID",input.getString("TRADE_DEPART_ID", ""));
                tradeInput.put("WORK_CITY_CODE", input.getString("TRADE_CITY_CODE", ""));
                
                //通过UU关系表查询宽带账户
            	IDataset tradeRelas = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(tradeId,"47");
            	String WideNetSerialNumber = tradeRelas.getData(0).getString("SERIAL_NUMBER_B");
                tradeInput.put("SERIAL_NUMBER_B", WideNetSerialNumber);
                
                tradeInput.put("TOP_SET_BOX_TIME", topSetBoxInfo.getString("RSRV_STR28")); //受理时长（月）
                tradeInput.put("TOP_SET_BOX_FEE", topSetBoxInfo.getString("RSRV_STR29")); //时长费用（元）
                tradeInput.put("TOP_SET_BOX_END_DATE", topSetBoxInfo.getString("RSRV_STR30")); //魔百和结束时间 
                
                IDataset dataset = CSAppCall.call("SS.NoPhoneTopSetBoxRegSVC.tradeReg", tradeInput);
                
                topSetBoxInfo.put("RSRV_TAG1", "Y");
                topSetBoxInfo.put("REMARK", "客户确认开通魔百和业务");
                String topBoxOrderId = dataset.getData(0).getString("ORDER_ID","");
                String topBoxTradeId = dataset.getData(0).getString("TRADE_ID","");
                topSetBoxInfo.put("RSRV_STR17", topBoxOrderId);  //魔百和开通订单ID
                topSetBoxInfo.put("RSRV_STR18", topBoxTradeId);  //魔百和开通台账ID
                
                Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_NOPHONE_TOPSETBOX_OPENTAG", topSetBoxInfo, Route.getJourDb());
                
                result.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));
                result.put("X_RESULTCODE", "0");
                result.put("X_RECORDNUM", "1");
                result.put("X_RESULTINFO", "OK");
            } 
            else
            {
                //用户确认取消魔百和后续退押金、返销营销活动等操作
                cancelTopSetBoxAction(tradeId);
                
                topSetBoxInfo.put("RSRV_TAG1", "N");
                topSetBoxInfo.put("REMARK", "客户确认不开通魔百和业务");
                topSetBoxInfo.put("RSRV_STR17", "");
                topSetBoxInfo.put("RSRV_STR18", "");
                //更新TF_B_TRADE_OTHER表
                int upNum = Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_NOPHONE_TOPSETBOX_OPENTAG", topSetBoxInfo, Route.getJourDb());
                
                result.put("X_RESULTCODE", "0");
                result.put("X_RECORDNUM", upNum);
                result.put("X_RESULTINFO", "OK");
            }
        }
        else
        {
            CSAppException.appError("101005","获取工单无数据");
        }
        
        return IDataUtil.idToIds(result);
    }
    
    /**
     * 重设交易工号信息
     * @param input
     * @throws Exception
     */
    public void setVisitTradeStaff(IData input) throws Exception
    {
    	//获取接口传入的交易信息
    	String tradeStaffId = input.getString("TRADE_STAFF_ID", "");
    	String tradeDepartId = input.getString("TRADE_DEPART_ID", "");
    	String tradeCityCode = input.getString("TRADE_CITY_CODE", "");
    	
    	if(tradeStaffId != null && !"".equals(tradeStaffId))
    		CSBizBean.getVisit().setStaffId(tradeStaffId);
    	if(tradeDepartId != null && !"".equals(tradeDepartId))
    		CSBizBean.getVisit().setDepartId(tradeDepartId);
    	if(tradeCityCode != null && !"".equals(tradeCityCode))
    		CSBizBean.getVisit().setCityCode(tradeCityCode);
    }
    
    /**
     * 用户确认取消魔百和后续退押金、返销营销活动等操作
     * @param mainTrade
     * @throws Exception
     * @author zhengkai5
     */
    public void cancelTopSetBoxAction(String tradeId) throws Exception
    {
        IDataset mainTrades =  TradeInfoQry.queryUserTradeByBTradeAndBhTrade(tradeId);
        boolean isTestFee = NoPhoneTradeUtil.checkWideUserFeeTest(tradeId);

        if (IDataUtil.isEmpty(mainTrades))
        {
            CSAppException.appError("-1", "主台账信息不存在！");
        }
        //有办理光猫调测费（438）或者魔百和调测费（439）时不允许在外线施工返销魔百和  add by guohuan
        if (isTestFee)
        {
            String msg = "外线施工环节不允许返销魔百和，请至营业厅办理";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }

        IData mainTrade = mainTrades.first();
        
        // 魔百和押金转账流水ID
        String depositFeeOutTradeId = mainTrade.getString("RSRV_STR4");
        
        //魔百和营销活动tradeID
        //String topSetBoxSaleActiveTradeId = mainTrade.getString("RSRV_STR6");

        //接口改造，当用户的宽带和魔百和一起办理的时候，不允许外线施工环节取消，只能到前台把宽带做撤单处理；即宽带开户开通魔百和业务不允许在外线施工时返销  add by guohuan
        String tradeType = mainTrade.getString("TRADE_TYPE_CODE");
        String rsrvStr6 = mainTrade.getString("RSRV_STR6");

        /**
         * 工单可能是宽带开户or魔百和开户
         * 宽带开户，    绑定魔百和号码存在预存字段RSRV_STR6
         * 魔百和开户，绑定魔百和号码存在预存字段RSRV_STR8
         * */
        String serialNumber = "4900".equals(mainTrade.getString("TRADE_TYPE_CODE")) ? mainTrade.getString("RSRV_STR8") : rsrvStr6;

        IDataset topSetBoxInfos = TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(tradeId, "TOPSETBOX");
        
        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            if (StringUtils.equals("680",tradeType)&&StringUtils.isNotBlank(rsrvStr6))
            {
                String msg = "宽带开户时开通魔百和业务不允许在外线施工时单独返销魔百和";
                CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
            }

            IData topSetBoxInfo = topSetBoxInfos.getData(0);

            String topSetBoxDeposit = topSetBoxInfo.getString("RSRV_STR6","");
            String boxModeFee = topSetBoxInfo.getString("RSRV_STR25","");//调测费优惠
            
            if (StringUtils.isNotBlank(topSetBoxDeposit))
            {
                int topSetBoxDepositFee = Integer.valueOf(topSetBoxDeposit);
                
                if (topSetBoxDepositFee > 0)
                {
                	//调测费用户没有押金所以不进行押金清退
                	if(!"84073843".equals(boxModeFee)){
                		//客户确认不开通魔百和,调用账务的接口进行押金清退
                        AcctCall.backFee(mainTrade.getString("USER_ID"), tradeId, "15000", "9016", "16001", topSetBoxDeposit);
                        //AcctCall.transFeeOutADSL(param);
                    }
                   

                }
            }
            
            String topSetFee = topSetBoxInfo.getString("RSRV_STR29","");
            
            if (StringUtils.isNotBlank(topSetFee))
            {
            	int topSetBoxDepositFee = Integer.valueOf(topSetFee);
            	
            	if (topSetBoxDepositFee > 0)
            	{
            		//客户确认不开通魔百和,调用账务的接口进行时长费用清退
            		AcctCall.backFee(mainTrade.getString("USER_ID"), tradeId, "15000", "9082", "16001", topSetFee);
            		//AcctCall.transFeeOutADSL(param);
            		
            	}
            }
            //147销户
         /*   IData input = new DataMap();
            input.put("SERIAL_NUMBER", serialNumber);
	    	input.put("TRADE_TYPE_CODE", "192");
	    	input.put("ROUTE_EPARCHY_CODE", "0898");
			CSAppCall.call( "SS.DestroyUserNowRegSVC.tradeReg", input);*/
            
            //截止147号码关系时间
			IData param = new DataMap();
			
		    //update条件
		    param.put("SERIAL_NUMBER_A", serialNumber);
		    param.put("RELATION_TYPE_CODE", "47");
		    param.put("REMARK", "取消魔百和，终止关系！");	
				  
		     //System.out.println("-----UndoTDRelationEndFinishAction-----param:"+param);
		    Dao.executeUpdateByCodeCode("TF_F_RELATION_UU", "UPD_END_DATE_BY_SNA", param);
		    
		    
		    
		    //返销号码资源和sim卡资源
		    IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, BofConst.MODIFY_TAG_ADD);

	        if (tradeResInfos.isEmpty())
	        {
	            return;
	        }
	        DataHelper.sort(tradeResInfos, "RES_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
	        for (int i = 0; i < tradeResInfos.size(); i++)
	        {
	            String strResTypeCode = tradeResInfos.getData(i).getString("RES_TYPE_CODE");
	            String strResCode = tradeResInfos.getData(i).getString("RES_CODE");
	            String rsrvStr5 = tradeResInfos.getData(i).getString("RSRV_STR5");
	            // 号码占用
	            if ("01".equals(rsrvStr5) && "0".equals(strResTypeCode))
	            {
	                ResCall.undoResPossessForIOTMphone(strResCode);
	            }
	            else if (!"01".equals(rsrvStr5) && "0".equals(strResTypeCode))
	            {
	                ResCall.undoResPossessForMphone(strResCode);
	            }
	            else if ("01".equals(rsrvStr5) && "1".equals(strResTypeCode))
	            {
	                ResCall.undoResPossessForIOTSim(strResCode);
	            }
	            else if (!"01".equals(rsrvStr5) && "1".equals(strResTypeCode))
	            {
	                ResCall.undoResPossessForSim(strResCode,"10");
	            }
	        }
        }
    }

    /**
     * 魔百和营销活动返销，如果已完工，则返销，如未完工，则搬到历史表中
     * @param tradeId
     * @throws Exception
     */
    private void cancelSaleActiveTrade(String wideTradeId, String topSetBoxSaleActiveTradeId,String serialNumber) throws Exception
    {
        if(topSetBoxSaleActiveTradeId != null && !"".equals(topSetBoxSaleActiveTradeId))
        {
            //查询是否已完工
            IData tradeInfos =  UTradeInfoQry.qryTradeByTradeId(topSetBoxSaleActiveTradeId,"0");
            if(IDataUtil.isEmpty(tradeInfos))
            {
                IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(topSetBoxSaleActiveTradeId, "0", null);
                if(IDataUtil.isNotEmpty(hisTradeInfos))
                {
                    IData pdData = new DataMap();
                    pdData.put("REMARKS", "宽带完工客户确认不开通魔百和，营销活动自动返销");
                    pdData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
                    pdData.put("TRADE_ID", topSetBoxSaleActiveTradeId);
                    pdData.put("IS_CHECKRULE", false);
                    pdData.put(Route.ROUTE_EPARCHY_CODE,hisTradeInfos.getString("TRADE_EPARCHY_CODE"));
                    IDataset resultDataset = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
                    
                    //撤销成功，需要更新营销活动预受理表
                    if (IDataUtil.isNotEmpty(resultDataset))
                    {
                        UserSaleActiveInfoQry.updateSaleActiveBookToCancel(wideTradeId, topSetBoxSaleActiveTradeId, serialNumber);
                    }
                }
            }
            else
            {
                //未完工，将工单移到历史表
                dealUnfinishTrade(getPublicData(tradeInfos),tradeInfos);
            }
        }
    }
    
    private IData getPublicData(IData tradeInfo) throws Exception 
    {
        IData pubData = new DataMap();
        pubData.put("ORDER_ID", tradeInfo.getString("ORDER_ID", ""));
        pubData.put("TRADE_ID", tradeInfo.getString("TRADE_ID", ""));
        pubData.put("NEW_ORDER_ID", SeqMgr.getOrderId());
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }
    
    private void dealUnfinishTrade(IData pubData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        tradeData.putAll(tradeInfo);
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("SUBSCRIBE_STATE", "A");
        tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("REMARK","宽带完工客户确认不开通魔百和，未完工营销活动搬迁到历史表");
        
        if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb()))
        {
            String msg = "宽带完工客户确认不开通魔百和，营销活动订单【" + tradeInfo.getString("TRADE_ID") + "】搬迁至历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        
        if (!Dao.delete("TF_B_TRADE", tradeInfo, Route.getJourDb()))
        {
            String msg = "宽带完工客户确认不开通魔百和，营销活动删除订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        
        //费用返销
        IData pageData = new DataMap();
        pageData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
        TradeCancelFee.cancelRecvFee(tradeInfo,pageData);
    }
    
    /**
     * 魔百和自动化任务：
     * 	魔百和到期后自动停机任务；
     * */
    public void TopSetBoxAutoStop(IData input) throws Exception
    {
    	
    			String userId = input.getString("USER_ID");  //无手机魔百和userId
    			IDataset boxInfos =  UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
    	    	if(IDataUtil.isNotEmpty(boxInfos))
    	    	{
    				IData boxInfo = boxInfos.first();
    				String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
    				if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
    					String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
    					if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
    						String basePlatSvcId=basePlatSvcIdArr[0];
    						if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
    							
    							IDataset userBaseServices=UserPlatSvcInfoQry.
    									queryUserPlatSvcByUserIdAndServiceId(userId, basePlatSvcId);
    							if(IDataUtil.isNotEmpty(userBaseServices)){
    								IData PlatSvcTradeData=userBaseServices.getData(0);
    								
    								String topsetboxTime = PlatSvcTradeData.getString("RSRV_STR4");
    								String startDate = PlatSvcTradeData.getString("RSRV_STR6");
    								String startDate2 = Converter(startDate);
    								//获取偏移n个月后的最后一天
    			    				String endDate = SysDateMgr.getAddMonthsLastDayNoEnv(Integer.parseInt(topsetboxTime)+1,startDate2);
    			    				int dateInteval = SysDateMgr.daysBetween(endDate , SysDateMgr.getSysDate() );
    			    				//当前日期和结束日期的比较： 如果当前日期大于结束日期，受理停机业务
    			    				if(dateInteval > 0)
    			    				{
    			    					//受理无手机魔百和停机业务
    			    					IData tradeInput = new DataMap();
    			    					tradeInput.put("USER_ID", userId);
    			    					tradeInput.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
    			    					tradeInput.put("STOP_ACTION", "1");  //	用户报停标志； 0：主动报停 ，1：欠费报停
    			    					tradeInput.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    			    					try {
    										CSAppCall.call("SS.NoPhoneStopTopSetBoxRegSVC.tradeReg",tradeInput);
    									} catch (Exception e) {
    										logger.error(input.getString("SERIAL_NUMBER")+"自动停机出错！ "+e.toString());
    									}
    			    				}
    							}
    						}
    					}
    				}
    			}
    }

	/**
	 * @Description：日期格式转化
	 * @param:@param startDate
	 * @return void
	 * @throws ParseException 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-9-26下午04:44:07
	 */
	private String Converter(String startDate) throws ParseException {
		SimpleDateFormat tDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = tDateFormat.parse(startDate);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	
	   // 外线处理：无手机魔百和拆机接口
    public IData topSetBoxBackSell(IData input) throws Exception {
        String tradeId = IDataUtil.chkParam(input, "TRADE_ID");
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //有办理光猫调测费（438）或者魔百和调测费（439）时不允许在外线施工返销魔百和   add by guohuan
        boolean isTestFee = NoPhoneTradeUtil.checkWideUserFeeTest(tradeId);
        if (isTestFee)
        {
            String msg = "外线施工环节不允许返销魔百和，请至营业厅办理";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }

        IData mainTradeInfo = null;
        IDataset mainTradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isEmpty(mainTradeInfos)) {
            mainTradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", this.getTradeEparchyCode());
        } else {
            mainTradeInfo = mainTradeInfos.getData(0);
        }

        String tradeStaffId = mainTradeInfo.getString("TRADE_STAFF_ID","");
        String tradeCityCode = mainTradeInfo.getString("TRADE_CITY_CODE","");
        String tradeDepartId = mainTradeInfo.getString("TRADE_DEPART_ID","");
        CSBizBean.getVisit().setStaffId(tradeStaffId);
        CSBizBean.getVisit().setCityCode(tradeCityCode);
        CSBizBean.getVisit().setDepartId(tradeDepartId);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        IDataset boxInfo = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
        if (IDataUtil.isEmpty(boxInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有有效的魔百和信息，无法办理该业务！");
        }
        
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("IS_RETURN_TOPSETBOX", "1");
        params.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset results = CSAppCall.call("SS.NoPhoneTopSetBoxDestroyRegSVC.tradeReg", params);
        
        IData result = results.first();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");
        
        return result;
    }
    
    /**
     * @Description：校验是否为机顶盒
     * @param:@param resNo
     * @param:@param value
     * @return void
     * @throws Exception 
     * @throws
     * @Author :tanzheng
     * @date :2018-5-31上午10:05:13
     */
    private void checkIsTopset(String resNo, String resType, String type) throws Exception {
        IDataset retDataset = HwTerminalCall.checkIsResRightType(resNo,resType);
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
             if(StringUtils.equals(retDataset.first().getString("retVal"), "0")){
                 if(type.equals("JDH")){
                     CSAppException.apperr(CrmCommException.CRM_COMM_103, "串号不是机顶盒串号，请确认！"); // 接口返回异常
                 }else if(type.equals("IPTV")){
                     CSAppException.apperr(CrmCommException.CRM_COMM_103, "串号不是魔百和IPTV串号，请确认！"); // 接口返回异常
                 }
             }
        }
        
    }
}
