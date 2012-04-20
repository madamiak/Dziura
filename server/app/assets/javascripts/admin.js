$(function() {
    setjQueryUI();
});

var idOfDialogForm = "dialog-form";

/* funkcja ustawiająca style i akcje elementów na zgodne z jQuery UI */
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
    $("#submit_asynchro").unbind('click').click(function(){
        commitForm(this);
    });
    $( "a.btn_dialog")
    .button()
    .unbind('click')
    .click(function() {
        var url = $(this).attr('href');
        var dialog_window =  createDialogWindow();
        setContentDialogWindowFromUrl(dialog_window, url)
        dialog_window.dialog( "open" );
        return false;
    });
        
    //ukrywanie komunikatów
    if($('div.notice')) $('div.notice').delay(4000).slideUp();
}
    
/* funckja tworzaca okno dialogowe */
function createDialogWindow(){
    //tworzenie elementu html
    var dialog = $('<div id="'+idOfDialogForm+'">').appendTo('body');
        
    //tworzenie okien dialogowych
    dialog.dialog({
        autoOpen: false,
        height: 300,
        width: 400,
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
    
/* funkcja ustawiajaca zawartosc zadanego okna dialogowego z danego URL */
function setContentDialogWindowFromUrl(dialog_window, url){
    $.get(url,
        function(data){
            setContentDialogWindow(dialog_window, data)
        });
}

/* funkcja ustawiajaca zawartosc zadanego okna dialogowego */
function setContentDialogWindow(dialog_window, data){
    /* czyszczenie i ustawianie zawartości */
    dialog_window.empty().html(data);
            
    // pobieranie i ustawianie tytuly okna
    title = dialog_window.find('h1').html();
    dialog_window.find('h1').empty();
    dialog_window.dialog( "option", "title", title );
            
    // ustawianie styli i akcji elementów na zgodne z jQuery UI
    setjQueryUI();
}

/* funkcja do asynchronicznej obsługi formularzy */
function commitForm(submit){
    var form = $(submit).parents('form:first');
    
    /* uchwyt formularza */
    form.submit(function(event) {

        /* stop form from submitting normally */
        event.preventDefault(); 
        
        /* get some values from elements on the page: */
        var $form = $( this );
        var url = $form.attr( 'action' );
        var $inputs = $form.find('input:text, input:file, input:password, input:hidden');

        /* set up all inputs elements */
        var values = {};
        $inputs.each(function() {
            values[this.name] = $(this).val();
        });

        /* Send the data using post and put the results in a dialog-form */
        $.post( url, values,
        function( data ) {
            setContentDialogWindow($('#'+idOfDialogForm), data)
        }
        );
    });



}