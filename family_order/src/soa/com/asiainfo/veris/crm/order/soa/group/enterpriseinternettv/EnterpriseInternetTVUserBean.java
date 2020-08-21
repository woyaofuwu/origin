
package com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;

public class EnterpriseInternetTVUserBean extends GroupOrderBaseBean
{
	private static transient Logger logger = Logger.getLogger(EnterpriseInternetTVUserBean.class);

    /**
     * 构造函数
     */
    public EnterpriseInternetTVUserBean()
    {

    }
    
    public void actOrderDataOther(IData map) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", map.getString("USER_ID")); // 集团user_ID
        param.put(Route.USER_EPARCHY_CODE, "0898");
        param.put("GPSERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        IDataset dataset = new DatasetList(map.getString("FTTH_DATASET"));
        for (int i = 0; i < dataset.size(); i++)
        {
            IData rela = dataset.getData(i);
    
            param.put("SERIAL_NUMBER", rela.getString("KD_NUMBER")); // 成员
            param.put("KD_NUMBER", rela.getString("KD_NUMBER")); // 宽带号码
            param.put("KD_RES_SUPPLY_COOPID", rela.getString("KD_RES_SUPPLY_COOPID")); // 终端供应商
            param.put("KD_RES_ID", rela.getString("KD_RES_ID")); // 终端编号
            param.put("KD_ADDR", rela.getString("KD_ADDR")); // 宽带地址
            param.put("KD_RES_BRAND_NAME", rela.getString("KD_RES_BRAND_NAME")); // 终端品牌
            param.put("KD_RES_BRAND_CODE",rela.getString("KD_RES_BRAND_CODE"));// 终端品牌编码
            param.put("KD_RES_TYPE_CODE",rela.getString("KD_RES_TYPE_CODE"));// 终端类型编码
            param.put("KD_RES_KIND_NAME", rela.getString("KD_RES_KIND_NAME")); // 终端型号
            param.put("KD_RES_KIND_CODE",rela.getString("KD_RES_KIND_CODE"));// 终端型号编码
            param.put("KD_ARTIFICIAL_SERVICES", rela.getString("KD_ARTIFICIAL_SERVICES")); // 上门标识      
            param.put("KD_RES_FEE", rela.getString("KD_RES_FEE")); // 终端价格(元)
            param.put("KD_DEVICE_COST", rela.getString("KD_DEVICE_COST")); // 终端价格
            param.put("CUST_NAME", rela.getString("CUST_NAME")); // 宽带联系人
            param.put("KD_RES_STATE_NAME", rela.getString("KD_RES_STATE_NAME")); // 终端状态          
            param.put("KD_RES_STATE_CODE", rela.getString("KD_RES_STATE_CODE")); // 终端状态编码 
            param.put("KD_PHONE", rela.getString("KD_PHONE")); // 宽带联系电话
            param.put("KD_USERID", rela.getString("KD_USERID")); // 宽带user_id
            param.put("KD_REMARK", rela.getString("KD_REMARK")); // 宽带备注
            //param.put("USER_ID", rela.getString("USER_ID")); //  集团user_ID
            param.put("REMARK", "企业互联网电视终端申领"); // 备注
            
            
            if (StringUtils.isNotBlank(param.getString("SERIAL_NUMBER")))
            {
            	IDataset ds = new DatasetList();
            	try {
            		ds = CSAppCall.call("SS.EnterpriseInternetTVSVC.crtTrade", param);
                    logger.debug(ds);
				} catch (Exception e) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"生成工单调用异常！" + e);
				} 
//            	Modify By chenwei6  第二次调用终端占用接口，重复以致报错
//            	finally{
//					if(IDataUtil.isNotEmpty(ds) && ds.size() > 0){
//						param.put("TRADE_ID", ds.getData(0).getString("TRADE_ID",""));
//						updateModem(param);
//					}
//				}
            }
        }
    }

    protected String setOrderTypeCode() throws Exception
    {
        return "4300";
    }
    
    /**
     * @Function: updateModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void updateModem(IData map) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_NO", map.getString("KD_RES_ID",""));//串号
        param.put("REMARK", "企业互联网电视终端申领");
        param.put("SALE_FEE", "0");//销售费用:不是销售传0
        param.put("PARA_VALUE1", map.getString("KD_PHONE",""));//购机用户的手机号码
        param.put("PARA_VALUE4", map.getString("KD_USERID",""));
        param.put("PARA_VALUE7", "0");//代办费
        param.put("DEVICE_COST", map.getString("KD_DEVICE_COST",""));//进货价格--校验接口取
        param.put("TRADE_ID ",  map.getString("TRADE_ID",""));//台账流水 
        param.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
        param.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
        param.put("CONTRACT_ID",  map.getString("TRADE_ID",""));//销售订单号
        param.put("INFO_TAG", "1");
        param.put("PRODUCT_MODE", "0");
        param.put("X_RES_NO_S", map.getString("KD_RES_ID",""));
        param.put("X_RES_NO_E", map.getString("KD_RES_ID",""));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("PARA_VALUE10", map.getString("KD_RES_KIND_CODE",""));
        param.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
        param.put("PARA_VALUE12", CSBizBean.getVisit().getDepartId());//销售部门
        param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
        param.put("PARA_VALUE14",  map.getString("KD_DEVICE_COST",""));//裸机价格  从检验接口取裸机价格
        param.put("PARA_VALUE15", "0");//客户购机折让价格
        param.put("PARA_VALUE16", "0");
        param.put("PARA_VALUE17", "0");
        param.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
        param.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
        param.put("PARA_VALUE1", map.getString("KD_PHONE",""));//客户号码
        param.put("USER_NAME", map.getString("CUST_NAME",""));//客户姓名
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());//销售员工
        param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
        if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
            String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
            if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
            }
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
        }
    }

}
