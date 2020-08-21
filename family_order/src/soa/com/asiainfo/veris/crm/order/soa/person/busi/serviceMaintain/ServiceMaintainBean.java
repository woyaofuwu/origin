package com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

import java.util.Date;

public class ServiceMaintainBean extends CSBizBean {

    public static IDataset queryAvailableServices() throws Exception{
        IData idata = new DataMap();
        // 取type_id 为：SERVICE_OLCOM 的记录
        idata.put("TYPE_ID", "SERVICE_OLCOM");
        IDataset services = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", idata, Route.CONN_CRM_CEN);
        return services;
    }

    // 批量导入基础功能服务信息数据
    public IData importServiceInfo(IData param) throws Exception {
        IData result = checkImportFile(param);
        IDataset successes = result.getDataset("SUCCESS");
        if (successes.size() > 0) {
            // 批量新增入库到接口表中
            importDealAdd(successes);
        }
        return result;
    }
    // 检查导入文件数据
    private IData checkImportFile(IData params) throws Exception {
        // 定义检查的结果
        IDataset succds = new DatasetList();
        IDataset faileds = new DatasetList();
        // 获取导入的文件数据
        IData fileData = params.getData("fileData");
        // 获取操作类型
        String operate =  fileData.getString("operate");
        // 获取操作的基础服务
        String baseServiceID = fileData.getString("baseServiceID");
        String baseServiceName = fileData.getString("baseServiceName");
        // 获取导入文件中正确的记录,并开始遍历Excel中的每条正确记录
        IDataset[] datasets = (IDataset[]) fileData.get("right");

        for (int i = 0; i < datasets.length; i++) {
            IDataset dataset = datasets[i];
            // 为空提示:导入数据不能为空！
            if (IDataUtil.isEmpty(dataset)) {
                CSAppException.apperr(BatException.CRM_BAT_20);
            }
            // 提示:导入条数不能超过1000条，导入失败！
            if (dataset.size() > 1000) {
                CSAppException.apperr(BatException.CRM_BAT_21, "1000");
            }
            // 遍历每一条数据
            for (int j = 0; j < dataset.size(); j++) {
                IData data = dataset.getData(j);
                String phoneNumber = data.getString("SERIAL_NUMBER","");
                // 根据服务号码,查找用户表,是否是有效用户.  如果不是就加入到faileds中.并说明错误原因
                data.put("phoneNumber",phoneNumber);
                data.put("baseServiceID", baseServiceID);
                data.put("operate", operate);
                data.put("baseServiceName", baseServiceName);
                succds.add(data);
            }
        }
        IData result = new DataMap();
        result.put("SUCCESS", succds);
        result.put("FAILED", faileds);
        return result;
    }
    // 导入数据入接口表
    private void importDealAdd(IDataset dataset) throws Exception {
        // 待新增的数据
        IDataset addParams = new DatasetList();
        // 待更新的数据
        IDataset updateParams = new DatasetList();
        // 遍历 检查有效的待插入的数据.
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            String phoneNumber = data.getString("phoneNumber");
            String baseServiceID = data.getString("baseServiceID");
            String baseServiceName = data.getString("baseServiceName");
            String operate = data.getString("operate");

            IData addParam = new DataMap();
            addParam.put("SERIAL_NUMBER",phoneNumber);
            addParam.put("STATE",'0');
            addParam.put("IN_DATE", SysDateMgr.getSysTime());
            addParam.put("OPERATE_TYPE",operate);
            addParam.put("BASE_SERVICE","");
            addParam.put("BASE_SERVICE_ID",baseServiceID);
            addParam.put("BASE_SERVICE",baseServiceName);

            addParams.add(addParam);

            // 查询该手机号是否已存在接口表中.
//            boolean isNewRecord = queryCountBySN(phoneNumber);
           /* if (isNewRecord){
                // 构建插入数据库的数据
                IData addParam = new DataMap();
                addParam.put("SERIAL_NUMBER",phoneNumber);
                addParam.put("STATE",'0');
                addParam.put("IN_DATE", SysDateMgr.getSysTime());
                addParam.put("OPERATE_TYPE",operate);
                addParam.put("BASE_SERVICE","");
                addParam.put("BASE_SERVICE_ID",baseServiceID);
                addParam.put("BASE_SERVICE",baseServiceName);

                addParams.add(addParam);
            }else {//如已存在，更新接口表中该服务号码的数据。
                IData updateParam = new DataMap();
                updateParam.put("SERIAL_NUMBER",phoneNumber);
                updateParam.put("STATE",'0');
                updateParam.put("IN_DATE", SysDateMgr.getSysTime());
                updateParam.put("FINISH_DATE","");
                updateParam.put("BASE_SERVICE_ID",baseServiceID);
                updateParam.put("BASE_SERVICE",baseServiceName);
                updateParam.put("OPERATE_TYPE",operate);
                updateParam.put("ORDER_ID","");
                updateParam.put("REMARK","");
                int result = Dao.executeUpdateByCodeCode("TI_B_SERVICE_OLCOM", "UPD_BY_SERIAL_NUMBER",updateParam,Route.getCrmDefaultDb());
            }*/
        }
        if (IDataUtil.isNotEmpty(addParams)) { // 接口表新增
            Dao.insert("TI_B_SERVICE_OLCOM", addParams, Route.getCrmDefaultDb());
        }
    }
    // 根据服务号码查询接口表
    private boolean queryCountBySN(String phoneNumber) throws Exception{
        IData data = new DataMap();
        data.put("SERIAL_NUMBER",phoneNumber);
        IDataset iDataset = Dao.qryByCode("TI_B_SERVICE_OLCOM","QRY_COUNT_BY_SERIAL_NUMBER",data,Route.getCrmDefaultDb());
        IData iData = iDataset.getData(0);
        if (null != iData.getString("COUNT") && "0".equals(iData.getString("COUNT"))){
            return true;
        }else {
            return false;
        }
    }

    // 查询接口表数据
    public IDataset queryBaseServiceInfo(IData param, Pagination pagination) throws Exception{
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select t.SERIAL_NUMBER, t.STATE, t.IN_DATE, t.FINISH_DATE, t.OPERATE_TYPE, t.BASE_SERVICE, t.BASE_SERVICE_ID, t.ORDER_ID, t.REMARK " +
                "from UCR_CRM1.TI_B_SERVICE_OLCOM t where 1=1 ");
        // 如果服务号码不为空，检索条件为手机号。
        if (!param.getString("phoneNumber").isEmpty()){
            param.put("SERIAL_NUMBER",param.getString("phoneNumber"));
            parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        }
        parser.addSQL(" ORDER BY IN_DATE desc");
        IDataset dataset = Dao.qryByParse(parser,pagination);
        return dataset ;

    }
    // 获取接口表待处理的数据
    public static IDataset getPendingData(IData param) throws Exception{
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select t.SERIAL_NUMBER, t.STATE, t.IN_DATE, t.FINISH_DATE, t.OPERATE_TYPE, t.BASE_SERVICE, t.BASE_SERVICE_ID " +
                "from UCR_CRM1.TI_B_SERVICE_OLCOM t where 1=1 and t.STATE='0' and rownum=1 ");
//        parser.addSQL(" ORDER BY IN_DATE desc");
        IDataset dataset = Dao.qryByParse(parser);
        return dataset ;

    }
}