
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class DestroyUserNowSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(DestroyUserNowSVC.class);
    private static final long serialVersionUID = 1L;

    /**
     * 查询用户impu信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuInfo(IData input) throws Exception
    {
        return null;
    }

    public IDataset queryUserScore(IData input) throws Exception
    {
        return AcctCall.queryUserScore(input.getString("USEr_ID"));
    }
    
    /**
     * 获取光猫租用信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserOtherInfo(IData input) throws Exception
    {
    	return UserOtherInfoQry.getOtherInfoByCodeUserId(input.getString("USER_ID"),"FTTH");
    }
    
    /**
     * 获取集团宽带光猫租用信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGroupUserOtherInfo(IData input) throws Exception
    {
    	return UserOtherInfoQry.getOtherInfoByCodeUserId(input.getString("USER_ID"),"FTTH_GROUP");
    }
    
    public IDataset getUserInfoBySerailNumber(IData input) throws Exception
    {
    	return UserInfoQry.getUserInfoBySerailNumber("0",input.getString("SERIAL_NUMBER"));
    }
    
    public IData checkUserExistsTopsetBox(IData data)throws Exception{
    	
    	IData result=new DataMap();
    	String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
    	if(serialNumber.indexOf("KD_")>-1) {//宽带账号
    		if(serialNumber.split("_")[1].length()>11)
    		{
    			result.put("WARM_TYPE", "0"); //商务宽带用户不存在魔百合业务
    			return result;
    		}
    		else
    		{
    			serialNumber=serialNumber.split("_")[1];//个人账号
    		}
    	}
    	else {
    		if(serialNumber.length()>11)
    		{
    			serialNumber="KD_"+serialNumber;
    			result.put("WARM_TYPE", "0"); //商务宽带用户不存在魔百合业务
    			return result;
    		}
    	}
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	
    	if (IDataUtil.isEmpty(userInfo))
    	{
    	    CSAppException.appError("-1", "手机用户信息不存在或已销户！");
    	}
    	
    	String userId = userInfo.getString("USER_ID");
    	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
    	if(IDataUtil.isNotEmpty(boxInfos)){
    		result.put("WARM_TYPE", "1");
    	}else{
    		result.put("WARM_TYPE", "0");
    	}
    	
    	return result;
    	
    }
    //查询用户的宽带营销活动
    public IDataset checkWidenetSaleActive(IData data)throws Exception{
    	String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
    	if(serialNumber.indexOf("KD_")>-1) {//宽带账号
    		if(serialNumber.split("_")[1].length()>11)
    		{
    		}
    		else
    		{
    			serialNumber=serialNumber.split("_")[1];//个人账号
    		}
    	}
    	else {
    		if(serialNumber.length()>11)
    		{
    			serialNumber="KD_"+serialNumber;
    		}
    	}
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	String userId = userInfo.getString("USER_ID");
    	IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PARAM_CODE", data.getString("TRADE_TYPE_CODE"));
        IDataset dt = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES3", params);
        IData result = new DataMap();
        IDataset res_dt = new DatasetList();
        if (dt!=null && dt.size()>0)
        {
        	result.putAll(dt.getData(0));
//        	String s_date = dt.getData(0).getString("START_DATE");
//        	int smonth= dt.getData(0).getInt("MONTHS");
//        	String e_date=SysDateMgr.addMonths(s_date, smonth);
//        	e_date = SysDateMgr.addSecond(e_date, -1);
//        	result.put("END_DATE_NEW", e_date);
        	res_dt.add(result);
        }
//        IDataset dt = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WADENET_1PULS_PREFER", params);
//        if (dt!=null && dt.size()>0)
//        {
//        	params.putAll(dt.getData(0));
//        	String discnt = dt.getData(0).getString("DISCNT_CODE");
//        	IData dis = DiscntInfoQry.getDiscntInfoByCode2(discnt);
//        	String discntname=dis.getString("DISCNT_NAME");
//        	params.put("DISCNT_NAME", discntname);
//        	String prod = dt.getData(0).getString("PRODUCT_ID");
//        	IData pro = ProductInfoQry.getProductInfo(prod, "0898");
//        	String pro_name = pro.getString("PRODUCT_NAME");
//        	params.put("PRODUCT_NAME", pro_name);
//        }
        return res_dt;
    }
    
    //当拆机完工后，要执行的终止营销活动的处理类，在拆机完工后修改定期执行的时间，自动执行处理类
    public IDataset dealWidenetActiveCancel(IData mainTrade)throws Exception{
    	IDataset result = new DatasetList();
    	
		String tradeId = mainTrade.getString("TRADE_ID");
		// 查历史台账 如存在未返销的 才进行处理
		IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
		if (IDataUtil.isNotEmpty(mainHiTrade))
		{
//			String userId = mainTrade.getString("USER_ID");
			String serialNumber = mainTrade.getString("SERIAL_NUMBER");
			
			//不知道为何取出来的号码和userid还是宽带的号码，只能重新再处理下
			String deal_cond = mainTrade.getString("DEAL_COND","");
			IData temp = new DataMap(deal_cond);
			
			String SERIAL_NUMBER_A = temp.getString("SERIAL_NUMBER","");
			String userId = temp.getString("USER_ID","");
			if ("".equals(SERIAL_NUMBER_A))
			{
				if(serialNumber.indexOf("KD_")>-1) {//宽带账号
		    		if(serialNumber.split("_")[1].length()>11)
		    			SERIAL_NUMBER_A = serialNumber;//商务宽带
		    		else
		    			SERIAL_NUMBER_A = serialNumber.split("_")[1];//个人账号
		    	}
		    	else {
		    		if(serialNumber.length()>11)
		    			SERIAL_NUMBER_A="KD_"+serialNumber;
		    		else
		    			SERIAL_NUMBER_A= serialNumber;
		    	}
		        IDataset ret = UserInfoQry.getUserinfo(SERIAL_NUMBER_A);
		        if(IDataUtil.isEmpty(ret))
		        {
		        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到对应的用户信息，请检查数据是否完整！["+SERIAL_NUMBER_A+"]");
		        }
		        userId = ret.getData(0).getString("USER_ID","");
			}
	    	
			
			String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
			String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
			String tradeCityCode = mainTrade.getString("TRADE_CITY_CODE");
			String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
			
			String cancelDate = SysDateMgr.getLastDateThisMonth();//获取当月月底日期
			//查询要取消的营销活动
			IData params = new DataMap();
	        params.put("USER_ID", userId);
	        params.put("PARAM_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
	        IDataset cancelSaleActiveList = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES3", params);
	        
	        if (IDataUtil.isNotEmpty(cancelSaleActiveList))
			{
				for (int i = 0, size = cancelSaleActiveList.size(); i < size; i++)
				{
					IData cancelSale = cancelSaleActiveList.getData(i);
					
					if(SysDateMgr.compareTo(cancelDate, cancelSale.getString("END_DATE")) >=0)
					{
						//如果本月底大于等于营销活动的结束时间，不用取消营销活动，兼容历史数据
						continue;
					}
					
					IData cancelParam = new DataMap();
					cancelParam.put("SERIAL_NUMBER", SERIAL_NUMBER_A);
					cancelParam.put("PRODUCT_ID", cancelSale.getString("PRODUCT_ID"));
					cancelParam.put("PACKAGE_ID", cancelSale.getString("PACKAGE_ID"));
					cancelParam.put("RELATION_TRADE_ID", cancelSale.getString("RELATION_TRADE_ID"));
					cancelParam.put("FORCE_END_DATE", cancelDate);
					cancelParam.put("TRADE_STAFF_ID", tradeStaffId);
					cancelParam.put("TRADE_DEPART_ID", tradeDepartId);
					cancelParam.put("TRADE_CITY_CODE", tradeCityCode);
					cancelParam.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
					cancelParam.put("REMARK", "宽带拆机，宽带特殊拆机-营销活动终止");
					cancelParam.put("NO_TRADE_LIMIT", "TRUE");
					cancelParam.put("SKIP_RULE", "TRUE");

					IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);

					IData returnData = new DataMap();
					returnData.clear();
					returnData.put("宽带拆机，宽带特殊拆机营销活动终止:", "ORDER_ID=[" + callData.getString("ORDER_ID", "") + "]," + "TRADE_ID=[" + callData.getString("TRADE_ID", "") + "]" + "FORCE_END_DATE=[" + cancelParam.getString("FORCE_END_DATE", "") + "]");

					result.add(returnData);
				}
			}
			//REQ202003180001 “共同战疫宽带助力”活动开发需求
			//非宽带营销活动终止
			DataMap param = new DataMap();
			param.put("SERIAL_NUMBER",SERIAL_NUMBER_A);
			param.put("USER_ID",userId);
            param.put("CHECK_SN_TAG","1");
			IDataset otherActives = DestroyUserNowBean.checkWideOtherSaleActive(param);
			if(IDataUtil.isNotEmpty(otherActives)){
				for (int i = 0; i < otherActives.size(); i++) {
					param.clear();

					param.put("SERIAL_NUMBER",SERIAL_NUMBER_A);
					param.put("USER_ID",userId);
					param.put("PRODUCT_ID",otherActives.getData(i).getString("PRODUCT_ID"));
					param.put("PACKAGE_ID",otherActives.getData(i).getString("PACKAGE_ID"));
					param.put("RELATION_TRADE_ID",otherActives.getData(i).getString("RELATION_TRADE_ID"));
					param.put("PRODUCT_MODE",otherActives.getData(i).getString("PRODUCT_MODE"));
					param.put("FEE",otherActives.getData(i).getString("FEE"));
					DestroyUserNowBean.cancelWideOtherSaleActive(param);
				}
			}

		}
		
		return result;
    }
    
    /**
     * BUS202005130026  宽带畅享服务特权活动需求
     * @author liwei29
     * @prama 宽带拆机终止517营销活动
     * @throws Exception
     */
    public  IDataset widenetDroopStopActive(IData mainTrade)throws Exception{
    	
        IDataset result = new DatasetList();
    	
	
//			String userId = mainTrade.getString("USER_ID");
			String serialNumber = mainTrade.getString("SERIAL_NUMBER");
			
			//不知道为何取出来的号码和userid还是宽带的号码，只能重新再处理下
			String deal_cond = mainTrade.getString("DEAL_COND","");
			IData temp = new DataMap(deal_cond);
			
			String SERIAL_NUMBER_A = temp.getString("SERIAL_NUMBER","");
			String userId = temp.getString("USER_ID","");
			if ("".equals(SERIAL_NUMBER_A))
			{
				if(serialNumber.indexOf("KD_")>-1) {//宽带账号
		    		if(serialNumber.split("_")[1].length()>11)
		    			SERIAL_NUMBER_A = serialNumber;//商务宽带
		    		else
		    			SERIAL_NUMBER_A = serialNumber.split("_")[1];//个人账号
		    	}
		    	else {
		    		if(serialNumber.length()>11)
		    			SERIAL_NUMBER_A="KD_"+serialNumber;
		    		else
		    			SERIAL_NUMBER_A= serialNumber;
		    	}
		        IDataset ret = UserInfoQry.getUserinfo(SERIAL_NUMBER_A);
		        if(IDataUtil.isEmpty(ret))
		        {
		        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到对应的用户信息，请检查数据是否完整！["+SERIAL_NUMBER_A+"]");
		        }
		        userId = ret.getData(0).getString("USER_ID","");
			}
	    	
			
			String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
			String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
			String tradeCityCode = mainTrade.getString("TRADE_CITY_CODE");
			String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
			
			String cancelDate = SysDateMgr.getLastDateThisMonth();//获取当月月底日期
			//查询要取消的营销活动
			IData params = new DataMap();
	        params.put("USER_ID", userId);
	        IDataset speedUpSaleActives = UserSaleActiveInfoQry.querySaleInfoByUserIdAndCommpara(userId);
	        
	        if (IDataUtil.isNotEmpty(speedUpSaleActives))
			{
				for (int i = 0, size = speedUpSaleActives.size(); i < size; i++)
				{
					IData cancelSale = speedUpSaleActives.getData(i);
					
					IData cancelParam = new DataMap();
					cancelParam.put("SERIAL_NUMBER", SERIAL_NUMBER_A);
					cancelParam.put("PRODUCT_ID", cancelSale.getString("PRODUCT_ID"));
					cancelParam.put("PACKAGE_ID", cancelSale.getString("PACKAGE_ID"));
					cancelParam.put("RELATION_TRADE_ID", cancelSale.getString("RELATION_TRADE_ID"));
					cancelParam.put("FORCE_END_DATE", cancelDate);
					cancelParam.put("TRADE_STAFF_ID", tradeStaffId);
					cancelParam.put("TRADE_DEPART_ID", tradeDepartId);
					cancelParam.put("TRADE_CITY_CODE", tradeCityCode);
					cancelParam.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
					cancelParam.put("REMARK", "517营销活动终止");
					cancelParam.put("NO_TRADE_LIMIT", "TRUE");
					cancelParam.put("SKIP_RULE", "TRUE");

					IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);

					IData returnData = new DataMap();
					returnData.clear();
					returnData.put("517营销活动终止:", "ORDER_ID=[" + callData.getString("ORDER_ID", "") + "]," + "TRADE_ID=[" + callData.getString("TRADE_ID", "") + "]" + "FORCE_END_DATE=[" + cancelParam.getString("FORCE_END_DATE", "") + "]");

					result.add(returnData);
				}
			}
		
    	
    	
    	return result;
    }
    
    
    /**
     * 预约拆机时，获取预约拆机时间
     * @param param
     * @throws Exception
     */
    public IDataset getBookTimeList(IData param) throws Exception
    {
    	String nextMonthFirst = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        String nextMonthFirstTwo = SysDateMgr.addMonths(nextMonthFirst, 1);
        String nextMonthFirstThree = SysDateMgr.addMonths(nextMonthFirst, 2);
        
        IDataset bookTimeList = new DatasetList();
        
        IData bookTime = new DataMap();
        bookTime.put("DATA_ID", nextMonthFirst);
        bookTime.put("DATA_NAME", nextMonthFirst);
        bookTimeList.add(bookTime);
        
        IData bookTime2 = new DataMap();
        bookTime2.put("DATA_ID", nextMonthFirstTwo);
        bookTime2.put("DATA_NAME", nextMonthFirstTwo);
        bookTimeList.add(bookTime2);
        
        IData bookTime3 = new DataMap();
        bookTime3.put("DATA_ID", nextMonthFirstThree);
        bookTime3.put("DATA_NAME", nextMonthFirstThree);
        bookTimeList.add(bookTime3);
        
    	return bookTimeList;
    }
    
    /**
     * 校验预约销号时间
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkDestroyTime(IData param) throws Exception
    {
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	String destroyTime = param.getString("DESTROY_TIME","");
    	String phoneNumber = "";
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0");
    	result.put("X_RESULTINFO", "OK");
    	if(serialNumber.startsWith("KD_"))
    	{
    		phoneNumber = serialNumber.substring(3,serialNumber.length());
    	}
    	else
    	{
    		phoneNumber = serialNumber ;
    	}
    	
    	//校验预约拆机时间不能为空
    	if(destroyTime == null || "".equals(destroyTime))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "预约拆机时间不能为空，请选择!");
    	}
    	
    	if(serialNumber == null || "".equals(serialNumber))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能为空，请检查!");
    	}
    	
    	//校验营销活动时间
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(phoneNumber);
    	if(IDataUtil.isNotEmpty(userInfo))
    	{
    		String userId = userInfo.getString("USER_ID");
            IDataset dt = CParamQry.getLimitActives(userId, param.getString("TRADE_TYPE_CODE"));
            
            if(dt != null && dt.size() > 0)
            {
            	for(int i = 0 ; i < dt.size() ; i++)
            	{
            		//判断是结束时间
            		String endTime = dt.getData(i).getString("END_DATE");
            		if(SysDateMgr.compareTo(destroyTime, endTime) <= 0 )
            		{
            			//不允许
            			result.put("X_RESULTCODE", "-1");
            			result.put("X_RESULTINFO", "您选择的预约拆机时间为[" + destroyTime + "]您当前有营销活动结束时间为[" + endTime + "],不能办理预约拆机!");
            			break;
            		}
            		
            		String startDate = dt.getData(i).getString("START_DATE");
            		//预约时间不能大于首免期
            		if(SysDateMgr.compareTo(destroyTime, startDate) < 0)
                	{
                		//销户时间不能大于首免期
                		result.put("X_RESULTCODE", "-1");
            			result.put("X_RESULTINFO", "首免期内不能办理预约拆机!");
            			break;
                	}
            	}
            }
    	}

        //判断包年套餐
        if(!serialNumber.startsWith("KD_") && serialNumber.length() == 11)
    	{
        	serialNumber = "KD_" + serialNumber;
    	}
        
        IData kdUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(kdUserInfo))
        {
        	String kdUserId = kdUserInfo.getString("USER_ID");
        	IData inParam = new DataMap();
        	inParam.put("USER_ID", kdUserId);
        	inParam.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
            IDataset userDiscntInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_DISCNT2", inParam);
            if(userDiscntInfos != null && userDiscntInfos.size() >0)
            {
                
            	for(int i = 0 ; i < userDiscntInfos.size(); i++ )
            	{
            		String endTime = userDiscntInfos.getData(i).getString("END_DATE");
            		if(SysDateMgr.compareTo(destroyTime, endTime) <= 0 )
            		{
            			//不允许
            			result.put("X_RESULTCODE", "-1");
            			result.put("X_RESULTINFO", "您选择的预约拆机时间为[" + destroyTime + "]您当前的包年套餐结束时间为[" + endTime + "],不能办理预约拆机!");
            			break;
            		}
            	}
            }
        }
        
        String openDate = kdUserInfo.getString("OPEN_DATE","");
        if(openDate != null && !"".equals(openDate))
        {
        	if(SysDateMgr.compareTo(destroyTime, openDate) < 0 )
        	{
        		result.put("X_RESULTCODE", "-1");
    			result.put("X_RESULTINFO", "首免期内不能办理预约拆机!");
        	}
        }
        
        return result;
    }
    
    /**
     * 查询租赁光猫
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUserModemRent(IData param) throws Exception{
    	String userId = param.getString("USER_ID","");
    	if(userId != null && !"".equals(userId))
    		return UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"FTTH");
    	return null;
    }
    
    /**
     * REQ201612080012_优化手机销户关联宽带销号的相关规则
     * <br/>
     * 资源调用接口：1、资源在做号码回收时调用此接口，如果接口返回失败则该号码不回收；
     *  2、判断该号码下有没有宽带，如果有宽带则在到期处理表里插入一条工单（立即执行的），并且返回成功。如果该号码下没有宽带也返回成功；
		3、到期工单执行时：
		1）生成宽带拆机工单；
		2）对光猫押金做沉淀处理； 
		3）对包年剩余费用做沉淀处理；
	 *@author zhuoyingzhi
	 *@date 20180302
     */
    public IData checkWideInfoAndDestroy(IData input) throws Exception
    {
    	return DestroyUserNowBean.checkWideInfoAndDestroy(input);
    }
    
    /**
     * REQ201612080012_优化手机销户关联宽带销号的相关规则
     * <br/>
     * 到期处理宽带拆机接口
     * SS.DestroyUserNowSVC.wideInfoAndDestroyExpireDeal
     * @author zhuoyingzhi
     * @date 20180302
     * */
    public IDataset wideInfoAndDestroyExpireDeal(IData input) throws Exception
    {
    	return DestroyUserNowBean.wideInfoAndDestroyExpireDeal(input); 
    }

	/**
	 * REQ202003180001 “共同战疫宽带助力”活动开发需求
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset checkWideOtherSaleActive(IData input) throws Exception{
		return DestroyUserNowBean.checkWideOtherSaleActive(input);
	}
}
