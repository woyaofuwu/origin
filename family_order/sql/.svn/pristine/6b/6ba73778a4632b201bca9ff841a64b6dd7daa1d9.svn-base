SELECT COUNT(1) recordcount FROM tf_bh_trade
       WHERE trade_type_code=:TRADE_TYPE_CODE
         AND user_id=:USER_ID
         AND subscribe_state='9'
         AND cancel_tag='0'
         AND finish_date>decode(:TYPE,
                           '0',SYSDATE+(-1)*:NUM,                  
                                '1',trunc(SYSDATE+(-1)*:NUM),                  
                                '2',ADD_MONTHS(SYSDATE,(-1)*:NUM),            
                                '3',trunc(ADD_MONTHS(SYSDATE,(-1)*:NUM),'mm'),
                                '4',ADD_MONTHS(SYSDATE,(-12)*:NUM),           
                                '5',trunc(ADD_MONTHS(SYSDATE,(-12)*:NUM),'yy'),
                                SYSDATE)