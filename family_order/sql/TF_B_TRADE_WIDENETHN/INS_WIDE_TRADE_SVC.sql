insert into tf_b_trade_svc (TRADE_ID, ACCEPT_MONTH, USER_ID, USER_ID_A, PRODUCT_ID, PACKAGE_ID, SERVICE_ID, MAIN_TAG, INST_ID, CAMPN_ID, START_DATE, END_DATE, MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)
select :TRADE_ID, to_number(substr(:TRADE_ID,5,2)), USER_ID, USER_ID_A, PRODUCT_ID, PACKAGE_ID, SERVICE_ID, MAIN_TAG, INST_ID, CAMPN_ID, START_DATE, END_DATE, '1', sysdate, :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3
  from tf_f_user_svc t
 where user_id = :USER_ID
   and partition_id = mod(user_id, 10000)   
   and sysdate between start_date and end_date   
   and exists (select 1
          from TD_B_PRODUCT tt
         where tt.product_id = t.product_id
           and tt.product_mode = '00'
           and sysdate between tt.start_date and tt.end_date  )