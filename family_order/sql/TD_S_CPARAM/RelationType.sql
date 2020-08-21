--IS_CACHE=Y
SELECT 'RelationType' KEY,relation_type_code VALUE1,'-1' VALUE2,relation_type_name VRESULT
FROM TD_S_RELATION WHERE 'RelationType'=:KEY