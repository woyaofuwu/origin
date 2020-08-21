
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WriteCardSVC extends CSBizService
{

    /**
     * SIM卡再利用,校验SIM卡、白卡。
     * 
     * @param input
     * @return
     * @throws Exception
     *             public IDataset afterCheckSimcard(IData input) throws Exception { WriteCardBean bean =
     *             BeanManager.createBean(WriteCardBean.class); IDataset returnSet = bean.afterCheckSimcard(input);
     *             return returnSet; }
     */

    /**
     * sim再利用,格式化白卡资料表、获取SIM卡个性化资料和拼串
     * 
     * @param input
     * @return
     * @throws Exception
     *             public IDataset afterFomatSimcard(IData input) throws Exception { WriteCardBean bean =
     *             BeanManager.createBean(WriteCardBean.class); IDataset returnSet = bean.afterFomatSimcard(input);
     *             return returnSet; }
     */

    /**
     * sim再利用，根据写卡结果更新白卡、SIM卡资料表，释放资料。
     * 
     * @param input
     * @return
     * @throws Exception
     *             public IDataset afterReuseSimcard(IData input) throws Exception { WriteCardBean bean =
     *             BeanManager.createBean(WriteCardBean.class); boolean result = bean.afterReuseSimcard(input); IDataset
     *             returnSet = new DatasetList(); IData output = new DataMap(); output.put("RESULT_CODE", "0");
     *             output.put("RESULT_INFO", "OK"); returnSet.add(output); return returnSet; }
     */

    /**
     * 写卡后处理, 根据写卡返回值更新白卡及SIM个性化资料状态
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset afterWriteCard(IData input) throws Exception
    {
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        IDataset returnSet = new DatasetList();
        String phoneNum = input.getString("PHONE_NUM", "1");

        if ("1".equals(phoneNum))
        {
            returnSet = bean.afterWriteCard(input);
        }
        else if ("2".equals(phoneNum))
        {
            returnSet = bean.afterWriteOCNCard(input);
        }

        return returnSet;
    }

    /**
     * 写卡前操作, 校验控件版本、白卡、取SIM卡个性化资料和拼串
     * 
     * @author zhangxing
     * @param IRequestCycle
     * @throws Exception
     */
    public IDataset beforeWriteCard(IData input) throws Exception
    {
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        IDataset returnSet = new DatasetList();
        String phoneNum = input.getString("PHONE_NUM", "1");
        if ("1".equals(phoneNum))
        {
            returnSet = bean.beforeWriteCard(input);
        }
        else if ("2".equals(phoneNum))
        {
            returnSet = bean.beforeWriteOCNCard(input);
        }
        return returnSet;
    }

    public IDataset checkWriteCard(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        returnSet.add(bean.checkWriteCard(input));
        return returnSet;
    }

    public IDataset getOcxVersion(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        WriteCardBean bean = (WriteCardBean) BeanManager.createBean(WriteCardBean.class);
        IData data = bean.getOcxVersion(input);
        returnSet.add(data);
        return returnSet;
    }

    /**
     * 获取远程写卡参数获取
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRemoteWriteCardUrl(IData input) throws Exception
    {
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        IDataset returnSet = bean.getRemoteWriteCardUrl(input);
        return returnSet;
    }

    /*
     * public IDataset getNewCardInfo(IData input) throws Exception { IDataset returnSet = new DatasetList();
     * WriteCardBean bean = BeanManager.createBean(WriteCardBean.class); returnSet.add(bean.getNewCardInfo(input));
     * return returnSet; }
     */

    public IDataset getSpeSimInfo(IData input) throws Exception
    {
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String occupyTimeCode = "0";
        // String remoteMode = input.getString("REMOTE_MODE");
        IDataset returnSet = bean.getSpeSimInfo(serialNumber, emptyCardId, occupyTimeCode, "2");
        return returnSet;

    }

}
