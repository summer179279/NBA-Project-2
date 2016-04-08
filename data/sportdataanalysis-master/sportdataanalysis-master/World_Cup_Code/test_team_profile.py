#coding:utf-8
import xmltodict;
import time
import httplib2
import json, io, sysconfig, locale, os
import sys
from pprint import pprint


def timer_second():
        now = time.localtime(time.time())
        return int(now[5])

def fetch(url,numb,country):
        http_header = {'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1',
        'Referer':'https://www.sportsdatallc.org/',
        'Host':'api.sportsdatallc.org'}

        doc_name = "/Users/kshen4/code/sportdataanalysis2/team_profile/" + str(numb) + "_" + str(country) + ".xml"

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

def pythonXmlToJson(url,numb,country):
    """
        demo Python xml to json
    """
    doc_name_xml = "/Users/kshen4/code/sportdataanalysis2/team_profile/" + str(numb) + "_" + str(country) + ".xml"
    doc_name_json = doc_name = "/Users/kshen4/code/sportdataanalysis2/team_profile_json/" + str(numb) + "_" + str(country) + ".json"
    f = open(doc_name_xml,"r", encoding='utf-8')
    print(f.encoding)
    # all_the_text = f.readlines()
    # print(all_the_text.encode('utf-8'))
    # sample = f.read(4)
    # print(sample)
    bin_data = f.read()

    print(type(bin_data))

    print(bin_data)
    convertedDict = xmltodict.parse(bin_data)
    jsonStr = json.dumps(convertedDict)
    print("jsonStr=", jsonStr)

    try:
            with open(doc_name_json, "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))

# make different game files
with open("/Users/kshen4/code/sportdataanalysis2/WC_schedule.json") as dig:
    data = json.load(dig)
#pprint(data)
dig.close()

# i = 0
# wanted_second = current_second = timer_second()
# print (wanted_second)
# print (current_second)

try:
        # while (current_second == wanted_second) & (i < len(data["schedule"]["matches"]["match"])):
        team_list = []
        team_name_list = []
        for i in range(len(data["schedule"]["matches"]["match"])):
                home_nation_id = data["schedule"]["matches"]["match"][i]["home"]["@id"]
                home_team_name = data["schedule"]["matches"]["match"][i]["home"]["@country"]
                if home_nation_id not in team_list:
                        team_list.append(home_nation_id)
                        team_name_list.append(home_team_name)

        print (team_list)
        print (team_name_list)
        for r in range(len(team_list)):
                nation_id = team_list[r]
                nation_name = team_name_list[r]
                url = "http://api.sportsdatallc.org/soccer-t2/wc/teams/" + str(nation_id) + "/profile.xml?api_key=ug2fudww7a67kcech2jhmemj"
                if __name__ == "__main__":
                    fetch(url, nation_id, nation_name)
                    pythonXmlToJson(url, nation_id, nation_name)
                time.sleep(2)

        # for i in range(len(data["schedule"]["matches"]["match"])):
        #         n = data["schedule"]["matches"]["match"][i]["@id"]
        #         home = data["schedule"]["matches"]["match"][i]["home"]["@country"]
        #         away = data["schedule"]["matches"]["match"][i]["away"]["@country"]
        #         date = data["schedule"]["matches"]["match"][i]["@scheduled"]
        #         url = "http://api.sportsdatallc.org/soccer-t2/wc/matches/" + str(n) + "/boxscore.xml?api_key=ug2fudww7a67kcech2jhmemj"
        #         print (str(url))
        #         if __name__ == "__main__":
        #             fetch(url, n, home, away, date)
                # i += 1
                # wanted_second += 2
                # if wanted_second == 61:
                #         wanted_second = 1
                # elif wanted_second == 62:
                #         wanted_second = 2
                # time.sleep(2)
                # print (current_second)
                # print (wanted_second)
                # print (timer_second())

except IOError as err:
        print ("File error: " + str(err))


# n = "dc3acf1b-dfb9-43bc-b7c9-f42626b66b51"
# url1 = "http://api.sportsdatallc.org/soccer-t2/wc/matches/" + str(n) + "/summary.xml?api_key=ug2fudww7a67kcech2jhmemj"

# if __name__ == "__main__":
#         fetch(url1, n)
