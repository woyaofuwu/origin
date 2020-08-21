package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;


public class NationalOpenLimitBean extends CSBizBean
{

    Logger log = Logger.getLogger(NationalOpenLimitBean.class);

    //----------------------------------------------------start 过渡方案代码-------------------------------------------------------------------------
    /**
     * 是否可以跳过全网用户数据平台校验，
     * @return true：跳过平台校验  false：需走平台校验
     * @throws Exception
     */
    public boolean isIgnoreCall() throws Exception
    {

        //默认调用全网用户数据查询平台进行一证五号校验，如果认证平台无法正常工作时，可通过此开关来跳过平台校验,        
        IDataset configInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "CHECK_NATION_OPENUM");
        String isVerify = "";
        if (IDataUtil.isNotEmpty(configInfo)) {
            isVerify = configInfo.getData(0).getString("PARA_CODE1", "").trim();//是否进行验证, 返回1为验证, 0不验证
        }

        //是否有不需要调用全网用户数据查询平台的权限,如果有权限，可跳过平台认证
        boolean hasPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CHECK_NATION_OPENUM");
        hasPriv = false;
        if (isVerify.equals("0") || hasPriv) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 5.3  证件号码查验（idCheck）
     * 通过此接口查询同一个客户证件号码下目前全国已开户的号码数量，平台向省公司返回查询结果。
     * @param cust_name
     * @param pspt_type_code
     * @param pspt_id
     * @param channelId
     * @return
     * @throws Exception
     */
    public IDataset idCheck(IData inparam) throws Exception
    {
        String psptTypeCode = IDataUtil.chkParam(inparam, "IDCARD_TYPE");
        boolean isIgnore = isIgnoreCall();
        if (isIgnore) {//开关打开、有权限、或者不是身份证的无需进行一证五号校验
             return null;
        }

        //不良信息校验开关
        String unTrustCheck = "";
        IDataset configInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "CHECK_NATION_OPENUM");
        if (IDataUtil.isNotEmpty(configInfo)) {
            unTrustCheck = configInfo.getData(0).getString("PARA_CODE2", "").trim();//是否进行验证, 返回1为验证, 其他不验证
        }

        String custName = IDataUtil.chkParam(inparam, "CUSTOMER_NAME");
        String psptId = IDataUtil.chkParam(inparam, "IDCARD_NUM");

        addChannelId(inparam);
        String channelId = IDataUtil.chkParam(inparam, "CHANNEL_ID");

        String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String seqRealId = SeqMgr.getRealId();

        String idCardType = "";

        IDataset psptTypeCodeTrans = checkPspt(psptTypeCode);

        if (IDataUtil.isNotEmpty(psptTypeCodeTrans)) {
            idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
        } else {
            return null;
        }

        IData param = new DataMap();
        param.put("SEQ", "898" + date + seqRealId);
        param.put("CUSTOMER_NAME", custName);
        param.put("IDCARD_TYPE", idCardType);//平台身份证类型为00
        param.put("IDCARD_NUM", psptId);
        param.put("CHANNEL_ID", channelId);
        param.put("MS_OPCODE", CSBizBean.getVisit().getStaffId());
        param.put("UN_TRUST_CHECK", unTrustCheck);
        param.put("KIND_ID", "idCheck_QWCX_0_0");
        IDataset callResult = IBossCall.callHttpIBOSS4("IBOSS", param);
        
        //start-REQ201901310001-wangsc10修改证件号码查验（idCheck_QWCX_0_0）接口，对调用一级BOSS返回成功时，对结果返回的数量字段TOTAL +  同步接口表未发送或发送失败的新增记录 总和作为已经占用的数量
        if (IDataUtil.isNotEmpty(callResult))
		{
			if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
			{
				int openNum = callResult.getData(0).getInt("TOTAL", 0);
				IDataset psptIdlsit = queryIdCardNum(psptId);
				if (IDataUtil.isNotEmpty(psptIdlsit))
				{
					int count = psptIdlsit.size();
					callResult.getData(0).put("TOTAL", String.valueOf(openNum+count));
				}

				if(!"1".equals(unTrustCheck))
                {
                    callResult.getData(0).put("UN_TRUST_RESULT", 0);
                }
			}
		}
        //end

        return callResult;
    }
    
    /**
	   * 查询新增OPR=01的证件号码还未处理成功的数据
	   * @param param
	   * @return
	   * @throws Exception
	   */
	public static IDataset queryIdCardNum(String idCardNum) throws Exception {	 	
		IData inparam = new DataMap();
		inparam.put("ID_CARD_NUM", idCardNum);
	 	return Dao.qryByCode("TF_F_OPENLIMIT_CAMPON_IBOSS", "SEL_OPENLIMIT_CAMPON_IBOSS_BY_IDCARDNUM", inparam,Route.CONN_CRM_CEN);
	}
	
    /**
     * 5.3  证件号码查验（idCheck）不校验权限
     * 通过此接口查询同一个客户证件号码下目前全国已开户的号码数量，平台向省公司返回查询结果。
     * @param cust_name
     * @param pspt_type_code
     * @param pspt_id
     * @param channelId
     * @return
     * @throws Exception
     */
    public IDataset idCheckNoPermi(IData inparam) throws Exception
    {
        String psptTypeCode = IDataUtil.chkParam(inparam, "IDCARD_TYPE");
        String custName = IDataUtil.chkParam(inparam, "CUSTOMER_NAME");
        String psptId = IDataUtil.chkParam(inparam, "IDCARD_NUM");

        addChannelId(inparam);
        String channelId = IDataUtil.chkParam(inparam, "CHANNEL_ID");

        String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String seqRealId = SeqMgr.getRealId();

        String idCardType = "";

        IDataset psptTypeCodeTrans = checkPspt(psptTypeCode);

        if (IDataUtil.isNotEmpty(psptTypeCodeTrans)) {
            idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
        } else {
            return null;
        }
        //不良信息校验开关
        String unTrustCheck = "";
        IDataset configInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "CHECK_NATION_OPENUM");
        if (IDataUtil.isNotEmpty(configInfo)) {
            unTrustCheck = configInfo.getData(0).getString("PARA_CODE3", "").trim();//是否进行验证, 返回1为验证,其他不验证
        }


        IData param = new DataMap();
        param.put("SEQ", "898" + date + seqRealId);
        param.put("CUSTOMER_NAME",ToDBC( custName));
        param.put("IDCARD_TYPE", idCardType);//平台身份证类型为00
        param.put("IDCARD_NUM", psptId);
        param.put("CHANNEL_ID", channelId);
        param.put("MS_OPCODE", CSBizBean.getVisit().getStaffId());
        param.put("UN_TRUST_CHECK", unTrustCheck);
        param.put("KIND_ID", "idCheck_QWCX_0_0");
        IDataset callResult = IBossCall.callHttpIBOSS4("IBOSS", param);

        if (IDataUtil.isNotEmpty(callResult))
        {
            if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
            {
                if(!"1".equals(unTrustCheck))
                {
                    callResult.getData(0).put("UN_TRUST_RESULT", 0);
                }
            }
        }

        return callResult;
    }

    /**
     * 全网用户数据查询 手机号码查询接口(phoneQuery)
     *   
     * @author zhaohj3
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryCustNumber(IData inparam) throws Exception
    {
        String psptTypeCode = IDataUtil.chkParam(inparam, "PSPT_TYPE_CODE");
        String psptId = IDataUtil.chkParam(inparam, "PSPT_ID");

        IDataset ChlInfos = ChnlInfoQry.getGlobalChlId(getVisit().getDepartId());

        if (IDataUtil.isNotEmpty(ChlInfos)) {
            inparam.put("CHANNEL_ID", ChlInfos.getData(0).getString("GLOBAL_CHNL_ID", ""));
        }

        String channelId = IDataUtil.chkParam(inparam, "CHANNEL_ID");

        String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String seqRealId = SeqMgr.getRealId();

        String idCardType = "";

        IDataset psptTypeCodeTrans = CommparaInfoQry.getCommparaAllCol("CSM", "2553", psptTypeCode, "ZZZZ");

        if (IDataUtil.isNotEmpty(psptTypeCodeTrans)) {
            idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_902);
        }

        //不良信息校验开关
        String unTrustCheck = "";
        IDataset configInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "CHECK_NATION_OPENUM");
        if (IDataUtil.isNotEmpty(configInfo)) {
            unTrustCheck = configInfo.getData(0).getString("PARA_CODE4", "").trim();//是否进行验证, 返回1为验证, 其他不验证
        }

        IData param = new DataMap();
        param.put("SEQ", "898" + date + seqRealId);
        param.put("IDCARD_TYPE", idCardType);
        param.put("IDCARD_NUM", psptId);
        param.put("CHANNEL_ID", channelId);
        param.put("MS_OPCODE", CSBizBean.getVisit().getStaffId());
        param.put("UN_TRUST_CHECK", unTrustCheck);
        param.put("KIND_ID", "phoneQuery_QWCX_0_0");
        //start-wangsc10 20181226 分页，写死100天，泰哥
        param.put("PAGE_NO", "0");
        param.put("PAGE_SIZE", "99");
        //end
        //新增限流开关配置
        String visitControlTag = StaticUtil.getStaticValue("PHONE_QUERY" , "VISIT_CONTROL");
        param.put("VISIT_CONTROL", inparam.getBoolean("VISIT_CONTROL","1".equals(visitControlTag)));

        IDataset callResult = IBossCall.callHttpIBOSS4("IBOSS", param);

        IDataset returnDataset = new DatasetList();
        if (IDataUtil.isEmpty(callResult)) {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        } else {
            if("b:24010".equals(callResult.getData(0).getString("X_RSPCODE"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"接口访问达到禁查阈值，查询请求拒绝");
            }
            if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
            {
                if(!"1".equals(unTrustCheck))
                {
                    callResult.getData(0).put("UN_TRUST_RESULT", 0);
                }
            }

            boolean visitAlarm = callResult.getData(0).getBoolean("VISIT_ALARM", false);
            int total = callResult.getData(0).getInt("TOTAL", 0);
            if (total > 0) {
                String psptTypeCodeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "TD_S_PASSPORTTYPE2", psptTypeCode });
                IData dataresult = callResult.getData(0);
                IDataset idv = dataresult.getDataset("IDV");
                IDataset customerName = dataresult.getDataset("CUSTOMER_NAME");
                IDataset homeProv = dataresult.getDataset("HOME_PROV");
                IDataset effetiTime = dataresult.getDataset("EFFETI_TIME");

                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (idv != null && idv.size() > 0) {
                    for (int i = 0; i < idv.size(); i++) {
                        IData reData = new DataMap();
                        reData.put("IDCARD_NUM", psptId);
                        reData.put("IDCARD_TYPE", psptTypeCodeName);
                        String idvs = (String) idv.get(i);
                        if (idvs == null || idvs.trim().length() == 0){
                            continue;
                        }
                        reData.put("IDV", (String) idv.get(i));
                        reData.put("CUSTOMER_NAME", (String) customerName.get(i));
                        reData.put("HOME_PROV", (String) homeProv.get(i));

                        String s = (String) effetiTime.get(i);
                        int year = Integer.parseInt(s.substring(0, 4));
                        int month = Integer.parseInt(s.substring(4, 6));
                        int day = Integer.parseInt(s.substring(6, 8));
                        int hrs = Integer.parseInt(s.substring(8, 10));
                        int min = Integer.parseInt(s.substring(10, 12));
                        int sec = Integer.parseInt(s.substring(12, 14));
                        reData.put("EFFETI_TIME", format.format(format.parse(year + "-" + month + "-" + day + " " + hrs + ":" + min + ":" + sec)));
                        reData.put("VISIT_ALARM",visitAlarm);
                        returnDataset.add(reData);
                    }
                }else{

                    if(dataresult.getString("IDV","").trim().length()>0){
                        IData reData = new DataMap();
                        reData.put("IDCARD_NUM", psptId);
                        reData.put("IDCARD_TYPE", psptTypeCodeName);
                        reData.put("IDV", dataresult.getString("IDV",""));                        
                        reData.put("CUSTOMER_NAME", dataresult.getString("CUSTOMER_NAME",""));
                        reData.put("HOME_PROV", dataresult.getString("HOME_PROV",""));

                        String s = dataresult.getString("EFFETI_TIME","");
                        int year = Integer.parseInt(s.substring(0, 4));
                        int month = Integer.parseInt(s.substring(4, 6));
                        int day = Integer.parseInt(s.substring(6, 8));
                        int hrs = Integer.parseInt(s.substring(8, 10));
                        int min = Integer.parseInt(s.substring(10, 12));
                        int sec = Integer.parseInt(s.substring(12, 14));
                        reData.put("EFFETI_TIME", format.format(format.parse(year + "-" + month + "-" + day + " " + hrs + ":" + min + ":" + sec)));
                        reData.put("VISIT_ALARM",visitAlarm);
                        returnDataset.add(reData);
                    }
               }
            }else{
                if(visitAlarm){
                    //预警判断:1有记录按记录中字段
                    //2无记录null为预警,不为null为非预警
                    returnDataset=null;
                }
            }
        }
        return returnDataset;
    }

    //----------------------------------------------------end 以上是过渡方案代码-------------------------------------------------------------------------

    /**
     * 5.4  证件预占状态变更（idCampOn）  //目标方案
     * 各省可通过此接口查询一个客户证件目前全国已开户的号码情况，省端系统开户时录入信息自动向平台传输证件类型及证件号码申请预占，平台向省公司返回预占结果。
     * @param：inparam
     * @author: ouyang
     */
    public IDataset idCampOn(IData inparam) throws Exception
    {

        IDataUtil.chkParam(inparam, "CUSTOMER_NAME");
        IDataUtil.chkParam(inparam, "ID_CARD_TYPE");
        IDataUtil.chkParam(inparam, "ID_CARD_NUM");
        IDataUtil.chkParam(inparam, "CAMP_ON");

        //解除预占
        if ("02".equals(inparam.getString("CAMP_ON"))) {
            IDataUtil.chkParam(inparam, "P_SEQ");
        }

        //补充操作流水号
        inparam = addSeq(inparam);

        inparam.put("KIND_ID", "idCampOn_QWCX_0_0");
        inparam = callIBossTrans(inparam);
        IDataset result = IBossCall.callHttpIBOSS4("IBOSS", inparam);
 
        if (IDataUtil.isEmpty(result)) {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0".equals(result.getData(0).getString("X_RESULTCODE"))) {
            if ("2998".equals(result.getData(0).getString("X_RESULTCODE"))) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RESULTINFO"));
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验【全网一证多号】预占情况出现异常，请联系系统管理员！");
            }
        }
        
        //预占时补充预占凭证
        if ("01".equals(inparam.getString("CAMP_ON"))) {
            inparam.put("P_SEQ", result.getData(0).getString("P_SEQ"));
        }
        //登记或解除预占表
        regCampOn(inparam);
        return result;
    }
    
    /**
     * 5.4  证件预占状态变更（idCampOn）  //目标方案
     * 各省可通过此接口查询一个客户证件目前全国已开户的号码情况，省端系统开户时录入信息自动向平台传输证件类型及证件号码申请预占，平台向省公司返回预占结果。
     * @param：inparam
     * @author: ouyang
     */
    public IDataset releaseCampOnIBOSS(IData inparam) throws Exception
    {

        IDataUtil.chkParam(inparam, "CUSTOMER_NAME");
        IDataUtil.chkParam(inparam, "ID_CARD_TYPE");
        IDataUtil.chkParam(inparam, "ID_CARD_NUM");
        IDataUtil.chkParam(inparam, "CAMP_ON");

        //解除预占
        if ("02".equals(inparam.getString("CAMP_ON"))) {
            IDataUtil.chkParam(inparam, "P_SEQ");
        }

        inparam.put("KIND_ID", "idCampOn_QWCX_0_0");
        inparam = callIBossTrans(inparam);
        IDataset result = IBossCall.callHttpIBOSS4("IBOSS", inparam);

        if (IDataUtil.isEmpty(result)) {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0".equals(result.getData(0).getString("X_RESULTCODE"))) {
            if ("2998".equals(result.getData(0).getString("X_RESULTCODE"))) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RESULTINFO"));
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验【全网一证多号】预占情况出现异常，请联系系统管理员！");
            }
        }


        inparam.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        //如果没有传入之前的开始时间，补充当前时间避免出错
        
        inparam.put("END_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        inparam.put("STATE","5");//解除预占成功
        Dao.update("TF_F_OPENLIMIT_CAMPON_IBOSS", inparam, new String[] { "P_SEQ" }, new String[] { inparam.getString("P_SEQ", "").trim() } ,Route.CONN_CRM_CEN);

        return result;
    }    

    /**
     * 5.6用户信息变更同步（userInfoSyn）  //目标方案
     * 用户信息变更同步是由省BOSS发起的，省BOSS通过该接口实现向全网用户数据查询平台准实时同步用户信息变更。
     * @param
     * @return
     * @throws Exception
     */
    public IDataset userInfoSyn(IData inparam) throws Exception
    {

        IDataset result = new DatasetList();

        //补充交易包流水号
        if (StringUtils.isEmpty(inparam.getString("PKG_SEQ", ""))) {
            String date = SysDateMgr.getSysDateYYYYMMDD();
            String seqRealId = SeqMgr.getRealId();
            inparam.put("PKG_SEQ", "898" + date + seqRealId);
        }

        //USER_DATASET 所有用户数据
        IDataUtil.chkParam(inparam, "USER_DATASET");
        IDataset userDataset = inparam.getDataset("USER_DATASET");
        IDataset userDatasetTrans = new DatasetList();
        for (int i = 0; i < userDataset.size(); i++) {
            IData userData = userDataset.getData(i);
            String homeProv = userData.getString("HOME_PROV","").trim();
            IDataUtil.chkParam(userData, "IDV");
            IDataUtil.chkParam(userData, "HOME_PROV");
            if(userData.getString("HOME_PROV","").trim().length()>3){
                userData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
            }
            IDataUtil.chkParam(userData, "CUSTOMER_NAME");
            IDataUtil.chkParam(userData, "ID_CARD_TYPE");
            IDataUtil.chkParam(userData, "ID_CARD_NUM");
            IDataUtil.chkParam(userData, "OPR");
            IDataUtil.chkParam(userData, "EFFETI_TIME");
            IDataUtil.chkParam(userData, "OPR_TIME");
            //增加需要凭证
            if ("01".equals(userData.getString("OPR"))) {
                IDataUtil.chkParam(userData, "P_SEQ");
            }
            IDataUtil.chkParam(userData, "CHECKING_DATE");

            //补充操作流水号
            userData = addSeq(userData);
            userDatasetTrans.add(userData);

            inparam.put("USER_DATASET", userDatasetTrans);
            inparam.put("REC_NUM", userDatasetTrans.size());

            IDataUtil.chkParam(inparam, "USER_DATASET");
            IDataUtil.chkParam(inparam, "REC_NUM");

            inparam.put("KIND_ID", "userInfoSyn_QWCX_0_0");
            inparam = callIBossTrans(inparam);
            result = IBossCall.callHttpIBOSS4("IBOSS", inparam);

            if (IDataUtil.isEmpty(result)) {
                CSAppException.apperr(IBossException.CRM_IBOSS_6);
            }

            if (!"0".equals(result.getData(0).getString("X_RESULTCODE"))) {
                if ("2998".equals(result.getData(0).getString("X_RESULTCODE"))) {
                   CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RESULTINFO"));
                } else {
                   CSAppException.apperr(CrmCommException.CRM_COMM_103, "【全网一证多号】用户信息同步出现异常，请联系系统管理员！");
                }
            }            
            
            //只有增加号码需要预占，同步后才需要修改预占表
            if ("01".equals(userData.getString("OPR"))) {
                userData.put("CAMP_ON", "02");
                regCampOn(userData);
            }
        }

        return result;

    }

    /**
     * @Function: getChannelId
     * @Description: 各省的渠道标识，遵照全网渠道统一编码规则
     * @param: @param
     * @author: ouyang
     */
    public IDataset getChannelId(IData inparam) throws Exception
    {
        //获取各省的渠道标识，遵照全网渠道统一编码规则
        IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(getVisit().getDepartId());
        if (IDataUtil.isEmpty(chnlInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取全网渠道编码失败");
        }
        return chnlInfos;
    }

    /**
     * @Function: checkPspt
     * @Description: 发平台证件类型，没有配置则不调用平台
     * @param: @param data
     * @author: ouyang
     */
    public IDataset checkPspt(String psptTypeCode) throws Exception
    {
        return CommparaInfoQry.getCommparaAllCol("CSM", "2553", psptTypeCode, "ZZZZ");
    }

    /**
     * @Function: addChannelId
     * @Description: 补充渠道编码
     * @param: @param data
     * @author: ouyang
     */
    public IData addChannelId(IData data) throws Exception
    {
        //补充渠道编码
        if (StringUtils.isEmpty(data.getString("CHANNEL_ID", ""))) {
            IDataset channelIdInfos = getChannelId(data);
            if (IDataUtil.isNotEmpty(channelIdInfos)) {
                data.put("CHANNEL_ID", channelIdInfos.getData(0).getString("GLOBAL_CHNL_ID"));
            }
        }
        return data;
    }

    /**
     * @Function: addSeq
     * @Description: 补充操作流水号
     * @param: @param data
     * @author: ouyang
     */
    public IData addSeq(IData data) throws Exception
    {
        //补充操作流水号
        if (StringUtils.isEmpty(data.getString("SEQ", ""))) {
            String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
            String seqRealId = SeqMgr.getRealId();
            data.put("SEQ", "898" + date + seqRealId);
        }
        return data;
    }

    /**
     * @Function: addMsOpCode
     * @Description: 补充操作员工号
     * @param: @param data
     * @author: ouyang
     */
    public IData addMsOpCode(IData data) throws Exception
    {
        //补充操作员工号
        if (StringUtils.isEmpty(data.getString("MS_OPCODE", ""))) {
            data.put("MS_OPCODE", CSBizBean.getVisit().getStaffId());
        }
        return data;
    }

    /*    //模糊化数据
        private IDataset fuzzyData(IDataset dataset) {
            
            IDataset results = new DatasetList();
            if(IDataUtil.isNotEmpty(dataset)){
                for(int i=0; i<dataset.size(); i++){
                   IData data = dataset.getData(i);
                   if(StringUtils.isNotBlank(data.getString("CUSTOMER_NAME",""))){
                       data.put("CUSTOMER_NAME", fuzzyCustName(data.getString("CUSTOMER_NAME","")));
                   }
                   if(StringUtils.isNotBlank(data.getString("IDV",""))){
                       data.put("IDV", fuzzySerialNumber(data.getString("IDV","")));
                   }
                   results.add(data);
                }
            }

            return results;
        }*/

    //模糊化客户姓名
    /*     private String fuzzyCustName(String custName) {
            if (custName == null) {
                return custName;
            }
            if (custName.length() == 2) {

                return custName.substring(0, 1) + "*";

            } else if (custName.length() > 2) {
                return custName.substring(0, 1) + "**" + custName.substring(3);
            }
            return custName;
         }
         
         //模糊化手机号码
         private String fuzzySerialNumber(String serialNumber) {
            if (serialNumber == null || serialNumber.length() != 11) {
                return serialNumber;
            }
            else {
                return serialNumber.substring(0, 7) + "****";
            }
         }*/

    /**
     * @Function: totalContractQry
     * @Description: 一证五号全量信息查询
     * @param: @param data
     * @author: ouyang
     */
    public IDataset totalContractQry(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");

        return Dao.qryByCodeParser("TF_F_OPENLIMIT_TOTALCONTRACT", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
    }

    /**
      * @Function: regOpenLimitCheck
      * @Description: 登记一证多号对账表
      * @param: @param data
      * @author: ouyang
    */
    public void regOpenLimitCheck(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SEQ");
        IDataUtil.chkParam(data, "OPR_TIME");
        IDataUtil.chkParam(data, "CHECKING_DATE");
        IDataUtil.chkParam(data, "HOME_PROV");
        if(data.getString("HOME_PROV","").trim().length()>3){
            String homeProv  = data.getString("HOME_PROV","").trim();
            data.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
        }
        IDataUtil.chkParam(data, "OPR");
        IDataUtil.chkParam(data, "CUSTOMER_NAME");
        IDataUtil.chkParam(data, "ID_VALUE");
        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");
        IDataUtil.chkParam(data, "EFFETI_TIME");
        Dao.insert("TF_F_OPENLIMIT_CHECK", data, Route.CONN_CRM_CEN);
    }

    /**
     * 
     * 写入IBOSS扫描操作的预占表  TF_F_OPENLIMIT_CAMPON_IBOSS
     * 
     */
    public void regCampOnIBOSS(IData data) throws Exception
    {
        //不良信息校验开关
        String unTrustCheck = "0";
        IDataset configInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "CHECK_NATION_OPENUM");
        if (IDataUtil.isNotEmpty(configInfo)) {
            unTrustCheck = configInfo.getData(0).getString("PARA_CODE5", "").trim();//是否进行验证, 返回1为验证, 其他不验证
        }
        data.put("RSRV_TAG3", unTrustCheck);
        IDataUtil.chkParam(data, "SEQ");
        IDataUtil.chkParam(data, "CUSTOMER_NAME");
        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");
        IDataUtil.chkParam(data, "ID_VALUE");
        //
//        if("10".equals(data.getString("TRADE_TYPE_CODE", ""))||"60".equals(data.getString("TRADE_TYPE_CODE", ""))){
//        	 //0是预占
//        	 data.put("STATE", "0");
//        }else{
//        	 data.put("STATE", "1");
//        }
        if("310".equals(data.getString("TRADE_TYPE_CODE", ""))){
        	//保留原来的复机业务
        	data.put("STATE", "0");
        }
       
        data.put("INSERT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        data.put("CAMP_ON", "01");
        data.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        //data.put("START_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        //结束时间为开始时间后三十分钟，1800秒
        //data.put("END_TIME", SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(SysDateMgr.getSysTime(), "yyyy-MM-dd HH:mm:ss"), 1800), "yyyy-MM-dd HH:mm:ss"));
        Dao.insert("TF_F_OPENLIMIT_CAMPON_IBOSS", data,Route.CONN_CRM_CEN);
        
    }
    
    /**
      * @Function: regCampOn
      * @Description: 登记预占表
      * @param: @param data
      * @author: ouyang
    */
    public void regCampOn(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SEQ");
        IDataUtil.chkParam(data, "CUSTOMER_NAME");
        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");
        IDataUtil.chkParam(data, "CAMP_ON");

        IDataUtil.chkParam(data, "P_SEQ");
        data.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        
        //预占
        if ("01".equals(data.getString("CAMP_ON"))) {
            data.put("START_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            //结束时间为开始时间后三十分钟，1800秒
            data.put("END_TIME", SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(SysDateMgr.getSysTime(), "yyyy-MM-dd HH:mm:ss"), 1800), "yyyy-MM-dd HH:mm:ss"));
            Dao.insert("TF_F_OPENLIMIT_CAMPON", data);
        } else {
            //如果没有传入之前的开始时间，补充当前时间避免出错
            data.put("START_TIME", data.getString("START_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
            data.put("END_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            Dao.update("TF_F_OPENLIMIT_CAMPON", data,new String[]{"P_SEQ"},new String[]{data.getString("P_SEQ", "").trim()});
        }
    }

    /**
      * @Function: selCampOn
      * @Description: 查询预占表
      * @param: @param data
      * @author: ouyang
    */
    public IDataset selCampOn(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");

        return Dao.qryByCodeParser("TF_F_OPENLIMIT_CAMPON", "SEL_BY_PK", data);
    }

/*    *//**
     * 一证五号违规信息查询
     * 对当天办理业务客户，在全网用户数据查询平台查询一证五号信息，对超过一证五号客户，展示其业务内容及数量。
     * @param param
     * @return
     * @throws Exception
     *//*
    public IDataset queryViolationNum(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "ID_CARD_TYPE");
        IDataUtil.chkParam(inparam, "ID_CARD_NUM");
        IDataset result = new DatasetList();
        result = Dao.qryByCodeParser("TF_F_OPENLIMIT_DAILYCONTRACT", "SEL_BY_CARDNUM", inparam, Route.CONN_CRM_CEN);
        return result;
    }*/

    /**
     * @Function: callIBossTrans
     * @Description: 转换字段
     * @param: @param data
     * @author: ouyang
     */
    public IData callIBossTrans(IData data) throws Exception
    {
        if (StringUtils.isEmpty(data.getString("IDCARD_TYPE", ""))) {
            data.put("IDCARD_TYPE", data.getString("ID_CARD_TYPE", ""));
        }
        if (StringUtils.isEmpty(data.getString("IDCARD_NUM", ""))) {
            data.put("IDCARD_NUM", data.getString("ID_CARD_NUM", ""));
        }
        return data;
    }

	  /**
	   * 一证五号违规信息查询
	   * 对当天办理业务客户，在全网用户数据查询平台查询一证五号信息，对超过一证五号客户，展示其业务内容及数量。
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public IDataset queryViolationNum(IData inparam) throws Exception
	 {	 	
		IDataUtil.chkParam(inparam, "ID_CARD_TYPE");
	 	IDataUtil.chkParam(inparam, "ID_CARD_NUM");	 	
	 	IDataset result = new DatasetList();
	 	result = Dao.qryByCodeParser("TF_F_OPENLIMIT_DAILYCONTRACT", "SEL_BY_CARDNUM", inparam,Route.CONN_CRM_CEN);		 			 		
		return result;
	 	
	 }
	  /**
	   * 查询预占记录
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public IDataset queryCampOnList(IData inparam) throws Exception
	 {	 	
		IDataUtil.chkParam(inparam, "ID_CARD_TYPE");
	 	IDataUtil.chkParam(inparam, "ID_CARD_NUM");	 
	 	inparam.put("CAMP_ON", "01");
	 	IDataset result = new DatasetList();
	 	result = queryAlreadyCampOn(inparam);		 			 		
		return result;
	 	
	 }
	 
		/**
	  * @Function: queryAlreadyCampOn
	  * @Description: 查询有效的预占记录
	  * @param: @param data
	  * @author: chenff5
	*/
	public IDataset queryAlreadyCampOn(IData data) throws Exception {
		return Dao.qryByCodeParser("TF_F_OPENLIMIT_CAMPON_IBOSS", "SEL_CAMPONON_BY_CARDNUM", data,Route.CONN_CRM_CEN);                     
	}
	
	  /**
	   	解除预占
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public IDataset releaseCampOn(IData inparam) throws Exception{
		int a = 0;
		IDataset dataset = inparam.getDataset("LIST");
		for (int i = 0; i < dataset.size(); i++) {
			IData da = dataset.getData(i);
			da.put("CAMP_ON", "02");
			da.put("ID_CARD_TYPE",inparam.getString("ID_CARD_TYPE","").trim());
            //证件类型转换
            IDataset checkPsptResults = checkPspt(inparam.getString("ID_CARD_TYPE","").trim());
            if (IDataUtil.isNotEmpty(checkPsptResults)) {        
                da.put("ID_CARD_TYPE", checkPsptResults.getData(0).getString("PARA_CODE1"));
            }
			//idCampOn(da);
            releaseCampOnIBOSS(da);
			a++;
		}
		IData r = new DataMap();
		r.put("RESULT_NUM", a);
		IDataset res = queryCampOnList(inparam);
		res.add(r);
		return res;
		
	}
	 
    public IDataset idCheckAndCampOn(IData param) throws Exception
    {
        String custName = param.getString("CUST_NAME", "");
        String psptTypeCode = param.getString("PSPT_TYPE_CODE", "");
        String psptId = param.getString("PSPT_ID", "");
        IDataset idCampOn = new DatasetList();
        //是否免验证一证五号平台
        boolean isIgnoreCall = isIgnoreCall();

        IData compara = new DataMap();
        compara.put("SUBSYS_CODE", "CSM");
        compara.put("PARAM_ATTR", "2552");
        compara.put("PARAM_CODE", psptTypeCode);
        compara.put("EPARCHY_CODE", "ZZZZ");
        IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));
        //证件类型转换
        IDataset checkPsptResults = checkPspt(psptTypeCode);
        if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult) && IDataUtil.isNotEmpty(checkPsptResults)) {

            //先调用查验接口
            IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.checkGlobalMorePsptId", param);
            
            if (DataSetUtils.isNotBlank(ds)) {
                IData checkData = ds.getData(0);
                if (checkData.getString("CODE", "").equals("0") || checkData.getString("CODE", "").equals("2")) {
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, checkData.getString("MSG", ""));
                }
            }

            IData input = new DataMap();
            //预占
            input.put("CUSTOMER_NAME", custName);
            input.put("ID_CARD_TYPE", psptTypeCode);
            input.put("ID_CARD_NUM", psptId);
            input.put("CAMP_ON", "01");//预占
            input.put("ID_CARD_TYPE", checkPsptResults.getData(0).getString("PARA_CODE1"));
            idCampOn = idCampOn(input);

        }

        return idCampOn;
    }

    /*       private IDataset resolveIBossData(IDataset iBossResults) throws Exception {
               IDataset results = new DatasetList();
               IData result = iBossResults.getData(0);
               int number = result.getInt("TOTAL");
               if(0==number){
                   return iBossResults;
               }
               
               //判断员工是否有敏感信息免模糊话权限
               boolean fuzzy = !StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_CRM_NOFUZZY_SN_QWCX");
               String serialNumber = result.getString("IDV");
               String homeProv = result.getString("HOME_PROV");
               String effetiItme = result.getString("EFFETI_TIME");
               
               if (serialNumber.startsWith("[")){
                       JSONArray serialNumbers = JSONArray.fromObject(serialNumber);
                       JSONArray homeProvs = JSONArray.fromObject(homeProv);
                       JSONArray effetiItmes = JSONArray.fromObject(effetiItme);
                       if (serialNumbers != null && serialNumbers.size() > 0){
                           int size = serialNumbers.size();
                           for (int i = 0; i < size; i++){
                               if(fuzzy){
                                   IData resultData = new DataMap();
                                   resultData.put("IDV", fuzzySerialNumber(serialNumbers.get(i).toString()));
                                   resultData.put("HOME_PROV", homeProvs.get(i).toString());
                                   resultData.put("EFFETI_TIME", dealTime(effetiItmes.get(i).toString()));
                                   results.add(resultData);
                               }else{
                                   IData resultData = new DataMap();
                                   resultData.put("IDV", serialNumbers.get(i).toString());
                                   resultData.put("HOME_PROV", homeProvs.get(i).toString());
                                   resultData.put("EFFETI_TIME", dealTime(effetiItmes.get(i).toString()));
                                   results.add(resultData);
                               } 
                           }
                       }
                }else{
                    if(fuzzy){
                       IData resultData = new DataMap();
                       resultData.put("IDV", fuzzySerialNumber(serialNumber));
                       resultData.put("HOME_PROV", homeProv);
                       resultData.put("EFFETI_TIME", dealTime(effetiItme));
                       results.add(resultData);
                   }else{
                       IData resultData = new DataMap();
                       resultData.put("IDV", serialNumber);
                       resultData.put("HOME_PROV", homeProv);
                       resultData.put("EFFETI_TIME", dealTime(effetiItme));
                       results.add(resultData);
                   }                      
                }
        
               return results;
           }  */

    /*         private String dealTime(String effetiItme) {
                StringBuilder sb = new StringBuilder();
                sb = sb.append(effetiItme.substring(0,4)).append("-").append(effetiItme.substring(4,6)).append("-").append(effetiItme.substring(6,8))
                        .append("-").append((effetiItme.substring(8,effetiItme.length())));
                return sb.toString();
            }*/
    /**
     * REQ201705210001_全国一证五号考核相关优化
     * <br/>
     * 品牌为 G001、G002、G010、G005 并用户在other表中不存在 HYYYKBATCHOPEN 
     * 返回true,
     * 则返回false
     * @author zhuoyingzhi
     * @date 2070609
     */
    public boolean isCheckBrandAndHYYYKBATCHOPEN(IData param) throws Exception{
    	//获取用户品牌编码
    	 String  brandCode=param.getString("BRAND_CODE", "");
    	 //获取用户userid
    	 String userId=param.getString("USER_ID", "");
    	 IDataset  brandInfo=CommparaInfoQry.getCommparaByAttrCode1("CSM", "1769", brandCode, "ZZZZ",null);
    	 log.debug("isCheckBrandAndHYYYKBATCHOPEN--brandInfo:"+brandInfo+",brandCode:"+brandCode);
    	 if(IDataUtil.isNotEmpty(brandInfo)){
    		 IData  paramOther= new DataMap();
    		 paramOther.put("USER_ID", userId);
    		 paramOther.put("RSRV_VALUE_CODE", "HYYYKBATCHOPEN");
    		 //code_code 在生产上已经存在了
    		IDataset  otherList= Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID", paramOther);
       	    log.debug("isCheckBrandAndHYYYKBATCHOPEN--otherList:"+otherList+",paramOther:"+paramOther);

    		if(IDataUtil.isEmpty(otherList)){
    			return true;
    		}
    	 }
    	return false;
    }
    /**
     * 判断新增时  证件类型、证件号码  是否修改过
     * @param mainTrade
     * @param newCustName
     * @param newPsptTypeCode
     * @param newPsptId
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170701
     */
    public  boolean isCheckOpenLimitAddCustInfo(IData mainTrade,String newPsptTypeCode,String newPsptId)throws Exception{
    	//证件类型、证件号码   	
    	//老的客户信息
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(mainTrade.getString("CUST_ID"));
    	if(IDataUtil.isNotEmpty(custInfo)){
    		String  oldPsptTypeCode=custInfo.getString("PSPT_TYPE_CODE", "").trim();
    		String  oldPsptId=custInfo.getString("PSPT_ID", "");
    		if(!oldPsptTypeCode.equals(newPsptTypeCode) ||!oldPsptId.equals(newPsptId)){
    			//证件类型、证件号码    信息有修改过
    			return true;
    		}
    	}
       return false;
    }
    
    /**
     * 判断修改时  客户姓名是否修改过
     * @param mainTrade
     * @param newCustName
     * @param newPsptTypeCode
     * @param newPsptId
     * @return
     * @throws Exception
     * @author wangsc10
     * @date 20181218
     */
    public  boolean isCheckOpenLimitModifyCustInfo(IData mainTrade,String newCustName)throws Exception{
    	//客户姓名
    	//老的客户信息
    	IData custInfo = UcaInfoQry.qryCustInfoByCustId(mainTrade.getString("CUST_ID"));
        log.debug("=============custInfo="+custInfo+",newCustName:"+newCustName);
    	if(IDataUtil.isNotEmpty(custInfo)){
    		String  oldCustName=custInfo.getString("CUST_NAME", "");
    		if(!oldCustName.equals(newCustName)){
    			//客户姓名    信息有修改过
    			return true;
    		}
    	}
       return false;
    }
    
    /**
     * 判断删除时  证件类型、证件号码  是否修改过
     * @param mainTrade
     * @param newCustName
     * @param newPsptTypeCode
     * @param newPsptId
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170701
     */
    public  boolean isCheckOpenLimitDeleteCustInfo(IData mainTrade,String oldPsptTypeCode,String oldPsptId)throws Exception{
    	//证件类型、证件号码 
    	//新客户信息
        IDataset custPerTradeInfo = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(mainTrade.getString("TRADE_ID"));
        if(IDataUtil.isNotEmpty(custPerTradeInfo)){
            String newPsptTypeCode = custPerTradeInfo.getData(0).getString("PSPT_TYPE_CODE", "").trim();
            String newPsptId = custPerTradeInfo.getData(0).getString("PSPT_ID");
			if (!oldPsptTypeCode.equals(newPsptTypeCode) || !oldPsptId.equals(newPsptId)) {
				// 证件类型、证件号码  信息有    修改过
				return true;
			}
        }
       return false;
    } 
    
    /**
	 * 全角变半角shenhai
	 * @param str
	 * @return
	 */
   	public  String ToDBC(String str) throws Exception{
		char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
          if (c[i] == '\u3000') {
            c[i] = ' ';
          } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
            c[i] = (char) (c[i] - 65248);

          }
        }
        String returnString = new String(c);
   
        return returnString;
	}   
}
