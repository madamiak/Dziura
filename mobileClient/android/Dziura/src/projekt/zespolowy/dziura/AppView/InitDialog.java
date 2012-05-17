package projekt.zespolowy.dziura.AppView;

import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Klasa dziedziczy po {@link android.app.Dialog}, reprezentuje dialog wyswietlany po uruchomieniu aplikacji.
 */
public class InitDialog extends Dialog
{
	private DziuraActivity dziuraAct;
	private CheckBox check;

	/**
	 * Konstruktor klasy <i>InitDialog</i>.
	 * @param context instancja aktywnosci aplikacji, dla ktorej dialog bedzie wyswietlany
	 */
	public InitDialog(DziuraActivity context)
	{
		super(context);
		this.dziuraAct = context;
	}
	
	/**
	 * Inicjalizacja dialogu.
	 */
	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initdialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setTitle("Witaj w aplikacji Zg³oœ dziurê!");
        check = (CheckBox) findViewById(R.id.dCheckBox);
        Button buttonOK = (Button) findViewById(R.id.dButtonOk);
        buttonOK.setOnClickListener(new OkListener());
        Button buttonMore = (Button) findViewById(R.id.dButtonMore);
        buttonMore.setOnClickListener(new MoreListener());
    }

    private class OkListener implements android.view.View.OnClickListener
    {
        public void onClick(View v)
        {
        	dziuraAct.appConfig.setShowInitDialog(check.isChecked());
            InitDialog.this.cancel();
        }
    }
    
    private class MoreListener implements android.view.View.OnClickListener
    {
        public void onClick(View v)
        {
        	dziuraAct.appConfig.setShowInitDialog(check.isChecked());
        	InitDialog.this.cancel();
            AlertDialog dialog = new AlertDialog.Builder(dziuraAct).create();
			dialog.setMessage("Aby wykonaæ zdjêcie, naciœnij przycisk z ikon¹ aparatu lub klawisz aparatu."+
					" Przegl¹daj¹c zdjêcia mo¿esz oznaczaæ na nich tagi, naciskaj¹c na fragment zdjêcia, który chcesz oznaczyæ."+
					" Aby z widoku aparatu i zdjêæ przejœæ do formularza, wykonaj na ekranie gest z prawej do lewej." +
					" Aby wróciæ z formularza do zdjêæ, wykonaj gest w drug¹ stronê. Mo¿esz te¿ po prostu prze³¹czyæ zak³adki.");
			dialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
			dialog.show();
        }
    }
}
