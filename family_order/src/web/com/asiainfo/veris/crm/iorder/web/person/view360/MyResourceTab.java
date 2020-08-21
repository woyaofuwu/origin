
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.tapestry.IRequestCycle;

public abstract class MyResourceTab extends PersonBasePage {
    /**
     * 用户资源信息查询
     *
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        // 如果查询所有资源复选框勾选，将 SelectTag = 1 传入后台
        String selectTag = "true".equals(data.getString("QUERY_ALL", "false")) ? "1" : "0";
        data.put("SelectTag", selectTag);
        String fuzzyFlag = data.getString("PARAM");
        if("no".equals(fuzzyFlag)){//免模糊化查询
        	data.put("X_DATA_NOT_FUZZY", true);
        }
        IDataset results = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryResourceInfo", data);	
        for (int i = 0; i < results.size(); i++) {
            IData result = results.getData(i);
            String resTypeCode = result.getString("RES_TYPE_CODE", "");
            String emptyCardId = result.getString("RSRV_STR5", "");
            String IMSI = result.getString("IMSI", "");
            if (resTypeCode.equals("1") && !"01".equals(emptyCardId)) {
                result.put("EmptyCardId", emptyCardId);
            } else {
                result.put("EmptyCardId", "无");
            }
            if (StringUtils.isBlank(IMSI)) {
                result.put("IMSI", "无");
                
            } else if("yes".equals(fuzzyFlag)){
            	boolean isPriv=StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012");
                //该工号没有免模糊化权限
                if (!isPriv){
                	//判断IMSI号是否为空
                	//IMSI号进行模糊化（第11位后用*代替）
                	if(!"".equals(IMSI) && IMSI.length()>11){
                		IMSI=IMSI.substring(0,11)+"****";
                		result.put("IMSI", IMSI);
            		}
                }
            }
            
        }
        
        setInfos(results);
    }

    public abstract void setInfos(IDataset infos);
    
    
}
