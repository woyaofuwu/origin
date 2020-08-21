package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;


/**
 *  REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305
 *  办理了“全球通银卡合约一年”活动的用户，只能办理指定的套餐。
 * @author mqx
 */
public class CheckCommpare305Limit extends BreBase implements IBREScript{
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		/*
		 * 判断用户是否有“全球通银卡合约一年”优惠
		 * 这次办理的套餐是否有配置表中的套餐
		 * 有，放行；否，拦截报错
		 */	
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData) && reqData.size()>0)
            {
            	//存在营销活动“全球通银卡合约一年”,只能办理配置表中的主产品
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String bookingDate = reqData.getString("BOOKING_DATE");// 预约时间
                
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                	String userId = databus.getString("USER_ID");
                	
    		    	IDataset Commpara305 = CommparaInfoQry.getCommparaAllCol("CSM", "305", "GSM_SILVER_CONTRACT_PRODUCT", databus.getString("EPARCHY_CODE"));
            		
    		    	for (int i = 0, size = Commpara305.size(); i < size; i++)
        			{
    		    		String productId = Commpara305.getData(i).getString("PARA_CODE1");
    		    		String packageId = Commpara305.getData(i).getString("PARA_CODE2");
    		    		String changeFlag = Commpara305.getData(i).getString("PARA_CODE5");
    		    		
    		    		IDataset userSaleActive = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId, packageId);
    		    		
    		    		if ("1".equals(changeFlag) && IDataUtil.isNotEmpty(userSaleActive) && userSaleActive.size()>0)//存在“全球通银卡合约一年”营销活动
    	        		{
    		    			String salePackageId = userSaleActive.getData(0).getString("PACKAGE_ID");
    		    			String salePackageName = userSaleActive.getData(0).getString("PACKAGE_NAME");
    		    			String endDate = userSaleActive.getData(0).getString("END_DATE");
                            
                            IDataset commpara305RelatedProduct = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "305", "RELATED_PRODUCT", newProductId, salePackageId, CSBizBean.getTradeEparchyCode());
                            
                            if(IDataUtil.isEmpty(commpara305RelatedProduct) && endDate.compareTo(SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND)) > 0)
                            {
                                String errorMsg = "该用户存在营销活动产品【"+salePackageName+"】,不能更改成主套餐【"+UProductInfoQry.getProductNameByProductId(newProductId)+"】,只能办理指定的套餐!";
                                
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20190306", errorMsg);

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
