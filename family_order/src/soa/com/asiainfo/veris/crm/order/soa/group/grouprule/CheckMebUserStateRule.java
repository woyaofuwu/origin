
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckMebUserStateRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = -4156033590753146896L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // 校验成员
        String serial_number = databus.getString("SERIAL_NUMBER");// 成员手机号码
        String productId = databus.getString("PRODUCT_ID");
        String ctrlType = databus.getString("CTRL_TYPE");// 业务操作类型

        if (StringUtils.isNotBlank(ctrlType))
        {
            // 产品控制信息
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);

            String grp_mebcheckflag = ctrlInfo.getAttrValue("GRP_MEBCHECKFLAG");

            if (StringUtils.equals("true", grp_mebcheckflag))// true表示不需要校验成员状态
            {
                return true;
            }
        }

        UcaData memUcaData = UcaDataFactory.getNormalUcaForGrp(serial_number, false, false);
        
        //BBOSS成员注销时，无论成员的状态是否正常，都能够进行注销
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(databus);       
        String brandCode = grpUcaData.getBrandCode();
        if(StringUtils.equals("BOSG", brandCode) && StringUtils.equals(BizCtrlType.DestoryMember, ctrlType)){
            return true;
        }

        // 判断服务号码状态
        String state = memUcaData.getUser().getUserStateCodeset();
        if (!"0".equals(state) && !"N".equals(state) && !"00".equals(state))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408151708", "该用户[" + serial_number + "]服务状态处于非正常状态！");
            return false;
        }

        return true;
    }

}
