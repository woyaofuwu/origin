SELECT  serial_number ,to_char(user_id) user_id, partition_id, para_code2, para_code3, para_code4, para_code5, para_code6, para_code7,
         para_code8, para_code9, para_code10, para_code11, para_code12, para_code13, para_code14, para_code15, para_code16,
         para_code17, para_code18, para_code19, para_code20, para_code21, para_code22, para_code23, para_code24, para_code25, 
         para_code26, para_code27, para_code28, para_code29, para_code30, to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
         
         decode(para_code1,null,'','0','该客户是高价值客户','1','该客户不是高价值客户',para_code1)
         ||decode(para_code2,null,'',',~近三个月平均消费金额:'||to_number(para_code2)/100||'元')
         ||decode(para_code3,null,'',',~近三个月平均新业务消费金额:'||to_number(para_code3)/100||'元')
         ||decode(para_code4,null,'',',~近三个月平均长途消费金额:'||to_number(para_code4)/100||'元') para_code1
  FROM  tf_f_user_wholeinfo
 WHERE  serial_number=:SERIAL_NUMBER