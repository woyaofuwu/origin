
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class CheckMebGlyCap extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebGlyCap.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebGlyCap() >>>>>>>>>>>>>>>>>>");
        /* 自定义区域 */
        boolean bResult = false;
        String userIdA = databus.getString("USER_ID", "");			//集团产品标识
        String memRoleB = databus.getString("MEM_ROLE_B");
        String serialNumber = databus.getString("SERIAL_NUMBER");
        
        //新增/变更管理员时才判断
        if("2".equals(memRoleB)){
        //产品成员
        IDataset productMebs = RelaUUInfoQry.check_byuserida_idbzm_A(userIdA,"S1",null);
        //最大管理员
        IDataset glyNums = UserAttrInfoQry.getBwOpenTag(userIdA, "BGAdminUserNum", "0898");
        IDataset newMebs = new DatasetList();
        for (int i = 0; i < productMebs.size(); i++) {
			IData productMeb = productMebs.getData(i);
			if ("2".equals(productMeb.getString("ROLE_CODE_B"))) {
				newMebs.add(productMeb);		//管理员
			}
		}
        int mebNum = newMebs.size();		//当前已有管理员数;
        
        //加上未完工管理员  
        IDataset glyTradeList = new DatasetList();			//未完工的管理员数
        IDataset tradeList = TradeInfoQry.queryUserIdB(userIdA);		//查找未完工台账表
        for (int i = 0; i < tradeList.size(); i++) {
        	IData trade = tradeList.getData(i);
        	String tradeId = trade.getString("TRADE_ID");
        	IDataset tradeOtherlist = TradeOtherInfoQry.queryUserOtherByTradeId(tradeId,"CNTRX");
        	for (int j = 0; j < tradeOtherlist.size(); j++) {
        		IData tradeOther = tradeOtherlist.getData(j);
        		String operCode = tradeOther.getString("OPER_CODE");
        		if ("28".equals(operCode)) {
        			glyTradeList.add(trade);
				}
			}
		}
        mebNum += glyTradeList.size();
        
        String str = glyNums.getData(0).getString("ATTR_VALUE");
        int glyNum = Integer.parseInt(str);
        if (glyNum > mebNum) {
        	bResult = true;
		}else if(glyNum == mebNum){
			for (int i = 0; i < newMebs.size(); i++) {
				IData Meb = newMebs.getData(i);
				if (serialNumber.equals(Meb.getString("SERIAL_NUMBER_B"))) {
					bResult = true;
				}
			}
		}
        
        }else{
        	bResult = true;
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckMebGlyCap() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
