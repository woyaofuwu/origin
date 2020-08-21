
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class SuspendResumeWlwServiceBean extends MemberBean
{

    private String suspendServices = "";

    private String resumeServices = "";

    private static final String stateNormal = "0";

    private static final String stateSuspend = "E";

    private static final String memIotSvcId = "9014";
    
    private String oprCode = "";
    
    private String closeOper = "";
    
    private String moreOper = "";
    

    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        // 登记物联网成员主体服务
        infoRegTradeSvc();
        // 登记服务状态
        infoRegSvcState();

        //登记服务属性
        regSvcAttr();
    }

    // 登记 TF_B_TRADE_SVCSTATE
    public void infoRegSvcState() throws Exception
    {
        IDataset svcstateSet = new DatasetList();
        if (StringUtils.isNotBlank(suspendServices))
        {
            String[] suspendArray = suspendServices.split(";");
            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                
                IData stateAdd = new DataMap();
                
                if(StringUtils.isNotBlank(oprCode) && "02".equals(oprCode))
                {
                	String svcId = suspendRow[0];
                	String userIdA = suspendRow[3];
                	String memUserId = reqData.getUca().getUserId();
                	if(StringUtils.isNotBlank(svcId) && 
                		StringUtils.isNotBlank(userIdA) &&
                		StringUtils.isNotBlank(memUserId))
                	{
                		UcaData uca = reqData.getUca();
                		//IDataset svcInfos = UserSvcInfoQry.getSvcUserId(memUserId, userIdA, svcId);
                		List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcId);
                		if(svcTradeData == null)
                		{
                			CSAppException.apperr(GrpException.CRM_GRP_713, "用户未开通" + svcId + "该服务!");
                		}
                		SvcStateTradeData svcStateTradeData = uca.getUserSvcsStateByServiceId(svcId);
            	        if (svcStateTradeData != null)
            	        {
            	            if (!"0".equals(svcStateTradeData.getStateCode()))
            	            {
            	                CSAppException.apperr(GrpException.CRM_GRP_713, "用户服务" + svcId + "状态为非正常,不能办理!!");
            	            }
            	        }
                		
                	}
                	String strRemark = "物联网流量双封顶暂停";
                	if(StringUtils.isNotBlank(moreOper))
                	{
                		stateAdd.put("RSRV_TAG1", "1");
                		stateAdd.put("REMARK", strRemark);
    	        	}
                	else if(StringUtils.isNotBlank(closeOper))
                	{
    		        	stateAdd.put("RSRV_TAG1", "2");
                		stateAdd.put("REMARK", "物联网流量用尽关停");
    	        	}
                }
                
                stateAdd.put("USER_ID", reqData.getUca().getUserId());
                stateAdd.put("SERVICE_ID", suspendRow[0]);
                stateAdd.put("INST_ID", SeqMgr.getInstId());
                stateAdd.put("MAIN_TAG", "0");
                stateAdd.put("STATE_CODE", stateSuspend);
                stateAdd.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                stateAdd.put("START_DATE", getAcceptTime());
                stateAdd.put("END_DATE", SysDateMgr.getTheLastTime());
                svcstateSet.add(stateAdd);

                IData stateDel = new DataMap();
                stateDel.put("USER_ID", reqData.getUca().getUserId());
                stateDel.put("SERVICE_ID", suspendRow[0]);
                stateDel.put("INST_ID", suspendRow[1]);
                stateDel.put("MAIN_TAG", "0");
                stateDel.put("STATE_CODE", stateNormal);
                stateDel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                stateDel.put("START_DATE", suspendRow[2]);
                stateDel.put("END_DATE", getAcceptTime());
                svcstateSet.add(stateDel);
            }
        }

        if (StringUtils.isNotBlank(resumeServices))
        {

            String[] suspendArray = resumeServices.split(";");

            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] resumeRow = suspendArray[i].split(",");
                IData stateAdd = new DataMap();
                stateAdd.put("USER_ID", reqData.getUca().getUserId());
                stateAdd.put("SERVICE_ID", resumeRow[0]);
                stateAdd.put("INST_ID", SeqMgr.getInstId());
                stateAdd.put("MAIN_TAG", "0");
                stateAdd.put("STATE_CODE", stateNormal);
                stateAdd.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                stateAdd.put("START_DATE", getAcceptTime());
                stateAdd.put("END_DATE", SysDateMgr.getTheLastTime());
                svcstateSet.add(stateAdd);

                IData stateDel = new DataMap();
                stateDel.put("USER_ID", reqData.getUca().getUserId());
                stateDel.put("SERVICE_ID", resumeRow[0]);
                stateDel.put("INST_ID", resumeRow[1]);
                stateDel.put("MAIN_TAG", "0");
                stateDel.put("STATE_CODE", stateSuspend);
                stateDel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                stateDel.put("START_DATE", resumeRow[2]);
                stateDel.put("END_DATE", getAcceptTime());
                svcstateSet.add(stateDel);
            }
        }

        addTradeSvcstate(svcstateSet);
        
        //流量用尽、双封顶时,下个月初的恢复工单
        if(StringUtils.isNotBlank(suspendServices) && StringUtils.isNotBlank(oprCode)
        		&& "02".equals(oprCode))
        {
        	String svcId = "";
        	String instId = "";
        	
        	String[] suspendArray = suspendServices.split(";");
            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                svcId = suspendRow[0];
                UcaData uca = reqData.getUca();
        		List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcId);
        		if(svcTradeData != null && svcTradeData.size() > 0)
        		{
        			SvcTradeData svcData = svcTradeData.get(0);
        			instId = svcData.getInstId();
        		}
            }
            
        	String strDealId = SeqMgr.getTradeId();
        	String strUserId = reqData.getUca().getUserId();
        	String strEparchyCode = reqData.getUca().getUserEparchyCode();
        	String strExecTime = SysDateMgr.getFirstDayOfNextMonth4WEB();
        	
        	IData param = new DataMap();
        	param.put("OPR_CODE", "01");
        	param.put("ROUTE_EPARCHY_CODE", strEparchyCode);
        	param.put("USER_ID", strUserId);
        	param.put("SERVICE_ID", svcId);
        	param.put("INST_ID", instId);
        	
        	IData paramProduct = new DataMap();
        	paramProduct.put("DEAL_ID", strDealId);
        	paramProduct.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        	paramProduct.put("USER_ID", strUserId);
        	paramProduct.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));
        	paramProduct.put("EPARCHY_CODE", strEparchyCode);
        	paramProduct.put("DEAL_COND", param);
        	paramProduct.put("DEAL_TYPE", "ResumeExpire");
        	paramProduct.put("EXEC_TIME", strExecTime);
        	paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(strExecTime));
        	paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
        	paramProduct.put("DEAL_STATE", "0");
        	paramProduct.put("REMARK", "物联网流量双封顶恢复-集团");
        	paramProduct.put("TRADE_ID", reqData.getTradeId());
        	Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);
        }
    }

    /*
     * 登记物联网成员主体服务
     */
    public void infoRegTradeSvc() throws Exception
    {
        IDataset userSvc = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(), memIotSvcId);
        if (userSvc.isEmpty())
        {
            return;
        }

        IDataset svcDatas = new DatasetList();
        for (int i = 0, size = userSvc.size(); i < size; i++)
        {
            IData map = new DataMap();
            map.putAll(userSvc.getData(i));
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            map.put("OPER_CODE", "08"); // 2，08-用户信息变更
            svcDatas.add(map);
        }

        if (StringUtils.isNotBlank(suspendServices))
        {
            String[] suspendArray = suspendServices.split(";");
            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String serviceId = suspendRow[0];
                String userIdA = suspendRow[3];

                IDataset svcDataset = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(), userIdA, serviceId);

                for (int j = 0, size = svcDataset.size(); j < size; j++)
                {
                    IData map = new DataMap();
                    map.putAll(svcDataset.getData(j));
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    map.put("OPER_CODE", "04"); // 暂停供服开使用
                    svcDatas.add(map);
                }
            }
        }

        if (StringUtils.isNotBlank(resumeServices))
        {

            String[] suspendArray = resumeServices.split(";");

            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String serviceId = suspendRow[0];
                String userIdA = suspendRow[3];

                IDataset svcDataset = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(), userIdA, serviceId);

                for (int j = 0, size = svcDataset.size(); j < size; j++)
                {
                    IData map = new DataMap();
                    map.putAll(svcDataset.getData(j));
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    map.put("OPER_CODE", "05"); // 恢复供服开使用
                    svcDatas.add(map);
                }
            }
        }

        if (svcDatas.size() > 0)
        {
            addTradeSvc(svcDatas);
        }
    }

    /**
     * 服务的属性
     * 
     * @throws Exception
     */
    public void regSvcAttr() throws Exception
    {
        IDataset attrDatas = new DatasetList();
        
        if (StringUtils.isNotBlank(suspendServices))
        {
            String[] suspendArray = suspendServices.split(";");
            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String serviceId = suspendRow[0];
                //String userIdA = suspendRow[3];

                if(StringUtils.isNotBlank(serviceId) && 
                        ("99010001".equals(serviceId) || "99011005".equals(serviceId) )){
                    IDataset svcDataset = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(),serviceId);
                    if(svcDataset != null && svcDataset.size() > 0){
                        IData svcData = svcDataset.getData(0);
                        String instId = svcData.getString("INST_ID","");
                        if(StringUtils.isNotBlank(instId)){
                           IDataset attrDataset = UserAttrInfoQry.queryUserAttrByUserIdInstTypeAndId(reqData.getUca().getUserId(),instId,"S","APNNAME");
                           if(attrDataset != null && attrDataset.size() > 0){
                               IData attrData = attrDataset.getData(0);
                               attrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                               attrDatas.add(attrData);
                           }
                        }
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(resumeServices))
        {

            String[] suspendArray = resumeServices.split(";");

            for (int i = 0; i < suspendArray.length; i++)
            {
                String[] suspendRow = suspendArray[i].split(",");
                String serviceId = suspendRow[0];
                //String userIdA = suspendRow[3];

                if(StringUtils.isNotBlank(serviceId) && 
                        ("99010001".equals(serviceId) || "99011005".equals(serviceId) )){
                    IDataset svcDataset = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(),serviceId);
                    if(svcDataset != null && svcDataset.size() > 0){
                        IData svcData = svcDataset.getData(0);
                        String instId = svcData.getString("INST_ID","");
                        if(StringUtils.isNotBlank(instId)){
                           IDataset attrDataset = UserAttrInfoQry.queryUserAttrByUserIdInstTypeAndId(reqData.getUca().getUserId(),instId,"S","APNNAME");
                           if(attrDataset != null && attrDataset.size() > 0){
                               IData attrData = attrDataset.getData(0);
                               attrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                               attrDatas.add(attrData);
                           }
                        }
                    }
                }
            }
        }
        
        super.addTradeAttr(attrDatas);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        suspendServices = map.getString("SUSPEND_SERVICE");//暂停
        resumeServices = map.getString("RESUME_SERVICE");//恢复
        
        oprCode = map.getString("OPR_CODE","");//暂停、恢复的操作类型
        closeOper = map.getString("CLOSE_OPER","");//流量用尽关停
        moreOper = map.getString("MORE_OPER","");//双封顶关停
        
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        map.put("USER_ID", map.getString("USER_ID_A")); // 集团的user_id
        makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "273";
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeDate = bizData.getTrade();
        tradeDate.put("PRODUCT_ID", reqData.getUca().getProductId());
    }

}
