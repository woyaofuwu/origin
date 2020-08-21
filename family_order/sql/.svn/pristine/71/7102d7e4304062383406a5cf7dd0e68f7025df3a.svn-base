update tf_b_tradebook a
  set a.process_tag=:PROCESS_TAG,
      a.rsrv_date1=(case when :PROCESS_TAG='1' then sysdate
          else rsrv_date1 end),
      a.rsrv_date2=(case when :PROCESS_TAG='2' then sysdate
          else rsrv_date2 end),
      a.rsrv_date3=(case when :PROCESS_TAG='3' then sysdate
          else rsrv_date3 end),
      a.rsrv_date4=(case when :PROCESS_TAG='4' then sysdate
          else rsrv_date4 end),
      a.rsrv_date5=(case when :PROCESS_TAG='5' then sysdate
          else rsrv_date5 end)                                         
    where a.res_code=:SERIAL_NUMBER
       and a.trade_type_code=:TRADE_TYPE_CODE