
package com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.ServiceMaintainBean;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.order.requestdata.ServiceMaintainReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildServiceMaintainReqData.java
 * @Description:
 * @version: v1.0.0
 * @author: chencn
 * @date: 2019-8-29 下午4:05:39
 */

public class BuildServiceMaintainReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        // TODO Auto-generated method stub
        ServiceMaintainReqData serviceMaintainReqData = (ServiceMaintainReqData)brd;
        // 封装业务请求对象
//        serviceMaintainReqData.setState(param.getString("STATE",""));
        serviceMaintainReqData.setOperateType(param.getString("OPERATE_TYPE",""));
        serviceMaintainReqData.setBaseServiceID(param.getString("BASE_SERVICE_ID",""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ServiceMaintainReqData();
    }

    // 重写获取用户资料
    /*@Override
    public UcaData buildUcaData(IData param) throws Exception {
        // 设置三户资料对象
        String sn = "";
        // 从接口表中读取一条状态为：0 的记录。
        ServiceMaintainBean bean = BeanManager.createBean(ServiceMaintainBean.class);
        IDataset iDataset = ServiceMaintainBean.getPendingData(param);
        if (null != iDataset && iDataset.size()>0){// 有待处理的服务同步
            IData data = iDataset.getData(0);
            sn = data.getString("SERIAL_NUMBER");
            param.put("SERIAL_NUMBER",data.getString("SERIAL_NUMBER"));
            param.put("STATE",data.getString("STATE"));
            param.put("OPERATE_TYPE",data.getString("OPERATE_TYPE"));
            param.put("BASE_SERVICE_ID",data.getString("BASE_SERVICE_ID"));

        }else {
            sn = param.getString("SERIAL_NUMBER");
        }
        UcaData uca = DataBusManager.getDataBus().getUca(sn);
        if (uca == null)
        {
            uca = UcaDataFactory.getNormalUca(sn);
        }

        if("1".equals(param.getString("PRE_TYPE",""))){
            uca = UcaDataFactory.getNormalUca(sn,true);
        }
        return uca;
    }*/

}
