
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.trade;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.RuleException;
import com.asiainfo.veris.crm.order.pub.exception.SalegoodsException;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ScoreExchangeRequestData;

public class ScoreExchangeTrade extends BaseTrade implements ITrade
{
    /**
     * 兑换有价卡时的业务处理
     * 
     * @param inparam
     * @param data
     * @param reqData
     * @return
     * @throws Exception
     */
    public void cardExchange(IData inparam, IData data, ScoreExchangeRequestData reqData, BusiTradeData btd) throws Exception
    {
        DeviceTradeData deviceTD = new DeviceTradeData();
        List<String> valueCardNos = reqData.getValueCardNos();
        Iterator<String> it = valueCardNos.iterator();
        while (it.hasNext())
        {
            String cardId = it.next();
            IDataset cards = ResCall.iGetValueCardInfo(cardId, cardId, "330");
            if (IDataUtil.isEmpty(cards))
            {
                // 有价卡不存在或已销售！
                CSAppException.apperr(CrmCardException.CRM_CARD_7);
            }
            IData cardtemp = cards.getData(0);
            deviceTD.setFeeTypeCode("0"); // 营业费用类型，待确认 
            deviceTD.setDeviceTypeCode(cardtemp.getString("RES_KIND_CODE")); // 大类型
            deviceTD.setDeviceNoS(cardId);// 卡开始编号
            deviceTD.setDeviceNoE(cardId);// 卡结束编号
            deviceTD.setDeviceNum("1"); // 数量
            deviceTD.setDevicePrice(cardtemp.getString("ADVISE_PRICE")); // 卡的单价
            deviceTD.setSalePrice("0"); // 实售价格
            deviceTD.setRsrvStr1(cardtemp.getString("RES_TYPE_CODE")); // 小类型

            IDataset result = ResCall.resValueCardCheck(cardId);
            if (IDataUtil.isNotEmpty(result))
            {
                deviceTD.setRsrvStr9("55560301");
            }
            else
            {
                deviceTD.setRsrvStr9("S000V927");
            }
            btd.add(reqData.getUca().getSerialNumber(), deviceTD);
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) btd.getRD();
        // 查用户积分
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }

        int scoreTotal = 0;
        List<ExchangeData> exList = reqData.getExchangeDatas();
        separateBunch(exList);
        IData inparam = getCommonParam(btd);
        int userScore = 0;
        String aviationFlag = "";
        String cardFlag = "";
        String reqID = "";//请求业务流水号  接口积分兑换等值电子券
        String actionID = "";//活动编号 接口积分兑换等值电子券
        String proID = "";//券别编号 接口积分兑换等值电子券


        Iterator<ExchangeData> it = exList.iterator();
        int sumFee = 0;// 费用台账合并费用
                
        boolean TypeIsEMZ = true ;
        
        while (it.hasNext())
        {

            // 获取兑换列表后的解释
            IData tempData = new DataMap();
            ExchangeData exchangedata = it.next();
            int tempCount = Integer.parseInt(exchangedata.getCount());
			IData ruleData = new DataMap();
            
            if (IDataUtil.isNotEmpty(scoreInfo.getData(0)))
            {
                userScore = scoreInfo.getData(0).getInt("SUM_SCORE");
            }
            //REQ201410100016积分兑换和包电子券业务系统需求        根据电子券金额造积分规则数据
            if(!"".equals(exchangedata.getEvalue()) && exchangedata.getEvalue()!=null && Integer.parseInt(exchangedata.getEvalue()) > 0)
            {
            	 ruleData = queryByEvalue(exchangedata.getRuleId(),exchangedata.getEvalue(),userScore);
            }
            //REQ201703030013  积分兑换和包电子券业务系统需求        根据电子券金额造积分规则数据  为了不影响其他接口，新增逻辑判断   add by duhj
            else if(!"".equals(exchangedata.getHbevalue()) && exchangedata.getHbevalue()!=null && Integer.parseInt(exchangedata.getHbevalue()) > 0)
            {
            	reqID=exchangedata.getReqId();//请求业务流水号
            	actionID=exchangedata.getActionId();//活动编号
            	proID=exchangedata.getProId();//券别编号

           	 ruleData = queryByHbEvalue(exchangedata.getRuleId(),exchangedata.getHbevalue(),userScore);
           	 ruleData.put("ACTION_ID", actionID);
           	 ruleData.put("PRO_ID", proID);

            }
            
            else
            {
            	 ruleData = queryByRuleId(exchangedata.getRuleId(), reqData.getUca().getUser().getEparchyCode());
            }
            tempData.putAll(ruleData);
            tempData.put("COUNT", tempCount);

            int tempScore = 0;
            // 积分兑换规则param_code下需要发送到E拇指
            IDataset paramDs = CommparaInfoQry.getCommPkInfo("CSM", "97", tempData.getString("RULE_ID"), this.getTradeEparchyCode());
            if (IDataUtil.isEmpty(paramDs))
            {
                tempScore = tempData.getInt("SCORE");                
				TypeIsEMZ = false ;
			}
                       
            int temValue = tempData.getInt("REWARD_LIMIT");
            int temTotal = tempCount * tempScore;
            int temVaTotal = temValue * tempCount;

            String exchgGiftCode = tempData.getString("GIFT_TYPE_CODE", "");
            String exchgType = tempData.getString("EXCHANGE_TYPE_CODE", "");
            tempData.put("SCORE_VALUE", temTotal);
            tempData.put("VALUE_CHANGED_SUB", temVaTotal);
            scoreTotal += temTotal;


            if (scoreTotal > userScore)
            {
                // 用户积分为[%s],所需积分最少为[%s],不足本次兑换！
                CSAppException.apperr(CrmUserException.CRM_USER_712, userScore, scoreTotal);
            }
            tradeScoreData(tempData, String.valueOf(userScore), btd); // 组织积分台账数据

            if (ScoreFactory.EXCHANGE_TYPE_REWARD.equals(exchgType))
            { // 兑换实物
                continue;
            }
            
            if (ScoreFactory.EXCHANGE_TYPE_MOVIES.equals(exchgType))
            { // 兑换平台观影体育等电子券
                continue;
            }

            if (ScoreFactory.EXCHANGE_TYPE_FEE.equals(exchgType))
            { // 兑换话费
                sumFee += temVaTotal;
                continue;
            }

            if (ScoreFactory.EXCHANGE_TYPE_DISCNT.equals(exchgType))
            {
                // 兑换优惠
                discntExchange(inparam, tempData, reqData, btd);
                continue;
            }

            if (ScoreFactory.EXCHANGE_TYPE_CARD.equals(exchgType) && StringUtils.isBlank(exchgGiftCode))
            {
                cardFlag = "cards";
                // 兑换有价卡
                cardExchange(inparam, tempData, reqData, btd);
                continue;
            }
            if (ScoreFactory.EXCHANGE_TYPE_HH.equals(exchgType))
            {
                // 兑换海航金鹏里程
                String cardId = reqData.getHhCardId();
                String reg = "^[0-9]{10}$";
                if ((!cardId.matches(reg)))
                {
                    // 金鹏会员卡卡号为10位数字，请核实是否正确！
                    CSAppException.apperr(CrmUserException.CRM_USER_1094);
                }
                String cardName = reqData.getHhCardName();
                if (StringUtils.isEmpty(cardName))
                {
                    // 请输入金鹏会员卡姓名！
                    CSAppException.apperr(CrmUserException.CRM_USER_1095);
                }
                aviationFlag = ScoreFactory.EXCHANGE_TYPE_HH;
            }
            if (ScoreFactory.EXCHANGE_TYPE_JLF_GOODS.equals(exchgType))
            {
                // 兑换礼品卷
                ticketsExchange(inparam, tempData, reqData, btd);
                continue;
            }
        }
        // 赠送费用台帐 只记一条
        if (sumFee > 0)
        {
            feeExchange(reqData, String.valueOf(sumFee), btd);
        }
        
        // 主台账
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(String.valueOf(userScore));// 用户积分
        mainList.setRsrvStr2(String.valueOf("-" + scoreTotal));// 总的积分异动值
        mainList.setRsrvStr3("ZZ");// 积分类型
        mainList.setRsrvStr4(aviationFlag);// 海航里程
        mainList.setRsrvStr5(cardFlag);// 有价卡
        mainList.setRsrvStr6(reqID);// 积分兑换等值和包电子券业务办理接口   请求业务流水号
        mainList.setRsrvStr7(actionID);// 积分兑换等值和包电子券业务办理接口   活动编号
        mainList.setRsrvStr8(proID);// 积分兑换等值和包电子券业务办理接口   券别编号
        
        if(TypeIsEMZ==true)
        {
        	mainList.setTradeTypeCode("3301");
        }
    }

    /**
     * 兑换优惠时的数据组织
     * 
     * @param inparam
     * @param data
     * @param reqData
     * @return
     * @throws Exception
     */
    public void discntExchange(IData inparam, IData data, ScoreExchangeRequestData reqData, BusiTradeData btd) throws Exception
    {
        // 判断是否重复选择优惠
        List<DiscntTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        Iterator<DiscntTradeData> it = list.iterator();
        while (it.hasNext())
        {
            DiscntTradeData tempData = it.next();
            if (data.getString("GIFT_TYPE_CODE").equals(tempData.getDiscntCode()))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_165, data.getString("GIFT_TYPE_CODE"));
            }
        }

        DiscntTradeData discntData = new DiscntTradeData();
        int count = Integer.parseInt(data.getString("COUNT"));
        int tempNum = Integer.parseInt(data.getString("FMONTHS", "1"));
        int num = tempNum;

        // 分散账期修改
        String fenabled_tag = data.getString("RSRV_STR9", "2"); // rsrv_str9(0:立即生效; 1:次日生效; 2:下帐期生效)

        String startDate = null;
        if ("0".equals(fenabled_tag))
        {
            startDate = reqData.getAcceptTime();
        }
        else if ("1".equals(fenabled_tag))
        {
            startDate = SysDateMgr.getTomorrowDate();
        }
        else if ("2".equals(fenabled_tag))
        {
            startDate = SysDateMgr.getDateNextMonthFirstDay(reqData.getAcceptTime());
        }
        String str1 = data.getString("RSRV_STR1", "");

        discntData.setModifyTag(BofConst.MODIFY_TAG_ADD); // 0-增加，1－删除，2－修改
        discntData.setStartDate(startDate);
        discntData.setUserIdA("-1"); // 具体填值有待确认
        discntData.setPackageId("-1"); // 包标识
        discntData.setProductId(reqData.getUca().getProductId());
        discntData.setSpecTag("0"); // 正常产品优惠
        discntData.setRelationTypeCode("0"); // 关系类型编码

        String endDate = SysDateMgr.endDateOffset(startDate.substring(0, 10), String.valueOf(num), "3");
        //RSRV_STR1=1按日算
        if("1".equals(str1)&&"3".equals(data.getString("EXCHANGE_TYPE_CODE", ""))){
        	if(tempNum==1){//日套餐按24小时算
        		num=tempNum * 24;
        		endDate=SysDateMgr.getAddHoursDate(startDate, num);
        	}else{//大于1天按自然日算
        		endDate = SysDateMgr.endDateOffset(startDate.substring(0, 10), String.valueOf(tempNum), "1");
        	}
        }

        discntData.setEndDate(endDate);
        discntData.setUserId(reqData.getUca().getUserId());
        discntData.setElementId(data.getString("GIFT_TYPE_CODE"));
        for (int i = 0; i < count; i++) {
            DiscntTradeData newDiscntData = new DiscntTradeData();
            newDiscntData = discntData.clone();

            String inst_id = SeqMgr.getInstId();
            newDiscntData.setInstId(inst_id); // 实例标识
            btd.add(reqData.getUca().getSerialNumber(), newDiscntData);
        }

    }

    /**
     * 兑换话费时的数据组织
     * 
     * @param inparam
     * @param data
     * @param reqData
     * @return
     * @throws Exception
     */
    public void feeExchange(ScoreExchangeRequestData reqData, String sumFee, BusiTradeData btd) throws Exception
    {
        String convertSerialNumber = null;
        String userId = null;
        String acctId = null;
        UcaData convertUca = reqData.getConvertUca();
        if (convertUca != null)
        {
            convertSerialNumber = convertUca.getSerialNumber();
            userId = convertUca.getUserId();
            acctId = convertUca.getAcctId();
        }
        GiftFeeTradeData feeData = new GiftFeeTradeData();
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(acctId) && StringUtils.isNotBlank(convertSerialNumber))
        {
            feeData.setUserId(userId);
            feeData.setChargeId(acctId);
           
            
        }
        else
        {
            feeData.setUserId(reqData.getUca().getUser().getUserId());
            feeData.setChargeId(reqData.getUca().getAccount().getAcctId());
           
           
        }
        feeData.setFeeMode(BofConst.FEE_MODE_ADVANCEFEE);// 费用类型 2：预存
        feeData.setFeeTypeCode("12");
        feeData.setFee(sumFee);
        
        btd.add(reqData.getUca().getSerialNumber(), feeData);
        if(StringUtils.isNotBlank(convertSerialNumber)){
            MainTradeData mainList = btd.getMainTradeData();
            mainList.setRsrvStr10(reqData.getConvertUca().getSerialNumber());// 转赠号码
        }
        
        
        
    }

    /**
     * 获取登记台账的公用参数
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    public IData getCommonParam(BusiTradeData btd) throws Exception
    {
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) btd.getRD();
        IData inparam = new DataMap(); // 存放公共数
        inparam.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        inparam.put("TRADE_TYPE_CODE", btd.getTradeTypeCode()); // 把这个放在这里是因为备份台账要用
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("CUST_ID", reqData.getUca().getUser().getCustId());
        inparam.put("ACCT_ID", reqData.getUca().getAccount().getAcctId());
        inparam.put("EXEC_TIME", btd.getRD().getAcceptTime()); // 执行时间：如果大于当前时间则不予处理
        inparam.put("REMARK", reqData.getRemark());

        return inparam;
    }

    /**
     * 根据规则ID查询规则
     * 
     * @param ruleId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IData queryByRuleId(String ruleId, String eparchyCode) throws Exception
    {
        IDataset ruleData = ExchangeRuleInfoQry.queryByRuleId(ruleId, eparchyCode);
        IData param = ruleData.size() == 1 ? ruleData.getData(0) : null;
        if (param == null)
        {
            CSAppException.apperr(RuleException.CRM_RULE_1);
        }
        return param;
    }
	//REQ201410100016积分兑换和包电子券业务系统需求
    public IData queryByEvalue(String rule_id , String evalue , int userscore) throws Exception
    {
    	
    	IData param = new DataMap();
    	
    	IDataset param1923 = CommparaInfoQry.getCommByParaAttr("CSM","1923",this.getTradeEparchyCode());
    	if (IDataUtil.isEmpty(param1923))
        {
    		CSAppException.apperr(ScoreException.CRM_SCORE_21);
        }
    	
    	String perscore = param1923.getData(0).getString("PARA_CODE1","0");
    	
    	if("0".equals(perscore))
    	{
    		CSAppException.apperr(ScoreException.CRM_SCORE_21);
    	}
    	
    	int needscore = Integer.parseInt(evalue) * Integer.parseInt(perscore) ;
    	
    	if(needscore > userscore)
    	{
    		CSAppException.apperr(ScoreException.CRM_SCORE_20,userscore,needscore); 
    	}
    	
    	param.put("REWARD_LIMIT","0");
    	param.put("SCORE",needscore);
    	param.put("GIFT_TYPE_CODE","");
    	param.put("EXCHANGE_TYPE_CODE","N");
    	param.put("RULE_ID",rule_id);
    	param.put("RULE_NAME","积分兑换和包电子券");
    	param.put("EVALUE",evalue);

        return param;
    }

	//REQ201703030013积分兑换和包电子券业务系统需求
    public IData queryByHbEvalue(String rule_id , String hbevalue , int userscore) throws Exception
    {
    	
    	IData param = new DataMap();
    	
    	IDataset param1924 = CommparaInfoQry.getCommByParaAttr("CSM","1924",this.getTradeEparchyCode());
    	if (IDataUtil.isEmpty(param1924))
        {
    		CSAppException.apperr(ScoreException.CRM_SCORE_21);
        }
    	
    	//hbevalue传值为分，  分转换为元，再用公式求得积分值 A=round(B/0.012,0) 积分值A等于B除以0.012的值进行四舍五入保留整数部分
    	// 所以最终为 hbevalue/1.2
    	int needscore =(int) Math.round(Integer.parseInt(hbevalue)/1.2); //待定 Integer.parseInt(evalue) * Integer.parseInt(perscore) ;
    	
    	if(needscore > userscore)
    	{
    		CSAppException.apperr(ScoreException.CRM_SCORE_20,userscore,needscore); 
    	}
    	
    	param.put("REWARD_LIMIT",hbevalue);
    	param.put("SCORE",needscore);
    	param.put("GIFT_TYPE_CODE","");
    	param.put("EXCHANGE_TYPE_CODE","N");
    	param.put("RULE_ID",rule_id);
    	param.put("RULE_NAME","积分兑换和包电子券");
    	param.put("HBEVALUE",hbevalue);

        return param;
    }
    
    /**
     * 数据转换
     * 
     * @param bunch
     * @return
     * @throws Exception
     */
    public void separateBunch(List<ExchangeData> exList) throws Exception
    {
        if (exList.size() <= 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_291);
        }
        Iterator<ExchangeData> it = exList.iterator();
        while (it.hasNext())
        {
            ExchangeData temp = it.next();
            if (temp == null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_454);
            }
            else
            {
                if (StringUtils.isBlank(temp.getCount()))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_326);
                }
                if (StringUtils.isBlank(temp.getRuleId()))
                {
                    CSAppException.apperr(ParamException.CRM_PARAM_193);
                }
            }
        }
    }

    /**
     * 兑换礼品券时的数据组织
     * 
     * @author huangsl
     * @param inparam
     * @param data
     * @param btd
     * @return
     * @throws Exception
     */
    public void ticketsExchange(IData inparam, IData data, ScoreExchangeRequestData reqData, BusiTradeData btd) throws Exception
    {
        OtherTradeData otherTD = new OtherTradeData();
        IDataset paramDs1 = CommparaInfoQry.getCommpara("CSM", "92", data.getString("RULE_ID", ""), this.getTradeEparchyCode());
        IDataset paramDs3 = CommparaInfoQry.getCommpara("CSM", "97", data.getString("RULE_ID", ""), this.getTradeEparchyCode());
        IDataset paramDs4 = CommparaInfoQry.getCommpara("CSM", "1001", data.getString("RULE_ID", ""), this.getTradeEparchyCode());
        if (paramDs1 != null && paramDs1.size() > 0)
        {
            IData tempData = paramDs1.getData(0);
            // --------------------组织other表台账-------------------------
            otherTD.setRsrvValue(inparam.getString("TRADE_ID"));
            otherTD.setRsrvStr1(tempData.getString("PARA_CODE1"));// callpf约定使用
            otherTD.setRsrvStr2(tempData.getString("PARA_CODE2"));// 接入系统号
            otherTD.setRsrvStr3(tempData.getString("PARA_CODE3"));// 业务商号
            otherTD.setRsrvStr4(tempData.getString("PARA_CODE4"));// 发送类型
            otherTD.setRsrvStr5(tempData.getString("PARA_CODE5"));// 凭证标题
            otherTD.setRsrvStr6(tempData.getString("PARA_CODE6"));// 连续密码错误最大次数
            otherTD.setRsrvStr7(tempData.getString("PARA_CODE7"));// 活动号
            otherTD.setRsrvStr8(tempData.getString("PARA_CODE8"));// 条码有效期(天)
            otherTD.setRsrvStr9(tempData.getString("PARA_CODE9"));// 活动可使用次数
            otherTD.setRsrvStr10(tempData.getString("PARA_CODE10"));// 活动可使用金额(元)
            otherTD.setRsrvStr11(tempData.getString("PARA_CODE11"));// 是否需要返回图片信息
            otherTD.setRsrvStr12(tempData.getString("PARA_CODE24"));// 活动打印内容
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER + SysDateMgr.END_DATE);// 条码结束使用时间默认2050年

            String strValidDays = tempData.getString("PARA_CODE8"); // 条码有效期(天)
            String chnDays = "";
            // 若存在有效天数，则以当前时间+有效天等到日期当天的最后一秒
            // 若不存在有效天数，则至2050 -12 -31 23 :59 :59
            if (StringUtils.isNotBlank(strValidDays))
            {
                int seconds = (60 * 60 * 24) * (new Integer(strValidDays));
                String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                strEndDate = strEndDate.substring(0, 10);
                strEndDate = strEndDate + SysDateMgr.END_DATE;
                otherTD.setEndDate(strEndDate); // 条码结束使用时间
                // 有效截止时间转换成中文日期格式
                chnDays = strEndDate.substring(0, 4) + "年" + strEndDate.substring(5, 7) + "月" + strEndDate.substring(8, 10) + "日";
            }

            String smsContent = tempData.getString("PARA_CODE22") + tempData.getString("PARA_CODE23");
            smsContent = smsContent.replace("%END_DATE!", chnDays);
            otherTD.setRsrvStr21(smsContent); // 短信内容

            // ---------判断下发的二位码是否包括彩信内容----------------------
            // 因为彩信的内容比较长，所以由一个单独的参数配置
            IDataset paramDs2 = CommparaInfoQry.getCommpara("CSM", "93", data.getString("RULE_ID"), this.getTradeEparchyCode());
            if (paramDs2 != null && paramDs2.size() > 0)
            {
                IData data2 = paramDs2.getData(0);
                String part1 = data2.getString("PARA_CODE20") + data2.getString("PARA_CODE23");
                otherTD.setRsrvStr25(part1.replace("%END_DATE!", chnDays)); // 彩信内容part1
                String part2 = data2.getString("PARA_CODE21") + data2.getString("PARA_CODE24");
                otherTD.setRsrvStr26(part2.replace("%END_DATE!", chnDays)); // 彩信内容part2
                String part3 = data2.getString("PARA_CODE22") + data2.getString("PARA_CODE25");
                otherTD.setRsrvStr27(part3.replace("%END_DATE!", chnDays)); // 彩信内容part3
            }
        }

        if (paramDs3 != null && paramDs3.size() > 0)
        {
            IData tempData = paramDs3.getData(0);
            // --------------------组织other表台账-------------------------
            otherTD.setRsrvValue(inparam.getString("TRADE_ID"));
            otherTD.setRsrvStr1(tempData.getString("PARA_CODE1"));// callpf约定使用
            otherTD.setRsrvStr2(tempData.getString("PARA_CODE2"));// 接入系统号
            otherTD.setRsrvStr3(tempData.getString("PARA_CODE3"));// 业务商号
            otherTD.setRsrvStr4(tempData.getString("PARA_CODE4"));// 发送类型
            otherTD.setRsrvStr5(tempData.getString("PARA_CODE5"));// 凭证标题
            otherTD.setRsrvStr6(tempData.getString("PARA_CODE6"));// 连续密码错误最大次数
            otherTD.setRsrvStr7(tempData.getString("PARA_CODE7"));// 活动号
            otherTD.setRsrvStr8(tempData.getString("PARA_CODE8"));// 条码有效期(天)
            otherTD.setRsrvStr9(tempData.getString("PARA_CODE9"));// 活动可使用次数
            otherTD.setRsrvStr10(tempData.getString("PARA_CODE10"));// 活动可使用金额(元)
            otherTD.setRsrvStr11(tempData.getString("PARA_CODE11"));// 是否需要返回图片信息
            otherTD.setRsrvStr12(tempData.getString("PARA_CODE24"));// 活动打印内容
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER + SysDateMgr.END_DATE);// 条码结束使用时间默认2050年

            String strValidDays = tempData.getString("PARA_CODE8"); // 条码有效期(天)
            String chnDays = "";
            // 若存在有效天数，则以当前时间+有效天等到日期当天的最后一秒
            if (strValidDays != null && strValidDays.trim().length() > 0)
            {
                int seconds = (60 * 60 * 24) * (new Integer(strValidDays));
                String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                strEndDate = strEndDate.substring(0, 10);
                strEndDate = strEndDate + SysDateMgr.END_DATE;
                otherTD.setEndDate(strEndDate); // 条码结束使用时间

                // 有效截止时间转换成中文日期格式
                chnDays = strEndDate.substring(0, 4) + "年" + strEndDate.substring(5, 7) + "月" + strEndDate.substring(8, 10) + "日";
            }

            String smsContent = tempData.getString("PARA_CODE23");
            smsContent = smsContent.replace("%END_DATE!", chnDays);
            otherTD.setRsrvStr21(smsContent); // 短信内容

        }
        otherTD.setRemark("积分兑换发送E拇指信息");
        
        if (paramDs4 != null && paramDs4.size() > 0)
        {
            IData tempData = paramDs4.getData(0);
            // --------------------组织other表台账-------------------------
            otherTD.setRsrvStr1(tempData.getString("PARA_CODE1"));
            otherTD.setRsrvValue(tempData.getString("PARA_CODE1"));
            otherTD.setRsrvStr2(tempData.getString("PARA_CODE2"));//活动编号
            otherTD.setRsrvStr3(tempData.getString("PARA_CODE3"));//券别类型
            otherTD.setRsrvStr5(tempData.getString("PARA_CODE5"));// 金额
			//REQ201410100016积分兑换和包电子券业务系统需求   wuxd  获取电子券金额
            IDataset param1923 = CommparaInfoQry.getCommByParaAttr("CSM","1923",this.getTradeEparchyCode());
            if(IDataUtil.isNotEmpty(param1923))
            {
            	if(param1923.getData(0).getString("PARA_CODE2","").equals(data.getString("RULE_ID", "")))
            	{
	            	String evalue = data.getString("EVALUE");
	            	int evale1 = Integer.parseInt(evalue) * 100 ;
	            	otherTD.setRsrvStr5(String.valueOf(evale1));// 金额      
            	}
            }
            
            //REQ201703030013  积分兑换和包电子券业务系统需求        为了不影响其他接口，新增逻辑判断 add by duhj
            IDataset param1924 = CommparaInfoQry.getCommByParaAttr("CSM","1924",this.getTradeEparchyCode());
            if(IDataUtil.isNotEmpty(param1924))
            {
            	if(param1924.getData(0).getString("PARA_CODE2","").equals(data.getString("RULE_ID", "")))
            	{
                    otherTD.setRsrvStr2(data.getString("ACTION_ID"));//活动编号
                    otherTD.setRsrvStr3(data.getString("PRO_ID"));//券别编号
                	otherTD.setRsrvStr5(data.getString("HBEVALUE"));// 金额      

            	}
            }
                         
            otherTD.setRsrvStr7(btd.getTradeId());//电子券编码
            otherTD.setRsrvStr9(tempData.getString("PARA_CODE9"));//接口功能码
            otherTD.setRsrvStr10(tempData.getString("PARA_CODE10"));//商户号
            otherTD.setRsrvStr11(SysDateMgr.getSysDateYYYYMMDD());//商户日期
            otherTD.setRsrvStr12(SysDateMgr.getSysDateYYYYMMDD());//发放日期
            otherTD.setRsrvStr14(tempData.getString("PARA_CODE14","0"));//是否开户，默认为0
            otherTD.setRsrvStr24(tempData.getString("PARA_CODE24"));//                             
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER + SysDateMgr.END_DATE);// 条码结束使用时间默认2050年

            String strValidDays = tempData.getString("PARA_CODE8"); // 条码有效期(天)
            String chnDays = "";
            // 若存在有效天数，则以当前时间+有效天等到日期当天的最后一秒
            if (strValidDays != null && strValidDays.trim().length() > 0)
            {
                int seconds = (60 * 60 * 24) * (new Integer(strValidDays));
                String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                strEndDate = strEndDate.substring(0, 10);
                strEndDate = strEndDate + SysDateMgr.END_DATE;
                otherTD.setEndDate(strEndDate); // 条码结束使用时间

                // 有效截止时间转换成中文日期格式
                chnDays = strEndDate.substring(0, 4) + "年" + strEndDate.substring(5, 7) + "月" + strEndDate.substring(8, 10) + "日";
            }


            
            //REQ201703030013  积分兑换和包电子券业务系统需求        为了不影响其他接口，新增逻辑判断
            if(IDataUtil.isNotEmpty(param1924)&&param1924.getData(0).getString("PARA_CODE2","").equals(data.getString("RULE_ID", "")))
            {
                    int value = data.getInt("SCORE_VALUE");
                    Double hbevalue = data.getInt("HBEVALUE")/100.00 ;
                    String smsContent21 ="尊敬的客户，您已成功使用"+value+"积分兑换"+hbevalue+"元和包电子券。【中国移动】";                      
                    smsContent21 = smsContent21.replace("%END_DATE!", chnDays);
                    otherTD.setRsrvStr21(smsContent21); // 短信内容
                    
                    String smsContent24 ="积分兑换礼券赠送"+hbevalue+"和包电子券（兑换券消费不开具发票）";                      
                    otherTD.setRsrvStr24(smsContent24);//                             

            	
            }else{
                String smsContent = tempData.getString("PARA_CODE23");                      
                smsContent = smsContent.replace("%END_DATE!", chnDays);
                otherTD.setRsrvStr21(smsContent); // 短信内容
            }
            

            
            otherTD.setRemark("发送积分兑换和包电子券信息");
        }
        otherTD.setRsrvValueCode("SCORE_DMS_SMS");// 积分兑换发送二维码标识
        otherTD.setUserId(inparam.getString("USER_ID"));
        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setStartDate(SysDateMgr.getSysDate());// 条码开始使用时间
        otherTD.setStaffId(getVisit().getStaffId());
        otherTD.setDepartId(getVisit().getDepartId());
        otherTD.setOperCode("06");// 服开要求
        otherTD.setModifyTag("0");
        btd.add(reqData.getUca().getSerialNumber(), otherTD);
    }

    /**
     * 组织登记积分台账子表的数据
     * 
     * @param data
     * @param scoreInfo
     * @param btd
     * @return
     * @throws Exception
     */
    public void tradeScoreData(IData data, String oldScore, BusiTradeData btd) throws Exception
    {
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) btd.getRD();
        ScoreTradeData tempScore = new ScoreTradeData();
        int score = data.getInt("VALUE_CHANGED_SUB");
        int value = data.getInt("SCORE_VALUE");
        String exchangeType = data.getString("EXCHANGE_TYPE_CODE");
        tempScore.setUserId(reqData.getUca().getUser().getUserId());
        tempScore.setSerialNumber(reqData.getUca().getUser().getSerialNumber());
        tempScore.setIdType("0");
        tempScore.setYearId("ZZZZ");
        tempScore.setStartCycleId("-1");
        tempScore.setRsrvStr1(exchangeType);
        tempScore.setEndCycleId("-1");
        tempScore.setScore(oldScore);
        IDataset paramDs = CommparaInfoQry.getCommPkInfo("CSM", "97", data.getString("RULE_ID"), this.getTradeEparchyCode());
        if (IDataUtil.isEmpty(paramDs))
        {
            tempScore.setScoreChanged("-" + String.valueOf(value));
        }
        else
        {
            tempScore.setScoreChanged("0");
        }
        tempScore.setValueChanged(String.valueOf(score));
        tempScore.setScoreTag("1");
        tempScore.setRuleId(data.getString("RULE_ID"));
        tempScore.setActionCount(data.getString("COUNT"));
        tempScore.setResId(data.getString("GIFT_TYPE_CODE"));
        tempScore.setGoodsName(data.getString("RULE_NAME"));
        tempScore.setCancelTag("0");
        tempScore.setRemark(reqData.getRemark());
        tempScore.setRsrvStr7(data.getString("RSRV_STR7"));
        tempScore.setScoreTypeCode("ZZ");

        // 增加校验，防止积分兑换兑换数量为0的物品
        if ("0".equals(data.getString("COUNT", "0")))
        {
            CSAppException.apperr(SalegoodsException.CRM_SALEGOODS_10, data.getString("RULE_NAME"));
        }
        if (ScoreFactory.EXCHANGE_TYPE_HH.equals(exchangeType))
        {
            tempScore.setRsrvStr3(reqData.getHhCardId());
            tempScore.setRsrvStr4(reqData.getHhCardName());

            // 海航金鹏里程
            IDataset results = ExchangeRuleInfoQry.queryByRuleId(data.getString("RULE_ID"), this.getTradeEparchyCode());
            if (results != null && results.size() > 0)
            {
                // 兑换总数计算
                int scoreTotal = data.getInt("COUNT") * results.getData(0).getInt("RSRV_STR4");
                tempScore.setRsrvStr5(String.valueOf(scoreTotal));
            }
            else
            {
                // 获取海航金鹏里程参数有误,请检查TD_B_EXCHANGE_RULE处相应的兑换物存在否！
                CSAppException.apperr(CrmUserException.CRM_USER_1096);
            }
        }
        
        if (ScoreFactory.EXCHANGE_TYPE_MOVIES.equals(exchangeType))
        { // 兑换平台观影体育等电子券
        	tempScore.setRsrvStr6("movies");//观影体育俱乐部
        }
 
        btd.add(reqData.getUca().getSerialNumber(), tempScore);
    }
    

    
}
