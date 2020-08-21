
package com.asiainfo.veris.crm.order.soa.group.custmanager;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.common.query.CustManagerTJNumQry;

/**
 * 客户经理业务办理登记Bean
 * 
 * @author lizu
 */
public class CustManagerTJNumBean extends GroupBean
{
    private static final Logger logger = Logger.getLogger(CustManagerTJNumBean.class);

    private IData tjInfo = null;

    /**
     * 根据工号查询集团客户经理
     * 
     * @param pd
     * @param param
     * @return
     */
    public static IDataset checkCustManagerStaff(IData param) throws Exception
    {
        IDataset resultinfos = new DatasetList();
        IData result = new DataMap();
        String staffId = param.getString("MANAGER_STAFF_ID");
        IData newParam = new DataMap();
        newParam.put("STAFF_ID", staffId);
        IData staffs = UStaffInfoQry.qryStaffInfoByPK(staffId);
        if (IDataUtil.isEmpty(staffs))
        {
            CSAppException.apperr(CustException.CRM_CUST_997, staffId);
        }
        // 判断员工号是否是集团客户经理
        int count = CustManagerTJNumQry.queryGroupCustManagerByStaffId(newParam);
        if (count == 0)
        {
            CSAppException.apperr(CustException.CRM_CUST_998, staffId);
        }
        String name = staffs.getString("STAFF_NAME", "");
        result.put("MANGER_NAME", name);
        resultinfos.add(result);
        return resultinfos;

    }

    /**
     * 验证号码是否为某一集团成员
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset checkGbmBySerialNumber(IData param) throws Exception
    {
        // 推荐的号码必须为某一898集团客户的成员
        IDataset infos = CustManagerTJNumQry.queryGbmBySerialNumber(param);

        return infos;
    }

    /**
     * 验证号码推荐次数
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset checkSerialNumberCont(IData param) throws Exception
    {
        IDataset contInfos = CustManagerTJNumQry.countCustManagerTjNum(param);

        return contInfos;
    }

    /**
     * 添加客户经理推荐号码信息
     * 
     * @param pd
     * @param param
     * @return
     * @return
     * @throws Exception
     */
    public IDataset insertCustManagerTJNum(IData param) throws Exception
    {
        IData newParam = new DataMap();
        newParam.put("LOG_ID", SeqMgr.getTJNumBizCode());
        newParam.put("MANGER_STAFF_ID", param.getString("MANAGER_STAFF_ID", ""));
        newParam.put("MANGER_NAME", param.getString("MANGER_NAME", ""));
        newParam.put("TJNUMBER", param.getString("TJNUMBER", "").trim());
        newParam.put("ACTIVE_ID", param.getString("ACTIVE_ID", ""));
        newParam.put("ACTIVE_NAME", param.getString("ACTIVE_NAME", ""));
        newParam.put("REMARK", param.getString("REMARK", ""));
        newParam.put("IN_MODE", "0");
        newParam.put("IN_DATE", SysDateMgr.getSysTime());
        newParam.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        newParam.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        newParam.put("RSRV_STR1", "");
        newParam.put("RSRV_STR2", "");
        newParam.put("RSRV_STR3", "");
        newParam.put("RSRV_STR4", "");
        newParam.put("RSRV_STR5", "");

        boolean infos = CustManagerTJNumQry.createInfosByParam(newParam);

        return CustManagerTJNumQry.queryInfosByParam(newParam.getString("LOG_ID"));
    }

    /**
     * 执行查询
     * 
     * @param pd
     * @param param
     * @param pageinfo
     * @return
     * @throws Exception
     */
    public static IDataset queryCustManagerTjNums(IData param) throws Exception
    {
        IDataset infos = CustManagerTJNumQry.queryCustManagertjNums(param);
        return infos;
    }

    /**
     * 根据类型查询营销活动
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryProductsByType(IData param) throws Exception
    {
        IDataset products = CustManagerTJNumQry.getProductInfos(param);
        if (IDataUtil.isNotEmpty(products))
        {
            for (int i = 0; i < products.size(); i++)
            {
                IData each = products.getData(i);
                // 不包含集团版尊荣畅享
                if ("69900375".equals(each.getString("PRODUCT_ID", "")))
                {
                    products.remove(each);
                    i--;
                }
                // 不包含集团合约计划
                if (each.getString("PRODUCT_NAME", "").indexOf("集团合约计划") > -1)
                {
                    products.remove(each);
                    i--;
                }
            }
            // 含常态终端销售
            IData data = new DataMap();
            data.put("PRODUCT_ID", "00000000");
            data.put("PRODUCT_NAME", "常态终端销售");
            products.add(data);
        }
        else
        {
            products = new DatasetList();
        }
        return products;
    }

    public IDataset custManagerTJNum(IData map) throws Exception
    {
        tjInfo = map.getData("TJ_INFO");

        String serialNumber = tjInfo.getString("TJNUMBER");
        String activeId = tjInfo.getString("ACTIVE_ID");
        String activeName = tjInfo.getString("ACTIVE_NAME");
        String staffName = tjInfo.getString("MANGER_NAME");
        String custManagerStaffid = tjInfo.getString("MANAGER_STAFF_ID");

        IData param = new DataMap();
        param.put("MANAGER_STAFF_ID", custManagerStaffid);
        param.put("TJNUMBER", serialNumber);
        param.put("ACTIVE_ID", activeId);
        param.put("ACTIVE_NAME", activeName);
        param.put("MANGER_NAME", staffName);
        param.put("REMARK", "批量导入");

        IDataset custinfosDataset = insertCustManagerTJNum(param);
        IDataset result = new DatasetList();
        IData cusData = custinfosDataset.getData(0);
        IData data = new DataMap();
        data.put("ORDER_ID", cusData.getString("LOG_ID"));
        result.add(data);

        return result;

    }
}
