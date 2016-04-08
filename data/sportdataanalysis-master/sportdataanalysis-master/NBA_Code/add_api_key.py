import json

def add_del_key():


    with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json") as dig5:
        data5 = json.load(dig5)
    dig5.close()

    print (data5)

    index = True
    while index:
        ask = input("Add or Delete API-KEY? (Add/Del): ")
        if ask in ["Add", "Del"]:
            index = False

    if ask == "Add":
        repeat = True
        while repeat:
            index_2 = True
            while index_2:
                ask_add = input("Please enter the API-KEY you wann to add (or type \"quit\" for quit): ")
                if len(ask_add) == 24:
                    index_2 = False
                elif ask_add == "quit":
                    repeat = False
                    break
            for i in range(len(data5)):
                if ask_add == data5[i]["@key_id"]:
                    exist = True
                    print ("API-KEY is already in the list")
                else:
                    exist = False
            if ask_add != "quit" and exist == False:
                index_3 = True
                while index_3:
                    ask_add_number = input("Usage (0 - 1000): ")
                    ask_add_number = int(ask_add_number)
                    if ask_add_number >= 0 and ask_add_number <= 1000:
                        index_3 = False
                data5.append({"@key_id": str(ask_add), "@usage": int(ask_add_number)})
                print ("Succeed")
                print (data5)
                with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
                    dig4.write(json.dumps(data5))


    elif ask == "Del":
        index_4 = True
        while index_4:
            ask_del = input("Please enter the API-KEY you wann to delete (or type \"quit\" for quit): ")
            if len(ask_del) == 24:
                index_4 = False
            elif ask_del == "quit":
                break
        if ask_del != "quit":
            it = 0
            while ask_del != data5[it]["@key_id"] and it != len(data5)-1:
                    it += 1
            if it == len(data5)-1 and ask_del != data5[it]["@key_id"]:
                print ("Not Found")
            else:
                del data5[it]
                print ("Succeed")
                print (data5)
            with open ("/Users/kshen4/code/sportdataanalysis/NBA_Code/API-KEY.json", "w") as dig4:
                dig4.write(json.dumps(data5))



index_8 = True
while index_8:
    add_del_key()
    ask = input("Continues?: (Y/N)")
    if ask != "Y":
        index_8 = False

