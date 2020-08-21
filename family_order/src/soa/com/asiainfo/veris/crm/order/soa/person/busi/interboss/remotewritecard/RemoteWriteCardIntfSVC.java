
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotewritecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemoteWriteCardIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 2394740971982801892L;

    public IDataset changeCardReg(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        return bean.changeCardReg(input);
    }

    public IDataset getSpeSimInfo(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        return bean.getSpeSimInfo(input);
    }

    public IDataset getSpeSimInfoForAir(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        return bean.getSpeSimInfoForAir(input);
    }

    public IDataset iboperTrans(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        IData data = bean.iboperTrans(input);
        IDataset set = new DatasetList();
        set.add(data);
        return set;
    }

    public void setTrans(IData input) throws Exception
    {
        if (StringUtils.isEmpty(input.getString("SERIAL_NUMBER")))
        {
            if (StringUtils.isNotEmpty(input.getString("PHONENUM")))
            {
                input.put("SERIAL_NUMBER", input.getString("PHONENUM"));
            }
        }
    }

    public IDataset updRmtWrtSimCard(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        return bean.updRmtWrtSimCard(input);
    }

    public IDataset writeCardExistVerify(IData input) throws Exception
    {
        RemoteWriteCardIntfBean bean = (RemoteWriteCardIntfBean) BeanManager.createBean(RemoteWriteCardIntfBean.class);
        return bean.writeCardExistVerify(input);
    }
}
