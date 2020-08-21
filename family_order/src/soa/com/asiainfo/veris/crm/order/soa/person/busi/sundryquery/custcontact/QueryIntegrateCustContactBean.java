
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.custcontact;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;

public class QueryIntegrateCustContactBean extends CSBizBean
{

    /**
     * 进行客户接触查询的权限控制
     * 
     * @data 2013-9-29
     * @return
     * @throws Exception
     */
    public IData custContactInit() throws Exception
    {
        String pv_str_GetCustContactInfo = fnGetSysTagInfo("CS_CHR_CUSTCONTACTINFO", "1");
        if ("-1".equals(pv_str_GetCustContactInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_35);
        }
        String pt_str_CurProvince = fnGetSysTagInfo("CS_CHR_REQUESTMODE");
        if ("-1".equals(pt_str_CurProvince))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_141);
        }
        String pv_str_SearchPopedom = fnGetSysTagInfo("CS_CHR_SEARCHPOPEDOM", "0");
        if ("-1".equals(pv_str_SearchPopedom))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_142);
        }
        String pv_str_InsertSerchLog = fnGetSysTagInfo("CS_CHR_INSERTSERCHLOG", "0");
        if ("-1".equals(pv_str_InsertSerchLog))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_143);
        }

        IData idata = new DataMap();
        idata.put("PV_STR_GETCUSTCONTACTINFO", pv_str_GetCustContactInfo);
        idata.put("PT_STR_CURPROVINCE", pt_str_CurProvince);
        idata.put("PV_STR_SEARCHPOPEDOM", pv_str_SearchPopedom);
        idata.put("PV_STR_INSERTSERCHLOG", pv_str_InsertSerchLog);
        return idata;
    }

    /**
     * @data 2013-9-29
     * @param Tagcode
     * @return
     * @throws Exception
     */
    public String fnGetSysTagInfo(String Tagcode) throws Exception
    {

        return fnGetSysTagInfo(Tagcode, "0");
    }

    /**
     * 查询td_s_tag的TAGCODE的值,找不到则返回默认值Default
     * 
     * @data 2013-9-29
     * @param Tagcode
     * @param Default
     * @return
     * @throws Exception
     */
    public String fnGetSysTagInfo(String Tagcode, String Default) throws Exception
    {

        IData idata = new DataMap();
        idata.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        idata.put("TAG_CODE", Tagcode);
        idata.put("USE_TAG", "0");
        IDataset out = TagInfoQry.getTagInfo(CSBizBean.getUserEparchyCode(), Tagcode, "0");

        return (out == null || out.size() == 0) ? Default : (String) out.get(0, "TAG_CHAR", Default);
    }

    /**
     * 查询客户接触子项
     * 
     * @data 2013-9-29
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset getcontractSubMode(IData inparams) throws Exception
    {

        return TradeCustomerInfoQry.getcontractSubMode(inparams);

    }

    /**
     * @data 2013-9-29
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset getCustContactSubInfos(IData inparams) throws Exception
    {
        return getcontractSubMode(inparams);
    }

    /**
     * 修改日志
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    private boolean insertOperationLog(IData inparam) throws Exception
    {

        IData param = new DataMap();

        param.put("FLOW_ID", SeqMgr.getTradeId());
        param.put("ACCEPT_MONTH", inparam.getString("ACCEPT_MONTH", ""));
        param.put("CUST_CONTACT_ID", inparam.getString("CUST_CONTACT_ID", ""));
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
        param.put("MODIFY_DESC", inparam.getString("MODIFY_DESC", ""));

        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        param.put("UPDATE_CITY_CODE", getVisit().getCityCode());
        param.put("UPDATE_EPARCHY_CODE", getVisit().getStaffEparchyCode());

        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("RSRV_STR1", inparam.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", inparam.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", inparam.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", inparam.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", inparam.getString("RSRV_STR5", ""));
        param.put("REMARK", inparam.getString("REMARK", ""));

        boolean flag = Dao.insert("TL_B_CUSTCONTACT_TRADELOG", param);
        return flag;
    }

    /**
     * 修改客户接触信息明细
     * 
     * @data 2013-9-29
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset modifyIntegrateCustContact(IData inparams, Pagination pagination) throws Exception
    {

        IDataset dataset = new DatasetList();
        IData inparam = new DataMap();
        int i = 0;

        inparam.put("CUST_CONTACT_ID", inparams.getString("CUST_CONTACT_ID"));

        IDataset results = TradeCustomerInfoQry.getCustContactByPk(inparam, pagination);

        boolean flag = false;
        if (results != null && results.size() > 0)
        {
            IData idata = new DataMap();
            idata.putAll((IData) results.get(0));
            idata.putAll(inparams);
            idata.put("UPDATE_TIME", SysDateMgr.getSysDate());
            // idata.put("TRADE_ID", SeqMgr.getTradeId());
            // 更新客户接触明细
            i = updateCustContact(idata);
            if (i > 0)
            {
                // 记录日志
                flag = this.insertOperationLog(idata) && flag;
            }
        }

        inparam.clear();
        if (i == 0 || i < 0)
        {
            inparam.put("TIP", "客户接触信息修改失败");
        }
        else
        {
            inparam.put("TIP", "客户接触信息修改成功");
        }

        dataset.add(inparam);
        return dataset;
    }

    /**
     * 客户接触查询的主方法
     * 
     * @data 2013-9-29
     * @param inparam
     * @param custDataset
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryCustContact(IData inparam, IDataset custDataset, Pagination pagination) throws Exception
    {

        IDataset results = new DatasetList();
        for (Iterator iter = custDataset.iterator(); iter.hasNext();)
        {
            IData tmpData = (IData) iter.next();
            inparam.put("CUST_ID", tmpData.getString("CUST_ID", ""));
            results.addAll(queryIntegrateCustContact(inparam, pagination));
        }
        return results;
    }

    /**
     * @data 2013-9-29
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryCustContact(IData inparam, Pagination pagination) throws Exception
    {

        IDataset custContacts = new DatasetList();
        if ("1".equals(inparam.getString("PV_STR_SEARCHPOPEDOM", "")))
        {
            if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS901"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_144);
            }
        }

        String queryType = inparam.getString("CUSTCONTACT_QUERY_TYPE");
        if ("1".equals(queryType))
        {
            custContacts = TradeCustomerInfoQry.getCustContactByPk(inparam, pagination);
        }
        else if ("0".equals(queryType))
        {
            custContacts = TradeCustomerInfoQry.getCustContactDate(inparam, pagination);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_504);
        }

        if(IDataUtil.isNotEmpty(custContacts)){
        	for(int i=0;i<custContacts.size();i++){
            	IData res=custContacts.getData(i);
            	String productId=res.getString("PRODUCT_ID");
                //获取产品名称，调用产商品接口,本次改造duhj 2017/03/08
                String productName=UProductInfoQry.getProductNameByProductId(productId);
                res.put("PRODUCT_NAME", productName);

        	}
        }
        
        return custContacts;

        /*
         * String RemoveTag = inparam.getString("REMOVE_TAG", ""); IDataset custDataset = new DatasetList(); if
         * (RemoveTag.equals("0")) { custDataset = UserInfoQry.getCustomerBySnNormal(inparam, pagination); } else {
         * custDataset = UserInfoQry.getCustomerBySn(inparam, pagination); }
         */

    }

    /**
     * 获得客户接触资料
     * 
     * @data 2013-9-29
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryIntegrateCustContact(IData inparams, Pagination pagination) throws Exception
    {
        // 权限控制
        if ("1".equals(inparams.getString("PV_STR_SEARCHPOPEDOM", "")))
        {
            if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS901"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_144);
            }
        }
        if ("1".equals(inparams.getString("CUSTCONTACT_QUERY_TYPE")))
        {
            return TradeCustomerInfoQry.getCustContactByPk(inparams, pagination);
        }
        else if ("1".equals(inparams.getString("PV_STR_GETCUSTCONTACTINFO")))
        {
            return TradeCustomerInfoQry.getCustContactDate(inparams, pagination);
        }
        return null;
    }

    public int updateCustContact(IData inparam) throws Exception
    {

        IData param = new DataMap();

        param.put("CUST_ID", inparam.getString("CUST_ID", ""));
        param.put("CUST_NAME", inparam.getString("CUST_NAME", ""));
        param.put("USER_ID", inparam.getString("USER_ID", ""));
        param.put("SERIAL_NUMBER", inparam.getString("SERIAL_NUMBER", ""));
        param.put("PRODUCT_ID", inparam.getString("PRODUCT_ID", ""));
        param.put("EPARCHY_CODE", inparam.getString("EPARCHY_CODE", ""));
        param.put("CITY_CODE", inparam.getString("CITY_CODE", ""));
        param.put("CONTACT_MODE", inparam.getString("CONTACT_MODE", ""));
        param.put("IN_MODE_CODE", inparam.getString("IN_MODE_CODE", ""));
        param.put("IN_MEDIA_CODE", inparam.getString("IN_MEDIA_CODE", ""));
        param.put("CHANNEL_ID", inparam.getString("CHANNEL_ID", ""));
        param.put("SUB_CHANNEL_ID", inparam.getString("SUB_CHANNEL_ID", ""));
        param.put("START_TIME", inparam.getString("START_TIME", ""));
        param.put("FINISH_TIME", inparam.getString("FINISH_TIME", ""));
        param.put("CONTACT_STATE", inparam.getString("CONTACT_STATE", ""));
        param.put("RSRV_STR1", inparam.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", inparam.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", inparam.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", inparam.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", inparam.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", inparam.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", inparam.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", inparam.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", inparam.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", inparam.getString("RSRV_STR10", ""));
        param.put("REMARK", inparam.getString("REMARK", ""));
        param.put("ACCEPT_MONTH", inparam.getString("ACCEPT_MONTH", ""));
        param.put("CUST_CONTACT_ID", inparam.getString("CUST_CONTACT_ID", ""));

        int i = Dao.executeUpdateByCodeCode("TF_B_CUST_CONTACT", "UPD_CUSTCONTACT", param);
        return i;
    }
}
