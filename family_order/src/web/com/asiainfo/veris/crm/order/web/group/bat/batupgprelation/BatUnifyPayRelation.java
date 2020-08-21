
package com.asiainfo.veris.crm.order.web.group.bat.batupgprelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee.UserFeeIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatUnifyPayRelation extends GroupBasePage
{

    /**
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = queryGroupCustInfo(cycle);
        String custId = result.getString("CUST_ID");

        if (StringUtils.isEmpty(custId))
        {
            return;
        }

        IData param = new DataMap();
        param.put("CUST_ID", custId);
        //查询集团统一付费的产品
        IDataset prodoctInfos = CSViewCall.call(this, 
                "CS.UserProductInfoQrySVC.getGrpProductByCustIdForUPGP",
                result);

        if (IDataUtil.isNotEmpty(prodoctInfos))
        {
            IData productInfo = prodoctInfos.getData(0);
            
            if(IDataUtil.isNotEmpty(productInfo))
            {
                
                String userId = productInfo.getString("USER_ID","");
                String serialNumber = productInfo.getString("SERIAL_NUMBER","");
                param.clear();
                param.put("USER_ID", userId);
                
                //欠费情况的判断
                IData oweFeeData = UserFeeIntfViewUtil.qryGrpUserOweFeeInfo(this, userId);
                if (IDataUtil.isEmpty(oweFeeData))
                {
                    CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_145, serialNumber);
                }
                else
                {
                    double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE","0"));// 实时欠费
                    if (acctBalance < 0)
                    {
                        CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_146, 
                                serialNumber);
                    }
                }
                
                IDataset acctInfos = CSViewCall.call(this, 
                        "CS.AcctInfoQrySVC.getAcctInfoByUserIdFroUPGP",
                        param);
                
                if(IDataUtil.isNotEmpty(acctInfos))
                {
                    for (int i = 0, len = acctInfos.size(); i < len; i++)
                    {
                        IData acctInfoData = acctInfos.getData(i);
                        String acctId = acctInfoData.getString("ACCT_ID", "");
                        String payName = acctInfoData.getString("PAY_NAME", "");
                        String productId = acctInfoData.getString("PRODUCT_ID", "");
                        String productName = acctInfoData.getString("PRODUCT_NAME", "");

                        String tempUserId = acctInfoData.getString("USER_ID", "");
                        String epachyCode = acctInfoData.getString("EPARCHY_CODE", "");
                        StringBuffer sb = new StringBuffer();
                        sb.append(acctId);
                        sb.append("|");
                        sb.append(payName);
                        sb.append("|");
                        sb.append(productId);
                        sb.append("|");
                        sb.append(productName);
                        sb.append("|");
                        sb.append(tempUserId);
                        sb.append("|");
                        sb.append(epachyCode);
                        acctInfoData.put("DISPLAY_NAME", sb.toString());
                    }
                }
                
                setAcctInfos(acctInfos);
            }
        }

        // 其它信息
        IData acctotherInfo = new DataMap();
        acctotherInfo.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
        acctotherInfo.put("END_CYCLE_ID", "205001");
        setAcctotherInfo(acctotherInfo);

        // 设置综合账目列表
        IData itemData = new DataMap();
        // 综合帐目列表
        IDataset itemList = CSViewCall.call(this, 
                "CS.NoteItemInfoQrySVC.queryNoteItems",
                itemData); 

        setNoteItemList(itemList);

        setGroupInfo(result);
    }

    /**
     * 取付费账目编码
     * 
     * @param cycle
     * @throws Exception
     */
    public void getPayItemCode(IRequestCycle cycle) throws Exception
    {
        String checkAll = getParameter("CheckAll");
        String payitemCode = "";

        // 处理付费帐目编码
        if (StringUtils.isNotBlank(checkAll)) // 如果前台勾选了“综合帐目全选”，编码为“-1”
        {
            payitemCode = "-1";
        }
        else
        // 如果前台没有勾选了“综合帐目全选”，则由账务流程返回编码
        {
            String itemCodes = getParameter("itemcodes");

            if (StringUtils.isBlank(itemCodes))
            {
                // 没有选择综合帐目，无法完成拼串
            }

            String[] itemStr = StringUtils.split(itemCodes, ",");

            IData input = new DataMap();
            input.put("ITEMCODES", itemStr);

            IData iData = CSViewCall.callone(this, 
                    "CS.AcctInfoQrySVC.callAccountSvc", input);

            payitemCode = iData.getString("PAYITEM_CODE");
        }

        setAjax("PAYITEM_CODE", payitemCode);
    }

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());

        String batchOperType = getData().getString("BATCH_OPER_TYPE");
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, 
                "CS.ParamInfoQrySVC.getCommparaByParamattr",
                iparam);
        if (IDataUtil.isNotEmpty(resultSet))
        {
            getData().put("MEB_FILE_SHOW","true");
        }
        
        setCondition(getData());
    }

    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        String acctId = getParameter("ACCT_ID");

        IData idAcctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        String strPayModeCode = idAcctInfo.getString("PAY_MODE_CODE", "0");
        // 如果不是现金帐户，即托收帐户，才查托收表
        if (!"0".equals(strPayModeCode))
        {
            IData idParam = new DataMap();
            idParam.put("ACCT_ID", getParameter("ACCT_ID"));
            IDataset idsAcctConsInfo = CSViewCall.call(this, 
                    "CS.AcctConsignInfoQrySVC.getConsignInfoByAcctIdForGrp",
                    idParam);
            IData idAcctConsInfo = (idsAcctConsInfo.size() > 0 ? idsAcctConsInfo.getData(0) : null);
            if (idAcctConsInfo != null)
            {
                idAcctInfo.put("SUPER_BANK_CODE", idAcctConsInfo.getString("SUPER_BANK_CODE", ""));
            }
        }

        setCondition(idAcctInfo);
    }

    /**
     * 过滤综合帐目
     * 
     * @param cycle
     * @throws Exception
     */
    public void filterNoteItems(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", getParameter("NOTE_ITEM"));

        IDataset iDataset = CSViewCall.call(this, 
                "CS.NoteItemInfoQrySVC.filterNoteItems", data);

        setNoteItemList(iDataset);
    }
    
    public abstract void setAcctInfo(IData idata);

    public abstract void setAcctInfoDesc(IData acctInfoDesc);

    public abstract void setAcctInfos(IDataset idataset);

    public abstract void setCondition(IData idAcctInfo);

    public abstract void setConsignDesc(IData consignDesc);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setMemacctinfo(IData idAcctConsInfo);

    public abstract void setNoteItem(IData noteItem);

    public abstract void setNoteItemList(IDataset noteItemList);

    public abstract void setAcctotherInfo(IData acctotherInfo);

}
