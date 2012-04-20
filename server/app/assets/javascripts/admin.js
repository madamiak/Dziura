$(function() {
    setjQueryUI();
});

var idOfDialogForm = "dialog-form";

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
    $("#submit_asynchro").click(function(){
        commitForm(this);
    });
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
    var dialog = $('<div id="'+idOfDialogForm+'">').appendTo('body');
        
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
            
            // ustawianie styli i akcji elementów na zgodne z jQuery UI
            setjQueryUI();
        });
}

// funkcja do asynchronicznej obsługi formularzy
function commitForm(submit){
    var form = $(submit).parents('form:first');
    
    //    form.attr('target', 'formiframe');
    //    
    //    iframe.load(function(){
    //        //$('#dialog-form').html(iframe.html());
    //        console.log($('iframe').html());
    //    })
    //    
    //    setTimeout(function(){
    //        console.log($('iframe').html());
    //    }, 100);
    //    
    //iframe.remove();
    
    
    /* attach a submit handler to the form */
    form.submit(function(event) {

        /* stop form from submitting normally */
        event.preventDefault(); 
        
        /* get some values from elements on the page: */
        var $form = $( this ),
        term = $form.find( 'input[name="status[name]"]' ).val(),
        url = $form.attr( 'action' );

        /* Send the data using post and put the results in a div */
        $.post( url, {
            "status[name]": term
        },
        function( data ) {
            var content = $( data );
            $('#dialog-form').empty().append( content );
        }
        );
    });



}