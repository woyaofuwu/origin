
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserinfo.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserinfo.order.requestdata.CttModifyUserInfoReqData;

/**
 * 修改用户资料拼台帐表trade类
 */
public class CttModifyUserInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        IData assusreCustInfo = getAssureCustInfo(btd);// 组织担保人相关信息
        createUserTradeInfo(btd, assusreCustInfo);// 修改资料表中担保人相关信息

        // 如果客户信息不为空且不存在，则需要新增tf_f_customer 和tf_f_cust_person
        if (StringUtils.equals("TRUE", assusreCustInfo.getString("IS_NEW_CUST", "")))
        {
            createCustPersonTradeInfo(btd, assusreCustInfo);
            createCustomerTradeInfo(btd, assusreCustInfo);
        }
    }

    /**
     * 准备台帐客户资料子表的数据
     * 
     * @param btd
     * @param assusreCustInfo
     * @throws Exception
     */
    private void createCustomerTradeInfo(BusiTradeData btd, IData assusreCustInfo) throws Exception
    {
        CttModifyUserInfoReqData reqData = (CttModifyUserInfoReqData) btd.getRD();
        CustomerTradeData customerTradeData = new CustomerTradeData();
        customerTradeData.setCustId(assusreCustInfo.getString("ASSURE_CUST_ID"));
        customerTradeData.setCustName(reqData.getAssureName());
        customerTradeData.setCustType("0");
        customerTradeData.setCustState("1");
        customerTradeData.setPsptTypeCode(reqData.getAssurePsptTypeCode());
        customerTradeData.setPsptId(reqData.getAssurePsptId());
        customerTradeData.setOpenLimit("0");
        customerTradeData.setEparchyCode(reqData.getUca().getUser().getEparchyCode());
        customerTradeData.setCityCode(reqData.getUca().getUser().getCityCode());
        customerTradeData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setInStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDate(btd.getRD().getAcceptTime());
        customerTradeData.setRemark(reqData.getRemark());
        customerTradeData.setRemoveTag("0");
        customerTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(btd.getRD().getUca().getSerialNumber(), customerTradeData);
    }

    /**
     * 准备台帐个人客户资料子表的数据
     * 
     * @param btd
     * @throws Exception
     */
    private void createCustPersonTradeInfo(BusiTradeData btd, IData assureCustInfo) throws Exception
    {
        CttModifyUserInfoReqData reqData = (CttModifyUserInfoReqData) btd.getRD();
        CustPersonTradeData custPersonTradeData = reqData.getUca().getCustPerson().clone();
        custPersonTradeData.setCustId(assureCustInfo.getString("ASSURE_CUST_ID", ""));
        custPersonTradeData.setPsptTypeCode(assureCustInfo.getString("ASSURE_PSPT_TYPE_CODE"));
        custPersonTradeData.setPsptId(reqData.getAssurePsptId());
        custPersonTradeData.setCustName(reqData.getAssureName());
        custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custPersonTradeData.setRemark(reqData.getRemark());
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), custPersonTradeData);
    }

    /**
     * 准备台帐用户资料子表的数据
     * 
     * @param btd
     * @throws Exception
     */
    private void createUserTradeInfo(BusiTradeData btd, IData assusreCustInfo) throws Exception
    {
        CttModifyUserInfoReqData reqData = (CttModifyUserInfoReqData) btd.getRD();
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();
        userTradeData.setAssureTypeCode(assusreCustInfo.getString("ASSURE_TYPE_CODE", ""));
        userTradeData.setAssureCustId(assusreCustInfo.getString("ASSURE_CUST_ID", ""));
        userTradeData.setAssureDate(assusreCustInfo.getString("ASSURE_DATE", ""));
        userTradeData.setRemark(reqData.getRemark());
        userTradeData.setUserTypeCode(reqData.getUserTypeCode());
        userTradeData.setRsrvTag1(reqData.getAreaType());
        userTradeData.setRsrvTag2(reqData.getClearAccount());

        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTradeData.setRemoveTag("0");
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }

    private IData getAssureCustInfo(BusiTradeData btd) throws Exception
    {
        IData assureCustInfo = new DataMap();
        CttModifyUserInfoReqData reqData = (CttModifyUserInfoReqData) btd.getRD();
        if (StringUtils.isNotBlank(reqData.getAssurePsptTypeCode()) && StringUtils.isNotBlank(reqData.getAssurePsptId()) && StringUtils.isNotBlank(reqData.getAssureName()))
        {
            IDataset dataset = UCustBlackInfoQry.qryBlackCustInfo(reqData.getAssurePsptTypeCode(), reqData.getAssurePsptId());
            if (dataset != null && dataset.size() > 0)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_193);// 你输入的担保人用户为黑名单客户!
            }
            String seqCustId = null;
            IDataset modifyDataset = CustomerInfoQry.getCustInfoByPspt(reqData.getAssurePsptTypeCode(), reqData.getAssurePsptId());
            if (modifyDataset.size() > 0)
            {
                for (int i = 0; i < modifyDataset.size(); i++)
                {
                    if (modifyDataset.getData(i).getString("CUST_NAME").equals(reqData.getAssureName()))
                    {
                        seqCustId = modifyDataset.getData(i).getString("CUST_ID");
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
                if (seqCustId == null)
                {
                    CSAppException.apperr(CustException.CRM_CUST_147);
                }
            }
            else
            {
                seqCustId = SeqMgr.getCustId();
                assureCustInfo.put("IS_NEW_CUST", "TRUE");// 标记是需要新增客户
            }
            assureCustInfo.put("ASSURE_CUST_ID", seqCustId);// 担保客户标识
            assureCustInfo.put("ASSURE_PSPT_TYPE_CODE", reqData.getAssurePsptTypeCode());// 担保证件类型
            assureCustInfo.put("ASSURE_TYPE_CODE", reqData.getAssureTypeCode());// 担保类型
            assureCustInfo.put("ASSURE_DATE", reqData.getAssureDate());// 担保日期

        }
        else
        {
            assureCustInfo.put("ASSURE_CUST_ID", "");
            assureCustInfo.put("ASSURE_PSPT_TYPE_CODE", "");
            assureCustInfo.put("ASSURE_TYPE_CODE", "");
            assureCustInfo.put("ASSURE_DATE", "");
        }

        return assureCustInfo;

    }

}
