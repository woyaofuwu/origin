
package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 魔百和开户接口数据转换
 * @author yuyj3
 *
 */
public class InternetTvOpenIntfFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        checkInparam(input);
        
        //新大陆传过来是以分为单位  但 校验接口以元为单位会*100，所以传过去是先除100
        input.put("TOP_SET_BOX_DEPOSIT", Integer.parseInt(input.getString("TOP_SET_BOX_DEPOSIT","0"))/100);
        
        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID","");
        
        if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
        {
            IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");
            
            if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
            {
                IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();
                
                input.put("MO_PRODUCT_ID",topSetBoxCommparaInfo.getString("PARA_CODE4"));
                input.put("MO_PACKAGE_ID", topSetBoxCommparaInfo.getString("PARA_CODE5"));
            }
            else
            {
                CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
            }
        }
        
        String topSetBoxSaleActiveId2 = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID2","");
        
        if (StringUtils.isNotBlank(topSetBoxSaleActiveId2))
        {
            IDataset topSetBoxCommparaInfos2 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId2, "0898");
            
            if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos2))
            {
                IData topSetBoxCommparaInfo2 = topSetBoxCommparaInfos2.first();
                
                input.put("MO_PRODUCT_ID2",topSetBoxCommparaInfo2.getString("PARA_CODE4"));
                input.put("MO_PACKAGE_ID2", topSetBoxCommparaInfo2.getString("PARA_CODE5"));
            }
            else
            {
                CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
            }
        }
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        String wSerialNumber = "KD_" + serialNumber;
        
        // 是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
        if (IDataUtil.isEmpty(wideInfos))
        {
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
                CSAppException.appError("-1", "该用户宽带资料信息不存在！");
            }
            
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2","")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            
            if (IDataUtil.isNotEmpty(UserSvcInfoQry.checkInternetTvWide(wSerialNumber)))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"该宽带服务不允许办理魔百和开户"); // 该用户不是不允许的带宽
            }
            
            input.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            input.put("RSRV_STR4", wideNetInfo.getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
        }
        else
        {
            IData wideTD = wideInfos.getData(0);
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            
            if (IDataUtil.isNotEmpty(addrTD))
            {
            	input.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            	input.put("RSRV_STR4", addrTD.first().getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
            }
        }
    }
    
    /**
     * 魔百和开户入参检查
     * 
     * @author yuyj3
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "PRODUCT_ID");
        IDataUtil.chkParam(param, "BASE_PACKAGES");
        IDataUtil.chkParam(param, "WORK_TYPE");
        
    	checkSerialNumber(param);
    }
    
    
    public void checkSerialNumber(IData input) throws Exception
    {
    	
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        
        //判断用户是否含有有效的平台业务
        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }
        
        IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfostow))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和平台业务，不能再办理。");
        }
    }
    
    
}
