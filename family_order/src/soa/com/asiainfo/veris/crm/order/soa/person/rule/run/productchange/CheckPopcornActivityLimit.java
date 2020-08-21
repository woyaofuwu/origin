package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 *  REQ201904090061新增全球通爆米花套餐及其合约活动  by mengqx 20190423
 *  “爆米花套餐合约送话费活动”生效期间，客户不能变更主套餐。如需变更为其它基础套餐，需要先终止“爆米花套餐合约送话费活动”。
 * @author mqx
 */
public class CheckPopcornActivityLimit extends BreBase implements IBREScript{
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData) && reqData.size()>0)
            {
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String bookingDate = reqData.getString("BOOKING_DATE");// 预约时间
                
                //判断是否是主产品变更
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                	String userId = databus.getString("USER_ID");
                	
    		    	IDataset Commpara423 = CommparaInfoQry.getCommparaAllCol("CSM", "423", "POPCORN_ACTIVITY", databus.getString("EPARCHY_CODE"));
            		
    		    	for (int i = 0, size = Commpara423.size(); i < size; i++)
        			{
    		    		String productId = Commpara423.getData(i).getString("PARA_CODE1");
    		    		String packageId = Commpara423.getData(i).getString("PARA_CODE2");
    		    		
    		    		IDataset userSaleActive = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId, packageId);
    		    		
    		    		if (IDataUtil.isNotEmpty(userSaleActive) && userSaleActive.size()>0)//存在“爆米花套餐合约送话费活动”营销活动
    	        		{
    		    			String salePackageName = userSaleActive.getData(0).getString("PACKAGE_NAME");
    		    			String endDate = userSaleActive.getData(0).getString("END_DATE");

    		    			if(endDate.compareTo(SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND)) > 0)
                            {
	                            String errorMsg = "【" + salePackageName + "】生效期间，客户不能变更主套餐。如需变更为其它基础套餐，需要先终止" + "【" + salePackageName + "】。";
	                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20190423", errorMsg);
	
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
