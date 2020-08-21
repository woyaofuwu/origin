
package com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class EnterpriseInternetTVSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        EnterpriseInternetTVBean bean = new EnterpriseInternetTVBean();

        return bean.crtTrade(inParam);
    }
       
    
    /**
     * @description 根据商品用户编号、产品关系编号和角色编号查询用户UU关系
     * @author xuyt
     * @date 2014-07-25
     */
    public  IDataset qryRelaBBInfoByRoleCodeBForGrp(IData data) throws Exception
    {
        EnterpriseInternetTVBean bean = new EnterpriseInternetTVBean();

        return bean.qryRelaBBInfoByRoleCodeBForGrp(data, this.getPagination());
    }
    
    public  IDataset getOtherInfoByCodeUserId(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID_B");
        return UserOtherInfoQry.getOtherInfoByCodeUserIdTrade(user_id,"EITV");
    }
    
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public IData checkTerminal(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
            IData res = retDataset.first();
            String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
            String supplyId = res.getString("SUPPLY_COOP_ID", "");
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            retData.put("RES_ID", resNo); // 终端串号
            retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
            retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
            retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
            retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
            retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
            retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
            String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
            retData.put("RES_STATE_CODE", resStateCode);
            retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
            retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
            retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
            
            retData.put("DEVICE_COST", res.getString("DEVICE_COST","0")); // 进货价格
                     
            
            // 获取产品信息
            IDataset prodInfos = ProductInfoQry.querySTBProducts(resKindCode, supplyId);
            if (DataSetUtils.isBlank(prodInfos) && !StringUtils.equals(resKindCode, "N"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1185, resKindCode); // 该机型[%s]未绑定产品，请联系系统管理员!
            }
            retData.put("PRODUCT_INFO_SET", prodInfos);
        }
        else
        {
            String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        return retData;
    }
    
}
