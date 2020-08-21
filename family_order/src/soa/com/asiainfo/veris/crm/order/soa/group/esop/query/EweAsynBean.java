package com.asiainfo.veris.crm.order.soa.group.esop.query;

import java.util.Iterator;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop.EsopDeskTopUtils;


public class EweAsynBean {
	
	private static int INIT_ATTR_ID = 1;
	
	private static int INIT_ATTR_GROUP = 0;
	
	private static int INIT_PARENT_ATTR_ID= 0;
	
	private static String ATTR_TYPE_STRING = "0";
	
	private static String ATTR_TYPE_DATA = "1";

	private static String ATTR_TYPE_DATASET = "2";
	/**
	 * 报错asyn表
	 * @param data
	 * @return
	 * @throws Exception
	 */
	
	public static void saveAsynInfo(IData data) throws Exception
	{

		String busiformId = data.getString("BUSIFORM_ID", "");
		String busiformNodeId =data.getString("BUSIFORM_NODE_ID", "");
		String nodeId = data.getString("NODE_ID", "");
		
		//查询按node_id + busiform_id 和 busiform_node_id 查询是否已经有异步数据了。如果有，则不再处理
		IDataset asynInfos = EweAsynInfoQry.qryInfosByBusiformNode(busiformNodeId, busiformId, nodeId);
		if(DataUtils.isNotEmpty(asynInfos))
		{
			return;
		}
		
		IData param = new DataMap();
		String asynId = SeqMgr.getAsynId();
		param.put("ASYN_ID", asynId);
		param.put("BUSIFORM_ID", data.getString("BUSIFORM_ID", ""));
		param.put("BUSIFORM_NODE_ID", data.getString("BUSIFORM_NODE_ID", ""));
		param.put("NODE_ID", data.getString("NODE_ID", ""));
		param.put("REMARK", data.getString("REMARK", ""));
		param.put("STATE", "0");
		param.put("VALID_TAG", "0");
		param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));
		param.put("ACCEPT_DEPART_ID", data.getString("ACCEPT_DEPART_ID", ""));
		param.put("UPDATE_DEPART_ID", data.getString("ACCEPT_DEPART_ID", ""));
		param.put("ACCEPT_STAFF_ID", data.getString("ACCEPT_STAFF_ID", ""));
		param.put("UPDATE_STAFF_ID", data.getString("ACCEPT_STAFF_ID", ""));
		param.put("CREATE_DATE", SysDateMgr.getSysTime());
		param.put("UPDATE_DATE", SysDateMgr.getSysTime());
		EweAsynInfoQry.insertAsynInfo(param);
		
		//剔除掉存储在asyn表数据
		IData temp = new DataMap();
		temp.putAll(data);
		temp.remove("BUSIFORM_ID");
		temp.remove("BUSIFORM_NODE_ID");
		temp.remove("NODE_ID");
		temp.remove("EPARCHY_CODE");
		temp.remove("REMARK");
		temp.remove("ACCEPT_DEPART_ID");
		temp.remove("UPDATE_DEPART_ID");
		temp.remove("ACCEPT_STAFF_ID");
		temp.remove("UPDATE_STAFF_ID");
		
		IDataset resultInfos = new DatasetList();
		dealData(resultInfos,temp, INIT_ATTR_ID, INIT_ATTR_GROUP, INIT_PARENT_ATTR_ID);
		
		IDataset asynAttrs = new DatasetList();
		
		if(DataUtils.isNotEmpty(resultInfos))
		{
			for(int i = 0 ; i < resultInfos.size() ; i ++)
			{
				IData resultInfo = resultInfos.getData(i);
				IData asynAttr = new DataMap();
				asynAttr.put("ASYN_ID", asynId);
				asynAttr.put("ATTR_ID", resultInfo.getInt("ATTR_ID"));
				asynAttr.put("PARENT_ATTR_ID",  resultInfo.getInt("PARENT_ATTR_ID"));
				asynAttr.put("ATTR_TYPE", resultInfo.getString("ATTR_TYPE",""));
				asynAttr.put("ATTR_GROUP", resultInfo.getInt("ATTR_GROUP"));
				asynAttr.put("ATTR_CODE", EsopDeskTopUtils.subStr(resultInfo.getString("ATTR_CODE",""), 200, "UTF-8"));
				asynAttr.put("ATTR_VALUE", EsopDeskTopUtils.subStr(resultInfo.getString("ATTR_VALUE",""), 500, "UTF-8"));
				asynAttr.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
				asynAttr.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
				asynAttr.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
				asynAttr.put("REMARK", data.getString("REMARK", ""));
				asynAttr.put("VALID_TAG", "0");
				asynAttr.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				asynAttr.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));
				asynAttr.put("ACCEPT_DEPART_ID", data.getString("ACCEPT_DEPART_ID", ""));
				asynAttr.put("UPDATE_DEPART_ID", data.getString("ACCEPT_DEPART_ID", ""));
				asynAttr.put("ACCEPT_STAFF_ID", data.getString("ACCEPT_STAFF_ID", ""));
				asynAttr.put("UPDATE_STAFF_ID", data.getString("ACCEPT_STAFF_ID", ""));
				asynAttr.put("CREATE_DATE", SysDateMgr.getSysTime());
				asynAttr.put("UPDATE_DATE", SysDateMgr.getSysTime());
				asynAttrs.add(asynAttr);
			}
		}
		
		if(DataUtils.isNotEmpty(asynAttrs))
		{
			EweAsynAttrQry.insertAsynAttrInfos(asynAttrs);
		}
	
	}
	
	/**
	 * 处理登记数据
	 * @param resultInfos
	 * @param data
	 * @param num
	 * @param groupint
	 * @param parentNum
	 * @return
	 * @throws Exception
	 */
	private static int dealData(IDataset resultInfos,IData data, int num, int groupint,int parentNum) throws Exception
	{
		Iterator<String> it = data.keySet().iterator();
		String key = "";
		
		while (it.hasNext()) 
		{
			key = it.next();
			if(data.get(key) instanceof IData)
			{
				IData dataTemp = data.getData(key);
				IData temp = new DataMap();
				
				temp.put("ATTR_TYPE", ATTR_TYPE_DATA);//0---代办单结构 1----IData数据结构   2-----IDataset数据结构
				temp.put("ATTR_ID", num); //元素id 0 开始，递增
				temp.put("PARENT_ATTR_ID", parentNum); //父元素id
				temp.put("ATTR_CODE", key); //元素key
				temp.put("ATTR_GROUP", groupint);//分组标识，默认为0，对于类似于IDataset,多条结构使用
				
				int tempParent = num;
				num ++;
				
				resultInfos.add(temp);
				num = dealData(resultInfos,dataTemp, num, groupint, tempParent);
			}
			else if(data.get(key) instanceof IDataset)
			{
				IDataset tempInfos = data.getDataset(key);
				IData temp = new DataMap();
				
				temp.put("ATTR_TYPE", ATTR_TYPE_DATASET);//0---代办单结构 1----IData数据结构   2-----IDataset数据结构
				temp.put("ATTR_ID", num); //元素id 0 开始，递增
				temp.put("PARENT_ATTR_ID", parentNum); //父元素id
				temp.put("ATTR_CODE", key); //元素key
				temp.put("ATTR_GROUP", groupint);//分组标识，默认为0，对于类似于IDataset,多条结构使用
				
				int tempParent = num;
				num ++;
				
				resultInfos.add(temp);
				
				for(int i = 0 ; i < tempInfos.size() ; i ++)
				{
					IData tempInfo = tempInfos.getData(i);
					num = dealData(resultInfos,tempInfo, num, i, tempParent);
				}
			}
			else
			{
				String value = data.getString(key);
				IData temp = new DataMap();
				
				temp.put("ATTR_TYPE", ATTR_TYPE_STRING);//0---代办单结构 1----IData数据结构   2-----IDataset数据结构
				temp.put("ATTR_ID", num); //元素id 0 开始，递增
				temp.put("PARENT_ATTR_ID", parentNum); //父元素id
				temp.put("ATTR_CODE", key); //元素key
				temp.put("ATTR_VALUE",value);//元素值
				temp.put("ATTR_GROUP", groupint);//分组标识，默认为0，对于类似于IDataset,多条结构使用
				
				resultInfos.add(temp);
				num ++;
			}
		}
		return num;
	}
	
	
	public static IDataset queryByBusiformNode(String busiformId,String nodeId) throws Exception
    {
    	IData param = new DataMap();
		param.put("BUSIFORM_ID", busiformId);
		param.put("NODE_ID", nodeId);
        return Dao.qryByCode("TF_B_EWE_ASYN", "SEL_ASYNATTR_BY_BUSIFORMIDNODE", param, Route.getJourDb(Route.getCrmDefaultDb()));
    }
	
}