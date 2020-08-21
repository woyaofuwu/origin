package com.asiainfo.veris.crm.order.soa.person.busi.agentcheck;
  
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 

/** 
 * REQ201602140004下岛问题优化--资源管理相关优化
 * @chenxy3@2016-2-26 修改 
 */
public class AgentStorageCheckSVC extends CSBizService
{
 
    /**
     * 查询库存
     */
    public IDataset checkCardStorage(IData inparams) throws Exception
    {

    	return AgentStorageCheckBean.checkCardStorage(inparams,getPagination());
    }
    
    /**
     * REQ201802260011_关于优化渠道空中充值缴费上限功能的需求
     * <br/>
     * 通过手机号码判断是否为代理商号码
     * @param inparam
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180307
     */
    public  IData checkIsAgentSerialNumber(IData inparam) throws Exception{
    	
    	return AgentStorageCheckBean.checkIsAgentSerialNumber(inparam);
    }
}