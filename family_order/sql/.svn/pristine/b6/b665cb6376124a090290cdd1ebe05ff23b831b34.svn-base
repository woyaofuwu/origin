SELECT a.user_id, a.city_code, b.area_code, b.area_name
  FROM tf_f_user a, td_m_area b
 WHERE a.city_code = b.area_code
   AND a.user_id = :USER_ID
   AND a.partition_id = MOD(:USER_ID, 10000)
   AND remove_tag = :REMOVE_TAG
   AND SYSDATE BETWEEN b.start_date AND b.end_date