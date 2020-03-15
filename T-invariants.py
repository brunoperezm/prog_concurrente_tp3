import re
import numpy as np



string_pattern = "(.*)ARRIVAL_RATE(.*?)START_BUFFER_1(.*?)(?:START_SERVICE_1(.*?)(?:END_SERVICE_RATE_1(.*?)CONSUME_PENDING_TASK_TOKEN_1|CONSUME_PENDING_TASK_TOKEN_1(.*?)END_SERVICE_RATE_1)|CONSUME_PENDING_TASK_TOKEN_1(.*?)START_SERVICE_1(.*?)END_SERVICE_RATE_1)(.*)"
pattern = re.compile(string_pattern)



transitions_file = open("tests/test1.txt", "r").read()

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