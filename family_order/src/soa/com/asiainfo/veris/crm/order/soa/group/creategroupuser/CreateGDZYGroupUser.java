
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

/**
 * 集团定制云产品新增
 * @author think
 *
 */
public class CreateGDZYGroupUser extends CreateGroupUser
{
	private static final Logger logger = Logger.getLogger(CreateGDZYGroupUser.class);
	
    public CreateGDZYGroupUser()
    {
       
    }

    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        regOtherInfoTrade();
    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

    }
    
    @Override
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
        
    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }
    
    public void regOtherInfoTrade() throws Exception
    {
    	IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();
        IData data = null;
        IDataset otherInfos = new DatasetList(param.getString("NOTIN_AttrGdzy", "[]"));
        if(IDataUtil.isNotEmpty(otherInfos))
        {
        	for (int i = 0; i < otherInfos.size(); i++)
            {
        		 data = new DataMap();
                 IData info = otherInfos.getData(i);

                 data.put("USER_ID", reqData.getUca().getUserId());
                 data.put("RSRV_VALUE_CODE", "GDZY");
                 data.put("START_DATE", getAcceptTime());
                 //data.put("END_DATE", SysDateMgr.getTheLastTime());
                 
                 //收费名称,用收费截止时间来填写end_date
                 String feeNameV = info.getString("pam_NOTIN_FEE_NAME");
                 if("4".equals(feeNameV)) //4其他一次性费用
                 {
                	 data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                 }
                 else 
                 {
                	 String endDate = info.getString("pam_NOTIN_FEE_END_DATE");
                     if(StringUtils.isNotBlank(endDate) && (endDate.length() == 10))
                     {
                    	 endDate = endDate + " 23:59:59";
                     }
                     data.put("END_DATE", endDate);
                 }
                 
                 
                 data.put("RSRV_VALUE", info.getString("pam_NOTIN_OPER_TAG"));
                 
                 //项目名称
                 data.put("RSRV_STR1", info.getString("pam_NOTIN_PROJECT_NAME"));
                 //收费名称
                 data.put("RSRV_STR2", info.getString("pam_NOTIN_FEE_NAME"));
                 //收费金额
                 data.put("RSRV_STR3", info.getString("pam_NOTIN_FEE_COST"));
                 //收费截止时间
                 data.put("RSRV_STR4", info.getString("pam_NOTIN_FEE_END_DATE"));
                 //备注
                 data.put("RSRV_STR6", info.getString("pam_NOTIN_REMARK"));
                 dataset.add(data);
            }
        	addTradeOther(dataset);
        }
    }
}
