import re
import numpy as np
import datetime




print ("Fecha de ejecucion: "  + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

string_pattern = "(.*)ARRIVAL_RATE(.*?)(?:START_BUFFER_1(.*?)(?:START_SERVICE_1(.*?)(?:END_SERVICE_RATE_1(.*?)CONSUME_PENDING_TASK_TOKEN_1|CONSUME_PENDING_TASK_TOKEN_1(.*?)END_SERVICE_RATE_1)|CONSUME_PENDING_TASK_TOKEN_1(.*?)START_SERVICE_1(.*?)END_SERVICE_RATE_1|WAKE_UP_1(.*?)POWER_UP_DELAY_1(.*?)START_SERVICE_1(.*?)END_SERVICE_RATE_1(.*?)POWER_DOWN_THRESHOLD_1)|START_BUFFER_2(.*?)(?:START_SERVICE_2(.*?)(?:END_SERVICE_RATE_2(.*?)CONSUME_PENDING_TASK_TOKEN_2|CONSUME_PENDING_TASK_TOKEN_2(.*?)END_SERVICE_RATE_2)|CONSUME_PENDING_TASK_TOKEN_2(.*?)START_SERVICE_2(.*?)END_SERVICE_RATE_2|WAKE_UP_2(.*?)POWER_UP_DELAY_2(.*?)START_SERVICE_2(.*?)END_SERVICE_RATE_2(.*?)POWER_DOWN_THRESHOLD_2))(.*)"
pattern = re.compile(string_pattern)



transitions_file = open("out/transitions.txt", "r").read()

no_of_matches = 0

resultado_de_transiciones = transitions_file


while True:
    nuevo_resultado = ""
    match = pattern.match(resultado_de_transiciones)
    if not match:
        break
    for group in match.groups():
        if group is not None: nuevo_resultado += group
    resultado_de_transiciones = nuevo_resultado
    no_of_matches += 1





print ("El resultado de reemplazar los grupos es: " + resultado_de_transiciones + "\n")
print ("La cantidad de invariantes eliminadas fue: " + str(no_of_matches))