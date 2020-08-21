
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: LongServiceFeeTransAction.java
 * @Description: 接口订购国际长途[service=15]预存转押金处理【接口使用】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 3, 2014 5:13:32 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 3, 2014 maoke v1.0.0 修改原因
 */
public class LongServiceFeeTransAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 国际漫游长途扣减预存当押金的费用类型(当工号是客服或者接口的工号时)
        if (!"0".equals(inModeCode))
        {
            // 已经存在押金
            IDataset foregift = UserOtherInfoQry.getUserOtherservByPK(btd.getRD().getUca().getUserId(), "FG", "0", null);

            if (IDataUtil.isNotEmpty(foregift))
            {
                for (int i = 0, size = foregift.size(); i < size; i++)
                {
                    IData gift = foregift.getData(i);
                    String rsrvNum1 = gift.getString("RSRV_NUM1", "");
                    String rsrvNum2 = gift.getString("RSRV_NUM2", "");

                    if ("3".equals(rsrvNum1) && "80000".equals(rsrvNum2))
                    {
                        return;
                    }
                }
            }
            
            //个人大客户不收取押金
            if(CustVipInfoQry.isPersonCustVip(btd.getRD().getUca().getUserId()))
            {
                return;
            }
            
            // 工号有“押金减免权限",则不需要做预存转押金处理
            String staffId = CSBizBean.getVisit().getStaffId();

            if (!StaffPrivUtil.isFuncDataPriv(staffId, "SYSFOREGIFT"))
            {
                UcaData uca = btd.getRD().getUca();

                String serialNumber = uca.getSerialNumber();

                List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_15);
                for (SvcTradeData svcData : svcDatas)
                {
                    String modifyTag = svcData.getModifyTag();
                    String productId = svcData.getProductId();
                    String packageId = svcData.getPackageId();
                    String eparchyCode = btd.getRD().getUca().getUserEparchyCode();

                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                    {
                        IDataset feeConfigs = ProductFeeInfoQry.getElementFee("110", CSBizBean.getVisit().getInModeCode(), "", BofConst.ELEMENT_TYPE_CODE_SVC, productId, packageId, "-1", PersonConst.SERVICE_ID_15, eparchyCode, "3");

                        if (IDataUtil.isNotEmpty(feeConfigs))
                        {
                            // 查询用户预存转押金专项存折的钱 DEPOSIT_CODE=603;
                            int money603 = this.getDepositBalance603BySn(serialNumber, "603");

                            int foregiftFee = feeConfigs.getData(0).getInt("FEE");

                            /* 判断用户是否已存在预存转押金专项款，存在的话则判断该专项款是否够800元，够则不需要再做预存转押金处理，不够则需要转不够的部分 */
                            if (money603 < foregiftFee)
                            {
                                // 实际还需要的押金金额
                                int factChgFee = foregiftFee - money603;

                                // 调用账务接口，查询用户当前余额是否够扣减国际长途漫游押金
                                String retFlag = AcctCall.queryIsCanTransByRoam(serialNumber, factChgFee).getData(0).getString("IS_ENOUGH", "");
                                if ("0".equals(retFlag))// 余额够扣减
                                {
                                    // 只简单台账登记 方便统计等 转账框架不统一做处理
                                    OtherFeeTradeData transFeeTrade = new OtherFeeTradeData();

                                    transFeeTrade.setAcctId(uca.getAcctId());
                                    transFeeTrade.setInDepositCode("603");
                                    transFeeTrade.setOutDepositCode("-1");
                                    transFeeTrade.setOperFee(String.valueOf(factChgFee));
                                    transFeeTrade.setOperType(BofConst.OTHERFEE_ROAM_TRANS);
                                    transFeeTrade.setUserId(uca.getUserId());
                                    transFeeTrade.setPaymentId("5");// 国际业务专款押金
                                    transFeeTrade.setStartDate(btd.getRD().getAcceptTime());
                                    transFeeTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
                                    transFeeTrade.setRemark("办理国际长途转押金");

                                    btd.add(uca.getSerialNumber(), transFeeTrade);

                                    // 开始调用转账接口【转押金】
                                    AcctCall.sameAcctTransFeeByRoam(serialNumber, factChgFee, "0", "15000");
                                }
                                else
                                // 余额不够扣减
                                {
                                    CSAppException.apperr(FeeException.CRM_FEE_160, factChgFee / 100);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 获取603账本余额
     * @param serialNumber
     * @param depositCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 5, 2014 3:18:24 PM
     */
    public int getDepositBalance603BySn(String serialNumber, String depositCode) throws Exception
    {
        IDataset depositDatas = AcctCall.queryAccountDepositBySn(serialNumber);

        if (IDataUtil.isNotEmpty(depositDatas))
        {
            for (int i = 0, size = depositDatas.size(); i < size; i++)
            {
                IData depositData = depositDatas.getData(i);

                String tempDepositCode = depositData.getString("DEPOSIT_CODE", "");
                String depositBalance = depositData.getString("DEPOSIT_BALANCE", "0");

                if (depositCode.equals(tempDepositCode))
                {
                    return Integer.parseInt(depositBalance);
                }
            }
        }
        return 0;
    }
}
