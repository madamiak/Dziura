package projekt.zespolowy.dziura.AppView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import projekt.zespolowy.dziura.DamageNotification;
import projekt.zespolowy.dziura.DziuraActivity;
import projekt.zespolowy.dziura.R;

/**
 * Asynchroniczne zadanie (rozszerza klase abstrakcyjna {@link android.os.AsyncTask}) sluazace do wyslania zgloszenia w
 * osobnym watku dzialajacym w tle. Ponad to wyswietla dialog o powodzeniu, lub z informacja o przyczynie bledu 
 * niepowodzeniu wyslania zgloszenia.
 *  Uruchamiane przez wywolanie funkcji {@link android.os.AsyncTask#execute(Object...)}
 * na instancji klasy.
 */
public class SendingTask extends AsyncTask < DziuraActivity, String, Integer >
{

	private DziuraActivity dziuraAct;
	private DamageNotification zgloszenie;
	private AlertDialog dialog;
	private boolean sendUsingServer;
	
	/**
	 * Konstruktor klasy {@link SendingTask}.
	 * @param dziuraAct referencja do instancji klasy {@link projekt.zespolowy.dziura.DziuraActivity}
	 */
	public SendingTask(DziuraActivity dziuraAct, boolean usingServer)
	{
		this.dziuraAct = dziuraAct;
		this.sendUsingServer = usingServer;
	}
	
	/**
	 * Funkcja wykonywana w watku interfejsu graficznego przed wykonaniem {@link #doInBackground(DziuraActivity...)}.
	 * Inicjalizuje dialog, ktory zostanie wyswietlony po wyslaniu zgloszenia oraz tworzy obiekt klasy {@link DamageNotification}.
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	protected void onPreExecute()
	{
		dialog = new AlertDialog.Builder(dziuraAct).create();
		Drawable icon = dziuraAct.getResources().getDrawable(R.drawable.ic_launcher);
		dialog.setIcon(icon);
		dialog.setTitle("Zg�oszenie");
		dialog.setCanceledOnTouchOutside(false);
		zgloszenie = new DamageNotification(dziuraAct);
	}

	/**
	 * Funkcja ta wykonywana jest przez watek w tle. Wysyla zgloszenie i zwraca wartosc zwrocona przez 
	 * funkcje wysylajaca (<i>send()</i> w klasie {@link DamageNotification}).
	 * 
	 * @param params argument nie wykorzystywany przez nas w funkcji
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(DziuraActivity... params)
	{	
		if(sendUsingServer == true)
		{
			return zgloszenie.send();
		}
		else
		{
			return zgloszenie.sendEmail();
		}
	}
	
	/**
	 * Funkcja wywolywana po zakonczeniu pracy przez watek uruchomiony poprzez {@link #execute(DziuraActivity...)}.
	 * W zaleznosci od parametru <i>result</i>, czyli rezultatu funkcji wysylajacej zgloszenie, wyswietla dialog
	 * o poprawnym wyslaniu zgloszenia lub o przyczynie bledu niepowodzenia wysylania. W pierwszym przypadku,
	 * wyswietlona zostaje takze informacja jaki identyfikator otrzymalo zgloszenie oraz na jaka strone nalezy
	 * wejsc, aby moc po podanym identyfikatorze wyszukac zgloszenie i sprawdzic jego stan.
	 * @param result wartosc zwrocona przez funkcje {@link #doInBackground(DziuraActivity...)}
	 */
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected void onPostExecute(Integer result)
	{
		DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		};
		String error = zgloszenie.getMessage();
		String id = zgloszenie.getId();
		if (sendUsingServer == true)
		{
			if (result == 0) {
				dialog.setMessage("Dzi�kujemy, zg�oszenie zosta�o pomy�lnie wys�ane. Aby sprawdzi� stan zg�oszenia, wejd� na stron� "
						+ zgloszenie.getURL()
						+ " i wyszukaj zg�oszenie o identyfikatorze "
						+ id
						+ ".");
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dziuraAct.appExit();
					}
				});
			} else if (result == -1) {
				dialog.setMessage("Wysy�anie trwa zbyt d�ugo, spr�buj ponownie za chwil�");
				dialog.setButton("OK", cancelListener);
			} else if (result == -3) {
				dialog.setMessage("Przepraszany, wyst�pi� b��d serwera ("
						+ error + ")");
				dialog.setButton("OK", cancelListener);
			} else {
				dialog.setMessage("Wyst�pi� b��d podczas wysy�ania, by� mo�e musisz sprawdzi� po��czenie z Internetem...");
				dialog.setButton("OK", cancelListener);
			}
		}
		else
		{
			if(result == 0)
			{
				dialog.setMessage("Dzi�kujemy, zg�oszenie zosta�o pomy�lnie wys�ane na nasz adres email.");
				dialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
						dziuraAct.appExit();
					}
				});
			}
			else if(result == -1)
			{
				dialog.setMessage("Wyst�pi� b��d podczas wysy�ania zg�oszenia na adres email, spr�buj ponownie za chwil�...");
				dialog.setButton("OK", cancelListener);
			}
			else if(result == -2)
			{
				dialog.setMessage("Wyst�pi� b��d podczas przygotowywania za��cznika, prosimy spr�buj ponownie za chwil�...");
				dialog.setButton("OK", cancelListener);
			}
			else
			{
				dialog.setMessage("Wyst�pi� b��d podczas wysy�ania zg�oszenia...");
				dialog.setButton("OK", cancelListener);
			}
		}
		dziuraAct.vOption.mZglosListener.sendProg.stop();
		dialog.show();
	}

}
