SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara a
 WHERE a.subsys_code=:SUBSYS_CODE
   AND a.param_attr=:PARAM_ATTR
   AND a.param_code=:PARAM_CODE
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND EXISTS(
            SELECT 1 FROM tf_f_user_discnt b 
             WHERE b.partition_id=mod(to_number(:USER_ID),10000)
               AND b.user_id=TO_NUMBER(:USER_ID)
               AND b.discnt_code =a.para_code3 
               AND b.end_date > sysdate
               AND b.start_date > sysdate)