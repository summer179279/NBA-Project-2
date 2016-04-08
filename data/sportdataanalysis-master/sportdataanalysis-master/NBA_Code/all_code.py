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

    try:
            with open(doc_name_json, "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))




# make different game files
with open('/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/Schedule_json/Schedule.json') as dig:
    data = json.load(dig)
#pprint(data)
dig.close()

# i = 0
# wanted_second = current_second = timer_second()
# print (wanted_second)
# print (current_second)

download = ["Game_Boxscore", "Standings", "Rankings", "League_Hierarchy", "Team_Profile", "Player_Profile", "Injuries", "Game_Summary", "Play_by_play", "Seasonal_Statistics", "Daily_Change", "Daily_Transfer"]

api_key = ["zd2jzy692szrht42y8ausdwg", "pu4wstfufst3jnmndnaza2xy", "f73zru3z3rranm82am6zfcmn", "8c477fsjre4cva3v82k68uvp", "knrbfr7hdstrmutvy9xjxaz6", "hx6kxn72gcd27hgabrcrgcx5", "kyzz73ecrpz6nbc8ytnhpagf", "d2q8cdrzq45tdqt3s9ynzg5e", "u8jcprmtaywka2ta47rgwseu", "d228mz9pj9me2xr577u57qph", "9vnzpnprb44gqpgdsfwenyws", "smbx6w36mgtz9x7w7r6rptp6", "8zrcma92awsgs43z2vn27f4u", "wa47fsysucf9mbqxtyzmftew", "trctsrdgxfbvcq4uhymeg5ag", "q9c8ugs5bgjteec563k9c4qf", "8y9w6se9krtx99qesr7y384t"]
key_counter1 = 0
key_counter2 = 0
key = api_key[0]

try:

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

    print (team_list)
    print (team_name_list)



    for item in download:
        if item == "Team_Profile" or item == "Seasonal_Statistics":
            single = "N"
            for r in range(len(team_list)):
                tm_id = team_list[r]
                tm = team_name_list[r]
                if item == "Team_Profile":
                    url = "http://api.sportsdatallc.org/nba-t3/teams/" + str(tm_id) + "/profile.xml?api_key=" + str(key)
                elif item == "Seasonal_Statistics":
                    url = "http://api.sportsdatallc.org/nba-t3/seasontd/2013/reg/teams/" + str(tm_id) + "/statistics.xml?api_key=" + str(key)
                if __name__ == "__main__":
                    fetch_team(url, tm_id, tm, item, single)
                    pythonXmlToJson_team(url, tm_id, tm, item, single)
                time.sleep(1)



                key_counter1 += 1
                if key_counter1 >= 950:
                    key_counter2 += 1
                    key = api_key[key_counter2]
                    key_counter1 = 0
                print (str(key) + " " + str(key_counter1))

        elif item == "Player_Profile":
            single = "N"
            for r in range(len(team_list)):
                tm_id = team_list[r]
                tm = team_name_list[r]
                document_name = "/Users/kshen4/code/sportdataanalysis/NBA_Data/Regular/Team_Profile_json/" + str(tm_id) + "_" + str(tm) + ".json"
                with open(document_name) as dig2:
                    data2 = json.load(dig2)
                dig2.close()
                for h in range(len(data2["team"]["players"]["player"])):
                    player_id = data2["team"]["players"]["player"][h]["@id"]
                    player_name = data2["team"]["players"]["player"][h]["@full_name"]
                    url = "http://api.sportsdatallc.org/nba-t3/players/" + str(player_id) + "/profile.xml?api_key=" + str(key)
                    if __name__ == "__main__":
                        fetch_team(url, player_id, player_name, item, single)
                        pythonXmlToJson_team(url, player_id, player_name, item, single)
                    time.sleep(1)



                    key_counter1 += 1
                    if key_counter1 >= 950:
                        key_counter2 += 1
                        key = api_key[key_counter2]
                        key_counter1 = 0
                    print (str(key) + " " + str(key_counter1))



        elif item == "Daily_Change" or item == "Daily_Transfer":
            single = "N"
            month_list = {10 : 31, 11 : 30, 12 : 31, 1 : 31, 2 : 28, 3 : 31, 4 : 17}
            for m in [10,11,12,1,2,3,4]:
                for d in range(month_list.get(m)):
                    d_1 = d+1
                    d_s = str(d_1)
                    m_s = str(m)
                    if len(d_s) == 1:
                        d_s = "0" + d_s
                    if len(m_s) == 1:
                        m_s = "0" + m_s
                    m_d = m_s + "_" + d_s

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

                    key_counter1 += 1
                    if key_counter1 >= 950:
                        key_counter2 += 1
                        key = api_key[key_counter2]
                        key_counter1 = 0
                    print (str(key) + " " + str(key_counter1))

        elif item in ["Standings", "Rankings", "League_Hierarchy", "Injuries"]:
            single = "Y"
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

            key_counter1 += 1
            if key_counter1 >= 950:
                key_counter2 += 1
                key = api_key[key_counter2]
                key_counter1 = 0
            print (str(key) + " " + str(key_counter1))





        elif item in ["Game_Boxscore", "Game_Summary", "Play_by_play"]:
            single = "N"
            for i in range(len(data["league"]["season-schedule"]["games"]["game"])):
                game_id = data["league"]["season-schedule"]["games"]["game"][i]["@id"]
                home = data["league"]["season-schedule"]["games"]["game"][i]["home"]["@alias"]
                home_id = data["league"]["season-schedule"]["games"]["game"][i]["home"]["@id"]
                away = data["league"]["season-schedule"]["games"]["game"][i]["away"]["@alias"]
                away_id = data["league"]["season-schedule"]["games"]["game"][i]["away"]["@id"]
                date = data["league"]["season-schedule"]["games"]["game"][i]["@scheduled"]

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

                key_counter2 += 1
                key = api_key[key_counter2]
                # key_counter1 += 1
                # if key_counter1 >= 950:
                #     key_counter2 += 1
                #     key = api_key[key_counter2]
                #     key_counter1 = 0
                print (str(key) + " " + str(key_counter1))

                # i += 1
                # wanted_second += 2
                # if wanted_second == 61:
                #         wanted_second = 1
                # elif wanted_second == 62:
                #         wanted_second = 2
                # time.sleep(1)
                # print (current_second)
                # print (wanted_second)
                # print (timer_second())

except IOError as err:
        print ("File error: " + str(err))


# n = "dc3acf1b-dfb9-43bc-b7c9-f42626b66b51"
# url1 = "http://api.sportsdatallc.org/soccer-t2/wc/matches/" + str(n) + "/summary.xml?api_key=ug2fudww7a67kcech2jhmemj"

# if __name__ == "__main__":
#         fetch(url1, n)
