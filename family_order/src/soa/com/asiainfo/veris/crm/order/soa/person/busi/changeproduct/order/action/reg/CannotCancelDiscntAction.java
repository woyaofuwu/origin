
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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * 不能取消的优惠param_attr=4949
 *
 */
public class CannotCancelDiscntAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(CannotCancelDiscntAction.class);
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String serialNumber = uca.getSerialNumber();
		List<String> delDiscntListDiscntList = new ArrayList<String>();
		logger.debug("CannotCancelDiscntAction>>>>>" + serialNumber +"---");
		//如果有优惠变更，将新增的优惠id放到addDiscntList list中
		List<DiscntTradeData> changeDiscnts=btd.get("TF_B_TRADE_DISCNT"); 
		if(changeDiscnts!=null&&changeDiscnts.size()>0){
			for(DiscntTradeData discntTradeData : changeDiscnts){
				if(BofConst.MODIFY_TAG_DEL.equals(discntTradeData.getModifyTag())){
					delDiscntListDiscntList.add(discntTradeData.getDiscntCode());
				}
			}
		}
		
		IData param = new DataMap();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "4949");
		param.put("PARAM_CODE", "INTERESTS");
		for(String discntCode : delDiscntListDiscntList){
			param.put("PARA_CODE1", discntCode);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			if(IDataUtil.isNotEmpty(dataset)){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, "该套餐"+discntCode+"不可取消");
					}
		}
		
		
    }
}
