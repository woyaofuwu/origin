package com.asiainfo.veris.crm.order.soa.person.busi.onekeystopmobile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.OneKeyStopMobileException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;


public class OneKeyStopMobileBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(OneKeyStopMobileBean.class);

    /**
     * 查询客户姓名及身份证
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryCustInfo(IData input) throws Exception{
        String sn = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        IDataset outset = new DatasetList();
        if(IDataUtil.isEmpty(userInfo)){
            CSAppException.apperr(OneKeyStopMobileException.CRM_CHANGEPHONE_1, sn);
        }
        String custId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(custId);
        if(IDataUtil.isEmpty(custInfo)){
            CSAppException.apperr(OneKeyStopMobileException.CRM_CHANGEPHONE_2, sn);
        }
        outset.add(custInfo);
        return outset;       
    }
    
    /**
     * 查询用户信息
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryUserInfo(IData input) throws Exception{
        String sn = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        IDataset outset = new DatasetList();
        if(IDataUtil.isEmpty(userInfo)){
            CSAppException.apperr(OneKeyStopMobileException.CRM_CHANGEPHONE_1, sn);
        }
        outset.add(userInfo);
        return outset;       
    }
    
    /**
     * 查询最后一条台账信息
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryLastTrade(IData input) throws Exception{
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IData usermap = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        param.put("USER_ID", usermap.getString("USER_ID"));
        param.put("CANCEL_TAG", "0");
        //add by liangdg3 at 20191019 for REQ201908120013一键停机界面优化需求
        //最后一次业务办理（半年内）认证只展示办理记录测试 过滤2101记录 新增sql语句
        IDataset lastTrade = Dao.qryByCode("TF_BH_TRADE", "SEL_LASTTRADE_BY_SERIAL_NUMBER_EXCLUDE_QUERY", param, Route.getJourDb(BizRoute.getRouteId()));
        //add by liangdg3 at 20191019 for REQ201908120013一键停机界面优化需求
        return lastTrade;
    }
    /**
     * 查询亲亲网号码
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryUUInfos(IData input) throws Exception{
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_B", input.getString("SERIAL_NUMBER"));
        param.put("RELATION_TYPE_CODE","45");
        IDataset UUInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_SB_AND_DATE_BY_SB", param);
        return UUInfos;       
    }
    
    /**
     * 查询近六个月缴费信息
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryPayLog(IData input) throws Exception{
        IData param = new DataMap();
        String sn = input.getString("SERIAL_NUMBER");
        Date BeginTime = new Date();
        Date EndTime = new Date();
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(BeginTime);
        calendar.add(Calendar.MONTH, -6);
        BeginTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String BEGIN_TIME = sdf.format(BeginTime);
        String END_TIME = sdf.format(EndTime);
        param.put("SERIAL_NUMBER", sn);
        param.put("BEGIN_TIME", BEGIN_TIME);
        param.put("END_TIME", END_TIME);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryPayLog", param, true);
        if(logger.isDebugEnabled()){
            logger.debug("queryPayLog output="+output);
        }
        IDataset PayLog = output.getData();
        if(IDataUtil.isNotEmpty(PayLog)){
            for (int i = 0; i < PayLog.size(); i++)
            {
                if("1".equals(PayLog.getData(i).getString("CANCEL_TAG"))){
                    PayLog.remove(i);
                }
            }
        }
        if(logger.isDebugEnabled()){
            logger.debug("queryPayLog PayLog="+PayLog);
        }
        return PayLog;
    }
    
    /**
     * 查询近三个月消费记录
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryMonFeeInfos(IData input) throws Exception{
        IData param = new DataMap();
        String sn = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if(IDataUtil.isEmpty(userInfo)){
            CSAppException.apperr(OneKeyStopMobileException.CRM_CHANGEPHONE_1, sn);
        }
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
        if(IDataUtil.isEmpty(acctInfo)){
            CSAppException.apperr(OneKeyStopMobileException.CRM_CHANGEPHONE_3, sn);
        }
        String acct_id = acctInfo.getString("ACCT_ID");
        Date StartDate = new Date();
        Date EndDate = new Date();
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(StartDate);
        calendar.add(Calendar.MONTH, -2);
        StartDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String START_CYCLE_ID = sdf.format(StartDate);
        String END_CYCLE_ID = sdf.format(EndDate);
        param.put("ACCT_ID", acct_id);
        param.put("START_CYCLE_ID", START_CYCLE_ID);
        param.put("END_CYCLE_ID", END_CYCLE_ID);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryMonFee", param, true);
        if(logger.isDebugEnabled()){
            logger.debug("queryMonFeeInfos output="+output);
        }
        IDataset MonFeeInfos = output.getData();
        if(logger.isDebugEnabled()){
            logger.debug("queryMonFeeInfos MonFeeInfos="+MonFeeInfos);
        }
        return MonFeeInfos;
    }
    
    /**
     * 查询并验证通话记录
     * 
     * @param input
     * @throws Exception
     */
    public IDataset queryCdrBil(IData input) throws Exception{
        IData param = new DataMap();
        String sn = input.getString("SERIAL_NUMBER");
        String sn1 = input.getString("SERIAL_NUMBER1");
        String sn2 = input.getString("SERIAL_NUMBER2");
        String sn3 = input.getString("SERIAL_NUMBER3");
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        try
        {
            //add by liangdg3 at 20191021 for REQ201908120013 一键停机界面优化需求 start
            //通话记录验证按集团的要求，主被叫均可纳入校验。（最近1个月内通话次数较多的3-5条通话记录，当前仅能校验主叫）
            String endTime= SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
            String beginTime=SysDateMgr.addMonths(endTime,-1)+SysDateMgr.START_DATE_FOREVER;
            //测试环境写死时间
//            IData res = AcctCall.qryVoiceRecord(sn, "2019-07-01 00:00:00", "2019-07-31 24:00:00", "0");
            logger.info(sn+"生产环境查询时间:开始时间"+beginTime+"结束时间"+endTime);
//            logger.info(sn+"测试环境查询时间:开始时间2019-07-01 00:00:00结束时间2019-07-31 24:00:00");
            IData res = AcctCall.qryVoiceRecord(sn, beginTime, endTime, "0");
            logger.info(sn+"查询通话记录:"+res);
            if(IDataUtil.isNotEmpty(res)&&"0".equals(res.getString("rtnCode"))){
                IDataset resultList=res.getData("object").getDataset("result");
                IDataset voiceRecList=new DatasetList();
                if(IDataUtil.isNotEmpty(resultList)){
                    voiceRecList=(IDataset)resultList.get(0,"voiceRecList");
                }
                if(IDataUtil.isNotEmpty(voiceRecList)){
                    //判断是否为通话记录次数较多的前5条的记录
                    for (int i = 0; i < voiceRecList.size(); i++) {
                        IData voiceRec = (IData) voiceRecList.get(i);
                        String svcNum = voiceRec.getString("svcNum");
                        int callNum=voiceRec.getInt("callNum");
                        if(sn1.equals(svcNum)){
                            flag1=true;
                        }
                        if(sn2.equals(svcNum)){
                            flag2=true;
                        }
                        if(sn3.equals(svcNum)){
                            flag3=true;
                        }
                        if(flag1 == true && flag2 == true && flag3 == true){
                            break;
                        }
                        if(i>=5){
                            IData voiceRecBefore = (IData) voiceRecList.get(i-1);
                            int callNumBefore=voiceRecBefore.getInt("callNum");
                            if (callNumBefore!=callNum){
                                break;
                            }
                        }
                    }
                }
            }else if(IDataUtil.isNotEmpty(res)&&"-9999".equals(res.getString("rtnCode"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调账务接口报错AM_ITF_QueryVoiceRecordByTime:"+res.getData("object").getString("respDesc"));
            }
            //add by liangdg3 at 20191021 for REQ201908120013 一键停机界面优化需求 end
//            param.put("SERIAL_NUMBER", sn);
//            param.put("CALLED_NUMBERS", sn1);
//            IData result = AcctCall.getCheckVoiceRecordInfo(param).getData(0);
//            String resultinfo = result.getString("RETURN_CODE");
//            if ("0000".equals(resultinfo))
//            {
//                String checkResult = result.getString("CHECK_RESULT");
//                //0不存在，1存在    {0|0}
//                if (checkResult.contains("0"))
//                {
//                    flag1 = false;
//                }
//                else
//                {
//                    //存在
//                    flag1 = true;
//                }
//            }
//            param.put("CALLED_NUMBERS", sn2);
//            result = AcctCall.getCheckVoiceRecordInfo(param).getData(0);
//            resultinfo = result.getString("RETURN_CODE");
//            if ("0000".equals(resultinfo))
//            {
//                String checkResult = result.getString("CHECK_RESULT");
//                //0不存在，1存在    {0|0}
//                if (checkResult.contains("0"))
//                {
//                    flag2 = false;
//                }
//                else
//                {
//                    //存在
//                    flag2 = true;
//                }
//            }
//            param.put("CALLED_NUMBERS", sn3);
//            result = AcctCall.getCheckVoiceRecordInfo(param).getData(0);
//            resultinfo = result.getString("RETURN_CODE");
//            if ("0000".equals(resultinfo))
//            {
//                String checkResult = result.getString("CHECK_RESULT");
//                //0不存在，1存在    {0|0}
//                if (checkResult.contains("0"))
//                {
//                    flag3 = false;
//                }
//                else
//                {
//                    //存在
//                    flag3 = true;
//                }
//            }
            IDataset outData = new DatasetList();
            IData data = new DataMap();
            if (flag1 == true && flag2 == true && flag3 == true)
            {
                data.put("RESULT_CODE", "0000");
            }
            else
            {
                data.put("RESULT_CODE", "1111");
            }
            outData.add(data);
            if (logger.isDebugEnabled())
            {
                logger.debug("queryCdrBil outData=" + outData);
            }
            return outData;
        }
        catch (Exception e)
        {
            throw e;
        }
    }
    
    /**
     * 校验入网时间
     * 
     * @param input
     * @throws Exception
     */
    public IDataset checkOpenDate(IData input) throws Exception{
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_OPEN_DATE_BY_SERIAL_NUMBER", param);
        IDataset outData = new DatasetList();
        IData data = new DataMap();
        if(IDataUtil.isNotEmpty(dataset)){
            String openData = input.getString("OPEN_DATE");
            String patternTagYYMM= input.getString("PATTERN_TAG_YYMM","");
            String qryOpenDate = dataset.getData(0).getString("OPEN_DATE");
            if("1".equals(patternTagYYMM)&&qryOpenDate.length()>8){
                //qryOpenDate格式yyyy-MM-dd
                qryOpenDate=qryOpenDate.substring(0,7);
            }
            if(openData.equals(qryOpenDate)){
                data.put("RESULT_CODE", "0000");
                outData.add(data);
            } else{
                data.put("RESULT_CODE", "1111");
                outData.add(data);
            }
        } else{
            data.put("RESULT_CODE", "1111");
            outData.add(data);
        }
        return outData;       
    }
}
