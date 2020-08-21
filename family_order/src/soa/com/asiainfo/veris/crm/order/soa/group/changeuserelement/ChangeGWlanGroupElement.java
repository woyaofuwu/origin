
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeGWlanGroupElement extends ChangeUserElement
{
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记other表，服务开通侧用
        infoRegDataOther();

    }

    /**
     * 其它台帐处理
     */
    public void infoRegDataOther() throws Exception
    {
     // 2- 根据产品编号获取产品参数
        IDataset productParamInfoList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return;
        }
        IDataset lineDataset = new DatasetList();
        
        IDataset otherInfoList = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "GRP_WLAN", null);


        
        if (productParamInfoList != null && productParamInfoList.size() > 0)
        {
            for (int i = 0; i < productParamInfoList.size(); i++)
            {
                IData productData = productParamInfoList.getData(i);
                if("PAY_APMONEY".equals(productData.getString("ATTR_CODE", "")) 
                        || "PAY_WLMONEY".equals(productData.getString("ATTR_CODE", ""))
                        || "PAY_ONECOST".equals(productData.getString("ATTR_CODE", "")))
                {
                    if (otherInfoList != null && otherInfoList.size() > 0)
                    {
                      //变更时先将原数据END_DATE修改为当前时间
                        for(int j=0;j<otherInfoList.size();j++)
                        {
                            IData olddata = otherInfoList.getData(j);
                                                       
                            //IData newdata  = (IData) Clone.deepClone(olddata);
                            if(productData.getString("ATTR_CODE").equals(olddata.getString("RSRV_VALUE")) && !productData.getString("ATTR_VALUE").equals(olddata.getString("RSRV_STR2")))
                            {
                                olddata.put("UPDATE_TIME", getAcceptTime());
                                //olddata.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                                
                                /*
                                Calendar calendar = Calendar.getInstance();
                                int month = calendar.get(Calendar.MONTH);
                                calendar.set(Calendar.MONTH, month-1);
                                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                                Date strDateTo = calendar.getTime();
                                olddata.put("END_DATE", DateFormatUtils.format(strDateTo, "yyyy-MM-dd"));
                                */
                                
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DATE, 1);// 设置月的第一天
                                cal.add(Calendar.DATE, -1);// 本月的一天-1就是上个月的最后一天
                                String endDate = format.format(cal.getTime()) + " 23:59:59";
                                
                                olddata.put("END_DATE", endDate);
                                olddata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        
                                lineDataset.add(olddata);
                                
                                
                                IData data = new DataMap();
                                data.put("USER_ID", reqData.getUca().getUserId());
                                data.put("RSRV_VALUE_CODE", "GRP_WLAN"); 
                                if("PAY_APMONEY".equals(productData.getString("ATTR_CODE", ""))){
                                    data.put("RSRV_STR1", "AP月租费");
                                    data.put("RSRV_VALUE", "PAY_APMONEY");
                                } else if("PAY_ONECOST".equals(productData.getString("ATTR_CODE", ""))){
                                    data.put("RSRV_STR1", "一次性开通使用费用");
                                    data.put("RSRV_VALUE", "PAY_ONECOST");
                                }
                                else 
                                {
                                    data.put("RSRV_STR1", "带宽月租费");
                                    data.put("RSRV_VALUE", "PAY_WLMONEY");
                                }
                                data.put("RSRV_STR2", productData.getString("ATTR_VALUE", ""));
                                data.put("RSRV_STR3", "集团WLAN");
                                data.put("RSRV_STR4", "ChgUs");//操作类型
                                data.put("RSRV_STR5", "3561");//业务类型
                                data.put("RSRV_STR6", "7130");//产品参数
                                data.put("START_DATE", getAcceptTime());
                                data.put("END_DATE", SysDateMgr.getTheLastTime());
                                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                data.put("INST_ID", SeqMgr.getInstId());
                                lineDataset.add(data);
                            }
                        }
                        
                    } 
                    else
                    {                    
                        IData data = new DataMap();
                        data.put("USER_ID", reqData.getUca().getUserId());
                        data.put("RSRV_VALUE_CODE", "GRP_WLAN"); 
                        if("PAY_APMONEY".equals(productData.getString("ATTR_CODE", ""))){
                            data.put("RSRV_STR1", "AP月租费");
                            data.put("RSRV_VALUE", "PAY_APMONEY");
                        } else if("PAY_ONECOST".equals(productData.getString("ATTR_CODE", ""))){
                            data.put("RSRV_STR1", "一次性开通使用费用");
                            data.put("RSRV_VALUE", "PAY_ONECOST");
                        } 
                        else
                        {
                            data.put("RSRV_STR1", "带宽月租费");
                            data.put("RSRV_VALUE", "PAY_WLMONEY");
                        }
                        data.put("RSRV_STR2", productData.getString("ATTR_VALUE", ""));
                        data.put("RSRV_STR3", "集团WLAN");
                        data.put("RSRV_STR4", "CrtUs");//操作类型
                        data.put("RSRV_STR5", "3560");//业务类型
                        data.put("RSRV_STR6", "7130");//产品参数
                        data.put("START_DATE", getAcceptTime());
                        data.put("END_DATE", SysDateMgr.getTheLastTime());
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        data.put("INST_ID", SeqMgr.getInstId());
                        lineDataset.add(data);
                    }
                }
            }
            addTradeOther(lineDataset);
        }
    }
    
    /**
     * 计算上一个月最后一天
     * @return
     */
    public static String getLastMonthLastDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);// 设置月的第一天
        cal.add(Calendar.DATE, -1);// 本月的一天-1就是上个月的最后一天
        return format.format(cal.getTime()) + " 23:59:59";
    }
}
