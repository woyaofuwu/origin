package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * 订购\变更安心包时, 修改安心包的endDate为所依赖的长周期/月套餐的最大的一个的endDate.
 * <br />
 * 备注： 当最大endDate的长周期/月套餐退订或者发生变更时，在部分依赖规则里会有判断，必须满足安心包依赖关系limitTag=6的要求.
 *     考虑长周期/月套餐扩容对安心包的影响(暂时不考虑)
 */
public class ProcAnxinbaoEndDateRegAction implements ITradeAction
{
	
	private static transient Logger logger = Logger.getLogger(ProcAnxinbaoEndDateRegAction.class);
	
	/** 安心包资费ID */
	private static String discntId_anxinbao;
	/** 安心包和月套餐、长周期套餐的依赖关系 */
	private static final String element_limit_tag6 = "6";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeAction(BusiTradeData btd) throws Exception
    {
		logger.info("ProcAnxinbaoEndDateRegAction--executeAction--" + btd.getTradeId());
		if(!"PWLW".equals(btd.getRD().getUca().getBrandCode())){
            return;
         }
		IDataset anxinbao = CommparaInfoQry.getCommparaByCode1("CSM","1920","IotAnxinBaoDiscnt","ZZZZ");
		if(IDataUtil.isEmpty(anxinbao)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "数据库中没有配置安心包对应资费编码！");   
		}else{
			discntId_anxinbao = anxinbao.getData(0).getString("PARAM_CODE");
		}
		
		String userId = btd.getRD().getUca().getUserId();
    	if (null == userId || "".equals(userId))
    	{
    		return;
    	}
		
		// 不存在资费台账
		List<DiscntTradeData> discntTradeDatalist = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
    	if (null == discntTradeDatalist || discntTradeDatalist.isEmpty())
    	{
    		return;
    	}
    	
    	logger.info("ProcAnxinbaoEndDateRegAction--executeAction--discntTradeDatalist--size--" + discntTradeDatalist.size());
		
    	// 本次是否安心包订购\变更??
    	IDataset anxinbaoLimitTag6Elements = new DatasetList();
    	DiscntTradeData anxinbaoDiscntTradeData = null;
    	String isRegTrade = "";
    	for (int i = 0; i < discntTradeDatalist.size(); i++)
    	{
    		DiscntTradeData item = discntTradeDatalist.get(i);
    		
    		// 订购、变更、继承安心包
    		if ( true 
    				&& discntId_anxinbao.equals(item.getElementId())  
    				&& "D".equals(item.getElementType())
    				&& (BofConst.MODIFY_TAG_ADD.equals(item.getModifyTag()) || BofConst.MODIFY_TAG_UPD.equals(item.getModifyTag()) || BofConst.MODIFY_TAG_INHERIT.equals(item.getModifyTag()))
    			)
    		{
    			isRegTrade = "REG_TRADE";
    			anxinbaoDiscntTradeData = item;
    			// 查询用户本次添加、变更、继承以及用户在用的长周期包（安心包所依赖的） --报错注释
    			anxinbaoLimitTag6Elements = ElemLimitInfoQry.queryElementLimitByElementIdA("D", discntId_anxinbao, element_limit_tag6, "0898");
    			logger.info("ProcAnxinbaoEndDateRegAction--anxinbaoLimitTag6Elements--" + btd.getTradeId() + "--" + anxinbaoLimitTag6Elements.toString());
    			break;
    		//本次没有订购、变更安心包
    		}
    		if("".equals(isRegTrade)){
    			//判断用户是否已经订购安心包
    			IDataset userAnxinbao = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId, discntId_anxinbao, "0898");
    			if(IDataUtil.isEmpty(userAnxinbao)){//用户没有订购安心包直接返回
    				return ;
       			}else{
       			    // 查询（安心包所依赖的）
        			anxinbaoLimitTag6Elements = ElemLimitInfoQry.queryElementLimitByElementIdA("D", discntId_anxinbao, element_limit_tag6, "0898");
        			logger.info("ProcAnxinbaoEndDateRegAction--anxinbaoLimitTag6Elements--" + btd.getTradeId() + "--" + anxinbaoLimitTag6Elements.toString());
        			
        			List<DiscntTradeData> userTrades = btd.getRD().getUca().getUserDiscnts();
        			for(DiscntTradeData discntTrade:userTrades){
        				if(discntId_anxinbao.equals(discntTrade.getElementId())){
        					isRegTrade = "NO_REG_TRADE";
        					anxinbaoDiscntTradeData = discntTrade;
        					break;
        				}
        			}
        			logger.info("ProcAnxinbaoEndDateRegAction--anxinbaoDiscntTradeData--" + anxinbaoDiscntTradeData.toString() + "--");
    			}
    		}
    	}  
    	// 安心包未有订购或者变更或者所依赖的元素为空，直接返回
    	if (anxinbaoLimitTag6Elements.isEmpty())
    	{
    		return;
    	} 
    	
    	// 只保留长周期套餐、月套餐
    	anxinbaoLimitTag6Elements = DataHelper.filter(anxinbaoLimitTag6Elements, "ELEMENT_TYPE_CODE_B=D");
    	
    	// 得到用户在用的安心包依赖的套餐
    	IDataset userAnxinbaoLimitDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId, "0898"); 
    	if (null != userAnxinbaoLimitDiscnts && !userAnxinbaoLimitDiscnts.isEmpty())
    	{
    		for (int i = userAnxinbaoLimitDiscnts.size() - 1; i >= 0; i--)
    		{
    			IDataset filter = DataHelper.filter(anxinbaoLimitTag6Elements, "ELEMENT_ID_B=" + userAnxinbaoLimitDiscnts.getData(i).getString("DISCNT_CODE"));
        		if (null == filter || filter.isEmpty())
        		{
        			userAnxinbaoLimitDiscnts.remove(i);
        		}
    		}
    	}
    	
    	logger.info("ProcAnxinbaoEndDateRegAction--33--" + btd.getTradeId() + "--" + userAnxinbaoLimitDiscnts.toString()); 
    	
    	// 遍历台账
    	for (int i = discntTradeDatalist.size() - 1; i >= 0 ; i--)
    	{
    		DiscntTradeData discntTradeDataTmp = discntTradeDatalist.get(i);
    		String eleTypeCode = discntTradeDataTmp.getElementType();
    		String eleId = discntTradeDataTmp.getElementId();
    		
    		if (!"D".equals(eleTypeCode))
    		{
    			continue;
    		}
    		
    		// 从用户存量中的安心包所依赖的套餐中去除本次删除的
    		if (BofConst.MODIFY_TAG_DEL.equals(discntTradeDataTmp.getModifyTag()))
    		{
    			IDataset filter = DataHelper.filter(userAnxinbaoLimitDiscnts, "DISCNT_CODE=" + eleId);
    			if (null != filter && !filter.isEmpty())
    			{
    				userAnxinbaoLimitDiscnts.removeAll(filter);
    			}
    			
    			continue;
    		}
    		
    		// 订购、变更、继承 长周期包
    		IDataset filter = DataHelper.filter(anxinbaoLimitTag6Elements, "ELEMENT_ID_B=" + eleId);
    		if (null == filter || filter.isEmpty())
    		{
    			continue;
    		}
    		else
    		{
    			IData limitTag6Ele = new DataMap();
    			limitTag6Ele.put("END_DATE", discntTradeDataTmp.getEndDate());
    			limitTag6Ele.put("ELEMENT_ID", discntTradeDataTmp.getElementId());
    			userAnxinbaoLimitDiscnts.add(limitTag6Ele);
    		}  		
    	}
    	
    	logger.info("ProcAnxinbaoEndDateRegAction--44--" + btd.getTradeId() + "--" + userAnxinbaoLimitDiscnts.toString()); 
    	
    	if (!userAnxinbaoLimitDiscnts.isEmpty())
    	{
    		//IData anxinbao = new DataMap();
    		//anxinbao.put("END_DATE", anxinbaoDiscntTradeData.getEndDate());
    		//userAnxinbaoLimitDiscnts.add(anxinbao);
    		
    		DataHelper.sort(userAnxinbaoLimitDiscnts, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);//时间按降序排序
    		
    		String anxinbaoEndDate = userAnxinbaoLimitDiscnts.getData(0).getString("END_DATE", "");
    		logger.info("ProcAnxinbaoEndDateRegAction--55--" + btd.getTradeId() + "--" + anxinbaoEndDate + "--" + anxinbaoDiscntTradeData.getEndDate()); 
    		if (!anxinbaoEndDate.equals(anxinbaoDiscntTradeData.getEndDate()))
    		{
    			anxinbaoDiscntTradeData.setEndDate(anxinbaoEndDate);  
    			if("NO_REG_TRADE".equals(isRegTrade)){     //从用户资料表中查询出来的数据需要重新登记一下安心包资费台账
					anxinbaoDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
					btd.add(btd.getRD().getUca().getSerialNumber(), anxinbaoDiscntTradeData);
				}
    		} 
    	}
    }
	
}
