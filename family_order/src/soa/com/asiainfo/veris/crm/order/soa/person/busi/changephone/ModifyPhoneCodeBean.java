
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.NetNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.OperFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;

public class ModifyPhoneCodeBean extends CSBizBean
{

    /**
     * 校验新手机号
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkphone(IData input) throws Exception
    {
        String oldSimCardNo = input.getString("OLD_SIM_CARD_NO", "");
        String newSn = input.getString("NEW_SERIAL_NUMBER", "");
        String sn = input.getString("SERIAL_NUMBER");
        IDataset outputSet = new DatasetList();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);

        IDataset uuset = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userInfo.getString("USER_ID"), "30", null);
        if (IDataUtil.isNotEmpty(uuset))
        {
            if (!sn.substring(0, 5).equals(newSn.substring(0, 5)))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户是一卡双号用户，新老号码必须同号段，否则请先取消一卡双号关系！");
            }
        }

        // 调用资源接口，进行资源信息校验 选占 TCS_CheckResource
        IDataset resultSet = ResCall.checkResoureForPhone(newSn, "0", "0", "0", "1", sn, userInfo.getString("CITY_CODE"), "143");
        if (IDataUtil.isEmpty(resultSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取改号号码选占信息为空！");
        }

        IData result = resultSet.getData(0);

        IData feeData = new DataMap();
        feeData.put("FEE", result.getString("RESERVE_FEE", "0"));
        outputSet.add(feeData);
        return outputSet;
    }

    /**
     * 校验新sim卡
     * 
     * @author hank
     * @params PageData,TradeData
     * @return IData
     * @exception
     */
    public IDataset checkSimCard(IData input) throws Exception
    {
        IDataset outputSet = new DatasetList();
        // 输入数据
        String oldSimNo = input.getString("OLD_SIM_CARD_NO");
        String newSimNo = input.getString("NEW_SIM_CARD_NO");
        String newMphone = input.getString("NEW_SERIAL_NUMBER");

        // 调用资源接口，进行资源信息校验 TCS_CheckResource
        IDataset dataset = ResCall.checkResourceForSim(newSimNo, newMphone, "0", "1", "", "", "1", "0", "");

        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "改号SIM卡信息选占错误！");
        }

        SimCardBean bean = BeanManager.createBean(SimCardBean.class);
        IData feeData = bean.getSimCardPrice(oldSimNo, newSimNo, input.getString("SERIAL_NUMBER"), "143");
        IData returnInfo = new DataMap();
        returnInfo.put("FEE_DATA", feeData);
        returnInfo.put("RES_INFO", dataset.getData(0));
        outputSet.add(returnInfo);

        return outputSet;
    }

    /**
     * 校验用户是否能办理换号业务
     * 
     * @param input
     * @throws Exception
     */
    public IData checkTrade(IData input) throws Exception
    {

        // TODO 平台业务表结构已改，sql还是海南的老sql 暂时注释
        IData output = new DataMap();
        IDataset userPlatSvcInfo = new DatasetList();// 此查询需要修改//UserPlatSvcInfoQry.getPlatSvcByUserBizType(input.getString("USER_ID"),
        // "54");
        if (IDataUtil.isNotEmpty(userPlatSvcInfo))
        {
            String tmpStr = "false";
            for (int i = 0; i < userPlatSvcInfo.size(); i++)
            {
                if (userPlatSvcInfo.getData(i).getString("BIZ_STATE_CODE", "").equals("E"))
                {
                    tmpStr = "true";
                }
            }
            if (tmpStr.equals(true))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_247);
            }

        }
        // 处理vpmn用户
        IDataset relationUUSet = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(input.getString("USER_ID"), "20");
        if (IDataUtil.isNotEmpty(relationUUSet) && relationUUSet.getData(0).getString("SERIAL_NUMBER_A").equals("V0HN001010"))
        {
            output.put("IS_VPMN", true);
            // 判断用户是否vpmn以及员工是否是客服数据班 td_s_commpara表 subsys_code='CSM' AND param_attr=870
            IDataset tmp = ParamInfoQry.getCommparaByCode("CSM", "870", getVisit().getStaffId(), getTradeEparchyCode());
            if (IDataUtil.isEmpty(tmp))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_255);
            }
        }

        // 判断用户是否订购了来电拒接业务
        IDataset inComingSet = UserOtherInfoQry.getOtherInfoByCodeUserId(input.getString("USER_ID"), "1301");
        if (IDataUtil.isNotEmpty(inComingSet))
        {
            output.put("IS_INCOMING", true);
        }

        // 判断用户是否订购了除biz_type_code=19以外的平台业务
        IDataset platSvcSet = new DatasetList();// UserPlatSvcInfoQry.queryPlatSvcByUserId(input.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(platSvcSet))
        {
            for (int i = 0; i < platSvcSet.size(); i++)
            {
                output.put("IS_ORDERPLAYSVC", true);
                if (!"19".equals(platSvcSet.getData(i).getString("BIZ_TYPE_CODE")))
                {
                    output.put("IS_ORDERPLAYSVC", true);
                    break;
                }
            }
        }

        // 携入用户不允许办理该业务 sql有问题
        IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(input.getString("SERIAL_NUMBER"), null);
        if (IDataUtil.isNotEmpty(msisdnInfo))
        {
            if (!msisdnInfo.getString("ASP").equals("1"))
            {
                CSAppException.apperr(NetNpException.CRM_NEPNP_7);
            }
        }

        return output;

    }

    /***
     * 获取sim卡费
     * 
     * @param pd
     * @param td
     * @param simcardNo
     * @return
     * @throws Exception
     */
    public IData fnGenSimCardFee(String simcardNo) throws Exception
    {

        IDataset oldSimInfos = ResCall.getSimCardInfo("0", simcardNo, "", "1");
        if (IDataUtil.isEmpty(oldSimInfos))
        {
            //
        }
        String resTypeCode = oldSimInfos.getData(0).getString("RES_TYPE_CODE");
        String emptyCardId = oldSimInfos.getData(0).getString("EMPTY_CARD_ID");
        String resKindCode = oldSimInfos.getData(0).getString("RES_KIND_CODE");

        if (StringUtils.isNotEmpty(emptyCardId))
        {
            IDataset emptyCardInfos = ResCall.getEmptycardInfo(emptyCardId, "", "USE");
            if (IDataUtil.isEmpty(emptyCardInfos))
            {

            }
            resTypeCode = emptyCardInfos.getData(0).getString("RES_TYPE_CODE");
            resKindCode = emptyCardInfos.getData(0).getString("RES_KIND_CODE");

        }

        DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", "143", resKindCode, resTypeCode);

        // 通过SIM卡类型，获取改号费用
        String feeMode = "0";
        String feeTypeCode = "10";// devicePrice.getData(0).getString("FEEITEM_CODE");
        String fee = "1500";// devicePrice.getData(0).getString("DEVICE_PRICE");
        IData result = new DataMap();
        result.put("FEE_MODE", feeMode);
        result.put("FEE_TYPE_CODE", feeTypeCode);
        result.put("FACT_PAY_FEE", fee);
        result.put("FEE", fee);
        // IData result1 = td.getBaseCommInfo();
        // result1.put("NEW_SIM_TYPE", oldSimcardInfo.getString("RES_KIND_CODE"));
        // result1.put("NEW_CAPACITY_CODE", oldSimcardInfo.getString("CAPACITY_TYPE_CODE"));
        // td.setBaseCommInfo(result1);
        return result;
    }

    /**
     * 获取营业费用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getOperFee(String tradeTypeCode) throws Exception
    {

        return OperFeeInfoQry.getFeeItem(tradeTypeCode);

    }

    public IData getSimCardFreeData(IData oldSimCard, IData newSimCard, IData userInfo) throws Exception
    {
        IData output = new DataMap();
        String oldEmptyCardId = oldSimCard.getString("EMPTY_CARD_ID", "");
        String newEmptyCardId = newSimCard.getString("EMPTY_CARD_ID", "");
        String oldSimTypeCode = oldSimCard.getString("SIM_TYPE_CODE");
        String newSimTypeCode = newSimCard.getString("SIM_TYPE_CODE");
        String oldCapacityType = oldSimCard.getString("CAPACITY_TYPE_CODE");
        String newCapacityType = newSimCard.getString("CAPACITY_TYPE_CODE");
        String newResKinkCode = newSimCard.getString("SIM_TYPE_CODE", "");
        boolean oldWritecardflag = oldSimCard.getBoolean("WRITE_CARD_FLAG", false);
        boolean newWritecardflag = newSimCard.getBoolean("WRITE_CARD_FLAG", false);
        boolean ispay = false;// SimCardQueryBean.isPayFee(oldWritecardflag, newWritecardflag, newSimTypeCode,
        // oldSimTypeCode);
        if (ispay)
        {
            output.put("FREE_TAG", "true");
            output.put("TIPS_TYPE", "2");
            output.put("TIPS_INFO", "非专属卡【旧卡】转为专属卡【新卡】不收卡费！");
        }
        else
        {
            IData feeData = null;// SimCardQueryBean.getDevicePrice(userInfo, getTradeEparchyCode(), newResKinkCode,
            // newCapacityType, newWritecardflag);
            output.putAll(feeData);
        }
        return newSimCard;
    }

    /**
     * 获取用户的号码和sim资源
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getUserRes(IData input) throws Exception
    {
        IDataset simRes = UserResInfoQry.queryUserResByUserIdResType(input.getString("USER_ID"), "1");

        if (IDataUtil.isEmpty(simRes))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_248);
        }
        IData outPut = new DataMap();
        outPut.put("OLD_SIM_CARD_NO", simRes.getData(0).getString("RES_CODE", ""));
        outPut.put("OLD_IMSI", simRes.getData(0).getString("IMSI", ""));
        return outPut;
    }

    /**
     * 查询网上选号 NETCHOOSE_PSPT_ID NETCHOOSE_TYPE
     * 
     * @param input
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryNetChoosePhone(IData input) throws Exception
    {
        String psptId = input.getString("NETCHOOSE_PSPT_ID", "");
        String netChooseType = input.getString("NETCHOOSE_TYPE", "");

        IDataset resInfo = ResCall.getSelTempOccupyNum("0", netChooseType, psptId);
        return resInfo;
    }

    /**
     * 释放网上选择的号码
     */
    public void releaseNetChoosePhone(IData input) throws Exception
    {
        ResCall.releaseRes("0", input.getString("NEW_SERIAL_NUMBER", ""), "0", "");
    }
}
