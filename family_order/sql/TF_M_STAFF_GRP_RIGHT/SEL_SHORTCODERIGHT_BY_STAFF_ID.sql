select t.STAFF_ID,t.RIGHT_CODE,t.USER_PRODUCT_CODE,t.START_DATE,
	    t.END_DATE,t.RSRV_STR1,t.RSRV_STR2,t.RSRV_STR3
  from tf_m_staff_grp_right t
 where 1 = 1
   and t.staff_id = :STAFF_ID
   and t.right_code = :RIGHT_CODE
   and t.user_product_code = :USER_PRODUCT_CODE
   and sysdate between t.start_date and t.end_date