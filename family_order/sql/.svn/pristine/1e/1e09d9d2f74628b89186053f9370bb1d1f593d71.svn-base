--IS_CACHE=Y
select /*+INDEX(a IDX_TD_M_DEPART_RSVALUE2)*/ depart_id PARACODE,depart_name PARANAME
 from td_m_depart a
where (rsvalue2=:TRADE_EPARCHY_CODE or rsvalue2 = 'ZZZZ')
  and validflag='0'
  and start_date<=sysdate
  and end_date>=sysdate