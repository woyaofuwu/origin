SELECT
orderno para_code1,serial_number_b para_code2,
decode(role_code_b,'1','主卡号码','2','守护号码','3','亲情号码','副卡号码') para_code3,
short_code para_code4,
role_code_b para_code5,user_id_a para_code6,
user_id_b para_code7, '' para_code8,
'' para_code9,'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
start_date start_date,end_date end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM
(
SELECT to_char(c.user_id_a) user_id_a,to_char(c.user_id_b) user_id_b,
c.serial_number_a ,c.serial_number_b ,
to_char(c.orderno) orderno,c.short_code,c.role_code_a,c.role_code_b,c.relation_type_code,
c.start_date,c.end_date
FROM tf_f_relation_uu c
WHERE c.user_id_a = (SELECT user_id_a
FROM tf_f_relation_uu
WHERE user_id_b = to_number(:PARA_CODE1)
AND partition_id = MOD(to_number(:PARA_CODE1),10000)
AND role_code_b <> '3'
AND relation_type_code = 40)
and c.relation_type_code = 40
And c.end_date > sysdate
UNION all
select to_char(c.user_id_a) user_id_a,to_char(c.user_id_b) user_id_b,
c.serial_number_a ,c.serial_number_b ,
to_char(c.orderno) orderno,c.short_code,c.role_code_a,c.role_code_b,c.relation_type_code,
c.start_date,c.end_date
FROM tf_f_relation_uu c
WHERE EXISTS (SELECT user_id_b
FROM tf_f_relation_uu b
WHERE b.user_id_a = (SELECT user_id_a
FROM tf_f_relation_uu
WHERE user_id_b = to_number(:PARA_CODE1)
AND partition_id = MOD(to_number(:PARA_CODE1),10000)
AND role_code_b <> '3'
AND relation_type_code = 40)
AND b.role_code_b = '2'
AND c.user_id_a = b.user_id_b)
and c.relation_type_code = 40
AND c.role_code_b = '3'
And c.end_date > sysdate
UNION all
SELECT to_char(c.user_id_a) user_id_a,to_char(c.user_id_b) user_id_b,
c.serial_number_a ,c.serial_number_b ,
to_char(c.orderno) orderno,c.short_code,c.role_code_a,c.role_code_b,c.relation_type_code,
c.start_date,c.end_date
FROM tf_f_relation_uu c
WHERE EXISTS (SELECT user_id_a
FROM tf_f_relation_uu b
WHERE b.user_id_b = (SELECT user_id_a
FROM tf_f_relation_uu
WHERE user_id_b = to_number(:PARA_CODE1)
AND partition_id = MOD(to_number(:PARA_CODE1),10000)
AND role_code_b = '3'
AND relation_type_code = 40)
AND b.role_code_b = '2'
AND c.user_id_a = b.user_id_a)
AND c.relation_type_code = 40
And c.end_date > sysdate
union all
SELECT to_char(c.user_id_a) user_id_a,to_char(c.user_id_b) user_id_b,
c.serial_number_a ,c.serial_number_b ,
to_char(c.orderno) orderno,c.short_code,c.role_code_a,c.role_code_b,c.relation_type_code,
c.start_date,c.end_date
FROM tf_f_relation_uu c
WHERE EXISTS
(select user_id_b
from tf_f_relation_uu d
where d.user_id_a =
(SELECT user_id_a
FROM tf_f_relation_uu b
WHERE b.user_id_b = (SELECT user_id_a
FROM tf_f_relation_uu
WHERE user_id_b = to_number(:PARA_CODE1)
AND partition_id = MOD(to_number(:PARA_CODE1),10000)
AND role_code_b = '3'
AND relation_type_code = 40)
AND b.role_code_b = '2')
and
c.user_id_a = d.user_id_b)
AND c.relation_type_code = 40
And c.end_date > sysdate
and c.role_code_b = '3'
order by role_code_b
)
WHERE (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)