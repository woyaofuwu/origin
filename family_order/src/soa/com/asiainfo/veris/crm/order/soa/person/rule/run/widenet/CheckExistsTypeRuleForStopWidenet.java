package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class CheckExistsTypeRuleForStopWidenet extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String sn=databus.getString("SERIAL_NUMBER");
		if(StringUtils.isNotEmpty(sn)){
			if(sn.contains("KD_")){
				sn=sn.replaceAll("KD_", "");
			}
			IData userInfo=UcaInfoQry.qryUserInfoBySn(sn);
			
			String userId = userInfo.getString("USER_ID");
	    	CustomerTradeData customerTradeData = UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER")).getCustomer();
	    	if(customerTradeData != null){
	        	String psptTypeCode = customerTradeData.getPsptTypeCode();
	        	String psptId = customerTradeData.getPsptId();
	        	String psptIdsub = psptId.substring(0, 2);
	        	String hflb = "N";
	        	//是否办理了60元报停专项款
				IDataset saleActivelist = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
				if(saleActivelist != null && saleActivelist.size() > 0){
					for (int i = 0; i < saleActivelist.size(); i++) {
						IData saleActiveData = saleActivelist.getData(i);
						String productId = saleActiveData.getString("PRODUCT_ID");
						String packageId = saleActiveData.getString("PACKAGE_ID");
						IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6600", productId, packageId);
						if(IDataUtil.isNotEmpty(commParaInfo6600)){
							String date = saleActiveData.getString("END_DATE");
							String dateNow = SysDateMgr.getSysTime();
			                if (date.compareTo(dateNow) > 0)
			                {
			                	hflb = "Y";;
			                }
						}
					}
				}
	        	
	        	if((psptTypeCode.equals("0") || psptTypeCode.equals("1")) && !psptIdsub.equals("46") && hflb.equals("N")){//外省身份证
	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "20190313", "本客户为外省身份证的宽带客户，如客户后续要进行手机号码报停，可以赠送60元手机报停专项款！！请先在“营销活动受理”界面办理后再进行手机报停。");
	        	}
	    	}
		}
    	return false;
    	
    }
}
