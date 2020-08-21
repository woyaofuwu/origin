SELECT b.cust_name para_code1,
       a.serial_number_b para_code2,
       a.short_code para_code3,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') para_code4,
       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') para_code5,
       decode(sign(a.start_date - sysdate), -1, '1', '0') para_code6,
       decode(sign(a.end_date - sysdate), -1, '1', '0') para_code7,
       a.role_b para_code8,
       c.eparchy_code para_code9,
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
  FROM (select user_id_b, serial_number_b, short_code, start_date, end_date,r.role_b
          from tf_f_relation_uu u,TD_S_RELATION_ROLE r
         where r.relation_type_code='20'
           and user_id_a = to_number(:PARA_CODE1)
           and start_date < end_date
           and u.role_code_b=r.role_code_b(+)
           and u.role_code_a=r.role_code_a(+)) a,
       tf_f_customer b,
       tf_f_user c
 WHERE c.user_id = a.user_id_b
   AND c.partition_id = mod(a.user_id_b, 10000)
   AND c.cust_id = b.cust_id
   AND b.partition_id = mod(c.cust_id, 10000)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)