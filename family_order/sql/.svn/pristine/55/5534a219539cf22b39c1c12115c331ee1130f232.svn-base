--IS_CACHE=Y
select b.rule_biz_id,
        t.rule_check_mode,
        t.tips_type,
        t.tips_info,
        b.right_code,
	b.brand_code,
	b.in_mode_code,
        r.rule_id,
        r.script_id,
        r.is_revolt,
        r.rule_description,
        d.script_id,
        d.script_path
   from td_bre_business_base b, td_bre_relation r, td_bre_definition d,td_bre_rule_manage t 
  where b.rule_biz_id = r.rule_biz_id
  and b.rule_biz_id = t.rule_biz_id
  and r.rule_biz_id=t.rule_biz_id
    and r.script_id = d.script_id
    and sysdate between t.start_date and t.end_date
    and sysdate between b.start_date and b.end_date
    and sysdate between r.start_date and r.end_date
    and (b.trade_type_code = -1 or b.trade_type_code = :TRADE_TYPE_CODE)
    and (b.product_id = -1 or b.product_id = :PRODUCT_ID)
    and (b.RULE_BIZ_KIND_CODE is null or b.rule_biz_kind_code = :RULE_BIZ_KIND_CODE)
    and b.action_type =:ACTION_TYPE
    and (:ACTION_ID is null or b.action_id = :ACTION_ID or b.action_id is null or :ACTION_ID =-1)
    and t.rule_twig_code = :RULE_BIZ_TWIG_CODE
    AND (INSTR(:TIPS_TYPE, t.tips_type) > 0)    
    and (b.eparchy_code = 'ZZZZ' or b.eparchy_code = :EPARCHY_CODE)
    and (b.spec_cdtion is null or
        instr(nvl(b.spec_cdtion, '-'), :TRADE_TYPE_CODE) < 1)
  order by b.rule_biz_id, r.execute_order