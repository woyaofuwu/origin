
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MofficeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.PreTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

public class ChangePhonePreRegisterBean extends CSBizBean
{
    protected static Logger logger = Logger.getLogger(ChangePhonePreRegisterBean.class);

    private static boolean updateStatus(IData indata, IData param) throws Exception
    {
        // TODO Auto-generated method stub
        DBConnection conn = null;
        PreparedStatement stmt = null;
        String errorInfo = "";

        IData statusUpdate = new DataMap();
        statusUpdate.put("SERIAL_NUMBER", indata.getString("RSRV_STR1", ""));// 激活发起方取新号码
        if (!"02".equals(param.getString("OPR_CODE")))
        {
            statusUpdate.put("FROM_STATUS", "0");
            statusUpdate.put("TO_STATUS", "9");
        }
        else
        {
            statusUpdate.put("FROM_STATUS", "2");
            statusUpdate.put("TO_STATUS", "1");
        }
        try
        {
            conn = new DBConnection("crm1", true, false);
            StringBuilder parser = new StringBuilder();
            parser.append(" update TF_F_USER_ALTSN ");
            parser.append(" SET status = ? ");
            parser.append(" where serial_number= ? ");
            parser.append(" and status = ?");

            stmt = conn.prepareStatement(parser.toString());
            stmt.setString(1, statusUpdate.getString("FROM_STATUS", ""));
            stmt.setString(2, statusUpdate.getString("SERIAL_NUMBER", ""));
            stmt.setString(3, statusUpdate.getString("TO_STATUS", ""));

            stmt.executeUpdate();
            conn.commit();
        }
        catch (Exception e)
        {
            if (null != conn)
            {
                conn.rollback();
            }

            Utility.print(e);
            errorInfo = Utility.getBottomException(e).getMessage();
        }
        finally
        {
            if (null != stmt)
            {
                stmt.close();
            }

            if (null != conn)
            {
                conn.close();
            }
        }

//        if (!"".equals(param.getString("BIZ_ORDER_RSP_DESC", "")) && !"0000".equals(param.getString("BIZ_ORDER_RESULT", "")))
//        {
//            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2930, param.getString("BIZ_ORDER_RSP_DESC", ""));
//        }

        if (!"".equals(errorInfo))
        {
            CSAppException.apperr(ChangePhoneException.valueOf(errorInfo));
        }
        return true;
    }

    private void afterSynSnActivate(IData input, IData indata, IData resultMap, IData param) throws Exception
    {
        // TODO Auto-generated method stub
        if ("01".equals(param.getString("OPR_CODE", "")))
        {
            if (!"0000".equals(resultMap.getString("X_RSPCODE", ""))&&!"00".equals(resultMap.getString("X_RSPCODE", ""))&&!"0".equals(resultMap.getString("X_RSPCODE", "")))
            {
                String content = "您好，很抱歉，您的改号业务办理不成功，详情请垂询10086。中国移动";
                this.SendSms(param.getString("NEW_ID_VALUE", ""), content);
                CSAppException.apperr(ChangePhoneException.valueOf("很抱歉，您的改号业务办理出错:"+ resultMap.getString("X_RESULTINFO")));
            }
            IData params = new DataMap();
            params.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));

            params.put("OPEN_DATE", resultMap.getString("OPR_TIME", ""));

            // 在新号码方，更新改号信息表老号码归属省，老号码新分配的IMSI
            IData pAltsnUpdate = new DataMap();
            pAltsnUpdate.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));
            pAltsnUpdate.put("FROM_STATUS", "0");
            pAltsnUpdate.put("TO_STATUS", "1");
            pAltsnUpdate.put("RELA_TYPE", "1");
            pAltsnUpdate.put("RSRV_STR1", resultMap.getString("OLD_IMPI", ""));
            pAltsnUpdate.put("RSRV_STR2", resultMap.getString("OLD_IMSI", ""));
            pAltsnUpdate.put("RSRV_STR3", resultMap.getString("VOLTE_TYPE", ""));
            input.put("OLD_IMSI", resultMap.getString("OLD_IMSI", ""));
    		input.put("OLD_IMPI", resultMap.getString("OLD_IMPI", "")) ;
    		input.put("VOLTE_TYPE", resultMap.getString("VOLTE_TYPE", "")) ;
            
            UserAltsnInfoQry.updRsrvStr1InfoBySn(pAltsnUpdate);

            // 更新入网时间
            IDataset dsUser = UserAltsnInfoQry.queryUserAltsnBySnLarger(params);
            if (dsUser.size() > 0)
            {
                IData pUserUpdate = new DataMap();
                pUserUpdate.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));
                pUserUpdate.put("OPEN_DATE", resultMap.getString("OPR_TIME", ""));
                UserAltsnInfoQry.updAltOpenInfoBySn(pUserUpdate);
            }
            // 更新VIP等级 today
            IData new_old_vipinfo = tuxCustPutVip(param, resultMap.getString("LEVEL", ""));
            // 备份入网时间，VIP信息。
            IData pOhter = new DataMap();
            pOhter.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));
            pOhter.put("RSRV_VALUE_CODE", "ALT_SN_TAG");
            pOhter.put("RSRV_VALUE", "1");
            pOhter.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
            pOhter.put("INST_ID", SeqMgr.getInstId());
            pOhter.put("RSRV_STR1", param.getString("OLD_ID_VALUE", ""));
            pOhter.put("RSRV_STR2", resultMap.getString("OPR_TIME", ""));
            pOhter.put("RSRV_STR3", dsUser.size() > 0 ? dsUser.getData(0).getString("OPEN_DATE", "") : "");
            pOhter.put("RSRV_STR4", "");
            pOhter.put("RSRV_STR5", new_old_vipinfo.getString("CHANGED", ""));
            pOhter.put("RSRV_STR6", new_old_vipinfo.getString("OLD_LEVEL", ""));
            pOhter.put("RSRV_STR7", new_old_vipinfo.getString("NEW_LEVEL", ""));
            pOhter.put("RSRV_STR8", "");

            UserAltsnInfoQry.insAltOpenInfoBySn(pOhter);

            //更新TF_B_TRADE_ALTSN_PLATMRG表的激活时间
            IData pAltsnPlatUpdate = new DataMap () ;
            pAltsnPlatUpdate.put ( "SERIAL_NUMBER" , param.getString ( "NEW_ID_VALUE", "" )) ;
            pAltsnPlatUpdate.put ( "ACTIVATE_TIME" , SysDateMgr.getSysTime()) ;
            pAltsnPlatUpdate.put ( "RELA_TYPE"   ,   "1" ) ;
            UserAltsnInfoQry.updAltPlatmrgBySn(pAltsnPlatUpdate);
        
            indata.put("NEW_ID_VALUE", param.getString ( "NEW_ID_VALUE", "" ));
            indata.put("OLD_ID_VALUE", param.getString ( "OLD_ID_VALUE", "" ));
            
            //封装从boss传给CRM的账单串
            IData inacctData = new DataMap();
	        boolean isEmptyMoInfo = true;
            inacctData.put("OLD_ID_VALUE", param.getString ( "OLD_ID_VALUE", "" ));
            inacctData.put("NEW_ID_VALUE", param.getString ( "NEW_ID_VALUE", "" ));
            IDataset infoContList = resultMap.getDataset("INFO_CONT");
            for(int i=0;i<infoContList.size();i++){
                IData infoCont = infoContList.getData(i);
                if("4".equals(infoCont.getString("INFO_TYPE_ID"))){
                    IDataset moveinfos = new DatasetList();
                    inacctData.put("InfoTypeID", infoCont.getString("INFO_TYPE_ID"));
                    IDataset itemList = infoCont.getDataset("INFO_ITEMS");
                    for(int j=0;j<itemList.size();j++){
                        IData fromItem = itemList.getData(j);
                        IData toItem = new DataMap();
                        toItem.put("ItemID", fromItem.getString("ITEM_ID"));
                        toItem.put("ItemCont", fromItem.getString("ITEM_CONT"));
                        if("407".equals(fromItem.getString("ITEM_ID"))){
                            IDataset fromChildItems = fromItem.getDataset("CHILD_ITEMS");
                            if(IDataUtil.isNotEmpty(fromChildItems)){
                                IDataset toChildItems = new DatasetList();
                                for(int k=0;k<fromChildItems.size();k++){
                                    IData fromChildItem = fromChildItems.getData(k);
                                    IData toChildItem = new DataMap();
                                    toChildItem.put("ChildItemID", fromChildItem.getString("CHILD_ITEM_ID"));
                                    toChildItem.put("ChildItemCont", fromChildItem.getString("CHILD_ITEM_CONT"));
                                    toChildItems.add(toChildItem);
                                }
                                toItem.put("ChildItems", toChildItems);                             
                            }
                        }
                        moveinfos.add(toItem);
	        			isEmptyMoInfo = false;
                    }
                    inacctData.put("MoInfo", moveinfos);
                    break;
                }
            }           
	        if(!isEmptyMoInfo){	        	
	        	indata.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));
	        	IData userInfo = getLocalUserInfo(indata);
	        	dealAcct(inacctData,userInfo.getString("CUST_ID"),userInfo.getString("USER_ID"),"2"); 
	        }
        }
        else
        // 取消
        {
            if (!"0000".equals(resultMap.getString("X_RSPCODE", ""))&&!"00".equals(resultMap.getString("X_RSPCODE", ""))&&!"0".equals(resultMap.getString("X_RSPCODE", "")))
            {
                CSAppException.apperr(ChangePhoneException.valueOf("落地方报错信息:"+ resultMap.getString("X_RESULTINFO")));
            }
            IData pAltsnUpdate = new DataMap();
            pAltsnUpdate.put("SERIAL_NUMBER", param.getString("NEW_ID_VALUE", ""));
            pAltsnUpdate.put("FROM_STATUS", "2");
            pAltsnUpdate.put("TO_STATUS", "3");
            pAltsnUpdate.put("RELA_TYPE", "1");
            pAltsnUpdate.put("ALT_CANCEL_TIME", SysDateMgr.getSysTime());

            UserAltsnInfoQry.updAltsnBySn(pAltsnUpdate);

        }
    }

    /**
     * 落地方激活
     * 
     * @param input
     * @throws Exception
     */
    public IData altSnActivate(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IData in = new DataMap();
        IData BizRsp = new DataMap();
        IData param = new DataMap();
        IData inTrade = new DataMap();

        String opr_code = input.getString("OPR_CODE");
        String old_sn = input.getString("OLD_ID_VALUE");
        inTrade.put("OLD_SN", input.getString("OLD_ID_VALUE"));
        inTrade.put("NEW_SN", input.getString("NEW_ID_VALUE"));
        inTrade.put("CHANNEL", input.getString("CHANNEL"));
        inTrade.put("OPR_CODE", input.getString("OPR_CODE"));
        inTrade.put("HAND_CHARGE", input.getString("HAND_CHARGE"));
        inTrade.put("RESERVE", input.getString("RESERVE"));
        inTrade.put("RSRV_STR6", "END");

        IData userInfo = new DataMap();
        IData custInfo = new DataMap();
        IData billInfo = new DataMap();
        String actived_time = SysDateMgr.getSysTime().replace(":", "").replace("-", "").replace(" ", "");
        if (!"02".equals(opr_code))
        {
            inTrade.put("ACTIVED_TIME", actived_time);

            in.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));

            userInfo = getLocalUserInfo(in);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2919);
                // 没有此用户，或此用户已消户:"+inTrade.getString ( "OLD_SN", "" )
            }

            this.checkIsExistsGhTrade(old_sn, old_sn, "SYNACT");

        }
        else
        {
            IDataset altUserInfo = UserAltsnInfoQry.queryUserAltsnBySn(old_sn, "2", "1");

            if (IDataUtil.isEmpty(altUserInfo))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2933, old_sn);
                // 落地方没有找到有效有改号信息记录，不能取消!" + pAltsn.getString( "SERIAL_NUMBER" , "")
            }
        }

        // 基本校验
        if (!"02".equals(opr_code))
        {
            IData oldCust = this.checkConditon(param, input, old_sn, "OLD");

            if ("1".equals(input.getString("NOT_PASS", "")))
            {
                return oldCust;
            }

        }
        else
        {
        	in.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
            userInfo = getLocalUserInfo(in);
            custInfo = getCustInfo(userInfo);
        }

        //
        if (!"02".equals(opr_code))
        {

            in.put("SERIAL_NUMBER", inTrade.getString("OLD_SN", ""));
            in.put("RSRV_STR1", inTrade.getString("NEW_SN", ""));
            in.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE", ""));
            insAltSnInfo(in, "-");
        }
        else
        {
            IData pAltsnUpdate = new DataMap();
            pAltsnUpdate.put("SERIAL_NUMBER", inTrade.getString("OLD_SN", ""));
            pAltsnUpdate.put("FROM_STATUS", "1");
            pAltsnUpdate.put("TO_STATUS", "3");
            pAltsnUpdate.put("RELA_TYPE", "2");
            pAltsnUpdate.put("ALT_CANCEL_TIME", SysDateMgr.getSysTime());
            UserAltsnInfoQry.updAltsnBySn(pAltsnUpdate);
        }

        if (!"02".equals(opr_code))
        {
            IData pAltsnPlatUpdate = new DataMap();
            pAltsnPlatUpdate.put("SERIAL_NUMBER", inTrade.getString("OLD_SN", ""));
            pAltsnPlatUpdate.put("ACTIVATE_TIME", SysDateMgr.getSysTime());
            pAltsnPlatUpdate.put("RELA_TYPE", "2");
            UserAltsnInfoQry.updAltPlatmrgBySn(pAltsnPlatUpdate);
        }

        String cust_id = userInfo.getString("CUST_ID");
        String user_id = userInfo.getString("USER_ID");
        // 调用账务接口
        if (!"02".equals(opr_code))
        {

            this.dealAcct(input, cust_id, user_id, "ACTIVE");
            billInfo = this.dealAcct(input, cust_id, user_id, "3");

        }
        else
        {
            this.dealAcct(input, cust_id, user_id, "");
        }

        // 落地方搬预约台帐到历史表
        if (!"02".equals(opr_code))
        {

            IData hisdata = new DataMap();
            hisdata.put("RSRV_STR6", "END");
            hisdata.put("RSRV_STR2", inTrade.getString("OLD_SN", ""));
            this.hisPreTrade("RSRV_STR2", hisdata);

        }

        IData pIbossGetUserInfo = new DataMap();
        IDataset dsIbossGetUserInfo = new DatasetList();
        if (!"02".equals(opr_code)) // 这是后面加的条件
        {
            pIbossGetUserInfo.put("IDTYPE", "01");
            pIbossGetUserInfo.put("IDVALUE", input.getString("OLD_ID_VALUE"));
            pIbossGetUserInfo.put("TYPEIDSET", "0"); /* 0基本资料1 实时话费2 账户资料3 账本资料4帐单资料 5大客户资料 6积分信息8 业务开通资料 */
            dsIbossGetUserInfo.add(userInfo);
            AltsnGetUserInfoBean ibossBean = new AltsnGetUserInfoBean();
            dsIbossGetUserInfo = ibossBean.getUserInfo(pIbossGetUserInfo);
        }
        // 获取老号码信息，返回给发起方
        if (!"02".equals(opr_code))
        {
            returnUserBaseInfo(input, BizRsp, user_id, dsIbossGetUserInfo,billInfo);
        }
        else
        {
            // 取消
            BizRsp.put("RESERVE", "");
            BizRsp.put("BIZ_ORDER_RESULT", "0000");
            BizRsp.put("BIZ_ORDER_RSP_DESC", "SYN OK");
        }
        return BizRsp;
    }

    private IData callIBossData(IData output, IData input, String snflag) throws Exception
    {

        IData iBossData = new DataMap();
        iBossData.put("SERIAL_NUMBER", input.getString(snflag));
        iBossData.put("IDCARDTYPE", output.getString("PSPT_TYPE_CODE"));
        iBossData.put("IDCARDNUM", output.getString("PSPT_ID"));

        IData remoteCust = queryBasicInfo(iBossData);

        if ("0".equals(remoteCust.getString("X_RSPTYPE")) && "0000".equals(remoteCust.getString("X_RSPCODE")))
        {

        }
        else
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2, remoteCust.getString("X_RSPTYPE"), remoteCust.getString("X_RESULTINFO"));
        }
        return output;
    }

    private IData changePhonePreRegisterSnActive(IData param) throws Exception
    {
        // TODO Auto-generated method stub
        // 本地号码落地激活
        IDataset results = CSAppCall.call("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegisterSnActive", param);
        return results.getData(0);
    }

    /**
     * 此过程，之只处理本地号码（调用到这里的，入口已做过路由，必然是本地号码)的校验，只根据新老号码做区分，不区分业务，也不区分发起方
     * 
     * @param param
     * @param input
     * @param sn
     * @param newTag
     * @return
     * @throws Exception
     */
    private IData checkConditon(IData param, IData input, String sn, String newTag) throws Exception
    {
        // TODO Auto-generated method stub

        IData data = new DataMap();
        // 1,处于停机等状态
        data.put("SERIAL_NUMBER", sn);

        IData userInfo = this.getLocalUserInfo(data);

        String userId = userInfo.getString("USER_ID");
        if (!"0".equals(userInfo.getString("USER_STATE_CODESET", "")))
        {

            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2909, sn, USvcStateInfoQry.getSvcIntfModeBySvcIdStateCode("0", userInfo.getString("USER_STATE_CODESET", "")));
        }

        // 2, 原号码欠费的，需先结清欠费
        if ("OLD".equals(newTag))
        {
            IData accountParam = new DataMap();
            accountParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            accountParam.put("TRADE_EPARCHY_CODE", input.getString("EPARCHY_CODE", ""));
            accountParam.put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE", ""));
            accountParam.put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID", ""));
            accountParam.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID", ""));
            accountParam.put("USER_ID", userInfo.getString("USER_ID", ""));
            accountParam.put("REMOVE_TAG", "0");

            IData fee = AcctCall.getOweFeeByUserId(userInfo.getString("USER_ID", ""));

            if (IDataUtil.isEmpty(fee))
            {

                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2910);

            }
        }

        // 3,国际一卡多号业务用户
        if ("OLD".equals(newTag))
        {
            IDataset dataset = UserOtherInfoQry.getUserOther(userId, "SIMM");

            if (!IDataUtil.isEmpty(dataset))
            {

                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2911);
            }
        }

        if ("OLD".equals(newTag))
        {
            this.checkInterRentPhone(userId);
            this.checkPayrelation(userId, userInfo); // //代付关系
            this.checkUserSvcState(userId);
            this.checkIsTDPhone(userId);
//            this.checkExistsOrder(userId);
        }

        IData result = getCustInfo(userInfo);

        return result;
    }

    public void checkExistsOrder(String userId) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset tradeInfos = TradeInfoQry.queryTradeNotFinishBy2437(userId);
        if (tradeInfos.size() > 0)
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2918);

        }
    }

    /**
     * 预申请落地方调用，预申请发起方调用 改由发起方调用客户资源查询接口，落地方便不再进行号码校验，只做特殊条件校验
     * 
     * @param data
     * @return
     */
    public IData checkIn(IData data, IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IData newCust = new DataMap();
        IData oldCust = new DataMap();
        String oldSn = input.getString("OLD_ID_VALUE");
        String newSn = input.getString("NEW_ID_VALUE");
        // 新号码为本地号码时，校验，并得到客户信息
        if ("A".equals(data.getString("NEW_PROVINCE", "")))
        {
            newCust = this.checkConditon(data, input, newSn, "NEW");
            if ("1".equals(input.getString("NOT_PASS", "")))
            {
                return newCust;
            }
        }

        if ("A".equals(data.getString("OLD_PROVINCE", "")))
        {
            oldCust = this.checkConditon(data, input, oldSn, "OLD");
            if ("1".equals(input.getString("NOT_PASS", "")))
                return oldCust;// newCust ;新疆发现的问题 2013-1-8
        }

        // 2013-06-27 加个if块 MODI_ID: 1234 改由发起方调用客户资源查询接口，落地方便不再进行号码校验
        if ("END".equals(data.getString("RSRV_STR6", "")))
        {
            if (newCust.size() > 0)
                return newCust;
            else
                return oldCust;
        }

        // 新老号码都是本地号码时，进行证件匹配校验
        // 2013-06-27 MODI_ID: 1234 本行注释细化，全为本地号码时，走 IF 分支，进行证件匹配校验
        if (("A".equals(data.getString("NEW_PROVINCE", ""))) && ("A".equals(data.getString("OLD_PROVINCE", ""))))
        {
            if ((!(newCust.getString("PSPT_TYPE_CODE", "X").equals(oldCust.getString("PSPT_TYPE_CODE", "Y")))) || (!(newCust.getString("PSPT_ID", "X").equals(oldCust.getString("PSPT_ID", "Y")))))
            {

                // common.error ( "8012" , "新老号码的证件类型，证件号码不匹配！" ) ;
            }
            if ((!(newCust.getString("PSPT_TYPE_CODE", "X").equals(input.getString("PSPT_TYPE", "Y")))) || (!(newCust.getString("PSPT_ID", "X").equals(input.getString("PSPT_ID", "Y")))))
            {
                // common.error ( "8013" , "客户提交的证件类型，证件号码，与新号码的证件类型，证件号码不匹配！" ) ; //today 这块校验要去掉，调用客户资料查询接口验证
            }
            if (newCust.size() > 0) // 2013-06-27 MODI_ID: 1234 加这4行，保持原有逻辑不变
                return newCust;
            else
                return oldCust;
        }
        // 2013-06-27 MODI_ID: 1234 加上本行注释，非全为本地号码时，走 ELSE 分支，进行证件匹配校验
        else if (newCust.size() > 0) // 2013-06-27 MODI_ID: 1234 改一行代码以便逻辑清晰
        {
            IData p = new DataMap();
            p.put("SERIAL_NUMBER", data.getString("OLD_SN", ""));
            p.put("IDCARDTYPE", newCust.getString("PSPT_TYPE_CODE", ""));
            p.put("IDCARDNUM", newCust.getString("PSPT_ID", ""));
            IData remoteCust = queryBasicInfo(p);
            if ("0".equals(remoteCust.getString("X_RSPTYPE")) && "0000".equals(remoteCust.getString("X_RSPCODE")))
            {
                // setInfo(data);
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2920, remoteCust.getString("X_RSPDESC"), remoteCust.getString("X_RESULTINFO"));
            }
            return newCust;
        }
        else
        {
            IData p = new DataMap();
            p.put("SERIAL_NUMBER", data.getString("NEW_SN", ""));
            p.put("IDCARDTYPE", oldCust.getString("PSPT_TYPE_CODE", ""));
            p.put("IDCARDNUM", oldCust.getString("PSPT_ID", ""));
            IData remoteCust = queryBasicInfo(p);
            if ("0".equals(remoteCust.getString("X_RSPTYPE")) && "0000".equals(remoteCust.getString("X_RSPCODE")))
            {
                // setInfo(data);
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2920, remoteCust.getString("X_RSPDESC"), remoteCust.getString("X_RESULTINFO"));
            }
            return oldCust;
        }
    }

    /**
     * 预申请落地方校验
     * 
     * @param input
     */
    private IData checkInOut(IData param, IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IData newCust = new DataMap();
        IData oldCust = new DataMap();

        String new_province = param.getString("NEW_PROVINCE");
        String old_province = param.getString("OLD_PROVINCE");

        // 新号码为本地号码时，校验，并得到客户信息
        if ("A".equals(new_province))
        {
            newCust = this.checkConditon(param, input, input.getString("NEW_SN"), "NEW");
            if ("1".equals(input.getString("NOT_PASS")))
            {
                return newCust;
            }
        }
        // 老号码为本地号码时，校验，并得到客户信息
        if ("A".equals(old_province))
        {
            oldCust = this.checkConditon(param, input, input.getString("OLD_SN"), "OLD");
            if ("1".equals(input.getString("NOT_PASS")))
            {
                return oldCust;
            }
        }

        if ("END".equals(input.getString("RSRV_STR6", "")))
        {
            if (newCust.size() > 0)
                return newCust;
            else
                return oldCust;
        }

        if ("A".equals(new_province) && "A".equals(old_province))
        {
            if(!newCust.getString("CUST_NAME").equals(oldCust.getString("CUST_NAME"))
               ||!newCust.getString("PSPT_TYPE_CODE").equals(oldCust.getString("PSPT_TYPE_CODE"))
               ||!newCust.getString("PSPT_ID").equals(oldCust.getString("PSPT_ID"))){
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2931);
            }
        }
        else if (!newCust.isEmpty())
        {
            IData indata = new DataMap();
            indata.put("IDTYPE", "01");
            indata.put("ID_ITEM_RANGE", input.getString("OLD_SN"));
            indata.put("IDCARDTYPE", newCust.getString("PSPT_TYPE_CODE"));
            indata.put("IDCARDNUM", newCust.getString("PSPT_ID"));
            if(StringUtils.isBlank(input.getString("USER_PASSWD",""))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"老号码是外省号码，请输入老号码的服务密码！");
            }
            indata.put("USER_PASSWD", input.getString("USER_PASSWD"));
            IDataset result = queryRemoteWriteCustomer(indata);
            return result.getData(0);
        }
        else
        {
            IData indata = new DataMap();
            indata.put("IDTYPE", "01");
            indata.put("ID_ITEM_RANGE", input.getString("NEW_SN"));
            indata.put("IDCARDTYPE", oldCust.getString("PSPT_TYPE_CODE"));
            indata.put("IDCARDNUM", oldCust.getString("PSPT_ID"));
            if(StringUtils.isBlank(input.getString("USER_PASSWD",""))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"新号码是外省号码，请输入新号码的服务密码！");
            }
            indata.put("USER_PASSWD", input.getString("USER_PASSWD"));
            IDataset result = queryRemoteWriteCustomer(indata);
            return result.getData(0);
        }
        return newCust;
    }

    public void checkInterRentPhone(String userId) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset dataset = CommparaInfoQry.queryByRuleId(userId);
        // CommparaInfoQry.queryExistsInterRentPhone(userId)
        if (IDataUtil.isNotEmpty(dataset))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2912);

        }
    }

    /**
     * 校验是否存在改号记录
     * 
     * @param oldSn
     * @param newSn
     * @throws Exception
     */
    public void checkIsExistsGh(String oldSn, String newSn) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset resultInfos = new DatasetList();
        resultInfos = UserAltsnInfoQry.queryAltStateInfoBySn(oldSn);

        if (!IDataUtil.isEmpty(resultInfos))
        {
            if ("1".equals(resultInfos.getData(0).getString("RELA_TYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2906, oldSn);

            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2906, oldSn);
            }
        }
        resultInfos = UserAltsnInfoQry.queryAltStateInfoBySn(newSn);

        if (!IDataUtil.isEmpty(resultInfos))
        {

            if ("1".equals(resultInfos.getData(0).getString("RELA_TYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2906, newSn);

            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2906, newSn);
            }
        }

        resultInfos = UserAltsnInfoQry.checkExistsInfoBySn(newSn, getCommonParam(null, "8000", "ALT_INTERVAL_MONTH"));

        if (!IDataUtil.isEmpty(resultInfos))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_1, newSn);
        }
        
        resultInfos = UserAltsnInfoQry.checkExistsInfoBySn(oldSn, getCommonParam(null, "8000", "ALT_INTERVAL_MONTH"));

        if (!IDataUtil.isEmpty(resultInfos))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_1, oldSn);
        }

    }

    /**
     * 校验是否存在改号台账
     * 
     * @param oldSn
     * @param newSn
     * @param flag
     * @throws Exception
     */
    public IData checkIsExistsGhTrade(String str1, String str2, String flag) throws Exception
    {
        // TODO Auto-generated method stub
        
        IDataset resultInfos = PreTradeInfoQry.queryTradeInfoBySn1(str1);

        if ("PRE".equals(flag))
        {
            if (resultInfos.size() > 0)
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2907, str1);
            }

        }
        if ("ACT".equals(flag) || "SYNACT".equals(flag))
        {
            IDataset actdataset = UserAltsnInfoQry.queryAllAltStateInfoBySn(str1);
            if(IDataUtil.isNotEmpty(actdataset)){
                IData act = actdataset.getData(0);
                if("ACT".equals(flag) && ("0".equals(act.getString("STATUS"))||"9".equals(act.getString("STATUS")))){
                    if("1".equals(act.getString("RELA_TYPE"))){
                        act.put("RSRV_STR2", act.getString("RELA_SERIAL_NUMBER"));
                    } else {
                        act.put("RSRV_STR2", act.getString("SERIAL_NUMBER"));
                    }
                    return act;
                }
            }
            if (resultInfos.size() < 1)
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2929, str1);
            }
        }
        
        IDataset resultInfos2 = PreTradeInfoQry.queryTradeInfoBySn1(str2);

        if ("PRE".equals(flag))
        {
            if (resultInfos2.size() > 0)
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2907, str2);
            }

        }
        return resultInfos.size() > 0 ? resultInfos.getData(0) : new DataMap();
    }

    public void checkIsTDPhone(String userId) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset comm_products = CommparaInfoQry.getCommpara("CSM", "9723", null, null);

        IDataset userInfos = UserAltsnInfoQry.queryUserAltsnByUserId(userId);

        if (IDataUtil.isNotEmpty(userInfos))
        {
            String productId = userInfos.getData(0).getString("PRODUCT_ID");

            for (int i = 0; i < comm_products.size(); i++)
            {

                if ("X".equals(productId))
                {
                    CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2917);

                }
            }
        }
    }

    public void checkPayrelation(String userId, IData userInfo) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();

        IDataset accountInfo = AcctInfoQry.queryAcctInfoBySn(userInfo.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(accountInfo))
        {
            // 客户没有账户资料
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2915);
        }

        if (!"0".equals(accountInfo.getData(0).getString("PAY_MODE_CODE", "")))
        {

            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2914);

        }
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        param.put("PAYRELATION_QUERY_TYPE", "-1");
        param.put("ACCT_ID", accountInfo.getData(0).getString("ACCT_ID"));

        IDataset payInfos = PayRelaInfoQry.getAdvPayRelation(param, null);

        if (!IDataUtil.isEmpty(payInfos))
        {

            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2913);
        }

    }

    public void checkProvince(String newEparchy, String newProvince, String oldEparchy, String oldProvince) throws Exception
    {
        if ((newProvince != "A") && (oldProvince != "A"))
            ;
        CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2927);

    }

    public IData checkSerialOldNew(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        String oldSn = input.getString("OLD_SN");
        String newSn = input.getString("NEW_SN");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(newSn);
        if (IDataUtil.isEmpty(userInfo))
        {
            IData userInfold = UcaInfoQry.qryUserInfoBySn(oldSn);
            if (IDataUtil.isEmpty(userInfold))
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2919, oldSn);

        }

        this.checkIsExistsGh(oldSn, newSn);
        this.checkIsExistsGhTrade(oldSn, newSn, "PRE");
        
        IData indata = new DataMap();
        setInData(indata, input);
        input.put("PSPT_TYPE_CODE", input.getString("custInfo_PSPT_TYPE_CODE"));
        input.put("PSPT_ID", input.getString("custInfo_PSPT_ID"));

        IData localCustInfo = this.checkInOut(indata, input);

        if ("1".equals(input.getString("NOT_PASS")))
        {

        }
        else
        {

        }

        input.put("REMARK", input.getString("RSRV_STR6", "BEGIN"));
        input.put("SYNC_INFO", indata);
//         IData result = synSn(indata , localCustInfo ) ;

        return input;
    }

    public void checkUserSvcState(String userId) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset userInfos = UserSvcStateInfoQry.getUserSvcStateBySvcId(userId, "0", "0");

        if (IDataUtil.isEmpty(userInfos) || (!"0".equals(userInfos.getData(0).getString("STATE_CODE"))))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2916);
        }
    }

    private IData dealAcct(IData input, String cust_id, String user_id, String flag) throws Exception
    {
        // TODO Auto-generated method stub

        IData paramAccount = new DataMap();
        IDataset resultset = new DatasetList();
        paramAccount.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));

        IDataset accountInfo = AcctInfoQry.getAcctInfoByCustId(cust_id);
        if (IDataUtil.isEmpty(accountInfo))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2915);
            // common.error( "没有帐务资料" ) ;
        }
        IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "8000", "ALT_ACT_DETAIL");

        if (IDataUtil.isNotEmpty(results) && "1".equals(results.getData(0).getString("PARA_CODE1")))
        {
            if ("ACTIVE".equals(flag) || "".equals(flag))
            {

                AcctCall.changeNumOperate(input, flag, user_id, accountInfo.getData(0).getString("ACCT_ID"));
            }

            if ("2".equals(flag))
            {
                AcctCall.insertMasterBill(input);
            }
            if ("3".equals(flag))
            {
                resultset = AcctCall.qryMasterBill(input);
            }
        }
        else
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2926, "ALT_ACT_DETAIL");

        }

        return resultset.size() > 0 ? resultset.getData(0) : new DataMap();

    }

    public IData dealActive(IData indata, IData input, IData outdata) throws Exception
    {
        // TODO Auto-generated method stub
        String newEparchy = this.getSnRoute(indata, outdata);
        String newProvince = outdata.getString("FLAG", "");

        if (!"A".equals(newProvince))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2921);
        }

        this.checkConditon(input, input, indata.getString("SERIAL_NUMBER"), "NEW");

        if ("1".equals(input.getString("NOT_PASS", "")))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2921);
            // common.error( pd.getData().getString( "NPASS_RspCode" , "") , pd.getData().getString( "NPASS_Reserve" ,
            // "" ) ) ;
        }

        indata.put("SERIAL_NUMBER", indata.getString("OLD_SN", ""));//SERIAL_NUMBER RSRV_STR2
        String oldEparchy = getSnRoute(indata, outdata);
        String oldProvince = outdata.getString("FLAG", "");

        IData inTrade = new DataMap();
        indata.put("EPARCHY_CODE", newEparchy);
        indata.put("RSRV_STR1", input.getString("NEW_SN", ""));//NEW_SN 
        
        this.insAltSnInfo(indata, "+");

        indata.put("NEW_PROVINCE", newProvince);
        indata.put("OLD_PROVINCE", oldProvince);
        indata.put("OLD_EPARCHY", oldEparchy);

        IData result = synSnActivate(input, indata);//input获取OLD_IMSI

        
        // 调用改号平台接口 改号激活today
        IData rData = new DataMap(), inData = new DataMap();
        String errStr = "";

        try
        {
            inData.put("KIND_ID", "BIP2B075_T2001075_0_0"); // 交易唯一标识
            inData.put("X_TRANS_CODE", ""); // 交易编码-IBOSS
            inData.put("OLD_ID_VALUE", indata.getString("OLD_SN", ""));
            inData.put("OLD_IMSI", input.getString("OLD_IMSI", "")); // OldIMSI
            inData.put("NEW_ID_VALUE", input.getString("NEW_SN", ""));
            inData.put("NEW_IMSI", getNewIdIMSI(input)); // NewIMSI
            inData.put("OPR_CODE", "01"); // 01-激活数据同步到改号平台
            inData.put("RESERVE", "");
            String volteType = input.getString ("VOLTE_TYPE","0");
            if("".equals(volteType)){
                volteType="0";
            }
            inData.put("VOLTE_TYPE", volteType);
            inData.put("OLD_IMPI", input.getString("OLD_IMPI"));
            String newimsi = inData.getString("NEW_IMSI");
            inData.put("IMPI",newimsi + "@ims.mnc0"+newimsi.substring(3, 5)+".mcc460.3gppnetwork.org" );

//            rData = IBossCall.callHttpIBOSS("IBOSS", inData).getData(0);
            rData = IBossCall.dealInvokeUrl("BIP2B075_T2001075_0_0", "IBOSS6", inData).getData(0);
            if (!"0000".equals(rData.getString("X_RSPCODE")))
            {
                logger.error("调用改号平台出错:" + rData.getString("X_RSPCODE") + " " + rData.getString("X_RSPDESC"));
//                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2922, rData.getString("X_RESULTINFO"));

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
//            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2923);
        }
        // 搬迁发起方预受理台帐到历史表
        indata.put("RSRV_STR6", "BEGIN");
        this.hisPreTrade("RSRV_STR1", indata);

        // 最后成功返回成功信息
        result.put("X_RESULTINFO", "OK");
        result.put("X_RECORDNUM", "1");
        result.put("X_RESULTCODE", "0");
        return result;
    }

    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        // **//需要改成参数配置,地州区分
        if ("0".equals(IdType) || "2".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    public String getCommonParam(IData input, String param_attr, String param_code) throws Exception
    {
        IDataset dsCommonParam = CommparaInfoQry.getCommpara("CSM", param_attr, param_code, null);
        if (dsCommonParam.size() > 0)
            return dsCommonParam.getData(0).getString("PARA_CODE1", "");
        else
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2926, param_attr);
        return "";
    }

    private IData getCustInfo(IData userInfo) throws Exception
    {
        // TODO Auto-generated method stub

        IDataset custInfo = CustomerInfoQry.getCustInfoBySn(userInfo, null);

        if (IDataUtil.isEmpty(custInfo))
        {
            // common.error 没有客户资料
        }
        return custInfo.getData(0);
    }

    public IData getLocalUserInfo(IData indata) throws Exception
    {
        // TODO Auto-generated method stub
        IData userInfo = UcaInfoQry.qryUserInfoBySn(indata.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(userInfo))
        {

            return new DataMap();
            // CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2919,indata.getString("SERIAL_NUMBER"));
        }

        return userInfo;
    }

    public String getNewIdIMSI(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", input.getString("NEW_SN", ""));
        params.put("USER_ID", getLocalUserInfo(params).getString("USER_ID", ""));

        IDataset dsRes = UserResInfoQry.getUserResInfoByUserId(params.getString("USER_ID"));
        IData resOld = new DataMap();
        for (int i = 0; i < dsRes.size(); i++)
        {
            if ("1".equals(dsRes.getData(i).getString("RES_TYPE_CODE", "")))
            {
                return dsRes.getData(i).getString("IMSI", "");
            }
        }
        CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2109);

        return "";
    }

    // 获取NP路由
    public String getNpEparchyCode(IData data) throws Exception
    {

        IDataset dsMoffice = UserInfoQry.getNetNPBySN(data.getString("SERIAL_NUMBER"), "0");
        if (dsMoffice.size() > 0)
            return dsMoffice.getData(0).getString("EPARCHY_CODE", "");
        else
            return "";
    }

    // 获取本省路由
    public String getProvinceEparchyCode(IData data) throws Exception
    {
        // 6、不能根据td_m_moffice 来判断号码的归属地，海南这边的号段包含全国移动的了，如果根据这个表来判断那异地的号也变成海南的号了。
        // 第六个问题解决方案，用select * from ucr_cen1.TD_M_MSISDN a WHERE 1=1 AND a.area_code = '0898'; 表来判断
        // 2013-06-27 海南只一个区号，直接返回外省
        IDataset dsMoffice = MofficeInfoQry.getPhoneMofficeBySn(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isNotEmpty(dsMoffice))
        {
            return dsMoffice.getData(0).getString("EPARCHY_CODE", "");
        }
        else
            return "";
    }

    /**
     * @param indata
     * @param input
     * @return
     * @throws Exception
     */
    public String getSnRoute(IData indata, IData input) throws Exception
    {
        // NP号码，虽然可能是同库内NP，但统一当省内走路由表
        String rt = "";
        String flag = getCommonParam(indata, "8000", "NETNP_SWITCH");
        if ("1".equals(flag))
        {
            rt = getNpEparchyCode(indata);
            if (!"".equals(rt))
            {
                input.put("FLAG", "B");
                return rt;
            }
        }
        // 本库号码
        rt = getLocalUserInfo(indata).getString("EPARCHY_CODE", "");
        if (!"".equals(rt))
        {
            input.put("FLAG", "A");
            return rt;
        }
        // 本省号码
//        rt = getProvinceEparchyCode(indata);
//        if (!"".equals(rt))
//        {
//            input.put("FLAG", "B");
//            return rt;
//        }
        // 非本省号码
        input.put("FLAG", "C");
        return "";
    }

    private void hisPre(IData indata) throws Exception
    {
        // TODO Auto-generated method stub
        IData pHis = new DataMap();
        pHis.put("SERIAL_NUMBER", indata.getString("RSRV_STR1", ""));
        pHis.put("RSRV_STR6", indata.getString("RSRV_STR6", ""));
        // daoHis.executeUpdateByCodeCode("TF_B_PRE_TRADE", "INS_TO_HIS", pHis );
        // daoHis.executeUpdateByCodeCode("TF_B_PRE_TRADE", "DEL_PRE", pHis );
    }

    private void hisPreTrade(String sntag, IData hisdata) throws Exception
    {
        // TODO Auto-generated method stub
        IData pHis = new DataMap();
        pHis.put("SERIAL_NUMBER", hisdata.getString(sntag, ""));
        pHis.put("RSRV_STR6", hisdata.getString("RSRV_STR6", ""));
        UserAltsnInfoQry.insBhPreTradeBySn1(pHis);

        UserAltsnInfoQry.delPreTradeBySn1(pHis);

    }

    public void insAltSnInfo(IData indata, String direct) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", indata.getString("EPARCHY_CODE", ""));
        inparam.put("ACTIVATE_TIME", SysDateMgr.getSysDate());
        inparam.put("RSRV_DATE1", SysDateMgr.getAddMonthsNowday(3, SysDateMgr.getSysDate()));
        if ("+".equals(direct))
        {
            inparam.put("SERIAL_NUMBER", indata.getString("RSRV_STR1", ""));
            inparam.put("RELA_SERIAL_NUMBER", indata.getString("SERIAL_NUMBER", ""));
            inparam.put("RELA_TYPE", "1");
            inparam.put("STATUS", "0"); // 0：已登记；1：已激活；2：已登记取消；3：已取消

            // 这里先判断同步是不是报错,即status是不是0或9,是的话删掉这条记录
            IData params = new DataMap();
            params.put("SERIAL_NUMBER", indata.getString("RSRV_STR1", ""));
            IDataset dsPreInfo = UserAltsnInfoQry.queryUserAltsnBySnStatus(params.getString("SERIAL_NUMBER"));
            if (dsPreInfo.size() > 0)
            {
                // 有记录则删掉
                UserAltsnInfoQry.delAltsnBySn(params.getString("SERIAL_NUMBER"));

            }

        }
        else
        {
            inparam.put("SERIAL_NUMBER", indata.getString("SERIAL_NUMBER", ""));
            inparam.put("RELA_SERIAL_NUMBER", indata.getString("RSRV_STR1", ""));
            inparam.put("RELA_TYPE", "2");
            inparam.put("STATUS", "0"); // 0：已登记；1：已激活；2：已登记取消；3：已取消
        }
        UserAltsnInfoQry.insAltsnBySn(inparam);
    }

    /**
     * 异地调用IBOSS接口校验号码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IData queryBasicInfo(IData param) throws Exception
    {
        // TODO Auto-generated method stub
        IData inparam = new DataMap();
        IData commonparam = new DataMap();

        commonparam.put("PROVINCE_CODE", getVisit().getProvinceCode());// 省别编码
        commonparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        // 接入方式0 营业厅 1 客服(callcenter)2 网上客服 3 网上营业厅 4 银行 5 短信平台 6 一级BOSS 7 手机支付 8 统一帐户服务系统(uasp)
        // 9 短信营销/短信营业厅/短信代办 A 触摸屏 B 自助打印机 C 多媒体 D 自助营业厅 E 个人代扣/银行代扣 F 电话开通 G 168点播信息
        // H 空中充值 I 积分平台 J 彩铃接口 K 梦网接口 L WAP接口 M 大客户接口 N 电信卡余额 O 家校通 P 缴费卡缴费 Q 手机钱包 R POS机缴费

        commonparam.put("TRADE_EPARCHY_CODE", "800");// 交易地州编码
        commonparam.put("TRADE_CITY_CODE", getVisit().getCityCode());// 交易城市代码
        commonparam.put("TRADE_DEPART_ID", getVisit().getDepartId());// 员工部门编码
        commonparam.put("TRADE_STAFF_ID", getVisit().getStaffId());// 员工城市编码
        commonparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        commonparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());// 路由地州编码
        commonparam.put("ROUTETYPE", "01");// 路由类型 00-省代码，01-手机号
        commonparam.put("ROUTEVALUE", param.getString("SERIAL_NUMBER"));

        inparam.putAll(commonparam);
        inparam.put("KIND_ID", "BIP1A001_T1000002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        inparam.put("IDTYPE", "01"); // 01 手机号
        inparam.put("IDVALUE", param.getString("SERIAL_NUMBER", "")); // 根据IDTYPE设置对应的值
        inparam.put("IDCARDTYPE", decodeIdType(param.getString("IDCARDTYPE")));
        inparam.put("IDCARDNUM", param.getString("IDCARDNUM"));
        inparam.put("USER_PASSWD", ""); // 有证件，这个可以没有
        inparam.put("TYPEIDSET", "0"); /* 0 基本资料 */
        inparam.put("START_DATE", ""); // 可以不传
        inparam.put("END_DATE", ""); // 可以不传

//        IDataset datas = IBossCall.callHttpIBOSS("IBOSS", inparam);
        IDataset datas = IBossCall.dealInvokeUrl("BIP1A001_T1000002_0_0", "IBOSS6", inparam);
        

        return datas.size() > 0 ? datas.getData(0) : new DataMap();
    }

    private void returnUserBaseInfo(IData input, IData BizRsp, String userId, IDataset dsIbossGetUserInfo, IData billInfo) throws Exception
    {
        // TODO Auto-generated method stub
        // 激活
        IData ActiveInfo = new DataMap();
        BizRsp.put("OLD_ID_VALUE", input.getString("OLD_ID_VALUE"));
        BizRsp.put("OLD_IMSI", input.getString("NEWIMSI", ""));// 调用落地返回NEWIMSI

        BizRsp.put("NEW_ID_VALUE", input.getString("NEW_ID_VALUE"));
        String level = dsIbossGetUserInfo.getData(0).getString("CUST_LEVLE", "");
        BizRsp.put("LEVEL", level);
        if (!"0".equals(level))
        {
            IData vipParam = new DataMap();
            vipParam.put("USER_ID", userId);
            vipParam.put("REMOVE_TAG", "0");
            IDataset vipids = CustVipInfoQry.getCustVipByUserId(userId, "0", getTradeEparchyCode());
            if (vipids != null && vipids.size() > 0)
            {
                String levelDate = ((IData) vipids.get(0)).getString("VIP_CARD_END_DATE", "");
                if (!"".equals(levelDate))
                {
                    levelDate = levelDate.substring(0, 10).replace("-", "");
                }
                BizRsp.put("LEVEL_DATE", levelDate);
            }
        }
        // LevelDate
        String servOpr = dsIbossGetUserInfo.getData( 0 ).getString ( "SERV_OPR", "" );
        if(servOpr.length()>256){
            servOpr = cutString(servOpr,256);
        }
        BizRsp.put("SERV_OPR", servOpr); // 基本服务开放情况
        // BasicOpr 基本业务开放情况
        String incrNo = dsIbossGetUserInfo.getData( 0 ).getString ( "INCREMENT_NO", "" );
        if(incrNo.length()>256){
            incrNo = cutString(incrNo, 256);
        }
        BizRsp.put("INCRE_OPR", incrNo) ;//增值业务开放情况
        String basicOpr = dsIbossGetUserInfo.getData( 0 ).getString ( "SERVICE_NAME", "" );
        if(basicOpr.length()>256){
            basicOpr = cutString(basicOpr, 256);
        }
        BizRsp.put("BASIC_OPR", basicOpr) ;  //BasicOpr 基本业务开放情况
        BizRsp.put("STATUS", "00"); // 客户状态 00：正常；01：单向停机；02：停机；03：预销户；

        BizRsp.put("USER_NAME", dsIbossGetUserInfo.getData(0).getString("CUST_NAME", "")); // 客户姓名
        BizRsp.put("USER_ADD", dsIbossGetUserInfo.getData(0).getString("CONTACT_ADDRESS", "")); // 联系地址
        BizRsp.put("USER_NUM", dsIbossGetUserInfo.getData(0).getString("CONTACT_PHONE", "")); // 联系电话
        BizRsp.put("ID_CARD_TYPE", dsIbossGetUserInfo.getData(0).getString("IDCARDTYPE", "")); // 证件类型
        BizRsp.put("ID_CARD_NUM", dsIbossGetUserInfo.getData(0).getString("IDCARDNUM", "")); // 证件号码
        String brandCode = dsIbossGetUserInfo.getData(0).getString("BRAND_CODE", "");
        if (brandCode.length() < 2)
        {
            String result = "";

            if ("0".equals(brandCode))
            {
                brandCode = "01";// 全球通
            }
            else if ("1".equals(brandCode))
            {
                brandCode = "02";// 神州行
            }
            else if ("2".equals(brandCode))
            {
                brandCode = "03";// 动感地带
            }
            else
            {
                brandCode = "09";
            }

        }
        BizRsp.put("BRAND", brandCode); // 用户品牌
        BizRsp.put("OPR_TIME", dsIbossGetUserInfo.getData(0).getString("OPEN_DATE", ""));// 入网时间
        BizRsp.put("HOME_PROV", dsIbossGetUserInfo.getData(0).getString("EPARCHY_NAME", "")); // 归属地

        IDataset infoConts = getInfoConts(input,dsIbossGetUserInfo,billInfo);

        BizRsp.put("INFO_CONT", infoConts);
        BizRsp.put("ACTIVE_INFO", ActiveInfo);
        BizRsp.put("RESERVE", "");
        BizRsp.put("BIZ_ORDER_RESULT", "0000");
        BizRsp.put("BIZ_ORDER_RSP_DESC", "SYN OK");
    }

    private IDataset getInfoConts(IData input,IDataset dsIbossGetUserInfo,IData billInfo) throws Exception {
        IDataset infoConts = new DatasetList();
        IData infoCont1 = new DataMap();
        IData infoCont2 = new DataMap();
        IData infoCont3 = new DataMap();
        IDataset infoItems = new DatasetList();
        
        //0-基本资料
        IData userInfo = dsIbossGetUserInfo.getData(0);
        if(IDataUtil.isNotEmpty(userInfo)){         
            infoCont1.put("INFO_TYPE_ID", "0");
            infoCont1.put("INFO_ITEMS", getBaseInfo(userInfo));     
            infoConts.add(infoCont1);
        }
        //4-帐单资料
        IDataset billCont= billInfo.getDataset("MoInfo");
        if( null != billCont  && !billCont.isEmpty()){          
            infoCont2.put("INFO_TYPE_ID", billInfo.getString("InfoTypeID"));
            for(int i=0;i<billCont.size();i++){
                IData Item = billCont.getData(i);
                IData infoItem = new DataMap();
                infoItem.put("ITEM_ID", Item.getString("ItemID"));
                infoItem.put("ITEM_CONT", Item.getString("ItemCont"));
                if("407".equals(infoItem.getString("ITEM_ID"))){
                    IDataset childItems = new DatasetList();
                    IData childItem = new DataMap();
                    IDataset childs = Item.getDataset("ChildItems");
                    for(int j=0;j<childs.size();j++){
                        IData child = childs.getData(j);
                        childItem.put("CHILD_ITEM_ID", child.getString("ChildItemID"));
                        childItem.put("CHILD_ITEM_CONT", child.getString("ChildItemCont"));
                        childItems.add(childItem);
                    }
                    infoItem.put("CHILD_ITEMS", childItems);                
                }
                infoItems.add(infoItem);
            }
            infoCont2.put("INFO_ITEMS", infoItems);
            infoConts.add(infoCont2);
        }
        
        //5-大客户资料
        IDataset vipInfo = getVipInfo(input);
        if( null != vipInfo  && !vipInfo.isEmpty()){            
            infoCont3.put("INFO_TYPE_ID", "5");
            infoCont3.put("INFO_ITEMS", vipInfo);       
            infoConts.add(infoCont3);       
        }
        
        return infoConts;
    }
    
    private IDataset getBaseInfo(IData dsIbossUserInfo) throws Exception{
        IDataset infoItems = new DatasetList();
        IData infoItem = new DataMap();     
        infoItem.put("ITEM_ID", "100");
        infoItem.put("ITEM_CONT", dsIbossUserInfo.getString("CUST_ID",""));
        infoItems.add(infoItem);
        IData infoItem1 = new DataMap();
        infoItem1.put("ITEM_ID", "101");
        infoItem1.put("ITEM_CONT", dsIbossUserInfo.getString("CONTACTNAME",""));
        infoItems.add(infoItem1);
        IData infoItem2 = new DataMap();
        infoItem2.put("ITEM_ID", "102");
        infoItem2.put("ITEM_CONT", dsIbossUserInfo.getString("SEX",""));
        infoItems.add(infoItem2);
        IData infoItem3 = new DataMap();
        infoItem3.put("ITEM_ID", "103");
        infoItem3.put("ITEM_CONT", dsIbossUserInfo.getString("JURI_PSPT_TYPE",""));
        infoItems.add(infoItem3);
        IData infoItem4 = new DataMap();
        infoItem4.put("ITEM_ID", "104");
        infoItem4.put("ITEM_CONT", dsIbossUserInfo.getString("JURI_PSPT_CODE",""));
        infoItems.add(infoItem4);
        IData infoItem5 = new DataMap();
        infoItem5.put("ITEM_ID", "105");
        infoItem5.put("ITEM_CONT", dsIbossUserInfo.getString("PHONE",""));
        infoItems.add(infoItem5);
        IData infoItem6 = new DataMap();
        infoItem6.put("ITEM_ID", "106");
        infoItem6.put("ITEM_CONT", dsIbossUserInfo.getString("POST_CODE",""));
        infoItems.add(infoItem6);
        IData infoItem7 = new DataMap();
        infoItem7.put("ITEM_ID", "107");
        infoItem7.put("ITEM_CONT", dsIbossUserInfo.getString("POST_ADDRESS",""));
        infoItems.add(infoItem7);
        IData infoItem8 = new DataMap();
        infoItem8.put("ITEM_ID", "108");
        infoItem8.put("ITEM_CONT", dsIbossUserInfo.getString("FAX_NBR",""));
        infoItems.add(infoItem8);
        IData infoItem9 = new DataMap();
        infoItem9.put("ITEM_ID", "109");
        infoItem9.put("ITEM_CONT", dsIbossUserInfo.getString("EMAIL",""));
        infoItems.add(infoItem9);
        IData infoItem10 = new DataMap();
        infoItem10.put("ITEM_ID", "110");
        infoItem10.put("ITEM_CONT", dsIbossUserInfo.getString("CONTACT",""));
        infoItems.add(infoItem10);
        IData infoItem11 = new DataMap();
        infoItem11.put("ITEM_ID", "111");
        infoItem11.put("ITEM_CONT", dsIbossUserInfo.getString("USEPHONE",""));
        infoItems.add(infoItem11);
        IData infoItem12 = new DataMap();
        infoItem12.put("ITEM_ID", "112");
        infoItem12.put("ITEM_CONT", dsIbossUserInfo.getString("HOME_ADDRESS",""));
        infoItems.add(infoItem12);
        IData infoItem13 = new DataMap();
        infoItem13.put("ITEM_ID", "113");
        infoItem13.put("ITEM_CONT", dsIbossUserInfo.getString("PSPT_ADDR",""));
        infoItems.add(infoItem13);
        IData infoItem14 = new DataMap();
        infoItem14.put("ITEM_ID", "114");
        infoItem14.put("ITEM_CONT", dsIbossUserInfo.getString("SCORE_VALUE",""));
        infoItems.add(infoItem14);
        IData infoItem15 = new DataMap();
        infoItem15.put("ITEM_ID", "115");
        infoItem15.put("ITEM_CONT", dsIbossUserInfo.getString("CUST_LEVLE",""));
        infoItems.add(infoItem15);
        IData infoItem16 = new DataMap();
        infoItem16.put("ITEM_ID", "116");
        infoItem16.put("ITEM_CONT", dsIbossUserInfo.getString("VIP_TAG",""));
        infoItems.add(infoItem16);
        IData infoItem17 = new DataMap();
        infoItem17.put("ITEM_ID", "117");
        infoItem17.put("ITEM_CONT", dsIbossUserInfo.getString("VIP_CARD_NO",""));
        infoItems.add(infoItem17);
        IData infoItem18 = new DataMap();
        infoItem18.put("ITEM_ID", "118");
        infoItem18.put("ITEM_CONT", dsIbossUserInfo.getString("REG_DATE",""));
        infoItems.add(infoItem18);
        IData infoItem19 = new DataMap();
        infoItem19.put("ITEM_ID", "119");
        infoItem19.put("ITEM_CONT", dsIbossUserInfo.getString("USER_STATE",""));
        infoItems.add(infoItem19);
        IData infoItem20 = new DataMap();
        infoItem20.put("ITEM_ID", "120");
        infoItem20.put("ITEM_CONT", dsIbossUserInfo.getString("STATUS_CHG_TIME",""));
        infoItems.add(infoItem20);
        IData infoItem21 = new DataMap();
        infoItem21.put("ITEM_ID", "121");
        infoItem21.put("ITEM_CONT", dsIbossUserInfo.getString("REMARK",""));
        infoItems.add(infoItem21);
        IData infoItem22 = new DataMap();
        infoItem22.put("ITEM_ID", "122");
        infoItem22.put("ITEM_CONT", dsIbossUserInfo.getString("RSRV_STR4",""));
        infoItems.add(infoItem22);
        IData infoItem23 = new DataMap();
        infoItem23.put("ITEM_ID", "123");
        infoItem23.put("ITEM_CONT", dsIbossUserInfo.getString("BASIC_CREDIT_VALUE",""));
        infoItems.add(infoItem23);
        IData infoItem24 = new DataMap();
        infoItem24.put("ITEM_ID", "124");
        String incrementno = dsIbossUserInfo.getString("INCREMENT_NO","");
        if(incrementno.length()>512){
            incrementno = cutString(incrementno,512);
        }
        infoItem24.put("ITEM_CONT", incrementno);
        infoItems.add(infoItem24);
        IData infoItem25 = new DataMap();
        infoItem25.put("ITEM_ID", "125");
        String servicename = dsIbossUserInfo.getString("SERVICE_NAME","");
        if(servicename.length()>512){
            servicename = cutString(incrementno,512);
        }
        infoItem25.put("ITEM_CONT", servicename);
        infoItems.add(infoItem25);
        IData infoItem26 = new DataMap();
        infoItem26.put("ITEM_ID", "126");
        infoItem26.put("ITEM_CONT", dsIbossUserInfo.getString("EPARCHY_NAME",""));
        infoItems.add(infoItem26);
        return infoItems;
    }
    
    private IDataset getVipInfo(IData input) throws Exception{
        IData pIbossGetUserInfo = new DataMap(); 
        pIbossGetUserInfo.put ( "IDTYPE", "01" ) ;
        pIbossGetUserInfo.put ( "IDVALUE", input.getString ( "OLD_ID_VALUE") ) ;
        pIbossGetUserInfo.put ( "TYPEIDSET", "5"); /*0基本资料1 实时话费2   账户资料3   账本资料4帐单资料   5大客户资料  6积分信息8  业务开通资料*/
        AltsnGetUserInfoBean ibossBean = new AltsnGetUserInfoBean();
        IDataset dsIbossGetUserInfo =  ibossBean.getUserInfo(pIbossGetUserInfo);
        IData vipInfo = dsIbossGetUserInfo.getData(0);  
        
        IDataset infoItems = new DatasetList();
        IData infoItem1 = new DataMap();
        infoItem1.put("ITEM_ID", "601");
        infoItem1.put("ITEM_CONT", vipInfo.getString("REGISTER_NAME",""));
        infoItems.add(infoItem1);
        IData infoItem2 = new DataMap();
        infoItem2.put("ITEM_ID", "602");
        infoItem2.put("ITEM_CONT", vipInfo.getString("SEX_NAME",""));
        infoItems.add(infoItem2);
        IData infoItem3 = new DataMap();
        infoItem3.put("ITEM_ID", "603");
        infoItem3.put("ITEM_CONT", vipInfo.getString("AGE",""));
        infoItems.add(infoItem3);
        IData infoItem4 = new DataMap();
        infoItem4.put("ITEM_ID", "604");
        infoItem4.put("ITEM_CONT", vipInfo.getString("PSPT_TYPE_CODE",""));
        infoItems.add(infoItem4);
        IData infoItem5 = new DataMap();
        infoItem5.put("ITEM_ID", "605");
        infoItem5.put("ITEM_CONT", vipInfo.getString("PSPT_ID",""));
        infoItems.add(infoItem5);
        IData infoItem6 = new DataMap();
        infoItem6.put("ITEM_ID", "606");
        infoItem6.put("ITEM_CONT", vipInfo.getString("MARRIAGE",""));
        infoItems.add(infoItem6);
        IData infoItem7 = new DataMap();
        infoItem7.put("ITEM_ID", "607");
        infoItem7.put("ITEM_CONT", vipInfo.getString("EDUCATE_DEGREE_CODE",""));
        infoItems.add(infoItem7);
        IData infoItem8 = new DataMap();
        infoItem8.put("ITEM_ID", "608");
        infoItem8.put("ITEM_CONT", vipInfo.getString("MOBILENUM",""));
        infoItems.add(infoItem8);
        IData infoItem9 = new DataMap();
        infoItem9.put("ITEM_ID", "609");
        infoItem9.put("ITEM_CONT", vipInfo.getString("TELPHONE",""));
        infoItems.add(infoItem9);
        IData infoItem10 = new DataMap();
        infoItem10.put("ITEM_ID", "610");
        infoItem10.put("ITEM_CONT", vipInfo.getString("CONTACT_POST_ADDR",""));
        infoItems.add(infoItem10);
        IData infoItem11 = new DataMap();
        infoItem11.put("ITEM_ID", "611");
        infoItem11.put("ITEM_CONT", vipInfo.getString("VIP_MANAGER_ID",""));
        infoItems.add(infoItem11);
        IData infoItem12 = new DataMap();
        infoItem12.put("ITEM_ID", "612");
        infoItem12.put("ITEM_CONT", vipInfo.getString("VIP_NO",""));
        infoItems.add(infoItem12);
        IData infoItem13 = new DataMap();
        infoItem13.put("ITEM_ID", "613");
        infoItem13.put("ITEM_CONT", vipInfo.getString("TAG_CODE",""));
        infoItems.add(infoItem13);
        IData infoItem14 = new DataMap();
        infoItem14.put("ITEM_ID", "614");
        infoItem14.put("ITEM_CONT", vipInfo.getString("CLASS_ID",""));
        infoItems.add(infoItem14);
        IData infoItem15 = new DataMap();
        infoItem15.put("ITEM_ID", "615");
        infoItem15.put("ITEM_CONT", vipInfo.getString("USERSCORE",""));
        infoItems.add(infoItem15);
        IData infoItem16 = new DataMap();
        infoItem16.put("ITEM_ID", "616");
        infoItem16.put("ITEM_CONT", vipInfo.getString("ACCT_ID",""));
        infoItems.add(infoItem16);
        IData infoItem17 = new DataMap();
        infoItem17.put("ITEM_ID", "617");
        infoItem17.put("ITEM_CONT", vipInfo.getString("OPEN_TIME",""));
        infoItems.add(infoItem17);
        IData infoItem27 = new DataMap();
        infoItem27.put("ITEM_ID", "627");
        infoItem27.put("ITEM_CONT", vipInfo.getString("LINK_PHONE",""));
        infoItems.add(infoItem27);
        IData infoItem18 = new DataMap();
        infoItem18.put("ITEM_ID", "618");
        infoItem18.put("ITEM_CONT", "");
        infoItems.add(infoItem18);
        IData infoItem19 = new DataMap();
        infoItem19.put("ITEM_ID", "619");
        infoItem19.put("ITEM_CONT", "");
        infoItems.add(infoItem19);
        IData infoItem20 = new DataMap();
        infoItem20.put("ITEM_ID", "620");
        infoItem20.put("ITEM_CONT", "");
        infoItems.add(infoItem20);
        IData infoItem21 = new DataMap();
        infoItem21.put("ITEM_ID", "621");
        infoItem21.put("ITEM_CONT", "");
        infoItems.add(infoItem21);
        IData infoItem22 = new DataMap();
        infoItem22.put("ITEM_ID", "622");
        infoItem22.put("ITEM_CONT", "");
        infoItems.add(infoItem22);
        IData infoItem23 = new DataMap();
        infoItem23.put("ITEM_ID", "623");
        infoItem23.put("ITEM_CONT", "");
        infoItems.add(infoItem23);
        IData infoItem24 = new DataMap();
        infoItem24.put("ITEM_ID", "624");
        infoItem24.put("ITEM_CONT", "");
        infoItems.add(infoItem24);
        IData infoItem25 = new DataMap();
        infoItem25.put("ITEM_ID", "625");
        infoItem25.put("ITEM_CONT", "");
        infoItems.add(infoItem25);
        IData infoItem26 = new DataMap();
        infoItem26.put("ITEM_ID", "626");
        infoItem26.put("ITEM_CONT", "");
        infoItems.add(infoItem26);
                
        return infoItems;
    }
    /**
     * 下发短信
     * @throws Exception 
     */
    public void SendSms(String serial_number, String content) throws Exception
    {
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", "0898");
        sendInfo.put("RECV_OBJECT", serial_number);
        sendInfo.put("RECV_ID", serial_number);
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", content);
        sendInfo.put("REMARK", "改号业务激活提醒");
        sendInfo.put("FORCE_OBJECT", "10086");
        SmsSend.insSms(sendInfo);
    }

    public void setDataInfo(IData data, IData input)
    {
        // TODO Auto-generated method stub
        data.putAll(input);
        data.put("OLD_SN", input.getString("OLD_ID_VALUE"));
        data.put("NEW_SN", input.getString("NEW_ID_VALUE"));
        data.put("WH_HANDLE", input.getString("WH_HANDLE"));
        data.put("CHANNEL", input.getString("CHANNEL"));
        data.put("ID_CARD_TYPE", input.getString("ID_CARD_TYPE"));
        data.put("ID_CARD_NUM", input.getString("ID_CARD_NUM"));
        data.put("OPR_CODE", input.getString("OPR_CODE"));
        data.put("RESERVE", input.getString("RESERVE"));

        String moveinfo = input.getString("MOVE_INFO");
        IDataset info = new DatasetList(moveinfo);

        data.put("MOVE_INFO", info);
        data.put("RSRV_STR6", "END");

    }

    public void setInData(IData indata, IData input) throws Exception
    {
        // TODO Auto-generated method stub
        indata.putAll(input);
        indata.put("SERIAL_NUMBER", input.getString("NEW_SN"));
        String new_eparchy = getSnRoute(indata, input);
        String new_province = input.getString("FLAG", "");

        indata.put("SERIAL_NUMBER", input.getString("OLD_SN"));

        String old_eparchy = getSnRoute(indata, input);
        String old_province = input.getString("FLAG", "");

        if (!"MOD".equals(input.getString("SYNC_TAG", "")))
        {
            indata.put("NEW_SN", input.getString("NEW_SN"));
            indata.put("NEW_EPARCHY", new_eparchy);
            indata.put("NEW_PROVINCE", new_province);
            indata.put("OLD_SN", input.getString("OLD_SN"));
            indata.put("OLD_EPARCHY", old_eparchy);
            indata.put("OLD_PROVINCE", old_province);
            indata.put("RSRV_STR6", input.getString("RSRV_STR6", "BEGIN"));
            indata.put("REMARK", input.getString("RSRV_STR6", "BEGIN"));
        }
        else
        {
            indata.put("NEW_EPARCHY", new_eparchy);
            indata.put("NEW_PROVINCE", new_province);
            indata.put("OLD_EPARCHY", old_eparchy);
            indata.put("OLD_PROVINCE", old_province);
            indata.put("NEW_SN", input.getString("NEW_ID_VALUE"));
            indata.put("OLD_SN", input.getString("OLD_ID_VALUE"));

            IData syn = new DataMap();
            syn.put("NEW_PROVINCE", new_eparchy);
            syn.put("OLD_PROVINCE", new_province);
            syn.put("NEW_EPARCHY", old_eparchy);
            syn.put("OLD_EPARCHY", old_province);
            indata.put("SYNC_INFO", syn);
        }

    }

    public void setMoveInfoTrans(IData input)
    {

        IDataset info = new DatasetList();
        IDataset bizinfos = new DatasetList();
        IDataset movedinfos = new DatasetList();
        String moveinfo = input.getString("MOVE_INFO");

        Map map = Wade3DataTran.strToMap(moveinfo);
        IData moveInfo = Wade3DataTran.wade3To4DataMap(map);

        Object obj = moveInfo.get("MOVED");
        if (obj instanceof String)
        {
            bizinfos.add(0, moveInfo.get("BIZ_INFO"));
            movedinfos.add(0, obj);

        }
        else if (obj instanceof IData)
        {
            bizinfos.add((IData) moveInfo.get("BIZ_INFO"));
            movedinfos.add((IData) moveInfo.get("MOVED"));

        }
        else if (obj instanceof IDataset)
        {

            bizinfos = (IDataset) moveInfo.get("BIZ_INFO");
            movedinfos = (IDataset) moveInfo.get("MOVED");
        }

        for (int i = 0; i < movedinfos.size(); i++)
        {
            IData itemsa = new DataMap();
            itemsa.put("BIZ_INFO", bizinfos.get(i));
            itemsa.put("MOVED", movedinfos.get(i));
            info.add(itemsa);
        }

        input.put("MOVE_INFO", info);
    }

    /**
     * 发送改号短信
     * 
     * @param serial_number
     * @param content
     * @throws Exception
     */
    private void sms(String serial_number, String content) throws Exception
    {
        // TODO Auto-generated method stub
        IData smsData = new DataMap();
        smsData.clear();

        IDataset userInfo = UserInfoQry.getUserinfo(serial_number);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            smsData.put("RECV_OBJECT", serial_number);
            smsData.put("NOTICE_CONTENT", content);
            smsData.put("BRAND_CODE", userInfo.getData(0).getString("BRAND_CODE"));

            SmsSend.insSms(smsData);
        }
        else
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2919, serial_number);

        }

    }

    public IData synSnActivate(IData input, IData indata) throws Exception
    {
        // TODO Auto-generated method stub

        if (!"C".equals(indata.getString("OLD_PROVINCE", "")))
        {
            IData param = new DataMap();

            param.put("OLD_ID_VALUE", indata.getString("SERIAL_NUMBER", ""));
            param.put("NEW_ID_VALUE", indata.getString("RSRV_STR1", ""));

            String channel = getVisit().getInModeCode();
            if (channel.equals("2"))
            {
                channel = "02";
            }
            else if (channel.equals("5"))
            {
                channel = "04";
            }
            else if (channel.equals("1"))
            {
                channel = "07";
            }
            else if (channel.equals("0"))
            {
                channel = "08";
            }
            else if (channel.equals("L"))
            {
                channel = "03";
            }
            else if (channel.equals("K"))
            {
                channel = "01";
            }
            else
            {
                channel = "08"; // 默认营业厅接入
            }
            param.put("CHANNEL", channel);

            if ("1".equals(indata.getString("IS_CANCEL", "")))
                param.put("OPR_CODE", "02"); // 01-激活 02-关联关系取消
            else
                param.put("OPR_CODE", "01"); // 01-激活 02-关联关系取消
            param.put("ACTIVED_TIME", SysDateMgr.getSysTime().replace(":", "").replace("-", "").replace(" ", "")); // 格式
            // yyyymmddhh(24)miss
            param.put("HAND_CHARGE", "0");
            param.put("RESERVE", "");

            // 框架要求特殊处理

            param.put("SERIAL_NUMBER", indata.getString("SERIAL_NUMBER", ""));// 老号码

            // 调用落地方写资料
            IData result = new DataMap();
            result.put("X_RSPCODE", "2998");

            result = changePhonePreRegisterSnActive(param);

            // 激活失败则把status从0更新到9
            if (!"0000".equals(result.getString("X_RSPCODE")) && !"00".equals(result.getString("X_RSPCODE"))
                && !"0".equals(result.getString("X_RSPCODE")))
            {
                updateStatus(indata, param);
            }

            afterSynSnActivate(input, indata, result, param);
            return result;

        }
        else
        {
            IData rData = new DataMap();
            IData inData = new DataMap();

            // 调用一级BOSS的时候要传ROUTETYPE,ROUTEVALUE

            if ("A".equals(indata.getString("NEW_PROVINCE", "")))
            {
                inData.put("ROUTEVALUE", indata.getString("SERIAL_NUMBER", ""));

            }
            else
            {
                inData.put("ROUTEVALUE", indata.getString("RSRV_STR1", ""));

            }

            // 调用一级BOSS的时候要传ROUTETYPE,ROUTEVALUE
            inData.put("ROUTETYPE", "01");

            inData.put("KIND_ID", "BIP2B075_T2001074_0_0"); // 交易唯一标识
            inData.put("X_TRANS_CODE", ""); // 发起方 不需要传X_TRANS_CODE
            inData.put("OLD_ID_VALUE", indata.getString("SERIAL_NUMBER", ""));
            inData.put("NEW_ID_VALUE", indata.getString("RSRV_STR1", ""));
            // inData.put ( "CHANNEL" , pd.getData().getString ( "IN_MODE_CODE" , "" ) ) ;

            String channel = getVisit().getInModeCode();
            if (channel.equals("2"))
            {
                channel = "02";
            }
            else if (channel.equals("5"))
            {
                channel = "04";
            }
            else if (channel.equals("1"))
            {
                channel = "07";
            }
            else if (channel.equals("0"))
            {
                channel = "08";
            }
            else if (channel.equals("L"))
            {
                channel = "03";
            }
            else if (channel.equals("K"))
            {
                channel = "01";
            }
            else
            {
                channel = "08"; // 默认营业厅接入
            }
            inData.put("CHANNEL", channel);

            if ("1".equals(indata.getString("IS_CANCEL", "")))
                inData.put("OPR_CODE", "02"); // 01-激活 02-关联关系取消
            else
                inData.put("OPR_CODE", "01"); // 01-激活 02-关联关系取消
            // inData.put ( "ACTIVATE_TIME" , sd ) ;
            inData.put("ACTIVED_TIME", SysDateMgr.getSysTime().replace(":", "").replace("-", "").replace(" ", "")); // 本地修改，一级BOSS取该字段
            inData.put("RESERVE", "");
            inData.put("HAND_CHARGE", "0");

            IData datas = new DataMap();

            datas = IBossCall.dealInvokeUrl("BIP2B075_T2001074_0_0", "IBOSS6", inData).getData(0);

            // 激活失败则把status从0更新到9
            if (!"0000".equals(datas.getString("X_RSPCODE")) && !"00".equals(datas.getString("X_RSPCODE"))
                && !"0".equals(datas.getString("X_RSPCODE")))
            {
                updateStatus(indata, inData);
            }

            this.afterSynSnActivate(input, indata, datas, inData);
            return datas;
        }

    }

    private IData tuxCustPutVip(IData param, String string)
    {
        // TODO Auto-generated method stub
        return param;
    }
    
    /**
     * 人像比对受理单编号与照片编号同步接口调用
     * @param cycle
     * @throws Exception
     */

    public void SynPicId(IData data) throws Exception
    {
        /*
         * input数据
         * RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxxxxx1365(applyResultActive) {"SIM_CARD_NO":"898600092610F1271346","HIDDEN_IDCARDNUM":"610524199308280030","ROUTETYPE":"01","SUB
  MIT_TYPE":"submit","listener":"onTradeSubmit","PIC_STREAM":"","PAY":"0","MOBILENUM":"15829130981","HIDDEN_CUST_NAME":"房路遥","CSSubmitID":"1","PROVINCE_CODE":"290","c
  ustinfo_PhoneFlag":"0","M2M_FLAG":"","SMSP":"+8613800290500","ID_ITEM_RANGE":"15829130981","IMSI":"460029918718201,","SCAN_TAG":"1","SUBMIT_SOURCE":"CRM_PAGE","ICCID":
  "898600092610F1271346","IDTYPE":"01","EMPTY_CARD_ID":"21170045080040000159","NEW_IMSI":"","FEE":"","PUK2":"36774412","IDVALUES":"15829130981","LEVEL":"09","l":"2017061
  940527308","m":"IBS9236","USER_PASSWD":"123467","RECORDID":"","PUK1":"22923778","p":"simcardmgr.RemoteWriteCardSingle","IDCARDTYPE":"0","ReqSeq":"898BIP2B0212017061916
  5522094224","NEW_SIM_CARD":"898600092610F1271346","service":"ajax","FRONTBASE64":"","IDCARDNUM":"610524199308280030","PIC_ID":"","PIN2":"0000","PIN1":"4321","BACKBASE6
  4":"","custinfo_RemoteVerifyFlag":"1","page":"simcardmgr.RemoteWriteCardSingle","TRADE_EPARCHY_CODE":"0898","COP_SI_PROV_CODE_NAME":"","SIM_FEE_TAG":"","TRADE_ID":"12345676576867"}
         * 
         */ 
        
       
        String serialNumber = data.getString("SERIAL_NUMBER","").trim(); 
        String tradeId = data.getString("TRADE_ID","").trim(); 
        String tradeTypeCode = "799";

        // 调用接口
        IData inParam = new DataMap();
        inParam.put("first_pic_id", data.get("PIC_ID"));
        inParam.put("trade_id", tradeId);
        inParam.put("op_code", tradeTypeCode);
        inParam.put("phone", serialNumber);    
        inParam.put("work_no",CSBizBean.getVisit().getStaffId());
        inParam.put("org_info",  CSBizBean.getVisit().getDepartId());
        IDataset ds = StaffInfoQry.queryValidStaffById(CSBizBean.getVisit().getStaffId());
        if (ds != null && ds.size() == 1) {
            inParam.put("work_name", ds.getData(0).getString("STAFF_NAME"));
        }
        IData param = new DataMap();
        param.put("DEPART_ID",  CSBizBean.getVisit().getDepartId());
        ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", param,Route.CONN_SYS);
        if (ds != null && ds.size() == 1) {
            inParam.put("org_name", ds.getData(0).getString("DEPART_NAME"));
        }
        inParam.put("op_time", SysDateMgr.getSysTime());
        JSONObject jSONObject = null;
        jSONObject = JSONObject.fromObject(inParam);

        String contentJson = jSONObject.toString();
        IData ibossData = new DataMap();
        ibossData.put("buffer", contentJson);

        try {
            String strResult = sendAutoAudit(contentJson);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
    
    /**
     * 1.2受理单编号与照片编号同步接口
     * @param saveBillRequ
     */
    private String sendAutoAudit(String str){
        OutputStreamWriter out = null;
        URL httpurl = null;
        HttpURLConnection httpConn=null;
        boolean flag = false;
        try{
            String url = BizEnv.getEnvString("crm.pic.syn.url");
            //String url ="http://localhost:8080/idvs/get_boss_custpic_info"; 
            httpurl = new URL(url);
      
            httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setRequestProperty("content-type", "text/html");
            out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");
            out.write(str);
            out.flush();
            //serviceLogger.info("|#受理单保存|#发送自动稽核请求|#工单流水号=" + saveBillRequ.getTradeId() + "|#消息内容=" + autoAuditStr+"|#发送成功");
            flag =true;
        }catch(Exception e){
            //serviceLogger.error("|#受理单保存|#发送自动稽核请求失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
        }finally{
            if(null!=out){
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
        }
        InputStream inStream = null;
        String strResult = null;
        BufferedReader br = null;
        if (flag) {
            try {
                    String line = null; 
                    inStream = (InputStream) httpConn.getInputStream();
                //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId());
                    br = new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    while((line = br.readLine())!=null){
                        sb.append(line);                
                    }
                strResult = sb.toString();
                //System.out.print(strResult);
            //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId() + "|#响应状态码=" +strResult);
            } catch (Exception e) {
            //  serviceLogger.info("|#受理单保存|#接收自动稽核响应失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                        br.close();
                    } catch (IOException e) {

                    //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#关闭流失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
                    }
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }
            }
        }
        return strResult;
    }
    
    private static String cutString(String str, int a) {
        String temp = "";
        int k = 0;
        for (int i = 0; i < str.length(); i++)
        {
            byte[] b = (str.charAt(i) + "").getBytes(); //每循环一次，将str里的值放入byte数组
            k = k + b.length;
            if (k > a)
            { //如果数组长度大于给定的长度，随机跳出循环
                break;
            }
            temp = temp + str.charAt(i); //拼接新字符串
        }
        return temp;
   }
    
    public IDataset queryRemoteWriteCustomer(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "IDTYPE");
        IDataUtil.chkParam(input, "ID_ITEM_RANGE");
        String userPasswd = input.getString("USER_PASSWD", "");
        if (StringUtils.isEmpty(userPasswd)) {
            IDataUtil.chkParam(input, "IDCARDTYPE");
            IDataUtil.chkParam(input, "IDCARDNUM");
        }
        String idCardType = input.getString("IDCARDTYPE", "");
        idCardType = changCardType(idCardType);
        IDataset typeIdset = new DatasetList(); 
        typeIdset.add("0");//0:基本资料 
        typeIdset.add("1");//1:个性化资料            
        //xxxxxxxxxxxx测试数据
       //15829130981 610524199308280030  123458     
       //14789016836 440582199608215451  568805
        //String stl = "{CCPASSWD=[\"568805\"], DEPART_ID=[\"35541\"], IDCARD_NUM=[\"440582199608215451\"], IDCARD_TYPE=[\"00\"], ID_TYPE=[\"01\"], IN_MODE_CODE=[\"0\"], KIND_ID=[\"BIP1A010_T1000008_0_0\"], PROVINCE_CODE=[\"HAIN\"], ROUTETYPE=[\"01\"], ROUTEVALUE=[\"14789016836\"], ROUTE_EPARCHY_CODE=[\"0898\"], SERIAL_NUMBER=[\"14789016836\"], TRADE_CITY_CODE=[\"HNSJ\"], TRADE_DEPART_ID=[\"35541\"], TRADE_EPARCHY_CODE=[\"0898\"], TRADE_STAFF_ID=[\"SUPERUSR\"], TYPE_ID=[\"0\", \"1\"], X_TRANS_CODE=[\"IBOSS\"], ORIGDOMAIN=[\"BOSS\"], HOMEDOMAIN=[\"BOSS\"], BIPCODE=[\"BIP1A010\"], ACTIVITYCODE=[\"T1000008\"], BIPVER=[\"\"], ACTIONCODE=[\"1\"], SVCCONTVER=[\"\"], TESTFLAG=[\"0\"], BUSI_SIGN=[\"BIP1A010_T1000008_0_0\"], UIPBUSIID=[\"317053113084192450234\"], TRANSIDO=[\"2017053113084094116308\"], PROCID=[\"\"], TRANSIDH=[\"12510336257\"], PROCESSTIME=[\"\"], TRANSIDC=[\"89801110-t8980-hjgw620170531131844846000095\"], CUTOFFDAY=[\"20170531\"], OSNDUNS=[\"8980\"], HSNDUNS=[\"8910\"], CONVID=[\"7d70d352-ebaf-439b-a87b-6428bf17b452\"], MSGSENDER=[\"8981\"], MSGRECEIVER=[\"8911\"], X_RSPTYPE=[\"0\"], X_RSPCODE=[\"0000\"], X_RSPDESC=[\"成功\"], X_RESULTINFO=[\"受理成功\"], X_RESULTCODE=[\"0000\"], INFO_CONT=[{INFO_TYPEID=[\"0\"], INFO_ITEMS=[{ITEM_ID=[\"101\", \"103\", \"104\", \"114\", \"118\", \"119\", \"122\", \"123\", \"126\", \"127\", \"128\", \"129\"], ITEM_CONT=[\"郑植锋\", \"00\", \"440582199608215451\", \"0\", \"20170321113943\", \"00\", \"66720431\", \"0\", \"西藏拉萨\", \"\", \"\", \"全球通\"]}]}, {INFO_TYPEID=[\"1\"], INFO_ITEMS=[{ITEM_ID=[\"601\", \"602\", \"603\", \"604\", \"605\", \"606\", \"607\", \"608\", \"609\", \"610\", \"611\", \"627\", \"628\"], ITEM_CONT=[\"郑植锋\", \"0\", \"20\", \"00\", \"440582199608215451\", \"2\", \"\", \"14789016836\", \"\", \"\", \"\", \"\", \"\"]}]}], ACNT_PAYAMOUNT=[\"0\"], ACNT_BALANCE=[\"0\"], BIPSTATUS=[\"1\"], IBBFEE=[\"0\"], IBBFEEDIR=[\"0\"], IBBFEEDIRTAG=[\"0\"], IBAFEE=[\"0\"], IBAFEEDIR=[\"3\"], IBSFEE=[\"0\"], IBSFEEDIR=[\"0\"], RECONTAG=[\"0\"]}";             
        //IData iboosResult = Wade3DataTran.wade3To4DataMap(Wade3DataTran.strToMap(stl));            
        IData iboosResult = IBossCall.querySingleRemoteCust(input.getString("IDTYPE"), input.getString("ID_ITEM_RANGE"), userPasswd, idCardType, input.getString("IDCARDNUM"),typeIdset);
        
        if(IDataUtil.isEmpty(iboosResult)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "改号业务调用IBoss返回数据为空！");
        }
        IData info = new DataMap();
        info.put("IDTYPE", iboosResult.getString("ID_TYPE"));
        info.put("IDVALUE", iboosResult.getString("SERIAL_NUMBER"));
        if("0000".equals(iboosResult.get("X_RSPCODE"))
                && "0000".equals(iboosResult.get("X_RESULTCODE"))) {//返回成功
             info.put("ACNTPAYAMOUNT",round(Double.parseDouble(iboosResult.getString("ACNT_PAYAMOUNT","0"))/1000.0));
             info.put("ACNTBALANCE", round(Double.parseDouble(iboosResult.getString("ACNT_BALANCE","0"))/1000.0));
             IDataset infoContList = iboosResult.getDataset("INFO_CONT");

             for (int i = 0, size = infoContList.size(); i < size; i++) {
                    IData infoCont = infoContList.getData(i);
                    String infoTypeId = infoCont.getString("INFO_TYPEID");
                    IDataset infoItems = infoCont.getDataset("INFO_ITEMS");

                if ("0".equals(infoTypeId)) {//客户基本资料
                    for (int j = 0; j < infoItems.size(); j++) {//按目前规范，该值应该为1,
                        IData infoItem = infoItems.getData(j);
                        IDataset itemIdList = infoItem.getDataset("ITEM_ID");
                        IDataset itemContList = infoItem.getDataset("ITEM_CONT");
                        if (itemIdList != null && itemIdList.size() > 0) {
                            for (int z = 0; z < itemIdList.size(); z++) {
                                String itemId = (String) itemIdList.get(z);
                                String itemCont = "";

                                if (itemContList != null && itemContList.size() > z && itemContList.get(z) != null) {
                                    itemCont = (String) itemContList.get(z);
                                }

                                String keyName = this.transItemId(itemId);
                                if (StringUtils.isEmpty(keyName)) {
                                    keyName = itemId;
                                }
                                info.put(keyName, itemCont);
                            }
                        }
                    }
                } else if ("1".equals(infoTypeId)) {//客户个性化资料
                    for (int j = 0; j < infoItems.size(); j++) {//按目前规范，该值应该为1,
                        IData infoItem = infoItems.getData(j);
                        IDataset itemIdList = infoItem.getDataset("ITEM_ID");
                        IDataset itemContList = infoItem.getDataset("ITEM_CONT");
                        if (itemIdList != null && itemIdList.size() > 0) {
                            for (int z = 0; z < itemIdList.size(); z++) {
                                String itemId = (String) itemIdList.get(z);
                                String itemCont = "";
                                if (itemContList != null && itemContList.size() > z && itemContList.get(z) != null) {
                                    itemCont = (String) itemContList.get(z);
                                }
                                String keyName = this.transItemId(itemId);
                                if (StringUtils.isEmpty(keyName)) {
                                    keyName = itemId;
                                }
                                info.put(keyName, itemCont);
                            }
                        }
                    }
                }
             }
        }else{
             CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地客户资料校验失败，请确认输入信息是否正确！"+iboosResult.getString("X_RESULTINFO"));
        }
        String crmBalance = iboosResult.getString("CRM_BALANCE", "");
        if (!iboosResult.getString("CRM_BALANCE", "").equals(""))
        {
            double balance = Double.parseDouble(iboosResult.getString("CRM_BALANCE", ""));
            balance = balance / 1000.0;
            crmBalance = Double.toString(balance);
        }
        String debtBalance = iboosResult.getString("DEBT_BALANCE", "");
        if (!iboosResult.getString("DEBT_BALANCE", "").equals(""))
        {
            double dbalance = Double.parseDouble(iboosResult.getString("DEBT_BALANCE", ""));
            dbalance = dbalance / 1000.0;
            debtBalance = Double.toString(dbalance);
        }
        info.put("BRAND_CODE", info.getString("BRAND_CODE", ""));//129
        info.put("OPEN_DATE", info.getString("OPEN_DATE", ""));//118        
        info.put("SCORE", info.getString("SCORE", ""));//114  
        info.put("PUK", info.getString("PUK", ""));//122
        info.put("USER_STATE_CODESET", info.getString("USER_STATE_CODESET", ""));//119
        info.put("CUST_NAME", info.getString("CUST_NAME") == null ? "" : info.getString("CUST_NAME"));//101
        info.put("DEBT_BALANCE", debtBalance);
        info.put("BALANCE", crmBalance);
        info.put("IDCARDTYPE", info.getString("IDCARDTYPE") == null ? "" : info.getString("IDCARDTYPE"));//103
        
        info.put("IDCARDNUM", info.getString("IDCARDNUM") == null ? "" : info.getString("IDCARDNUM"));//104
        info.put("PSPT_ADDR", info.getString("PSPT_ADDR") == null ? "" : info.getString("PSPT_ADDR"));
        info.put("GPRS_TAG", info.getString("GPRS_TAG") == null ? "" : info.getString("GPRS_TAG"));
        info.put("ROAM_TYPE", info.getString("ROAM_TYPE") == null ? "" : info.getString("ROAM_TYPE"));
        info.put("OPER_FEE", "0");
        info.put("LEVEL", info.getString("CREDIT_LEVEL") == null ? "" : info.getString("CREDIT_LEVEL"));
        info.put("USER_MGR", info.getString("CUST_MANAGER") == null ? "" : info.getString("CUST_MANAGER"));
        info.put("USER_MGR_NUM", info.getString("CUST_MANAGER_PHONE") == null ? "" : info.getString("CUST_MANAGER_PHONE"));
        info.put("SERV_OPR", info.getString("SERV_OPR") == null ? "" : info.getString("SERV_OPR"));
        String lanuchTdType = encodeIdType(info.getString("IDCARDTYPE"));
        info.put("IDCARDTYPE", lanuchTdType);
        info.put("ICARDSTYPES", StaticUtil.getStaticValue("IBOSS_PSPT_TYPE_CODE", lanuchTdType));
        info.put("LEVELS", StaticUtil.getStaticValue("IBOSS_STAR_LEVEL", info.getString("LEVEL")));
        String provCode = iboosResult.getString("HSNDUNS", "");
        if (provCode.length() > 1)
        {
            info.put("COP_SI_PROV_CODE", provCode.substring(0, provCode.length() - 1));
        }
        IDataset set = new DatasetList();
        info.put("PSPT_TYPE_CODE", info.getString("IDCARDTYPE") == null ? "" : info.getString("IDCARDTYPE"));
        IData authCustInfo = new DataMap();
        authCustInfo.putAll(info);
        info.put("AUTH_CUST_INFO", authCustInfo);
        set.add(info);
        return set;         
    }
    
    public String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    public static String round(double value) {  
        DecimalFormat df2 = new DecimalFormat("###.00");  
        return df2.format(value); 
       }  
    
    private String changCardType(String idCardType) {
        
        if("0".equals(idCardType)){
            idCardType = "00";//身份证件
            
        }else if("1".equals(idCardType)){
            idCardType = "01";//VIP卡

        }else if("A".equals(idCardType)){
            idCardType = "02";//护照

        }else if("C".equals(idCardType)){
            idCardType = "04";//军官证

        }else if("K".equals(idCardType)){
            idCardType = "05";//武装警察身份证

        }else if("Z".equals(idCardType)){
            idCardType = "99";//其他证件
        }
        return idCardType;
    }
    
    private String transItemId(String itemId) {
        String keyName = "";
        if (StringUtils.equalsIgnoreCase("100", itemId)) {//客户标识
            keyName = "CUST_ID";
        } else if (StringUtils.equalsIgnoreCase("101", itemId)) {//客户姓名
            keyName = "CUST_NAME";
        } else if (StringUtils.equalsIgnoreCase("103", itemId)) {//证件类别
            keyName = "IDCARDTYPE";
        } else if (StringUtils.equalsIgnoreCase("104", itemId)) {//证件号码
            keyName = "IDCARDNUM";
        } else if (StringUtils.equalsIgnoreCase("114", itemId)) {//客户积分:可用积分余额
            keyName = "SCORE";
        } else if (StringUtils.equalsIgnoreCase("118", itemId)) {//注册日期/入网时间:YYYYMMDDhhmmss
            keyName = "OPEN_DATE";
        } else if (StringUtils.equalsIgnoreCase("119", itemId)) {//用户状态
            keyName = "USER_STATE_CODESET";
        } else if (StringUtils.equalsIgnoreCase("122", itemId)) {//PUK码
            keyName = "PUK";
        } else if (StringUtils.equalsIgnoreCase("123", itemId)) {//信用度
            keyName = "CREDIT";
        } else if (StringUtils.equalsIgnoreCase("124", itemId)) {//增值业务开放情况
        } else if (StringUtils.equalsIgnoreCase("127", itemId)) {//用户星级
            keyName = "CREDIT_LEVEL";
        } else if (StringUtils.equalsIgnoreCase("128", itemId)) {//客户俱乐部
        } else if (StringUtils.equalsIgnoreCase("129", itemId)) {//客户品牌
            keyName = "BRAND_CODE";
        } 
        else if (StringUtils.equalsIgnoreCase("601", itemId)) {//姓名
            keyName = "NAME";
        } else if (StringUtils.equalsIgnoreCase("602", itemId)) {//性别
            keyName = "SEX";
        } else if (StringUtils.equalsIgnoreCase("603", itemId)) {//年龄
            keyName = "AGE";
        } else if (StringUtils.equalsIgnoreCase("604", itemId)) {//身份证件类型
            keyName = "PSTP_TYPE_CODE";
        } else if (StringUtils.equalsIgnoreCase("605", itemId)) {//身份证件号码
            keyName = "PSTP_ID";
        } else if (StringUtils.equalsIgnoreCase("606", itemId)) {//婚姻状况
            keyName = "MARRIAGE";
        } else if (StringUtils.equalsIgnoreCase("607", itemId)) {//教育程度
            keyName = "EDUCATE_DEGREE";
        } else if (StringUtils.equalsIgnoreCase("608", itemId)) {//手机号码
            keyName = "SERIAL_NUMBER";
        } else if (StringUtils.equalsIgnoreCase("609", itemId)) {//联系电话
            keyName = "CONTACT_PHONE";
        } else if (StringUtils.equalsIgnoreCase("610", itemId)) {//联系地址
            keyName = "CONTACT_ADDRESS";
        } else if (StringUtils.equalsIgnoreCase("611", itemId)) {//客户经理工号/姓名
            keyName = "CUST_MANAGER";
        } else if (StringUtils.equalsIgnoreCase("627", itemId)) {//客户经理联系电话
            keyName = "CUST_MANAGER_PHONE";
        } else if (StringUtils.equalsIgnoreCase("628", itemId)) {//工作单位
            keyName = "WORK_NAME";
        } else if (StringUtils.equalsIgnoreCase("621", itemId)) {//本地市话月消费额(当月)
        } else if (StringUtils.equalsIgnoreCase("622", itemId)) {//国内长途月消费额(当月)
        }  else if (StringUtils.equalsIgnoreCase("623", itemId)) {//国际长途月消费额(当月)
        }  else if (StringUtils.equalsIgnoreCase("624", itemId)) {//国内漫游月消费额(当月)
        }  else if (StringUtils.equalsIgnoreCase("625", itemId)) {//国际漫游月消费额(当月)
        }  else if (StringUtils.equalsIgnoreCase("629", itemId)) {//主要增值业务收入(当月)
        }
        return keyName;
      }
      

}
