
package com.asiainfo.veris.crm.iorder.web.person.saleactive;

import java.util.StringTokenizer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SaleActiveRuleCheckNew extends PersonBasePage
{
	/**
	 * 取活动
	 * */
    public void initCampnTypes(IRequestCycle cycle) throws Exception
    {
    	IData params = this.getData(); 
        
        IDataset activeList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryCampnTypes", params);
        setBatcampnTypes(activeList);;
    } 
    
    /**
     * 取产品
     * */
    public void queryProdByLabel(IRequestCycle cycle) throws Exception
    {
    	IData params =  getData();
    	params.put("EPARCHY_CODE", this.getTradeEparchyCode());
    	IDataset prodList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryProductsByLabel", params);
		
    	if (IDataUtil.isNotEmpty(prodList)) {
			for (int i = 0; i < prodList.size(); i++) {
				IData prodMap = prodList.getData(i);
				prodMap.put("SALE_PRODUCT_NAME", prodMap.getString("PRODUCT_ID") + "|" + prodMap.getString("PRODUCT_NAME"));
			}
		}
		setSaleProducts(prodList);
    	setAjax(prodList);
    }
    
    /**
     * 取包列表
     * */
    public void queryPackageList(IRequestCycle cycle) throws Exception
    {
    	IData params =  getData();
    	params.put("EPARCHY_CODE", this.getTradeEparchyCode());
    	IDataset packList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryPackageByProdID", params);
    	setSalePackages(packList);
    	setAjax(packList);
    } 
    
    
    /**
     * 校验规则
     * */
    public void checkSaleActiveRule(IRequestCycle cycle) throws Exception{
    	IData cond = this.getData();
        Pagination page = getPagination("listnav"); 
    	IDataOutput callOut = CSViewCall.callPage(this, "SS.SaleActiveSVC.checkPrdAndPkgForSMS", cond,page);
    	IDataset callSet= callOut.getData();
        
    	IDataset rtnSet=new DatasetList();
    	
        for(int k=0;k<callSet.size();k++){ 
        	IData otherRightData=callSet.getData(k);
        	/*
        	 * 0$$查询成功  14024019$$该用户不是个人宽带装机完工用户！
        	 * 14024019$$该用户不是个人宽带装机完工用户！##14024019$$该用户不是个人宽带装机完工用户！
        	 * */
        	String result=otherRightData.getString("RESULTS","");
        	/*
        	 * 接口可能涉及工号权限，返回有所不同，要分别判断
        	 * */
        	if("".equals(result)){
        		String xResultCode=otherRightData.getString("X_RESULTCODE","");
        		String xResultInfo=otherRightData.getString("X_RESULTINFO","");
        		IData errData=new DataMap();
        		errData.put("ERR_NUM", xResultCode);
        		errData.put("ERR_INFO", xResultInfo);
        		errData.put("DEAL_DESC", "产品及权限错误");
        		errData.put("DEAL_LINK", "产品及权限错误");
				rtnSet.add(errData);
        	}else{
        	
	        	StringTokenizer st=new StringTokenizer(result,"##");
	        	
				while(st.hasMoreElements()){
					IData rtnData=new DataMap();
					String rtnCode="";
		        	String rtnInfo="";
					String code_info=st.nextToken();
					rtnCode=code_info.substring(0,code_info.indexOf("$$"));
					rtnInfo=code_info.substring(code_info.indexOf("$$")+2);
					if("0".equals(rtnCode)){
						rtnInfo="该用户可以办理此活动。";
					}
					rtnData.put("ERR_NUM", rtnCode);
					rtnData.put("ERR_INFO", rtnInfo);
					IData commParam=new DataMap();
					commParam.put("ERR_CODE", rtnCode);
					//获取commpara param_attr=1581 配置信息
					IDataset linkInfos =  CSViewCall.call(this, "SS.SaleActiveRuleCheckSVC.getCommparaInfo", commParam);
					String pageLink="";//链接地址
					String descInfo="";
					if(linkInfos!=null && linkInfos.size()>0){
						pageLink="";//链接地址
						descInfo=linkInfos.getData(0).getString("PARA_CODE1","");//描述文字
						String modCode=linkInfos.getData(0).getString("PARA_CODE2","");//链接配置
						//链接配置不为空，取链接
						if(!"".equals(modCode)){
							IData modParam=new DataMap();
							modParam.put("MOD_CODE", modCode);
							IDataset modFiles =  CSViewCall.call(this, "SS.SaleActiveRuleCheckSVC.qryModfileInfo", modParam);
							if(modFiles!=null && modFiles.size()>0){
								String title=modFiles.getData(0).getString("MENU_TITLE","");
								String link=modFiles.getData(0).getString("MOD_NAME","");
								if(!"".equals(link)){
									String para1=title;
									String para2="";
									if(link.indexOf("&")>-1){
										para2=link.substring(link.indexOf("page/")+5,link.indexOf("&"));
									}else{
										para2=link.substring(link.indexOf("page/")+5);
									}
									String para3="";
									String para4="";
									if(link.indexOf("listener")>-1 ){
										int idx1=link.indexOf("&");
										int idx2=link.lastIndexOf("&");
										if(link.indexOf("&")!=link.lastIndexOf("&")){
											para3=link.substring(link.indexOf("listener=")+9,link.lastIndexOf("&"));
										}else{
											para3=link.substring(link.indexOf("listener=")+9);
										}
									}
									if(link.indexOf("&")!=link.lastIndexOf("&")){
										para4=link.substring(link.lastIndexOf("&"));
									}
									
									pageLink="<a href=\"#nogo\" onclick=\"javascript:openNav('"+para1+"','"+para2+"','"+para3+"','"+para4+"')\">"+para1+"</a>";
								}else{
									pageLink=descInfo;
								} 
							}
						}else{
							pageLink=descInfo;
						} 
					} 
					rtnData.put("DEAL_DESC", descInfo);
					rtnData.put("DEAL_LINK", pageLink);
					rtnSet.add(rtnData);
				}
        	}
        }
        //long dataCount=result.getDataCount();
        //setRecordCount(dataCount);
        setInfos(rtnSet); 
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset products); 
    
    public abstract void setBatcampnTypes(IDataset products);  
    
    public abstract void setSaleProducts(IDataset saleProducts);  
    
    public abstract void setSalePackages(IDataset salePackages);  
    
    public abstract void setRecordCount(long recordCount);
}
