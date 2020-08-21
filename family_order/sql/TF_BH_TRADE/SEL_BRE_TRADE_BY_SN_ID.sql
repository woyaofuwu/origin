SELECT /*+ index(a IDX_TF_BH_TRADE_SN)*/a.trade_id, a.trade_type_code, a.trade_eparchy_code, a.accept_date
         FROM   tf_bh_trade a 
         WHERE  a.serial_number = :SERIAL_NUMBER
         AND    a.cancel_tag = '0'
         and    a.trade_type_code in ('110','153')
         AND    a.accept_date  > TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') 
     			AND  a.trade_id > :TRADE_ID
     			AND  a.accept_month in (to_number(to_char(add_months(trunc(sysdate),-3),'mm')),to_number(to_char(add_months(trunc(sysdate),-2),'mm')),to_number(to_char(add_months(trunc(sysdate),-1),'mm')),to_number(to_char(add_months(trunc(sysdate),0),'mm')))