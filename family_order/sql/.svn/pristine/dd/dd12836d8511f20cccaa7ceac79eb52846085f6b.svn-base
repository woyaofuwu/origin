--IS_CACHE=Y
SELECT staff_id,depart_id,staff_name,job_code,manager_info,sex,email,user_pid,serial_number,to_char(cust_id) cust_id,dimission_tag,to_char(birthday,'yyyy-mm-dd hh24:mi:ss') birthday,staff_group_id,cust_hobyy,remark,rsvalue1,rsvalue2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id ,depart_id B_DEPARTID,sex b_sex,dimission_tag b_dimissiontag,update_staff_id b_updatestaffid,update_depart_id b_updatedepartid
  FROM td_m_staff
 WHERE depart_id in (select depart_id from td_m_depart where AREA_CODE=:AREA_CODE AND validflag='0')
   AND dimission_tag=:DIMISSION_TAG