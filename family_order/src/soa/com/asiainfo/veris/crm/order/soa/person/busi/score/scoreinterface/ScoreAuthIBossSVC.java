
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

public class ScoreAuthIBossSVC extends CSBizService
{

    public IData scoreAuth(IData inparam) throws Exception
    {
        IData returnData = new DataMap();
        String userLevel = "";
        String brandCode = "";
        String userStatus = "";
        
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

        // 判断用户客服密码是否正确
        if(StringUtils.isNotBlank(inparam.getString("USER_PASSWD"))){
        	String oldPsw = uca.getUser().getUserPasswd();// 数据库取出加密后的密码
            boolean flag = PasswdMgr.checkUserPassword(inparam.getString("USER_PASSWD"), uca.getUserId(), oldPsw);
            if (!flag)
            {
                // 用户客服密码错误!
                CSAppException.apperr(CrmUserException.CRM_USER_401);
            }
        }     

        // 获取客户等级并转换客户等级
        VipTradeData vipData = uca.getVip();
        if (vipData == null)
        {
            userLevel = "100"; // 普通客户
        }
        else
        {
            userLevel = transVipClass(vipData);
        }

        // 获取用户品牌并转换品牌
        brandCode = uca.getBrandCode();
        if ("G001".equals(brandCode))
        {
            brandCode = "01"; // 全球通
        }
        else if ("G002".equals(brandCode))
        {
            brandCode = "02"; // 神州行
        }
        else if ("G010".equals(brandCode))
        {
            brandCode = "03"; // 动感地带
        }
        else
        {
            brandCode = "09"; // 其它品牌
        }
        if ("02".equals(brandCode) && "0".equals(uca.getCustomer().getIsRealName()))
        {
            // 神州行非实名制用户无积分!
            CSAppException.apperr(CrmUserException.CRM_USER_1150);
        }
        // 对于用户品牌为“动感地带和神州行”时，客户级别不能返回以3开头的代码
        if (userLevel.startsWith("3") && ("02".equals(brandCode) || "03".equals(brandCode)))
        {
            userLevel = "100";
        }

        // 获取用户状态并转换用户状态
        userStatus = transUserState(uca.getUserSvcsState().get(0).getStateCode());

        //zhouyl5 20150325 用户星级
        IData creditInfo = CreditCall.queryUserCreditInfos(uca.getUserId());
        //STAR_LEVEL
        int creditClass = creditInfo.getInt("CREDIT_CLASS",-1);
        String inDate = uca.getUser().getInDate();//入网时间
        //入网时间 6个月后的 积分使用开始时间  VALIDATE_START_TIME
        String validateStartTime="";
        if(SysDateMgr.string2Date("2015-04-01", "yyyy-MM-dd").before(SysDateMgr.string2Date(inDate, "yyyy-MM-dd"))){
        	validateStartTime=SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsLastDay(6,inDate), "yyyyMMddHHmmss");
        }
        // 获取客户可用积分余额
        String score = "0";
        String totalPoint = "";// 用户总积分
        IDataset scoreInfo = AcctCall.queryUserScore(uca.getUserId());
        if (IDataUtil.isNotEmpty(scoreInfo))
        {
            score = scoreInfo.getData(0).getString("SCORE");
            //TOTAL_POINT 用户总积分
            totalPoint = scoreInfo.getData(0).getString("SUM_SCORE");
        }
        IDataset scoreLimitDs = UserInfoQry.qryUserScoreLimit(serialNumber);
        if (IDataUtil.isNotEmpty(scoreLimitDs))
        {
            returnData.put("SCORE_VALUE", "0");
        }
        else
        {
            returnData.put("SCORE_VALUE", score);
        }

        returnData.put("RSRV_STR1", inparam.getString("RSRV_STR1"));
        returnData.put("BRAND_CODE", brandCode);
        returnData.put("USER_LEVEL", userLevel);
        returnData.put("USER_STATUS", userStatus);
        returnData.put("X_RESULTCODE", "0");
        returnData.put("X_RESULTINFO", "OK");
        
        //zhouyl5
        returnData.put("STAR_LEVEL", tranStarLevel(creditClass));
        returnData.put("VALIDATE_START_TIME", validateStartTime);
        returnData.put("TOTAL_POINT", totalPoint);

        return returnData;
    }

    // 转换用户状态
    public String transUserState(String stateCode) throws Exception
    {
        String userStatus = "";
        if ("0".equals(stateCode) || "N".equals(stateCode))
        {
            userStatus = "00"; // 正常
        }
        else if ("8".equals(stateCode))
        {
            userStatus = "03"; // 预销户
        }
        else if ("6".equals(stateCode) || "9".equals(stateCode))
        {
            userStatus = "04"; // 销户
        }
        else if ("R".equals(stateCode) || "A".equals(stateCode) || "B".equals(stateCode) || "G".equals(stateCode))
        {
            userStatus = "01"; // 单向停机
        }
        else if ("1".equals(stateCode) || "2".equals(stateCode) || "3".equals(stateCode) || "4".equals(stateCode) || "5".equals(stateCode) || "7".equals(stateCode) || "E".equals(stateCode) || "F".equals(stateCode) || "I".equals(stateCode)
                || "S".equals(stateCode))
        {
            userStatus = "02"; // 停机
        }

        return userStatus;
    }
    /**
     * 转换客户星级
     */
    public String tranStarLevel(int creditClass){
    	String starLevel = "13";
    	if(creditClass == -1){
    		starLevel = "13";
    	}
    	if(creditClass == 0){
    		starLevel = "12";
    	}
    	if(creditClass == 1){
    		starLevel = "11";
    	}
    	if(creditClass == 2){
    		starLevel = "10";
    	}
    	if(creditClass == 3){
    		starLevel = "09";
    	}
    	if(creditClass == 4){
    		starLevel = "08";
    	}
    	if(creditClass == 5){
    		starLevel = "07";
    	}
    	if(creditClass == 6){
    		starLevel = "06";
    	}
    	if(creditClass == 7){
    		starLevel = "05";
    	}
    	
    	return starLevel;
    }

    /**
     * 转换客户级别
     * 
     * @author huangsl
     * @param inparam
     * @return
     * @throws Exception
     */
    public String transVipClass(VipTradeData vipData) throws Exception
    {
        String userLevel = "";
        String vipTypeCode = vipData.getVipTypeCode();
        String classId = vipData.getVipClassId();
        if ("0".equals(vipTypeCode))
        {
            if ("1".equals(classId))
            {
                userLevel = "100";// 贵宾卡 -> 100
            }
            else if ("2".equals(classId))
            {
                userLevel = "303"; // 银卡大客户
            }
            else if ("3".equals(classId))
            {
                userLevel = "302"; // 金卡大客户
            }
            else if ("4".equals(classId))
            {
                userLevel = "301"; // 钻石卡大客户
            }
            else
            {
                userLevel = "200"; // 重要客户
            }
        }
        else if ("2".equals(vipTypeCode))
        {
            if ("1".equals(classId))
            {
                userLevel = "100";// 贵宾卡 -> 100
            }
            else if ("2".equals(classId))
            {
                userLevel = "303"; // 银卡大客户
            }
            else if ("3".equals(classId))
            {
                userLevel = "302"; // 金卡大客户
            }
            else if ("4".equals(classId))
            {
                userLevel = "301"; // 钻石卡大客户
            }
            else
            {
                userLevel = "200"; // 重要客户
            }
        }
        else if ("3".equals(vipTypeCode))
        {
            if ("5".equals(classId))
            {
                userLevel = "100"; // 潜在大客户
            }
            else
            {
                userLevel = "200"; // 重要客户
            }
        }
        else if ("5".equals(vipTypeCode))
        {
            if ("3".equals(classId))
            {
                userLevel = "302"; // 金卡大客户
            }
            else if ("4".equals(classId))
            {
                userLevel = "301"; // 钻石卡大客户
            }
            else
            {
                userLevel = "200"; // 重要客户
            }
        }
        else if ("6".equals(vipTypeCode))
        {// 准大客户当做普通客户处理
            userLevel = "100";
        }
        else
        {
            userLevel = "100";
        }
        return userLevel;
    }
}
