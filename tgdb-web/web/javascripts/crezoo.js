$(document).ready(function(){
    $("ul.sf-menu").supersubs({
        minWidth:    12,                                // minimum width of sub-menus in em units
        maxWidth:    27,                                // maximum width of sub-menus in em units
        extraWidth:  1                                  // extra width can ensure lines don't sometimes turn over
                                                        // due to slight rounding differences and font-family
    }).superfish();
	
	$("#tabs").tabs();

});