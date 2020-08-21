package com.asiainfo.veris.crm.order.soa.person.rule.run.imslandline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckExistsIMSActive extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String userId = "";
        String errorInfo = "";
        
        String ksn = databus.getString("SERIAL_NUMBER");
        if(StringUtils.isBlank(ksn))
        {
        	return false ;
        }else{
        	ksn = ksn.substring(3);
        }
        
        IData user = UcaInfoQry.qryUserInfoBySn(ksn);
        
        if(IDataUtil.isEmpty(user))
        {
        	return false ;
        }else
        {
        	userId = user.getString("USER_ID");
        }
        
        //查询是否有家庭IMS固话预存活动套餐
        IDataset disnset = UserDiscntInfoQry.getUserIMSDiscnt(userId, "5345", "0898");
        if (IDataUtil.isNotEmpty(disnset))
        {
        	for(int i=0;i<disnset.size();i++)
        	{
        		String discode = disnset.getData(i).getString("DISCNT_CODE");
        		String endDate  = disnset.getData(i).getString("END_DATE");
        		String sysdate = SysDateMgr.getSysDateYYYYMMDD();
        		String strEndDate = endDate.replace("-", "") ;
        		
        		//最后一个月不限制
        		if(strEndDate.substring(0,6).equals(sysdate.substring(0, 6)))
        		{
        			return false ;
        		}
				errorInfo = "该用户有家庭IMS固话活动["+discode+"]，不允许拆机!";
	            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604031", errorInfo);
        	}
            
        }
        
		//是否存在有效爱家音箱营销活动
		IDataset saleActivelistIMS = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
		if(saleActivelistIMS != null && saleActivelistIMS.size() > 0){
			for (int i = 0; i < saleActivelistIMS.size(); i++) {
				IData saleActiveData = saleActivelistIMS.getData(i);
				String productId = saleActiveData.getString("PRODUCT_ID");
				String packageId = saleActiveData.getString("PACKAGE_ID");
				IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCode4to6("CSM", "178", "6800", productId, packageId, null ,"0898");
				if(IDataUtil.isNotEmpty(commParaInfo6600)){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, packageId, "用户存在有效的爱家音箱营销活动，不允许家庭IMS固话拆机！");
				}
			}
		}
		
		//REQ201909180009关于开发2019尊享信用购机活动的需求（智能音箱活动）-增加IMG固话开户判断
		IDataset saleActivelist = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
		if(IDataUtil.isNotEmpty(saleActivelist)){
			for (int i = 0; i < saleActivelist.size(); i++) {
				IData saleActiveData = saleActivelistIMS.getData(i);
				String productId = saleActiveData.getString("PRODUCT_ID");
				String productName = saleActiveData.getString("PRODUCT_NAME");
				String packageId = saleActiveData.getString("PACKAGE_ID");
				IDataset commParaInfo6805 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","6805",productId,packageId);
				if(IDataUtil.isNotEmpty(commParaInfo6805)){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20190717", "用户存在有效的"+productName+"活动，不允许家庭IMS固话拆机！");
				}
			}
		}
		
		
        return false;
    	
    }
}
