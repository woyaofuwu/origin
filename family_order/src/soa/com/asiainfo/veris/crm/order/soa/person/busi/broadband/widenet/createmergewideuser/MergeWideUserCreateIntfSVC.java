package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.ResTypeEnum;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzy;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

public class MergeWideUserCreateIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 魔百和终端校验，并更新魔百和受理信息临时表(提供给PBOSS校验)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkTopSetBoxTerminal(IData input) throws Exception
    {
    	//
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

        //在录入串号时，系统自动前后去掉空格
        resNo = resNo.trim();

        //判断是否是4800魔百和开户
        IData trade4800Info = TradeInfoQry.queryTradeByTradeIdAndTradeType(tradeId, "4800");

        IDataset topSetBoxInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "TOPSETBOX");
        String IPTV = "";
        //如果是魔百和开户工单，则取出工单内手机号码，判断用户表是否存在有效的宽带用户
        if(IDataUtil.isNotEmpty(trade4800Info))
        {
        	String serialNumber = trade4800Info.getString("SERIAL_NUMBER","").replace("KD_", "");
        	boolean isWidenetUser = BreQry.isWideNetUser(serialNumber);//判断是否为宽带完工用户
        	if (!isWidenetUser)//如果不是有效的宽带完工用户
            {
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "由于宽带还未完工，暂时无法办理!");
            }
        	IPTV = trade4800Info.getString("RSRV_STR10", "");
        }else if(IDataUtil.isNotEmpty(topSetBoxInfos)){//如果是宽带开户，查看是否是IPTV
        	IData topSetBoxInfo = topSetBoxInfos.first();
        	if(null != topSetBoxInfo){
        		IPTV = topSetBoxInfo.getString("RSRV_STR24", "");
        	}
        }
        //判断结束

        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            IData topSetBoxInfo = topSetBoxInfos.first();

            //扩展字段1存放的手机号码
            String serialNumber = topSetBoxInfo.getString("RSRV_STR1");
            if(!"".equals(IPTV) && "IPTV_OPEN".equals(IPTV)){
            	this.checkIsTopset(resNo,ResTypeEnum.IPTV.getValue(),"IPTV");//取4800工单表的预留字段10或者600other表的预留字段24,判断IPTV串号-wangsc10-20181127
            }else{
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
                //edit by zhangxing3 for 度假宽带用户魔百和费用问题:魔百和押金应该从tf_b_trade_other表的RSRV_STR6取值，而不应该通过查询资源表获取。
                //retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
                retData.put("RES_FEE", Double.parseDouble(topSetBoxInfo.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
                //edit by zhangxing3 for 度假宽带用户魔百和费用问题:魔百和押金应该从tf_b_trade_other表的RSRV_STR6取值，而不应该通过查询资源表获取。


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
                topSetBoxInfo.put("RSRV_STR24", IPTV);
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
     * @author yuyj3
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
            // 手机号码不能为空
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"服务号码不能为空！");
        }
        if (StringUtils.isEmpty(reversion))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户回复信息不能为空!");
        }

        //宽带开户、魔百和开户，在施工环节回单后，业务办理工号变成施工人员的工号，影响店员积分、营业员量酬、渠道酬金
        IDataset mainTradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
        IData mainTradeInfo = mainTradeInfos.getData(0);
        String tradeStaffId = mainTradeInfo.getString("TRADE_STAFF_ID","");
        String tradeCityCode = mainTradeInfo.getString("TRADE_CITY_CODE","");
        String tradeDepartId = mainTradeInfo.getString("TRADE_DEPART_ID","");
        CSBizBean.getVisit().setStaffId(tradeStaffId);
        CSBizBean.getVisit().setCityCode(tradeCityCode);
        CSBizBean.getVisit().setDepartId(tradeDepartId);
        //end

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
                tradeInput.put("RES_FEE", Integer.valueOf(topSetBoxInfo.getString("RSRV_STR17","0"))/100);
                tradeInput.put("IS_HAS_SALE_ACTIVE", topSetBoxInfo.getString("RSRV_TAG2"));

                //----------家庭魔百和新开增加familyMemberInstId add by zhangxi start------------
                tradeInput.put("FAMILY_MEMBER_INST_ID", topSetBoxInfo.getString("RSRV_STR23"));
                //----------家庭魔百和新开增加familyMemberInstId add by zhangxi end------------

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

                if("600".equals(mainTradeInfo.getString("TRADE_TYPE_CODE", "")))
                {
                	IDataset wideCode = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
                	if(IDataUtil.isNotEmpty(wideCode))
                	{
                		tradeInput.put("IPTV_CITYCODE", wideCode.getData(0).getString("RSRV_STR4", ""));
                	}

                }

                IDataset dataset = CSAppCall.call("SS.TopSetBoxSVC.tradeReg", tradeInput);

                topSetBoxInfo.put("RSRV_TAG1", "Y");
                topSetBoxInfo.put("REMARK", "客户确认开通魔百和业务");
                String topBoxOrderId = dataset.getData(0).getString("ORDER_ID","");
                String topBoxTradeId = dataset.getData(0).getString("TRADE_ID","");
                topSetBoxInfo.put("RSRV_STR17", topBoxOrderId);  //魔百和开通订单ID
                topSetBoxInfo.put("RSRV_STR18", topBoxTradeId);  //魔百和开通台账ID

                Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TOPSETBOX_OPENTAG", topSetBoxInfo, Route.getJourDb());

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
                int upNum = Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TOPSETBOX_OPENTAG", topSetBoxInfo, Route.getJourDb());

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
     * @author yuyj3
     */
    public void cancelTopSetBoxAction(String tradeId) throws Exception
    {
        IData mainTrade =  UTradeInfoQry.qryTradeByTradeId(tradeId,"0");

        if (IDataUtil.isEmpty(mainTrade))
        {
            CSAppException.appError("-1", "主台账信息不存在！");
        }

        //光猫押金转账流水ID
        String depositFeeOutTradeId = mainTrade.getString("RSRV_STR4");

        //魔百和营销活动tradeID
        String topSetBoxSaleActiveTradeId = mainTrade.getString("RSRV_STR6","");

        String serialNumber = mainTrade.getString("SERIAL_NUMBER").indexOf("KD_")!= -1 ? mainTrade.getString("SERIAL_NUMBER").substring(3):mainTrade.getString("SERIAL_NUMBER");

        IDataset topSetBoxInfos = TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(tradeId, "TOPSETBOX");

        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            IData topSetBoxInfo = topSetBoxInfos.getData(0);

            String topSetBoxDeposit = topSetBoxInfo.getString("RSRV_STR6","");

            if (StringUtils.isNotBlank(topSetBoxDeposit))
            {
                int topSetBoxDepositFee = Integer.valueOf(topSetBoxDeposit);

                if (topSetBoxDepositFee > 0)
                {
                    //客户确认不开通魔百和,调用账务的接口进行押金返回
                    IData param = new DataMap();
                    param.put("OUTER_TRADE_ID", depositFeeOutTradeId);
                    param.put("SERIAL_NUMBER", serialNumber);
                    param.put("TRADE_FEE", topSetBoxDeposit);
                    param.put("CHANNEL_ID", "15000");
                    param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
                    param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

                    AcctCall.transFeeOutADSL(param);

                }
                //BUS201907310012关于开发家庭终端调测费的需求
        	    String topSetBoxSaleActiveTradeId2 = "" ;
        	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId))
                {
	        	    if(topSetBoxSaleActiveTradeId.contains("|"))
	        	    {
	        	    	String []strTradeIds = topSetBoxSaleActiveTradeId.split("\\|");
	        	    	topSetBoxSaleActiveTradeId = strTradeIds[0];
	        	    	topSetBoxSaleActiveTradeId2 = strTradeIds[1];
	        	    }
                }
        	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId2))
        	    {
        	    	//客户确认不开通魔百和，需要对营销活动返销处理
                    cancelSaleActiveTrade(tradeId, topSetBoxSaleActiveTradeId2, serialNumber);
        	    }
        	    //BUS201907310012关于开发家庭终端调测费的需求
                if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId))
                {
                    //客户确认不开通魔百和，需要对营销活动返销处理
                    cancelSaleActiveTrade(tradeId, topSetBoxSaleActiveTradeId, serialNumber);
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
                    pdData.put("REMARKS", "宽带完工客户确认不开通该业务，营销活动自动返销");
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
     *  根据宽带产品ID查询宽带营销活动(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset querySaleActivesByProductIdIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "PRODUCT_ID");

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        String productId = input.getString("PRODUCT_ID");

        //BUS201907310012关于开发家庭终端调测费的需求
        String queryType = input.getString("QUERY_TYPE","");
        if("FTTH".equals(queryType))
        {
        	IDataset retrunSaleActiveList =  new DatasetList();

	        IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "600", "FTTH");

	        saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);
	        if (IDataUtil.isNotEmpty(saleActiveList))
	        {
	            IData saleActive = null;
	            IData returnSaleActive = null;
	            IData inData = null;
	            IData saleActiveFeeData = null;

	            for (int i = 0; i < saleActiveList.size(); i++)
	            {
	                saleActive = saleActiveList.getData(i);

	                returnSaleActive = new DataMap();
	                inData = new DataMap();

	                returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
	                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
	                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));

	                inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
	                //活动标记，1：宽带营销活动，2：魔百和营销活动;
	                inData.put("ACTIVE_FLAG", "1");
	                //营销活动产品ID
	                inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
	                //营销活动包ID
	                inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
	                //获得营销活动预存费用
	                saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

	                if (IDataUtil.isNotEmpty(saleActiveFeeData))
	                {
	                    returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
	                }

	                retrunSaleActiveList.add(returnSaleActive);
	            }
	        }
	        return retrunSaleActiveList;
        }
        //BUS201907310012关于开发家庭终端调测费的需求
        else
        {
	        IDataset retrunSaleActiveList =  new DatasetList();

	        IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "600", productId);

	        saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);
	        if (IDataUtil.isNotEmpty(saleActiveList))
	        {
	            IData saleActive = null;
	            IData returnSaleActive = null;
	            IData inData = null;
	            IData saleActiveFeeData = null;

	            for (int i = 0; i < saleActiveList.size(); i++)
	            {
	                saleActive = saleActiveList.getData(i);

	                returnSaleActive = new DataMap();
	                inData = new DataMap();

	                returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
	                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
	                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));

	                inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
	                //活动标记，1：宽带营销活动，2：魔百和营销活动
	                inData.put("ACTIVE_FLAG", "1");
	                //营销活动产品ID
	                inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
	                //营销活动包ID
	                inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
	                //获得营销活动预存费用
	                saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

	                if (IDataUtil.isNotEmpty(saleActiveFeeData))
	                {
	                    returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
	                }

	                retrunSaleActiveList.add(returnSaleActive);
	            }
	        }
	        return retrunSaleActiveList;
        }

    }


    /**
     * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset queryTopSetBoxDiscntPackagesIntf(IData input) throws Exception
    {
        IDataset retDataset = new DatasetList();

        //校验魔百和产品ID是否传入
        IDataUtil.chkParam(input, "TOP_SET_BOX_PRODUCT_ID");

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //新大陆不支持解析 IData里面放多个IDataset 所以需要分多次查询 。查询内容 1：魔百和必选包 2：魔百和可选包 3：魔百和营销活动 4魔百和押金
        IDataUtil.chkParam(input, "QUERY_TYPE");

        String queryType = input.getString("QUERY_TYPE");

        input.put("serialNumber", input.getString("SERIAL_NUMBER"));

        IData retData = new MergeWideUserCreateSVC().queryTopSetBoxDiscntPackagesByPID(input);


        if ("1".equals(queryType))
        {
            retDataset = retData.getDataset("B_P");
        }
        else if ("2".equals(queryType))
        {
            retDataset = retData.getDataset("O_P");
        }
        else if ("3".equals(queryType))
        {
            IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");

            if (IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
            {
                IData saleActive = null;
                IData returnSaleActive = null;
                IData inData = null;
                IData saleActiveFeeData = null;

                for (int i = 0; i < topSetBoxSaleActiveList.size(); i++)
                {
                    saleActive = topSetBoxSaleActiveList.getData(i);

                    returnSaleActive = new DataMap();
                    inData = new DataMap();

                    returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
                    returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                    returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));


                    inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                    //活动标记，1：宽带营销活动，2：魔百和营销活动
                    inData.put("ACTIVE_FLAG", "2");
                    //营销活动产品ID
                    inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                    //营销活动包ID
                    inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                    //获得营销活动预存费用
                    saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

                    if (IDataUtil.isNotEmpty(saleActiveFeeData))
                    {
                        returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                    }

                    retDataset.add(returnSaleActive);
                }

            }
        }
        //BUS201907310012关于开发家庭终端调测费的需求
        else if ("5".equals(queryType))
        {
            IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST2");

            if (IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
            {
                IData saleActive = null;
                IData returnSaleActive = null;
                IData inData = null;
                IData saleActiveFeeData = null;

                for (int i = 0; i < topSetBoxSaleActiveList.size(); i++)
                {
                    saleActive = topSetBoxSaleActiveList.getData(i);

                    returnSaleActive = new DataMap();
                    inData = new DataMap();

                    returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
                    returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                    returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));


                    inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                    //活动标记，1：宽带营销活动，2：魔百和营销活动;
                    inData.put("ACTIVE_FLAG", "2");
                    //营销活动产品ID
                    inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                    //营销活动包ID
                    inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                    //获得营销活动预存费用
                    saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

                    if (IDataUtil.isNotEmpty(saleActiveFeeData))
                    {
                        returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                    }

                    retDataset.add(returnSaleActive);
                }

            }
        }
        //BUS201907310012关于开发家庭终端调测费的需求
        else
        {
            IData dataTemp =  new DataMap();

            dataTemp.put("TOP_SET_BOX_DEPOSIT", retData.getString("TOP_SET_BOX_DEPOSIT", "0"));

            retDataset = IDataUtil.idToIds(dataTemp);
        }

        return retDataset;
    }

    /**
     * 校验魔百盒IPTV受理校验接口(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author wangsc10
     */
    public IData checkIPTVProductIdIntf(IData input) throws Exception
    {
        IData retData = new DataMap();

        //校验魔百盒产品编码是否传入
        IDataUtil.chkParam(input, "TOP_SET_BOX_PRODUCT_ID");

        //校验办理宽带的手机号码
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        IDataset isIPTV=CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", input.getString("TOP_SET_BOX_PRODUCT_ID",""), "0898");
        if(IDataUtil.isNotEmpty(isIPTV))
        {
        	String wideProductType = input.getString("WIDE_PRODUCT_TYPE","");//宽带产品类型

            //家庭宽带开户、宽带开户
            if(wideProductType != null && !"".equals(wideProductType)){
            	if(!"FTTH".equals(wideProductType)){
            		CSAppException.appError("-1", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
            	}
            	String wideProductId = input.getString("WIDE_PRODUCT_ID","");//宽带产品档次
    			if(null != wideProductId && !"".equals(wideProductId)){
    				IDataset ftthkddc=CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", wideProductId, "0898");//查询宽带FTTH档次是不是50M以下
                    if(IDataUtil.isNotEmpty(ftthkddc)){
    					CSAppException.appError("-1", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
                    }
    			}
            }else{
            	//魔百和开户
            	//查询用户宽带类型
            	String kdSerialNumber = "KD_"+input.getString("SERIAL_NUMBER","");
                IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(kdSerialNumber).first();
            	if(null != wideNetInfo && IDataUtil.isNotEmpty(wideNetInfo)){
    				String rsrvstr2 = wideNetInfo.getString("RSRV_STR2", "");//宽带类型
    				if (StringUtils.equals("1", rsrvstr2) || StringUtils.equals("6", rsrvstr2))
                    {
    					CSAppException.appError("-1", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
                    }
    				String kdUserId = wideNetInfo.getString("USER_ID", "");
    				IData kdProduct = ProductInfoQry.getUserProductByUserIdForGrp(kdUserId).first();
    				if(null != kdProduct && IDataUtil.isNotEmpty(kdProduct)){
    					String wideProductId = kdProduct.getString("PRODUCT_ID", "");//宽带产品档次
	    				if(null != wideProductId && !"".equals(wideProductId)){
	    					IDataset ftthkddc=CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", wideProductId, "0898");//查询宽带FTTH档次是不是50M以下
	    	                if(IDataUtil.isNotEmpty(ftthkddc)){
	    						CSAppException.appError("-1", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
	    	                }
	    				}
    				}else{
    					CSAppException.appError("-1", "该用户宽带产品信息不存在！");
    				}
    			}else{
    				CSAppException.appError("-1", "该用户宽带资料信息不存在！");
    			}
            }
        }
        retData.put("RESULT_CODE", "0");

        return retData;
    }

    /**
     * 获得光猫费用，并校验用户是否有有效的光猫记录、现金余额是否足够(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData getModemDepositInft(IData input) throws Exception
    {
        IData retData = null;

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        input.put("saleActiveId", input.getString("SALE_ACTIVE_ID"));
        input.put("serialNumber", input.getString("SERIAL_NUMBER"));

        retData = new MergeWideUserCreateSVC().checkModemDeposit(input);

        //转换为分
        retData.put("MODEM_DEPOSIT", Integer.valueOf(retData.getString("MODEM_DEPOSIT"))*100);

        return retData;
    }


    /**
     * 宽带营销活动办理校验(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSaleActiveInft(IData input) throws Exception
    {
        IData retData = new DataMap();

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //校验优惠ID列表是否传入
        IDataUtil.chkParam(input, "DISCNT_IDS");

        //校验服务ID列表是否传入
        IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");

        //校验宽带营销活动ID是否传入
        IDataUtil.chkParam(input, "SALE_ACTIVE_ID");

        //校验宽带产品ID是否传入
        IDataUtil.chkParam(input, "WIDE_PRODUCT_ID");

        //标记是宽带开户营销活动
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

        String saleActiveId = input.getString("SALE_ACTIVE_ID");
        String wideProductId = input.getString("WIDE_PRODUCT_ID");


        IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", wideProductId, saleActiveId, "0898");

        if (IDataUtil.isNotEmpty(saleActiveIDataset))
        {
            IData saleActiveData = saleActiveIDataset.first();

            input.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
            input.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
        }
        else
        {
            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
        }

        String packageId = input.getString("PACKAGE_ID","");
        //add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"
        if("84015243".equals(packageId) || "84015244".equals(packageId))
        {
        	checkSaleActiveStock(packageId);
        }
        //add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"


        // 预受理校验，不写台账
        input.put("PRE_TYPE",  BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        retData = new MergeWideUserCreateSVC().checkSelectedDiscnts(input);

        if ("-1".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160001", "用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！");
        }

        if ("-2".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160002", "用户已经选择了VIP体验套餐，不能再办理宽带营销活动，如果要办理请先取消VIP体验套餐！");
        }

        retData.put("RESULT_CODE", retData.getString("resultCode"));

        return retData;
    }

    /**
     * 校验营销包库存
     * zhangxing3
     */
    public void checkSaleActiveStock(String packageId)throws Exception
    {
    	IDataset result = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }
        IData pkgExtInfo = result.getData(0);
        String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");

        if (StringUtils.isBlank(condFactor3))
        {
            return;
        }

        if ("ZZZZ".equals(condFactor3))
        {

        }
        else
        {
            result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_43);
            }
            IData activeStockInfo = result.getData(0);

            int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
            int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
            if (warnningValueU >= warnningValueD)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_44);
            }
        }
    }

    /**
     * 魔百和营销活动校验接口(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkTopSetBoxSaleActiveInft(IData input) throws Exception
    {
        IData retData = new DataMap();

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "TOP_SET_BOX_SALE_ACTIVE_ID");

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "WIDE_PRODUCT_ID");


        String saleActiveId = input.getString("SALE_ACTIVE_ID","");
        String wideProductId = input.getString("WIDE_PRODUCT_ID","");
        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID","");

        if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
        {
            // add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费（即度假宽带月、季、半年套餐）
            // 度假宽带活动与魔百和营销活动互斥
            if ("113245".equals(saleActiveId) || "113257".equals(saleActiveId) || "113254".equals(saleActiveId))
            {
            	CSAppException.appError("-1", "您已经选择了度假宽带活动，不能再办理该魔百和营销活动！");
            }
            // add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费（即度假宽带月、季、半年套餐）

        	IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");

            if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
            {
                IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();

                //所依赖的宽带1+营销活动虚拟ID
                String paraCode25 = topSetBoxCommparaInfo.getString("PARA_CODE25");

                String paraCode23 = topSetBoxCommparaInfo.getString("PARA_CODE23");

                boolean flag = false;
                boolean flag23 = false;
                if (StringUtils.isNotBlank(paraCode23))
                {
                    String paraCode23Array [] = paraCode23.split("\\|");
                    for(int i = 0 ; i < paraCode23Array.length ; i++ )
                    {
                        if (wideProductId.equals(paraCode23Array[i]))
                        {
                            flag23 = true;
                            break;
                        }
                    }

                    if (!flag23)
                    {
                        CSAppException.appError("-1", "该宽带产品不允许办理该营销活动。");
                    }
                }

                //不为空，则需要校验
                if (StringUtils.isNotBlank(paraCode25))
                {
                    if (StringUtils.isNotBlank(saleActiveId))
                    {
                        String paraCode25Array [] = paraCode25.split("\\|");

                        //判断是否选中了依赖的1+营销活动
                        for(int i = 0 ; i < paraCode25Array.length ; i++ )
                        {
                            if (saleActiveId.equals(paraCode25Array[i]))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag)
                        {
                            CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                        }
                    }
                    else
                    {
                        CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                    }
                }

                input.put("PRODUCT_ID",topSetBoxCommparaInfo.getString("PARA_CODE4"));
                input.put("PACKAGE_ID", topSetBoxCommparaInfo.getString("PARA_CODE5"));
            }else
            {
                CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
            }
        }


        //标记是宽带开户营销活动
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

        // 预受理校验，不写台账
        input.put("PRE_TYPE",  BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);


        retData.put("RESULT_CODE", "0");

        return retData;
    }



    /**
     * 提交前费用校验(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkFeeBeforeSubmitInft(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //光猫押金
        IDataUtil.chkParam(input, "MODEM_DEPOSIT");

        //魔百和押金
        IDataUtil.chkParam(input, "TOPSETBOX_DEPOSIT");

        //宽带1+营销活动费用
        IDataUtil.chkParam(input, "SALE_ACTIVE_FEE");

        //魔百和营销活动费用
        IDataUtil.chkParam(input, "TOPSETBOX_SALE_ACTIVE_FEE");

        //新大陆传过来是以分为单位  但 校验接口以元为单位会*100，所以传过去是先除100
        input.put("MODEM_DEPOSIT", Integer.parseInt(input.getString("MODEM_DEPOSIT","0"))/100);
        input.put("TOPSETBOX_DEPOSIT", Integer.parseInt(input.getString("TOPSETBOX_DEPOSIT","0"))/100);

        return new MergeWideUserCreateSVC().checkFeeBeforeSubmit(input);
    }

    /**
     * 根据宽带类型查询宽带产品(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getWidenetProductInfoIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "WIDE_TYPE");
        IDataUtil.chkParam(input, "TRADE_STAFF_ID");
        String staffId = input.getString("TRADE_STAFF_ID");

        String wideType =  input.getString("WIDE_TYPE");
        String wideProductType = "";

        if ("FTTH".equals(wideType))
        {
            wideProductType = "3";
        }
        else if ("TTFTTH".equals(wideType))
        {
            wideProductType = "5";
        }
        else if ("GPON".equals(wideType))
        {
            wideProductType = "1";
        }
        else if ("TTFTTB".equals(wideType))
        {
            wideProductType = "6";
        }
        else if ("TTADSL".equals(wideType))
        {
            wideProductType = "2";
        }

        input.put("wideProductType", wideProductType);

        IDataset productDataset = new MergeWideUserCreateSVC().getWidenetProductInfoByWideType(input);

        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(staffId, productDataset);

        return productDataset;
    }



    /**
     * 查询魔百和产品(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getTopSetBoxProductInfoIntf(IData input) throws Exception
    {
        IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");

        return topSetBoxProducts;
    }


    /**
     * 宽带鉴权后校验接口(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkBeforeTradeIntf(IData input) throws Exception
    {
        IData resultData = new DataMap();

        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        String sn = input.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(sn);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的用户信息！");
        }

        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));

        if (IDataUtil.isEmpty(customerInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
        }


        input.putAll(userInfo);
        input.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        input.put("TRADE_TYPE_CODE", "600");
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));

        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");

        IDataset infos = CSAppCall.call( "CS.CheckTradeSVC.checkBeforeTrade", input);

        CSAppException.breerr(infos.getData(0));

        resultData.put("RESULT_CODE", "0");

        return resultData;
    }


    /**
     * 宽带产品元素查询接口(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset qryWidenetProductElementsIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "TRADE_STAFF_ID");

        String staffId = input.getString("TRADE_STAFF_ID");

        IDataset resultProductElements = new DatasetList();

        IDataset productElements = new QueryInfoSVC().qryProductElementsInft(input);

        if (IDataUtil.isNotEmpty(productElements))
        {
            DatasetList discntList = new DatasetList();
            DatasetList svcList = new DatasetList();

            IData productElement = null;

            //宽带接口开户需要过滤掉的discntCode，目前配置的是商务宽带优惠
            IDataset discntCodeList = CommparaInfoQry.getCommparaAllCol("CSM", "185", "600", "0898");

            for (int i = 0; i < productElements.size(); i++)
            {
                productElement = productElements.getData(i);

                if ("D".equals(productElement.getString("ELEMENT_TYPE_CODE")))
                {
                    //标记是否过滤掉，默认不过滤
                    boolean falg = false;

                    if (IDataUtil.isNotEmpty(discntCodeList))
                    {
                        for (int j = 0; j < discntCodeList.size(); j++)
                        {
                            //如果在配置参数中有则过滤掉
                            if (StringUtils.equals(productElement.getString("ELEMENT_ID"), discntCodeList.getData(j).getString("PARA_CODE1")))
                            {
                                falg = true;

                                break;
                            }
                        }
                    }

                    if (!falg)
                    {
                        discntList.add(productElements.getData(i));
                    }

                }
                else
                {
                    svcList.add(productElements.getData(i));
                }
            }

            //过滤掉没有权限的优惠
            DiscntPrivUtil.filterDiscntListByPriv(staffId, discntList);


            resultProductElements.addAll(discntList);
            resultProductElements.addAll(svcList);
        }

        return resultProductElements;
    }

	/**
     *  根据宽带营销活动ID查询宽带附加营销活动(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author xuyt
     */
    public IDataset querySaleActivesByAttrIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "PRODUCT_ID");

        IDataUtil.chkParam(input, "SALE_ACTIVE_ID");

        String productId = input.getString("PRODUCT_ID");
        String sale_active_id = input.getString("SALE_ACTIVE_ID");

        IDataset retrunSaleActiveList =  new DatasetList();

        IData data = new DataMap();
        data.put("SUBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "178");
        data.put("PARAM_CODE", "600");
        data.put("EPARCHY_CODE", "0898");
        data.put("PARA_CODE2", sale_active_id);
        data.put("PARA_CODE1", productId);
        IDataset saleActiveList = CommparaInfoQry.getCommparaInfoByAll(data);

        if (IDataUtil.isNotEmpty(saleActiveList))
        {
            IData saleActive = null;
            IData returnSaleActive = null;

            for (int i = 0; i < saleActiveList.size(); i++)
            {
                saleActive = saleActiveList.getData(i);

                returnSaleActive = new DataMap();

                returnSaleActive.put("SALE_ACTIVE_IDATTR", saleActive.getString("PARA_CODE11"));
                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE12"));
                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));


                retrunSaleActiveList.add(returnSaleActive);
            }
        }

        return retrunSaleActiveList;
    }

    /**
     * 宽带附加营销活动办理校验(提供给APP宽带开户接口)
     * @param input
     * @return
     * @throws Exception
     * @author xuyt
     */
    public IData checkSaleActiveAttrInft(IData input) throws Exception
    {
        IData retData = new DataMap();

        //校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        //校验优惠ID列表是否传入
        IDataUtil.chkParam(input, "DISCNT_IDS");

        //校验服务ID列表是否传入
        IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");

        //校验宽带营销活动ID是否传入
        IDataUtil.chkParam(input, "SALE_ACTIVE_IDATTR");

        //校验宽带产品ID是否传入
        IDataUtil.chkParam(input, "WIDE_PRODUCT_ID");

        //标记是宽带开户营销活动
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

        String saleActiveId = input.getString("SALE_ACTIVE_IDATTR");

        IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600",null, null,saleActiveId, "0898");

        if (IDataUtil.isNotEmpty(saleActiveIDataset))
        {
        	saleActiveIDataset = WideNetUtil.filterWideSaleActiveAttrListByPriv(getVisit().getStaffId(), saleActiveIDataset);
        	if(IDataUtil.isEmpty(saleActiveIDataset)){
        		CSAppException.appError("-1", "无对应权限办理，请联系管理员申请权限！");
        	}

            IData saleActiveData = saleActiveIDataset.first();

            input.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE14"));
            input.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE15"));
        }
        else
        {
            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
        }

        // 预受理校验，不写台账
        input.put("PRE_TYPE",  BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        retData = new MergeWideUserCreateSVC().checkSelectedDiscnts(input);

        if ("-1".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160001", "用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！");
        }

        if ("-2".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160002", "用户已经选择了VIP体验套餐，不能再办理宽带营销活动，如果要办理请先取消VIP体验套餐！");
        }

        retData.put("RESULT_CODE", retData.getString("resultCode"));

        return retData;
    }



    /**
     * 宽带开户施工完成后进行付费转账操作(提供给PBOSS、装维APP调用)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData transferWidenetTradeFeeInft(IData input) throws Exception
    {
    	IData resultData = new DataMap();

        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "TRADE_ID");

        String tradeId = input.getString("TRADE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (serialNumber.startsWith("KD_"))
        {
        	serialNumber = serialNumber.substring(3);
        }

        //光猫押金
        String modemDeposit = "0";
        //魔百和押金
    	String topSetBoxDeposit = "0";
    	//宽带营销活动费用
    	String saleActiveFee = "0";
    	//魔百和营销活动费用
    	String topSetBoxSaleActiveFee = "0";
    	//IMS固话营销活动费用
    	String imsSaleActiveFee = "0";
    	//和目和营销活动费用
    	String heMuSaleActiveFee = "0";

    	//BUS201907310012关于开发家庭终端调测费的需求
    	//宽带调测费活动费用
    	String saleActiveFee2 = "0";
    	//魔百和调测费活动费用
    	String topSetBoxSaleActiveFee2 = "0";
    	IData saleDepositTD2 = null;
    	IData topSetBoxSaleDepositTD2 = null;
    	//BUS201907310012关于开发家庭终端调测费的需求

    	IData modemTradeInfo = null;
    	IData topSetBoxTradeInfo = null;
    	IData saleDepositTD = null;
    	IData topSetBoxSaleDepositTD = null;
    	IData imsSaleDepositTD = null;
    	IData heMuSaleDepositTD = null;


        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");

        if (IDataUtil.isEmpty(tradeInfos))
        {
        	CSAppException.appError("-1", "该业务流水号["+tradeId+"]订单信息不存在！");
        }

        IData widenetTrade = tradeInfos.getData(0);

        if (!"A".equals(widenetTrade.getString("RSRV_STR1")))
        {
        	CSAppException.appError("-1", "该宽带开户付费模式不是“先装后付”，不需要进行付费操作！");
        }


        if ("Y".equals(widenetTrade.getString("RSRV_STR8")))
        {
        	CSAppException.appError("-1", "该宽带开户已经付费成功，不需要再次进行付费操作！");
        }


		// 是否有光猫
		String modemStyle = widenetTrade.getString("RSRV_STR2");

		if (StringUtils.isNotBlank(modemStyle))
		{
			// 取TF_B_TRADE_OTHER RSRV_VALUE_CODE = FTTH
			IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH");
			if (IDataUtil.isNotEmpty(tradeOtherInfos))
			{
	    		for(int i=0;i<tradeOtherInfos.size();i++)
	    		{
	    			String tradeStr = tradeOtherInfos.getData(i).getString("RSRV_STR1");
	    			if(StringUtils.isBlank(tradeStr))
	    			{
	    				CSAppException.appError("-1", "光猫未出库，不能付款！");
	    			}
	    			if("0".equals(tradeOtherInfos.getData(i).getString("MODIFY_TAG")))
	    			{
	    				modemTradeInfo = tradeOtherInfos.getData(i);
	    				modemDeposit = modemTradeInfo.getString("RSRV_STR2", "0");
	    				break;
	    			}
	    		}
			}
		}

		//魔百和押金转移流水ID
        String topSetBoxDepositTradeId = widenetTrade.getString("RSRV_STR4");

        //办理魔百合业务标识
        String rsrvStr3 = widenetTrade.getString("RSRV_STR3");
        if(StringUtils.isNotBlank(rsrvStr3) && "1".equals(rsrvStr3) )
        {
        	IDataset topSetBoxTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");

        	if(IDataUtil.isNotEmpty(topSetBoxTradeInfos))
            {
        		topSetBoxTradeInfo = topSetBoxTradeInfos.getData(0);

	        	//预留字段RSRV_STR8 如果为 null 表示魔百合没有出库
	        	String topSetBoxStr = topSetBoxTradeInfo.getString("RSRV_STR8");
	        	//没有取消魔百和并且魔百和串号为空时 拦截  by xuzh5 2018-6-11 11:24:35   先装后付取消魔百和后无法继续装机问题优化
				if( StringUtils.isBlank(topSetBoxStr) && !"N".equals(topSetBoxTradeInfo.getString("RSRV_TAG1","0")))
				{
					CSAppException.appError("-1", "魔百和未出库，不能付款！");
				}

	        	if (StringUtils.isNotBlank(topSetBoxDepositTradeId))
	            {
	                	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
	                	if(!"N".equals(topSetBoxTradeInfo.getString("RSRV_TAG1","0")))
	                	{
	                		topSetBoxDeposit = topSetBoxTradeInfo.getString("RSRV_STR6","0");
	                	}
	            }
            }
        }


	    //宽带营销活动
	    String saleActiveTradeId = widenetTrade.getString("RSRV_STR5","");

	    //BUS201907310012关于开发家庭终端调测费的需求
	    String saleActiveTradeId2 = "" ;
	    if(saleActiveTradeId.contains("|"))
	    {
	    	String []strTradeIds = saleActiveTradeId.split("\\|");
	    	saleActiveTradeId = strTradeIds[0];
	    	saleActiveTradeId2 = strTradeIds[1];
	    }
	    if (StringUtils.isNotBlank(saleActiveTradeId2))
	    {
	    	IDataset saleActiveDepositInfos2 = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(saleActiveTradeId2);

	    	if (IDataUtil.isNotEmpty(saleActiveDepositInfos2))
	    	{
	    		saleDepositTD2 = saleActiveDepositInfos2.getData(0);
	    		saleActiveFee2 = saleDepositTD2.getString("FEE","0");
	    	}
	    }
	    //BUS201907310012关于开发家庭终端调测费的需求

	    if (StringUtils.isNotBlank(saleActiveTradeId))
	    {
	    	IDataset saleActiveDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(saleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(saleActiveDepositInfos))
	    	{
	    		saleDepositTD = saleActiveDepositInfos.getData(0);
	    		saleActiveFee = saleDepositTD.getString("FEE","0");
	    	}
	    }


	    //宽带营销活动
	    String topSetBoxSaleActiveTradeId = widenetTrade.getString("RSRV_STR6","");
	    //BUS201907310012关于开发家庭终端调测费的需求
	    boolean cancelTopSetBoxTag = false;
	    IDataset topSetBoxTradeInfos1 = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");
        if(IDataUtil.isNotEmpty(topSetBoxTradeInfos1))
        {
        	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
        	if("N".equals(topSetBoxTradeInfos1.getData(0).getString("RSRV_TAG1","0")))
        	{
        		cancelTopSetBoxTag = true;
        	}
        }
	    String topSetBoxSaleActiveTradeId2 = "" ;
	    if(topSetBoxSaleActiveTradeId.contains("|"))
	    {
	    	String []strTradeIds = topSetBoxSaleActiveTradeId.split("\\|");
	    	topSetBoxSaleActiveTradeId = strTradeIds[0];
	    	topSetBoxSaleActiveTradeId2 = strTradeIds[1];
	    }
	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId2) && !cancelTopSetBoxTag)
	    {
	    	IDataset topSetBoxSaleTradeDepositInfos2 = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(topSetBoxSaleActiveTradeId2);

	    	if (IDataUtil.isNotEmpty(topSetBoxSaleTradeDepositInfos2))
	    	{
	    		topSetBoxSaleDepositTD2 = topSetBoxSaleTradeDepositInfos2.getData(0);
	    		topSetBoxSaleActiveFee2 = topSetBoxSaleDepositTD2.getString("FEE","0");
	    	}
	    }
	    //BUS201907310012关于开发家庭终端调测费的需求
	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId) && !cancelTopSetBoxTag)
	    {
	    	IDataset topSetBoxSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(topSetBoxSaleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(topSetBoxSaleTradeDepositInfos))
	    	{
	    		topSetBoxSaleDepositTD = topSetBoxSaleTradeDepositInfos.getData(0);
	    		topSetBoxSaleActiveFee = topSetBoxSaleDepositTD.getString("FEE","0");
	    	}
	    }

	    //和目营销活动
		String heMuSaleActiveTradeId = widenetTrade.getString("RSRV_STR10","");
		if(StringUtils.isNotBlank(heMuSaleActiveTradeId) && heMuSaleActiveTradeId.length() > 2)
		{
			IDataset heMuSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(heMuSaleActiveTradeId);

		    if (IDataUtil.isNotEmpty(heMuSaleTradeDepositInfos))
		    {
		    	heMuSaleDepositTD = heMuSaleTradeDepositInfos.getData(0);
		    	heMuSaleActiveFee = heMuSaleDepositTD.getString("FEE","0");
		     }
		}


		String imsSaleActiveTradeId = "";

		//查询回去是否办理了IMS固话业务
	   	IDataset imsTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSTRADE");


		if (IDataUtil.isNotEmpty(imsTradeInfos))
		{
			IData imsTradeInfo = imsTradeInfos.first();

			//N表示用户确认不开通IMS固话业务
			if (!"N".equals(imsTradeInfo.getString("RSRV_TAG1","")))
			{
				//IMS固话营销活动TRADEID
				imsSaleActiveTradeId = imsTradeInfo.getString("RSRV_STR30","");

				if (StringUtils.isNotBlank(imsSaleActiveTradeId))
				{
					IDataset imsSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(imsSaleActiveTradeId);

				    if (IDataUtil.isNotEmpty(imsSaleTradeDepositInfos))
				    {
				    	imsSaleDepositTD = imsSaleTradeDepositInfos.getData(0);
				    	imsSaleActiveFee = imsSaleDepositTD.getString("FEE","0");
				    }
				}
			}
		}


	    String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);

        int allTotalTransFee = Integer.parseInt(saleActiveFee2) + Integer.parseInt(topSetBoxSaleActiveFee2) + Integer.parseInt(saleActiveFee) + Integer.parseInt(topSetBoxSaleActiveFee)
        					 + Integer.parseInt(imsSaleActiveFee) + Integer.parseInt(heMuSaleActiveFee);

        if(Integer.parseInt(leftFee)< allTotalTransFee )
        {
            CSAppException.appError("61314", "您的账户存折可用余额（"+Double.parseDouble(leftFee)/100+"元）不足，请先办理缴费。本次需转出费用：[光猫调测费活动金额：" + Double.parseDouble(saleActiveFee2)/100+"元，魔百和调测费活动金额："
            			+ Double.parseDouble(topSetBoxSaleActiveFee2)/100 + "元,宽带营销活动费用金额：" + Double.parseDouble(saleActiveFee)/100
            			+ "元,魔百和营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee)/100
            			+ "元,IMS固话营销活动费用金额:" + Double.parseDouble(imsSaleActiveFee)/100
            			+ "元,和目营销活动费用金额:" + Double.parseDouble(heMuSaleActiveFee)/100
            			+ "元,共计"+allTotalTransFee/100+"]");
        }


        //2.1 光猫押金转账操作
        if (Integer.parseInt(modemDeposit) > 0)
        {
        	if (IDataUtil.isNotEmpty(modemTradeInfo))
        	{
        		IData inparams=new DataMap();
                inparams.put("SERIAL_NUMBER", serialNumber);
                inparams.put("OUTER_TRADE_ID", modemTradeInfo.getString("RSRV_STR8")); //光猫扣押金的流水ID，退还时需要此流水
                inparams.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
                inparams.put("DEPOSIT_CODE_IN", "9002"); //9002 光猫押金转入存折
                inparams.put("TRADE_FEE", modemDeposit);
                inparams.put("CHANNEL_ID", "15000");
                inparams.put("UPDATE_DEPART_ID", widenetTrade.getString("TRADE_DEPART_ID"));
                inparams.put("UPDATE_STAFF_ID", widenetTrade.getString("TRADE_STAFF_ID"));
                inparams.put("TRADE_CITY_CODE",  widenetTrade.getString("TRADE_CITY_CODE"));
                inparams.put("TRADE_DEPART_ID", widenetTrade.getString("TRADE_DEPART_ID"));
                inparams.put("TRADE_STAFF_ID", widenetTrade.getString("TRADE_STAFF_ID"));

                IData inAcct = AcctCall.transFeeInADSL(inparams);

                String result=inAcct.getString("RESULT_CODE","");

                if("".equals(result) || !"0".equals(result))
                {
                    CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + inparams + "错误:" + inAcct.getString("RESULT_INFO"));
                }
        	}
        }

        //2.2 魔百和押金转账操作
        if (Integer.parseInt(topSetBoxDeposit) > 0)
        {
            //调用账务的接口进行押金返回
            IData params=new DataMap();

            params.put("OUTER_TRADE_ID", widenetTrade.getString("RSRV_STR4"));
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
            params.put("DEPOSIT_CODE_IN", "9016");
            params.put("TRADE_FEE", topSetBoxDeposit);
            params.put("CHANNEL_ID", "15000");
            params.put("UPDATE_DEPART_ID", widenetTrade.getString("TRADE_DEPART_ID"));
            params.put("UPDATE_STAFF_ID", widenetTrade.getString("TRADE_STAFF_ID"));
            params.put("TRADE_CITY_CODE",  widenetTrade.getString("TRADE_CITY_CODE"));
            params.put("TRADE_DEPART_ID", widenetTrade.getString("TRADE_DEPART_ID"));
            params.put("TRADE_STAFF_ID", widenetTrade.getString("TRADE_STAFF_ID"));

            //调用接口，将【现金类】——>【押金】
            IData transFeeResultData = AcctCall.transFeeInADSL(params);


            String result = transFeeResultData.getString("RESULT_CODE","");

            if("".equals(result) || !"0".equals(result))
            {
                CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + transFeeResultData.getString("RESULT_INFO"));
            }
        }

        //2.3 宽带营销活动转账操作
        if (Integer.parseInt(saleActiveFee) > 0)
        {
            transSaleActiveFee(saleActiveTradeId, serialNumber, saleDepositTD);
        }


        //2.4 魔百和营销活动转账操作
        if (Integer.parseInt(topSetBoxSaleActiveFee) > 0)
        {
        	transSaleActiveFee(topSetBoxSaleActiveTradeId, serialNumber, topSetBoxSaleDepositTD);
        }

        //2.3.1 宽带调测费活动转账操作
        if (Integer.parseInt(saleActiveFee2) > 0)
        {
            transSaleActiveFee(saleActiveTradeId2, serialNumber, saleDepositTD2);
        }


        //2.4.1 魔百和调测费活动转账操作
        if (Integer.parseInt(topSetBoxSaleActiveFee2) > 0)
        {
        	transSaleActiveFee(topSetBoxSaleActiveTradeId2, serialNumber, topSetBoxSaleDepositTD2);
        }


        //2.5 IMS固话营销活动转账操作
        if (Integer.parseInt(imsSaleActiveFee) > 0)
        {
        	transSaleActiveFee(imsSaleActiveTradeId, serialNumber, imsSaleDepositTD);
        }

        //2.6 和目营销活动转账操作
        //BUS201907310012关于开发家庭终端调测费的需求--修改和目逻辑错误BUG
        //if (Integer.parseInt(topSetBoxSaleActiveFee) > 0)
        if (Integer.parseInt(heMuSaleActiveFee) > 0)
        {
        	transSaleActiveFee(heMuSaleActiveTradeId, serialNumber, heMuSaleDepositTD);
        }
        //BUS201907310012关于开发家庭终端调测费的需求

        //修改订单标记为已付费
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RSRV_STR8", "Y");
        StringBuilder sql = new StringBuilder(100);
        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.RSRV_STR8 = :RSRV_STR8 ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        Dao.executeUpdate(sql, param, Route.getJourDbDefault());

        resultData.put("code_Result", "0");
        resultData.put("reqSerialNo", tradeId);
        resultData.put("dateTime", SysDateMgr.getSysTime());
        resultData.put("charge", allTotalTransFee);

        return resultData;
    }

    /**
     * 查询无手机宽带账号应缴的费用
     * @param input
     * @return
     * @throws Exception
     * @author chenzh5
     */
    public IData queryWidenetTradeFee(IData input) throws Exception
    {
    	IData resultData = new DataMap();

        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        String sn = input.getString("SERIAL_NUMBER");

        //修改新的费用查询=================================================================start
        if(!sn.startsWith("KD_")){
        	sn="KD_"+sn;
        }
        IDataset tradeList = BroadBandInfoQry.qryTradeHisInfoBySerialNumber(sn);
        if(IDataUtil.isNotEmpty(tradeList)){
        	//调费用接口
        	/*IData feeParam=new DataMap();
        	feeParam.put("NEW_PRODUCT_ID", tradeList.getData(0).getString("PRODUCT_ID"));
        	feeParam.put("ROUTE_EPARCHY_CODE", "0898");
        	feeParam.put("TRADE_TYPE_CODE", "680");
        	IDataset feeList=CSAppCall.call("CS.SelectedElementSVC.getWidenetUserOpenElements", feeParam);
        	String allTotalTransFee="0";
        	if(IDataUtil.isNotEmpty(feeList)){
        		IDataset selectList=feeList.getData(0).getDataset("SELECTED_ELEMENTS");
        		if(IDataUtil.isNotEmpty(selectList)){
        			for(int i=0;i<selectList.size();i++){
        				if(selectList.getData(i).getString("ELEMENT_TYPE_CODE").equals("D")){
        					IDataset feeData=selectList.getData(i).getDataset("FEE_DATA");
        					if(IDataUtil.isNotEmpty(feeData)){
        						for(int j=0;j<feeData.size();j++){
        							if("9021".equals(feeData.getData(j).getString("CODE"))){
        								allTotalTransFee=feeData.getData(j).getString("FEE");
        								break;
        							}
        						}
        					}
        				}
        			}
        		}
        	}*/
        	int allTotalTransFee=0;
        	IDataset tradeDiscntList = BroadBandInfoQry.qryTradeDiscntInfoByTradeId(tradeList.getData(0).getString("TRADE_ID"));
        	if(IDataUtil.isNotEmpty(tradeDiscntList)){
        		for(int i=0;i<tradeDiscntList.size();i++){
        			if("0".equals(tradeDiscntList.getData(i).getString("MODIFY_TAG"))){
		        		IData feeParam=new DataMap();
		            	feeParam.put("ROUTE_EPARCHY_CODE", "0898");
		            	feeParam.put("TRADE_TYPE_CODE", "680");
		            	feeParam.put("PRODUCT_ID", tradeList.getData(0).getString("PRODUCT_ID"));
		            	feeParam.put("ELEMENT_ID", tradeDiscntList.getData(i).getString("DISCNT_CODE"));
		            	feeParam.put("ELEMENT_TYPE_CODE", "D");
		            	IDataset feeList=CSAppCall.call("CS.ProductFeeInfoQrySVC.getProductFeeInfo", feeParam);
		            	if(IDataUtil.isNotEmpty(feeList)){
		            		for(int j=0;j<feeList.size();j++){
		            			if("9021".equals(feeList.getData(j).getString("FEE_TYPE_CODE"))){
									allTotalTransFee+=Integer.parseInt(feeList.getData(j).getString("FEE","0"));
								}
		            			//add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
		            			if("9081".equals(feeList.getData(j).getString("FEE_TYPE_CODE"))){
									allTotalTransFee+=Integer.parseInt(feeList.getData(j).getString("FEE","0"));
								}
		            			//add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
		            		}
		            	}
        			}
        		}
        	}

        	//是否有光猫
        	if("0".equals(tradeList.getData(0).getString("RSRV_STR2"))){
        		allTotalTransFee+=10000;
        	}

	        resultData.put("code_Result", "0");
	        resultData.put("reqSerialNo", tradeList.getData(0).getString("TRADE_ID"));
	        resultData.put("dateTime", SysDateMgr.getSysTime());
	        resultData.put("charge", allTotalTransFee+"");

	        IData tradeInfo = tradeList.getData(0);
	        IDataset wideNetInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeList.getData(0).getString("TRADE_ID"));
	        String epachyCode = "";

	        if (IDataUtil.isNotEmpty(wideNetInfos))
	        {
	        	String areaCode = wideNetInfos.first().getString("RSRV_STR4");
	        	epachyCode = UAreaInfoQry.getAreaNameByAreaCode(areaCode);
	        }

	        resultData.put("custName", DataFuzzy.fuzzyName(tradeInfo.getString("CUST_NAME")));
	        resultData.put("openDate", tradeInfo.getString("ACCEPT_DATE"));
	        resultData.put("eparchyCode", epachyCode);
	        return resultData;
        }else{
        	CSAppException.appError("-1", "该手机号["+sn+"]无手机宽带开户业务订单信息不存在！");
        }
	    //修改新的费用查询=================================================================end

        IDataset trade = BroadBandInfoQry.qryBroadbandTradeInfo(sn,"680");
        if(IDataUtil.isEmpty(trade)) {
        	CSAppException.appError("-1", "该手机号["+sn+"]无手机宽带开户业务订单信息不存在！");
        }
        String tradeId = trade.getData(0).getString("TRADE_ID");

        //光猫押金
        String modemDeposit = "0";
        //魔百和押金
    	String topSetBoxDeposit = "0";
    	//宽带营销活动费用
    	String saleActiveFee = "0";
    	//魔百和营销活动费用
    	String topSetBoxSaleActiveFee = "0";
    	//IMS固话营销活动费用
    	String imsSaleActiveFee = "0";
    	//和目和营销活动费用
    	String heMuSaleActiveFee = "0";

    	IData modemTradeInfo = null;
    	IData topSetBoxTradeInfo = null;
    	IData saleDepositTD = null;
    	IData topSetBoxSaleDepositTD = null;
    	IData imsSaleDepositTD = null;
    	IData heMuSaleDepositTD = null;


        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");

        if (IDataUtil.isEmpty(tradeInfos))
        {
        	CSAppException.appError("-1", "该业务流水号["+tradeId+"]订单信息不存在！");
        }

        IData widenetTrade = tradeInfos.getData(0);

//        if (!"A".equals(widenetTrade.getString("RSRV_STR1")))
//        {
//        	CSAppException.appError("-1", "该宽带开户付费模式不是“先装后付”，不需要进行付费操作！");
//        }


        if ("Y".equals(widenetTrade.getString("RSRV_STR8")))
        {
        	CSAppException.appError("-1", "该宽带开户已经付费成功，不需要再次进行付费操作！");
        }


		// 是否有光猫
		String modemStyle = widenetTrade.getString("RSRV_STR2");

		if (StringUtils.isNotBlank(modemStyle))
		{
			// 取TF_B_TRADE_OTHER RSRV_VALUE_CODE = FTTH
			IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH");
			if (IDataUtil.isNotEmpty(tradeOtherInfos))
			{
	    		for(int i=0;i<tradeOtherInfos.size();i++)
	    		{

	    			if("0".equals(tradeOtherInfos.getData(i).getString("MODIFY_TAG")))
	    			{
	    				modemTradeInfo = tradeOtherInfos.getData(i);
	    				modemDeposit = modemTradeInfo.getString("RSRV_STR2", "0");
	    				break;
	    			}
	    		}
			}
		}

		//魔百和押金转移流水ID
        String topSetBoxDepositTradeId = widenetTrade.getString("RSRV_STR4");

        //办理魔百合业务标识
        String rsrvStr3 = widenetTrade.getString("RSRV_STR3");

        if(StringUtils.isNotBlank(rsrvStr3) && "1".equals(rsrvStr3) )
        {
        	IDataset topSetBoxTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");

        	if(IDataUtil.isNotEmpty(topSetBoxTradeInfos))
            {
        		topSetBoxTradeInfo = topSetBoxTradeInfos.getData(0);



	        	if (StringUtils.isNotBlank(topSetBoxDepositTradeId))
	            {
	                	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
	                	if(!"N".equals(topSetBoxTradeInfo.getString("RSRV_TAG1","0")))
	                	{
	                		topSetBoxDeposit = topSetBoxTradeInfo.getString("RSRV_STR6","0");
	                	}
	            }
            }
        }


	    //宽带营销活动
	    String saleActiveTradeId = widenetTrade.getString("RSRV_STR5","");

	    if (StringUtils.isNotBlank(saleActiveTradeId))
	    {
	    	IDataset saleActiveDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(saleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(saleActiveDepositInfos))
	    	{
	    		saleDepositTD = saleActiveDepositInfos.getData(0);
	    		saleActiveFee = saleDepositTD.getString("FEE","0");
	    	}
	    }


	    //宽带营销活动
	    String topSetBoxSaleActiveTradeId = widenetTrade.getString("RSRV_STR6","");

	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId))
	    {
	    	IDataset topSetBoxSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(topSetBoxSaleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(topSetBoxSaleTradeDepositInfos))
	    	{
	    		topSetBoxSaleDepositTD = topSetBoxSaleTradeDepositInfos.getData(0);
	    		topSetBoxSaleActiveFee = topSetBoxSaleDepositTD.getString("FEE","0");
	    	}
	    }

	    //和目营销活动
		String heMuSaleActiveTradeId = widenetTrade.getString("RSRV_STR10","");
		if(StringUtils.isNotBlank(heMuSaleActiveTradeId) && heMuSaleActiveTradeId.length() > 2)
		{
			IDataset heMuSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(heMuSaleActiveTradeId);

		    if (IDataUtil.isNotEmpty(heMuSaleTradeDepositInfos))
		    {
		    	heMuSaleDepositTD = heMuSaleTradeDepositInfos.getData(0);
		    	heMuSaleActiveFee = heMuSaleDepositTD.getString("FEE","0");
		     }
		}


		String imsSaleActiveTradeId = "";

		//查询回去是否办理了IMS固话业务
	   	IDataset imsTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSTRADE");


		if (IDataUtil.isNotEmpty(imsTradeInfos))
		{
			IData imsTradeInfo = imsTradeInfos.first();

			//N表示用户确认不开通IMS固话业务
			if (!"N".equals(imsTradeInfo.getString("RSRV_TAG1","")))
			{
				//IMS固话营销活动TRADEID
				imsSaleActiveTradeId = imsTradeInfo.getString("RSRV_STR30","");

				if (StringUtils.isNotBlank(imsSaleActiveTradeId))
				{
					IDataset imsSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(imsSaleActiveTradeId);

				    if (IDataUtil.isNotEmpty(imsSaleTradeDepositInfos))
				    {
				    	imsSaleDepositTD = imsSaleTradeDepositInfos.getData(0);
				    	imsSaleActiveFee = imsSaleDepositTD.getString("FEE","0");
				    }
				}
			}
		}


//	    String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);

        int allTotalTransFee = Integer.parseInt(modemDeposit) + Integer.parseInt(topSetBoxDeposit) + Integer.parseInt(saleActiveFee) + Integer.parseInt(topSetBoxSaleActiveFee)
        					 + Integer.parseInt(imsSaleActiveFee) + Integer.parseInt(heMuSaleActiveFee);


        resultData.put("code_Result", "0");
        resultData.put("reqSerialNo", tradeId);
        resultData.put("dateTime", SysDateMgr.getSysTime());
        resultData.put("charge", allTotalTransFee);

        IData tradeInfo = trade.getData(0);
        IDataset wideNetInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
        String epachyCode = "";

        if (IDataUtil.isNotEmpty(wideNetInfos))
        {
        	String areaCode = wideNetInfos.first().getString("RSRV_STR4");
        	epachyCode = UAreaInfoQry.getAreaNameByAreaCode(areaCode);
        }

        resultData.put("custName", DataFuzzy.fuzzyName(tradeInfo.getString("CUST_NAME")));
        resultData.put("openDate", tradeInfo.getString("ACCEPT_DATE"));
        resultData.put("eparchyCode", epachyCode);
        return resultData;
    }


    /**
     * 营销活动费用同账户转账
     * @param saleActiveTradeId
     * @param serialNumber
     * @param saleDepositTD
     * @throws Exception
     */
    private void transSaleActiveFee(String saleActiveTradeId, String serialNumber, IData saleDepositTD) throws Exception
    {
        	IData otherFee = new DataMap();

        	otherFee.put("OPER_TYPE", BofConst.OTHERFEE_SAME_TRANS);
        	otherFee.put("USER_ID", saleDepositTD.getString("USER_ID"));
        	otherFee.put("OPER_FEE", saleDepositTD.getString("FEE"));
        	otherFee.put("ACTION_CODE", saleDepositTD.getString("A_DISCNT_CODE"));
        	otherFee.put("PAYMENT_ID", saleDepositTD.getString("PAYMENT_ID"));
        	otherFee.put("IN_DEPOSIT_CODE", saleDepositTD.getString("IN_DEPOSIT_CODE"));
        	otherFee.put("OUT_DEPOSIT_CODE", saleDepositTD.getString("OUT_DEPOSIT_CODE"));
        	otherFee.put("START_DATE", saleDepositTD.getString("START_DATE"));
        	otherFee.put("RSRV_STR4", saleDepositTD.getString("RSRV_STR4"));
        	otherFee.put("RSRV_STR5", saleDepositTD.getString("RSRV_STR5"));
        	otherFee.put("TRADE_ID", saleDepositTD.getString("TRADE_ID"));
        	otherFee.put("ACCEPT_MONTH", saleDepositTD.getString("ACCEPT_MONTH"));
        	otherFee.put("UPDATE_TIME", saleDepositTD.getString("UPDATE_TIME"));
        	otherFee.put("UPDATE_STAFF_ID", saleDepositTD.getString("UPDATE_STAFF_ID"));
        	otherFee.put("UPDATE_DEPART_ID", saleDepositTD.getString("UPDATE_DEPART_ID"));


        	if (!Dao.insert("TF_B_TRADEFEE_OTHERFEE", otherFee, Route.getJourDb()))
            {
                String msg = "宽带营销活动转账插'TF_B_TRADEFEE_OTHERFEE'表【" + otherFee.getString("TRADE_ID") + "】失败!";
                CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
            }


            if("TO_TEMP".equals(saleDepositTD.getString("RSRV_STR4")))//如果是配置了将预存转入临时存折
            {
            	//费用大于0时才调用转账
            	if (StringUtils.isNotBlank(saleDepositTD.getString("FEE")) && Integer.valueOf(saleDepositTD.getString("FEE")) > 0)
            	{
            	    AcctCall.tempTransFee(saleActiveTradeId, serialNumber, saleDepositTD.getString("FEE"), saleDepositTD.getString("OUT_DEPOSIT_CODE"),
            	    		saleDepositTD.getString("IN_DEPOSIT_CODE"), saleDepositTD.getString("PAYMENT_ID"), saleDepositTD.getString("A_DISCNT_CODE"), saleDepositTD.getString("START_DATE"), saleDepositTD.getString("RSRV_STR5"));
            	}
            }
            else
            {
                AcctCall.transFee(saleActiveTradeId, saleDepositTD.getString("USER_ID"), saleDepositTD.getString("USER_ID"), saleDepositTD.getString("OUT_DEPOSIT_CODE"), saleDepositTD.getString("IN_DEPOSIT_CODE"), saleDepositTD.getString("FEE"), "");
            }
        }


    /**
     * 获得宽带费用明细列表(提供给PBOSS、装维APP调用)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData getWidenetTradeFeeInft(IData input) throws Exception
    {
    	IData resultData = new DataMap();

        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "TRADE_ID");

        String tradeId = input.getString("TRADE_ID");

        //光猫押金
        String modemDeposit = "0";
        //魔百和押金
    	String topSetBoxDeposit = "0";
    	//宽带营销活动费用
    	String saleActiveFee = "0";
    	//魔百和营销活动费用
    	String topSetBoxSaleActiveFee = "0";
    	//IMS固话营销活动费用
    	String imsSaleActiveFee = "0";
    	//和目和营销活动费用
    	String heMuSaleActiveFee = "0";

    	//BUS201907310012关于开发家庭终端调测费的需求
    	//宽带调测费活动费用
    	String saleActiveFee2 = "0";
    	//魔百和调测费活动费用
    	String topSetBoxSaleActiveFee2 = "0";
    	IData saleDepositTD2 = null;
    	IData topSetBoxSaleDepositTD2 = null;
    	//BUS201907310012关于开发家庭终端调测费的需求

    	IData modemTradeInfo = null;
    	IData topSetBoxTradeInfo = null;
    	IData saleDepositTD = null;
    	IData topSetBoxSaleDepositTD = null;
    	IData imsSaleDepositTD = null;
    	IData heMuSaleDepositTD = null;


        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");

        if (IDataUtil.isEmpty(tradeInfos))
        {
        	CSAppException.appError("-1", "该业务流水号["+tradeId+"]订单信息不存在！");
        }

        IData widenetTrade = tradeInfos.getData(0);

		// 是否有光猫
		String modemStyle = widenetTrade.getString("RSRV_STR2");

		if (StringUtils.isNotBlank(modemStyle))
		{
			// 取TF_B_TRADE_OTHER RSRV_VALUE_CODE = FTTH
			IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "FTTH");
			if (IDataUtil.isNotEmpty(tradeOtherInfos))
			{
	    		for(int i=0;i<tradeOtherInfos.size();i++)
	    		{
	    			if("0".equals(tradeOtherInfos.getData(i).getString("MODIFY_TAG")))
	    			{
	    				modemTradeInfo = tradeOtherInfos.getData(i);
	    				modemDeposit = modemTradeInfo.getString("RSRV_STR2", "0");
	    				break;
	    			}
	    		}
			}
		}

		//魔百和押金转移流水ID
        String topSetBoxDepositTradeId = widenetTrade.getString("RSRV_STR4");

        if (StringUtils.isNotBlank(topSetBoxDepositTradeId))
        {
        	IDataset topSetBoxTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");
            if(IDataUtil.isNotEmpty(topSetBoxTradeInfos))
            {
            	topSetBoxTradeInfo = topSetBoxTradeInfos.getData(0);

            	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
            	if(!"N".equals(topSetBoxTradeInfo.getString("RSRV_TAG1","0")))
            	{
            		topSetBoxDeposit = topSetBoxTradeInfo.getString("RSRV_STR6","0");
            	}
            }
        }



	    //宽带营销活动
	    String saleActiveTradeId = widenetTrade.getString("RSRV_STR5","");
		 //BUS201907310012关于开发家庭终端调测费的需求
	    String saleActiveTradeId2 = "" ;
	    if(saleActiveTradeId.contains("|"))
	    {
	    	String []strTradeIds = saleActiveTradeId.split("\\|");
	    	saleActiveTradeId = strTradeIds[0];
	    	saleActiveTradeId2 = strTradeIds[1];
	    }
	    if (StringUtils.isNotBlank(saleActiveTradeId2))
	    {
	    	IDataset saleActiveDepositInfos2 = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(saleActiveTradeId2);

	    	if (IDataUtil.isNotEmpty(saleActiveDepositInfos2))
	    	{
	    		saleDepositTD2 = saleActiveDepositInfos2.getData(0);
	    		saleActiveFee2 = saleDepositTD2.getString("FEE","0");

	    	}
	    }
	    //BUS201907310012关于开发家庭终端调测费的需求
	    if (StringUtils.isNotBlank(saleActiveTradeId))
	    {
	    	IDataset saleActiveDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(saleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(saleActiveDepositInfos))
	    	{
	    		saleDepositTD = saleActiveDepositInfos.getData(0);
	    		saleActiveFee = saleDepositTD.getString("FEE","0");

	    	}
	    }

	    //魔百和营销活动
	    //BUS201907310012关于开发家庭终端调测费的需求
	    boolean cancelTopSetBoxTag = false;
	    IDataset topSetBoxTradeInfos1 = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"TOPSETBOX");
        if(IDataUtil.isNotEmpty(topSetBoxTradeInfos1))
        {
        	//预留字段RSRV_TAG1如果为N表示服务开通那边已经取消魔百和开通了。完工不需要再调账务接口转账
        	if("N".equals(topSetBoxTradeInfos1.getData(0).getString("RSRV_TAG1","0")))
        	{
        		cancelTopSetBoxTag = true;
        	}
        }

	    String topSetBoxSaleActiveTradeId = widenetTrade.getString("RSRV_STR6","");

	    String topSetBoxSaleActiveTradeId2 = "" ;
	    if(topSetBoxSaleActiveTradeId.contains("|"))
	    {
	    	String []strTradeIds = topSetBoxSaleActiveTradeId.split("\\|");
	    	topSetBoxSaleActiveTradeId = strTradeIds[0];
	    	topSetBoxSaleActiveTradeId2 = strTradeIds[1];
	    }
	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId2) && !cancelTopSetBoxTag)
	    {
	    	IDataset topSetBoxSaleTradeDepositInfos2 = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(topSetBoxSaleActiveTradeId2);

	    	if (IDataUtil.isNotEmpty(topSetBoxSaleTradeDepositInfos2))
	    	{
	    		topSetBoxSaleDepositTD2 = topSetBoxSaleTradeDepositInfos2.getData(0);
	    		topSetBoxSaleActiveFee2 = topSetBoxSaleDepositTD2.getString("FEE","0");

	    	}
	    }
	    //BUS201907310012关于开发家庭终端调测费的需求

	    if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId) && !cancelTopSetBoxTag)
	    {
	    	IDataset topSetBoxSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(topSetBoxSaleActiveTradeId);

	    	if (IDataUtil.isNotEmpty(topSetBoxSaleTradeDepositInfos))
	    	{
	    		topSetBoxSaleDepositTD = topSetBoxSaleTradeDepositInfos.getData(0);
	    		topSetBoxSaleActiveFee = topSetBoxSaleDepositTD.getString("FEE","0");

	    	}
	    }


	   //和目营销活动
	   String heMuSaleActiveTradeId = widenetTrade.getString("RSRV_STR10","");
	   if(StringUtils.isNotBlank(heMuSaleActiveTradeId) && heMuSaleActiveTradeId.length() > 2)
	   {
		   IDataset heMuSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(heMuSaleActiveTradeId);

	       if (IDataUtil.isNotEmpty(heMuSaleTradeDepositInfos))
	       {
	    	   heMuSaleDepositTD = heMuSaleTradeDepositInfos.getData(0);
	    	   heMuSaleActiveFee = heMuSaleDepositTD.getString("FEE","0");
	       }
	   }


	   //判断办理的IMS固话业务是否已经出库开通
   	  IDataset imsTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSTRADE");

	   if (IDataUtil.isNotEmpty(imsTradeInfos))
	   {
		   IData imsTradeInfo = imsTradeInfos.first();

		   //N表示用户确认不开通IMS固话业务
		   if (!"N".equals(imsTradeInfo.getString("RSRV_TAG1","")))
		   {
			 //IMS固话营销活动TRADEID
			   String imsSaleActiveTradeId = imsTradeInfo.getString("RSRV_STR30","");

			   if (StringUtils.isNotBlank(imsSaleActiveTradeId))
			   {
				   IDataset imsSaleTradeDepositInfos = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(imsSaleActiveTradeId);

			       if (IDataUtil.isNotEmpty(imsSaleTradeDepositInfos))
			       {
			    	   imsSaleDepositTD = imsSaleTradeDepositInfos.getData(0);
			    	   imsSaleActiveFee = imsSaleDepositTD.getString("FEE","0");
			       }
			   }
		   }

	   }

        int allTotalTransFee = Integer.parseInt(modemDeposit) + Integer.parseInt(topSetBoxDeposit) + Integer.parseInt(saleActiveFee) + Integer.parseInt(topSetBoxSaleActiveFee)
        		             + Integer.parseInt(imsSaleActiveFee) + Integer.parseInt(heMuSaleActiveFee)+ Integer.parseInt(saleActiveFee2) + Integer.parseInt(topSetBoxSaleActiveFee2) ;

        IDataset feeDataList = new DatasetList();

        IData feeData = new DataMap();

        if (!"0".equals(saleActiveFee))
        {
        	feeData.put("FEE_NAME", "宽带营销活动费用");
            feeData.put("FEE", saleActiveFee);
            feeDataList.add(feeData);
        }


        if (!"0".equals(topSetBoxSaleActiveFee))
        {
        	feeData = new DataMap();
            feeData.put("FEE_NAME", "魔百和营销活动费用");
            feeData.put("FEE", topSetBoxSaleActiveFee);
            feeDataList.add(feeData);
        }

        //BUS201907310012关于开发家庭终端调测费的需求
        if (!"0".equals(saleActiveFee2))
        {
        	feeData = new DataMap();
        	feeData.put("FEE_NAME", "宽带调测费活动费用");
            feeData.put("FEE", saleActiveFee2);
            feeDataList.add(feeData);
        }


        if (!"0".equals(topSetBoxSaleActiveFee2))
        {
        	feeData = new DataMap();
            feeData.put("FEE_NAME", "魔百和调测费活动费用");
            feeData.put("FEE", topSetBoxSaleActiveFee2);
            feeDataList.add(feeData);
        }
        //BUS201907310012关于开发家庭终端调测费的需求

        if (!"0".equals(topSetBoxDeposit))
        {
        	feeData = new DataMap();
            feeData.put("FEE_NAME", "魔百和押金");
            feeData.put("FEE", topSetBoxDeposit);
            feeDataList.add(feeData);
        }

        if (!"0".equals(modemDeposit))
        {
        	feeData = new DataMap();
            feeData.put("FEE_NAME", "光猫押金");
            feeData.put("FEE", modemDeposit);
            feeDataList.add(feeData);
        }

        if (!"0".equals(imsSaleActiveFee))
        {
        	feeData = new DataMap();
        	feeData.put("FEE_NAME", "IMS固话营销活动预存费用");
            feeData.put("FEE", imsSaleActiveFee);
            feeDataList.add(feeData);
        }


        if (!"0".equals(heMuSaleActiveFee))
        {
        	feeData = new DataMap();
            feeData.put("FEE_NAME", "和目营销活动预存费用");
            feeData.put("FEE", heMuSaleActiveFee);
            feeDataList.add(feeData);
        }


        resultData.put("RESULT_CODE", "0");
        resultData.put("TOTAL_FEE", allTotalTransFee);
        resultData.put("FEE_DATA_LIST", feeDataList);

        return resultData;
    }


    /**
     * 判断先装后付模式 宽带用户是否已经缴费(提供给PBOSS、装维APP调用)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData wideuserCreateTradeIsPayCost(IData input) throws Exception
    {
    	IData resultData = new DataMap();

        IDataUtil.chkParam(input, "TRADE_ID");

        String tradeId = input.getString("TRADE_ID");

    	String isPayCost = "N";

        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");

        if (IDataUtil.isEmpty(tradeInfos))
        {
        	CSAppException.appError("-1", "该业务流水号["+tradeId+"]订单信息不存在！");
        }

        IData widenetTrade = tradeInfos.getData(0);

        //非先装后付模的 默认已经付费成功
        if (!"A".equals(widenetTrade.getString("RSRV_STR1")))
        {
        	isPayCost = "Y";
        }
        else
        {
        	if ("Y".equals(widenetTrade.getString("RSRV_STR8")))
            {
            	isPayCost = "Y";
            }
        }

        resultData.put("RESULT_CODE", "0");
        resultData.put("IS_PAY_COST", isPayCost);

        return resultData;
    }


    /**
     * 宽带开户IMS固话号码校验接口(APP调用)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkFixPhoneNumIntf(IData input) throws Exception
    {
    	IData resultData = new DataMap();

        IDataUtil.chkParam(input, "FIX_NUMBER");
        IDataUtil.chkParam(input, "AREA_CODE");
        IDataUtil.chkParam(input, "WIDE_TYPE");

        String wideType = input.getString("WIDE_TYPE");

        IData result = null;

        if ("FTTH".equals(wideType) || "TTFTTH".equals(wideType))
        {
        	//接口入参转换
        	input.put("CITYCODE_RSRVSTR4", input.getString("AREA_CODE"));
        	if(input.getString("FIX_NUMBER").startsWith("0898"))
        	{
                input.put("FIX_NUMBER", input.getString("FIX_NUMBER"));
            }
            else
            {
                input.put("FIX_NUMBER", "0898"+input.getString("FIX_NUMBER"));
            }

        	result = CSAppCall.callOne("SS.IMSLandLineSVC.checkFixPhoneNum", input);

        	//校验成功，接口出参转换
        	if ("1".equals(result.getString("RESULT_CODE")))
        	{
        		result.put("RESULT_CODE", "0");
        	}
        	else
        	{
        		CSAppException.appError("2018032801", result.getString("RESULT_INFO",""));
        	}
        }
        else
        {
        	CSAppException.appError("2018032802", "只有FTTH宽带才能办理IMS固话业务！");
        }

        resultData.put("RESULT_CODE", "0");

        return resultData;
    }


    /**
     * 获取固话产品类型(APP调用)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getImsPorductTypeListIntf(IData input) throws Exception
    {
    	IDataset resultDataset = CSAppCall.call("SS.IMSLandLineSVC.onInitTrade", input);

        return resultDataset;
    }

    /**
     * 宽带开户根据IMS固话产品类型查询固话产品信息(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getIMSProductByTypeIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "IMS_PRODUCT_TYPE_CODE");

    	IDataset result = CSAppCall.call("SS.MergeWideUserCreateSVC.getIMSProductByType", input);

        return result;
    }

    /**
     * 根据IMS固话产品ID获取IMS固话营销活动(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getImsSaleActiveIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "IMS_PRODUCT_ID");

        IDataset retrunImsSaleActiveList = new DatasetList();

        input.put("PRODUCT_ID", input.getString("IMS_PRODUCT_ID"));
        IData result = CSAppCall.callOne("SS.IMSLandLineSVC.queryDiscntPackagesByPID", input);

        IDataset imsSaleActiveDataset = result.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");

        //营销活动权限校验
        imsSaleActiveDataset = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), imsSaleActiveDataset);

        if (IDataUtil.isNotEmpty(imsSaleActiveDataset))
        {
            IData saleActive = null;
            IData returnSaleActive = null;
            IData inData = null;
            IData saleActiveFeeData = null;

            for (int i = 0; i < imsSaleActiveDataset.size(); i++)
            {
                saleActive = imsSaleActiveDataset.getData(i);

                returnSaleActive = new DataMap();
                inData = new DataMap();

                returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE5"));
                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));

                inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                //营销活动产品ID
                inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                //营销活动包ID
                inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                //获得营销活动预存费用
                saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

                if (IDataUtil.isNotEmpty(saleActiveFeeData))
                {
                    returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                }

                retrunImsSaleActiveList.add(returnSaleActive);
            }
        }

        return retrunImsSaleActiveList;
    }

    /**
     * IMS固话营销活动校验接口(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkImsSaleActiveIntf(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IDataUtil.chkParam(input, "IMS_SALE_ACTIVE_ID");
    	IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");

    	IData result = new DataMap();


    	String imsSaleActivePackageId = input.getString("IMS_SALE_ACTIVE_ID");

    	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "6800", null, null, imsSaleActivePackageId, null);

    	if (IDataUtil.isNotEmpty(saleActiveList))
    	{
    		input.put("PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));
    		input.put("PACKAGE_ID", imsSaleActivePackageId);

    		CSAppCall.call("SS.IMSLandLineSVC.checkSaleActive", input);
    	}
    	else
    	{
    		CSAppException.appError("201800001", "该营销活动["+imsSaleActivePackageId+"]参数配置信息不存在，请联系管理员！");
    	}

    	result.put("RESULT_CODE", "0");

    	return result;
    }


    /**
     * 获取和目营销活动(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getHeMuSaleActiveIntf(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "WIDE_PRODUCT_ID");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        IDataset retrunHeMuSaleActiveList = new DatasetList();

        input.put("productId", input.getString("WIDE_PRODUCT_ID"));
        IDataset resultSaleActives = CSAppCall.call("SS.MergeWideUserCreateSVC.getHeMuSaleActive", input);

        //营销活动权限校验
        resultSaleActives = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), resultSaleActives);

        if (IDataUtil.isNotEmpty(resultSaleActives))
        {
            IData saleActive = null;
            IData returnSaleActive = null;
            IData inData = null;
            IData saleActiveFeeData = null;

            for (int i = 0; i < resultSaleActives.size(); i++)
            {
                saleActive = resultSaleActives.getData(i);

                returnSaleActive = new DataMap();
                inData = new DataMap();

                returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE5"));
                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("PARA_CODE24"));

                inData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                //营销活动产品ID
                inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                //营销活动包ID
                inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                //获得营销活动预存费用
                saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);

                if (IDataUtil.isNotEmpty(saleActiveFeeData))
                {
                    returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                }

                retrunHeMuSaleActiveList.add(returnSaleActive);
            }
        }

        return retrunHeMuSaleActiveList;
    }

    /**
     * 和目营销活动校验接口(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkHeMuSaleActiveIntf(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IDataUtil.chkParam(input, "HEMU_SALE_ACTIVE_ID");
    	IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");

    	IData result = new DataMap();

    	String heMuSaleActivePackageId = input.getString("HEMU_SALE_ACTIVE_ID");

    	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, heMuSaleActivePackageId, null);

    	if (IDataUtil.isNotEmpty(saleActiveList))
    	{
    		input.put("PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));
    		input.put("PACKAGE_ID", heMuSaleActivePackageId);

    		CSAppCall.call("SS.MergeWideUserCreateSVC.checkHeMuSaleActive", input);
    	}
    	else
    	{
    		CSAppException.appError("201800002", "该营销活动["+heMuSaleActivePackageId+"]参数配置信息不存在，请联系管理员！");
    	}

    	result.put("RESULT_CODE", "0");

    	return result;
    }

    /**
     * 和目营销活动校验接口(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkHeMuTerminalIntf(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "HEMU_SALE_ACTIVE_ID");
    	IDataUtil.chkParam(input, "HEMU_RES_ID");
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");

    	IData result = new DataMap();

    	String heMuSaleActivePackageId = input.getString("HEMU_SALE_ACTIVE_ID");

    	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, heMuSaleActivePackageId, null);

    	if (IDataUtil.isNotEmpty(saleActiveList))
    	{
    		input.put("HEMU_SALE_ACTIVE_PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));

    		CSAppCall.call("SS.MergeWideUserCreateSVC.checkHeMuTerminal", input);
    	}
    	else
    	{
    		CSAppException.appError("201800002", "该营销活动["+heMuSaleActivePackageId+"]配置信息不存在，请联系管理员！");
    	}

    	result.put("RESULT_CODE", "0");

    	return result;
    }

    /**
     * 和目营销活动单独受理接口(APP调用接口)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset acceptHeMuSaleAciveIntf(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "HEMU_SALE_ACTIVE_ID");
    	IDataUtil.chkParam(input, "HEMU_RES_ID");
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");

    	String heMuSaleActivePackageId = input.getString("HEMU_SALE_ACTIVE_ID");

    	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, heMuSaleActivePackageId, null);

    	if (IDataUtil.isNotEmpty(saleActiveList))
    	{
    		input.put("PRODUCT_ID",saleActiveList.getData(0).getString("PARA_CODE4"));
    		input.put("PACKAGE_ID", input.getString("HEMU_SALE_ACTIVE_ID"));
    		input.put("SALEGOODS_IMEI", input.getString("HEMU_RES_ID"));
    	}
    	else
    	{
    		CSAppException.appError("201800002", "该营销活动["+heMuSaleActivePackageId+"]配置信息不存在，请联系管理员！");
    	}

        IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        return result;
    }


    /**
     * IMS终端出库校验，并更新IMS受理信息临时表(提供给PBOSS校验)
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkIMSTerminal(IData input) throws Exception
    {
    	//
    	setVisitTradeStaff(input);

        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");

        String tradeId = input.getString("TRADE_ID");

        if (StringUtils.isBlank(resNo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "IMS固话终端编码为空!");
        }

        if (StringUtils.isBlank(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TRADE_ID参数为空!");
        }

        //在录入串号时，系统自动前后去掉空格
        resNo = resNo.trim();

        IDataset imsInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSTRADE");

        if (IDataUtil.isNotEmpty(imsInfos))
        {
            IData imsInfo = imsInfos.first();

            //扩展字段1存放的手机号码
            String serialNumber = imsInfo.getString("RSRV_STR1");

            //调用华为接口进行终端查询校验
            IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);

            if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
            {
                IData res = retDataset.first();

                retData.put("X_RESULTCODE", "0");
                retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));

                //将终端校验信息设置到IMS临时受理信息中
                imsInfo.put("RSRV_STR10", resNo);
                imsInfo.put("RSRV_STR11", res.getString("DEVICE_MODEL_CODE", ""));
                imsInfo.put("RSRV_STR12", res.getString("DEVICE_MODEL", ""));
                imsInfo.put("RSRV_STR13", res.getString("DEVICE_COST","0"));
                imsInfo.put("RSRV_STR14", res.getString("DEVICE_BRAND_CODE"));
                imsInfo.put("RSRV_STR15", res.getString("DEVICE_BRAND"));
                imsInfo.put("RSRV_STR16", res.getString("SUPPLY_COOP_ID", ""));
                imsInfo.put("RSRV_STR17", res.getString("TERMINAL_TYPE_CODE", ""));

                imsInfo.put("TRADE_ID", tradeId);
                imsInfo.put("RSRV_VALUE_CODE", "IMSTRADE");
                //更新TF_B_TRADE_OTHER表
                Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TRADE_OTHER", imsInfo, Route.getJourDb());
            }
            else
            {
                String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID查询不到IMS固话业务受理信息!");
        }

        return retData;
    }

    /**
     * IMS固话开通处理
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset sureCreateIMSTrade(IData input) throws Exception
    {
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
            // 手机号码不能为空
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"服务号码不能为空！");
        }
        if (StringUtils.isEmpty(reversion))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户回复信息不能为空!");
        }

        //宽带开户、IMS固话开户，在施工环节回单后，业务办理工号变成施工人员的工号，影响店员积分、营业员量酬、渠道酬金
        IDataset mainTradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
        IData mainTradeInfo = mainTradeInfos.getData(0);
        String tradeStaffId = mainTradeInfo.getString("TRADE_STAFF_ID","");
        String tradeCityCode = mainTradeInfo.getString("TRADE_CITY_CODE","");
        String tradeDepartId = mainTradeInfo.getString("TRADE_DEPART_ID","");
        CSBizBean.getVisit().setStaffId(tradeStaffId);
        CSBizBean.getVisit().setCityCode(tradeCityCode);
        CSBizBean.getVisit().setDepartId(tradeDepartId);
        //end

        IDataset imsInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSTRADE");
        IDataset imsCustInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "IMSCUSTINFO");


        if (IDataUtil.isNotEmpty(imsInfos))
        {
            IData imsInfo = imsInfos.first();

            String rsrvTag1 =   imsInfo.getString("RSRV_TAG1");

            //如果不是初始状态，说明用户已经回复过了，不能再进行回复
            if (!"0".equals(rsrvTag1))
            {
                CSAppException.appError("101008","您已经回复["+rsrvTag1+"],不能再次回复!");
            }


            //Y-为同意；其它不同意
            if ("Y".equals(reversion.trim().toUpperCase()))
            {
                IData tradeInput = new DataMap();

                tradeInput.put("SERIAL_NUMBER", imsInfo.getString("RSRV_STR1"));
                tradeInput.put("WIDE_SERIAL_NUMBER",imsInfo.getString("RSRV_STR3"));

                tradeInput.put("PRODUCT_ID", imsInfo.getString("RSRV_STR4"));

                tradeInput.put("PRODUCT_NAME", imsInfo.getString("RSRV_STR5"));
                tradeInput.put("WIDE_PRODUCT_NAME", imsInfo.getString("RSRV_STR6"));

                tradeInput.put("USERM_PASSWD", imsInfo.getString("RSRV_STR19"));
                tradeInput.put("USER_PASSWD", imsInfo.getString("RSRV_STR20"));

                //融合宽带开户调用不需要再次进IMS固话号码预占
                tradeInput.put("IS_MERGE_WIDE_USER_CREATE", "1");

                tradeInput.put("TRADE_TYPE_CODE", "6900");
                tradeInput.put("ORDER_TYPE_CODE", "6900");

            	// IMS固话产品必选或者默认的元素
        		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, tradeInput.getString("PRODUCT_ID"), "1", "1");
        		forceElements = ProductUtils.offerToElement(forceElements, input.getString("IMS_PRODUCT_ID"));

        		IDataset selectedelements = new DatasetList();

            	if (IDataUtil.isNotEmpty(forceElements))
            	{
            		String sysDate = SysDateMgr.getSysTime();

            		for (int i = 0; i < forceElements.size(); i++)
            		{
            			IData element = new DataMap();
            	        element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
            	        element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
            	        element.put("PRODUCT_ID", tradeInput.getString("PRODUCT_ID"));
            	        element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
            	        element.put("MODIFY_TAG", "0");
            	        element.put("START_DATE", sysDate);
            	        element.put("END_DATE", "2050-12-31");
            	        selectedelements.add(element);
    				}
            	}

            	tradeInput.put("SELECTED_ELEMENTS", selectedelements.toString());

                //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
            	if (IDataUtil.isNotEmpty(imsCustInfos))
                {
                    IData imsCustInfo = imsCustInfos.first();
                    tradeInput.put("CUST_NAME", imsCustInfo.getString("RSRV_STR1"));
                    tradeInput.put("PSPT_TYPE_CODE", imsCustInfo.getString("RSRV_STR2"));
                    tradeInput.put("PSPT_ID", imsCustInfo.getString("RSRV_STR3"));
                    tradeInput.put("PSPT_END_DATE", imsCustInfo.getString("RSRV_STR4"));
                    tradeInput.put("PSPT_ADDR", imsCustInfo.getString("RSRV_STR5"));
                    tradeInput.put("SEX", imsCustInfo.getString("RSRV_STR6"));
                    tradeInput.put("BIRTHDAY", imsCustInfo.getString("RSRV_STR7"));
                    tradeInput.put("POST_ADDRESS", imsCustInfo.getString("RSRV_STR8"));
                    tradeInput.put("POST_CODE", imsCustInfo.getString("RSRV_STR9"));
                    tradeInput.put("PHONE", imsCustInfo.getString("RSRV_STR10"));
                    tradeInput.put("FAX_NBR", imsCustInfo.getString("RSRV_STR11"));
                    tradeInput.put("EMAIL", imsCustInfo.getString("RSRV_STR12"));
                    tradeInput.put("HOME_ADDRESS", imsCustInfo.getString("RSRV_STR13"));
                    tradeInput.put("WORK_NAME", imsCustInfo.getString("RSRV_STR14"));
                    tradeInput.put("WORK_DEPART", imsCustInfo.getString("RSRV_STR15"));
                    tradeInput.put("CONTACT", imsCustInfo.getString("RSRV_STR16"));
                    tradeInput.put("CONTACT_PHONE", imsCustInfo.getString("RSRV_STR17"));
                    tradeInput.put("AGENT_CUST_NAME", imsCustInfo.getString("RSRV_STR18"));
                    tradeInput.put("AGENT_PSPT_TYPE_CODE", imsCustInfo.getString("RSRV_STR19"));
                    tradeInput.put("AGENT_PSPT_ID", imsCustInfo.getString("RSRV_STR20"));
                    tradeInput.put("AGENT_PSPT_ADDR", imsCustInfo.getString("RSRV_STR21"));
                    tradeInput.put("legalperson", imsCustInfo.getString("RSRV_STR22"));
                    tradeInput.put("startdate", imsCustInfo.getString("RSRV_STR23"));
                    tradeInput.put("termstartdate", imsCustInfo.getString("RSRV_STR24"));
                    tradeInput.put("termenddate", imsCustInfo.getString("RSRV_STR25"));
                    tradeInput.put("CALLING_TYPE_CODE", imsCustInfo.getString("RSRV_STR26"));
                    tradeInput.put("USE", imsCustInfo.getString("RSRV_STR27"));
                    tradeInput.put("USE_PSPT_TYPE_CODE", imsCustInfo.getString("RSRV_STR28"));
                    tradeInput.put("USE_PSPT_ID", imsCustInfo.getString("RSRV_STR29"));
                    tradeInput.put("USE_PSPT_ADDR", imsCustInfo.getString("RSRV_STR30"));
                    tradeInput.put("HAS_REAL_NAME_INFO", "HAS_REAL_NAME_INFO");
                }else {
                	//标识为增加实名制校验之前,按增加实名制校验之前逻辑处理
                	tradeInput.put("BEFORE_REAL_NAME_CHECK", "BEFORE_REAL_NAME_CHECK");
                }
            	//REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx end

            	tradeInput.put("TT_TRANSFER", imsInfo.getString("RSRV_TAG10"));

                IDataset imsResult = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", tradeInput);


                imsInfo.put("RSRV_TAG1", "Y");
                imsInfo.put("REMARK", "客户确认开通魔百和业务");
                String imsOrderId = imsResult.getData(0).getString("ORDER_ID","");
                String imsTradeId = imsResult.getData(0).getString("TRADE_ID","");
                imsInfo.put("RSRV_STR28", imsOrderId);
                imsInfo.put("RSRV_STR29", imsTradeId);

                Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TRADE_OTHER", imsInfo, Route.getJourDb());

                String imsSaleActiveTradeId = imsInfo.getString("RSRV_STR30","");

                //
            	if (StringUtils.isNotBlank(imsSaleActiveTradeId))
                {
            		insertIMSSaleGoodInfo(mainTradeInfo, imsInfo);
                }

                result.put("TRADE_ID", imsResult.getData(0).getString("TRADE_ID"));
                result.put("X_RESULTCODE", "0");
                result.put("X_RECORDNUM", "1");
                result.put("X_RESULTINFO", "OK");
            }
            else
            {
            	String imsSaleActiveTradeId = imsInfo.getString("RSRV_STR30","");

                //用户确认取消ims固话同时需要返销营销活动等操作
            	if (StringUtils.isNotBlank(imsSaleActiveTradeId))
                {
                    //客户确认不开通ims固话，需要对营销活动返销处理
                    cancelSaleActiveTrade(tradeId, imsSaleActiveTradeId, serialNumber);

                    String imsResNo = imsInfo.getString("RSRV_STR10","");

                    //如果IMS固话终端已经出库，则需要释放
                    if (StringUtils.isNotBlank(imsResNo))
                    {
                    	IData data = new DataMap();

        				data.put("RES_NO", imsResNo);
        	    		data.put("SERIAL_NUMBER", serialNumber);

        	    		//释放IMS固话终端预占
        	    		HwTerminalCall.releaseResTempOccupy(data);
                    }
                }

        		// 释放固话号码预占
        		ResCall.releaseAllResByNo(imsInfo.getString("RSRV_STR3"), "0", tradeId + "IMS订单取消", "TRADE_CANCEL");


            	imsInfo.put("RSRV_TAG1", "N");
            	imsInfo.put("REMARK", "客户确认不开通IMS固话业务");
                //更新TF_B_TRADE_OTHER表
                int upNum = Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TRADE_OTHER", imsInfo, Route.getJourDb());

                result.put("X_RESULTCODE", "0");
                result.put("X_RECORDNUM", upNum);
                result.put("X_RESULTINFO", "OK");
            }
        }
        else
        {
        	//6800工单
        	IData tradeInfos = TradeInfoQry.queryTradeByTradeIdAndTradeType(tradeId,"6800");
        	if(IDataUtil.isNotEmpty(tradeInfos))
        	{
        		IData cancelIMS = new DataMap();
        		cancelIMS.put("TRADE_ID", tradeId);
        		cancelIMS.put("SERIAL_NUMBER", serialNumber);
        		cancelIMS.put("CANCEL_TYPE", "1");
        		IDataset dataset = CSAppCall.call("SS.CancelIMSLandLineService.cancelTradeReg", cancelIMS);
        		result.put("X_RESULTCODE", "0");
                result.put("X_RECORDNUM", "1");
                result.put("X_RESULTINFO", "OK");
        	}
        	else
        	{
        		CSAppException.appError("101005","获取工单无数据");
			}
        }

        return IDataUtil.idToIds(result);
    }

    /**
     * ims固话赠送类营销活动，固话终端插入SaleGood表
     * @param mianTrade
     * @param imsInfo
     * @throws Exception
     */
    private void insertIMSSaleGoodInfo(IData mianTrade, IData imsInfo) throws Exception
    {
    	//
        if(StringUtils.isNotBlank(imsInfo.getString("RSRV_STR10","")))
        {
        	IData param = new DataMap();
        	param.put("USER_ID", imsInfo.getString("RSRV_STR2"));
        	param.put("SERIAL_NUMBER_B", "");
        	param.put("PRODUCT_ID", imsInfo.getString("RSRV_STR7"));
        	param.put("PACKAGE_ID", imsInfo.getString("RSRV_STR8"));
        	param.put("INST_ID", SeqMgr.getInstId());
        	param.put("CAMPN_ID", "");

        	String goodsID = "99032303";
        	IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "1887", imsInfo.getString("RSRV_STR8"), "0898");
        	if(IDataUtil.isNotEmpty(commparaInfos))
        	{
        		goodsID = commparaInfos.getData(0).getString("PARA_CODE1", "");
        	}
    		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("G", goodsID, "");
    		param.put("GOODS_ID", goodsID);
    		if(IDataUtil.isNotEmpty(returnResult))
    		{
    			param.put("GOODS_NAME", returnResult.getData(0).getString("OFFER_NAME"));
    		}else
    		{
    			param.put("GOODS_NAME", "家庭IMS固话预存送机");
    		}
        	param.put("GOODS_NUM", "1");
        	param.put("GOODS_VALUE", "0");
        	param.put("GOODS_STATE", "0");
        	param.put("RES_TAG", "1");
        	param.put("RES_TYPE_CODE", "4");
        	param.put("RES_ID", "");
        	param.put("RES_CODE", imsInfo.getString("RSRV_STR10"));
        	param.put("DEVICE_MODEL_CODE", imsInfo.getString("RSRV_STR11",""));
        	param.put("DEVICE_MODEL", imsInfo.getString("RSRV_STR12",""));
        	param.put("DEVICE_COST", imsInfo.getString("RSRV_STR13",""));
        	param.put("DEVICE_BRAND_CODE", imsInfo.getString("RSRV_STR14",""));
        	param.put("DEVICE_BRAND", imsInfo.getString("RSRV_STR15",""));
        	param.put("DESTROY_FLAG", "0");
        	param.put("GIFT_MODE", "0");
        	param.put("POST_NAME", "");
        	param.put("POST_ADDRESS", "");
        	param.put("POST_CODE", "");

        	//获取预受理的230工单
            param.put("RELATION_TRADE_ID", imsInfo.getString("RSRV_STR30",""));

        	param.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	param.put("CANCEL_DATE", "2050-12-31 23:59:59");
        	param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	param.put("UPDATE_STAFF_ID", mianTrade.getString("TRADE_STAFF_ID", "SUPERUSR"));
        	param.put("UPDATE_DEPART_ID", mianTrade.getString("TRADE_DEPART_ID", "36601"));
        	param.put("REMARK", "家庭IMS固话出库");
        	param.put("RSRV_NUM1", "1");
        	param.put("RSRV_NUM2", "0");
        	param.put("RSRV_NUM3", "0");
        	param.put("RSRV_NUM4", "");
        	param.put("RSRV_NUM5", "");
        	param.put("RSRV_STR1", imsInfo.getString("RSRV_STR16",""));
        	param.put("RSRV_STR2", "");
        	param.put("RSRV_STR3", "");
        	param.put("RSRV_STR4", "");
        	param.put("RSRV_STR5", "");
        	param.put("RSRV_STR6", "0");
        	param.put("RSRV_STR7", "0");
        	param.put("RSRV_STR8", "");
        	param.put("RSRV_STR9", imsInfo.getString("RSRV_STR17",""));
        	param.put("RSRV_STR10", "YX02");
        	param.put("RSRV_DATE1", "");
        	param.put("RSRV_DATE2", "");
        	param.put("RSRV_DATE3", "");
        	param.put("RSRV_TAG1", "");
        	param.put("RSRV_TAG2", "");
        	param.put("RSRV_TAG3", "");

        	UserSaleGoodsInfoQry.insertIMSTopsetboxOnline(param);
        }
	}


	 /**
     * 开户短信确认码接口 （提供APP调用）
     * @param input
     * @return
     * @throws Exception
     * xuzh5 2018-7-13 15:33:38
     */
    public IData checkSmsVerifyCode(IData input) {

     IData result = new DataMap();
     String sms_verify_code_tmpalet = "尊敬的客户，您的验证码为：VERIFY_CODE,有效期10分钟。中国移动。";
   	 String sms_verify_code_widenet_tmpalet = "尊敬的客户，您好，您办理的TRADE_TYPE确认码为:VERIFY_CODE,有效期5分钟。中国移动。";
   	 String verify_code_cache_key = "com.ailk.csservice.person.busi.passwdmgr.SmsVerifyCodeBean_";
   	 String result_code="0";
   	 String result_message="success";
    	try{
	    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
	    	IDataUtil.chkParam(input, "OPERAT_FLG");

	    	 String serialNumber=input.getString("SERIAL_NUMBER");
	    	 String tradeTypeCode=input.getString("OPERAT_FLG");

	    	 //生成短信验证码
    			String verifyCode = RandomStringUtils.randomNumeric(6);

    			String msg = sms_verify_code_tmpalet.replaceAll("VERIFY_CODE", verifyCode);
    			String remark = "";
    			int validMinutes = 10;
    			if ("600".equals(tradeTypeCode) || "4800".equals(tradeTypeCode) || "6800".equals(tradeTypeCode) || "606".equals(tradeTypeCode))
    			{
    				String tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);

    				msg = sms_verify_code_widenet_tmpalet.replaceAll("VERIFY_CODE", verifyCode);
    				if("6800".equals(tradeTypeCode))
    					tradeTypeName="和家固话开户";
    				msg = msg.replaceAll("TRADE_TYPE", tradeTypeName);
    				remark = "宽带产品开户确认码";
    				validMinutes = 5;
    				//发送短信通知
        			IData inparam = new DataMap();
        	        inparam.put("NOTICE_CONTENT", msg);
        	        inparam.put("RECV_OBJECT", serialNumber);
        	        inparam.put("RECV_ID", serialNumber);
        	        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
        	        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
        	        inparam.put("REMARK", remark);
        	        SmsSend.insSms(inparam);
        	        //保存短信验证码
        	        SharedCache.set(verify_code_cache_key+serialNumber, verifyCode, 60*validMinutes);
    			}else{
    				result_code="2998";
    			    result_message="开户确认码的OPERAT_FLG应该为：600或4800或6800的标识";
    			}
			}catch(Exception e){
				result_code="2998";
				result_message=e.getMessage();
			 }

    	        result.put("X_RESULTCODE", result_code);
    	        result.put("X_RESULTMESSAGE", result_message);

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

	/**
     * REQ201810090021智能组网终端出库接口
	 * @param input
	 * @throws Exception
	 * @author yanghb6
     * @date 2018-11-20
     */
	public IData insertMergeWideTerm(IData input) {
		IData result = new DataMap();
		String result_code="0";
		String result_message="success";
		try {
			IDataUtil.chkParam(input, "TERM_STR");
			IDataUtil.chkParam(input, "TRADE_ID");
			IDataUtil.chkParam(input, "SERIAL_NUMBER");

			String termStr=input.getString("TERM_STR");
			String serialNumber = input.getString("SERIAL_NUMBER");
			String tradeId = input.getString("TRADE_ID");
			String userId = input.getString("USER_ID");
			if(StringUtils.isEmpty(userId)) {
				IDataset trades = TradeInfoQry.getMainTradeByTradeId(tradeId);
				if(IDataUtil.isNotEmpty(trades)) {
					IData trade = trades.getData(0);
					userId = trade.getString("USER_ID");
				}
			}
			String [] termArr = termStr.split(";");
			if(termArr.length > 0) {
				for(int i = 0; i < termArr.length; i++) {
					if(StringUtils.isNotBlank(termArr[i])) {
						IData param = new DataMap();
						String [] termInfoArr = termArr[i].split(",");  // 品牌，机型，串号；
						param.put("USER_ID", userId);
						param.put("RSRV_VALUE_CODE", "ZNZW");
						param.put("SERIAL_NUMBER", serialNumber);
						param.put("RSRV_STR1", termInfoArr[2]); // 串号
						param.put("RSRV_STR2", tradeId);
						param.put("RSRV_STR3", termInfoArr[0]); // 品牌
						param.put("RSRV_STR4", termInfoArr[1]); // 机型
						param.put("START_DATE", SysDateMgr.getSysTime());
						param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
						param.put("REMARK", "智能组网终端出库");
						param.put("INST_ID", SeqMgr.getInstId());
						UserOtherInfoQrySVC.insertMergeWideTerm(param);
					}
				}
			}
		}catch(Exception e){
			result_code="3000";
			result_message=e.getMessage();
		}

		result.put("X_RESULTCODE", result_code);
        result.put("X_RESULTMESSAGE", result_message);

        return result;
	}

	/**
	 * 更新无手机宽带工单是否缴费
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData updateTradePayInfo(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "PAY_TAG");//缴费标识 0失败 1成功
        IDataUtil.chkParam(input, "PAY_MONEY");//缴费金额
        IDataUtil.chkParam(input, "BUSINESS_ID");//流水号

        String sn = input.getString("SERIAL_NUMBER");

        if(!sn.startsWith("KD_")){
        	sn="KD_"+sn;
        }
        IDataset tradeList = BroadBandInfoQry.qryTradeHisInfoBySerialNumber(sn);
        IData resultData=new DataMap();
        try{
	        if(IDataUtil.isNotEmpty(tradeList)){
	        	//更新是否缴费
	        	IDataset otherFees=UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(tradeList.getData(0).getString("USER_ID"),"NOPHONE_WNET_PAY_TAG");
	        	BroadBandInfoQry.updateTradePayInfo(otherFees,tradeList,input);
	        	//缴费成功调复开更新
	        	if("1".equals(input.getString("PAY_TAG"))){
	        		IData pfParam=new DataMap();
	        		pfParam.put("TRADE_ID", tradeList.getData(0).getString("TRADE_ID"));
	        		IDataOutput dataOutput =CSAppCall.callNGPf("PF_ORDER_UPDPAYSTATUS",pfParam);
	        		IDataset result = dataOutput.getData();
	        		if (IDataUtil.isNotEmpty(result)){
	        			 if(!("0".equals(result.getData(0).getString("result_code")))){
	        				 throw new Exception(result.getData(0).getString("result_rsg","调用复开更新缴费标识失败"));
	        			 }
	                }
	        	}
	        	resultData.put("code", "0");
	        	resultData.put("result", "success");
	        }else{
	        	resultData.put("code", "-1");
	        	resultData.put("result", "该手机号["+sn+"]无手机宽带开户工单信息不存在！");
	        }
        }catch(Exception e){
        	resultData.put("code", "-1");
        	resultData.put("result", e.toString());
        	throw e;
        }
        return resultData;
    }
 /**
	 * 检验是否可以办理高价值活动
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData checkHightUserInfo(IData input) throws Exception
    {
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
    	result.put("DEVICE_ID_HIGHT", "0");
    	result.put("RAT_HIGHT", "0");
        result.put("X_RESULTMESSAGE", "成功！");

    	String wideProductId = input.getString("WIDE_PRODUCT_ID");
    	String deviceId = input.getString("DEVICE_ID");

    	String rat = WideNetUtil.getWidenetProductRate(wideProductId);
    	if(StringUtils.isNotBlank(rat)){
        	if(Integer.parseInt(rat)>=102400){
        		result.put("RAT_HIGHT", "1");
        	}

        }


        IData params = new DataMap();
        params.put("DEVICE_ID", deviceId);
    	if(UserOtherInfoQry.IsHightDevice(params))
    	{
    		result.put("DEVICE_ID_HIGHT", "1");
    	}



    	return result;
    }
    /**
     *  校验营销活动
     * @param input
     * @return
     * @throws Exception
     * @author liangdg3
     */
    public IData checkTimeLimitedSaleActive(IData input) throws Exception{
        String timeLimitedSaleActive = input.getString("TIME_LIMITED_SALE_ACTIVE");
        IDataset saleActiveIDataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "178", timeLimitedSaleActive, getTradeEparchyCode());
        if(IDataUtil.isNotEmpty(saleActiveIDataset)){
            input.put("PRODUCT_ID",saleActiveIDataset.getData(0).getString("PARA_CODE1"));
            input.put("PACKAGE_ID", saleActiveIDataset.getData(0).getString("PARA_CODE2"));
            //标记是宽带开户营销活动
            input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

            // 预受理校验，不写台账
            input.put("PRE_TYPE",  BofConst.PRE_TYPE_CHECK);
            input.put("TRADE_TYPE_CODE", "240");

            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
        }
        return input;
    }

 // 外线处理：魔百和拆机接口  党林涛需求
    public IData topSetBoxBackSell(IData input) throws Exception {
        String tradeId = IDataUtil.chkParam(input, "TRADE_ID");
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String isReturn = input.getString("IS_RETURN_TOPSETBOX", "");

        IData mainTradeInfo = null;
        IDataset mainTradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isEmpty(mainTradeInfos)) {
            mainTradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", this.getTradeEparchyCode());
        } else {
            mainTradeInfo = mainTradeInfos.getData(0);
        }

        String tradeStaffId = mainTradeInfo.getString("TRADE_STAFF_ID", "");
        String tradeCityCode = mainTradeInfo.getString("TRADE_CITY_CODE", "");
        String tradeDepartId = mainTradeInfo.getString("TRADE_DEPART_ID", "");
        CSBizBean.getVisit().setStaffId(tradeStaffId);
        CSBizBean.getVisit().setCityCode(tradeCityCode);
        CSBizBean.getVisit().setDepartId(tradeDepartId);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        IDataset boxInfo = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
        if (IDataUtil.isEmpty(boxInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有有效的魔百和信息，无法办理该业务！");
        }

        //获取魔百和营销活动，如果有返销掉
        cancelTopSetBoxAction(tradeId);

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("IS_RETURN_TOPSETBOX", isReturn);
        params.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		//施工人员工号和部门编码
		params.put("WORK_STAFF_ID",input.getString("TRADE_STAFF_ID", ""));
		params.put("WORK_DEPART_ID",input.getString("TRADE_DEPART_ID", ""));
		params.put("WORK_CITY_CODE", input.getString("TRADE_CITY_CODE", ""));

        IDataset results = CSAppCall.call("SS.DestroyTopSetBoxRegSVC.tradeReg", params);

        IData result = results.first();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");

        return result;
    }


}
