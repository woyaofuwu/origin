
package com.asiainfo.veris.crm.order.pub.util;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;

/*
 * 该类直接和资源国税接口相接,将业务传过来的参数,转换成资源国税接口相对应字段
 */
public final class TaxUtils
{
    /**
     * 发票种类代码
     * 
     * @param
     * @return String 发票种类代码
     * @throws Exception
     */
    public static String getFpzldm() throws Exception
    {
        return BizEnv.getEnvString("crm.statetax.Fpzldm", "");
    }

    /**
     * 纳税人识别号,业务类型大于9000的为铁通业务,其它为移动业务
     * 
     * @param tradeTypeCode
     *            业务编码
     * @return
     * @throws Exception
     */
    public static String getNsrlx(String tradeTypeCode)
    {
        return tradeTypeCode.compareTo("9000") > 0 ? "2" : "1";//此处是和资源约定的值,不会变化,此处写死
    }

    /**
     * 票样代码,一般业务使用模板为SIM卡销售发票打印模板701005,营销活动采用终端预存合约发票打印模板701006,有价卡销售采用预存款发布打印模板701003
     * 该票样代码为航信约定,后续恐新增或者变动,此处采用配置
     * @param tradeTypeCode
     *            业务编码
     * @return String 票样代码
     * @throws Exception
     */
    public static String getPydm(String tradeTypeCode) throws Exception
    {
        //return yxTrade(tradeTypeCode) ? BizEnv.getEnvString("crm.statetax.Hypydm", "") : BizEnv.getEnvString("crm.statetax.Simpydm", "");
    	String pydm="";
    	if(yxTrade(tradeTypeCode)){
    		pydm=BizEnv.getEnvString("crm.statetax.Hypydm", "");
    	}else if("416".equals(tradeTypeCode)){//有价卡销售采用预存款发布打印模板701003
    		pydm=BizEnv.getEnvString("crm.statetax.ValueCardydm", "");
    	}else{
    		pydm=BizEnv.getEnvString("crm.statetax.Simpydm", "");
    	}
    	return pydm;
    }

    /**
     * 判断系统是否支持营改增
     * 
     * @return
     * @throws Exception
     */
    public static boolean isYgzTag() throws Exception
    {
        // 默认不支持
        return BizEnv.getEnvBoolean("crm.ygz.enable", false);
    }

    /**
     * 组装调用资源接口时要传入的国税发票信息
     * 
     * @param feeList
     *            费用项
     * @param param
     *            业务参数
     * @return IData 组装好的发票信息
     * @throws Exception
     */
    public static IData setStateTaxPackageInfo(IDataset spList, IData param) throws Exception
    {
        IData packageInfo = new DataMap();

        // 1:移动,2:铁通
        packageInfo.put("NSRLX", getNsrlx(param.getString("TRADE_TYPE_CODE", "")));
        packageInfo.put("TICKET_TYPE_CODE", param.getString("TICKET_TYPE_CODE", "D"));
        packageInfo.put("TAX_NO", param.getString("TAX_NO", ""));
        packageInfo.put("TICKET_ID", param.getString("TICKET_ID", ""));
        packageInfo.put("O_TAX_NO", param.getString("O_TAX_NO", ""));
        packageInfo.put("O_TICKET_ID", param.getString("O_TICKET_ID", ""));
        packageInfo.put("TICKET_TYPE_CODE", param.getString("TICKET_TYPE_CODE", ""));
        
        String custName = param.getString("CUST_NAME");
        if(StringUtils.isBlank(custName))
        	custName = "个人";
        packageInfo.put("FKDW", custName);
        packageInfo.put("KPHJJE", param.getString("TOTAL_FEE", "0"));
        packageInfo.put("HJJE", param.getString("TOTAL_FEE", "0"));
        packageInfo.put("KHPP", param.getString("BRAND", ""));// 客户品牌

        packageInfo.put("KPLX", param.getString("KPLX", ""));//开票类型
        packageInfo.put("FPZL_DM", getFpzldm());// 发票种类代码,测试数据
        packageInfo.put("PYCODE", getPydm(param.getString("TRADE_TYPE_CODE", "")));

        packageInfo.put("YWLB", param.getString("TRADE_TYPE", ""));
        packageInfo.put("YHHM", param.getString("SERIAL_NUMBER", ""));
        packageInfo.put("HFZQ", param.getString("CUR_CYCLE", ""));
        packageInfo.put("LSH", param.getString("TRADE_ID", ""));

        IDataset spmx = new DatasetList();
        packageInfo.put("SPMX", spList);
        return packageInfo;
    }

    /**
     * @Description 是否为营销活动
     * 
     * @param tradeTypeCode
     *            业务编码
     * @return
     * @throws Exception
     */
    public static boolean yxTrade(String tradeTypeCode)
    {
        boolean yxTrade = false;

        if ("230".equals(tradeTypeCode) || "240".equals(tradeTypeCode) || "255".equals(tradeTypeCode) || "257".equals(tradeTypeCode) || "258".equals(tradeTypeCode) || "252".equals(tradeTypeCode) || "3814".equals(tradeTypeCode) || "256".equals(tradeTypeCode) || "3815".equals(tradeTypeCode))
            yxTrade = true;

        return yxTrade;
    }
    
    /**
     * @Description 是否为国税发票
     * 
     * @param ticketTypeCode 票据类型
     * @return
     * @throws Exception
     */
    public static boolean stateTaxTicket(String ticketTypeCode){
        boolean stateTaxTicket = false;

        if (StringUtils.equals("D", ticketTypeCode) || StringUtils.equals("E", ticketTypeCode))
        	stateTaxTicket = true;

        return stateTaxTicket;
    }
    
    /**
     * @Description 是否为收据
     * 
     * @param ticketTypeCode 票据类型
     * @return
     * @throws Exception
     */
    public static boolean voucherTicket(String ticketTypeCode){
        boolean voucherTicket = false;

        if (StringUtils.equals("B", ticketTypeCode) || StringUtils.equals("F", ticketTypeCode))
        	voucherTicket = true;

        return voucherTicket;
    }
}
