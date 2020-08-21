/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.marketmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MarketInfoQry;

/**
 * @CREATED by gongp@2014-7-25 修改历史 Revision 2014-7-25 下午08:02:46
 */
public class MarketInfoMgrBean extends CSBizBean
{
    // 主套餐
    private static final String PACKAGE_CODE = "01";

    // 营销活动
    private static final String ACTIVE_CODE = "02";

    // 产品
    private static final String PRODUCT_CODE = "03";

    public IDataset synchronizeAllCommodity(IData inparam) throws Exception
    {

        IDataUtil.chkParam(inparam, "PROVINCE");
        IDataUtil.chkParam(inparam, "BIZ_TYPE");
        IDataUtil.chkParam(inparam, "ELEMENT_TYPE");
        IDataUtil.chkParam(inparam, "OPR_NUMB");
        // 省份编码
        String province = inparam.getString("PROVINCE", "");
        // 渠道标识 07-移动商城 09-一级WAP营业厅 69-手机营业厅
        String bizType = inparam.getString("BIZ_TYPE", "");
        // 商品类型
        String elementType = inparam.getString("ELEMENT_TYPE", "").trim();
        // 操作流水号 4位机构编码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始
        String oprNumb = inparam.getString("OPR_NUMB", "");

        // 返回查询数据
        IDataset retList = new DatasetList();
        IData exData = new DataMap();
        exData.put("PROD_SYN_RSP", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        retList.add(exData);

        if (PACKAGE_CODE.equals(elementType))
        {
            retList.addAll(MarketInfoQry.getMarketPackage(province));
        }
        else if (ACTIVE_CODE.equals(elementType))
        {
            retList.addAll(MarketInfoQry.getMarketActive(province));
        }
        else if (PRODUCT_CODE.equals(elementType))
        {
            retList.addAll(MarketInfoQry.getMarketProduct(province));
        }
        else
        {
            CSAppException.apperr(ParamException.CRM_PARAM_106, "ELEMENT_TYPE", elementType);
        }

        return retList;
    }

}
