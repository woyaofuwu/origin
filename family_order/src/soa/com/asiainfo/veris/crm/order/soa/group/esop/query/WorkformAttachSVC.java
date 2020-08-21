package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformAttachSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public static IDataset qryByIbsysidNode(IData param) throws Exception
    {
    	IDataset result = new DatasetList();
    	String ibsysid = param.getString("IBSYSID","");
    	String nodeId = param.getString("NODE_ID", "");
    	IDataset nodeInfos = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysid, nodeId);
    	if(DataUtils.isEmpty(nodeInfos))
    	{
    		return result;
    	}
    	result = WorkformAttachBean.qryEopAttrBySubIbsysid(nodeInfos.first().getString("SUB_IBSYSID", ""));
    	return result;
    }

    public static IDataset qryAttachInfo(IData param) throws Exception
    {
    	IDataset result = new DatasetList();
    	String subIbsysid = param.getString("SUB_IBSYSID","");
    	IDataset attachInfos = WorkformAttachBean.qryEopAttrBySubIbsysid(subIbsysid);
    	
    	if(DataUtils.isEmpty(attachInfos))
    	{
    		return result;
    	}
    	
    	IDataset configInfos = EweConfigQry.qryByConfigName("STATIC_EOMS_URL", "0");
    	String url = "";
    	if(DataUtils.isNotEmpty(configInfos))
    	{
    		url = configInfos.first().getString("PARAMVALUE", "");
    	}
    	
    	IData info = new DataMap();
    	
    	IDataset infos = new DatasetList();
    	for(int i = 0 ; i < attachInfos.size() ; i ++)
    	{
    		IData attachInfo = attachInfos.getData(i);
    		String attachName = attachInfo.getString("DISPLAY_NAME","");
    		String attachLength = attachInfo.getString("ATTACH_LENGTH","");
    		String fileId = attachInfo.getString("FILE_ID","");
    		String fileurl = "FILE_ID="+fileId+"|"+subIbsysid+"|download";
    		IData temp = new DataMap();
    		IData temp1 = new DataMap();
    		temp.put("attachName", attachName);
    		temp.put("attachLength", attachLength);
    		temp.put("attachURL", url+fileurl);
    		temp1.put("attachInfo", temp);
    		infos.add(temp1);
    	}
    	info.put("attachRef", infos);
    	result.add(info);
    	return result;
    }
    
    public static void insertAttach(IData param) throws Exception
    {
    	WorkformAttachBean.insertAttach(param);
    }
    
    public static IDataset qryContractAttach(IData param) throws Exception
    {
    	IDataset attachInfos = WorkformAttachBean.qryContractAttach(param);
		if(DataUtils.isEmpty(attachInfos)){
			return null;
		}
		return attachInfos;
    }
    
    public static IDataset qryMaxEopAttachByIbsysId(IData param) throws Exception
    {
    	String ibsysId =  param.getString("IBSYSID");
    	IDataset attachInfos = WorkformAttachBean.qryMaxEopAttachByIbsysId(ibsysId);
    	return attachInfos;
    }
    
    public static IDataset qryEopAllAttachByIbsysid(IData param) throws Exception
    {
    	String ibsysId =  param.getString("IBSYSID");
    	String attachType = param.getString("ATTACH_TYPE");
    	IDataset attachInfos = WorkformAttachBean.qryEopAllAttachByIbsysid(ibsysId,attachType);
    	return attachInfos;
    }
    
    
    public IDataset qryExistsFile(IData param) throws Exception{
    	String subIbsysid =  param.getString("SUB_IBSYSID");
    	String fileid =  param.getString("FILE_ID");
    	IData date = new DataMap();
    	if(StringUtils.isEmpty(subIbsysid) || StringUtils.isEmpty(fileid))
    	{
    		return new DatasetList();
    	}
    	date.put("SUB_IBSYSID", subIbsysid);
    	date.put("FILE_ID", fileid);
    	IDataset attachInfos = WorkformAttachBean.qryExistsFile(date);
    	return attachInfos;
    }
}
