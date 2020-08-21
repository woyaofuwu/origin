--IS_CACHE=Y
select /*+INDEX(a IDX_TD_M_DEPART_RSVALUE2)*/ depart_code PARACODE,depart_name PARANAME
 from td_m_depart a
where rsvalue2=:TRADE_EPARCHY_CODE
  and validflag='0'
  and start_date<=sysdate
  and end_date>=sysdate
  and exists (select 1 from td_m_departkind b
           where b.depart_kind_code=a.depart_kind_code
             and b.code_type_code='0')