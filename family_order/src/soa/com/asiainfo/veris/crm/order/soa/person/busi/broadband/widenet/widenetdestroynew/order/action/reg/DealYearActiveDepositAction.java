
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;

/**
 * 根据宽带回单时间，处理包年优惠的剩余费用
 * 
 */
public class DealYearActiveDepositAction implements ITradeAction
{
	public void executeAction(BusiTradeData btd) throws Exception 
    {
        String tradeId = btd.getTradeId();
        String serialNumber = btd.getMainTradeData().getSerialNumber();
    	String acceptDate = SysDateMgr.getSysTime();
        
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");

    	IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(seriUserId);
    	if (IDataUtil.isEmpty(saleActives)){
    		return ;
    	}
    	
    	IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
    	if (IDataUtil.isEmpty(kdActives)){
    		return ;
    	}
    	IData effActive = new DataMap();
		if ((saleActives != null && saleActives.size() > 0)&&(kdActives != null && kdActives.size() > 0))
        {
			for (int i = 0; i < saleActives.size(); i++)
            {
                IData element = saleActives.getData(i);
                for(int j=0;j<kdActives.size();j++){
                	if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
                		effActive = element;
                		String endDate = saleActives.getData(i).getString("END_DATE");
                		if((0==SysDateMgr.monthIntervalYYYYMM(chgFormat(acceptDate,"yyyy-MM-dd","yyyyMM"),chgFormat(endDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")))){
                			return ;
                		}
                	}
                }
            }
        }
		if (IDataUtil.isEmpty(effActive)){
    		return ;
    	}
		
		String productId = effActive.getString("PRODUCT_ID");
		String packageId = effActive.getString("PACKAGE_ID");
		//包年费用
    	String activeYearFee = "";
    	WidenetMoveBean wb = new WidenetMoveBean();
    	IData input = new DataMap();
    	input.put("PRODUCT_ID", productId);
    	input.put("PACKAGE_ID", packageId);
    	IData feeInfo = wb.queryCheckSaleActiveFee(input);
    	if(IDataUtil.isNotEmpty(feeInfo)){
    		activeYearFee = feeInfo.getString("SALE_ACTIVE_FEE", "0");
    	}else{
    		return ;
    	}
    	
    	//根据包年优惠，计算当前包年活动优惠使用时间
    	int endMonths = 0;
    	IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(seriUserId);
    	if (IDataUtil.isNotEmpty(useDiscnts)){
    		for(int i=0;i<useDiscnts.size();i++){
    			if(productId.equals(useDiscnts.getData(i).getString("PRODUCT_ID", ""))&&packageId.equals(useDiscnts.getData(i).getString("PACKAGE_ID", ""))){
    				String startDate = useDiscnts.getData(i).getString("START_DATE", "");
    				endMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(acceptDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")) + 1;
    			}
    		}
    	}
        
        //查看包年活动周期
    	int activeMonth = 12;
        IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
        if(IDataUtil.isNotEmpty(com181)) activeMonth = Integer.parseInt(com181.getData(0).getString("PARA_CODE4", "12"));
    	if(endMonths == activeMonth) return ;


    	//计算包年活动需要转账赠送或沉淀的预存
    	int yearFee = Integer.parseInt(activeYearFee);
    	int backFee = yearFee - yearFee/activeMonth*endMonths;

    	//计算专项存折的费用
    	int balance9021 = 0;
    	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
    	for(int i=0;i<allUserMoney.size();i++){
    		if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
    			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
                int balance2 = Integer.parseInt(balance1);
                balance9021 = balance9021 + balance2;
    		}
    	}
    	
    	if(backFee!=balance9021){
    		String flag = "2";
    		if(balance9021==0)flag= "0";
    		if(backFee>balance9021) flag="1";

    		IData param = new DataMap();
	    	param.put("TRADE_ID",tradeId);
	    	param.put("REMARK",flag);
    		StringBuilder buf = new StringBuilder();
            buf.append(" UPDATE TF_B_TRADE T ");
            buf.append(" SET T.REMARK=:REMARK ");
            buf.append(" WHERE T.CANCEL_TAG='0' ");
            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
            Dao.executeUpdate(buf, param, Route.getJourDb(CSBizBean.getUserEparchyCode()));
    		if(balance9021<=0) return;
    	}
		
		//资金进行沉淀
		IData depositeParam=new DataMap();
		IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(seriUserId);
		depositeParam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
		depositeParam.put("TRADE_FEE", balance9021);
		depositeParam.put("ANNUAL_TAG", "1");
		depositeParam.put("DEPOSIT_CODE", "9021");
		IData inAcct = AcctCall.AMBackFee(depositeParam);
		if(IDataUtil.isNotEmpty(inAcct)&&("0".equals(inAcct.getString("RESULT_CODE",""))||"0".equals(inAcct.getString("X_RESULTCODE","")))){
			// 成功！ 处理other表
		}else{
			CSAppException.appError("61312", "调用接口 AM_CRM_BackFee 接口错误:"+inAcct.getString("X_RESULTINFO"));
		}
		
    }
    
	public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

		String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
		return newStr;
	}

}
