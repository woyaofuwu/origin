
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
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
 * 开户超过30天不能领取的首月免费优惠
 *
 */
public class BeyondThirtyDiscntLimitAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(BeyondThirtyDiscntLimitAction.class);
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String openDate = uca.getUser().getOpenDate();
    	int dayInterval = SysDateMgr.dayInterval(openDate,SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));//时间差
		List<String> addDiscntList = new ArrayList<String>();
		//如果有优惠变更，将新增的优惠id放到addDiscntList list中
		List<DiscntTradeData> changeDiscnts=btd.get("TF_B_TRADE_DISCNT"); 
		if(changeDiscnts!=null&&changeDiscnts.size()>0){
			for(DiscntTradeData discntTradeData : changeDiscnts){
				if(BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag())){
					addDiscntList.add(discntTradeData.getDiscntCode());
				}
			}
		}
		int dayNum=30;
		IData param = new DataMap();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "790");
		param.put("PARAM_CODE", "BEYONDTHIRTY");
		for(String discntCode : addDiscntList){
			param.put("PARA_CODE1", discntCode);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			if(IDataUtil.isNotEmpty(dataset)){
				String dayLimit = dataset.getData(0).getString("PARA_CODE3");
				if(StringUtils.isNotBlank(dayLimit)){
					dayNum=Integer.parseInt(dayLimit);
				}
				if(dayInterval>dayNum){
					CSAppException.apperr(CrmCommException.CRM_COMM_888, "新开户用户号码激活"+dayNum+"天内，才能办理该优惠【"+discntCode+"】");

				}
				
			}
		}
		
		
    }
}
