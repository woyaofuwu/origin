
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserUnionInfoQry;

/**
 * 校验是否是宽带用户
 * 
 * @author likai3
 */
public class CheckBookDiscnt extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            IDataset userDiscntAll = UserDiscntInfoQry.queryUserAllDiscntByUserId(databus.getString("USER_ID"));
            if(IDataUtil.isNotEmpty(userDiscntAll)){
                for(int i=0 ;i<userDiscntAll.size();i++){
                    IData userDiscnt = userDiscntAll.getData(i);
                    if(!StringUtils.equals(userDiscnt.getString("PRODUCT_ID"),"-1")&&userDiscnt.getString("START_DATE").compareTo(SysDateMgr.getSysDate())>0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
