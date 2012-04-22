/**
 * admin.js - funkcje do obsługi UI panelu admina
 */

$(function() {
    setjQueryUI();
});

var idOfDialogForm = "dialog-form";
var microtimeToHideNotice = 5000;

/* Funkcja ustawiająca style i akcje elementów na zgodne z jQuery UI */
function setjQueryUI ()
{
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
    $('fieldset').addClass('ui-widget ui-widget-content');
    $('fieldset legend').addClass('ui-widget-header ui-corner-all');
    $("input:submit, button, a.btn").button();
    $("#submit_asynchro").unbind('click').click(function(){
        commitForm(this);
    });
    $( "a.btn_dialog")
    .button()
    .unbind('click')
    .click(function() {
        var url = $(this).attr('href');
        initDialogWindow(url);
        return false;
    });

    //ukrywanie komunikatów
    if($('div.notice')) $('div.notice').delay(microtimeToHideNotice).slideUp();
}

// Tworzy okno dialogowe z zawartością pobraną z danego URL
function initDialogWindow(url)
{
    var dialog_window =  createDialogWindow();
    setContentDialogWindowFromUrl(dialog_window, url);
    return dialog_window;
}

/* funckja tworzaca okno dialogowe */
function createDialogWindow()
{
    //tworzenie elementu html
    var dialog = $('<div id="'+idOfDialogForm+'">').appendTo('body');

    //tworzenie okien dialogowych
    dialog.dialog({
        autoOpen: false,
        minHeight: 200,
        minWidth: 200,
        height: 380,
        width: 400,
        resizable: true,
        modal: true,
        buttons: {
            "Anuluj" : function() {
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
function setContentDialogWindowFromUrl(dialog_window, url)
{
    $.get(url,
        function(data){
            setContentDialogWindow(dialog_window, data)
        });
}

/* funkcja ustawiajaca zawartosc zadanego okna dialogowego */
function setContentDialogWindow(dialog_window, data)
{
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
function commitForm(submit)
{
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
