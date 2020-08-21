package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class ModifySchoolGpsTimeRegAction implements ITradeAction {

	
	@SuppressWarnings("unchecked")
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		List<AttrTradeData> attrTradeDatas=btd.get("TF_B_TRADE_ATTR");
		
		if(attrTradeDatas != null && attrTradeDatas.size() > 0)
		{
			
			AttrTradeData addData = null;
			AttrTradeData delData = null;
			boolean isDelAttr = false;
			boolean isAddattr = false;
			
			for(int i=0, size = attrTradeDatas.size(); i<size; i++)
			{
				AttrTradeData attrTradeData = attrTradeDatas.get(i);
				
				String instType = attrTradeData.getInstType();
				//String attrCode = attrTradeData.getAttrCode();
				String attrValue = attrTradeData.getAttrValue();
				String modifyTag = attrTradeData.getModifyTag();
				
				IDataset checkDatas = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1416", "SCHOOL_GPS_ATTR", attrValue, instType, "0898");
				
				if(IDataUtil.isNotEmpty(checkDatas))
				{
					if(modifyTag.equals(BofConst.MODIFY_TAG_DEL))
					{	//如果是删除，需要修改成本月底结束
						delData = attrTradeData;
						isDelAttr = true;
					}
					else if(modifyTag.equals(BofConst.MODIFY_TAG_ADD))
					{	//如果是新增，需要修改成下月初生效
						addData = attrTradeData;
						isAddattr = true;
					}
				}
				
			}
			
			/*
			 * 如果同时存在删除和添加，会按照规则进行修改
			 */
			if(isDelAttr && isAddattr)
			{
				/*
				 * 修改要办理的属性的时间
				 */
				/*delData.setModifyTag(BofConst.MODIFY_TAG_UPD);
				delData.setEndDate(SysDateMgr.getLastDateThisMonth());
				
				addData.setStartDate(SysDateMgr.getFirstDayOfNextMonth());*/
				
				/*
				 * 如果存在未生效的属性，要进行删除处理
				 */
				UcaData uca = btd.getRD().getUca();
				
				String attrCode = addData.getAttrCode();
				String instType = addData.getInstType();
				String elementID = addData.getElementId(); 
				String relaInstId = addData.getRelaInstId();
				IDataset existAttrs = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCodeBook(uca.getUserId(), relaInstId, attrCode, uca.getUserEparchyCode());
				
				if(IDataUtil.isNotEmpty(existAttrs))
				{
					for(int i=0, size = existAttrs.size(); i<size; i++)
					{
						AttrTradeData data = new AttrTradeData(existAttrs.getData(i));
						data.setModifyTag(BofConst.MODIFY_TAG_DEL);
						data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
						
						btd.add(uca.getSerialNumber(), data);
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
