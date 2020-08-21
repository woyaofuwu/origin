
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * 
 * 
 * 首免权益包只能办理一次param_attr=3939
 *
 */
public class FisrtFreeOnlyOneDiscntAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(FisrtFreeOnlyOneDiscntAction.class);
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String userId = uca.getUserId();
    	String serialNumber = uca.getSerialNumber();
		List<String> addDiscntList = new ArrayList<String>();
		logger.debug("FisrtFreeOnlyOneDiscntAction>>>>>" + serialNumber +"---");
		//如果有优惠变更，将新增的优惠id放到addDiscntList list中
		List<DiscntTradeData> changeDiscnts=btd.get("TF_B_TRADE_DISCNT"); 
		if(changeDiscnts!=null&&changeDiscnts.size()>0){
			for(DiscntTradeData discntTradeData : changeDiscnts){
				if(BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag())){
					addDiscntList.add(discntTradeData.getDiscntCode());
				}
			}
		}
		
		IData param = new DataMap();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "3939");
		param.put("PARAM_CODE", "INTERESTS");
		for(String discntCode : addDiscntList){
			param.put("PARA_CODE1", discntCode);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			if(IDataUtil.isNotEmpty(dataset)){
				param.remove("PARA_CODE1");
				IDataset dataset2 = CommparaInfoQry.getCommparaInfoByPara(param);
				for(Object temp :dataset2){
					IData commpara = (IData)temp;
					String existDiscntCode = commpara.getString("PARA_CODE1");
					IDataset existDiscnt = UserDiscntInfoQry.getAllDiscntByUser_2(userId, existDiscntCode);
					if(IDataUtil.isNotEmpty(existDiscnt)){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, "已经办理过首月免费权益包，不能再次办理");
					}
				}
			}
		}
		
		
    }
}
