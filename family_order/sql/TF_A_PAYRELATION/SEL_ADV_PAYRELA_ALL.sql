SELECT  :SERIAL_NUMBER serial_number_a,d.serial_number serial_number_b,
		a.payitem_code payitem_code,
		a.start_cycle_id start_cycle_id,
		a.end_cycle_id end_cycle_id,
		decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') limit_type,
		to_number(nvl(a.LIMIT,0))/100.0 limit,
		decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag,
		:EPARCHY_CODE eparchy_code
  FROM  tf_a_payrelation a,tf_f_user d
 WHERE  a.user_id!=:USER_ID
   AND  a.acct_id=:ACCT_ID
   AND  a.act_tag='1' AND a.default_tag='0'
   AND  (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) 
                                   BETWEEN a.start_cycle_id 
                                           AND a.end_cycle_id))
   AND  a.start_cycle_id<=a.end_cycle_id
   AND  d.user_id=a.user_id
UNION
SELECT  f_csb_getaccountserialnumber(a.acct_id) serial_number_a,
	:SERIAL_NUMBER serial_number_b,a.payitem_code payitem_code,
	a.start_cycle_id start_cycle_id,
	a.end_cycle_id end_cycle_id,
	decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') limit_type,
	to_number(nvl(a.LIMIT,0))/100.0 limit,
	decode(a.complement_tag,'0','不补足','1','补足','其他') complement_tag,
	:EPARCHY_CODE eparchy_code
  FROM tf_a_payrelation a
 WHERE a.user_id=:USER_ID
   AND a.acct_id!=:ACCT_ID
   AND a.act_tag='1' AND a.default_tag='0'
   AND (:PAYRELATION_QUERY_TYPE='1' OR (to_number(to_char(sysdate, 'yyyymmdd')) 
                                   BETWEEN a.start_cycle_id 
                                           AND a.end_cycle_id))
   AND a.start_cycle_id<=a.end_cycle_id
 ORDER BY payitem_code