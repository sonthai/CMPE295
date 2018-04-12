import sys
import string
print "Welcome to the parser for United."
airline = raw_input("What is the airline partner for United? ")
print "OK This entries are for '"+airline+"'"

# F: Suites & First, Mile Earnings, PQM, PQS
class_of_service = dict((e, []) for e in list(string.ascii_uppercase))
step = 1
prompt_question = "Award Miles Earning"
step2_question = "PQM, PQS"
while True:
    entry = raw_input(prompt_question)
    if entry is "n":
        step = 2
        prompt_question = step2_question
        continue
    elif entry is "q":
        step = 3
        continue
    if step == 1:
        data = entry.split("\t")
        classes = data[1].strip().split(",")
        for each_class in classes:
            array = class_of_service[each_class.strip()]
            array.append(data[0].strip())
            array.append(data[2].strip())
            class_of_service[each_class.strip()] = array
    elif step == 2:
        data = entry.split("\t")
        classes = data[1].split(",")
        print classes
        for each_class in classes:
            array = class_of_service[each_class.strip()]
            array.append(data[2].strip())
            array.append(data[3].strip())
    elif step == 3:
        filepath = open("plaintext.txt",”w”)
        print "The table for "+airline+" banking miles at United."
        print class_of_service
        print "\n"
        print "Class Of Service"
        keys = sorted(class_of_service.keys())
        for k in keys:
            sys.stdout.write(k+", ")
        print ""
        for k in keys:
            content = class_of_service[k][0]+", ";
            sys.stdout.write(content)
            filepath.write(content)
        print ""
        print ""
        for k in keys:
            content = class_of_service[k][1]+", "
            sys.stdout.write(content)
            filepath.write(content)

        print ""
        print ""
        for k in keys:
            content = class_of_service[k][2]+", "
            sys.stdout.write(content)
            filepath.write(content)
        print ""
        print ""
        for k in keys:
            content = class_of_service[k][3]+", "
            sys.stdout.write(content)
            filepath.write(content)
        print ""
        break
