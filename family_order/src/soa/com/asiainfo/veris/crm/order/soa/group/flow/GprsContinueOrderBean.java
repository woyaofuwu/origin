package com.asiainfo.veris.crm.order.soa.group.flow;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GprsContinueOrderBean extends GroupBean
{
    private static final Logger logger = Logger.getLogger(GprsContinueOrderBean.class);
	public static String TIME_FORMAT ="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static String TIME_FORMAT2 ="yyyy-MM-dd HH:mm:ss";
	public GprsContinueOrderBean()
    {

    }
	
	/**
	 * 其他台账处理
	 */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.dealDataPackContinue();
    }
    /**
     * 流量包延期处理
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-2
     */
    public void dealDataPackContinue() throws Exception
    {
        IData reqParams = this.reqData.getPageRequestData();
        String instIdStrs = reqParams.getString("INST_ID_STRS");
        String endDate = reqParams.getString("END_DATE");
        String grpCustId = this.reqData.getUca().getCustId();
        String[] arrs = instIdStrs.split(",");
        for(String instId : arrs){
        	IData param = new DataMap();
        	param.put("INST_ID", instId);
        	param.put("END_DATE", endDate);		//新失效时间
        	param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	//查询流量包信息
        	IDataset rs = FlowInfoQry.qryGrpFlowInfoForContinueByInstId(instId);
        	if(IDataUtil.isEmpty(rs))
			{
				CSAppException.apperr(GrpException.CRM_GRP_895, grpCustId);
			}
        	//修改集团流量包有效期
        	FlowInfoQry.updateCustDataPckContinue(param);
        	//修改集团流量包库存有效期
        	FlowInfoQry.updateCustDataStockContinue(param);
        	//记录集团流量包延期日志
        	IData logData = new DataMap();
        	logData.put("ORDER_TRADE_ID", rs.getData(0).getString("RSRV_STR1", ""));				//流量包订购流水
        	logData.put("CHANGE_TRADE_ID", this.getTradeId());										//本次修改流水
        	logData.put("INST_ID", rs.getData(0).getString("INST_ID", ""));							//流量包标识
        	logData.put("CUST_ID", rs.getData(0).getString("ID", ""));								//集团客户id
        	logData.put("DATAPACK_VALUE", rs.getData(0).getString("DATAPCK_VALUE", ""));			//流量包大小
        	logData.put("CONTI_COUNT", rs.getData(0).getString("STOCK_COUNT", ""));					//延期流量包个数							
        	logData.put("OLD_END_DATE", rs.getData(0).getString("END_DATE", ""));					//原失效日期
        	logData.put("NEW_END_DATE", endDate);													//新失效日期
        	logData.put("UPDATE_TIME", SysDateMgr.getSysTime());									//延期操作时间
        	logData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	logData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	Dao.insert("TF_F_CUST_DATAPACK_CHGLOG", logData);
        }
        
    }
    
    protected void regTrade() throws Exception
    {
    	super.regTrade();
    }
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setPageRequestData(map);
    }
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    protected void makUca(IData map) throws Exception
    {
    	this.makUcaForGrpNormal(map);
    }
    
    protected void regOrder() throws Exception
    {
        IData data = bizData.getOrder();
        data.put("CUST_ID", reqData.getUca().getCustomer().getCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustomer().getCustName()); // 客户名称
        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1112"; //集团电子流量包延期业务
    }
	
    
    @Override
    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);
        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }
	
}

