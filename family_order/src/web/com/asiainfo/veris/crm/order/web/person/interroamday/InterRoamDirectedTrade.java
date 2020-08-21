
package com.asiainfo.veris.crm.order.web.person.interroamday;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterRoamDirectedTrade extends PersonBasePage
{


    /**
     * 查询定向套餐开通地区信息，套餐名
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        /*** add by xieyf5 ***/
        IData input = getData();
        IData userInfo = new DataMap(input.getString("USER_INFO"));
        String serial_number =  userInfo.getString("SERIAL_NUMBER");
        /*** add by xieyf5 ***/

        IData params = new DataMap();
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "1212");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        params.put("SERIAL_NUMBER", serial_number);
        IDataset areaInfos = CSViewCall.call(this, "SS.InterRoamDayTradeSVC.queryDiscntAreas", params);
        
        IData euroArea = new DataMap();
        if(IDataUtil.isNotEmpty(areaInfos))
        {
           for (int i = 0; i < areaInfos.size(); i++) 
           {
        	
        	   if("EU".equals(areaInfos.getData(i).getString("PARA_CODE1")))
        	   {
        		   euroArea = areaInfos.getData(i);
        		   break;
        	   }
           }   
           
           setEuroArea(euroArea);
        }
        /*IData params1 = new DataMap();
        params1.put("SUBSYS_CODE", "CSM");
        params1.put("PARAM_ATTR", "1644");
        params1.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset discntInfos = CSViewCall.call(this, "SS.InterRoamDayTradeSVC.queryDiscntInfos", params1);*/
        
        /*IDataset discntInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(areaInfos))
        {
        	for (int i = 0; i < areaInfos.size(); i++) 
        	{
        		IData areaInfo = areaInfos.getData(i);
        		String strParaCode3 = areaInfo.getString("PARA_CODE3", "");
        		String strParaCode20 = areaInfo.getString("PARA_CODE20", "");
        		if(StringUtils.isNotBlank(strParaCode3) && StringUtils.isNotBlank(strParaCode20))
        		{
        			String[] strValues = strParaCode3.split("\\|");
        			String[] strKey = strParaCode20.split("\\|");
        			for (int j = 0; j < strKey.length; j++) 
        			{
        				IData discntInfo = new DataMap();
                		discntInfo.put("PARA_CODE1", strValues[j]);
                		discntInfo.put("PARA_CODE2", strKey[j]);
                		discntInfos.add(discntInfo);
					}
        		}
        		
			}
        }*/
        //查询可退订优惠根据userID
        IData param = new DataMap();
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        IDataset infos = CSViewCall.call(this, "SS.InterRoamDayTradeSVC.queryCancelRoamDict", param); 

        setOutInfos(infos);
        
        
        setAreaInfos(areaInfos);
        //setDiscntInfos(discntInfos);
        
        //办理功能性套餐需要先较验国漫一卡多号是否办理过日租套餐,若已经办理则不能办理功能性套餐
        param.put("RSRV_VALUE_CODE", "SIMM");
        IDataset dataset = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getUserOther", param);
        setDeputyCount("0");
        if (!dataset.isEmpty())
        {
        	for (int i = 0; i < dataset.size(); i++)
            {
                if ("4".equals(dataset.getData(i).getString("RSRV_STR8")) || "5".equals(dataset.getData(i).getString("RSRV_STR8")))
                {
                	setDeputyCount("1");
                	break;
                }
            }
        }
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
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
            
        }                      
        IData param = new DataMap();
//        IDataset dataset = new DatasetList();
        param.putAll(data);
//        IDataset Elements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
//        if (CollectionUtils.isNotEmpty(Elements))
//        {
//            for (int i = 0; i < Elements.size(); i++)
//            {
//                if ("0".equals(Elements.getData(i).getString("MODIFY_TAG")))
//                {
//                    param.put("ELEMENT_ID", Elements.getData(i).getString("ELEMENT_ID"));
//                    param.put("MODIFY_TAG", Elements.getData(i).getString("MODIFY_TAG"));
//                    dataset = CSViewCall.call(this, "SS.InterRoamingSVC.SynchProdCallIboss", param);
//                }
//            }
//        }else{
//            data.put("ELEMENT_ID", data.getString("DISCNT_CODE"));
//            data.put("MODIFY_TAG", "0");
//            dataset = CSViewCall.call(this, "SS.InterRoamingSVC.SynchProdCallIboss", data);
//        }
        
        String tag = data.getString("OP_TAG");
        if("0".equals(tag))
        {
        	// 抵扣期间 不允许发起订购
        	CSViewCall.call(this, "SS.InterRoamDaySVC.checkIsWriteOffPeriod", data);
        	
        	data.put("ELEMENT_ID", data.getString("DISCNT_CODE"));
        	data.put("MODIFY_TAG", "0");
        	data.put("USER_TYPE", "00");
        }else if("1".equals(tag)) //退订
        {
        	data.put("ELEMENT_ID", data.getString("DISCNT_CODE"));
        	data.put("MODIFY_TAG", "1");
        	data.put("USER_TYPE", "00");
        }
        IDataset dataset = CSViewCall.call(this, "SS.InterRoamDayRegSVC.tradeReg", data);
        setAjax(dataset);

    }
    
    //根据开通地区和优惠名查询优惠详情
    public void queryDiscntInfo(IRequestCycle cycle)throws Exception
    {
    	IData data = getData();
    	IData result = new DataMap();
    	result.put("RESULT_CODE", "00");
    	result.put("RESULT_INFO", "OK");
    	String openArea = data.getString("OPEN_AREA");
    	String discntName = data.getString("DISCNT_NAME");
    	String discntCode = "";
    	String elementName = "";
    	String discntFee = "0";
    	IData param = new DataMap();
    	IData discntDetail = new DataMap();
    	param.put("PACKAGE_ID", "99990000");
    	IDataset discntDetails = CSViewCall.call(this, "SS.InterRoamDayTradeSVC.queryDiscntDetails", param);
    	if( discntDetails != null && discntDetails.size()>0){
    		for (int i = 0; i < discntDetails.size(); i++) {
    			if(discntDetails.getData(i).getString("RSRV_TAG1","").equals("2")){		//排除掉能力平台的定向套餐
    				if(openArea.equals(discntDetails.getData(i).getString("RSRV_STR3")) && discntName.equals(discntDetails.getData(i).getString("RSRV_STR4"))){
       				 discntCode = discntDetails.getData(i).getString("ELEMENT_ID");
       				 elementName = discntDetails.getData(i).getString("RSRV_STR2");
       				 discntFee = discntDetails.getData(i).getString("RSRV_STR1");
       				 discntDetail.put("DISCNT_CODE", discntCode);
       				 discntDetail.put("ELEMENT_NAME", elementName);
       				 setDiscntDetail(discntDetail);
       			   }
    			}
    			 
    		}
    	}
    	
    	String serialNumber = data.getString("SERIAL_NUMBER","");
    	param.put("X_GETMODE", "0");
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("DISCNT_FEE", discntFee);
    	result = CSViewCall.callone(this, "SS.InterRoamDayTradeSVC.checkAcctBalanceBySerialNumber", param);
 	   	setAjax(result);
    }
    
    public void queryAreaInfo(IRequestCycle cycle)throws Exception
    {
    	IData data = getData();
    	String openArea = data.getString("OPEN_AREA");
    	String snb = data.getString("snb");
    	IData params = new DataMap();
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "1212");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        params.put("SERIAL_NUMBER", snb);
        IDataset areaInfos = CSViewCall.call(this, "SS.InterRoamDayTradeSVC.queryDiscntAreas", params);
        setAreaInfos(areaInfos);
    	//boolean isBreak = false;
        IDataset discntInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(areaInfos))
        {
        	IData euroArea = new DataMap();
        	for (int i = 0; i < areaInfos.size(); i++) 
        	{
        		IData areaInfo = areaInfos.getData(i);
        		String strParaCode1 = areaInfo.getString("PARA_CODE1", "");
        		String strParaCode3 = areaInfo.getString("PARA_CODE3", "");
        		String strParaCode20 = areaInfo.getString("PARA_CODE20", "");
        		if(StringUtils.isNotBlank(strParaCode3) && StringUtils.isNotBlank(strParaCode20) 
        		   && openArea.equals(strParaCode1))
        		{
        			String[] strValues = strParaCode3.split("\\|");
        			String[] strKey = strParaCode20.split("\\|");
        			for (int j = 0; j < strKey.length; j++) 
        			{
        				IData discntInfo = new DataMap();
                		discntInfo.put("PARA_CODE1", strValues[j]);
                		discntInfo.put("PARA_CODE2", strKey[j]);
                		discntInfos.add(discntInfo);
                		//isBreak = true;
                		
					}
        			setDiscntInfos(discntInfos);
        			euroArea = areaInfos.getData(i);
        			setEuroArea(euroArea);
        			break;
        		}
        		
			}
        }
        
    }
    
    
    public abstract void setAreaInfos(IDataset areaInfos);
    
    public abstract void setDiscntInfos(IDataset discntInfos);
    
    public abstract void setEuroArea(IData euroArea);
    
    public abstract void setDiscntDetail(IData discntDetail);
    
    public abstract void setOutInfos(IDataset outInfos);
    
    public abstract void setDeputyCount(String deputyCount);

}
