
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.auth;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;

public class GetInfosSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获取鉴权用户[宽带用户使用]
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAuthBroadbandUser(IData input) throws Exception
    {
        return getInfos(input);
    }

    /**
     * 加载鉴权的三户资料[宽带用户使用]
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBroadbandUCAInfos(IData input) throws Exception
    {
        return getUCAInfos(input);
    }

    /**
     * 获取三户查询业务类型信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getInfos(IData input) throws Exception
    {
        IData data = new DataMap();

        TradeInfoBean infoBean = BeanManager.createBean(TradeInfoBean.class);

        // 获取业务类型资料
        IData tradePara = infoBean.getTradeType(input);

        input.putAll(tradePara);
        IData userInfo = infoBean.getAuthUser(input);

        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

        data.put("USER_INFO", userInfo);
        data.put("TRADE_TYPE_INFO", tradePara);

        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    /**
     * 加载鉴权的三户资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUCAInfos(IData input) throws Exception
    {
        TradeInfoBean infoBean = BeanManager.createBean(TradeInfoBean.class);
        IData ucainfo = infoBean.getTradeUCAInfo(input);

        IDataset dataset = new DatasetList();
        dataset.add(ucainfo);
        return dataset;
    }
    
    /**
     * @chenxy3@REQ201501130023 关于密码修改身份证件校验的需求
     * 2015-1-20
     * 要求配置在COMMPARA表中的6890的业务要单独鉴权，即每次点查询都要认证
     * */
    public IDataset getCommparaSVC(IData inparams) throws Exception
    {	
    	String tradeTypecode=inparams.getString("TRADE_TYPE_CODE");
    	//通过公用参数表获取参数值。
    	IDataset ids=CommparaInfoQry.getCommparaAllColByParser("CSM", "6890",tradeTypecode, "0898");
		return ids;
    }
    
    /**
     * @chenxy3@REQ201707100010 安全检查渗透问题修改 
     * COMMPARA表中的6899的para_code1=0则不拦截，只输出日志
     * CS.GetInfosSVC.getCommCommparaSVC
     * */
    public IDataset getCommCommparaSVC(IData inparams) throws Exception
    {	 
    	String paramAttr=inparams.getString("PARAM_ATTR","6899");
    	//通过公用参数表获取参数值。
    	IDataset ids=CommparaInfoQry.getCommByParaAttr("CSM", paramAttr,"0898");
		return ids;
    }
    
    public IDataset getTradeCommparaSVC(IData inparams) throws Exception
    {	 
    	String tradeTypecode=inparams.getString("TRADE_TYPE_CODE");
    	//通过公用参数表获取参数值。
    	IDataset ids=CommparaInfoQry.getCommparaAllColByParser("CSM", "6999",tradeTypecode, "0898");
		return ids;
    }
}
