
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CreateFixTelUserSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 证件号码校验
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkPsptId(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IData returnData = createFixTelUserBean.checkPsptId(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkSerialNumber(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IData returnData = createFixTelUserBean.checkSerialNumber(input);
        IData data = createFixTelUserBean.createProductInfo(returnData);
        returnData.putAll(data);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkSimCardNo(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IData returnData = createFixTelUserBean.checkSimResource(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 密码校验
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkSimplePassword(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IData returnData = createFixTelUserBean.checkSimplePassword(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset getProductFeeInfo(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IDataset dataset = createFixTelUserBean.getProductFeeInfo(input);
        return dataset;
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     * 
     * @author dengyong3
     * @param input
     * @throws Exception
     */
    public IDataset initProductType(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        IDataset returnData = createFixTelUserBean.initProductType(input);
        return returnData;
    }

    /**
     * 开户数限制校验
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset judgeOpenLimit(IData input) throws Exception
    {
        IData ajaxReturnData = new DataMap();
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        int iCount = createFixTelUserBean.JudgeOpenLimit(input);
        int num = createFixTelUserBean.getQfXhCnt(input);
        if (num > 0)
        {
            ajaxReturnData.put("OPEN_LIMIT_MESSAGE", "当前用户证件下共有在网用户【" + iCount + "】个，有欠费销号用户【" + num + "】个!");
        }
        else
        {
            // 显示此证件的开户数量
            ajaxReturnData.put("OPEN_LIMIT_MESSAGE", "当前用户证件下共有在网用户【" + iCount + "】个！");
        }
        String alertnum = createFixTelUserBean.getAlertNum();
        ajaxReturnData.put("OPEN_NUM", iCount + num);// 包括欠费与正常用户
        ajaxReturnData.put("ALERT_NUM", alertnum);
        IDataset dataset = new DatasetList();
        dataset.add(ajaxReturnData);
        return dataset;
    }

    /**
     * 费用重算
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset mputeFee(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        return createFixTelUserBean.mputeFee(input);
    }

    /**
     * 界面初始化参数
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset onInitTrade(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);

        createFixTelUserBean.IsCanOperate(input);
        IData returnData = createFixTelUserBean.InitPara(input);

        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 释放选占的号码资源
     * 
     * @author dengyong3
     * @param inData
     * @throws Exception
     */
    public void releaseSelectedSNRes(IData input) throws Exception
    {
        CreateFixTelUserBean createFixTelUserBean = BeanManager.createBean(CreateFixTelUserBean.class);
        createFixTelUserBean.releaseSelectedSNRes(input);
    }
}
