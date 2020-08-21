--IS_CACHE=Y
select b.rule_biz_id,
        t.rule_check_mode,
        t.tips_type,
        t.tips_info,
        b.right_code,
        r.rule_id,
        r.script_id,
        r.is_revolt,
        r.rule_description,
        d.script_id,
        d.script_path
   from td_bre_business_saleactive b, td_bre_relation r, td_bre_definition d,td_bre_rule_manage t 
  where b.rule_biz_id = r.rule_biz_id
  and b.rule_biz_id = t.rule_biz_id
  and r.rule_biz_id=t.rule_biz_id
    and r.script_id = d.script_id
    and sysdate between b.start_date and b.end_date
    and sysdate between r.start_date and r.end_date
    and (b.trade_type_code = -1 or b.trade_type_code = :TRADE_TYPE_CODE)
    and b.action_type =:ACTION_TYPE
    and (b.action_id is null or b.action_id = :ACTION_ID)
    and (b.campn_type = -1 or b.CAMPN_TYPE = :CAMPN_TYPE)
    and (b.product_id = -1 or b.PRODUCT_ID = :PRODUCT_ID)
    and (b.package_id = -1 or b.PACKAGE_ID = :PACKAGE_ID)
    and (b.campn_id = -1 or b.CAMPN_ID = :CAMPN_ID)
    and (b.ELEMENT_ID = -1 or b.ELEMENT_ID = :ELEMENT_ID)   
    and (b.ELEMENT_TYPE = -1 or b.ELEMENT_TYPE = :ELEMENT_TYPE)      
    and t.rule_twig_code = :RULE_BIZ_TWIG_CODE
    AND (INSTR(:TIPS_TYPE, t.tips_type) > 0)    
    and (b.eparchy_code = 'ZZZZ' or b.eparchy_code = :EPARCHY_CODE)
    and (b.in_mode_code is null or instr(b.in_mode_code, :IN_MODE_CODE) > 0)
    and (b.spec_cdtion is null and
        instr(nvl(b.spec_cdtion, '-'), :TRADE_TYPE_CODE) < 1)
  order by b.rule_biz_id, r.execute_order