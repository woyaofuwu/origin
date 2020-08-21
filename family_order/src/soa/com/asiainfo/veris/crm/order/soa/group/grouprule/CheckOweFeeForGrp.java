
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class CheckOweFeeForGrp extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	String staffId = CSBizBean.getVisit().getStaffId();
    	boolean prvGrp = StaffPrivUtil.isFuncDataPriv(staffId, "DESTROY_OWEFEEGRP_PRV");
    	if(prvGrp)
    	{
    		return false;
    	}
    	
        String userId = databus.getString("USER_ID", "");

        String productId = databus.getString("PRODUCT_ID", "");

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        String destroyAttr = databus.getString("DESTROY_ATTR", "");
        // 集团产品欠费注销
        if ("OWEFEE".equals(destroyAttr))
        {
            return false;
        }

        // 设置默认值
        String judgeOweTag = "1"; // 任何情况下都判断欠费
        String acctBalance = "0"; // 实时结余

        if (!"0".equals(judgeOweTag) && !"GS01".equals(brandCode) && !brandCode.startsWith("VP") && !brandCode.equals("ESPG"))
        {
            IData oweFeeData = AcctCall.getOweFeeByUserId(userId);

            acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
            
        } else if(!"0".equals(judgeOweTag) && ("VPEN".equals(brandCode) 
                || "VPMN".equals(brandCode) || "VPMR".equals(brandCode) || "VPMB".equals(brandCode))){
            
            IData oweFeeData = AcctCall.getOweFeeByUserId(userId);

            acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
            
        }

        // 查询用户预受理业务
        IDataset userOtherList = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, "PRTA", Route.CONN_CRM_CG, null);

        int prtaSize = 0;

        if (IDataUtil.isNotEmpty(userOtherList))
        {
            prtaSize = userOtherList.size();
        }

        if (Double.parseDouble(acctBalance) < 0 && prtaSize == 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "46_1150", "业务受理前条件判断: 用户已经欠费不能办理业务!");
            return false;
        }

        return false;
    }

}
