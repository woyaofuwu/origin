--IS_CACHE=Y
select a.X_TRANS_CODE, a.EXPRESSION, a.IN_MODE_CODE, a.CLASS_NAME, a.TRANS_DESC, a.STATE, a.SORT_NO, a.TAG_CODE 
  from TD_B_REQUESTTRANS a 
where a.state = '1' 
and a.x_trans_code = :X_TRANS_CODE 
and (a.in_mode_code is null or INSTR(a.IN_MODE_CODE, ',' || :IN_MODE_CODE || ',', 1, 1) > 0) 
 order by a.sort_no  