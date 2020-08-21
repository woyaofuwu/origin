package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NationalOpenLimitSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    //----------------------------------------------------start 过渡方案代码-------------------------------------------------------------------------
    /**
     * 5.3	证件号码查验（idCheck）
     * 通过此接口查询同一个客户证件号码下目前全国已开户的号码数量，平台向省公司返回查询结果。
     * @return
     * @throws Exception
     */
    public IDataset idCheck(IData param) throws Exception
    {
        NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.idCheck(param);
    }

    /**
     * 全网用户数据查询 手机号码查询接口
     * 
     * @author zhaohj3
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryCustNumber(IData param) throws Exception
    {
        NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
        IDataset custNumbers =bean.queryCustNumber(param);
        //复原原接口返回值,防止原调用该接口使用报错
        if (custNumbers==null){
            return new DatasetList();
        }
        return custNumbers;
    }

    /**
     * 全网用户数据查询 手机号码查询接口 新增查询阈值控制
     * 兼容旧接口:返回null为无返回数据且查询已达预禁值
     * @author liangdg3
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCustNumberNew(IData param) throws Exception
    {
        NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
        IDataset custNumbers = bean.queryCustNumber(param);
        IData result=new DataMap();
        if(custNumbers==null){
            result.put("VISIT_ALARM",true);
        }else if(custNumbers.size()==0){
            result.put("VISIT_ALARM",false);
        }else{
            result.put("VISIT_ALARM",custNumbers.first().getBoolean("VISIT_ALARM"));
            result.put("CUST_NUMBERS",custNumbers);
        }
        return result;
    }

	  /**
	   * 一证五号违规信息查询
	   * 对当天办理业务客户，在全网用户数据查询平台查询一证五号信息，对超过一证五号客户，展示其业务内容及数量。
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public  IDataset queryViolationNum(IData param) throws Exception{
		 NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		 return bean.queryViolationNum(param);
	 }
	 
	  /**
	   * 查询证件号码的预占情况
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public  IDataset queryCampOnList(IData param) throws Exception{
		 NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		 return bean.queryCampOnList(param);
	 }
	 
	  /**
	   * 解除预占
	   * @param param
	   * @return
	   * @throws Exception
	   */
	 public  IDataset releaseCampOn(IData param) throws Exception{
		 NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		 return bean.releaseCampOn(param);
	 }
    //----------------------------------------------------end 以上是过渡方案代码-------------------------------------------------------------------------

    public IDataset idCampOn(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.idCampOn(param);
    }

    public IDataset userInfoSyn(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.userInfoSyn(param);
    }

    public IDataset getChannelId(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.getChannelId(param);
    }

/*    public IDataset checkPspt(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.checkPspt(param);
    }
*/
    public IDataset isIgnoreCall(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        boolean flag = bean.isIgnoreCall();
        IDataset ds = new DatasetList();
        IData data = new DataMap();
        data.put("isIgnoreCall", flag);
        ds.add(data);
        return ds;
    }

    public IDataset idCheckAndCampOn(IData param) throws Exception
    {
        NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        return bean.idCheckAndCampOn(param);        
    }

    public IData queryCustNumberNewForMod(IData param) throws Exception
    {
        return null;
    }
}
