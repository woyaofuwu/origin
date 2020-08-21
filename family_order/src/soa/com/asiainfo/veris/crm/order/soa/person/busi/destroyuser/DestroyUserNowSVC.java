
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

public class DestroyUserNowSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    
    private static StringBuilder getInterFaceSQL;

	static
	{
		getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
	}

    public IDataset queryBusiInfo(IData input) throws Exception
    {
        IData busiInfo = new DataMap();

        String serialNumber = input.getString("SERIAL_NUMBER", "0");
        String userId = input.getString("USER_ID", "0");

        // 需要调用账管接口查询账户余额、押金和欠费信息;
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        busiInfo.put("LEFT_MONEY", oweFee.getDouble("ACCT_BALANCE", 0) / 100.0);// 余额
        busiInfo.put("OWE_FEE", oweFee.getDouble("LAST_OWE_FEE", 0) / 100.0);// 用户欠费信息
        IDataset foregiftDataset = UserForegiftInfoQry.getUserForegift(userId);
        if (IDataUtil.isNotEmpty(foregiftDataset))
        {
            String foregift = foregiftDataset.getData(0).getString("MONEY", "0");
            busiInfo.put("FOREGIFT", Double.parseDouble(foregift) / 100.0);
        }
        else
        {
            busiInfo.put("FOREGIFT", "0");
        }

        // 获取用户携转资料
        IDataset npInfos = UserNpInfoQry.qryUserNpInfosByUserId(userId);
        if (IDataUtil.isNotEmpty(npInfos))
        {
            if (npInfos.size() == 1) // 携转用户
            {
                IDataset msisdnInfos = TradeNpQry.getValidTradeNpBySn(serialNumber);
                if (IDataUtil.isEmpty(msisdnInfos))
                {
                    StringBuilder msgBuilder = new StringBuilder(50);
                    msgBuilder.append("该号码【").append(serialNumber).append("】没有号码归属地信息！");
                    CSAppException.apperr(TradeException.CRM_TRADE_95, msgBuilder.toString());
                }
                busiInfo.putAll(npInfos.getData(0));
                busiInfo.put("ASP", msisdnInfos.getData(0).get("ASP"));
                busiInfo.put("NPTAG", "1");
            }
            else
            {
                StringBuilder msgBuilder = new StringBuilder(50);
                msgBuilder.append("23460：该号码【").append(serialNumber).append("】存在多条携转资料记录！");
                CSAppException.apperr(TradeException.CRM_TRADE_95, msgBuilder.toString());
            }
        }

        return IDataUtil.idToIds(busiInfo);
    }

    public IDataset queryUserScore(IData input) throws Exception
    {
        IDataset scoreInfo = AcctCall.queryUserScore(input.getString("USER_ID"));
        return scoreInfo;
    }
    //REQ201807230029一机多宽业务需求1.0版本
    public IDataset queryTFRelation(IData input) throws Exception
    {
    	IDataset relaUUInfos = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(input.getString("USER_ID"),"58");
		if(IDataUtil.isEmpty(relaUUInfos)){
			return new DatasetList();
		}
        return relaUUInfos;
    }
    
    //销户号码同步给能开
    public IData destroyUsersysnAbility(IData input) throws Exception{
		
		IData param1 = new DataMap();
    	param1.put("PARAM_NAME", input.getString("getUrl",""));
    	IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0){
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}
    	else
    	{
    		throw new Exception("["+input.getString("getUrl","")+"]接口地址未在TD_S_BIZENV表中配置");
    	}
    	String apiAddress = Abilityurl;
		
		if(StringUtils.isBlank(input.getString("oprTime","")))
		{
			input.getString("oprTime",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		}

		if(StringUtils.isBlank(input.getString("numberActivateTime","")))
		{
			input.getString("numberActivateTime",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		}
        if(input.containsKey("getUrl")){
        	input.remove("getUrl");
        }
        if(input.containsKey("TRADE_EPARCHY_CODE")){
        	input.remove("TRADE_EPARCHY_CODE");
        }
		IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,input);
		return stopResult;
	}
}
