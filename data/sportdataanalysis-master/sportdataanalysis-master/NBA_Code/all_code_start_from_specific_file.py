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


def fetch(url,numb,home,away,time,file_dirc,single):
        http_header = {'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1',
        'Referer':'https://www.sportsdatallc.org/',
        'Host':'api.sportsdatallc.org'}

        if single == "N":
            doc_name = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".xml"
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


def pythonXmlToJson(url,numb,home,away,time,file_dirc,single):
    """
        demo Python xml to json
    """
    if single == "Y":
        doc_name_xml = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(file_dirc) + ".xml"
        doc_name_json = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "_json/" + str(file_dirc) + ".json"
    elif single == "N":
        doc_name_xml = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".xml"
        doc_name_json = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "_json/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".json"


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
    print(doc_name_json)

    try:
            with open(doc_name_json, "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))


def pythonXmlToJson_team(url, numb, team, file_dirc, single):
    """
        demo Python xml to json
    """
    if single == "Y":
        doc_name_xml = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(file_dirc) + ".xml"
        doc_name_json = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "_json/" + str(file_dirc) + ".json"
    elif single == "N":
        doc_name_xml = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "/" + str(numb) + "_" + str(team) + ".xml"
        doc_name_json = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/" + str(file_dirc) + "_json/" + str(numb) + "_" + str(team) + ".json"

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
    print (doc_name_json)

    try:
            with open(doc_name_json, "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))







with open('/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/Schedule_json/Schedule.json') as dig:
    data = json.load(dig)
#pprint(data)
dig.close()


team_list = []
team_name_list = []
for j in range(len(data["league"]["season-schedule"]["games"]["game"])):
    home = data["league"]["season-schedule"]["games"]["game"][j]["home"]["@alias"]
    home_id = data["league"]["season-schedule"]["games"]["game"][j]["home"]["@id"]
    away = data["league"]["season-schedule"]["games"]["game"][j]["away"]["@alias"]
    away_id = data["league"]["season-schedule"]["games"]["game"][j]["away"]["@id"]
    if home_id not in team_list:
        team_list.append(home_id)
        team_name_list.append(home)
    if home_id not in team_list:
        team_list.append(home_id)
        team_name_list.append(home)
    if "WEST" in team_name_list:
        team_name_list.remove("WEST")
        del team_list[30]




# download = ["Game_Boxscore", "Standings", "Rankings", "League_Hierarchy", "Team_Profile", "Player_Profile", "Injuries", "Game_Summary", "Play_by_play", "Seasonal_Statistics", "Daily_Change", "Daily_Transfer"]
download = ["Play_by_play"]
print (download)
print ('\n')

# if if_start_over = "N":
#     if if_single_document = "single":
#         Good4 = True
#         while Good4:
index_1 = True
index_2 = True
while index_1:
    start_over_1 = input("You want to start from begining? (Y/N): ")
    if start_over_1 == "Y":
        index_2 = False
        ask = "Game_Boxscore"
    if start_over_1 == "Y" or start_over_1 == "N":
        index_1 = False

if index_2 == False:
    file_dirctory = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/Game_Boxscore_json/0b3d21c7-c13f-4ee8-8d9d-4f334754c7e4_IND_ORL_2013-10-29T23/00/00+00/00.json"

Good_3 = True
while Good_3 & index_2:
    file_dirctory = input("Please enter the file directory you want to download:")
    if file_dirctory[:54] == "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/":
        for item in download:
            if file_dirctory[54:].find(item) != -1:
                ask = item
                Good_3 = False
                index_2 = False


download_index = download.index(ask)
for thing in range(download_index):
    del download[0]
print (download)

great_index = file_dirctory[54:]
flash_index_1 = great_index.find("/")
flash_index_1 += 1
flash_index_2 = great_index[flash_index_1:].find("_")
flash_index_2 += flash_index_1
match_start_id = great_index[flash_index_1:flash_index_2]

if ask in ["Game_Boxscore", "Game_Summary", "Play_by_play"]:
    print (match_start_id)
    start_number = 0
    while data["league"]["season-schedule"]["games"]["game"][start_number]["@id"] != match_start_id:
        start_number += 1
    print (start_number)

elif ask in ["Daily_Change", "Daily_Transfer"]:
    flash_index_2 = flash_index_1 + 2
    start_number = int(great_index[flash_index_1: flash_index_2])
    print (start_number)

elif ask in ["Team_Profile", "Seasonal_Statistics"]:
    print (match_start_id)

elif ask == "Player_Profile":
    print (match_start_id)
    with open(file_dirctory) as dig9:
        data_temp = json.load(dig9)
    start_team_id = data_temp["player"]["team"]["@id"]












try:


    for item in download:
        if item == "Team_Profile" or item == "Seasonal_Statistics":
            single = "N"
            found_tm_id = False
            for r in range(len(team_list)):
                tm_id = team_list[r]
                tm = team_name_list[r]
                if __name__ == "__main__":
                    if tm_id == match_start_id and ask in ["Seasonal_Statistics", "Team_Profile"]:
                        found_tm_id = True

                    if found_tm_id == False and ask in ["Seasonal_Statistics", "Team_Profile"]:
                        pass

                    else:
                        key = api_key_apply()
                        if item == "Team_Profile":
                            url = "http://api.sportsdatallc.org/nba-t3/teams/" + str(tm_id) + "/profile.xml?api_key=" + str(key)
                        elif item == "Seasonal_Statistics":
                            url = "http://api.sportsdatallc.org/nba-t3/seasontd/2013/reg/teams/" + str(tm_id) + "/statistics.xml?api_key=" + str(key)
                        fetch_team(url, tm_id, tm, item, single)
                        pythonXmlToJson_team(url, tm_id, tm, item, single)
                        time.sleep(1)



        elif item == "Player_Profile":
            single = "N"
            found_player_id = False
            found_team_id = False
            for r in range(len(team_list)):
                tm_id = team_list[r]
                tm = team_name_list[r]
                if start_team_id == tm_id:
                    found_team_id = True
                if found_team_id == False and ask == "Player_Profile":
                    pass
                else:
                    document_name = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/Team_Profile_json/" + str(tm_id) + "_" + str(tm) + ".json"
                    with open(document_name) as dig2:
                        data2 = json.load(dig2)
                    dig2.close()
                    for h in range(len(data2["team"]["players"]["player"])):
                        player_id = data2["team"]["players"]["player"][h]["@id"]
                        player_name = data2["team"]["players"]["player"][h]["@full_name"]
                        if __name__ == "__main__":
                            if match_start_id == player_id and  ask == "Player_Profile":
                                found_player_id = True
                            if found_player_id == False and ask == "Player_Profile":
                                pass

                            else:
                                key = api_key_apply()
                                url = "http://api.sportsdatallc.org/nba-t3/players/" + str(player_id) + "/profile.xml?api_key=" + str(key)
                                fetch_team(url, player_id, player_name, item, single)
                                pythonXmlToJson_team(url, player_id, player_name, item, single)
                                time.sleep(1)




        elif item == "Daily_Change" or item == "Daily_Transfer":
            single = "N"
            if ask not in ["Daily_Change", "Daily_Transfer"]:
                start_number = 1
            month_list = {10 : 31, 11 : 30, 12 : 31, 1 : 31, 2 : 28, 3 : 31, 4 : 17, 5 : 0, 6 : 0, 7 : 0, 8 : 0, 9 : 0}
            for m in range(start_number, 13):
                for d in range(month_list.get(m)):
                    d_1 = d+1
                    d_s = str(d_1)
                    m_s = str(m)
                    if len(d_s) == 1:
                        d_s = "0" + d_s
                    if len(m_s) == 1:
                        m_s = "0" + m_s
                    m_d = m_s + "_" + d_s
                    key = api_key_apply()

                    if m in [10, 11, 12]:
                        y_s = "2013"
                    else:
                        y_s = "2014"

                    if item == "Daily_Change":
                        url = "http://api.sportsdatallc.org/nba-t3/league/" + str(y_s) + "/" + str(m_s) + "/" + str(d_s) + "/changes.xml?api_key=" + str(key)
                    elif item == "Daily_Transfer":
                        url = "http://api.sportsdatallc.org/nba-t3/league/" + str(y_s) + "/" + str(m_s) + "/" + str(d_s) + "/transfers.xml?api_key=" + str(key)
                    if __name__ == "__main__":
                        fetch_team(url, m_d, y_s, item, single)
                        pythonXmlToJson_team(url, m_d, y_s, item, single)
                        time.sleep(1)


        elif item in ["Standings", "Rankings", "League_Hierarchy", "Injuries"]:
            single = "Y"
            key = api_key_apply()
            if item == "Standings":
                url = "http://api.sportsdatallc.org/nba-t3/seasontd/2013/reg/standings.xml?api_key=" + str(key)
            elif item == "Rankings":
                url = "http://api.sportsdatallc.org/nba-t3/seasontd/2013/reg/rankings.xml?api_key=" + str(key)
            elif item == "League_Hierarchy":
                url = "http://api.sportsdatallc.org/nba-t3/league/hierarchy.xml?api_key=" + str(key)
            elif item == "Injuries":
                url = "http://api.sportsdatallc.org/nba-t3/league/injuries.xml?api_key=" + str(key)
            print (str(url))
            if __name__ == "__main__":
                fetch_team(url, "", item, item, single)
                pythonXmlToJson_team(url, "", item, item, single)
                time.sleep(1)






        elif item in ["Game_Boxscore", "Game_Summary", "Play_by_play"]:
            single = "N"
            if ask not in ["Game_Boxscore", "Game_Summary", "Play_by_play"]:
                start_number = 0

            for i in range(start_number,len(data["league"]["season-schedule"]["games"]["game"])):
                game_id = data["league"]["season-schedule"]["games"]["game"][i]["@id"]
                home = data["league"]["season-schedule"]["games"]["game"][i]["home"]["@alias"]
                home_id = data["league"]["season-schedule"]["games"]["game"][i]["home"]["@id"]
                away = data["league"]["season-schedule"]["games"]["game"][i]["away"]["@alias"]
                away_id = data["league"]["season-schedule"]["games"]["game"][i]["away"]["@id"]
                date = data["league"]["season-schedule"]["games"]["game"][i]["@scheduled"]

                key = api_key_apply()

                if item == "Game_Boxscore":
                    url = "http://api.sportsdatallc.org/nba-t3/games/" + str(game_id) + "/boxscore.xml?api_key=" + str(key)
                elif item == "Game_Summary":
                    url = "http://api.sportsdatallc.org/nba-t3/games/" + str(game_id) + "/summary.xml?api_key=" + str(key)
                elif item == "Play_by_play":
                    url = "http://api.sportsdatallc.org/nba-t3/games/" + str(game_id) + "/pbp.xml?api_key=" + str(key)

                if __name__ == "__main__":
                    fetch(url, game_id, home, away, date, item, single)
                    pythonXmlToJson(url, game_id, home, away, date, item, single)
                    time.sleep(1)



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
