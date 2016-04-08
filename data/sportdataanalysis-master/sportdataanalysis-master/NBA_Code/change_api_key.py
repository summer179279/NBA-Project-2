import json

def api_key_apply(key = "d228mz9pj9me2xr577u57qph", start_over = "N"):
    with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json") as dig5:
        data5 = json.load(dig5)
    dig5.close()
    i = 0
    while data5[i]["@key_id"] != key and i != len(data5)-1:
        i += 1
    if i == len(data5)-1 or data5[i]["@usage"] >= 1000:
        start_over = "Y"

    if start_over == "Y":
        i = 0
        while data5[i]["@usage"] >= 1000 and i != len(data5)-1:
            i += 1
        key = data5[i]["@key_id"]

    if i == len(data5)-1 and data5[i]["@usage"] >= 1000:
        print ("Running out of API KEY")
        return None
    else:
        data5[i]["@usage"] += 1
        if data5[i]["@usage"] == 1000:
            if i == len(data5)-1:
                with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
                    dig4.write(json.dumps(data5))
                print (key, data5[i]["@usage"])
                print ("Running out of API KEY")
                return None
            else:
                print (key, data5[(i)]["@usage"])
                i += 1
                key = data5[(i)]["@key_id"]

        with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
            dig4.write(json.dumps(data5))
        print (key, data5[i]["@usage"])
        return key


    with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
        dig4.write(json.dumps(data5))
    print (key, data5[i]["@usage"])


def fetch_team(url, numb, team, file_dirc, single):
        http_header = {'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1',
        'Referer':'https://www.sportsdatallc.org/',
        'Host':'api.sportsdatallc.org'}
        if single == "N":
            doc_name = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(numb) + "_" + str(team) + ".xml"
        elif single == "Y":
            doc_name = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(file_dirc) + ".xml"

        h = httplib2.Http('.cache')

        print ("Start downloading data....")
        response, content = h.request(url,headers = http_header)
        print ("Finish downloading data...")

        # print(response['-content-encoding']
        print(response['status'])
        str_content = content.decode('utf-8')
        print(str_content)
        try:
                file = open(doc_name,"w")
                file.write(str_content)
        except IOError as err:
                print('File error: ' + str(err))


url = "http://api.sportsdatallc.org/nba-t3/games/2014/reg/schedule.xml?api_key=" + api_key_apply()
print (url)

