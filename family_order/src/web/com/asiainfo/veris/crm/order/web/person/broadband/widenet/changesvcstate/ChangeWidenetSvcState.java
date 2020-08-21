/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeWidenetSvcState extends PersonBasePage
{
    /**
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        IData wideInfo = dataset.first();
        if(wideInfo == null){
        	CSViewException.apperr(WidenetException.CRM_WIDENET_1,data.getString("USER_ID"));
        }else{
        	wideInfo.put("WIDE_TYPE",wideInfo.getString("RSRV_STR2"));
        	 setWideInfo(wideInfo);
        }
        /**
         * REQ201708240014_家庭IMS固话开发需求
         * <br/>
         * 初始化是否显示关联报开IMS家庭固话
         * @author zhuoyingzhi
         * @date 20171219
         */
        IData retrunData=new DataMap();
        String  tradeTypeCode=data.getString("TRADE_TYPE_CODE","");
        String  isImsUser="N";
        if("604".equals(tradeTypeCode)){
        	//宽带报开
        	IData param = new DataMap();
        		  param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));
        	IData ImsInfo=CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//存在IMS家庭固话
        		isImsUser="Y";
        	}
        }
        retrunData.put("IS_IMS_USER", isImsUser);
        setAjax(retrunData);
    }
    
    /**
     * 业务类型初始化
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        if ("SCHOOL".equals(data.getString("WIDE_TYPE")))
        {
            if ("0".equals(data.getString("TAG")))
            {
                param.put("TRADE_TYPE_CODE", "632");// 报停
            }
            else if ("1".equals(data.getString("TAG")))
            {
                param.put("TRADE_TYPE_CODE", "633");// 报开
            }
        }
        else
        {
            if ("0".equals(data.getString("TAG")))
            {
                param.put("TRADE_TYPE_CODE", "603");// 报停
            }
            else if ("1".equals(data.getString("TAG")))
            {
                param.put("TRADE_TYPE_CODE", "604");// 报开
            }
        }

        setInfo(param);
    }

    /**
     * @param requestCycle
     * @description:服务状态变更业务提交类
     * @author: chenzm
     */
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ChangeWidenetSvcStateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    /**
     * REQ201708240014_家庭IMS固话开发需求
     * <br/>
     * 判断用户是否是宽带报停
     * @author zhuoyingzhi
     * @date 20171220
     */
    public void checkWidnetStopIMS(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.WidenetInfoQuerySVC.checkWidnetStopIMS", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            setAjax(resultData);
        }
    }
    public abstract void setInfo(IData info);

    public abstract void setWideInfo(IData wideInfo);
}
