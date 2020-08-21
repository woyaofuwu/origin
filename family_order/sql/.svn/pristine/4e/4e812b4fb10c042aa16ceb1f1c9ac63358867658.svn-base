SELECT /*+ ordered use_nl(b,a)*/COUNT(1) recordcount
  FROM (SELECT imsi FROM tf_f_user_infochange b WHERE b.user_id=:USER_ID AND b.partition_id=MOD(:USER_ID,10000) AND SYSDATE BETWEEN b.start_date+0 AND b.end_date+0) c,tf_r_simcard_use a
 WHERE a.sim_type_code=:SIM_TYPE_CODE
   AND (a.capacity_type_code = :CAPACITY_TYPE_CODE OR :CAPACITY_TYPE_CODE='*')
   AND a.imsi=c.imsi