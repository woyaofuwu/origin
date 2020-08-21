
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class CheckPayRelaAdv extends BreBase implements IBREScript
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 549884901873075286L;

	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // 校验成员
        String sn = databus.getString("SERIAL_NUMBER");// 成员手机号码
        String userId = databus.getString("USER_ID");// 成员用户标识
        String acctId = databus.getString("ACCT_ID");// 成员账户标识
        String grpAcctId = databus.getString("GRP_ACCT_ID");// 集团账户ID

        boolean result = false;
        IData idata = RouteInfoQry.getMofficeInfoBySn(sn);
        if (IDataUtil.isEmpty(idata))
        {// common.error("341412", "成员号码非移动号码不存在，业务不能继续！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "341412", "成员号码非移动号码不存在，业务不能继续");
            return result;
        }
        IData payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(payrelationInfo))
        {// common.error("233124", "获取用户普通付费关系无记录!!");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "233124", "获取用户普通付费关系无记录!!");
            return result;
        }
        
        /*--begin--add by chenzg@20160926--REQ201609180012关于优化集团统付业务限制条件的要求---*/
        //不存在往月欠费，不管实时结余+信用度是否>0，只判断手机状态是否正常，如果正常开通则可以办理； 
        IData userInfo = UcaInfoQry.qryUserInfoByUserIdFromDB(userId, Route.CONN_CRM_CG);
        String userState = userInfo.getString("USER_STATE_CODESET", "");
        if(!"0".equals(userState) && !"N".equals(userState)){
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "233125", "该用户手机状态处于非正常状态，请核实恢复后进行办理。");
            return result;
        }
        //调用账务接口
        IData oweFeeInfo = AcctCall.getOweFeeByUserId(userId);
        //存在往月欠费，不管信用度多少，不管当前是否开通，都不能办理；
        double lastOweFee = oweFeeInfo.getDouble("LAST_OWE_FEE", 0);   //往月欠费
        if(lastOweFee>0){
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "233126", "该用户存在往月欠费，请结清欠费后进行办理！");
            return result;
        }
        /*--end----add by chenzg@20160926--REQ201609180012关于优化集团统付业务限制条件的要求---*/
        
        IDataset relationUUInfos = RelaUUInfoQry.queryRelationInfo(userId);
        // common.error("31424", "该用户是一卡双号副卡或一卡付多号并已和主卡绑定付费，不能再办理集团代付!");
        if (IDataUtil.isNotEmpty(relationUUInfos))
        {
            IData relationInfo = (IData) relationUUInfos.get(0);
            String userIdA = relationInfo.getString("USER_ID_A");
            IData grpPayRelaInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
            if (IDataUtil.isNotEmpty(grpPayRelaInfo))
            {
                String acctIda = grpPayRelaInfo.getString("ACCT_ID");
                if (acctIda.equals(acctId))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "31424", "该用户是一卡双号副卡或一卡付多号并已和主卡绑定付费，不能再办理集团代付!");
                    return result;
                }

            }
        }

        IDataset relaDatas = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");

        if (IDataUtil.isNotEmpty(relaDatas))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "31424", "号码[" + sn + "]是统一账户付费副卡号码，不允许办理集团统付业务。");
            return result;
        }
        
        //---add by chenzg@20180607--begin---REQ2018052100182018年海洋通约定消费送话费营销活动开发需求----
        /*选择集团海洋通产品账户进行集团高级付费关系管理（即进行集团代付）时，需判断代付成员不存在指定的营销活动才允许集团代付 */
        IDataset grpAcctPayrelas = AcctInfoQry.getGhytPrdPayrelaAcctByAcctId(grpAcctId);
        if(IDataUtil.isNotEmpty(grpAcctPayrelas)){
        	//判断集团代付成员号码是否办理了某些活动，有则不允许集团代付
        	IDataset saleactives = SaleActiveInfoQry.getUserSaleActiveForGhytAdvPay(userId);
        	if(IDataUtil.isNotEmpty(saleactives)){
        		 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20180607", "号码[" + sn + "]已办理某些活动，不允许办理海洋通产品的集团统付业务。");
                 return result;
        	}
        	
        }
        //---add by chenzg@20180607--end---REQ2018052100182018年海洋通约定消费送话费营销活动开发需求------

        return true;
    }

}
