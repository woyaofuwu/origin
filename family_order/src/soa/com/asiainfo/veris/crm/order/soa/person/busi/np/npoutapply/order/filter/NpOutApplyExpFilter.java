package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.filter;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class NpOutApplyExpFilter implements IFilterException
{

    @Override
    public IData transferException(Throwable e, IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);
        if(IDataUtil.isNotEmpty(userInfos)){
            String userIdString = userInfos.getData(0).getString("USER_ID");
            input.put("USER_ID", userIdString);
            String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            
       
            
            input.put("X_RESULTCODE", errorArray[0]);
            input.put("X_RSPCODE", errorArray[0]);
            input.put("X_RESULTINFO", errorArray[1]);
            input.put("X_RSPDESC", errorArray[1]);
        }

        return input;
    }

}
