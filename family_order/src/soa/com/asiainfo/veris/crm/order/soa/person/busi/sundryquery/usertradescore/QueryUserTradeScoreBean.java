
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.usertradescore;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserTradeScoreQry;

public class QueryUserTradeScoreBean extends CSBizBean
{
    /**
     * 根据trade_id查询积分兑换明细项信息
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryScoreDetailInfoByTradeId(IData inparam, Pagination pagination) throws Exception
    {
        IDataset dataset = QueryUserTradeScoreQry.queryScoreDetailInfoByTradeId(inparam.getString("TRADE_ID"), inparam.getString("CANCEL_TAG"), pagination);

        return dataset;
    }

    /**
     * 根据trade_id查询转预存受益号码信息
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset querySNByTradeId(IData inparam, Pagination pagination) throws Exception
    {
        //因订单表与资料表没在一个库了，所以拆分 本次改造duhj
        String tradeId = inparam.getString("TRADE_ID", "");
        IDataset dataset = QueryUserTradeScoreQry.querySNByTradeId(tradeId, pagination);
        if(IDataUtil.isNotEmpty(dataset)){
        	 for(int i = 0; i < dataset.size(); i++){ 
        		 String userId=dataset.getData(i).getString("USER_ID");
        	        IDataset dataset2 = QueryUserTradeScoreQry.querySNByTradeId2(userId);
	                String sn=dataset2.getData(0).getString("SERIAL_NUMBER");
	                dataset.getData(i).put("SERIAL_NUMBER", sn);
     
        	 }
        }
        
        return dataset;
    }

    /**
     * 功能：用于积分兑换明细查询 作者：GongGuang
     */
    public IDataset queryUserTradeScore(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = BizRoute.getRouteId();
        String serialNum = data.getString("SERIAL_NUMBER", "");
        String acceptStart = data.getString("ACCEPT_START", "");
        String acceptEnd = data.getString("ACCEPT_END", "");
        //因订单表与资料表没在一个库了，所以拆分 本次改造duhj
        IDataset dataSet = QueryUserTradeScoreQry.queryUserTradeScore(serialNum, routeEparchyCode, acceptStart, acceptEnd, page);
       
        if(IDataUtil.isNotEmpty(dataSet)){
        	
             for(int i = 0; i < dataSet.size(); i++){
            	 IData  temp=dataSet.getData(i);
		        String tradeTypeCode=temp.getString("TRADE_TYPE_CODE");
		        String tradeStaffId =temp.getString("TRADE_STAFF_ID");
			        IDataset dataSet2 = QueryUserTradeScoreQry.queryUserTradeScore2(tradeTypeCode, tradeStaffId,routeEparchyCode);
			        if(IDataUtil.isNotEmpty(dataSet2)){
			            IData resultDataTemp = dataSet2.getData(0);
			            String tradeType=resultDataTemp.getString("TRADE_TYPE");
			            String staffName=resultDataTemp.getString("STAFF_NAME");
			            temp.put("TRADE_TYPE", tradeType);
			            temp.put("STAFF_NAME", staffName);
			          }
        

        
        	  }
         }
        
        
        
        return dataSet;
    }
}
