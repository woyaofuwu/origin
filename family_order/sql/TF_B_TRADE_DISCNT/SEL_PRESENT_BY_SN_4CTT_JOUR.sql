select a.user_id user_id,
       a.discnt_code discnt_code,
       a.inst_id inst_id,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date
  from (select *
          from tf_b_trade_discnt t
         where t.modify_tag = '0'
           and t.trade_id in
               (select trade_id
                  from tf_b_trade
                 where net_type_code in ('11')
                   and trade_type_code in ('9711', '9712', '9725')
                   and serial_number = :SERIAL_NUMBER)
           and t.end_date > sysdate) a
