package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckPayRelaAdvForArea extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        //判断主号码账户id归属和副号码用户id归属是否符合以下条件:
        //1.  当主号码账户id归属为HNSJ、HNHN,用户id的归属必须和主要码一致.
        //2.  当主号码账户id归属不为HNSJ、HNHN,用户id的归属不能为HNSJ、HNHN.

        String groupAcctCityCode = databus.getString("GROUP_ACCT_CITY_CODE","");
        String userCityCode = databus.getString("USER_CITY_CODE","");
        if(!"".equals(groupAcctCityCode) && !"".equals(userCityCode))
        {
            if("HNSJ".equals(groupAcctCityCode) || "HNHN".equals(groupAcctCityCode))
            {
                if(!groupAcctCityCode.equals(userCityCode))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "32701", "成员号码与集团的归属业务区不一致，此业务不能继续办理，请更换成员号码!");
                    return false;
                }
            }
            else
            {
                if("HNSJ".equals(userCityCode) || "HNHN".equals(userCityCode))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "32702", "成员号码的业务区编码不能为HNSJ或HNHN!");
                    return false;
                }
            }
        }
        
        return true;
    }

}
