
package com.asiainfo.veris.crm.order.soa.person.busi.batelectronicworkorderbulu;
 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;

public class BatElectronicworkorderbuluBean extends CSBizBean
{
    private static transient final Logger log = Logger.getLogger(BatElectronicworkorderbuluBean.class); 

    /**
     * 纸质单据电子化信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset queryElectronicworkorder(IData input, Pagination pagination) throws Exception
    {
        String eparchyCode = CSBizBean.getTradeEparchyCode();
        String serialNumber = input.getString("SERIAL_NUMBER", "").trim();
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        IDataset printsInfos = null ;       
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);
        if (IDataUtil.isNotEmpty(userInfo))
        {
			String endDate = input.getString("END_DATE", "");
	        if (StringUtils.isNotBlank(endDate))
	        {
	            endDate = endDate + SysDateMgr.getEndTime235959();
	        }
            printsInfos = TradeReceiptInfoQry.queryElectronicworkorderbulu(serialNumber,input.getString("START_DATE", ""), endDate, tradeTypeCode, pagination, eparchyCode);
            IDataset returnValues = new DatasetList();
	        IData printsInfo = new DataMap();
	        if (IDataUtil.isNotEmpty(printsInfos))
	        {
	            for (int i = 0; i < printsInfos.size(); i++)
	            {
	                printsInfo = (IData) printsInfos.getData(i);
	                
	                if("0".equals(printsInfo.getString("CANCEL_TAG")))
	                {
		                printsInfo.put("VIP_CLASS", getCheckModeName(printsInfo.getString("VIP_CLASS", "")));

		                printsInfo.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(printsInfo.getString("PRIORITY", "")));

		                printsInfo.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(printsInfo.getString("STAFF_ID", "")));

		                printsInfo.put("DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(printsInfo.getString("DEPART_ID", "")));

		                returnValues.add(printsInfo);
	                
	                }
	            }
	        }
	        return returnValues;
		}
        return null;
    }
    
    /**
     * 纸质单据电子化，传到东软
     * 
     * @param cycle
     * @throws Exception
     */
    public IData electronicworkorderbuluToDzh(IData input, Pagination pagination) throws Exception
    {
    	String eparchyCode = CSBizBean.getTradeEparchyCode();
        String tradeId = input.getString("TRADE_ID");
        IDataset printsInfos = TradeReceiptInfoQry.toElectronicworkorderbulu(tradeId, pagination, eparchyCode);
        IDataset returnValues = new DatasetList();
        IData printsInfo = new DataMap();
        if (IDataUtil.isNotEmpty(printsInfos))
        {
            for (int i = 0; i < printsInfos.size(); i++)
            {
                printsInfo = (IData) printsInfos.getData(i);
                printsInfo.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(printsInfo.getString("TRADE_STAFF_ID", "")));
                printsInfo.put("DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(printsInfo.getString("ORG_INFO", "")));
                printsInfo.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(printsInfo.getString("BRAND_CODE", "")));
                
                returnValues.add(printsInfo);
            }
        }
        return returnValues.getData(0);
    }
    
    /**
     * 转义成认证方式
     * 
     * @param tag
     * @return
     */
    private String getCheckModeName(String tag)
    {
        String checkName = null;
        if (StringUtils.equals(tag, "0"))
        {
            checkName = "证件号码校验";
        }
        else if (StringUtils.equals(tag, "1"))
        {
            checkName = "服务密码校验";
        }
        else if (StringUtils.equals(tag, "2"))
        {
            checkName = "SIM卡号+服务密码校验";
        }
        else if (StringUtils.equals(tag, "3"))
        {
            checkName = "服务号码+证件号码校验   ";
        }
        else if (StringUtils.equals(tag, "4"))
        {
            checkName = "证件号码+服务密码校验";
        }
        else if (StringUtils.equals(tag, "5"))
        {
            checkName = "SIM卡号+短信验证码校验";
        }
        /**
         * REQ201512020036 用户押金发票号码查询界面优化
         * chenxy3 20160106 增加换卡（写卡）新的认证组合方式
         * */
        else if (StringUtils.equals(tag, "6"))
        {
            checkName = "服务密码+验证码";
        }
        /**
		   * REQ201606230019非实名用户关停改造需求
		   * chenxy3 新的独立认证方式
		   * */
        else if (StringUtils.equals(tag, "7"))
        {
            checkName = "验证码";
        }
        else if (StringUtils.equals(tag, "8"))
        {
            checkName = "SIM卡号(或白卡号)";
        }
        /**
		   * REQ201610200008 补换卡业务调整需求
		   * chenxy3 新的独立认证方式 客户证件+证件类型+验证码
		   * */
        else if (StringUtils.equals(tag, "9")){
        	checkName = "有效证件+验证码";
 		  }
        else if (StringUtils.equals(tag, "F"))
        {
            checkName = "免认证";
        }
        else
        {
            checkName = "未知校验方式";
        }

        return checkName;
    }
}
