
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryScoreRewardSumQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;

/**
 * 功能：积分兑奖汇总小计 作者：GongGuang
 */
public class QueryScoreRewardSumBean extends CSBizBean
{
    /**
     * 查询业务区
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset queryArea(String eparchyCode) throws Exception
    {

        return UAreaInfoQry.qryAreaByParentAreaCode(eparchyCode);
    }

    /**
     * 按县市查询
     * 
     * @param startDate
     * @param endDate
     * @param ruleId
     * @param tradeCityCode
     * @param departKindCode
     * @return
     * @throws Exception
     */
    public IDataset queryCityScoreExchange(String startDate, String endDate, String ruleId, String tradeCityCode, String departKindCode, Pagination pagination) throws Exception
    {
        //订单库与中心库拆分sql  modify by duhj  2017/03/15   原sql GET_SCORE_REWARDSUM2    	
        IDataset scoreSums=QueryScoreRewardSumQry.queryCityScoreExchange(startDate, endDate, ruleId, tradeCityCode, departKindCode,pagination);   	       
        
    	if(IDataUtil.isNotEmpty(scoreSums)){
    		for(int i=0;i<scoreSums.size();i++){
                IData scoreSum = scoreSums.getData(i);
                String rule_id=scoreSum.getString("RULE_ID");
                IDataset scoreRuleNames=ExchangeRuleInfoQry.queryCityScoreExchange2(rule_id);
                String rule_name=scoreRuleNames.getData(0).getString("RULE_NAME");
                scoreSum.put("RULE_NAME", rule_name);
    		}
    	}
        
        return scoreSums;
    }

    /**
     * 查询部门类型
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset queryDepartKind(String eparchyCode) throws Exception
    {

        return QueryScoreRewardSumQry.queryDepartKind(eparchyCode);
    }

    /**
     * 查询兑奖编码
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset queryRules(String eparchyCode) throws Exception
    {

        return QueryScoreRewardSumQry.queryRules(eparchyCode);
    }

    /**
     * 按用户查询
     * 分库了 不分页 duhj
     * @param tradeFlag
     * @param startDate
     * @param endDate
     * @param tradeStaffIdS
     * @param tradeStaffIdE
     * @param tradeDepartId
     * @return
     * @throws Exception
     */
    public IDataset queryUserScoreExchange(String tradeFlag, String startDate, String endDate, String tradeStaffIdS, String tradeStaffIdE, String tradeDepartId) throws Exception
    {
    	//拆分sql,订单表拆分出来,改为jour用户
        IDataset retDs = QueryScoreRewardSumQry.queryUserScoreExchange(tradeFlag, startDate, endDate, tradeStaffIdS, tradeStaffIdE, tradeDepartId);
        
        if(IDataUtil.isNotEmpty(retDs)){
        	 for(int i = 0; i < retDs.size(); i++){ 
        		 String ruleId=retDs.getData(i).getString("RULE_ID");
        	        IDataset dataset = ExchangeRuleInfoQry.queryExchRuleByRuleId(ruleId);//此方法后续要修改调产商品接口，duhj
        	        if(IDataUtil.isNotEmpty(dataset)){
    	                String ruleName=dataset.getData(0).getString("RULE_NAME");
    	                retDs.getData(i).put("RULE_NAME", ruleName);
        	        }
        	        else{
        	        	retDs.remove(i);
        	        	i--;
        	        }

     
        	 }
        }
        
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(retDs))
        {
            int size = retDs.size();
            for (int i = 0; i < size; i++)
            {
                IData temp = retDs.getData(i);
                if ("返销".equals(temp.getString("CANCEL_TAG")))
                {
                    temp.put("SCORE_CHANGED", Math.abs(temp.getInt("SCORE_CHANGED")));
                    temp.put("ACTION_COUNT", "-" + temp.getString("ACTION_COUNT"));
                }
                result.add(temp);
            }
        }
        return result;
    }
}
