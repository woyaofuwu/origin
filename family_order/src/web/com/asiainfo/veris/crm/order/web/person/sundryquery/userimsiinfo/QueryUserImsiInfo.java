
package com.asiainfo.veris.crm.order.web.person.sundryquery.userimsiinfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.FtpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户IMSI信息查询 作者：GongGuang
 */
public abstract class QueryUserImsiInfo extends PersonBasePage
{

    /**
     * 连接FTP服务器读取数据文件，获取IMSI结果集
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
    private IDataset genImsiInfo(IRequestCycle cycle, boolean isExport) throws Exception
    {
        IData inparam = getData("cond", true);
        // 从数据库中读取ftp配置参数
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUserImsiInfoSVC.getImsiFtpParams", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "获取FTP配置参数失败！";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setCount(dataCount.getDataCount());

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

        Pagination pg = getPagination("navt"); // pd.getPagination();
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

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        data.put("cond_QUERY_MODE", "0");
        this.setCondition(data);
        IDataset qryModes = new DatasetList();
        IData gprs = new DataMap();
        gprs.put("DATA_ID", "0");
        gprs.put("DATA_NAME", "GPRS用户");
        qryModes.add(gprs);
        IData vpn = new DataMap();
        vpn.put("DATA_ID", "1");
        vpn.put("DATA_NAME", "VPN用户");
        qryModes.add(vpn);
        IData gjct = new DataMap();
        gjct.put("DATA_ID", "2");
        gjct.put("DATA_NAME", "国际及港澳台长途用户");
        qryModes.add(gjct);
        IData gjmy = new DataMap();
        gjmy.put("DATA_ID", "3");
        gjmy.put("DATA_NAME", "国际及港澳台漫游用户");
        qryModes.add(gjmy);
        IData sqtj = new DataMap();
        sqtj.put("DATA_ID", "4");
        sqtj.put("DATA_NAME", "申请停机用户");
        qryModes.add(sqtj);
        IData sxtj = new DataMap();
        sxtj.put("DATA_ID", "5");
        sxtj.put("DATA_NAME", "双向停机用户");
        qryModes.add(sxtj);
        this.setQryModes(qryModes);

    }

    /**
     * 功能：用户IMSI信息查询
     */
    public void queryUserImsiInfo(IRequestCycle cycle) throws Exception
    {
        IDataset infos = genImsiInfo(cycle, false);
        this.setInfos(infos);
        setCondition(getData("cond", true));
        this.init(cycle);
    }

    public abstract void setAllImsi(IDataset allImsi);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setQryModes(IDataset qryModes);
}
