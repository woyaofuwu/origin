--IS_CACHE=Y
SELECT 'CredNameByKind' KEY,'R' VALUE1,res_kind_code VALUE2, kind_name VRESULT FROM td_s_reskind
 WHERE 'CredNameByKind'=:KEY