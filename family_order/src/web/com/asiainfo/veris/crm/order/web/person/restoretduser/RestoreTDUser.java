
package com.asiainfo.veris.crm.order.web.person.restoretduser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.person.simcardmgr.SimCardBasePage;

public abstract class RestoreTDUser extends SimCardBasePage
{
    /**
     * 功能说明：资源校验 修改时间
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkNewResource(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData resData = this.checkResource(pageData);
        if (null != resData)
        {
            resData.put("RESULT_CODE", "Y");
            resData.put("START_DATE", SysDateMgr.getSysTime());
            resData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            setAjax(resData);
        }
        else
        {
            IData res = new DataMap();
            res.put("RESULT_CODE", "N");
            setAjax(res);
        }
    }

    /**
     * 功能: 资源校验
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    private IData checkResource(IData pageData) throws Exception
    {
        String resTypeCode = pageData.getString("RES_TYPE_CODE", "");
        pageData.put("SERIAL_NUMBER", pageData.getString("OLD_PHONE_NO"));
        if (StringUtils.equals("1", resTypeCode) || StringUtils.equals("0", resTypeCode))
        {
            IDataset resDataset = CSViewCall.call(this, "SS.RestoreUserSVC.checkRestoreNewRes", pageData);
            if (IDataUtil.isNotEmpty(resDataset))
            {
                return resDataset.getData(0);
            }
        }
        return null;
    }

    /**
     * sim卡校验
     * 
     * @param cycle
     * @throws Exception
     */
    private void checkSimCardInfo(String simCardNo, IData userInfo) throws Exception
    {
        IData editInfo = new DataMap();
        try
        {
            IData input = new DataMap();
            input.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            input.put("SIM_CARD_NO", simCardNo);
            input.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE", ""));// 物联网
            // 校验原sim卡信息
            /* 此处先注释,到现场再放开 modify by dengyong3 */
            // IDataset simCardInfo = CSViewCall.call(this, "SS.RestoreUserSVC.checkRestoreOldSimCard", input);

            editInfo.put("SIM_CHECK_TAG", "0");// sim卡可以继续使用
            editInfo.put("OPC_VALUE", "");// OPC值
            if (StringUtils.isNotBlank(""))
            {
                editInfo.put("IS_TD_USER", "1");// TD用户
            }
            else
            {
                editInfo.put("IS_TD_USER", "0");// 不是3G用户
            }
        }
        catch (Exception e)
        {
            editInfo.put("IS_TD_USER", "0");// 不是3G用户
            editInfo.put("SIM_CHECK_TAG", "-1");// sim卡不可以继续使用
        }
        finally
        {
            setEditInfo(editInfo);
        }
    }

    /******************************** 私有工具方法 *****************************************/
    /**
     * 功能: 获取销户用户资源 修改时间:
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    private IDataset getResInfo(IData userInfo) throws Exception
    {
        IData tempdata = new DataMap();
        tempdata.put("USER_ID", userInfo.getString("USER_ID"));
        tempdata.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "CS.UserResInfoQrySVC.getUserResMaxDateByUserId", tempdata);
        if (dataset == null || dataset.size() < 1)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_235);
        }
        return dataset;
    }

    /**
     * 认证结束之后回调的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        // 用户客户资料
        IData custUserInfo = new DataMap();
        custUserInfo.putAll(custInfo);
        custUserInfo.putAll(userInfo);

        IDataset dataset = this.getResInfo(userInfo);// 用户销号资源
        IData returndata = new DataMap();
        IDataset resset = new DatasetList();
        // 处理逻辑：用户最后的关系截止时间去获取关系记录
        // 通过这条记录的user_id_a,short_code和资源的user_id_a,res_code比较，不相等则去掉
        // 由于没有改变的资源类型也要拼串，于是添加一个标记
        for (Object data : dataset)
        {
            IData resData = (IData) data;
            String resTypeCode = resData.getString("RES_TYPE_CODE", "");
            if (StringUtils.equals("1", resTypeCode))
            {
                returndata.put("OLD_SIM_CARD_NO", resData.getString("RES_CODE", ""));
                resset.add(resData);
            }
            else if (StringUtils.equals("0", resTypeCode))
            {
                returndata.put("OLD_PHONE_NO", resData.getString("RES_CODE", ""));
                resset.add(resData);
            }
        }
        setResInfos(IDataUtil.idToIds(resset));// 原始资源信息
        setOldSimCardNo(returndata.getString("OLD_SIM_CARD_NO", ""));// 原sim卡
        setOldPhoneNo(returndata.getString("OLD_PHONE_NO", ""));// 原号码
        setCustUserInfo(custUserInfo);// 客户信息

        checkSimCardInfo(returndata.getString("OLD_SIM_CARD_NO", ""), userInfo);// 校验sim卡是否还可以继续使用
    }

    /**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData editInfo = new DataMap();
        editInfo.put("IS_TD_USER", "0");// 默认不是3G用户
        editInfo.put("SIM_CHECK_TAG", "first");
        setEditInfo(editInfo);
    }

    /**
     * 业务提交,组件默认提交action方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put("TRADE_TYPE_CODE", "3813");
        IDataset dataset = CSViewCall.call(this, "SS.RestoreUserRemoteRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 功能: 资源信息列表每行鼠标点击
     * 
     * @return
     * @throws Exception
     */
    public IDataset resClickTag() throws Exception
    {
        DataMap inparams = new DataMap();
        inparams.put("USE_TAG", "0");
        inparams.put("SUBSYS_CODE", "CSM");
        inparams.put("TAG_CODE", "CS_VIEWCON_RESTORE");
        inparams.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        // 查询复机界面控制参数
        IDataset dataset = CSViewCall.call(this, "CS.TagInfoQrySVC.getTagInfoBySubSys", inparams);
        if (null == dataset || dataset.size() < 1)
        {
            dataset = new DatasetList();
            IData data = new DataMap();
            data.put("TAG_NUMBER", "2");
            dataset.add(data);
        }
        return dataset;
    }

    /**
     * 功能说明：资源信息列表每行鼠标点击校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void resTableRowClick(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset outlist = new DatasetList();
        outlist.add(pageData);
        IDataset dataset = this.resClickTag();
        if (dataset.size() > 0)
        {
            outlist.add(dataset.get(0));
        }
        setAjax(outlist);
    }

    public abstract void setCustUserInfo(IData commInfo);

    public abstract void setEditInfo(IData commInfo);

    public abstract void setOldPhoneNo(String oldphoneno);

    public abstract void setOldSimCardNo(String oldsimno);

    public abstract void setResInfos(IDataset relaList);

}
