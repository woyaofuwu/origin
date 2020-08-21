package com.asiainfo.veris.crm.iorder.web.person.ailk.presview.view.popup;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.wade.web.v4.tapestry.component.tree.TreeFactory;
import com.wade.web.v4.tapestry.component.tree.TreeParam;

public abstract class AddressQryTreeViewNew extends PersonBasePage {

	private static Logger logger = Logger.getLogger(AddressQryTreeViewNew.class);
	
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setCondition(IData condition);
	public abstract void setCount(long count);

	public void init(IRequestCycle cycle) throws Exception{	
		IData data =  getData();
		
		IData param = new DataMap();
		String city = this.getVisit().getCityCode();
		if(!city.equals("HNSJ")&&!city.equals("HNKF")){			
			param.put("AREA_CODE", city);
			param.put("PARENT_ADDR_ID", "1");
			IDataset resultTemp  = call("PB.AddressQryTreeSVC.getAddressDataByParentId", param);
			if(resultTemp != null && resultTemp.size()>0){
				data.put("AREA_CODE", ((IData) resultTemp.get(0)).get("ADDR_ID"));
			}
		}
		
		setCondition(data);
		
	}
	
	 /**
     * 获取地州
     * 
     * @param cycle
     * @throws Exception
     */
	public IDataset getAreaCode() throws Exception{
		IData param = new DataMap();
		String city = this.getVisit().getCityCode();
		if(!city.equals("HNSJ")&&!city.equals("HNKF")){			
			param.put("AREA_CODE", city);
		}
    	param.put("PARENT_ADDR_ID", "1");
//    	IDataset resultTemp  = call("PB.AddressQryTreeSVC.getAddressDataByParentId", param);
    	IDataset resultTemp  = call1("PB.AddressQryTreeSVC.getAddressDataByParentId", param,null);  // modify by duhj
    	return resultTemp;
	}
	
	
	 /**
     * 宽带地址模糊查詢
     * 
     * @param cycle
     * @throws Exception
     */

    public void submitEparchy(IRequestCycle cycle) throws Exception{    	
    	IData param = this.getData();   	 
    	IDataset resultTemp  = call("PB.AddressQryTreeSVC.getAddressDataByParentId", param);
    	setInfos(resultTemp);
    }
    
    
    /**
     * 获取3级地址名称
     * @param cycle
     * @throws Exception
     */
    public void getParentName(IRequestCycle cycle) throws Exception{
    	IData param = this.getData();  
    	IDataset result  = call("PB.AddressQryTreeSVC.getParentName", param);
    	this.setAjax(result);
    }
    
    /**
     * 标准地址树加载
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadTreeData(IRequestCycle cycle) throws Exception
    {
        /** 定义节点参数 */
        TreeParam param = TreeParam.getTreeParam(cycle);
        IData params = getData();
        IData houseLevel=new DataMap();
        
        
        String cityCode = params.getString("CITY_CODE", "");
        String treeSearch = params.getString("TREE_SEARCH", "").trim();
        String houseSearch=params.getString("HOUSE_SEARCH","").trim();
        if (!StringUtils.isBlank(cityCode)){
            String parent_id = param.getParentNodeId();
           
            /** 若没有上级节点，表示第一次载入，此时初始根节点 */
            if (parent_id == null){
                IData inParam = new DataMap();
                inParam.put("TREE_TYPE", "ROOT");
                inParam.put("PARENT_ADDR_ID", cityCode);
                inParam.put("ADDR_NAME", treeSearch);
                inParam.put("HOUSE_NAME",houseSearch);
                IDataset reasonInfos = qryAddressTree(inParam);
                /** 根据上级节点获取节点数据，节点需要包含子节点数量 */
                setAjax(TreeFactory.buildTreeData(param, reasonInfos, "ADDR_ID", "ADDR_NAME", "NODE_COUNT", true));
            }else{
            	houseSearch="";
            	houseLevel.put("PARENT_ID", parent_id);
                IDataset houseInfo=call("PB.AddressQryTreeSVC.getHouseSearchLevel", houseLevel);//查询如果是4级地址以上 HOUSE_SEARCH 为空
                String houselevel=((IData)houseInfo.get(0)).getString("ADDR_LEVEL");
                if("3".equals(houselevel) ||"4".equals(houselevel))
                 	houseSearch=params.getString("HOUSE_SEARCH","").trim();
                IData inParam = new DataMap();
                inParam.put("TREE_TYPE", "CHILD");
                inParam.put("PARENT_ADDR_ID",parent_id);
                inParam.put("ADDR_NAME", treeSearch);
                inParam.put("HOUSE_NAME",houseSearch);
                IDataset reasonInfos = qryAddressTree(inParam);
                
                //展示到几层
                if (IDataUtil.isNotEmpty(reasonInfos) && params.getInt("VIEW_LEVEL" ,0) != 0)
                {
                    String level = reasonInfos.getData(0).getString("ADDR_LEVEL");
                    
                    if (Integer.parseInt(level) >= params.getInt("VIEW_LEVEL"))
                    {
                        for (Iterator iterator = reasonInfos.iterator(); iterator.hasNext();)
                       {
                         IData object = (IData) iterator.next();
                         object.put("NODE_COUNT", "0");                         
                       }
                    }                    
                }                
                /** 根据上级节点获取节点数据，节点需要包含子节点数量 */
                setAjax(TreeFactory.buildTreeData(param, reasonInfos, "ADDR_ID", "ADDR_NAME", "NODE_COUNT", true));
            }
        }else{
            setAjax(new DataMap());
        }

    }
    
    
    
    
    
    
    public IDataset qryAddressTree(IData data) throws Exception
    {
        
        String treeType = data.getString("TREE_TYPE");
        IDataset result = new DatasetList();;
        if (treeType.equals("ROOT"))
        {
            String parentId =  data.getString("PARENT_ADDR_ID");
            String addrName = data.getString("ADDR_NAME");
            String houseName =data.getString("HOUSE_NAME");
            getAddressTreeDataBySearch(parentId,addrName,houseName,treeType,result);
            
            for (Iterator iterator = result.iterator(); iterator.hasNext();){
                IData object = (IData) iterator.next();
                object.put("ADDR_LEVEL", "ROOT");
            }
          //  result = DataHelper.filter(result, "PARENT_ADDR_ID="+parentId);
            DataHelper.sort(result, "ADDR_NAME",IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        else
        {
            String parentId =  data.getString("PARENT_ADDR_ID");
            String addrName = data.getString("ADDR_NAME");
            String houseName =data.getString("HOUSE_NAME");
            getAddressTreeDataBySearch(parentId,addrName,houseName,treeType,result);
            result = DataHelper.filter(result, "PARENT_ADDR_ID="+parentId);
            DataHelper.sort(result, "ADDR_NAME",IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
          return result;
    }
    
    
    protected void getAddressTreeDataBySearch(String parentId,String addrName,String houseName,String treeType,IDataset resultFinal,String...destParentId) throws Exception
    { 
    	IData param = new DataMap();
    	param.put("PARENT_ADDR_ID", parentId);
    	param.put("TREE_TYPE", treeType);
    	if(!StringUtils.isBlank(addrName)){
    		param.put("ADDR_NAME", addrName);
        }
    	if(!StringUtils.isBlank(houseName)){
    		param.put("HOUSE_NAME", houseName);
    	}
        IDataset resultTemp  = call("PB.AddressQryTreeSVC.getAddressTreeDataByParentId", param);
        
        for (Iterator iterator = resultTemp.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            
            if (null != destParentId && destParentId.length > 0)
            {
                item.put("PARENT_ADDR_ID", destParentId[0]);
            }
            //特殊处理,地址名称为-1则本级不展示,取下级地址挂到本级的父节点
            if ("".equals(item.getString("ADDR_NAME","")))
            { 
                 getAddressTreeDataBySearch(item.getString("ADDR_ID"),addrName,houseName,treeType,resultFinal,item.getString("PARENT_ADDR_ID"));
            }
            else
            {
                resultFinal.add(item);
            } 
        } 
    }
    
    public void queryAddress(IRequestCycle cycle) throws Exception{
    	IData param = getData();
    	String svcname = "";
    	IDataOutput infos = new DataOutput();
    	
    	String selectAddr = param.getString("DETAILGIS");
    	String address = param.getString("address");
    	
    	String tempCount = param.getString("tempCount","");
		if("".equals(tempCount)){
			tempCount = "-1";
		}
    	Pagination pagination = this.getPagination("pageNav");
    	
    	if(address.equals(selectAddr)){
    		svcname = "PB.AddressQryTreeSVC.queryAddress";
//    		infos = CSViewCall.callPage(this,svcname, param, pagination);
            infos = call2(svcname, param ,pagination);//modify by duhj
    		
    		if(pagination.isNeedCount()){
    			pagination.setOnlyCount(true);
//    			IDataOutput pageCountResult = CSViewCall.callPage(this,svcname, param, pagination);
    			IDataOutput pageCountResult = call2(svcname, param ,pagination);//modify by duhj

    			tempCount = String.valueOf(pageCountResult.getDataCount());
    			pagination.setOnlyCount(false);
    		}
    		
    	}else{			
			svcname = "PB.AddressQryTreeSVC.queryAddressLike";
//			infos = CSViewCall.callPage(this,svcname, param, pagination);
	        infos = call2(svcname, param ,pagination);//modify by duhj

			
			if(pagination.isNeedCount()){
    			pagination.setOnlyCount(true);
//    			IDataOutput pageCountResult = CSViewCall.callPage(this,svcname, param, pagination);
    			IDataOutput pageCountResult = call2(svcname, param ,pagination);//modify by duhj

    			tempCount = String.valueOf(pageCountResult.getDataCount());
    			pagination.setOnlyCount(false);
    		}
    	}
    	if(infos.getData().size() == 0){
    		tempCount = "0";
    	}
    	
		param.put("DATACOUNT", infos.getDataCount());
		param.put("tempCount", tempCount);
		
		setInfos(infos.getData());        
        setCount(Long.valueOf(tempCount));	
        setCondition(param);    
        IData count=new DataMap();
        count.put("count", Long.valueOf(tempCount));
        this.setAjax(count);
    	
    }
    
    public void queryAddressLike(IRequestCycle cycle) throws Exception{
    	IDataset result = new DatasetList();
    	IData param = getData();
    	String svcname = "PB.AddressQryTreeSVC.queryAddressLike";
//		IDataOutput infos = CSViewCall.callPage(this,svcname, param, this.getPagination("pageNav"));
        IDataOutput infos = call2(svcname, param ,this.getPagination("pageNav"));//modify by duhj

		param.put("DATACOUNT", infos.getDataCount());
		setInfos(infos.getData());        
        setCount((long) infos.getDataCount());	
        setCondition(param);
    	
    }
    
    /**
	 * @param svcName
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected IDataset call(String svcName, IData data) throws Exception{
        if (logger.isDebugEnabled()){
        	logger.debug("ServiceFactory.call Service=[" + svcName + "]");
        }

        IDataInput input = createDataInput(data);
        IDataOutput output = ServiceFactory.call(svcName, input);
        IDataset result = output.getData();
        return result;
    }
	
    // modify by duhj
    protected IDataset call1(String svcName, IData data ,Pagination pagination) throws Exception
    {
        IDataInput input = createDataInput(data);
        String url = "http://10.200.138.15:10001/service";
        IDataOutput output = ServiceFactory.call(url, svcName, input, pagination);
        IDataset result = output.getData();
        return result;
    }
    
    // modify by duhj
    protected IDataOutput call2(String svcName, IData data, Pagination pagination) throws Exception
    {
        IDataInput input = createDataInput(data);
        String url = "http://10.200.138.15:10001/service";
        IDataOutput output = ServiceFactory.call(url, svcName, input, pagination);
        return output;
    }

}
