
package com.asiainfo.veris.crm.order.soa.person.busi.mobileSpecialepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class MobileSpecialepaySVC extends CSBizService
{

    public IData getInfo(IData input) throws Exception
    {
    	
    	String userid = input.getString("USER_ID");
    	IDataset results = UserOtherInfoQry.getUserOther(userid, "30");
        IDataset resultset = UserOtherInfoQry.getUserOther(userid, "40");
        IData info = new DataMap();

        if(IDataUtil.isNotEmpty(results)&&IDataUtil.isNotEmpty(resultset)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户有移动员工代付费和非移动员工代付费，无法办理员工代付费调整!");
        }else{
        	
        	if(IDataUtil.isNotEmpty(results)){
        		info.put("RSRV_VALUE", results.getData(0).getString("RSRV_VALUE", ""));
        	}
        	if(IDataUtil.isNotEmpty(resultset)){
        		info.put("RSRV_VALUE", resultset.getData(0).getString("RSRV_VALUE", ""));
        	}
        }
        
        return info;

    }

}
