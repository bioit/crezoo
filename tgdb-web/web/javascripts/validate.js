// Generic Form Validation
var checkObjects	= new Array();
var errors		= "";
var returnVal		= false;
var language		= new Array();
language["header"]	= "The following error(s) occured:"
language["start"]	= "->";
language["field"]	= " Field ";
language["require"]	= " is required";
language["min"]		= " and must consist of at least ";
language["max"]		= " and must not contain more than ";
language["minmax"]	= " and no more than ";
language["chars"]	= " characters";
language["num"]		= " and must contain a number";
language["email"]	= " must contain a valid e-mail address";
language["date"]	= " must contain a valid date";
// -----------------------------------------------------------------------------
// define - Call this function in the beginning of the page. I.e. onLoad.
// n = name of the input field (Required)
// type= string, num, email (Required)
// min = the value must have at least [min] characters (Optional)
// max = the value must have maximum [max] characters (Optional)
// d = (Optional)
// -----------------------------------------------------------------------------


// Example use in html file
//function init() {
//    define('field1', 'string', 'Apple'); // Required
//    define('field2', 'string', 'Peach', 4); // Required - minimum 4 characters
//    define('field3', 'string', 'Cherry', null, 8); // Required - maximum 8 characters
//    define('field4', 'string', 'Melon', 4, 8); // Required - minimum 4 characters - maximum 8 characters
//    define('field5', 'num', 'Banana'); // Required - Must be a numeric character
//    define('field6', 'num', 'Grape', 3); // Required - Must be a numeric character - minimum 3 digits
//    define('field7', 'num', 'Carot', null, 6); // Required - Must be a numeric character - maximum 6 digits
//    define('field8', 'num', 'Sugar', 4, 6); // Required - Must be a numeric character - minimum 4 digits - maximum 6 digits
//    define('field9', 'email', 'Fruit'); // Required - Valid e-mail
//}
//  --------------------------------


function define(n, type, HTMLname, min, max, d) {
    var p;
    var i;
    var x;
    if (!d) d = document;
    if ((p=n.indexOf("?"))>0&&parent.frames.length) {
        d = parent.frames[n.substring(p+1)].document;
        n = n.substring(0,p);
    }

    if (!(x = d[n]) && d.all) x = d.all[n];
        for (i = 0; !x && i < d.forms.length; i++) {
        x = d.forms[i][n];
    }

    for (i = 0; !x && d.layers && i < d.layers.length; i++) {
        x = define(n, type, HTMLname, min, max, d.layers[i].document);
        return x;       
    }

    eval("V_"+n+" = new formResult(x, type, HTMLname, min, max);");
    checkObjects[eval(checkObjects.length)] = eval("V_"+n);
}

function formResult(form, type, HTMLname, min, max) {
    this.form = form;
    this.type = type;
    this.HTMLname = HTMLname;
    this.min  = min;
    this.max  = max;
}

function validate() {
    if (checkObjects.length > 0) {
        errorObject = "";
        for (i = 0; i < checkObjects.length; i++) {
            validateObject = new Object();
            validateObject.form = checkObjects[i].form;
            validateObject.HTMLname = checkObjects[i].HTMLname;
            validateObject.val = checkObjects[i].form.value;
            validateObject.len = checkObjects[i].form.value.length;
            validateObject.min = checkObjects[i].min;
            validateObject.max = checkObjects[i].max;
            validateObject.type = checkObjects[i].type;

            if (validateObject.type == "num" || validateObject.type == "string") {
                if ((validateObject.type == "num" && validateObject.len <= 0) || (validateObject.type == "num" && isNaN(validateObject.val))) { 
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['require'] + language['num'] + "\n";
                } 
                else if (validateObject.min && validateObject.max && (validateObject.len < validateObject.min || validateObject.len > validateObject.max)) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['require'] + language['min'] + validateObject.min + language['minmax'] + validateObject.max+language['chars'] + "\n";
                } 
                else if (validateObject.min && !validateObject.max && (validateObject.len < validateObject.min)) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['require'] + language['min'] + validateObject.min + language['chars'] + "\n";
                }
                else if (validateObject.max && !validateObject.min &&(validateObject.len > validateObject.max)) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['require'] + language['max'] + validateObject.max + language['chars'] + "\n";
                }
                else if (!validateObject.min && !validateObject.max && validateObject.len <= 0) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['require'] + "\n";
                }
            }
            else if(validateObject.type == "email") {
                // Checking existense of "@" and ".". 
                // Length of must >= 5 and the "." must 
                // not directly precede or follow the "@"
                if ((validateObject.val.indexOf("@") == -1) || (validateObject.val.charAt(0) == ".") || (validateObject.val.charAt(0) == "@") || (validateObject.len < 6) || (validateObject.val.indexOf(".") == -1) || (validateObject.val.charAt(validateObject.val.indexOf("@")+1) == ".") || (validateObject.val.charAt(validateObject.val.indexOf("@")-1) == ".")) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['email'] + "\n";
                }
            }
            else if(validateObject.type == "date") {
                if (!isValidDate(validateObject.val)) {
                    errors += language['start'] + language['field'] + validateObject.HTMLname + language['date'] + "\n";
                }
            }
        }
    }
    if (errors) {
        alert(language["header"].concat("\n" + errors));
        errors = "";
        returnVal = false;
    }
    else {
        returnVal = true;
    }
}

// Validates if we have a date...only supported format is yyyy-mm-dd
function isValidDate(theDate){
    var dteDate;

    var tmp = theDate.split("-");
    if(tmp.length != 3)
        return false;
    dteDate = new Date(tmp[0], tmp[1], tmp[2]);

    return ((day==dteDate.getDate()) && (month==dteDate.getMonth()) && (year==dteDate.getFullYear()));
}