
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.tapestry.IRequestCycle;

public abstract class MyAccountTab extends PersonBasePage
{

    /**
     * 账户信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
    	
        IData inParam = getData();
        String fuzzyFlag = inParam.getString("PARAM");
        if("no".equals(fuzzyFlag)){
        	inParam.put("X_DATA_NOT_FUZZY", true);
        }
        IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryAccountInfo", inParam);
        if("yes".equals(fuzzyFlag)){//模糊化查询
        	
        	boolean isPriv=StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012");
            //该工号没有免模糊化权限
    		if (!isPriv) {
    			if(IDataUtil.isNotEmpty(output)){
    				for (int i = 0, count = output.size(); i < count; i++){
    					IData map = output.getData(i);
    					String openTare = map.getString("OPEN_DATE");
    			        if (StringUtils.isNotBlank(openTare)) {
    						if (openTare.length()>10) {
    							StringBuilder vagueSB = new StringBuilder(19);
    							for(int j=0;j<11;j++){
    								vagueSB.append(openTare.charAt(j));
    							}for(int j=14;j<=19;j++){
    								vagueSB.append("*");
    							}
    							map.put("OPEN_DATE",vagueSB.toString());
    						}
    					}
    				}
    				
    			}
    			
    		}
        	
        }

        setInfo(output.first());
    }

    public abstract void setInfo(IData info);
   
    
}
