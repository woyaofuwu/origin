package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class LineFinishBean extends EopIntfBaseBean
{

    public void dealLineAfterTradeFinish(IData param) throws Exception
    {
        String ibsysid = getMandaData(param, "IBSYSID");
        //String recordNum = getMandaData(param, "RECORD_NUM");
        String serialNo = param.getString("TRADE_ID", "");
        IDataUtil.chkParam(param, "TRADE_ID");
        IDataUtil.chkParam(param, "USER_ID");
        //查询流程资料表获取路由信息
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isEmpty(subscribeInfos))
        {
        	return;
        }
        String bpmTempletId = subscribeInfos.first().getString("BPM_TEMPLET_ID", "");
        String eparchCode = subscribeInfos.first().getString("EPARCHY_CODE", "");
        
        //更新对应product表的tradeid
        EopNodeDealBean bean = new EopNodeDealBean();
        bean.saveEopProduct(param);
        
        //判断当前专线是否是最后一条完工的
        IDataset eomsList = WorkformEomsBean.qryworkformEOMSBySerialNo(serialNo);
        if(DataUtils.isEmpty(eomsList))
        {
        	if ("EDIRECTLINECHANGEINCOME".equals(bpmTempletId)) {
        		IDataset eweNodeDataset = EweNodeQry.qryEweNodeByIbsysid(ibsysid);
                if(IDataUtil.isNotEmpty(eweNodeDataset))
                {
                    IData input = new DataMap();
                    input.put("IBSYSID", ibsysid);
                    input.put("BUSIFORM_ID", eweNodeDataset.first().getString("BUSIFORM_ID"));
                    input.put("NODE_ID", eweNodeDataset.first().getString("NODE_ID"));
                    CSAppCall.call("SS.WorkformDriveSVC.execute", input);
                }
			}
        	return;
        }
        IData eoms = eomsList.first();
    	String sheettype = eoms.getString("SHEETTYPE", "");
    	IDataset eomsInfos = WorkformEomsBean.qryworkformEOMSByIbsysidSheettype(ibsysid, sheettype);
    	if(DataUtils.isEmpty(eomsInfos))
    	{
    		return;
    	}
        
    	for (int i = eomsInfos.size()-1; i >= 0; i--)
		{
    		IData eomsInfo = eomsInfos.getData(i);
    		if(serialNo.equals(eomsInfo.getString("SERIALNO", "")))
    		{
    			eomsInfos.remove(eomsInfo);
    		}
		}
    	
    	boolean isLastLine = true;
    	if(DataUtils.isNotEmpty(eomsInfos))
    	{
    		for(int i = 0 ; i < eomsInfos.size() ; i ++)
    		{
    			IData eomsInfo= eomsInfos.getData(i);
    			String tradeIdTemp = eomsInfo.getString("SERIALNO", "");
    			if(StringUtils.isNotEmpty(tradeIdTemp))
    			{
    				IDataset tradeInfos = new DatasetList();//TradeInfoQry.getMainTradeByPk(tradeIdTemp, "0", eparchCode);
    				if(DataUtils.isNotEmpty(tradeInfos))
    				{
    					isLastLine = false;
    					break;
    				}
    			}
    		}
    	}
    	
    	if(isLastLine)
        {//如果是最后一条专线，驱动流程
            IDataset eweNodeList = EweNodeQry.qryEweNodeByIbsysid(ibsysid);
            if(IDataUtil.isNotEmpty(eweNodeList))
            {
                IData input = new DataMap();
                input.put("IBSYSID", ibsysid);
                input.put("BUSIFORM_ID", eweNodeList.first().getString("BUSIFORM_ID"));
                input.put("NODE_ID", eweNodeList.first().getString("NODE_ID"));
                CSAppCall.call("SS.WorkformDriveSVC.execute", input);
            }
        }
    }
}
