
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import java.util.Iterator;

public abstract class SaleActiveGoodsManage extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData(); 
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset checkResult = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data);
        this.setProducts(checkResult);
    }
    
    public void getPackages(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset condition = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data); 
        this.setPackages(condition);

    }
    public void getGoods(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset condition = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data); 
        this.setGoods(condition);

    }
    public void queryGoodsLists(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset condition = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data); 
        this.setInfos(condition);

    }
    
    public void getGoodsElem(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset goods = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data); 
        IData rtnData=new DataMap();
        if(goods!=null&&goods.size()==1){
        	rtnData=goods.getData(0);
        	rtnData.put("RTN_TYPE", "1");
        }else if(goods.size()>1){
        	rtnData.put("RTN_TYPE", "9");
        	rtnData.put("RTN_COMM", "包'"+data.getString("PACKAGE_ID")+"'配置的D类型的礼品超过1条，请找相关人员确认TD_B_SALE_GOODS配置！");
        }else{
        	rtnData.put("RTN_TYPE", "9");
        	rtnData.put("RTN_COMM", "未找到包'"+data.getString("PACKAGE_ID")+"'的D类型礼品配置，请找相关人员确认TD_B_SALE_GOODS配置！");
        }
        this.setAjax(rtnData);
    }
    
    public void checkIfExist(IRequestCycle cycle) throws Exception
    {

    	IData data = getData(); 
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset ifExistSet = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data); 
        IData rtnData=new DataMap();
        String ifExist=ifExistSet.getData(0).getString("IF_EXIST","");
        if(!"".equals(ifExist)&&"0".equals(ifExist)){
        	rtnData.put("RTN_TYPE", "0");//0代表没有数据
        }else{
        	rtnData.put("RTN_TYPE", "9");
        	rtnData.put("RTN_COMM", "礼品配置表TD_B_SALE_GOODS_EXT已存在该配置，请不要重复添加或修改！");
        }
         
        this.setAjax(rtnData);
    }
    
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.SaleActiveGoodsSVC.getSaleGoodsCond", data);
        //setAjax(dataset);
    }
   
    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);
    public abstract void setCond(IData cond);
    
    public abstract void setPackages(IDataset deposits);
    public abstract void setProducts(IDataset servs); 
    public abstract void setGoods(IDataset goods);
      
}
