DECLARE
iv_user_id NUMBER(16):=TO_NUMBER(:USER_ID);
iv_partition_id NUMBER(4):=MOD(iv_user_id,10000);
iv_trade_id NUMBER(16):=TO_NUMBER(:TRADE_ID);
iv_serial_number VARCHAR2(15):=:serial_number;
iv_trade_type_code NUMBER(4):=:trade_type_code;
iv_olcom_work_id VARCHAR2(16);
iv_exec_month NUMBER(2);
iv_switch_id VARCHAR2(6);
iv_switch_type_code CHAR(2);
iv_curdate DATE:=SYSDATE;
iv_eparchy_code CHAR(4);
BEGIN
:CODE:=-1;
:INFO:='TRADE OK';
SELECT DISTINCT b.switch_id,switch_type_code,f_sys_getseqid(b.eparchy_code,'seq_olcom_id'),b.eparchy_code
INTO iv_switch_id,iv_switch_type_code,iv_olcom_work_id,iv_eparchy_code
FROM td_m_moffice b,td_m_switch c
WHERE b.switch_id=c.switch_id AND b.eparchy_code=c.eparchy_code AND iv_serial_number BETWEEN serialnumber_s AND serialnumber_e;
iv_exec_month:=SUBSTRB(iv_olcom_work_id,5,2);
INSERT INTO ti_c_olcomwork(olcom_work_id,trade_id,switch_id,switch_type_code,trade_type_code,cancel_tag,product_id,user_id,serial_number,refer_time,refer_staff_id,refer_depart_id,reparchy_code,exec_time,exec_month,priority,olcom_state)
SELECT iv_olcom_work_id,trade_id,iv_switch_id,iv_switch_type_code,trade_type_code,cancel_tag,product_id,user_id,serial_number,iv_curdate,trade_staff_id,trade_depart_id,trade_eparchy_code,iv_curdate,iv_exec_month,priority,'0' FROM tf_b_trade
WHERE trade_id=iv_trade_id AND cancel_tag='0';
INSERT INTO ti_c_olcomwork_serv (olcom_work_id,exec_month,olcom_serv_code,serv_order)
SELECT iv_olcom_work_id,iv_exec_month,b.olcom_serv_code,b.serv_order
FROM td_o_tradeolcom a,td_o_olcomservcode b
WHERE trade_type_code =iv_trade_type_code
AND UPPER(eparchy_code)='ZZZZ'
AND a.olcom_serv_code=b.olcom_serv_code;
FOR i IN (SELECT DISTINCT olcom_var_code FROM td_o_tradeolcom a,td_o_olcomservvar b
WHERE trade_type_code = iv_trade_type_code AND UPPER(eparchy_code)='ZZZZ' AND a.olcom_serv_code=b.olcom_serv_code) LOOP
IF i.olcom_var_code='G002' THEN
INSERT INTO ti_c_olcomwork_var (olcom_work_id,exec_month,olcom_var_code,var_value)
SELECT iv_olcom_work_id,iv_exec_month,i.olcom_var_code,res_info1
FROM tf_f_user_res
WHERE  iv_curdate BETWEEN start_date AND end_date+0
AND user_id=iv_user_id AND partition_id=iv_partition_id
AND res_type_code ='1';
ELSIF i.olcom_var_code='G004' THEN
INSERT INTO ti_c_olcomwork_var (olcom_work_id,exec_month,olcom_var_code,var_value)
SELECT iv_olcom_work_id,iv_exec_month,i.olcom_var_code,res_code
FROM tf_f_user_res
WHERE  iv_curdate BETWEEN start_date AND end_date+0
AND user_id=iv_user_id AND partition_id=iv_partition_id
AND res_type_code ='0';
ELSIF i.olcom_var_code='G007' THEN
INSERT INTO ti_c_olcomwork_var (olcom_work_id,exec_month,olcom_var_code,var_value)
SELECT iv_olcom_work_id,iv_exec_month,i.olcom_var_code,para_code1
FROM td_s_commpara
WHERE  iv_curdate BETWEEN start_date AND end_date+0
AND INSTR('ZZZZ'||iv_eparchy_code,eparchy_code)>0
AND param_attr=750 AND param_code=i.olcom_var_code;
END IF;
END LOOP;
:CODE:=0;
EXCEPTION
WHEN OTHERS THEN
:INFO:='填写指令异常:'||SUBSTRB(SQLERRM,12);
END;