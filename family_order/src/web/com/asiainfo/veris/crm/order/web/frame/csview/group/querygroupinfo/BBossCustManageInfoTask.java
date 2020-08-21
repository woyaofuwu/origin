
package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Bboss成员同步业务冲抵
 * 
 * @author chenkh 2014-7-24
 */
public abstract class BBossCustManageInfoTask extends CSBasePage
{
    /**
     * @param data
     *            取集团客户信息 入参 cust_id
     * @return 查询结果
     * @throws Exception
     */
    public IDataset getKeyInfos(IData data) throws Exception
    {
        IDataset result = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.queryCustGroupInfoByCID", data);

        return result;
    }

    public void sendHttpStr(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        IData pageData = getData();
        IDataset userattrs = CSViewCall.call(this, "CS.BbossTaskGrpChargeAgainstSVC.queryBbossTaskGrp", pageData);

        String customerNumber = getData().getString("grpNum");
        if (userattrs.size() > 0)
        {
            customerNumber = userattrs.getData(RandomUtils.nextInt(100) % userattrs.size()).getString("MP_GROUP_ID");// 全网集团编码
        }

        IData inParam = new DataMap();
        IDataset groupInfos = new DatasetList();
        inParam.put("MP_GROUP_CUST_CODE", customerNumber);
        groupInfos = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.queryCustGrpByGID", inParam);
        if (groupInfos.size() == 0)
        {
            data.put("FLAG", "false");
            setAjax(data);

            return;
        }

        String ectypecode = "";
        if ("1".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "1";
        }
        else if ("2".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "1";
        }
        else if ("3".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "2";
            ;
        }
        else if ("4".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "3";
        }
        else if ("6".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "3";
        }
        else if ("7".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "4";
        }
        else if ("C".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "4";
        }
        else if ("D".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "4";
        }
        else if ("A".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "6";
        }
        else if ("9".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "8";
        }
        else if ("E".equals(groupInfos.get(0, "ENTERPRISE_TYPE_CODE", "")))
        {
            ectypecode = "8";
        }
        else
            ectypecode = "7";
        String ENTERPRISE_TYPE = "0" + ectypecode; // 企业类型/公司性质

        String GROUP_ADDR = (String) groupInfos.get(0, "GROUP_ADDR", "");

        String credit_level = "";
        if (groupInfos.get(0, "CLASS_ID").equals("A1"))
        {
            credit_level = "01";
        }
        else if (groupInfos.get(0, "CLASS_ID").equals("A2"))
        {
            credit_level = "02";
        }
        else if (groupInfos.get(0, "CLASS_ID").equals("B1"))
        {
            credit_level = "03";
        }
        else if (groupInfos.get(0, "CLASS_ID").equals("B2"))
        {
            credit_level = "04";
        }
        else if (groupInfos.get(0, "CLASS_ID").equals("C"))
        {
            credit_level = "05";
        }
        else if (groupInfos.get(0, "CLASS_ID").equals("D"))
        {
            credit_level = "06";
        }
        else
        {
            credit_level = "01";
        }

        // 集团扩展信息取得
        IData temp = new DataMap();
        temp.put("CUST_ID", groupInfos.get(0, "CUST_ID", ""));
        IDataset keymanList = getKeyInfos(temp);

        String PHONENUM = "";
        String SEX = "";
        String NAME = "";
        String ROLE_CODE = "";
        if (keymanList.size() > 0)
        {
            if ("F".equals(groupInfos.get(0, "RSRV_STR3")))
            {
                SEX = "1";
            }
            else if ("M".equals(groupInfos.get(0, "RSRV_STR3")))
            {
                SEX = "2";
            }
            else
            {
                SEX = "2";
            }
            PHONENUM = (String) keymanList.get(0, "RSRV_STR25", "");
            NAME = (String) keymanList.get(0, "RSRV_STR2", "");
            ROLE_CODE = (String) keymanList.get(0, "RSRV_STR1", "");
        }
        else
        {
            data.put("FLAG", "false");
            setAjax(data);
            return;
        }
        BizVisit pd = getVisit();

        String Strhttp = "{SERVICECITY=[\"8980\"], REMARK=[\"修改集团\"], TRADE_CITY_CODE=[\"" + pd.getCityCode() + "\"], SEX=[\"" + SEX + "\"], SYNEC_DATE=[\"" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + "\"], CUST_ID=[\""
                + groupInfos.get(0, "CUST_ID", "") + "\"], ROUTEVALUE=[\"000\"], X_TRANS_CODE=[\"IBOSS2\"], TRADE_DEPART_ID=[\"" + pd.getDepartId() + "\"], NAME=[\"" + NAME + "\"], ENTERPRISE_SIZE_CODE=[\""
                + groupInfos.get(0, "ENTERPRISE_SIZE_CODE", "") + "\"], EMP_NUM_ALL=[\"" + groupInfos.get(0, "EMP_NUM_LOCAL", "") + "\"], EC_SERIAL_NUMBER=[\"" + customerNumber + "\"], TRADE_STAFF_ID=[\"" + pd.getStaffId()
                + "\"], REG_MONEY=[\"\"], ROLE_CODE=[\"" + ROLE_CODE + "\"], X_TRANS_TYPE=[\"HTTP\"], PHONENUM=[\"" + PHONENUM + "\"], ENTERPRISE_TYPE=[\"" + ENTERPRISE_TYPE + "\"], ID=[\"898\"], PROVINCE_CODE=[\"" + pd.getProvinceCode()
                + "\"], CUST_ADDR=[\"" + GROUP_ADDR + "\"], CREDIT_LEVEL=[\"" + credit_level + "\"], ROUTETYPE=[\"00\"], KIND_ID=[\"BIP4B254_T4011003_0_0\"], CUST_TYPE=[\"10\"], ROUTE_EPARCHY_CODE=[\"" + getTradeEparchyCode()
                + "\"], STAFF_NAME=[\"陈宋隆\"], PARENT=[\"0\"], CUSTOMER_SERVLEVEL=[\"" + groupInfos.get(0, "SERV_LEVEL", "1") + "\"], EC_USER_ID=[\"" + groupInfos.get(0, "GROUP_ID", "")
                + "\"], HOMEPAGE=[\"\"], TRADE_EPARCHY_CODE=[\"0898\"], IN_MODE_CODE=[\"1\"], TELPHONE=[\"13707571355\"], SERIAL_NUMBER=[\"13707571355\"], TRADE_CLASS1=[\"" + groupInfos.get(0, "CALLING_SUB_TYPE_CODE", "00")
                + "\"], RSRV_STR10=[\"86\"], ACTION=[\"2\"], CUST_NAME=[\"" + groupInfos.get(0, "CUST_NAME", "") + "\"], RSRV_STR11=[\"\"], USERRANK=[\"3\"], TITLE=[\"\"], STAFF_ID=[\"chensonglong\"], POST_CONTENT=[\""
                + groupInfos.get(0, "POST_CODE", "") + "\"], RSRV_STR1=[\"3\"], JURISTIC=[\"\"]}";
        Map httpMap = Wade3DataTran.strToMap(Strhttp);
        IData httpStr = Wade3DataTran.wade3To4DataMap(httpMap);
        IDataset result = CSViewCall.call(this, "CS.BBossTaskSVC.callIBOSS", httpStr);
        if (IDataUtil.isEmpty(result) || !("00".equals(result.getData(0).getString("RSPCODE", "")) || "99".equals(result.getData(0).getString("RSPCODE", ""))))
        {
            // 执行失败
            data.put("FLAG", "false");
        }
        else
        {
            data.put("FLAG", "true");
        }
        setAjax(data);
    }

    public abstract void setInfo(IData info);
}
