/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.coupons;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CouponsQuotaInfoQry;


/**
 * @CREATED
 */
public class CouponsQuotaMgrBean extends CSBizBean
{

	static Logger logger=Logger.getLogger(CouponsQuotaMgrBean.class);
	 
    

    /**
     * 新增记录
     * 
     * @param param
     * @throws Exception
     */
    public int createCouponsQuotaInfo(IData param) throws Exception
    {

        String sql = " INSERT INTO TL_B_COUPONS_QUOTA (AREA_CODE,AUDIT_ORDER_ID,TOTAL_AMOUNT,BALANCE,AMOUNTS,OPERA_TIME,OPERA_STAFF_ID,START_TIME,END_TIME) " + " VALUES (:AREA_CODE,:AUDIT_ORDER_ID,:TOTAL_AMOUNT*100,:TOTAL_AMOUNT*100,0,SYSDATE,:OPERA_STAFF_ID,to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss'),to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss')) ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }

    /**
     * 根据rowid删除记录
     * 
     * @param param
     * @throws Exception
     */
    public int deleteCouponsQuotaInfo(IData param) throws Exception
    {

        String sql = "DELETE  FROM TL_B_COUPONS_QUOTA WHERE ROWID=:ROW_ID ";
        //String sql = "update TL_B_COUPONS_QUOTA set end_time=sysdate,OPERA_TIME=SYSDATE,OPERA_STAFF_ID=:OPERA_STAFF_ID WHERE ROWID=:ROW_ID ";
        return Dao.executeUpdate(new StringBuilder(sql), param);
    }

   




    /**
     * 获取赠送总额审批工单及工单余额
     */
    public IDataset getWorkOrders() throws Exception
    {
        IDataset dataset = new DatasetList();

        String cityId = getVisit().getCityCode();

        dataset = CouponsQuotaInfoQry.getWorkOrders(cityId);
        
        if(IDataUtil.isNotEmpty(dataset)){
            IData data = dataset.getData(0);
            
            //获取某个员工对应的所有的优惠券并把优惠券值相加返回
            String audit_opera_id = data.getString("AUDIT_ORDER_ID","");
            IData quotaData = new DataMap();
            quotaData.put("AUDIT_ORDER_ID", audit_opera_id);
            int usedBanlance=CouponsQuotaInfoQry.getSumTicketValue(quotaData);
            data.put("SUM_VALUE",usedBanlance+"");
        }

        //

        return dataset;
    }




    /**
     * 修改记录
     * 
     * @param param
     * @throws Exception
     */
    public int updateCouponsQuotaInfo(IData param) throws Exception
    {

        String sql = " UPDATE TL_B_COUPONS_QUOTA SET AREA_CODE=:AREA_CODE ,AUDIT_ORDER_ID=:AUDIT_ORDER_ID,TOTAL_AMOUNT=:TOTAL_AMOUNT*100,BALANCE=:TOTAL_AMOUNT*100-to_number(AMOUNTS),OPERA_TIME=SYSDATE,START_TIME=to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss'),END_TIME=to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss'),OPERA_STAFF_ID=:OPERA_STAFF_ID " + " where rowid=:ROW_ID ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }
    
    /**
     * 修改记录
     * 
     * @param param
     * @throws Exception
     */
    public int updateCouponsBalanceInfo(IData param) throws Exception
    {

        String sql = " UPDATE TL_B_COUPONS_QUOTA T SET BALANCE=to_number(balance)-to_number(:TICKET_VALUE) * 100,AMOUNTS=to_number(AMOUNTS)+to_number(:TICKET_VALUE) * 100,OPERA_TIME=SYSDATE,OPERA_STAFF_ID=:OPERA_STAFF_ID " + " where AUDIT_ORDER_ID=:AUDIT_ORDER_ID AND SYSDATE BETWEEN T.START_TIME AND T.END_TIME ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }
    
    /**
     * 修改记录
     * 
     * @param param
     * @throws Exception
     */
    public int updateCouponsSumValueInfo(IData param) throws Exception
    {

    	//在时间范围内可以允许对某发券员已经使用总额的更新
        String sql = " UPDATE TL_B_COUPONS_QUOTA T SET " +
        		"OPERA_TIME=SYSDATE,OPERA_STAFF_ID=:OPERA_STAFF_ID " + " where AUDIT_ORDER_ID=:AUDIT_ORDER_ID AND SYSDATE BETWEEN T.START_TIME AND T.END_TIME ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }
    
}
