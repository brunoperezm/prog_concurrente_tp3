import re
import numpy as np


patterns = [
    "ARRIVAL_RATE,(([A-Z_0-9]*,)*?)START_BUFFER_1,(([A-Z_0-9]*,)*?)START_SERVICE_1,(([A-Z_0-9]*,)*?)END_SERVICE_RATE_1,(([A-Z_0-9]*,)*?)ZT19,(([A-Z_0-9]*,)*?)CONSUME_PENDING_TASK_TOKEN_1,(([A-Z_0-9]*,)*?)ZT17",
    #"ARRIVAL_RATE,(([A-Z_0-9]*,)*?)START_BUFFER_1,(([A-Z_0-9]*,)*?)WAKE_UP_1,(([A-Z_0-9]*,)*?)RETURN_PENDING_TASK_1,(([A-Z_0-9]*,)*?)POWER_UP_DELAY_1,(([A-Z_0-9]*,)*?)START_SERVICE_1,(([A-Z_0-9]*,)*?)ZT19,(([A-Z_0-9]*,)*?)END_SERVICE_RATE_1,(([A-Z_0-9]*,)*?)POWER_DOWN_THRESHOLD_1",
    "ARRIVAL_RATE,(([A-Z_0-9]*,)*?)START_BUFFER_2,(([A-Z_0-9]*,)*?)START_SERVICE_2,(([A-Z_0-9]*,)*?)END_SERVICE_RATE_2,(([A-Z_0-9]*,)*?)ZT20,(([A-Z_0-9]*,)*?)CONSUME_PENDING_TASK_TOKEN_2,(([A-Z_0-9]*,)*?)ZT18"
    #"ARRIVAL_RATE,(([A-Z_0-9]*,)*?)START_BUFFER_2,(([A-Z_0-9]*,)*?)WAKE_UP_2,(([A-Z_0-9]*,)*?)RETURN_PENDING_TASK_2,(([A-Z_0-9]*,)*?)POWER_UP_DELAY_2,(([A-Z_0-9]*,)*?)START_SERVICE_2,(([A-Z_0-9]*,)*?)ZT20,(([A-Z_0-9]*,)*?)END_SERVICE_RATE_2,(([A-Z_0-9]*,)*?)POWER_DOWN_THRESHOLD_2"
]
file = open("out/transitions.txt", "r").read()
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
