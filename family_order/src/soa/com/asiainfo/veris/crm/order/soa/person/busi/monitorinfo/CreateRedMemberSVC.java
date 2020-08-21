
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CreateRedMemberSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询品牌名称和产品名称,调产商品接口
     * @param input
     * @return
     * @throws Exception
     * add by duhj 
     * 2017/03/04
     */
    public IData getUserName(IData input) throws Exception
    {
    	IData  resuData=new DataMap();
        String productName = UProductInfoQry.getProductNameByProductId(input.getString("PRODUCT_ID"));
        String brandName=UBrandInfoQry.getBrandNameByBrandCode(input.getString("BRAND_CODE"));
        resuData.put("PRODUCT_NAME", productName);
        resuData.put("BRAND_NAME", brandName);


        return resuData;
    }
    
    public IDataset checkUserInfo(IData input) throws Exception
    {
        // 5:短信服务
        IDataset dataset = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(input.getString("USER_ID"), "5");
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_184, "关闭");
        }

        return dataset;
    }

    public IDataset createRedMember(IData input) throws Exception
    {
        CreateRedMemberBean bean = (CreateRedMemberBean) BeanManager.createBean(CreateRedMemberBean.class);

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER").trim());
        data.put("USER_ID", input.getString("USER_ID").trim());
        data.put("START_TIME", SysDateMgr.getSysTime());
        data.put("END_TIME", input.getString("END_DATE"));
        data.put("REMARK", input.getString("REMARK"));

        IDataset ret = new DatasetList(data);
        IDataset redData = UserBlackInfoQry.getRedmemberUserdata(input.getString("SERIAL_NUMBER").trim());

        if (IDataUtil.isEmpty(redData))
        {
            bean.createRedMember(ret);
        }
        else
        {
            if (SysDateMgr.END_TIME_FOREVER.equals(input.getString("END_DATE")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_233);
            }
            else
            {
                bean.delRedMember(input.getString("SERIAL_NUMBER"), input.getString("END_DATE"));
            }

        }

        IDataset blackUserInfo = UserBlackInfoQry.getBlackUserdata(input.getString("USER_ID").trim());
        if (IDataUtil.isNotEmpty(blackUserInfo))
        {
            IData inParam = new DataMap();
            inParam.put("USER_ID", input.getString("USER_ID").trim());
            inParam.put("PROCESS_TAG", "1");
            bean.updateExitBlackUser(inParam);
            inParam.clear();
            inParam.put("USER_ID", input.getString("USER_ID").trim());
            inParam.put("PROCESS_TAG", "2");
            inParam.put("SERIAL_NUMBER", "86" + input.getString("SERIAL_NUMBER").trim());
            bean.InsertBlackUser(inParam);
        }

        IData retValue = new DataMap();
        retValue.put("F", "添加成功！");
        ret.add(retValue);
        return ret;
    }

}
