SELECT c.EPARCHY_CODE,
c.ACCT_ID,
c.CUST_ID,
c.PAY_NAME,
c.PAY_MODE_CODE,
c.BANK_CODE,
c.BANK_ACCT_NO,
c.SCORE_VALUE,
c.BASIC_CREDIT_VALUE,
c.CREDIT_VALUE,
c.DEBUTY_USER_ID,
c.DEBUTY_CODE,
c.CONTRACT_NO,
c.DEPOSIT_PRIOR_RULE_ID,
c.ITEM_PRIOR_RULE_ID,
c.REMOVE_TAG,
c.OPEN_DATE,
c.REMOVE_DATE,
c.RSRV_STR1,
c.RSRV_STR2,
c.RSRV_STR3,
c.RSRV_STR4,
c.RSRV_STR5,
c.RSRV_STR6,
c.RSRV_STR7,
c.RSRV_STR8,
c.RSRV_STR9,
c.RSRV_STR10,
c.UPDATE_TIME,
c.CREDIT_CLASS_ID,
c.UPDATE_STAFF_ID,
c.UPDATE_DEPART_ID,
b.USER_ID,
b.PAYITEM_CODE,
b.ACCT_PRIORITY,
b.USER_PRIORITY,
b.BIND_TYPE,
b.START_CYCLE_ID,
b.END_CYCLE_ID,
b.DEFAULT_TAG,
b.ACT_TAG,
b.LIMIT_TYPE,
b.COMPLEMENT_TAG
  FROM tf_f_user a,tf_a_payrelation b,tf_f_account c
 WHERE a.serial_number = :SERIAL_NUMBER
   AND a.remove_tag||NULL != '0'
   AND a.destroy_time = (SELECT MAX(destroy_time) FROM tf_f_user WHERE serial_number = :SERIAL_NUMBER AND remove_tag||NULL != '0')
   AND a.user_id+0 = b.user_id
   AND a.partition_id = b.partition_id
   AND b.act_tag = '1'
   AND b.default_tag = '1'
   AND b.payitem_code = -1
   AND TO_NUMBER(TO_CHAR(a.destroy_time,'yyyymm')) BETWEEN b.start_cycle_id AND b.end_cycle_id
   AND b.acct_id+0 = c.acct_id
   AND MOD(b.acct_id,10000) = c.partition_id