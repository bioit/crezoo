function expandCollapse() {
    for (var i=0; i<expandCollapse.arguments.length; i++) {
        var element = document.getElementById(expandCollapse.arguments[i]);
        element.style.display = (element.style.display == "none") ? "block" : "none";
    }
}