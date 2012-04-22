/**
 * admin.js - funkcje do obsługi UI panelu admina
 */

// Ustawienie stylów przy ładowaniu strony
$(function() {
    setjQueryUI();
});

var idOfDialogForm = "dialog-form";
var microtimeToHideNotice = 5000;

// Funkcja ustawiająca style i akcje elementów na zgodne z jQuery UI
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
        var dialog = initDialogWindow(url);
        dialog.dialog( "open" );
        return false;
    });

    //ukrywanie komunikatów
    if($('div.notice')) $('div.notice').delay(microtimeToHideNotice).slideUp();
}

// Tworzy okno dialogowe z zawartością pobraną z danego URL
function initDialogWindow(url, w, h, dialogLoaded)
{
    var dialog_window = createDialogWindow(w, h);
    setContentDialogWindowFromUrl(dialog_window, url, dialogLoaded);
    return dialog_window;
}

// Funkcja tworząca okno dialogowe
function createDialogWindow(w, h)
{
    if (!w)
        var w = 400;
    if (!h)
        var h = 380;

    //tworzenie elementu html
    var dialog = $('<div id="'+idOfDialogForm+'">').appendTo('body');

    //tworzenie okien dialogowych
    dialog.dialog({
        autoOpen: false,
        minHeight: 200,
        minWidth: 200,
        height: h,
        width: w,
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

// Funkcja ustawiająca zawartość zadanego okna dialogowego z danego URL
function setContentDialogWindowFromUrl(dialog_window, url, dialogLoaded)
{
    $.get(url,
        function(data){
            setContentDialogWindow(dialog_window, data);
            if (dialogLoaded !== undefined)
              dialogLoaded(dialog_window);
        });
}

// Funkcja ustawiająca zawartość zadanego okna dialogowego
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

// Funkcja do asynchronicznej obsługi formularzy
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
