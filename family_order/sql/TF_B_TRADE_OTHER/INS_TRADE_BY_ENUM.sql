insert into TF_B_TRADE_OTHER
       (TRADE_ID,
       ACCEPT_MONTH    ,
       USER_ID         ,
       RSRV_VALUE_CODE ,
       RSRV_VALUE      ,  
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_STR9 ,
       RSRV_STR11,
       START_DATE, 
       END_DATE,
       MODIFY_TAG)
       select to_number(:TRADE_ID),
              :ACCEPT_MONTH,
              v.user_id,
              'ENUM',
              'ENUM成员',
              '0',
              '0',
              '0',
              '8173',
              '1',
              sysdate -1,
              sysdate,
              '0'
         from tf_f_user_impu v
        where v.partition_id = MOD(to_number(:USER_ID), 10000)
          and v.user_id = to_number(:USER_ID)
          and (v.RSRV_STR1 = '0' or (v.RSRV_STR1 = '1' and v.RSRV_STR5 = '1'))
          and sysdate between v.start_date and v.end_date