SELECT  sum(decode(a.integral_type_code,'k',a.integral_fee,0)) para_code1,--在网奖励积分
        sum(decode(a.integral_type_code,'l',a.integral_fee,0)) para_code2,--特别消费奖励积分 
        sum(decode(a.integral_type_code,'m',a.integral_fee,0)) para_code3,--增值业务奖励积分 
        sum(decode(a.integral_type_code,'n',a.integral_fee,0)) para_code4,--新入网奖励积分 
        sum(decode(a.integral_type_code,'e',a.integral_fee,0)) para_code5,--服务奖励积分
        sum(decode(a.integral_type_code,'f',a.integral_fee,0)) para_code6,--积分兑换参与奖励积分
        sum(decode(a.integral_type_code,'g',a.integral_fee,0)) para_code7,--购机奖励积分
        sum(decode(a.integral_type_code,'h',a.integral_fee,0)) para_code8,--信用奖励积分
        sum(decode(a.integral_type_code,'i',a.integral_fee,0)) para_code9,--公益奖励积分
        sum(decode(a.integral_type_code,'j',a.integral_fee,0)) para_code10,--预存奖励积分
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
        sum(decode(a.integral_type_code,'D',a.integral_fee,0)) para_code21,--消费积分
        sum(decode(a.integral_type_code,'5',a.integral_fee,0)) para_code22,--总奖励积分,
       '' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,
       sum(a.integral_fee) para_code30,--总新增积分,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,
       '' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_ah_integralbill a
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.acyc_id=:ACYC_ID
   AND a.integral_fee > 0
   AND exists(select 1 from td_s_scoretype c
               where a.integral_type_code = c.SCORE_TYPE_CODE)