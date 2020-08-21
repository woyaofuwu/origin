
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class RelationxxtTradeData extends BaseTradeData
{
    private String user_id_a;

    private String serial_number_a;

    private String serial_number_b;

    private String element_type_code;

    private String element_id;

    private String inst_id;

    private String start_date;

    private String end_date;

    private String name;

    private String update_time;

    private String update_staff_id;

    private String update_depart_id;

    private String remark;

    private String rsrv_num1;

    private String rsrv_num2;

    private String rsrv_num3;

    private String rsrv_str1;

    private String rsrv_str2;

    private String rsrv_str3;

    private String rsrv_str4;

    private String rsrv_str5;

    private String rsrv_str6;

    private String rsrv_str7;

    private String rsrv_str8;

    private String rsrv_str9;

    private String rsrv_str10;

    private String rsrv_date1;

    private String rsrv_date2;

    private String rsrv_date3;

    private String modify_tag;

    private String ec_user_id;
    private String service_id;
    private String rela_inst_id;

    public RelationxxtTradeData()
    {

    }

    public RelationxxtTradeData(IData data)
    {
        this.user_id_a = data.getString("USER_ID_A");
        this.serial_number_a = data.getString("SERIAL_NUMBER_A");
        this.serial_number_b = data.getString("SERIAL_NUMBER_B");
        this.element_type_code = data.getString("ELEMENT_TYPE_CODE");
        this.element_id = data.getString("ELEMENT_ID");
        this.inst_id = data.getString("INST_ID");
        this.start_date = data.getString("START_DATE");
        this.end_date = data.getString("END_DATE");
        this.name = data.getString("NAME");
        this.update_time = data.getString("UPDATE_TIME");
        this.update_staff_id = data.getString("UPDATE_STAFF_ID");
        this.update_depart_id = data.getString("UPDATE_DEPART_ID");
        this.remark = data.getString("REMARK");
        this.rsrv_num1 = data.getString("RSRV_NUM1");
        this.rsrv_num2 = data.getString("RSRV_NUM2");
        this.rsrv_num3 = data.getString("RSRV_NUM3");
        this.rsrv_str1 = data.getString("RSRV_STR1");
        this.rsrv_str2 = data.getString("RSRV_STR2");
        this.rsrv_str3 = data.getString("RSRV_STR3");
        this.rsrv_str4 = data.getString("RSRV_STR4");
        this.rsrv_str5 = data.getString("RSRV_STR5");
        this.rsrv_str6 = data.getString("RSRV_STR6");
        this.rsrv_str7 = data.getString("RSRV_STR7");
        this.rsrv_str8 = data.getString("RSRV_STR8");
        this.rsrv_str9 = data.getString("RSRV_STR9");
        this.rsrv_str10 = data.getString("RSRV_STR10");
        this.rsrv_date1 = data.getString("RSRV_DATE1");
        this.rsrv_date2 = data.getString("RSRV_DATE2");
        this.rsrv_date3 = data.getString("RSRV_DATE3");
        this.modify_tag = data.getString("MODIFY_TAG");
        this.ec_user_id = data.getString("EC_USER_ID");
        this.service_id = data.getString("SERVICE_ID");
        this.rela_inst_id = data.getString("RELA_INST_ID");
    }

    public String getuser_id_a()
    {
        return user_id_a;
    }

    public String getserial_number_a()
    {
        return serial_number_a;
    }

    public String getserial_number_b()
    {
        return serial_number_b;
    }

    public String getelement_type_code()
    {
        return element_type_code;
    }

    public String getelement_id()
    {
        return element_id;
    }

    public String getinst_id()
    {
        return inst_id;
    }

    public String getstart_date()
    {
        return start_date;
    }

    public String getend_date()
    {
        return end_date;
    }

    public String getname()
    {
        return name;
    }

    public String getupdate_time()
    {
        return update_time;
    }

    public String getupdate_staff_id()
    {
        return update_staff_id;
    }

    public String getupdate_depart_id()
    {
        return update_depart_id;
    }

    public String getremark()
    {
        return remark;
    }

    public String getrsrv_num1()
    {
        return rsrv_num1;
    }

    public String getrsrv_num2()
    {
        return rsrv_num2;
    }

    public String getrsrv_num3()
    {
        return rsrv_num3;
    }

    public String getrsrv_str1()
    {
        return rsrv_str1;
    }

    public String getrsrv_str2()
    {
        return rsrv_str2;
    }

    public String getrsrv_str3()
    {
        return rsrv_str3;
    }

    public String getrsrv_str4()
    {
        return rsrv_str4;
    }

    public String getrsrv_str5()
    {
        return rsrv_str5;
    }

    public String getrsrv_str6()
    {
        return rsrv_str6;
    }

    public String getrsrv_str7()
    {
        return rsrv_str7;
    }

    public String getrsrv_str8()
    {
        return rsrv_str8;
    }

    public String getrsrv_str9()
    {
        return rsrv_str9;
    }

    public String getrsrv_str10()
    {
        return rsrv_str10;
    }

    public String getrsrv_date1()
    {
        return rsrv_date1;
    }

    public String getrsrv_date2()
    {
        return rsrv_date2;
    }

    public String getrsrv_date3()
    {
        return rsrv_date3;
    }
    public String getmodify_tag()
    {
        return modify_tag;
    }
    public String getec_user_id()
    {
        return ec_user_id;
    }
    public String getservice_id()
    {
        return service_id;
    }
    public String getrela_inst_id()
    {
        return rela_inst_id;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RELATION_XXT";
    }

    public void setuser_id_a(String user_id_a)
    {
        this.user_id_a = user_id_a;
    }

    public void setserial_number_a(String serial_number_a)
    {
        this.serial_number_a = serial_number_a;
    }

    public void setserial_number_b(String serial_number_b)
    {
        this.serial_number_b = serial_number_b;
    }

    public void setelement_type_code(String element_type_code)
    {
        this.element_type_code = element_type_code;
    }

    public void setelement_id(String element_id)
    {
        this.element_id = element_id;
    }

    public void setinst_id(String inst_id)
    {
        this.inst_id = inst_id;
    }

    public void setstart_date(String start_date)
    {
        this.start_date = start_date;
    }

    public void setend_date(String end_date)
    {
        this.end_date = end_date;
    }

    public void setname(String name)
    {
        this.name = name;
    }

    public void setupdate_time(String update_time)
    {
        this.update_time = update_time;
    }

    public void setupdate_staff_id(String update_staff_id)
    {
        this.update_staff_id = update_staff_id;
    }

    public void setupdate_depart_id(String update_depart_id)
    {
        this.update_depart_id = update_depart_id;
    }

    public void setremark(String remark)
    {
        this.remark = remark;
    }

    public void setrsrv_num1(String rsrv_num1)
    {
        this.rsrv_num1 = rsrv_num1;
    }

    public void setrsrv_num2(String rsrv_num2)
    {
        this.rsrv_num2 = rsrv_num2;
    }

    public void setrsrv_num3(String rsrv_num3)
    {
        this.rsrv_num3 = rsrv_num3;
    }

    public void setrsrv_str1(String rsrv_str1)
    {
        this.rsrv_str1 = rsrv_str1;
    }

    public void setrsrv_str2(String rsrv_str2)
    {
        this.rsrv_str2 = rsrv_str2;
    }

    public void setrsrv_str3(String rsrv_str3)
    {
        this.rsrv_str3 = rsrv_str3;
    }

    public void setrsrv_str4(String rsrv_str4)
    {
        this.rsrv_str4 = rsrv_str4;
    }

    public void setrsrv_str5(String rsrv_str5)
    {
        this.rsrv_str5 = rsrv_str5;
    }

    public void setrsrv_str6(String rsrv_str6)
    {
        this.rsrv_str6 = rsrv_str6;
    }

    public void setrsrv_str7(String rsrv_str7)
    {
        this.rsrv_str7 = rsrv_str7;
    }

    public void setrsrv_str8(String rsrv_str8)
    {
        this.rsrv_str8 = rsrv_str8;
    }

    public void setrsrv_str9(String rsrv_str9)
    {
        this.rsrv_str9 = rsrv_str9;
    }

    public void setrsrv_str10(String rsrv_str10)
    {
        this.rsrv_str10 = rsrv_str10;
    }

    public void setrsrv_date1(String rsrv_date1)
    {
        this.rsrv_date1 = rsrv_date1;
    }

    public void setrsrv_date2(String rsrv_date2)
    {
        this.rsrv_date2 = rsrv_date2;
    }

    public void setrsrv_date3(String rsrv_date3)
    {
        this.rsrv_date3 = rsrv_date3;
    }

    public void setmodify_tag(String modify_tag)
    {
        this.modify_tag = modify_tag;
    }

    public void setec_user_id(String ec_user_id)
    {
        this.ec_user_id = ec_user_id;
    }
    public void setservice_id(String service_id)
    {
        this.service_id = service_id;
    }
    public void setrela_inst_id(String rela_inst_id)
    {
        this.rela_inst_id = rela_inst_id;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("USER_ID_A", this.user_id_a);
        data.put("SERIAL_NUMBER_A", this.serial_number_a);
        data.put("SERIAL_NUMBER_B", this.serial_number_b);
        data.put("ELEMENT_TYPE_CODE", this.element_type_code);
        data.put("ELEMENT_ID", this.element_id);
        data.put("INST_ID", this.inst_id);
        data.put("START_DATE", this.start_date);
        data.put("END_DATE", this.end_date);
        data.put("NAME", this.name);
        data.put("UPDATE_TIME", this.update_time);
        data.put("UPDATE_STAFF_ID", this.update_staff_id);
        data.put("UPDATE_DEPART_ID", this.update_depart_id);
        data.put("REMARK", this.remark);
        data.put("RSRV_NUM1", this.rsrv_num1);
        data.put("RSRV_NUM2", this.rsrv_num2);
        data.put("RSRV_NUM3", this.rsrv_num3);
        data.put("RSRV_STR1", this.rsrv_str1);
        data.put("RSRV_STR2", this.rsrv_str2);
        data.put("RSRV_STR3", this.rsrv_str3);
        data.put("RSRV_STR4", this.rsrv_str4);
        data.put("RSRV_STR5", this.rsrv_str5);
        data.put("RSRV_STR6", this.rsrv_str6);
        data.put("RSRV_STR7", this.rsrv_str7);
        data.put("RSRV_STR8", this.rsrv_str8);
        data.put("RSRV_STR9", this.rsrv_str9);
        data.put("RSRV_STR10", this.rsrv_str10);
        data.put("RSRV_DATE1", this.rsrv_date1);
        data.put("RSRV_DATE2", this.rsrv_date2);
        data.put("RSRV_DATE3", this.rsrv_date3);
        data.put("MODIFY_TAG", this.modify_tag);
        data.put("EC_USER_ID", this.ec_user_id);
        data.put("SERVICE_ID", this.service_id);
        data.put("RELA_INST_ID", this.rela_inst_id);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
