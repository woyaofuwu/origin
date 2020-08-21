SELECT f1 para_code1,f2 para_code2,f3 para_code3,f4 para_code4,f5 para_code5,f6 para_code6,
       f7 para_code7,f8 para_code8,f9 para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
       '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,
       '' param_name
FROM      
(SELECT '变更前' f1,cust_name f2,decode(sex,'M','男','F','女','其他') f3,b.folk f4,birthday f5,pspt_addr f6,post_address f7,pspt_id f8,pspt_end_date f9
  FROM tf_b_trade_pcust_bak a,td_s_folk b
WHERE a.trade_id=:PARA_CODE1
  AND a.accept_month = substr(:PARA_CODE1,5,2)
  AND b.folk_code=a.folk_code
UNION ALL
SELECT '变更后' f1,cust_name f2,decode(sex,'M','男','F','女','其他') f3,d.folk f4,birthday f5,pspt_addr f6,post_address f7,pspt_id f8,pspt_end_date f9
  FROM tf_f_cust_person c,td_s_folk d
WHERE (c.cust_id,c.partition_id)=(SELECT cust_id,MOD(cust_id,10000) FROM tf_b_trade_pcust_bak WHERE trade_id=:PARA_CODE1 AND accept_month = substr(:PARA_CODE1,5,2))
  AND d.folk_code=c.folk_code)
WHERE (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
  AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
  AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
  AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
  AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
  AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
  AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
  AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
  AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)