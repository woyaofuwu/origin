package com.asiainfo.veris.crm.order.soa.person.rule.run.gprsdiscntchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;


public class CheckGPRSDiscntChange extends BreBase implements IBREScript {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(CheckGPRSDiscntChange.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGPRSDiscntChange.java() >>>>>>>>>>>>>>>>>>");
		}
		
		String userId = databus.getString("USER_ID");
		UcaData uca	= UcaDataFactory.getUcaByUserId(userId);
//		String serialNumber = uca.getSerialNumber();
		
//		//REQ201809270017 GPRS优惠变更界面优化
		//查询是否已有共享用户，如有，则不允许变更   

        IDataset shareDiscntds = ShareInfoQry.queryDiscnts(userId);
//        System.out.println("GPRSDiscntChangeTradexxxxxxxxxxxxxx52 "+shareDiscntds);
		IData cond = new DataMap();		
		cond.put("USER_ID",uca.getUserId());
		cond.put("SERIAL_NUMBER", uca.getSerialNumber());
        IDataset shareMebds = ShareInfoQry.queryMember(userId);
//		System.out.println("GPRSDiscntChangeTradexxxxxxxxxxxxxx57 "+shareMebds);
		if ((shareDiscntds != null && shareDiscntds.size() > 0 )&& (shareMebds != null && shareMebds.size()> 0)){       
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < shareMebds.size(); k++) {
				if (k == shareMebds.size() - 1) {
					sb.append(shareMebds.getData(k).getString("SERIAL_NUMBER", ""));
				} else {
					sb.append(shareMebds.getData(k).getString("SERIAL_NUMBER", "") + ",");
				}
			}
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180103, "主卡用户已绑定共享用户【"+sb.toString()+"】，请解除多终端共享或者到产品变更界面办理！");
			return true;
		}
		
		/*System.out.println("CheckGPRSDiscntChangexxxxxxxxxxxxxxxx38 "+uca);
		System.out.println("CheckGPRSDiscntChangexxxxxxxxxxxxxxxx39 "+userId+"\t"+serialNumber);
		// 获取用户原有GPRS优惠
		List<DiscntTradeData> list = uca.getUserDiscnts();
		System.out.println("CheckGPRSDiscntChangexxxxxxxxxxxxxxxx42 "+list);
		IDataset gprsDiscnts = UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("", "5");
		System.out.println("CheckGPRSDiscntChangexxxxxxxxxxxxxxxx44 "+gprsDiscnts);

		for (int i = 0, size = list.size(); i < size; i++) {
			DiscntTradeData dtd = list.get(i);
			// IDataset gprsDiscnts = DiscntInfoQry.queryDiscntsByDtype("5");			
			for (int j = 0, gsize = gprsDiscnts.size(); j < gsize; j++) {
				if (dtd.getElementId().equals(gprsDiscnts.get(j, "DISCNT_CODE"))) {
					
					// REQ201809270017 GPRS优惠变更界面优化
					// 查询是否已有共享用户，如有，则不运行变更

					IData cond = new DataMap();
					cond.put("USER_ID", userId);
					cond.put("SERIAL_NUMBER", serialNumber);
					ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
					IDataset ds = bean.queryShareMebList(cond);
					System.out.println("CheckGPRSDiscntChangexxxxxxxxxxxxxxxx59 "+ds);
										
					if (ds != null && ds.size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (int k = 0; k < ds.size(); k++) {
							sb.append(ds.getData(k).getString("SERIAL_NUMBER","")+",");
						}
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180103, "主卡用户已绑定共享用户【"+sb.toString()+"】，请解除多终端共享或者到产品变更界面办理！");
						return true;
					}

				}
			}
		}*/

		return false;
	}

}
