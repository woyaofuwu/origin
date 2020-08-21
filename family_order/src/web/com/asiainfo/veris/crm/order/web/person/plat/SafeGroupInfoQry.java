
package com.asiainfo.veris.crm.order.web.person.plat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SafeGroupInfoQry extends PersonBasePage
{
    public abstract void setFamilyMember(IDataset familyMember);
    public abstract void setFamilyMemberinfo(IData familyMemberinfo);
    
    
    
    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        String group_area = data.getString("GROUP_NAME");
        String target_msisdn = data.getString("TARGET_MSISDN");
        String target_name = data.getString("TARGET_NAME");

        IData param = new DataMap();
		param.put("GROUP_NAME", group_area);
		param.put("TARGET_MSISDN", target_msisdn);
		param.put("TARGET_NAME", target_name);
		param.put("STATUS", "1");
		param.put("GROUP_TYPE", "02");
        
        IDataset datas = CSViewCall.call(this, "SS.QryPlatSVC.getSafeGroupInfo", param);

        // 查询子业务信息
        setFamilyMember(datas);

        //查询成功
        this.setAjax("FLAG", "true");
    }
}
