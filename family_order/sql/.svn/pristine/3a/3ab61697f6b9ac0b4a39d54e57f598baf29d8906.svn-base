insert into ti_a_sync_recv
          (sync_sequence,trade_id,trade_type_code,batch_id,priority,
           charge_id,acct_id,user_id,channel_id,payment_id,
           pay_fee_mode_code,payment_op,recv_fee,recv_time,recv_eparchy_code,
           recv_city_code,recv_depart_id,recv_staff_id,payment_reason_code,cancel_tag,
           deal_tag,act_tag,START_DATE,months,ACCT_ID2,
           USER_ID2,outer_trade_Id,SYNC_DAY,RSRV_INFO1)
        values(:SYNC_SEQUENCE,:SYNC_SEQUENCE,7044,:SYNC_SEQUENCE,100,
              :SYNC_SEQUENCE,0,:USER_ID,15000,102,
              '0','16000',0,sysdate,'0898',
              '0000','00000','SUPERUSR','0',0,
              0,'4',to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),240,0,
              0,:SYNC_SEQUENCE,to_number(substr(to_char(:SYNC_SEQUENCE),7,2)),to_number(:BET_MONTH))