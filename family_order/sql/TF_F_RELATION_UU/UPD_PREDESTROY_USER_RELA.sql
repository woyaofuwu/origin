UPDATE tf_f_relation_uu
   SET end_date = decode(relation_type_code,'70',to_date(to_char(last_day(SYSDATE),'yyyy-mm-dd')||' 23:59:59','yyyy-mm-dd hh24:mi:ss'),SYSDATE)
 WHERE user_id_b = :USER_ID_B
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND (relation_type_code != '30' and relation_type_code != '45')
   AND end_date > SYSDATE