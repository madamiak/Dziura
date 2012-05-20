package projekt.zespolowy.dziura;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Listener oblugujacy zmiane wybranego elementu. Instancja tej klasy przypisana jest do listy, ktora umozliwia wybor rodzaju szkody.
 * @see android.widget.Spinner
 */
public class MyOnItemSelectedListener implements OnItemSelectedListener 
{
	private int selectedItem = 0;

	/**
	 * Funkcja wywolywana po zmianie wybranego elementu. Zapisuje w lokalnej zmiennej numer nowo wybranego elementu z listy.
	 * @param arg0 AdapterView w ktorym nastapila zmiana
	 * @param arg1 widok, w ktorym dany AdapterView zostal klikniety
	 * @param arg2 pozycja nowo wybranego elementu z adaptera
	 * @param arg3 identyfikator rzedu wybranego elementu
	 */
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		this.selectedItem = arg2;
	}

	/**
	 * Funkcja nie zaimplementowana.
	 */
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> arg0)
	{
	}
	
	/**
	 * Zwraca odczytany wczesniej przez {@link projekt.zespolowy.dziura.MyOnItemSelectedListener#onItemSelected(AdapterView, View, int, long)}
	 * numer wybranego elementu.
	 * @return <li>pozycja zaznaczonego elementu (indeksowana od 0)
	 */
	public int getSelectedDamageType()
	{
		return this.selectedItem;
	}

}
