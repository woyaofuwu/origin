
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule.CheckTradeBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ChangeCardSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(ChangeCardSVC.class);

    /**
     * 查询用户资源信息
     * @return IData
     * @exception
     */
    public IDataset getUserResource(IData input) throws Exception
    {

        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        IData data = cardBean.getUserResource(input);
        data.putAll(cardBean.getUserSvcState(input));
        data.putAll(SimCardQueryBean.getSimCardInfo(data.getString("SIM_CARD_NO"), input));
        // data.put("OLD_SIM_CARD_INFO", SimCardQueryBean.getSimCardInfo(data.getString("SIM_CARD_NO")).toString());
        IDataset dataset = new DatasetList();

        // add by yangxd3 商务电话补换卡把固话号码转换成157号码
        IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(input.getString("USER_ID", ""), "T2", null);
        if (DataSetUtils.isNotBlank(relaUUInfos))
        {
            String serialNumberA = relaUUInfos.first().getString("SERIAL_NUMBER_A");
            data.put("SERIAL_NUMBER_A", serialNumberA);
        }

        dataset.add(data);
        return dataset;
    }
    public IData getWriteSimCardInfoL2F(IData input) throws Exception
    {
        if (log.isDebugEnabled()) {
            log.debug("异地写卡请求个性化数据接口>>>" + input.toString());
        }
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "ok");
        result.put("X_RSPTYPE", "0");
        result.put("X_RSPCODE", "0000");
        result.put("X_RSPDESC", "ok");
        try {
        chkParam(input, "SEQ");
        chkParam(input, "SERIAL_NUMBER"); //CARDSN NAME VALUE
        chkParam(input, "CARDSN");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String emptyNoId = input.getString("CARDSN");
        String identCode="";
        IDataset extensionReq = input.getDataset("EXTENSION_REQ");
        if (log.isDebugEnabled()) {
            log.debug("异地写卡请求个性化数据接口extensionReq>>>" + extensionReq);
        }
        if(IDataUtil.isNotEmpty(extensionReq)){
        	result.put("EXTENSION_RSP", extensionReq);
        	for(int k=0;k<extensionReq.size();k++){
        		IData nameAndVlue = extensionReq.getData(k);
        		String identTag = nameAndVlue.getString("Name");
        		if("02".equals(identTag)){
        			identCode = nameAndVlue.getString("Value");
        			break;
        		}
        	}
        }
        if (log.isDebugEnabled()) {
            log.debug("异地写卡请求个性化数据接口identCode>>>" + identCode);
        }
        if(StringUtils.isNotBlank(identCode)){
        	IDataset userAuth = UserIdentInfoQry.queryIdentInfoByCode(identCode, serialNumber);
        	if(IDataUtil.isEmpty(userAuth)){
        		result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        		result.put("X_RESULTCODE", "3018");
        		result.put("X_RESULTINFO", "用户身份凭证错误");
        		result.put("SEQ", input.getString("SEQ"));
        		result.put("RETURN_MESSAGE","用户身份凭证错误");
        		result.put("HOME_PROV", "898");
        		result.put("X_RSPCODE", "3018");
        		result.put("X_RSPDESC", "用户身份凭证错误");
        		result.put("X_RSPTYPE", "2");
        		return result;
        	}else {
        		IData identInfo = userAuth.getData(0);
        		String isValidate = identInfo.getString("TAG");
        		if("EXPIRE".equals(isValidate)){
        			result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        			result.put("X_RESULTCODE", "3018");
        			result.put("X_RESULTINFO", "用户身份凭证已失效/过期");
        			result.put("SEQ", input.getString("SEQ"));
        			result.put("RETURN_MESSAGE","用户身份凭证已失效/过期");
        			result.put("HOME_PROV", "898");
        			result.put("X_RSPCODE", "3018");
        			result.put("X_RSPDESC", "用户身份凭证已失效/过期");
        			result.put("X_RSPTYPE", "2");
        			return result;
        		}
        	}
        }
        
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        IData resInfo = bean.getSpeSimInfo(serialNumber, emptyNoId, "", "1").getData(0);//空卡序列号是否要传，不知道资源侧有什么用，两不一快没传，这里先不传。
        result.put("IMSI", resInfo.getString("IMSI"));
        result.put("ICCID", resInfo.getString("SIM_CARD_NO"));
        result.put("SMSP", resInfo.getString("SMSP"));
        result.put("PIN1", resInfo.getString("PIN"));
        result.put("PIN2", resInfo.getString("PIN2"));
        result.put("PUK1", resInfo.getString("PUK"));
        result.put("PUK2", resInfo.getString("PUK2"));
        result.put("HOME_PROV", "898");
        result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        result.put("SEQ", input.getString("SEQ"));
        } catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e);
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            result.put("X_RESULTCODE", "5007");//异地写卡失败（找不到该号码所述字段）	5007
            result.put("SEQ", input.getString("SEQ"));
			result.put("HOME_PROV", "898");
			result.put("X_RSPCODE", "2998"); 
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPDESC", e.getMessage());
			result.put("X_RESULTINFO", "异地写卡失败");
    		if (null != errorArray && errorArray.length>1) {
    			result.put("RETURN_MESSAGE", errorArray[1]);
    		} else {
    			result.put("RETURN_MESSAGE", error);
    		}
        }
        catch (Exception ex2) {
        	log.error("异地写卡异常>>>>>"+ex2.getMessage(),ex2);
            result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            result.put("X_RESULTCODE", "5007");//异地写卡失败（找不到该号码所述字段）	5007
            result.put("SEQ", input.getString("SEQ"));
    		result.put("RETURN_MESSAGE",ex2.getMessage());
			result.put("HOME_PROV", "898");
			result.put("X_RSPCODE", "5007");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPDESC", ex2.getMessage());
			result.put("X_RESULTINFO", ex2.getMessage());
        }
        return result;
    }

    /**
     * 补卡结果反馈接口 RECARD_RESULT_RETURN
     * 为实名认证平台提供
     * @return IData
     * @exception
     */
    public IData reCardResult(IData input) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("异地补卡结果反馈接口>>>" + input.toString());
        }
        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        IData result  = new DataMap();
        try {
        	result = cardBean.reCardResult(input);
        } catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e);
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
    		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID","0"));
    		result.put("RETURN_CODE", "2999");
    		if (null != errorArray && errorArray.length>1) {
    			result.put("RETURN_MESSAGE", errorArray[1]);
    		} else {
    			result.put("RETURN_MESSAGE", error);
    		}
    		result.put("IS_SUC", "0");
        }
        catch (Exception ex2) {
        	log.error(ex2);
    		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID","0"));
    		result.put("RETURN_CODE", "1001");
    		result.put("RETURN_MESSAGE","系统异常");
    		result.put("IS_SUC", "0");
    		//ex2.printStackTrace();
        }

        return result;
    }
    /**
     * 查询异地补卡结果反馈信息
     * 为打印业务提供
     * @return IData
     * @exception
     */
    public IDataset queryReCardResult(IData input) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("查询异地补卡结果反馈信息>>>" + input.toString());
        }
        SimCardBean bean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        return bean.queryReCardResult(input, getPagination());
    }

    /**
     * 补卡号码校验接口 CHANG_CARD_CHECK_SVCNUM
     * 为实名认证平台提供
     * @return IData
     * @exception
     */
    public IData checkSerialNumber(IData input) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("补卡号码校验接口>>>" + input.toString());
        }
		IData returnMap = new DataMap();
		returnMap.put("rtnCode", "0");
		returnMap.put("rtnMsg", "成功");
		IData object = new DataMap();
		returnMap.put("object", object);
		object.put("respCode", "0");
		object.put("respDesc", "success");
		IDataset result = new DatasetList();
		object.put("result", result);
		IData resultItem = new DataMap();
		result.add(resultItem);
		resultItem.put("transactionID", input.getString("TRANSACTION_ID"));
		IData resInfo = new DataMap();
		resultItem.put("resInfo", resInfo);
		resInfo.put("isReg", "1");
        try {
        	String tradeTypeCode = "142";
        	checkBefore(input,tradeTypeCode);
        } catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e);
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			returnMap.put("rtnCode", "2999");
			returnMap.put("rtnMsg", "失败");
			resInfo.put("isReg", "0");
			object.put("respCode", "2999");
    		if (null != errorArray && errorArray.length>1) {
				object.put("respDesc", errorArray[1]);
    		} else {
				object.put("respDesc", error);
    		}
        } catch (Exception ex2) {
        	log.error(ex2);
			returnMap.put("rtnCode", "2999");
			returnMap.put("rtnMsg", "系统异常");
			resInfo.put("isReg", "0");
			object.put("respCode", "1001");
			object.put("respDesc", "系统异常");
        }
        return returnMap;
    }

    /**
     * 实名认证平台补卡号码校验     *
     * @return IData
     * @exception
     */
    private IData checkBefore(IData input,String tradeTypeCode) throws Exception
    {
        //校验逻辑跟页面点击Auth组件查询时调用的各服务业务校验保持一致
        //String inputStr ="TRADE_TYPE_CODE":"141","USER_ID":"3116041425050498","page":"simcardmgr.RemoteCard","SERIAL_NUMBER":"13407315704","AUTH_TYPE":"00","USER_CAN_BE_NULL":"false"";
        //CS.GetInfosSVC.getUCAInfos
        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo)) {
        	CSAppException.apperr(CrmUserException.CRM_USER_273);
        }
        IData param = new DataMap();//UCA查询校验入参
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("USER_ID", userInfo.getString("USER_ID",""));
        param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
        param.put("AUTH_TYPE", "00");
        param.put("USER_CAN_BE_NULL", "false");
        TradeInfoBean infoBean = BeanManager.createBean(TradeInfoBean.class);
        IData ucainfo = infoBean.getTradeUCAInfo(param);

        IData ucaUserInfo = ucainfo.getData("USER_INFO");
        if (IDataUtil.isEmpty(userInfo)) {
        	CSAppException.apperr(CrmUserException.CRM_USER_273);
        }
        //CS.CheckTradeSVC.checkBeforeTrade
        IData checkParam =  new DataMap();//checkBeforeTrade查询校验入参
        checkParam.put("SERIAL_NUMBER", ucaUserInfo.getString("SERIAL_NUMBER"));
        checkParam.put("USER_ID", ucaUserInfo.getString("USER_ID"));
        checkParam.put("CUST_ID", ucaUserInfo.getString("CUST_ID"));
        checkParam.put("PRODUCT_ID", ucaUserInfo.getString("PRODUCT_ID"));
        checkParam.put("BRAND_CODE", ucaUserInfo.getString("BRAND_CODE"));
        checkParam.put("X_CHOICE_TAG", "0");
        checkParam.put("ACCT_TAG", "0");//传该参数过去，以便吉祥号能进入业务规则判断
        checkParam.put("TRADE_TYPE_CODE", tradeTypeCode);

        CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
        IDataset dataset = bean.checkBeforeTrade(checkParam);
        if(!dataset.isEmpty()){
            IData checkRs =dataset.getData(0);
            CSAppException.breerr(checkRs);//对吉祥号业务规则判断结果进行处理
        }
        //RM.ResSimCardIntfSvc.getSimcardInfo
        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        IData data = cardBean.getUserResource(userInfo);//user_id
        SimCardQueryBean.getSimCardInfo(data.getString("SIM_CARD_NO"), input);
		IData result = new DataMap();
		result.put("isReg", "1");
        return result;
    }
    private String chkParam(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);
        if (StringUtils.isEmpty(strParam))
        {
            StringBuilder strError = new StringBuilder("-1:接口参数检查: 输入参数[");
            strError.append(strColName).append("]不存在或者参数值为空");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
        }
        return strParam;
    }
    /**
     * 获取客户相关资料信息
     * @author zhuoyingzhi
     * @date 20170904
     * @param param
     * @return
     * @throws Exception
     */
    public IData getCustInfo(IData param) throws Exception
    {
        String custId = param.getString("CUST_ID");
        /**
         * BUG20171115091427_生僻字名字用户无法过户问题
         * <br/>
         * 由于在过户的时候要获取CUST_ID比较麻烦，就直接传空的过来,
         * 从过手机号码重新获取CUST_ID。
         * @author zhuoyingzhi
         * @date 20171127
         */
        if("".equals(custId)||custId==null){
	        String  serialNumber=param.getString("SERIAL_NUMBER","");
        	IData ucaUserInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
        	custId=ucaUserInfo.getString("CUST_ID","");
        }
        IData custInfo = new DataMap();
        IData personData = UcaInfoQry.qryPerInfoByCustId(custId);
        custInfo.put("USE", personData.getString("RSRV_STR5",""));
        custInfo.put("USE_PSPT_TYPE_CODE", personData.getString("RSRV_STR6",""));
        custInfo.put("USE_PSPT_ID", personData.getString("RSRV_STR7",""));
        custInfo.put("USE_PSPT_ADDR", personData.getString("RSRV_STR8",""));
        if (IDataUtil.isEmpty(personData))
        {
            CSAppException.apperr(CustException.CRM_CUST_69);// 获取个人客户资料无数据!(考虑资料未全异常资料的情况下)
        }
        custInfo.putAll(personData);
        // 查个人客户
        custInfo.putAll(UcaInfoQry.qryCustomerInfoByCustId(custId));
        return custInfo;

    }
}
