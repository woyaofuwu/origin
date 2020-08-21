
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimsiinfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.FtpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用户IMSI信息查询的导出 作者：GongGuang
 */
public class ExportQueryUserImsiInfo extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        IDataset res = genImsiInfo(data, false, paramPagination);
        return res;
    }

    /**
     * 连接FTP服务器读取数据文件，获取IMSI结果集
     * 
     * @return
     * @throws Exception
     */
    private IDataset genImsiInfo(IData inparam, boolean isExport, Pagination pg) throws Exception
    {
        // 从数据库中读取ftp配置参数
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset results = CSAppCall.call("SS.QueryUserImsiInfoSVC.getImsiFtpParams", inparam);

        // 根据配置参数连接ftp服务器
        IData ftpParams = results.getData(0);
        String hostIp = ftpParams.getString("PARA_CODE1");
        String port = ftpParams.getString("PARA_CODE2");
        String userName = ftpParams.getString("PARA_CODE3");
        String password = ftpParams.getString("PARA_CODE4");
        String fileName = ftpParams.getString("PARA_CODE10") + inparam.getString("QUERY_MODE");
        String hostFileName = ftpParams.getString("PARA_CODE5") + fileName;

        FtpUtil ftpUtil = new FtpUtil(hostIp, userName, password);
        ftpUtil.setFileType(FtpUtil.FILE_TYPE_BINARY);
        InputStream in = ftpUtil.getFileStream(hostFileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        // 读取ftp文件，并将数据转换成IDataset
        IDataset imsiResult = new DatasetList();
        String str = null;
        while ((str = br.readLine()) != null)
        {
            IData data = new DataMap();
            data.put("IMSI", str);
            imsiResult.add(data);
        }
        br.close();
        ftpUtil.closeServer();
        // 排序结果集
        // imsiResult.sort("IMSI", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND); need included

        // imsiResult = imsiResult.subList((pg.getCurrPage()-1)*pg.getSize(), pg.getCurrPage()*pg.getSize());
        IDataset tempResult = new DatasetList();

        for (int i = (pg.getCurrent() - 1) * pg.getCurrentSize(); i < pg.getCurrent() * pg.getCurrentSize(); i++)
        {
            tempResult.add(imsiResult.getData(i));
        }
        if (!isExport)
        {
            // 分页用
            // tempResult.setCount(imsiResult.size()); need included

            return tempResult;
        }
        else
        {
            return imsiResult;
        }
    }
}
