
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;

/**
 * 账户资料修改服务类
 * 
 * @author liutt
 */
public class ModifyAcctInfoSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(ModifyAcctInfoSVC.class);

    private static final long serialVersionUID = 1L;

    // 判断是否是统付用户
    public IDataset checkUserSpecialepay(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        String payModeCode = params.getString("PAY_MODE_CODE");
        String erroInfo = "";
        String flag = "0";
        IData returnInfo = new DataMap();
        IData info = new DataMap();
        IDataset result = PayRelaInfoQry.getSpecPayByUserId(userId);// 查询用户统付信息
        if (IDataUtil.isNotEmpty(result)) {
            flag = "1";
            info = UcaInfoQry.qryUserInfoByUserId(result.getData(0).getString("USER_ID_A"));
        } else {
            flag = "0";
        }

        if (StringUtils.equals("0", payModeCode) && StringUtils.equals("1", flag)) {
            erroInfo = "用户有统付，统付编码:" + info.getString("SERIAL_NUMBER") + "，是否要将帐户类型改成现金？\n \n选择[确定]继续办理本业务，[取消]退出本业务？";
            returnInfo.put("X_RESULT_CODE", "1");
            returnInfo.put("PLUG_TYPE", "2");
            returnInfo.put("X_RESULTINFO", erroInfo);
        }

        if (StringUtils.equals("1", payModeCode))//payModeCode==1, 账户类型 为 ：托收
        {            
            IData cond = new DataMap();
            //REQ201605160029 办理托收时新增判断是否已维护对应的付费银行和收款银行关系 start
            cond.clear(); 
            cond.put("SUPER_BANK_CODE", params.getString("BANK_CODE"));
            IDataset ds = CSAppCall.call("AM_CRM_QryRecBankMsg", cond);
            if (DataSetUtils.isBlank(ds)) {
                erroInfo = "请先维护付费银行与收款银行的对应关系才能办理托收，请到付费银行与收款银行对应维护界面办理。";
                returnInfo.put("X_RESULT_CODE", "1");
                returnInfo.put("X_RESULTINFO", erroInfo);
                return new DatasetList(returnInfo);   
            }
            //REQ201605160029 办理托收时新增判断是否已维护对应的付费银行和收款银行关系 end
            
            
            //REQ201606140013关于优化中行托收自动扣款业务的需求 start
            IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(acctInfo)) {
                cond.put("ACCT_ID", acctInfo.get("ACCT_ID"));
                ds = CSAppCall.call("AM_CRM_GetAccountOwefee", cond);
                if (DataSetUtils.isNotBlank(ds)) {
                    IData acctData = ds.getData(0);
                    if (!acctData.isEmpty()) {//有欠费
                                                
                        //检查当前工号是否有权限，在欠费情况下办理托收
                        String privTag = "0";
                        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "ACCT_PAYMODE_CHGPRIV")) {//有权限
                            privTag = "1";
                        }
                        if ("1".equals(privTag)) {//有权限
                            erroInfo = "您已特殊办理银行托收业务，为避免停机给您造成不便，请您务必及时缴清往月欠费。";
                            returnInfo.put("X_RESULT_CODE", "2");
                            returnInfo.put("X_RESULTINFO", erroInfo);
                            return new DatasetList(returnInfo);                           
                        }else{//无权限                            
                            erroInfo = "该服务号码有欠费记录，不能办理托收业务。";
                            returnInfo.put("X_RESULT_CODE", "1");
                            returnInfo.put("X_RESULTINFO", erroInfo);
                            return new DatasetList(returnInfo);   
                        }
                                                
                        /*
                        String month = acctData.getString("MIN_OWE_MONTH").substring(0,6);// 最早欠费月份，格式为：yyyymmdd MIN_OWE_MONTH
                        long fee = acctData.getLong("OWE_FEE"); // 欠费金额，单位：分
                        String curMonth = SysDateMgr.getSysDateYYYYMMDD().substring(0, 6);
                        int compareMonth = 0;
                        if (Integer.parseInt(curMonth.substring(4, 6)) == 1 || Integer.parseInt(curMonth.substring(4, 6)) == 2) {// 当前月份是1和2月
                            compareMonth = Integer.parseInt(curMonth) - 90;
                        } else {
                            compareMonth = Integer.parseInt(curMonth) - 2;
                        }
                        if (fee > 0 && Integer.parseInt(month) <= compareMonth) {
                            
                        }*/                        
                        
//                        erroInfo = "该服务号码有欠费记录，不能办理托收业务。";
//                        returnInfo.put("X_RESULT_CODE", "1");
//                        returnInfo.put("X_RESULTINFO", erroInfo);
//                        return new DatasetList(returnInfo);

                    }
                }
            }
            //REQ201606140013关于优化中行托收自动扣款业务的需求 end
            


            
        }

        IDataset dataset = new DatasetList();
        dataset.add(returnInfo);
        return dataset;
    }

    /**
     *获取用户账期相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctDayInfo(IData input) throws Exception
    {

        IData acctPayInfo = new DataMap();
        String userId = input.getString("USER_ID");
        IDataset userAcctDays = UserAcctDayInfoQry.getUserAcctDay(userId);// 据USER_ID查询用户的结账日以及首次出账日
        IData userAcctDay = new DataMap();
        if (IDataUtil.isNotEmpty(userAcctDays))
        {
            userAcctDay = userAcctDays.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_137);// "获取用户账期资料无数据，业务办理失败！"
        }

        if (!StringUtils.equals("1", userAcctDay.getString("ACCT_DAY")))
        {
            acctPayInfo.put("ACCT_DAY_TAG", "0");// 做页面标记"0"代表非自然月，1代表自然月
        }
        else
        {
            acctPayInfo.put("ACCT_DAY_TAG", "1");
        }

        // 判断是否存在预约账期
        IData bookUserAcctDays = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);//
        if (IDataUtil.isNotEmpty(bookUserAcctDays) && StringUtils.isNotBlank(bookUserAcctDays.getString("NEXT_ACCT_DAY")))
        {
            acctPayInfo.put("NEXT_ACCT_DAY", bookUserAcctDays.getString("NEXT_ACCT_DAY"));
        }
        else
        {
            acctPayInfo.put("NEXT_ACCT_DAY", "0");
        }

        if (StringUtils.equals("0", acctPayInfo.getString("ACCT_DAY_TAG")))
        {// 判断非自然月，办理托收时的生效时间，给前台展示
            // 获取下帐期第一天
            String nextAcct = SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), userAcctDay.getString("ACCT_DAY"), userAcctDay.getString("FIRST_DATE"));
            acctPayInfo.put("BOOK_ACCT_DAY", nextAcct);
        }
        else
        {
            acctPayInfo.put("BOOK_ACCT_DAY", SysDateMgr.getSysDate());
        }

        IDataset dataset = new DatasetList();
        dataset.add(acctPayInfo);
        return dataset;
    }

    /**
     * 根据上级银行获取银行数据
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBankInfoBySup(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        return BankInfoQry.getBankBySuperBank(super_bank_code, null);
    }

    /**
     * 根据银行名称或编码模糊查询
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryBurBankInfo(IData params) throws Exception
    {
        String super_bank_code = params.getString("SUPERBANK_CODE");
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String bank_code = params.getString("BANK_OR_CODE", "").toUpperCase();
        String bank = params.getString("BANK_OR_CODE", "").toUpperCase();
        return BankInfoQry.getBankByBank(eparchy_code, super_bank_code, bank_code, bank);
    }
}
