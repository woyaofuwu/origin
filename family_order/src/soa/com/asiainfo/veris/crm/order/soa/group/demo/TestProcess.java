
package com.asiainfo.veris.crm.order.soa.group.demo;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestProcess extends BizProcess
{
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

        return new DataMap(buffer.toString());
    }

    /**
     * 运行输入[文件路径 + 文件名] 右键 Run As——>Open Run Dialog
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        IData svcData = loadDataFromFile(args[0]);

        TestProcess process = new TestProcess();
        process.setRouteId(svcData.getString("STAFF_EPARCHY_CODE"));
        process.setGroup(svcData.getString("SUBSYS_CODE"));
        process.start(svcData);
    }

    private Logger log = Logger.getLogger(TestProcess.class);

    public void run() throws Exception
    {
        IDataset dataset = CSAppCall.call(null, getInput().getString("X_TRANS_CODE"), getInput(), false);
    }

}
