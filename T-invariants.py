import re
import numpy as np



pattern = "ARRIVAL_RATE,(.*?)START_BUFFER_1,(.*?)(?:START_SERVICE_1,(.*?)(?:END_SERVICE_RATE_1,(.*?)CONSUME_PENDING_TASK_TOKEN_1|CONSUME_PENDING_TASK_TOKEN_1,(.*?)END_SERVICE_RATE_1)|CONSUME_PENDING_TASK_TOKEN_1,(.*?)START_SERVICE_1,(.*?)END_SERVICE_RATE_1)"


transitions_file = open("out/transitions.txt", "r").read()

# Intento 1 match, no_of_matches = re.subn(pattern, '\g<2>', transitions_file)

resultado = ""
no_of_matches = 0

match = re.match(pattern,transitions_file)
while (match):
    for group in match.groups():
        if group is not None:
            resultado += group
    no_of_matches += 1
    match = re.match(pattern,resultado)


print ("El resultado de reemplazar los grupos es: " + resultado + "\n")
print ("La cantidad de matches fue: " + str(no_of_matches))
"""
for x in range(1, 1500):
    print(x)
    match_starts = []
    matches = []

    for pattern in patterns:
        match = re.search(pattern, file)
        if (match):
            matches.append(match)
            match_starts.append(match.start())
            print(match.start(), " - ", match.end())
    if (matches != []):
        write_file = open("out/invariants.txt", "w+")
        match_starts = np.array(match_starts)

        selected_match = matches[match_starts.argmin()]
        part_to_replace = ""

        for g in selected_match.groups():
            if g is not None:
                part_to_replace += g

        file = file[0:selected_match.start()] + part_to_replace + \
            file[selected_match.end():]
        write_file.write(file)
        write_file.close()
    else:
        break
"""