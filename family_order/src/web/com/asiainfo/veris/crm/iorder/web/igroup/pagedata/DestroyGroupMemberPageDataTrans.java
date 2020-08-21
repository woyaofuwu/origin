package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;

public class DestroyGroupMemberPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();
        
        IData data = new DataMap();
        
        //成员用户信息
        IData memSubscriber = getMemSubscriber();
        if(DataUtils.isEmpty(memSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员用户信息数据结构！");
        }
        data.put("SERIAL_NUMBER", memSubscriber.getString("SERIAL_NUMBER"));
        
        // 集团用户信息
        IData ecSubscriber = getEcSubscriber();
        if(DataUtils.isEmpty(ecSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到集团用户信息数据结构！");
        }
        data.put("USER_ID", ecSubscriber.getString("USER_ID"));
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo))
        {
            data.putAll(commonInfo);
        }
        
        return data;
    }
    
    public void setServiceName() throws Exception
    {
    	String brandCode = getBrandCode();
    	if("BOSG".equals(brandCode)){
    		 setSvcName("CS.DestroyBBossMemSVC.dealBBossMebBiz");
    	}else{
            setSvcName(EcConstants.EC_OPER_SERVICE.DELETE_ENTERPRISE_MEMBER.getValue());
        }

    }

}
