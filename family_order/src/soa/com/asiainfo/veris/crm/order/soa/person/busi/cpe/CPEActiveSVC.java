
package com.asiainfo.veris.crm.order.soa.person.busi.cpe;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 *  
 */
public class CPEActiveSVC extends CSBizService
{

    private static final long serialVersionUID = 5502421746441250576L;

    /**
     * 副号码校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-18
     */
    public IData checkBySerialNumber(IData input) throws Exception
    {

        CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class);

        return bean.checkBySerialNumber(input);
    }
    
    public IDataset loadChildInfo(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        return bean.loadChildInfo(input);
    }
    
    public IData checkCEPNumber(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        return bean.checkCPENumber(input);
    }
    
    /**
     * CPE小区锁定
     * */
    public void lockArea(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        bean.lockArea(input);
    }
    
    /**
     * CPE小区解锁查询
     * */
    public IDataset unlockAreaQryInfo(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        return bean.unlockAreaQryInfo(input);
    }
    
    /**
     * CPE 开户满3年返还押金
     * */
    public void check3yearCPEUser(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
         bean.check3yearCPEUser(input);
    } 
    
    /**
     * CPE用户信息查询
     * */
    public IDataset cpeInfoQry(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        return bean.cpeInfoQry(input);
    } 
    
    /**
     * REQ201601120007 CPE无线宽带查询用户使用业务所在小区界面
     * chenxy3 2016-01-15
     * */
    public IDataset dataAmountQry(IData input)throws Exception{
    	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
        return bean.dataAmountQry(input);
    }
    /**
     * REQ201612260011_新增CPE终端退回和销户界面
     * @author zhuoyingzhi
     * @date 20170215
     * 获取设备信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryDeviceInfo(IData input)throws Exception{
        CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class);
        String userId=input.getString("USER_ID", "");
        return bean.qryCPEOtherInfo(userId,"CPE_DEVICE");
    }    
}