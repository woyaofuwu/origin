 
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.PluginProcess;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.pos.PosBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard.KIFunc;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.asiainfo.veris.crm.order.soa.person.common.action.trade.tax.CreateTaxAction;

public class SimCardBean extends CSBizBean
{
    public static int calNeedScore(String scorePer, String fee)
    {
        float per = Float.parseFloat(scorePer);// 单位
        float price = Float.parseFloat(fee) / 100;// 价钱

        // 计算规则：如果个位大于1，十位则加1，如333则为340
        float a1 = price / per;
        int a = (int) a1;
        int b = a % 10;
        int c = 0;
        if (b > 0)
        {
            c = (int) a / 10;
            c = (c + 1) * 10;
        }
        else
        {
            c = a;
        }
        return c;
    }

    
    
    public IData getSimCardPrice(String oldSimCardNo, String newSimCardNo, String serialNumber, String tradeTypeCode) throws Exception
    {
        IData userProdInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        return getSimCardPrice(oldSimCardNo, newSimCardNo,serialNumber, tradeTypeCode, null,userProdInfo.getString("PRODUCT_ID"));
    }
    
    /**
     * @param input
     *            OLD_SIM_CARD_NO 用户老卡 ，NEW_SIM_CARD_NO 用户新卡， SERIAL_NUMEBR 手机号码，TRADE_TYPE_CODE默认是142
     * @return FEE_TAG 0:免费 1：收费 FEE： 费用 FEE_INFO：免费详细信息或部分收费信息 FEE_TARDE 0：登记OHTER台账 1：不登记OHTER台账
     * @throws Exception
     */
    public IData getSimCardPrice(String oldSimCardNo, String newSimCardNo, String serialNumber,String tradeTypeCode,IData userData,String productId) throws Exception
    {
        IData returnInfo = new DataMap();
        IData userInfo = new DataMap();
        if(IDataUtil.isNotEmpty(userData)){
            userInfo = userData;
        }else{
            userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        }
        
        IDataset oldSimInfos = ResCall.getSimCardInfo("0", oldSimCardNo, "", "1", userInfo.getString("NET_TYPE_CODE"));
        IData oldSimInfo = new DataMap();
        if(IDataUtil.isNotEmpty(oldSimInfos) && oldSimInfos.size()>0){
        	oldSimInfo = oldSimInfos.getData(0);
        }else{
        	// 获取老sim卡信息不存在
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询旧的sim卡" + oldSimCardNo + "获取不到信息。");
        }
        
        IDataset newSimInfos = ResCall.getSimCardInfo("0", newSimCardNo, "", "0", userInfo.getString("NET_TYPE_CODE"));
        IData newSimInfo = new DataMap();
        if(IDataUtil.isNotEmpty(newSimInfos) && newSimInfos.size()>0){
        	newSimInfo = newSimInfos.getData(0);
        }else{
        	// 获取老sim卡信息不存在
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询新的sim卡" + newSimCardNo + "获取不到信息。");
        }
        
      //判断员工是否有免卡费权限
        boolean isPriv =StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"CARDFEE_PRIV");
        if(isPriv){
        	returnInfo.put("FEE_TAG", "0");
            returnInfo.put("FEE_INFO", "员工有免收卡费权限");
            returnInfo.put("FEE", "0");
            returnInfo.put("FEE_TARDE", "0");
            return returnInfo;
        }
        
        //补换卡次数最多不能超过3次
        IDataset userResInfo = MvelMiscQry.queryOneResAll(userInfo.getString("USER_ID"),"1");
        boolean bFeeTag = false;
        if(userResInfo.size() > 3)
        {
        	bFeeTag = true;
            returnInfo.put("FEE_TAG", "0");
            returnInfo.put("FEE_INFO", "免费补换卡次数超过最大免费补换次数3次,本次补换卡需要收费!");
            returnInfo.put("FEE", "0");
            returnInfo.put("FEE_TARDE", "1");
        }
        /*
         * 2,3G卡，更换至4G卡，免费
         */
        IData startData = StaticInfoQry.getStaticInfoByTypeIdDataId("QUERY_STATICTIME_4G_ENDTIME", "01");
        String startTime = startData.getString("DATA_NAME", "");
        IData endData = StaticInfoQry.getStaticInfoByTypeIdDataId("QUERY_STATICTIME_4G_ENDTIME", "02");
        String endTime = endData.getString("DATA_NAME", "");
        String curTime = SysDateMgr.getSysTime();
        Timestamp beginTimeTs = Timestamp.valueOf(startTime);
        Timestamp endTimeTs = Timestamp.valueOf(endTime);
        Timestamp curTimeTs = Timestamp.valueOf(curTime);
        if (curTimeTs.before(endTimeTs) && curTimeTs.after(beginTimeTs))
        {
            boolean simModeFlag = true;// 默认23G卡
            IDataset oldSet =  ResCall.qrySimCardTypeByTypeCode(oldSimInfo.getString("RES_TYPE_CODE"));
            if (IDataUtil.isNotEmpty(oldSet))
            {
            	String typeCode = oldSet.getData(0).getString("NET_TYPE_CODE");
            	if("01".equals(typeCode)){
            		simModeFlag = false;// 4G卡
            	}
            }
            if (simModeFlag)
            {// 如果旧卡是23G卡，就去判断新卡类型
                IDataset newSet = ResCall.qrySimCardTypeByTypeCode(newSimInfo.getString("RES_TYPE_CODE"));
                if (IDataUtil.isNotEmpty(newSet) && !bFeeTag)
                {// 如果新卡是4G卡
                	String typeCode = newSet.getData(0).getString("NET_TYPE_CODE");
                	if("01".equals(typeCode)){
                		returnInfo.put("FEE_TAG", "0");
                		returnInfo.put("FEE_INFO", "2G/3G卡用户更换4G卡免费");
                		returnInfo.put("FEE", "0");
                		returnInfo.put("FEE_TARDE", "1");
                		return returnInfo;
                	}
                }
            }
        }

        /*
         * 非专属卡转专属卡为免费 --白卡专属卡 --5 神州行专用卡白卡 --0 全球通专属白卡 --D OTA白卡 --SIM卡专属卡 --D M2.0 OTA卡 --F 全球通专属卡 --H 神州行专用卡
         */
        String oldResTypeCode = oldSimInfo.getString("RES_TYPE_CODE");
        String newResTypeCode = newSimInfo.getString("RES_TYPE_CODE");
        String newResKindCode = newSimInfo.getString("RES_KIND_CODE");
        boolean newCardFlag = false;
        boolean oldCardFlag = false;
        if (StringUtils.isNotEmpty(oldSimInfo.getString("EMPTY_CARD_ID")))
        {
            oldCardFlag = true;// 老卡是否为白卡写成
            
            IDataset oldEmptyCardInfos = ResCall.getEmptycardInfo(oldSimInfo.getString("EMPTY_CARD_ID"), serialNumber, "USE");
            IData oldEmptyCardInfo = new DataMap();
            if(IDataUtil.isNotEmpty(oldEmptyCardInfos) && oldEmptyCardInfos.size()>0){
            	oldEmptyCardInfo = oldEmptyCardInfos.getData(0);
            }else{
            	// 获取老sim卡信息不存在
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询旧的空卡" + oldSimInfo.getString("EMPTY_CARD_ID") + "获取不到信息。");
            }
            
            oldResTypeCode = oldEmptyCardInfo.getString("RES_TYPE_CODE", "");
        }
        if (StringUtils.isNotEmpty(newSimInfo.getString("EMPTY_CARD_ID")))
        {
            newCardFlag = true;// 新卡是否为白卡写成
            IDataset newEmptyCardInfos = ResCall.getEmptycardInfo(newSimInfo.getString("EMPTY_CARD_ID"), serialNumber, "");
            IData newEmptyCardInfo = new DataMap();
            if(IDataUtil.isNotEmpty(newEmptyCardInfos) && newEmptyCardInfos.size()>0){
            	newEmptyCardInfo = newEmptyCardInfos.getData(0);
            }else{
            	// 获取老sim卡信息不存在
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询新的空卡" + newSimInfo.getString("EMPTY_CARD_ID") + "获取不到信息。");
            }
            
            newResTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "");
            newResKindCode = newEmptyCardInfo.getString("RES_KIND_CODE", "");
        }
        // 旧卡为非专用卡，新卡为白卡专属卡时不收费
        if (newCardFlag && ("6D".equals(newResTypeCode) || "60".equals(newResTypeCode) || "65".equals(newResTypeCode)))
        {
            if (!oldCardFlag && !"1D".equals(oldResTypeCode) && !"1F".equals(oldResTypeCode) && !"1H".equals(oldResTypeCode) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");

                return returnInfo;
            }
            else if (oldCardFlag && !"6D".equals(oldResTypeCode) && !"65".equals(oldResTypeCode) && !"60".equals(oldResTypeCode) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");

                return returnInfo;
            }
        }
        // 旧卡为非专用卡，新卡为SIM卡专属卡时不收费
        else if (!newCardFlag && ("1D".equals(newResTypeCode) || "1F".equals(newResTypeCode) || "1H".equals(newResTypeCode)))
        {
            if (!oldCardFlag && !"1D".equals(oldResTypeCode) && !"1F".equals(oldResTypeCode) && !"1H".equals(oldResTypeCode) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");

                return returnInfo;
            }
            else if (oldCardFlag && !"6D".equals(oldResTypeCode) && !"65".equals(oldResTypeCode) && !"60".equals(oldResTypeCode) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");

                return returnInfo;
            }
        }

        /*
         * 先判断是否为大客户，大客户每年可免费补换卡2次， 非大客户则判断用户主套餐，全球通商旅、全球通上网888/588/388套餐每年免费换卡2次， 全球通商旅、全球通上网288套餐每年免费换卡1次
         */
        int vipFreeChgCard = 0;
        IDataset tagInfo = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_VIPFREECHGCARDS", "CSM", "0");
        // 获取大客户是否需要缴费：大客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
        IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userInfo.getString("USER_ID"));
        String userVipTypeCode = "";
        String userVipClassId = "";
        if (IDataUtil.isNotEmpty(vipInfo))
        {
            userVipTypeCode = vipInfo.getData(0).getString("VIP_TYPE_CODE", "");
            userVipClassId = vipInfo.getData(0).getString("VIP_CLASS_ID", "");
        }
        if (userVipTypeCode.equals("2") || userVipClassId.equals("1") || userVipClassId.equals("2") || userVipClassId.equals("3") || userVipClassId.equals("4") || userVipClassId.equals("5"))
        {
            if (IDataUtil.isNotEmpty(tagInfo))
            {
                vipFreeChgCard = Integer.parseInt(tagInfo.getData(0).getString("TAG_NUMBER"));
            }
            else
            {
                // 获取大客户免费换卡次数失败
                CSAppException.apperr(CrmCardException.CRM_CARD_242);
            }
        }

        int productFreeChgCard = 0;
        String strTagInfo = "";
        IDataset tagInfo2 = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_PRODUCTFREECHGCARDS", "CSM", "0");
        if (StringUtils.isNotEmpty(productId))
        {
            strTagInfo = tagInfo2.getData(0).getString("TAG_INFO", "");
            String[] p = strTagInfo.split(",");
            for (int i = 0; i < p.length; i = i + 2)
            {
                if (StringUtils.equals(productId,p[i]))
                {
                    productFreeChgCard = Integer.parseInt(p[i + 1]);
                }
            }
        }
        else
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_254);
        }
        
        //Start 3至5星级客户免费补换卡次数需求
        //3至5星级用户，判断免费办卡次数是否大于大客户和全球通商旅套餐免费换卡次数，如果都大于就用星级免费补换卡次数限制 
    	int ClassUserFreeeChgCard = 0;
    	IDataset tagInfoClassUser = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_CLASSUSERFREECHGCARDS", "CSM", "0");
    	
        if (IDataUtil.isNotEmpty(tagInfoClassUser))
        {
        	ClassUserFreeeChgCard = Integer.parseInt(tagInfoClassUser.getData(0).getString("TAG_NUMBER"));
        }
        else
        {
            // 获取3至5星级客户免费换卡次数失败
            CSAppException.apperr(CrmCardException.CRM_CARD_274);
        }
        
        if((ClassUserFreeeChgCard > vipFreeChgCard) && (ClassUserFreeeChgCard > productFreeChgCard))
        {
            UcaData uca = null;
            String strCreditClass = "0";
            //复机由于用户已经不在网，用户资料也有可能已经搬历史表了，这样是取不到账户星级的，默认5星级复机。
            IDataset userList =  UserInfoQry.getUserInfoBySn(userInfo.getString("SERIAL_NUMBER"), "0");
            if(IDataUtil.isEmpty(userList)){
            	strCreditClass = "5";
            }else{
            	uca = UcaDataFactory.getUcaByUserId(userInfo.getString("USER_ID", ""));
                strCreditClass = uca.getUserCreditClass();
            }
            int iCreditClass = Integer.parseInt(strCreditClass);
        	 if(3 <= iCreditClass)
             {
                 // 获取3至5星级客户是否需要缴费：星级客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
                 IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID", ""), "05", "0");// new
                 int iCount = 0;
                 for (int i = 0; i < userOtherserv.size(); i++)
                 {
                     IData tmp = (IData) userOtherserv.get(i);
                     String nowYear = SysDateMgr.getNowYear();
                     if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                     {
                         iCount++;
                     }
                 }
                 if ((iCount < ClassUserFreeeChgCard || ClassUserFreeeChgCard == 0) && !bFeeTag)
                 {
                     returnInfo.put("FEE_TAG", "0");
                     returnInfo.put("FEE_INFO", "3至5星级客户免费换卡，每年可免费补换卡" + ClassUserFreeeChgCard + "次");
                     returnInfo.put("FEE", "0");
                     returnInfo.put("FEE_TARDE", "0");
                     returnInfo.put("RSRV_VALUE_CODE", "05");
                     returnInfo.put("RSRV_VALUE", strCreditClass + "星级用户免费补换卡");

                     return returnInfo;

                 }
                 else
                 {
                     returnInfo.put("FEE_TAG", "1");
                     returnInfo.put("FEE_INFO", "该用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
                     returnInfo.put("FEE", "0");
                     returnInfo.put("FEE_TARDE", "1");
                 }
             }
        }
        //End 3至5星级客户免费补换卡次数需求
       
        // 如果vip大客户免费换卡次数大于全球通商旅套餐免费换卡次数
        if (vipFreeChgCard > productFreeChgCard)
        {
            // 获取大客户是否需要缴费：大客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID", ""), "05", "0");// new
            int iCount = 0;
            for (int i = 0; i < userOtherserv.size(); i++)
            {
                IData tmp = (IData) userOtherserv.get(i);
                String nowYear = SysDateMgr.getNowYear();
                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                {
                    iCount++;
                }
            }
            if ((iCount < vipFreeChgCard || vipFreeChgCard == 0) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE_INFO", "大客户免费换卡，每年可免费补换卡2次");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "0");
                returnInfo.put("RSRV_VALUE_CODE", "05");
                returnInfo.put("RSRV_VALUE", "VIP用户免费补换卡");

                return returnInfo;

            }
            else
            {
                returnInfo.put("FEE_TAG", "1");
                returnInfo.put("FEE_INFO", "该用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");
            }
        }
        else
        {
            if (productFreeChgCard > 0)
            {
                IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID"), "07", "0");// new
                int iCount = 0;
                for (int i = 0; i < userOtherserv.size(); i++)
                {
                    IData tmp = (IData) userOtherserv.get(i);
                    String nowYear = SysDateMgr.getNowYear();
                    if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                    {
                        iCount++;
                    }
                }
                if ((iCount < productFreeChgCard || productFreeChgCard == 0) && !bFeeTag)
                {
                    returnInfo.put("FEE_TAG", "0");
                    returnInfo.put("FEE_INFO", "该全球通商旅套餐用户在今年可以免费补换" + productFreeChgCard + "次！");
                    returnInfo.put("FEE", "0");
                    returnInfo.put("FEE_TARDE", "0");

                    returnInfo.put("RSRV_VALUE_CODE", "07");
                    returnInfo.put("RSRV_VALUE", "2011年全球通商旅套餐免费换卡");

                    return returnInfo;
                }
                else
                {
                    returnInfo.put("FEE_TAG", "1");
                    returnInfo.put("FEE_INFO", "该全球通商旅套餐用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
                    returnInfo.put("FEE", "0");
                    returnInfo.put("FEE_TARDE", "1");
                }
            }
        }

        // SIM卡补换卡费用调整,在网3年以上的老客户可以免费换卡
        String strOpenDate = userInfo.getString("OPEN_DATE", "");
        int openMonths = SysDateMgr.monthInterval(strOpenDate, SysDateMgr.getSysDate());
        if (openMonths >= 36)
        {
            // 获取3年老客户免费换卡次数
            int oldCustFreeChgCard = 0;
            IDataset tagInfo3 = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_OLDCUSTFREECHGCARDS", "CSM", "0");
            if (IDataUtil.isNotEmpty(tagInfo3))
            {
                oldCustFreeChgCard = Integer.parseInt(tagInfo3.getData(0).getString("TAG_NUMBER"));
            }
            else
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_243);
            }

            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID"), "03", "0");// new
            int iCount = 0;
            for (int i = 0; i < userOtherserv.size(); i++)
            {
                IData tmp = (IData) userOtherserv.get(i);
                String nowYear = SysDateMgr.getNowYear();
                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                {
                    iCount++;
                }
            }
            if ((iCount < oldCustFreeChgCard || oldCustFreeChgCard == 0) && !bFeeTag)
            {
                returnInfo.put("FEE_TAG", "0");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "0");

                if (oldCustFreeChgCard == 0)
                    returnInfo.put("FEE_INFO", "该用户在网年限大于3年,可以不限次数免费换卡！");
                else
                    returnInfo.put("FEE_INFO", "该用户在网年限大于3年,可以一年免费换卡" + oldCustFreeChgCard + "次！");

                // if("true".equals(Va)) //如果营改增不是放开标记则直接返回
                // {
                // //start
                // td.put("CARD_FREE_CARD", "1");
                // td.put("CARD_RES_KIND_CODE", strResKindCode);
                // td.put("CARD_CAPACITY_TYPE_CODE", strCapacityTypeCode);
                // td.put("CARD_PRODUCT_ID", strProductId);
                // td.put("CARD_CLASS_ID", strClassId);
                // //end
                // }
                returnInfo.put("RSRV_VALUE_CODE", "03");
                returnInfo.put("RSRV_VALUE", "在网3年以上老客户免费补换卡");

                return returnInfo;
            }
            else
            {
                returnInfo.put("FEE_TAG", "1");
                returnInfo.put("FEE_INFO", "该用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
                returnInfo.put("FEE", "0");
                returnInfo.put("FEE_TARDE", "1");
            }
        }

        /**
         * 获取卡费，通过resTypeCode获取卡费
         */
        // String feeResTypeCode = "1";
        // if(newCardFlag){
        // feeResTypeCode="6";
        // }
        IData feeData = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", tradeTypeCode, newResKindCode, newResTypeCode);
        if (IDataUtil.isEmpty(feeData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "544032:获取补换卡费用失败！");
        }
        else
        {
            returnInfo.put("FEE_TAG", "1");
            if (StringUtils.isEmpty(returnInfo.getString("FEE_INFO")))
            {
                returnInfo.put("FEE_INFO", "本次换卡将收取卡费！");
            }
            returnInfo.put("FEE", feeData.getString("DEVICE_PRICE"));
            returnInfo.put("FEE_TARDE", "1");
        }

        /**
         * 获取用户积分及计算换卡所用积分
         */
        returnInfo.put("SCORE_DO", "1");// 不用积分换取标识
        IData scoreData = AcctCall.queryUserScoreone(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(scoreData))
        {
            String score = scoreData.getString("SUM_SCORE", "0");
            IDataset commData = CommparaInfoQry.getCommPkInfo("CSM", "1422", "SCOREFORCHANGECARD", getTradeEparchyCode());
            if (IDataUtil.isEmpty(commData))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取积分配置数据失败！");
            }
            String canExchage = commData.getData(0).getString("PARA_CODE5", "0");//等于1则关闭积分兑换功能
            String scorePer = commData.getData(0).getString("PARA_CODE1", "0");
            int scoreNeedInt = calNeedScore(scorePer, returnInfo.getString("FEE"));
            int scoreInt = Integer.valueOf(score);
            if (scoreInt >= scoreNeedInt&&scoreNeedInt>0&&"0".equals(canExchage))
            {// 用户积分值大于所需积分值
            	//Date GJDate = SysDateMgr.string2Date("2015-03-31 23:59:59", SysDateMgr.PATTERN_STAND_SHORT);
               // Date OpenDate = SysDateMgr.string2Date(strOpenDate, SysDateMgr.PATTERN_STAND_SHORT);
            	String GJDate = SysDateMgr.getDateForSTANDYYYYMMDD("2015-03-31 23:59:59");
                String OpenDate = SysDateMgr.getDateForSTANDYYYYMMDD(strOpenDate);
                if(OpenDate.compareTo(GJDate)>0 &&openMonths<6 ){
                	returnInfo.put("SCORE_DO", "1");//不用积分换取标识
                }else{
                	returnInfo.put("SCORE_DO", "0");// 积分换取标识
                }
                returnInfo.put("USER_SCORE", score);
                returnInfo.put("NEED_SCORE", String.valueOf(scoreNeedInt));
            }
        }
        return returnInfo;
    }

    /**
     * 查询用户资源信息
     * 
     * @param PageData
     *            ，IData
     * @return IData
     * @exception
     */
    public IData getUserResource(IData input) throws Exception
    {
        input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset userRes = UserResInfoQry.getUserResInfoByUserId(input.getString("USER_ID", ""));
        IData out = new DataMap();
        if (IDataUtil.isEmpty(userRes))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_71); // 查询用户原SIM卡资源信息无记录！
        }
        for (int i = 0; i < userRes.size(); i++)
        {
            IData data = new DataMap();
            data = (IData) userRes.get(i);
            String resTypeCode = data.getString("RES_TYPE_CODE");
            if (resTypeCode.equals("1"))
            {
                out.put("SIM_CARD_NO", data.getString("RES_CODE"));
                out.put("IMSI", data.getString("IMSI"));
                out.put("KI", data.getString("KI", ""));
                out.put("INST_ID", data.getString("INST_ID"));
                out.put("START_DATE", data.getString("START_DATE"));
                out.put("SIM_TYPE_CODE", data.getString("RES_TYPE_CODE"));
            }
        }

        IDataset userAttr = UserAttrInfoQry.getUserAttrByPK(input.getString("USER_ID", ""), out.getString("INST_ID", ""), new Pagination());

        for (int i = 0; i < userAttr.size(); i++)
        {
            IData data = userAttr.getData(i);
            if (data.getString("ATTR_CODE").equals("OPC_VALUE"))
            {
                out.put("OLD_OPC", data.getString("ATTR_VALUE"));
                out.put("ATTR_INST_ID", data.getString("INST_ID"));
                out.put("INST_TYPE", data.getString("INST_TYPE"));
                out.put("ATTR_CODE", data.getString("ATTR_CODE"));
                out.put("ATTR_VALUE", data.getString("ATTR_VALUE"));
                out.put("ATTR_START_DATE", data.getString("START_DATE"));
            }
        }

        return out;
    }

    /**
     * 获取用户主服务状态信息
     * 
     * @param input
     * @param page
     * @return
     * @throws Exception
     */
    public IData getUserSvcState(IData input) throws Exception
    {
        IData outPut = new DataMap();
        outPut.put("USER_SVC_STATE_TAG", "false");
        IDataset userSvcState = UserSvcStateInfoQry.queryUserMainTagScvState(input.getString("USER_ID"));
        if (userSvcState.size() == 1)
        {
            IData tempInfo = userSvcState.getData(0);
            if (tempInfo.getString("MAIN_TAG", "").equals("1") && tempInfo.getString("STATE_CODE", "").equals("1"))
            {
                outPut.put("USER_SVC_STATE_TAG", "true");
            }
        }

        return outPut;

    }
    
	/**
	 * 作用：补卡结果反馈接口
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData reCardResult (IData data) throws Exception{
	    
	    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx662 "+data);
	    
		if (data.getString("TRANSACTION_ID") == null
	                || data.getString("TRANSACTION_ID").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_1154);			
	    }
		if (data.getString("SERIAL_NUMBER") == null
				|| data.getString("SERIAL_NUMBER").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_120);
	    }
		if (data.getString("OPER_CODE") == null
				|| data.getString("OPER_CODE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1156);
	    }
		if (data.getString("CHANNEL_ID") == null
				|| data.getString("CHANNEL_ID").equals("")) {
			 CSAppException.apperr(CrmCommException.CRM_COMM_1149);
	    }
		if (data.getString("CUST_NAME") == null
				|| data.getString("CUST_NAME").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1155);
	    }
		if (data.getString("CREDIT_LEVEL") == null
				|| data.getString("CREDIT_LEVEL").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户星级不能为空");
	    }
		if (data.getString("CHARGE_FEE") == null
				|| data.getString("CHARGE_FEE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务费不能为空");
	    }
		if (data.getString("CARD_FEE") == null
				|| data.getString("CARD_FEE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "卡费不能为空");
	    }

		if (data.getString("CARD_NUMBER") == null
				|| data.getString("CARD_NUMBER").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "白卡卡号不能为空");
	    }
		if (data.getString("ICCID") == null
				|| data.getString("ICCID").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "Iccid不能为空");
	    }
		if (data.getString("CUST_CERT_NO") == null
				|| data.getString("CUST_CERT_NO").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户证件号不能为空");
	    }
		if (data.getString("ORD_CODE") == null
				|| data.getString("ORD_CODE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "流水号不能为空");
	    }
		
        String operCode = data.getString("OPER_CODE", "");

        IData staff = UStaffInfoQry.qryStaffInfoByPK(operCode);//根据工号查
        if (!staff.isEmpty()) {
            CSBizBean.getVisit().setStaffId(staff.getString("STAFF_ID"));
            CSBizBean.getVisit().setStaffName(staff.getString("STAFF_NAME"));
            CSBizBean.getVisit().setDepartId(staff.getString("DEPART_ID"));
            CSBizBean.getVisit().setCityCode(staff.getString("CITY_CODE"));            
        } else {
            IDataset staffDs = UStaffInfoQry.qryStaffInfoBySn(operCode);//根据手机号码 
            if (!staffDs.isEmpty()) {
                CSBizBean.getVisit().setStaffId(staffDs.getData(0).getString("STAFF_ID"));
                CSBizBean.getVisit().setStaffName(staffDs.getData(0).getString("STAFF_NAME"));
                CSBizBean.getVisit().setDepartId(staffDs.getData(0).getString("DEPART_ID"));
                CSBizBean.getVisit().setCityCode(staffDs.getData(0).getString("CITY_CODE"));                
            }
        }
		IData result = new DataMap();
		IData tableData = new DataMap();
		tableData.put("TRANSACTION_ID", data.getString("TRANSACTION_ID",""));
		String tradeId = SeqMgr.getTradeIdFromDb();
		tableData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
		tableData.put("CHANNEL_ID", data.getString("CHANNEL_ID",""));
		tableData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));
		tableData.put("CUST_NAME", data.getString("CUST_NAME",""));
		tableData.put("CUST_CERT_NO", data.getString("CUST_CERT_NO",""));
		tableData.put("ICCID", data.getString("ICCID",""));
		tableData.put("STAFF_ID", data.getString("OPER_CODE",""));
		tableData.put("CREDIT_LEVEL", data.getString("CREDIT_LEVEL",""));
		tableData.put("CHARGE_FEE", data.getString("CHARGE_FEE",""));
		tableData.put("CARD_FEE", data.getString("CARD_FEE",""));
		tableData.put("STAFF_ID", data.getString("OPER_CODE",""));
		tableData.put("CREDIT_LEVEL", data.getString("CREDIT_LEVEL",""));
		tableData.put("CHARGE_FEE", data.getString("CHARGE_FEE",""));
		tableData.put("CARD_FEE", data.getString("CARD_FEE",""));
		tableData.put("EMPTY_CARD_ID", data.getString("CARD_NUMBER",""));
		tableData.put("ORD_CODE", data.getString("ORD_CODE",""));
		tableData.put("TRADE_ID", tradeId);
		tableData.put("CARD_FEE", data.getString("CARD_FEE",""));
		tableData.put("UPDATE_TIME", SysDateMgr.getSysTime());		
		
		//先插入日志表，记录信息
	    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx753 "+tableData);
		Dao.insert("TF_F_RECARD_INFO", tableData);
	      System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx755 "+tableData.getString("EMPTY_CARD_ID"));
		//处理白卡卡号
		remoteWriteEmptyCard(tradeId,tableData.getString("EMPTY_CARD_ID"));
		//构造漫入省打印发票需要的数据
		IData param = new DataMap();
		param.putAll(data);
		param.put("TRADE_ID", tradeId);
		param.put("PSPT_ID", data.getString("CUST_CERT_NO",""));
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx763 "+param);		
		createTrade(param);
		
        //构造漫入省打印免填单需要的数据
        ReceiptNotePrintMgr rnpMgr = new ReceiptNotePrintMgr();        
        RegTradeData rtd = new RegTradeData(tradeId);
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx769 "+rtd); 
        rnpMgr.getTradeReceipt(rtd);//记录打印模板数据到TF_B_TRADE_CNOTE_INFO表
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx771 "+rtd); 
        
        if ("0".equals(data.getString("IN_MODE_CODE")) 
        		&& "CRM".equalsIgnoreCase(data.getString("CHANNEL_ID"))) {
            IData realParam = new DataMap();
            realParam.put("ReqSeq",data.getString("TRANSACTION_ID"));
            realParam.put("REALNAME_SEQ",data.getString("ORD_CODE"));
            realParam.put("HOME_PROV", data.getString("HOME_PROV"));// 归属省代码        
            realParam.put("TRADE_ID", tradeId);
            LanuchUtil util = new LanuchUtil();
            util.writeRealNameLog4RemoteWriteCard(realParam);
            
            result.put("TRADE_ID", tradeId);
        }
		//返回结果。默认成功，中间有异常，上层处理异常，返回失败消息。
		result.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "SUCCESS");
		result.put("IS_SUC", "1");
		System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx793 "+result); 
		return result;
	}
	/**
	 * 查询异地补卡结果反馈信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryReCardResult (IData input,Pagination pagination) throws Exception{
//		if (input.getString("SERIAL_NUMBER") == null
//				|| input.getString("SERIAL_NUMBER").equals("")) {
//			 CSAppException.apperr(CrmCommException.CRM_COMM_120);
//	    }
		String serialNumber = input.getString("SERIAL_NUMBER");
		String eparchyCode = CSBizBean.getTradeEparchyCode();
		String endDate = input.getString("END_DATE", "");
        if (StringUtils.isNotBlank(endDate))
        {
            endDate = endDate + SysDateMgr.getEndTime235959();
        }
        
        IDataset printsInfos = TradeReceiptInfoQry.queryReCardResult(serialNumber, input.getString("START_DATE", ""), endDate, pagination, eparchyCode);
        if (IDataUtil.isNotEmpty(printsInfos)) {
        	for (int i = 0; i < printsInfos.size(); i++) {
        		IData param = printsInfos.getData(i);
        		param.put("IS_INVOICE_PRINTED", "0");
                IDataset printRecords = TicketInfoQry.qryPrintedReceipts(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_STAFF_ID", ""));
                //IDataset printPDFRecordes = TicketInfoQry.queryPrintPDFLogByTradeIdSerialNumber(param.getString("TRADE_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("TRADE_STAFF_ID", ""));还没上线的，先留着，后面有要求，再补
                if (IDataUtil.isNotEmpty(printRecords)) {
                    for (int j = 0; j < printRecords.size(); j++)
                    {
                        String tradeId = printRecords.getData(j).getString("TRADE_ID", "");
                        if (tradeId.equals(param.getString("TRADE_ID")) )
                        {
                    	    param.put("IS_INVOICE_PRINTED", "1");//如果当前记录已经打印过发票,则标识已打印                    	
                            break;
                        }
                    }
        	    }
            }
        }        
		return printsInfos;
	}
	
    /**
     * 异地写卡 白卡回写
     * 
     * @param tradeId
     * @param emptyCardId
     * @return
     * @throws Exception
     */
    public IData remoteWriteEmptyCard(String tradeId,String emptyCardId) throws Exception
    {
    	String result = "0";// 0 成功 1 失败
    	String xGetMode = "1";//传入1时如果修改不到白卡也不抛错
        IDataset resInfos = ResCall.remoteWriteEmptyCard(result, emptyCardId,tradeId,xGetMode);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))//0-成功；其他-失败
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "白卡回写错误！");
        }
        return resInfos.getData(0);
    }
    public void remoteWriteUpdateByIntf(IData data) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("TRANS_ID", data.getString("TRANS_ID"));
        returnInfo.put("TEMP_NUMBER", data.getString("TEMP_NUMBER"));
        returnInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            
        String encKi = data.getString("KI");
        String encOpc = data.getString("OPC");
        String imsi = data.getString("IMSI");
        String writeTag = "3";
        String simCardNo = data.getString("ICCID");
        String remoteMode = "1";
        String xChoiceTag = "0";  

        KIFunc kifunc = new KIFunc();
        String ki = kifunc.EncryptKI(encKi);
        String opc = kifunc.EncryptKI(encOpc);
        IDataset resInfos = ResCall.backWriteSimCardByIntf(imsi,writeTag,simCardNo,remoteMode, xChoiceTag,ki,opc,data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }
    }

	/**
	 * 创建业务台帐
	 */
	public BusiTradeData createTrade(IData param) throws Exception
	{
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx871 "+param); 
		//构造漫入省打印发票需要的数据
		List<BaseTradeData> tradeDataList = new ArrayList<BaseTradeData>();
	    UcaData uca = this.buildUcaData(param);
	    BaseReqData brd = new BaseReqData();
        brd.setUca(uca);
        brd.setXTransCode(CSBizBean.getVisit().getXTransCode());
        brd.setJoinType("0");
        String tradeTypeCode = "149";
        IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, uca.getUserEparchyCode());

        if (IDataUtil.isEmpty(tradeType))
        {
            CSAppException.apperr(BofException.CRM_BOF_001, tradeTypeCode);
        }
        String tradeId = param.getString("TRADE_ID");
        brd.setTradeType(new TradeTypeData(tradeType));
        //brd.setCheckMode(param.getString("CHECK_MODE", "Z"));// 默认为无
        brd.setTradeId(tradeId);
        brd.setPageRequestData(param);
        
		BusiTradeData bd = new BusiTradeData();
		bd.setBrd(brd);

		//根据受理时间生成，避免跨月产生tf_b_order和tf_b_trade表不一致。
		//rd.setTradeId(SeqMgr.getNewTradeIdFromDb(rd.getAcceptTime()));
		IData input = brd.getPageRequestData();
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx898 "+param); 
		// 拼主台账
		createBusiMainTradeData(input, bd);
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx901 "); 
		createFeeTradeData(input,input.getString("CHARGE_FEE"),"0",bd);//构建服务费数据到TF_B_TRADEFEE_SUB表
		System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx903 ");
		createFeeTradeData(input,input.getString("CARD_FEE"),"10",bd);//构建卡费数据到TF_B_TRADEFEE_SUB表	
		System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx905 ");
        createResTradeData(input,bd);//构建数据到tf_b_trade_res表        
        //构建收费业务涉及的其他数据
        IData tmpInput = new DataMap();
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx909 ");
        createPayMoneyTD(tmpInput,bd);
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx911 ");
        //算税，tf_b_tradefee_tax 
		CreateTaxAction taxAction = new CreateTaxAction();
		taxAction.executeAction(bd);
		System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx915 ");
		List<BusiTradeData> btds = new ArrayList<BusiTradeData>();
		IData tmpInput2 = new DataMap();
		btds.add(bd);
	      System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx919 ");
		PluginProcess.pluginProcessBefore(btds, tmpInput2);
		System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx921 ");
        insertTrades(bd);
		return bd;
	}
	
	private void createBusiMainTradeData(IData input,BusiTradeData bd) throws Exception
	{
		MainTradeData mainTradeData = new MainTradeData();
		mainTradeData.setTradeTypeCode("149");
		mainTradeData.setPriority("1000");
		mainTradeData.setUserId("0");
		mainTradeData.setCustId("0");
		mainTradeData.setAcctId("0");
		mainTradeData.setCustName(input.getString("CUST_NAME"));
		mainTradeData.setExecTime(SysDateMgr.getSysTime());
		mainTradeData.setSerialNumber(input.getString("SERIAL_NUMBER"));
		mainTradeData.setSubscribeState("0");
		mainTradeData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);
		mainTradeData.setNextDealTag("0");
		mainTradeData.setInModeCode(CSBizBean.getVisit().getInModeCode());
		String strPTS = BofConst.PROCESS_TAG_SET;
		
		strPTS = strPTS.substring(0, 19) + "4" + strPTS.substring(20);//设置受理方式为： 证件号码+服务密码校验
		mainTradeData.setProcessTagSet(strPTS);//
		mainTradeData.setOlcomTag("0");// 默认不发
		mainTradeData.setCancelTag(BofConst.CANCEL_TAG_NO);
		mainTradeData.setNetTypeCode("00");
		/*int chargeFee = Integer.parseInt(input.getString("CHARGE_FEE","0"));
		int cardFee = Integer.parseInt(input.getString("CARD_FEE","0"));
		int totalOperMoney = chargeFee + cardFee;
		mainTradeData.setOperFee(String.valueOf(totalOperMoney));*/
		mainTradeData.setOperFee("0");
		mainTradeData.setForegift("0");
		mainTradeData.setAdvancePay("0");
		mainTradeData.setOrderId("0");
		mainTradeData.setProductId("-1");
		mainTradeData.setBrandCode("-1");
		mainTradeData.setEparchyCode("0898");//该接口只可能是0898,所以写死， 取消CSBizBean.getVisit().getStaffEparchyCode()
		mainTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
		mainTradeData.setRemark("跨区补卡记录日志");
		mainTradeData.setTermIp(CSBizBean.getVisit().getRemoteAddr());
        mainTradeData.setPfWait("0");        
        mainTradeData.setRsrvStr8("跨区补卡USIM卡");// 新SIM卡类型
        mainTradeData.setRsrvStr10(input.getString("ICCID"));//跟simcardtrade的逻辑保持一致
        
        BaseReqData brd = bd.getRD();
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx967 "+mainTradeData); 
        bd.add(brd.getUca().getSerialNumber(), mainTradeData);
     }
	
	private void createFeeTradeData(IData input,String fee,String feeTypeCode,BusiTradeData bd) throws Exception
	{
		// 更新主台账的费用信息
		
		FeeTradeData feeTradeData = new FeeTradeData();
		feeTradeData.setFeeTypeCode(feeTypeCode);
		feeTradeData.setFeeMode("0");
		feeTradeData.setFee(fee);
		feeTradeData.setOldfee(fee);
		feeTradeData.setRsrvStr1(input.getString("CARD_NUMBER"));
		feeTradeData.setUserId("0");
		
        BaseReqData brd = bd.getRD();
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx984 "+feeTradeData); 
        bd.add(brd.getUca().getSerialNumber(), feeTradeData);
	}
	
	private void createResTradeData(IData input,BusiTradeData bd) throws Exception
    {
        String instId = SeqMgr.getInstId();
        ResTradeData resTradeDataNew = new ResTradeData();
        resTradeDataNew.setUserId("0");
        resTradeDataNew.setUserIdA("-1");
        resTradeDataNew.setResTypeCode("1");
        resTradeDataNew.setResCode(input.getString("ICCID"));
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setStartDate(SysDateMgr.getSysTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeDataNew.setRsrvStr5(input.getString("CARD_NUMBER"));

        BaseReqData brd = bd.getRD();
        System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1003 ");
        bd.add(brd.getUca().getSerialNumber(), resTradeDataNew);
    }
	
	/**
	 * 构建PAYMONEY台账
	 * 
	 * @param input
	 * @param mainBtd
	 * @throws Exception
	 */
	public void createPayMoneyTD(IData input,BusiTradeData bd) throws Exception
	{
        BaseReqData brd = bd.getRD();
		String tradeId = brd.getTradeId();
		MainTradeData mainTradeData = bd.getMainTradeData();
		IDataset payMoneyList = new DatasetList();
		if (StringUtils.isNotBlank(input.getString("X_TRADE_PAYMONEY")))
		{
			// 传了则已传的为准
			payMoneyList = new DatasetList(input.getString("X_TRADE_PAYMONEY"));
		}
		else
		{
			String advancePay = mainTradeData.getAdvancePay();
			String foreGift = mainTradeData.getForegift();
			String operFee = mainTradeData.getOperFee();

			// 这个是用户总共缴纳的费用
			int totalPayMoney = Integer.parseInt(advancePay) + Integer.parseInt(foreGift) + Integer.parseInt(operFee);
			if (totalPayMoney > 0)
			{
				IData cashPayMoney = new DataMap();
				cashPayMoney.put("MONEY", String.valueOf(totalPayMoney));
				cashPayMoney.put("PAY_MONEY_CODE", input.getString("X_PAY_MONEY_CODE", "0"));
				payMoneyList.add(cashPayMoney);
			}
		}

		for (int i = 0, size = payMoneyList.size(); i < size; i++)
		{
			IData payMoneyData = payMoneyList.getData(i);
			payMoneyData.put("ORDER_ID", mainTradeData.getOrderId());
			PayMoneyTradeData payMoneyTD = new PayMoneyTradeData(payMoneyData);
			bd.add(brd.getUca().getSerialNumber(), payMoneyTD);

			// POS机刷卡
			if (payMoneyData.getString("PAY_MONEY_CODE").equals(PosBean.POS_PAY_CODE))
			{
				PosBean.checkPosFee(input.getString("TRADE_POS_ID"), mainTradeData.getOrderId(), tradeId, payMoneyData.getString("MONEY"));
			}
		}
	}
	
	private void insertTrades(BusiTradeData btd) throws Exception {
	    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1041 "+btd);
		Map<String, List<BaseTradeData>> tradeDatasMap = btd.getTradeDatasMap();        
	    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1043 "+tradeDatasMap);
        if (tradeDatasMap != null && !tradeDatasMap.isEmpty()) {
            System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1045 "+tradeDatasMap);
            IData tableData = new DataMap();
            Iterator lter = tradeDatasMap.keySet().iterator();
            String tradeId = btd.getRD().getTradeId();

            while (lter.hasNext()) {
                IDataset regTradeDataSet = new DatasetList();
                System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1052 "+regTradeDataSet);                
                String key = (String) lter.next();
                List<BaseTradeData> tradeDataList = (tradeDatasMap.get(key));
                System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1055 "+tradeDataList);   
                if (tradeDataList != null)
                {
                    for (int j = 0, size = tradeDataList.size(); j < size; j++)
                    {
                        BaseTradeData regTradeData = tradeDataList.get(j);
                        IData regData = regTradeData.toData();                        
		                regData.put("TRADE_ID", tradeId);
		                regData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
		                regData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		                regData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		                regData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		                regData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                        regData.put("TRADE_EPARCHY_CODE", "0898");
		                regData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		                regData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		                regData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		                String routeId = Route.getJourDbDefault();
		                //主台账特殊处理，插历史表与TF_BH_TRADE_STAFF表
		                System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1074 "+regTradeData.getTableName());   
		                if (regTradeData.getTableName().equals(TradeTableEnum.TRADE_MAIN.getValue()))
		                {
		                    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1077 "+regTradeData.getTableName()+"  ___   "+regData+routeId);
		                    regData.put("DAY", tradeId.substring(6, 8));// 为了bh_trade_staff表用
		            		Dao.insert("TF_BH_TRADE", regData, routeId);
		                    String tradeTypeCode = regData.getString("TRADE_TYPE_CODE");
		                    // 是否搬迁到历史2表
		                    boolean bSecond = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_FINISH_MOVE_SECOND, false);
		                    // 如果是，则不插trade_bh_staff表
	                          System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1083 "+bSecond);  
		                    if (bSecond == false)
		                    {System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1086 "+regData);
		                        Dao.insert("TF_BH_TRADE_STAFF", regData, routeId);
		                    }
		                } else {
		                    System.out.println("SimCardBean.javaxxxxxxxxxxxxxxxxxx1090 "+regTradeData.getTableName()+"  ___   "+regData+routeId);
		                	Dao.insert(regTradeData.getTableName(), regData, routeId);
		                } 
                    }
                }
            }
        }
	}
	
	public UcaData buildUcaData(IData param) throws Exception
	{
		UcaData uca = new UcaData();

		String userId =  "0";
		String custId = "0";
		String acctId = "0";
		String strCustCityCode = param.getString("CITY_CODE", "");
		String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
		// 1.构建用户资料
		UserTradeData userData = new UserTradeData();
		userData.setUserId(userId);
		userData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		userData.setSerialNumber(param.getString("SERIAL_NUMBER"));
		userData.setNetTypeCode("00");
		userData.setUserStateCodeset("0");
		userData.setUserDiffCode("0");
		userData.setOpenMode(param.getString("OPEN_MODE", "0"));
		userData.setAcctTag(param.getString("ACCT_TAG", "0"));
		userData.setPrepayTag("1");
		userData.setMputeMonthFee("0");
		userData.setRemoveTag("0");
		
		userData.setCustId(custId);
		userData.setUsecustId(custId);
		userData.setEparchyCode(CSBizBean.getUserEparchyCode());
		userData.setCityCode(cityCode);
		if (StringUtils.equals("402", param.getString("PRODUCT_ID", "")))
		{
			userData.setUserPasswd(StringUtils.substring(userData.getSerialNumber(), 5));
		}
		else
		{
			userData.setUserPasswd(param.getString("USER_PASSWD", ""));
		}
		userData.setUserTypeCode(param.getString("USER_TYPE_CODE", "0"));
		userData.setDevelopDepartId(param.getString("DEVELOP_DEPART_ID"));
		userData.setDevelopDate(SysDateMgr.getSysTime());
		userData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
		userData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
		userData.setDevelopCityCode(cityCode);
		userData.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode());
		userData.setInDate(SysDateMgr.getSysTime());
		userData.setInStaffId(CSBizBean.getVisit().getStaffId());
		userData.setInDepartId(CSBizBean.getVisit().getDepartId());
		userData.setOpenDate(SysDateMgr.getSysTime());
		userData.setOpenStaffId(CSBizBean.getVisit().getStaffId());
		userData.setOpenDepartId(CSBizBean.getVisit().getDepartId());
		userData.setAssureTypeCode(param.getString("ASSURE_TYPE_CODE", ""));
		userData.setAssureDate(param.getString("ASSURE_DATE", ""));
		userData.setDevelopNo(param.getString("DEVELOP_NO", ""));

		// 2.构建客户核心资料
		CustomerTradeData customerData = new CustomerTradeData();
		customerData.setCustId(custId);
		customerData.setCustName(param.getString("CUST_NAME", "").trim());
		customerData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE", ""));
		customerData.setPsptId(param.getString("PSPT_ID", ""));
		customerData.setCityCode(cityCode);
		customerData.setModifyTag("0");
		customerData.setCustType("0");
		customerData.setCustState("0");
		customerData.setOpenLimit(param.getString("OPEN_LIMIT", "0"));
		customerData.setInDate(SysDateMgr.getSysTime());
		customerData.setInStaffId(CSBizBean.getVisit().getStaffId());
		customerData.setInDepartId(CSBizBean.getVisit().getDepartId());
		customerData.setRemoveTag("0");
		customerData.setEparchyCode(CSBizBean.getUserEparchyCode());
		customerData.setCustPasswd(param.getString("CUST_PASSWD", "0"));
		customerData.setScoreValue(param.getString("SCORE_VALUE", "0"));
		customerData.setCreditClass(param.getString("CREDIT_CLASS", "0"));
		customerData.setBasicCreditValue(param.getString("BASIC_CREDIT_VALUE", "0"));
		customerData.setCreditValue(param.getString("CREDIT_VALUE", "0"));
		customerData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
		customerData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
		// 3.构建个人客户资料
		CustPersonTradeData custPersonData = new CustPersonTradeData();
		custPersonData.setCustId(custId);
		custPersonData.setCustName(param.getString("CUST_NAME", "").trim());
		custPersonData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
		custPersonData.setPsptId(param.getString("PSPT_ID"));
		custPersonData.setPsptEndDate(param.getString("PSPT_END_DATE", ""));
		custPersonData.setPsptAddr(param.getString("PSPT_ADDR", ""));
		custPersonData.setSex(param.getString("SEX", ""));
		custPersonData.setCityCode(cityCode);
		custPersonData.setModifyTag("0");
		custPersonData.setEparchyCode(CSBizBean.getUserEparchyCode());
		custPersonData.setBirthday(param.getString("BIRTHDAY", ""));
		custPersonData.setPostAddress(param.getString("CONTACT_ADDRESS", ""));
		custPersonData.setPostCode(param.getString("POST_CODE", ""));
		custPersonData.setPhone(param.getString("PHONE", ""));
		custPersonData.setFaxNbr(param.getString("FAX_NBR", ""));
		custPersonData.setEmail(param.getString("EMAIL", ""));
		custPersonData.setHomeAddress(param.getString("HOME_ADDRESS", ""));
		custPersonData.setWorkName(param.getString("WORK_NAME", ""));
		custPersonData.setWorkDepart(param.getString("WORK_DEPART", ""));
		custPersonData.setJobTypeCode(param.getString("JOB_TYPE_CODE", ""));
		custPersonData.setContact(param.getString("CONTACT", ""));
		custPersonData.setContactPhone(param.getString("CONTACT_PHONE", ""));
		custPersonData.setContactTypeCode(param.getString("CONTACT_TYPE_CODE", ""));
		custPersonData.setNationalityCode(param.getString("NATIONALITY_CODE", ""));
		custPersonData.setFolkCode(param.getString("FOLK_CODE", ""));
		custPersonData.setReligionCode(param.getString("RELIGION_CODE", ""));
		custPersonData.setLanguageCode(param.getString("LANGUAGE_CODE", ""));
		custPersonData.setEducateDegreeCode(param.getString("EDUCATE_DEGREE_CODE", ""));
		custPersonData.setMarriage(param.getString("MARRIAGE", ""));
		custPersonData.setRemoveTag("0");
		custPersonData.setCallingTypeCode(param.getString("CALLING_TYPE_CODE", ""));
		// 4.构建账户资料
		AccountTradeData accountData = new AccountTradeData();
		accountData.setAcctId(acctId);
		accountData.setCustId(custId);
		accountData.setModifyTag("0");
		accountData.setNetTypeCode("00");
		accountData.setPayName(param.getString("PAY_NAME", "").trim());
		accountData.setPayModeCode(param.getString("PAY_MODE_CODE", ""));
		accountData.setBankCode(param.getString("BANK_CODE", ""));
		accountData.setBankAcctNo(param.getString("BANK_ACCT_NO", ""));
		accountData.setCityCode(cityCode);
		accountData.setEparchyCode(CSBizBean.getUserEparchyCode());
		accountData.setScoreValue("0");
		accountData.setBasicCreditValue("0");
		accountData.setCreditValue("0");
		accountData.setOpenDate(SysDateMgr.getSysTime());
		accountData.setRemoveTag("0");
		accountData.setRsrvStr6(param.getString("BANK_AGREEMENT_NO", "")); // 银行协议号
		accountData.setRsrvStr7(param.getString("BANK_DEPOSIT_TYPE", ""));// 银行业务类型
		accountData.setAcctDiffCode(param.getString("ACCT_DIFF_CODE", "0"));
		accountData.setDebutyCode(param.getString("DEBUTY_CODE", userData.getSerialNumber()));
		accountData.setDebutyUserId(param.getString("DEBUTY_USER_ID", userData.getUserId()));
		accountData.setCreditClassId(param.getString("CREDIT_CLASS_ID", ""));

		uca.setUser(userData);
		uca.setCustomer(customerData);
		uca.setCustPerson(custPersonData);
		uca.setAccount(accountData);
		uca.setAcctBlance("0");
		uca.setLastOweFee("0");
		uca.setRealFee("0");

		AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "", "", "");
		AcctTimeEnvManager.setAcctTimeEnv(env);
		return uca;
	}
	
	 public IData verifyIMEI(IData input) throws Exception {		
         String serialNumber = input.getString("SERIAL_NUMBER");
         String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

     	 IDataset  simSet = ResCall.selOneOccupyESim(serialNumber,getVisit());
 		 if(IDataUtil.isEmpty(simSet)){
 		    CSAppException.apperr(CrmCommException.CRM_COMM_103,"资源接口返回新卡数据为空！");			
 		 }
         IData tempInfo = simSet.getData(0);
         String resTypeCode = tempInfo.getString("RES_TYPE_CODE", "");
         String resKindCode = tempInfo.getString("RES_KIND_CODE", "");

         UcaData userInfo = UcaDataFactory.getNormalUca(serialNumber);
         IData feeData = DevicePriceQry.getDevicePrice(getTradeEparchyCode(),userInfo.getProductId(), tradeTypeCode,resKindCode, resTypeCode);        
         if (IDataUtil.isEmpty(feeData)){
        	 tempInfo.put("FEE_TAG", "1");
        	 tempInfo.put("FEE_INFO", "本次换卡将不收取卡费！");
        	 tempInfo.put("FEE", "0");
        	 tempInfo.put("FEE_TARDE", "1");
         } else {
        	 tempInfo.put("FEE_TAG", "1");
        	 tempInfo.put("FEE_INFO", "本次换卡将收取卡费！");
        	 tempInfo.put("FEE", feeData.getString("DEVICE_PRICE"));
        	 tempInfo.put("FEE_TARDE", "1");
         }
         return tempInfo;
	}
}
