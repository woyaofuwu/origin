
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.requestdata.CreateFixTelUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest.BaseBuildCreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;

public class BuildCreateFixTelUserRequestData extends BaseBuildCreateUserRequestData implements IBuilder
{
    /**
     * 构建登记流程 业务数据输入，后续逻辑处理从RequestData中获取数据，即这里的brd
     */

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) brd;
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) brd;
        String batchDealType = param.getString("BATCH_OPER_TYPE");
        if ("BATAPPENDTRUNKUSER".equals(batchDealType))
        {
            baseCreatePersonUserRD.setMainProduct(param.getData("TF_B_TRADE_SVC").getString("PRODUCT_ID"));// 设置产品信息
            baseCreatePersonUserRD.getUca().setProductId(param.getData("TF_B_TRADE_SVC").getString("PRODUCT_ID"));
            baseCreatePersonUserRD.getUca().setBrandCode(createFixTelUserRD.getMainProduct().getBrandCode());
            baseCreatePersonUserRD.getUca().setAcctDay(param.getString("ACCT_DAY", "1"));
        }
        else
        {
            super.buildBusiRequestData(param, brd);

        }
        //地域编码
        createFixTelUserRD.setAreaType(param.getString("AREA_TYPE"));
        //清算设置
        createFixTelUserRD.setClearAccount(param.getString("CLEAR_ACCOUNT"));
        //账单类型、
        createFixTelUserRD.setPaperType(param.getString("PAPER_TYPE"));
        // 初始化参数，隐藏信息
        createFixTelUserRD.setOpenLimitTag(param.getString("OPEN_LIMIT_TAG"));
        createFixTelUserRD.setOpenLimitCount(param.getString("OPEN_LIMIT_COUNT"));
        createFixTelUserRD.setCustNameLimit(param.getString("CUSTNAME_LIMIT"));
        createFixTelUserRD.setDefaultPwdMode(param.getInt("DEFAULT_PWD_MODE", 4));
        createFixTelUserRD.setDefaultPwd(param.getString("DEFAULT_PWD", "123456"));
        createFixTelUserRD.setDefaultPwdLength(param.getInt("DEFAULT_PWD_LENGTH", 6));
        createFixTelUserRD.setDefaultUserType(param.getString("DEFAULT_USER_TYPE", "0"));
        createFixTelUserRD.setDefaultPsptType(param.getString("DEFAULT_PSPT_TYPE"));
        createFixTelUserRD.setDefaultPayMode(param.getString("DEFAULT_PAY_MODE"));
        createFixTelUserRD.setChrBlackCheckMode(param.getString("CHR_BLACKCHECKMODE"));
        createFixTelUserRD.setChrCheckOweFeeByPspt(param.getString("CHR_CHECKOWEFEEBYPSPT"));
        createFixTelUserRD.setChrCheckOweFeeByPsptAllUser(param.getString("CHR_CHECKOWEFEEBYPSPT_ALLUSER"));
        createFixTelUserRD.setResCheckByDepart(param.getString("RES_CHECK_BY_DEPART"));
        createFixTelUserRD.setChrAutoPasswd(param.getString("CHR_AUTO_PASSWD"));
        createFixTelUserRD.setProvOpenAdvancePayFlag(param.getString("PROV_OPEN_ADVANCE_PAY_FLAG"));
        createFixTelUserRD.setProvOpenAdvancePay(param.getString("PROV_OPEN_ADVANCE_PAY"));
        createFixTelUserRD.setProvOpenOperFeeFlag(param.getString("PROV_OPEN_OPERFEE_FLAG"));
        createFixTelUserRD.setProvOpenOperFee(param.getString("PROV_OPEN_OPERFEE"));
        createFixTelUserRD.setChrUserGgCard(param.getString("CHR_USEGGCARD", ""));
        createFixTelUserRD.setChrCheckSeleNum(param.getString("CHR_CHECKSELENUM"));
        createFixTelUserRD.setReversePolarity(param.getString("REVERSE_POLARITY"));
        createFixTelUserRD.setSecret(param.getString("SECRET"));
        // 其他隐藏信息
        createFixTelUserRD.setCheckResultCode(param.getString("CHECK_RESULT_CODE"));
        createFixTelUserRD.setCheckPsptCode(param.getString("CHECK_PSPT_CODE"));
        createFixTelUserRD.setResKindName(param.getString("RES_KIND_NAME"));

        // 基本信息
        createFixTelUserRD.setNoteType(param.getString("NOTE_TYPE", "0"));
        createFixTelUserRD.setAcctTag(param.getString("ACCT_TAG", "0"));
        createFixTelUserRD.setOpenMode(param.getString("OPEN_MODE", "0"));
        createFixTelUserRD.setInPhone(param.getString("IN_PHONE", ""));
        createFixTelUserRD.setSuperBankCode(param.getString("SUPER_BANK_CODE"));

        // 用户网别
        createFixTelUserRD.getUca().getUser().setNetTypeCode(PersonConst.FIX_TEL_NET_TYPE_CODE);

        // 号码资源相关信息
        createFixTelUserRD.setResKindCode(param.getString("RES_KIND_CODE", ""));
        createFixTelUserRD.setResTypeCode(param.getString("RES_TYPE_CODE", ""));
        createFixTelUserRD.setSwitchId(param.getString("SWITCH_ID", ""));
        createFixTelUserRD.setSwitchType(param.getString("SWITCH_TYPE", ""));
        createFixTelUserRD.setStandAddress(param.getString("STAND_ADDRESS", ""));
        createFixTelUserRD.setDetailAddress(param.getString("DETAIL_ADDRESS", ""));
        createFixTelUserRD.setStandAddressCode(param.getString("STAND_ADDRESS_CODE", ""));
        createFixTelUserRD.setSignPath(param.getString("SIGN_PATH", ""));

        // 账户优惠
        createFixTelUserRD.setAcctDiscount(param.getString("ACCT_DISCNT"));

        // 设备是否为空待定,此处先假定前台必须选择设备
        if (!param.getString("DEVICE_STRING", "").equals(""))
            createFixTelUserRD.setDeviceRes(new DatasetList(param.getString("DEVICE_STRING")));

        // 千群百号业务相关代表号处理
        createFixTelUserRD.setMainServNumFlg(param.getInt("MAIN_SERI_FLG"));
        createFixTelUserRD.setMainUserId(param.getString("MAIN_USER_ID"));
        createFixTelUserRD.setMainAcctId(param.getString("MAIN_ACCT_ID"));
        createFixTelUserRD.setMainCustId(param.getString("MAIN_CUST_ID"));
        createFixTelUserRD.setIsBat(param.getString("IS_BAT"));
        createFixTelUserRD.setMainSerNum(param.getString("MAIN_SERIAL_NUMBER"));
        
        createFixTelUserRD.setAgentCustName(param.getString("AGENT_CUST_NAME",""));
        createFixTelUserRD.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE", ""));
        createFixTelUserRD.setAgentPsptId(param.getString("AGENT_PSPT_ID",""));
        createFixTelUserRD.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR","")); 

    }

    /**
     * 定义requestData对象
     */

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CreateFixTelUserRequestData();
    }
}
