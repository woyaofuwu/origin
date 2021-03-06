--IS_CACHE=Y
select a.res_id,a.res_name,a.eparchy_code,a.res_type_code,a.res_kind_code,a.card_kind_code,b.card_kind_name,a.brand_code,a.model_code,a.class_id,a.use_tag,a.score_code,a.remark
 from td_m_resgift a,td_s_cardkind b
 where a.eparchy_code=b.eparchy_code
 and a.eparchy_code=:EPARCHY_CODE
 and a.res_type_code=:RES_TYPE_CODE
 and a.res_type_code=b.res_type_code
 and a.res_kind_code=b.res_kind_code
 and a.card_kind_code=b.card_kind_code
 and (:RES_ID is null or a.res_id=:RES_ID)
 and a.valid_tag='0'