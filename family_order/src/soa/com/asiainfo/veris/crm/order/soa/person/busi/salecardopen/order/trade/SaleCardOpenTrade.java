
package com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order.requestdata.SaleCardOpenReqData;


/**
 * 买断开户
 * 
 * @author sunxin
 */
public class SaleCardOpenTrade extends BaseTrade implements ITrade
{

    /**
     * 处理主台账的数据
     * 
     * @param reqData
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) btd.getRD();
        btd.getMainTradeData().setRsrvStr3(SaleCardOpenRD.getProductId());
        btd.getMainTradeData().setRsrvStr4(SaleCardOpenRD.getPackageId());
        //btd.setMainTradeProcessTagSet(1,SaleCardOpenRD.getSaleTypeCode());
        btd.getMainTradeData().setRemark("代理商直接买断");
        String isTag = SaleCardOpenRD.getIsTag();
        if ("1".equals(isTag))
            dealReward(btd);// 处理reward表
    }

    /*
     * 检查数据，校验操作 sunxin
     */
    public void checkData(BusiTradeData btd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) btd.getRD();
        String SaleTypeCode = SaleCardOpenRD.getSaleTypeCode();
        String userTypeCode = SaleCardOpenRD.getUca().getUser().getUserTypeCode();
        String brandCode = SaleCardOpenRD.getUca().getBrandCode();
        String accTag = SaleCardOpenRD.getUca().getUser().getAcctTag();
        String removeTag = SaleCardOpenRD.getUca().getUser().getRemoveTag();
        String userId = SaleCardOpenRD.getUca().getUserId();
        String epachyCode = SaleCardOpenRD.getUca().getUserEparchyCode();
        if (SaleTypeCode.equals("9")) // 海南按级段销售
        {
            if (((brandCode.equals("G001") || brandCode.equals("G010") || brandCode.equals("G018") || brandCode.equals("G019") || brandCode.equals("G012")) && userTypeCode.equals("B")) || brandCode.equals("G002") || brandCode.equals("G011")
                    || brandCode.equals("G009") || brandCode.equals("GS01") || brandCode.equals("GS03") || brandCode.equals("GS02"))
            {
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1171);
            }
        }

        if (SaleTypeCode.equals("b")) // 海南SIM卡特殊买断
        {
            if (!accTag.equals("2") && !userTypeCode.equals("B"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1172);
            }
        }

       /* if (SaleTypeCode.equals("1")) // 买断销售
        {
            if (brandCode.equals("G001") && userTypeCode.equals("B"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1173);
            }
        }*/

        if (SaleTypeCode.equals("8")) // 海南特殊卡的回缴,对欠费销号用户有效
        {
            if ("0".equals(removeTag))
                CSAppException.apperr(CrmUserException.CRM_USER_1174);

            IDataset User = UserInfoQry.getUserInfoByUserId(userId, "4", epachyCode);
            if (IDataUtil.isEmpty(User))
                CSAppException.apperr(CrmUserException.CRM_USER_1175);
        }
        
        IDataset userInfo = UserInfoQry.getUserInfoByUserId(userId, "0", epachyCode);
		if (IDataUtil.isNotEmpty(userInfo))
		{
			String openMode = userInfo.getData(0).getString("OPEN_MODE");
			if (!"1".equals(openMode))
				CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是预开未返单");
		}

    }

    // @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        checkData(btd);
        appendTradeMainData(btd);
        createUserTradeData(btd);// 处理用户台账
        createCustomerTradeData(btd);// 处理客户核心资料
        //吉祥号码订购:20141002
        //6067【吉祥号码承诺在网套餐 （立即生效，结束时间至2050年）】 
        //6070【吉祥号码承诺不销户，不过户 （立即生效，结束时间为不含当月的24个月后，如：2014年9月25日xx:xx:xx生效，2016年9月30日23：59：59）】
        createTradeDiscnt(btd);
    }
    /**
     * 
    * @Function: createTradeDiscnt()
    * @Description: 批量买断时候，只有吉祥号码才会订购6067和6070
    *
    * @param:
    * @return：
    * @throws：异常描述
    *
    * @version: v1.0.0
    * @author: yxd
    * @date: 2014-10-2 下午5:34:06
    *
    * Modification History:
    * Date         Author          Version            Description
    *---------------------------------------------------------*
    * 2014-10-2      yxd         v1.0.0               修改原因
     */
    private void createTradeDiscnt(BusiTradeData btd) throws Exception
    {
    	SaleCardOpenReqData saleCardOpenRD = (SaleCardOpenReqData) btd.getRD();    	
    	//查询是否为吉祥号码（已经预开只从已用表查询）RM.ResPhoneIntfSvc.getMphonecodeInfo
    	IDataset dataSet = ResCall.getMphonecodeInfo(saleCardOpenRD.getUca().getSerialNumber());
 		if (IDataUtil.isNotEmpty(dataSet))
 		{
 			IData mphonecodeInfo = dataSet.first();
 			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
 			//只有吉祥号码才会订购6067和6070
 			if (StringUtils.equals("1", beautifulTag))
 			{
/* 				String discntCode = mphonecodeInfo.getString("BIND_ELEMENT_ID");
 				String bindMonth = mphonecodeInfo.getString("DEPOSIT_MONTH");
 				int month = Integer.parseInt(bindMonth)+1;
 				IData discntConfig = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
 	            if (IDataUtil.isEmpty(discntConfig))
 				{
 					CSAppException.apperr(ElementException.CRM_ELEMENT_140,discntCode);
 				}
 	            //1.6067
 	            IData tempPage0 = new DataMap();
 	            tempPage0.put("USER_ID", saleCardOpenRD.getUca().getUserId());
 	            tempPage0.put("PRODUCT_ID", "-1");
 	            tempPage0.put("PACKAGE_ID", "-1");
 	            tempPage0.put("DISCNT_CODE", "6067");// 吉祥号码承诺在网套餐
 	            tempPage0.put("START_DATE", SysDateMgr.getSysTime());
 	            tempPage0.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
 	            dealForDiscnt(btd, saleCardOpenRD.getUca(), tempPage0);*/
 	            
 	            //2.6070
 	            /* REQ201411240003  批量买断吉祥号码规则优化  屏蔽6070绑定
 	            IData tempPage1 = new DataMap();
	            tempPage1.put("USER_ID", saleCardOpenRD.getUca().getUserId());
	            tempPage1.put("PRODUCT_ID", "-1");
	            tempPage1.put("PACKAGE_ID", "-1");
	            tempPage1.put("DISCNT_CODE", discntCode);// 6070吉祥号码承诺不销户，不过户套餐
	            tempPage1.put("START_DATE", SysDateMgr.getSysTime());
	            tempPage1.put("END_DATE", SysDateMgr.endDate(SysDateMgr.getSysTime(), "1", "", month + "", "3"));
	            dealForDiscnt(btd, saleCardOpenRD.getUca(), tempPage1);
	            */
 			}
 		}
    }

    /**
     * 
    * @Function: dealForDiscnt()
    * @Description: 处理绑定优惠
    *
    * @param:
    * @return：
    * @throws：异常描述
    *
    * @version: v1.0.0
    * @author: yxd
    * @date: 2014-10-2 下午6:49:10
    *
    * Modification History:
    * Date         Author          Version            Description
    *---------------------------------------------------------*
    * 2014-10-2      yxd         v1.0.0               修改原因
     */
    private void dealForDiscnt(BusiTradeData btd, UcaData uca, IData param) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(param.getString("USER_ID"));
        newDiscnt.setProductId(param.getString("PRODUCT_ID"));
        newDiscnt.setPackageId(param.getString("PACKAGE_ID"));
        newDiscnt.setElementId(param.getString("DISCNT_CODE"));
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("0");
        newDiscnt.setStartDate(param.getString("START_DATE"));
        newDiscnt.setEndDate(param.getString("END_DATE"));
        newDiscnt.setRemark("吉祥号码批量买断默认绑定");
        btd.add(uca.getSerialNumber(), newDiscnt);

    }
    /**
     * 客户资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) btd.getRD();
        String serialNumber = SaleCardOpenRD.getUca().getUser().getSerialNumber();
        String activeTime = SaleCardOpenRD.getActiveTime();
        CustomerTradeData customerTD = SaleCardOpenRD.getUca().getCustomer().clone();
        String agentId = SaleCardOpenRD.getAgentId();
        customerTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        customerTD.setDevelopDepartId(agentId);
        customerTD.setInDate(activeTime);
        btd.add(serialNumber, customerTD);
    }

    /**
     * 生成台帐用户子表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) btd.getRD();
        String serialNumber = SaleCardOpenRD.getUca().getUser().getSerialNumber();
        String user_id = SaleCardOpenRD.getUca().getUserId();
        UserTradeData userTD = SaleCardOpenRD.getUca().getUser().clone();
        String SaleTypeCode = SaleCardOpenRD.getSaleTypeCode();
        String userTypeCode = SaleCardOpenRD.getUca().getUser().getUserTypeCode();
        String brandCode = SaleCardOpenRD.getUca().getBrandCode();
        String agentId = SaleCardOpenRD.getAgentId();
        String accTag = SaleCardOpenRD.getUca().getUser().getAcctTag();
        String activeTime = SaleCardOpenRD.getActiveTime();

        if (SaleTypeCode.equals("b"))
        {
            accTag = "2";
            userTypeCode = "B";
        }
        if (SaleTypeCode.equals("1") || SaleTypeCode.equals("6") || SaleTypeCode.equals("9") || SaleTypeCode.equals("a"))
        {
            if (brandCode.equals("G002") || brandCode.equals("G010") || brandCode.equals("TDYD"))
            {
                accTag = "2";
                userTypeCode = "B";
            }
            else if (brandCode.equals("G001") || brandCode.equals("G012") || brandCode.equals("G005")) 
            {
               
            }
            else
            {
                accTag = "0";
            }
        }

        if (SaleTypeCode.equals("8"))
        {
            if (brandCode.equals("G001") || brandCode.equals("G012") || brandCode.substring(0, 2).equals("GS"))
            {
            }
            else if (brandCode.equals("G002") || brandCode.equals("G010") || brandCode.equals("TDYD"))
            {
                accTag = "2";
                userTypeCode = "B";
            }
            else
            {
                accTag = "0";
            }
        }

        userTD.setUserTypeCode(userTypeCode);
        userTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTD.setDevelopDepartId(agentId);
        userTD.setAcctTag(accTag);
        userTD.setOpenMode("2");
        userTD.setOpenDate(activeTime);
        userTD.setInDate(activeTime);
        userTD.setDevelopDate(activeTime);
        // userTD.setRsrvNum1("1");// 更新用户表标记,用于实名制 sunxin
        btd.add(serialNumber, userTD);

    }

    /**
     * 处理tf_f_user_reward
     * 
     * @param reqData
     * @param btd
     * @throws Exception
     */
    private void dealReward(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) btd.getRD();
        IData param = new DataMap();
        param.put("USER_ID", SaleCardOpenRD.getUca().getUserId());
        param.put("BRAND_CODE", SaleCardOpenRD.getUca().getBrandCode());
        param.put("PRODUCT_ID", SaleCardOpenRD.getUca().getProductId());
        param.put("EPARCHY_CODE", SaleCardOpenRD.getUca().getUserEparchyCode());
        param.put("NET_TYPE_CODE", SaleCardOpenRD.getUca().getUser().getNetTypeCode());
        param.put("SERIAL_NUMBER", SaleCardOpenRD.getUca().getSerialNumber());
        param.put("IN_DATE", SysDateMgr.getSysTime());
        param.put("IN_STAFF_ID", getVisit().getStaffId());
        param.put("IN_DEPART_ID", getVisit().getDepartId());
        param.put("DEVELOP_STAFF_ID", getVisit().getStaffId());
        param.put("DEVELOP_DATE", SysDateMgr.getSysTime());
        param.put("DEVELOP_DEPART_ID", SaleCardOpenRD.getAgentId());
        param.put("REMOVE_TAG", SaleCardOpenRD.getUca().getUser().getRemoveTag());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        Dao.executeUpdateByCodeCode("TF_F_USER_REWARD", "INS_ALL_NEW", param);
    }

}
