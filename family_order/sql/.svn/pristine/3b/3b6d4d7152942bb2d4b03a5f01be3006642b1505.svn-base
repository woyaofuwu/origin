--IS_CACHE=Y
select t.busi_type,
       t.id_type,
       t.id,
       t.tag,
       to_char(t.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(t.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       t.remark
  from td_b_qry_config t
 where 1 = 1
   and t.busi_type = 'G'
   and t.id_type = 'P'
   and sysdate between t.start_date and t.end_date