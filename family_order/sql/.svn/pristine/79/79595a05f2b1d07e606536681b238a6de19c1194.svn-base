Select a.trade_id para_code1,
       '['||TO_CHAR(c.action_code)||']'||c.action_name para_code2,
       a.accept_date para_code3,
       decode(a.in_mode_code,'0','普通营业厅','1','客服接入','2','网上客服','3','网上营业厅','4','银行','5','短信平台','6','一级BOSS',7,'手机支付',9,'短信营销','A','触摸屏','B','自助打印机','C','多媒体','D','自助营业厅','F','电话开通','其它') para_code4,
       b.action_count para_code5,
       '' para_code6,'' para_code7,'' para_code8,'' para_code9, '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name 
From tf_bh_trade a,tf_b_trade_scoresub b,(SELECT action_code,action_name FROM td_b_score_action WHERE eparchy_code=:PARA_CODE2 OR eparchy_code='ZZZZ')c
Where a.user_id=:PARA_CODE1
And a.trade_type_code=330
And a.cancel_tag='0'
And a.accept_date>trunc(Sysdate)-15
And a.trade_id=b.trade_id
And b.action_code=c.action_code(+)
And b.action_code In(Select to_number(para_code1) From td_s_commpara Where param_attr=2900 And subsys_code='CSM' And (eparchy_code=:PARA_CODE2 OR  eparchy_code='ZZZZ'))
And a.trade_id Not In
(Select rsrv_str1 From tf_bh_trade Where user_id=:PARA_CODE1
 And cancel_tag='0' And trade_type_code=371)
AND :PARA_CODE3 IS NULL
AND :PARA_CODE4 IS NULL
AND :PARA_CODE5 IS NULL
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL