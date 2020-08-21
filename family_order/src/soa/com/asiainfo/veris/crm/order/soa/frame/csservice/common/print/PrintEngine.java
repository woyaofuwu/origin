
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public final class PrintEngine
{
    /**
     * 解析打印信息、拼打印串
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String parseData(IData data) throws Exception
    {

        // 得到地州模版配置
        IDataset idsTempletItem = PrintItem.getTempletItem(data);

        // 如果没有地州模板，则获取同用模板
        if (idsTempletItem == null || idsTempletItem.size() == 0)
        {
            IData map = new DataMap();
            map.put("TEMPLET_TYPE", data.getString("TEMPLET_TYPE"));
            map.put("TRADE_EPARCHY_CODE", "ZZZZ");
            idsTempletItem = PrintItem.getTempletItem(map);
        }

        // 初始化
        PrintFormat pf = new PrintFormat();

        StringBuilder cmd = new StringBuilder();

        for (int i = 0; i < idsTempletItem.size(); i++)
        {
            IData idata = (IData) idsTempletItem.get(i);

            pf.autowrap = false;
            pf.itype = idata.getString("ITEM_TYPE", "");
            pf.icontent = idata.getString("ITEM_CONTENT", "");
            pf.iwidth = idata.getString("ITEM_WIDTH");
            pf.iheight = idata.getString("ITEM_HEIGHT");
            pf.itop = idata.getString("ITEM_TOP");
            pf.ileft = idata.getString("ITEM_LEFT");
            pf.ialign = idata.getString("ITEM_ALIGNMENT");
            pf.isize = idata.getString("FONT_SIZE");
            pf.ibold = idata.getString("FONT_BOLD");
            pf.icol = idata.getString("FONT_COLOR");
            pf.iunderline = idata.getString("FONT_UNDERLINE");

            if (pf.itype != null)
            {
                if (pf.itype.equalsIgnoreCase("A"))
                {
                    cmd.append("<?SetPaper ");
                    // if (pf.icontent != null) {
                    // cmd.append("name=\"" + pf.icontent + "\", ");
                    // }
                    if (pf.iwidth != null)
                    {
                        cmd.append("width=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iwidth) / pf.rate) + "\" ");
                    }
                    if (pf.iheight != null)
                    {
                        cmd.append("height=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iheight) / pf.rate) + "\" ");
                    }
                    cmd.append("?>");
                    continue;
                }
                else if (pf.itype.equals("0"))
                {
                    cmd.append("<?String value=\"" + pf.icontent + "\"");
                    posifix(cmd, pf);
                    continue;
                }
                else if (pf.itype.equals("1"))
                {
                    String value = data.getString(pf.icontent);
                    if (value == null)
                    {
                        continue;
                    }
                    cmd.append("<?String value=\"" + value + "\"");
                    posifix(cmd, pf);
                    continue;
                }
                else if (pf.itype.equals("2"))
                {
                    String value = data.getString(pf.icontent);
                    if (value == null)
                    {
                        continue;
                    }
                    pf.autowrap = true;
                    String[] datas = value.split("~");

                    for (int j = 0; j < datas.length; j++)
                    {
                        String str = datas[j];
                        if (str.equals(""))
                        {
                            pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                            pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                            continue;
                        }
                        else
                        {
                            cmd.append("<?String value=\"" + str + "\"");
                            posifix(cmd, pf);
                        }
                        pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                        pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                    }
                    continue;
                }
            }
            else
            {
                cmd.append("?>");
                continue;
            }
        }

        // 为了符合java传给js格式添加
        return cmd.toString().replaceAll("\"", "'").replaceAll("=", "&");
    }

    /**
     * 解析打印信息、拼打印串
     * 
     * @param receiptData
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String parseDataForJ(IDataset item, IData receiptData) throws Exception
    {

        if (item == null || item.size() == 0)
        {
            return "";
        }

        // 初始化
        PrintFormat pf = new PrintFormat();

        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.HNAN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI) || ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            pf.rate = 59;
        }

        StringBuilder cmd = new StringBuilder();

        for (int i = 0; i < item.size(); i++)
        {
            IData idata = item.getData(i);

            pf.autowrap = false;
            pf.itype = idata.getString("ITEM_TYPE", "");
            pf.icontent = idata.getString("ITEM_CONTENT", "");
            pf.iwidth = idata.getString("ITEM_WIDTH");
            pf.iheight = idata.getString("ITEM_HEIGHT");
            pf.itop = idata.getString("ITEM_TOP");
            pf.ileft = idata.getString("ITEM_LEFT");
            pf.ialign = idata.getString("ITEM_ALIGNMENT");
            pf.isize = idata.getString("FONT_SIZE");
            pf.ibold = idata.getString("FONT_BOLD");
            pf.icol = idata.getString("FONT_COLOR");
            pf.iunderline = idata.getString("FONT_UNDERLINE");

            if (pf.itype == null)
            {
                cmd.append("?>");
                continue;
            }
            else if (pf.itype.equalsIgnoreCase("A"))
            {
                cmd.append("<?SetPaper ");
                // if (pf.icontent != null) {
                // cmd.append("name=\"" + pf.icontent + "\", ");
                // }
                if (pf.iwidth != null)
                {
                    cmd.append("width=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iwidth) / pf.rate) + "\" ");
                }
                if (pf.iheight != null)
                {
                    cmd.append("height=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iheight) / pf.rate) + "\" ");
                }
                cmd.append("?>");
                continue;
            }
            else if (pf.itype.equals("0"))
            {
                cmd.append("<?String value=\"" + pf.icontent + "\"");
                posifix(cmd, pf);
                continue;
            }
            else if (pf.itype.equals("1"))
            {
                String value = receiptData.getString(pf.icontent);
                if (value == null)
                {
                    continue;
                }
                cmd.append("<?String value=\"" + value + "\"");
                posifix(cmd, pf);
                continue;
            }
            else if (pf.itype.equals("2"))
            {
                String value = receiptData.getString(pf.icontent);
                if (value == null)
                {
                    continue;
                }
                pf.autowrap = true;
                String[] datas = value.split("~");
                for (int j = 0; j < datas.length; j++)
                {
                    String str = datas[j];
                    if (str.equals(""))
                    {
                        pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                        pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                        continue;
                    }
                    else
                    {
                        cmd.append("<?String value=\"" + str + "\"");
                        posifix(cmd, pf);
                    }
                    pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                    pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                }
                continue;
            }
            else if (pf.itype.equals("3"))
            {
                cmd.append("<?Line ");
                posifix(cmd, pf);
                continue;
            }
        }

        // 为了符合java传给js格式添加
        return cmd.toString().replaceAll("\"", "'").replaceAll("=", "&");
    }

    /**
     * 解析打印信息，拼打印串（个人） Sep 6, 200912:01:36 PM
     * 
     * @author xj
     */
    public static String parseDataForPer(IData receiptData, IDataset receiptTempletItems) throws Exception
    {

        if (receiptTempletItems == null || receiptTempletItems.size() == 0)
        {
            return "";
        }

        // 初始化
        PrintFormat pf = new PrintFormat();

        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {

            pf.rate = 59;
        }

        StringBuilder cmd = new StringBuilder();

        for (int i = 0; i < receiptTempletItems.size(); i++)
        {
            IData idata = (IData) receiptTempletItems.get(i);

            pf.autowrap = false;
            pf.itype = idata.getString("ITEM_TYPE", "");
            pf.icontent = idata.getString("ITEM_CONTENT", "");
            pf.iwidth = idata.getString("ITEM_WIDTH");
            pf.iheight = idata.getString("ITEM_HEIGHT");
            pf.itop = idata.getString("ITEM_TOP");
            pf.ileft = idata.getString("ITEM_LEFT");
            pf.ialign = idata.getString("ITEM_ALIGNMENT");
            pf.isize = idata.getString("FONT_SIZE");
            pf.ibold = idata.getString("FONT_BOLD");
            pf.icol = idata.getString("FONT_COLOR");
            pf.iunderline = idata.getString("FONT_UNDERLINE");

            if (pf.itype != null)
            {
                if (pf.itype.equalsIgnoreCase("A"))
                {
                    cmd.append("<?SetPaper ");
                    // if (pf.icontent != null) {
                    // cmd.append("name=\"" + pf.icontent + "\", ");
                    // }
                    if (pf.iwidth != null)
                    {
                        cmd.append("width=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iwidth) / pf.rate) + "\" ");
                    }
                    if (pf.iheight != null)
                    {
                        cmd.append("height=\"" + String.format("%1$2.1f", Double.parseDouble(pf.iheight) / pf.rate) + "\" ");
                    }
                    cmd.append("?>");
                    continue;
                }
                else if (pf.itype.equals("0"))
                {
                    cmd.append("<?String value=\"" + pf.icontent + "\"");
                    posifix(cmd, pf);
                    continue;
                }
                else if (pf.itype.equals("1"))
                {
                    String value = receiptData.getString(pf.icontent);
                    if (value == null)
                    {
                        continue;
                    }
                    cmd.append("<?String value=\"" + value + "\"");
                    posifix(cmd, pf);
                    continue;
                }
                else if (pf.itype.equals("2"))
                {
                    String value = receiptData.getString(pf.icontent);
                    if (value == null)
                    {
                        continue;
                    }
                    pf.autowrap = true;
                    String[] datas = value.split("~");

                    for (int j = 0; j < datas.length; j++)
                    {
                        String str = datas[j];
                        if (str.equals(""))
                        {
                            pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                            pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                            continue;
                        }
                        else
                        {
                            cmd.append("<?String value=\"" + str + "\"");
                            posifix(cmd, pf);
                        }
                        pf.itop = String.valueOf(Double.valueOf(pf.itop) + pf.lineDistance * pf.rate);
                        pf.iheight = String.valueOf(Double.valueOf(pf.iheight) - pf.lineDistance * pf.rate);
                    }
                    continue;
                }
                else if (pf.itype.equals("3"))
                {
                    cmd.append("<?Line ");
                    posifix(cmd, pf);
                    continue;
                }
            }
            else
            {
                cmd.append("?>");
                continue;
            }
        }

        // 为了符合java传给js格式添加
        return cmd.toString().replaceAll("\"", "'").replaceAll("=", "&");
    }

    /**
     * 拼串的后部分
     * 
     * @param cmd
     * @throws Exception
     */
    private static void posifix(StringBuilder cmd, PrintFormat pf) throws Exception
    {

        cmd.append(", position=\"");

        if (pf.itop != null)
        {
            cmd.append("top:" + String.format("%1$2.1f", Double.parseDouble(pf.itop) / pf.rate) + ";");
        }
        if (pf.ileft != null)
        {
            cmd.append("left:" + String.format("%1$2.1f", Double.parseDouble(pf.ileft) / pf.rate) + ";");
        }
        if (pf.iwidth != null)
        {
            cmd.append("width:" + String.format("%1$2.1f", Double.parseDouble(pf.iwidth) / pf.rate) + ";");
        }
        if (pf.iheight != null)
        {
            cmd.append("height:" + String.format("%1$2.1f", Double.parseDouble(pf.iheight) / pf.rate) + ";");
        }

        cmd.append("\"");
        cmd.append(", style=\"valign:top;");

        if (pf.ialign != null)
        {
            if (pf.ialign.equalsIgnoreCase("1"))
            {
                cmd.append("align:left;");
            }
            else if (pf.ialign.equalsIgnoreCase("2"))
            {
                cmd.append("align:right;");
            }
            else if (pf.ialign.equalsIgnoreCase("3"))
            {
                cmd.append("align:center;");
            }
        }

        if (pf.isize != null)
        {
            cmd.append("font-size:" + pf.isize + ";");
        }

        if (pf.ibold != null)
        {
            cmd.append("font-weight:bold;");
        }

        if ((pf.icol != null) && Long.parseLong(pf.icol) >= 0)
        {
            cmd.append("fore-color:" + pf.icol + ";");
        }

        if ((pf.iunderline != null) && (Integer.parseInt(pf.iunderline) == 1))
        {
            cmd.append("text-decoration:underline;");
        }
        cmd.append("\", spec=\"");

        if (pf.autowrap)
        {
            cmd.append("auto-wrap:TRUE;");
        }

        cmd.append("auto-scale:TRUE;");
        cmd.append("\"?>");
    }
}
