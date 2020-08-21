
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;

public class DestroyWlwGroupUser extends DestroyGroupUser
{

//    public void actTradeBefore() throws Exception
//    {
//        super.actTradeBefore();
//
//        IDataset svcDatas = reqData.cd.getSvc();
//        for (int row = 0; row < svcDatas.size(); row++)
//        {
//            IData map = svcDatas.getData(row);
//            String serviceId = map.getString("ELEMENT_ID");
//
//            if ("9013".equals(serviceId))
//            {
//                map.put("OPER_CODE", "07"); // 主体服务操作码, 07-退订(服务开通定义)
//            }
//            else
//            {
//                map.put("OPER_CODE", "02"); // 产品信息节点，02-删除本业务(按规范)
//            }
//
//            if ("99010012".equals(serviceId) || "99010013".equals(serviceId))
//            {
//                String svcState = IotGprsPondUtil.getSvcState(reqData.getUca().getUserId(), serviceId);
//                if ("0".equals(svcState))
//                {
//                    map.put("IS_NEED_PF", "1"); // 是正常状态，则发服务开通。
//                }
//                else
//                {
//                    map.put("IS_NEED_PF", "0"); // 不是正常状态，不发服务开通。
//                }
//            }
//        }
//    }
    
    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);
    	String serviceId = map.getString("SERVICE_ID");

        if ("9013".equals(serviceId))
        {
            map.put("OPER_CODE", "07"); // 主体服务操作码, 07-退订(服务开通定义)
        }
        else
        {
            map.put("OPER_CODE", "02"); // 产品信息节点，02-删除本业务(按规范)
        }

        /*
        if ("99010012".equals(serviceId) || "99010013".equals(serviceId))
        {
            String svcState = IotGprsPondUtil.getSvcState(reqData.getUca().getUserId(), serviceId);
            if ("0".equals(svcState))
            {
                map.put("IS_NEED_PF", "1"); // 是正常状态，则发服务开通。
            }
            else
            {
                map.put("IS_NEED_PF", "0"); // 不是正常状态，不发服务开通。
            }
        }
        */
    }

    /**
     * 其它台帐处理
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        String userId = reqData.getUca().getUserId();
        String tradeId = getTradeId();
        String tradeTypeCode = getTradeTypeCode();
        
        //截止用户实例转换
        GeneIotInstIdBean.updateSubsId(userId, bizData);
        
        // 服务，资费， 产品 实例转换
        GeneIotInstIdBean.geneProdInstId(reqData, tradeId,tradeTypeCode,bizData);
        // 生成包实例
        GeneIotInstIdBean.genePkgInstId(reqData, tradeId, tradeTypeCode,bizData,false);
        
        infoRegTradeAttr();
    }
    
    private void infoRegTradeAttr() throws Exception
    {
    	IDataset svcTrades = bizData.getTradeSvc();
    	boolean svcDel = false;
    	if(IDataUtil.isNotEmpty(svcTrades))
    	{
    		for(int i=0; i < svcTrades.size(); i++)
        	{
        		IData svc = svcTrades.getData(i);
        		String serviceId = svc.getString("SERVICE_ID","");
        		String modifyTag = svc.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) 
        				&& "99011015".equals(serviceId))
        		{
        			svcDel = true;
        			break;
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
            		}
            	}
            }
    	}
    }
}
