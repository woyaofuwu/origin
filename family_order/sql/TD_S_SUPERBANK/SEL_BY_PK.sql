--IS_CACHE=Y
SELECT super_bank_code,super_bank,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_s_superbank
 WHERE super_bank_code=:SUPER_BANK_CODE