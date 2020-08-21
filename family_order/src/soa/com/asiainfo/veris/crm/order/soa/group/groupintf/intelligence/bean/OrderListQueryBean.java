package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr.TradeQueryBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 *
 * 订单列表查询
 *
 * */
public class OrderListQueryBean extends CSBizBean
{

    protected static final Logger logger = Logger.getLogger(OrderListQueryBean.class);
    
    public IData orderListQuery(IData input) throws Exception
    {
        String queryTpye = IDataUtil.chkParam(input,"queryType");
        String queryValue = IDataUtil.chkParam(input,"queryValue");

        IDataset resultList = new DatasetList();
        if("1".equals(queryTpye))  // 1：客户姓名
        {
            resultList = TradeInfoQry.queryTradeInfoByCustName(queryValue);
        }
        else if("2".equals(queryTpye))  // 2：客户证件号码
        {
        	resultList = queryTradeInfoByIdentityId(queryValue);
        }
        else if("3".equals(queryTpye) )  // 3：订单编号
        {
        	input.put("ORDER_ID",queryValue);
            input.put("ROUTE_ID",BizRoute.getRouteId() );
            resultList = TradeQueryBean.queryTradeInfo(input);
        }
        else if("4".equals(queryTpye))  // 3：订单编号
        {
            input.put("ROUTE_ID", BizRoute.getRouteId() );
            input.put("SERIAL_NUMBER",queryValue);
            resultList = TradeQueryBean.queryTradeInfo(input);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,"查询类型取值不正确！");
        }
        if( CollectionUtils.isEmpty( resultList ) ) {
        	CSAppException.apperr(GrpException.CRM_GRP_713,"根据条件查询订单列表不存在！");
        }
        
        return transferDatasOut( resultList );
    } 

    /**
     * 根据证件类型查询台账表表
     * @param psptId
     * @return
     * @throws Exception
     */
    private IDataset queryTradeInfoByIdentityId(String psptId) throws Exception{
        IData param = new DataMap();
        param.put("PSPT_ID", psptId);
        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT  t.* FROM tf_b_trade t,tf_b_order c  ");
        sql.append(" where t.order_id = c.order_id  ");
        sql.append(" and c.PSPT_ID= :PSPT_ID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    /**
     * 根据证件类型查询台账表表
     * @param tradeList
     * @return
     * @throws Exception
     */
    private IData transferDatasOut ( IDataset tradeList) throws Exception{
        IData retDatas = new DataMap();
        IDataset orderList = new DatasetList();
        for (Object object : tradeList) {
        	IData tempDatas = (IData) object;
        	IData orderData = new DataMap();
        	orderData.put("orderNumber", tempDatas.getString("ORDER_ID") );
        	orderData.put("orderType", OrderInfoQueryBean.dealSubcribeType( tempDatas.getString("SUBSCRIBE_TYPE") ));
        	orderData.put("acceptNumber", tempDatas.getString("SERIAL_NUMBER") );
        	orderData.put("customerCode", tempDatas.getString("CUST_ID") );
        	String inModeCode = tempDatas.getString("IN_MODE_CODE");
    		IData channelDatas =  StaticInfoQry.getStaticInfoByTypeIdDataId("IN_MODE_CODE",inModeCode);
    		orderData.put("acceptChannel", channelDatas.getString("DATA_NAME"));
        	orderData.put("acceptOperator", tempDatas.getString("TRADE_STAFF_ID") );
        	orderData.put("acceptTime", tempDatas.getString("ACCEPT_DATE").replaceAll("-", "").replace(" ", "") );
        	if ( "6".equals(tempDatas.getString("SUBSCRIBE_STATE") ) )
        			orderData.put("orderStatus", "1" );
        	else
        		orderData.put("orderStatus", "0" );
        	orderData.put("businessNumber", tempDatas.getString("TRADE_ID") );
        	
        	orderList.add( orderData );
			
		}
        retDatas.put("orderList", orderList );
    	return retDatas ;
    }
}
