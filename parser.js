$(document).ready(function () {
    let display = $('#parsed-matrix'),
        hidden = $('#hidden');

    function readFile(e) {
        let file = e.target.files[0];
        if (!file) return;

        let reader = new FileReader();

        reader.onload = function (e) {
            let contents = e.target.result;
            hidden.append(contents);

            //TODO: parse stuff
            parseTransition(hidden);
            parsePlaces(hidden);
            parseMatrix(hidden);

            hidden.empty();
        }
        reader.readAsText(file);
    }

    function parseTransition(el) {
        display.append("<H1>TRANSICIONES</H1>");

        $("table", el).first().find(".cell .colhead").
            each(function (index) {
                display.append(this.textContent.trim() + ",<br>");
            });
    }

    function parsePlaces(el) {
        display.append("<H1>PLAZAS</H1>");

        $("table", el).first().find(".cell tbody .rowhead").
            each( function (i) {
                display.append(this.textContent.trim() + ",<br>");
            });
    }

    function parseMatrix(el) {
        $("table", el).each( function () {
            let tableItem = $(this),
                rows = tableItem.find(".cell tbody tr");

            // nombre de cada matriz
            let title = tableItem.find(".colhead").first().text().trim();
            if (title !== "Arrival_rate" && title !== "Active_N1")
                display.append("<H1>" + title + "</H1>");

            rows.each(function (i) {
                if (i === 0) return;

                display.append("{");
                let rowItem = $(this);
                rowItem.find(".cell").each(function (j) {
                    display.append(this.textContent.trim());
                    display.append(
                        j == rowItem.find(".cell").size()-1 ? "" : ","
                    );
                });

                display.append("},");
                display.append(" // " + rowItem.find(".rowhead").
                    text().trim() + "<br>");
            });

        });
    }

    document.getElementById("petri-net-matrix").addEventListener('change', readFile, false);
});