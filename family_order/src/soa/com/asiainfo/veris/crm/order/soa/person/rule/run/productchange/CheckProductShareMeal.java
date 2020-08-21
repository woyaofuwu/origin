
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;

public class CheckProductShareMeal extends BreBase implements IBREScript
{
	private final static Logger logger = Logger.getLogger(CheckProductShareMeal.class);


    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据
            
            //logger.debug("reqData==========="+reqData);
            //logger.debug("databus==========="+databus);


            if (IDataUtil.isNotEmpty(reqData))
            {
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String userProductId = databus.getString("PRODUCT_ID");// 老产品

                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
                {
                	IData commpara = new DataMap();
                    commpara.put("SUBSYS_CODE", "CSM");
                    commpara.put("PARAM_ATTR", "5544");
                    commpara.put("PARAM_CODE", "SHARE");
                    commpara.put("PARA_CODE1", newProductId);
                    IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                    
                    if (IDataUtil.isNotEmpty(commparaDs))
                    {
                    	IData mainUser = UcaInfoQry.qryUserInfoBySn(databus.getString("SERIAL_NUMBER"));
                        String userId = mainUser.getString("USER_ID");
                        IDataset returnDataMenber = ShareInfoQry.queryMember(userId);
                        
                        if(IDataUtil.isNotEmpty(returnDataMenber)){
                        	/*if (returnDataMenber.size() > Integer.parseInt(commparaDs.getData(0).getString("PARA_CODE2")))
                            {
                            	 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500020, "当前号码有"+ returnDataMenber.size() +"个多终端共享副卡成员,不满足办理条件，限制办理！");
                                 return true;
                            }*/
                            
                        	int count = 0;
                        	
                            for(int i=0;i<returnDataMenber.size();i++)
                            {
                            	IData menber = returnDataMenber.getData(i);
                            	// 查询成员信息
                                IData user = UcaInfoQry.qryUserInfoBySn(menber.getString("SERIAL_NUMBER"));
                                
                                if(IDataUtil.isEmpty(user)){
                                	continue;
                                }
                                
                                boolean isDependOpenDate = false;
                                String openDate = user.getString("OPEN_DATE");
                                String limitCode = commparaDs.getData(0).getString("PARA_CODE3");
                                String sysDate = SysDateMgr.getSysTime();
                                String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(limitCode));

                                if(SysDateMgr.compareTo(menber.getString("END_DATE"), reqData.getString("BOOKING_DATE",sysDate)) > 0){
                                	
                                	count = count + 1;
                                	
	                                if (tempDate.compareTo(sysDate) > 0)
	                                    isDependOpenDate = true;
	
	                                if (!isDependOpenDate)
	                                {
	                                    commpara.put("PARA_CODE1", userProductId);
	                                    IDataset oldcommparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
	                                    if(IDataUtil.isEmpty(oldcommparaDs))
	                                    {
	                                    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500020, "多终端共享副卡不满足新入网或携入"+Integer.parseInt(limitCode)+"天内条件，限制办理！");
		                                    return true;
	                                    }      	
	                                }
                                }
                                
                            }
                            
                            if (count > Integer.parseInt(commparaDs.getData(0).getString("PARA_CODE2")))
                            {
                            	 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500020, "当前号码有"+ returnDataMenber.size() +"个多终端共享副卡成员,不满足办理条件，限制办理！");
                                 return true;
                            }
                        }
                        
                    }
                }
                if (StringUtils.isNotBlank(newProductId) && userProductId.equals(newProductId))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500021, "用户当前产品与新产品ID相同!");

                    return true;
                }
            }
        }

        return false;
    }
}
