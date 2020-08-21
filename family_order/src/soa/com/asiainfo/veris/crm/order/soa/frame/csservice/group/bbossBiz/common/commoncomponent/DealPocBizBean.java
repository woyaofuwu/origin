
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;



/**
 * @description 用于处理和对讲业务
 * @author xunyl
 * @date 2016年1月21日
 */
public class DealPocBizBean extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     *@description 和对讲业务成员归档时，新增APN服务记录
     *@author xunyl
     *@date 2016-01-21 
     */
    public static void rigistApnSeviceTrd(String memberUserId,String tradeId)throws Exception{
    	 IData svcData = new DataMap();       
         svcData.put("USER_ID", memberUserId);
         svcData.put("SERVICE_ID", "146");//与服开商定的APN服务号
         svcData.put("MODIFY_TAG", "1");
         svcData.put("SERV_PARA1", "");
         svcData.put("SERV_PARA2", "");
         svcData.put("SERV_PARA3", "");
         svcData.put("SERV_PARA4", "");
         svcData.put("SERV_PARA5", "");
         svcData.put("SERV_PARA6", "");
         svcData.put("SERV_PARA7", "");
         svcData.put("SERV_PARA8", "");
         svcData.put("START_DATE", SysDateMgr.getSysTime());
         svcData.put("END_DATE", SysDateMgr.getTheLastTime());
         svcData.put("TRADE_ID", tradeId);
         svcData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
         svcData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
         svcData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
         svcData.put("UPDATE_TIME", SysDateMgr.getSysTime());
         Dao.insert(TradeTableEnum.TRADE_SVC.getValue(), svcData);        
    }

    /**
     *@description 和对讲业务成员归档时，新增APN服务记录
     *@author xunyl
     *@date 2016-01-21
     */
    public static IData rigistApnSeviceData(String memberUserId)throws Exception{
        IData svcData = new DataMap();
        svcData.put("USER_ID", memberUserId);
        svcData.put("SERVICE_ID", "146");//与服开商定的APN服务号
        svcData.put("MODIFY_TAG", "2");
        svcData.put("SERV_PARA1", "");
        svcData.put("SERV_PARA2", "");
        svcData.put("SERV_PARA3", "");
        svcData.put("SERV_PARA4", "");
        svcData.put("SERV_PARA5", "");
        svcData.put("SERV_PARA6", "");
        svcData.put("SERV_PARA7", "");
        svcData.put("SERV_PARA8", "");
        svcData.put("START_DATE", SysDateMgr.getSysTime());
        svcData.put("END_DATE", SysDateMgr.getTheLastTime());
        svcData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        svcData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        svcData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        return svcData;
    }
    
    /**
     * @description 服务开通回单时，判断是否为和对讲业务
     * @author xunyl
     * @date 2016-01-21
     */
    public static boolean isPocBiz(String tradeId)throws Exception{
    	//1- 初始化返回值
    	boolean isPocBiz = false;
    	
    	//2- 获取台帐信息
    	IDataset tradeInfoList = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeInfoList)){
            return isPocBiz;
        }
        
        //3- 判断是否为和对讲业务的成员回单
        if(!isPocMebPfBack(tradeId,tradeInfoList)){
        	return isPocBiz;
        }
        
        //4- 判断当前回单是否属于再次发送服开的结果
        String appType ="";
        String orderId = tradeInfoList.getData(0).getString("ORDER_ID");
        String acceptMonth = orderId.substring(4, 6);
        IData orderInfo = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, "0");
        if (IDataUtil.isEmpty(orderInfo))
        {
            IData orderHisInfo = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, "C");
            if (IDataUtil.isEmpty(orderInfo))
            {
                return isPocBiz;
            }
            appType = orderHisInfo.getString("APP_TYPE");
        }
        else 
        {
            appType = orderInfo.getString("APP_TYPE");
        }
        if ("C".equals(appType))
        {
        	isPocBiz = true;
        }
        
        //5- 返回核实结果
        return isPocBiz;

    }
    
    /**
     * @description 判断是否为和对讲业务的成员回单
     * @author xunyl
     * @date 2016-01-21
     */
    private static boolean isPocMebPfBack(String tradeId, IDataset tradeInfoList) throws Exception
    {
        String productId = tradeInfoList.getData(0).getString("PRODUCT_ID");
        String tradeTypeCode = tradeInfoList.getData(0).getString("TRADE_TYPE_CODE");

        // 成员业务
        if ("4694".equals(tradeTypeCode) || "4695".equals(tradeTypeCode) || "4697".equals(tradeTypeCode))
        {
            IDataset mainProductInfo = ProductMebInfoQry.getProductMebByPidC(productId);
            if (mainProductInfo.isEmpty())
            {
                return false;
            }
            productId = mainProductInfo.getData(0).getString("PRODUCT_ID");
        }

        productId = GrpCommonBean.productToMerch(productId, 0);
        if ("9101101".equals(productId))
        {
            return true;
        }

        return false;
    }
}
