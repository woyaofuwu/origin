
package com.asiainfo.veris.crm.order.soa.person.busi.wap;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WapException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.WapSessionInfoQry;

public abstract class BaseServiceForWap extends CSBizService
{

    protected UcaData uca = null; // 用户的三户资料数据

    /**
	 * 
	 */
    private static final long serialVersionUID = 1800097249200313766L;

    /**
     * 2、记录操作日志
     * 
     * @param param
     */
    protected void addCustContactTraceLog(IData param) throws Exception
    {

    }

    /**
     * 3、业务基本规则检查
     * 
     * @param param
     */
    protected void checkBaseRules(IData param) throws Exception
    {
        String userId = "";
        String checkRealName = param.getString("CHECK_REAL_NAME", "");
        String serialNumber = param.getString("SERIAL_NUMBER");

        IData data = new DataMap();
        data.put("REMOVE_TAG", "0");
        data.put("NET_TYPE_CODE", "00");
        data.putAll(param);

        this.uca = UcaDataFactory.getNormalUca(serialNumber);

        // 用户状态不正常不能办理
        SvcStateTradeData svcStateTrade = uca.getUserSvcsStateByServiceId("0");
        if (svcStateTrade != null)
        {
            if ("".equals(svcStateTrade.getStateCode()))
            {
                CSAppException.apperr(WapException.CRM_WAP_200001);
            }
            else if (!"0".equals(svcStateTrade.getStateCode()))
            {
                CSAppException.apperr(WapException.CRM_WAP_200002, svcStateTrade.getStateCode());
            }
        }

        // b、黑名单用户不能办理
        // IDataset dataset = UserBlackWhiteInfoQry.getBlackUserInfo(userId, "-1", serialNumber, "B");
        // if ((dataset != null) && (dataset.size() > 0))
        // {
        // CSAppException.apperr(WapException.CRM_WAP_200004);
        // }

        if ("true".equals(checkRealName))
        {
            // c、未进行实名登记用户不可以办理
            if (!"1".equals(uca.getCustomer().getIsRealName()))
            {
                CSAppException.apperr(WapException.CRM_WAP_200005);
            }
        }
    }

    protected abstract void checkChildParams(IData param) throws Exception;

    protected void checkParams(IData param) throws Exception
    {
        // 公共业务参数
        if ("".equals(param.getString("IN_MODE_CODE", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "IN_MODE_CODE");
        }
        if ("".equals(param.getString("KIND_ID", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "KIND_ID");
        }
        if ("".equals(param.getString("IDTYPE", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "IDTYPE");
        }

        if ("".equals(param.getString("IDITEMRANGE", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "IDITEMRANGE");
        }

        if ("".equals(param.getString("OPRNUMB", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "OPRNUMB");
        }

        if ("".equals(param.getString("BIZTYPE", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "BIZTYPE");
        }

        // if ("".equals(param.getString("CHANNELID", "")))
        // {
        // CSAppException.apperr(WapException.CRM_WAP_100001, "CHANNELID");
        // }

        if ("".equals(param.getString("SESSIONID", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "SESSIONID");
        }

        if ("".equals(param.getString("IDENTCODE", "")))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "IDENTCODE");
        }
        this.checkChildParams(param);
    }

    /**
     * 身份验证
     * 
     * @param param
     */
    protected void checkUserVoucher(IData param) throws Exception
    {
        IData result = new DataMap();

        // 校验必填参数是否存在，如果必传参数不存在则直接返回错误信息
        ArrayList list = new ArrayList();
        list.add("SERIAL_NUMBER"); // 标识号码
        // list.add("CREDENCE_NO"); // 凭证号码
        list.add("IDENTCODE"); // 报文中是这个凭证
        list.add("SESSIONID"); // SESSIONID
        list.add("IDTYPE"); // 标识类型
        for (int i = 0; i < list.size(); i++)
        {
            if ("".equals(param.getString(list.get(i).toString())) || null == param.getString(list.get(i).toString()))
            {
                CSAppException.apperr(WapException.CRM_WAP_700001);
            }
        }

        IDataset res = WapSessionInfoQry.queryWapSession(param.getString("SESSIONID"), param.getString("SERIAL_NUMBER"));// 加上IDYTPE
        // 和号码就行查询
        if (null == res || res.size() < 1)
        {
            CSAppException.apperr(WapException.CRM_WAP_700002);
        }
        String credenceNo = ((IData) res.get(0)).getString("CREDENCE_NO"); // 用户身份凭证号

        if ("".equals(credenceNo) || null == credenceNo)
        {
            CSAppException.apperr(WapException.CRM_WAP_700003);
        }
        String inCredenceNo = param.getString("IDENTCODE");
        if (!(inCredenceNo.equals(credenceNo)))
        {
            CSAppException.apperr(WapException.CRM_WAP_700004);
        }
    }

    public IDataset execute(IData param) throws Exception
    {
        this.transParam(param);
        this.checkParams(param);
        this.checkUserVoucher(param);
        this.addCustContactTraceLog(param);
        this.checkBaseRules(param);
        IDataset resultList = this.handleBiz(param);
        return resultList;
    }

    protected abstract IDataset handleBiz(IData param) throws Exception;

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("SERIAL_NUMBER", "")))
            {
                return;
            }
            else if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (!"".equals(input.getString("MSISDN", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
            }
            else if (!"".equals(input.getString("ID_VALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE", ""));
                return;
            }
            else if (!"".equals(input.getString("IDITEMRANGE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
                return;
            }
        }
    }

    protected void transParam(IData param)
    {
        param.put("SERIAL_NUMBER", param.getString("IDITEMRANGE"));
        if ("WLAN".equals(param.getString("BIZTYPE")))
        {
            param.put("CHECK_REAL_NAME", "true");
        }
    }
}
