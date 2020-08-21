
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserWidenetInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 宽带信息查询
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String msisdn = data.getString("SERIAL_NUMBER", "");
        String userId = data.getString("USER_ID", "");
        IDataset output = new DatasetList();
        IData result = new DataMap();
        output.add(result);

        if (StringUtils.isNotBlank(userId))
        {
            output = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryWideUserInfo", data);
            if (IDataUtil.isNotEmpty(output))
            { 
                setTipInfo("");
                IData productSetInfo = output.getData(0).getData("USER_INFO").getData("PRODUCT_SET");
                productSetInfo.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, productSetInfo.getString("BRAND_CODE","")));
                setWidenetInfo(output.getData(0));
                
                if (StringUtils.isBlank(msisdn)) 
                {
                	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
				}
                
                IDataset topsetboxInfos = CSViewCall.call(this,"SS.NoPhoneTopSetBoxSVC.queryTopSetBoxInfoByWsn",data);
                if (IDataUtil.isNotEmpty(topsetboxInfos)) {
					
                	IData topsetboxInfo = topsetboxInfos.first();
					
                	String TopSetBoxSn = topsetboxInfo.getString("SERIAL_NUMBER");  //147手机号码
                	String TopSetBoxUserId = topsetboxInfo.getString("USER_ID");  //147手机号码
					
                	String wideSn = topsetboxInfo.getString("WIDE_SERIAL_NUMBER");  // 宽带号码
                	String wideUserId = topsetboxInfo.getString("WIDE_USER_ID");  // 宽带userid
                	
                	IData inparam = new DataMap();
                	inparam.put("USER_ID", wideUserId);
                	inparam.put("RSRV_VALUE_CODE", "TOPSETBOX");
                	IDataset topsetboxOther = CSViewCall.call(this,"CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparam);
                		
                	if (IDataUtil.isNotEmpty(topsetboxOther)) 
                	{
						IData topsetboxResultInfo = new DataMap();
						topsetboxResultInfo.put("TOPSETBOX_TIME", topsetboxOther.first().getString("RSRV_STR28"));
						topsetboxResultInfo.put("TOPSETBOX_STARTDATE", topsetboxOther.first().getString("START_DATE"));
						topsetboxResultInfo.put("TOPSETBOX_ENDDATE", topsetboxOther.first().getString("RSRV_STR30"));
						setTopsetboxInfo(topsetboxResultInfo);
					}
                }
                
                this.setAjax("SHOWTIP", "NO");
            }
            else
            {
                setTipInfo("该用户尚未办理宽带业务");
                this.setAjax("SHOWTIP", "YES");
            }
        }
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setTipInfo(String tipInfo);

    public abstract void setWidenetInfo(IData wideNetInfo);
    
    public abstract void setTopsetboxInfo(IData topsetboxInfo);
}
