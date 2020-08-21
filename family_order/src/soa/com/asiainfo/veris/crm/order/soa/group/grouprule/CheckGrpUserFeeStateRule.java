package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class CheckGrpUserFeeStateRule extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String grpUserId = databus.getString("USER_ID");                //集团用户ID
        String mebUserId = databus.getString("USER_ID_B");              //成员用户ID
        String serial_number = databus.getString("SERIAL_NUMBER");      // 成员手机号码
        String productId = databus.getString("PRODUCT_ID");
        String payTypeCode = databus.getString("PLAN_TYPE_CODE", "");       //付费计划:G-集团付
        //集团付的时候才做集团用户欠费校验
        if(!"G".equals(payTypeCode)){
            return true;
        }
        //判断该产品是否需要做欠费判断
        IDataset paramDs = ParamInfoQry.getCommparaByCode("CSM", "1138", productId, "0898");
        if(IDataUtil.isNotEmpty(paramDs)){
        	IData grpUser = UcaInfoQry.qryUserMainProdInfoByUserId(grpUserId);
        	if(IDataUtil.isNotEmpty(grpUser))
        	{
        		String rsrvStr10 = grpUser.getString("RSRV_STR10", "");	//集团代付暂停恢复标识
                if("CRDIT_STOP".equals(rsrvStr10)){
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20161219801", "集团产品（用户）已欠费，不能办理该业务！");
                    return false;
                }
        	}
        }
        
        return true;
    }

}
