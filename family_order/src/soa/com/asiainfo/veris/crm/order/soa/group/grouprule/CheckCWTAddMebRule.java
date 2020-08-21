package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckCWTAddMebRule extends BreBase implements IBREScript {
	
	private static final long serialVersionUID = 1L;

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		// 错误页面提示
		String errCode = ErrorMgrUtil.getErrorCode(databus);
		String err = "";
		// 判断成员是否是物联网号码
		String userIdB = databus.getString("USER_ID_B", "");
		// 得到用户信息
		IData info = UcaInfoQry.qryUserInfoByUserId(userIdB);
		if (!info.getString("BRAND_CODE").equals("PWLW")) {
			return false; 
		}
		String eparchyCode = info.getString("EPARCHY_CODE");
		// 通过方法获得全网服务编码
		IDataset idataset1 = CommparaInfoQry.getCommparaAllCol("CSM", "9088", "WLWDATASERVICE", eparchyCode);// param_attr,param_code
		if(DataUtils.isEmpty(idataset1)){
			err = "您添加的是物联网号码成员，请配置对应的数据通信服务到TD_S_COMMPARA表中，PARAM_ATTR=9088,PARAM_CODE=WLWDATASERVICE。";
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, errCode, err);
		}
		// 获得PARA_CODE1,并且转由全网转成省网
		boolean isHaveSvc = false;
		IDataset usersvc = null;
		for (int i = 0; i < idataset1.size(); i++) {
			String pbossProductID = idataset1.getData(i).getString("PARA_CODE1");
			IDataset idataset3 = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9014", pbossProductID, eparchyCode, null);
			if(DataUtils.isEmpty(idataset3)){
				continue;
			}
			//循环省网编码
			for (int j = 0; j < idataset3.size(); j++) {
				// 获得service_id
				usersvc = UserSvcInfoQry.getSvcUserId(userIdB, idataset3.getData(j).getString("PARA_CODE"));
				if (usersvc != null) {
					isHaveSvc = true;
					// 获得APNNAME
					IDataset dataset = UserAttrInfoQry.getUserAttrSingleByPK(userIdB, "APNNAME",
							usersvc.getData(0).getString("INST_ID"), "S");
					if(DataUtils.isEmpty(dataset)){
						continue;
					}
					String apnname = dataset.getData(0).getString("ATTR_VALUE");
					// 查询Para_code2 是否等于 1
					IDataset idataset2 = CommparaInfoQry.getCommparaAllCol("", "9031", apnname, "");
					if (DataUtils.isNotEmpty(idataset2)) { 
						IData idata = idataset2.getData(0);
						if ("1".equals(idata.getString("PARA_CODE2"))) {
							return false;
						}
					}
				}
			}
		}
		if (!isHaveSvc) {
			err = "您添加的物联网号码成员，没有开通数据通信服务。";
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, errCode, err);
		} else {
			err = "您添加的物联网号码成员，没有开通公网APN。";
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, errCode, err);
		}
		return false;
	}

}
