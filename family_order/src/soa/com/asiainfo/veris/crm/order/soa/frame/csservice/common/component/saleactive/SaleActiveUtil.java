
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

public final class SaleActiveUtil
{
    public static IData buildTerminalInfo(IData terminalInfo)
    {
        IData map = new DataMap();
        map.put("DEVICE_MODEL", terminalInfo.getString("DEVICE_MODEL"));
        map.put("DEVICE_BRAND", terminalInfo.getString("DEVICE_BRAND"));
        map.put("COLOR", terminalInfo.getString("RSRV_STR3"));
        map.put("BATTERY", terminalInfo.getString("RSRV_STR4"));
        map.put("SALE_PRICE", terminalInfo.getString("SALE_PRICE"));
        map.put("DEVICE_MODEL_CODE", terminalInfo.getString("DEVICE_MODEL_CODE"));
        map.put("TERMINAL_DETAIL_INFO", terminalInfo.toString());
        return map;
    }

    public static void checkIntfInparam(IData input, String name) throws Exception
    {
        String value = input.getString(name);
        if (StringUtils.isBlank(value))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_34, name);
        }
    }

    public static IDataset filterElementByCreditClass(IDataset credits, String userId) throws Exception
    {
        IDataset matchInfos = new DatasetList();

        if (credits != null && credits.size() > 1)
        {
            String creditClass = queryUserCreditClass(userId);
            for (int i = 0, size = credits.size(); i < size; i++)
            {
                IData credit = credits.getData(i);
                String rsrvStr1 = credit.getString("RSRV_STR1", "");
                
                if(StringUtils.isNotBlank(rsrvStr1))
                {
                	int length = "CREDIT_CLASS".length();
                	String configCreditClass = rsrvStr1.substring(length);
                	if(configCreditClass.indexOf(creditClass)>-1)
                	{
                		matchInfos.add(credit);
                	}
                }
            }
        }
        else
        {
            matchInfos = credits;
        }

        return matchInfos;
    }

    public static void filterPackageElementPropertys(IData element)
    {
        String elementPropertyArr[] =
        { "ELEMENT_TYPE_CODE", "ELEMENT_ID", "MODIFY_TAG", "START_DATE", "END_DATE" };

        List<String> remveKeyList = new ArrayList<String>();

        Set<String> keySet = element.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext())
        {
            boolean hasKey = false;
            String key = it.next();
            for (int index = 0, length = elementPropertyArr.length; index < length; index++)
            {
                if (key.equals(elementPropertyArr[index]))
                {
                    hasKey = true;
                    break;
                }
            }
            if (!hasKey)
            {
                remveKeyList.add(key);
            }
        }

        for (int index = 0, size = remveKeyList.size(); index < size; index++)
        {
            String key = remveKeyList.get(index);
            element.remove(key);
        }

    }

    public static List<SaleActiveTradeData> filterUserSaleActivesByProcessTag(List<SaleActiveTradeData> userSaleActives)
    {
        List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            if ("0".equals(userSaleActive.getProcessTag()) || "4".equals(userSaleActive.getProcessTag()))
            {
                result.add(userSaleActive);
            }
        }
        return result;
    }
    
    
    public static List<SaleActiveTradeData> filterUserSaleActivesByModifyTag(List<SaleActiveTradeData> userSaleActives)
    {
        List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            if (BofConst.MODIFY_TAG_USER.equals(userSaleActive.getModifyTag()))
            {
                result.add(userSaleActive);
            }
        }
        return result;
    }
    
    
    public static List<SaleActiveTradeData> sortUserSaleActivesByEndDateDesc(List<SaleActiveTradeData> userSaleActives)
    {
        List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
        IDataset tempDataset = new DatasetList();
        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            tempDataset.add(userSaleActive.toData());
        }
        
        DataHelper.sort(tempDataset, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        
        for(int i=0, size=tempDataset.size(); i<size; i++)
        {
        	SaleActiveTradeData userSaleActive = new SaleActiveTradeData(tempDataset.getData(i));
        	result.add(userSaleActive);
        }
        return result;
    }

    public static IDataset filterUserSaleActivesByQyyx(IDataset userSaleActives) throws Exception
    {
        IDataset result = new DatasetList();

        if (IDataUtil.isEmpty(userSaleActives))
        {
            return result;
        }

        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            IData userSaleActive = userSaleActives.getData(i);
            String campnType = userSaleActive.getString("CAMPN_TYPE");

            if (isQyyx(campnType))
            {
                result.add(userSaleActive);
            }
        }

        return result;
    }

    public static List<SaleActiveTradeData> filterUserSaleActivesByQyyx(List<SaleActiveTradeData> userSaleActives) throws Exception
    {
        List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String campnType = userSaleActive.getCampnType();

            if (isQyyx(campnType))
                result.add(userSaleActive);

        }
        return result;
    }

    public static void getCommparaProductIdAndPackageId(List<String> productIds, List<String> packageIds, IDataset configs) throws Exception
    {
        for (int i = 0, size = configs.size(); i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            String value = config.getString("PARA_CODE1");
            if ("0".equals(paramCode))
            {
                productIds.add(value);
            }
            else if ("1".equals(paramCode))
            {
                packageIds.add(value);
            }
        }
    }

    public static void getCommparaProductIds(List<String> productIds, IDataset configs) throws Exception
    {
        for (int i = 0, size = configs.size(); i < size; i++)
        {
            IData config = configs.getData(i);
            String value = config.getString("PARA_CODE1");
            productIds.add(value);
        }
    }

    public static int getDayIntervalNoAbs(String strDate1, String strDate2) throws Exception
    {
        Date date1 = SysDateMgr.string2Date(strDate1, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        Date date2 = SysDateMgr.string2Date(strDate2, SysDateMgr.PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        long allDays = ((c2.getTimeInMillis()) - (c1.getTimeInMillis())) / (1000 * 24 * 60 * 60);

        return Integer.parseInt(String.valueOf(allDays));
    }

    public static String getEnableActiveActionCode(UcaData uca, String relationTradeId) throws Exception
    {
        List<SaleDepositTradeData> saleDepositTradeDataList = uca.getUserSaleDepositByRelationTradeId(relationTradeId);

        if (saleDepositTradeDataList.isEmpty())
            return null;

        for (SaleDepositTradeData saleDepositTradeData : saleDepositTradeDataList)
        {
            String aActionCode = saleDepositTradeData.getADiscntCode();

            if (StringUtils.isNotBlank(aActionCode) && !"0".equals(aActionCode))
                return aActionCode;
        }

        return null;
    }

    public static String getIntervalMoths(String startDate, String endDate, String month) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();

        if (sysDate.compareTo(startDate) < 0)
            return month;

        return String.valueOf(SysDateMgr.monthInterval(sysDate, endDate));
    }
    
    /**
     * 该方法是对上面的getIntervalMoths补充 by songlm 20150116
     * getIntervalMoths：如果系统时间大于等于参入开始时间，则返回传入（开始时间 减 系统时间）的月差值绝对值+1，即 （2015-12-31 23:59:59) - (2015-01-15 11:12:23) = 12
     * getIntervalMonths：如果系统时间大于等于参入开始时间，则返回传入（开始时间 减 系统时间）的月差绝对值，即 （2015-12-31 23:59:59) - (2015-01-15 11:12:23) = 11 用于后继营销活动偏移月份量
     * */
    public static String getIntervalMonths(String startDate, String endDate, String month) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();

        if (sysDate.compareTo(startDate) < 0)
            return month;

        return String.valueOf(Math.abs(SysDateMgr.monthIntervalYYYYMM(sysDate, endDate)));
    }

    public static String getMaxEndDateFromUserSaleActive(List<SaleActiveTradeData> userSaleActives)
    {
        String maxEndDate = "";
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String endDate = userSaleActive.getEndDate();
            if (endDate.compareTo(maxEndDate) > 0)
            {
                maxEndDate = endDate;
            }
        }
        return maxEndDate;
    }
    
    /**
     * 获取用户营销活动宽带1+活动优惠最后生效时间 @yanwu
     * @param userDiscnts
     * @return
     */
    public static String getMaxEndDateFromUserDiscnt(IDataset userDiscnts)
    {
        String maxEndDate = "";
        int size = userDiscnts.size();
        for (int i = 0; i < size; i++)
        {
        	IData userDiscnt = userDiscnts.getData(i);
            String endDate = userDiscnt.getString("END_DATE");
            if (endDate.compareTo(maxEndDate) > 0)
            {
                maxEndDate = endDate;
            }
        }
        return maxEndDate;
    }
    
    /**
     * 获取用户营销活动宽带1+活动优惠最后生效时间 @yanwu
     * @param userDiscnts
     * @return
     */
    public static String getMaxEndDateFromUserDiscnt(List<DiscntTradeData> userDiscnts)
    {
        String maxEndDate = "";
        int size = userDiscnts.size();
        for (int i = 0; i < size; i++)
        {
        	DiscntTradeData userDiscnt = userDiscnts.get(i);
            String endDate = userDiscnt.getEndDate();
            if (endDate.compareTo(maxEndDate) > 0)
            {
                maxEndDate = endDate;
            }
        }
        return maxEndDate;
    }

    public static String getMaxOnNetEndDateFromUserSaleActive(List<SaleActiveTradeData> userSaleActives)
    {
        String maxOnNetEndDate = "";
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String onNetEndDate = userSaleActive.getRsrvDate2();
            if ("".equals(onNetEndDate))
                onNetEndDate = userSaleActive.getEndDate();
            if (onNetEndDate.compareTo(maxOnNetEndDate) > 0)
            {
                maxOnNetEndDate = onNetEndDate;
            }
        }
        return maxOnNetEndDate;
    }

    public static int getMonthIntervalNoAbs(String strDate1, String strDate2) throws Exception
    {
        Date date1 = SysDateMgr.string2Date(strDate1, "yyyy-MM-dd");
        Date date2 = SysDateMgr.string2Date(strDate2, "yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int month = (c2.get(1) - c1.get(1)) * 12 + c2.get(2) - c1.get(2);

        if (month < 0)
            return month - 1;

        return month + 1;
    }

    public static String getWidenetUserOpenTradeId(String serialNumber) throws Exception
    {
        IDataset tradeInfo = TradeInfoQry.queryWidenetUserOpenTradeid("KD_" + serialNumber);

        return IDataUtil.isNotEmpty(tradeInfo) ? tradeInfo.getData(0).getString("TRADE_ID") : "";
    }

    public static boolean isBackActive(String productId, String eparchyCode) throws Exception
    {
        IDataset noBackParaActives = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "155", "0", productId, eparchyCode);

        String labelId = LabelInfoQry.getLogicLabelIdByElementId(productId);

        if (IDataUtil.isEmpty(noBackParaActives) && isQyyx(labelId))
            return true;

        return false;
    }

    public static boolean isCommparaConfigsByParamCodeElementId(String elementId, IDataset configs) throws Exception
    {
        for (int i = 0, size = configs.size(); i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            if (elementId.equals(paramCode))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isComputeActiveFee(String callType)
    {
        if (SaleActiveConst.CALL_TYPE_ACTIVATE_ACTIVE.equals(callType))
            return false;

        return true;
    }

    public static boolean isExistPlatSvc(BaseSaleActiveReqData brd, String elementId) throws Exception
    {
        boolean isExist = false;
        List<PlatSvcTradeData> userPlatSvcs = brd.getUca().getUserPlatSvcs();
        for (int j = 0; j < userPlatSvcs.size(); j++)
        {
            PlatSvcTradeData userPlatSvcTradeData = userPlatSvcs.get(j);
            isExist = userPlatSvcTradeData.getElementId().equals(elementId);
            if (isExist)
                break;
        }
        return isExist;
    }

    public static boolean isExistSvc(BaseSaleActiveReqData brd, String elementId) throws Exception
    {
        return brd.getUca().checkUserIsExistSvcId(elementId);
    }

    public static boolean isInCommparaConfigs(String productId, String packageId, IDataset configs) throws Exception
    {
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            String value = config.getString("PARA_CODE1");
            if ("0".equals(paramCode) && productId.equals(value))
            {
                return true;
            }
            else if ("1".equals(paramCode) && packageId.equals(value))
            {
                return true;
            }
        }
        return false;
    }

    /** 是否签约营销 */
    public static boolean isQyyx(String campnType) throws Exception
    {
//        IDataset labelSet = LabelInfoQry.getLabelsByParentLabelId(SaleActiveConst.SALE_ACTIVE_LOGIC_LABEL_QYYX, campnType);
//        if (IDataUtil.isNotEmpty(labelSet))
//            return true;
//        IDataset staticParas = UpcCall.qryStaticParam("PM_PARA_DETAIL", "EXT_VALUE", "PARA_CODE,PARA_VALUE", "SALEACTIVE_CATALOG_TYPE,L100");
//        if(IDataUtil.isNotEmpty(staticParas))
//        {
//            if (staticParas.getData(0).getString("EXT_VALUE").indexOf(campnType) > -1)
//                return true;
//        }
    	
    	//由于和产商品交互太多次，网络开销大，暂时写死签约类营销活动类型，后续后好办法再处理
    	if("YX02|YX03|YX05|YX06|YX08|YX09|YX10|YX11|YX12|YX13".indexOf(campnType) > -1)
    		return true;
    	
        if (campnType.indexOf("GPYX") > -1)
            return true;

        return false;
    }

    public static boolean isWideNetAciveTrade(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset commparaConfig = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "942", productId, packageId, eparchyCode);
        return IDataUtil.isNotEmpty(commparaConfig) ? true : false;
    }

    @SuppressWarnings("unchecked")
    public static boolean isWideNetActiveAccept(BusiTradeData btd) throws Exception
    {
        BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();
        String productId = sard.getProductId();
        String pacakgeId = sard.getPackageId();
        String eparchyCode = sard.getUca().getUserEparchyCode();
        return isWideNetAciveTrade(productId, pacakgeId, eparchyCode);
    }

    public static String queryUserCreditClass(String userId) throws Exception
    {
        IData creditData = CreditCall.queryUserCreditInfos(userId);
        String creditClass = null;
        if (IDataUtil.isNotEmpty(creditData))
        {
            int resultCode = creditData.getInt("X_RESULTCODE");
            if (resultCode == 0)
            {
                creditClass = creditData.getString("CREDIT_CLASS", "0");
                if ("-1".equals(creditClass))
                {
                    creditClass = "0";
                }
            }
        }
        return creditClass;
    }
    
    public static IDataset searchProducts(String searchCode, String searchContent, String labelId)throws Exception
    {
    	 Map<String, String> cond = new HashMap<String, String>();
         if (StringUtils.isNotBlank(labelId))
         {
             cond.put("LABEL_ID", labelId);
         }
         SearchResponse resp = SearchClient.search(searchCode, searchContent, cond, 0, 30);
         IDataset saleProducts = resp.getDatas();
         return saleProducts;
    }

    public static IDataset searchActives(String searchCode, String searchContent, String productId, String campnType, String eparchyCode)
    {
        Map<String, String> cond = new HashMap<String, String>();
//        cond.put("EPARCHY_CODE", eparchyCode);
        if (StringUtils.isNotBlank(productId))
        {
            cond.put("PRODUCT_ID", productId);
        }
        if (StringUtils.isNotBlank(campnType))
        {
            cond.put("CAMPN_TYPE", campnType);
        }
        SearchResponse resp = SearchClient.search(searchCode, searchContent, cond, 0, 30);
        IDataset saleActives = resp.getDatas();
//        cond.put("EPARCHY_CODE", "ZZZZ");
//        saleActives.addAll(SearchClient.search(searchCode, searchContent, cond, 0, 30).getDatas());
        return saleActives;
    }

    public static void setAcctDayInfo(IData input) throws Exception
    {
        String acctDay = input.getString("ACCT_DAY");
        String firstDate = input.getString("FIRST_DATE");
        String nextAcctDay = input.getString("NEXT_ACCT_DAY");
        String nextFirstDate = input.getString("NEXT_FIRST_DATE");

        if (StringUtils.isNotBlank(acctDay))
        {
            AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate);
            AcctTimeEnvManager.setAcctTimeEnv(env);
        }
    }
    
    
    public static void setAcctDayInfo(String acctDay, String firstDate, String nextAcctDay, String nextFirstDate, String startDate, String nextStartDate) throws Exception
    {
        if (StringUtils.isNotBlank(acctDay))
        {
            AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate, startDate, nextStartDate);
            AcctTimeEnvManager.setAcctTimeEnv(env);
        }
    }

    public static int valueForScore(String depositRate, String scoreValue, int userScore) throws Exception
    {
        int iDepositRate = Integer.parseInt(depositRate); // 预存比率
        int iScoreValue = Integer.parseInt(scoreValue); // 要扣减的积分(一般为负值)
        int iRemainScore = userScore + iScoreValue; // 扣减后，剩余积分

        if (iRemainScore < 0) // 用户当前积分不够
        {
            return Math.abs(iRemainScore) * iDepositRate;
        }

        return 0;
    }
    
    public static List<SaleActiveTradeData> filterActiesByEendDate(List<SaleActiveTradeData> userActives, String endDate)throws Exception
    {
    	List<SaleActiveTradeData> returnActives = new ArrayList<SaleActiveTradeData>();
    	if(CollectionUtils.isEmpty(userActives))
    	{
    		return returnActives;
    	}
    	
    	for(SaleActiveTradeData userActive : userActives)
    	{
    		String _endDate = userActive.getEndDate();
    		if(_endDate.compareTo(endDate)>0)
    		{
    			returnActives.add(userActive);
    		}
    	}
    	return returnActives;
    }
    
    public static boolean isInCommparaParaCode2Configs(String productId, String packageId, IDataset configs) throws Exception
    {
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode2 = config.getString("PARA_CODE2");
            String paramCode3 = config.getString("PARA_CODE3");
            if (productId.equals(paramCode2) && packageId.equals(paramCode3))
            {
                return true;
            }
        }
        return false;
    }
    
    //获取魔百和开户未完工工单tradeId
    public static String getInternetTvUserOpenTradeId(String serialNumber) throws Exception
    {
        IDataset tradeInfo = TradeInfoQry.getMainTradeBySN(serialNumber,"4800");

        return IDataUtil.isNotEmpty(tradeInfo) ? tradeInfo.getData(0).getString("TRADE_ID") : "";
    }
    
    //判断是否存在依赖的活动信息，包含预受理的未完工台帐信息、预受理的资料表信息、正式的未完工台帐信息、正式的资料表信息
    public static boolean saleActvieHave(String sn, String rsrvStr5) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("RSRV_STR5", rsrvStr5);
		
		//判断（包含tf_f_user_sale_active、tf_f_user_saleactive_book、tf_b_trade_sale_active、tf_b_trade_saleactive_book）
		IDataset userSaleActiveInfo = SaleActiveInfoQry.querySaleActiveInfoAndTrade(param);
		IDataset userSaleActiveTradeInfo = SaleActiveInfoQry.querySaleActiveInfoByTrade(param);
		userSaleActiveInfo.addAll(userSaleActiveTradeInfo);
		if(IDataUtil.isNotEmpty(userSaleActiveInfo) && userSaleActiveInfo.size() >0)
		{
			return true;
		}
		
		return false;
	}
    
    public static IDataset getElementsByPackageIdAndElementType(String packageId, String elementTypeCode) throws Exception
    {
        IDataset offers = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, null, null);
        
        return null;

    }
    
    
    
    
    public static IDataset getSaleActivesByPackageIdAndElementType(String packageId, String elementTypeCode) throws Exception
    {
    	return UPackageElementInfoQry.getElementsByElementTypeCode(packageId, elementTypeCode);
    	
//        IDataset results = new DatasetList();
//        
//        IDataset saleActiveDataset = UpcCall.qryOfferFromSaleActiveByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
//        if(IDataUtil.isEmpty(saleActiveDataset))
//        {
//            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_8);
//        }
//        
//        IData saleActiveData = saleActiveDataset.getData(0);
//        
//        if(!elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT) && !elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_PACKAGE))
//        {
//            //必选
//            IDataset offerComRelList = saleActiveData.getDataset("OFFER_COM_REL_LIST");
//            if(IDataUtil.isNotEmpty(offerComRelList))
//            {
//                for(int i=0; i<offerComRelList.size(); i++)
//                {
//                    IData offerComRelInfo = offerComRelList.getData(i);
//                    String offerType = offerComRelInfo.getString("OFFER_TYPE");
//                    String offerCode = offerComRelInfo.getString("OFFER_CODE");
//                    String relId = offerComRelInfo.getString("REL_ID");
//                    if(elementTypeCode.equals(offerType))
//                    {
//                        IData cha = UPackageElementInfoQry.getPkgElemExtCha(relId, "TD_B_PACKAGE_ELEMENT", UpcConst.PM_OFFER_COM_REL_CHA_TYPE);
//                        if("|D|S|C|Z|".indexOf(offerType) > 0 && "B".equals(cha.getString("RSRV_TAG1")))
//                        {
//                            continue;
//                        }
//                        
//                        offerComRelInfo.putAll(cha);
//                        dealParam(offerComRelInfo, elementTypeCode);
//                        offerComRelInfo.put("FORCE_TAG", "1");
//                        
////                        IDataset offerModeInfo = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, offerType, offerCode, "C");
//                        IDataset offerModeInfo = UpcCall.qryEnableModeInfoByRelObjectAndId(UpcConst.REL_OBJECT_OFFER_COM_REL, relId);
//                        if(IDataUtil.isNotEmpty(offerModeInfo))
//                        {
//                            offerComRelInfo.putAll(offerModeInfo.getData(0));
//                        }
//                        
//                        results.add(offerComRelInfo);
//                    }
//                }
//            }
//            
//            //可选
//            IDataset offerJoinRelList = saleActiveData.getDataset("OFFER_JOIN_REL_LIST");
//            if(IDataUtil.isNotEmpty(offerJoinRelList))
//            {
//                for(int i=0; i<offerJoinRelList.size(); i++)
//                {
//                    IData offerJoinRelInfo = offerJoinRelList.getData(i);
//                    String offerType = offerJoinRelInfo.getString("OFFER_TYPE");
//                    String offerCode = offerJoinRelInfo.getString("OFFER_CODE");
//                    String relType = offerJoinRelInfo.getString("REL_TYPE");
//                    String relId = offerJoinRelInfo.getString("REL_ID");
//                    if(elementTypeCode.equals(offerType))
//                    {
//                        IData cha = UPackageElementInfoQry.getPkgElemExtCha(relId, "TD_B_PACKAGE_ELEMENT", UpcConst.PM_OFFER_JOIN_REL_CHA_TYPE);
//                        if("|D|S|C|Z|".indexOf(offerType) > 0 && "B".equals(cha.getString("RSRV_TAG1")))
//                        {
//                            continue;
//                        }
//                        
//                        offerJoinRelInfo.putAll(cha);
//                        dealParam(offerJoinRelInfo, elementTypeCode);
//                        offerJoinRelInfo.put("FORCE_TAG", "0");
//                        
////                        IDataset offerModeInfo = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, offerType, offerCode, relType);
//                        IDataset offerModeInfo = UpcCall.qryEnableModeInfoByRelObjectAndId(UpcConst.REL_OBJECT_OFFER_JOIN_REL, relId);
//                        if(IDataUtil.isNotEmpty(offerModeInfo))
//                        {
//                            offerJoinRelInfo.putAll(offerModeInfo.getData(0));
//                        }
//                        
//                        results.add(offerJoinRelInfo);
//                    }
//                    
//                    String selectFlag = offerJoinRelInfo.getString("SELECT_FLAG");
//                    if("0".equals(selectFlag))
//                    {
//                        offerJoinRelInfo.put("FORCE_TAG", "1");
//                    }else if("1".equals(selectFlag))
//                    {
//                        offerJoinRelInfo.put("DEFAULT_TAG", "1");
//                    }else if("2".equals(selectFlag))
//                    {
//                        offerJoinRelInfo.put("DEFAULT_TAG", "0");
//                    }
//                }
//            }
//        }
//        
//        //预存
//        if(elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT))
//        {
//            //直接根据OFFER_ID找PM_OFFER_GIFT
//            IDataset offerGifts = UpcCall.qryOfferGiftsByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
//            for(Object obj : offerGifts)
//            {
//                IData giftData = (IData)obj;
//                giftData.put("DEPOSIT_TYPE", giftData.getString("GIFT_TYPE"));
//                giftData.put("MONTHS", giftData.getString("GIFT_CYCLE"));
//                giftData.put("GIFT_USE_TAG", giftData.getString("GIFT_USE_TAG"));
//                giftData.put("DISCNT_GIFT_ID", giftData.getString("EXT_GIFT_ID"));
//                giftData.put("DISCNT_GIFT_NAME", giftData.getString("GIFT_NAME"));
//                giftData.put("A_DISCNT_CODE", giftData.getString("GIFT_OBJ_ID"));
//                giftData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
//                giftData.put("ELEMENT_ID", giftData.getString("EXT_GIFT_ID"));
//                giftData.put("DEFAULT_TAG", "1");
//                giftData.put("FORCE_TAG", "1");
//                giftData.put("RSRV_STR1", "");
//                
//                IData cha = UPackageElementInfoQry.getPkgElemExtCha(giftData.getString("REL_ID"), "TD_B_PACKAGE_ELEMENT", "5");
//                giftData.putAll(cha);
//                
//                results.add(giftData);
//            }
//        }
//        
//        //组合包
//        if(elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_PACKAGE) || elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
//        {
//            IDataset offerGroupRelList = saleActiveData.getDataset("OFFER_GROUP_REL_LIST");
//            if(IDataUtil.isNotEmpty(offerGroupRelList))
//            {
//                for(int i=0; i<offerGroupRelList.size(); i++)
//                {
//                    IData offerGroupRelData = offerGroupRelList.getData(i);
//                    IDataset groupComRelList = offerGroupRelData.getDataset("GROUP_COM_REL_LIST");
//                    String groupId = offerGroupRelData.getString("GROUP_ID");
//                    String groupType = offerGroupRelData.getString("GROUP_TYPE");
//                    if(elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
//                    {
//                        //有最大最小元素限制的，包倒成了组，限制元素在GROUP_COM_REL里，GROUP_TYPE=0
//                        if("0".equals(groupType))
////                        if(StringUtils.equals(packageId, groupId))//说明原包导成了组
//                        {
//                            if(IDataUtil.isNotEmpty(groupComRelList))
//                            {
//                                for(int j=0; j<groupComRelList.size(); j++)
//                                {
//                                    IData element = new DataMap();
//                                    IData groupComRelData = groupComRelList.getData(j);
//                                    String offerCode = groupComRelData.getString("OFFER_CODE");
//                                    String offerName = groupComRelData.getString("OFFER_NAME");
//                                    String offerType = groupComRelData.getString("OFFER_TYPE");
//                                    String description = groupComRelData.getString("DESCRIPTION");
//                                    
//                                    String relId = groupComRelData.getString("REL_ID");
//                                    IData elementInfo = UPackageElementInfoQry.getPkgElemExtCha(relId, "TD_B_PACKAGE_ELEMENT", "2");
//                                    if("|D|S|C|Z|".indexOf(offerType) > 0 && "B".equals(elementInfo.getString("RSRV_TAG1")))
//                                    {
//                                        continue;
//                                    }
//                                    
//                                    element.put("RSRV_TAG3", elementInfo.getString("RSRV_TAG3"));
//                                    element.put("DISCNT_CODE", offerCode);
//                                    element.put("DISCNT_NAME", offerName);
//                                    element.put("DISCNT_EXPLAIN", description);
//                                    element.put("MAIN_TAG", groupComRelData.getString("IS_MAIN"));
//                                    element.put("ELEMENT_TYPE_CODE", offerType);
//                                    element.put("ELEMENT_ID", offerCode);
//                                    
//                                    String selectFlag = groupComRelData.getString("SELECT_FLAG");
//                                    if("0".equals(selectFlag))
//                                    {
//                                        element.put("FORCE_TAG", "1");
//                                    }else if("1".equals(selectFlag))
//                                    {
//                                        element.put("DEFAULT_TAG", "1");
//                                    }else if("2".equals(selectFlag))
//                                    {
//                                        element.put("DEFAULT_TAG", "0");
//                                    }
//                                    
//                                    IDataset offerModeInfo = UpcCall.queryGroupComEnableModeByGroupIdOfferId(groupId, offerType, offerCode);
//                                    if(IDataUtil.isNotEmpty(offerModeInfo))
//                                    {
//                                        element.putAll(offerModeInfo.getData(0));
//                                    }
//                                    
//                                    results.add(element);
//                                }
//                            }
//                        }
//                    }else
//                    {
//                        if("9".equals(groupType))
//                        {
//                            offerGroupRelData.put("ELEMENT_ID", groupId);
//                            offerGroupRelData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PACKAGE);
//                            offerGroupRelData.put("COMBINE_ID", offerGroupRelData.getString("GROUP_ID"));
//                            offerGroupRelData.put("COMBINE_NAME", offerGroupRelData.getString("GROUP_NAME"));
//                            offerGroupRelData.put("COMBINE_DESC", offerGroupRelData.getString("DESCRIPTION"));
//                            
//                            IData groupCha = UpcCall.queryTempChaByCond(groupId, "TD_B_PACKAGE");
//                            offerGroupRelData.put("COMBINE_ENABLE_DESC", groupCha.getString("RSRV_STR1"));
//        
//                            String selectFlag = offerGroupRelData.getString("SELECT_FLAG");//0必选，1可选默选，2可选
//                            if("0".equals(selectFlag))
//                            {
//                                offerGroupRelData.put("FORCE_TAG", "1");
//                            }else if("1".equals(selectFlag))
//                            {
//                                offerGroupRelData.put("DEFAULT_TAG", "1");
//                            }else if("2".equals(selectFlag))
//                            {
//                                offerGroupRelData.put("DEFAULT_TAG", "0");
//                            }
//                            
//                            results.add(offerGroupRelData);
//                        }
//                    }
//                }
//            }
//        }
//        return results;
    }

    public static void dealParam(IData param, String elementTypeCode) throws Exception
    {
        String offerCode = param.getString("OFFER_CODE");
        IData cha = UpcCall.qryOfferComChaTempChaByCond(offerCode, elementTypeCode);
        
        if(BofConst.ELEMENT_TYPE_CODE_SALEGOODS.equals(elementTypeCode))
        {
            param.put("GOODS_ID", param.getString("OFFER_CODE"));
            param.put("GOODS_NAME", param.getString("OFFER_NAME"));
            param.put("GOODS_EXPLAIN", param.getString("DESCRIPTION"));
        }else if(BofConst.ELEMENT_TYPE_CODE_CREDIT.equals(elementTypeCode))
        {
            param.put("CREDIT_GIFT_ID", param.getString("OFFER_CODE"));
            param.put("CREDIT_GIFT_EXPLAIN", param.getString("OFFER_NAME"));
            param.put("CREDIT_GIFT_MONTHS", cha.getString("CREDIT_GIFT_MONTHS"));
            param.put("CREDIT_VALUE", cha.getString("CREDIT_VALUE"));
        }else if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
        {
            param.put("DISCNT_CODE", param.getString("OFFER_CODE"));
            param.put("DISCNT_NAME", param.getString("OFFER_NAME"));
            param.put("DISCNT_EXPLAIN", param.getString("DESCRIPTION"));
        }else if(BofConst.ELEMENT_TYPE_CODE_SCORE.equals(elementTypeCode))
        {
            param.put("SCORE_DEDUCT_ID", param.getString("OFFER_CODE"));
            param.put("PAYMENT_ID", cha.getString("PAYMENT_ID"));
            param.put("DEPOSIT_TAG", cha.getString("DEPOSIT_TAG"));
            param.put("DEPOSIT_RATE", cha.getString("DEPOSIT_RATE"));
            param.put("SCORE_VALUE", cha.getString("SCORE_VALUE"));
            param.put("SCORE_TYPE_CODE", cha.getString("SCORE_TYPE_CODE"));
        }else if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
        {
            param.put("SERVICE_ID", param.getString("OFFER_CODE"));
            param.put("SERVICE_NAME", param.getString("OFFER_NAME"));
        }
        
        param.put("MAIN_TAG", param.getString("IS_MAIN"));
        param.put("ELEMENT_TYPE_CODE", param.getString("OFFER_TYPE"));
        param.put("ELEMENT_ID", param.getString("OFFER_CODE"));
    }
    
    public static boolean isInCommparaParamCodeConfigs(String objId, IDataset configs)throws Exception
    {
        if(IDataUtil.isEmpty(configs))
        {
            return false;
        }
        
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            if (objId.equals(paramCode))
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isCommparaConfigs3119(String objId, IDataset configs)throws Exception
    {
        if(IDataUtil.isEmpty(configs))
        {
            return true;
        }
        
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARA_CODE4");
            String paraCode16 = config.getString("PARA_CODE16");
            if (objId.equals(paramCode)&&"Y".equals(config.getString("PARA_CODE8")))
            {
            	if("Y".equals(paraCode16)){
            		 return false;
            	}
            }
        }
        return true;
    }
    
    public static IDataset filterSalePackagesByParamAttr526New(IDataset salePackages, String productId, String campnType, boolean isBuildExtCha,IData input) throws Exception
    {
        if(IDataUtil.isEmpty(salePackages))
        {
            return salePackages;
        }
        
        IDataset results = new DatasetList();
        
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "526", CSBizBean.getTradeEparchyCode());
        IDataset configs3119 = CommparaInfoQry.getCommparaByParaAttr("CSM", "3119", CSBizBean.getTradeEparchyCode());
        for(int i=0;i<salePackages.size();i++)
        {
            IData salePackage = salePackages.getData(i);
            String offerCode = salePackage.getString("OFFER_CODE");
            if(StringUtils.isBlank(offerCode))
            {
                offerCode = salePackage.getString("PACKAGE_ID");
            }
            
            if(isInCommparaParamCodeConfigs(offerCode, configs))
            {
        		if(isCommparaConfigs3119(offerCode, configs3119))
                {
        			continue;
                }
            		 
            }
            
            if(StringUtils.isBlank(salePackage.getString("PACKAGE_ID")))
            {
                salePackage.put("PACKAGE_ID", offerCode);
            }
            
            if(StringUtils.isBlank(salePackage.getString("CAMPN_TYPE")))
            {
                salePackage.put("CAMPN_TYPE", campnType);
            }
            
            if(StringUtils.isBlank(salePackage.getString("PRODUCT_ID")))
            {
                salePackage.put("PRODUCT_ID", productId);
            }
            
            String offerName = salePackage.getString("OFFER_NAME");
            if(StringUtils.isNotBlank(offerName))
            {
                salePackage.put("PACKAGE_NAME", offerName);
            }
            
            if(isBuildExtCha)
            {
                IData extCha = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, "TD_B_PACKAGE_EXT").getData(0);
                if(!salePackage.containsKey("RSRV_STR2"))
                {
                    salePackage.put("RSRV_STR2", extCha.getString("RSRV_STR2"));
                }
                
                if(!salePackage.containsKey("RSRV_STR5"))
                {
                    salePackage.put("RSRV_STR5", extCha.getString("RSRV_STR5"));
                }
                
                if(!salePackage.containsKey("RSRV_STR20"))
                {
                    salePackage.put("PACKAGE_TYPE", extCha.getString("RSRV_STR20"));
                }
            }
            results.add(salePackage);
        }
        
        return results;
    }
    
    public static IDataset filterSalePackagesByParamAttr526(IDataset salePackages, String productId, String campnType, boolean isBuildExtCha) throws Exception
    {
        if(IDataUtil.isEmpty(salePackages))
        {
            return salePackages;
        }
        
        IDataset results = new DatasetList();
        
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "526", CSBizBean.getTradeEparchyCode());
        
        for(int i=0;i<salePackages.size();i++)
        {
            IData salePackage = salePackages.getData(i);
            String offerCode = salePackage.getString("OFFER_CODE");
            if(StringUtils.isBlank(offerCode))
            {
                offerCode = salePackage.getString("PACKAGE_ID");
            }
            
            if(isInCommparaParamCodeConfigs(offerCode, configs))
            {
                continue;
            }
            
            if(StringUtils.isBlank(salePackage.getString("PACKAGE_ID")))
            {
                salePackage.put("PACKAGE_ID", offerCode);
            }
            
            if(StringUtils.isBlank(salePackage.getString("CAMPN_TYPE")))
            {
                salePackage.put("CAMPN_TYPE", campnType);
            }
            
            if(StringUtils.isBlank(salePackage.getString("PRODUCT_ID")))
            {
                salePackage.put("PRODUCT_ID", productId);
            }
            
            String offerName = salePackage.getString("OFFER_NAME");
            if(StringUtils.isNotBlank(offerName))
            {
                salePackage.put("PACKAGE_NAME", offerName);
            }
            
            if(isBuildExtCha)
            {
                IData extCha = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, "TD_B_PACKAGE_EXT").getData(0);
                if(!salePackage.containsKey("RSRV_STR2"))
                {
                    salePackage.put("RSRV_STR2", extCha.getString("RSRV_STR2"));
                }
                
                if(!salePackage.containsKey("RSRV_STR5"))
                {
                    salePackage.put("RSRV_STR5", extCha.getString("RSRV_STR5"));
                }
                
                if(!salePackage.containsKey("RSRV_STR20"))
                {
                    salePackage.put("PACKAGE_TYPE", extCha.getString("RSRV_STR20"));
                }
            }
            results.add(salePackage);
        }
        
        return results;
    }
    
    public static IDataset filterSaleProductsByParamAttr522(IDataset saleProducts, String campnType) throws Exception
    {
        if(IDataUtil.isEmpty(saleProducts))
        {
            return saleProducts;
        }
        
        IDataset results = new DatasetList();
        
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "522", CSBizBean.getTradeEparchyCode());
        for(int i=0;i<saleProducts.size();i++)
        {
            IData saleProduct = saleProducts.getData(i);
            String catalogId = saleProduct.getString("CATALOG_ID");
            if(isInCommparaParamCodeConfigs(catalogId, configs))
            {
                continue;
            }
            
            saleProduct.put("PRODUCT_ID", catalogId);
            saleProduct.put("PRODUCT_NAME", saleProduct.getString("CATALOG_NAME"));
            saleProduct.put("CAMPN_TYPE", campnType);
            
            results.add(saleProduct);
        }
        
        return results;
    }
    
    public static void buildSalePackagesByPackageExt(IDataset salePackages, String[] fieldNames) throws Exception
    {
        if(IDataUtil.isEmpty(salePackages))
        {
            return;
        }
        
        if(fieldNames==null || fieldNames.length==0)
        {
            return;
        }
        
        
        for(int i=0;i<salePackages.size();i++)
        {
            IData salePackage = salePackages.getData(i);
            String packageId = salePackage.getString("PACKAGE_ID");
            IData extInfo = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT").getData(0);
            
            for(String filedName : fieldNames)
            {
                String fieldValue = extInfo.getString(filedName);
                salePackage.put(filedName, fieldValue);
            }
        }
    }
    
    public static String getPackageExtTagSet1(String packageId, IDataset extInfos) throws Exception
    {
        if(IDataUtil.isNotEmpty(extInfos))
        {
            return extInfos.getData(0).getString("TAG_SET1", "");
        }
        
        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        return packageExtInfos.getData(0).getString("TAG_SET1", "");
    }
    
    public static String getPackageExtTagSet2(String packageId, IDataset extInfos) throws Exception
    {
        if(IDataUtil.isNotEmpty(extInfos))
        {
            return extInfos.getData(0).getString("TAG_SET2", "");
        }
        
        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        return packageExtInfos.getData(0).getString("TAG_SET2", "");
    }
    
    public static String getSaleGoodsTagSet(String goodsId, int index) throws Exception
    {
        IData goodsInfo = UpcCall.qryOfferComChaTempChaByCond(goodsId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        if(index == 1)
        {
            return goodsInfo.getString("ENTER_SALE_STAFF_TAG", "");
        }else if(index == 2)
        {
            return goodsInfo.getString("TERMINAL_SALE_TYPE", "");
        }
        
        return null;
    }
    
    public static IData getPkgLimitInfo(String packageId, String elementTypeCode) throws Exception
    {
        IData result = new DataMap();
        
        IDataset offerGroupRelList = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, packageId);
        if(IDataUtil.isNotEmpty(offerGroupRelList))
        {
            IData offerGroupRelData = offerGroupRelList.getData(0);
            
            IDataset offerComRels = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, elementTypeCode, null);
            int offerComRelSize = offerComRels.size();
            int minNum = -1;
            int maxNum = -1;
            if(offerComRelSize == 0)
            {
                minNum = offerGroupRelData.getInt("MIN_NUM", -1);
                maxNum = offerGroupRelData.getInt("MAX_NUM", -1);
            }else
            {
                minNum = offerGroupRelData.getInt("MIN_NUM", -1);
                maxNum = offerGroupRelData.getInt("MAX_NUM", -1) + offerComRelSize;
            }
            result.put("LIMIT_TYPE", offerGroupRelData.getString("LIMIT_TYPE"));
            result.put("MIN_NUMBER", minNum);
            result.put("MAX_NUMBER", maxNum);
        }else
        {
            result.put("LIMIT_TYPE", "");
            result.put("MIN_NUMBER", "-1");
            result.put("MAX_NUMBER", "-1");
        }
        
        return result;
    }
    
    /**
     * 查询营销包下面所有默认必选元素费用
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getSaleAtiveFeeList(String productId, String packageId) throws Exception
    {
        IDataset tradeFeeDataset = new DatasetList();
        
        //查询下面所有
        IDataset forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "1");
        
        if (IDataUtil.isEmpty(forceAndDefaultElements))
        {
            return null;
        }
        
        for (int i = 0; i < forceAndDefaultElements.size(); i++)
        {
            IData forceAndDefaultElement = forceAndDefaultElements.getData(i);
            //查询营销活动费用配置
            IDataset businessFee = ProductFeeInfoQry.getSaleActiveFee("240", BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, forceAndDefaultElement.getString("ELEMENT_TYPE_CODE"), forceAndDefaultElement.getString("ELEMENT_ID"), productId);
            
            if (IDataUtil.isNotEmpty(businessFee))
            {
                tradeFeeDataset.addAll(businessFee);
            }
        }
        
        return tradeFeeDataset;
    }
    
    public static void sortSaleActivesByTotalNum(IDataset saleActives) throws Exception
    {
    	if(IDataUtil.isEmpty(saleActives))
    	{
    		return;
    	}
    	
    	IDataset saleNums = UserSaleActiveInfoQry.qrySaleActiveTotalNum();
    	
    	if(IDataUtil.isEmpty(saleNums))
    	{
    		return;
    	}
    	
    	for(int i=0;i<saleActives.size();i++)
        {
        	IData cata = saleActives.getData(i);
        	int saleNumSize = saleNums.size();
        	for(int j=0;j<saleNumSize;j++)
        	{
        		IData saleNum = saleNums.getData(j);
        		if(cata.getString("CATALOG_ID").equals(saleNum.getString("PRODUCT_ID")))
        		{
        			cata.put("TOTAL_NUM", saleNum.getInt("TOTAL_NUM"));
        			break;
        		}
        	}
        }
    	DataHelper.sort(saleActives, "CATALOG_ID", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
    	
        DataHelper.sort(saleActives, "TOTAL_NUM", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
    }
    
    public static String getIMSUserOpenTradeId(String serialNumber) throws Exception
    {
        IDataset tradeInfo = TradeInfoQry.queryIMSUserOpenTradeid(serialNumber);

        return IDataUtil.isNotEmpty(tradeInfo) ? tradeInfo.getData(0).getString("TRADE_ID") : "";
    }
    
    public static IData getMaxPackageIdFromUserSaleActiveMerch(List<SaleActiveTradeData> userSaleActives)
    {
        IData info=new DataMap();
        String maxEndDate = "";
        String packageId = "";
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String endDate = userSaleActive.getEndDate();
            if (endDate.compareTo(maxEndDate) > 0)
            {
                maxEndDate = endDate;
                packageId=userSaleActive.getPackageId();
            }
        }
        info.put("MAX_END_DATE", maxEndDate);
        info.put("REL_PACKAGE_ID", packageId);
        return info;
    }
}
