package com.asiainfo.veris.crm.order.soa.person.rule.run.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;


 

/**
*检查是否移动用户
 */
public class CheckIsNp extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
//        System.out.println("CheckIsNp.javaxxxxxxxxxxxxxxxxxx24 " + xChoiceTag);
        if (StringUtils.isNotBlank(xChoiceTag) && "0".equals(xChoiceTag))// 查询号码时校验
        {
        IDataset  ds = (DatasetList ) databus.get("TF_F_USER");
//        System.out.println("CheckIsNp.javaxxxxxxxxxxxxxxxxxx26 "+databus);
        String serialNumber = ds.getData(0).getString("SERIAL_NUMBER","").trim();
//        System.out.println("CheckIsNp.javaxxxxxxxxxxxxxxxxxx28 "+serialNumber);
        
        String nettypecode = ds.getData(0).getString("NET_TYPE_CODE","").trim();
//        System.out.println("CheckIsNp.javaxxxxxxxxxxxxxxxxxx37 "+nettypecode);
        
        if(nettypecode.equals("11")
         ||nettypecode.equals("12")
         ||nettypecode.equals("13")
         ||nettypecode.equals("14")
         ||nettypecode.equals("15")
         ){
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2017040601, "只有归属移动的号码才能办理账户资料变更业务！");
            return true;            
        }
        
       /* FamilyTradeBean bean = (FamilyTradeBean) BeanManager.createBean(FamilyTradeBean.class);
        boolean flag = bean.isNpUser(serialNumber);
        System.out.println("CheckIsNp.javaxxxxxxxxxxxxxxxxxx32 "+flag);
        if (!flag) {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2017040601, "只有归属移动的号码才能办理账户资料变更业务！");
            return true;
            }
        }*/
        
        }
        return false;
       
    }

}
