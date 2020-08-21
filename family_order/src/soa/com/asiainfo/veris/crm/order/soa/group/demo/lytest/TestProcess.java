
package com.asiainfo.veris.crm.order.soa.group.demo.lytest;

import java.io.File;
import java.io.FileInputStream;

import com.ailk.biz.BizVisit;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class TestProcess extends BizProcess
{
    private static final long serialVersionUID = 1L;

    private static final String URL = "http://10.200.130.83:10000/service";

    private static final String URL_4A = "http://10.200.174.245:10000/service"; // http://10.200.174.170:10000/service

    // http://10.200.174.245:10000/service
    // 10.200.174.244:10000
    // http://10.200.174.148:10001/service

    /**
     * 运行输入[文件路径 + 文件名] 右键 Run As——>Open Run Dialog
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        IData svcData = new DataMap();

        svcData.put("HEADER", loadDataFromFile("D:\\ServiceFile\\HEADER.txt"));
        svcData.put("BODY", loadDataFromFile(args[0]));

        TestProcess process = new TestProcess();
        process.setRouteId(svcData.getString("STAFF_EPARCHY_CODE"));
        process.setGroup(svcData.getString("SUBSYS_CODE"));
        process.start(svcData);

    }

    public static IData loadDataFromFile(String filePath) throws Exception
    {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        StringBuilder buffer = new StringBuilder(1000);
        byte[] bytes = new byte[1024];
        int fileLength = 0;

        while ((fileLength = fis.read(bytes)) != -1)
        {
            buffer.append(new String(bytes, 0, fileLength));
        }

        return new DataMap(buffer.toString().trim());
    }

    public void run() throws Exception
    {
        ////System.out.println("服务调用开始<<<<<<<<<<<<<<<<<<<<<<<");

        IData headerData = getInput().getData("HEADER");
        IData bodyData = getInput().getData("BODY");

        BizVisit bizVisit = CSBizBean.getVisit();

        bizVisit.setStaffEparchyCode(headerData.getString("STAFF_EPARCHY_CODE"));
        bizVisit.setLoginEparchyCode(headerData.getString("LOGIN_EPARCHY_CODE"));

        bizVisit.setStaffId(headerData.getString("STAFF_ID"));
        bizVisit.setDepartId(headerData.getString("DEPART_ID"));
        bizVisit.setInModeCode(headerData.getString("IN_MODE_CODE"));
        bizVisit.setCityCode("A311");

        String xTransCode = bodyData.getString("X_TRANS_CODE");

        IDataInput dataInput = new DataInput();
        dataInput.getHead().putAll(headerData);
        dataInput.getData().putAll(bodyData);

        // IDataOutput dataOutput = ServiceFactory.call(URL_4A, xTransCode, dataInput, null, false, true);
        IDataOutput dataOutput = ServiceFactory.call(null, xTransCode, dataInput, null, false, false);
        ////System.out.println("服务调用结束>>>>>>>>>>>>>>>>>>>>>>>");
        ////System.out.println("返回结果[dataOutput] == " + dataOutput);
        ////System.out.println("返回结果[dataOutputHead] == " + dataOutput.getHead());
        ////System.out.println("返回结果[dataOutput.getData()] == " + dataOutput.getData());
        ////System.out.println("返回结果[dataOutputDataCount] == " + dataOutput.getDataCount());

    }

}
