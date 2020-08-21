select u.city_code para_code1,
       a.serial_number para_code2,
       (select v1.cust_name
          from tf_f_customer v1
         where v1.cust_id = u.cust_id) para_code3,
       '' para_code4,
       a.rsrv_str22 para_code5,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') para_code6,
       to_number(a.advance_pay) / 100 para_code7,
       a.trade_staff_id para_code8,
       '' para_code9,
       '' para_code10,
       '' para_code11,
       '' para_code12,
       '' para_code13,
       '' para_code14,
       '' para_code15,
       '' para_code16,
       '' para_code17,
       '' para_code18,
       '' para_code19,
       '' para_code20,
       '' para_code21,
       '' para_code22,
       '' para_code23,
       '' para_code24,
       '' para_code25,
       '' para_code26,
       '' para_code27,
       '' para_code28,
       '' para_code29,
       '' para_code30,
       '' start_date,
       '' end_date,
       '' eparchy_code,
       '' remark,
       '' update_staff_id,
       '' update_depart_id,
       '' update_time,
       '' subsys_code,
       0 param_attr,
       '' param_code,
       '' param_name
  from tf_f_user_sale_active a, tf_f_user u
 where a.user_id = u.user_id
   and substr(a.campn_code, 0, 1) = 'w'
   and a.process_tag = '0'
   and (u.city_code = :PARA_CODE1 or :PARA_CODE1 is null)
   and (a.rsrv_str22 = :PARA_CODE2 or :PARA_CODE2 is null)
   and a.start_date >= to_date(:PARA_CODE3, 'yyyy-mm-dd hh24:mi:ss')
   and a.end_date <= to_date(:PARA_CODE4, 'yyyy-mm-dd hh24:mi:ss')