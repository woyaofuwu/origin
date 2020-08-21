select * from (select a.user_id para_code1,
       a.serial_number para_code2,
       b.user_id para_code3,
       decode(c.is_real_name, '1', '是') para_code4,
       b.RSRV_STR1 para_code5,
       b.RSRV_STR2 para_code6,
       decode(b.RSRV_STR4,
              '10',
              '用户开户',
              '60',
              '客户资料变更',
              '100',
              '过户',
              '62',
              '实名制预登记') para_code7,
       a.cust_id para_code8,
       c.cust_id PARA_CODE9,
       c.cust_name PARA_CODE10,
       decode(a.remove_tag, '0', '正常', '1', '主动预销号', '2', '主动销号', '3', '欠费预销号', '4', '欠费销号', '5', '开户返销', '6', '过户注销') PARA_CODE11,
       b.remark
  from tf_f_user a, tf_f_user_other b, tf_f_customer c
 WHERE a.serial_number = :SERIAL_NUMBER
   AND a.user_id = b.user_id
   AND a.cust_id = c.cust_id
   AND b.RSRV_VALUE_CODE = 'CHRN'
 union all
 select a.user_id para_code1,
       a.serial_number para_code2,
       b.user_id para_code3,
       decode(c.is_real_name, '1', '是') para_code4,
       b.RSRV_STR1 para_code5,
       b.RSRV_STR2 para_code6,
       decode(b.RSRV_STR4,
              '10',
              '用户开户',
              '60',
              '客户资料变更',
              '100',
              '过户',
              '62',
              '实名制预登记') para_code7,
       a.cust_id para_code8,
       c.cust_id PARA_CODE9,
       c.cust_name PARA_CODE10,
       decode(a.remove_tag, '0', '正常', '1', '主动预销号', '2', '主动销号', '3', '欠费预销号', '4', '欠费销号', '5', '开户返销', '6', '过户注销') PARA_CODE11,
       b.remark
  from tf_fh_user a, tf_f_user_other b, tf_f_customer c
 WHERE a.serial_number = :SERIAL_NUMBER
   AND a.user_id = b.user_id
   AND a.cust_id = c.cust_id
   AND b.RSRV_VALUE_CODE = 'CHRN')
 ORDER BY para_code6 DESC
 
 
 