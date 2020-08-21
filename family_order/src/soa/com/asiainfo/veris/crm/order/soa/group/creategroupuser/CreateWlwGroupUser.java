
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GrpWlwInstancePfQuery;

public class CreateWlwGroupUser extends CreateGroupUser
{

    public void actTradeBefore() throws Exception
    {
        IDataset dataset = reqData.cd.getElementParam();
        for (int i = dataset.size() - 1; i >= 0; i--)
        {
            if (!dataset.getData(i).containsKey("ATTR_CODE") || StringUtils.isEmpty(dataset.getData(i).getString("ATTR_CODE")))
            {
                dataset.remove(i);
            }
        }

        IDataset svcDatas = reqData.cd.getSvc();
        for (int row = svcDatas.size() - 1; row >= 0; row--)
        {
            IData map = svcDatas.getData(row);
            String serviceId = map.getString("ELEMENT_ID");
            if ("9013".equals(serviceId))
            {
                map.put("OPER_CODE", "06"); // 操作码, 06-订购. 服务开通定义
            }
            else
            {
                map.put("OPER_CODE", "01"); // 产品信息节点，01-添加本业务
            }

            //if ("99010012".equals(serviceId) || "99010013".equals(serviceId))
            //{
            //    map.put("IS_NEED_PF", "0"); // 集团新增时不会满足流量池开通条件，不发服务开通
            //}
        }

        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String userId = reqData.getUca().getUserId();
        String tradeId = getTradeId();
        String tradeTypeCode = getTradeTypeCode();
        
        // 用户实例转换
        GeneIotInstIdBean.geneSubsId(userId, tradeId);

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
    
    private void infoRegTradeAttr() throws Exception 
    {
    	String userId = reqData.getUca().getUserId();
		IData subData = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(userId);
		String groupCode = "";
		if(IDataUtil.isNotEmpty(subData))
		{
			groupCode = subData.getString("SUBS_ID","");
		}
		 
		//新增 集团智能网语音通信服务时，对该服务属性做处理
		boolean svcExist = false;
		IDataset tradeSvcs = bizData.getTradeSvc();
		if(IDataUtil.isNotEmpty(tradeSvcs))
		{
			for(int i=0; i < tradeSvcs.size(); i++)
			{
				IData svc = tradeSvcs.getData(i);
		 		String serviceId = svc.getString("SERVICE_ID","");
		 		if("99011015".equals(serviceId))
		 		{
		 			svcExist = true;
		 			break;
		 		}
		 	}
		}
		
		if(svcExist)
		{
			IDataset tradeAttrs = bizData.getTradeAttr();
		    if(IDataUtil.isNotEmpty(tradeAttrs))
		    {
		    	for(int i=0; i < tradeAttrs.size(); i++)
		     	{
		    		IData param = tradeAttrs.getData(i);
		     		String modifyTag = param.getString("MODIFY_TAG","");
		     		String attrCode = param.getString("ATTR_CODE","");
		     		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && !"".equals(attrCode))
		     		{
		     			//IDataset infos = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3997", "WLWATTRCODE", attrCode);
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
		svcExist = false;
    }
    
    /**
     * 校验和对讲、机器卡相应服务的审批文号、折扣率
     * @throws Exception
     */
    private void checkTradeSvcAttr() throws Exception 
    {
    	String productId = reqData.getUca().getProductId();
    	//只校验和对讲产品和机器卡
        if("20005013".equals(productId) || "20161124".equals(productId))
        {
        	IDataset tradeSvcs = bizData.getTradeSvc();
        	if(IDataUtil.isNotEmpty(tradeSvcs))
    		{
    			for(int i=0; i < tradeSvcs.size(); i++)
    			{
    				IData svc = tradeSvcs.getData(i);
    		 		String serviceId = svc.getString("SERVICE_ID","");
    		 		IDataset commparaAll = CommparaInfoQry.getCommparaAllCol("CSM", "4008", serviceId, "ZZZZ");
    		 		if(IDataUtil.isNotEmpty(commparaAll))
    	            {
    		 			String message = "该服务" + serviceId + "不能在集团产品新增时新增,请在集团产品新增后,在集团资料修改界面进行新增!";
	            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
    	            }
    		 	}
    		}
        }
    }
    
}
