--IS_CACHE=Y
SELECT staff_id,depart_id,staff_name,job_code,manager_info,sex,email,user_pid,serial_number,to_char(cust_id) cust_id,dimission_tag,to_char(birthday,'yyyy-mm-dd hh24:mi:ss') birthday,staff_group_id,cust_hobyy,manager_staff_id,receive_type_code,login_flag,cust_manager_flag,city_code,eparchy_code,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,
   (select b.staff_passwd from tf_m_staffpasswd b where b.staff_id=a.staff_id) STAFF_PASSWD,
   (select c.area_code from td_m_depart c where c.depart_id=a.depart_id) city_code,
   (select c.rsvalue2 from td_m_depart c where c.depart_id=a.depart_id) eparchy_code
  FROM td_m_staff a
 WHERE job_code=:JOB_CODE
   AND dimission_tag='0'