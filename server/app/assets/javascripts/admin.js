$(function() {
    setjQueryUI();
});

// funkcja ustawiająca style i akcje elementów na zgodne z jQuery UI
function setjQueryUI (){
    //ustawianie styli globalnych
    $('#container').addClass('ui-widget');
    $('#auth').addClass('ui-widget');
    
    //ustawianie styli tabel
    $('tbody tr').hover(function() {
        $(this).toggleClass('ui-state-hover');
    });
    $('table').addClass('ui-widget ui-widget-content');
    $('thead tr').addClass('ui-widget-header');
    
    //ustawianie styli forms
    $("input:submit, button, a.btn").button();
    $( "a.btn_dialog")
    .button()
    .click(function() {
        var url = $(this).attr('href');
        var dialog_window =  createDialogWindow();
        setContentDialogWindow(dialog_window, url)
        dialog_window.dialog( "open" );
        return false;
    });
        
    //ukrywanie komunikatów
    if($('div.notice')) $('div.notice').delay(4000).slideUp();
}
    
// funckja tworzaca okno dialogowe
function createDialogWindow(){
    //tworzenie elementu html
    var rand = Math.floor((Math.random()*100)+1);
    //var dialog = $('body').append($('<div id="dialog-form'+rand+'"></div>'));
    var dialog = $('<div id="dialog-form'+rand+'">').appendTo('body');
        
    //tworzenie okien dialogowych
    dialog.dialog({
        autoOpen: false,
        height: 300,
        width: 350,
        modal: true,
        buttons: {
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        },
        close: function() {
            dialog.remove();
        }
    });
        
    return dialog;
}
    
// funkcja ustawiajaca zawartosc zadanego okna dialogowego
function setContentDialogWindow(dialog_window, url){
    $.get(url,
        function(data){
            dialog_window.html(data);
            
            // pobieranie i ustawianie tytuly okna
            title = dialog_window.find('h1').html();
            dialog_window.find('h1').empty();
            dialog_window.dialog( "option", "title", title );
        });
}