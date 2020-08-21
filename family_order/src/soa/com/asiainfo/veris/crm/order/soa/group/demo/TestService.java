
package com.asiainfo.veris.crm.order.soa.group.demo;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class TestService extends BizProcess
{
    private static final Logger logger = Logger.getLogger(TestService.class);

    private static final long serialVersionUID = 1L;

    private static final String URL_LOCAL = "http://localhost:8080/order/service";

    //private static final String URL = "http://135.191.71.35:10000/service";

    //private static final String URL_4A = "http://10.200.174.170:10000/service";

    /**
     * 运行输入[文件路径 + 文件名] 右键 Run As——>Open Run Dialog
     * 
     * @param args
     * @throws Exception
     */
    // 
    private static String faceName = "CS.TcsGrpIntfSVC.dealBbossGroupBiz";
    //"CS.TcsGrpIntfSVC.dealBbossGroupBiz";
    //CS.TcsGrpIntfSVC.synBBossGrpMgrBiz
    //CS.TcsGrpIntfSVC.dealBbossMemBiz
    
    //CS.TcsGrpIntfSVC.dealBbossMemBiz
    public static void main(String[] args) throws Exception
    {
        IData svcData = new DataMap();

        svcData.put("HEADER", loadDataFromFile("F:/ServiceFile/HEADER.txt", false));
        svcData.put("BODY", loadDataFromFile("F:/ServiceFile/"+faceName+".txt", true));

        TestService process = new TestService();
        process.setRouteId(svcData.getString("STAFF_EPARCHY_CODE"));
        process.setGroup(svcData.getString("SUBSYS_CODE"));
        process.start(svcData);

    }

    public static IData loadDataFromFile(String filePath, boolean needMapTrans) throws Exception
    {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        StringBuilder buffer = new StringBuilder(100000);
        byte[] bytes = new byte[1024];
        int fileLength = 0;

        while ((fileLength = fis.read(bytes)) != -1)
        {
            buffer.append(new String(bytes, 0, fileLength));
        }
        if (needMapTrans)
        {
            String data = new String(buffer);
            Map map = Wade3DataTran.strToMap(data.trim());
            
            return Wade3DataTran.wade3To4DataMap(map);
        }
        else 
        {
            return new DataMap(buffer.toString().trim());
        }
    }

    public void run() throws Exception
    {

        IData headerData = getInput().getData("HEADER");
        IData bodyData = getInput().getData("BODY");

        BizVisit bizVisit = CSBizBean.getVisit();

        bizVisit.setStaffEparchyCode(headerData.getString("STAFF_EPARCHY_CODE"));
        bizVisit.setLoginEparchyCode(headerData.getString("LOGIN_EPARCHY_CODE"));

        bizVisit.setStaffId(headerData.getString("STAFF_ID"));
        bizVisit.setDepartId(headerData.getString("DEPART_ID"));
        bizVisit.setInModeCode(headerData.getString("IN_MODE_CODE"));
        bizVisit.setCityCode(headerData.getString("CITY_CODE"));

        String xTransCode = faceName;

        IDataInput dataInput = new DataInput();
        dataInput.getHead().putAll(headerData);
        dataInput.getData().putAll(bodyData);

        // 调用远程
        IDataset dataset = CSAppCall.call(URL_LOCAL, xTransCode, dataInput, false);

    }

}
