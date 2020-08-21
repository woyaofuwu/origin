select  
b.subsys_code,b.param_attr,b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3,b.para_code4,b.para_code5,b.para_code6,b.para_code7,b.para_code8,b.para_code9,b.para_code10,b.para_code11,b.para_code12,b.para_code13,b.para_code14,b.para_code15,b.para_code16,b.para_code17,b.para_code18,b.para_code19,b.para_code20,b.para_code21,b.para_code22,b.para_code23,b.para_code24,b.para_code25,to_char(b.para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(b.para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(b.para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(b.para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,b.eparchy_code,b.remark,b.update_staff_id,b.update_depart_id,to_char(b.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
from tf_f_user_discnt a,td_s_commpara b
where a.user_id=to_number(:PARA_CODE1)                        
and b.param_attr='1240'                          
and b.param_code=to_char(:PARA_CODE2)                    
and a.discnt_code=b.para_code1                   
and sysdate between a.start_date and a.end_date
AND (:PARA_CODE3 IS NULL OR :PARA_CODE3='')
AND (:PARA_CODE4 IS NULL OR :PARA_CODE4='')
AND (:PARA_CODE5 IS NULL OR :PARA_CODE5='')
AND (:PARA_CODE6 IS NULL OR :PARA_CODE6='')
AND (:PARA_CODE7 IS NULL OR :PARA_CODE7='')
AND (:PARA_CODE8 IS NULL OR :PARA_CODE8='')
AND (:PARA_CODE9 IS NULL OR :PARA_CODE9='')
AND (:PARA_CODE10 IS NULL OR :PARA_CODE10='')