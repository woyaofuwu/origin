SELECT a.partition_id,
       to_char(a.user_id) user_id,
       to_char(a.user_id_a) user_id_a,
       a.discnt_code,
       a.spec_tag,
       a.relation_type_code,
       to_char(a.inst_id) inst_id,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_discnt a 
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND sysdate between a.start_date and a.end_date
   AND a.discnt_code IN (SELECT to_number(param_code)
                         FROM td_s_commpara
                        WHERE SUBSYS_CODE = 'CSM'
                          AND param_attr = :PARAM_ATTR
                          AND end_date > SYSDATE
                          AND eparchy_code = '0898')
