
package com.asiainfo.veris.crm.order.soa.person.busi.speservice;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.ScoreExchangeBean;

public class NewSvcRecomdParaSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(NewSvcRecomdParaSVC.class);

    private static final long serialVersionUID = 1L;

    public void dealData(IDataset input, String type) throws Exception
    {//type 优惠标注1,服务标注2
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        String element_id;// 元素ID
        String element_name;// 元素名称
        String recomm_content;// 元素推荐用语
        String tag;// 增：0、删：1、改：2表示

        if (IDataUtil.isNotEmpty(input))
        {
            for (int i = 0; i < input.size(); i++)
            {

                IData data = input.getData(i);

                if (IDataUtil.isNotEmpty(data))
                {
                    if (type.equals("1"))
                    {
                        element_id = data.getString("DISCNT_ID");
                        element_name = data.getString("DISCNT_NAME");
                        recomm_content = data.getString("DISCNT_CONTENT");
                        tag = data.getString("tag");
                        if (!"1".equals(tag))
                        {
                            tag = dealTag(type, element_id);
                        }

                    }
                    else
                    {
                        element_id = data.getString("SERVICE_ID");
                        element_name = data.getString("SERVICE_NAME");
                        recomm_content = data.getString("SERVICE_CONTENT");
                        tag = data.getString("tag");
                        if (!"1".equals(tag))
                        {
                            tag = dealTag(type, element_id);
                        }
                    }

                    if (tag.equals("1"))// 用语终止
                    {
                        bean.delRecommInfo(type, element_id);
                    }
                    else if (tag.equals("2"))// 修改用语
                    {
                        bean.upRecommInfo(type, element_id, recomm_content);
                    }
                    else if (tag.equals("0"))// 新增用语
                    {
                        bean.insertRecommInfo(type, element_id, element_name, recomm_content);
                    }
                }

            }
        }
    }

    public String dealTag(String type, String element_id) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        String flag = "2";
        IData param = new DataMap();
        param.put("RECOMM_TYPE", type);
        param.put("ELEMENT_ID", element_id);
        param.put("RECOMM_SOURCE", "2");// 来源 手工
        IDataset recommInfo = bean.getUserRecommPara(param);
        if (IDataUtil.isEmpty(recommInfo))
        {
            flag = "0";
        }
        return flag;
    }

    public IDataset getAllBrandInfo(IData input) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        IDataset results = bean.getAllBrandInfo(input);

        return results;
    }

    public IDataset getProductByBrand(IData input) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        IDataset results = bean.getProductByBrand(input);

        return results;
    }

    public IDataset getRecommparaDiscnt(IData input) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        IDataset results = bean.getUserRecommparaDiscnt(input);

        return results;
    }

    public IDataset getRecommparaService(IData input) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        IDataset results = bean.getUserRecommparaService(input);

        return results;
    }

    public IDataset updateRecommData(IData input) throws Exception
    {
        IDataset discntLists = new DatasetList(input.getString("discntData"));
        IDataset serviceLists = new DatasetList(input.getString("serviceData"));

        dealData(discntLists, "1");
        dealData(serviceLists, "2");
        IDataset ret = new DatasetList();
        IData  res=new DataMap();
        res.put("issuccess", "ok");
        ret.add(res);
        return ret;
    }

    
    public IData getRecommparaDiscntAndService(IData input) throws Exception
    {
        NewSvcRecomdParaBean bean = (NewSvcRecomdParaBean) BeanManager.createBean(NewSvcRecomdParaBean.class);
        return bean.getUserRecommparaServiceAndDiscntCode(input);
    }
}
