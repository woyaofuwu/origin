
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupBizNoRuleCheckUtil;

public class ChkDiscntToCommparaValid extends BreBase implements IBREScript
{
    /**
     * 查询选择的折扣套餐是否有对应的原始套餐，折扣套餐去除前三位1004485--4485
     */
    private static final long serialVersionUID = -245534769209563115L;

    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        String err = "";        
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS"); // 所有选择的元素
        String meb_userId = databus.getString("USER_ID_B", "-1");//用户ID
        String grp_userId = databus.getString("USER_ID", "-1");//集团用户ID
        if (StringUtils.isBlank(userElementsStr))
        {
            return true;
        }
        IDataset userElements = new DatasetList(userElementsStr);
        for(int i = 0; i < userElements.size(); i++)
        {
            IData discnt = userElements.getData(i);
            String element_id = discnt.getString("ELEMENT_ID", "");
            if(StringUtils.isBlank(element_id))
            {
                return true;
            }
            
            String modify_tag = discnt.getString("MODIFY_TAG","");
            String eleTypeCode = discnt.getString("ELEMENT_TYPE_CODE","");
            if(!"0".equals(modify_tag)&&!"2".equals(modify_tag))
            {
                continue;
            }
            
            
            if(StringUtils.isNotBlank(element_id) && "D".equals(eleTypeCode) && "0".equals(modify_tag))
            {
                //选择了整体打折的套餐不做校验
                IDataset commFilter =  CommparaInfoQry.getCommparaInfoBy5("CSM","6013","GPWP_FILTER",element_id,"0898",null);
                if(IDataUtil.isNotEmpty(commFilter)){
                    continue;
                }
            }  
            
            IDataset ds =  CommparaInfoQry.getCommparaInfoBy5("CSM","6013","GPWP",element_id,"0898",null);
            if(IDataUtil.isNotEmpty(ds))
            {
                String discnt_code = ds.getData(0).getString("PARA_CODE2", "");
                int sum = UserDiscntInfoQry.getDiscntByMUIdToCommpara(meb_userId, element_id, grp_userId);
                if(sum == 0)
                {
                    err = "选择的折扣套餐【" + element_id + "】没有查询到符合对应的套餐【" + discnt_code +"】";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
            
            /*add by chenzg@20161024判断是否有套餐(10010201 工作手机省内语音包,10010202 工作手机省内流量包)的折扣权限*/
            String checkInfo = GroupBizNoRuleCheckUtil.checkGPWPDiscntAttrPriv(discnt);
            if(StringUtils.isNotBlank(checkInfo)){
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, checkInfo);
                return false;
            }
            
        }
        return true;
    }
}
