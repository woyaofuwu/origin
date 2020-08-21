
package com.asiainfo.veris.crm.order.web.person.sundryquery.realname;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryCustNumber extends PersonQueryPage
{
    /**
     * 一证多号信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustNumber(IRequestCycle cycle) throws Exception
    {

        IData cond = getData();
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", cond.getString("PSPT_TYPE_CODE"));
        param.put("PSPT_ID", cond.getString("PSPT_ID"));
        param.put("ROUTE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.NationalOpenLimitSVC.queryCustNumber", param);

        if (IDataUtil.isNotEmpty(results)) {
            for (int i = 0; i < results.size(); i++) {
                String province = results.getData(i).getString("HOME_PROV");
                String provinceName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "GLOBAL_PROVINCE_CODE", province });
                if (provinceName != null && provinceName.trim().length() > 0) {
                    results.getData(i).put("HOME_PROV", provinceName);
                }
            }
        }

        String strSYS012 = cond.getString("X_DATA_NOT_FUZZY", "");
        //手机号中间4位模糊化处理，有免模糊化权限的工号跳过模糊化处理
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012") && !"true".equals(strSYS012)) {
            if (IDataUtil.isNotEmpty(results)) {
                for (int i = 0; i < results.size(); i++) {
                    String serialNumber = results.getData(i).getString("IDV");
                    if (StringUtils.isNotBlank(serialNumber)) {
                        if (serialNumber.length() == 11) {
                            StringBuilder mask = new StringBuilder(11);
                            for (int j = 0; j < 3; j++) {
                                mask.append(serialNumber.charAt(j));
                            }
                            for (int j = 3; j < 7; j++) {
                                mask.append("*");
                            }
                            for (int j = 7; j < serialNumber.length(); j++) {
                                mask.append(serialNumber.charAt(j));
                            }
                            results.getData(i).put("IDV", mask.toString());
                        }
                    }
                }
            }
        }

        setAjax(results);
        setNumberInfos(results);
        setNumberCount(results.size());
    }

    public abstract void setNumberCount(long discntCount);

    public abstract void setNumberInfos(IDataset infos);
}
