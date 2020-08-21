package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;



public class ChangeImsMebDesktopTelElementBean extends MemberBean
{
	private Logger logger = Logger.getLogger(ChangeImsMebDesktopTelElementBean.class);
	
	private static String newStatecode = "";
	private static String oldStatecode = "";
	private static String stateCode = "";
	private String tradeTypeCode = "";
	
	@Override
	protected void makReqData(IData map) throws Exception 
	{
		super.makReqData(map);

		map.put("CHK_FLAG", "BaseInfo");
		map.put("TRADE_TYPE_CODE", this.getTradeTypeCode());

		newStatecode = map.getString("USER_STATE_CODESET");
		stateCode = map.getString("STATE_CODE");
		tradeTypeCode = map.getString("TRADE_TYPE_CODE");

		if (logger.isDebugEnabled()) 
		{
			logger.debug("<<<<<<<map参数>>>>>>" + map);
			logger.debug("<<<<<<<newStatecode参数>>>>>>" + newStatecode);
			logger.debug("<<<<<<<stateCode参数>>>>>>" + stateCode);
		}
	}
	
	@Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        makUcaForMebNormal(map);
    }
	
	@Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        newStatecode = map.getString("USER_STATE_CODESET");
		stateCode = map.getString("STATE_CODE");
		tradeTypeCode = map.getString("TRADE_TYPE_CODE");
		
		if (logger.isDebugEnabled()) 
		{
			logger.debug("<<<<<<<map参数>>>>>>" + map);
			logger.debug("<<<<<<<newStatecode参数>>>>>>" + newStatecode);
			logger.debug("<<<<<<<stateCode参数>>>>>>" + stateCode);
		}
    }
	 
	public void actTradeSub() throws Exception 
	{
		actTradeDataUser();

		actTradeSvcState();
		
		infoRegOtherData();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void actTradeDataUser() throws Exception 
	{
		IData userInfo = reqData.getUca().getUser().toData();
		oldStatecode = userInfo.getString("USER_STATE_CODESET", "");
		if (logger.isDebugEnabled()) 
		{
			logger.debug("<<<<<<<oldStatecode参数>>>>>>" + oldStatecode);
			logger.debug("<<<<<<<newStatecode参数>>>>>>" + newStatecode);
		}

		if (!oldStatecode.equals(newStatecode)) 
		{
			userInfo.put("USER_STATE_CODESET", newStatecode);
			userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

		}

		this.addTradeUser(userInfo);
	}
	
	/**
	 * 
	 */
	public void actTradeSvcState() throws Exception
	{
		String userId = reqData.getUca().getUserId();//成员的userId
		String productId = reqData.getUca().getProductId();
		IDataset result = new DatasetList();

		if ("5".equals(newStatecode) || "05".equals(newStatecode))// 停机
		{
			IDataset idataset = UserSvcStateInfoQry.getUserNextMonthMainSvcStateByUId(userId);

			if (IDataUtil.isEmpty(idataset)) 
			{
				CSAppException.apperr(GrpException.CRM_GRP_713, "当前[" + productId + "]没有配置主体服务!");
			}

			// 截止老服务
			IData userSvcSateData = idataset.getData(0);
			userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
			userSvcSateData.put("END_DATE", SysDateMgr.getLastDateThisMonth());

			result.add(userSvcSateData);

			// 新增服务状态
			IData addSvcStateData = new DataMap();
			addSvcStateData.put("USER_ID", reqData.getUca().getUserId());
			addSvcStateData.put("STATE_CODE", newStatecode); // 正常
			addSvcStateData.put("MAIN_TAG", "1");
			addSvcStateData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
			addSvcStateData.put("END_DATE", SysDateMgr.getTheLastTime());
			addSvcStateData.put("SERVICE_ID", userSvcSateData.getString("SERVICE_ID"));
			addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
			addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID
			result.add(addSvcStateData);
		} 
		else// 开机
		{
			IDataset idataset = UserSvcStateInfoQry.getUserMainSvcStateByUId(userId);
			if (IDataUtil.isEmpty(idataset)) 
			{
				CSAppException.apperr(GrpException.CRM_GRP_713, "当前[" + productId + "]没有配置主体服务!");
			}

			String lastStopTime = UserSvcStateInfoQry.getUserLastStopTime(userId);
			if (lastStopTime.compareTo(getAcceptTime()) > 0) 
			{
				for (int i = 0; i < idataset.size(); i++) 
				{
					IData userSvcState = idataset.getData(i);
					String svcstate = userSvcState.getString("STATE_CODE");
					if ("5".equals(svcstate)) 
					{
						userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
						userSvcState.put("END_DATE", getAcceptTime());
					} 
					else if ("0".equals(svcstate)) 
					{
						userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
						userSvcState.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
					}
					result.add(userSvcState);
				}
			} 
			else 
			{
				// 截止老服务
				IData userSvcSateData = idataset.getData(0);
				userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
				userSvcSateData.put("END_DATE", getAcceptTime());

				result.add(userSvcSateData);

				// 新增服务状态
				IData addSvcStateData = new DataMap();
				addSvcStateData.put("USER_ID", reqData.getUca().getUserId());
				addSvcStateData.put("STATE_CODE", newStatecode); // 正常
				addSvcStateData.put("MAIN_TAG", "1");
				addSvcStateData.put("START_DATE", getAcceptTime());
				addSvcStateData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
				addSvcStateData.put("SERVICE_ID", userSvcSateData.getString("SERVICE_ID"));
				addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
				addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID
				result.add(addSvcStateData);
			}
		}

		this.addTradeSvcstate(result);
	}

	/**
	 * 处理台账Other子表的数据
	 * @throws Exception
	 */
    public void infoRegOtherData() throws Exception
    {
        String operCode = "08";
        String desc = "成员修改";
        
        if("3917".equals(tradeTypeCode))
        {
        	operCode = "04";
        } 
        else if("3916".equals(tradeTypeCode))
        {
        	operCode = "08";
        }
        
        if("3917".equals(tradeTypeCode)) //集团多媒体桌面电话成员暂停
        {
        	//用于做发送报文
            setRegTradeOther("8171", operCode, TRADE_MODIFY_TAG.MODI.getValue(), "CNTRX", desc); 
        } 
        else if("3916".equals(tradeTypeCode))//集团多媒体桌面电话成员恢复
        {
        	setRegTradeOther1("8171", operCode, TRADE_MODIFY_TAG.MODI.getValue(), "CNTRX", desc); 
        }
        
    }
    
    /**
     * 作用：写other表，用来发报文用
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @param valueCode
     * @param rsrvValue
     * @throws Exception
     */
    private void setRegTradeOther(String serviceId, String operCode, 
    		String modifyTag, String valueCode, String rsrvValue)
    	throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", serviceId); // 服务id
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        addTradeOther(dataset);
    }
    
    /**
     * 作用：写other表，用来发报文用
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @param valueCode
     * @param rsrvValue
     * @throws Exception
     */
    private void setRegTradeOther1(String serviceId, String operCode, 
    		String modifyTag, String valueCode, String rsrvValue)
    	throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", serviceId); // 服务id
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        addTradeOther(dataset);
    }
    
    /**
     * 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        String netTypeCode = reqData.getUca().getUser().getNetTypeCode();
        data.put("NET_TYPE_CODE", netTypeCode);

    }
    
	@Override
	protected String setTradeTypeCode() throws Exception
    {
		if (logger.isDebugEnabled()) 
		{
			logger.debug("<<<<<<<tradeTypeCode=>>>>>>" + tradeTypeCode);
		}
		return tradeTypeCode;
    }
	
}