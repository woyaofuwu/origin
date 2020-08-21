package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 *  移动商城2.8 
 *   校验下互斥列表的元素是否拼成了删除的台账
 * @author huangyq
 * 20190813
 */
public class DealOpposeInfoAction implements ITradeAction {
	protected static Logger logger = Logger.getLogger(DealOpposeInfoAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		 UcaData uca = btd.getRD().getUca();
		IDataset opposeList = btd.getRD().getPageRequestData().getDataset("OPPOSE_PROD_INFO");
		if(IDataUtil.isEmpty(opposeList)){
			return;
		}
		for(int i = 0; i < opposeList.size(); i++){
			IData oppose = opposeList.getData(i);
			String elementId = oppose.getString("ELEMENT_ID");
			String elementTypeCode = oppose.getString("ELEMENT_TYPE_CODE");
			if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(convertMall2ElementType(elementTypeCode))){
				List<DiscntTradeData> userDiscntTrade = uca.getUserDiscntByDiscntId(elementId);
			    if(userDiscntTrade != null && userDiscntTrade.size() > 0){
			    	DiscntTradeData discntTradeData = userDiscntTrade.get(0).clone();
			    	discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
			    	discntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());// 失效时间设为月底失效
                    btd.add(uca.getSerialNumber(), discntTradeData);
			    }
			}
			if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(convertMall2ElementType(elementTypeCode))){
				List<SvcTradeData> userSvcTrade = uca.getUserSvcBySvcId(elementId);
			    if(userSvcTrade != null && userSvcTrade.size() > 0){
			    	SvcTradeData svcTradeData = userSvcTrade.get(0).clone();
			    	svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
			    	svcTradeData.setEndDate(SysDateMgr.getSysDate());
                    btd.add(uca.getSerialNumber(), svcTradeData);
			    }
			}
		}
	}
	public static  String convertMall2ElementType(String eleTypeMall) throws Exception
    {
        String eleType = "";
        if (eleTypeMall.equals("01"))
        	eleType = "D";// 套餐类
        else if (eleTypeMall.equals("02"))
        	eleType = "Z";// 增值业务类
        else if (eleTypeMall.equals("03"))
        	eleType = "S";// 服务功能类
        return eleType;
    }

}
