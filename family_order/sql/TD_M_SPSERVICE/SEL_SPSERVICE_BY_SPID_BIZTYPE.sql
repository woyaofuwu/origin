--IS_CACHE=Y
SELECT SP_ID,SP_SVC_ID,BIZ_TYPE,BIZ_DESC FROM TD_M_SPSERVICE WHERE trim(SP_ID) = :SP_ID AND trim(BIZ_TYPE_CODE)=:BIZ_TYPE_CODE AND BIZ_STATUS<>'S'