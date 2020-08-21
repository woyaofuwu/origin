package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.tapestry.IRequestCycle;
import com.ailk.biz.BizEnv;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SHA256Util;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;


public abstract class LoginJkHall extends CSBasePage
{
	public abstract void setCondition(IData condition);	
    
    public void initial(IRequestCycle cycle) throws Exception
    {

    	String staffId = getVisit().getStaffId();
        IData data = new DataMap();
        data.put("STAFF_ID", staffId);
         
    	String jkUrl = BizEnv.getEnvString("JKHALL_PARAMS_URL");
        String appSecret =BizEnv.getEnvString("JKHALL_PARAMS_APPSECRET");
        String appkey = BizEnv.getEnvString("JKHALL_PARAMS_APPKEY");
        String timestamp  = new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date());

        String staffnumber = "";
		IData result=CSViewCall.callone(this, "CS.StaffInfoQrySVC.queryStaffInfoForBBoss", data);
      
		 if(result!=null&&result.size()>0) {
        	staffnumber = result.getString("STAFF_NUMBER");
        	if(staffnumber!=null&&!staffnumber.equals("")){
				String secret = SHA256Util.getSHA256Str(appSecret+appkey+staffnumber+timestamp);
				jkUrl = jkUrl+"?"+"staffnumber="+staffnumber+"&timestamp="+timestamp+"&appkey="+appkey+"&secret="+secret;
				IData map = new DataMap();
				map.put("url", jkUrl);
				setCondition(map);
			}else{
				setCondition(new DataMap());

			}

        }else {
        	setCondition(new DataMap());
        }

    }
}
