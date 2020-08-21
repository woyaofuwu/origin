
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.discntmodifyhisinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDiscntModifyHisInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 查询业务区
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryArea(IData param) throws Exception
    {
        boolean fltByStaffId = true;
        QueryDiscntModifyHisInfoBean bean = BeanManager.createBean(QueryDiscntModifyHisInfoBean.class);
        IDataset areas = bean.queryArea();
        String cityId = this.getVisit().getCityCode();
        String staffId = this.getVisit().getStaffId();
        // 过滤掉：一级BOSS业务区(HNIB)、门户网站接口(HWEB)、多媒体自助接口(MEDA)、短信接口(MESG)、客服系统接口(SERV)
        Map filterMap = new HashMap();
        filterMap.put("HNIB", "一级BOSS业务区");
        filterMap.put("HWEB", "门户网站接口");
        filterMap.put("MEDA", "多媒体自助接口");
        filterMap.put("MESG", "短信接口");
        filterMap.put("SERV", "客服系统接口");
        IDataset temp = new DatasetList();
        for (Iterator it = areas.iterator(); it.hasNext();)
        {
            IData dt = (IData) it.next();
            if (!filterMap.containsKey(dt.get("AREA_CODE")))
            {
                String areaCode = dt.getString("AREA_CODE", "").toUpperCase();
                // 根据工号限制业务区
                if (fltByStaffId)
                {
                    // 只有省局的账号可以查看所有业务区的数据
                    if (cityId.equals("HNSJ") || staffId.startsWith("HNSJ"))
                    {
                        temp.add(dt);
                        continue;
                    }
                    // 不是省局的账号只能查看所属业务区的数据
                    if (cityId.equals(areaCode) || staffId.startsWith(areaCode))
                    {
                        temp.add(dt);
                        continue;
                    }
                }
                else
                {// 不用限制业务区
                    temp.add(dt);
                }

            }
        }

        return temp;
    }

    /**
     * 功能：优惠变更历史查询 作者：GongGuang
     */
    public IDataset queryDiscntModifyHisInfo(IData data) throws Exception
    {
        QueryDiscntModifyHisInfoBean bean = (QueryDiscntModifyHisInfoBean) BeanManager.createBean(QueryDiscntModifyHisInfoBean.class);
        return bean.queryDiscntModifyHisInfo(data, getPagination());
    }
}
