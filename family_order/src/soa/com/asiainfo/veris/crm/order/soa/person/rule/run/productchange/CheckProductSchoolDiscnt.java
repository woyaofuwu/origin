
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common.CheckHdhkActiveLimit;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductSchoolDiscnt
 * @Description: 校园套餐内必选套餐变更时，SYS_CRM_CHANGEG010权限判断，如果没有权限只能做预约1号产品变更。
 * @version: v1.0.0
 * @author: wukw3
 * @date: 20141104
 */
public class CheckProductSchoolDiscnt extends BreBase implements IBREScript
{
	private static final long serialVersionUID = -2792489391805861229L;

    private static Logger logger = Logger.getLogger(CheckProductSchoolDiscnt.class);

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
    	IData reqData = databus.getData("REQDATA");// 请求的数据
    	IDataset TradeDiscntset = databus.getDataset("TF_B_TRADE_DISCNT");
    	String xChoiceTag = databus.getString("X_CHOICE_TAG");
    	String userProductId = databus.getString("PRODUCT_ID","");
    	String routeEparchyCode = BizRoute.getRouteId();
    	IDataset Tradeset = databus.getDataset("TF_B_TRADE");// 预约时间
    	String bookingDate = SysDateMgr.getSysDate();
    	if(IDataUtil.isNotEmpty(Tradeset)&&Tradeset.size()>0){
    		bookingDate = Tradeset.getData(0).getString("RSRV_STR3");
    	}
    	
    	if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
    		IDataset commpara8859 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8859", "PRODUCT", userProductId, routeEparchyCode); 
    		//当8859存在的时候
    		if(IDataUtil.isNotEmpty(commpara8859) && !StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_CRM_CHANGEG010"))
    		{
    			
    			if(TradeDiscntset != null && TradeDiscntset.size() > 0)
    			{
    				
    				boolean delTag = false;
    				boolean addTag = false;
                
    				for(int i=0;i<TradeDiscntset.size();i++)
    				{
    					IData discnt = TradeDiscntset.getData(i);
    					String elementType = DiscntInfoQry.getDiscntTypeByDiscntCode(discnt.getString("DISCNT_CODE",""));
    					if("R".equals(elementType) && BofConst.MODIFY_TAG_ADD.equals(discnt.getString("MODIFY_TAG","")))
    					{
    						addTag = true;
    					}
    					if("R".equals(elementType) && BofConst.MODIFY_TAG_DEL.equals(discnt.getString("MODIFY_TAG","")))
    					{
    						delTag = true;
    					}
    				}
    				if(delTag && addTag)//有变更必选优惠
    				{
    					//分散账期
    					IDataset userAcctDays = UserAcctDayInfoQry.getUserAcctDay(databus.getString("USER_ID",""));// 据USER_ID查询用户的结账日以及首次出账日
    					String acct_day = "1";
    					if(IDataUtil.isNotEmpty(userAcctDays)&&userAcctDays.size()>0){
    						acct_day = userAcctDays.getData(0).getString("ACCT_DAY");
    					}
    					if(StringUtils.isNotBlank(bookingDate)){
    						String acct_day2 = bookingDate.substring(8, 10);
    					
    						int intAcctDat = Integer.parseInt(acct_day); 
    						int intAcctDat2 = Integer.parseInt(acct_day2); 
    					
    						if(intAcctDat!=intAcctDat2){
    							//BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2014110401, "没有【SYS_CRM_CHANGEG010】权限的员工只能办理预约1号的优惠变更！");

    							return true;
    						}
    						//对应1号受理的时候
    						if(!ProductUtils.isBookingChange(bookingDate)){
    							return true;
    						}
    					}
    				}
    			}
    		}
        }
    	
    	return false;
    }
}
