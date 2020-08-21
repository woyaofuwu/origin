SELECT SUM(NVL(a.para_value9,0)) para_value9 
  FROM tf_b_resdaystat_log a,td_m_res_commpara b,td_m_staff c
 WHERE a.oper_time>=TRUNC(SYSDATE-1)
   AND a.res_type_code=:RES_TYPE_CODE
   AND a.oper_flag=:OPER_FLAG
   AND a.stat_type=:STAT_TYPE
   AND a.eparchy_code=:EPARCHY_CODE   
   AND c.staff_id=:STAFF_ID
   AND a.staff_id=:STAFF_ID   
   AND b.eparchy_code=:EPARCHY_CODE
   AND b.para_attr=35
   AND b.para_code2=:RES_TYPE_CODE
   AND a.para_value2=b.para_code1
   AND a.depart_id=c.depart_id