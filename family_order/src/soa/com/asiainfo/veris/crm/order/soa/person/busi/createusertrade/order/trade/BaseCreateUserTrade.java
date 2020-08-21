
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;

public class BaseCreateUserTrade extends BaseTrade implements ITrade
{

    /**
     * 账户资料
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void BaseCreateAcctTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        String serialNumber = baseCreatePersonUserRD.getUca().getUser().getSerialNumber();
        AccountTradeData acctTD = baseCreatePersonUserRD.getUca().getAccount().clone();
        if (!acctTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
            btd.add(serialNumber, acctTD);

    }

    /**
     * 客户资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void BaseCreateCustomerTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        String serialNumber = baseCreatePersonUserRD.getUca().getUser().getSerialNumber();
        CustomerTradeData customerTD = baseCreatePersonUserRD.getUca().getCustomer().clone();
        if (!customerTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
            btd.add(serialNumber, customerTD);
    }

    /**
     * 生成台帐个人客户表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void BaseCreateCustPersonTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        String serialNumber = baseCreatePersonUserRD.getUca().getUser().getSerialNumber();
        CustPersonTradeData custpersonTD = baseCreatePersonUserRD.getUca().getCustPerson().clone();
        if (!custpersonTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
        {
        	btd.add(serialNumber, custpersonTD);
        	
        	//CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
        	
        	//新增使用人证件类型录入
        	String strUseName = custpersonTD.getRsrvStr5();
        	String strUsePsptTypeCode = custpersonTD.getRsrvStr6();
        	String strUsePsptId = custpersonTD.getRsrvStr7();
        	String strUsePsptAddr = custpersonTD.getRsrvStr8();
        	
        	String strTradeTpyeCode = btd.getTradeTypeCode();
        	
        	if( StringUtils.isNotBlank(strUseName) || StringUtils.isNotBlank(strUsePsptTypeCode)
        	  ||StringUtils.isNotBlank(strUsePsptId) || StringUtils.isNotBlank(strUsePsptAddr) ){
        		
            	String strCustId = custpersonTD.getCustId();
            	String strPartition_id = strCustId.substring(strCustId.length() - 4);
            	
            	/*SIData param = new DataMap();
    			//param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
    			param.put("CUST_ID", strCustId);
    			QLParser parser = new SQLParser(param);
    			parser.addSQL(" SELECT T.PARTITION_ID, T.CUST_ID, T.USE_NAME, T.USE_PSPT_TYPE_CODE, T.USE_PSPT_ID, T.USE_PSPT_ADDR ");
    			parser.addSQL(" FROM TF_F_CUST_PERSON_OTHER T ");
    			parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
    			parser.addSQL(" AND T.PARTITION_ID = MOD(:CUST_ID, 10000) "); */       
    			IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);//Dao.qryByParse(parser, BizRoute.getRouteId());
    			if( IDataUtil.isNotEmpty(list) ){
    				IData custPersonOtherData = list.first();
    				custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
    	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
    	        	custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
    	        	custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    				Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    			}else{
    				IData custPersonOtherData = new DataMap();
    	        	custPersonOtherData.put("PARTITION_ID", strPartition_id);
    	        	custPersonOtherData.put("CUST_ID", custpersonTD.getCustId());
    	        	custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
    	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
    	        	custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
    	        	custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
    	        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("REMARK", "个人开户-使用人证据录入");
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    	        	custPersonOtherData.put("RSRV_STR2", "");
    	        	custPersonOtherData.put("RSRV_STR3", "");
    	        	custPersonOtherData.put("RSRV_STR4", "");
    	        	custPersonOtherData.put("RSRV_STR5", "");
    				Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
    			}
        		
        	}
        	
        }
       
    }

    /**
     * 生成台帐用户子表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void BaseCreateUserTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        String serialNumber = baseCreatePersonUserRD.getUca().getUser().getSerialNumber();
        String userPasswd = baseCreatePersonUserRD.getUca().getUser().getUserPasswd();
        String user_id = baseCreatePersonUserRD.getUca().getUserId();
        UserTradeData userTD = baseCreatePersonUserRD.getUca().getUser().clone();
        // 处理密码
        if (!"".equals(userPasswd))
        {
            userPasswd = PasswdMgr.encryptPassWD(userPasswd, user_id);
            /*
             * String strTemp = ""; if (user_id.length() > 9) strTemp = user_id; else strTemp = "000000000" + user_id;
             * userPasswd = Encryptor.fnEncrypt(userPasswd, strTemp.substring(strTemp.length() - 9));
             */

        }
        // 添加预付费1和后付费0标识 add by yangxd3
        /*
         * String prepayTag = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PREPAY_TAG",
         * baseCreatePersonUserRD.getMainProduct().getProductId()); userTD.setPrepayTag(prepayTag); 暂时屏蔽 sunxin
         */

        userTD.setUserPasswd(userPasswd);
        if (!userTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
            btd.add(serialNumber, userTD);

    }

    /**
     * 创建开户具体业务台账
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        BaseCreateUserTradeData(btd);// 处理用户台账
        BaseCreateCustomerTradeData(btd);// 处理客户核心资料
        BaseCreateCustPersonTradeData(btd);// 处理客户个人资料
        BaseCreateAcctTradeData(btd);// 处理账户资料

        createProductTradeData(btd);
        /*
         * ProductTimeEnv env = new ProductTimeEnv(); String startDate = baseCreatePersonUserRD.getAcceptTime();
         * env.setBasicAbsoluteStartDate(startDate);
         */
        // 处理元素
        ProductModuleCreator.createProductModuleTradeData(baseCreatePersonUserRD.getPmds(), btd);

        // 处理分散账期 因为有预约的概念，需要修改 后续处理 sunxin
        btd.addOpenUserAcctDayData(baseCreatePersonUserRD.getUca().getUserId(), baseCreatePersonUserRD.getUca().getAcctDay());

        btd.addOpenAccountAcctDayData(baseCreatePersonUserRD.getUca().getAcctId(), baseCreatePersonUserRD.getUca().getAcctDay());

    }

    /**
     * 产品台账处理
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createProductTradeData(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        String serialNumber = baseCreatePersonUserRD.getUca().getUser().getSerialNumber();
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(baseCreatePersonUserRD.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(baseCreatePersonUserRD.getMainProduct().getProductId());
        productTD.setProductMode(baseCreatePersonUserRD.getMainProduct().getProductMode());
        productTD.setBrandCode(baseCreatePersonUserRD.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(baseCreatePersonUserRD.getUca().getUser().getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        btd.add(serialNumber, productTD);
    }

}
