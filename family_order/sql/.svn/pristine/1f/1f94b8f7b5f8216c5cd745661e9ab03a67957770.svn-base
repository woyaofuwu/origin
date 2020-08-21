--IS_CACHE=Y
select trade_type_code,trade_type,bpm_code from TD_S_TRADETYPE t
       where 1=1
       and t.end_date>sysdate
       AND (t.EPARCHY_CODE = :EPARCHY_CODE or t.EPARCHY_CODE='ZZZZ')
order by t.trade_type,t.trade_type_code