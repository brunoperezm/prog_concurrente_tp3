// Instrucciones de uso:

// Ir a PIPE y clickear Incidence & Marking y clickear Save

// Abrir el archivo html en el chrom

// Copiar en la consola hasta:


var jqry = document.createElement('script');
jqry.src = "https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js";
document.getElementsByTagName('head')[0].appendChild(jqry);
jQuery.noConflict();


// COPIAR HASTA ACÁ ---------------------------------------- 
// y presionar enter

// Luego copiar desde aquí para abajo y presionar enter


console.log(" -------- TRANSICIONES ---------");

var transitions = $("table").first().find(".cell .colhead");

var result = ""
transitions.each(function (index) {
    result += this.textContent.trim() + ",\n";
});
console.log(result);

console.log(" -------- PLAZAS ---------");

var places = $("table").first().find(".cell tbody .rowhead");

places.each(function (index) {
    result += this.textContent.trim() + ",\n";
});
console.log(result);

$('table').each( function (indice) {
    

    var table_item = $(this);

    var rows = table_item.find(".cell tbody tr");

    console.log(table_item.find(".colhead").first().text().trim())

    result = ""

    rows.each(function (index) { 
        if (index ==0 ) return;
        result += "{"
        var rowItem = $(this);
        rowItem.find(".cell").each(function (index2) {
            result += this.textContent.trim();
            result += index2 == rowItem.find(".cell").size()-1 ? "" : ","; 
        });
        result += "},"
        result += " // " + rowItem.find(".rowhead").text().trim() + "\n"
    });

    console.log(result);

});