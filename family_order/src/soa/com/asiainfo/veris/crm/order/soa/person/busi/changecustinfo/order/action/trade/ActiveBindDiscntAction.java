
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;
import com.asiainfo.veris.crm.order.soa.person.common.util.TradeUtils;

/**
 * 订购这个套餐时直接根据年龄判断绑定这个减免套餐，有效期至25周岁当年年底（12月31号）。若用户变更套餐，需同步截止该减免套餐到本月底。
 * 配置在TD_S_COMMPARA表中param_attr=6969
 * 
 * @author tanzheng
 *
 */
public class ActiveBindDiscntAction implements ITradeAction {
	private static final Logger log = Logger.getLogger(ActiveBindDiscntAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String tradeTypeCode = btd.getTradeTypeCode();
		if ("60".equals(tradeTypeCode)) {
			
			
			 UcaData ucaData = btd.getRD().getUca();
			 String userId = ucaData.getUserId();
             CustomerTradeData customer = ucaData.getUserOriginalData().getCustomer();
             String serialNumber = ucaData.getSerialNumber();
             String isRealName = customer.getIsRealName();
             String oldPsptId = customer.getPsptId();
             String psptId = TradeUtils.getChangeNewPsptId(btd);
             
             //如果是做激活没有实名，并且老的身份证号和新的身份证号不相同
             if(!StringUtils.equals("1", isRealName)  && !StringUtils.equals(oldPsptId, psptId)){
            		String tradePsptid = TradeUtils.getChangeNewPsptId(btd);
        			String tradePsptType = TradeUtils.getChangeNewPsptType(btd);
        			String startDate = "";
        			if ("0".equals(tradePsptType) || "1".equals(tradePsptType) || "2".equals(tradePsptType)) {
        				List<DiscntTradeData> lsDiscnt = btd.getRD().getUca().getUserDiscnts();
        				IData param = new DataMap();
        				param.put("SUBSYS_CODE", "CSM");
        				param.put("PARAM_ATTR", "6969");
        				param.put("PARAM_CODE", "BIND_DISCNT");
        				for(int i= 0;i < lsDiscnt.size();i++){
        					DiscntTradeData discntTradeData = lsDiscnt.get(i);
        					param.put("PARA_CODE1", discntTradeData.getDiscntCode());
        					IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
        					if (IDataUtil.isNotEmpty(dataset)) {
        						int age = IdcardUtils.getAgeByIdCard(tradePsptid);
        						startDate = discntTradeData.getStartDate();
        						String bindDiscntCode = dataset.first().getString("PARA_CODE2");
        						String intervalYear = dataset.first().getString("PARA_CODE3");
        						int limitYear = Integer.parseInt(intervalYear);
        						// 如果是新增并且年龄小于等于25就绑定P2优惠
        						if (age <= limitYear) {
        							String endData = SysDateMgr.get25yearEndDate(tradePsptid, intervalYear);

        							String instId = SeqMgr.getInstId();
        							DiscntTradeData discntData = new DiscntTradeData();
        							discntData.setUserId(userId);
        							discntData.setUserIdA("-1");
        							discntData.setProductId("-1");
        							discntData.setPackageId("-1");
        							discntData.setElementId(bindDiscntCode);
        							discntData.setSpecTag("0");
        							discntData.setRelationTypeCode("");
        							discntData.setInstId(instId);
        							discntData.setCampnId("");
        							discntData.setStartDate(startDate);
        							discntData.setEndDate(endData);
        							discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        							discntData.setRemark("25岁以下用户激活绑定指定套餐，配置在6969");
        							btd.add(serialNumber, discntData);
        						}
        					}
        				}

        			}
            	 
            	 
             }                
             
			
			
		
		}

	}
}
