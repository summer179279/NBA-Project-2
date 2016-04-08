import json
key =[
{"@key_id": "d228mz9pj9me2xr577u57qph", "@usage": 1},
{"@key_id": "u8jcprmtaywka2ta47rgwseu", "@usage": 1},



]

with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
    dig4.write(json.dumps(key))
