/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * @CREATED by gongp@2014-4-9 修改历史 Revision 2014-4-9 下午04:51:33
 */
public class ForeGiftSVC extends CSBizService
{
    private static final long serialVersionUID = -5231437292008262616L;

    /**
     * 根据发票号码获取发票信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-17
     */
    public IDataset getInvoiceInfo(IData param) throws Exception
    {

        ForeGiftBean bean = BeanManager.createBean(ForeGiftBean.class);

        return bean.getInvoiceInfo(param);
    }

    public IDataset getLoadInfo(IData param) throws Exception
    {
        ForeGiftBean bean = BeanManager.createBean(ForeGiftBean.class);

        return bean.getLoadInfo(param);
    }

    public IDataset getStaffForeGiftOpTypes(IData param) throws Exception
    {
        String paraCode1 = param.getString("PARA_CODE1");

        if (paraCode1 == null || "".equals(paraCode1))
        {
            return CommparaInfoQry.getCommByParaAttr("CSM", "500", "0898");
        }
        else
        {
            return CommparaInfoQry.getCommparaByCode1("CSM", "500", paraCode1, "0898");
        }
    }
    
    /**
     * REQ201610110009_押金业务界面增加判断拦截
     * @author zhuoyingzhi
     * 20161117
     * 效验无主押金清退
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkNotForeGift(IData param) throws Exception
    {
    	IDataset returnData=new DatasetList();
    	IData  data=new DataMap();
    	
        ForeGiftBean bean = BeanManager.createBean(ForeGiftBean.class);
        
        String userId=param.getString("userId");
        double money =param.getDouble("money"); 

        String foregiftCode=param.getString("foregiftCode");
        
        if(bean.isForeGiftMoney(userId, money,foregiftCode)){
        	//需要提示拦截信息
        	data.put("flag", "1");
        }else{
        	data.put("flag", "0");
        }
        
        returnData.add(data);
        
        return returnData;
    } 
    /**
     * REQ201610110009_押金业务界面增加判断拦截
     * @author zhuoyingzhi
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset sysDateCompareToEndDate(IData param) throws Exception
    {
    	IDataset returnData=new DatasetList();
    	IData  data=new DataMap();
    	
        ForeGiftBean bean = BeanManager.createBean(ForeGiftBean.class);
        
        String endDate=param.getString("endDate");
        
        if(bean.sysDateCompareToEndDate(endDate)){
        	//需要提示拦截信息
        	data.put("flag", "1");
        }else{
        	data.put("flag", "0");
        }
        
        returnData.add(data);
        
        return returnData;
    }


    /**
     * add by liangdg3 at 20191024 for REQ201910180018押金业务受理及清退电子化存储需求
     * 查询用户余额（实时结余）接口
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserBalance(IData param) throws Exception{
        ForeGiftBean bean = BeanManager.createBean(ForeGiftBean.class);
        String userBalance= bean.getUserBalance(param.getString("USER_ID_FOR_BALANCE"));
        IDataset returnData=new DatasetList();
        IData  data=new DataMap();
        data.put("USER_BALANCE",userBalance);
        returnData.add(data);
        return returnData;
    }
}
