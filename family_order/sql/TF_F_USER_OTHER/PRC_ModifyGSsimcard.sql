DECLARE
iv_user_id NUMBER(16) := TO_NUMBER(:USER_ID);
iv_serial_number NUMBER(11) := TO_NUMBER(:SERIAL_NUMBER);
iv_rsrv_str1 VARCHAR2(50) := :RSRV_STR1;
iv_rsrv_str2 VARCHAR2(50) := :RSRV_STR2;
iv_rsrv_str3 VARCHAR2(50) := :RSRV_STR3;
iv_rsrv_str4 VARCHAR2(50) := :RSRV_STR4;
BEGIN
:CODE:=-1;
:INFO:='TRADE OK';
insert into tf_f_user_other(partition_id, user_id, rsrv_value_code, rsrv_value, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4,start_date,end_date)
values(MOD(iv_user_id,10000), iv_user_id, 'GS','ModifySimcard', iv_rsrv_str1, iv_rsrv_str2,iv_rsrv_str3,iv_rsrv_str4,sysdate,to_date('2050-01-01','yyyy-mm-dd'));
--tf_f_user_res表修改
update tf_f_user_res
set end_date = sysdate
where user_id = iv_user_id and res_type_code = '1' and end_date > sysdate;
insert into tf_f_user_res(partition_id, user_id, res_type_code, res_code, res_info1, start_date, end_date)
values(MOD(iv_user_id,10000), iv_user_id, '1', iv_rsrv_str3, iv_rsrv_str4, sysdate, to_date('2050-12-31','yyyy-mm-dd'));
--tf_f_user_infochange表修改
update tf_f_user_infochange 
set imsi = iv_rsrv_str4
where user_id = iv_user_id and end_date > sysdate;
--tf_r_simcard_use表修改
insert into tf_r_simcard_use 
select imsi,sim_card_no,empty_card_id,factory_code,imei,esn,sim_type_code,capacity_type_code,pin,puk,pin2,puk2,ki,moffice_id,'4',ki_state,serial_number,value_card_no,eparchy_code,city_code,stock_id_o,stock_id,staff_id,stock_level,agent_id,assign_time,assign_staff_id,time_in,staff_id_in,precode_tag,serial_number_code,staff_id_code,time_code,preopen_tag,date_time_open,staff_id_open,destory_time,double_tag,log_id,oper_time,oper_staff_id,open_time,open_mode,fee_tag,contract_id,cost,sale_log_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5
from tf_r_simcard_idle where sim_card_no = iv_rsrv_str3;
--tf_r_simcard_idle表修改
delete from tf_r_simcard_idle where sim_card_no = iv_rsrv_str3;
--tf_r_mphone_use表修改
insert into tf_r_mphonecode_use
select serial_number,net_code,imsi,sim_card_no,moffice_id,brand_tag,'4',code_type_code,fee_code_rule_code,fee_code_fee,null,null,precode_tag,imsi_code,precode_time,preopen_tag,eparchy_code,city_code,stock_id,stock_id_O,stock_level,staff_id,pool_code,agent_id,assign_time,assign_staff_id,null,null,time_in,staff_id_in,occupy_time,reuse_count,special_flag,log_id,oper_time,oper_staff_id,open_time,product_id,null,class_id,oper_flag,null,'????????????',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null
from tf_r_mphonecode_idle where serial_number = to_char(iv_serial_number);
--tf_r_mphone_idle表修改
delete from tf_r_mphonecode_idle where serial_number = to_char(iv_serial_number);
:CODE:=0;
EXCEPTION
WHEN OTHERS THEN
:INFO:='更改用户simcard资料异常:' || SQLERRM;
END;