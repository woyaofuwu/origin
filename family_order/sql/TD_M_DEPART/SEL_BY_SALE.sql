--IS_CACHE=Y
SELECT depart_id,depart_code,depart_name,depart_kind_code,depart_frame,validflag,area_code,parent_depart_id,manager_id,order_no,user_depart_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,depart_level,remark,rsvalue1,rsvalue2,rsvalue3,rsvalue4,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_depart
 WHERE ((    depart_frame LIKE :DEPART_FRAME||'%'
         AND depart_id<>:DEPART_ID
         AND depart_kind_code IN ('3','4','5','6','7','8','B')
        )
        OR 
        (    depart_frame LIKE :DEPART_FRAME2||'%'
         AND depart_kind_code='6'
        )
       )
   AND validflag='0'