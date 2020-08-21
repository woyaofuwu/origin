package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckForeignProvinceAndMixProduct extends BreBase implements IBREScript{
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		String sn=databus.getString("SERIAL_NUMBER");
		if(StringUtils.isNotEmpty(sn)){
			if(sn.contains("KD_")){
				sn=sn.replaceAll("KD_", "");
			}
			IData userInfo=UcaInfoQry.qryUserInfoBySn(sn);
			if (userInfo == null || userInfo.size() == 0) {
	            return false;
	        }
			IDataset custInfo = CustomerInfoQry.getNormalCustInfoBySN(sn);
			if (custInfo!=null && custInfo.size()>0)
			{
				String psptTypeCode = custInfo.getData(0).getString("PSPT_TYPE_CODE","");
				if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)){//证件类型是 0或1
					String psptId = custInfo.getData(0).getString("PSPT_ID","");
					if(!psptId.startsWith("46")){//证件号是非46开头
						//查找配置：主套餐是融合套餐（具体
						IDataset mixProducts = CommparaInfoQry.getCommPkInfo("CSM", "1312", "MixProducts", "0898");
						if(mixProducts!=null && mixProducts.size()>0)
						{
							for (int i = 0; i < mixProducts.size(); i++) 
							{
								IData mixProduct = mixProducts.getData(i);
								//查询用户主套餐是否存在融合套餐
								String userId = userInfo.getString("USER_ID");
								String productId = mixProduct.getString("PARA_CODE1", "");
								IDataset mainProducts = UserProductInfoQry.queryUserValidMainProduct(userId, productId);

								if(mainProducts!=null && mainProducts.size()>0)
								{
									BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "190312", "客户为外省身份证且办理了融合套餐的宽带客户，请咨询客户是否也要手机销号？请引导客户报停宽带和报停手机。<br/>" +
											"服务口径：为了方便您后续回岛后更加快捷的开通手机和宽带，您无需拆机，端口和宽带账号保留一年，一年可以随时报开手机和宽带。" +
											"你可直接拨打10086或到营业厅，报开手机号码和宽带。如不报开一年以后自动进行手机号码和宽带销户。");
								}
							}
						}
					}
				}
			}else{
//				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "190312", "客户信息不存在！");
//				return true;
			}
		}
		
		return false;
	}
}
