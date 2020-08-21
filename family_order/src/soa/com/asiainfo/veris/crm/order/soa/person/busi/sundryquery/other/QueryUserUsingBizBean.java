
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import java.util.ArrayList;
import java.util.HashMap;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.uservoucher.UserVoucherOutQry;

public class QueryUserUsingBizBean extends CSBizBean
{

    public boolean checkUserVoucher(IData data) throws Exception
    {
        boolean isOK = false;
        ArrayList list = new ArrayList();
        list.add("IDITEMRANGE"); // 标识号码
        list.add("SESSIONID"); // SESSIONID
        list.add("IDTYPE"); // 标识类型
        list.add("IDENTCODE");
        for (int i = 0; i < list.size(); i++)
        {
            if ("".equals(data.getString(list.get(i).toString())) || null == data.getString(list.get(i).toString()))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "700001:缺少必填参数: " + list.get(i).toString());
            }
        }
        // data.put("SESSION_ID", data.getString("SESSIONID"));
        // data.put("SERIAL_NUMBER", data.getString("IDITEMRANGE"));

        // 校验必填参数是否存在，如果必传参数不存在则直接返回错误信息
        // if(null == data.get("SESSION_ID") || "".equals(data.get("SESSION_ID"))
        // || null == data.get("SERIAL_NUMBER") || "".equals(data.get("SERIAL_NUMBER")))
        // {
        // common.error("880002:缺少SESSION_ID或SERIAL_NUMBER参数或值！");
        // }

        // 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息
        data.put("CREDENCE_NO_INTF", data.getString("IDENTCODE"));
        data.put("SESSION_ID", data.getString("SESSIONID"));
        data.put("NEWDATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        IDataset res = UserVoucherOutQry.getUserVoucherInfo(data); // 查询该用户wap凭证信息
        if (null == res || res.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "700002:WAP用户信息不存在！");
        }
        String credenceNo = ((IData) res.get(0)).getString("CREDENCE_NO"); // 用户身份凭证号

        if ("".equals(credenceNo) || null == credenceNo)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "700003:用户登录凭证不存在！");
        }
        String inCredenceNo = data.getString("IDENTCODE");
        if (inCredenceNo.equals(credenceNo) || inCredenceNo == credenceNo)
        {
            isOK = true;
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "700004:用户凭证与系统现存凭证不匹配！");
        }
        return isOK;
    }

    /**
     * 在用业务查询2
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset QueryUserUsingBizInfo2(IData data) throws Exception
    {
        data.put("SERIAL_NUMBER", data.getString("IDITEMRANGE"));

        // 加入对凭证的校验 add by huanghui 20120406
        boolean isCheckUserVoucher = checkUserVoucher(data);
        if (!isCheckUserVoucher)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "700009:用户凭证没有通过校验！");
        }

        String eparchy_code = getVisit().getStaffEparchyCode();
        IData param = new DataMap();
        param.put("REMOVE_TAG", "0");
        param.put("NET_TYPE_CODE", "00");
        param.putAll(data);
        IDataset userInfos = UserVoucherOutQry.getUserInfoBySn(param);
        if (userInfos != null && userInfos.size() > 0)
        {
            param.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
            eparchy_code = userInfos.getData(0).getString("EPARCHY_CODE");
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "880002:获取用户信息无数据！");
        }
        param.put("USER_ID_A", "-1");

        IDataset productInfos = UserVoucherOutQry.getUserAllProducts(param);

        IDataset svcInfos = UserVoucherOutQry.getUserAllSvcInfos(param);

        IDataset discntInfos = UserVoucherOutQry.getUserAlldiscnts(param);

        IDataset platsvcInfos = UserVoucherOutQry.getUserPlatSvc(param);

        IDataset saleactiveInfos = UserVoucherOutQry.getUserSeleactiveInfos(param);

        // 剔除重复的项目
        HashMap<String, String> map = new HashMap<String, String>();

        IDataset pdts = new DatasetList();
        if (productInfos != null && productInfos.size() > 0)
        {
            IData pdt = null;
            for (int i = 0; i < productInfos.size(); i++)
            {
                pdt = new DataMap();
                pdt.put("BIZ_ID", productInfos.getData(i).getString("PRODUCT_ID"));
                pdt.put("BIZ_NAME", UProductInfoQry.getProductNameByProductId(productInfos.getData(i).getString("PRODUCT_ID")));
                pdt.put("BIZ_START_DATE", productInfos.getData(i).getString("START_DATE"));
                pdt.put("BIZ_END_DATE", productInfos.getData(i).getString("END_DATE"));
                pdt.put("BIZ_TIME", SysDateMgr.getSysDate());
                pdt.put("BIZ_TYPE", "套餐");
                map.put(pdt.getString("BIZ_NAME"), "1");
                pdts.add(pdt);
            }
        }

        IDataset svcs = new DatasetList();
        if (svcInfos != null && svcInfos.size() > 0)
        {
            IData svc = null;
            for (int i = 0; i < svcInfos.size(); i++)
            {
                svc = new DataMap();
                svc.put("BIZ_ID", svcInfos.getData(i).getString("SERVICE_ID"));
                svc.put("BIZ_NAME", svcInfos.getData(i).getString("SERVICE_NAME"));
                svc.put("BIZ_START_DATE", svcInfos.getData(i).getString("START_DATE"));
                svc.put("BIZ_END_DATE", svcInfos.getData(i).getString("END_DATE"));
                svc.put("BIZ_TIME", SysDateMgr.getSysDate());
                svc.put("BIZ_TYPE", "服务");
                if (map.get(svc.getString("BIZ_NAME")) == null)
                {
                    map.put(svc.getString("BIZ_NAME"), "1");
                    svcs.add(svc);
                }
            }
        }

        IDataset discnts = new DatasetList();
        if (discntInfos != null && discntInfos.size() > 0)
        {
            IData discnt = null;
            for (int i = 0; i < discntInfos.size(); i++)
            {
                discnt = new DataMap();
                discnt.put("BIZ_ID", discntInfos.getData(i).getString("DISCNT_CODE"));
                discnt.put("BIZ_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discntInfos.getData(i).getString("DISCNT_CODE")));
                discnt.put("BIZ_START_DATE", discntInfos.getData(i).getString("START_DATE"));
                discnt.put("BIZ_END_DATE", discntInfos.getData(i).getString("END_DATE"));
                discnt.put("BIZ_TIME", SysDateMgr.getSysDate());
                discnt.put("BIZ_TYPE", "套餐");
                if (map.get(discnt.getString("BIZ_NAME")) == null)
                {
                    map.put(discnt.getString("BIZ_NAME"), "1");
                    discnts.add(discnt);
                }
            }
        }

        IDataset platsvcs = new DatasetList();
        if (platsvcInfos != null && platsvcInfos.size() > 0)
        {
            IData platsvc = null;
            for (int i = 0; i < platsvcInfos.size(); i++)
            {
                platsvc = new DataMap();
                platsvc.put("BIZ_ID", platsvcInfos.getData(i).getString("SERVICE_ID"));
                platsvc.put("BIZ_NAME", UPlatSvcInfoQry.getSvcNameBySvcId(platsvcInfos.getData(i).getString("SERVICE_ID")));//StaticUtil.getStaticValue(getVisit(), "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME", platsvcInfos.getData(i).getString("SERVICE_ID")));
                platsvc.put("BIZ_START_DATE", platsvcInfos.getData(i).getString("START_DATE"));
                platsvc.put("BIZ_END_DATE", platsvcInfos.getData(i).getString("END_DATE"));
                platsvc.put("BIZ_TIME", SysDateMgr.getSysDate());
                platsvc.put("BIZ_TYPE", "平台服务");
                if (map.get(platsvc.getString("BIZ_NAME")) == null)
                {
                    map.put(platsvc.getString("BIZ_NAME"), "1");
                    platsvcs.add(platsvc);
                }
            }
        }

        IDataset saleactives = new DatasetList();
        if (saleactiveInfos != null && saleactiveInfos.size() > 0)
        {
            IData saleactive = null;
            for (int i = 0; i < saleactiveInfos.size(); i++)
            {
                saleactive = new DataMap();
                saleactive.put("BIZ_ID", saleactiveInfos.getData(i).getString("PRODUCT_ID"));
                saleactive.put("BIZ_NAME", saleactiveInfos.getData(i).getString("PACKAGE_NAME"));
                saleactive.put("BIZ_START_DATE", saleactiveInfos.getData(i).getString("START_DATE"));
                saleactive.put("BIZ_END_DATE", saleactiveInfos.getData(i).getString("END_DATE"));
                saleactive.put("BIZ_TIME", SysDateMgr.getSysDate());
                saleactive.put("BIZ_TYPE", "营销活动");
                if (map.get(saleactive.getString("BIZ_NAME")) == null)
                {
                    map.put(saleactive.getString("BIZ_NAME"), "1");
                    saleactives.add(saleactive);
                }
            }
        }

        IDataset returnInfos = new DatasetList();
        returnInfos.addAll(pdts);
        returnInfos.addAll(discnts);
        returnInfos.addAll(svcs);
        returnInfos.addAll(platsvcs);
        returnInfos.addAll(saleactives);
        return returnInfos;
    }
}
