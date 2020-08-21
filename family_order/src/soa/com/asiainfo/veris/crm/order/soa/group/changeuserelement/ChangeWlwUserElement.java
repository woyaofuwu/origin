
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.cargroup.CarGroupRateApproveBean;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GrpWlwInstancePfQuery;

public class ChangeWlwUserElement extends ChangeUserElement
{

    private static final String grpIotSvcId = "9013";
    private String reverseOperSeq = "";//反向流水号

    @Override
    public void actTradeBefore() throws Exception
    {
        String userId = reqData.getUca().getUserId();

        IDataset svcDatas = reqData.cd.getSvc();
        for (int row = 0; row < svcDatas.size(); row++)
        {
            IData map = svcDatas.getData(row);
            String modifyTag = map.getString("MODIFY_TAG");
            String serviceId = map.getString("ELEMENT_ID");

            if ("0".equals(modifyTag))
            {
                map.put("OPER_CODE", "01"); // 产品信息节点，01-添加本业务
            }
            else if ("1".equals(modifyTag))
            {
                map.put("OPER_CODE", "02"); // 产品信息节点，02-删除本业务
            }

            /*
            if ("99010012".equals(serviceId) || "99010013".equals(serviceId))
            {
                if ("0".equals(modifyTag))
                {
                    int memNum = IotGprsPondUtil.getMemNumAllCrm(userId);
                    int validNum = IotGprsPondUtil.getValidNum();
                    if (memNum >= validNum)
                    {
                        map.put("IS_NEED_PF", "1"); // 满足人数时立即开通
                    }
                    else
                    {
                        map.put("IS_NEED_PF", "0");
                    }
                }
                else if ("1".equals(modifyTag))
                {
                    String svcState = IotGprsPondUtil.getSvcState(userId, serviceId);
                    if ("0".equals(svcState))
                    {
                        map.put("IS_NEED_PF", "1"); // 原来是正常状态，则发服务开通删除
                    }
                    else
                    {
                        map.put("IS_NEED_PF", "0"); // 原来是关闭的，不发服务开通，只修改资料
                    }
                }
            }
            */
        }
    }

    /**
     * 其它台帐处理
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记物联网主体服务
        infoRegTradeSvc();

        String tradeId = getTradeId();
        String tradeTypeCode = getTradeTypeCode();
        
        // 服务，资费， 产品 实例转换
        GeneIotInstIdBean.geneProdInstId(reqData, tradeId,tradeTypeCode,bizData);
        // 生成包实例
        GeneIotInstIdBean.genePkgInstId(reqData, tradeId, tradeTypeCode,bizData,false);
        
        infoRegTradeAttr();
        
        //校验对应服务的属性是否正确
        checkTradeSvcAttr();
        
    }

    /*
    @Override
    protected void actTradeSvcState() throws Exception
    {
        IDataset svcstate = IotGprsPondUtil.actTradeSvcState(reqData);
        if (IDataUtil.isNotEmpty(svcstate))
        {
            addTradeSvcstate(svcstate);
        }
    }
	*/

    /*
     * 登记物联网主体服务
     */
    public void infoRegTradeSvc() throws Exception
    {
    	IDataset svcTrades = bizData.getTradeSvc();
    	
    	boolean hasSvc = false;
    	
    	if(IDataUtil.isNotEmpty(svcTrades))
    	{
    		for (int i = 0; i < svcTrades.size(); i++)
    		{
    			IData map = svcTrades.getData(i);
    	        String serviceId = map.getString("SERVICE_ID","");
    	        if(StringUtils.isNotBlank(serviceId) && StringUtils.equals("9013",serviceId))
    	        {
    	        	map.put("OPER_CODE", "08"); // 2，08-用户信息变更
    	        	hasSvc = true;
    	        	break;
    	        }
    		}
    	}
    	
    	if(!hasSvc) //无物联网的主体服务时,重新捞取主体服务9013,登记服务台账表
    	{
    		IDataset userSvc = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(), grpIotSvcId);
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

            if (svcDatas.size() > 0)
            {
                addTradeSvc(svcDatas);
            }
    	}
    }

    private void infoRegTradeAttr() throws Exception
    {
    	IDataset svcTrades = bizData.getTradeSvc();
    	boolean svcAdd = false;
    	boolean svcModify = false;
    	boolean svcDel = false;
    	if(IDataUtil.isNotEmpty(svcTrades))
    	{
    		for(int i=0; i < svcTrades.size(); i++)
        	{
        		IData svc = svcTrades.getData(i);
        		String serviceId = svc.getString("SERVICE_ID","");
        		String modifyTag = svc.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
        				&& "99011015".equals(serviceId))
        		{
        			svcAdd = true;
        			break;
        		} 
        		else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag) 
        				&& "99011015".equals(serviceId))
        		{
        			svcModify = true;
        			break;
        		} 
        		else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) 
        				&& "99011015".equals(serviceId))
        		{
        			svcDel = true;
        			break;
        		}
        	}
    	}
    	
    	String userId = reqData.getUca().getUserId();
    	IData subData = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(userId);
        String groupCode = "";
        if(IDataUtil.isNotEmpty(subData))
        {
        	groupCode = subData.getString("SUBS_ID","");
        }
        
        //新增集团语音服务
    	if(svcAdd){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
            				&& !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011015", attrCode,"0","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            			if("GroupCode".equals(attrCode))
            			{
                			param.put("ATTR_VALUE", groupCode);
                		}
            		}
            	}
            }
    	}
    	
    	//删除集团语音服务
    	if(svcDel){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011015", attrCode,"1","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "NO_WLW_SVC_ATTR");
            			}
            			if("GroupCode".equals(attrCode))
            			{
                			param.put("ATTR_VALUE", groupCode);
                		}
            		}
            	}
            }
    	}
    	
    	boolean hasGroupCode = false;
    	boolean hasGroupType = false;
    	//修改集团语音服务
    	if(svcModify){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
            				&& !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011015", attrCode,"2","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            			if("GroupCode".equals(attrCode))
            			{
                			param.put("ATTR_VALUE", groupCode);
                		}
            		}
            		else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)
            				&& "GroupCode".equals(attrCode))
            		{
            			param.put("ATTR_VALUE", groupCode);
            			hasGroupCode = true;
            		}
            		else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)
            				&& "GroupType".equals(attrCode))
            		{
            			hasGroupType = true;
            		}
            	}
            }
            
            //GroupCode没有做修改，要对GroupCode的属性登记一下台账
            if(!hasGroupCode){
            	IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserInstType(userId, "GroupCode");
            	if (IDataUtil.isNotEmpty(userAttrs))
                {
                    IDataset dataSet = new DatasetList();
                    IData data = new DataMap();
                    data = userAttrs.getData(0);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataSet.add(data);
                    addTradeAttr(dataSet);
                }
            }
            //GroupType没有修改，要对GroupType的属性登记一下台账
            if(!hasGroupType){
            	IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserInstType(userId, "GroupType");
            	if (IDataUtil.isNotEmpty(userAttrs))
                {
                    IDataset dataSet = new DatasetList();
                    IData data = new DataMap();
                    data = userAttrs.getData(0);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataSet.add(data);
                    addTradeAttr(dataSet);
                }
            }
    	}
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData mainTradeData = bizData.getTrade();
        //反向流水号设置
        if(StringUtils.isNotBlank(reverseOperSeq))
        {
            mainTradeData.put("RSRV_STR7",reverseOperSeq);
        }
    }
    
    @Override
    public void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reverseOperSeq = map.getString("REVERSE_OPER_SEQ","");
    }
    
    
    /**
     * 校验和对讲、机器卡相应服务的审批文号、折扣率
     * @throws Exception
     */
    private void checkTradeSvcAttr() throws Exception 
    {
    	String productId = reqData.getUca().getProductId();
    	String userId = reqData.getUca().getUserId();
    	//只校验和对讲产品和机器卡
        if("20005013".equals(productId) || "20161124".equals(productId))
        {
        	IDataset tradeSvcs = bizData.getTradeSvc();
        	IDataset tradeAttrs = bizData.getTradeAttr();
        	if(IDataUtil.isNotEmpty(tradeSvcs))
    		{
        		String groupCode = "";
        		IDataset addElement = DataHelper.filter(tradeSvcs, "MODIFY_TAG=0");
        		IDataset modifyElement = DataHelper.filter(tradeSvcs, "MODIFY_TAG=2");
        		IDataset allElements = new DatasetList();
        		allElements.addAll(addElement);
        		allElements.addAll(modifyElement);
        		if(IDataUtil.isNotEmpty(allElements))
        		{
        			for(int i=0; i < allElements.size(); i++)
        			{
        				IData svc = allElements.getData(i);
        		 		String serviceId = svc.getString("SERVICE_ID","");
        		 		String instId = svc.getString("INST_ID","");
        		 		IDataset commparaAll = CommparaInfoQry.getCommparaAllCol("CSM", "4008", serviceId, "ZZZZ");
        		 		if(IDataUtil.isNotEmpty(commparaAll))
        	            {
        		 			if(StringUtils.isBlank(groupCode))
        		 			{
        		 				IData subData = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(userId);
        		 				if(IDataUtil.isEmpty(subData))
        		        		{
        		        			String message = "未获取到集团产品的EC_ID!";
        		            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
        		        		}
        		        		groupCode = subData.getString("SUBS_ID","");
        		        		if(StringUtils.isBlank(groupCode))
        		        		{
        		        			String message = "集团产品的EC_ID为空!";
        		            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
        		        		}
        		 			}
        		 			
        		 			String approvealNum = "";
        		 			String discount = "";
        		 			//获取对应服务的属性
        		 			IDataset attrs = DataHelper.filter(tradeAttrs, "RELA_INST_ID=" + instId);
        		 			for(int j=0; j < attrs.size(); j++)
        		 			{
        		 				IData attr = attrs.getData(j);
        		 				String attrCode = attr.getString("ATTR_CODE");
        		 				if("ApprovalNum".equals(attrCode))
        		 				{
        		 					approvealNum = attr.getString("ATTR_VALUE");
        		 				}
        		 				else if("Discount".equals(attrCode))
        		 				{
        		 					discount = attr.getString("ATTR_VALUE");
        		 				}
        		 			}
        		 			
        		 			IData param = new DataMap();
        		 			param.put("EC_ID", groupCode);
        		 			param.put("APPROVAL_NO", approvealNum);
        		 			param.put("DISCNT_RATE", discount);
        		 			
        		 			IDataset proNoInfos = CarGroupRateApproveBean.queryGrpEcByProNoRate(param);
        		 			if(IDataUtil.isEmpty(proNoInfos))
        		 			{
        		 				String message = "服务" + serviceId + "填写的审批文号、折扣率未存在,请填写正确的审批文号、折扣率!";
        	            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
        		 			}
        	            }
        		 	}
        		}
        		
    		}
        }
    }
    
}
