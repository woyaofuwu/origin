
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.ScoreFactory;

public class ScoreExchangeBean extends CSBizBean
{
    /**
     * 调资源的流程 查询有价卡信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset callResCard(IData param) throws Exception
    {
        String device_no_s = param.getString("CARD_ID");
        String device_no_e = param.getString("CARD_END");
        IDataset dataset = ResCall.iGetValueCardInfo(device_no_s, device_no_e, "330");

        return dataset;
    }

    /**
     * 去掉重复记录
     * 
     * @param idata
     * @param code
     * @return
     * @throws Exception
     */
    public IDataset cleanRepeatData(IDataset idata, String code) throws Exception
    {

        IDataset tempData = new DatasetList();
        if (idata == null || idata.size() < 1)
        {
            return idata;
        }
        for (int i = 0; i < idata.size(); i++)
        {
            IDataset filder = DataHelper.filter(tempData, code + "=" + idata.getData(i).getString(code));

            if (filder.size() < 1)
            {
                tempData.add(idata.get(i));
            }
        }

        return tempData;
    }

    /**
     * 计算有价卡数量
     * 
     * @param cardStart
     * @param cardEnd
     * @return
     * @throws Exception
     */
    public int countCardNumber(String cardStart, String cardEnd) throws Exception
    {
        long start = Long.parseLong(cardStart);
        long end = Long.parseLong(cardEnd);
        int number = (int) (end - start);

        return Math.abs(number) + 1;
    }

    /**
     * 获取子业务信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getCommInfo(IData inData) throws Exception
    {

        IData data = new DataMap();
        IData commInfo = new DataMap();
        String eparchyCode = BizRoute.getRouteId();

        String userId = inData.getString("USER_ID");
        String brandCode = inData.getString("BRAND_CODE");
        if (StringUtils.isBlank(brandCode))
        {
            // 根据用户服务号码获取用户品牌出错！
            CSAppException.apperr(CrmUserException.CRM_USER_278);
        }
        if (!"G001".equals(brandCode) && !"G010".equals(brandCode) && !"G002".equals(brandCode))
        { // G002老神州行用户有积分也能参与兑换
            // 该用户产品不能进行积分兑换，用户品牌必须为全球通或动感地带或神州行！
            CSAppException.apperr(CrmUserException.CRM_USER_896);
        }

        // 查用户积分
        IData param = new DataMap();
        IDataset scoreInfo = AcctCall.queryUserScore(userId);
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分
        if (StringUtils.isNotBlank(score) && Integer.parseInt(score) <= 0)
        {
            // 用户可兑换积分为[" + score + "]，业务不能继续!
            CSAppException.apperr(CrmUserException.CRM_USER_271, score);
        }

        // 查用户品牌
        param.clear();
        param.put("PRODUCT_TYPE_CODE", brandCode);

        // 查客户级别
        param.clear();
        param.put("USER_ID", userId);
        String className = queryVipClass(param);

        // 获取兑换资格
        String exchangeRight = getExchangeRight(score);

        // 获取业务信息
        IData tradeInfo = getTradeInfoByBrand(brandCode);

        // 兑换类型数据
        IDataset exchangeType = ExchangeRuleInfoQry.queryExchangeType(eparchyCode, null);

        // 物品类型数据
        IDataset objectType = ExchangeRuleInfoQry.queryExchangeObject(eparchyCode, brandCode, score, null);

        // 分值类型数据
        IDataset centType = ExchangeRuleInfoQry.queryExchangeCent(eparchyCode, brandCode, null); // 分值类型数据

        // 兑换类型限制
        IData limitInfo = getExchangeTypeLimit(exchangeType);
        
        //兑换列表
        IDataset exchangeList = queryExchangeList(score,brandCode);
        
        data.put("EXCHANGE_LIST", exchangeList);
        data.put("OBJECT_TYPE", objectType);
        data.put("CENT_TYPE", centType);
        data.put("EXCHANGE_TYPE_CODE", cleanRepeatData(exchangeType, "EXCHANGE_TYPE_CODE"));

        //获取品牌编码，调用产商品接口,本次改造duhj 2017/03/08
        String brandName=UBrandInfoQry.getBrandNameByBrandCode(brandCode);
 
        
        commInfo.putAll(limitInfo);
        commInfo.putAll(scoreInfo.getData(0));
        commInfo.putAll(tradeInfo);
        commInfo.put("CLASS_NAME", className);
        commInfo.put("SCORE", score);
        commInfo.put("RSRV_NUM2", scoreInfo.getData(0).getString("SUM_TOTAL_SCORE"));// 总消费积分
        commInfo.put("EXCHANGE_RIGHT", exchangeRight);
        commInfo.put("BRAND_CODE", brandCode);
        commInfo.put("BRAND_NAME", brandName);

        data.put("COMMINFO", commInfo);

        return data;
    }

    /**
     * 获取兑换资格
     * 
     * @param str1
     * @return
     * @throws Exception
     */
    public String getExchangeRight(String str1) throws Exception
    {

        String exchangeRight = "有";
        int a = Integer.parseInt(str1);
        if (a <= 0)
        {
            exchangeRight = "无";
        }

        return exchangeRight;
    }

    /**
     * 查询兑换类型限制
     * 
     * @param inparams
     * @return
     */
    public IData getExchangeTypeLimit(IDataset inparams)
    {

        IData data = new DataMap();
        if (IDataUtil.isEmpty(inparams))
        {
            return data;
        }
        else
        {
            int size = inparams.size();
            for (int i = 0; i < size; i++)
            {
                IData temp = inparams.getData(i);
                String exchangeTypeCode = temp.getString("EXCHANGE_TYPE_CODE");
                String exchangeTypeLimit = temp.getString("EXCHANGE_TYPE_LIMIT");
                if (ScoreFactory.EXCHANGE_TYPE_CARD.equals(exchangeTypeCode))
                {
                    data.put("CARD_COUNT", exchangeTypeLimit);
                }
                if (ScoreFactory.EXCHANGE_TYPE_FEE.equals(exchangeTypeCode))
                {
                    data.put("FEE_COUNT", exchangeTypeLimit);
                }
                if (ScoreFactory.EXCHANGE_TYPE_REWARD.equals(exchangeTypeCode))
                {
                    data.put("GOODS_COUNT", exchangeTypeLimit);
                }
                if (ScoreFactory.EXCHANGE_TYPE_DISCNT.equals(exchangeTypeCode))
                {
                    data.put("DISCNT_COUNT", exchangeTypeLimit);
                }
            }
        }

        return data;
    }

    /**
     * 根据品牌获取业务信息
     * 
     * @param brandCode
     * @return data
     * @throws Exception
     */
    public IData getTradeInfoByBrand(String brandCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", "330");
        data.put("REMARK", "积分兑换");
//        if ("G010".equals(brandCode))
//        {
//            data.put("TRADE_TYPE_CODE", "333");
//            data.put("REMARK", "M值兑换");
//        }
        data.put("SCORE_TYPE_CODE", "ZZ");

        return data;
    }

    private IData getVipInfo(IData param) throws Exception
    {

        IDataset results = new DatasetList();

        IData result = new DataMap();

        results = CustVipInfoQry.qryVipInfoByUserId(param.getString("USER_ID"));

        if (IDataUtil.isNotEmpty(results))
        {
            result = results.getData(0);
        }

        return result;
    }

    /**
     * 查询有价卡状态
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryCardState(IData data) throws Exception
    {
        IDataset reCardDatas = new DatasetList();
        String cardStart = data.getString("CARD_ID");
        String cardEnd = data.getString("CARD_END");
        int startLength = cardStart.length();
        int endLength = cardEnd.length();
        if (startLength != endLength || (!cardStart.substring(0, startLength - 3).equals(cardEnd.substring(0, endLength - 3))))
        {
            CSAppException.apperr(ResException.CRM_RES_80);
        }
        int cardNumber = countCardNumber(cardStart.substring(startLength - 3, startLength), cardEnd.substring(startLength - 3, startLength));
        if (cardNumber > 10)
        {
            CSAppException.apperr(ResException.CRM_RES_81);
        }

        IDataset cardDatas = callResCard(data);

        if (IDataUtil.isEmpty(cardDatas) || ("0".equals(cardDatas.getData(0).getString("X_RESULTCODE")) && "0".equals(cardDatas.getData(0).getString("X_RECORDNUM"))))// 查询成功且无数据
        {
            return returnQueryMessageByset(reCardDatas, ScoreFactory.X_RESULTCODE_TAG, "获取资源有价卡信息无数据");
        }
        else
        // 查询成功且有数据
        {
            int cardDataSize = cardDatas.size();
            for (int i = 0; i < cardDataSize; i++)
            {
                IData tempData = new DataMap();
                IData cardData = cardDatas.getData(i);
                tempData.put("CARD_TYPE", cardData.getString("RES_KIND_NAME"));
                tempData.put("CARD_ID", cardData.getString("VALUE_CARD_NO"));
                tempData.put("CARD_COUNT", "1");
                tempData.put("CARD_ACTIVE", "0".equals(cardData.getString("ACTIVE_FLAG")) ? "未激活" : "激活");
                String cardValue = cardData.getString("ADVISE_PRICE");
                tempData.put("CARD_VALUE", cardValue);
                if (StringUtils.isNotBlank(cardValue))
                {
                    tempData.put("CARD_NAME", Integer.parseInt(cardValue) / 100);
                }
                reCardDatas.add(tempData);
            }
            return returnQueryMessageByset(reCardDatas, ScoreFactory.X_RESULTCODE_SECCESS, "ok");
        }

    }

    /**
     * 获取所有可兑换的记录列表
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    @SuppressWarnings("static-access")
	public IDataset queryExchangeList(String score,String brandCode) throws Exception
    {
        IDataset exchangeList = ExchangeRuleInfoQry.queryExRuleByVipBrandCode(score,brandCode,this.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(exchangeList))
        {
            // 处理兑换类型为物品且在资源侧可管理的记录,用于更新EXCHANGE_LIMIT
            IData exchangeData = null;
            String exchangeTypeCode = "";
            String exchangeResId = "";

            IData resGiftNumData = null;
            int exchangeRuleLimit = -1;
            int exchangeLimit = -1;

            int listSize = exchangeList.size();
            for (int i = 0; i < listSize; i++)
            {
                exchangeData = exchangeList.getData(i);
                exchangeTypeCode = exchangeData.getString("EXCHANGE_TYPE_CODE");// 兑换类型编码
                exchangeResId = exchangeData.getString("GIFT_TYPE_CODE");// 资源管理标识RES_ID
                exchangeRuleLimit = exchangeData.getInt("EXCHANGE_LIMIT");// 营业侧定义可兑换数量

                if ((ScoreFactory.EXCHANGE_TYPE_REWARD.equals(exchangeTypeCode)) && (StringUtils.isNotBlank(exchangeResId)))
                {

                    resGiftNumData = ResCall.queryGoods(exchangeResId);

                    exchangeLimit = resGiftNumData.getInt("GOODS_NUM");// 资源定义的兑换数量:库存
                    if (exchangeLimit > 0)
                    {// 要注意营业侧定义的兑换次数为-1，这个时候以资源库存数据为准
                        exchangeData.put("EXCHANGE_LIMIT", exchangeLimit < exchangeRuleLimit ? "" + exchangeLimit : (exchangeRuleLimit == -1 ? "" + exchangeLimit : "" + exchangeRuleLimit));
                    }
                    else
                    {
                        exchangeList.remove(i);
                        i--;
                        continue;
                    }

                }
            }
        }
        return exchangeList;
    }

    /**
     * 查询转赠手机号的基本资料
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryObjectBySN(IData data) throws Exception
    {
        IData info = new DataMap();
        String sn = data.getString("SERIAL_NUMBER", "");
        IData userInfos = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        else
        {
            data.put("CUST_ID", userInfos.getString("CUST_ID"));
            data.put("USER_ID", userInfos.getString("USER_ID"));
            info.put("USERINFO", userInfos);
        }

        String custId = data.getString("CUST_ID");
        IData custInfos = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(custInfos))
        {
            info.put("CUSTINFO", custInfos);
        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_35);
        }

        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(data.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(acctInfo))
        {
            data.put("ACCT_ID", acctInfo.get("ACCT_ID"));
            info.put("ACCTINFO", acctInfo);
        }
        else
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_123);
        }

        return returnQueryMessage(info, ScoreFactory.X_RESULTCODE_SECCESS, "OK");// 直接返回INFO
    }

    /**
     * 获取客户级别
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public String queryVipClass(IData param) throws Exception
    {
        String className = "";
        IData data = new DataMap();
        data.put("USER_ID", param.getString("USER_ID"));
        data.put("REMOVE_TAG", "0");
        IData vipInfo = getVipInfo(data);

        if ("".equals(data.getString("VIP_TYPE_CODE", "")))
        {
            className = "非大客户";
        }
        else
        {
            className = UVipTypeInfoQry.getVipTypeNameByVipTypeCode(vipInfo.getString("VIP_TYPE_CODE", ""));
        }
        return className;
    }

    /**
     * 返回查询成功与否的标志
     * 
     * @param data
     * @param flag
     * @param message
     * @return
     * @throws Exception
     */
    public IData returnQueryMessage(IData data, String flag, String message) throws Exception
    {

        data.put("X_RESULTCODE", flag);
        data.put("X_RESULTINFO", message);

        return data;
    }

    /**
     * 返回查询信息
     * 
     * @param idataset
     * @param flag
     * @param message
     * @return
     * @throws Exception
     */
    public IDataset returnQueryMessageByset(IDataset idataset, String flag, String message) throws Exception
    {

        IDataset idata = new DatasetList();
        IData param = new DataMap();
        returnQueryMessage(param, flag, message);
        idata.add(0, param);
        idata.addAll(idataset);

        return idata;
    }
    
    @SuppressWarnings("static-access")
	public IData queryHbdzq(IData input) throws Exception
    {
    	String serialnumber = input.getString("SERIAL_NUMBER");
    	
    	IData result = new DataMap();
    	String message ;
    	IDataset scoreInfo = null ;
    	IData info = null ;
    	
    	if("".equals(serialnumber) || serialnumber == null)
    	{
    		message = "手机号码不能为空";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("USERSCORE","0");
    		result.put("DZQNUM", "0");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
    	}
    	
        info = UcaInfoQry.qryUserInfoBySn(serialnumber);

    	if(IDataUtil.isEmpty(info))
    	{
    		message = "获取用户资料失败";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("USERSCORE","0");
    		result.put("DZQNUM", "0");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
    	}
    	
    	scoreInfo = AcctCall.queryUserScore(info.getString("USER_ID"));
    	int userScore = 0 ;
    	
    	if (IDataUtil.isEmpty(scoreInfo.getData(0)))
        {
    		message = "获取用户积分信息失败";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("USERSCORE","0");
    		result.put("DZQNUM", "0");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }
    	
    	userScore = scoreInfo.getData(0).getInt("SUM_SCORE");
    	
    	IDataset param1923 = CommparaInfoQry.getCommByParaAttr("CSM","1923",this.getTradeEparchyCode());
    	
    	if (IDataUtil.isEmpty(param1923))
        {
    		message = "获取积分兑换电子券金额配置失败";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("USERSCORE",String.valueOf(userScore));
    		result.put("DZQNUM", "0");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }

    	String perscore = param1923.getData(0).getString("PARA_CODE1","1");
    	
    	int perscore1 = Integer.parseInt(perscore);
    	
    	if(perscore1 > userScore)
    	{
    		message = "用户积分为" + String.valueOf(userScore) +",所需积分最少为"+ perscore +",不足本次兑换！";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("USERSCORE",String.valueOf(userScore));
    		result.put("DZQNUM", "0");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
    	}
    	
    	int totalfee = userScore/perscore1 ;
    	
    	message = "查询成功，兑换1元电子券需要的积分为：" + perscore + "分";
    	result.put("RETCODE", "0000" );
		result.put("RETMSG", message );
		result.put("USERSCORE",String.valueOf(userScore));
		result.put("DZQNUM", String.valueOf(totalfee));
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPDESC",message);
		result.put("X_RESULTINFO",message);
		result.put("X_RESULTCODE","00");
        return result ;
        
    }
    
    @SuppressWarnings("static-access")
	public IDataset getEmzData(IData input) throws Exception
    {
    	IDataset result = new DatasetList();
    	IDataset params = new DatasetList();
    	IDataset params97 = new DatasetList();
    	IDataset temps = new DatasetList(input.getString("EXCHANGE_DATA"));
    	for (int j = 0; j < temps.size(); j++) {
    		IData temp = temps.getData(j);
    		String strRuleId = temp.getString("RULE_ID");
    		//String strCount = temp.getString("COUNT");
    		IDataset param97 = CommparaInfoQry.getCommPkInfo("CSM", "97", strRuleId, this.getTradeEparchyCode());
    		if ( IDataUtil.isNotEmpty(param97) ) {
    			params97.add(temp);
            }else{
            	params.add(temp);
            }
    		
		}
    	IData data = new DataMap();
    	data.put("EXCHANGE_EMZ", params97);
    	data.put("EXCHANGE_DATA", params);
    	result.add(data);
    	return result;
    }
    /**
     * REQ201703030013_新增积分兑换等值和包电子券业务
     * <br/>
     * 积分兑换和电子劵结果查询
     * @param input
     * @return
     * @throws Exception
     */
	public IDataset queryScoreExchange(IData input) throws Exception{
		
    	 IData inparams=new DataMap();
    	 //手机号码
    	 inparams.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
    	 //请求业务流水
    	 inparams.put("RSRV_STR6", input.getString("REQ_ID", ""));
    	 
    	 inparams.put("TRADE_TYPE_CODE","330");
         
		return Dao.qryByCode("TF_BH_TRADE", "SEL_SCORE_EXCHANGE", inparams,Route.getJourDb(BizRoute.getRouteId()));
	}

}
