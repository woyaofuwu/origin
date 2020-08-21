package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class CheckExistsYuYueForWidenetDestroy extends BreBase implements IBREScript{

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
			
			String hflb = "N";
			
			//是否办理了60元话费礼包
			IDataset saleActivelist1 = SaleActiveInfoQry.getUserAllSaleActiveInfo(userId,"66000280");
			if(saleActivelist1 != null && saleActivelist1.size() > 0){
				for (int i = 0; i < saleActivelist1.size(); i++) {
					IData saleActiveData = saleActivelist1.getData(i);
					String productId = saleActiveData.getString("PRODUCT_ID");
					String packageId = saleActiveData.getString("PACKAGE_ID");
					IDataset commParaInfo6601 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6601", productId, packageId);
					if(IDataUtil.isNotEmpty(commParaInfo6601)){
						hflb = "Y";
					}
				}
			}
			//存有有效宽带
        	IDataset widenetInfoQry = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+sn);
			if(IDataUtil.isNotEmpty(widenetInfoQry) && hflb.equals("N")){//宽带完工，同时没办理过60元话费礼包
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "190314", "注意！宽带拆机将损失一个宽带净增KPI客户数！请挽留客户！如客户愿意继续使用宽带6个月，则可赠送60元话费礼包。");
			}
			
			IDataset saleActivelist = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
			//是否办理了60元报停专项款,60元话费礼包
			if(saleActivelist != null && saleActivelist.size() > 0){
				for (int i = 0; i < saleActivelist.size(); i++) {
					IData saleActiveData = saleActivelist.getData(i);
					String productId = saleActiveData.getString("PRODUCT_ID");
					String packageId = saleActiveData.getString("PACKAGE_ID");
					IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6600", productId, packageId);
					if(IDataUtil.isNotEmpty(commParaInfo6600)){
						String date = saleActiveData.getString("END_DATE");
						String dateNow = SysDateMgr.getLastDateThisMonth();//获取本月最后一天(yyyy-MM-dd HH:mm:ss)
						
		                if (date.compareTo(dateNow) > 0)
		                {
		                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "190315", "客户存在生效的60元报停专项款，不能预约拆机！");
		                }
					}
					IDataset commParaInfo6601 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6601", productId, packageId);
					if(IDataUtil.isNotEmpty(commParaInfo6601)){
						String date = saleActiveData.getString("END_DATE");
						String dateNow = SysDateMgr.getLastDateThisMonth();//获取本月最后一天(yyyy-MM-dd HH:mm:ss)
		                if (date.compareTo(dateNow) > 0)
		                {
		                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "190316", "客户存在生效的60元话费礼包，不能预约拆机！");
		                }
					}
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
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "84070042", "用户存在有效的爱家音箱营销活动，不允许拆机！");
					}
				}
			}
		}else{
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "190312", "客户信息不存在！");
		}
        return false;
    }
}