
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyCreate extends PersonBasePage
{

    /**
     * 添加副号码的校验
     * 
     * @param cycle
     * @throws Exception
     * @author zhouwu
     * @data 2014-05-28 15:29:18
     */
    public void checkAddMeb(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.FamilyCreateSVC.checkAddMeb", pageData);
        this.setAjax(rtDataset.getData(0));
    }
    
    
    public void obtainShortCodes(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	String existShortCodes=pageData.getString("EXIST_SHORT_CODES","");
    	
    	IData param=new DataMap();
    	param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    	IDataset shortCodes = CSViewCall.call(this, "SS.FamilyCreateSVC.obtainShortCodes", param);
    	if(IDataUtil.isNotEmpty(shortCodes)){
    		
    		Map<String, IData> shortContainer=new HashMap<String, IData>();
    		for(int i=0,size=shortCodes.size();i<size;i++){
    			shortContainer.put(shortCodes.getData(i).getString("DATA_ID"), 
    					shortCodes.getData(i));
    		}
    		
    		if(existShortCodes!=null&&!existShortCodes.trim().equals("")){
        		String[] existShortCodesArr=existShortCodes.split(";");
        		
        		for(int i=0,size=existShortCodesArr.length;i<size;i++){
        			if(existShortCodesArr[i]!=null&&!existShortCodesArr[i].trim().equals("")){
        				if(shortContainer.containsKey(existShortCodesArr[i])){
        					shortCodes.remove(shortContainer.get(existShortCodesArr[i]));
        				}
        			}
        		}
        	}
    	}else{
    		shortCodes=new DatasetList();
    	}
    	
    	IData result=new DataMap();
    	result.put("SHORT_CODES", shortCodes);
    	
    	setAjax(result);

    }
    

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData fmyParam = new DataMap();

        IData loadInfo = CSViewCall.call(this, "SS.FamilyCreateSVC.loadFamilyCreateInfo", pageData).getData(0);
        fmyParam.putAll(loadInfo);
        IDataset productList = loadInfo.getDataset("PRODUCT_LIST");
        setProductList(productList);

        if (productList.size() == 1)
        {
            String productId = productList.getData(0).getString("PRODUCT_ID");
            fmyParam.put("PRODUCT_ID", productId);

            IDataset discntList = loadInfo.getDataset("DISCNT_LIST");
            setDiscntList(discntList);

            if (discntList.size() == 1)
            {
                String discntCode = discntList.getData(0).getString("DISCNT_CODE");
                fmyParam.put("DISCNT_CODE", discntCode);
            }

            IDataset viceDiscntList = loadInfo.getDataset("VICE_DISCNT_LIST");

            if (viceDiscntList.size() == 1)
            {
                String viceDiscntCode = viceDiscntList.getData(0).getString("DISCNT_CODE");
                fmyParam.put("VICE_DISCNT_CODE", viceDiscntCode);
            }

            setViceDiscntList(viceDiscntList);
            setAppDiscntList(null);//loadInfo.getDataset("APP_DISCNT_LIST")
        }

        if (StringUtils.isNotBlank(loadInfo.getString("PRODUCT_ID")))
        {
            fmyParam.put("PRODUCT_ID", loadInfo.getString("PRODUCT_ID"));
            fmyParam.put("SERVICE_ID", loadInfo.getString("SERVICE_ID"));
            fmyParam.put("CREATE_FLAG", "true");
        }

        setFmyParam(fmyParam);
        setViceInfos(loadInfo.getDataset("MEB_LIST"));

        IDataset verifyModeList = new DatasetList();
        IDataset tmpList = StaticUtil.getStaticList("FAMILY_VERIFY_MODE");
        // 判断是否具有“免密码”权限
        boolean isPriv = StaffPrivUtil.isPriv(getVisit().getStaffId(), "FAMILY_NO_VERIFY", "1");
        if (!isPriv)
        {
            for (int i = 0, size = tmpList.size(); i < size; i++)
            {
                IData tmp = tmpList.getData(i);
                String dataId = tmp.getString("DATA_ID");
                if (dataId.equals("2"))
                {
                    continue;
                }
                verifyModeList.add(tmp);
            }
        }else{
        	verifyModeList.addAll(tmpList);
        }

        this.setVerifyModeList(verifyModeList);
        
        String isJwt=loadInfo.getString("ISJWT_FLAG");
        
        String valideMemberNumber="-1";
        //查询有效成员的数量
        if(IDataUtil.isNotEmpty(loadInfo.getDataset("MEB_LIST"))&&
        		loadInfo.getDataset("MEB_LIST").size()>0){
        	valideMemberNumber=String.valueOf(loadInfo.getDataset("MEB_LIST").size()+1);
        }
        
        IData returnParam=new DataMap();
        returnParam.put("isJwt", isJwt);
        returnParam.put("VALIDE_MEBMER_NUMBER", valideMemberNumber);
        
        this.setAjax(returnParam);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.putAll(getData("FMY"));
        IDataset rtDataset = CSViewCall.call(this, "SS.FamilyCreateRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setAppDiscntList(IDataset appDiscntList);

    public abstract void setDiscntList(IDataset discntList);

    public abstract void setFmyParam(IData fmyParam);

    public abstract void setProductList(IDataset productList);

    public abstract void setVerifyModeList(IDataset verifyModeList);

    public abstract void setViceDiscntList(IDataset viceDiscntList);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
