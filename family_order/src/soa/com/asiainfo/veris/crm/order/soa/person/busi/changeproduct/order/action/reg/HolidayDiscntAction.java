
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

/**
 * Copyright: Copyright (c) 2016
 * 
 * @ClassName: UnLimitServiceChgAction
 * @Title: REQ201604130016 关于关闭流量欺诈客户上网功能的需求
 * @Description: 通过该界面或批量操作关闭上网功能后，无法通过其它渠道方式恢复，只能通过该界面或操作才能恢复客户的上网功能。
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2016-05-11
 * 
 */
public class HolidayDiscntAction implements ITradeAction
{

	protected static final Logger log = Logger.getLogger(HolidayDiscntAction.class);
	
    @SuppressWarnings({ "unchecked", "static-access" })
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (CollectionUtils.isEmpty(tradeDiscnts) || tradeDiscnts == null || tradeDiscnts.size() <= 0){
            return;
        }
        IData dtObject = btd.getRD().getPageRequestData();
        if(IDataUtil.isEmpty(dtObject))
        {
        	return;
        }
        String strSn = dtObject.getString("SERIAL_NUMBER");
    	String strObect = dtObject.getString("OBJECT", "");
    	if(StringUtils.isBlank(strObect)) 
    	{
    		return;
    	}
    	
    	if(StringUtils.isNotBlank(strObect)) 
    	{
    		IData dtObjectDiscnt = new DataMap(strObect);
    		String strDc = dtObjectDiscnt.getString("DISCNT_CODE");
    		String strOsn = dtObjectDiscnt.getString("OBJECT_SERIAL_NUMBER");
    		String strOdc = dtObjectDiscnt.getString("OBJECT_DISCNT_CODE");
    		
    		UcaData uca = UcaDataFactory.getNormalUca(strOsn);
            if (uca != null)
            {
            	String strUserID = uca.getUserId(); //dtUser.getString("USER_ID");
            	DiscntTradeData tdDiscnt = tradeDiscnts.get(0);
            	String strSD = tdDiscnt.getStartDate();
            	String strED = tdDiscnt.getEndDate();
            	for (int i = 0; i < tradeDiscnts.size(); i++) {
            		
            		DiscntTradeData dtD = tradeDiscnts.get(i);
					String strElemtId = dtD.getDiscntCode();
					String strModifyTag = dtD.getModifyTag();
					
					if(strElemtId.equals(strDc) && "0".equals(strModifyTag)){
						tdDiscnt = dtD;
						strSD = dtD.getStartDate();
		            	strED = dtD.getEndDate();
		            	break;
					}
				}
            	
            	String strODn = UDiscntInfoQry.getDiscntNameByDiscntCode(strOdc);
                	
            	DiscntTradeData ObjectDiscnt = tdDiscnt.clone();
            	ObjectDiscnt.setUserId(strUserID);
            	ObjectDiscnt.setElementId(strOdc);
            	ObjectDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
            	ObjectDiscnt.setInstId(SeqMgr.getInstId());
            	ObjectDiscnt.setRemark("假日流量套餐办理赠送");
            	ObjectDiscnt.setStartDate(strSD);
            	ObjectDiscnt.setEndDate(strED);
        		btd.add(strSn, ObjectDiscnt);
        		
                //XXXXXXXXXXX客户已为您支付劳动节假日流量套餐（功能费10元，一次性收取，含1G国内移动数据流量。
        		//套餐内的国内数据流量有效期为2016-05-01 00:00:00至2016-05-03 23:59:59，其中国内流量不含港澳台流量），
        		//您可发送“CXJRTC”至10086查询本套餐使用情况。中国移动
        		String strTemplate = "尊敬的客户：%s客户已为您支付%s（功能费10元，一次性收取，含1G国内移动数据流量。"
        						   + "套餐内的国内数据流量有效期为%s至%s，其中国内流量不含港澳台流量），"
        						   + "您可发送“CXJRTC”至10086查询本套餐使用情况。中国移动";
        		String strContent = strTemplate.format(strTemplate, strSn, strODn, strSD, strED);
                IData ObjectsmsData = new DataMap(); // 短信数据
                ObjectsmsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                ObjectsmsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                ObjectsmsData.put("FORCE_OBJECT", "10086");// 发送对象
                ObjectsmsData.put("RECV_OBJECT", strOsn);// 接收对象
                ObjectsmsData.put("NOTICE_CONTENT", strContent);// 短信内容
                PerSmsAction.insTradeSMS(btd, ObjectsmsData);
                
            }
            
    	}
    	
        
    }

}
