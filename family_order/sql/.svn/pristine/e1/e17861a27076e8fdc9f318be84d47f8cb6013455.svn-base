SELECT
to_char(user_id_a) para_code1,to_char(user_id_b) para_code2,
serial_number_a para_code3,serial_number_b para_code4,
to_char(orderno) para_code5,short_code para_code6,
role_code_a para_code7, role_code_b para_code8,
relation_type_code para_code9,'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
to_char(start_date,'YYYY-MM-DD HH24:MI:SS') start_date,to_char(end_date,'YYYY-MM-DD HH24:MI:SS') end_date,
'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM
(
  SELECT user_id_a,user_id_b,
  serial_number_a ,serial_number_b ,
  orderno,short_code,role_code_a,role_code_b,relation_type_code,
  start_date,end_date
  FROM tf_f_relation_uu
  WHERE user_id_a = (SELECT user_id_a
                      FROM tf_f_relation_uu
                      WHERE user_id_b = to_number(:PARA_CODE1)
                        AND partition_id = MOD(to_number(:PARA_CODE1),10000)
                        AND role_code_b = '1'
                        AND relation_type_code = 40
                        AND end_date > SYSDATE)
  UNION
  SELECT c.user_id_a,c.user_id_b,
  c.serial_number_a ,c.serial_number_b ,
  c.orderno,c.short_code,c.role_code_a,c.role_code_b,c.relation_type_code,
  c.start_date,c.end_date
  FROM tf_f_relation_uu c
  WHERE EXISTS (SELECT user_id_b
                FROM tf_f_relation_uu b
                WHERE b.user_id_a = (SELECT user_id_a
                                      FROM tf_f_relation_uu
                                      WHERE user_id_b = to_number(:PARA_CODE1)
                                        AND partition_id = MOD(to_number(:PARA_CODE1),10000)
                                        AND role_code_b = '1'
                                        AND relation_type_code = 40
                                        AND end_date > SYSDATE)
                   AND b.role_code_b = '2'
                   AND c.user_id_a = b.user_id_b
                   AND b.end_date > SYSDATE)
  AND c.role_code_b = '3'
  AND c.end_date > SYSDATE
  UNION
  SELECT to_number(rsrv_str3) user_id_a,to_number(rsrv_str4) user_id_b,
  '' serial_number_a ,rsrv_str2 serial_number_b ,
  to_number(rsrv_str5) orderno,rsrv_str1 short_code,'' role_code_a,'5' role_code_b,'' relation_type_code,
  start_date start_date,end_date end_date
  FROM tf_f_user_other
  WHERE user_id = to_number(:PARA_CODE1)
  AND partition_id = MOD(to_number(:PARA_CODE1),10000)
  AND rsrv_value_code = 'WTHT'
  AND end_date > SYSDATE
)
WHERE end_date > SYSDATE
     AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
     AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
     AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
     AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
     AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
     AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
     AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
     AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
     AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
order by para_code8,para_code5