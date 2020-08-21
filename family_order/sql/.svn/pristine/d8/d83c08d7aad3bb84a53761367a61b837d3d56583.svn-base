
select STAFF_ID,RIGHT_CODE,USER_PRODUCT_CODE,START_DATE,END_DATE,RSRV_STR1,RSRV_STR2,RSRV_STR3,REMARK,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID 
  from tf_m_staff_grp_right t 
   where 1 = 1 
    and t.staff_id = :STAFF_ID 
    and t.user_product_code = :USER_PRODUCT_CODE 
    and t.right_code = :RIGHT_CODE 
    and to_char(t.start_date,'yyyy-mm-dd') = :START_DATE  