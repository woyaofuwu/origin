--IS_CACHE=Y
select a.staff_id staff_id,a.depart_id depart_id,a.cust_manager_flag,b.area_code rsvalue1,b.rsvalue2 rsvalue2 , a.CITY_CODE  CITY_CODE , a.staff_name STAFF_NAME  
 from td_m_staff a,td_m_depart b
 where a.serial_number=:SERIAL_NUMBER and a.depart_id=b.depart_id
 and a.dimission_tag='0'
 and sysdate between a.start_date and a.end_date 